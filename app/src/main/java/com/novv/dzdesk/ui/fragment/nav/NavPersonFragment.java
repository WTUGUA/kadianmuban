package com.novv.dzdesk.ui.fragment.nav;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ark.baseui.XLazyFragment;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.novv.dzdesk.R;
import com.novv.dzdesk.res.model.UserModel;
import com.novv.dzdesk.res.user.LoginContext;
import com.novv.dzdesk.ui.activity.SettingsActivity;
import com.novv.dzdesk.ui.activity.user.UserInfoEditActivity;
import com.novv.dzdesk.ui.activity.user.UserLoginActivity;
import com.novv.dzdesk.ui.activity.user.UserVipActivity;
import com.novv.dzdesk.ui.presenter.PresentProfile;
import com.novv.dzdesk.util.DeviceUtil;
import com.novv.dzdesk.util.UmUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.xslczx.widget.ShapeImageView;

public class NavPersonFragment extends XLazyFragment<PresentProfile> implements OnClickListener {

    private ImageView btnSettings;
    private ShapeImageView ivUserImg;
    private LinearLayout llUserLogout;
    private TextView btnLogin;
    private LinearLayout llUserLogin;
    private TextView tvUserName;
    private ImageView ivUserVipFlag;
    private TextView tvUserSign;
    private TextView tvUserVipTime;
    private TextView tvUserVipOpen;
    private FrameLayout flFavor;
    private FrameLayout flDownload;
    private FrameLayout flUpload;
    private FrameLayout flMake;
    private SmartRefreshLayout refreshLayout;

    public static NavPersonFragment getInstance(FragmentActivity activity) {
        FragmentManager fm = activity.getSupportFragmentManager();
        return (NavPersonFragment) fm.findFragmentByTag(NavPersonFragment.class
                .getSimpleName());
    }

    @Override
    public int getLayoutId() {
        return R.layout.nav_person_fragment;
    }

    @Nullable
    @Override
    public PresentProfile newP() {
        return new PresentProfile();
    }

    @Override
    public void setUpView(@NonNull View view) {
        super.setUpView(view);
        btnSettings = view.findViewById(R.id.btn_settings);
        ivUserImg = view.findViewById(R.id.iv_user_img);
        llUserLogout = view.findViewById(R.id.ll_user_logout);
        btnLogin = view.findViewById(R.id.btn_login);
        llUserLogin = view.findViewById(R.id.ll_user_login);
        tvUserName = view.findViewById(R.id.tv_user_name);
        ivUserVipFlag = view.findViewById(R.id.iv_user_vip_flag);
        tvUserSign = view.findViewById(R.id.tv_user_sign);
        tvUserVipTime = view.findViewById(R.id.tv_user_vip_time);
        tvUserVipOpen = view.findViewById(R.id.tv_user_vip_open);
        flFavor = view.findViewById(R.id.fl_favor);
        flDownload = view.findViewById(R.id.fl_download);
        flUpload = view.findViewById(R.id.fl_upload);
        refreshLayout = view.findViewById(R.id.refreshLayout);
        flMake = view.findViewById(R.id.fl_make);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        btnSettings.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        flDownload.setOnClickListener(this);
        flUpload.setOnClickListener(this);
        flFavor.setOnClickListener(this);
        ivUserVipFlag.setOnClickListener(this);
        ivUserImg.setOnClickListener(this);
        tvUserName.setOnClickListener(this);
        flMake.setOnClickListener(this);
        setUserLogout();
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (LoginContext.getInstance().isLogin()) {
                    setUserLogin();
                    getP().loadProfile();
                } else {
                    setUserLogout();
                }
            }
        });
    }

    @Override
    protected void onStartLazy() {
        super.onStartLazy();
        if (LoginContext.getInstance().isLogin()) {
            setUserLogin();
            getP().loadProfile();
        } else {
            setUserLogout();
        }
    }

    public void setUserLogin() {
        refreshLayout.finishRefresh();
        UserModel userModel = LoginContext.getInstance().getUser();
        if (userModel == null) {
            return;
        }
        llUserLogout.setVisibility(View.GONE);
        llUserLogin.setVisibility(View.VISIBLE);
        String vipTime = userModel.getVipTime();
        boolean vipInvalid = TextUtils.isEmpty(vipTime);
        boolean isVip = userModel.isVip();
        ivUserVipFlag.setVisibility(
                TextUtils.isEmpty(vipTime) ? View.GONE : View.VISIBLE);
        ivUserVipFlag.setImageResource(
                isVip ? R.mipmap.ic_vip_valid : R.mipmap.ic_vip_invalid);
        tvUserSign.setVisibility(View.VISIBLE);
        String sign = userModel.getDesc();
        tvUserSign.setText(TextUtils.isEmpty(sign) ? "个人签名，用户可以在设置编辑修改" : sign);
        tvUserName.setText(userModel.getNickname());
        tvUserVipOpen.setVisibility(isVip ? View.GONE : View.VISIBLE);
        tvUserVipOpen.setText(vipInvalid ? "开通VIP" : "续费VIP");
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.TRANSPARENT);
        drawable.setCornerRadius(DeviceUtil.dip2px(context, 50));
        drawable.setStroke(DeviceUtil.dip2px(context, 1), Color.parseColor("#07c0e9"));
        tvUserVipOpen.setBackground(drawable);
        tvUserVipOpen.setOnClickListener(this);
        tvUserVipTime.setText(String.format("VIP到期时间：%s", vipTime));
        tvUserVipTime.setVisibility(isVip ? View.VISIBLE : View.GONE);
        Glide.with(context)
                .load(userModel.getImg())
                .apply(new RequestOptions()
                        .placeholder(ivUserImg.getDrawable())
                        .error(R.drawable.user_avatar_default)
                        .dontAnimate())
                .into(ivUserImg);
    }

    public void setUserLogout() {
        refreshLayout.finishRefresh();
        llUserLogout.setVisibility(View.VISIBLE);
        llUserLogin.setVisibility(View.GONE);
        ivUserVipFlag.setVisibility(View.GONE);
        tvUserSign.setVisibility(View.GONE);
        tvUserVipOpen.setVisibility(View.VISIBLE);
        ivUserImg.setImageResource(R.drawable.user_avatar_default);
        refreshLayout.finishRefresh();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.tv_user_vip_open:
            case R.id.iv_user_vip_flag:
                UmUtil.anaVIPPage(context, "mine");
                startActivity(new Intent(context, UserVipActivity.class));
                break;
            case R.id.btn_login:
                UmUtil.anaLoginFrom(context, "个人资料");
                startActivity(new Intent(context, UserLoginActivity.class));
                break;
            case R.id.btn_settings:
                startActivity(new Intent(context, SettingsActivity.class));
                break;
            case R.id.fl_favor:
                LoginContext.getInstance().userFavorList(context);
                break;
            case R.id.fl_download:
                LoginContext.getInstance().userDownloadList(context);
                break;
            case R.id.fl_upload:
                LoginContext.getInstance().userUploadList(context);
                break;
            case R.id.iv_user_img:
                if (LoginContext.getInstance().getUser() == null) {
                    return;
                }
                startActivity(new Intent(context, UserInfoEditActivity.class));
                break;
            case R.id.tv_user_name:
                startActivity(new Intent(context, UserInfoEditActivity.class));
                break;
            case R.id.fl_make:
                LoginContext.getInstance().userMakeList(context);
                break;
        }
    }
}
