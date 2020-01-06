package com.novv.dzdesk.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import com.novv.dzdesk.R;

public class VipPayDialog extends Dialog implements View.OnClickListener {

    private Context mContext;
    private DialogCallback mDialogCallback;
    private View aliPay, wxPay;
    private int payId;

    public VipPayDialog(@NonNull Context context) {
        this(context, R.style.AppDialogLight);
    }

    public VipPayDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
    }

    protected VipPayDialog(@NonNull Context context, boolean cancelable,
            @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = getLayoutInflater().inflate(R.layout.dialog_vip_pay, null);
        setContentView(view);
        Window window = getWindow();
        if (window != null) {
            WindowManager m = window.getWindowManager();
            WindowManager.LayoutParams p = window.getAttributes();
            p.gravity = Gravity.BOTTOM;
            Point size = new Point();
            m.getDefaultDisplay().getSize(size);
            p.width = size.x;
            window.setAttributes(p);
        }
        initView();
    }

    private void initView() {
        View btnBack = findViewById(R.id.btn_back);
        View btnAction = findViewById(R.id.btn_action);
        aliPay = findViewById(R.id.iv_alipay);
        wxPay = findViewById(R.id.iv_wxpay);
        View llWxPay = findViewById(R.id.ll_wxpay);
        View llAliPay = findViewById(R.id.ll_alipay);

        llWxPay.setOnClickListener(this);
        llAliPay.setOnClickListener(this);
        btnAction.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        aliPay.setOnClickListener(this);
        wxPay.setOnClickListener(this);
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(Color.parseColor("#00bbe3"));
        drawable.setCornerRadius(dip2px(mContext, 6f));
        btnAction.setBackground(drawable);
    }

    private int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public void showPayDialog(DialogCallback dialogCallback) {
        if (!isShowing()) {
            show();
        }
        aliPay.setVisibility(View.GONE);
        wxPay.setVisibility(View.VISIBLE);
        mDialogCallback = dialogCallback;
        payId = 0;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_back) {
            dismiss();
        } else if (v.getId() == R.id.ll_alipay) {
            payId = 1;
            wxPay.setVisibility(View.GONE);
            aliPay.setVisibility(View.VISIBLE);
        } else if (v.getId() == R.id.ll_wxpay) {
            payId = 0;
            aliPay.setVisibility(View.GONE);
            wxPay.setVisibility(View.VISIBLE);
        } else if (v.getId() == R.id.btn_action) {
            dismiss();
            if (mDialogCallback != null) {
                mDialogCallback.onPay(payId);
            }
        }
    }

    public interface DialogCallback {

        void onPay(int payId);
    }
}
