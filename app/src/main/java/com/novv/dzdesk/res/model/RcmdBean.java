package com.novv.dzdesk.res.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class RcmdBean implements Serializable {

    private static final long serialVersionUID = 2016760255473047222L;
    @SerializedName("_id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("title")
    private String title;
    @SerializedName("items")
    private List<AvatarBean> items;
    @SerializedName("stime")
    private long stime;
    @SerializedName("atime")
    private long atime;
    @SerializedName("type")
    private int type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<AvatarBean> getItems() {
        return items;
    }

    public void setItems(List<AvatarBean> items) {
        this.items = items;
    }

    public long getStime() {
        return stime;
    }

    public void setStime(long stime) {
        this.stime = stime;
    }

    public long getAtime() {
        return atime;
    }

    public void setAtime(long atime) {
        this.atime = atime;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
