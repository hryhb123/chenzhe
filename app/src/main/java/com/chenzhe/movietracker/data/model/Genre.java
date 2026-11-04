package com.chenzhe.movietracker.data.model;

import com.google.gson.annotations.SerializedName;

/** 影视题材（TMDb 详情接口里的 genres 元素）。 */
public class Genre {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    public Genre(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name == null ? "" : name;
    }
}
