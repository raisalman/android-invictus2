package net.invictusmanagement.invictuslifestyle.webservice;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.invictusmanagement.invictuslifestyle.BuildConfig;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.Util;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class RestClientBrivoSmartHome {
    private static final String JsonDateFormatString = "yyyy-MM-dd'T'HH:mm:ss";
    private String TAG = this.getClass().getSimpleName();

    public static RestClientBrivoSmartHome restClient;
    private Retrofit retrofit;
    private RestService restService;
    private Context mContext;
    private OkHttpClient httpClient;
    private String _authenticationCookie = null;


    public void setAuthenticationCookie(String cookie) {
        _authenticationCookie = cookie;
    }

    public String getAuthenticationCookie() {
        return _authenticationCookie;
    }

    private RestClientBrivoSmartHome() {

        final Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .create();

        final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        final OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
        builder.protocols(Util.immutableListOf(Protocol.HTTP_1_1));
        builder.readTimeout(125, TimeUnit.SECONDS);
        builder.connectTimeout(60, TimeUnit.SECONDS);
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(logging);
        }


        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                if (!WebService.isNetworkAvailable(mContext)) {
                    WSException throwable = new WSException();
                    throwable.setCode(0);
                    throwable.setServerMessage(Utilities.NO_INTERNET);
                    throw throwable;
                }

                Request original = chain.request();
                Request request = original.newBuilder()
//                        .header("Content-Type", "application/json")
                        .addHeader("Cookie", _authenticationCookie != null
                                ? _authenticationCookie : "12345")
                        .method(original.method(), original.body())
                        .build();

                Response response;
                try {
                    response = chain.proceed(request);
                } catch (Exception e) {
                    e.printStackTrace();
                    WSException throwable = new WSException();
                    throwable.setCode(401);
                    throwable.setServerMessage(Utilities.NO_WIFI);
                    throw throwable;
                }

                if (!response.isSuccessful()) {
                    WSException throwable = new WSException();
                    try {
                        String errorResponse = response.body().string();
                        Log.i(TAG, "Error_code = " + response.code());
                        Log.i(TAG, "Error = " + errorResponse);
                        if (isJSONValid(errorResponse)) {
                            JSONObject jsonObject = new JSONObject(errorResponse);
                            String message = jsonObject.optString("message");
                            throwable.setCode(response.code());
                            throwable.setServerMessage(message);
                        } else {
                            throwable.setCode(response.code());
                            throwable.setServerMessage(errorResponse);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        throwable.setCode(response.code());
                        throwable.setServerMessage(response.message());
                    }
                    throw throwable;
                }
                return response;
            }
        });

        httpClient = builder.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Utilities._baseUrlBrivoSmartHome)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient)
                .build();


        restService = retrofit.create(RestService.class);
    }

    public static boolean isJSONValid(String jsonInString) {
        try {
            Gson gson = new Gson();
            gson.fromJson(jsonInString, Object.class);
            return true;
        } catch (com.google.gson.JsonSyntaxException ex) {
            return false;
        }
    }

    public static RestClientBrivoSmartHome getInstance() {
        if (restClient == null) {
            restClient = new RestClientBrivoSmartHome();
        }

        return restClient;
    }

    public void init(Context context) {
        if (restClient == null)
            restClient = new RestClientBrivoSmartHome();
        this.mContext = context;
    }

    public RestService getRestService() {
        return restService;
    }

    public OkHttpClient getOkHttpClient() {
        return httpClient;
    }

}