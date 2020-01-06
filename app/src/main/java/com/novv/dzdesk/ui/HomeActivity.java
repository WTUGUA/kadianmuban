package com.novv.dzdesk.ui;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.adesk.tool.plugin.PSetUtils;
import com.ark.adkit.basics.handler.Run;
import com.ark.adkit.basics.utils.AppUtils;
import com.ark.adkit.basics.utils.LogUtils;
import com.ark.baseui.XAppCompatActivity;
import com.ark.dict.ConfigMapLoader;
import com.novv.dzdesk.Const;
import com.novv.dzdesk.R;
import com.novv.dzdesk.live.LwPrefUtil;
import com.novv.dzdesk.live.LwVideoLiveWallpaper;
import com.novv.dzdesk.live.PrefUtil;
import com.novv.dzdesk.res.event.EventCode;
import com.novv.dzdesk.res.model.UserModel;
import com.novv.dzdesk.res.user.LoginContext;
import com.novv.dzdesk.rxbus2.RxBus;
import com.novv.dzdesk.rxbus2.Subscribe;
import com.novv.dzdesk.rxbus2.ThreadMode;
import com.novv.dzdesk.service.AppReceiver;
import com.novv.dzdesk.service.UriObserver;
import com.novv.dzdesk.ui.activity.local.VideoSelectActivity;
import com.novv.dzdesk.ui.activity.user.UserVipActivity;
import com.novv.dzdesk.ui.adapter.AdapterHomePager;
import com.novv.dzdesk.ui.dialog.PrivacyDialog;
import com.novv.dzdesk.ui.fragment.nav.NavHomeFragment;
import com.novv.dzdesk.ui.fragment.nav.NavRingsFragment;
import com.novv.dzdesk.ui.presenter.PresentHome;
import com.novv.dzdesk.ui.view.SpecialTab;
import com.novv.dzdesk.ui.view.SpecialTabRound;
import com.novv.dzdesk.util.AnaUtil;
import com.novv.dzdesk.util.UmConst;
import com.novv.dzdesk.util.UmUtil;
import com.tencent.mmkv.MMKV;

import java.util.Map;

import me.majiajie.pagerbottomtabstrip.NavigationController;
import me.majiajie.pagerbottomtabstrip.PageNavigationView;
import me.majiajie.pagerbottomtabstrip.item.BaseTabItem;
import me.majiajie.pagerbottomtabstrip.listener.SimpleTabItemSelectedListener;

public class HomeActivity extends XAppCompatActivity<PresentHome> {

    private UriObserver mContentObserver;
    private Uri mUri = Uri.parse("content://com.adesk.wpplugin.PluginProvider/settings");
    private NavigationController navigationController;
    private AppReceiver appReceiver;
    private long exitTime = 0;
    private AdapterHomePager adapter;
    private ViewPager viewPager;

    @Override
    public int getLayoutId() {
        return R.layout.activity_home;
    }

    @Nullable
    @Override
    public PresentHome newP() {
        return new PresentHome();
    }

    @Override
    public void setUpView(@NonNull View rootView) {
        super.setUpView(rootView);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        reload(LoginContext.getInstance().getUser());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        checkTabStatus();
    }

    private void checkTabStatus() {
        Intent intent = getIntent();
        if (intent != null) {
            String tab = intent.getStringExtra("tab");
            if (TextUtils.equals(tab, "diy")) {
                if (navigationController != null) {
                    navigationController.setSelect(0);
                }
                for (Fragment fragment : getSupportFragmentManager().getFragments()) {
                    if (fragment instanceof NavHomeFragment) {
                        ((NavHomeFragment) fragment).setDIYSelect();
                        break;
                    }
                }
            }
        }
        setIntent(new Intent());
    }

