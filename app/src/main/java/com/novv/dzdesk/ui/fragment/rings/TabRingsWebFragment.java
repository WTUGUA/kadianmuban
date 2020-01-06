package com.novv.dzdesk.ui.fragment.rings;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import com.ark.adkit.polymers.ydt.utils.Logger;
import com.ark.baseui.XLazyFragment;
import com.ark.dict.ConfigMapLoader;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;
import com.novv.dzdesk.R;

public class TabRingsWebFragment extends XLazyFragment {

    private AgentWeb mAgentWeb;
    private LinearLayout mLayout;
    private boolean isStopped;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_web_rings;
    }

    @Nullable
    @Override
    public Object newP() {
        return null;
    }

    @Override
    public void setUpView(@NonNull View rootView) {
        super.setUpView(rootView);
        mLayout = rootView.findViewById(R.id.mLayout);
    }

    @Override
    public void initData(@Nullable Bundle savedInstanceState) {
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(mLayout, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator(Color.parseColor("#fb4d4c"), 2)
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .setMainFrameErrorView(R.layout.webview_error_page, -1)
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)
                .interceptUnkownUrl()
                .createAgentWeb()
                .ready()
                .go(getRingsUrl());
        isStopped = false;
    }

    public String getRingsUrl() {
        String ringsUrl = ConfigMapLoader.getInstance().getResponseMap().get("diy_ring_url");
        if (TextUtils.isEmpty(ringsUrl)) {
            ringsUrl = "http://m.ym1998.com:88/MjAwMDIzMTdfYmEzMTVhNjU2MGI1NmRlMQ==";
            Logger.i("logger", "彩铃本地配置地址:" + ringsUrl);
        } else {
            Logger.i("logger", "彩铃远程配置地址:" + ringsUrl);
        }
        return ringsUrl;
    }

    public void reload() {
        if (mAgentWeb != null && isStopped) {
            isStopped = false;
            mAgentWeb.getWebCreator().getWebView().loadUrl(getRingsUrl());
        }
    }

    @Override
    protected void onStartLazy() {
        super.onStartLazy();
        resumePlay();
    }

    @Override
    protected void onStopLazy() {
        super.onStopLazy();
        stopPlay();
    }

    public void stopPlay() {
        isStopped = true;
        if (mAgentWeb != null) {
            mAgentWeb.getJsAccessEntrace().quickCallJs("audioPlayer.stop()");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                mAgentWeb.getWebCreator().getWebView()
                        .loadUrl("file:///android_asset/audio-pause.html");
            }
        }
    }

    public void resumePlay() {
        if (mAgentWeb != null) {
            mAgentWeb.getWebCreator().getWebView()
                    .loadUrl(getRingsUrl());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopPlay();
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onPause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onResume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAgentWeb != null) {
            mAgentWeb.getWebLifeCycle().onDestroy();
        }
    }
}
