package com.novv.dzdesk.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingParent2;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

public class MyNestedScrollingParent extends LinearLayout implements NestedScrollingParent2 {

    private NestedScrollingParentHelper mParentHelper;
    private int mFirstChildHeight;

    public MyNestedScrollingParent(Context context) {
        this(context, null);
    }

    public MyNestedScrollingParent(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyNestedScrollingParent(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyNestedScrollingParent(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        this.mParentHelper = new NestedScrollingParentHelper(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        final View firstChild = this.getChildAt(0);
        if (firstChild == null)
            throw new IllegalStateException(String.format("%s must own a child view", MyNestedScrollingParent.class.getSimpleName()));
        firstChild.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                MyNestedScrollingParent.this.mFirstChildHeight = firstChild.getMeasuredHeight();
                firstChild.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        // only cares vertical scroll
        return (ViewCompat.SCROLL_AXIS_VERTICAL & axes) != 0;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        this.mParentHelper.onNestedScrollAccepted(child, target, axes, type);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        boolean isFirstChildVisible = (dy > 0 && this.getScrollY() < this.mFirstChildHeight)
                || (dy < 0 && target.getScrollY() <= 0);
        if (isFirstChildVisible) {
            //consume dy
            consumed[1] = dy;
            this.scrollBy(0, dy);
        }
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
    }


    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        this.mParentHelper.onStopNestedScroll(target, type);
    }

    @Override
    public void scrollTo(int x, int y) {
        y = y < 0 ? 0 : y;
        y = y > this.mFirstChildHeight ? this.mFirstChildHeight : y;
        super.scrollTo(x, y);
    }
}
