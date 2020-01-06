package com.novv.dzdesk.res.model;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RingsBean implements Serializable {

    private static final long serialVersionUID = -576522086256826791L;
    @SerializedName("durl")
    private String durl;
    @SerializedName("name")
    private String name;
    @SerializedName("m4r")
    private String m4r;
    @SerializedName("during")
    private int during;
    @SerializedName("atime")
    private String atime;
    @SerializedName("desc")
    private String desc;
    @SerializedName("type")
    private String type;
    @SerializedName("id")
    private String id;
    @SerializedName("size")
    private int size;
    /**
     * 本地属性
     */
    private int progress;
    private int playState;
    private String filePath;
    private boolean isAdFlag;

    public static RingsBean objectFromData(String str) {

        return new Gson().fromJson(str, RingsBean.class);
    }

    public boolean isAdFlag() {
        return isAdFlag;
    }

    public void setAdFlag(boolean adFlag) {
        isAdFlag = adFlag;
    }

    public String getDurl() {
        return durl;
    }

    public void setDurl(String durl) {
        this.durl = durl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getM4r() {
        return m4r;
    }

    public void setM4r(String m4r) {
        this.m4r = m4r;
    }

    public int getDuring() {
        return during;
    }

    public void setDuring(int during) {
        this.during = during;
    }

    public String getAtime() {
        return atime;
    }

    public void setAtime(String atime) {
        this.atime = atime;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getPlayState() {
        return playState;
    }

    public void setPlayState(int playState) {
        this.playState = playState;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
