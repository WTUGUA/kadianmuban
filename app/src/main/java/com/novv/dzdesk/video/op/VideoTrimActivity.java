package com.novv.dzdesk.video.op;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;
import com.novv.dzdesk.R;
import com.novv.dzdesk.util.DeviceUtil;
import com.novv.dzdesk.util.UmConst;
import com.novv.dzdesk.util.UmUtil;
import com.qiniu.pili.droid.shortvideo.PLMediaFile;
import com.qiniu.pili.droid.shortvideo.PLShortVideoTrimmer;
import com.qiniu.pili.droid.shortvideo.PLVideoFrame;
import com.qiniu.pili.droid.shortvideo.PLVideoSaveListener;
import java.io.File;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.novv.dzdesk.video.op.utils.RecordSettings.RECORD_SPEED_ARRAY;

public class VideoTrimActivity extends Activity {

  private static final String TAG = VideoTrimActivity.class.getSimpleName();
  private static final int SLICE_COUNT = 8;
  private PLShortVideoTrimmer mShortVideoTrimmer;
  private PLMediaFile mMediaFile;
  private LinearLayout mFrameListView;
  private View mHandlerLeft;
  private View mHandlerRight;
  private View mCoverLeft;
  private View mCoverRight;
  private CustomProgressDialog mProcessingDialog;
  private VideoView mPreview;
  private View mPreviewRl;
  private View mVideoOpV;
  private long mSelectedBeginMs = 0;
  private long mSelectedEndMs;
  private long mDurationMs;
  private int mSlicesTotalLength;
  private TextView mSpeedTextView;
  private MediaPlayer mMediaPlayer;
  private Handler mHandler = new Handler();

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mProcessingDialog = new CustomProgressDialog(this);
    mProcessingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
      @Override
      public void onCancel(DialogInterface dialog) {
        mShortVideoTrimmer.cancelTrim();
      }
    });
    Intent intent = getIntent();
    String videoPath = null;
    if (intent != null) {
      videoPath = intent.getStringExtra("data");
    }
    if (!TextUtils.isEmpty(videoPath) && new File(videoPath).exists()) {
      init(videoPath);
    } else {
      finish();
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    play();
  }

  @Override
  protected void onPause() {
    super.onPause();
    stopTrackPlayProgress();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (mShortVideoTrimmer != null) {
      mShortVideoTrimmer.destroy();
    }
    if (mMediaFile != null) {
      mMediaFile.release();
    }
  }

  private void startTrackPlayProgress() {
    stopTrackPlayProgress();
    mHandler.postDelayed(new Runnable() {
      @Override
      public void run() {
        if (mPreview.getCurrentPosition() >= mSelectedEndMs) {
          mPreview.seekTo((int) mSelectedBeginMs);
        }
        mHandler.postDelayed(this, 100);
      }
    }, 100);
  }

  private void stopTrackPlayProgress() {
    mHandler.removeCallbacksAndMessages(null);
  }

  private void play() {
    if (mPreview != null) {
      mPreview.seekTo((int) mSelectedBeginMs);
      mPreview.start();
      startTrackPlayProgress();
    }
  }

  public void onSpeedClicked(View view) {
    mSpeedTextView.setSelected(false);
    mSpeedTextView = (TextView) view;
    mSpeedTextView.setSelected(true);

    double recordSpeed = 1.0;
    switch (view.getId()) {
      case R.id.super_slow_speed_text:
        recordSpeed = RECORD_SPEED_ARRAY[0];
        break;
      case R.id.slow_speed_text:
        recordSpeed = RECORD_SPEED_ARRAY[1];
        break;
      case R.id.normal_speed_text:
        recordSpeed = RECORD_SPEED_ARRAY[2];
        break;
      case R.id.fast_speed_text:
        recordSpeed = RECORD_SPEED_ARRAY[3];
        break;
      case R.id.super_fast_speed_text:
        recordSpeed = RECORD_SPEED_ARRAY[4];
        break;
      default:
        break;
    }

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
      PlaybackParams params;
      params = mMediaPlayer.getPlaybackParams();
      params.setSpeed((float) recordSpeed);
      mMediaPlayer.setPlaybackParams(params);
      if (mPreviewRl.isSelected()) {
        mPreview.pause();
      }
    }
  }

  private void init(final String videoPath) {
    setContentView(R.layout.activity_trim);
    mSpeedTextView = findViewById(R.id.normal_speed_text);
    mSpeedTextView.setSelected(true);
    TextView durationStart = findViewById(R.id.duration_start);
    TextView durationEnd = findViewById(R.id.duration_end);
    mPreview = findViewById(R.id.preview);
    mVideoOpV = findViewById(R.id.video_op_iv);
    mPreviewRl = findViewById(R.id.preview_rl);
    mPreviewRl.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        view.setSelected(!view.isSelected());
        if (view.isSelected()) {
          mPreview.pause();
          mVideoOpV.setVisibility(View.VISIBLE);
        } else {
          mPreview.start();
          mVideoOpV.setVisibility(View.GONE);
        }
      }
    });

    mShortVideoTrimmer = new PLShortVideoTrimmer(this, videoPath, Config.TRIM_FILE_PATH);
    mMediaFile = new PLMediaFile(videoPath);

    mSelectedEndMs = mDurationMs = mMediaFile.getDurationMs();
    // duration.setText("时长: " + formatTime(mDurationMs));
    durationStart.setText(formatTime(0));
    durationEnd.setText(formatTime(mDurationMs));

    updateRangeText();
    Log.i(TAG, "video duration: " + mDurationMs);

    int mVideoFrameCount = mMediaFile.getVideoFrameCount(false);
    Log.i(TAG, "video frame count: " + mVideoFrameCount);

    mPreview.setVideoPath(videoPath);
    mPreview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
      @Override
      public void onPrepared(MediaPlayer mediaPlayer) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
          PlaybackParams params = null;
          params = mediaPlayer.getPlaybackParams();
          mediaPlayer.setPlaybackParams(params);
        }
        mMediaPlayer = mediaPlayer;
      }
    });

    initVideoFrameList();
  }

  private void initVideoFrameList() {
    mFrameListView = findViewById(R.id.video_frame_list);
    mHandlerLeft = findViewById(R.id.handler_left);
    mHandlerRight = findViewById(R.id.handler_right);
    mCoverLeft = findViewById(R.id.cover_left);
    mCoverRight = findViewById(R.id.cover_right);

    mHandlerLeft.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        float viewX = v.getX();
        float movedX = event.getX();
        float finalX = viewX + movedX;
        updateHandlerLeftPosition(finalX);

        if (action == MotionEvent.ACTION_UP) {
          calculateRange();
        }

        return true;
      }
    });

    mHandlerRight.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        int action = event.getAction();
        float viewX = v.getX();
        float movedX = event.getX();
        float finalX = viewX + movedX;
        updateHandlerRightPosition(finalX);

        if (action == MotionEvent.ACTION_UP) {
          calculateRange();
        }

        return true;
      }
    });

    mFrameListView.getViewTreeObserver()
        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
          @SuppressLint("StaticFieldLeak")
          @Override
          public void onGlobalLayout() {
            mFrameListView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

            final int sliceEdge = mFrameListView.getWidth() / SLICE_COUNT;
            mSlicesTotalLength = sliceEdge * SLICE_COUNT;
            Log.i(TAG, "slice edge: " + sliceEdge);
            final float px = TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2,
                    getResources().getDisplayMetrics());

            new AsyncTask<Void, PLVideoFrame, Void>() {
              @Override
              protected Void doInBackground(Void... v) {
                for (int i = 0; i < SLICE_COUNT; ++i) {
                  PLVideoFrame frame = mMediaFile
                      .getVideoFrameByTime(
                          (long) ((1.0f * i / SLICE_COUNT) * mDurationMs),
                          true,
                          sliceEdge, sliceEdge);
                  publishProgress(frame);
                }
                return null;
              }

              @Override
              protected void onProgressUpdate(PLVideoFrame... values) {
                super.onProgressUpdate(values);
                PLVideoFrame frame = values[0];
                if (frame != null) {
                  View root = LayoutInflater.from(VideoTrimActivity.this)
                      .inflate(R.layout.frame_item, null);

                  int rotation = frame.getRotation();
                  ImageView thumbnail = root
                      .findViewById(R.id.thumbnail);
                  thumbnail.setImageBitmap(frame.toBitmap());
                  thumbnail.setRotation(rotation);
                  FrameLayout.LayoutParams thumbnailLP = (FrameLayout.LayoutParams) thumbnail
                      .getLayoutParams();
                  if (rotation == 90 || rotation == 270) {
                    thumbnailLP.leftMargin = thumbnailLP.rightMargin = (int) px;
                  } else {
                    thumbnailLP.topMargin = thumbnailLP.bottomMargin = (int) px;
                  }
                  thumbnail.setLayoutParams(thumbnailLP);

                  LinearLayout.LayoutParams rootLP = new LinearLayout.LayoutParams(
                      sliceEdge,
                      sliceEdge);
                  mFrameListView.addView(root, rootLP);
                }
              }
            }.execute();
          }
        });
  }

  private void updateHandlerLeftPosition(float movedPosition) {
    RelativeLayout.LayoutParams coverLp = (RelativeLayout.LayoutParams) mCoverLeft
        .getLayoutParams();
    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mHandlerLeft
        .getLayoutParams();
    if ((movedPosition + mHandlerLeft.getWidth()) > mHandlerRight.getX()) {
      lp.leftMargin = (int) (mHandlerRight.getX() - mHandlerLeft.getWidth());
    } else if (movedPosition < 0) {
      lp.leftMargin = 0;
    } else {
      lp.leftMargin = (int) movedPosition;
    }
    coverLp.width = lp.leftMargin;
    mCoverLeft.setLayoutParams(coverLp);
    mHandlerLeft.setLayoutParams(lp);
  }

  private void updateHandlerRightPosition(float movedPosition) {
    RelativeLayout.LayoutParams coverLp = (RelativeLayout.LayoutParams) mCoverRight
        .getLayoutParams();
    RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mHandlerRight
        .getLayoutParams();
    lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
    lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
    if (movedPosition < (mHandlerLeft.getX() + mHandlerLeft.getWidth())) {
      lp.leftMargin = (int) (mHandlerLeft.getX() + mHandlerLeft.getWidth());
    } else if ((movedPosition + (mHandlerRight.getWidth() / 2)) > (mFrameListView.getX()
        + mSlicesTotalLength)) {
      lp.leftMargin = (int) ((mFrameListView.getX() + mSlicesTotalLength) - (
          mHandlerRight.getWidth() / 2));
    } else {
      lp.leftMargin = (int) movedPosition;
    }
    coverLp.width = DeviceUtil.getDisplayW(this) - lp.leftMargin - lp.width;
    mCoverRight.setLayoutParams(coverLp);
    mHandlerRight.setLayoutParams(lp);
  }

  private float clamp(float origin) {
    if (origin < 0) {
      return 0;
    }
    if (origin > 1) {
      return 1;
    }
    return origin;
  }

  private void calculateRange() {
    float beginPercent =
        1.0f * ((mHandlerLeft.getX() + mHandlerLeft.getWidth() / 2) - mFrameListView.getX())
            / mSlicesTotalLength;
    float endPercent =
        1.0f * ((mHandlerRight.getX() + mHandlerRight.getWidth() / 2) - mFrameListView
            .getX())
            / mSlicesTotalLength;
    beginPercent = clamp(beginPercent);
    endPercent = clamp(endPercent);

    Log.i(TAG, "begin percent: " + beginPercent + " end percent: " + endPercent);

    mSelectedBeginMs = (long) (beginPercent * mDurationMs);
    mSelectedEndMs = (long) (endPercent * mDurationMs);

    Log.i(TAG, "new range: " + mSelectedBeginMs + "-" + mSelectedEndMs);
    updateRangeText();
    play();
  }

  public void onDone(View v) {
    Log.i(TAG, "trim to file path: " + Config.TRIM_FILE_PATH + " range: " + mSelectedBeginMs
        + " - "
        + mSelectedEndMs);
    mProcessingDialog.setProgress(0);
    mProcessingDialog.setCancelable(true);
    mProcessingDialog.setCanceledOnTouchOutside(false);
    mProcessingDialog.show();
    UmUtil.anaEvent(this, UmConst.CLICK_VIDEO_SELECT_NEXT);
    PLShortVideoTrimmer.TRIM_MODE mode = PLShortVideoTrimmer.TRIM_MODE.ACCURATE;
    mShortVideoTrimmer.trim(mSelectedBeginMs, mSelectedEndMs, mode, new PLVideoSaveListener() {
      @Override
      public void onSaveVideoSuccess(String path) {
        mProcessingDialog.dismiss();
        VideoEditorActivity.launch(VideoTrimActivity.this, path);
      }

      @Override
      public void onSaveVideoFailed(final int errorCode) {
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            mProcessingDialog.dismiss();
            ToastUtils.toastErrorCode(VideoTrimActivity.this, errorCode);
          }
        });
      }

      @Override
      public void onSaveVideoCanceled() {
        mProcessingDialog.dismiss();
      }

      @Override
      public void onProgressUpdate(final float percentage) {
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            mProcessingDialog.setProgress((int) (100 * percentage));
          }
        });
      }
    });
  }

  public void onBack(View v) {
    finish();
  }

  private String formatTime(long timeMs) {
    return String.format(Locale.CHINA, "%02d:%02d",
        TimeUnit.MILLISECONDS.toMinutes(timeMs),
        TimeUnit.MILLISECONDS.toSeconds(timeMs) - TimeUnit.MINUTES.toSeconds(
            TimeUnit.MILLISECONDS.toMinutes(timeMs)));
  }

  private void updateRangeText() {
    TextView range = findViewById(R.id.range);
    range.setText("已选择" + ((int) ((mSelectedEndMs - mSelectedBeginMs) / 1000)) + "秒");
  }
}
