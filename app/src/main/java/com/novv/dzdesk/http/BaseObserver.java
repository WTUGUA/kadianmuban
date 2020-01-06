package com.novv.dzdesk.http;

import android.content.Context;
import android.support.annotation.NonNull;
import com.novv.dzdesk.res.model.BaseResult;
import com.novv.dzdesk.res.user.LoginContext;
import com.novv.dzdesk.ui.activity.user.UserLoginActivity;
import io.reactivex.observers.DisposableObserver;

/**
 * 数据返回封装体,可以对请求过程做一些统一处理 Created by lijianglong on 2017/8/2.
 */

public abstract class BaseObserver<T> extends DisposableObserver<BaseResult<T>> {

  private static final int RESPONSE_CODE_OK = 0;       //成功返回积极数据
  private static final int RESPONSE_CODE_FAILED = -1;  //返回数据失败
  private static final int RESPONSE_CODE_LOGOUT = -9;  //未登陆
  private boolean toLogin;
  private Context context;

  public BaseObserver() {
    toLogin = false;
  }

  /**
   * 构造方法
   *
   * @param context Activity上下文
   * @param loginIfInvalid 如果登陆失效跳转登陆页面
   */
  public BaseObserver(Context context, boolean loginIfInvalid) {
    this.context = context;
    this.toLogin = loginIfInvalid;
  }

  public abstract void onSuccess(@NonNull T t);

  public abstract void onFailure(int code, @NonNull String message);

  @Override
  public final void onNext(BaseResult<T> result) {
    if (result == null) {
      return;
    }
    switch (result.getCode()) {
      case RESPONSE_CODE_OK:
        T t = result.getRes();
        if (t != null) {
          onSuccess(t);
        } else {
//          onFailure(RESPONSE_CODE_FAILED, "数据解析异常");
          onSuccess(t);
        }
        break;
      case RESPONSE_CODE_LOGOUT:
        onLogout();
        break;
      case RESPONSE_CODE_FAILED:
      default:
        onFailure(result.getCode(), result.getMsg());
        break;
    }
  }

  @Override
  public final void onError(Throwable t) {
    onFailure(RESPONSE_CODE_FAILED, t == null ? "请求失败" : t.getMessage());
  }

  public void onLogout() {
    LoginContext.getInstance().logout();
    if (toLogin && context != null) {
      UserLoginActivity.launch(context);
    }
  }

  /**
   * 用的比较少,自行实现
   */
  @Override
  public void onComplete() {

  }
}
