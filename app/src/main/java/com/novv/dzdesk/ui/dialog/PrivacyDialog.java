package com.novv.dzdesk.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.novv.dzdesk.Const;
import com.novv.dzdesk.R;
import com.novv.dzdesk.ui.activity.WebUrlActivity;


public class PrivacyDialog extends Dialog implements DialogInterface.OnKeyListener,View.OnClickListener {

    public PrivacyDialog(Context context) {
        super(context, R.style.ActionSheetDialogStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_privacy);
        setCanceledOnTouchOutside(false);
        setOnKeyListener(this);
        initPrivacy();
    }

    private void initPrivacy() {
        TextView tvPrivacy = findViewById(R.id.tv_privacy);
        findViewById(R.id.tv_agree).setOnClickListener(this);
        findViewById(R.id.tv_disagree).setOnClickListener(this);
        SpannableStringBuilder spannableSb = new SpannableStringBuilder();
        String front = "欢迎使用\"彩蛋视频壁纸\"！我们非常重视您的个人信息和隐私保护。在您用\"彩蛋视频壁纸\"服务之前，请仔细阅读并同意";
        String privacy = "《彩蛋视频壁纸隐私政策》";
        String back =  "，我们将严格按照经您同意的各项条款使用您的个人信息，以便为您提供更好的服务。";
        spannableSb.append(front).append(privacy).append(back);
        ForegroundColorSpan spanColor = new ForegroundColorSpan(getContext().getResources().getColor(R.color.general_blue));
        ClickableSpan spanClickable = new ClickableSpan(){
            @Override
            public void onClick(@NonNull View widget) {
                WebUrlActivity.launch(getContext(), Const.Policy.PRIVACY_POLICY, "隐私协议");
            }


            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(getContext().getResources().getColor(R.color.general_blue));
                ds.setUnderlineText(false);
            }
        };
        spannableSb.setSpan(spanColor,front.length(),front.length() + privacy.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableSb.setSpan(spanClickable,front.length(),front.length() + privacy.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvPrivacy.setMovementMethod(LinkMovementMethod.getInstance());
        tvPrivacy.setText(spannableSb);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_disagree:
                dismiss();
                if (mListener != null){
                    mListener.onDisagree();
                }
                break;
            case R.id.tv_agree:
                dismiss();
                if (mListener != null){
                    mListener.onAgree();
                }
                break;
        }
    }


    @Override
    public void show() {
        super.show();
    }

    @Override
    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (mListener != null){
                mListener.onDisagree();
            }
        }
        return false;
    }

    private OnButtonClickListener mListener;

    public PrivacyDialog setOnButtonClickListener(OnButtonClickListener listener) {
        mListener = listener;
        return this;
    }

    public interface OnButtonClickListener {
        void onAgree();
        void onDisagree();
    }

}
