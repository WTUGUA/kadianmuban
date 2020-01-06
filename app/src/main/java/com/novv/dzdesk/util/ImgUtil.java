package com.novv.dzdesk.util;

import android.content.Context;
import android.graphics.*;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;

import java.io.*;

public class ImgUtil {

    /**
     * 1. 获取资源图片信息  {@link #getBitmapInfo(Context context, int resId)} 2. 获取图片文件信息  {@link
     * #getBitmapInfo(String filePath)} 3. 读取资源图片  {@link #loadBitmapFromRes(Context context, int
     * resId)} 4. 读取资源图片  {@link #loadBitmapFromRes(Context context, int resId, Config config)} 5.
     * 读取资源图片  {@link #loadBitmapFromRes(Context context, int resId, int sampleSize)} 6. 读取资源图片
     * {@link #loadBitmapFromRes(Context context, int resId, Config config, int sampleSize)} 7.
     * 读取资源图片 {@link #loadBitmapFromRes(Context context, int resId, Options options)} 8.
     * 从assets中读取图片文件 {@link #loadBitmapFromAssets(Context context, String assetPath)} 9.
     * 从assets中读取图片文件,含编码格式 {@link #loadBitmapFromAssets(Context context, String assetPath, Config
     * config)} 10.从assets中读取图片文件,含采样比率  {@link #loadBitmapFromAssets(Context context, String
     * assetPath, int sampleSize)} 11.从assets中读取图片文件,含编码格式，采样比率  {@link
     * #loadBitmapFromAssets(Context context, String assetPath, Config config, int sampleSize)}
     * 12.从assets中读取图片文件,含位图解码选项  {@link #loadBitmapFromAssets(Context context, String assetPath,
     * Options options)} 13.从磁盘读取图片文件  {@link #loadBitmapFromFile(String filePath, Config config)}
     * 14.从磁盘读取图片文件  {@link #loadBitmapFromFile(String filePath, int sampleSize)} 15.从磁盘读取图片文件
     * {@link #loadBitmapFromFile(String filePath, Config config, int sampleSize)} 16.从磁盘读取图片文件
     * {@link #loadBitmapFromFile(String filePath, Options options)} 17.图片保存  {@link
     * #saveBitmap(Bitmap src, String path, CompressFormat format, int quality)} 18.图片保存  {@link
     * #saveBitmap(Bitmap src, File file, CompressFormat format, int quality) } 19.从大图获取缩略图  {@link
     * #getThumbnailFromFile(String path, int width, int height, boolean crop)} 20.从Drawable对象获取位图
     * {@link #getBitmapFromDrawable(Drawable drawable)} 21.从Bitmap获取像素字节数组  {@link
     * #getBytesFromBitmap(Bitmap src, boolean recycleSrc)} 22.图片变换  {@link
     * #getTransformedBitmap(Bitmap src, Matrix matrix, boolean recycleSrc)} 23.图片旋转  {@link
     * #getRotatedBitamp(Bitmap src, float degree, boolean recycleSrc)} 24.图片旋转  {@link
     * #getRotatedBitamp(Bitmap src, float degree, float px, float py, boolean recycleSrc)} 25.图片放缩
     * {@link #getScaledBitmap(Bitmap src, float sx, float sy, boolean recycleSrc)} 26.图片放缩  {@link
     * #getScaledBitmap(Bitmap src, float sx, float sy, float px, float py, boolean recycleSrc)}
     * 27.图片放缩  {@link #getScaledBitmap(Bitmap src, int w, int h, boolean recycleSrc)} 28.图片翻转
     * {@link #getMirroredBitmap(Bitmap src, int flag, boolean recycleSrc)} 29.图片歪斜  {@link
     * #getSkewedBitmap(Bitmap src, float kx, float ky, boolean recycleSrc)} 30.图片歪斜  {@link
     * #getSkewedBitmap(Bitmap src, float kx, float ky, float px, float py, boolean recycleSrc)}
     * 31.图片裁剪  {@link #getCroppedBitmap(Bitmap src, int x, int y, int width, int height, boolean
     * recycleSrc)} 32.图片适配  {@link #getAdaptedBitmap(Bitmap src, int width, int height, boolean
     * crop, boolean recycleSrc)} 33.图片层叠  {@link #overlayBitmap(Bitmap src, Bitmap overlay)}
     * 34.图片层叠 {@link #overlayBitmap(Bitmap src, Bitmap overlay, int x, int y)} 35.图片层叠  {@link
     * #getOverlaidBitmap(Bitmap src, Bitmap overlay, boolean recycleSrc)} 36.图片层叠  {@link
     * #getOverlaidBitmap(Bitmap src, Bitmap overlay, int x, int y, boolean recycleSrc)} 37.图片上加文字
     * {@link #drawTextBitmap(Bitmap src, String textList, int x, int y, int color)} 38.图片上加文字  {@link
     * #getDrawnTextBitmap(Bitmap src, String textList, int x, int y, int color, boolean recycleSrc)}
     * 39.图片圆角  {@link #getRoundCornerBitmap(Bitmap src, float rx, float ry, boolean recycleSrc)}
     * 40.图片倒影  {@link #getReflectionWithOriginBitmap(Bitmap src, int gap, boolean recycleSrc)}
     * 41.图片——老照片  {@link #ageBitmap(Bitmap src)} 42.图片——老照片  {@link #getAgedBitmap(Bitmap src,
     * boolean recycleSrc)} 43.图片——模糊  {@link #blurBitmap(Bitmap src, int ratio)} 44.图片——模糊  {@link
     * #getBlurredBitmap(Bitmap src, int ratio, boolean recycleSrc)} 45.图片——素描  {@link
     * #sketchBitmap(Bitmap src, float ratio)} 46.图片——素描  {@link #getSketchedBitmap(Bitmap src,
     * float ratio, boolean recycleSrc)} 47.图片——浮雕  {@link #embossBitmap(Bitmap src)} 48.图片——浮雕
     * {@link #getEmbossedBitmap(Bitmap src, boolean recycleSrc)} 49.图片——锐化  {@link
     * #sharpenBitmap(Bitmap src)} 50.图片——锐化  {@link #getSharpenedBitmap(Bitmap src, boolean
     * recycleSrc)} 51.图片——底片  {@link #filmBitmap(Bitmap src)} 52.图片——底片  {@link
     * #getFilmedBitmap(Bitmap src, boolean recycleSrc)} 53.图片——光照  {@link #lightBitmap(Bitmap src,
     * int centerX, int centerY)} 54.图片——光照  {@link #getLightedBitmap(Bitmap src, int centerX, int
     * centerY, boolean recycleSrc)} 55.一些未知...
     */

    private static final String TAG = ImgUtil.class.getSimpleName();

    private static final int OPTIONS_TEMP_STORAGE = 16 * 1024;
    private static final int MAX_DECODE_PICTURE_SIZE = 1920 * 1440;

