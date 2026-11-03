package com.chenzhe.movietracker;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

/**
 * 首页占位 Activity。
 * <p>
 * 当前阶段（项目初始化）只验证工程骨架、依赖与构建链路是否打通，
 * 不包含任何影视追踪业务逻辑；后续在此接入列表 / 搜索 / 详情等页面。
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
