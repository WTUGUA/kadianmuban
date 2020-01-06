package com.novv.dzdesk.ui.fragment.vwp;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.ark.adkit.basics.handler.Action;
import com.ark.adkit.basics.handler.Run;
import com.ark.baseui.XLazyFragment;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.novv.dzdesk.R;
import com.novv.dzdesk.http.DefaultDisposableObserver;
import com.novv.dzdesk.http.DefaultScheduler;
import com.novv.dzdesk.http.ServerApi;
import com.novv.dzdesk.loader.GlideBannerLoader;
import com.novv.dzdesk.res.model.BaseResult;
import com.novv.dzdesk.res.model.DataEntity;
import com.novv.dzdesk.res.model.ResourceBean;
import com.novv.dzdesk.res.model.VModel;
import com.novv.dzdesk.ui.activity.WebUrlActivity;
import com.novv.dzdesk.ui.activity.ae.AEPreviewActivity;
import com.novv.dzdesk.ui.activity.vwp.VwpResDetailActivity;
import com.novv.dzdesk.ui.adapter.vwp.AdapterVwpResWithAds;
import com.novv.dzdesk.ui.presenter.PresentVwpRes;
import com.novv.dzdesk.util.AnaUtil;
import com.novv.dzdesk.util.NetUtil;
import com.novv.dzdesk.util.UmUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 视频壁纸资源（推荐，最新，热门，定制模板）
 */
@SuppressLint("DefaultLocale")
public class TabVwpResFragment extends XLazyFragment<PresentVwpRes> {

