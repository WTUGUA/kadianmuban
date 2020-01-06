package com.novv.dzdesk.res.ae;

import com.lansosdk.LanSongAe.LSOAeText;

public class LSOAeTextImpl {

    public String text;
    public String fontName;
    public double size;
    public int justification;
    public int tracking;
    public double lineHeight;
    public double baselineShift;
    public int color;
    public int strokeColor;
    public int strokeWidth;
    public boolean strokeOverFill;
    private LSOAeText mLSOAeText;
    private boolean isChanged;

    public LSOAeTextImpl(LSOAeText lsoAeText) {
        this.mLSOAeText = lsoAeText;
        this.text = lsoAeText.text;
        this.fontName = lsoAeText.fontName;
        this.size = lsoAeText.size;
        this.justification = lsoAeText.justification;
        this.tracking = lsoAeText.tracking;
        this.lineHeight = lsoAeText.lineHeight;
        this.baselineShift = lsoAeText.baselineShift;
        this.color = lsoAeText.color;
        this.strokeColor = lsoAeText.strokeColor;
        this.strokeWidth = lsoAeText.strokeWidth;
        this.strokeOverFill = lsoAeText.strokeOverFill;
    }

    public LSOAeText getLSOAeText() {
        return mLSOAeText;
    }

    public void setNewText(String text) {
        isChanged = true;
        this.text = text;
    }

    public boolean isChanged() {
        return isChanged;
    }
}
