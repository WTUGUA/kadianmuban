package com.novv.dzdesk.res.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RingsResult {

    @SerializedName("category")
    private List<RingsCategoryBean> category;
    @SerializedName("new")
    private List<RingsBean> newX;
    @SerializedName("hot")
    private List<RingsBean> hot;
    private List<RingsBean> msg;

    public List<RingsBean> getMsg() {
        return msg;
    }

    public void setMsg(List<RingsBean> msg) {
        this.msg = msg;
    }

    public static RingsResult objectFromData(String str) {

        return new Gson().fromJson(str, RingsResult.class);
    }

    public List<RingsCategoryBean> getCategory() {
        return category;
    }

    public void setCategory(List<RingsCategoryBean> category) {
        this.category = category;
    }

    public List<RingsBean> getNewX() {
        return newX;
    }

    public void setNewX(List<RingsBean> newX) {
        this.newX = newX;
    }

    public List<RingsBean> getHot() {
        return hot;
    }

    public void setHot(List<RingsBean> hot) {
        this.hot = hot;
    }
}
