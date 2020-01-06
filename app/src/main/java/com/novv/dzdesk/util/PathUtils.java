package com.novv.dzdesk.util;

import android.util.Log;

import com.novv.dzdesk.VVApplication;

import java.io.File;

public final class PathUtils {

  //SDCard根目录
  private static final String ROOT_DIR = "avideoshare";
  //模板DIY部分
  private static final String DIY_DIR = "ae";
  private static final String DIY_DIR_TEMP = ".temp";
  private static final String DIY_DIR_OUTPUT = ".output";
  private static final String DIY_DIR_WATERMARK = ".watermark";
  //视频壁纸部分
  private static final String DIR_PREVIEW = "mp4preview";
  private static final String DIR_PREVIEW_MP4 = "livewallaper";
  //视频编辑部分
  private static final String EDIT_DIR = "video_edit";
  private static final String EDIT_DIR_MUSIC = "music";
  private static final String EDIT_DIR_TEMP = ".temp";

  /**
   * 获取SDCard项目根目录
   *
   * @return /avideoshare/
   */
  public static String getRootDir() {
    File file = new File(VVApplication.rootPath, ROOT_DIR);
    if (!file.exists()) {
      boolean success = file.mkdirs();
      Log.d("logger", "create rootDir " + success + ",path=" + file.getAbsolutePath());
    }
    return file.getAbsolutePath();
  }

  /**
   * 获取AE DIY 目录
   *
   * @return /avideoshare/ae/
   */
  public static String getDiyDir() {
    String root = getRootDir();
    File file = new File(root, DIY_DIR);
    if (!file.exists()) {
      boolean success = file.mkdirs();
      Log.d("logger", "create diyDir " + success + ",path=" + file.getAbsolutePath());
    }
    return file.getAbsolutePath();
  }

  /**
   * 获取AE DIY 输出目录
   *
   * @param resId 模板资源ID
   * @return /avideoshare/ae/.output/{resId}/
   */
  public static String getDiyOutputDir(String resId) {
    String diy = getDiyDir();
    File dir = new File(diy, DIY_DIR_OUTPUT);
    File file = new File(dir, resId);
    if (!file.exists()) {
      boolean success = file.mkdirs();
      Log.d("logger", "create diyOutputDir " + success + ",path=" + file.getAbsolutePath());
    }
    return file.getAbsolutePath();
  }

  /**
   * 获取水印地址
   *
   * @return /avideoshare/ae/.watermark/watermark.png
   */
  public static String getDiyWatermarkFilePath() {
    String diy = getDiyDir();
    File file = new File(diy, DIY_DIR_WATERMARK);
    if (!file.exists()) {
      boolean success = file.mkdirs();
      Log.d("logger", "create watermarkDir " + success + ",path=" + file.getAbsolutePath());
    }
    File watermark = new File(file, "watermark.png");
    return watermark.getAbsolutePath();
  }

  /**
   * 获取AE DIY 资源根目录
   *
   * @return /avideoshare/ae/.temp/
   */
  public static String getDiyResRootDir() {
    String diy = getDiyDir();
    File file = new File(diy, DIY_DIR_TEMP);
    if (!file.exists()) {
      boolean success = file.mkdirs();
      Log.d("logger", "create diyResRootDir " + success + ",path=" + file.getAbsolutePath());
    }
    return file.getAbsolutePath();
  }

  /**
   * 获取AE DIY 资源目录
   *
   * @param resId 模板资源ID
   * @return /avideoshare/ae/.temp/{resId}/
   */
  public static String getDiyResDir(String resId) {
    String dir = getDiyResRootDir();
    File file = new File(dir, resId);
    if (!file.exists()) {
      boolean success = file.mkdirs();
      Log.d("logger", "create diyResDir " + success + ",path=" + file.getAbsolutePath());
    }
    return file.getAbsolutePath();
  }

  /**
   * 获取视频壁纸资源下载目录
   * (错别字livewallaper请忽略)
   *
   * @return /avideoshare/mp4preview/livewallaper/
   */
  public static String getLwpDownloadDir() {
    String root = getRootDir();
    File mp4preview = new File(root, DIR_PREVIEW);
    File file = new File(mp4preview, DIR_PREVIEW_MP4);
    if (!file.exists()) {
      boolean success = file.mkdirs();
      Log.d("logger", "create livewallaper " + success + ",path=" + file.getAbsolutePath());
    }
    return file.getAbsolutePath();
  }

  /**
   * 获取视频编辑目录
   *
   * @return /avideoshare/video_edit/
   */
  public static String getVideoEditDir() {
    String root = getRootDir();
    File file = new File(root, EDIT_DIR);
    if (!file.exists()) {
      boolean success = file.mkdirs();
      Log.d("logger", "create videoEditDir " + success + ",path=" + file.getAbsolutePath());
    }
    return file.getAbsolutePath();
  }

  /**
   * 获取视频编辑的背景音乐目录
   *
   * @return /avideoshare/video_edit/music/
   */
  public static String getVideoEditMusicDir() {
    String videoEdit = getVideoEditDir();
    File file = new File(videoEdit, EDIT_DIR_MUSIC);
    if (!file.exists()) {
      boolean success = file.mkdirs();
      Log.d("logger", "create videoEditMusicDir " + success + ",path=" + file.getAbsolutePath());
    }
    return file.getAbsolutePath();
  }

  /**
   * 获取视频编辑的缓存目录
   *
   * @return /avideoshare/video_edit/.temp/
   */
  public static String getVideoEditTempDir() {
    String videoEdit = getVideoEditDir();
    File file = new File(videoEdit, EDIT_DIR_TEMP);
    if (!file.exists()) {
      boolean success = file.mkdirs();
      Log.d("logger", "create videoEditTempDir " + success + ",path=" + file.getAbsolutePath());
    }
    return file.getAbsolutePath();
  }

  /**
   * 获取视频编辑的封面路径
   *
   * @return /avideoshare/video_edit/cover.jpg
   */
  public static String getVideoEditCoverFilePath() {
    String tempDir = getVideoEditTempDir();
    File file = new File(tempDir, "cover.jpg");
    return file.getAbsolutePath();
  }
}
