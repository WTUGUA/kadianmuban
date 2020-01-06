package com.novv.dzdesk.ui.dialog;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.novv.dzdesk.R;
import com.novv.dzdesk.http.DefaultScheduler;
import com.novv.dzdesk.http.ServerApi;
import com.novv.dzdesk.http.SimpleObserver;
import com.novv.dzdesk.res.model.BaseResult;
import com.novv.dzdesk.res.model.UserModel;
import com.novv.dzdesk.util.ToastUtil;

/**
 * 设置昵称对话框 Created by lijianglong on 2017/9/7.
 */

public class SetInfoDialog extends BaseDialogFragment implements View.OnClickListener {

    private static final String tag = SetInfoDialog.class.getSimpleName();

    private UserModel userModel;
    private EditText etInput;

    public static void launch(FragmentActivity activity, UserModel userModel) {
        SetInfoDialog fragment = new SetInfoDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("userModel", userModel);
        fragment.setArguments(bundle);
        fragment.show(activity, tag);
    }

    @Override
    public void handleArgument(Bundle argument) {
        super.handleArgument(argument);
        userModel = (UserModel) argument.getSerializable("userModel");
        if (userModel == null) {
            userModel = new UserModel();
        }
    }

    @Override
    public void setWindowSize(Window window) {
        setCancelable(true);
    }

    @Override
    public int setFragmentStyle() {
        return R.style.AppDialogDark;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_edit_info;
    }

    @Override
    public void initView(View view) {

        TextView tvLogin = view.findViewById(R.id.btn_sure);
        ImageView ivClose = view.findViewById(R.id.btn_close);
        etInput = view.findViewById(R.id.et_input);

        ivClose.setOnClickListener(this);
        tvLogin.setOnClickListener(this);
        etInput.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void pullData() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sure:
                if (TextUtils.isEmpty(etInput.getText().toString().trim())) {
                    ToastUtil.showToast(mContext, "昵称不能为空");
                    return;
                }
                editComplete();
                break;
            case R.id.btn_close:
                dismissAllowingStateLoss();
                break;
        }
    }

    private void editComplete() {
        ServerApi.modifyUserInfo(etInput.getText().toString().trim()
                , userModel.getDesc())
                .compose(DefaultScheduler.<BaseResult>getDefaultTransformer())
                .subscribe(new SimpleObserver() {
                    @Override
                    public void onSuccess() {
                        userModel.setNickname(etInput.getText().toString().trim());
                        ToastUtil.showToast(mContext, "昵称修改成功");
                        dismissAllowingStateLoss();
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        dismissAllowingStateLoss();
                    }

                    @Override
                    public void onLogout() {
                        super.onLogout();
                        ToastUtil.showToast(mContext, "登录失效，请重新登录");
                    }
                });
    }
}
