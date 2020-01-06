package com.novv.dzdesk.ui.presenter;

import android.support.annotation.NonNull;
import com.ark.adkit.basics.handler.Action;
import com.ark.adkit.basics.handler.Run;
import com.ark.baseui.XPresent;
import com.lansosdk.videoeditor.LanSoEditor;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.novv.dzdesk.res.ae.AEConfig;
import com.novv.dzdesk.res.model.VModel;
import com.novv.dzdesk.ui.activity.ae.AEPreviewActivity;
import com.novv.dzdesk.ui.activity.ae.Utils;

public class PresentAePreview extends XPresent<AEPreviewActivity> {

    private boolean reDownload;

    @Override
    public void attachV(@NonNull AEPreviewActivity view) {
        super.attachV(view);
        LanSoEditor.initSDK(view, "gd_LanSongSDK_android4.key");
    }

    @Override
    public void detachV() {
        super.detachV();
      //  LanSoEditor.unInitSDK();
    }

    public void prepareResToEdit(@NonNull VModel vModel) {
        if (!hasV()) {
            return;
        }
        final AEConfig aeConfig = new AEConfig(getV(), vModel);
        FileDownloader.getImpl().create(vModel.zip)
                .setForceReDownload(reDownload)
                .setMinIntervalUpdateSpeed(200)
                .setPath(aeConfig.zipFilePath)
                .setListener(new FileDownloadSampleListener() {
                    @Override
                    protected void completed(final BaseDownloadTask task) {
                        super.completed(task);
                        reDownload = false;
                        Run.onBackground(new Action() {
                            @Override
                            public void call() {
                                try {
                                    Utils.UnZipFolder(aeConfig.zipFilePath, aeConfig.unZipDir);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Run.onUiAsync(new Action() {
                                    @Override
                                    public void call() {
                                        if (hasV()) {
                                            getV().hideProgressDialog();
                                            getV().onEditConfigPrepared(aeConfig);
                                        }
                                    }
                                });
                            }
                        });
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.progress(task, soFarBytes, totalBytes);
                        if (hasV()) {
                            getV().showProgress(soFarBytes * 100 / totalBytes);
                        }
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        super.error(task, e);
                        reDownload = true;
                        if (hasV()) {
                            getV().getVDelegate().toastShort("资源下载失败,无法制作，请重试");
                            getV().hideProgressDialog();
                        }
                    }
                }).start();
    }

    public void prepareRes(@NonNull VModel vModel) {
        if (!hasV()) {
            return;
        }
        final AEConfig aeConfig = new AEConfig(getV(), vModel);
        FileDownloader.getImpl().create(vModel.zip)
                .setForceReDownload(reDownload)
                .setPath(aeConfig.zipFilePath)
                .setMinIntervalUpdateSpeed(200)
                .setListener(new FileDownloadSampleListener() {

                    @Override
                    protected void completed(final BaseDownloadTask task) {
                        super.completed(task);
                        reDownload = false;
                        Run.onBackground(new Action() {
                            @Override
                            public void call() {
                                try {
                                    Utils.UnZipFolder(aeConfig.zipFilePath, aeConfig.unZipDir);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Run.onUiAsync(new Action() {
                                    @Override
                                    public void call() {
                                        if (hasV()) {
                                            getV().hideProgressDialog();
                                            getV().onResPrepared(aeConfig);
                                        }
                                    }
                                });
                            }
                        });

                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.progress(task, soFarBytes, totalBytes);
                        if (hasV()) {
                            getV().showProgress(soFarBytes * 100 / totalBytes);
                        }
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        super.error(task, e);
                        reDownload = true;
                        if (hasV()) {
                            getV().getVDelegate().toastShort("资源下载失败");
                            getV().hideProgressDialog();
                        }
                    }
                }).start();
    }
}
