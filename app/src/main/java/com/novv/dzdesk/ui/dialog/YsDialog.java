package com.novv.dzdesk.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.novv.dzdesk.R;


public class YsDialog extends Dialog {
    /**取消按钮*/
    private Button button_unagree;

    /**确认按钮*/
    private Button button_agree;
    /**标题文字*/
    private TextView tv;




    public YsDialog(Context context){
        super(context, R.style.mdialog);
        //通过LayoutInflater获取布局
        View view = LayoutInflater.from(getContext()).
                inflate(R.layout.dialog_yingsi, null);
        tv =  view.findViewById(R.id.yingsi);
        button_unagree = view.findViewById(R.id.unagree);
        button_agree =  view.findViewById(R.id.agree);
        SpannableString spannableString = new SpannableString("欢迎使用“节奏模板视频”!我们非常重视您的个人信息和隐私保护。在您使用“节奏模板视频”服务之前，请仔细阅读并同意《隐私政策》,《用户协议》");
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.parseColor("#FF22D7FD"));
        URLSpan urlSpan = new URLSpan("http://s.novapps.com/web_html/rhythm_model_privacy.html");
        spannableString.setSpan(urlSpan, 56, 62, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        spannableString.setSpan(colorSpan, 56, 62, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tv.setText(spannableString);
        //用户协议
        ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(Color.parseColor("#FF22D7FD"));
        URLSpan urlSpan1 = new URLSpan("https://s.novapps.com/web_html/rhythm_template_video_service_protocol.html");
        spannableString.setSpan(urlSpan1, 63, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        spannableString.setSpan(colorSpan1, 63, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        tv.setText(spannableString);
        //设置显示的视图
        setContentView(view);

    }
    /**
     * 设置显示的标题文字
     */
    public void setTv(String content) {
        tv.setText(content);
    }


    /**
     * 取消按钮监听
     * */
    public void setOnUnagreeListener(View.OnClickListener listener){
        button_unagree.setOnClickListener(listener);
    }

    /**
     * 退出按钮监听
     * */
    public void setOnAgreeListener(View.OnClickListener listener){
        button_agree.setOnClickListener(listener);
    }

}