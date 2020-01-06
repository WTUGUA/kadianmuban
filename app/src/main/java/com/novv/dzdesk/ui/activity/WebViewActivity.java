package com.novv.dzdesk.ui.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telecom.TelecomManager;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.novv.dzdesk.R;

public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        WebView webView = findViewById(R.id.webView);
        TextView textView=findViewById(R.id.web_top);
        ImageView imageView=findViewById(R.id.web_back_view);
        Intent intent = getIntent();
        int i=intent.getIntExtra("data",2);
        if(i==1){
            textView.setText("隐私政策");
            webView.loadUrl("http://s.novapps.com/web_html/rhythm_model_privacy.html");   //(如:file:///android_assets...)
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
        }else{
            textView.setText("用户协议");
            webView.loadUrl("https://s.novapps.com/web_html/rhythm_template_video_service_protocol.html");   //(如:file:///android_assets...)
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
                    return true;
                }
            });
        }
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //支持App内部javascript交互

        webView.getSettings().setJavaScriptEnabled(true);

    }
}
