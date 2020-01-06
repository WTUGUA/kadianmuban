package com.novv.dzdesk.res.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UploadMsg {
    @SerializedName("file_keys")
    private List<String> fileKeys;
    @SerializedName("token")
    private String token;

    public List<String> getFileKeys() {
        return fileKeys;
    }

    public void setFileKeys(List<String> fileKeys) {
        this.fileKeys = fileKeys;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
