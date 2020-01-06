package com.novv.dzdesk.ui.presenter;

import android.support.annotation.NonNull;
import com.ark.baseui.XPresent;
import com.novv.dzdesk.http.BaseObserver;
import com.novv.dzdesk.http.DefaultScheduler;
import com.novv.dzdesk.http.ServerApi;
import com.novv.dzdesk.http.SimpleObserver;
import com.novv.dzdesk.res.model.BaseResult;
import com.novv.dzdesk.res.model.UserModel;
import com.novv.dzdesk.res.user.LoginContext;
import com.novv.dzdesk.ui.activity.user.UserBindTelActivity;

public class PresentUserBindTel extends XPresent<UserBindTelActivity> {

    public void bindTel(String phoneNum, String checkCode) {
        if (!hasV()) {
            return;
        }
        ServerApi.telVerify(phoneNum, checkCode)
                .compose(DefaultScheduler.<BaseResult<UserModel>>getDefaultTransformer())
                .subscribe(new BaseObserver<UserModel>() {
                    @Override
                    public void onSuccess(@NonNull UserModel userModel) {
                        if (hasV()) {
                            getV().setLoginSuccess(userModel);
                        }
                    }

                    @Override
                    public void onFailure(int code, @NonNull String message) {
                        if (hasV()) {
                            getV().setLoginFailed();
                            getV().getVDelegate().toastShort(message);
                        }
                    }

                    @Override
                    public void onLogout() {
                        super.onLogout();
                        if (hasV()){
                            getV().getVDelegate().toastShort("身份过期，请重新登录");
                            LoginContext.getInstance().logout();
                            getV().finish();
                        }
                    }
                });
    }

    public void getSms(String tel) {
        if (!hasV()) {
            return;
        }
        ServerApi.getLoginSms(tel)
                .compose(DefaultScheduler.<BaseResult>getDefaultTransformer())
                .subscribe(new SimpleObserver() {
                    @Override
                    public void onSuccess() {
                        if (hasV()) {
                            getV().setSmsGetSucess();
                        }
                    }

                    @Override
                    public void onFailure(int code, String message) {
                        if (hasV()) {
                            getV().getVDelegate().toastShort(message);
                        }
                    }
                });
    }
}
