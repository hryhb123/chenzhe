package com.chenzhe.movietracker.data.local;

import com.chenzhe.movietracker.data.local.entity.MovieEntity;
import com.chenzhe.movietracker.data.model.Movie;
import com.chenzhe.movietracker.data.model.MovieDetail;

/**
 * 网络模型（Movie / MovieDetail）与本地实体（MovieEntity）之间的转换。
 * 放在一处，避免 UI 层直接接触实体或网络 DTO。
 */
public final class MovieMapper {

    private MovieMapper() {
    }

    public static MovieEntity toEntity(Movie movie) {
        if (movie == null) {
            return null;
        }
        return new MovieEntity(movie.getId(), movie.getTitle(), movie.getPosterPath(),
                movie.getReleaseDate(), movie.getVoteAverage(), movie.getOverview());
    }

    public static MovieEntity toEntity(MovieDetail detail) {
        if (detail == null) {
            return null;
        }
        return new MovieEntity(detail.getId(), detail.getTitle(), detail.getPosterPath(),
                detail.getReleaseDate(), detail.getVoteAverage(), detail.getOverview());
    }

    /** 实体 → 列表用模型，看单 / 评分页点击卡片时可直接复用详情页的跳转逻辑。 */
    public static Movie toMovie(MovieEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Movie(entity.getId(), entity.getTitle(),
                entity.getPosterPath(), entity.getReleaseDate(),
                entity.getVoteAverage(), entity.getOverview());
    }
}