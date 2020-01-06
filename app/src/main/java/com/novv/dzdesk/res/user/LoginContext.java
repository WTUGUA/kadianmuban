package com.novv.dzdesk.res.user;

import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.GsonBuilder;
import com.novv.dzdesk.res.model.ResourceBean;
import com.novv.dzdesk.res.model.UserModel;
import com.novv.dzdesk.res.model.VModel;
import com.tencent.mmkv.MMKV;

import java.util.List;

public class LoginContext implements UserState {

    private UserState state;
    private UserModel mUserModel = null;

    private LoginContext() {
        MMKV mmkv = MMKV.mmkvWithID("user", MMKV.MULTI_PROCESS_MODE);
        String userStr = mmkv.decodeString("userModel", "");
        if (!TextUtils.isEmpty(userStr)) {
            try {
                mUserModel = new GsonBuilder().create().fromJson(userStr, UserModel.class);
            } catch (Exception e) {
                //
            }
        }
        state = mUserModel == null ? new LogoutState() : new LoginState();
    }

    public static LoginContext getInstance() {
        return SingletonHolder.instance;
    }

    @Override
    public void userFavorList(Context context) {
        state.userFavorList(context);
    }

    @Override
    public void userUploadList(Context context) {
        state.userUploadList(context);
    }

    @Override
    public void userDownloadList(Context context) {
        state.userDownloadList(context);
    }

    @Override
    public void userMakeList(Context context) {
        state.userMakeList(context);
    }

    @Override
    public <T> void doComment(Context context, ResourceBean resourceBean,
                              StateCallback<T> callback) {
        state.doComment(context, resourceBean, callback);
    }

    @Override
    public <T> void doFavor(Context context, ResourceBean resourceBean, StateCallback<T> callback) {
        state.doFavor(context, resourceBean, callback);
    }

    @Override
    public <T> void doPraise(Context context, ResourceBean resourceBean,
                             StateCallback<T> callback) {
        state.doPraise(context, resourceBean, callback);
    }

    @Override
    public void doDownload(Context context, ResourceBean resourceBean) {
        state.doDownload(context, resourceBean);
    }

    @Override
    public <T> void doUpload(Context context, List<String> ids, StateCallback<T> callback) {
        state.doUpload(context, ids, callback);
    }

    @Override
    public void checkAEState(Context context, VModel vModel, AEStateCallback stateCallback) {
        state.checkAEState(context, vModel, stateCallback);
    }

    @Nullable
    public UserModel getUser() {
        return mUserModel;
    }

    public boolean isLogin() {
        return mUserModel != null;
    }

    public boolean isVip() {
        return mUserModel != null && mUserModel.isVip();
    }

    public int getTrialNum() {
        if (mUserModel != null) {
            return mUserModel.getTrialNum();
        } else {
            return 0;
        }
    }

    public void setTrialNum(int num) {
        if (mUserModel != null) {
            mUserModel.setTrialNum(num);
        }
    }

    public void login(UserModel userModel) {
        if (userModel == null) {
            return;
        }
        mUserModel = userModel;
        state = new LoginState();
        MMKV mmkv = MMKV.mmkvWithID("user", MMKV.MULTI_PROCESS_MODE);
        String userStr = new GsonBuilder().create().toJson(mUserModel);
        mmkv.encode("userModel", userStr);
    }

    public void logout() {
        mUserModel = null;
        state = new LogoutState();
        MMKV mmkv = MMKV.mmkvWithID("user", MMKV.MULTI_PROCESS_MODE);
        mmkv.encode("userModel", "");
    }

    private static class SingletonHolder {

        private static final LoginContext instance = new LoginContext();
    }
}
