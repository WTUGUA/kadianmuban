package com.lansosdk.videoeditor;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.FrameLayout;

import com.lansosdk.LanSongAe.LSOAeDrawable;
import com.lansosdk.box.LSOAeTextRunnable;
import com.lansosdk.box.LSLog;
import com.lansosdk.box.LSOOneLineText;
import com.lansosdk.box.onDrawPadCompletedListener;
import com.lansosdk.box.onDrawPadErrorListener;
import com.lansosdk.box.onDrawPadProgressListener;
import com.lansosdk.box.onDrawPadSizeChangedListener;

import java.util.List;


public class LSOAETextPreview extends FrameLayout {

    private static final String TAG = "LanSongSDK";
    private TextureRenderView mTextureRenderView;
    private SurfaceTexture mSurfaceTexture = null;
    private int drawPadWidth, drawPadHeight;
    private onViewAvailable mViewAvailable = null;
    private boolean isLayoutOk=false;
    // ----------------------------------------------
    public LSOAETextPreview(Context context) {
        super(context);
        initVideoView(context);
    }

    public LSOAETextPreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        initVideoView(context);
    }

    public LSOAETextPreview(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVideoView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LSOAETextPreview(Context context, AttributeSet attrs, int defStyleAttr,
                            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initVideoView(context);
    }

    private void initVideoView(Context context) {
        setTextureView();
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();

        renderer =new LSOAeTextRunnable(getContext());
    }

    private void setTextureView() {
        mTextureRenderView = new TextureRenderView(getContext());
        mTextureRenderView.setSurfaceTextureListener(new SurfaceCallback());

        mTextureRenderView.setDispalyRatio(0);

        View renderUIView = mTextureRenderView.getView();
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, Gravity.CENTER);
        renderUIView.setLayoutParams(lp);
        addView(renderUIView);
        mTextureRenderView.setVideoRotation(0);
    }
    /**
     *当前View有效的时候, 回调监听;
     */
    public void setOnViewAvailable(onViewAvailable listener) {
        mViewAvailable = listener;
        if (mSurfaceTexture != null) {
            if(drawPadHeight>0 && drawPadWidth>0 && desireWidth>0 && desireHeight>0){

                float acpect = (float) desireWidth / (float) desireHeight;
                float padAcpect = (float) drawPadWidth / (float) drawPadHeight;

                if (acpect == padAcpect) { // 如果比例已经相等,不需要再调整,则直接显示.
                    isLayoutOk=true;
                    mViewAvailable.viewAvailable(this);
                } else if (Math.abs(acpect - padAcpect) * 1000 < 16.0f) {
                    isLayoutOk=true;
                    mViewAvailable.viewAvailable(this);
                }else{
                    mTextureRenderView.setVideoSize(desireWidth, desireHeight);
                    mTextureRenderView.setVideoSampleAspectRatio(1, 1);
                    Log.e(TAG, "layout again...");
                    requestLayout();
                }
            }
        }
    }
    public Surface getSurface(){
        if(mSurfaceTexture!=null){
            return  new Surface(mSurfaceTexture);
        }
        return null;
    }
    public interface onViewAvailable {
        void viewAvailable(LSOAETextPreview v);
    }

    private class SurfaceCallback implements TextureView.SurfaceTextureListener {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surface,
                                              int width, int height) {
            mSurfaceTexture = surface;
            drawPadHeight = height;
            drawPadWidth = width;
            Log.d(TAG, "onSurfaceTextureAvailable--------:wh "+ drawPadWidth+ drawPadHeight);
            checkLayoutSize();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surface,
                                                int width, int height) {
            mSurfaceTexture = surface;
            drawPadHeight = height;
            drawPadWidth = width;
            Log.d(TAG,  "onSurfaceTextureSizeChanged--------: ");
            checkLayoutSize();
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
            mSurfaceTexture = null;
            isLayoutOk=false;
            Log.d(TAG,  "onSurfaceTextureDestroyed--------: ");
            return false;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        }
    }
    boolean isDrawPadSizeChanged=false;

    private int desireWidth;
    private int desireHeight;

    private onDrawPadSizeChangedListener mSizeChangedCB = null;
    public void setDrawPadSize(int width, int height, onDrawPadSizeChangedListener cb) {

        isDrawPadSizeChanged=true;
        if (width != 0 && height != 0) {
            if(drawPadWidth==0 || drawPadHeight==0){  //直接重新布局UI
                mTextureRenderView.setVideoSize(width, height);
                desireWidth=width;
                desireHeight=height;
                mTextureRenderView.setVideoSampleAspectRatio(1, 1);
                mSizeChangedCB = cb;
                requestLayout();
            }else{
                float setAcpect = (float) width / (float) height;
                float setViewacpect = (float) drawPadWidth / (float) drawPadHeight;

                if (setAcpect == setViewacpect) { // 如果比例已经相等,不需要再调整,则直接显示.
                    if (cb != null) {
                        isLayoutOk=true;
                        cb.onSizeChanged(width, height);
                    }
                } else if (Math.abs(setAcpect - setViewacpect) * 1000 < 16.0f) {
                    if (cb != null) {
                        cb.onSizeChanged(width, height);
                    }
                } else if (mTextureRenderView != null) {
                    mTextureRenderView.setVideoSize(width, height);
                    mTextureRenderView.setVideoSampleAspectRatio(1, 1);
                    mSizeChangedCB = cb;
                }
                requestLayout();
            }
        }
    }

    /**
     * 检查得到的大小, 如果和view成比例,则直接回调; 如果不成比例,则重新布局;
     */
    private void checkLayoutSize(){
//        if(mSizeChangedCB!=null){
        float setAcpect = (float) desireWidth / (float) desireHeight;
        float setViewacpect = (float) drawPadWidth / (float) drawPadHeight;

        if (setAcpect == setViewacpect) { // 如果比例已经相等,不需要再调整,则直接显示.
            isLayoutOk=true;
            if (mSizeChangedCB != null) {
                mSizeChangedCB.onSizeChanged(drawPadWidth, drawPadHeight);
                mSizeChangedCB=null;
            }else if(mViewAvailable!=null){
                mViewAvailable.viewAvailable(this);
            }
        } else if (Math.abs(setAcpect - setViewacpect) * 1000 < 16.0f) {
            isLayoutOk=true;
            if (mSizeChangedCB != null) {
                mSizeChangedCB.onSizeChanged(drawPadWidth, drawPadHeight);
                mSizeChangedCB=null;
            }else if(mViewAvailable!=null){
                mViewAvailable.viewAvailable(this);
            }
        }else{
            mTextureRenderView.setVideoSize(desireWidth, desireHeight);
            mTextureRenderView.setVideoSampleAspectRatio(1, 1);
            Log.d(TAG,"layout again...");
            requestLayout();
        }
//        }
    }
   //-------------------------------增加Ae的代码....
    public LSOAeTextRunnable renderer;
