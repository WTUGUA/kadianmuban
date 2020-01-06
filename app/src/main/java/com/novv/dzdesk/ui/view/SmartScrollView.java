package com.novv.dzdesk.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class SmartScrollView extends ScrollView {

    private boolean isScrolledToTop = true;
    private boolean isScrolledToBottom = false;
    private ISmartScrollChangedListener mSmartScrollChangedListener;
    private boolean disallow;

    public SmartScrollView(Context context) {
        super(context);
    }

    public SmartScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean root = super.dispatchTouchEvent(ev);
        if (disallow) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return root;
    }

    public void setDisallow(boolean disallow) {
        this.disallow = disallow;
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
        if (scrollY == 0) {
            isScrolledToTop = clampedY;
            isScrolledToBottom = false;
        } else {
            isScrolledToTop = false;
            isScrolledToBottom = clampedY;
        }
        notifyScrollChangedListeners();
    }

    public void setScanScrollChangedListener(
            ISmartScrollChangedListener smartScrollChangedListener) {
        mSmartScrollChangedListener = smartScrollChangedListener;
    }

    private void notifyScrollChangedListeners() {
        if (isScrolledToTop) {
            if (mSmartScrollChangedListener != null) {
                mSmartScrollChangedListener.onScrolledToTop();
            }
        } else if (isScrolledToBottom) {
            if (mSmartScrollChangedListener != null) {
                mSmartScrollChangedListener.onScrolledToBottom();
            }
        }
    }

    public boolean isScrolledToTop() {
        return isScrolledToTop;
    }

    public boolean isScrolledToBottom() {
        return isScrolledToBottom;
    }

    /**
     * 定义监听接口
     */
    public interface ISmartScrollChangedListener {

        void onScrolledToBottom();

        void onScrolledToTop();
    }
}
