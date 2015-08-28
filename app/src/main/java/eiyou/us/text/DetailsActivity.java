package eiyou.us.text;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;
import com.tencent.connect.share.QQShare;
import com.tencent.tauth.Tencent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.v3.BmobQuery;
import eiyou.us.text.communication.CommentAdapter;
import eiyou.us.text.communication.CommentBean;
import eiyou.us.text.download.ShowDownload;
import eiyou.us.text.download.entities.FileInfo;
import eiyou.us.text.download.service.DownLoadService;
import eiyou.us.text.qqShare.BaseUiListener;
import eiyou.us.text.utils.Utils;
import eiyou.us.text.video.DensityUtil;
import eiyou.us.text.video.FullScreenVideoView;
import eiyou.us.text.video.LightnessController;
import eiyou.us.text.video.VideoMainActivity;
import eiyou.us.text.video.VolumnController;
import eiyou.us.text.yuyin.Config;
import eiyou.us.text.yuyin.Constants;

public class DetailsActivity extends Activity implements View.OnClickListener, MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {

    private ListView commentListView;
    // 自定义VideoView
    private FullScreenVideoView mVideo;

    // 头部View
    private View mTopView;
    //语音
    private BaiduASRDigitalDialog mDialog = null;
    private DialogRecognitionListener mRecognitionListener;
    private int mCurrentTheme = Config.DIALOG_THEME;


    // 底部View
    private View mBottomView;
    // 视频播放拖动条
    private SeekBar mSeekBar;
    private ImageView mPlay, fullscreenImageView, mdownload;
    private TextView mPlayTime;
    private TextView mDurationTime, loadRateView;

    // 音频管理器
    private AudioManager mAudioManager;

    // 屏幕宽高
    private float width;
    private float height;

    // 视频播放时间
    private int playTime;

    private String videoUrl;
    // 自动隐藏顶部和底部View的时间
    private static final int HIDE_TIME = 5000;

    // 声音调节Toast
    private VolumnController volumnController;

