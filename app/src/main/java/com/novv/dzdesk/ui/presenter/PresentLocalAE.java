package com.novv.dzdesk.ui.presenter;

import android.os.Environment;
import com.ark.adkit.basics.handler.Action;
import com.ark.adkit.basics.handler.Run;
import com.ark.adkit.basics.utils.LogUtils;
import com.ark.baseui.XPresent;
import com.novv.dzdesk.res.model.VideoEntity;
import com.novv.dzdesk.ui.activity.user.UserAEMakeListActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PresentLocalAE extends XPresent<UserAEMakeListActivity> {

    private static List<VideoEntity> getLocalVideos(String fileAbsolutePath) {
        List<VideoEntity> videoEntities = new ArrayList<>();
        File file = new File(fileAbsolutePath);
        File[] files = file.listFiles();
        if (files != null) {
            for (File subDir : file.listFiles()) {
                if (subDir.isDirectory()) {
                    File[] subs = subDir.listFiles();
                    if (subs != null) {
                        for (File subFile : subs) {
                            if (!subFile.isDirectory()) {
                                String filename = subFile.getName();
                                VideoEntity videoEntity = new VideoEntity();
                                if (filename.startsWith("AEV_") && filename.endsWith(".mp4")) {
                                    videoEntity.setFileName(filename);
                                    videoEntity.setId(subFile.getParentFile().getName());
                                    videoEntity.setVideoPath(subFile.getAbsolutePath());
                                    videoEntities.add(videoEntity);
                                    LogUtils.i(
                                            "add local:" + filename + ",id=" + videoEntity.getId());
                                } else {
                                    subFile.delete();
                                }
                            }
                        }
                    }
                }
            }
        }
        return videoEntities;
    }

    public void loadUserMake(boolean isLoadMore) {
        if (isLoadMore) {
            if (hasV()) {
                getV().addMoreData(new ArrayList<VideoEntity>());
            }
        } else {
            Run.onBackground(new Action() {
                @Override
                public void call() {
                    final List<VideoEntity> list = getLocalVideos(
                            new File(Environment.getExternalStorageDirectory(),
                                    "avideoshare/ae/.output/").getAbsolutePath());
                    Collections.sort(list, new Comparator<VideoEntity>() {
                        @Override
                        public int compare(VideoEntity o1, VideoEntity o2) {
                            return o2.getFileName().compareTo(o1.getFileName());
                        }
                    });
                    Run.onUiAsync(new Action() {
                        @Override
                        public void call() {
                            if (hasV()) {
                                getV().setNewData(list);
                            }
                        }
                    });
                }
            });
        }
    }
}
