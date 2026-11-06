package com.chenzhe.movietracker.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.chenzhe.movietracker.data.local.AppDatabase;
import com.chenzhe.movietracker.data.local.MovieMapper;
import com.chenzhe.movietracker.data.local.dao.MovieDao;
import com.chenzhe.movietracker.data.local.dao.RatingDao;
import com.chenzhe.movietracker.data.local.dao.WatchlistDao;
import com.chenzhe.movietracker.data.local.entity.MovieEntity;
import com.chenzhe.movietracker.data.local.entity.RatingEntity;
import com.chenzhe.movietracker.data.local.entity.WatchlistEntity;
import com.chenzhe.movietracker.data.model.Movie;
import com.chenzhe.movietracker.data.model.MovieDetail;
import com.chenzhe.movietracker.ui.common.UiState;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 本地数据仓库：看单与评分的唯一入口。
 * <p>
 * 线程模型：
 * <ul>
 *   <li>写操作（加入/移出看单、打分）交给单线程 {@link ExecutorService}，保证顺序执行、不占主线程；</li>
 *   <li>读操作返回 {@link LiveData}，Room 在数据库变化时自动重新查询并推送到主线程，UI 无需手动刷新。</li>
 * </ul>
 * 写失败（如磁盘异常）通过 {@link #getLastError()} 暴露，由 ViewModel 转成错误态展示。
 */
public class WatchlistRepository {

    private static volatile WatchlistRepository instance;

    private final MovieDao movieDao;
    private final WatchlistDao watchlistDao;
    private final RatingDao ratingDao;

    /** 单线程写队列：保证「点两次加入」之类的并发操作按顺序落库。 */
    private final ExecutorService writeExecutor = Executors.newSingleThreadExecutor();

    /** 最近一次写操作的错误信息；null 表示没有错误。 */
    private final MutableLiveData<String> lastError = new MutableLiveData<>();

    private WatchlistRepository(Context context) {
        AppDatabase db = AppDatabase.get(context);
        this.movieDao = db.movieDao();
        this.watchlistDao = db.watchlistDao();
        this.ratingDao = db.ratingDao();
    }

    public static WatchlistRepository get(Context context) {
        WatchlistRepository local = instance;
        if (local == null) {
            synchronized (WatchlistRepository.class) {
                local = instance;
                if (local == null) {
                    local = new WatchlistRepository(context.getApplicationContext());
                    instance = local;
                }
            }
        }
        return local;
    }

    // ---------------------------------------------------------------- 写操作

    /** 加入看单；先确保影视基础信息已入库（外键约束要求父表有记录）。 */
    public void addToWatchlist(@Nullable Movie movie) {
        if (movie == null || movie.getId() <= 0) {
            return;
        }
        MovieEntity entity = MovieMapper.toEntity(movie);
        writeExecutor.execute(() -> {
            try {
                movieDao.upsert(entity);
                watchlistDao.insert(new WatchlistEntity(movie.getId(), System.currentTimeMillis()));
            } catch (Exception e) {
                lastError.postValue("加入看单失败：" + e.getMessage());
            }
        });
    }

    /** 从看单移除，并清理不再被引用的影视缓存。 */
    public void removeFromWatchlist(int movieId) {
        writeExecutor.execute(() -> {
            try {
                watchlistDao.deleteByMovieId(movieId);
                movieDao.deleteOrphans();
            } catch (Exception e) {
                lastError.postValue("移出看单失败：" + e.getMessage());
            }
        });
    }

    /** 打分（1~10）；重复打分覆盖旧值。影视信息不在库里时先补写。 */
    public void rateMovie(@Nullable MovieDetail detail, int score) {
        if (detail == null || detail.getId() <= 0) {
            return;
        }
        MovieEntity entity = MovieMapper.toEntity(detail);
        writeExecutor.execute(() -> {
            try {
                movieDao.upsert(entity);
                ratingDao.upsert(new RatingEntity(detail.getId(), score,
                        System.currentTimeMillis()));
            } catch (Exception e) {
                lastError.postValue("保存评分失败：" + e.getMessage());
            }
        });
    }

    /** 清空某部影视的评分。 */
    public void clearRating(int movieId) {
        writeExecutor.execute(() -> {
            try {
                ratingDao.deleteByMovieId(movieId);
                movieDao.deleteOrphans();
            } catch (Exception e) {
                lastError.postValue("删除评分失败：" + e.getMessage());
            }
        });
    }

    // ---------------------------------------------------------------- 读操作

    /** 是否已在看单（true=在，false=不在；数据库首帧为 null）。 */
    public LiveData<Boolean> observeInWatchlist(int movieId) {
        return Transformations.map(
                watchlistDao.observeByMovieId(movieId),
                entity -> entity != null);
    }

    /** 我的评分（无评分时为 null）。 */
    public LiveData<Integer> observeMyScore(int movieId) {
        return Transformations.map(
                ratingDao.observeByMovieId(movieId),
                entity -> entity == null ? null : entity.getScore());
    }

    /** 看单页数据：影视信息 + 我的评分，已滚动到 {@link UiState} 的三态。 */
    public LiveData<UiState<List<Entry>>> observeWatchlistEntries() {
        return Transformations.map(movieDao.observeWatchlist(),
                rows -> toUiState(rows));
    }

    /** 已看列表（有评分的影视），同样包成三态。 */
    public LiveData<UiState<List<Entry>>> observeRatedEntries() {
        return Transformations.map(movieDao.observeRated(),
                rows -> toUiState(rows));
    }

    /** 看单数量，用于首页入口角标。 */
    public LiveData<Integer> observeWatchlistCount() {
        return watchlistDao.observeCount();
    }

    public LiveData<String> getLastError() {
        return lastError;
    }

    /** 清除错误提示，避免重复弹出。 */
    public void clearError() {
        lastError.setValue(null);
    }

    // ---------------------------------------------------------------- 内部

    private static UiState<List<Entry>> toUiState(List<MovieDao.MovieWithRating> rows) {
        if (rows == null || rows.isEmpty()) {
            return UiState.empty();
        }
        List<Entry> entries = new ArrayList<>(rows.size());
        for (MovieDao.MovieWithRating row : rows) {
            entries.add(new Entry(MovieMapper.toMovie(toEntity(row)), row.getMyScore()));
        }
        return UiState.success(entries);
    }

    private static MovieEntity toEntity(MovieDao.MovieWithRating row) {
        return new MovieEntity(row.getId(), row.getTitle(), row.getPosterPath(),
                row.getReleaseDate(), row.getVoteAverage(), null);
    }

    /** 列表项：影视信息 + 我的评分（可为空）。 */
    public static final class Entry {
        @NonNull
        public final Movie movie;
        @Nullable
        public final Integer myScore;

        public Entry(@NonNull Movie movie, @Nullable Integer myScore) {
            this.movie = movie;
            this.myScore = myScore;
        }
    }
}