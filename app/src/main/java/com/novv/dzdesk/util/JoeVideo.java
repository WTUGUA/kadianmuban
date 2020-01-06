package com.novv.dzdesk.util;

import com.novv.dzdesk.ffmpeg.EpEditor;
import com.novv.dzdesk.ffmpeg.EpVideo;

/**
 * Created by lijianglong on 2017/8/18.
 */

public class JoeVideo {

  /**
   * @param videoPath 视频路径
   * @param startTime 开始裁剪时间
   * @param cropTime 裁剪时间长度
   * @param cropWidth 裁剪宽度
   * @param cropHeight 裁剪高度
   * @param x 裁剪x坐标
   * @param y 裁剪y坐标
   * @param outPath 裁剪输出路径
   * @param listener 回调
   */
  public static void cropVideo(String videoPath
      , float startTime
      , float cropTime
      , int cropWidth
      , int cropHeight
      , int x
      , int y
      , String outPath
      , final OnEditorListener listener) {
    EpVideo epVideo = new EpVideo(videoPath);
    epVideo.clip(startTime, cropTime);
    epVideo.crop(cropWidth, cropHeight, x, y);
    EpEditor.OutputOption outputOption = new EpEditor.OutputOption(outPath);
    outputOption.setWidth(cropWidth);
    outputOption.setHeight(cropHeight);
    try {
      EpEditor.exec(epVideo, outputOption, listener);
    } catch (Exception e) {
      if (listener != null) {
        listener.onFailure();
      }
      e.printStackTrace();
    } catch (Error error) {
      if (listener != null) {
        listener.onFailure();
      }
      error.printStackTrace();
    }
  }

  /**
   * @param videoPath 视频路径
   * @param startTime 开始裁剪时间
   * @param cropTime 裁剪时间长度
   * @param outPath 裁剪输出路径
   * @param listener 回调
   */
  public static void cropVideo(String videoPath
      , float startTime
      , float cropTime
      , String outPath
      , final OnEditorListener listener) {
    EpVideo epVideo = new EpVideo(videoPath);
    epVideo.clip(startTime, cropTime);
    EpEditor.OutputOption outputOption = new EpEditor.OutputOption(outPath);
    try {
      EpEditor.exec(epVideo, outputOption, listener);
    } catch (Exception e) {
      if (listener != null) {
        listener.onFailure();
      }
      e.printStackTrace();
    } catch (Error error) {
      if (listener != null) {
        listener.onFailure();
      }
      error.printStackTrace();
    }
  }

  public interface OnEditorListener {

    void onSuccess();

    void onFailure();

    void onProgress(int p);
  }
}
