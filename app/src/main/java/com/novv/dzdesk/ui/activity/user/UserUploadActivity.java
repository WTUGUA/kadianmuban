package com.novv.dzdesk.ui.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.ark.baseui.XAppCompatActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;
import com.novv.dzdesk.R;
import com.novv.dzdesk.res.model.ResourceBean;
import com.novv.dzdesk.res.user.LoginContext;
import com.novv.dzdesk.ui.activity.vwp.VwpResDetailActivity;
import com.novv.dzdesk.ui.adapter.user.AdapterUserUploads;
import com.novv.dzdesk.ui.presenter.PresentUserUpload;
import com.novv.dzdesk.util.NetUtil;
import com.novv.dzdesk.util.UmUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.umeng.analytics.MobclickAgent;
import java.util.ArrayList;
import java.util.List;

public class UserUploadActivity extends XAppCompatActivity<PresentUserUpload> implements
    BaseQuickAdapter.OnItemClickListener, View.OnClickListener {

  private SmartRefreshLayout mRefreshLayout;
  private RecyclerView mRecyclerView;
  private AdapterUserUploads mAdAdapter;
  private int lastPosition = 0;
  private int lastOffset = 0;
  private GridLayoutManager mGridLayoutManager;
  private FrameLayout mFlTips;

  @Override
  public int getLayoutId() {
    return R.layout.activity_user_upload;
  }

  @Override
  public void setUpView(@NonNull View rootView) {
    super.setUpView(rootView);
    mRefreshLayout = rootView.findViewById(R.id.refreshLayout);
    mRecyclerView = rootView.findViewById(R.id.recyclerView);
    mFlTips = rootView.findViewById(R.id.fl_tips);
    ImageView btnTipsClose = rootView.findViewById(R.id.btn_tips_close);
    ImageView backView = rootView.findViewById(R.id.btn_back);
    TextView tvTitle = rootView.findViewById(R.id.tv_title);
    tvTitle.setText("我的上传");
    backView.setOnClickListener(this);
    btnTipsClose.setOnClickListener(this);
    mFlTips.setOnClickListener(this);
    boolean isVip = LoginContext.getInstance().isVip();
    mFlTips.setVisibility(isVip ? View.GONE : View.VISIBLE);
  }

  @Nullable
  @Override
  public PresentUserUpload newP() {
    return new PresentUserUpload();
  }

  @Override
  public void initData(@Nullable Bundle savedInstanceState) {
    ImmersionBar.with(this)
        .titleBar(findViewById(R.id.fl_top))
        .navigationBarColor(R.color.color_primary_dark)
        .init();
    mAdAdapter = new AdapterUserUploads(new ArrayList<ResourceBean>());
    mAdAdapter.setOnItemClickListener(this);
    mRefreshLayout.setEnableLoadMore(true);
    mAdAdapter.setEnableLoadMore(false);
    mRecyclerView.setLayoutManager(mGridLayoutManager = new GridLayoutManager(this, 3));
    mRecyclerView.setAdapter(mAdAdapter);
    mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
      @Override public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        getP().loadUserUpload(true);
      }

      @Override public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getP().loadUserUpload(false);
      }
    });
    mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        View topView = mGridLayoutManager.getChildAt(0);//获取可视的第一个view
        if (topView != null) {
          lastOffset = topView.getTop();//获取与该view的顶部的偏移量
          lastPosition = mGridLayoutManager.getPosition(topView);//得到该View的数组位置
        }
      }

      @Override
      public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
      }
    });
    getP().loadUserUpload(false);
  }

  @Override
  public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
    ResourceBean resourceBean = mAdAdapter.getData().get(position);
    VwpResDetailActivity.launch(this, resourceBean, false,
        resourceBean.isReject() || !resourceBean.isPass());
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
            NetUtil.isNetworkAvailable(this) ? R.mipmap.ic_empty_upload :
                R.mipmap.ic_empty_nonet);
        TextView textView = view.findViewById(R.id.tvEmpty);
        textView.setText("暂无上传~");
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
      case R.id.fl_tips:
        mFlTips.setVisibility(View.GONE);
        UmUtil.anaVIPPage(context, "upload");
        startActivity(new Intent(this, UserVipActivity.class));
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

  @Override
  protected void onDestroy() {
    super.onDestroy();
    ImmersionBar.with(this).destroy();
  }
}
