package com.novv.dzdesk.ui.activity.user;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import cn.pedant.SweetAlert.SweetAlertDialog;

import com.ark.auth.Auth;
import com.ark.auth.AuthCallback;
import com.ark.auth.UserInfoForThird;
import com.ark.baseui.XAppCompatActivity;
import com.gyf.barlibrary.ImmersionBar;
import com.novv.dzdesk.Const;
import com.novv.dzdesk.R;
import com.novv.dzdesk.http.BaseObserver;
import com.novv.dzdesk.http.DefaultScheduler;
import com.novv.dzdesk.http.ServerApi;
import com.novv.dzdesk.http.SimpleObserver;
import com.novv.dzdesk.res.event.EventCode;
import com.novv.dzdesk.res.model.BaseResult;
import com.novv.dzdesk.res.model.UserModel;
import com.novv.dzdesk.res.user.LoginContext;
import com.novv.dzdesk.rxbus2.RxBus;
import com.novv.dzdesk.ui.activity.WebUrlActivity;
import com.novv.dzdesk.util.DeviceUtil;
import com.novv.dzdesk.util.SelectorFactory;
import com.novv.dzdesk.util.ShareUtils;
import com.novv.dzdesk.util.ToastUtil;
import com.novv.dzdesk.util.UmConst;
import com.novv.dzdesk.util.UmUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 用户登陆页面 Created by lijianglong on 2017/8/31.
 */
