package com.novv.dzdesk.ui.activity.user;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ark.adkit.basics.utils.LogUtils;
import com.ark.baseui.XAppCompatActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gyf.barlibrary.ImmersionBar;
import com.novv.dzdesk.R;
import com.novv.dzdesk.res.model.UserModel;
import com.novv.dzdesk.res.model.VModel;
import com.novv.dzdesk.res.model.VipDesc;
import com.novv.dzdesk.res.model.VipPrice;
import com.novv.dzdesk.res.user.LoginContext;
import com.novv.dzdesk.ui.activity.SplashActivity;
import com.novv.dzdesk.ui.activity.ae.AEPreviewActivity;
import com.novv.dzdesk.ui.adapter.ae.AdapterAERes;
import com.novv.dzdesk.ui.adapter.edit.AdapterVideoFilter;
import com.novv.dzdesk.ui.adapter.user.AdapterUserVipDesc;
import com.novv.dzdesk.ui.adapter.user.AdapterUserVipPrice;
import com.novv.dzdesk.ui.dialog.LoadingDialog;
import com.novv.dzdesk.ui.dialog.VipPayDialog;
import com.novv.dzdesk.ui.presenter.PresentUserVip;
import com.novv.dzdesk.ui.view.AppBarLayoutBehavior;
import com.novv.dzdesk.ui.view.AppBarStateChangeListener;
import com.novv.dzdesk.ui.view.UserCheckTextView;
import com.novv.dzdesk.util.DeviceUtil;
import com.novv.dzdesk.util.UmUtil;
import com.novv.dzdesk.video.op.PLBuiltinFilterImpl;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.umeng.analytics.MobclickAgent;
import java.util.ArrayList;
import java.util.List;

