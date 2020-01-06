package com.novv.dzdesk.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.just.agentweb.AgentWeb;
import com.novv.dzdesk.R;
import com.novv.dzdesk.util.ToastUtil;
import com.umeng.analytics.MobclickAgent;

public class WebUrlActivity extends AppCompatActivity {

    public static final String KEY_URL = "key_url";
    public static final String KEY_TITLE = "key_title";
    private AgentWeb mAgentWeb;

    public static void launch(Context context, String url, String title) {
        Intent intent = new Intent(context, WebUrlActivity.class);
        intent.putExtra(KEY_TITLE, title);
        intent.putExtra(KEY_URL, url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_url_activity);
        LinearLayout mLinearLayout = findViewById(R.id.mLinearLayout);
        String mUrl = getIntent().getStringExtra(KEY_URL);
        String mTitle = getIntent().getStringExtra(KEY_TITLE);
        TextView tvTitle = findViewById(R.id.tv_title);
        tvTitle.setText(mTitle);
        if (TextUtils.isEmpty(mUrl)) {
            ToastUtil.showToast(this, "地址已失效");
            finish();
            return;
        }
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(mLinearLayout, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()
                .createAgentWeb()
                .ready()
                .go(mUrl);
        View btnClose = findViewById(R.id.btn_finish);
        View btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mAgentWeb.back()) {
                    finish();
                }
            }
        });
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        WebView webView = mAgentWeb.getWebCreator().getWebView();
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setBlockNetworkImage(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mAgentWeb.getWebCreator().getWebView().setOverScrollMode(WebView.OVER_SCROLL_NEVER);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mAgentWeb.handleKeyEvent(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAgentWeb.getWebLifeCycle().onDestroy();
    }
}
