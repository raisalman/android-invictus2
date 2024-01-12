package net.invictusmanagement.invictuslifestyle.webservice;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import net.invictusmanagement.invictuslifestyle.BuildConfig;
import net.invictusmanagement.invictuslifestyle.utils.Utilities;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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

public class RestClient {
    private static final String JsonDateFormatString = "yyyy-MM-dd'T'HH:mm:ss";
    private String TAG = this.getClass().getSimpleName();

    public static RestClient restClient;
    private Retrofit retrofit;
    private RestService restService;
    private Context mContext;
    private OkHttpClient httpClient;
    private String _authenticationCookie = null;


    public void setAuthenticationCookie(String cookie) {
        _authenticationCookie = cookie;
    }

    private RestClient() {

        final Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .disableHtmlEscaping()
                .registerTypeAdapter(Date.class, new DateSerializer())
                .registerTypeAdapter(Date.class, new DateDeserializer())
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
                        Log.i(TAG, "Error_API = " + request.url().toString());
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

        // SSL certificate bypass logic
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            httpClient = builder
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(Utilities._baseUrl)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //ssl code end

//        httpClient = builder.build();
//
//        retrofit = new Retrofit.Builder()
//                .baseUrl(Utilities._baseUrl)
//                .addConverterFactory(ScalarsConverterFactory.create())
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .client(httpClient)
//                .build();


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

    public static RestClient getInstance() {
        if (restClient == null) {
            restClient = new RestClient();
        }

        return restClient;
    }

    public void init(Context context) {
        if (restClient == null)
            restClient = new RestClient();
        this.mContext = context;
    }

    public RestService getRestService() {
        return restService;
    }

    public OkHttpClient getOkHttpClient() {
        return httpClient;
    }

    private static class DateDeserializer implements JsonDeserializer<Date> {

        @Override
        public Date deserialize(JsonElement element, Type arg1, JsonDeserializationContext arg2) throws JsonParseException {
            String date = element.getAsString();

            SimpleDateFormat formatter = new SimpleDateFormat(JsonDateFormatString);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

            try {
                return formatter.parse(date);
            } catch (ParseException ex) {
                Log.e(Utilities.TAG, "JsonDeserializer<Date>() failed", ex);
                return null;
            }
        }
    }

    private class DateSerializer implements JsonSerializer<Date> {

        @Override
        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
            SimpleDateFormat formatter = new SimpleDateFormat(JsonDateFormatString);
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            return new JsonPrimitive(formatter.format(src));
        }
    }
}