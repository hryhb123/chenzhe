package com.chenzhe.movietracker.ui.watchlist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chenzhe.movietracker.R;
import com.chenzhe.movietracker.data.model.Movie;
import com.chenzhe.movietracker.data.repository.WatchlistRepository;
import com.chenzhe.movietracker.ui.common.UiState;
import com.chenzhe.movietracker.ui.detail.MovieDetailActivity;
import com.chenzhe.movietracker.util.InsetsHelper;

import java.util.List;

/**
 * 「我的看单」页面：展示已加入看单的影视，可点击回到详情，也可直接移出。
 * <p>
 * 三种界面状态（加载 / 空 / 错误）与列表状态互斥显示：
 * 空态明确告诉用户「还没加过影视」，不会出现一片空白。
 */
public class WatchlistActivity extends AppCompatActivity
        implements WatchlistAdapter.OnWatchlistActionListener {

    public static Intent createIntent(Context context) {
        return new Intent(context, WatchlistActivity.class);
    }

    private WatchlistViewModel viewModel;
    private WatchlistAdapter adapter;

    private RecyclerView recyclerView;
    private View loadingView;
    private View emptyView;
    private View errorView;
    private TextView errorMessageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watchlist);

        Toolbar toolbar = findViewById(R.id.toolbar_watchlist);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        setTitle(R.string.watchlist_title);

        InsetsHelper.applySystemBarInsets(findViewById(R.id.root_watchlist));

        recyclerView = findViewById(R.id.recycler_watchlist);
        loadingView = findViewById(R.id.view_watchlist_loading);
        emptyView = findViewById(R.id.view_watchlist_empty);
        errorView = findViewById(R.id.view_watchlist_error);
        errorMessageView = findViewById(R.id.text_watchlist_error_message);

        adapter = new WatchlistAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        findViewById(R.id.button_watchlist_retry).setOnClickListener(v -> {
            viewModel.clearError();
            render(viewModel.getEntries().getValue());
        });

        viewModel = new ViewModelProvider(this).get(WatchlistViewModel.class);

        viewModel.getEntries().observe(this, this::render);

        // 写操作失败时进入错误态，并提供重试入口
        viewModel.getLastError().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                render(UiState.error(message));
            }
        });
    }

    private void render(UiState<List<WatchlistRepository.Entry>> state) {
        if (state == null) {
            // 首帧未到：显示加载态，避免闪一下空态
            state = UiState.loading();
        }
        boolean loading = state.status == UiState.Status.LOADING;
        boolean empty = state.status == UiState.Status.EMPTY;
        boolean error = state.status == UiState.Status.ERROR;
        boolean success = state.status == UiState.Status.SUCCESS;

        loadingView.setVisibility(loading ? View.VISIBLE : View.GONE);
        emptyView.setVisibility(empty ? View.VISIBLE : View.GONE);
        errorView.setVisibility(error ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(success ? View.VISIBLE : View.GONE);

        if (success) {
            adapter.submitList(state.data);
        } else if (error) {
            errorMessageView.setText(state.message);
        }
    }

    @Override
    public void onEntryClick(Movie movie) {
        startActivity(MovieDetailActivity.createIntent(this, movie.getId(), movie.getTitle()));
    }

    @Override
    public void onRemoveClick(Movie movie) {
        viewModel.removeFromWatchlist(movie.getId());
        Toast.makeText(this, R.string.toast_removed_from_watchlist, Toast.LENGTH_SHORT).show();
    }
}