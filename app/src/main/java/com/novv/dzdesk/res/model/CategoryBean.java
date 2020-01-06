package com.novv.dzdesk.res.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by lingyfh on 2017/6/1.
 */
public class CategoryBean extends ItemBean {

    @SerializedName("id")
    private String uuid;
    @SerializedName("name")
    private String name;
    @SerializedName("img")
    private String cover;
    @SerializedName("clickurl")
    private String clickurl;

    public CategoryBean() {
    }

    public String getId() {
        return uuid;
    }

    public void setId(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getClickurl() {
        return clickurl;
    }

    public void setClickurl(String clickurl) {
        this.clickurl = clickurl;
    }
}
