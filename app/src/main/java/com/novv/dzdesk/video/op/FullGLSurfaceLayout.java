package com.novv.dzdesk.video.op;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * Created by lingyfh on 2018/6/8.
 */

public class FullGLSurfaceLayout extends GLSurfaceView {

    private String TAG = FullGLSurfaceLayout.class.getSimpleName();

    private int mVideoWidth;
    private int mVideoHeight;

    public FullGLSurfaceLayout(Context context) {
        super(context);
    }

    public FullGLSurfaceLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public void layout(int l, int t, int layoutWidth, int layoutHeight) {
        // 视频比例
        float scale = (float) mVideoWidth / (float) mVideoHeight;

        float fullHeight = layoutWidth / scale;
        if (fullHeight > layoutHeight) {
            layoutHeight = (int) fullHeight;
        }
        super.layout(l, t, layoutWidth, layoutHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l - dip2px(getContext(), 20), t, r, b);
    }

    public void reLayout(int videoWidth, int videoHeight) {
        mVideoWidth = videoWidth;
        mVideoHeight = videoHeight;
        requestLayout();
    }

    /**
     * dp转px
     *
     * @return px
     */
    private int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
}
