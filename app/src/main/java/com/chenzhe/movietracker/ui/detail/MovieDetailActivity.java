package com.chenzhe.movietracker.ui.detail;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.chenzhe.movietracker.R;
import com.chenzhe.movietracker.data.model.Genre;
import com.chenzhe.movietracker.data.model.MovieDetail;
import com.chenzhe.movietracker.ui.common.UiState;
import com.chenzhe.movietracker.util.InsetsHelper;
import com.chenzhe.movietracker.util.PosterUrls;

import java.util.List;
import java.util.Locale;

/**
 * 影视详情页：从列表点击进入，展示海报、基本信息、题材与简介。
 * <p>
 * 与列表页同一套状态模型（加载 / 成功 / 错误），网络失败时显示重试而不是白屏。
 */
public class MovieDetailActivity extends AppCompatActivity {

    private static final String EXTRA_MOVIE_ID = "extra_movie_id";
    private static final String EXTRA_MOVIE_TITLE = "extra_movie_title";

    public static Intent createIntent(Context context, int movieId, String title) {
        Intent intent = new Intent(context, MovieDetailActivity.class);
        intent.putExtra(EXTRA_MOVIE_ID, movieId);
        intent.putExtra(EXTRA_MOVIE_TITLE, title);
        return intent;
    }

    private MovieDetailViewModel viewModel;
    private int movieId;

    private View contentView;
    private View loadingView;
    private View errorView;
    private TextView errorMessageView;
    private TextView titleView;
    private TextView taglineView;
    private TextView metaView;
    private TextView genresView;
    private TextView overviewView;
    private ImageView backdropView;
    private ImageView placeholderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        movieId = getIntent().getIntExtra(EXTRA_MOVIE_ID, -1);
        String initialTitle = getIntent().getStringExtra(EXTRA_MOVIE_TITLE);

        Toolbar toolbar = findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        setTitle(initialTitle == null ? getString(R.string.detail_title) : initialTitle);

        InsetsHelper.applySystemBarInsets(findViewById(R.id.root_detail));

        contentView = findViewById(R.id.view_detail_content);
        loadingView = findViewById(R.id.view_detail_loading);
        errorView = findViewById(R.id.view_detail_error);
        errorMessageView = findViewById(R.id.text_detail_error_message);
        titleView = findViewById(R.id.text_detail_title);
        taglineView = findViewById(R.id.text_detail_tagline);
        metaView = findViewById(R.id.text_detail_meta);
        genresView = findViewById(R.id.text_detail_genres);
        overviewView = findViewById(R.id.text_detail_overview);
        backdropView = findViewById(R.id.image_detail_backdrop);
        placeholderView = findViewById(R.id.image_detail_placeholder);

        findViewById(R.id.button_detail_retry).setOnClickListener(v -> {
            if (movieId > 0) {
                viewModel.loadMovieDetail(movieId);
            }
        });

        if (movieId <= 0) {
            renderError("影视 ID 无效，无法加载详情");
            return;
        }

        viewModel = new ViewModelProvider(this).get(MovieDetailViewModel.class);
        viewModel.getState().observe(this, this::render);
        if (viewModel.getState().getValue() == null) {
            viewModel.loadMovieDetail(movieId);
        }
    }

    private void render(UiState<MovieDetail> state) {
        if (state == null) {
            return;
        }
        boolean loading = state.status == UiState.Status.LOADING;
        boolean error = state.status == UiState.Status.ERROR || state.status == UiState.Status.EMPTY;
        boolean success = state.status == UiState.Status.SUCCESS;

        loadingView.setVisibility(loading ? View.VISIBLE : View.GONE);
        errorView.setVisibility(error ? View.VISIBLE : View.GONE);
        contentView.setVisibility(success ? View.VISIBLE : View.GONE);

        if (error) {
            errorMessageView.setText(state.message == null
                    ? getString(R.string.detail_empty)
                    : state.message);
        } else if (success && state.data != null) {
            bind(state.data);
        }
    }

    private void renderError(String message) {
        loadingView.setVisibility(View.GONE);
        contentView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
        errorMessageView.setText(message);
    }

    private void bind(MovieDetail detail) {
        setTitle(detail.getTitle());
        titleView.setText(detail.getTitle());

        if (TextUtils.isEmpty(detail.getTagline())) {
            taglineView.setVisibility(View.GONE);
        } else {
            taglineView.setVisibility(View.VISIBLE);
            taglineView.setText(detail.getTagline());
        }

        metaView.setText(buildMeta(detail));

        String genres = joinGenres(detail.getGenres());
        genresView.setVisibility(TextUtils.isEmpty(genres) ? View.GONE : View.VISIBLE);
        genresView.setText(genres);

        overviewView.setText(TextUtils.isEmpty(detail.getOverview())
                ? getString(R.string.detail_no_overview)
                : detail.getOverview());

        String imageUrl = PosterUrls.backdrop(detail.getBackdropPath() != null
                ? detail.getBackdropPath()
                : detail.getPosterPath());
        placeholderView.setVisibility(imageUrl == null ? View.VISIBLE : View.GONE);
        Glide.with(backdropView)
                .load(imageUrl)
                .placeholder(R.drawable.bg_poster_placeholder)
                .error(R.drawable.bg_poster_placeholder)
                .centerCrop()
                .into(backdropView);
    }

    /** 拼装「年份 · 时长 · 评分」这类元信息。 */
    private String buildMeta(MovieDetail detail) {
        StringBuilder builder = new StringBuilder();
        if (!TextUtils.isEmpty(detail.getReleaseYear())) {
            builder.append(detail.getReleaseYear());
        }
        if (detail.getRuntime() > 0) {
            if (builder.length() > 0) {
                builder.append(" · ");
            }
            builder.append(getString(R.string.detail_runtime, detail.getRuntime()));
        }
        if (detail.getVoteAverage() > 0) {
            if (builder.length() > 0) {
                builder.append(" · ");
            }
            builder.append(String.format(Locale.CHINA, "★ %.1f", detail.getVoteAverage()));
            if (detail.getVoteCount() > 0) {
                builder.append(String.format(Locale.CHINA, "（%d 人评分）", detail.getVoteCount()));
            }
        }
        return builder.toString();
    }

    private String joinGenres(List<Genre> genres) {
        if (genres == null || genres.isEmpty()) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (Genre genre : genres) {
            if (builder.length() > 0) {
                builder.append(" / ");
            }
            builder.append(genre.getName());
        }
        return builder.toString();
    }
}
