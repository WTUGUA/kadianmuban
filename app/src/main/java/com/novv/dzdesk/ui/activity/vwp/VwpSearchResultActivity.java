package com.novv.dzdesk.ui.activity.vwp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import com.ark.baseui.XAppCompatActivity;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.novv.dzdesk.Const;
import com.novv.dzdesk.R;
import com.novv.dzdesk.http.BaseObserver;
import com.novv.dzdesk.http.DefaultScheduler;
import com.novv.dzdesk.http.ServerApi;
import com.novv.dzdesk.res.manager.HistoryManager;
import com.novv.dzdesk.res.model.BaseResult;
import com.novv.dzdesk.res.model.ResourceBean;
import com.novv.dzdesk.res.user.LoginContext;
import com.novv.dzdesk.ui.adapter.vwp.AdapterVwpResWithAds;
import com.novv.dzdesk.util.AnaUtil;
import com.novv.dzdesk.util.LogUtil;
import com.novv.dzdesk.util.NetUtil;
import com.novv.dzdesk.util.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import com.umeng.analytics.MobclickAgent;
import io.reactivex.Observer;
import java.util.ArrayList;
import java.util.List;

@SuppressLint("DefaultLocale")
public class VwpSearchResultActivity extends XAppCompatActivity implements
    View.OnClickListener {

  private static final String KEY_KEYWORD = "key_title";
  private String tag = VwpSearchResultActivity.class.getSimpleName();
  private View resultSearchTv;
  private EditText mInputEt;
  private View closeIv;
  private String mSearchKeyword;
  private RecyclerView mRecyclerView;
  private InputMethodManager imm;
  private GridLayoutManager layoutManager;
  private AdapterVwpResWithAds mAdapter;
  private SmartRefreshLayout refreshLayout;

  private ArrayList<ResourceBean> mItems = new ArrayList<>();

  public static void launch(Context context, String keyword) {
    if (TextUtils.isEmpty(keyword)) {
      ToastUtil.showToast(context, R.string.search_input_empty);
      return;
    }
    Intent intent = new Intent(context, VwpSearchResultActivity.class);
    intent.putExtra(KEY_KEYWORD, keyword);
    context.startActivity(intent);
  }

  @Override
  public int getLayoutId() {
    return R.layout.activity_search_result;
  }

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    // 关闭软键盘
    getWindow().setSoftInputMode(
        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    super.onCreate(savedInstanceState);
  }

  @Nullable
  @Override
  public Object newP() {
    return null;
  }

  @Override
  public void setUpView(@NonNull View rootView) {
    super.setUpView(rootView);
    resultSearchTv = findViewById(R.id.result_search_tv);
    closeIv = findViewById(R.id.result_close_iv);
    mInputEt = findViewById(R.id.search_input_et);
    refreshLayout = findViewById(R.id.refreshLayout);
    mRecyclerView = findViewById(R.id.recyclerView);
  }

  @Override
  public void initData(@Nullable Bundle bundle) {
    closeIv.setOnClickListener(this);
    mInputEt.setText(mSearchKeyword);
    Intent intent = getIntent();
    mSearchKeyword = intent.getStringExtra(KEY_KEYWORD);
    resultSearchTv.setOnClickListener(this);
    closeIv.setOnClickListener(this);
    mInputEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId,
          KeyEvent event) {
        // 软键盘搜索
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
          mSearchKeyword = mInputEt.getText().toString();
          startSearch(VwpSearchResultActivity.this);
          // 关闭软键盘
          closeSoftKeyBord();
          return true;
        }
        return false;
      }
    });
    //设置布局管理
    layoutManager = new GridLayoutManager(this, 3) {
      @Override
      public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
          super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException e) {
          e.printStackTrace();
        }
      }
    };
    mRecyclerView.setLayoutManager(layoutManager);
    mAdapter = new AdapterVwpResWithAds(mItems);
    mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        switch (newState) {
          case RecyclerView.SCROLL_STATE_DRAGGING:
            int lastItemPosition1 = layoutManager.findLastVisibleItemPosition();
            int firstItemPosition1 = layoutManager.findFirstVisibleItemPosition();
            anaItemView(firstItemPosition1, lastItemPosition1);
            break;
          case RecyclerView.SCROLL_STATE_SETTLING:
            break;
          case RecyclerView.SCROLL_STATE_IDLE:
            break;
          default:
            break;
        }
      }
    });
    mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
      @Override
      public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        if (mItems == null || mItems.isEmpty() || position >= mItems.size()) {
          ToastUtil.showToast(view.getContext(), R.string.op_failed);
          return;
        }
        VwpResDetailActivity.launch(VwpSearchResultActivity.this, mItems.get(position));
      }
    });
    mAdapter.bindToRecyclerView(mRecyclerView);
    mAdapter.disableLoadMoreIfNotFullPage(mRecyclerView);
    refreshLayout.setEnableLoadMore(true);
    mAdapter.setEnableLoadMore(false);
    refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
      @Override public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        requestData(false);
      }

      @Override public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        requestData(true);
      }
    });
    mAdapter.setEmptyView(R.layout.empty_view, mRecyclerView);
    View mEmptyView = mAdapter.getEmptyView();
    if (mEmptyView != null) {
      mEmptyView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          LogUtil.i(tag, "v == " + v);
          if (!NetUtil.isNetworkAvailable(v.getContext())) {
            try {
              Intent intent = new Intent(Settings.ACTION_SETTINGS);
              v.getContext().startActivity(intent);
            } catch (Exception e) {
              ToastUtil.showToast(v.getContext(), R.string.op_failed);
              e.printStackTrace();
            }
          } else {
            refreshLayout.autoRefresh();
          }
        }
      });
    }
    refreshLayout.autoRefresh();
  }

  private void closeSoftKeyBord() {
    if (imm.isActive()) {
      imm.hideSoftInputFromWindow(mInputEt.getWindowToken(), 0);
    }
    getWindow().setSoftInputMode(
        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
  }

  /**
   * 分析统计可见资源
   *
   * @param startPos 开始位置
   * @param endPos 终止位置
   */
  private void anaItemView(final int startPos, final int endPos) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          LogUtil.i(tag, String.format("开启线程统计从%1$d到%2$d的数据展示", startPos, endPos));
          for (int i = startPos; i <= endPos; i++) {
            ResourceBean bean = mItems.get(i);
            bean.setAnalyticViewed(true);
            bean.setAnalyticViewedIndex(i);
            AnaUtil.anaView(VwpSearchResultActivity.this, bean);
          }
        } catch (Exception e) {
          LogUtil.e(tag, e.getMessage());
        }
      }
    }).start();
  }

  /**
   * 请求资源数据
   *
   * @param isPullRefresh 是否是下拉刷新,区分分页加载
   */
  private void requestData(final boolean isPullRefresh) {
    LogUtil.d(tag, "requestData---->" + (isPullRefresh ? "pull" : "loadmore"));
    int skip = 0;
    if (!isPullRefresh) {
      for (ResourceBean bean : mItems) {
        if (bean.getContentType() != Const.OnlineKey.CONTENT_TYPE_AD) {
          skip++;
        }
      }
    } else {
      mItems.clear();
    }
    LogUtil.i(tag, "skip=" + skip);
    ServerApi.getSearch(mSearchKeyword, skip)
        .compose(DefaultScheduler.<BaseResult<List<ResourceBean>>>getDefaultTransformer())
        .subscribe(getDefaultObserver(isPullRefresh));
  }

  private Observer<BaseResult<List<ResourceBean>>> getDefaultObserver(final boolean isPull) {
    return new BaseObserver<List<ResourceBean>>() {

      @Override
      public void onSuccess(@NonNull List<ResourceBean> mList) {
        if (isPull) {
          refreshLayout.finishRefresh();
        }
        List<ResourceBean> resourceBeanList = new ArrayList<>();
        if (!mList.isEmpty()) {
          resourceBeanList.addAll(mList);
          if (!LoginContext.getInstance().isVip()) {
            int size = mList.size();
            for (int i = 0; i < size; i++) {
              if (i < 8) {
                i += 8;
              } else {
                i += 9;
              }
              if (i < mList.size()) {
                ResourceBean adBean = null;
                if (!mList.isEmpty()) {
                  adBean = (ResourceBean) mList.get(0).deepClone();
                }
                if (adBean == null) {
                  adBean = new ResourceBean();
                }
                adBean.setContentType(Const.OnlineKey.CONTENT_TYPE_AD);
                LogUtil.d(tag, String.format("向第%d条插入一条广告", i + 1));
                resourceBeanList.add(i, adBean);
              }
              i += 1;
            }
          }
        }
        if (isPull) {
          mAdapter.replaceData(resourceBeanList);
        } else {
          mAdapter.addData(resourceBeanList);
          if (mList.isEmpty()) {
            refreshLayout.finishLoadMore(0, true, true);
          } else {
            refreshLayout.finishLoadMore();
          }
        }
      }

      @Override
      public void onFailure(int code, @NonNull String message) {
        if (!isPull) {
          refreshLayout.finishLoadMore(0, false, false);
        } else {
          refreshLayout.finishRefresh();
        }
      }
    };
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.result_search_tv:// 结果页里面搜索
        startSearch(v.getContext());
        break;
      case R.id.result_close_iv:
        if (TextUtils.isEmpty(mInputEt.getText().toString())) {
          ToastUtil.showToast(v.getContext(), R.string.op_success);
          break;
        }
        mInputEt.setText("");
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
        break;
      default:
        break;
    }
  }

  public void startSearch(Context context) {
    if (TextUtils.isEmpty(mInputEt.getText().toString())) {
      ToastUtil.showToast(context, R.string.search_input_empty);
      return;
    }

    mSearchKeyword = mInputEt.getText().toString();
    if (refreshLayout != null) {
      refreshLayout.autoRefresh();
    }
    HistoryManager.saveHistory2File(this, mSearchKeyword, false,
        VwpSearchActivity.sMaxHistorySize);
    // 关闭软键盘
    closeSoftKeyBord();
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
