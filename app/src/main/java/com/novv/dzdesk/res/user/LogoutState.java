package com.novv.dzdesk.res.user;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import com.novv.dzdesk.res.model.ResourceBean;
import com.novv.dzdesk.res.model.VModel;
import com.novv.dzdesk.ui.activity.user.UserLoginActivity;
import com.novv.dzdesk.ui.dialog.LoginDialog;
import com.novv.dzdesk.util.UmUtil;

import java.util.List;

public class LogoutState implements UserState {

    @Override
    public void userFavorList(Context context) {
        UmUtil.anaLoginFrom(context,"我的收藏");
        startLogin(context, "登录后查看收藏内容~");
    }

    @Override
    public void userUploadList(Context context) {
        UmUtil.anaLoginFrom(context,"我的上传");
        startLogin(context, "登录后查看上传内容~");
    }

    @Override
    public void userDownloadList(Context context) {
        UmUtil.anaLoginFrom(context,"我的下载");
        startLogin(context, "登录后查看下载内容~");
    }

    @Override
    public void userMakeList(Context context){
        UmUtil.anaLoginFrom(context,"我的制作");
        startLogin(context, "登录后才查看制作内容~");
    }

    @Override
    public <T> void doComment(Context context, ResourceBean resourceBean, StateCallback<T> callback) {
        UmUtil.anaLoginFrom(context,"评论");
        startLogin(context, "登录后才可以评论哦~");
    }

    @Override
    public <T> void doFavor(Context context, ResourceBean resourceBean, StateCallback<T> callback) {
        UmUtil.anaLoginFrom(context,"收藏");
        startLogin(context, "登录后才可以收藏哦~");
    }

    @Override
    public <T> void doPraise(Context context, ResourceBean resourceBean, StateCallback<T> callback) {
        UmUtil.anaLoginFrom(context,"点赞");
        startLogin(context, "登录后才可以点赞哦~");
    }

    @Override
    public void doDownload(Context context, ResourceBean resourceBean) {
        UmUtil.anaLoginFrom(context,"下载");
        startLogin(context, null);
    }

    @Override
    public <T> void doUpload(Context context, List<String> ids, StateCallback<T> callback) {

    }

    @Override
    public void checkAEState(Context context, VModel vModel, AEStateCallback stateCallback) {
        if (context == null || vModel == null || stateCallback == null) {
            return;
        }
        if (vModel.free) {
            stateCallback.onLimitFree();
        } else {
            boolean isPrice = vModel.price != 0;
            boolean isVip = LoginContext.getInstance().isVip();
            boolean needVip = vModel.needVip;
            if (isPrice) {
                stateCallback.onPriceNeed();
            } else {
                if (needVip) {
                    stateCallback.onVipFree(isVip,false,0);
                } else {
                    stateCallback.onFree();
                }
            }
        }
    }

    private void startLogin(Context context, String msg) {
        if (TextUtils.isEmpty(msg)) {
            UserLoginActivity.launch(context);
        } else {
            LoginDialog.launch((FragmentActivity) context, msg);
        }
    }
}
