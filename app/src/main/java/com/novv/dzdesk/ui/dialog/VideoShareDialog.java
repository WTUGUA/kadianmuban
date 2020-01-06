package com.novv.dzdesk.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.novv.dzdesk.R;
import com.novv.dzdesk.util.PlatformUtil;
import com.novv.dzdesk.util.UmUtil;

public class VideoShareDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private String videoFile;

    public VideoShareDialog(@NonNull Context context) {
        this(context, R.style.AppDialogLight);
    }

    public VideoShareDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.dialog_video_share, null);
        setContentView(view);
        Window window = getWindow();
        if (window != null) {
            WindowManager m = window.getWindowManager();
            WindowManager.LayoutParams p = window.getAttributes();
            p.gravity = Gravity.BOTTOM;
            Point size = new Point();
            m.getDefaultDisplay().getSize(size);
            p.width = size.x;
            window.setAttributes(p);
        }
        initView();
    }

    private void initView() {
        View btnClose = findViewById(R.id.btn_close);
        View btnShareWx = findViewById(R.id.btn_share_wx);
        View btnShareQq = findViewById(R.id.btn_share_qq);
        View btnShareMore = findViewById(R.id.btn_share_more);
        btnClose.setOnClickListener(this);
        btnShareWx.setOnClickListener(this);
        btnShareQq.setOnClickListener(this);
        btnShareMore.setOnClickListener(this);
    }

    public void showShare(String videoFile) {
        if (!isShowing()) {
            show();
        }
        this.videoFile = videoFile;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_close:
                dismiss();
                break;
            case R.id.btn_share_more:
                UmUtil.anaDIYClickShare(mContext, "系统");
                PlatformUtil.shareSystem(mContext, videoFile, "video/*");
                dismiss();
                break;
            case R.id.btn_share_wx:
                UmUtil.anaDIYClickShare(mContext, "微信");
                PlatformUtil.shareWxSession(mContext, videoFile, "video/*");
                dismiss();
                break;
            case R.id.btn_share_qq:
                UmUtil.anaDIYClickShare(mContext, "QQ");
                PlatformUtil.shareToQQ(mContext, videoFile, "video/*");
                dismiss();
                break;
        }
    }
}
