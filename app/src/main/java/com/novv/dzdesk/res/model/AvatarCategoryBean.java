package com.novv.dzdesk.res.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class AvatarCategoryBean implements Serializable {

    private static final long serialVersionUID = -4077482595124257851L;
    @SerializedName("_id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("img")
    private String img;

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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
