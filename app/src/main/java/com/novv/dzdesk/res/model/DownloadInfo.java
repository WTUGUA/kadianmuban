package com.novv.dzdesk.res.model;

import com.google.gson.annotations.SerializedName;

public class DownloadInfo {

    @SerializedName("_id")
    private String id;
    @SerializedName("info")
    private ResourceBean info;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ResourceBean getInfo() {
        return info;
    }

    public void setInfo(ResourceBean info) {
        this.info = info;
    }
}
