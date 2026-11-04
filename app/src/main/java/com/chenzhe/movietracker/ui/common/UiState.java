package com.chenzhe.movietracker.ui.common;

import androidx.annotation.Nullable;

/**
 * 统一的页面状态模型：UI 只依赖「加载 / 成功 / 空 / 错误」四种状态渲染，
 * 不再各自维护一堆 boolean。配合 LiveData 使用时，旋转屏幕或重建 Activity
 * 后重新观察即可恢复上一次状态，不会重复请求网络。
 *
 * @param <T> 成功时携带的数据类型
 */
public final class UiState<T> {

    public enum Status {
        /** 正在加载（展示进度条） */
        LOADING,
        /** 加载成功且有数据 */
        SUCCESS,
        /** 加载成功但没有数据 */
        EMPTY,
        /** 加载失败（展示错误信息 + 重试） */
        ERROR
    }

    public final Status status;

    @Nullable
    public final T data;

    @Nullable
    public final String message;

    private UiState(Status status, @Nullable T data, @Nullable String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> UiState<T> loading() {
        return new UiState<>(Status.LOADING, null, null);
    }

    public static <T> UiState<T> success(T data) {
        return new UiState<>(Status.SUCCESS, data, null);
    }

    public static <T> UiState<T> empty() {
        return new UiState<>(Status.EMPTY, null, null);
    }

    public static <T> UiState<T> error(String message) {
        return new UiState<>(Status.ERROR, null,
                message == null || message.trim().isEmpty() ? "未知错误" : message);
    }
}