//    public void addVideoLayer(String videoPath) throws IOException {
//        if(renderer ==null){
//            renderer =new LSOAeTextRunnable(getContext(),videoPath);
//        }
//    }
//    /**
//     * 增加图片图层,
//     * 在start前调用
//     * @param bmp
//     * @return
//     */
//    public BitmapLayer addBitmapLayer(Bitmap bmp){
//
//        createRender();
//        if(renderer !=null && bmp!=null){
//            return  renderer.addBitmapLayer(bmp);
//        }else {
//            Log.e(TAG,"增加图片图层失败...");
//            return  null;
//        }
//    }

//    /**
//     * 增加Ae json图层;
//     * 在start前调用
//     * @param drawable
//     * @return
//     */
    public void setDrawable(LSOAeDrawable drawable, List<LSOOneLineText> lineTexts){
        if(renderer !=null) {
            renderer.setDrawable(drawable,lineTexts);
        }
    }

    /**
     * 获取当前正在播放的第几帧;
     * @return
     */
    public int getCurrentFrame(){
        if(renderer!=null){
            return renderer.getCurrentFrame();
        }else{
            LSLog.e("DrawPadAePreview#getCurrentFrame error.render is null");
            return 0;
        }
    }

//    /**
//     * 增加mv图层
//     * 在start前调用
//     * @param colorPath
//     * @param maskPath
//     */
//    public void addMVLayer(String colorPath, String maskPath){
//
//        createRender();
//        if(renderer !=null){
//            renderer.addMVLayer(colorPath,maskPath);
//        }
//    }
    /**
     * 返回Ae的总时长;
     * 单位us
     * @return
     */
    public long getDuration(){
        if(renderer !=null){
            return renderer.getDurationUS();
        }else {
            Log.e(TAG,"get duration error, renderer==null.here return 1000");
            return  1000;
        }
    }

    /**
     * 进度
     * @param listener
     */
    public void setDrawPadProgressListener(onDrawPadProgressListener listener) {
        if (renderer != null) {
            renderer.setDrawPadProgressListener(listener);
        }else{
            LSLog.e(" render is nulll. setDrawPadProgressListener error ");
        }
    }

    /**
     * 完成监听
     * @param listener
     */
    public void setDrawPadCompletedListener(onDrawPadCompletedListener listener) {
        if (renderer != null) {
            renderer.setDrawPadCompletedListener(listener);
        }
    }
    public void setDrawPadErrorListener(onDrawPadErrorListener listener) {
        if (renderer != null) {
            renderer.setDrawPadErrorListener(listener);
        }
    }
    public void setDrawPadSize(int w,int h){
        if(renderer!=null){
            renderer.updateDrawPadSize(w,h);
        }
    }
    public boolean  isValid(){
        return isLayoutOk;
    }
    /**
     * 开始运行
     * @return
     */
    public  boolean start(){

        if(renderer!=null && mSurfaceTexture!=null && renderer.isRunning()==false){

            LSLog.d("drawpad  wxh:"+drawPadWidth+drawPadHeight);

            renderer.updateDrawPadSize(drawPadWidth,drawPadHeight);
            renderer.setSurface(new Surface(mSurfaceTexture));
            return renderer.startDrawPad();
        }else{
            if(renderer!=null && !renderer.isRunning()){
                LSLog.e("开启AePreview 失败.mSurfaceTexture 无效 :");
            }
            return  false;
        }
    }
    public void export(String dstPath)
    {
        if(renderer!=null){
            renderer.startExport(dstPath);
        }
    }
    public boolean isExportMode(){
        if(renderer!=null){
            return renderer.isExportMode();
        }
        return false;
    }
    public void addAudioPath(String audioPath){
        if(renderer!=null){
            renderer.addAudioPath(audioPath);
        }
    }

    //LSTODO
    public void addAudioPath(String audio,float volume,String bgMusic,float volume2){
        if(renderer!=null){
            renderer.addAudioPath(audio,volume,bgMusic,volume2);
        }
    }

    /**
     * 停止,
     * 如果已经收到完成监听, 则不需要调用;
     */
    public void stop(){
        if(renderer!=null){
            renderer.stopDrawPad();
            renderer=null;
        }
    }

    /**
     * 取消
     * 如果已经收到完成监听, 则不需要调用;
     */
    public void cancel(){
        if(renderer!=null){
            renderer.cancelDrawPad();
            renderer=null;
        }
    }

    /**
     * 释放
     * 如果已经收到完成监听, 则不需要调用;
     */
    public void release(){
        if(renderer!=null){
            if( renderer.isRunning()){
                renderer.cancelDrawPad();
            }else{
                renderer.releaseDrawPad();
            }
        }
    }
}
