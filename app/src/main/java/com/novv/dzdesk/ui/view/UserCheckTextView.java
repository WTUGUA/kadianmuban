package com.novv.dzdesk.ui.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class UserCheckTextView extends ClickableSpan {

    private Context context;

    public UserCheckTextView(Context context) {
        this.context = context;
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        super.updateDrawState(ds);
        ds.setColor(Color.parseColor("#00A9CE"));
        ds.setUnderlineText(false);
    }

    @Override
    public void onClick(@NonNull View widget) {
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://s.novapps.com/web_html/videowallpaper_pay.html?t=1"));
        context.startActivity(intent);
    }
}
