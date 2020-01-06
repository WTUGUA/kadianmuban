package com.novv.dzdesk.ui.fragment.avatar;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import com.ark.baseui.XLazyFragment;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.novv.dzdesk.R;
import com.novv.dzdesk.http.DefaultDisposableObserver;
import com.novv.dzdesk.http.DefaultScheduler;
import com.novv.dzdesk.http.ServerApi;
import com.novv.dzdesk.res.model.AvatarBean;
import com.novv.dzdesk.res.model.AvatarResult;
import com.novv.dzdesk.res.model.BaseResult;
import com.novv.dzdesk.res.model.Level0Item;
import com.novv.dzdesk.res.model.Level1Item;
import com.novv.dzdesk.res.model.RcmdBean;
import com.novv.dzdesk.res.user.LoginContext;
import com.novv.dzdesk.ui.activity.avatar.AvatarDetailActivity;
import com.novv.dzdesk.ui.adapter.avatar.AdapterAvatarRcmd;
import com.novv.dzdesk.util.LogUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 头像推荐栏目
 */
public class TabAvatarRcmdFragment extends XLazyFragment {

  private SmartRefreshLayout refreshLayout;
  private RecyclerView recyclerView;
  private AdapterAvatarRcmd mAdapterAvatarRcmd;
  private boolean isLoading;

  @Override
  public int getLayoutId() {
    return R.layout.layout_refresh_list;
  }

  @Override
  public void setUpView(@NonNull View rootView) {
    super.setUpView(rootView);
    recyclerView = rootView.findViewById(R.id.recyclerView);
    refreshLayout = rootView.findViewById(R.id.refreshLayout);
  }

  @Nullable
  @Override
  public Object newP() {
    return null;
  }

  @Override
  public void initData(@Nullable Bundle savedInstanceState) {
    recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
    mAdapterAvatarRcmd = new AdapterAvatarRcmd(new ArrayList<MultiItemEntity>());
    refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
      @Override public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        loadData(false);
      }

