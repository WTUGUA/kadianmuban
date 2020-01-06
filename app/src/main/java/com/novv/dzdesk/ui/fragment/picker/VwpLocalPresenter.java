package com.novv.dzdesk.ui.fragment.picker;

import com.ark.adkit.basics.handler.Action;
import com.ark.adkit.basics.handler.Run;
import com.ark.adkit.basics.utils.LogUtils;
import com.ark.baseui.XPresent;
import com.novv.dzdesk.util.PathUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class VwpLocalPresenter extends XPresent<VwpLocalFragment> {

    private static List<String> getLocalVideos() {
        List<String> videoEntities = new ArrayList<>();
        File file = new File(PathUtils.getLwpDownloadDir());
        File[] files = file.listFiles();
        if (files != null) {
            for (File subFile : files) {
                if (!subFile.isDirectory()) {
                    String filename = subFile.getName();
                    LogUtils.i("fileName:" + filename);
                    if (filename.length() == "597b29c9e7bce756b71ca58c.mp4".length() && filename
                            .endsWith(".mp4")) {
                        videoEntities.add(subFile.getAbsolutePath());
                    } else {
                        subFile.delete();
                    }
                }
            }
        }
        return videoEntities;
    }

    public void loadDownloadVwp(boolean isLoadMore) {
        if (isLoadMore) {
            if (hasV()) {
                getV().addMoreData(new ArrayList<String>());
            }
        } else {
            if (hasV()) {
                getV().showProgress();
            }
            Run.onBackground(new Action() {
                @Override
                public void call() {
                    final List<String> list = getLocalVideos();
                    Collections.sort(list, new Comparator<String>() {
                        @Override
                        public int compare(String o1, String o2) {
                            return o2.compareTo(o1);
                        }
                    });
                    Run.onUiAsync(new Action() {
                        @Override
                        public void call() {
                            if (hasV()) {
                                getV().setNewData(list);
                                getV().hideProgress();
                            }
                        }
                    });
                }
            });
        }
    }
}
