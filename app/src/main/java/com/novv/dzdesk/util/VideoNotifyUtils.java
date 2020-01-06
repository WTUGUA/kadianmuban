package com.novv.dzdesk.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.provider.MediaStore;
import java.io.File;

/**
 * 通知系统刷新视频到相册 Created by lijianglong on 2017/8/27.
 */

public class VideoNotifyUtils {

  public static void notify(Context context, File file) {
    try {
      ContentResolver localContentResolver = context.getContentResolver();
      ContentValues localContentValues = getVideoContentValues(file, System.currentTimeMillis());
      localContentResolver
          .insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, localContentValues);
    } catch (IllegalStateException e) {
      e.printStackTrace();
    }
  }

  private static ContentValues getVideoContentValues(File paramFile, long paramLong) {
    ContentValues localContentValues = new ContentValues();
    localContentValues.put(MediaStore.MediaColumns.TITLE, paramFile.getName());
    localContentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, paramFile.getName());
    localContentValues.put(MediaStore.MediaColumns.MIME_TYPE,
        "video/" + VideoTypeUtils.getVideoType(paramFile.getPath()));
    localContentValues.put(MediaStore.MediaColumns.DATE_MODIFIED, paramLong);
    localContentValues.put(MediaStore.MediaColumns.DATE_ADDED, paramLong);
    localContentValues.put(MediaStore.MediaColumns.DATA, paramFile.getAbsolutePath());
    localContentValues.put(MediaStore.MediaColumns.SIZE, paramFile.length());
    return localContentValues;
  }
}
