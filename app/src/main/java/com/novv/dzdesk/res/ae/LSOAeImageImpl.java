package com.novv.dzdesk.res.ae;

import com.lansosdk.LanSongAe.LSOAeImage;

import java.io.File;

public class LSOAeImageImpl {

    private final LSOAeImage mLSOAeImage;
    private int width;
    private int height;
    private String id;
    private String fileName;
    private String dirName;
    private String unzipDir;
    private String absFilePath;
    private boolean isChanged;

    public LSOAeImageImpl(LSOAeImage lsoAeImage, AEConfig aeConfig) {
        this.mLSOAeImage = lsoAeImage;
        this.width = lsoAeImage.getWidth();
        this.height = lsoAeImage.getHeight();
        this.id = lsoAeImage.getId();
        this.fileName = lsoAeImage.getFileName();
        this.dirName = lsoAeImage.getDirName();
        this.unzipDir = aeConfig.unZipDir;
        absFilePath = getOriginPath();
    }

    public LSOAeImage getLSOAeImage() {
        return mLSOAeImage;
    }

    public String getOriginPath() {
        return new File(unzipDir, dirName + "/" + fileName).getAbsolutePath();
    }

    /**
     * 设置新的图片
     *
     * @param absFilePath 图片路径
     */
    public void setNewPath(String absFilePath) {
        this.absFilePath = absFilePath;
        isChanged = true;
    }

    /**
     * 还原
     */
    public void reset() {
        this.absFilePath = getOriginPath();
        isChanged = false;
    }

    /**
     * 是否已替换图片
     *
     * @return true:已替换
     */
    public boolean isChanged() {
        return isChanged;
    }

    public String getAbsFilePath() {
        return absFilePath;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public String getId() {
        return this.id;
    }
}
