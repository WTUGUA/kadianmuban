package com.novv.dzdesk.ui.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import com.novv.dzdesk.R;

@SuppressWarnings("deprecation")
public class ProcessDialog {

    private ProgressDialog progressDialog;
    private boolean isShowing = false;
    private String msg = "正在处理...";
    private OnCancelListener mOnCancelListener;

    public boolean isShowing() {
        return isShowing;
    }

    public ProcessDialog setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public void setOnCancelListener(
            OnCancelListener onCancelListener) {
        mOnCancelListener = onCancelListener;
        if (progressDialog != null) {
            progressDialog.setCancelable(true);
        }
    }

    public void show(Activity activity) {
        release();
        if (activity.isFinishing()) {
            return;
        }
        progressDialog = new ProgressDialog(activity, R.style.AppDialogLightWhite);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgressDrawable(
                ContextCompat.getDrawable(activity, R.drawable.common_progressbar_bg));
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (mOnCancelListener != null) {
                    mOnCancelListener.onCancel();
                }
            }
        });
        progressDialog.show();
        isShowing = true;
    }

    public void setProgress(int percent) {
        if (percent >= 0 && percent <= 100) {
            progressDialog.setMessage(msg + percent + "%");
        }
    }

    public void show(int percent, Activity activity) {
        if (progressDialog == null) {
            show(activity);
        }
        progressDialog.setMessage(msg + percent + "%");
    }

    public void release() {
        if (progressDialog != null) {
            progressDialog.cancel();
            isShowing = false;
            progressDialog = null;
        }
    }

    public interface OnCancelListener {

        void onCancel();
    }
}
