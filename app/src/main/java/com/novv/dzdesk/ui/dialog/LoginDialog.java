package com.novv.dzdesk.ui.dialog;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import com.novv.dzdesk.R;
import com.novv.dzdesk.ui.activity.user.UserLoginActivity;

/**
 * 提示登录验证的对话框，收藏，点赞，评论，上传 Created by lijianglong on 2017/9/7.
 */

public class LoginDialog extends BaseDialogFragment implements View.OnClickListener {

    private static final String tag = LoginDialog.class.getSimpleName();

    private TextView tvTitle;
    private TextView tvDesc;
    private TextView tvLogin;
    private ImageView ivClose;

    private String msg;

    public static void launch(FragmentActivity activity, String msg) {
        LoginDialog fragment = new LoginDialog();
        Bundle bundle = new Bundle();
        bundle.putString("msg", msg);
        fragment.setArguments(bundle);
        fragment.show(activity, tag);
    }

    @Override
    public void handleArgument(Bundle argument) {
        super.handleArgument(argument);
        msg = argument.getString("msg");
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
        return R.layout.dialog_login;
    }

    @Override
    public void initView(View view) {
        tvLogin = (TextView) view.findViewById(R.id.btn_sure);
        ivClose = (ImageView) view.findViewById(R.id.btn_close);
        tvTitle = (TextView) view.findViewById(R.id.tv_title);
        tvDesc = (TextView) view.findViewById(R.id.tv_desc);
    }

    @Override
    public void initData() {
        ivClose.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
        setTvDesc(msg);
    }

    @Override
    public void pullData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sure:
                UserLoginActivity.launch(mContext);
                dismissAllowingStateLoss();
                break;
            case R.id.btn_close:
                dismissAllowingStateLoss();
                break;
        }
    }

    public void setTvTitle(String title) {
        tvTitle.setText(title);
    }

    public void setTvDesc(String desc) {
        tvDesc.setText(desc);
    }
}
