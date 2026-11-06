package com.chenzhe.movietracker.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.chenzhe.movietracker.data.local.entity.RatingEntity;

/**
 * 评分 DAO：同一部影视只保留一条评分，重复打分直接覆盖。
 */
@Dao
public interface RatingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(RatingEntity entity);

    @Query("DELETE FROM ratings WHERE movie_id = :movieId")
    void deleteByMovieId(int movieId);

    @Query("SELECT * FROM ratings WHERE movie_id = :movieId LIMIT 1")
    LiveData<RatingEntity> observeByMovieId(int movieId);

    @Query("SELECT * FROM ratings WHERE movie_id = :movieId LIMIT 1")
    RatingEntity findByMovieId(int movieId);
}