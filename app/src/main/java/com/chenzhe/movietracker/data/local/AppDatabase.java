package com.chenzhe.movietracker.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.chenzhe.movietracker.data.local.dao.MovieDao;
import com.chenzhe.movietracker.data.local.dao.RatingDao;
import com.chenzhe.movietracker.data.local.dao.WatchlistDao;
import com.chenzhe.movietracker.data.local.entity.MovieEntity;
import com.chenzhe.movietracker.data.local.entity.RatingEntity;
import com.chenzhe.movietracker.data.local.entity.WatchlistEntity;

/**
 * Room 数据库入口（三张表：影视基础信息 / 看单 / 评分）。
 * <p>
 * 单例 + 懒加载：第一次访问才真正打开数据库文件；
 * 数据库操作由 Room 内部的 IO 线程池执行，不会占用主线程。
 * schema 通过 room.schemaLocation 导出到 app/schemas，升级版本时可据此写迁移。
 */
@Database(
        entities = {MovieEntity.class, WatchlistEntity.class, RatingEntity.class},
        version = 1,
        exportSchema = true)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME = "movie_tracker.db";

    private static volatile AppDatabase instance;

    public abstract MovieDao movieDao();

    public abstract WatchlistDao watchlistDao();

    public abstract RatingDao ratingDao();

    public static AppDatabase get(Context context) {
        AppDatabase local = instance;
        if (local == null) {
            synchronized (AppDatabase.class) {
                local = instance;
                if (local == null) {
                    local = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    DB_NAME)
                            // 课设阶段表结构还会调整，先允许破坏性迁移；正式项目应改为 Migration
                            .fallbackToDestructiveMigration(true)
                            .build();
                    instance = local;
                }
            }
        }
        return local;
    }
}