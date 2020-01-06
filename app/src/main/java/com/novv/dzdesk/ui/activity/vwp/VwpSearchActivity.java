package com.novv.dzdesk.ui.activity.vwp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.ark.baseui.XAppCompatActivity;
import com.novv.dzdesk.R;
import com.novv.dzdesk.http.BaseObserver;
import com.novv.dzdesk.http.DefaultScheduler;
import com.novv.dzdesk.http.ServerApi;
import com.novv.dzdesk.live.PrefUtil;
import com.novv.dzdesk.res.manager.HistoryManager;
import com.novv.dzdesk.res.model.BaseResult;
import com.novv.dzdesk.ui.adapter.vwp.AdapterVwpHistory;
import com.novv.dzdesk.ui.view.FlowLayout;
import com.novv.dzdesk.util.LogUtil;
import com.novv.dzdesk.util.ToastUtil;
import com.umeng.analytics.MobclickAgent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VwpSearchActivity extends XAppCompatActivity
    implements View.OnClickListener {

  private static final String TAG = "VwpSearchActivity";
  public static int sMaxHistorySize = 5;
  private FlowLayout flowLayout;
  private EditText mInputET;
  private ImageView closeIv;
  private View searchView;
  private View clearRl;
  private ListView mListView;
  private AdapterVwpHistory mAdapter;
  private ArrayList<String> mHistoryWords = new ArrayList<>();

  public static void launch(Context context) {
    if (context == null) {
      return;
    }
    Intent intent = new Intent(context, VwpSearchActivity.class);
    context.startActivity(intent);
  }

  @Override
  public int getLayoutId() {
    return R.layout.activity_search;
  }

  @Override
  public void setUpView(@NonNull View rootView) {
    super.setUpView(rootView);
    mInputET = findViewById(R.id.search_auto_text);
    flowLayout = findViewById(R.id.flowlayout);
    closeIv = findViewById(R.id.auto_close_iv);
    searchView = findViewById(R.id.search_search_tv);
    clearRl = findViewById(R.id.clear_history_layout);
    mListView = findViewById(R.id.history_list);
  }

  @Nullable
  @Override
  public Object newP() {
    return null;
  }

  @Override
  public void initData(@Nullable Bundle bundle) {
    mAdapter = new AdapterVwpHistory(this, mHistoryWords);
    mListView.setAdapter(mAdapter);
    mAdapter.notifyDataSetChanged();
    String savedStr = PrefUtil.getString(VwpSearchActivity.this, "hotTagString", "");
    if (savedStr != null && savedStr.contains(",")) {
      String[] arr = savedStr.split(",");
      List<String> stringList = Arrays.asList(arr);
      if (stringList.size() > 0) {
        addHotTag(stringList);
      }
    }
    getHotTag();
    viewOnClick();
  }

  private void viewOnClick() {
    searchView.setOnClickListener(this);
    closeIv.setOnClickListener(this);
    clearRl.setOnClickListener(this);
    // 软键盘搜索
    mInputET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
      @Override
      public boolean onEditorAction(TextView v, int actionId,
          KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
          LogUtil.i(TAG,
              "onEditorAction EditorInfo.IME_ACTION_SEARCH = "
                  + mInputET.getText());
          launchRearchResult(mInputET.getText().toString());
          return true;
        }
        return false;
      }
    });
    // 底部历史记录点击搜索
    mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view,
          int position, long id) {
        if (position >= mHistoryWords.size()) {
          ToastUtil
              .showToast(VwpSearchActivity.this, R.string.op_failed);
          return;
        }
        String title = mHistoryWords.get(position);
        launchRearchResult(title);
      }
    });
  }

  private void getHotTag() {
    ServerApi.getHotTag()
        .compose(DefaultScheduler.<BaseResult<List<String>>>getDefaultTransformer())
        .subscribe(new BaseObserver<List<String>>() {
          @Override
          public void onSuccess(@NonNull List<String> strings) {
            if (strings.size() > 0) {
              addHotTag(strings);
              StringBuilder sb = new StringBuilder();
              for (String s : strings) {
                sb.append(s).append(",");
              }
              PrefUtil.putString(VwpSearchActivity.this, "hotTagString", sb.toString());
            }
          }

          @Override
          public void onFailure(int code, @NonNull String message) {

          }
        });
  }

  /**
   * 添加热门搜索标签
   *
   * @param hotList 非空的热门标签集合
   */
  private void addHotTag(List<String> hotList) {
    LayoutInflater mInflater = LayoutInflater.from(VwpSearchActivity.this);
    // 加入大家都在搜标签
    flowLayout.removeAllViews();
    for (String s : hotList) {
      final TextView tagTv = (TextView) mInflater.inflate(
          R.layout.tag_tv, flowLayout, false);
      tagTv.setText(s);
      flowLayout.addView(tagTv);
      // 加入标签点击事件
      tagTv.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          launchRearchResult(tagTv.getText().toString());
        }
      });
    }
  }

  // 执行搜索功能并打开新的activity展示数据
  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.clear_history_layout:
        mHistoryWords.clear();
        mAdapter.notifyDataSetChanged();
        HistoryManager.clearHistory(this);
        break;
      case R.id.search_search_tv:
        launchRearchResult(mInputET.getText().toString());
        break;
      case R.id.auto_close_iv:
        if (TextUtils.isEmpty(mInputET.getText().toString())) {
          ToastUtil.showToast(v.getContext(), R.string.op_success);
          break;
        }
        mInputET.setText("");
        break;
      default:
        break;
    }
  }

  private void launchRearchResult(String keyword) {
    if (TextUtils.isEmpty(keyword)) {
      ToastUtil.showToast(this, R.string.search_input_empty);
      return;
    }
    HistoryManager.saveHistory2File(this, keyword, false, sMaxHistorySize);
    refreshHistory();
    VwpSearchResultActivity.launch(this, keyword);
  }

  public void refreshHistory() {
    mHistoryWords.clear();
    mHistoryWords.addAll(HistoryManager.getHistoryFromFile(this));
    mAdapter.notifyDataSetChanged();
  }

  @Override
  protected void onResume() {
    MobclickAgent.onResume(this);
    super.onResume();
    refreshHistory();
  }

  @Override
  protected void onPause() {
    MobclickAgent.onPause(this);
    super.onPause();
  }
}
