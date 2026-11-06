package com.chenzhe.movietracker.ui.watchlist;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.chenzhe.movietracker.data.repository.WatchlistRepository;
import com.chenzhe.movietracker.ui.common.UiState;

import java.util.List;

/**
 * 「我的看单」页面 ViewModel。
 * <p>
 * 继承 {@link AndroidViewModel}：默认 ViewModel 工厂能直接构造它（只需 Application），
 * 不需要额外写 Factory。列表三态（加载 / 成功 / 空）完全由 Room 的
 * {@link LiveData} 推送驱动：数据库没有看单记录时仓库给出 EMPTY，有记录给出 SUCCESS。
 */
public class WatchlistViewModel extends AndroidViewModel {

    private final WatchlistRepository repository;
    private final LiveData<UiState<List<WatchlistRepository.Entry>>> entries;

    public WatchlistViewModel(@NonNull Application application) {
        super(application);
        this.repository = WatchlistRepository.get(application);
        this.entries = repository.observeWatchlistEntries();
    }

    public LiveData<UiState<List<WatchlistRepository.Entry>>> getEntries() {
        return entries;
    }

    /** 写操作错误的来源：增删改失败时由仓库上报，页面展示错误态并提供重试入口。 */
    public LiveData<String> getLastError() {
        return repository.getLastError();
    }

    public void clearError() {
        repository.clearError();
    }

    /** 移出看单：写入子线程执行，列表由 LiveData 自动刷新。 */
    public void removeFromWatchlist(int movieId) {
        repository.removeFromWatchlist(movieId);
    }
}