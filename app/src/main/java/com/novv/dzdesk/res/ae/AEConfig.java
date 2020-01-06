package com.novv.dzdesk.res.ae;

import android.content.Context;
import android.support.annotation.NonNull;
import com.novv.dzdesk.res.model.VModel;
import com.novv.dzdesk.ui.activity.ae.Utils;
import com.novv.dzdesk.util.PathUtils;

import java.io.File;
import java.io.Serializable;

public class AEConfig implements Serializable {

    //？
    private static final long serialVersionUID = 5526011629529500733L;
    public final VModel mVmodel;
    public String unZipDir;//解压缩的目录
    public String zipFilePath;//zip文件路径
    public String jsonPath;//json脚本路径
    public String imgDir;//图片解压缩目录
    public String bgVideoPath;//背景视频路径
    public String colorPath;//mv color.mp4
    public String maskPath;//mv mask.mp4
    public String watermark;//水印路径
    public String outMp4Path;
    public String previewPath;
    public String fontDir;//字体目录

    public AEConfig(@NonNull Context context, @NonNull VModel vModel) {
        this.mVmodel = vModel;
        String id = vModel.id;
        unZipDir = getUnzipDir();
        zipFilePath = new File(PathUtils.getDiyResDir(id), id + ".zip").getAbsolutePath();
        jsonPath = new File(unZipDir, "ae.json").getAbsolutePath();
        imgDir = new File(unZipDir, "images").getAbsolutePath();
        fontDir = new File(unZipDir, "fonts").getAbsolutePath();
        bgVideoPath = new File(unZipDir, "bg.mp4").getAbsolutePath();
        colorPath = new File(unZipDir, "color.mp4").getAbsolutePath();
        maskPath = new File(unZipDir, "mask.mp4").getAbsolutePath();
        watermark = PathUtils.getDiyWatermarkFilePath();
        Utils.copyFileFromAssets(context, "watermark.png", watermark);
        outMp4Path = resetOutMp4();
        previewPath = new File(unZipDir, "preview.mp4").getAbsolutePath();
    }

    /**
     * 输出MP4路径
     *
     * @return /avideoshare/ae/.output/{resId}/"AEV_" + {System.nanoTime()} + ".mp4")
     */
    public String resetOutMp4() {
        String dir = PathUtils.getDiyOutputDir(mVmodel.id);
        File outMp4 = new File(dir, "AEV_" + System.nanoTime() + ".mp4");
        outMp4Path = outMp4.getAbsolutePath();
        return outMp4Path;
    }

    /**
     * 解压目录
     *
     * @return /avideoshare/ae/.temp/{resId}/{resId}
     */
    private String getUnzipDir() {
        String dir = PathUtils.getDiyResDir(mVmodel.id);
        File file = new File(dir, mVmodel.id);
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.getAbsolutePath();
    }
}
