package com.chenzhe.movietracker.ui.watchlist;

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
import com.chenzhe.movietracker.data.repository.WatchlistRepository;
import com.chenzhe.movietracker.util.PosterUrls;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;

/**
 * 我的看单列表适配器：一行一部影视，展示海报、标题、年份、我的评分，并提供「移出」按钮。
 */
public class WatchlistAdapter extends ListAdapter<WatchlistRepository.Entry,
        WatchlistAdapter.EntryViewHolder> {

    /** 点击条目（跳详情）与点击「移出」的回调。 */
    public interface OnWatchlistActionListener {
        void onEntryClick(Movie movie);

        void onRemoveClick(Movie movie);
    }

    private final OnWatchlistActionListener listener;

    public WatchlistAdapter(OnWatchlistActionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<WatchlistRepository.Entry> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<WatchlistRepository.Entry>() {
                @Override
                public boolean areItemsTheSame(@NonNull WatchlistRepository.Entry oldItem,
                                               @NonNull WatchlistRepository.Entry newItem) {
                    return oldItem.movie.getId() == newItem.movie.getId();
                }

                @Override
                public boolean areContentsTheSame(@NonNull WatchlistRepository.Entry oldItem,
                                                  @NonNull WatchlistRepository.Entry newItem) {
                    return oldItem.movie.getTitle().equals(newItem.movie.getTitle())
                            && equalsNullable(oldItem.myScore, newItem.myScore);
                }

                private boolean equalsNullable(Integer a, Integer b) {
                    return a == null ? b == null : a.equals(b);
                }
            };

    @NonNull
    @Override
    public EntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_watchlist, parent, false);
        return new EntryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EntryViewHolder holder, int position) {
        holder.bind(getItem(position), listener);
    }

    static class EntryViewHolder extends RecyclerView.ViewHolder {

        private final ImageView posterView;
        private final ImageView posterPlaceholder;
        private final TextView titleView;
        private final TextView metaView;
        private final TextView scoreView;
        private final MaterialButton removeButton;

        EntryViewHolder(@NonNull View itemView) {
            super(itemView);
            posterView = itemView.findViewById(R.id.image_watchlist_poster);
            posterPlaceholder = itemView.findViewById(R.id.image_watchlist_poster_placeholder);
            titleView = itemView.findViewById(R.id.text_watchlist_title);
            metaView = itemView.findViewById(R.id.text_watchlist_meta);
            scoreView = itemView.findViewById(R.id.text_watchlist_score);
            removeButton = itemView.findViewById(R.id.button_watchlist_remove);
        }

        void bind(WatchlistRepository.Entry entry, OnWatchlistActionListener listener) {
            Movie movie = entry.movie;
            titleView.setText(movie.getTitle());

            String year = movie.getReleaseYear();
            metaView.setText(year.isEmpty()
                    ? itemView.getContext().getString(R.string.movie_year_unknown)
                    : year);

            if (entry.myScore == null) {
                scoreView.setText(R.string.watchlist_not_rated);
            } else {
                scoreView.setText(String.format(Locale.CHINA,
                        itemView.getContext().getString(R.string.watchlist_my_score),
                        entry.myScore));
            }

            String posterUrl = PosterUrls.poster(movie.getPosterPath());
            posterPlaceholder.setVisibility(posterUrl == null ? View.VISIBLE : View.GONE);
            Glide.with(posterView)
                    .load(posterUrl)
                    .placeholder(R.drawable.bg_poster_placeholder)
                    .error(R.drawable.bg_poster_placeholder)
                    .centerCrop()
                    .into(posterView);

            itemView.setOnClickListener(v -> listener.onEntryClick(movie));
            removeButton.setOnClickListener(v -> listener.onRemoveClick(movie));
        }
    }
}