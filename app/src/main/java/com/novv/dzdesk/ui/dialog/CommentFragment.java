package com.novv.dzdesk.ui.dialog;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.novv.dzdesk.R;
import com.novv.dzdesk.http.BaseObserver;
import com.novv.dzdesk.http.DefaultScheduler;
import com.novv.dzdesk.http.ServerApi;
import com.novv.dzdesk.res.dto.CommentModelListDTO;
import com.novv.dzdesk.res.model.BaseResult;
import com.novv.dzdesk.res.model.CommentList;
import com.novv.dzdesk.res.model.CommentModel;
import com.novv.dzdesk.res.model.UserModel;
import com.novv.dzdesk.ui.activity.user.UserDetailActivity;
import com.novv.dzdesk.ui.adapter.vwp.AdapterVwpResComment;
import com.novv.dzdesk.util.KeyBoardUtils;
import com.novv.dzdesk.util.ToastUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;
import java.util.ArrayList;
import java.util.List;

/**
 * 详情页面评论弹框 Created by lijianglong on 2017/9/3.
 */

public class CommentFragment extends BaseDialogFragment {

  public static final String tag = CommentFragment.class.getSimpleName();

  private CallBack callBack;
  private CharSequence tempChars;
  private int selectionStart;
  private int selectionEnd;
  private AdapterVwpResComment adapter;
  private EditText etInput;
  private ImageView ivClose;
  private TextView btnSend;
  private TextView tvNothing;
  private RecyclerView mRv;
  private String resId;
  private SmartRefreshLayout refreshLayout;
  private UserModel mUserInfo;

  public static CommentFragment launch(FragmentActivity activity, String resId,
      UserModel userModel) {
    CommentFragment fragment = new CommentFragment();
    Bundle bundle = new Bundle();
    bundle.putString("resId", resId);
    bundle.putSerializable("userModel", userModel);
    fragment.setArguments(bundle);
    fragment.show(activity, tag);
    return fragment;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setCancelable(true);
  }

  @Override
  public int setFragmentStyle() {
    setCancelable(true);
    return R.style.AppDialogLight;
  }

  @Override
  public void setWindowSize(Window window) {
    WindowManager.LayoutParams lp = window.getAttributes();
    lp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
    lp.gravity = Gravity.BOTTOM;
    window.setAttributes(lp);
  }

  @Override
  public void handleArgument(Bundle argument) {
    super.handleArgument(argument);
    //noinspection unchecked
    resId = argument.getString("resId");
    mUserInfo = (UserModel) argument.getSerializable("userModel");
  }

  @Override
  protected int getLayoutId() {
    return R.layout.dialog_comment_layout;
  }