    /**
     * 获取资源图片信息
     *
     * @param context 上下文
     * @param resId   图片资源id
     * @return Options对象
     */
    public static Options getBitmapInfo(Context context, int resId) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        InputStream is = context.getResources().openRawResource(resId);
        BitmapFactory.decodeStream(is, null, options);
        return options;
    }

    /**
     * 获取图片文件信息
     *
     * @param filePath 图片文件路径
     * @return Options对象
     */
    public static Options getBitmapInfo(String filePath) {
        Options options = new Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        return options;
    }

    /**
     * 读取资源图片
     *
     * @param context 上下文
     * @param resId   图片资源id
     * @return Bitmap对象
     */
    public static Bitmap loadBitmapFromRes(Context context, int resId) {
        Options options = new Options();
        options.inPreferredConfig = Config.ARGB_8888;
        options.inTempStorage = new byte[OPTIONS_TEMP_STORAGE];
        options.inPurgeable = true;
        options.inInputShareable = true;
        return loadBitmapFromRes(context, resId, options);
    }

    /**
     * 读取资源图片
     *
     * @param context 上下文
     * @param resId   图片资源id
     * @param config  位图解码格式
     * @return Bitmap对象
     */
    public static Bitmap loadBitmapFromRes(Context context, int resId,
            Config config) {
        Options options = new Options();
        options.inPreferredConfig = config;
        options.inTempStorage = new byte[OPTIONS_TEMP_STORAGE];
        options.inPurgeable = true;
        options.inInputShareable = true;
        return loadBitmapFromRes(context, resId, options);
    }

    /**
     * 读取资源图片
     *
     * @param context    上下文
     * @param resId      图片资源id
     * @param sampleSize 采样比率
     * @return Bitmap对象
     */
    public static Bitmap loadBitmapFromRes(Context context, int resId,
            int sampleSize) {
        Options options = new Options();
        options.inPreferredConfig = Config.ARGB_8888;
        options.inSampleSize = sampleSize;
        options.inTempStorage = new byte[OPTIONS_TEMP_STORAGE];
        options.inPurgeable = true;
        options.inInputShareable = true;
        return loadBitmapFromRes(context, resId, options);
    }

    /**
     * 读取资源图片
     *
     * @param context    上下文
     * @param resId      图片资源id
     * @param config     位图解码格式
     * @param sampleSize 采样比率
     * @return Bitmap对象
     */
    public static Bitmap loadBitmapFromRes(Context context, int resId,
            Config config, int sampleSize) {
        Options options = new Options();
        options.inPreferredConfig = config;
        options.inSampleSize = sampleSize;
        options.inTempStorage = new byte[OPTIONS_TEMP_STORAGE];
        options.inPurgeable = true;
        options.inInputShareable = true;
        return loadBitmapFromRes(context, resId, options);
    }

    /**
     * 读取资源图片
     *
     * @param context 上下文
     * @param resId   图片资源id
     * @return Bitmap对象
     */
    public static Bitmap loadBitmapFromRes(Context context, int resId,
            Options options) {
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = context.getResources().openRawResource(resId);
            bitmap = BitmapFactory.decodeStream(is, null, options);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    /**
     * 从assets中读取图片文件
     *
     * @param assetPath 图片文件路径
     */
    public static Bitmap loadBitmapFromAssets(Context context, String assetPath)
            throws OutOfMemoryError {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Config.ARGB_8888;
        options.inScaled = false;
        options.inTempStorage = new byte[OPTIONS_TEMP_STORAGE];
        options.inPurgeable = true;
        options.inInputShareable = true;
        return loadBitmapFromAssets(context, assetPath, options);
    }

    /**
     * 从assets读取图片文件
     *
     * @param assetPath 图片文件路径
     * @param config    位图解码格式
     * @return Bitmap对象
     */
    public static Bitmap loadBitmapFromAssets(Context context,
            String assetPath, Config config) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = config;
        options.inScaled = false;
        options.inTempStorage = new byte[OPTIONS_TEMP_STORAGE];
        options.inPurgeable = true;
        options.inInputShareable = true;
        return loadBitmapFromAssets(context, assetPath, options);
    }

    /**
     * 从assets读取图片文件
     *
     * @param assetPath  图片文件路径
     * @param sampleSize 采样比率
     * @return Bitmap对象
     */
    public static Bitmap loadBitmapFromAssets(Context context,
            String assetPath, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Config.ARGB_8888;
        options.inSampleSize = sampleSize;
        options.inScaled = false;
        options.inTempStorage = new byte[OPTIONS_TEMP_STORAGE];
        options.inPurgeable = true;
        options.inInputShareable = true;
        return loadBitmapFromAssets(context, assetPath, options);
    }

    /**
     * 从assets读取图片文件
     *
     * @param assetPath  图片文件路径
     * @param config     位图解码格式
     * @param sampleSize 采样比率
     * @return Bitmap对象
     */
    public static Bitmap loadBitmapFromAssets(Context context,
            String assetPath, Config config, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = config;
        options.inSampleSize = sampleSize;
        options.inScaled = false;
        options.inTempStorage = new byte[OPTIONS_TEMP_STORAGE];
        options.inPurgeable = true;
        options.inInputShareable = true;
        return loadBitmapFromAssets(context, assetPath, options);
    }

    /**
     * 从assets读取图片文件
     *
     * @param assetPath 图片文件路径
     * @param options   位图解码选项
     * @return Bitmap对象
     */
    public static Bitmap loadBitmapFromAssets(Context context,
            String assetPath, Options options) {
        InputStream is = null;
        Bitmap bitmap = null;
        try {
            is = context.getAssets().open(assetPath);
            bitmap = BitmapFactory.decodeStream(is, null, options);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bitmap;
    }

    /**
     * 从磁盘读取图片文件
     *
     * @param filePath 图片文件路径
     * @return Bitmap对象
     */
    public static Bitmap loadBitmapFromFile(String filePath)
            throws OutOfMemoryError {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Config.ARGB_8888;
        options.inScaled = false;
        options.inTempStorage = new byte[OPTIONS_TEMP_STORAGE];
        options.inPurgeable = true;
        options.inInputShareable = true;
        return loadBitmapFromFile(filePath, options);
    }

    /**
     * 从磁盘读取图片文件
     *
     * @param filePath 图片文件路径
     * @param config   位图解码格式
     * @return Bitmap对象
     */
    public static Bitmap loadBitmapFromFile(String filePath, Config config) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = config;
        options.inScaled = false;
        options.inTempStorage = new byte[OPTIONS_TEMP_STORAGE];
        options.inPurgeable = true;
        options.inInputShareable = true;
        return loadBitmapFromFile(filePath, options);
    }

    /**
     * 从磁盘读取图片文件
     *
     * @param filePath   图片文件路径
     * @param sampleSize 采样比率
     * @return Bitmap对象
     */
    public static Bitmap loadBitmapFromFile(String filePath, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Config.ARGB_8888;
        options.inSampleSize = sampleSize;
        options.inScaled = false;
        options.inTempStorage = new byte[OPTIONS_TEMP_STORAGE];
        options.inPurgeable = true;
        options.inInputShareable = true;
        return loadBitmapFromFile(filePath, options);
    }

    /**
     * 从磁盘读取图片文件
     *
     * @param filePath   图片文件路径
     * @param config     位图解码格式
     * @param sampleSize 采样比率
     * @return Bitmap对象
     */
    public static Bitmap loadBitmapFromFile(String filePath, Config config,
            int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = config;
        options.inSampleSize = sampleSize;
        options.inScaled = false;
        options.inTempStorage = new byte[OPTIONS_TEMP_STORAGE];
        options.inPurgeable = true;
        options.inInputShareable = true;
        return loadBitmapFromFile(filePath, options);
    }

    /**
     * 从磁盘读取图片文件
     *
     * @param filePath 图片文件路径
     * @param options  位图解码选项
     * @return Bitmap对象
     */
    public static Bitmap loadBitmapFromFile(String filePath, Options options) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeFile(filePath, options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 图片保存磁盘文件
     *
     * @param src     源图片
     * @param path    目标文件路径
     * @param format  文件格式 -JPEG -PNG
     * @param quality 压缩质量
     * @return 是否成功
     */
    public static boolean saveBitmap(Bitmap src, String path,
            CompressFormat format, int quality) {
        return saveBitmap(src, new File(path), format, quality);
    }

    /**
     * 图片保存
     *
     * @param src     源图片
     * @param file    目标文件
     * @param format  文件格式 -JPEG -PNG
     * @param quality 压缩质量
     * @return 是否成功
     */
    public static boolean saveBitmap(Bitmap src, File file,
            CompressFormat format, int quality) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            return src.compress(format, quality, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 从大图获取缩略图
     *
     * @param path   图片文件路径
     * @param width  目标缩略图宽度
     * @param height 目标缩略图高度
     * @param crop   是否剪裁，若为true，则适配时会将图片等比例缩放至目标宽高，保持图片尺寸尽可能大，并进行居中剪裁
     * @return Bitmap对象
     */
    public static Bitmap getThumbnailFromFile(String path, int width,
            int height, boolean crop) {
        if (path == null || !path.equals("") || width <= 0 || height <= 0) {
            LogUtil.w(TAG, "extractThumbnail", "invalid arguments");
            return null;
        }

        BitmapFactory.Options options = getBitmapInfo(path);
        LogUtil.d(TAG, "extractThumbnail", "round=" + width + "x" + height
                + ", crop=" + crop);
        final double scaleX = options.outWidth * 1.0 / width;
        final double scaleY = options.outHeight * 1.0 / height;
        LogUtil.d(TAG, "extractThumbnail", "extract beX = " + scaleX
                + ", beY = " + scaleY);
        options.inSampleSize = (int) (crop ? (scaleY > scaleX ? scaleX : scaleY)
                : (scaleY < scaleX ? scaleX : scaleY));
        if (options.inSampleSize <= 1) {
            options.inSampleSize = 1;
        }
        while (options.outHeight * options.outWidth / options.inSampleSize
                > MAX_DECODE_PICTURE_SIZE) {
            ++options.inSampleSize;
        }
        LogUtil.i(TAG, "extractThumbnail", "sample=" + options.inSampleSize);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = loadBitmapFromFile(path, options);
        if (bitmap == null) {
            LogUtil.w(TAG, "extractThumbnail", "decode bitmap failed");
            return null;
        }
        return getAdaptedBitmap(bitmap, width, height, crop, true);
    }

    /**
     * 从Drawable对象获取位图
     *
     * @param drawable 源drawable
     * @return Bitmap对象
     */
    public static Bitmap getBitmapFromDrawable(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
                .getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    /**
     * 从Bitmap获取像素字节数组
     *
     * @param src        源位图
     * @param recycleSrc 是否回收源位图
     * @return byte[]数组
     */
    public static byte[] getBytesFromBitmap(Bitmap src, boolean recycleSrc) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        if (!src.compress(CompressFormat.PNG, 100, output)) {
            return null;
        }

        if (recycleSrc) {
            src.recycle();
        }
        return output.toByteArray();
    }

    /**
     * 图片变换
     *
     * @param src        源图片
     * @param matrix     变换矩阵
     * @param recycleSrc 是否回收源位图
     * @return Bitmap对象
     */
    public static Bitmap getTransformedBitmap(Bitmap src, Matrix matrix,
            boolean recycleSrc) {
        if (src == null) {
            LogUtil.w(TAG, "getTransformedBitmap", "source bitmap is null");
            return null;
        }

        Bitmap dst = null;
        try {
            dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(),
                    src.getHeight(), matrix, true);
            if (recycleSrc && dst != src) {
                src.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dst;
    }

    /**
     * 图片旋转
     *
     * @param src        源图片
     * @param degree     旋转角度
     * @param recycleSrc 是否回收源位图
     * @return Bitmap对象
     */
    public static Bitmap getRotatedBitamp(Bitmap src, float degree,
            boolean recycleSrc) {
        Matrix matrix = new Matrix();
        matrix.setRotate(degree);
        return getTransformedBitmap(src, matrix, recycleSrc);
    }

    /**
     * 图片旋转
     *
     * @param src        源图片
     * @param degree     旋转角度
     * @param px         旋转中心点x坐标
     * @param py         旋转中心点y坐标
     * @param recycleSrc 是否回收源位图
     * @return Bitmap对象
     */
    public static Bitmap getRotatedBitamp(Bitmap src, float degree, float px,
            float py, boolean recycleSrc) {
        Matrix matrix = new Matrix();
        matrix.setRotate(degree, px, py);
        return getTransformedBitmap(src, matrix, recycleSrc);
    }

    /**
     * 图片缩放
     *
     * @param src        源图片
     * @param sx         x轴缩放比例
     * @param sy         y轴缩放比例
     * @param recycleSrc 是否回收源位图
     * @return Bitmap对象
     */
    public static Bitmap getScaledBitmap(Bitmap src, float sx, float sy,
            boolean recycleSrc) {
        Matrix matrix = new Matrix();
        matrix.setScale(sx, sy);
        return getTransformedBitmap(src, matrix, recycleSrc);
    }

    /**
     * 图片缩放
     *
     * @param src        源图片
     * @param sx         x轴缩放比例
     * @param sy         y轴缩放比例
     * @param px         中心点x坐标
     * @param py         中心点y坐标
     * @param recycleSrc 是否回收源位图
     * @return Bitmap对象
     */
    public static Bitmap getScaledBitmap(Bitmap src, float sx, float sy,
            float px, float py, boolean recycleSrc) {
        Matrix matrix = new Matrix();
        matrix.setScale(sx, sy, px, py);
        return getTransformedBitmap(src, matrix, recycleSrc);
    }

    /**
     * 图片缩放
     *
     * @param src        源图片
     * @param w          目标图片宽度
     * @param h          目标图片高度
     * @param recycleSrc 是否回收源位图
     * @return Bitmap对象
     */
    public static Bitmap getScaledBitmap(Bitmap src, int w, int h,
            boolean recycleSrc) {
        if (src == null) {
            LogUtil.w(TAG, "getScaledBitmap", "source bitmap is null");
            return null;
        }

        Bitmap dst = null;
        try {
            dst = Bitmap.createScaledBitmap(src, w, h, true);
            if (recycleSrc && dst != src) {
                src.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dst;
    }

    /**
     * 图片翻转
     *
     * @param src        源图片
     * @param flag       0-水平翻转 1-垂直翻转
     * @param recycleSrc 是否回收源位图
     * @return Bitmap对象
     */
    public static Bitmap getMirroredBitmap(Bitmap src, int flag,
            boolean recycleSrc) {
        Matrix matrix = new Matrix();
        switch (flag) {
            case 0: // 水平翻转
                matrix.setScale(-1, 1);
                break;
            case 1: // 垂直翻转
                matrix.setScale(1, -1);
                break;
        }
        return getTransformedBitmap(src, matrix, recycleSrc);
    }

    /**
     * 图片歪斜
     *
     * @param src        源图片
     * @param kx         x轴歪斜比例
     * @param ky         y轴歪斜比例
     * @param recycleSrc 是否回收源位图
     * @return Bitmap对象
     */
    public static Bitmap getSkewedBitmap(Bitmap src, float kx, float ky,
            boolean recycleSrc) {
        Matrix matrix = new Matrix();
        matrix.setSkew(kx, ky);
        return getTransformedBitmap(src, matrix, recycleSrc);
    }

    /**
     * 图片歪斜
     *
     * @param src        源图片
     * @param kx         x轴歪斜比例
     * @param ky         y轴歪斜比例
     * @param px         中心点x坐标
     * @param py         中心点y坐标
     * @param recycleSrc 是否回收源位图
     * @return Bitmap对象
     */
    public static Bitmap getSkewedBitmap(Bitmap src, float kx, float ky,
            float px, float py, boolean recycleSrc) {
        Matrix matrix = new Matrix();
        matrix.setSkew(kx, ky, px, py);
        return getTransformedBitmap(src, matrix, recycleSrc);
    }

    /**
     * 图片剪裁
     *
     * @param src        源图片
     * @param x          剪裁区域左上角点x坐标
     * @param y          剪裁区域左上角点y坐标
     * @param width      剪裁区域宽度
     * @param height     剪裁区域高度
     * @param recycleSrc 是否回收源位图
     * @return Bitmap对象
     */
    public static Bitmap getCroppedBitmap(Bitmap src, int x, int y, int width,
            int height, boolean recycleSrc) {
        if (src == null) {
            LogUtil.w(TAG, "getCroppedBitmap", "source bitmap is null");
            return null;
        }

        Bitmap dst = null;
        try {
            dst = Bitmap.createBitmap(src, x, y, width, height);
            if (recycleSrc && dst != src) {
                src.recycle();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dst;
    }

    /**
     * 图片适配
     *
     * @param src        源图片
     * @param width      目标宽度
     * @param height     目标高度
     * @param crop       是否剪裁，若为true，则适配时会将图片等比例缩放至目标宽高，保持图片尺寸尽可能大，并进行居中剪裁
     * @param recycleSrc 是否回收源位图
     * @return Bitmap对象
     */
    public static Bitmap getAdaptedBitmap(Bitmap src, int width, int height,
            boolean crop, boolean recycleSrc) {
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();
        int dstWidth = width;
        int dstHeight = height;
        if (width * srcHeight > height * srcWidth) { // scaleX > scaleY
            if (crop) {
                dstHeight = srcHeight * width / srcWidth;
            } else {
                dstWidth = srcWidth * height / srcHeight;
            }
        } else if (width * srcHeight < height * srcWidth) { // scaleX < scaleY
            if (crop) {
                dstWidth = srcWidth * height / srcHeight;
            } else {
                dstHeight = srcHeight * width / srcWidth;
            }
        } else { // scaleX == scaleY, 此时不需要剪裁
            crop = false;
        }
        Bitmap bitmap = getScaledBitmap(src, dstWidth, dstHeight, recycleSrc);
        if (crop) {
            bitmap = getCroppedBitmap(bitmap, (dstWidth - width) / 2,
                    (dstHeight - height) / 2, width, height, true);
        }
        return bitmap;
    }

    /**
     * 图片层叠
     *
     * @param src     源图片
     * @param overlay 覆盖图片
     * @return 源Bitmap对象
     */
    public static Bitmap overlayBitmap(Bitmap src, Bitmap overlay) {
        if (!src.isMutable()) {
            LogUtil.w(TAG, "overlayBitmap", "source bitmap is not mutable");
            return src;
        }
        Canvas canvas = new Canvas(src);
        canvas.drawBitmap(overlay, (src.getWidth() - overlay.getWidth()) / 2,
                (src.getHeight() - overlay.getHeight()) / 2, null);
        canvas.save();
        canvas.restore();
        return src;
    }

    /**
     * 图片层叠
     *
     * @param src     源图片
     * @param overlay 覆盖图片
     * @param x       覆盖图片绘制位置x坐标
     * @param y       覆盖图片绘制位置y坐标
     * @return 源Bitmap对象
     */
    public static Bitmap overlayBitmap(Bitmap src, Bitmap overlay, int x, int y) {
        if (!src.isMutable()) {
            LogUtil.w(TAG, "overlayBitmap", "source bitmap is not mutable");
            return src;
        }
        Canvas canvas = new Canvas(src);
        canvas.drawBitmap(overlay, x, y, null);
        canvas.save();
        canvas.restore();
        return src;
    }

    /**
     * 图片层叠
     *
     * @param src        源图片
     * @param overlay    覆盖图片
     * @param recycleSrc 是否回收源位图
     * @return 新Bitmap对象
     */
    public static Bitmap getOverlaidBitmap(Bitmap src, Bitmap overlay,
            boolean recycleSrc) {
        Bitmap overlaid = overlayBitmap(src.copy(Config.ARGB_8888, true),
                overlay);
        if (recycleSrc && overlaid != src) {
            src.recycle();
        }
        return overlaid;
    }

    /**
     * 图片层叠
     *
     * @param src        源图片
     * @param overlay    覆盖图片
     * @param x          覆盖图片绘制位置x坐标
     * @param y          覆盖图片绘制位置y坐标
     * @param recycleSrc 是否回收源位图
     * @return 新Bitmap对象
     */
    public static Bitmap getOverlaidBitmap(Bitmap src, Bitmap overlay, int x,
            int y, boolean recycleSrc) {
        Bitmap overlaid = overlayBitmap(src.copy(Config.ARGB_8888, true),
                overlay, x, y);
        if (recycleSrc && overlaid != src) {
            src.recycle();
        }
        return overlaid;
    }

    /**
     * 图片上写文字
     *
     * @param src   源图片
     * @param text  文字内容
     * @param x     文字绘制位置x坐标
     * @param y     文字绘制位置y坐标
     * @param color 文字绘制颜色
     * @return 源Bitmap对象
     */
    public static Bitmap drawTextBitmap(Bitmap src, String text, int x, int y,
            int color) {
        if (!src.isMutable()) {
            LogUtil.w(TAG, "drawTextBitmap", "source bitmap is not mutable");
            return src;
        }
        Canvas canvas = new Canvas(src);
        Paint paint = new Paint();
        paint.setColor(color);
        canvas.drawText(text, x, y, paint);
        canvas.save();
        canvas.restore();
        return src;
    }

    /**
     * 图片上写文字
     *
     * @param src        源图片
     * @param text       文字内容
     * @param x          文字绘制位置x坐标
     * @param y          文字绘制位置y坐标
     * @param color      文字绘制颜色
     * @param recycleSrc 是否回收源位图
     * @return 新Bitmap对象
     */
    public static Bitmap getDrawnTextBitmap(Bitmap src, String text, int x,
            int y, int color, boolean recycleSrc) {
        Bitmap drawnText = drawTextBitmap(src.copy(Config.ARGB_8888, true),
                text, x, y, color);
        if (recycleSrc && drawnText != src) {
            src.recycle();
        }
        return drawnText;
    }

    /**
     * 获取圆角图片
     *
     * @param src        源位图
     * @param rx         x轴圆角半径
     * @param ry         y轴圆角半径
     * @param recycleSrc 是否回收源位图
     * @return Bitmap对象
     */
    public static Bitmap getRoundCornerBitmap(Bitmap src, float rx, float ry,
            boolean recycleSrc) {
        Bitmap dst = Bitmap.createBitmap(src.getWidth(), src.getHeight(),
                Config.ARGB_8888);
        Canvas canvas = new Canvas(dst);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, src.getWidth(), src.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, rx, ry, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(src, rect, rect, paint);
        if (recycleSrc) {
            src.recycle();
        }

        return dst;
    }

    /**
     * 获取带倒影图片
     *
     * @param src        源位图
     * @param gap        倒影与源图之间的间距
     * @param recycleSrc 是否回收源位图
     * @return Bitmap对象
     */
    public static Bitmap getReflectionWithOriginBitmap(Bitmap src, int gap,
            boolean recycleSrc) {
        int width = src.getWidth();
        int height = src.getHeight();

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        Bitmap reflectionImage = Bitmap.createBitmap(src, 0, height / 2, width,
                height / 2, matrix, false);
        Bitmap dst = Bitmap.createBitmap(width, (height + height / 2),
                Config.ARGB_8888);
        Canvas canvas = new Canvas(dst);
        canvas.drawBitmap(src, 0, 0, null);
        Paint deafalutPaint = new Paint();
        canvas.drawRect(0, height, width, height + gap, deafalutPaint);
        canvas.drawBitmap(reflectionImage, 0, height + gap, null);
        reflectionImage.recycle();
        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, src.getHeight(), 0,
                dst.getHeight() + gap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, height, width, dst.getHeight() + gap, paint);
        if (recycleSrc) {
            src.recycle();
        }
        return dst;
    }

    /**
     * 获取老照片效果图片
     *
     * @param src        源位图
     * @return 源Bitmap对象
     */
    public static Bitmap ageBitmap(Bitmap src) {
        if (!src.isMutable()) {
            LogUtil.w(TAG, "ageBitmap", "source bitmap is not mutable");
            return src;
        }

        int width = src.getWidth();
        int height = src.getHeight();
        int pixColor = 0;
        int pixR = 0;
        int pixG = 0;
        int pixB = 0;
        int newR = 0;
        int newG = 0;
        int newB = 0;
        int[] pixels = new int[width * height];
        src.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < height; i++) {
            for (int k = 0; k < width; k++) {
                pixColor = pixels[width * i + k];
                pixR = Color.red(pixColor);
                pixG = Color.green(pixColor);
                pixB = Color.blue(pixColor);
                newR = (int) (0.393 * pixR + 0.769 * pixG + 0.189 * pixB);
                newG = (int) (0.349 * pixR + 0.686 * pixG + 0.168 * pixB);
                newB = (int) (0.272 * pixR + 0.534 * pixG + 0.131 * pixB);
                int newColor = Color.argb(255, newR > 255 ? 255 : newR,
                        newG > 255 ? 255 : newG, newB > 255 ? 255 : newB);
                pixels[width * i + k] = newColor;
            }
        }
        src.setPixels(pixels, 0, width, 0, 0, width, height);
        return src;
    }

    /**
     * 获取老照片效果图片
     *
     * @param src        源位图
     * @param recycleSrc 是否回收源位图
     * @return 新Bitmap对象
     */
    public static Bitmap getAgedBitmap(Bitmap src, boolean recycleSrc) {
        Bitmap aged = ageBitmap(src.copy(Config.RGB_565, true));
        if (recycleSrc && aged != src) {
            src.recycle();
        }
        return aged;
    }

    /**
     * 获取模糊效果图片
     *
     * @param src   源位图
     * @param ratio 建议取值16，值越小图片会越亮，越大则越暗
     * @return 源Bitmap对象
     */
    public static Bitmap blurBitmap(Bitmap src, int ratio) {
        if (!src.isMutable()) {
            LogUtil.w(TAG, "blurImage", "source bitmap is not mutable");
            return src;
        }

        // 高斯矩阵
        int[] gauss = new int[]{1, 2, 1, 2, 4, 2, 1, 2, 1};
        int width = src.getWidth();
        int height = src.getHeight();
        int pixR = 0;
        int pixG = 0;
        int pixB = 0;
        int pixColor = 0;
        int newR = 0;
        int newG = 0;
        int newB = 0;
        int idx = 0;
        int[] pixels = new int[width * height];
        src.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 1, length = height - 1; i < length; i++) {
            for (int k = 1, len = width - 1; k < len; k++) {
                idx = 0;
                for (int m = -1; m <= 1; m++) {
                    for (int n = -1; n <= 1; n++) {
                        pixColor = pixels[(i + m) * width + k + n];
                        pixR = Color.red(pixColor);
                        pixG = Color.green(pixColor);
                        pixB = Color.blue(pixColor);
                        newR = newR + (int) (pixR * gauss[idx]);
                        newG = newG + (int) (pixG * gauss[idx]);
                        newB = newB + (int) (pixB * gauss[idx]);
                        idx++;
                    }
                }
                newR /= ratio;
                newG /= ratio;
                newB /= ratio;
                newR = Math.min(255, Math.max(0, newR));
                newG = Math.min(255, Math.max(0, newG));
                newB = Math.min(255, Math.max(0, newB));
                pixels[i * width + k] = Color.argb(255, newR, newG, newB);
                newR = 0;
                newG = 0;
                newB = 0;
            }
        }

        src.setPixels(pixels, 0, width, 0, 0, width, height);
        return src;
    }

    /**
     * 获取模糊效果图片
     *
     * @param src        源位图
     * @param ratio      强度，建议取值16，值越小图片会越亮，越大则越暗
     * @param recycleSrc 是否回收源位图
     * @return 新Bitmap对象
     */
    public static Bitmap getBlurredBitmap(Bitmap src, int ratio,
            boolean recycleSrc) {
        Bitmap aged = blurBitmap(src.copy(Config.RGB_565, true), ratio);
        if (recycleSrc && aged != src) {
            src.recycle();
        }
        return aged;
    }

    /**
     * 获取素描效果图片
     *
     * @param src        源位图
     * @param ratio      强度，建议取值5
     * @return 源Bitmap对象
     */
    public static Bitmap sketchBitmap(Bitmap src, float ratio) {
        if (!src.isMutable()) {
            LogUtil.w(TAG, "sketchBitmap", "source bitmap is not mutable");
            return src;
        }

        int pos, row, col, clr;
        int width = src.getWidth();
        int height = src.getHeight();
        int[] pixSrc = new int[width * height];
        int[] pixNvt = new int[width * height];
        src.getPixels(pixSrc, 0, width, 0, 0, width, height);

        // 先将位图像素处理成灰度颜色后再取反
        for (row = 0; row < height; ++row) {
            for (col = 0; col < width; ++col) {
                pos = row * width + col;
                pixSrc[pos] = (Color.red(pixSrc[pos])
                        + Color.green(pixSrc[pos]) + Color.blue(pixSrc[pos])) / 3;
                pixNvt[pos] = 255 - pixSrc[pos];
            }
        }

        // 对取反的像素进行高斯模糊
        gaussGray(pixNvt, ratio, ratio, width, height);

        // 灰度颜色和模糊后的像素进行差值运算
        for (row = 0; row < height; ++row) {
            for (col = 0; col < width; ++col) {
                pos = row * width + col;
                clr = pixSrc[pos] << 8;
                clr /= 256 - pixNvt[pos];
                clr = Math.min(clr, 255);
                pixSrc[pos] = Color.rgb(clr, clr, clr);
            }
        }

        src.setPixels(pixSrc, 0, width, 0, 0, width, height);
        return src;
    }

    /**
     * 获取素描效果图片
     *
     * @param src        源位图
     * @param ratio      强度，建议取值5
     * @param recycleSrc 是否回收源位图
     * @return 新Bitmap对象
     */
    public static Bitmap getSketchedBitmap(Bitmap src, float ratio,
            boolean recycleSrc) {
        Bitmap sketched = sketchBitmap(src.copy(Config.RGB_565, true), ratio);
        if (recycleSrc && sketched != src) {
            src.recycle();
        }
        return sketched;
    }

    private static int gaussGray(int[] psrc, double horz, double vert,
            int width, int height) {
        int[] dst, src;
        double[] n_p, n_m, d_p, d_m, bd_p, bd_m;
        double[] val_p, val_m;
        int i, j, t, k, row, col, terms;
        int[] initial_p, initial_m;
        double std_dev;
        int row_stride = width;
        int max_len = Math.max(width, height);
        int sp_p_idx, sp_m_idx, vp_idx, vm_idx;

        val_p = new double[max_len];
        val_m = new double[max_len];

        n_p = new double[5];
        n_m = new double[5];
        d_p = new double[5];
        d_m = new double[5];
        bd_p = new double[5];
        bd_m = new double[5];

        src = new int[max_len];
        dst = new int[max_len];

        initial_p = new int[4];
        initial_m = new int[4];

        // 垂直方向
        if (vert > 0.0) {
            vert = Math.abs(vert) + 1.0;
            std_dev = Math.sqrt(-(vert * vert) / (2 * Math.log(1.0 / 255.0)));

            // 初试化常量
            findConstants(n_p, n_m, d_p, d_m, bd_p, bd_m, std_dev);

            for (col = 0; col < width; col++) {
                for (k = 0; k < max_len; k++) {
                    val_m[k] = val_p[k] = 0;
                }

                for (t = 0; t < height; t++) {
                    src[t] = psrc[t * row_stride + col];
                }

                sp_p_idx = 0;
                sp_m_idx = height - 1;
                vp_idx = 0;
                vm_idx = height - 1;

                initial_p[0] = src[0];
                initial_m[0] = src[height - 1];

                for (row = 0; row < height; row++) {
                    terms = (row < 4) ? row : 4;

                    for (i = 0; i <= terms; i++) {
                        val_p[vp_idx] += n_p[i] * src[sp_p_idx - i] - d_p[i]
                                * val_p[vp_idx - i];
                        val_m[vm_idx] += n_m[i] * src[sp_m_idx + i] - d_m[i]
                                * val_m[vm_idx + i];
                    }
                    for (j = i; j <= 4; j++) {
                        val_p[vp_idx] += (n_p[j] - bd_p[j]) * initial_p[0];
                        val_m[vm_idx] += (n_m[j] - bd_m[j]) * initial_m[0];
                    }

                    sp_p_idx++;
                    sp_m_idx--;
                    vp_idx++;
                    vm_idx--;
                }

                transferGaussPixels(val_p, val_m, dst, 1, height);

                for (t = 0; t < height; t++) {
                    psrc[t * row_stride + col] = dst[t];
                }
            }
        }

        // 水平方向
        if (horz > 0.0) {
            horz = Math.abs(horz) + 1.0;

            if (horz != vert) {
                std_dev = Math.sqrt(-(horz * horz)
                        / (2 * Math.log(1.0 / 255.0)));

                // 初试化常量
                findConstants(n_p, n_m, d_p, d_m, bd_p, bd_m, std_dev);
            }

            for (row = 0; row < height; row++) {
                for (k = 0; k < max_len; k++) {
                    val_m[k] = val_p[k] = 0;
                }

                for (t = 0; t < width; t++) {
                    src[t] = psrc[row * row_stride + t];
                }

                sp_p_idx = 0;
                sp_m_idx = width - 1;
                vp_idx = 0;
                vm_idx = width - 1;

                initial_p[0] = src[0];
                initial_m[0] = src[width - 1];

                for (col = 0; col < width; col++) {
                    terms = (col < 4) ? col : 4;

                    for (i = 0; i <= terms; i++) {
                        val_p[vp_idx] += n_p[i] * src[sp_p_idx - i] - d_p[i]
                                * val_p[vp_idx - i];
                        val_m[vm_idx] += n_m[i] * src[sp_m_idx + i] - d_m[i]
                                * val_m[vm_idx + i];
                    }
                    for (j = i; j <= 4; j++) {
                        val_p[vp_idx] += (n_p[j] - bd_p[j]) * initial_p[0];
                        val_m[vm_idx] += (n_m[j] - bd_m[j]) * initial_m[0];
                    }

                    sp_p_idx++;
                    sp_m_idx--;
                    vp_idx++;
                    vm_idx--;
                }

                transferGaussPixels(val_p, val_m, dst, 1, width);

                for (t = 0; t < width; t++) {
                    psrc[row * row_stride + t] = dst[t];
                }
            }
        }

        return 0;
    }

    private static void findConstants(double[] n_p, double[] n_m, double[] d_p,
            double[] d_m, double[] bd_p, double[] bd_m, double std_dev) {
        double div = Math.sqrt(2 * 3.141593) * std_dev;
        double x0 = -1.783 / std_dev;
        double x1 = -1.723 / std_dev;
        double x2 = 0.6318 / std_dev;
        double x3 = 1.997 / std_dev;
        double x4 = 1.6803 / div;
        double x5 = 3.735 / div;
        double x6 = -0.6803 / div;
        double x7 = -0.2598 / div;
        int i;

        n_p[0] = x4 + x6;
        n_p[1] = (Math.exp(x1)
                * (x7 * Math.sin(x3) - (x6 + 2 * x4) * Math.cos(x3)) + Math
                .exp(x0) * (x5 * Math.sin(x2) - (2 * x6 + x4) * Math.cos(x2)));
        n_p[2] = (2
                * Math.exp(x0 + x1)
                * ((x4 + x6) * Math.cos(x3) * Math.cos(x2) - x5 * Math.cos(x3)
                * Math.sin(x2) - x7 * Math.cos(x2) * Math.sin(x3)) + x6
                * Math.exp(2 * x0) + x4 * Math.exp(2 * x1));
        n_p[3] = (Math.exp(x1 + 2 * x0)
                * (x7 * Math.sin(x3) - x6 * Math.cos(x3)) + Math.exp(x0 + 2
                * x1)
                * (x5 * Math.sin(x2) - x4 * Math.cos(x2)));
        n_p[4] = 0.0;

        d_p[0] = 0.0;
        d_p[1] = -2 * Math.exp(x1) * Math.cos(x3) - 2 * Math.exp(x0)
                * Math.cos(x2);
        d_p[2] = 4 * Math.cos(x3) * Math.cos(x2) * Math.exp(x0 + x1)
                + Math.exp(2 * x1) + Math.exp(2 * x0);
        d_p[3] = -2 * Math.cos(x2) * Math.exp(x0 + 2 * x1) - 2 * Math.cos(x3)
                * Math.exp(x1 + 2 * x0);
        d_p[4] = Math.exp(2 * x0 + 2 * x1);

        for (i = 0; i <= 4; i++) {
            d_m[i] = d_p[i];
        }

        n_m[0] = 0.0;
        for (i = 1; i <= 4; i++) {
            n_m[i] = n_p[i] - d_p[i] * n_p[0];
        }

        double sum_n_p, sum_n_m, sum_d;
        double a, b;

        sum_n_p = 0.0;
        sum_n_m = 0.0;
        sum_d = 0.0;

        for (i = 0; i <= 4; i++) {
            sum_n_p += n_p[i];
            sum_n_m += n_m[i];
            sum_d += d_p[i];
        }

        a = sum_n_p / (1.0 + sum_d);
        b = sum_n_m / (1.0 + sum_d);

        for (i = 0; i <= 4; i++) {
            bd_p[i] = d_p[i] * a;
            bd_m[i] = d_m[i] * b;
        }
    }

    private static void transferGaussPixels(double[] src1, double[] src2,
            int[] dest, int bytes, int width) {
        int i, j, k, b;
        int bend = bytes * width;
        double sum;

        i = j = k = 0;
        for (b = 0; b < bend; b++) {
            sum = src1[i++] + src2[j++];

            if (sum > 255) {
                sum = 255;
            } else if (sum < 0) {
                sum = 0;
            }

            dest[k++] = (int) sum;
        }
    }

    /**
     * 获取浮雕效果的图片
     *
     * @param src 源位图
     * @return 源Bitmap对象
     */
    public static Bitmap embossBitmap(Bitmap src) {
        if (!src.isMutable()) {
            LogUtil.w(TAG, "embossBitmap", "source bitmap is not mutable");
            return src;
        }

        int width = src.getWidth();
        int height = src.getHeight();
        int pixR = 0;
        int pixG = 0;
        int pixB = 0;
        int pixColor = 0;
        int newR = 0;
        int newG = 0;
        int newB = 0;
        int[] pixels = new int[width * height];
        src.getPixels(pixels, 0, width, 0, 0, width, height);
        int pos = 0;
        for (int i = 1, length = height - 1; i < length; i++) {
            for (int k = 1, len = width - 1; k < len; k++) {
                pos = i * width + k;
                pixColor = pixels[pos];
                pixR = Color.red(pixColor);
                pixG = Color.green(pixColor);
                pixB = Color.blue(pixColor);
                pixColor = pixels[pos + 1];
                newR = Color.red(pixColor) - pixR + 127;
                newG = Color.green(pixColor) - pixG + 127;
                newB = Color.blue(pixColor) - pixB + 127;
                newR = Math.min(255, Math.max(0, newR));
                newG = Math.min(255, Math.max(0, newG));
                newB = Math.min(255, Math.max(0, newB));
                pixels[pos] = Color.argb(255, newR, newG, newB);
            }
        }

        src.setPixels(pixels, 0, width, 0, 0, width, height);
        return src;
    }

    /**
     * 获取浮雕效果的图片
     *
     * @param src        源位图
     * @param recycleSrc 是否回收源位图
     * @return 新Bitmap对象
     */
    public static Bitmap getEmbossedBitmap(Bitmap src, boolean recycleSrc) {
        Bitmap embossed = embossBitmap(src.copy(Config.RGB_565, true));
        if (recycleSrc && embossed != src) {
            src.recycle();
        }
        return embossed;
    }

    /**
     * 获取锐化效果的图片（拉普拉斯变换）
     *
     * @param src 源位图
     * @return 源Bitmap对象
     */
    public static Bitmap sharpenBitmap(Bitmap src) {
        if (!src.isMutable()) {
            LogUtil.w(TAG, "sharpenBitmap", "source bitmap is not mutable");
            return src;
        }

        // 拉普拉斯矩阵
        int[] laplacian = new int[]{-1, -1, -1, -1, 9, -1, -1, -1, -1};
        int width = src.getWidth();
        int height = src.getHeight();
        int pixR = 0;
        int pixG = 0;
        int pixB = 0;
        int pixColor = 0;
        int newR = 0;
        int newG = 0;
        int newB = 0;
        int idx = 0;
        float alpha = 0.3F;
        int[] pixels = new int[width * height];
        src.getPixels(pixels, 0, width, 0, 0, width, height);
        for (int i = 1, length = height - 1; i < length; i++) {
            for (int k = 1, len = width - 1; k < len; k++) {
                idx = 0;
                for (int m = -1; m <= 1; m++) {
                    for (int n = -1; n <= 1; n++) {
                        pixColor = pixels[(i + n) * width + k + m];
                        pixR = Color.red(pixColor);
                        pixG = Color.green(pixColor);
                        pixB = Color.blue(pixColor);
                        newR = newR + (int) (pixR * laplacian[idx] * alpha);
                        newG = newG + (int) (pixG * laplacian[idx] * alpha);
                        newB = newB + (int) (pixB * laplacian[idx] * alpha);
                        ++idx;
                    }
                }
                newR = Math.min(255, Math.max(0, newR));
                newG = Math.min(255, Math.max(0, newG));
                newB = Math.min(255, Math.max(0, newB));
                pixels[i * width + k] = Color.argb(255, newR, newG, newB);
                newR = 0;
                newG = 0;
                newB = 0;
            }
        }

        src.setPixels(pixels, 0, width, 0, 0, width, height);
        return src;
    }

    /**
     * 获取锐化效果的图片（拉普拉斯变换）
     *
     * @param src        源位图
     * @param recycleSrc 是否回收源位图
     * @return 新Bitmap对象
     */
    public static Bitmap getSharpenedBitmap(Bitmap src, boolean recycleSrc) {
        Bitmap sharpened = sharpenBitmap(src.copy(Config.RGB_565, true));
        if (recycleSrc && sharpened != src) {
            src.recycle();
        }
        return sharpened;
    }

    /**
     * 获取底片效果的图片
     *
     * @param src 源位图
     * @return 源Bitmap对象
     */
    public static Bitmap filmBitmap(Bitmap src) {
        if (!src.isMutable()) {
            LogUtil.w(TAG, "filmBitmap", "source bitmap is not mutable");
            return src;
        }

        // RGBA的最大值
        final int MAX_VALUE = 255;
        int width = src.getWidth();
        int height = src.getHeight();
        int pixR = 0;
        int pixG = 0;
        int pixB = 0;
        int pixColor = 0;
        int newR = 0;
        int newG = 0;
        int newB = 0;
        int[] pixels = new int[width * height];
        src.getPixels(pixels, 0, width, 0, 0, width, height);
        int pos = 0;
        for (int i = 1, length = height - 1; i < length; i++) {
            for (int k = 1, len = width - 1; k < len; k++) {
                pos = i * width + k;
                pixColor = pixels[pos];
                pixR = Color.red(pixColor);
                pixG = Color.green(pixColor);
                pixB = Color.blue(pixColor);
                newR = MAX_VALUE - pixR;
                newG = MAX_VALUE - pixG;
                newB = MAX_VALUE - pixB;
                newR = Math.min(MAX_VALUE, Math.max(0, newR));
                newG = Math.min(MAX_VALUE, Math.max(0, newG));
                newB = Math.min(MAX_VALUE, Math.max(0, newB));
                pixels[pos] = Color.argb(MAX_VALUE, newR, newG, newB);
            }
        }

        src.setPixels(pixels, 0, width, 0, 0, width, height);
        return src;
    }

    /**
     * 获取底片效果的图片
     *
     * @param src        源位图
     * @param recycleSrc 是否回收源位图
     * @return 新Bitmap对象
     */
    public static Bitmap getFilmedBitmap(Bitmap src, boolean recycleSrc) {
        Bitmap filmed = sharpenBitmap(src.copy(Config.RGB_565, true));
        if (recycleSrc && filmed != src) {
            src.recycle();
        }
        return filmed;
    }

    /**
     * 获取光照效果的图片
     *
     * @param src     源位图
     * @param centerX 光照中心x坐标
     * @param centerY 光照中心y坐标
     * @return 源Bitmap对象
     */
    public static Bitmap lightBitmap(Bitmap src, int centerX, int centerY) {
        if (!src.isMutable()) {
            LogUtil.w(TAG, "lightBitmap", "source bitmap is not mutable");
            return src;
        }

        int width = src.getWidth();
        int height = src.getHeight();
        int pixR = 0;
        int pixG = 0;
        int pixB = 0;
        int pixColor = 0;
        int newR = 0;
        int newG = 0;
        int newB = 0;
        int radius = Math.min(centerX, centerY);
        final float strength = 150F; // 光照强度 100~150
        int[] pixels = new int[width * height];
        src.getPixels(pixels, 0, width, 0, 0, width, height);
        int pos = 0;
        for (int i = 1, length = height - 1; i < length; i++) {
            for (int k = 1, len = width - 1; k < len; k++) {
                pos = i * width + k;
                pixColor = pixels[pos];
                pixR = Color.red(pixColor);
                pixG = Color.green(pixColor);
                pixB = Color.blue(pixColor);
                newR = pixR;
                newG = pixG;
                newB = pixB;

                // 计算当前点到光照中心的距离，平面座标系中求两点之间的距离
                int distance = (int) (Math.pow((centerY - i), 2) + Math.pow(
                        centerX - k, 2));
                if (distance < radius * radius) {
                    // 按照距离大小计算增加的光照值
                    int result = (int) (strength * (1.0 - Math.sqrt(distance)
                            / radius));
                    newR = pixR + result;
                    newG = pixG + result;
                    newB = pixB + result;
                }
                newR = Math.min(255, Math.max(0, newR));
                newG = Math.min(255, Math.max(0, newG));
                newB = Math.min(255, Math.max(0, newB));
                pixels[pos] = Color.argb(255, newR, newG, newB);
            }
        }

        src.setPixels(pixels, 0, width, 0, 0, width, height);
        return src;
    }

    /**
     * 获取光照效果的图片
     *
     * @param src        源位图
     * @param centerX    光照中心x坐标
     * @param centerY    光照中心y坐标
     * @param recycleSrc 是否回收源位图
     * @return 新Bitmap对象
     */
    public static Bitmap getLightedBitmap(Bitmap src, int centerX, int centerY,
            boolean recycleSrc) {
        Bitmap lighted = lightBitmap(src.copy(Config.RGB_565, true), centerX,
                centerY);
        if (recycleSrc && lighted != src) {
            src.recycle();
        }
        return lighted;
    }

    public static void convertYUV420SPToARGB(int[] argbBuf, byte[] yuv420sp,
            int width, int height) {
        final int frameSize = width * height;
        if (argbBuf == null) {
            throw new NullPointerException("buffer 'rgbBuf' is null");
        }
        if (argbBuf.length < frameSize) {
            throw new IllegalArgumentException("buffer 'rgbBuf' size "
                    + argbBuf.length + " < minimum " + frameSize);
        }
        if (yuv420sp == null) {
            throw new NullPointerException("buffer 'yuv420sp' is null");
        }
        if (yuv420sp.length < frameSize * 3 / 2) {
            throw new IllegalArgumentException("buffer 'yuv420sp' size "
                    + yuv420sp.length + " < minimum " + frameSize * 3 / 2);
        }

        int i = 0, y = 0;
        int uvp = 0, u = 0, v = 0;
        int y1192 = 0, r = 0, g = 0, b = 0;

        for (int j = 0, yp = 0; j < height; j++) {
            uvp = frameSize + (j >> 1) * width;
            u = 0;
            v = 0;
            for (i = 0; i < width; i++, yp++) {
                y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0) {
                    y = 0;
                }
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }

                y1192 = 1192 * y;
                r = (y1192 + 1634 * v);
                g = (y1192 - 833 * v - 400 * u);
                b = (y1192 + 2066 * u);

                if (r < 0) {
                    r = 0;
                } else if (r > 262143) {
                    r = 262143;
                }
                if (g < 0) {
                    g = 0;
                } else if (g > 262143) {
                    g = 262143;
                }
                if (b < 0) {
                    b = 0;
                } else if (b > 262143) {
                    b = 262143;
                }

                argbBuf[yp] = 0xff000000 | ((r << 6) & 0xff0000)
                        | ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
    }

    public static Bitmap decodeYUV420SP_oldApi(int[] argbBuf, byte[] yuv420sp,
            int width, int height) {
        if (argbBuf == null) {
            argbBuf = new int[width * height];
        }
        convertYUV420SPToARGB(argbBuf, yuv420sp, width, height);
        return Bitmap.createBitmap(argbBuf, width, height, Config.ARGB_8888);
    }

    public static void saveYuvToJpeg_oldApi(File jpegFile, int[] argbBuf,
            byte[] yuv420sp, int width, int height, int quality) {
        Bitmap bmp = decodeYUV420SP_oldApi(argbBuf, yuv420sp, width, height);
        if (bmp != null) {
            saveBitmap(bmp, jpegFile, CompressFormat.JPEG, quality);
            bmp.recycle();
        }
    }

    public static Bitmap decodeYUV420SP_newApi(byte[] yuv420sp, int width,
            int height, int format, int quality) {
        YuvImage image = new YuvImage(yuv420sp, format, width, height, null);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        image.compressToJpeg(
                new Rect(0, 0, image.getWidth(), image.getHeight()), quality,
                bos);
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        return BitmapFactory.decodeStream(bis);
    }

    public static void saveYuvToJpeg_newApi(File jpegFile, byte[] yuv420sp,
            int width, int height, int format, int quality) {
        try {
            if (!jpegFile.exists()) {
                jpegFile.createNewFile();
            }
            YuvImage image = new YuvImage(yuv420sp, format, width, height, null);
            FileOutputStream fos = new FileOutputStream(jpegFile);
            image.compressToJpeg(
                    new Rect(0, 0, image.getWidth(), image.getHeight()),
                    quality, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap decodeYUV420SP(int[] argbBuf, byte[] yuv420sp,
            int width, int height, int format, int quality) {
        if (VerUtil.sdkSupport(VerUtil.FROYO)) {
            return decodeYUV420SP_newApi(yuv420sp, width, height, format,
                    quality);
        } else {
            return decodeYUV420SP_oldApi(argbBuf, yuv420sp, width, height);
        }
    }

    public static void saveYuvToJpeg(File jpegFile, int[] argbBuf,
            byte[] yuv420sp, int width, int height, int format, int quality) {
        if (VerUtil.sdkSupport(VerUtil.FROYO)) {
            saveYuvToJpeg_newApi(jpegFile, yuv420sp, width, height, format,
                    quality);
        } else {
            saveYuvToJpeg_oldApi(jpegFile, argbBuf, yuv420sp, width, height,
                    quality);
        }
    }
}