    // 原始屏幕亮度
    private int orginalLight;
    //intent
    Intent intent;
    String videoNameFromDown;
    //bmob
    BmobQuery<CommentBean> query;
    //download
    FileInfo fileInfo;
    //qqShare
    Tencent tencent;
//    exam
    WebView examVebView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);
        init();
        videoAction();
        event();
    }

    private void event() {
        fullscreenImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewGroup.LayoutParams lp;
                lp = mVideo.getLayoutParams();
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                lp.height = dm.heightPixels;
                lp.width = dm.widthPixels;
                mVideo.setLayoutParams(lp);
            }
        });
        new CommentAsyncTask().execute(query);

        mdownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.toast.show(getApplicationContext(), "正在下载");
                BroadcastReceiver MyReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        if (intent.getAction().equals(DownLoadService.ACTION_UPDATE)) {
                            int finished = intent.getIntExtra("finished", 0);
                        }
                    }
                };
                IntentFilter filter = new IntentFilter(DownLoadService.ACTION_UPDATE);
                registerReceiver(MyReceiver, filter);
                Intent intent = new Intent(getApplicationContext(), DownLoadService.class);
                intent.setAction(DownLoadService.ACTION_START);
                intent.putExtra("fileinfo", fileInfo);
                startService(intent);
            }
        });
        findViewById(R.id.share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qqShare();
            }
        });
        findViewById(R.id.share).getBackground().setAlpha(200);
        findViewById(R.id.b_finish).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
    }

    private void init() {
        fullscreenImageView = (ImageView) findViewById(R.id.iv_fullscreen);
        commentListView = (ListView) findViewById(R.id.lv_comment);
        intent = getIntent();
        videoUrl = intent.getStringExtra("videoUrl");
        fileInfo = new FileInfo(0,
                intent.getStringExtra("videoUrl"),
                intent.getStringExtra("videoName"),
                intent.getStringExtra("videoIcon"),
                intent.getStringExtra("videoContent"),
                0,
                0);
        videoNameFromDown=intent.getStringExtra("videoNameFromDown");
        tencent = Tencent.createInstance("1104828934", this.getApplicationContext());
        examVebView=(WebView)findViewById(R.id.wv_exam);
        examVebView.getSettings().setJavaScriptEnabled(true);
        examVebView.loadUrl("http://eiyou.us/mooc/exam.html");
    }

    private void videoAction() {
        volumnController = new VolumnController(this);
        mVideo = (FullScreenVideoView) findViewById(R.id.videoview);
        mPlayTime = (TextView) findViewById(R.id.play_time);
        mDurationTime = (TextView) findViewById(R.id.total_time);
        mPlay = (ImageView) findViewById(R.id.play_btn);
        mSeekBar = (SeekBar) findViewById(R.id.seekbar);
        loadRateView = (TextView) findViewById(R.id.tv_loadRateView);
        mTopView = findViewById(R.id.top_layout);
        mdownload = (ImageView) findViewById(R.id.iv_download);
        mBottomView = findViewById(R.id.bottom_layout);

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        width = DensityUtil.getWidthInPx(this);
        height = DensityUtil.getHeightInPx(this);
        threshold = DensityUtil.dip2px(this, 18);

        orginalLight = LightnessController.getLightness(this);

        mPlay.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);

        playVideo();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            height = DensityUtil.getWidthInPx(this);
            width = DensityUtil.getHeightInPx(this);
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            width = DensityUtil.getWidthInPx(this);
            height = DensityUtil.getHeightInPx(this);
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LightnessController.setLightness(this, orginalLight);
    }

    private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            mHandler.postDelayed(hideRunnable, HIDE_TIME);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mHandler.removeCallbacks(hideRunnable);
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            if (fromUser) {
                int time = progress * mVideo.getDuration() / 100;
                mVideo.seekTo(time);
            }
        }
    };

    private void backward(float delataX) {
        int current = mVideo.getCurrentPosition();
        int backwardTime = (int) (delataX / width * mVideo.getDuration());
        int currentTime = current - backwardTime;
        mVideo.seekTo(currentTime);
        mSeekBar.setProgress(currentTime * 100 / mVideo.getDuration());
        mPlayTime.setText(formatTime(currentTime));
    }

    private void forward(float delataX) {
        int current = mVideo.getCurrentPosition();
        int forwardTime = (int) (delataX / width * mVideo.getDuration());
        int currentTime = current + forwardTime;
        mVideo.seekTo(currentTime);
        mSeekBar.setProgress(currentTime * 100 / mVideo.getDuration());
        mPlayTime.setText(formatTime(currentTime));
    }

    private void volumeDown(float delatY) {
        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int down = (int) (delatY / height * max * 3);
        int volume = Math.max(current - down, 0);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        int transformatVolume = volume * 100 / max;
        volumnController.show(transformatVolume);
    }

    private void volumeUp(float delatY) {
        int max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int current = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int up = (int) ((delatY / height) * max * 3);
        int volume = Math.min(current + up, max);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        int transformatVolume = volume * 100 / max;
        volumnController.show(transformatVolume);
    }

    private void lightDown(float delatY) {
        int down = (int) (delatY / height * 255 * 3);
        int transformatLight = LightnessController.getLightness(this) - down;
        LightnessController.setLightness(this, transformatLight);
    }

    private void lightUp(float delatY) {
        int up = (int) (delatY / height * 255 * 3);
        int transformatLight = LightnessController.getLightness(this) + up;
        LightnessController.setLightness(this, transformatLight);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(0);
        mHandler.removeCallbacksAndMessages(null);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (mVideo.getCurrentPosition() > 0) {
                        mPlayTime.setText(formatTime(mVideo.getCurrentPosition()));
                        int progress = mVideo.getCurrentPosition() * 100 / mVideo.getDuration();
                        mSeekBar.setProgress(progress);
                        if (mVideo.getCurrentPosition() > mVideo.getDuration() - 100) {
                            mPlayTime.setText("00:00");
                            mSeekBar.setProgress(0);
                        }
                        mSeekBar.setSecondaryProgress(mVideo.getBufferPercentage());
                    } else {
                        mPlayTime.setText("00:00");
                        mSeekBar.setProgress(0);
                    }

                    break;
                case 2:
                    showOrHide();
                    break;

                default:
                    break;
            }
        }
    };

    private void playVideo() {
        //如果已经下载好
        try {
            if (ShowDownload.isExist(Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/.us.mooc/", fileInfo.getFilename())) {

                mVideo.setVideoPath(Environment.getExternalStorageDirectory()
                        .getAbsolutePath() + "/.us.mooc/" + fileInfo.getFilename());
            } else {
                mVideo.setVideoPath(videoUrl);
            }
        }catch (Exception e){
            mVideo.setVideoPath(
                    Environment.getExternalStorageDirectory()
                            .getAbsolutePath() + "/.us.mooc/"+videoNameFromDown+".mp4"
            );
            Log.e("eee",Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/.us.mooc/"+videoNameFromDown+".mp4");
        }

        mVideo.requestFocus();
        mVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mVideo.setVideoWidth(mp.getVideoWidth());
                mVideo.setVideoHeight(mp.getVideoHeight());

                mVideo.start();
                if (playTime != 0) {
                    mVideo.seekTo(playTime);
                }

                mHandler.removeCallbacks(hideRunnable);
                mHandler.postDelayed(hideRunnable, HIDE_TIME);
                mDurationTime.setText(formatTime(mVideo.getDuration()));
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        mHandler.sendEmptyMessage(1);
                    }
                }, 0, 1000);
            }
        });
        mVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlay.setImageResource(R.drawable.video_btn_down);
                mPlayTime.setText("00:00");
                mSeekBar.setProgress(0);
                examVebView.setVisibility(View.VISIBLE);
                findViewById(R.id.b_finish).setVisibility(View.VISIBLE);
            }
        });
    }

    private Runnable hideRunnable = new Runnable() {

        @Override
        public void run() {
            showOrHide();
        }
    };

    @SuppressLint("SimpleDateFormat")
    private String formatTime(long time) {
        DateFormat formatter = new SimpleDateFormat("mm:ss");
        return formatter.format(new Date(time));
    }

    private float mLastMotionX;
    private float mLastMotionY;
    private int startX;
    private int startY;
    private int threshold;
    private boolean isClick = true;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            final float x = event.getX();
            final float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mLastMotionX = x;
                    mLastMotionY = y;
                    startX = (int) x;
                    startY = (int) y;
                    break;
                case MotionEvent.ACTION_MOVE:
                    float deltaX = x - mLastMotionX;
                    float deltaY = y - mLastMotionY;
                    float absDeltaX = Math.abs(deltaX);
                    float absDeltaY = Math.abs(deltaY);
                    // 声音调节标识
                    boolean isAdjustAudio = false;
                    if (absDeltaX > threshold && absDeltaY > threshold) {
                        if (absDeltaX < absDeltaY) {
                            isAdjustAudio = true;
                        } else {
                            isAdjustAudio = false;
                        }
                    } else if (absDeltaX < threshold && absDeltaY > threshold) {
                        isAdjustAudio = true;
                    } else if (absDeltaX > threshold && absDeltaY < threshold) {
                        isAdjustAudio = false;
                    } else {
                        return true;
                    }
                    if (isAdjustAudio) {
                        if (x < width / 2) {
                            if (deltaY > 0) {
                                lightDown(absDeltaY);
                            } else if (deltaY < 0) {
                                lightUp(absDeltaY);
                            }
                        } else {
                            if (deltaY > 0) {
                                volumeDown(absDeltaY);
                            } else if (deltaY < 0) {
                                volumeUp(absDeltaY);
                            }
                        }

                    } else {
                        if (deltaX > 0) {
                            forward(absDeltaX);
                        } else if (deltaX < 0) {
                            backward(absDeltaX);
                        }
                    }
                    mLastMotionX = x;
                    mLastMotionY = y;
                    break;
                case MotionEvent.ACTION_UP:
                    if (Math.abs(x - startX) > threshold
                            || Math.abs(y - startY) > threshold) {
                        isClick = false;
                    }
                    mLastMotionX = 0;
                    mLastMotionY = 0;
                    startX = (int) 0;
                    if (isClick) {
                        showOrHide();
                    }
                    isClick = true;
                    break;

                default:
                    break;
            }
            return true;
        }

    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_btn:
                if (mVideo.isPlaying()) {
                    mVideo.pause();
                    mPlay.setImageResource(R.drawable.video_btn_down);
                } else {
                    mVideo.start();
                    mPlay.setImageResource(R.drawable.video_btn_on);
                }
                break;
            default:
                break;
        }
    }

    private void showOrHide() {
        if (mTopView.getVisibility() == View.VISIBLE) {
            mTopView.clearAnimation();
            Animation animation = AnimationUtils.loadAnimation(this,
                    R.anim.option_leave_from_top);
            animation.setAnimationListener(new AnimationImp() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    super.onAnimationEnd(animation);
                    mTopView.setVisibility(View.GONE);
                }
            });
            mTopView.startAnimation(animation);

            mBottomView.clearAnimation();
            Animation animation1 = AnimationUtils.loadAnimation(this,
                    R.anim.option_leave_from_bottom);
            animation1.setAnimationListener(new AnimationImp() {
                @Override
                public void onAnimationEnd(Animation animation) {
                    super.onAnimationEnd(animation);
                    mBottomView.setVisibility(View.GONE);
                }
            });
            mBottomView.startAnimation(animation1);
        } else {
            mTopView.setVisibility(View.VISIBLE);
            mTopView.clearAnimation();
            Animation animation = AnimationUtils.loadAnimation(this,
                    R.anim.option_entry_from_top);
            mTopView.startAnimation(animation);

            mBottomView.setVisibility(View.VISIBLE);
            mBottomView.clearAnimation();
            Animation animation1 = AnimationUtils.loadAnimation(this,
                    R.anim.option_entry_from_bottom);
            mBottomView.startAnimation(animation1);
            mHandler.removeCallbacks(hideRunnable);
            mHandler.postDelayed(hideRunnable, HIDE_TIME);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        loadRateView.setText(percent + "%");
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                //开始缓存，暂停播放
                if (mVideo.isPlaying()) {
                    mVideo.pause();
                    loadRateView.setText("");
                    loadRateView.setVisibility(View.VISIBLE);
                }
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                //缓存完成，继续播放

                mVideo.start();
                loadRateView.setVisibility(View.GONE);
                break;
        }
        return true;
    }

    private class AnimationImp implements Animation.AnimationListener {

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationStart(Animation animation) {
        }

    }

    class CommentAsyncTask extends AsyncTask<BmobQuery<CommentBean>, Void, List<CommentBean>> {

        @Override
        protected List<CommentBean> doInBackground(BmobQuery<CommentBean>... params) {
            return getComment(params[0]);
        }

        @Override
        protected void onPostExecute(List<CommentBean> commentBeanList) {
            CommentAdapter commentAdapter = new CommentAdapter(getApplicationContext(), commentBeanList);
            commentListView.setAdapter(commentAdapter);
        }

        private List<CommentBean> getComment(BmobQuery<CommentBean> query) {
            List<CommentBean> commentBeanList = new ArrayList<>();
            CommentBean commentBean = new CommentBean(R.drawable.user_avatar, "jeoook", "蟹壳！", "12:34");

            query = new BmobQuery<>();
            for (int i = 0; i < 10; i++) {
                commentBeanList.add(commentBean);
            }
            return commentBeanList;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            mRecognitionListener = new DialogRecognitionListener() {
                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> rs = results != null ? results
                            .getStringArrayList(RESULTS_RECOGNITION) : null;
                    if (rs != null && rs.size() > 0) {
                        String heard = rs.get(0);
                        if (heard.indexOf("退出") >= 0) {
                            startActivity(new Intent(Intent.ACTION_MAIN).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addCategory(Intent.CATEGORY_HOME));
                        } else if (heard.indexOf("个人信息") >= 0) {
                            Log.e("ee", "个人信息");
                            startActivity(new Intent(getApplicationContext(), UserInfoActivity.class));
                        } else if (heard.indexOf("编辑") >= 0) {
                            startActivity(new Intent(getApplicationContext(), EditInfoActivity.class));
                        } else if (heard.indexOf("主页") >= 0) {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else if (heard.indexOf("下载") >= 0) {
                            startActivity(new Intent(getApplicationContext(), DownloadedActivity.class));
                        } else if (heard.indexOf("我们") >= 0) {
                            startActivity(new Intent(getApplicationContext(), AboutUsActivity.class));
                        }
                    }

                }
            };
            mCurrentTheme = Config.DIALOG_THEME;
            if (mDialog != null) {
                mDialog.dismiss();
            }
            Bundle params = new Bundle();
            params.putString(BaiduASRDigitalDialog.PARAM_API_KEY, Constants.API_KEY);
            params.putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY, Constants.SECRET_KEY);
            params.putInt(BaiduASRDigitalDialog.PARAM_DIALOG_THEME, Config.DIALOG_THEME);
            mDialog = new BaiduASRDigitalDialog(DetailsActivity.this, params);
            mDialog.setDialogRecognitionListener(mRecognitionListener);
//                }
            mDialog.getParams().putInt(BaiduASRDigitalDialog.PARAM_PROP, Config.CURRENT_PROP);
            mDialog.getParams().putString(BaiduASRDigitalDialog.PARAM_LANGUAGE,
                    Config.getCurrentLanguage());
            Log.e("DEBUG", "Config.PLAY_START_SOUND = " + Config.PLAY_START_SOUND);
            mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_START_TONE_ENABLE, Config.PLAY_START_SOUND);
            mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_END_TONE_ENABLE, Config.PLAY_END_SOUND);
            mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_TIPS_TONE_ENABLE, Config.DIALOG_TIPS_SOUND);
            mDialog.show();
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return true;
    }

    //    qq share
    private void qqShare() {
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, fileInfo.getFilename());
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, fileInfo.getVideoContent());
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,  fileInfo.getUrl());
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL,"http://eiyou.us/mooc/pic/icon.png");
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "us.mooc");
        tencent.shareToQQ(DetailsActivity.this, params, new BaseUiListener());
    }


}
