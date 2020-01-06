package com.novv.dzdesk.res.user;

import android.content.Context;

import com.novv.dzdesk.res.model.ResourceBean;
import com.novv.dzdesk.res.model.VModel;

import java.util.List;

public interface UserState {

    void userFavorList(Context context);

    void userUploadList(Context context);

    void userDownloadList(Context context);

    void userMakeList(Context context);

    <T> void doComment(Context context, ResourceBean resourceBean, StateCallback<T> callback);

    <T> void doFavor(Context context, ResourceBean resourceBean, StateCallback<T> callback);

    <T> void doPraise(Context context, ResourceBean resourceBean, StateCallback<T> callback);

    void doDownload(Context context, ResourceBean resourceBean);

    <T> void doUpload(Context context, List<String> ids, StateCallback<T> callback);

    void checkAEState(Context context, VModel vModel, AEStateCallback stateCallback);
}
