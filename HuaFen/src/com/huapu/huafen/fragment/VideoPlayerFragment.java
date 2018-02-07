package com.huapu.huafen.fragment;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.facebook.drawee.view.SimpleDraweeView;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.VBanner;
import com.huapu.huafen.fragment.base.BaseFragment;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.ImageLoader;
import com.huapu.huafen.utils.LogUtil;

/**
 * Created by admin on 2016/12/12.
 */
public class VideoPlayerFragment extends BaseFragment implements
        TextureView.SurfaceTextureListener,
        View.OnClickListener,
        AudioManager.OnAudioFocusChangeListener {

    public final static String TAG = VideoPlayerFragment.class.getSimpleName();
    private FrameLayout flRoot;
    private static final int WHAT_CREATED_BITMAP = 1;
    private TextureView textureView;
    private MediaPlayer mediaPlayer;
    private Surface surface;
    private ImageView ivPlay;
    private SimpleDraweeView videoImage;
    private VBanner banner;
    private FrameLayout flContainer;
    private ProgressBar progressBar;
    private AudioManager mAudioManager;
    private boolean isTextureAvailableFirst = true;
    private boolean isCreated;
    private boolean isPlay = true;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtil.e(TAG, "onCreate");
        isCreated = true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LogUtil.e(TAG, "onCreateView");
        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup container) {
        return inflater.inflate(R.layout.fragment_video_player_layout, container, false);
    }


    @Override
    public void onViewCreated(View root) {
        super.onViewCreated(root);
        mAudioManager = (AudioManager) getContext().getSystemService(getContext().AUDIO_SERVICE);

        flRoot = (FrameLayout) root.findViewById(R.id.flRoot);
        flRoot.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (onRootViewClick != null) {
                    onRootViewClick.onClick();
                }
            }
        });
        flContainer = (FrameLayout) root.findViewById(R.id.flContainer);

        textureView = (TextureView) root.findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(this);//设置监听函数  重写4个方法
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) flContainer.getLayoutParams();
        layoutParams.width = layoutParams.height = CommonUtils.getScreenWidth();
        textureView.setOnClickListener(this);
        videoImage = (SimpleDraweeView) root.findViewById(R.id.ivVideo);
        ivPlay = (ImageView) root.findViewById(R.id.ivPlay);
        ivPlay.setOnClickListener(this);
        progressBar = (ProgressBar) root.findViewById(R.id.progressBar);
    }


    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_CREATED_BITMAP:
                    Bitmap bmp = (Bitmap) msg.obj;
                    videoImage.setImageBitmap(bmp);
                    break;
                default:
                    break;
            }
            return true;
        }
    });

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtil.e(TAG, "onActivityCreated");
        banner = (VBanner) getArguments().getSerializable("banner");
        audioStreamType = getArguments().getInt("audioStreamType");
        isPlay = getArguments().getBoolean("isPlay", true);
        if (banner == null) {
            return;
        }
        if (!TextUtils.isEmpty(banner.imgUrl)) {
            ImageLoader.loadImage(videoImage, banner.imgUrl);
        } else {//本地视频
            new Thread(new Runnable() {

                @Override
                public void run() {
                    Bitmap bitmap = CommonUtils.createVideoThumbnail(banner.videoPath);
                    Message msg = mHandler.obtainMessage(WHAT_CREATED_BITMAP, bitmap);
                    mHandler.sendMessage(msg);
                }
            }).start();
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        LogUtil.e(TAG, "onSurfaceTextureAvailable");
        surface = new Surface(surfaceTexture);
        initMediaPlayer();
        if (CommonUtils.getNetState(getActivity()) == CommonUtils.TYPE_WIFI && isTextureAvailableFirst && isPlay) {
            if (ivPlay != null) {
                ivPlay.setVisibility(View.GONE);
            }
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
            }

            PlayThread playThread = new PlayThread();
            playThread.setAutoPlay(true);
            playThread.start();
        }
        isTextureAvailableFirst = false;
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
        LogUtil.e(TAG, "onSurfaceTextureDestroyed");
        surfaceTexture = null;
        surface = null;
        stop();
        mediaPlayer.release();
        mediaPlayer = null;
        ivPlay.setVisibility(View.VISIBLE);
        videoImage.setVisibility(View.VISIBLE);
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    private int audioStreamType = AudioManager.STREAM_MUSIC;

    public void initMediaPlayer() {
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(banner.videoPath);
            mediaPlayer.setSurface(surface);
            mediaPlayer.setAudioStreamType(audioStreamType);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                @Override
                public void onPrepared(MediaPlayer mp) {
                    LogUtil.e(TAG, "onPrepared");
                    if (audioStreamType == AudioManager.STREAM_VOICE_CALL) {
                        mp.setVolume(0f, 0f);
                    }

                    try {
                        mp.start();
                    } catch (IllegalStateException e) {

                    }
                    videoImage.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    LogUtil.e(TAG, "onCompletion");
                    try {
                        mp.stop();
                    } catch (IllegalStateException e) {

                    }

                    ivPlay.setVisibility(View.VISIBLE);
                    videoImage.setVisibility(View.VISIBLE);
                }
            });

            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    LogUtil.e(TAG, "what:" + what + "");
                    try {
                        mp.stop();
                    } catch (IllegalStateException e) {

                    }
                    if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
                        progressBar.setVisibility(View.GONE);
                    }
                    if (ivPlay != null) {
                        ivPlay.setVisibility(View.VISIBLE);
                    }

                    if (videoImage != null) {
                        videoImage.setVisibility(View.VISIBLE);
                    }

                    return false;
                }
            });

        } catch (Exception e) {
            try {
                mediaPlayer.stop();
            } catch (IllegalStateException exception) {

            }
            LogUtil.e(TAG, e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.ivPlay) {
            ivPlay.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            new PlayThread().start();
        } else if (v.getId() == R.id.textureView) {
            LogUtil.e(TAG, "onClick:" + "R.id.textureView");
            stop();
            if (onViewClick != null) {
                onViewClick.onClick();
            }
        }
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            // 你已经完全获得了音频焦点
            case AudioManager.AUDIOFOCUS_GAIN:
                break;
            //你会长时间的失去焦点，所以不要指望在短时间内能获得。
            // 请结束自己的相关音频工作并做好收尾工作。
            // 比如另外一个音乐播放器开始播放音乐了
            // （前提是这个另外的音乐播放器他也实现了音频焦点的控制，baidu音乐，天天静听很遗憾的就没有实现，所以他们两个是可以跟别的播放器同时播放的）
            case AudioManager.AUDIOFOCUS_LOSS:
                LogUtil.e(TAG, "AudioManager.AUDIOFOCUS_LOSS");
                stop();
                break;
            // 你会短暂的失去音频焦点，你可以暂停音乐，但不要释放资源，因为你一会就可以夺回焦点并继续使用
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                break;
            //你的焦点会短暂失去，但是你可以与新的使用者共同使用音频焦点
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                break;
        }
    }

    public class PlayThread extends Thread {

        private boolean autoPlay;

        public void setAutoPlay(boolean autoPlay) {
            this.autoPlay = autoPlay;
        }

        @Override
        public void run() {
            play(autoPlay);
        }
    }

    private void play(boolean autoPlay) {
        LogUtil.e(TAG, "play");
        try {
            if (mediaPlayer.isPlaying()) {
                return;
            }
            if (!autoPlay) {
                mAudioManager.requestAudioFocus(this, audioStreamType, AudioManager.AUDIOFOCUS_GAIN);
            }
            mediaPlayer.prepare();
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
            handler.sendEmptyMessage(WHAT_PLAY_FAILED);
        }
    }

    private static final int WHAT_PLAY_FAILED = -1;

    private Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_PLAY_FAILED:
                    toast("播放失败");
                    LogUtil.e(TAG, "播放失败");
                    stop();
                    break;
                default:
                    break;
            }
            return true;
        }
    });

    private void stop() {
        if (progressBar != null && progressBar.getVisibility() == View.VISIBLE) {
            progressBar.setVisibility(View.GONE);
        }
        try {
            if (!mediaPlayer.isPlaying()) {
                return;
            }
            mediaPlayer.stop();
            LogUtil.e(TAG, "stopPlay");
            ivPlay.setVisibility(View.VISIBLE);
            videoImage.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
        }
    }

    @Override
    public void setUserVisibleHint(boolean userVisibleHint) {
        if (!isCreated) {
            return;
        }
        super.setUserVisibleHint(userVisibleHint);
        LogUtil.e(TAG, "setUserVisibleHint>>>>>" + userVisibleHint);
        if (userVisibleHint == false) {
            stop();
        }
    }


    public interface OnViewClick {
        void onClick();
    }

    private OnViewClick onViewClick;

    public void setOnViewClick(OnViewClick onViewClick) {
        this.onViewClick = onViewClick;
    }


    public interface OnRootViewClick {
        void onClick();
    }

    private OnRootViewClick onRootViewClick;

    public void setOnRootViewClick(OnRootViewClick onRootViewClick) {
        this.onRootViewClick = onRootViewClick;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.e(TAG, "onDestroy");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtil.e(TAG, "onDestroyView");
    }


    @Override
    public void onStop() {
        super.onStop();
        LogUtil.e(TAG, "onStop");
        stop();
    }
}