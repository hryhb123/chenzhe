package com.chenzhe.movietracker.data.mock;

/**
 * 示例数据场景开关（仅调试包可用）。
 * <p>
 * 没有 TMDb Key 时，数据层走本地示例数据；为了演示「加载 / 空 / 错误」三种 UI，
 * 这里提供一个进程内开关，由首页右上角菜单切换（{@code BuildConfig.DEBUG} 才显示菜单）。
 * 配置真实 Key 后该开关不再生效。
 */
public final class MockScenario {

    public enum Mode {
        SUCCESS,
        EMPTY,
        ERROR
    }

    private static volatile Mode current = Mode.SUCCESS;

    private MockScenario() {
    }

    public static Mode current() {
        return current;
    }

    public static void set(Mode mode) {
        current = mode;
    }
}
