package com.novv.dzdesk.res.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AvatarResult {

    @SerializedName("recommend")
    private List<RcmdBean> recommendList;
    @SerializedName("avatar")
    private List<AvatarBean> avatarList;
    @SerializedName("category")
    private List<AvatarCategoryBean> categoryList;

    public List<RcmdBean> getRecommendList() {
        return recommendList;
    }

    public void setRecommendList(List<RcmdBean> recommendList) {
        this.recommendList = recommendList;
    }

    public List<AvatarBean> getAvatarList() {
        return avatarList;
    }

    public void setAvatarList(List<AvatarBean> avatarList) {
        this.avatarList = avatarList;
    }

    public List<AvatarCategoryBean> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<AvatarCategoryBean> categoryList) {
        this.categoryList = categoryList;
    }
}
