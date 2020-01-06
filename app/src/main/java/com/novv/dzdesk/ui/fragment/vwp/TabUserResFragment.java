package com.novv.dzdesk.ui.fragment.vwp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.ark.baseui.XLazyFragment;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.novv.dzdesk.R;
import com.novv.dzdesk.res.model.ResourceBean;
import com.novv.dzdesk.ui.activity.vwp.VwpResDetailActivity;
import com.novv.dzdesk.ui.adapter.vwp.AdapterVwpResWithAds;
import com.novv.dzdesk.ui.presenter.PresentUserRes;
import com.novv.dzdesk.util.NetUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import java.util.ArrayList;
import java.util.List;

public class TabUserResFragment extends XLazyFragment<PresentUserRes> {

  private SmartRefreshLayout mRefreshLayout;
  private RecyclerView mRecyclerView;
  private AdapterVwpResWithAds mAdAdapter;

  public static TabUserResFragment newInstance(int type, String uid) {
    Bundle args = new Bundle();
    if (!TextUtils.isEmpty(uid)) {
      args.putString("uid", uid);
    }
    args.putInt("type", type);
    TabUserResFragment fragment = new TabUserResFragment();
    fragment.setArguments(args);
    return fragment;
  }

  public int getDataType() {
    int dataType = 0;
    Bundle argument = getArguments();
    if (argument != null) {
      dataType = argument.getInt("type");
    }
    return dataType;
  }

  @Nullable
  public String getUID() {
    String uid = null;
    Bundle argument = getArguments();
    if (argument != null) {
      uid = argument.getString("uid");
    }
    return uid;
  }

  @Override
  public int getLayoutId() {
    return R.layout.layout_refresh_list;
  }

  @Nullable
  @Override
  public PresentUserRes newP() {
    return new PresentUserRes();
  }

  @Override
  public void setUpView(@NonNull View rootView) {
    super.setUpView(rootView);
    mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
    mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
  }

  @Override
  public void initData(@Nullable Bundle savedInstanceState) {
    mAdAdapter = new AdapterVwpResWithAds(new ArrayList<ResourceBean>());
    mAdAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        VwpResDetailActivity.launch(context, mAdAdapter.getData().get(position));
      }
    });
    mRecyclerView.setLayoutManager(new GridLayoutManager(context, 3));
    mRecyclerView.setAdapter(mAdAdapter);
    mAdAdapter.setEnableLoadMore(false);
    mRefreshLayout.setEnableLoadMore(true);
    mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
      @Override public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        getP().loadUserRes(getDataType(), getUID(), true);
      }

      @Override public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getP().loadUserRes(getDataType(), getUID(), false);
      }
    });
    getP().loadUserRes(getDataType(), getUID(), false);
  }

  public void setError(boolean isLoadMore) {
    if (isLoadMore) {
      mRefreshLayout.finishLoadMore(0, false, false);
    } else {
      mRefreshLayout.finishRefresh();
      mAdAdapter.replaceData(new ArrayList<ResourceBean>());
      View view = mAdAdapter.getEmptyView();
      if (view == null) {
        view = LayoutInflater.from(context).inflate(R.layout.empty_view_top, null);
        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(
            NetUtil.isNetworkAvailable(context) ? (getDataType() == 0
                ? R.mipmap.ic_empty_favor
                : R.mipmap.ic_empty_upload) : R.mipmap.ic_empty_nonet);
        TextView textView = view.findViewById(R.id.tvEmpty);
        textView.setText(getDataType() == 0 ? "没有收藏资源~" : "没有上传资源");
        mAdAdapter.setEmptyView(view);
        view.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            mRefreshLayout.autoRefresh();
          }
        });
      }
    }
  }

  public void setNewData(List<ResourceBean> newData) {
    mAdAdapter.replaceData(newData);
    mRefreshLayout.finishRefresh();
    if (newData.isEmpty()) {
      setError(false);
    }
  }

  public void addMoreData(List<ResourceBean> moreData) {
    mAdAdapter.addData(moreData);
    if (moreData.isEmpty()) {
      mRefreshLayout.finishLoadMore(0, true, true);
    } else {
      mRefreshLayout.finishLoadMore();
    }
  }
}
