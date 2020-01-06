package com.novv.dzdesk.res.model;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.novv.dzdesk.ui.adapter.avatar.AdapterAvatarRcmd;

import java.io.Serializable;

public class Level1Item implements MultiItemEntity, Serializable {

    public int parentPos;
    public long time;
    public int ncos;
    public String thumb;
    public int rank;
    public String name;
    public int view;
    public int favorites;
    public String id;
    public String desc;

    @Override
    public int getItemType() {
        return AdapterAvatarRcmd.TYPE_DATA;
    }
}
