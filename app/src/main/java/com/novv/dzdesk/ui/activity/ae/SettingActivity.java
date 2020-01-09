package com.novv.dzdesk.ui.activity.ae;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.novv.dzdesk.R;
import com.novv.dzdesk.ui.activity.WebViewActivity;
import com.umeng.analytics.MobclickAgent;

public class SettingActivity extends AppCompatActivity {
    private ClipboardManager cm;
    private ClipData mClipData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        RelativeLayout ys=findViewById(R.id.setting_select);
        RelativeLayout yh=findViewById(R.id.setting_time);
        ImageView ht=findViewById(R.id.back_view);
        ys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置跳转的网站
                Intent intent = new Intent(SettingActivity.this, WebViewActivity.class);
                intent.putExtra("data",1);
                startActivity(intent);
            }
        });
        yh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置跳转的网站
                Intent intent = new Intent(SettingActivity.this, WebViewActivity.class);
                intent.putExtra("data",0);
                startActivity(intent);
            }
        });
        ht.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView textView3 = findViewById(R.id.setting_advice);
        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取剪贴板管理器：
                cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 创建普通字符型ClipData
                mClipData = ClipData.newPlainText("Label", "3607799199");
                // 将ClipData内容放到系统剪贴板里。
                cm.setPrimaryClip(mClipData);
                Toast.makeText(SettingActivity.this, "复制成功", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
