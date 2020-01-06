package com.novv.dzdesk.video.op.model;

import com.novv.dzdesk.util.PathUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;

public class MusicItem implements Serializable {

    private static final long serialVersionUID = -8610402265965561358L;

    public String id;
    public String name;
    public String cover;
    public String music;

    public MusicItem(JSONObject jsonObject) {
        this.id = jsonObject.optString("id");
        this.name = jsonObject.optString("name");
        this.cover = jsonObject.optString("cover");
        this.music = jsonObject.optString("mp3url");
    }

    public String getFilePath() {
        String editVideoDir = PathUtils.getVideoEditMusicDir();
        File file = new File(editVideoDir, id + ".m4r");
        return file.getAbsolutePath();
    }

    public String getTempFilePath() {
        return getFilePath() + ".temp";
    }
}
