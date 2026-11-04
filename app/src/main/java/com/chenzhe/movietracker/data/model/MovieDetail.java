package com.chenzhe.movietracker.data.model;

import com.google.gson.annotations.SerializedName;

/**
 * 影视详情，对应 TMDb {@code /movie/{movie_id}} 的响应体。
 * 只声明本课设需要展示的字段，TMDb 返回的其余字段 Gson 会自动忽略。
 */
public class MovieDetail {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("original_title")
    private String originalTitle;

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("backdrop_path")
    private String backdropPath;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("runtime")
    private int runtime;

    @SerializedName("vote_average")
    private double voteAverage;

    @SerializedName("vote_count")
    private int voteCount;

    @SerializedName("tagline")
    private String tagline;

    @SerializedName("overview")
    private String overview;

    @SerializedName("genres")
    private java.util.List<Genre> genres;

    /** 供本地示例数据（mock）构造用。 */
    public MovieDetail(int id, String title, String posterPath, String backdropPath,
                       String releaseDate, int runtime, double voteAverage, int voteCount,
                       String tagline, String overview, java.util.List<Genre> genres) {
        this.id = id;
        this.title = title;
        this.originalTitle = title;
        this.posterPath = posterPath;
        this.backdropPath = backdropPath;
        this.releaseDate = releaseDate;
        this.runtime = runtime;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.tagline = tagline;
        this.overview = overview;
        this.genres = genres;
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

    public String getBackdropPath() {
        return backdropPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getReleaseYear() {
        if (releaseDate == null || releaseDate.length() < 4) {
            return "";
        }
        return releaseDate.substring(0, 4);
    }

    public int getRuntime() {
        return runtime;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public int getVoteCount() {
        return voteCount;
    }

    public String getTagline() {
        return tagline;
    }

    public String getOverview() {
        return overview;
    }

    public java.util.List<Genre> getGenres() {
        return genres;
    }
}
