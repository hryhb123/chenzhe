package com.chenzhe.movietracker.ui.detail;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.chenzhe.movietracker.data.local.MovieMapper;
import com.chenzhe.movietracker.data.model.MovieDetail;
import com.chenzhe.movietracker.data.repository.MovieRepository;
import com.chenzhe.movietracker.data.repository.WatchlistRepository;
import com.chenzhe.movietracker.ui.common.UiState;

/**
 * 详情页 ViewModel：网络详情 + 本地看单/评分状态。
 * <p>
 * 详情数据来自 {@link MovieRepository}（子线程请求），
 * 看单与评分状态来自 {@link WatchlistRepository} 的 Room LiveData，
 * 任何一处变化都会自动推送到界面，不需要手动调用刷新。
 */
public class MovieDetailViewModel extends AndroidViewModel {

    private final MutableLiveData<UiState<MovieDetail>> state = new MutableLiveData<>();

    private MovieRepository.Handle requestHandle;

    /**
     * 当前已绑定本地状态（看单/评分）的影视 id。
     * 与网络请求的 id 分开记录，避免「先请求详情、再绑定本地状态」时互相覆盖。
     */
    private int boundMovieId = -1;

    /** 详情请求对应的影视 id，用于避免重复请求。 */
    private int loadedId = -1;

    private final WatchlistRepository watchlistRepository;

    /** 是否在「想看」看单中；首次查询完成前为 null。 */
    private LiveData<Boolean> inWatchlist;

    /** 我的评分；没打分为 null。 */
    private LiveData<Integer> myScore;

    public MovieDetailViewModel(@NonNull Application application) {
        super(application);
        this.watchlistRepository = WatchlistRepository.get(application);
    }

    public LiveData<UiState<MovieDetail>> getState() {
        return state;
    }

    /** 加载指定影视的详情；同一部电影且已成功加载过时不重复请求。 */
    public void loadMovieDetail(int movieId) {
        if (movieId == loadedId && state.getValue() != null
                && state.getValue().status == UiState.Status.SUCCESS) {
            return;
        }
        cancelRequest();
        loadedId = movieId;
        state.setValue(UiState.loading());
        requestHandle = MovieRepository.get()
                .loadMovieDetail(movieId, new MovieRepository.ResultCallback<MovieDetail>() {
                    @Override
                    public void onSuccess(MovieDetail data) {
                        if (data == null) {
                            state.postValue(UiState.empty());
                        } else {
                            state.postValue(UiState.success(data));
                        }
                    }

                    @Override
                    public void onError(String message) {
                        state.postValue(UiState.error(message));
                    }
                });
    }

    // ------------------------------------------------------------ 看单与评分

    /** 绑定某部影视的本地状态（看单 + 我的评分），进行一次性观察。 */
    public void bindLocalState(int movieId) {
        if (movieId == boundMovieId && inWatchlist != null && myScore != null) {
            return;
        }
        boundMovieId = movieId;
        inWatchlist = watchlistRepository.observeInWatchlist(movieId);
        myScore = watchlistRepository.observeMyScore(movieId);
    }

    public LiveData<Boolean> getInWatchlist() {
        return inWatchlist;
    }

    public LiveData<Integer> getMyScore() {
        return myScore;
    }

    public LiveData<String> getLastError() {
        return watchlistRepository.getLastError();
    }

    public void clearError() {
        watchlistRepository.clearError();
    }

    /** 「加入 / 移出看单」：写入在仓库的子线程队列中执行。 */
    public void toggleWatchlist(MovieDetail detail, boolean currentlyInWatchlist) {
        if (detail == null) {
            return;
        }
        if (currentlyInWatchlist) {
            watchlistRepository.removeFromWatchlist(detail.getId());
        } else {
            watchlistRepository.addToWatchlist(MovieMapper.toMovie(MovieMapper.toEntity(detail)));
        }
    }

    /** 保存评分（1~10）。 */
    public void rate(MovieDetail detail, int score) {
        watchlistRepository.rateMovie(detail, score);
    }

    /** 清除评分。 */
    public void clearRating(MovieDetail detail) {
        if (detail != null) {
            watchlistRepository.clearRating(detail.getId());
        }
    }

    private void cancelRequest() {
        if (requestHandle != null) {
            requestHandle.cancel();
            requestHandle = null;
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        cancelRequest();
    }
}