package com.novv.dzdesk.res.model;

import com.chad.library.adapter.base.entity.AbstractExpandableItem;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.novv.dzdesk.ui.adapter.avatar.AdapterAvatarRcmd;

import java.io.Serializable;

public class Level0Item extends AbstractExpandableItem<Level1Item> implements MultiItemEntity,
        Serializable {

    public String title;
    public String day;
    public String month;
    public boolean isAd;

    public Level0Item(String title, String day, String month, boolean isAd) {
        this.title = title;
        this.day = day;
        this.month = month;
        this.isAd = isAd;
    }

    @Override
    public int getLevel() {
        return 0;
    }

    @Override
    public int getItemType() {
        return isAd ? AdapterAvatarRcmd.TYPE_AD : AdapterAvatarRcmd.TYPE_FOLDER;
    }
}
