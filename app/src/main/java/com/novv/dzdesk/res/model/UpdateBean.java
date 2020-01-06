package com.novv.dzdesk.res.model;

import com.google.gson.annotations.SerializedName;
import com.novv.dzdesk.Const;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lingyfh on 2017/7/3.
 */
public class UpdateBean extends ItemBean {

    @SerializedName("versionCode")
    private int versionCode;
    @SerializedName("size")
    private long apkSize;
    @SerializedName("url")
    private String url;
    @SerializedName("updateMessage")
    private String msg;
    @SerializedName("time")
    private String updateTime;
    private String dir = Const.Dir.APK_DOWNLOAD;
    private String apkFileName = "video_wp_last.apk";
    private String apkTempFileName = "video_wp_last.apk.temp";
    private String versionName;

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getUpdateTime() {
        return getUpdateTime(updateTime);
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getApkSize() {
        return formatFileSize(apkSize);
    }

    public void setApkSize(long apkSize) {
        this.apkSize = apkSize;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    private String getUpdateTime(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM月dd日");
        String dateString = strDate;
        try {
            Date date = formatter.parse(strDate);
            dateString = simpleDateFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dateString;
    }

    public String getTempFilePath() {
        return getApkDirPath() + File.separator + apkTempFileName;
    }

    private String getApkDirPath() {
        File file = new File(dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (!file.exists()) {
            file.mkdirs();
        }
        return dir;
    }

    public String getFilePath() {
        return getApkDirPath() + File.separator + apkFileName;
    }

    private String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }
}
