package com.chenzhe.movietracker;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chenzhe.movietracker.data.mock.MockScenario;
import com.chenzhe.movietracker.data.model.Movie;
import com.chenzhe.movietracker.data.remote.ApiConfig;
import com.chenzhe.movietracker.ui.common.UiState;
import com.chenzhe.movietracker.ui.detail.MovieDetailActivity;
import com.chenzhe.movietracker.ui.list.MovieAdapter;
import com.chenzhe.movietracker.ui.list.MovieListViewModel;
import com.chenzhe.movietracker.ui.watchlist.WatchlistActivity;
import com.chenzhe.movietracker.util.InsetsHelper;

import java.util.List;

/**
 * 首页：热门影视列表（MVVM 的 View 层）。
 * <p>
 * 只做两件事：把用户操作交给 ViewModel，把 ViewModel 的 LiveData 状态渲染成界面。
 * 加载 / 空 / 错误 三种状态各有一个独立视图，任何状态都不会出现白屏。
 */
public class MainActivity extends AppCompatActivity implements MovieAdapter.OnMovieClickListener {

    private MovieListViewModel viewModel;
    private MovieAdapter adapter;

    private RecyclerView recyclerView;
    private View loadingView;
    private View emptyView;
    private View errorView;
    private TextView errorMessageView;
    private TextView mockNoticeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        InsetsHelper.applySystemBarInsets(findViewById(R.id.root_main));

        recyclerView = findViewById(R.id.recycler_movies);
        loadingView = findViewById(R.id.view_loading);
        emptyView = findViewById(R.id.view_empty);
        errorView = findViewById(R.id.view_error);
        errorMessageView = findViewById(R.id.text_error_message);
        mockNoticeView = findViewById(R.id.text_mock_notice);

        adapter = new MovieAdapter(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        findViewById(R.id.button_retry).setOnClickListener(v -> viewModel.loadPopularMovies());

        // 未配置 Key 时明确告知当前是示例数据，避免误以为「真实数据加载失败」
        mockNoticeView.setVisibility(ApiConfig.hasApiKey() ? View.GONE : View.VISIBLE);

        viewModel = new ViewModelProvider(this).get(MovieListViewModel.class);
        viewModel.getState().observe(this, this::render);

        // 首次进入才发起请求；旋转屏幕后 ViewModel 里已有状态，直接复用
        if (viewModel.getState().getValue() == null) {
            viewModel.loadPopularMovies();
        }
    }

    /** 把状态渲染成界面：同一时刻只显示一种状态视图。 */
    private void render(UiState<List<Movie>> state) {
        if (state == null) {
            return;
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
    public void onMovieClick(Movie movie) {
        startActivity(MovieDetailActivity.createIntent(this, movie.getId(), movie.getTitle()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // 示例数据场景切换只在调试包里出现，正式包不暴露
        if (!BuildConfig.DEBUG) {
            menu.findItem(R.id.action_mock_success).setVisible(false);
            menu.findItem(R.id.action_mock_empty).setVisible(false);
            menu.findItem(R.id.action_mock_error).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_watchlist) {
            startActivity(WatchlistActivity.createIntent(this));
            return true;
        }
        if (itemId == R.id.action_mock_success) {
            switchMockScenario(MockScenario.Mode.SUCCESS, R.string.toast_mock_success);
            return true;
        }
        if (itemId == R.id.action_mock_empty) {
            switchMockScenario(MockScenario.Mode.EMPTY, R.string.toast_mock_empty);
            return true;
        }
        if (itemId == R.id.action_mock_error) {
            switchMockScenario(MockScenario.Mode.ERROR, R.string.toast_mock_error);
            return true;
        }
        if (itemId == R.id.action_refresh) {
            viewModel.loadPopularMovies();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void switchMockScenario(MockScenario.Mode mode, int toastRes) {
        MockScenario.set(mode);
        viewModel.loadPopularMovies();
        Toast.makeText(this, toastRes, Toast.LENGTH_SHORT).show();
    }
}
