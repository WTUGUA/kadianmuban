package com.novv.dzdesk.player;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.SurfaceHolder;
import com.novv.dzdesk.util.LogUtil;

import java.lang.ref.WeakReference;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MediaManager implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener,
        MediaPlayer.OnSeekCompleteListener {

    private static final int STATE_PROGRESS = 111;
    private static final int STATE_ERROR = 112;
    private static final int STATE_COMPLETE = 113;
    private static final int STATE_PAUSE = 114;
    private static final int STATE_PREPARE = 115;
    private static final int STATE_STOP = 116;
    private static final int STATE_BUFFERING = 117;
    private MediaPlayer mMediaPlayer;// 播放器
    private OnMediaPlayerListener mOnMediaPlayerListener;
    private WeakHandler weakHandler;
    private ScheduledExecutorService mExecutor;
    private WeakReference<Activity> weakReference;
    private String filePath;

    public MediaManager(Activity activity) {
        weakReference = new WeakReference<>(activity);
        weakHandler = new WeakHandler(activity.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case STATE_COMPLETE:
                        if (mOnMediaPlayerListener != null) {
                            mOnMediaPlayerListener.onMediaPlayerComplete();
                        }
                        break;
                    case STATE_ERROR:
                        if (mOnMediaPlayerListener != null) {
                            mOnMediaPlayerListener.onMediaPlayerError();
                        }
                        break;
                    case STATE_PROGRESS:
                        int arg1 = msg.arg1;
                        if (mOnMediaPlayerListener != null) {
                            mOnMediaPlayerListener.onMediaPlayerProgress(arg1);
                        }
                        break;
                    case STATE_PAUSE:
                        if (mOnMediaPlayerListener != null) {
                            mOnMediaPlayerListener.onMediaPlayerPause();
                        }
                        break;
                    case STATE_STOP:
                        if (mOnMediaPlayerListener != null) {
                            mOnMediaPlayerListener.onMediaPlayerStop();
                        }
                        break;
                    case STATE_PREPARE:
                        if (mOnMediaPlayerListener != null) {
                            mOnMediaPlayerListener.onMediaPlayerPrepared();
                        }
                        break;
                    case STATE_BUFFERING:
                        if (mOnMediaPlayerListener != null) {
                            mOnMediaPlayerListener.onMediaPlayerBuffering(msg.arg1);
                        }
                        break;
                }
                return false;
            }
        });
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setWakeMode(activity.getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setOnSeekCompleteListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnInfoListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mExecutor = Executors.newScheduledThreadPool(10);
        mExecutor.scheduleAtFixedRate(new MyScheduledExecutor(), 500, 1, TimeUnit.MILLISECONDS);
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        if (mMediaPlayer != null) {
            mMediaPlayer.start();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (null != weakHandler) {
            weakHandler.sendEmptyMessage(STATE_COMPLETE);
        }
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
            if (null != weakHandler) {
                Message message = Message.obtain();
                message.what = STATE_BUFFERING;
                message.arg1 = extra;
                weakHandler.sendMessage(message);
            }
        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
            if (!mp.isPlaying() && null != weakHandler) {
                Message message = Message.obtain();
                message.what = STATE_BUFFERING;
                message.arg1 = extra;
                weakHandler.sendMessage(message);
            }
        }
        return false;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        if (null != weakHandler && what != -38) {
            LogUtil.e("mediaPlayer-->", what + "," + extra);
            weakHandler.sendEmptyMessage(STATE_ERROR);
        }
        return true;
    }

    public MediaManager resetPath(String filePath, @Nullable SurfaceHolder surfaceHolder) {
        if (TextUtils.equals(this.filePath, filePath)) {
            return this;
        }
        this.filePath = filePath;
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(filePath);
            if (surfaceHolder != null) {
                mMediaPlayer.setDisplay(surfaceHolder);
            }
            mMediaPlayer.prepareAsync();
            mMediaPlayer.setOnPreparedListener(null);
        } catch (Exception e) {
            if (null != weakHandler) {
                weakHandler.sendEmptyMessage(STATE_ERROR);
            }
        }
        return this;
    }

    public MediaManager resetPath(String filePath) {
        return resetPath(filePath, null);
    }

    public boolean isPlaying() {
        boolean isPlaying = false;
        try {
            isPlaying = mMediaPlayer != null && mMediaPlayer.isPlaying();
        } catch (Exception e) {
            //do nothing
        }
        return isPlaying;
    }

    public void start() {
        seekTo(0);
    }

    public void seekTo(int progress) {
        int msec;
        if (null != mMediaPlayer) {
            try {
                int total = mMediaPlayer.getDuration();
                msec = (int) (total / 100.0 * progress);
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        if (null != weakHandler) {
                            weakHandler.sendEmptyMessage(STATE_PREPARE);
                        }
                        mMediaPlayer.start();
                    }
                });
                mMediaPlayer.seekTo(Math.min(msec, total));
            } catch (Exception e) {
                if (null != weakHandler) {
                    weakHandler.sendEmptyMessage(STATE_ERROR);
                }
            }
        }
    }

    public void pause() {
        try {
            if (null != mMediaPlayer && isPlaying()) {
                mMediaPlayer.pause();
                if (weakHandler != null) {
                    weakHandler.sendEmptyMessage(STATE_PAUSE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            if (null != mMediaPlayer && isPlaying()) {
                mMediaPlayer.stop();
                mMediaPlayer.setOnPreparedListener(null);
                mMediaPlayer.prepareAsync();
                if (null != weakHandler) {
                    weakHandler.sendEmptyMessage(STATE_STOP);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void release() {
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
            }
            if (mExecutor != null) {
                mExecutor.shutdown();
            }
            if (weakReference != null) {
                weakReference.clear();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public MediaManager setOnMediaPlayerListener(OnMediaPlayerListener onMediaPlayerListener) {
        this.mOnMediaPlayerListener = onMediaPlayerListener;
        return this;
    }

    public interface OnMediaPlayerListener {

        void onMediaPlayerProgress(int progress);

        void onMediaPlayerComplete();

        void onMediaPlayerError();

        void onMediaPlayerPause();

        void onMediaPlayerStop();

        void onMediaPlayerBuffering(int percent);

        void onMediaPlayerPrepared();
    }

    private class MyScheduledExecutor implements Runnable {

        @Override
        public void run() {
            if (null != mMediaPlayer && null != weakHandler && isPlaying()) {
                int arg1 = mMediaPlayer.getCurrentPosition();
                int arg2 = mMediaPlayer.getDuration();
                int progress = (int) ((double) arg1 / arg2 * 100);
                Message message = Message.obtain();
                message.what = STATE_PROGRESS;
                message.arg1 = progress;
                if (progress > 0 && progress < 97) {
                    weakHandler.sendMessage(message);
                }
            }
        }
    }
}
