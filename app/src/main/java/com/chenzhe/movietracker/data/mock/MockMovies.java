package com.chenzhe.movietracker.data.mock;

import com.chenzhe.movietracker.data.model.Genre;
import com.chenzhe.movietracker.data.model.Movie;
import com.chenzhe.movietracker.data.model.MovieDetail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 内置示例数据：在拿到真实 TMDb Key 之前，用它把「列表 → 详情」链路先跑通。
 * <p>
 * 数据字段与真实接口完全一致（同样的 Movie / MovieDetail 模型），
 * 因此换成真实接口时 UI 层不需要任何改动；海报地址刻意留空，
 * 由 Glide 显示占位图，避免示例数据误发起网络请求。
 */
public final class MockMovies {

    private static final List<Movie> POPULAR = buildPopular();

    private MockMovies() {
    }

    public static List<Movie> popular() {
        return POPULAR;
    }

    public static MovieDetail detail(int movieId) {
        Movie movie = find(movieId);
        int[] runtimes = {166, 180, 169, 108, 192, 140, 114, 169, 92, 102};
        List<List<String>> tags = Arrays.asList(
                Arrays.asList("科幻", "冒险", "剧情"),
                Arrays.asList("剧情", "历史"),
                Arrays.asList("科幻", "冒险", "剧情"),
                Arrays.asList("动画", "喜剧", "冒险"),
                Arrays.asList("科幻", "冒险", "动作"),
                Arrays.asList("动画", "动作", "冒险"),
                Arrays.asList("喜剧", "奇幻"),
                Arrays.asList("动作", "惊悚", "犯罪"),
                Arrays.asList("动画", "冒险", "喜剧"),
                Arrays.asList("动画", "冒险", "喜剧"));

        int index = POPULAR.indexOf(movie);
        if (index < 0) {
            index = 0;
        }

        List<Genre> genres = new ArrayList<>();
        for (String name : tags.get(index)) {
            genres.add(new Genre(name));
        }

        return new MovieDetail(
                movie.getId(),
                movie.getTitle(),
                movie.getPosterPath(),
                movie.getPosterPath(),
                movie.getReleaseDate(),
                runtimes[index],
                movie.getVoteAverage(),
                1200 + index * 137,
                "示例数据 · 仅用于验证页面链路",
                movie.getOverview() + "\n\n（以上为本地示例数据，配置 TMDb Key 后将显示真实简介。）",
                genres);
    }

    private static Movie find(int movieId) {
        for (Movie movie : POPULAR) {
            if (movie.getId() == movieId) {
                return movie;
            }
        }
        return POPULAR.get(0);
    }

    private static List<Movie> buildPopular() {
        List<Movie> list = new ArrayList<>();
        list.add(new Movie(693134, "沙丘2", null, "2024-02-27", 8.2,
                "保罗·厄崔迪与弗雷曼人联手，向毁灭其家族的阴谋者展开复仇，并努力阻止一场只有他能预见的可怕未来。"));
        list.add(new Movie(872585, "奥本海默", null, "2023-07-19", 8.1,
                "讲述原子弹之父罗伯特·奥本海默在二战期间主持曼哈顿计划，以及战后因安全听证会而身陷政治漩涡的故事。"));
        list.add(new Movie(157336, "星际穿越", null, "2014-11-05", 8.4,
                "地球环境恶化，一群探险家穿越虫洞，为人类寻找新的家园，一位父亲与女儿之间的爱成为跨越时空的纽带。"));
        list.add(new Movie(269149, "疯狂动物城", null, "2016-02-11", 8.5,
                "兔子警官朱迪与狐狸尼克联手，在一座动物共居的现代都市里侦破一起离奇的失踪案。"));
        list.add(new Movie(76600, "阿凡达：水之道", null, "2022-12-14", 7.6,
                "杰克·萨利与奈蒂莉组建家庭后，为了躲避追杀，举家迁往潘多拉星球的海洋部落。"));
        list.add(new Movie(569094, "蜘蛛侠：纵横宇宙", null, "2023-05-31", 8.4,
                "迈尔斯·莫拉莱斯穿越多元宇宙，与来自不同宇宙的蜘蛛侠们相遇，并对抗一场关于命运的抉择。"));
        list.add(new Movie(346698, "芭比", null, "2023-07-19", 7.0,
                "生活在芭比乐园的芭比某天发现现实世界的裂缝，于是与肯一起踏上前往人类世界的冒险。"));
        list.add(new Movie(603692, "疾速追杀4", null, "2023-03-22", 7.7,
                "约翰·威克为了重获自由，向高桌会发起最后的挑战，并在全球各地与老友和宿敌交锋。"));
        list.add(new Movie(502356, "超级马力欧兄弟大电影", null, "2023-04-05", 7.6,
                "水管工马力欧与弟弟路易吉被卷入蘑菇王国，为了救回弟弟，马力欧与碧奇公主并肩对抗库巴。"));
        list.add(new Movie(315162, "穿靴子的猫2", null, "2022-12-07", 8.2,
                "传奇剑客穿靴子的猫发现自己的九条命只剩下一条，于是踏上寻找许愿星的旅程。"));
        return Collections.unmodifiableList(list);
    }
}