    private void checkVipDays() {
        boolean isNew = MMKV.defaultMMKV()
                .decodeBool("version" + AppUtils.getVersionName(context), true);
        if (isNew) {
            LoginContext.getInstance().logout();
            MMKV.defaultMMKV().encode("version" + AppUtils.getVersionName(context), false);
            return;
        }
        UserModel userModel = LoginContext.getInstance().getUser();
        //开通过VIP且即将到期，提示一次续费
        if (userModel != null && !TextUtils.isEmpty(userModel.getVipTime())
                && userModel.getVipDays() <= 1) {
            //这里如果vip到期时间变化了，说明续费了，再次到期了也需要提示
            boolean notifyVip = MMKV.defaultMMKV()
                    .decodeBool("is_notify_vip" + userModel.getVipTime(), true);
            if (!notifyVip) {
                return;
            }
            getVDelegate().toastLong("您的VIP" + (userModel.isVip() ? "即将到期" : "已经到期"));
            startActivity(new Intent(context, UserVipActivity.class));
            MMKV.defaultMMKV().encode("is_notify_vip" + userModel.getVipTime(), false);
        }
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        RxBus.getDefault().register(this);
        mContentObserver = new UriObserver(this, new Handler());
        ContentResolver cr = getContentResolver();
        try {
            cr.registerContentObserver(mUri, true, mContentObserver);
        } catch (Exception e) {
            LogUtils.w(e.toString());
        }
        PageNavigationView tab = findViewById(R.id.tab);
        navigationController = tab.custom()
                .addItem(newItem(R.drawable.nav_home_normal, R.drawable.nav_home_selected, "首页"))
                .addItem(newItem(R.drawable.icon_toux_normal, R.drawable.icon_toux_seleted, "头像"))
                .addItem(newRoundItem(R.drawable.nav_main_upload, R.drawable.nav_main_upload, "上传"))
                .addItem(newItem(R.drawable.icon_ring_normal, R.drawable.icon_ring_seleted, "铃声"))
                .addItem(
                        newItem(R.drawable.nav_person_normal, R.drawable.nav_person_selected, "我的"))
                .build();

        viewPager = findViewById(R.id.viewPager);
        adapter = new AdapterHomePager(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(false, null);
        navigationController.addSimpleTabItemSelectedListener(new SimpleTabItemSelectedListener() {
            @Override
            public void onSelected(int index, int old) {
                //第二页跳转到本地视频选择界面
                if (index == 2) {
                    navigationController.setSelect(old);
                    UmUtil.anaTabBottomOp(context, UmConst.MAIN_TAB_UPLOAD);
                    UmUtil.anaTabBottomOp(context, UmConst.CLICK_VIDEO_SELECT);
                    Intent intent = new Intent(HomeActivity.this, VideoSelectActivity.class);
                    startActivity(intent);
                } else {
                    viewPager.setCurrentItem(index, false);
                    if (index == 0) {
                        AnaUtil.anaViewResources(context, false);
                        UmUtil.anaTabBottomOp(context, UmConst.MAIN_TAB_HOME);
                    } else if (index == 1) {
                        AnaUtil.anaViewResources(context, false);
                        UmUtil.anaTabBottomOp(context, UmConst.MAIN_TAB_AVATAR);
                    } else if (index == 3) {
                        AnaUtil.anaViewResources(context, false);
                        UmUtil.anaTabBottomOp(context, UmConst.MAIN_TAB_RINGS);
                    } else if (index == 4) {
                        AnaUtil.anaViewResources(context, false);
                        UmUtil.anaTabBottomOp(context, UmConst.MAIN_TAB_PERSON);
                    }
                    Fragment fragment = adapter.getItem(3);
                    if (fragment instanceof NavRingsFragment) {
                        if (index != 3) {
                            ((NavRingsFragment) fragment).pausePlay();
                        } else {
                            ((NavRingsFragment) fragment).reload();
                        }
                    }
                }
            }
        });
        Run.getUiHandler()
                .postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getP().checkLogin();
                        getP().checkSet();
                        getP().downloadPatch();
                    }
                }, 2000);

        showPolicyDialog();
        checkTabStatus();
        checkVipDays();
        regAppReceiver();
    }

    private void showPolicyDialog() {
        int privacyUpgradeVersion = PrefUtil.getInt(getApplicationContext(), Const.Policy.PRIVACY_UPGRADE_VERSION, 0);
        Map<String,String> config = ConfigMapLoader.getInstance().getResponseMap();
        String privacyVersion = config.get(Const.Policy.PRIVACY_UPGRADE_VERSION);
        Log.e("-----","PRIVACY_UPGRADE_VERSION " + privacyVersion);
        int version = -1;
        try {
            version = Integer.parseInt(privacyVersion);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (privacyUpgradeVersion <= 0 || version > 0 && privacyUpgradeVersion < version){
            PrivacyDialog privacyDialog = new PrivacyDialog(this);
            final int finalVersion = version;
            privacyDialog.setOnButtonClickListener(new PrivacyDialog.OnButtonClickListener() {
                @Override
                public void onAgree() {
                    PrefUtil.putInt(getApplicationContext(),Const.Policy.PRIVACY_UPGRADE_VERSION, finalVersion);
                }

                @Override
                public void onDisagree() {
                    finish();
                }
            }).show();
        }
    }


    private void regAppReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addDataScheme("package");
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        appReceiver = new AppReceiver();
        registerReceiver(appReceiver, filter);
    }

    /**
     * 正常tab
     */
    private BaseTabItem newItem(int drawable, int checkedDrawable, String text) {
        SpecialTab mainTab = new SpecialTab(this);
        mainTab.initialize(drawable, checkedDrawable, text);
        mainTab.setTextDefaultColor(0xFF888888);
        mainTab.setTextCheckedColor(0XFF05CAEE);
        return mainTab;
    }

    /**
     * 圆形tab
     */
    @SuppressWarnings("SameParameterValue")
    private BaseTabItem newRoundItem(int drawable, int checkedDrawable, String text) {
        SpecialTabRound mainTab = new SpecialTabRound(this);
        mainTab.initialize(drawable, checkedDrawable, text);
        mainTab.setTextDefaultColor(0xFF888888);
        mainTab.setTextCheckedColor(0XFF05CAEE);
        return mainTab;
    }

    @Override
    protected void onPause() {
        super.onPause();
        AnaUtil.anaViewResources(this, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RxBus.getDefault().unregister(this);
        try {
            ContentResolver cr = getContentResolver();
            cr.unregisterContentObserver(mContentObserver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            unregisterReceiver(appReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 声音开关事件 与组件中的声音设置同步
     *
     * @param obj Voice
     */
    @SuppressWarnings("unused")
    @Subscribe(code = EventCode.VOICE_CODE, threadMode = ThreadMode.MAIN)
    public void onEvent(UriObserver.Voice obj) {
        boolean isOn = obj.isOn;
        boolean isLocalOn = LwPrefUtil.isLwpVoiceOpened(this);
        if (isOn != isLocalOn) {
            LwPrefUtil.setLwpVoiceOpened(this, isOn);
            if (isOn) {
                LwVideoLiveWallpaper.voiceNormal(this);
                UmUtil.anaOp(this, UmConst.UM_DESK_VOICE_OPEN);
            } else {
                UmUtil.anaOp(this, UmConst.UM_DESK_VOICE_CLOSE);
                LwVideoLiveWallpaper.voiceSilence(this);
            }
        }
        if (obj.isSelf && isOn != isLocalOn) {
            PSetUtils.setVolume(this, isOn);
        }
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment instanceof NavHomeFragment) {
                ((NavHomeFragment) fragment).refreshVoice();
                break;
            }
        }
    }

    /**
     * VIP登录，开通VIP事件，刷新主页面，防止广告仍然显示
     *
     * @param userModel UserModel
     */
    @SuppressWarnings("unused")
    @Subscribe(code = EventCode.VIEW_VIP_LOGIN, threadMode = ThreadMode.MAIN)
    public void reload(UserModel userModel) {
        int current = viewPager.getCurrentItem();
        adapter = new AdapterHomePager(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(current);
    }

    @Override
    public void onBackPressed() {
        doubleBackQuit();
    }

    /**
     * 连续按两次返回键，退出应用
     */
    private void doubleBackQuit() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }
}
