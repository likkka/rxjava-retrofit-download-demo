package com.example.rio.icontools.icontools.download;

import com.example.rio.icontools.icontools.utils.InputStreamConverterFactory;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

/**
 * Created by huangminzhi on 17-6-21.
 */

public class IconRetrofit {
    final ServerApi serverService;
    boolean isDebug = true;
    private static final String BASE_URL = "http://api-baas.flyme.cn/v1/api/schema/";
    private static final String BASE_URL_DEBUG = "http://172.17.140.199:92/v1/api/schema/";


    public IconRetrofit() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        if (isDebug) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(logging);
        }
        httpClient.connectTimeout(12, TimeUnit.SECONDS);
        OkHttpClient client = httpClient.build();

        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(BASE_URL_DEBUG)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io()))
                .addConverterFactory(InputStreamConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        serverService = retrofit.create(ServerApi.class);
    }
    public ServerApi getServerService() {
        return serverService;
    }
}
