package com.novv.dzdesk.ui.activity.ae;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.novv.dzdesk.R;
import com.novv.dzdesk.ui.activity.WebViewActivity;

public class SettingActivity extends AppCompatActivity {

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
                Uri uri = Uri.parse("http://s.novapps.com/web_html/kadian_model_privacy.html");    //设置跳转的网站
                Intent intent = new Intent(SettingActivity.this, WebViewActivity.class);
                intent.putExtra("data",1);
                startActivity(intent);
            }
        });
        yh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri1 = Uri.parse("https://s.novapps.com/web_html/kadian_template_video_service_protocol.html");    //设置跳转的网站
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
    }
}
