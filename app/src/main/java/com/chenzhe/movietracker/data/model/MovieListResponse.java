package com.chenzhe.movietracker.data.model;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;

/** TMDb 分页列表响应体（{@code /movie/popular}、{@code /movie/top_rated} 等共用）。 */
public class MovieListResponse {

    @SerializedName("page")
    private int page;

    @SerializedName("total_pages")
    private int totalPages;

    @SerializedName("total_results")
    private int totalResults;

    @SerializedName("results")
    private List<Movie> results;

    public MovieListResponse(int page, List<Movie> results) {
        this.page = page;
        this.results = results;
    }

    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }

    /** 服务器返回 results: null 时兜底为空列表，避免 UI 层再判空。 */
    public List<Movie> getResults() {
        return results == null ? Collections.emptyList() : results;
    }
}
