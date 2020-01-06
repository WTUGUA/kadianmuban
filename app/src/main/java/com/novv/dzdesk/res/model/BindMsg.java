package com.novv.dzdesk.res.model;

import com.google.gson.annotations.SerializedName;

public class BindMsg {

    @SerializedName("pass")
    private boolean pass;
    @SerializedName("user")
    private UserModel user;

    public boolean isPass() {
        return pass;
    }

    public void setPass(boolean pass) {
        this.pass = pass;
    }

    public UserModel getUser() {
        return user;
    }

    public void setUser(UserModel user) {
        this.user = user;
    }
}
