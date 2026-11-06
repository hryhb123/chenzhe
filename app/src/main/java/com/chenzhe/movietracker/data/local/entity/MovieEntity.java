package com.chenzhe.movietracker.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * 影视基础信息表（本地缓存 + 看单/评分页的标题海报来源）。
 * <p>
 * 主键直接用 TMDb 的电影 id，方便与网络层、看单表、评分表关联；
 * 只存展示需要的字段，不缓存简介之外的冗余数据。
 */
@Entity(tableName = "movies")
public class MovieEntity {

    @PrimaryKey
    @ColumnInfo(name = "id")
    private int id;

    @NonNull
    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "poster_path")
    private String posterPath;

    @ColumnInfo(name = "release_date")
    private String releaseDate;

    @ColumnInfo(name = "vote_average")
    private double voteAverage;

    @ColumnInfo(name = "overview")
    private String overview;

    public MovieEntity(int id, @NonNull String title, String posterPath, String releaseDate,
                       double voteAverage, String overview) {
        this.id = id;
        this.title = title == null ? "" : title;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.overview = overview;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getTitle() {
        return title == null || title.trim().isEmpty() ? "未命名" : title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title == null ? "" : title;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    /** 只取年份用于列表展示，形如 "2024"；没有日期时返回空串。 */
    public String getReleaseYear() {
        if (releaseDate == null || releaseDate.length() < 4) {
            return "";
        }
        return releaseDate.substring(0, 4);
    }
}