package com.chenzhe.movietracker.data.local.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * 用户评分表：一部影视一条记录，分值为 1~10 的整数。
 * <p>
 * 与看单表分开：看单是「想看」，评分是「已看并打分」，两者互不依赖。
 * 外键指向 movies，影视记录删除时评分级联删除。
 */
@Entity(
        tableName = "ratings",
        foreignKeys = @ForeignKey(
                entity = MovieEntity.class,
                parentColumns = "id",
                childColumns = "movie_id",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("movie_id")})
public class RatingEntity {

    /** 评分下限与上限，UI 的评分条与之保持一致。 */
    public static final int MIN_SCORE = 1;
    public static final int MAX_SCORE = 10;

    @PrimaryKey
    @ColumnInfo(name = "movie_id")
    private int movieId;

    /** 1~10 分。 */
    @ColumnInfo(name = "score")
    private int score;

    /** 最后一次打分时间戳（毫秒）。 */
    @ColumnInfo(name = "updated_at")
    private long updatedAt;

    public RatingEntity(int movieId, int score, long updatedAt) {
        this.movieId = movieId;
        this.score = clamp(score);
        this.updatedAt = updatedAt;
    }

    public int getMovieId() {
        return movieId;
    }

    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    public int getScore() {
        return score;
    }

    /** 写入前统一夹取到 1~10，防止 UI 传进越界值。 */
    public void setScore(int score) {
        this.score = clamp(score);
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    private static int clamp(int score) {
        if (score < MIN_SCORE) {
            return MIN_SCORE;
        }
        return Math.min(score, MAX_SCORE);
    }
}