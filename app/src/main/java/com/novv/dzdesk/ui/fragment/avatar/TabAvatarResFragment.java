package com.novv.dzdesk.ui.fragment.avatar;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;
import com.ark.baseui.XLazyFragment;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.novv.dzdesk.R;
import com.novv.dzdesk.http.DefaultDisposableObserver;
import com.novv.dzdesk.http.DefaultScheduler;
import com.novv.dzdesk.http.ServerApi;
import com.novv.dzdesk.res.model.AvatarBean;
import com.novv.dzdesk.res.model.AvatarResult;
import com.novv.dzdesk.res.model.BaseResult;
import com.novv.dzdesk.res.user.LoginContext;
import com.novv.dzdesk.ui.activity.avatar.AvatarDetailActivity;
import com.novv.dzdesk.ui.adapter.avatar.AdapterAvatarRes;
import com.novv.dzdesk.util.LogUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import java.util.ArrayList;
import java.util.List;

import static com.novv.dzdesk.res.model.MultipleItem.TYPE_AD;

/**
 * 头像资源
 */
public class TabAvatarResFragment extends XLazyFragment {

  private static final int limit = 18;
  private int request_type = 0;
  private SmartRefreshLayout refreshLayout;
  private RecyclerView recyclerView;
  private AdapterAvatarRes mAdapter;
  private boolean isLoading;
  private String cid = "";
  private int skip = 0;

  public static TabAvatarResFragment getInstance(int request_type, Bundle bundle) {
    bundle.putInt("request_type", request_type);
    TabAvatarResFragment fragment = new TabAvatarResFragment();
    fragment.setArguments(bundle);
    return fragment;
  }

  @Override
  public int getLayoutId() {
    return R.layout.hot_avatar_fragment;
  }

  @Nullable
  @Override
  public Object newP() {
    return null;
  }

  @Override
  public void setUpView(@NonNull View rootView) {
    super.setUpView(rootView);
    refreshLayout = rootView.findViewById(R.id.refreshLayout);
    recyclerView = rootView.findViewById(R.id.recyclerView);
  }

  @Override
  public void initData(@Nullable Bundle savedInstanceState) {
    Bundle argument = getArguments();
    if (argument != null) {
      request_type = argument.getInt("request_type");
      if (request_type == 2) {
        cid = argument.getString("cid", "55f7d52969401b227e98de4f");
      }
    }
    mAdapter = new AdapterAvatarRes(new ArrayList<AvatarBean>(), false);
    mAdapter.setEnableLoadMore(false);
    refreshLayout.setEnableAutoLoadMore(true);
    recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
    recyclerView.setAdapter(mAdapter);
    refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
      @Override public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        loadDatas(false);
      }

      @Override public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        isLoading = false;
        loadDatas(true);
      }
    });
    mAdapter.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
      @Override
      public int getSpanSize(GridLayoutManager gridLayoutManager, int position) {
        return mAdapter.getItemViewType(position) == TYPE_AD ? 3 : 1;
      }
    });
    mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        ArrayList<AvatarBean> currentData = new ArrayList<>(mAdapter.getData());
        if (position < currentData.size()) {
          ArrayList<AvatarBean> mList = new ArrayList<>();
          int adCount = 0;
          for (int i = 0; i < currentData.size(); i++) {
            AvatarBean bean = currentData.get(i);
            if (bean.getFlagIsAd()) {
              if (i < position) {
                adCount++;
              }
            } else {
              mList.add(bean);
            }
          }
          try {
            AvatarDetailActivity.launch(context, mList, position - adCount);
          } catch (Exception e) {
            Toast.makeText(context, "出错啦！", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
            loadDatas(true);
          }
        }
      }
    });
    loadDatas(true);
  }

  private void loadDatas(final boolean isPull) {
    if (isLoading) {
      return;
    }
    isLoading = true;
    if (isPull) {
      skip = 0;
    }
    LogUtil.i("logger", "request_type--->" + request_type + ",id---->" + cid);
    String mSkip = String.valueOf(skip);
    skip += limit;
    ServerApi.getAvatarList(request_type, mSkip, cid)
        .compose(DefaultScheduler.<BaseResult<AvatarResult>>getDefaultTransformer())
        .subscribe(new DefaultDisposableObserver<BaseResult<AvatarResult>>() {
          @Override
          public void onFailure(Throwable throwable) {
            isLoading = false;
          }

          @Override
          public void onSuccess(BaseResult<AvatarResult> result) {
            isLoading = false;
            List<AvatarBean> mList = new ArrayList<>();
            if (result != null && result.getRes() != null
                && result.getRes().getAvatarList() != null) {
              List<AvatarBean> list = result.getRes().getAvatarList();
              if (!LoginContext.getInstance().isVip() && list.size() == limit) {
                AvatarBean avatarBean = new AvatarBean();
                avatarBean.setFlagIsAd(true);
                list.add(avatarBean);
              }
              mList.addAll(list);
            }
            if (isPull) {
              onRefresh(mList);
            } else {
              onLoadMore(mList);
            }
          }
        });
  }

  private void onRefresh(@NonNull List<AvatarBean> newData) {
    mAdapter.replaceData(newData);
    refreshLayout.finishRefresh();
  }

  private void onLoadMore(@NonNull List<AvatarBean> newData) {
    mAdapter.addData(newData);
    if (newData.isEmpty()) {
      refreshLayout.finishLoadMore(0, true, true);
    } else {
      refreshLayout.finishLoadMore();
    }
  }
}