      @Override public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        isLoading = false;
        loadData(true);
      }
    });
    refreshLayout.setEnableLoadMore(true);
    mAdapterAvatarRcmd.setEnableLoadMore(false);
    recyclerView.setAdapter(mAdapterAvatarRcmd);
    mAdapterAvatarRcmd.setSpanSizeLookup(new BaseQuickAdapter.SpanSizeLookup() {
      @Override
      public int getSpanSize(GridLayoutManager gridLayoutManager, int position) {
        return mAdapterAvatarRcmd.getItemViewType(position) == AdapterAvatarRcmd.TYPE_DATA
            ? 1 : 2;
      }
    });
    mAdapterAvatarRcmd.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        ArrayList<MultiItemEntity> currentData = new ArrayList<>(
            mAdapterAvatarRcmd.getData());
        ArrayList<AvatarBean> list = new ArrayList<>();
        int pos = 0;
        LogUtil.i("logger", "size--->" + currentData.size());
        if (position < currentData.size()) {

          List<Level0Item> level0Items = new ArrayList<>();
          for (MultiItemEntity entity : currentData) {
            if (entity instanceof Level0Item && !((Level0Item) entity).isAd) {
              level0Items.add((Level0Item) entity);
            }
          }
          MultiItemEntity item = currentData.get(position);
          if (item instanceof Level0Item) {
            if (((Level0Item) item).isAd) {
              LogUtil.i("logger", "点击了广告");
            } else {
              LogUtil.i("logger", "点击了标题");
            }
          } else if (item instanceof Level1Item) {
            int parentPos = ((Level1Item) item).parentPos;
            LogUtil.i("logger", "点击了内容,parentPos" + parentPos);
            Level0Item parent = level0Items.get(parentPos);
            List<Level1Item> level1Items = parent.getSubItems();
            if (level1Items == null || level1Items.isEmpty()) {
              return;
            }
            LogUtil.i("logger", "子内容" + level1Items.size() + "条");
            for (int i = 0; i < level1Items.size(); i++) {
              Level1Item level1Item = level1Items.get(i);
              AvatarBean avatarBean = new AvatarBean();
              avatarBean.setDesc(level1Item.desc);
              avatarBean.setFavs(level1Item.favorites);
              avatarBean.setFlagIsAd(false);
              avatarBean.setIdStr(level1Item.id);
              avatarBean.setName(level1Item.name);
              avatarBean.setNcos(level1Item.ncos);
              avatarBean.setRank(level1Item.rank);
              avatarBean.setThumb(level1Item.thumb);
              avatarBean.setTime(level1Item.time);
              avatarBean.setView(level1Item.view);
              list.add(avatarBean);
              if (TextUtils.equals(level1Item.id, ((Level1Item) item).id)) {
                pos = i;
              }
            }
            AvatarDetailActivity.launch(context, list, pos);
          }
        }
      }
    });
    loadData(true);
  }

  private void loadData(final boolean isPull) {
    if (isLoading) {
      return;
    }
    isLoading = true;
    String skip = getSkip();
    if (!isPull && TextUtils.equals(skip, "0")) {
      isLoading = false;
      refreshLayout.finishLoadMore(0, true, true);
      return;
    }
    ServerApi.getAvatarRcmd(isPull ? "0" : getSkip())
        .compose(DefaultScheduler.<BaseResult<AvatarResult>>getDefaultTransformer())
        .subscribe(new DefaultDisposableObserver<BaseResult<AvatarResult>>() {
          @Override
          public void onFailure(Throwable throwable) {
            isLoading = false;
            if (isPull) {
              refreshLayout.finishRefresh();
            } else {
              refreshLayout.finishLoadMore(0, false, false);
            }
          }

          @Override
          public void onSuccess(BaseResult<AvatarResult> result) {
            isLoading = false;
            List<MultiItemEntity> mList = new ArrayList<>();
            List<MultiItemEntity> last = mAdapterAvatarRcmd.getData();
            int lastPos = 0;
            for (MultiItemEntity entity : last) {
              if (entity instanceof Level1Item) {
                lastPos = ((Level1Item) entity).parentPos;
              }
            }
            if (isPull) {
              lastPos = 0;
            } else {
              lastPos++;
            }
            if (result != null && result.getRes() != null
                && result.getRes().getRecommendList() != null) {
              Calendar calendar = Calendar.getInstance();
              List<RcmdBean> rcmdBeans = result.getRes().getRecommendList();
              for (int i = 0; i < rcmdBeans.size(); i++) {
                RcmdBean rcmdBean = rcmdBeans.get(i);
                calendar.setTimeInMillis(rcmdBean.getStime());
                Level0Item lv0 = new Level0Item(rcmdBean.getTitle(),
                    String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)),
                    "/" + (calendar.get(Calendar.MONTH) + 1), false);
                List<AvatarBean> avatarBeans = rcmdBean.getItems();
                if (avatarBeans != null && !avatarBeans.isEmpty()) {
                  for (AvatarBean avatarBean : avatarBeans) {
                    Level1Item lv1 = new Level1Item();
                    lv1.desc = avatarBean.getDesc();
                    lv1.favorites = avatarBean.getFavs();
                    lv1.id = avatarBean.getIdStr();
                    lv1.name = avatarBean.getName();
                    lv1.ncos = avatarBean.getNcos();
                    lv1.parentPos = i + lastPos;
                    lv1.rank = avatarBean.getRank();
                    lv1.thumb = avatarBean.getThumb();
                    lv1.time = avatarBean.getTime();
                    lv1.view = avatarBean.getView();
                    lv0.addSubItem(lv1);
                  }
                }
                mList.add(lv0);
              }
              Level0Item ad = new Level0Item("", "", "", true);
              if (!LoginContext.getInstance().isVip() && mList.size() > 2) {
                mList.add(mList.size() - 1, ad);
              }
            }
            if (isPull) {
              onRefresh(mList);
            } else {
              onLoadMore(mList);
            }
          }
        });
  }

  private String getSkip() {
    List<MultiItemEntity> mDatas = mAdapterAvatarRcmd.getData();
    int count = 0;
    for (MultiItemEntity bean : mDatas) {
      if (bean instanceof Level1Item) {
        count++;
      }
    }
    return String.valueOf(count);
  }

  private void onRefresh(List<MultiItemEntity> list) {
    refreshLayout.finishRefresh();
    mAdapterAvatarRcmd.replaceData(list);
    mAdapterAvatarRcmd.expandAll();
  }

  private void onLoadMore(List<MultiItemEntity> list) {
    mAdapterAvatarRcmd.addData(list);
    mAdapterAvatarRcmd.expandAll();
    if (list.isEmpty()) {
      refreshLayout.finishLoadMore(0, true, true);
    } else {
      refreshLayout.finishLoadMore();
    }
  }
}
