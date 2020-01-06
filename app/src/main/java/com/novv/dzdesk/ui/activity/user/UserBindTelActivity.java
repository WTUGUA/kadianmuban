package com.novv.dzdesk.ui.activity.user;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import com.ark.baseui.XAppCompatActivity;
import com.gyf.barlibrary.ImmersionBar;
import com.novv.dzdesk.Const;
import com.novv.dzdesk.R;
import com.novv.dzdesk.res.model.UserModel;
import com.novv.dzdesk.res.user.LoginContext;
import com.novv.dzdesk.ui.activity.WebUrlActivity;
import com.novv.dzdesk.ui.presenter.PresentUserBindTel;
import com.novv.dzdesk.util.DeviceUtil;
import com.novv.dzdesk.util.SelectorFactory;
import com.novv.dzdesk.util.ToastUtil;
import com.novv.dzdesk.util.UmUtil;
import com.umeng.analytics.MobclickAgent;

public class UserBindTelActivity extends XAppCompatActivity<PresentUserBindTel> implements
        View.OnClickListener {

    private EditText etPhone, etCode;
    private TextView btnCode, btnLogin;
    private TimeCount timeCount;
    private SweetAlertDialog pDialog;
    private View btnClear;

    @Nullable
    @Override
    public PresentUserBindTel newP() {
        return new PresentUserBindTel();
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_user_bind_tel;
    }

    @Override
    public void setUpView(@NonNull View rootView) {
        super.setUpView(rootView);
        btnCode = findViewById(R.id.btn_get_code);
        etCode = findViewById(R.id.et_input_code);
        etPhone = findViewById(R.id.et_input_phone);
        btnLogin = findViewById(R.id.btn_login);
        ImageView ivBack = findViewById(R.id.iv_back);
        btnClear = findViewById(R.id.btn_clear);
        btnClear.setVisibility(View.GONE);
        btnClear.setOnClickListener(this);
        btnCode.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                btnClear.setVisibility(TextUtils.isEmpty(s.toString().trim()) ? View.GONE : View.VISIBLE);
            }
        });

        initPolicy();
    }

    private void initPolicy() {
        TextView tvPolicy = findViewById(R.id.tv_policy);
        SpannableStringBuilder spannableSb = new SpannableStringBuilder();
        spannableSb.append("登录即代表同意 《隐私协议》 和 《用户协议》");
        ForegroundColorSpan spanColor = new ForegroundColorSpan(Color.parseColor("#07C0E9"));
        ClickableSpan spanClickable1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                WebUrlActivity.launch(UserBindTelActivity.this, Const.Policy.PRIVACY_POLICY, "隐私协议");
            }


            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(Color.parseColor("#07C0E9"));
                ds.setUnderlineText(false);
            }
        };
        ClickableSpan spanClickable2 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                WebUrlActivity.launch(UserBindTelActivity.this, Const.Policy.USER_POLICY, "用户协议");
            }


            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(Color.parseColor("#07C0E9"));
                ds.setUnderlineText(false);
            }
        };
        spannableSb.setSpan(spanColor, 8, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableSb.setSpan(spanClickable1, 8, 14, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableSb.setSpan(spanColor, 17, 23, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableSb.setSpan(spanClickable2, 17, 23, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvPolicy.setMovementMethod(LinkMovementMethod.getInstance());
        tvPolicy.setText(spannableSb);
    }


    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        ImmersionBar.with(this)
                .titleBar(findViewById(R.id.title_bar), false)
                .transparentStatusBar()
                .init();
        timeCount = new TimeCount(60000L, 1000L);
        UmUtil.anaOp(context, "login_telblinding_pageview");
        GradientDrawable loginDr = new GradientDrawable();
        loginDr.setColor(Color.parseColor("#05CAEE"));
        loginDr.setCornerRadius(DeviceUtil.dip2px(this, 50));
        btnLogin.setBackground(loginDr);
        btnCode.setBackground(SelectorFactory.newShapeSelector()
                .setStrokeWidth(DeviceUtil.dip2px(this, 1))
                .setDefaultBgColor(Color.parseColor("#FFFFFF"))
                .setDefaultStrokeColor(Color.parseColor("#05CAEE"))
                .setDisabledBgColor(Color.parseColor("#787878"))
                .setDisabledStrokeColor(Color.parseColor("#787878"))
                .setCornerRadius(DeviceUtil.dip2px(this, 17))
                .create());
        btnCode.setTextColor(SelectorFactory.newColorSelector()
                .setDefaultColor(Color.parseColor("#05CAEE"))
                .setDisabledColor(Color.parseColor("#FFFFFF"))
                .create());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.btn_login:
                UmUtil.anaOp(context, "login_telblinding_pagecontinue");
                bind(etPhone.getText().toString().trim(), etCode.getText().toString().trim());
                break;
            case R.id.btn_get_code:
                getLoginSms(etPhone.getText().toString());
                break;
            case R.id.btn_clear:
                etPhone.setText("");
                etPhone.requestFocus();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        UmUtil.anaOp(context, "login_telblinding_pageclose");
    }

    /**
     * 手机号验证
     *
     * @param phoneNum  手机号
     * @param checkCode 验证码
     */
    private void bind(String phoneNum, String checkCode) {
        if (TextUtils.isEmpty(phoneNum)) {
            ToastUtil.showToast(this, "请输入手机号");
            return;
        }
        if (phoneNum.length() < 11) {
            ToastUtil.showToast(this, "请输入正确的手机号");
            return;
        }
        if (TextUtils.isEmpty(checkCode)) {
            ToastUtil.showToast(this, "请输入验证码");
            return;
        }
        showProgress();
        getP().bindTel(phoneNum, checkCode);
    }

    public void setLoginSuccess(@NonNull UserModel userModel) {
        LoginContext.getInstance().logout();
        dismissProgress();
        finish();
    }

    public void setLoginFailed() {
        dismissProgress();
    }

    private void getLoginSms(String phoneNum) {
        if (TextUtils.isEmpty(phoneNum)) {
            ToastUtil.showToast(this, "请输入手机号");
            return;
        }
        if (phoneNum.length() < 11) {
            ToastUtil.showToast(this, "请输入正确的手机号");
            return;
        }
        getP().getSms(phoneNum);
    }

    public void setSmsGetSucess() {
        etCode.requestFocus();
        timeCount.start();
        ToastUtil.showToast(UserBindTelActivity.this, "发送成功");
    }

    private void showProgress() {
        if (pDialog == null) {
            pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        }
        if (isFinishing()) {
            return;
        }
        try {
            pDialog.setCancelable(true);
            pDialog.getProgressHelper()
                    .setBarColor(ContextCompat.getColor(this, R.color.color_accent));
            pDialog.setTitleText("正在加载，请稍后...");
            pDialog.setCanceledOnTouchOutside(false);
            pDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void dismissProgress() {
        try {
            if (pDialog != null && pDialog.isShowing()) {
                pDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        pDialog = null;
    }

    @Override
    protected void onResume() {
        MobclickAgent.onResume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        MobclickAgent.onPause(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgress();
        ImmersionBar.with(this).destroy();
    }

    /**
     * 计时器
     */
    private class TimeCount extends CountDownTimer {

        TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onFinish() {
            btnCode.setText("发送验证码");
            btnCode.setEnabled(true);
        }


        @SuppressLint("SetTextI18n")
        @Override
        public void onTick(long arg0) {
            btnCode.setText((arg0 / 1000) + "秒后重新获取");
            btnCode.setEnabled(false);
        }
    }
}