  public static final int TYPE_HOT = 0;
  public static final int TYPE_RECOMMEND = 1;
  public static final int TYPE_NEW = 2;
  public static final int TYPE_CATEGORY = 3;
  public static final int TYPE_AMUSEMENT = 4;
  private GridLayoutManager layoutManager;
  private RecyclerView mRecyclerView;
  private SmartRefreshLayout mRefreshLayout;
  private AdapterVwpResWithAds mAdAdapter;
  private Map<String, ResourceBean> resourceBeanMap = new HashMap<>();
  private RecyclerView.OnScrollListener scroll = new RecyclerView.OnScrollListener() {
    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
      super.onScrollStateChanged(recyclerView, newState);
      switch (newState) {
        case RecyclerView.SCROLL_STATE_DRAGGING:
          break;
        case RecyclerView.SCROLL_STATE_SETTLING:
          break;
        case RecyclerView.SCROLL_STATE_IDLE:
          int lastItemPosition1 = layoutManager.findLastVisibleItemPosition();
          int firstItemPosition1 = layoutManager.findFirstVisibleItemPosition();
          anaItemView(firstItemPosition1, lastItemPosition1);
          break;
      }
    }
  };
  private View bannerLayout, loadingView;
  private Banner bannerView;

  public static TabVwpResFragment newInstance(int type, String categoryId) {
    Bundle args = new Bundle();
    if (!TextUtils.isEmpty(categoryId)) {
      args.putString("categoryId", categoryId);
    }
    args.putInt("type", type);
    TabVwpResFragment fragment = new TabVwpResFragment();
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
  public String getCategoryId() {
    String categoryId = null;
    Bundle argument = getArguments();
    if (argument != null) {
      categoryId = argument.getString("categoryId");
    }
    return categoryId;
  }

  @Override
  public int getLayoutId() {
    return R.layout.content_fragment;
  }

  @Override
  public void initData(@Nullable Bundle savedInstanceState) {
    layoutManager = new GridLayoutManager(context, 3);
    mRecyclerView.setLayoutManager(layoutManager);
    mAdAdapter = new AdapterVwpResWithAds(new ArrayList<ResourceBean>());
    mAdAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (position == -1 || position >= mAdAdapter.getData().size()) {
          return;
        }
        ResourceBean bean = mAdAdapter.getData().get(position);
        if (bean.getvModel() != null) {
          Intent intent = new Intent(context, AEPreviewActivity.class);
          UmUtil.anaDIYDetail(context, bean.getvModel().id, "推荐");
          intent.putExtra(VModel.class.getSimpleName(), bean.getvModel());
          startActivity(intent);
        } else if (!TextUtils.isEmpty(bean.getCoverURL())) {
          VwpResDetailActivity.launch(context, mAdAdapter.getData().get(position));
        }
      }
    });
    mRecyclerView.addOnScrollListener(scroll);
    mRefreshLayout.setDisableContentWhenRefresh(true);
    mAdAdapter.bindToRecyclerView(mRecyclerView);
    mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
      @Override public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        getP().loadVwpRes(false);
        getP().loadBannerData();
      }
    });
    mAdAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
      @Override public void onLoadMoreRequested() {
        getP().loadVwpRes(true);
      }
    },mRecyclerView);
    mAdAdapter.setPreLoadNumber(3);
    mAdAdapter.setEnableLoadMore(true);
    mRefreshLayout.setEnableLoadMore(false);
    if (getDataType() == TYPE_NEW) {
      mAdAdapter.addHeaderView(bannerLayout);
    }
    mRefreshLayout.autoRefresh();
  }

  @Nullable
  @Override
  public PresentVwpRes newP() {
    return new PresentVwpRes();
  }

  @Override
  public void setUpView(@NonNull View rootView) {
    super.setUpView(rootView);
    mRefreshLayout = rootView.findViewById(R.id.refreshLayout);
    mRecyclerView = rootView.findViewById(R.id.recyclerView);
    loadingView = rootView.findViewById(R.id.loading);
    bannerLayout = LayoutInflater.from(context)
        .inflate(R.layout.view_header_banner, mRecyclerView, false);
    bannerView = bannerLayout.findViewById(R.id.bannerView);
  }

  public void setNoBanner() {
    bannerView.setVisibility(View.GONE);
  }

  public void setBannerData(@NonNull List<String> images, @NonNull List<String> titles,
      final @NonNull List<DataEntity> datas) {
    if (images.size() < 1 || titles.size() < 1 || images.size() != titles.size()) {
      bannerView.setVisibility(View.GONE);
      return;
    }
    bannerView.setVisibility(View.VISIBLE);
    bannerView.setOnBannerListener(new OnBannerListener() {
      @Override
      public void OnBannerClick(int i) {
        if (i < datas.size()) {
          DataEntity dataEntity = datas.get(i);
          String value = dataEntity.getValue();
          if (dataEntity.isVideoBanner()) {
            loadingView.setVisibility(View.VISIBLE);
            ResourceBean bean = resourceBeanMap.get(value);
            if (bean != null) {
              loadingView.setVisibility(View.GONE);
              VwpResDetailActivity.launch(context, bean);
            } else {
              getSourceInfo(value);
            }
          } else if (dataEntity.isWebBanner()) {
            WebUrlActivity.launch(context, value, dataEntity.getTitle());
          } else if (dataEntity.isAppBanner()) {
            //todo 下载 app
          }
        }
      }
    });
    bannerView.setBannerStyle(BannerConfig.NUM_INDICATOR_TITLE);
    bannerView.setImageLoader(new GlideBannerLoader());
    bannerView.setImages(images);
    bannerView.setBannerTitles(titles);
    bannerView.isAutoPlay(true);
    bannerView.setDelayTime(3500);
    bannerView.setIndicatorGravity(BannerConfig.CENTER);
    bannerView.start();
  }

  /**
   * 根据资源 ID 获取资源详细信息
   *
   * @param sourceId 资源 ID
   */
  private void getSourceInfo(final String sourceId) {
    ServerApi.getVideoWpDetails(sourceId)
        .compose(DefaultScheduler.<BaseResult<ResourceBean>>getDefaultTransformer())
        .subscribe(new DefaultDisposableObserver<BaseResult<ResourceBean>>() {
          @Override
          public void onFailure(Throwable throwable) {

          }

          @Override
          public void onSuccess(BaseResult<ResourceBean> result) {
            if (result.getCode() == 0) {
              ResourceBean resourceBean = result.getRes();
              resourceBeanMap.put(sourceId, resourceBean);
              loadingView.setVisibility(View.GONE);
              VwpResDetailActivity.launch(context, resourceBean);
            }
          }
        });
  }

  /**
   * 分析统计可见资源
   *
   * @param startPos 开始位置
   * @param endPos 终止位置
   */
  private void anaItemView(final int startPos, final int endPos) {
    Run.onBackground(new Action() {
      @Override
      public void call() {
        try {
          for (int i = startPos; i <= endPos; i++) {
            ResourceBean bean = mAdAdapter.getData().get(i);
            bean.setAnalyticViewed(true);
            bean.setAnalyticViewedIndex(i);
            if (!TextUtils.isEmpty(bean.getCoverURL())) {
              AnaUtil.anaView(context, bean);
            }
          }
        } catch (Exception e) {
          //
        }
      }
    });
  }

  public void setError(boolean isLoadMore) {
    if (isLoadMore) {
      mAdAdapter.loadMoreFail();
      //mRefreshLayout.finishLoadMore(0, false, false);
    } else {
      mRefreshLayout.finishRefresh();
      mAdAdapter.replaceData(new ArrayList<ResourceBean>());
      View view = mAdAdapter.getEmptyView();
      if (view == null) {
        view = LayoutInflater.from(context).inflate(R.layout.empty_view, null);
        ImageView imageView = view.findViewById(R.id.imageView);
        imageView.setImageResource(
            NetUtil.isNetworkAvailable(context) ? R.mipmap.ic_empty_nores :
                R.mipmap.ic_empty_nonet);
        TextView textView = view.findViewById(R.id.tvEmpty);
        textView.setText("暂无资源~");
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
      mAdAdapter.loadMoreEnd();
      //mRefreshLayout.finishLoadMore(0, true, true);
    } else {
      mAdAdapter.loadMoreComplete();
      //mRefreshLayout.finishLoadMore();
    }
  }
}
