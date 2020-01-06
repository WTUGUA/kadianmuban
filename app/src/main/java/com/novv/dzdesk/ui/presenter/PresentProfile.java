package com.novv.dzdesk.ui.presenter;

import android.support.annotation.NonNull;
import com.ark.baseui.XPresent;
import com.novv.dzdesk.http.BaseObserver;
import com.novv.dzdesk.http.DefaultScheduler;
import com.novv.dzdesk.http.ServerApi;
import com.novv.dzdesk.res.model.BaseResult;
import com.novv.dzdesk.res.model.UserModel;
import com.novv.dzdesk.res.user.LoginContext;
import com.novv.dzdesk.ui.fragment.nav.NavPersonFragment;

public class PresentProfile extends XPresent<NavPersonFragment> {


    public void loadProfile() {
        if (!hasV()) {
            return;
        }
        ServerApi
                .getUserInfo()
                .compose(
                        DefaultScheduler
                                .<BaseResult<UserModel>>getDefaultTransformer())
                .subscribe(new BaseObserver<UserModel>() {
                    @Override
                    public void onSuccess(@NonNull UserModel userModel) {
                        if (hasV()) {
                            LoginContext.getInstance().login(userModel);
                            getV().setUserLogin();
                        }
                    }

                    @Override
                    public void onFailure(int code, @NonNull String message) {

                    }

                    @Override
                    public void onLogout() {
                        super.onLogout();
                        if (hasV()) {
                            getV().setUserLogout();
                        }
                    }
                });
    }
}
