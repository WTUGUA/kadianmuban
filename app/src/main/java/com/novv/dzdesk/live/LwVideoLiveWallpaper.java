package com.novv.dzdesk.live;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.service.wallpaper.WallpaperService;
import android.support.annotation.Nullable;
import android.view.SurfaceHolder;
import com.novv.dzdesk.Const;
import com.novv.dzdesk.R;
import com.novv.dzdesk.util.CtxUtil;
import com.novv.dzdesk.util.LogUtil;
import com.novv.dzdesk.util.ToastUtil;
import com.novv.dzdesk.util.UmConst;
import com.novv.dzdesk.util.UmUtil;

public class LwVideoLiveWallpaper extends WallpaperService {

  public static final int REQUEST_CODE_SET_WALLPAPER = 3334;
  public static final String defaultMp4 = "dilireba.mp4";
  public static final String VIDEO_PARAMS_CONTROL_ACTION = "live_wp_video_controller";
  public static final String KEY_ACTION = "action";
  public static final int ACTION_VOICE_SILENCE = 110;
  public static final int ACTION_VOICE_NORMAL = 111;
  private static final String tag = LwVideoLiveWallpaper.class
      .getSimpleName();

  public static void voiceSilence(Context context) {
    Intent intent = new Intent(
        LwVideoLiveWallpaper.VIDEO_PARAMS_CONTROL_ACTION);
    intent.putExtra(LwVideoLiveWallpaper.KEY_ACTION,
        LwVideoLiveWallpaper.ACTION_VOICE_SILENCE);
    context.sendBroadcast(intent);
    LogUtil.i(tag, "sendBroadcast voiceSilence");
  }

  public static void voiceNormal(Context context) {
    Intent intent = new Intent(
        LwVideoLiveWallpaper.VIDEO_PARAMS_CONTROL_ACTION);
    intent.putExtra(LwVideoLiveWallpaper.KEY_ACTION,
        LwVideoLiveWallpaper.ACTION_VOICE_NORMAL);
    context.sendBroadcast(intent);
    LogUtil.i(tag, "sendBroadcast voiceNormal");
  }

  public static void setToWallPaper(@Nullable Activity activity) {
    if (activity == null || activity.isFinishing()) {
      return;
    }
    Context context = activity.getApplicationContext();
    if (!CtxUtil.isLwServiceRun(context)) {
      LogUtil.i(tag, "lw service is not run");
      LwPrefUtil.setLwpStarted(context, false);
    } else {
      LogUtil.i(tag, "lw service is run");
    }
    if (CtxUtil.isLwServiceRun(context)) {
      UmUtil.anaGeneralEventOp(context, UmConst.EVENT_OP_SET_LIVE_EXISTS);
      ToastUtil.showGeneralToast(context, "设置成功，回桌面看效果");
      CtxUtil.launchHome(context);
    } else {
      UmUtil.anaGeneralEventOp(context, UmConst.EVENT_OP_SET_LIVE_NOTEXISTS);
      try {
        final Intent intent = new Intent(
            WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
            new ComponentName(activity, LwVideoLiveWallpaper.class));
        activity.startActivityForResult(intent, REQUEST_CODE_SET_WALLPAPER);
      } catch (Exception e) {
        e.printStackTrace();
        ToastUtil.showToast(context, R.string.op_failed);
      } catch (Error e) {
        e.printStackTrace();
        ToastUtil.showToast(context, R.string.op_failed);
      }
    }
    boolean isLwOn = LwPrefUtil.isLwpVoiceOpened(context);
    if (!isLwOn) {
      LwVideoLiveWallpaper.voiceSilence(context);
    } else {
      LwVideoLiveWallpaper.voiceNormal(context);
    }
  }

  public Engine onCreateEngine() {
    LogUtil.i(tag, "onCreateEngine");
    return new VideoEngine();
  }

  class VideoEngine extends Engine {

    private String mp4FilePath = "";
    private MediaPlayer mMediaPlayer;

    private BroadcastReceiver mVideoParamsControlReceiver;

