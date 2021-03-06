package com.novv.dzdesk.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.TintTypedArray;
import android.util.AttributeSet;
import android.view.View;
import com.novv.dzdesk.R;

public class MyTabItem extends View {

    final CharSequence mText;
    final Drawable mIcon;
    final int mCustomLayout;

    public MyTabItem(Context context) {
        this(context, null);
    }

    @SuppressLint("RestrictedApi")
    public MyTabItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        final TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs,
                R.styleable.MyTabItem);
        mText = a.getText(R.styleable.MyTabItem_myText);
        mIcon = a.getDrawable(R.styleable.MyTabItem_myIcon);
        mCustomLayout = a.getResourceId(R.styleable.MyTabItem_myLayout, 0);
        a.recycle();
    }
}
