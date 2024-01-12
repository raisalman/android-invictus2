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

import net.invictusmanagement.invictuslifestyle.utils.Utilities;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
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

public class RestClient {
    private static final String JsonDateFormatString = "yyyy-MM-dd'T'HH:mm:ss";
    private String TAG = this.getClass().getSimpleName();

    public static RestClient restClient;
    private Retrofit retrofit;
    private RestService restService;
    private Context mContext;
    private OkHttpClient httpClient;
    private String _authenticationCookie = null;
//    private String _authenticationCookie = ".AspNetCore.MobileIdentity.Application=CfDJ8Oi-AtBhHTZBnf8H5K1E3A8yDimxrrkW_r9lVKLZwB8iex98h4K-zi7LAt5Om2t9HWYk7tI3sg57OV-yoRrlPWytgoBklYcOqnEvKuqwBxPeWbwOHg8AmM7DZUCUeLitzJ1enPk1Vgtzi535HGKR-lN0bv26Gyr_YUHzOJ7ibQ8dj3dfO6eC140SZlM-mSGPZDGvsRkt2EyNuFfuBPkcvx9FZZxslZRyDCffVwUBPHF3RbDIx2LKGPuI1FMqti4AQGjIvDdRMIGPCy7_cJvNFbGxtzgK2FYwXb7R6MvTmrlSIC-UEwjuTnH66EmycsqwgSbGhNCjz0v5q37XASxv35earxA6jJzvZ8hFihThSwCvTOLPsAHXejqIcqGf9APSThhDVRYgaUkZM0WfymMIL6lZSyWD2DzErkFlCo_2e0nRKFx-PiFnj2--Gv-Aq-FSzaySSWmnFc4ijUUapQdizTsbV4k2s2dGemVTHRLEH7XZ_2uXELv2OfbFwgxTlVlQE_0ULVyTG2pW9byV3gQXNLfg0GYcRvt5YxSKCuuF0IzSWQGCNlH51wVcVkxRhCxmk6KZeWM9_I9t0ZvdkgqWXc8FvZ9pFmOwd00Satr5eFZE-YeQDwpsh3ek_Did_e7CZfelV3eZPl086eJ7qa1EAmeJC5YGK0gJey1XMWU7NTdp7o6BvsRg3QNQMgVa5bwioVUerTTExTCDsxiQV1eqnl9vskNyNy881AU_po20EZityzQ4pp6o5EeCpCPIVQxvpjcdCosrBG9oW19nIgvZ4YTnUvt5C9suShEnWZ2yufrc; expires=Sun, 11 Jan 2032 10:02:07 GMT; path=/; secure; httponly";


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
        if (!Utilities.IS_RELEASE) {
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
                .baseUrl(Utilities._baseUrl)
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