package com.novv.dzdesk.ui.dialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.novv.dzdesk.R;
import com.novv.dzdesk.res.model.UpdateBean;
import com.novv.dzdesk.util.CtxUtil;
import com.novv.dzdesk.util.LogUtil;

import java.io.File;

/**
 * 版本更新对话框 Created by lingyfh on 2017/6/13.
 */
public class UpdateFragment extends BaseDialogFragment {

    private static final String tag = UpdateFragment.class.getSimpleName();
    private static final String KEY_ITEM = "key_itme";
    private UpdateBean mItem;
    private TextView updateVersionName;
    private TextView updateSize;
    private TextView updateTime;
    private TextView updateExplain;
    private TextView updateCancelBtn;
    private TextView updateNowBtn;

    public static UpdateFragment launch(FragmentActivity activity,
            UpdateBean bean) {
        LogUtil.i(tag, "launch bean = " + bean);
        UpdateFragment splash = new UpdateFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_ITEM, bean);
        splash.setArguments(bundle);
        splash.show(activity, tag);
        return splash;
    }

    @Override
    public void setWindowSize(Window window) {
        setCancelable(true);
    }

    @Override
    public int setFragmentStyle() {
        return R.style.AppDialogDark;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.update_fragment;
    }

    @Override
    public void initView(View view) {
        updateVersionName = view.findViewById(R.id.apk_version_code_tv);
        updateSize = view.findViewById(R.id.apk_size_tv);
        updateTime = view.findViewById(R.id.update_time_tv);
        updateExplain = view.findViewById(R.id.update_msg_tv);
        updateCancelBtn = view.findViewById(R.id.updateCancelBtn);
        updateNowBtn = view.findViewById(R.id.updateNowBtn);
    }

    @Override
    public void handleArgument(Bundle argument) {
        super.handleArgument(argument);
        mItem = (UpdateBean) getArguments().getSerializable(KEY_ITEM);
    }

    @Override
    public void initData() {
        updateVersionName.setText(String
                .format(getActivity().getResources().getString(R.string.update_version),
                        mItem.getVersionName()));
        updateTime
                .setText(String.format(getActivity().getResources().getString(R.string.update_date),
                        mItem.getUpdateTime()));
        updateSize.setText(String
                .format(getActivity().getResources().getString(R.string.update_size),
                        mItem.getApkSize()));
        updateExplain.setText(mItem.getMsg());
        updateNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
                if (checkoutLocalApkFile(v.getContext(), mItem.getVersionCode())) {
                    return;
                }
                try {
                    LogUtil.i(tag, "Build.BRAND ==== " + Build.BRAND);
                    downloadApk(getActivity(), mItem.getUrl());
                } catch (Exception e) {
                    // 发生异常，使用内置下载更新
                    downloadApk(getActivity(), mItem.getUrl());
                }
            }
        });
        updateCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });
    }

    @Override
    public void pullData() {

    }

    private boolean checkoutLocalApkFile(Context context,
            int onlineVersionCode) {
        File file = new File(mItem.getFilePath());
        if (!file.exists()) {
            return false;
        }
        int versionCode = CtxUtil.getVersionCode(context, mItem.getFilePath());
        LogUtil.i(tag,
                "version code apk file = " + versionCode + " online version code = "
                        + onlineVersionCode);
        if (versionCode == onlineVersionCode) {
            CtxUtil.installApk(context, file);
            return true;
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    private void downloadApk(final Context context, String apkURL) {
        LogUtil.i(tag, "apk url = " + apkURL);
        if (TextUtils.isEmpty(apkURL)) {
            return;
        }

        final ProgressDialog pd = new ProgressDialog(context);
        pd.setCancelable(false);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在下载安装包，请稍后");
        pd.setTitle("版本升级");
        pd.show();

        final String tempFilePath = mItem.getTempFilePath();
        final String filePath = mItem.getFilePath();

        File tempFile = new File(tempFilePath);
        if (tempFile.exists()) {
            //noinspection ResultOfMethodCallIgnored
            tempFile.delete();
        }

        File file = new File(filePath);
        if (file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.delete();
        }

        FileDownloadListener listener = new FileDownloadListener() {
            @Override
            protected void pending(BaseDownloadTask task, int soFarBytes,
                    int totalBytes) {
                LogUtil.i(tag, "pending  = " + soFarBytes + "/" + totalBytes);
            }

            @Override
            protected void connected(BaseDownloadTask task, String etag,
                    boolean isContinue, int soFarBytes, int totalBytes) {
                LogUtil.i(tag, "connected isContinue = " + isContinue
                        + " etag = " + etag + soFarBytes + "/" + totalBytes);
            }

            @Override
            protected void progress(BaseDownloadTask task, int soFarBytes,
                    int totalBytes) {
                LogUtil.i(tag, "progress  = " + soFarBytes + "/" + totalBytes);
                int progress = (int) ((float) soFarBytes / (float) totalBytes * 100);

                pd.setProgress(progress);
            }

            @Override
            protected void blockComplete(BaseDownloadTask task) {
                LogUtil.i(tag, "blockComplete");
            }

            @Override
            protected void retry(final BaseDownloadTask task,
                    final Throwable ex, final int retryingTimes,
                    final int soFarBytes) {
                LogUtil.i(tag, "retry retryingTimes = " + retryingTimes
                        + " soFarBytes = " + soFarBytes);
            }

            @Override
            protected void completed(BaseDownloadTask task) {
                LogUtil.i(tag, "completed");

                File downloadTempFile = new File(tempFilePath);
                File downloadFile = new File(filePath);
                if (downloadTempFile.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    downloadTempFile.renameTo(downloadFile);
                }

                CtxUtil.installApk(context, downloadFile);
                pd.dismiss();
            }

            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes,
                    int totalBytes) {
                LogUtil.i(tag, "paused  = " + soFarBytes + "/" + totalBytes);
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                if (e != null) {
                    e.printStackTrace();
                }
                LogUtil.i(tag,
                        "error  = " + (e == null ? "null" : e.getMessage()));
                pd.dismiss();
            }

            @Override
            protected void warn(BaseDownloadTask task) {
                LogUtil.i(tag, "warn");
            }
        };

        FileDownloader.getImpl().create(apkURL).setPath(tempFilePath)
                .setListener(listener).start();
    }

}
