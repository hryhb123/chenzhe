package com.chenzhe.movietracker.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.ColumnInfo;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Upsert;

import com.chenzhe.movietracker.data.local.entity.MovieEntity;
import com.chenzhe.movietracker.data.local.entity.RatingEntity;

import java.util.List;

/**
 * 影视基础信息 DAO。
 * <p>
 * 所有方法都在 Room 的 IO 线程池执行（Room 会自己调度），
 * 查询返回 {@link LiveData} 时数据库一有变化就自动推送新结果，UI 无需手动刷新。
 * <p>
 * 注意：写入必须用 {@link Upsert}（内部是 UPDATE + INSERT），不能用
 * {@code @Insert(OnConflictStrategy.REPLACE)}。SQLite 的 REPLACE 是「先 DELETE 再 INSERT」，
 * 会触发 watchlist / ratings 外键上的 ON DELETE CASCADE，把用户的看单和评分一起删掉。
 */
@Dao
public interface MovieDao {

    /** 插入或更新影视信息（不触发级联删除）。 */
    @Upsert
    void upsert(MovieEntity movie);

    @Upsert
    void upsertAll(List<MovieEntity> movies);

    @Query("SELECT * FROM movies WHERE id = :movieId LIMIT 1")
    MovieEntity findById(int movieId);

    @Query("SELECT * FROM movies WHERE id = :movieId LIMIT 1")
    LiveData<MovieEntity> observeById(int movieId);

    /** 看单 / 评分列表页的联合查询结果行：影视信息 + 我的评分（可为空）。 */
    public static class MovieWithRating {
        @ColumnInfo(name = "id")
        private int id;

        @ColumnInfo(name = "title")
        private String title;

        @ColumnInfo(name = "poster_path")
        private String posterPath;

        @ColumnInfo(name = "release_date")
        private String releaseDate;

        @ColumnInfo(name = "vote_average")
        private double voteAverage;

        /** 没打过分时返回 null。 */
        @ColumnInfo(name = "myScore")
        private Integer myScore;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title == null || title.trim().isEmpty() ? "未命名" : title;
        }

        public void setTitle(String title) {
            this.title = title;
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

        public Integer getMyScore() {
            return myScore;
        }

        public void setMyScore(Integer myScore) {
            this.myScore = myScore;
        }
    }

    /** 已加入看单的影视，按加入时间倒序。 */
    @Query("SELECT m.id, m.title, m.poster_path, m.release_date, m.vote_average,"
            + " r.score AS myScore"
            + " FROM movies m INNER JOIN watchlist w ON w.movie_id = m.id"
            + " LEFT JOIN ratings r ON r.movie_id = m.id"
            + " ORDER BY w.added_at DESC")
    LiveData<List<MovieWithRating>> observeWatchlist();

    /** 已打分（即已看）的影视，按评分时间倒序。 */
    @Query("SELECT m.id, m.title, m.poster_path, m.release_date, m.vote_average,"
            + " r.score AS myScore"
            + " FROM movies m INNER JOIN ratings r ON r.movie_id = m.id"
            + " ORDER BY r.updated_at DESC")
    LiveData<List<MovieWithRating>> observeRated();

    /** 清理既不在看单、也没打分的影视缓存，避免数据库无限增长。 */
    @Query("DELETE FROM movies WHERE id NOT IN"
            + " (SELECT movie_id FROM watchlist UNION SELECT movie_id FROM ratings)")
    int deleteOrphans();

    /** 评分行（供详情页回显）。 */
    @Query("SELECT * FROM ratings WHERE movie_id = :movieId LIMIT 1")
    RatingEntity findRating(int movieId);
}