package com.novv.dzdesk.res.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by lijianglong on 2017/9/5.
 */

public class UserModel implements Serializable {

    private static final long serialVersionUID = -7295289718933263888L;
    @SerializedName("status")
    private String status;
    @SerializedName("openid")
    private String openid;
    @SerializedName("password")
    private String password;
    @SerializedName("tel")
    private String tel;
    @SerializedName("img")
    private String img;
    @SerializedName("auth")
    private String auth;
    @SerializedName(value = "id", alternate = "_id")
    private String userId;
    @SerializedName("nickname")
    private String nickname;
    @SerializedName("fav_count")
    private int favCount;
    @SerializedName("desc")
    private String desc;
    @SerializedName("active")
    private boolean active;
    @SerializedName("vip")
    private boolean vip;
    @SerializedName("viptime")
    private String vipTime;
    @SerializedName("tel_ver")
    private boolean needVerTel;
    @SerializedName("usefreevip")
    private boolean vipTry;
    @SerializedName("vipday")
    private int vipDays;
    @SerializedName("trial_num")
    private int trialNum;

    public int getVipDays() {
        return vipDays;
    }

    public void setVipDays(int vipDays) {
        this.vipDays = vipDays;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getFavCount() {
        return favCount;
    }

    public void setFavCount(int favCount) {
        this.favCount = favCount;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isVip() {
        return vip;
    }

    public void setVip(boolean vip) {
        this.vip = vip;
    }

    public String getVipTime() {
        return vipTime;
    }

    public void setVipTime(String vipTime) {
        this.vipTime = vipTime;
    }

    public boolean isNeedVerTel() {
        return !needVerTel;
    }

    public void setNeedVerTel(boolean needVerTel) {
        this.needVerTel = needVerTel;
    }

    public boolean isVipTry() {
        return !vipTry;
    }

    public void setVipTry(boolean vipTry) {
        this.vipTry = vipTry;
    }

    public int getTrialNum() {
        return trialNum;
    }

    public void setTrialNum(int trialNum) {
        this.trialNum = trialNum;
    }

    @Override public String toString() {
        final StringBuilder sb = new StringBuilder("UserModel{");
        sb.append("status='").append(status).append('\'');
        sb.append(", openid='").append(openid).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", tel='").append(tel).append('\'');
        sb.append(", img='").append(img).append('\'');
        sb.append(", auth='").append(auth).append('\'');
        sb.append(", userId='").append(userId).append('\'');
        sb.append(", nickname='").append(nickname).append('\'');
        sb.append(", favCount=").append(favCount);
        sb.append(", desc='").append(desc).append('\'');
        sb.append(", active=").append(active);
        sb.append(", vip=").append(vip);
        sb.append(", vipTime='").append(vipTime).append('\'');
        sb.append(", needVerTel=").append(needVerTel);
        sb.append(", vipTry=").append(vipTry);
        sb.append(", vipDays=").append(vipDays);
        sb.append(", trialNum=").append(trialNum);
        sb.append('}');
        return sb.toString();
    }
}
