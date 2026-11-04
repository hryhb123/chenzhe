package com.chenzhe.movietracker.data.repository;

import androidx.annotation.NonNull;

import com.chenzhe.movietracker.data.mock.MockMovies;
import com.chenzhe.movietracker.data.mock.MockScenario;
import com.chenzhe.movietracker.data.model.Movie;
import com.chenzhe.movietracker.data.model.MovieDetail;
import com.chenzhe.movietracker.data.model.MovieListResponse;
import com.chenzhe.movietracker.data.remote.ApiConfig;
import com.chenzhe.movietracker.data.remote.TMDbClient;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import retrofit2.Call;
import retrofit2.Response;

/**
 * 数据仓库：MVVM 中的唯一数据出口（UI → ViewModel → Repository → 网络/本地）。
 * <p>
 * 两条数据通路：
 * <ul>
 *     <li>已配置 TMDb Key → Retrofit 走真实接口；</li>
 *     <li>未配置 Key → 本地示例数据（可切换 空 / 错误 场景，用于演示 UI 状态）。</li>
 * </ul>
 * 回调统一回到调用方（ViewModel），由它 postValue 到 LiveData；
 * 返回 {@link Handle} 便于在 ViewModel 销毁时取消请求，避免回调持有已回收的页面。
 */
public final class MovieRepository {

    /** 数据回调，成功或失败只会回调其中之一。 */
    public interface ResultCallback<T> {
        void onSuccess(T data);

        void onError(String message);
    }

    /** 取消句柄：Retrofit 的 Call 与本地线程任务都能被取消。 */
    public interface Handle {
        void cancel();
    }

    private static final MovieRepository INSTANCE = new MovieRepository();

    /** 本地示例数据的执行线程，避免在主线程 sleep 或读取数据。 */
    private final ExecutorService mockExecutor = Executors.newCachedThreadPool();

    private MovieRepository() {
    }

    public static MovieRepository get() {
        return INSTANCE;
    }

    /**
     * 拉取热门影视列表。
     *
     * @return 可用于取消本次请求的句柄
     */
    public Handle loadPopularMovies(ResultCallback<List<Movie>> callback) {
        if (!ApiConfig.hasApiKey()) {
            return loadMockPopular(callback);
        }
        Call<MovieListResponse> call = TMDbClient.api()
                .getPopularMovies(ApiConfig.API_KEY, ApiConfig.LANGUAGE, 1);
        call.enqueue(new retrofit2.Callback<MovieListResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieListResponse> call,
                                   @NonNull Response<MovieListResponse> response) {
                MovieListResponse body = response.body();
                if (response.isSuccessful() && body != null) {
                    callback.onSuccess(body.getResults());
                } else {
                    callback.onError("服务器返回异常（HTTP " + response.code() + "）");
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieListResponse> call, @NonNull Throwable t) {
                callback.onError(friendlyMessage(t));
            }
        });
        return call::cancel;
    }

    /**
     * 拉取影视详情。
     *
     * @return 可用于取消本次请求的句柄
     */
    public Handle loadMovieDetail(int movieId, ResultCallback<MovieDetail> callback) {
        if (!ApiConfig.hasApiKey()) {
            return loadMockDetail(movieId, callback);
        }
        Call<MovieDetail> call = TMDbClient.api()
                .getMovieDetail(movieId, ApiConfig.API_KEY, ApiConfig.LANGUAGE);
        call.enqueue(new retrofit2.Callback<MovieDetail>() {
            @Override
            public void onResponse(@NonNull Call<MovieDetail> call,
                                   @NonNull Response<MovieDetail> response) {
                MovieDetail body = response.body();
                if (response.isSuccessful() && body != null) {
                    callback.onSuccess(body);
                } else {
                    callback.onError("服务器返回异常（HTTP " + response.code() + "）");
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieDetail> call, @NonNull Throwable t) {
                callback.onError(friendlyMessage(t));
            }
        });
        return call::cancel;
    }

    private Handle loadMockPopular(ResultCallback<List<Movie>> callback) {
        Future<?> future = mockExecutor.submit(() -> {
            // 模拟网络耗时，方便观察加载态
            sleepQuietly(500L);
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            switch (MockScenario.current()) {
                case EMPTY:
                    callback.onSuccess(Collections.emptyList());
                    break;
                case ERROR:
                    callback.onError("模拟网络异常：无法连接 TMDb（当前为示例数据模式）");
                    break;
                case SUCCESS:
                default:
                    callback.onSuccess(MockMovies.popular());
                    break;
            }
        });
        return () -> future.cancel(true);
    }

    private Handle loadMockDetail(int movieId, ResultCallback<MovieDetail> callback) {
        Future<?> future = mockExecutor.submit(() -> {
            sleepQuietly(400L);
            if (Thread.currentThread().isInterrupted()) {
                return;
            }
            if (MockScenario.current() == MockScenario.Mode.ERROR) {
                callback.onError("模拟网络异常：无法连接 TMDb（当前为示例数据模式）");
            } else {
                callback.onSuccess(MockMovies.detail(movieId));
            }
        });
        return () -> future.cancel(true);
    }

    /** 把底层异常翻译成用户能看懂的提示，避免把堆栈直接甩给用户。 */
    private static String friendlyMessage(Throwable t) {
        if (t instanceof IOException) {
            return "网络连接失败，请检查网络或代理设置后重试";
        }
        String message = t.getMessage();
        return message == null || message.trim().isEmpty() ? "请求失败，请稍后重试" : message;
    }

    private static void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
