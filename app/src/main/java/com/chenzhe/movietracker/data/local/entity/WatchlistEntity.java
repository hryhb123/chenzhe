package com.chenzhe.movietracker.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * 「想看」看单表：一行代表一部已加入看单的影视。
 * <p>
 * 单独建表而不是在 movies 表里加状态字段，原因：
 * 状态与影视信息生命周期不同（移出看单不该删掉影视缓存），
 * 且以后要加「已看」等状态时不会污染基础信息表。
 * 外键指向 movies，影视记录被删时看单行级联删除，避免出现孤儿条目。
 */
@Entity(
        tableName = "watchlist",
        foreignKeys = @ForeignKey(
                entity = MovieEntity.class,
                parentColumns = "id",
                childColumns = "movie_id",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("movie_id")})
public class WatchlistEntity {

    @PrimaryKey
    @ColumnInfo(name = "movie_id")
    private int movieId;

    /** 加入看单的时间戳（毫秒），用于按加入顺序倒序展示。 */
    @ColumnInfo(name = "added_at")
    private long addedAt;

    public WatchlistEntity(int movieId, long addedAt) {
        this.movieId = movieId;
        this.addedAt = addedAt;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    @Override
    public String toString() {
        return "WatchlistEntity{movieId=" + movieId + ", addedAt=" + addedAt + '}';
    }

    public long getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(long addedAt) {
        this.addedAt = addedAt;
    }
}