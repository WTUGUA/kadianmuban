package com.novv.dzdesk.util;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by lijianglong on 2017/9/6.
 */

public class ImageUtils {

    /**
     * 随机生产文件名
     */
    private static String generateFileName() {
        return UUID.randomUUID().toString();
    }

    /**
     * 保存bitmap到本地
     */
    public static String saveBitmap(Bitmap mBitmap, String savePath) {
        File filePic;
        try {
            filePic = new File(savePath + File.separator + generateFileName() + ".jpg");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return filePic.getAbsolutePath();
    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        String path = "";
        //4.4以上版本的地址为content://com.android.provider.media.documents/document/imageList%3A123
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context, uri)) {
            //从地址中获取id信息---> imageList:35
            String idStr = DocumentsContract.getDocumentId(uri);
            //获取id内容  imageList:35-->35
            String id = idStr.split(":")[1];
            String[] col = {MediaStore.Images.Media.DATA};
            Cursor c = context.getContentResolver()
                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            col, MediaStore.Images.Media._ID + "=?", new String[]{id}, null);
            if (c != null && c.moveToFirst()) {
                path = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            if (c != null) {
                c.close();
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] col = {MediaStore.Images.Media.DATA};
            Cursor c = context.getContentResolver().query(uri, col, null, null, null);
            if (c != null && c.moveToFirst()) {
                path = c.getString(c.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            if (c != null) {
                c.close();
            }
        } else {
            path = uri.getPath();
        }
        return path;
    }
}
