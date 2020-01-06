package com.novv.dzdesk.ui.activity.local;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import com.ark.baseui.XAppCompatActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.novv.dzdesk.R;
import com.novv.dzdesk.res.model.ResourceBean;
import com.novv.dzdesk.res.model.RxAsyncTask;
import com.novv.dzdesk.ui.adapter.plugin.AdapterSimpleRes;
import com.novv.dzdesk.util.LogUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LocalVideoListActivity extends XAppCompatActivity implements
        View.OnClickListener {

    private final static int CHOSE_VIDEO_CODE_EDIT = 111;
    private final static int CHOSE_VIDEO_CODE = 112;
    private static final String tag = LocalVideoListActivity.class.getSimpleName();
    private View mNothingkView, btnBack;
    private RecyclerView mRv;
    private SmartRefreshLayout refreshLayout;
    private View mChoseLocalVEdit;
    private View mChoseLocalV;

    private ArrayList<ResourceBean> mItems = new ArrayList<>();
    private AdapterSimpleRes mAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_local_video_list;
    }

    @Override
    public void setUpView(@NonNull View rootView) {
        super.setUpView(rootView);
        refreshLayout = findViewById(R.id.refreshLayout);
        mNothingkView = findViewById(R.id.nothing_tv);
        mRv = findViewById(R.id.recyclerView);
        mChoseLocalV = findViewById(R.id.iv_chose_local);
        mChoseLocalVEdit = findViewById(R.id.chose_local_video_rl);
        btnBack = findViewById(R.id.btn_back);
    }

    @Nullable
    @Override
    public Object newP() {
        return null;
    }

    @Override
    public void initData(@Nullable Bundle bundle) {
        LogUtil.i(tag, "initData");
        mRv.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter = new AdapterSimpleRes(this, R.layout.resource_item_layout, mItems);
        mRv.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                LocalDetailActivity.launch(LocalVideoListActivity.this, mItems.get(position));
            }
        });
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshLayout) {
                loadDownloadedRes();
            }
        });
        refreshLayout.autoRefresh();
        mChoseLocalVEdit.setOnClickListener(this);
        mChoseLocalV.setOnClickListener(this);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadDownloadedRes() {
        new RxAsyncTask<Void, Void, List<ResourceBean>>() {

            @Override
            protected List<ResourceBean> call(Void... voids) {
                List<ResourceBean> lives = new ArrayList<>();
                List<ResourceBean> newLives = new ArrayList<>();
                return newLives;
            }

            @Override
            protected void onResult(List<ResourceBean> lives) {
                super.onResult(lives);
                mItems.clear();
                mItems.addAll(lives);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            protected void onCompleted() {
                super.onCompleted();
                refreshLayout.finishRefresh();
                mNothingkView.setVisibility(mItems.size() <= 0 ? View.VISIBLE : View.GONE);
            }
        }.execute();
    }

    private void choseLocalFile(int requestCode) {
        Intent intent = new Intent(this, VideoSelectActivity.class);
        startActivityForResult(intent, requestCode);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            String videoPath = data.getStringExtra("data");
            if (TextUtils.isEmpty(videoPath) || !new File(videoPath).exists()) {
                return;
            }
            if (requestCode == CHOSE_VIDEO_CODE_EDIT) {
                VideoEditActivity.launch(LocalVideoListActivity.this, videoPath, false);
            } else if (requestCode == CHOSE_VIDEO_CODE) {
                LocalVideoActivity.launch(LocalVideoListActivity.this, videoPath, false);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.chose_local_video_rl:
                choseLocalFile(CHOSE_VIDEO_CODE_EDIT);
                break;
            case R.id.iv_chose_local:
                choseLocalFile(CHOSE_VIDEO_CODE);
                break;
        }
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
}
