package com.chenzhe.movietracker.util;

/**
 * TMDb 图片地址拼装。
 * <p>
 * 接口只返回 {@code poster_path}（形如 "/abc.jpg"），需要拼上固定域名与尺寸前缀。
 */
public final class PosterUrls {

    private static final String IMAGE_BASE = "https://image.tmdb.org/t/p/";

    /** 列表页海报宽度 342px，足够 2 列网格显示。 */
    private static final String POSTER_SIZE = "w342";

    /** 详情页大图宽度 780px。 */
    private static final String BACKDROP_SIZE = "w780";

    private PosterUrls() {
    }

    public static String poster(String posterPath) {
        return isEmpty(posterPath) ? null : IMAGE_BASE + POSTER_SIZE + posterPath;
    }

    public static String backdrop(String backdropPath) {
        return isEmpty(backdropPath) ? null : IMAGE_BASE + BACKDROP_SIZE + backdropPath;
    }

    private static boolean isEmpty(String path) {
        return path == null || path.trim().isEmpty();
    }
}
