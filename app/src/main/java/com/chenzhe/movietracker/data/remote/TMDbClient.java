package com.chenzhe.movietracker.data.remote;

import com.chenzhe.movietracker.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit / OkHttp 单例构建器。
 * <p>
 * OkHttp 自带线程池：{@code call.enqueue()} 的回调在后台线程执行，
 * UI 层不需要自己起线程也不会阻塞主线程。
 */
public final class TMDbClient {

    private static volatile TMDbApi api;

    private TMDbClient() {
    }

    public static TMDbApi api() {
        TMDbApi local = api;
        if (local == null) {
            synchronized (TMDbClient.class) {
                local = api;
                if (local == null) {
                    local = create();
                    api = local;
                }
            }
        }
        return local;
    }

    private static TMDbApi create() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // 只打印请求行与响应码，避免日志里出现完整 Key
        logging.setLevel(BuildConfig.DEBUG
                ? HttpLoggingInterceptor.Level.BASIC
                : HttpLoggingInterceptor.Level.NONE);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .build();

        return new Retrofit.Builder()
                .baseUrl(ApiConfig.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(TMDbApi.class);
    }
}
