package com.novv.dzdesk.res.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AvatarBean implements Serializable {

    private static final long serialVersionUID = -1684234546779221416L;
    @SerializedName("atime")
    private Long time;
    @SerializedName("ncos")
    private int ncos;
    @SerializedName("thumb")
    private String thumb;
    @SerializedName("rank")
    private int rank;
    @SerializedName("name")
    private String name;
    @SerializedName("view")
    private int view;
    @SerializedName("favs")
    private int favs;
    @SerializedName("id")
    private String idStr;
    @SerializedName("desc")
    private String desc;
    private boolean flagIsAd;

    public AvatarBean() {
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public int getNcos() {
        return ncos;
    }

    public void setNcos(int ncos) {
        this.ncos = ncos;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public int getFavs() {
        return favs;
    }

    public void setFavs(int favs) {
        this.favs = favs;
    }

    public String getIdStr() {
        return idStr;
    }

    public void setIdStr(String idStr) {
        this.idStr = idStr;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean getFlagIsAd() {
        return this.flagIsAd;
    }

    public void setFlagIsAd(boolean flagIsAd) {
        this.flagIsAd = flagIsAd;
    }
}
