package com.novv.dzdesk.ui.activity.user;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.ark.baseui.XAppCompatActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;
import com.novv.dzdesk.R;
import com.novv.dzdesk.res.model.ResourceBean;
import com.novv.dzdesk.ui.activity.vwp.VwpResDetailActivity;
import com.novv.dzdesk.ui.adapter.vwp.AdapterVwpResWithAds;
import com.novv.dzdesk.ui.presenter.PresentUserFavor;
import com.novv.dzdesk.util.NetUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.umeng.analytics.MobclickAgent;
import java.util.ArrayList;
import java.util.List;

public class UserFavorActivity extends XAppCompatActivity<PresentUserFavor> implements
    BaseQuickAdapter.OnItemClickListener, View.OnClickListener {

  private SmartRefreshLayout mRefreshLayout;
  private RecyclerView mRecyclerView;
  private AdapterVwpResWithAds mAdAdapter;
  private int lastPosition = 0;
  private int lastOffset = 0;
  private GridLayoutManager mGridLayoutManager;

  @Override
  public int getLayoutId() {
    return R.layout.activity_user_favor;
  }

  @Override
  public void setUpView(@NonNull View rootView) {
    super.setUpView(rootView);
    mRefreshLayout = rootView.findViewById(R.id.refreshLayout);
    mRecyclerView = rootView.findViewById(R.id.recyclerView);
    ImageView backView = rootView.findViewById(R.id.btn_back);
    TextView tvTitle = rootView.findViewById(R.id.tv_title);
    tvTitle.setText("我的收藏");
    backView.setOnClickListener(this);
  }

  @Nullable
  @Override
  public PresentUserFavor newP() {
    return new PresentUserFavor();
  }

  @Override
  public void initData(@Nullable Bundle savedInstanceState) {
    ImmersionBar.with(this)
        .titleBar(findViewById(R.id.fl_top))
        .navigationBarColor(R.color.color_primary_dark)
        .init();
    mAdAdapter = new AdapterVwpResWithAds(new ArrayList<ResourceBean>());
    mAdAdapter.setOnItemClickListener(this);
    mAdAdapter.setEnableLoadMore(false);
    mRefreshLayout.setEnableAutoLoadMore(true);
    mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
      @Override public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        getP().loadUserFavor(true);
      }

      @Override public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getP().loadUserFavor(false);
      }
    });
    mRecyclerView.setLayoutManager(mGridLayoutManager = new GridLayoutManager(this, 3));
    mRecyclerView.setAdapter(mAdAdapter);
    mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        View topView = mGridLayoutManager.getChildAt(0);
        if (topView != null) {
          lastOffset = topView.getTop();
          lastPosition = mGridLayoutManager.getPosition(topView);
        }
      }

      @Override
      public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
      }
    });
    getP().loadUserFavor(false);
  }

  @Override
  public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
    ResourceBean resourceBean = mAdAdapter.getData().get(position);
    VwpResDetailActivity.launch(this, resourceBean, true, false);
  }

  public void setError(boolean isLoadMore) {
    if (isLoadMore) {
      mRefreshLayout.finishLoadMore(0, false, false);
    } else {
      mRefreshLayout.finishRefresh();
      mAdAdapter.replaceData(new ArrayList<ResourceBean>());
      View view = mAdAdapter.getEmptyView();
      if (view == null) {
        view = LayoutInflater.from(this).inflate(R.layout.empty_view, null);
        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(
            NetUtil.isNetworkAvailable(this) ? R.mipmap.ic_empty_favor :
                R.mipmap.ic_empty_nonet);
        TextView textView = view.findViewById(R.id.tvEmpty);
        textView.setText("没有收藏资源~");
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
    if (lastPosition < mAdAdapter.getData().size()) {
      mGridLayoutManager.scrollToPositionWithOffset(lastPosition, lastOffset);
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

  @Override
  public void onClick(View v) {
    int id = v.getId();
    switch (id) {
      case R.id.btn_back:
        finish();
        break;
    }
  }

  @Override
  protected void onRestart() {
    super.onRestart();
    getP().loadUserFavor(false);
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

  @Override
  protected void onDestroy() {
    super.onDestroy();
    ImmersionBar.with(this).destroy();
  }
}
