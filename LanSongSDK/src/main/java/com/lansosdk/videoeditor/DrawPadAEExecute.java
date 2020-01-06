package com.lansosdk.videoeditor;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.lansosdk.LanSongAe.LSOAeDrawable;
import com.lansosdk.box.AEJsonLayer;
import com.lansosdk.box.AudioLayer;
import com.lansosdk.box.BitmapLayer;
import com.lansosdk.box.DrawPadAERunnable;
import com.lansosdk.box.LSLog;
import com.lansosdk.box.onAudioPadThreadProgressListener;
import com.lansosdk.box.onDrawPadCompletedListener;
import com.lansosdk.box.onDrawPadErrorListener;
import com.lansosdk.box.onDrawPadProgressListener;


public class DrawPadAEExecute {
    private static final String TAG= LSLog.TAG;

    public DrawPadAERunnable renderer;

    /**
     * 构造方法, 输入一个视频, 放到最底层,
     * 默认容器的宽高 等于视频的宽高;
     *
     * @param ctx
     * @param inputVideo  有背景视频的视频完整路径
     * @param output  处理完毕后的视频目标路径
     */
    public DrawPadAEExecute(Context ctx,String inputVideo,String output){
        if(renderer ==null){
            renderer =new DrawPadAERunnable(ctx,inputVideo,output);
        }
    }
    /**
     * 在没有调用addBitmapLayer的场合中, 可以用这个;
     * @param ctx
     * @param output 处理完毕后的目标视频保存到的路径
     */
    public DrawPadAEExecute(Context ctx,String output){
        if(renderer ==null){
            renderer =new DrawPadAERunnable(ctx,output);
        }
    }
    /**
     * 设置输出视频的码率, 可以不设置
     * @param bitrate
     */
    public void setEncodeBitrate(int bitrate){
        renderer.setEncodeBitrate(bitrate);
    }


    /**
     * 当内部没有视频的时候, 设置输出视频的帧率;
     * @param rate
     */
    public void setFrateRate(int rate){
        renderer.setFrateRate(rate);
    }

    /**
     * 获取Ae模板的声音,在AudioPad中的对象;
     * 在 addSubAudio后, 调用
     * 如果没有增加其他声音, 则返回null;
     * @return
     */
    public AudioLayer getAEAudioLayer(){
        if (renderer != null) {
            return renderer.getMainAudioLayer();
        }else{
            return null;
        }
    }
    /**
     * 增加音频,
     *
     * 在AE线程开始前 + 所有图层增加后 调用;
     *
     * 音频采样率必须和视频的声音采样率一致
     * @param srcPath
     * @return
     */
    public AudioLayer addSubAudio(String srcPath) {
        if (renderer != null && !renderer.isRunning()) {
            return renderer.addSubAudio(srcPath);
        } else {
            return null;
        }
    }
    public AudioLayer addSubAudio(String srcPath, boolean loop) {
        if (renderer != null && !renderer.isRunning()) {
            AudioLayer source= renderer.addSubAudio(srcPath);
            if(source!=null && loop){
                source.setLooping(loop);
            }
            return source;
        } else {
            return null;
        }
    }

    /**
     * 增加其他声音;
     *
     * 在AE线程开始前 + 所有图层增加后 调用;
     *
     * 音频采样率仅支持44100或48000;
     * 音频采样率必须和视频的声音采样率一致
     * @param srcPath
     * @param startFromPadTime 从Ae模板的什么时间开始增加
     * @return  返回增加后音频层, 可以用来设置音量,快慢,变声等.
     */
    public AudioLayer addSubAudio(String srcPath, long startFromPadTime) {
        if (renderer != null && !renderer.isRunning()) {
            return renderer.addSubAudio(srcPath, startFromPadTime, -1);
        } else {
            return null;
        }
    }

    /**
     * 增加其他声音;
     *
     *在AE线程开始前 + 所有图层增加后 调用;
     *
     * 音频采样率仅支持44100或48000;
     * 音频采样率必须和视频的声音采样率一致
     *
     * @param srcPath        路径, 可以是mp3或m4a或 带有音频的MP4文件;
     * @param startFromPadUs 从主音频的什么时间开始增加
     * @param durationUs     把这段声音多长插入进去.
     * @return 返回一个AudioLayer对象;
     */
    public AudioLayer addSubAudio(String srcPath, long startFromPadUs,
                                  long durationUs) {
        if (renderer != null && !renderer.isRunning()) {
            return renderer.addSubAudio(srcPath, startFromPadUs, durationUs);
        } else {
            return null;
        }
    }