public class UserVipActivity extends XAppCompatActivity<PresentUserVip> implements
    View.OnClickListener, DialogInterface.OnDismissListener {

  private RecyclerView mRvVip;
  private AdapterUserVipDesc mDescAdapter;
  private RecyclerView mRvPrice;
  private AdapterUserVipPrice mPriceAdapter;
  private SmartRefreshLayout mRefreshLayout;
  private TextView tvArgument;
  private UserModel mUserModel;
  private int selectedIndex = 0;
  private View btnPay;
  private RecyclerView mRvFilter;
  private RecyclerView mRvDiy;
  private AdapterVideoFilter mFilterAdapter;
  private AdapterAERes mResAdapter;
  private TextView mTitleFilter, mTitleDiy;
  private CollapsingToolbarLayout collapsingToolbarLayout;
  private boolean isWebLaunch;
  @Nullable
  private LoadingDialog loadingDialog;

  @Override
  public int getLayoutId() {
    return R.layout.activity_vip;
  }

  @Override
  public void setUpView(@NonNull View rootView) {
    super.setUpView(rootView);
    mRvVip = findViewById(R.id.rv_vip);
    mRvPrice = findViewById(R.id.rv_price);
    mRefreshLayout = findViewById(R.id.refresh_layout);
    tvArgument = findViewById(R.id.user_agreement);
    btnPay = findViewById(R.id.btn_action);
    mRvDiy = findViewById(R.id.rv_diy);
    mRvFilter = findViewById(R.id.rv_filter);
    mTitleDiy = findViewById(R.id.tv_title_diy);
    mTitleFilter = findViewById(R.id.tv_title_filter);
    View btnBack = findViewById(R.id.btn_back);
    btnBack.setOnClickListener(this);
    AppBarLayout appBarLayout = findViewById(R.id.appbar);
    collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
    collapsingToolbarLayout.setTitle("");
    CoordinatorLayout.LayoutParams params =
        new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
    params.setBehavior(new AppBarLayoutBehavior(context, null));
    appBarLayout.setLayoutParams(params);
    appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
      @Override
      public void onStateChanged(AppBarLayout appBarLayout, State state, int verticalOffset) {
        switch (state) {
          case IDLE:
          case EXPANDED:
            collapsingToolbarLayout.setTitle("");
            break;
          case COLLAPSED:
            collapsingToolbarLayout.setTitle("现在就成为VIP");
            break;
          default:
            break;
        }
      }
    });
    btnPay.setOnClickListener(this);
  }

  @Nullable
  @Override
  public PresentUserVip newP() {
    return new PresentUserVip();
  }

  @Override
  public void initData(@Nullable Bundle savedInstanceState) {
    isWebLaunch = getIntent().getBooleanExtra("isWebLaunch", false);
    ImmersionBar.with(this)
        .titleBar(findViewById(R.id.toolbar), false)
        .transparentStatusBar()
        .init();
    SpannableString str = new SpannableString(
        "购买即表示同意《购买协议》");
    str.setSpan(new UserCheckTextView(this), 7, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    tvArgument.setText(str);
    tvArgument.setMovementMethod(LinkMovementMethod.getInstance());
    tvArgument.setHighlightColor(Color.TRANSPARENT);

    GradientDrawable drawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
        new int[] { Color.parseColor("#F6C163"), Color.parseColor("#FDE19F") });
    drawable.setCornerRadius(DeviceUtil.dip2px(context, 22));
    btnPay.setBackground(drawable);

    //VIP描述
    mRvVip.setLayoutManager(new GridLayoutManager(this, 2));
    mRvVip.setNestedScrollingEnabled(false);
    mDescAdapter = new AdapterUserVipDesc(new ArrayList<VipDesc>());
    mRvVip.setAdapter(mDescAdapter);

    //价格列表
    mPriceAdapter = new AdapterUserVipPrice(new ArrayList<VipPrice>());
    mRvPrice.setLayoutManager(
        new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    mRvPrice.setNestedScrollingEnabled(false);
    mRvPrice.setAdapter(mPriceAdapter);
    ((SimpleItemAnimator) mRvPrice.getItemAnimator()).setSupportsChangeAnimations(false);
    mPriceAdapter.setOnItemSelectListener(new AdapterUserVipPrice.OnItemSelectListener() {
      @Override
      public void onItemSelected(int position) {
        selectedIndex = position;
      }
    });

    //滤镜
    mRvFilter.setLayoutManager(
        new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    mRvFilter.setNestedScrollingEnabled(false);
    mFilterAdapter =
        new AdapterVideoFilter(R.layout.item_filter_vip, new ArrayList<PLBuiltinFilterImpl>());
    mRvFilter.setAdapter(mFilterAdapter);

    //模板
    mRvDiy.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    mRvDiy.setNestedScrollingEnabled(false);
    mResAdapter = new AdapterAERes(R.layout.item_video_model_vip, new ArrayList<VModel>());
    mRvDiy.setAdapter(mResAdapter);
    mResAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        VModel model = mResAdapter.getData().get(position);
        UmUtil.anaDIYDetail(context, model.id, "VIP");
        Intent intent = new Intent(context, AEPreviewActivity.class);
        intent.putExtra(VModel.class.getSimpleName(), model);
        startActivity(intent);
      }
    });

    mRefreshLayout.setEnableLoadMore(false);
    mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
      @Override
      public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getP().loadVipPrice();
        getP().loadVipDesc();
        if (mFilterAdapter.getData().isEmpty()) {
          getP().loadVipFilters();
        }
        if (mResAdapter.getData().isEmpty()) {
          getP().loadVipDiys();
        }
      }
    });
    checkUserInfo();
    getP().loadVipPrice();
    getP().loadVipDesc();
    getP().loadVipFilters();
    getP().loadVipDiys();
  }

  public void setNewFilters(List<PLBuiltinFilterImpl> newFilters) {
    mTitleFilter.setVisibility(View.VISIBLE);
    mFilterAdapter.replaceData(newFilters);
    if (newFilters.isEmpty()) {
      mTitleFilter.setVisibility(View.GONE);
    }
  }

  public void setNewDiys(List<VModel> vModels) {
    mTitleDiy.setVisibility(View.VISIBLE);
    mResAdapter.replaceData(vModels);
  }

  public void setEmptyDiys() {
    mTitleDiy.setVisibility(View.GONE);
    mResAdapter.replaceData(new ArrayList<VModel>());
  }

  public void checkUserInfo() {
    mUserModel = LoginContext.getInstance().getUser();
    if (mUserModel != null) {
      if (mUserModel.isVipTry() && mUserModel.isNeedVerTel()) {
        Intent intent = new Intent(context, UserBindTelActivity.class);
        intent.putExtra("userModel", mUserModel);
        startActivity(intent);
      }
    }
  }

  public void setDescData(List<VipDesc> descData) {
    mDescAdapter.replaceData(descData);
  }

  public void setPriceData(List<VipPrice> priceData) {
    mPriceAdapter.replaceData(priceData);
    mRefreshLayout.finishRefresh();
    mPriceAdapter.setSelectItem(0);
    btnPay.setEnabled(true);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.btn_action:
        btnPay.setEnabled(false);
        mPriceAdapter.setSelectItem(selectedIndex);
        if (mPriceAdapter.getData().isEmpty()) {
          getP().loadVipPrice();
          getVDelegate().toastShort("价格获取失败，请刷新重试");
          return;
        }
        final VipPrice vipPrice = mPriceAdapter.getData().get(selectedIndex);
        VipPayDialog vipPayDialog = new VipPayDialog(UserVipActivity.this);
        vipPayDialog.showPayDialog(new VipPayDialog.DialogCallback() {
          @Override
          public void onPay(int payId) {
            UmUtil.anaVIPClickBuy(context, vipPrice.getType());
            if (mUserModel != null) {
              switch (payId) {
                case 0:
                  getP().loadWXOrder(vipPrice, mUserModel.getTel());
                  break;
                case 1:
                  getP().loadAlipayOrder(vipPrice, mUserModel.getTel());
                  break;
                default:
                  break;
              }
            } else {
              startActivity(new Intent(context, UserLoginActivity.class));
            }
          }
        });
        btnPay.setEnabled(true);
        break;
      case R.id.btn_back:
        finish();
        break;
      default:
        break;
    }
  }

  /**
   * 重新登录返回，绑定手机号返回，如果已经是VIP则直接退出
   */
  @Override
  protected void onRestart() {
    super.onRestart();
    getP().loadVipPrice();
    getP().loadVipDesc();
    if (mFilterAdapter != null && mFilterAdapter.getData().isEmpty()) {
      getP().loadVipFilters();
    }
    if (mResAdapter != null && mResAdapter.getData().isEmpty()) {
      getP().loadVipDiys();
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

  public void showNormal() {
    if (loadingDialog == null) {
      loadingDialog = new LoadingDialog(this);
      loadingDialog.setOnDismissListener(this);
      loadingDialog.setCancelable(true);
      loadingDialog.setCanceledOnTouchOutside(true);
    }
    if (loadingDialog != null) {
      if (!loadingDialog.isShowing() && !isFinishing()) {
        loadingDialog.show();
      }
      loadingDialog.setStateNormal();
    }
  }

  public void showWarn() {
    if (loadingDialog == null) {
      loadingDialog = new LoadingDialog(this);
      loadingDialog.setOnDismissListener(this);
      loadingDialog.setCancelable(true);
      loadingDialog.setCanceledOnTouchOutside(true);
    }
    if (loadingDialog != null) {
      if (!loadingDialog.isShowing() && !isFinishing()) {
        loadingDialog.show();
      }
      loadingDialog.setStateWarn();
    }
  }

  public void showSuccess() {
    if (loadingDialog == null) {
      loadingDialog = new LoadingDialog(this);
      loadingDialog.setOnDismissListener(this);
    }
    if (loadingDialog != null) {
      if (!loadingDialog.isShowing() && !isFinishing()) {
        loadingDialog.show();
      }
      loadingDialog.setStateSuccess();
    }
  }

  public void hideLoading() {
    if (loadingDialog != null && loadingDialog.isShowing()) {
      loadingDialog.dismiss();
      loadingDialog = null;
    }
  }

  @Override protected void onStop() {
    super.onStop();
    getP().release();
  }

  @Override
  protected void onDestroy() {
    if (isWebLaunch) {
      startActivity(new Intent(context, SplashActivity.class));
    }
    super.onDestroy();
    ImmersionBar.with(this).destroy();
  }

  @Override public void onDismiss(DialogInterface dialog) {
    if (loadingDialog != null) {
      int state = loadingDialog.getState();
      LogUtils.i("state--->" + state);
      if (state == LoadingDialog.STATE_SUCCESS) {
        finish();
      }
    }
  }
}
