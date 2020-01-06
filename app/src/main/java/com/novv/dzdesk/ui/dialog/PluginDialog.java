package com.novv.dzdesk.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.*;
import android.widget.TextView;

import com.ark.adkit.basics.utils.AppUtils;
import com.ark.adkit.basics.utils.FileUtils;
import com.novv.dzdesk.R;
import com.novv.dzdesk.live.LwVideoLiveWallpaper;
import com.novv.dzdesk.util.FileUtil;
import com.novv.dzdesk.util.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class PluginDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private TextView tvDes1, tvDes2, tvDes3, btnDownload, btnSetOrigin, dialogTitle;

    public PluginDialog(@NonNull Context context) {
        this(context, R.style.UpdateDialog);
    }

    public PluginDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        this.context = context;
        init(context);
    }

    public PluginDialog showOrigin(boolean showOrigin) {
        if (btnSetOrigin != null) {
            btnSetOrigin.setVisibility(showOrigin ? View.VISIBLE : View.GONE);
        }
        return this;
    }

    public PluginDialog setTitle(String str) {
        if (dialogTitle != null) {
            dialogTitle.setText(str);
        }
        return this;
    }

    private void init(Context context) {
        View view = LayoutInflater
                .from(context).inflate(R.layout.dialog_plugin, null);
        setContentView(view);
        setWindowSize(context);
        initView(view);
    }

    private int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        if (wm == null) {
            return context.getResources().getDisplayMetrics().widthPixels;
        }
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            wm.getDefaultDisplay().getRealSize(point);
        } else {
            wm.getDefaultDisplay().getSize(point);
        }
        return point.x;
    }

    private void setWindowSize(Context context) {
        setCanceledOnTouchOutside(false);
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (getScreenWidth(context) * 0.90f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
    }

    private void initView(View view) {
        tvDes1 = view.findViewById(R.id.tv_des1);
        tvDes2 = view.findViewById(R.id.tv_des2);
        tvDes3 = view.findViewById(R.id.tv_des3);
        btnSetOrigin = view.findViewById(R.id.btn_set_origin);
        btnDownload = view.findViewById(R.id.btn_donwload);
        dialogTitle = view.findViewById(R.id.dialog_title);
        SpannableString ss1 = new SpannableString("减少壁纸消失概率，稳定性可提升90%");
        ss1.setSpan(new TextAppearanceSpan(context, R.style.text_style_orange), 13, ss1.length(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tvDes1.setText(ss1);
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            tvDes1.setLetterSpacing(0.05f);
        }

        SpannableString ss2 = new SpannableString("轻点唤起设置界面，桌面壁纸一键切换");
        ss2.setSpan(new TextAppearanceSpan(context, R.style.text_style_orange), 13, ss2.length(),
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tvDes2.setText(ss2);
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            tvDes2.setLetterSpacing(0.05f);
        }

        SpannableString ss3 = new SpannableString("壁纸声音1s开关，bgm想听就听");
        ss3.setSpan(new TextAppearanceSpan(context, R.style.text_style_orange), 4, 8,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tvDes3.setText(ss3);
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            tvDes3.setLetterSpacing(0.05f);
        }
        btnDownload.setOnClickListener(this);
        btnSetOrigin.setOnClickListener(this);
    }

    private void installSetPlug() {
        String plugPath = "wpplugin-release.apk";
        InputStream is = null;
        try {
            is = context.getAssets().open(plugPath);

            File plugFolder = FileUtils.getFileDir(context, "avideoshare/plug");
            File plugFile = new File(plugFolder, "wpplugin-release.apk");
            if (plugFile.exists()) {
                plugFile.delete();
            }
            plugFile.createNewFile();
            FileUtil.saveFileFromInputStream(is, plugFile);
            installApk(context, plugFile);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void installApk(Context context, File appFile) {
        if (AppUtils.isTargetOver(context, 26)
            && Build.VERSION.SDK_INT >= 26) {
            boolean hasInstallPermission = context.getPackageManager().canRequestPackageInstalls();
            if (!hasInstallPermission) {
                ToastUtil.showGeneralToastLongTime(context,"请选择彩蛋视频壁纸并允许安装应用");
                AppUtils.startInstallPermissionSettingActivity(context);
                return;
            }
        }
        try {
            Intent intent = AppUtils.getInstallApkIntent(context, appFile);
            if (context.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
                context.startActivity(intent);
            }
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        MobclickAgent.onEvent(context, "show_plugin");
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        switch (i) {
            case R.id.btn_set_origin:
                dismiss();
                if (context instanceof Activity) {
                    LwVideoLiveWallpaper.setToWallPaper((Activity) context);
                }
                break;
            case R.id.btn_donwload:
                MobclickAgent.onEvent(context, "download_plugin");
                dismiss();
                installSetPlug();
                break;
        }
    }
}