  @Override
  public void initView(View view) {
    view.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        dismissAllowingStateLoss();
      }
    });
    ivClose = view.findViewById(R.id.iv_close);
    btnSend = view.findViewById(R.id.send_btn);
    etInput = view.findViewById(R.id.send_edt);
    tvNothing = view.findViewById(R.id.tv_no_comment_list);
    mRv = view.findViewById(R.id.recyclerView);
    refreshLayout = view.findViewById(R.id.refreshLayout);
  }

  @Override
  public void initData() {
    etInput.setHint(mUserInfo == null ? mContext.getString(R.string.comment_input_hint_login) :
        mContext.getString(R.string.comment_input_hint));
    ivClose.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        dismissAllowingStateLoss();
      }
    });
    btnSend.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        if (callBack != null) {
          callBack.toSendComment(etInput.getText().toString());
        }
        KeyBoardUtils.hideInputSoftKeybord(mContext, etInput);
        if (mUserInfo != null
            && !TextUtils.isEmpty(mUserInfo.getAuth())
            && !TextUtils.isEmpty(mUserInfo.getNickname())) {
          if (mUserInfo.isActive()) {
            List<CommentModel> mItems = adapter.getData();
            CommentModel commentModel = new CommentModel();
            commentModel.setCommentContent(etInput.getText().toString());
            commentModel.setCommentName(mUserInfo.getNickname());
            commentModel.setCommentPicture(mUserInfo.getImg());
            mItems.add(commentModel);
            adapter.notifyItemInserted(mItems.size() - 1);
            tvNothing.setVisibility(mItems.size() > 0 ? View.GONE : View.VISIBLE);
          }
        }
        etInput.setText("");
      }
    });
    etInput.addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        tempChars = s;
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        if (etInput.length() == 0 || TextUtils.isEmpty(s.toString())) {
          btnSend.setEnabled(false);
        } else {
          btnSend.setEnabled(true);
        }
        selectionStart = etInput.getSelectionStart();
        selectionEnd = etInput.getSelectionEnd();
        if (tempChars.length() > 50) {
          ToastUtil.showToast(mContext.getApplicationContext(),
              R.string.user_comment_edit_content_limit);
          s.delete(selectionStart - 1, selectionEnd);
          int tempSelection = selectionStart;
          etInput.setText(s);
          etInput.setSelection(tempSelection);
        }
      }
    });
    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),
        LinearLayoutManager.VERTICAL, false);
    mRv.setLayoutManager(layoutManager);
    adapter = new AdapterVwpResComment(new ArrayList<CommentModel>());
    mRv.setAdapter(adapter);
    adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
      @Override
      public void onItemChildClick(BaseQuickAdapter baseQuickAdapter, View view,
          int position) {
        List<CommentModel> mItems = adapter.getData();
        switch (view.getId()) {
          case R.id.img_avatar:
            CommentModel c = mItems.get(position);
            UserModel userModel = new UserModel();
            userModel.setNickname(c.getCommentName());
            userModel.setImg(c.getCommentPicture());
            userModel.setUserId(c.getCommentUserId());
            UserDetailActivity.start(mContext, userModel);
            break;
          case R.id.rl_praise:
            CommentModel c1 = mItems.get(position);
            if (callBack != null) {
              callBack.toPraiseComment(c1);
            }
            if (mUserInfo != null
                && !TextUtils.isEmpty(mUserInfo.getAuth())
                && !TextUtils.isEmpty(mUserInfo.getNickname())) {
              if (!c1.isPraise(mContext)) {
                String num = c1.getCommentPraiseNum();
                int count = 0;
                if (!TextUtils.isEmpty(num)) {
                  count = Integer.parseInt(num);
                }
                count = count + 1;
                c1.setPraise(mContext, true);
                c1.setCommentPraiseNum(String.valueOf(count < 0 ? 0 : count));
                adapter.notifyItemChanged(position);
              }
            }
            break;
        }
      }
    });
    adapter.setEnableLoadMore(false);
    refreshLayout.setEnableAutoLoadMore(true);
    refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
      @Override public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        requestData(false);
      }

      @Override public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        requestData(true);
      }
    });
  }

  @Override
  public void pullData() {
    refreshLayout.autoRefresh();
  }

  @Override
  public void reloadData() {
    super.reloadData();
    if (isViewPrepared) {
      requestData(true);
    }
  }

  private void requestData(final boolean isPull) {
    int skip = 0;
    List<CommentModel> mItems = adapter.getData();
    if (!isPull) {
      skip = mItems.size();
    }
    ServerApi.getCommentList(skip, ServerApi.LIMIT, resId)
        .compose(
            DefaultScheduler.<BaseResult<CommentList<List<CommentModelListDTO.CommentDTO>>>>getDefaultTransformer())
        .subscribe(new BaseObserver<CommentList<List<CommentModelListDTO.CommentDTO>>>() {
          @Override
          public void onSuccess(
              @NonNull CommentList<List<CommentModelListDTO.CommentDTO>> listCommentList) {
            List<CommentModelListDTO.CommentDTO> mDTOList = listCommentList.getList();
            List<CommentModel> list = new ArrayList<>();
            if (mDTOList != null && mDTOList.size() != 0) {
              CommentModelListDTO dto = new CommentModelListDTO(mDTOList);
              List<CommentModel> mList = dto.transform();
              if (mList != null && mList.size() != 0) {
                list.addAll(mList);
              }
            }
            if (isPull) {
              refreshLayout.finishRefresh();
              adapter.replaceData(list);
            } else {
              adapter.addData(list);
              if (list.isEmpty()) {
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
            List<CommentModel> mItems = adapter.getData();
            tvNothing.setVisibility(mItems.size() <= 0 ? View.VISIBLE : View.GONE);
          }
        });
  }

  public void setCallBack(CallBack callBack) {
    this.callBack = callBack;
  }

  @Override
  public void dismissAllowingStateLoss() {
    super.dismissAllowingStateLoss();
  }

  public interface CallBack {

    void toSendComment(String content);

    void toPraiseComment(CommentModel commentModel);
  }
}
