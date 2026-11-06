package com.chenzhe.movietracker.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.chenzhe.movietracker.data.local.entity.WatchlistEntity;

/**
 * 看单 DAO。返回值给 LiveData 的地方由 Room 自动在数据变化时重新查询并推送。
 */
@Dao
public interface WatchlistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(WatchlistEntity entity);

    @Query("DELETE FROM watchlist WHERE movie_id = :movieId")
    void deleteByMovieId(int movieId);

    @Query("SELECT * FROM watchlist WHERE movie_id = :movieId LIMIT 1")
    LiveData<WatchlistEntity> observeByMovieId(int movieId);

    @Query("SELECT COUNT(*) FROM watchlist")
    LiveData<Integer> observeCount();
}