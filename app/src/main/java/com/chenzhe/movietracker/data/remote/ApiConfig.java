package com.chenzhe.movietracker.data.remote;

import com.chenzhe.movietracker.BuildConfig;

/**
 * TMDb 接口的静态配置。
 * <p>
 * Key 与域名都来自本机 {@code local.properties}，构建时由 Gradle 写入 BuildConfig，
 * 因此仓库里永远看不到真实 Key。Key 为空时 {@link #hasApiKey()} 返回 false，
 * 数据层会自动降级到内置示例数据（mock），保证没有 Key 也能把页面跑通。
 */
public final class ApiConfig {

    /** TMDb v3 API Key（local.properties → tmdb.api.key）。 */
    public static final String API_KEY = BuildConfig.TMDB_API_KEY;

    /** API 根地址，必须以 "/" 结尾（local.properties → tmdb.base.url）。 */
    public static final String BASE_URL = BuildConfig.TMDB_BASE_URL;

    /** 接口语言，让标题与简介直接返回中文。 */
    public static final String LANGUAGE = "zh-CN";

    private ApiConfig() {
    }

    public static boolean hasApiKey() {
        return API_KEY != null && !API_KEY.trim().isEmpty();
    }
}