    @Override
    public void onCreate(SurfaceHolder surfaceHolder) {
      super.onCreate(surfaceHolder);
      LwPrefUtil.setLwpStarted(LwVideoLiveWallpaper.this, true);
      LogUtil.i(tag, "VideoEngine#onCreate");

      IntentFilter intentFilter = new IntentFilter(
          VIDEO_PARAMS_CONTROL_ACTION);
      registerReceiver(
          mVideoParamsControlReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
              LogUtil.i(tag, "onReceive");
              int action = intent.getIntExtra(KEY_ACTION, -1);
              switch (action) {
                case ACTION_VOICE_NORMAL:
                  setMediaPlayerNormal();
                  break;
                case ACTION_VOICE_SILENCE:
                  setMediaPlayerMute();
                  break;
                default:
                  break;
              }
            }
          }, intentFilter);
    }

    private void setMediaPlayerMute() {
      if (mMediaPlayer != null) {
        mMediaPlayer.setVolume(0, 0);
      }
    }

    private void setMediaPlayerNormal() {
      if (mMediaPlayer != null) {
        float volume = LwPrefUtil.getVideoVolume(LwVideoLiveWallpaper.this) / 100.0f;
        if (volume < 0) {
          volume = 1.0f;
        }
        LogUtil.i(tag, "setMediaPlayerNormal volume == " + volume);
        mMediaPlayer.setVolume(volume, volume);
      }
    }

    private void checkMediaPlayerVoice() {
      if (LwPrefUtil.isLwpVoiceOpened(getApplicationContext())) {
        setMediaPlayerNormal();
      } else {
        setMediaPlayerMute();
      }
    }

    @Override
    public void onDestroy() {
      LogUtil.i(tag, "VideoEngine#onDestroy");
      unregisterReceiver(mVideoParamsControlReceiver);
      LwPrefUtil.setLwpStarted(LwVideoLiveWallpaper.this, false);
      super.onDestroy();
    }

    @Override
    public void onVisibilityChanged(boolean visible) {
      LogUtil.i(tag, "VideoEngine#onVisibilityChanged visible = "
          + visible);
      if (mMediaPlayer == null) {
        return;
      }
      if (visible) {
        reloadMp4();
        mMediaPlayer.start();
      } else {
        mMediaPlayer.pause();
      }
    }

    public void reloadMp4() {
      try {
        String currentMp4Path = PrefUtil.getString(
            LwVideoLiveWallpaper.this, Const.PARAMS.LiveMp4Key,
            defaultMp4);
        if (mp4FilePath.equalsIgnoreCase(currentMp4Path)) {
          return;
        }
        mMediaPlayer.reset();
        mp4FilePath = currentMp4Path;
        LogUtil.i(tag, "mp4filepath = " + mp4FilePath);
        AssetManager assetMg = getApplicationContext().getAssets();
        if (mp4FilePath.equalsIgnoreCase(defaultMp4)) {
          AssetFileDescriptor fileDescriptor = assetMg
              .openFd(mp4FilePath);
          mMediaPlayer.setDataSource(
              fileDescriptor.getFileDescriptor(),
              fileDescriptor.getStartOffset(),
              fileDescriptor.getLength());
        } else {
          try {
            mMediaPlayer.setDataSource(mp4FilePath);
          } catch (Exception e) {
            e.printStackTrace();
          } catch (Error e) {
            e.printStackTrace();
          }
        }
        mMediaPlayer.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING);
        mMediaPlayer.setLooping(true);
        mMediaPlayer.setVolume(0, 0);
        mMediaPlayer.prepare();
        mMediaPlayer.start();

        checkMediaPlayerVoice();
      } catch (Exception e) {
        e.printStackTrace();
      } catch (Error e) {
        e.printStackTrace();
      }
    }

    @Override
    public void onSurfaceCreated(SurfaceHolder holder) {
      LogUtil.i(tag, "onSurfaceCreated visible = " + holder);
      super.onSurfaceCreated(holder);
      mMediaPlayer = new MediaPlayer();
      mMediaPlayer.setSurface(holder.getSurface());
      reloadMp4();
    }

    @Override
    public void onSurfaceChanged(SurfaceHolder holder, int format,
        int width, int height) {
      LogUtil.i(tag, "VideoEngine#onSurfaceChanged = " + holder);
      super.onSurfaceChanged(holder, format, width, height);
    }

    @Override
    public void onSurfaceDestroyed(SurfaceHolder holder) {
      LogUtil.i(tag, "VideoEngine#onSurfaceDestroyed = " + holder);
      super.onSurfaceDestroyed(holder);
      mMediaPlayer.release();
      mMediaPlayer = null;
    }
  }
}