package com.novv.dzdesk.ui.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.adesk.polymers.common.CommonTool;
import com.ark.dict.ConfigMapLoader;
import com.novv.dzdesk.R;
import com.novv.dzdesk.ui.activity.ae.AEVideoShareActivity;
import com.novv.dzdesk.ui.activity.ae.UmCount;
import com.novv.dzdesk.ui.activity.ae.onClickButtonListener;
import com.novv.dzdesk.ui.dialog.YsDialog;
import com.novv.dzdesk.ui.dialog.haopingDialog;
import com.novv.dzdesk.ui.dialog.shengjiDialog;
import com.novv.dzdesk.ui.fragment.ae.TabAEResFragment;
import com.umeng.analytics.MobclickAgent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.novv.dzdesk.ui.activity.ae.onClickButtonListener.BUTTON_TYPE_A;

public class MainActivity extends AppCompatActivity {
    private long exitTime = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int xieyi = getxieyi("showtime");
        if (xieyi == 0) {
            showDialog1();
        }
        int getversion=getVersion();
        int update=getUpdate();
        if(update==1) {
            try {
               int  version=getAppVersionCode(this);
                if(version<getversion){
                    showDialog();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        setStatusBarColor(0,MainActivity.this);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container,new TabAEResFragment())   // 此处的R.id.fragment_container是要盛放fragment的父容器
                .commit();

//        String packageName=packageName(MainActivity.this);
//        String version=getVersion();
//        int packagename=Integer.getInteger(packageName);
//        int Version=Integer.getInteger(version);
//        if(version.equals("null")&&Version>packagename){
//            Toast.makeText(MainActivity.this,"更新应用",Toast.LENGTH_SHORT).show();
//        }

    }
    @Override
    public void onBackPressed() {
        doubleBackQuit();
    }
    private void doubleBackQuit() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            setSharedPreference("showtime",0);
        }
    }
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarColor(int statusColor, Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            //取消设置Window半透明的Flag
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //添加Flag把状态栏设为可绘制模式
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏为透明
            window.setStatusBarColor(Color.parseColor("#ffffff"));
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }
    public void setSharedPreference(String key, int values) {
        SharedPreferences sp = getSharedPreferences("showtime", Context.MODE_PRIVATE);
        SharedPreferences.Editor et = sp.edit();
        et.putInt(key, values);
        et.commit();
    }
    public static int getVersion() {
        String update_version_min_android = ConfigMapLoader.getInstance().getResponseMap().get("update_version_min_android");
        if (update_version_min_android == null) {
            return 104;
        }

        return  Integer.parseInt(update_version_min_android);
    }

    public static int getUpdate() {
        String update_enable = ConfigMapLoader.getInstance().getResponseMap().get("update_enable");
        if (update_enable == null) {
            return 1;
        }
        return Integer.parseInt(update_enable);
    }
    public static int getAppVersionCode(Context context) {
        int versioncode = 0;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            // versionName = pi.versionName;
            versioncode = pi.versionCode;
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versioncode;
    }

    public void showDialog() {
        //实例化自定义对话框
        final shengjiDialog mdialog = new shengjiDialog(this);
        //对话框中退出按钮事件
        mdialog.setCanceledOnTouchOutside(false);
        mdialog.setCancelable(false);
        mdialog.setOnExitListener(new onClickButtonListener(BUTTON_TYPE_A) {
            @Override
            public void onClick(View v) {
                //如果对话框处于显示状态
                if (mdialog.isShowing()) {
                    //关闭对话框
                    try {
                        Uri uri = Uri.parse("market://details?id=" + getPackageName());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivityForResult(intent, 1);
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "您的手机没有安装应用市场", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        });
        mdialog.setOnCancelListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mdialog != null && mdialog.isShowing()) {
                    mdialog.dismiss();
                }
            }
        });
        mdialog.show();
    }
    public void showDialog1() {
        //实例化自定义对话框
        final YsDialog mdialog = new YsDialog(this);
        mdialog.setCanceledOnTouchOutside(false);
        mdialog.setCancelable(false);
        //对话框中确认按钮事件
        mdialog.setOnAgreeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果对话框处于显示状态
                if (mdialog.isShowing()) {
                    //关闭对话框
                    setxieyi("showtime",1);
                    mdialog.dismiss();
                }
            }
        });
        //对话框中取消按钮事件
        mdialog.setOnUnagreeListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mdialog != null && mdialog.isShowing()) {
                    setxieyi("showtime",0);
                    mdialog.dismiss();
//                    ActivityManager activityManager = (ActivityManager) MainActivity.this.getSystemService(Context.ACTIVITY_SERVICE);
//                    List<ActivityManager.AppTask> appTaskList = activityManager.getAppTasks();
//                    for (ActivityManager.AppTask appTask : appTaskList) {
//                        appTask.finishAndRemoveTask();
//                    }
//                    android.os.Process.killProcess(android.os.Process.myPid());
//                    System.exit(0);
                    finish();
                }
            }
        });
        mdialog.show();
    }
    public int getxieyi(String key) {
        SharedPreferences sp = getSharedPreferences("showtime", Context.MODE_PRIVATE);
        int str = sp.getInt(key, 0);
        return str;
    }

    public void setxieyi(String key, int values) {
        SharedPreferences sp = getSharedPreferences("showtime", Context.MODE_PRIVATE);
        SharedPreferences.Editor et = sp.edit();
        et.putInt(key, values);
        et.commit();
    }

}
