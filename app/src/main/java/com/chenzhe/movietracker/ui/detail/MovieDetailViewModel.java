package com.chenzhe.movietracker.ui.detail;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.chenzhe.movietracker.data.model.MovieDetail;
import com.chenzhe.movietracker.data.repository.MovieRepository;
import com.chenzhe.movietracker.ui.common.UiState;

/**
 * 详情页 ViewModel：与列表页同样的状态驱动方式，数据来自 {@link MovieRepository}。
 */
public class MovieDetailViewModel extends ViewModel {

    private final MutableLiveData<UiState<MovieDetail>> state = new MutableLiveData<>();

    private MovieRepository.Handle requestHandle;

    private int loadedMovieId = -1;

    public LiveData<UiState<MovieDetail>> getState() {
        return state;
    }

    /** 加载指定影视的详情；同一部电影且已成功加载过时不重复请求。 */
    public void loadMovieDetail(int movieId) {
        if (movieId == loadedMovieId && state.getValue() != null
                && state.getValue().status == UiState.Status.SUCCESS) {
            return;
        }
        cancelRequest();
        loadedMovieId = movieId;
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
