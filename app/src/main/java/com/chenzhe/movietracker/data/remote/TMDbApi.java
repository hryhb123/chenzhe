package com.chenzhe.movietracker.data.remote;

import com.chenzhe.movietracker.data.model.MovieDetail;
import com.chenzhe.movietracker.data.model.MovieListResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * TMDb v3 接口定义（Retrofit 动态代理生成实现）。
 * <p>
 * 只声明本课设用到的两个接口：
 * <ul>
 *     <li>{@code GET /movie/popular}：热门影视列表（分页）</li>
 *     <li>{@code GET /movie/{movie_id}}：影视详情</li>
 * </ul>
 * 返回 {@link Call} 而不是响应体，便于在 ViewModel 销毁时取消请求，避免回调泄漏。
 */
public interface TMDbApi {

    @GET("movie/popular")
    Call<MovieListResponse> getPopularMovies(
            @Query("api_key") String apiKey,
            @Query("language") String language,
            @Query("page") int page);

    @GET("movie/{movie_id}")
    Call<MovieDetail> getMovieDetail(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey,
            @Query("language") String language);
}
