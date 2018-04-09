package com.sticker_android.network;

import android.util.Base64;


import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by root on 5/5/17.
 */

public final class RestClient {

    private static Retrofit instance;

    //private constructor, user cant create object of this class
    private RestClient() {
    }

    /***
     * singleton pattern
     * @return instance of Retrofit
     */
    private static Retrofit getInstance() {
        if (instance == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(logging);
            httpClient.readTimeout(500, TimeUnit.SECONDS);
            httpClient.connectTimeout(500, TimeUnit.SECONDS);
            httpClient.retryOnConnectionFailure(true);

            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Accept", "application/json")
                            .header("Accept" ,"multipart/form-data")
                            .method(original.method(), original.body());
                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });

            instance = new Retrofit.Builder()
                    .baseUrl(ApiConstant.BASE_API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();

            return instance;
        } else {
            return instance;
        }
    }


    /***
     * for header
     * email/ password
     * @return encoded email/password encoded into base 64
     */
    public static String getAuth(){
        String credentials = "vivek@gmail.com" + ":" + "qUW4gNLW5OWw4rEd1U7UyVQBKrCqAwe7R";
        // create Base64 encode string
        return "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
    }

    /***
     * for header
     * email/ password
     * @return encoded email/password encoded into base 64
     */
    public static String getAuth(String userName, String token){
        String credentials = userName + ":" + token;
        // create Base64 encode string
        return "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
    }


    /***
     * to retrieve service from Retrofit
     * @return instance of SosService instance
     */
    public static StickerService getService() {
        // prepare call in Retrofit 2.0
        return getInstance().create(StickerService.class);
    }
}
