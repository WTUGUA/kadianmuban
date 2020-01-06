package com.novv.dzdesk.http.callback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.Window;
import com.lzy.okgo.request.base.Request;

public abstract class DialogCallback<T> extends JsonCallback<T> {

    private ProgressDialog dialog;

    public DialogCallback(Activity activity) {
        super();
        initDialog(activity, "正在上传...");
    }

    public DialogCallback(Activity activity, String msg) {
        super();
        initDialog(activity, msg);
    }

    private void initDialog(Activity activity, String msg) {
        dialog = new ProgressDialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(msg);
    }

    @Override
    public void onStart(Request<T, ? extends Request> request) {
        if (dialog != null && !dialog.isShowing()) {
            dialog.show();
        }
    }

    @Override
    public void onFinish() {
        //网络请求结束后关闭对话框
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
