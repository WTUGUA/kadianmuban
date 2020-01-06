package com.novv.dzdesk.res.model;

import android.text.TextUtils;
import com.google.gson.annotations.SerializedName;
import com.novv.dzdesk.util.PathUtils;

import java.io.*;

/**
 * Created by lingyfh on 2017/5/30.
 */
public class ResourceBean extends ItemBean {
    @SerializedName("id")
    private String uuid;
    @SerializedName("category")
    private String category;
    @SerializedName("img")
    private String coverURL;
    @SerializedName("name")
    private String name;
    @SerializedName("video")
    private String linkMp4;
    @SerializedName("view_video")
    private String previewMp4;
    private int type;//1 为广告
    @SerializedName("seturl")
    private String analyticSetURL;
    @SerializedName("downloadurl")
    private String analyticDownloadURL;
    @SerializedName("clickurl")
    private String analyticClickURL;
    @SerializedName("viewurl")
    private String analyticViewURL;
    @SerializedName("playurl")
    private String analyticPlayURL;
    @SerializedName("favoriteurl")
    private String analyticFavoriteURL;
    @SerializedName("shareurl")
    private String analyticShareURL;
    private boolean analyticViewed;
    private int analyticViewedIndex = -1000;
    @SerializedName("status")
    private String status;
    private boolean isUpload;
    private String cid;
    @SerializedName("uid")
    private String userId;
    @SerializedName("header")
    private String userAvatar;
    @SerializedName("nickname")
    private String userNickname;
    @SerializedName("duration")
    private int duration;
    @SerializedName("size")
    private double size;
    @SerializedName("share_video")
    private String share_video;
    @SerializedName("favnum")
    private int favnum;
    @SerializedName("hot")
    private String hot;

    private VModel vModel;
    @SerializedName("vip")
    private boolean isVipUser;

    public ResourceBean() {
        super();
    }

    public VModel getvModel() {
        return vModel;
    }

    public void setvModel(VModel vModel) {
        this.vModel = vModel;
    }

    public Object deepClone() {
        try {
            //将对象写到流里
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(this);
            //从流里读出来
            ByteArrayInputStream bi = new ByteArrayInputStream(bo.toByteArray());
            ObjectInputStream oi = new ObjectInputStream(bi);
            return (oi.readObject());
        } catch (Exception e) {
            //
        }
        return null;
    }

    public String getShare_video() {
        return share_video;
    }

    public void setShare_video(String share_video) {
        this.share_video = share_video;
    }

    public int getFavnum() {
        return favnum;
    }

    public void setFavnum(int favnum) {
        this.favnum = favnum;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public boolean isUpload() {
        return isUpload;
    }

    public void setUpload(boolean upload) {
        isUpload = upload;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isReview() {
        if (TextUtils.equals(getStatus(), "review")) {
            return true;
        }
        return false;
    }

    public boolean isPass() {
        if (TextUtils.equals(getStatus(), "pass")) {
            return true;
        }
        return false;
    }

    public boolean isReject() {
        if (TextUtils.equals(getStatus(), "refuse")) {
            return true;
        }
        return false;
    }

    public String get_id() {
        return uuid;
    }

    public void set_id(String _id) {
        this.uuid = _id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCoverURL() {
        return coverURL;
    }

    public void setCoverURL(String coverURL) {
        this.coverURL = coverURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLinkMp4() {
        return linkMp4;
    }

    public void setLinkMp4(String linkMp4) {
        this.linkMp4 = linkMp4;
    }

    public String getPreviewMp4() {
        return previewMp4;
    }

    public void setPreviewMp4(String previewMp4) {
        this.previewMp4 = previewMp4;
    }

    public String getAnalyticSetURL() {
        return analyticSetURL;
    }

    public void setAnalyticSetURL(String analyticSetURL) {
        this.analyticSetURL = analyticSetURL;
    }

    public String getAnalyticDownloadURL() {
        return analyticDownloadURL;
    }

    public void setAnalyticDownloadURL(String analyticDownloadURL) {
        this.analyticDownloadURL = analyticDownloadURL;
    }

    public String getAnalyticClickURL() {
        return analyticClickURL;
    }

    public void setAnalyticClickURL(String analyticClickURL) {
        this.analyticClickURL = analyticClickURL;
    }

    public String getAnalyticViewURL() {
        return analyticViewURL;
    }

    public void setAnalyticViewURL(String analyticViewURL) {
        this.analyticViewURL = analyticViewURL;
    }

    public String getAnalyticPlayURL() {
        return analyticPlayURL;
    }

    public void setAnalyticPlayURL(String analyticPlayURL) {
        this.analyticPlayURL = analyticPlayURL;
    }

    public String getAnalyticFavoriteURL() {
        return analyticFavoriteURL;
    }

    public String getHot() {
        return hot;
    }

    public void setHot(String hot) {
        this.hot = hot;
    }

    public void setAnalyticFavoriteURL(String analyticFavoriteURL) {
        this.analyticFavoriteURL = analyticFavoriteURL;
    }

    public String getAnalyticShareURL() {
        return analyticShareURL;
    }

    public void setAnalyticShareURL(String analyticShareURL) {
        this.analyticShareURL = analyticShareURL;
    }

    public boolean getAnalyticViewed() {
        return analyticViewed;
    }

    public void setAnalyticViewed(boolean analyticViewed) {
        this.analyticViewed = analyticViewed;
    }

    public int getAnalyticViewedIndex() {
        return analyticViewedIndex;
    }

    public void setAnalyticViewedIndex(int analyticViewedIndex) {
        this.analyticViewedIndex = analyticViewedIndex;
    }

    public File getMp4File() {
        String previewCacheDir = PathUtils.getLwpDownloadDir();
        return new File(previewCacheDir, get_id() + ".mp4");
    }

    public File getMp4TempFile() {
        String previewCacheDir = PathUtils.getLwpDownloadDir();
        return new File(previewCacheDir, get_id() + ".mp4.temp");
    }

    public boolean isVipUser() {
        return isVipUser;
    }

    public void setVipUser(boolean vipUser) {
        isVipUser = vipUser;
    }

    public int getContentType() {
        return type;
    }

    public void setContentType(int type) {
        this.type = type;
    }
}
