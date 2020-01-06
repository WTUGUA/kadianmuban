package com.novv.dzdesk.ui.fragment.ae;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.ark.baseui.XLazyFragment;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.novv.dzdesk.R;
import com.novv.dzdesk.res.model.VModel;
import com.novv.dzdesk.ui.activity.ae.AEPreviewActivity;
import com.novv.dzdesk.ui.activity.ae.UmCount;
import com.novv.dzdesk.ui.adapter.ae.AdapterAERes;
import com.novv.dzdesk.ui.presenter.PresentAERes;
import com.novv.dzdesk.util.NetUtil;
import com.novv.dzdesk.util.UmUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

public class TabAEResFragment extends XLazyFragment<PresentAERes> implements
    BaseQuickAdapter.OnItemClickListener {

  private AdapterAERes mAdapter;
  private RecyclerView mRecyclerView;
  private SmartRefreshLayout mRefreshLayout;

  @Override
  public int getLayoutId() {
    return R.layout.fragment_tab_list;
  }

  @Nullable
  @Override
  public PresentAERes newP() {
    return new PresentAERes();
  }

  @Override
  public void setUpView(@NonNull View rootView) {
    super.setUpView(rootView);
    mRefreshLayout = rootView.findViewById(R.id.refreshLayout);
    mRecyclerView = rootView.findViewById(R.id.recyclerView);
  }

  @Override
  public void initData(@Nullable Bundle savedInstanceState) {
    int spanCount = 2; // 3 columns
    int spacing = 25; // 50px
    boolean includeEdge = false;
    mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
    mAdapter = new AdapterAERes(new ArrayList<VModel>());
    mAdapter.setOnItemClickListener(this);
    mRecyclerView.setLayoutManager( new GridLayoutManager(context, 2));
    mRecyclerView.setAdapter(mAdapter);
    mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
      @Override public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        getP().loadAERes(true);
      }

      @Override public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getP().loadAERes(false);
      }
    });
    mAdapter.setEnableLoadMore(false);
    mRefreshLayout.setEnableLoadMore(true);
    getP().loadAERes(false);
  }

  public void setError(boolean isLoadMore) {
    if (isLoadMore) {
      mRefreshLayout.finishLoadMore(0, false, false);
    } else {
      mRefreshLayout.finishRefresh();
      mAdapter.replaceData(new ArrayList<VModel>());
      View view = mAdapter.getEmptyView();
      if (view == null) {
        view = LayoutInflater.from(context).inflate(R.layout.empty_view, null);
        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(
            NetUtil.isNetworkAvailable(context) ? R.mipmap.ic_empty_nores :
                R.mipmap.ic_empty_nonet);
        TextView textView = view.findViewById(R.id.tvEmpty);
        textView.setText("没有定制资源~");
        mAdapter.setEmptyView(view);
        view.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mRefreshLayout.autoRefresh();
          }
        });
      }
    }
  }

  public void setNewData(List<VModel> newData) {
    mAdapter.replaceData(newData);
    mRefreshLayout.finishRefresh();
    if (newData.isEmpty()) {
      setError(false);
    }
  }

  public void addMoreData(List<VModel> moreData) {
    mAdapter.addData(moreData);
    if (moreData.isEmpty()) {
      mRefreshLayout.finishLoadMore(0, true, true);
    } else {
      mRefreshLayout.finishLoadMore();
    }
  }

  @Override
  public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
    MobclickAgent.onEvent(context, UmCount.HomeVedioClick);
    Intent intent = new Intent(context, AEPreviewActivity.class);
    VModel mVModel = mAdapter.getData().get(position);
    UmUtil.anaDIYDetail(context, mVModel.id, "定制");
    intent.putExtra(VModel.class.getSimpleName(), mVModel);
    startActivity(intent);
  }
  @Override
  public void onResume() {
    MobclickAgent.onResume(context);
    super.onResume();
  }

  @Override
  public void onPause() {
    MobclickAgent.onPause(context);
    super.onPause();

  }
}
