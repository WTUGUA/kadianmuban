package com.novv.dzdesk.ui.view.player;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.media.MediaPlayer.*;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;
import android.view.TextureView.SurfaceTextureListener;
import com.ark.adkit.basics.utils.LogUtils;

import java.io.IOException;
import java.util.Map;

@TargetApi(14)
public class TextureVideoView extends TextureView implements SurfaceTextureListener, Callback,
        OnPreparedListener, OnVideoSizeChangedListener, OnCompletionListener, OnErrorListener,
        OnInfoListener, OnBufferingUpdateListener {

    private static final int STATE_ERROR = -1;
    private static final int STATE_IDLE = 0;
    private static final int STATE_PREPARING = 1;
    private static final int STATE_PREPARED = 2;
    private static final int STATE_PLAYING = 3;
    private static final int STATE_PAUSED = 4;
    private static final int STATE_PLAYBACK_COMPLETED = 5;
    private static final int MSG_START = 1;
    private static final int MSG_PAUSE = 4;
    private static final int MSG_STOP = 6;
    private static final HandlerThread sThread = new HandlerThread("VideoPlayThread");

    static {
        sThread.start();
    }

    private volatile int mCurrentState;
    private volatile int mTargetState;
    private Uri mUri;
    private Context mContext;
    private Surface mSurface;
    private MediaPlayer mMediaPlayer;
    private AudioManager mAudioManager;
    private TextureVideoView.MediaPlayerCallback mMediaPlayerCallback;
    private Handler mHandler;
    private Handler mVideoHandler;
    private boolean mSoundMute;
    private boolean mHasAudio;
    private ScaleType mScaleType;
    private boolean loop;

    public TextureVideoView(Context context) {
        this(context, (AttributeSet) null);
    }

    public TextureVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TextureVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mCurrentState = STATE_IDLE;
        this.mTargetState = STATE_IDLE;
        this.mScaleType = ScaleType.NONE;
        if (attrs != null) {
//            TypedArray a = context.obtainStyledAttributes(attrs, styleable.scaleStyle, STATE_IDLE, STATE_IDLE);
//            if (a != null) {
//                int scaleType = a.getInt(styleable.scaleStyle_scaleType, ScaleType.NONE.ordinal());
            int scaleType = ScaleType.NONE.ordinal();
//                a.recycle();
            this.mScaleType = ScaleType.values()[scaleType];
            this.init();
//            }
        }
    }

    public void setMediaPlayerCallback(TextureVideoView.MediaPlayerCallback mediaPlayerCallback) {
        this.mMediaPlayerCallback = mediaPlayerCallback;
        if (mediaPlayerCallback == null) {
            this.mHandler.removeCallbacksAndMessages((Object) null);
        }
    }

    public int getCurrentPosition() {
        return this.isInPlaybackState() ? this.mMediaPlayer.getCurrentPosition() : 0;
    }

    public int getDuration() {
        return this.isInPlaybackState() ? this.mMediaPlayer.getDuration() : -1;
    }

    public int getVideoHeight() {
        return this.mMediaPlayer != null ? this.mMediaPlayer.getVideoHeight() : 0;
    }

    public int getVideoWidth() {
        return this.mMediaPlayer != null ? this.mMediaPlayer.getVideoWidth() : 0;
    }

    public ScaleType getScaleType() {
        return this.mScaleType;
    }

    public void setScaleType(ScaleType scaleType) {
        this.mScaleType = scaleType;
        this.scaleVideoSize(this.getVideoWidth(), this.getVideoHeight());
    }

    private void scaleVideoSize(int videoWidth, int videoHeight) {
        if (videoWidth != 0 && videoHeight != 0) {
            Size viewSize = new Size(this.getWidth(), this.getHeight());
            Size videoSize = new Size(videoWidth, videoHeight);
            ScaleManager scaleManager = new ScaleManager(viewSize, videoSize);
            final Matrix matrix = scaleManager.getScaleMatrix(this.mScaleType);
            if (matrix != null) {
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    this.setTransform(matrix);
                } else {
                    this.mHandler.postAtFrontOfQueue(new Runnable() {
                        public void run() {
                            TextureVideoView.this.setTransform(matrix);
                        }
                    });
                }

            }
        }
    }

    public boolean handleMessage(Message msg) {
        Class var2 = TextureVideoView.class;
        synchronized (var2) {
            switch (msg.what) {
                case MSG_START:
                    LogUtils.i("start Playing");
                    this.openVideo();
                    break;
                case MSG_PAUSE:
                    LogUtils.i("Pause Playing");
                    if (this.mMediaPlayer != null) {
                        this.mMediaPlayer.pause();
                    }
                    this.mCurrentState = STATE_PAUSED;
                    break;
                case MSG_STOP:
                    LogUtils.i("Stop Playing");
                    this.release(true);
            }
            return true;
        }
    }

    private void init() {
        if (!this.isInEditMode()) {
            this.mContext = this.getContext();
            this.mCurrentState = STATE_IDLE;
            this.mTargetState = STATE_IDLE;
            this.mHandler = new Handler();
            this.mVideoHandler = new Handler(sThread.getLooper(), this);
            this.setSurfaceTextureListener(this);
        }
    }

    private void release(boolean cleartargetstate) {
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.reset();
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
            this.mCurrentState = STATE_IDLE;
            if (cleartargetstate) {
                this.mTargetState = STATE_IDLE;
            }
        }
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    @SuppressLint("WrongConstant")
    private void openVideo() {
        if (this.mUri != null && this.mSurface != null && this.mTargetState == 3) {
            this.mAudioManager = (AudioManager) this.mContext.getSystemService("audio");
            this.mAudioManager.requestAudioFocus((OnAudioFocusChangeListener) null, 3, 1);
            this.release(false);
            try {
                this.mMediaPlayer = new MediaPlayer();
                this.mMediaPlayer.setOnPreparedListener(this);
                this.mMediaPlayer.setOnVideoSizeChangedListener(this);
                this.mMediaPlayer.setOnCompletionListener(this);
                this.mMediaPlayer.setOnErrorListener(this);
                this.mMediaPlayer.setOnInfoListener(this);
                this.mMediaPlayer.setOnBufferingUpdateListener(this);
                this.mMediaPlayer.setDataSource(this.mContext, this.mUri);
                this.mMediaPlayer.setSurface(this.mSurface);
                this.mMediaPlayer.setAudioStreamType(3);
                this.mMediaPlayer.setLooping(loop);
                this.mMediaPlayer.prepareAsync();
                this.mCurrentState = 1;
                this.mTargetState = 1;
                this.mHasAudio = true;
                if (VERSION.SDK_INT >= 16) {
                    try {
                        MediaExtractor mediaExtractor = new MediaExtractor();
                        mediaExtractor.setDataSource(this.mContext, this.mUri, (Map) null);
                        for (int i = 0; i < mediaExtractor.getTrackCount(); ++i) {
                            MediaFormat format = mediaExtractor.getTrackFormat(i);
                            String mime = format.getString("mime");
                            if (mime.startsWith("audio/")) {
                                this.mHasAudio = true;
                                break;
                            }
                        }
                    } catch (Exception var5) {
                        //
                    }
                }
            } catch (IllegalArgumentException | IOException var6) {
                this.mCurrentState = STATE_ERROR;
                this.mTargetState = STATE_ERROR;
                if (this.mMediaPlayerCallback != null) {
                    this.mHandler.post(new Runnable() {
                        public void run() {
                            if (TextureVideoView.this.mMediaPlayerCallback != null) {
                                TextureVideoView.this.mMediaPlayerCallback
                                        .onError(TextureVideoView.this.mMediaPlayer, 1, 0);
                            }
                        }
                    });
                }
            }
        }
    }

    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        this.mSurface = new Surface(surface);
        if (this.mTargetState == 3) {
            this.start();
        }
    }

    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        this.mSurface = null;
        this.stop();
        return true;
    }

    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    public void setVideoPath(String path) {
        this.setVideoURI(Uri.parse(path));
    }

    public void setVideoURI(Uri uri) {
        this.mUri = uri;
    }

    public void start() {
        this.mTargetState = STATE_PLAYING;
        if (this.isInPlaybackState()) {
            this.mVideoHandler.obtainMessage(MSG_STOP).sendToTarget();
        }
        if (this.mUri != null && this.mSurface != null) {
            this.mVideoHandler.obtainMessage(MSG_START).sendToTarget();
        }
    }

    public void pause() {
        this.mTargetState = STATE_PAUSED;
        if (this.isPlaying()) {
            this.mVideoHandler.obtainMessage(MSG_PAUSE).sendToTarget();
        }
    }

    public void resume() {
        this.mTargetState = STATE_PLAYING;
        if (!this.isPlaying()) {
            this.mVideoHandler.obtainMessage(MSG_START).sendToTarget();
        }
    }

    public void stop() {
        this.mTargetState = STATE_PLAYBACK_COMPLETED;
        if (this.isInPlaybackState()) {
            this.mVideoHandler.obtainMessage(MSG_STOP).sendToTarget();
        }
    }

    public boolean isPlaying() {
        return this.isInPlaybackState() && this.mMediaPlayer.isPlaying();
    }

    public void mute() {
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.setVolume(0.0F, 0.0F);
            this.mSoundMute = true;
        }
    }

    public void unMute() {
        if (this.mAudioManager != null && this.mMediaPlayer != null) {
            int max = 100;
            int audioVolume = 100;
            double numerator =
                    max - audioVolume > 0 ? Math.log((double) (max - audioVolume)) : 0.0D;
            float volume = (float) (1.0D - numerator / Math.log((double) max));
            this.mMediaPlayer.setVolume(volume, volume);
            this.mSoundMute = false;
        }
    }

    public boolean isMute() {
        return this.mSoundMute;
    }

    public boolean isHasAudio() {
        return this.mHasAudio;
    }

    private boolean isInPlaybackState() {
        return this.mMediaPlayer != null && this.mCurrentState != STATE_ERROR
                && this.mCurrentState != STATE_IDLE
                && this.mCurrentState != STATE_PREPARING;
    }

    public void onCompletion(final MediaPlayer mp) {
        LogUtils.i("onCompletion");
        this.mCurrentState = STATE_PLAYBACK_COMPLETED;
        this.mTargetState = STATE_PLAYBACK_COMPLETED;
        if (this.mMediaPlayerCallback != null) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    if (TextureVideoView.this.mMediaPlayerCallback != null) {
                        TextureVideoView.this.mMediaPlayerCallback.onCompletion(mp);
                    }
                }
            });
        }
    }

    public boolean onError(final MediaPlayer mp, final int what, final int extra) {
        LogUtils.i("onError");
        this.mCurrentState = STATE_ERROR;
        this.mTargetState = STATE_ERROR;
        if (this.mMediaPlayerCallback != null) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    if (TextureVideoView.this.mMediaPlayerCallback != null) {
                        TextureVideoView.this.mMediaPlayerCallback.onError(mp, what, extra);
                    }
                }
            });
        }
        return true;
    }

    public void onPrepared(final MediaPlayer mp) {
        LogUtils.i("onPrepared");
        if (this.mTargetState == STATE_PREPARING && this.mCurrentState == STATE_PREPARING) {
            this.mCurrentState = STATE_PREPARED;
            if (this.isInPlaybackState()) {
                this.mMediaPlayer.start();
                this.mCurrentState = STATE_PLAYING;
                this.mTargetState = STATE_PLAYING;
            }

            if (this.mMediaPlayerCallback != null) {
                this.mHandler.post(new Runnable() {
                    public void run() {
                        if (TextureVideoView.this.mMediaPlayerCallback != null) {
                            TextureVideoView.this.mMediaPlayerCallback.onPrepared(mp);
                        }
                    }
                });
            }
        }
    }

    public void onVideoSizeChanged(final MediaPlayer mp, final int width, final int height) {
        LogUtils.i("onVideoSizeChanged");
        this.scaleVideoSize(width, height);
        if (this.mMediaPlayerCallback != null) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    if (TextureVideoView.this.mMediaPlayerCallback != null) {
                        TextureVideoView.this.mMediaPlayerCallback
                                .onVideoSizeChanged(mp, width, height);
                    }
                }
            });
        }
    }

    public void onBufferingUpdate(final MediaPlayer mp, final int percent) {
        if (this.mMediaPlayerCallback != null) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    if (TextureVideoView.this.mMediaPlayerCallback != null) {
                        TextureVideoView.this.mMediaPlayerCallback.onBufferingUpdate(mp, percent);
                    }
                }
            });
        }
    }

    public boolean onInfo(final MediaPlayer mp, final int what, final int extra) {
        if (this.mMediaPlayerCallback != null) {
            this.mHandler.post(new Runnable() {
                public void run() {
                    if (TextureVideoView.this.mMediaPlayerCallback != null) {
                        TextureVideoView.this.mMediaPlayerCallback.onInfo(mp, what, extra);
                    }
                }
            });
        }
        return true;
    }

    public interface MediaPlayerCallback {

        void onPrepared(MediaPlayer var1);

        void onCompletion(MediaPlayer var1);

        void onBufferingUpdate(MediaPlayer var1, int var2);

        void onVideoSizeChanged(MediaPlayer var1, int var2, int var3);

        boolean onInfo(MediaPlayer var1, int var2, int var3);

        boolean onError(MediaPlayer var1, int var2, int var3);
    }
}