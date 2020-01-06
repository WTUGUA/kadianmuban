package com.novv.dzdesk.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.novv.dzdesk.R;

public class LoadingDialog extends Dialog {
  public static final int STATE_NORMAL = 0;
  public static final int STATE_WARN = 1;
  public static final int STATE_SUCCESS = 2;
  private ProgressBar loadingProgress;
  private ImageView loadingWarn;
  private TextView loadingTvMessage;
  private Context mContext;
  private int state;

  public LoadingDialog(@NonNull Context context) {
    this(context, R.style.AppDialogLightWhite);
  }

  public LoadingDialog(@NonNull Context context, int themeResId) {
    super(context, themeResId);
    mContext = context;
    View view = getLayoutInflater().inflate(R.layout.loading_wait_dialog, null);
    setContentView(view);
    Window window = getWindow();
    if (window != null) {
      WindowManager m = window.getWindowManager();
      WindowManager.LayoutParams p = window.getAttributes();
      p.gravity = Gravity.CENTER;
      Point size = new Point();
      m.getDefaultDisplay().getSize(size);
      p.width = (int) (0.44 * size.x);
      window.setAttributes(p);
    }
    initView();
  }

  private void initView() {
    loadingProgress = findViewById(R.id.loading_progress);
    loadingWarn = findViewById(R.id.loading_warn);
    loadingTvMessage = findViewById(R.id.loading_tv_message);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      loadingProgress.setIndeterminateTintList(ColorStateList.valueOf(Color.parseColor("#06C0E8")));
    }
  }

  @NonNull
  public LoadingDialog setStateNormal() {
    state = STATE_NORMAL;
    loadingProgress.setVisibility(View.VISIBLE);
    loadingWarn.setVisibility(View.GONE);
    return setLoadingMessage("支付中");
  }

  @NonNull
  public LoadingDialog setStateWarn() {
    state = STATE_WARN;
    loadingProgress.setVisibility(View.GONE);
    loadingWarn.setVisibility(View.VISIBLE);
    loadingWarn.setImageResource(R.mipmap.ic_load_warn);
    return setLoadingMessage("支付失败");
  }

  @NonNull
  public LoadingDialog setStateSuccess() {
    state = STATE_SUCCESS;
    loadingProgress.setVisibility(View.GONE);
    loadingWarn.setVisibility(View.VISIBLE);
    loadingWarn.setImageResource(R.mipmap.ic_load_success);
    return setLoadingMessage("支付成功");
  }

  @NonNull
  public LoadingDialog setLoadingMessage(@NonNull String msg) {
    loadingTvMessage.setText(msg);
    return this;
  }

  public int getState() {
    return state;
  }
}
