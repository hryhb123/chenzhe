package com.chenzhe.movietracker.util;

import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * 系统栏（状态栏 / 导航栏）避让。
 * <p>
 * Android 15（API 35）起默认强制沉浸式（edge-to-edge），页面内容会画到状态栏下面。
 * 这里在运行时把系统栏高度作为 padding 加到对应控件上：顶部加给标题栏，
 * 底部加给内容区，避免标题与状态栏图标重叠、列表被导航栏遮挡。
 */
public final class InsetsHelper {

    private InsetsHelper() {
    }

    /** 只补顶部（状态栏）留白。 */
    public static void applyTopInset(View view) {
        ViewCompat.setOnApplyWindowInsetsListener(view, (target, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            target.setPadding(target.getPaddingLeft(), insets.top,
                    target.getPaddingRight(), target.getPaddingBottom());
            return windowInsets;
        });
    }

    /**
     * 四边都补系统栏留白：加在页面根容器上，让标题栏整体下移而不是被挤压。
     */
    public static void applySystemBarInsets(View view) {
        ViewCompat.setOnApplyWindowInsetsListener(view, (target, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            target.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return windowInsets;
        });
    }

    /** 只补底部（导航栏）留白。 */
    public static void applyBottomInset(View view) {
        ViewCompat.setOnApplyWindowInsetsListener(view, (target, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            target.setPadding(target.getPaddingLeft(), target.getPaddingTop(),
                    target.getPaddingRight(), insets.bottom);
            return windowInsets;
        });
    }
}