    /**
     * 如果要调节音量, 则增加拿到对象后, 开始调节.
     *
     * 在AE线程开始前 + 所有图层增加后 调用;
     *
     * 音频采样率仅支持44100或48000;
     * @param srcPath
     * @param startFromPadUs   从容器的什么位置开始增加
     * @param startAudioTimeUs 把当前声音的开始时间增加进去.
     * @param durationUs       增加多少, 时长.
     * @return
     */
    public AudioLayer addSubAudio(String srcPath, long startFromPadUs,
                                  long startAudioTimeUs, long durationUs) {
        if (renderer != null && !renderer.isRunning()) {
            return renderer.addSubAudio(srcPath, startFromPadUs,
                    startAudioTimeUs, durationUs);
        } else {
            return null;
        }
    }

    /**
     * 是否在开始运行DrawPad的时候,检查您设置的码率和分辨率是否正常.
     * <p>
     * 默认是检查, 如果您清楚码率大小的设置,请调用此方法,不再检查.
     */
    public void setNotCheckBitRate() {
        if (renderer != null && !renderer.isRunning()) {
            renderer.setNotCheckBitRate();
        }
    }

    /**
     * 设置音频回调;
     * @param li
     */
    public void setAudioProgressListener(onAudioPadThreadProgressListener li) {
        if (renderer != null) {
            renderer.setAudioProgressListener(li);
        }
    }
    /**
     * 增加图片图层,
     * 在start前调用
     * @param bmp
     * @return
     */
    public BitmapLayer addBitmapLayer(Bitmap bmp){
        if(renderer !=null && bmp!=null){
            return  renderer.addBitmapLayer(bmp);
        }else {
            Log.e(TAG,"增加图片图层失败...");
            return  null;
        }
    }
    public AEJsonLayer addAeLayer(LSOAeDrawable drawable){
        if(renderer !=null) {
            return renderer.addAeLayer(drawable);
        }else{
            return null;
        }
    }
    /**
     * 增加Ae json图层;
     * 在start前调用
     *
     * [特定客户使用,不建议使用]
     * @param drawable
     * @param isDisplayLastFrame  当走到最后一帧后, 是否要一直显示最后一帧;默认显示;如不显示,这里false;
     * @param offsetTimeUs Ae模板是否相对视频偏移多少, 单位微秒, 默认不偏移.
     * @return
     */
    public AEJsonLayer addAeLayer(LSOAeDrawable drawable,boolean isDisplayLastFrame,long offsetTimeUs){
        if(renderer !=null) {
            AEJsonLayer ret=renderer.addAeLayer(drawable);
            ret.setOnLastFrame(isDisplayLastFrame);
            ret.setOffsetTimeUs(offsetTimeUs);
            return ret;
        }else{
            return null;
        }
    }
    /**
     * 增加mv图层
     * 在start前调用
     * @param colorPath
     * @param maskPath
     * @return
     */
    public void addMVLayer(String colorPath, String maskPath){
        if(renderer !=null){
            renderer.addMVLayer(colorPath,maskPath);
        }
    }

    /**
     * 返回处理的时长
     * 单位us
     * @return
     */
    public long getDuration(){
        if(renderer !=null){
            return renderer.getDuration();
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
    /**
     * 开始运行
     * @return
     */
    public  boolean start(){
        if(renderer.isRunning()==false){
            return renderer.startDrawPad();
        }else{
            return  false;
        }
    }

    /**
     * 停止,
     * 如果已经收到完成监听, 则不需要调用;
     */
    public void stop(){
        if(renderer.isRunning()){
            renderer.stopDrawPad();
        }
    }

    /**
     * 取消
     * 如果已经收到完成监听, 则不需要调用;
     */
    public void cancel(){
        if(renderer.isRunning()){
            renderer.cancelDrawPad();
        }
    }

    /**
     * 释放
     * 如果已经收到完成监听, 则不需要调用;
     */
    public void release(){
        if(renderer.isRunning()){
            renderer.cancelDrawPad();
        }else{
            renderer.releaseDrawPad();
        }
    }
}