public class UserLoginActivity extends XAppCompatActivity implements
        View.OnClickListener, UmConst {

    private EditText etPhone, etCode;
    private TextView btnCode, btnLogin;
    private TimeCount timeCount;
    private SweetAlertDialog pDialog;
    private View btnClear;
    private UserInfoForThird userInfoForThird;
    private int type;

    public static void launch(Context context) {
        Intent intent = new Intent(context, UserLoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_user_login;
    }

    @Override
    public void setUpView(@NonNull View rootView) {
        super.setUpView(rootView);
        ImageView mQQIv = findViewById(R.id.btn_login_qq);
        ImageView mWeiXinIv = findViewById(R.id.btn_login_wx);
        btnCode = findViewById(R.id.btn_get_code);
        etCode = findViewById(R.id.et_input_code);
        etPhone = findViewById(R.id.et_input_phone);
        btnLogin = findViewById(R.id.btn_login);
        ImageView ivBack = findViewById(R.id.iv_back);
        btnClear = findViewById(R.id.btn_clear);
        btnClear.setOnClickListener(this);
        btnClear.setVisibility(View.GONE);
        btnCode.setOnClickListener(this);
        mWeiXinIv.setOnClickListener(this);
        mQQIv.setOnClickListener(this);
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
        timeCount = new TimeCount(60000L, 1000L);

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
                WebUrlActivity.launch(UserLoginActivity.this, Const.Policy.PRIVACY_POLICY, "隐私协议");
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
                WebUrlActivity.launch(UserLoginActivity.this, Const.Policy.USER_POLICY, "用户协议");
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


    @Nullable
    @Override
    public Object newP() {
        return null;
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        ImmersionBar.with(this)
                .titleBar(findViewById(R.id.iv_back))
                .navigationBarColor(R.color.color_primary_dark)
                .init();

        GradientDrawable loginDr = new GradientDrawable();
        loginDr.setColor(Color.parseColor("#05CAEE"));
        loginDr.setCornerRadius(DeviceUtil.dip2px(this, 22));
        btnLogin.setBackground(loginDr);
        btnCode.setBackground(SelectorFactory.newShapeSelector()
                .setStrokeWidth(DeviceUtil.dip2px(this, 1))
                .setDefaultBgColor(Color.parseColor("#FFFFFF"))
                .setDefaultStrokeColor(Color.parseColor("#07C0E9"))
                .setDisabledBgColor(Color.parseColor("#DDDDDD"))
                .setDisabledStrokeColor(Color.parseColor("#DDDDDD"))
                .setCornerRadius(DeviceUtil.dip2px(this, 16))
                .create());
        btnCode.setTextColor(SelectorFactory.newColorSelector()
                .setDefaultColor(Color.parseColor("#07C0E9"))
                .setDisabledColor(Color.parseColor("#FFFFFF"))
                .create());

        SpannableString ssPwd =
                new SpannableString(getResources().getString(R.string.login_et_pwd_hint));
        AbsoluteSizeSpan assPwd = new AbsoluteSizeSpan(13, true);
        ssPwd.setSpan(assPwd, 0, ssPwd.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        etCode.setHint(new SpannedString(ssPwd));

        SpannableString ssPhone =
                new SpannableString(getResources().getString(R.string.login_et_phone_hint));
        AbsoluteSizeSpan assPhone = new AbsoluteSizeSpan(13, true);
        ssPhone.setSpan(assPhone, 0, ssPhone.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        etPhone.setHint(new SpannedString(ssPhone));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login_qq:
                if (!ShareUtils.isQQClientAvailable(this)) {
                    ToastUtil.showToast(this, "请先安装手机QQ客户端");
                    return;
                }
                showProgress();
                Auth.withQQ(this)
                        .setAction(Auth.Login)
                        .build(new AuthCallback() {
                            @Override
                            public void onSuccessForLogin(@NonNull UserInfoForThird info) {
                                super.onSuccessForLogin(info);
                                login(info, Auth.WithQQ);
                            }

                            @Override
                            public void onFailed(@NonNull String code, @NonNull String msg) {
                                super.onFailed(code, msg);
                                dismissProgress();
                            }

                            @Override
                            public void onCancel() {
                                super.onCancel();
                                dismissProgress();
                            }
                        });
                break;
            case R.id.btn_login_wx:
                showProgress();
                Auth.withWX(this)
                        .setAction(Auth.Login)
                        .build(new AuthCallback() {
                            @Override
                            public void onSuccessForLogin(@NonNull UserInfoForThird info) {
                                super.onSuccessForLogin(info);
                                dismissProgress();
                                login(info, Auth.WithWX);
                            }

                            @Override
                            public void onFailed(@NonNull String code, @NonNull String msg) {
                                super.onFailed(code, msg);
                                dismissProgress();
                            }

                            @Override
                            public void onCancel() {
                                super.onCancel();
                                dismissProgress();
                            }
                        });
                break;
            case R.id.btn_get_code:
                getLoginSms(etPhone.getText().toString());
                break;
            case R.id.btn_login:
                login(etPhone.getText().toString().trim(), etCode.getText().toString().trim());
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.btn_clear:
                etPhone.setText("");
                etPhone.requestFocus();
                break;
            default:
                break;
        }
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
        ServerApi.getLoginSms(phoneNum)
                .compose(DefaultScheduler.<BaseResult>getDefaultTransformer())
                .subscribe(new SimpleObserver() {
                    @Override
                    public void onSuccess() {
                        etCode.requestFocus();
                        timeCount.start();
                        ToastUtil.showToast(UserLoginActivity.this, "发送成功");
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        ToastUtil.showToast(UserLoginActivity.this, message);
                    }
                });
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

    /**
     * 手机号登录
     *
     * @param phoneNum  手机号
     * @param checkCode 验证码
     */
    private void login(String phoneNum, String checkCode) {
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
        ServerApi.login(phoneNum, checkCode)
                .compose(DefaultScheduler.<BaseResult<UserModel>>getDefaultTransformer())
                .subscribe(new BaseObserver<UserModel>() {
                    @Override
                    public void onSuccess(@NonNull UserModel userModel) {
                        UmUtil.anaLoginOp(UserLoginActivity.this, LOGIN_TEL_SUCCESS);
                        dismissProgress();
                        onLoginEvent(userModel);
                    }

                    @Override
                    public void onFailure(int code, @NonNull String message) {
                        UmUtil.anaLoginOp(UserLoginActivity.this, LOGIN_TEL_FAIL);
                        dismissProgress();
                        ToastUtil.showToast(UserLoginActivity.this, message);
                    }
                });
    }

    /**
     * 三方登录
     *
     * @param platformInfo 三方用户信息
     */
    private void login(final UserInfoForThird platformInfo, final int type) {
        this.userInfoForThird = platformInfo;
        this.type = type;
        String auth = "";
        String token = "";
        if (type == Auth.WithWX) {
            UmUtil.anaLoginOp(UserLoginActivity.this, LOGIN_WX_SUCCESS);
            auth = "weixin";
            token = platformInfo.aToken;
        } else if (type == Auth.WithQQ) {
            UmUtil.anaLoginOp(UserLoginActivity.this, LOGIN_QQ_SUCCESS);
            auth = "qq";
            token = platformInfo.aToken;
        }
        ServerApi.login(platformInfo.nickname, auth, platformInfo.openid, platformInfo.portrait, token)
                .compose(DefaultScheduler.<BaseResult<UserModel>>getDefaultTransformer())
                .subscribe(new BaseObserver<UserModel>() {
                    @Override
                    public void onSuccess(@NonNull UserModel userModel) {
                        dismissProgress();
                        onLoginEvent(userModel);
                    }

                    @Override
                    public void onFailure(int code, @NonNull String message) {
                        dismissProgress();
                        ToastUtil.showToast(UserLoginActivity.this, message);
                    }
                });
    }

    /**
     * 登录事件
     *
     * @param userModel UserModel
     */
    private void onLoginEvent(UserModel userModel) {
        LoginContext.getInstance().login(userModel);
        if (userModel.isVip()) {
            RxBus.getDefault().send(EventCode.VIEW_VIP_LOGIN, userModel);
        }
        if (userModel.isVipTry() && userModel.isNeedVerTel()) {
            Intent intent = new Intent(context, UserBindTelActivity.class);
            intent.putExtra("userModel", userModel);
            startActivityForResult(intent, 333);
        } else if (TextUtils.isEmpty(userModel.getNickname())) {
            UserInfoEditActivity.launch(this);
            finish();
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 333) {
            UserModel userModel = LoginContext.getInstance().getUser();
            if (userModel == null && userInfoForThird != null) {
                showProgress();
                getVDelegate().toastShort("正在重新登录");
                login(userInfoForThird, type);
            } else {
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissProgress();
        ImmersionBar.with(this).destroy();
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
            btnCode.setText((arg0 / 1000) + "s重新发送");
            btnCode.setEnabled(false);
        }
    }
}
