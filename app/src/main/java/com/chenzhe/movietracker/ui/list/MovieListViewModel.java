package com.chenzhe.movietracker.ui.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.chenzhe.movietracker.data.model.Movie;
import com.chenzhe.movietracker.data.repository.MovieRepository;
import com.chenzhe.movietracker.ui.common.UiState;

import java.util.List;

/**
 * 首页列表的 ViewModel：持有页面状态（LiveData）并负责发起请求。
 * <p>
 * 它不持有任何 Activity / View 引用，旋转屏幕时由 ViewModelStore 保留，
 * 页面重建后重新 observe 即可拿到上一次的状态，不会重复请求。
 */
public class MovieListViewModel extends ViewModel {

    private final MutableLiveData<UiState<List<Movie>>> state = new MutableLiveData<>();

    private MovieRepository.Handle requestHandle;

    public LiveData<UiState<List<Movie>>> getState() {
        return state;
    }

    /** 加载热门影视列表；重复调用会先取消上一次未完成的请求。 */
    public void loadPopularMovies() {
        cancelRequest();
        state.setValue(UiState.loading());
        requestHandle = MovieRepository.get()
                .loadPopularMovies(new MovieRepository.ResultCallback<List<Movie>>() {
                    @Override
                    public void onSuccess(List<Movie> data) {
                        if (data == null || data.isEmpty()) {
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
        // 页面真正销毁时取消请求，防止后台回调继续持有已结束的 ViewModel
        cancelRequest();
    }
}
