package com.chenzhe.movietracker.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * 影视列表项，对应 TMDb {@code /movie/popular} 响应里 results 数组的单个元素。
 * <p>
 * 字段名与 JSON 完全一致（Gson 靠 {@link SerializedName} 映射），
 * 详情页需要的额外字段放在 {@link MovieDetail} 里，避免列表接口携带冗余数据。
 */
public class Movie {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("original_title")
    private String originalTitle;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("vote_average")
    private double voteAverage;

    @SerializedName("overview")
    private String overview;

    /** 供本地示例数据（mock）构造用；真实数据由 Gson 反序列化生成。 */
    public Movie(int id, String title, String posterPath, String releaseDate,
                 double voteAverage, String overview) {
        this.id = id;
        this.title = title;
        this.originalTitle = title;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.overview = overview;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title == null || title.trim().isEmpty() ? "未命名" : title;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    /** 只取年份用于列表展示，形如 "2024"；没有日期时返回空串。 */
    public String getReleaseYear() {
        if (releaseDate == null || releaseDate.length() < 4) {
            return "";
        }
        return releaseDate.substring(0, 4);
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getOverview() {
        return overview;
    }
}
