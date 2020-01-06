package com.novv.dzdesk.ui.dialog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import com.novv.dzdesk.R;

@SuppressWarnings("deprecation")
public class DownloadDialog {

    private ProgressDialog progressDialog;
    private boolean isShowing = false;
    private OnCancelListener mOnCancelListener;

    public void setOnCancelListener(
            OnCancelListener onCancelListener) {
        mOnCancelListener = onCancelListener;
        if (progressDialog != null) {
            progressDialog.setCancelable(true);
        }
    }

    public boolean isShowing() {
        return isShowing;
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
        progressDialog.setMessage("正在下载...");
        progressDialog.setCancelable(false);
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

    public void setProgress(int index, int size) {
        if (progressDialog != null) {
            progressDialog.setMessage("正在下载第" + index + "个，共计" + size + "个");
        }
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
