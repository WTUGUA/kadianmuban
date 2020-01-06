package com.novv.dzdesk.res.user;

import android.content.Context;
import android.content.Intent;
import com.novv.dzdesk.res.model.ResourceBean;
import com.novv.dzdesk.res.model.VModel;
import com.novv.dzdesk.ui.activity.user.UserAEMakeListActivity;
import com.novv.dzdesk.ui.activity.user.UserDownloadActivity;
import com.novv.dzdesk.ui.activity.user.UserFavorActivity;
import com.novv.dzdesk.ui.activity.user.UserUploadActivity;

import java.util.List;

public class LoginState implements UserState {

    @Override
    public void userFavorList(Context context) {
        context.startActivity(new Intent(context, UserFavorActivity.class));
    }

    @Override
    public void userUploadList(Context context) {
        context.startActivity(new Intent(context, UserUploadActivity.class));
    }

    @Override
    public void userDownloadList(Context context) {
        context.startActivity(new Intent(context, UserDownloadActivity.class));
    }

    @Override
    public void userMakeList(Context context){
        context.startActivity(new Intent(context, UserAEMakeListActivity.class));
    }

    @Override
    public <T> void doComment(Context context, ResourceBean resourceBean, StateCallback<T> callback) {

    }

    @Override
    public <T> void doFavor(Context context, ResourceBean resourceBean, StateCallback<T> callback) {

    }

    @Override
    public <T> void doPraise(Context context, ResourceBean resourceBean, StateCallback<T> callback) {

    }

    @Override
    public void doDownload(Context context, ResourceBean resourceBean) {

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
                    stateCallback.onVipFree(isVip,true,LoginContext.getInstance().getTrialNum());
                } else {
                    stateCallback.onFree();
                }
            }
        }
    }
}
