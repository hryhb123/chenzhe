package com.chenzhe.movietracker.ui.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chenzhe.movietracker.R;
import com.chenzhe.movietracker.data.model.Movie;
import com.chenzhe.movietracker.util.PosterUrls;

import java.util.Locale;
import java.util.Objects;

/**
 * 影视列表适配器（2 列网格：海报 + 标题 + 年份/评分）。
 * <p>
 * 继承 {@link ListAdapter}，用 DiffUtil 做最小化刷新，避免 notifyDataSetChanged 造成
 * 整屏重绘与图片闪烁。
 */
public class MovieAdapter extends ListAdapter<Movie, MovieAdapter.MovieViewHolder> {

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
    }

    private static final DiffUtil.ItemCallback<Movie> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Movie>() {
                @Override
                public boolean areItemsTheSame(@NonNull Movie oldItem, @NonNull Movie newItem) {
                    return oldItem.getId() == newItem.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull Movie oldItem, @NonNull Movie newItem) {
                    return oldItem.getId() == newItem.getId()
                            && oldItem.getVoteAverage() == newItem.getVoteAverage()
                            && Objects.equals(oldItem.getTitle(), newItem.getTitle())
                            && Objects.equals(oldItem.getPosterPath(), newItem.getPosterPath())
                            && Objects.equals(oldItem.getReleaseDate(), newItem.getReleaseDate());
                }
            };

    private final OnMovieClickListener listener;

    public MovieAdapter(OnMovieClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = getItem(position);
        holder.bind(movie);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMovieClick(movie);
            }
        });
    }

    static class MovieViewHolder extends RecyclerView.ViewHolder {

        private final ImageView posterView;
        private final ImageView posterPlaceholderView;
        private final TextView titleView;
        private final TextView metaView;

        MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            posterView = itemView.findViewById(R.id.image_poster);
            posterPlaceholderView = itemView.findViewById(R.id.image_poster_placeholder);
            titleView = itemView.findViewById(R.id.text_title);
            metaView = itemView.findViewById(R.id.text_meta);
        }

        void bind(Movie movie) {
            titleView.setText(movie.getTitle());

            String year = movie.getReleaseYear().isEmpty()
                    ? itemView.getContext().getString(R.string.movie_year_unknown)
                    : movie.getReleaseYear();
            String rating = movie.getVoteAverage() > 0
                    ? String.format(Locale.CHINA, "★ %.1f", movie.getVoteAverage())
                    : "★ --";
            metaView.setText(String.format(Locale.CHINA, "%s · %s", year, rating));

            String posterUrl = PosterUrls.poster(movie.getPosterPath());
            // 没有海报（示例数据）时显示占位图标；有海报时隐藏，避免盖住真实图片
            posterPlaceholderView.setVisibility(posterUrl == null ? View.VISIBLE : View.GONE);
            Glide.with(posterView)
                    .load(posterUrl)
                    .placeholder(R.drawable.bg_poster_placeholder)
                    .error(R.drawable.bg_poster_placeholder)
                    .centerCrop()
                    .into(posterView);
        }
    }
}
