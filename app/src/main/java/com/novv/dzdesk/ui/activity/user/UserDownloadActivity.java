package com.novv.dzdesk.ui.activity.user;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.ark.baseui.XAppCompatActivity;
import com.gyf.barlibrary.ImmersionBar;
import com.novv.dzdesk.R;
import com.novv.dzdesk.res.model.ResourceBean;
import com.novv.dzdesk.res.model.UserModel;
import com.novv.dzdesk.res.user.LoginContext;
import com.novv.dzdesk.ui.activity.vwp.VwpResDetailActivity;
import com.novv.dzdesk.ui.adapter.user.AdapterUerDownloads;
import com.novv.dzdesk.ui.dialog.DownloadDialog;
import com.novv.dzdesk.ui.presenter.PresentUserDownload;
import com.novv.dzdesk.util.DeviceUtil;
import com.novv.dzdesk.util.NetUtil;
import com.novv.dzdesk.util.UmUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.umeng.analytics.MobclickAgent;
import java.util.ArrayList;
import java.util.List;

public class UserDownloadActivity extends XAppCompatActivity<PresentUserDownload> implements
    View.OnClickListener {

  private LinearLayout mLlTop;
  private ImageView mBtnBack;
  private ImageView mBtnCloud;
  private TextView mBtnSort;
  private FrameLayout mFlTips;
  private SmartRefreshLayout mRefreshLayout;
  private RecyclerView mRecyclerView;
  private ImageView mTipsClose;
  private AdapterUerDownloads mAdAdapter;
  private DownloadDialog progressDialog;
  private View llSort;
  private CheckBox allCheck;

  @Override
  public int getLayoutId() {
    return R.layout.activity_user_download;
  }

  @Nullable
  @Override
  public PresentUserDownload newP() {
    return new PresentUserDownload();
  }

  @Override
  public void setUpView(@NonNull View rootView) {
    super.setUpView(rootView);
    mLlTop = findViewById(R.id.ll_top);
    mBtnBack = findViewById(R.id.btn_back);
    mBtnCloud = findViewById(R.id.btn_cloud);
    mBtnSort = findViewById(R.id.btn_sort);
    mFlTips = findViewById(R.id.fl_tips);
    mRefreshLayout = findViewById(R.id.refreshLayout);
    mRecyclerView = findViewById(R.id.recyclerView);
    mTipsClose = findViewById(R.id.btn_tips_close);
    llSort = findViewById(R.id.ll_sort);
    allCheck = findViewById(R.id.all_check);
    llSort.setVisibility(View.GONE);
  }

  @Override
  public void initData(@Nullable Bundle savedInstanceState) {
    ImmersionBar.with(this)
        .titleBar(mLlTop)
        .navigationBarColor(R.color.color_primary_dark)
        .init();

    mBtnBack.setOnClickListener(this);
    mTipsClose.setOnClickListener(this);
    mBtnSort.setOnClickListener(this);
    mBtnCloud.setOnClickListener(this);
    mFlTips.setOnClickListener(this);

    GradientDrawable drawable = new GradientDrawable();
    drawable.setColor(Color.parseColor("#07c0e9"));
    drawable.setCornerRadius(DeviceUtil.dip2px(this, 2));
    mBtnSort.setBackground(drawable);

    mAdAdapter = new AdapterUerDownloads(new ArrayList<ResourceBean>());
    mRefreshLayout.setEnableLoadMore(true);
    mAdAdapter.setEnableLoadMore(false);
    mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
    mRecyclerView.setAdapter(mAdAdapter);
    mRefreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
      @Override public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        getP().loadUserDownload(true);
      }

      @Override public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getP().loadUserDownload(false);
      }
    });
    getP().loadUserDownload(false);
    UserModel userModel = LoginContext.getInstance().getUser();
    mFlTips.setVisibility(userModel != null && userModel.isVip() ? View.GONE : View.VISIBLE);
    mAdAdapter.setOnCheckedChangeListener(new AdapterUerDownloads.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(boolean check, int size) {
        if (mBtnSort.isSelected()) {
          mBtnSort.setText(String.format("删除(%d)", size));
        } else {
          mBtnSort.setText("整理");
        }
      }

      @Override
      public void onItemClick(int position) {
        VwpResDetailActivity.launch(context, mAdAdapter.getData().get(position));
      }
    });
    allCheck.setOnCheckedChangeListener(
        new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView,
              boolean isChecked) {
            if (mBtnSort.isSelected()) {
              mAdAdapter.setAllChecked(isChecked);
            }
          }
        });
    allCheck.setChecked(false);
    mBtnSort.setSelected(false);
  }

  @Override
  public void onClick(View v) {
    int id = v.getId();
    switch (id) {
      case R.id.btn_back:
        finish();
        break;
      case R.id.btn_tips_close:
        mFlTips.setVisibility(View.GONE);
        break;
      case R.id.btn_cloud:
        if (!LoginContext.getInstance().isVip()) {
          UmUtil.anaVIPPage(context, "download");
          startActivity(new Intent(this, UserVipActivity.class));
          return;
        }
        getP().loadLocalDownload();
        break;
      case R.id.btn_sort:
        if (mAdAdapter.getData().isEmpty()) {
          getVDelegate().toastShort("暂无资源");
          return;
        }
        mBtnSort.setSelected(!mBtnSort.isSelected());
        if (mBtnSort.isSelected()) {
          allCheck.setChecked(false);
          llSort.setVisibility(View.VISIBLE);
          mAdAdapter.setShowCheckBox(true);
          mAdAdapter.notifyDataSetChanged();
          mBtnSort.setText(String.format("删除(%d)", 0));
        } else {
          allCheck.setChecked(false);
          llSort.setVisibility(View.GONE);
          mAdAdapter.setShowCheckBox(false);
          mAdAdapter.notifyDataSetChanged();
          mBtnSort.setText("整理");
          final List<ResourceBean> list = mAdAdapter.getChecked();
          if (list.isEmpty()) {
            onResReload();
            return;
          }
          showProgressDialog();
          getP().delDownload(list);
        }
        break;
      case R.id.fl_tips:
        mFlTips.setVisibility(View.GONE);
        UmUtil.anaVIPPage(context, "download");
        startActivity(new Intent(this, UserVipActivity.class));
        break;
    }
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
            NetUtil.isNetworkAvailable(this) ? R.mipmap.ic_empty_nores :
                R.mipmap.ic_empty_nonet);
        TextView textView = view.findViewById(R.id.tvEmpty);
        textView.setText("没有下载资源~");
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

  public void onResReload() {
    mAdAdapter.setNewData(new ArrayList<ResourceBean>());
    dismissDialog();
    mRefreshLayout.autoRefresh();
  }

  public void showProgressDialog() {
    if (progressDialog == null) {
      progressDialog = new DownloadDialog();
      progressDialog.setOnCancelListener(new DownloadDialog.OnCancelListener() {
        @Override
        public void onCancel() {
          getP().cancel();
        }
      });
    }
    if (!progressDialog.isShowing()) {
      progressDialog.show(context);
    }
  }

  public void setProgress(int i, int size) {
    if (progressDialog != null) {
      progressDialog.setProgress(i, size);
    }
  }

  public void dismissDialog() {
    if (progressDialog != null) {
      progressDialog.release();
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
    dismissDialog();
  }
}
