package net.invictusmanagement.invictuslifestyle.webservice;

import net.invictusmanagement.invictuslifestyle.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


class APIServiceGenerator {
    private static String _baseUrl = BuildConfig._baseUrl;

    private static final Retrofit.Builder builder =
            new Retrofit.Builder()
                    .baseUrl(_baseUrl)
//                    .baseUrl(BuildConfig._baseUrl)
                    .addConverterFactory(GsonConverterFactory.create());

    private static Retrofit retrofit = builder.build();

    private static final HttpLoggingInterceptor logging =
            new HttpLoggingInterceptor()
                    .setLevel(HttpLoggingInterceptor.Level.BODY);

    public static <S> S createService(
            Class<S> serviceClass, OkHttpClient.Builder httpClient) {
        if (!httpClient.interceptors().contains(logging)) {

            httpClient.addInterceptor(logging);
            builder.client(httpClient.build());

            retrofit = builder.build();
        }
        return retrofit.create(serviceClass);
    }
}
