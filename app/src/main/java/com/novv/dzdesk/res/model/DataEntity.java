package com.novv.dzdesk.res.model;

import android.text.TextUtils;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public class DataEntity {

    @SerializedName("index")
    private String index;
    @SerializedName("img")
    private String img;
    @SerializedName("title")
    private String title;
    @SerializedName("value")
    private String value;
    @SerializedName("_id")
    private String id;
    @SerializedName("type")
    private String type;

    public static DataEntity objectFromData(String str) {

        return new Gson().fromJson(str, DataEntity.class);
    }

    public boolean isVideoBanner() {
        return TextUtils.equals(type, "video") && !TextUtils.isEmpty(value);
    }

    public boolean isWebBanner() {
        return TextUtils.equals(type, "url") && !TextUtils.isEmpty(value);
    }

    public boolean isAppBanner() {
        return TextUtils.equals(type, "apk") && !TextUtils.isEmpty(value);
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
