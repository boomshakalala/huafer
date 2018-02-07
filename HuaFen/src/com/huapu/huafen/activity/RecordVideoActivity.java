package com.huapu.huafen.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huapu.CameraUtil;
import com.huapu.huafen.MyApplication;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.R;
import com.huapu.huafen.dialog.ProgressDialog;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.FileUtils;
import com.huapu.huafen.utils.LogUtil;

import org.ffmpeg.android.FFMPEGController;
import org.ffmpeg.android.ShellUtils;
import java.io.File;
import java.util.List;

/**
 * Created by admin on 2016/12/19.
 */
public class RecordVideoActivity extends BaseActivity implements SurfaceHolder.Callback, View.OnClickListener {

    public static final String TAG = RecordVideoActivity.class.getSimpleName();
    private TextView tvCancel;//取消
    private CheckBox chbFlash;//闪光灯
    private SurfaceView surfaceView;//视频
    private SurfaceHolder mHolder;
    private RelativeLayout rlBottom;//底部布局
    private ProgressBar progressBar;//进度条
    private TextView tvTip;//提示文案
    private ImageView ivRecord;//录制按钮
    private CheckBox chbCameraRevert;//前后摄像头翻转按钮
    private MediaRecorder mediaRecorder;
    private Camera mCamera;
    private FFMPEGController fc;
    private int screenWidth;
    private int screenHeight;
    private String pathName;
    private File file;
    private CamcorderProfile profile;
    private int recorderRotation;
    private int videoWidth;
    private int videoHeight;
    private int PROGRESS_MAX = 15000;
    //默认前置或者后置相机 0:后置 1:前置
    private int mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    private File file2;
    private File file3;
    private RelativeLayout rlTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        fc = MyApplication.getApplication().getFc();
        super.onCreate(savedInstanceState);
        screenWidth = CommonUtils.getScreenWidth();
        screenHeight = CommonUtils.getScreenHeight();
        setContentView(R.layout.activity_record);
        initView();
    }

    private void initView(){
        rlTitle = (RelativeLayout)findViewById(R.id.rlTitle);
        tvCancel = (TextView) findViewById(R.id.tvCancel);
        tvCancel.setOnClickListener(this);
        chbFlash = (CheckBox) findViewById(R.id.chbFlash);
        chbFlash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(mCamera!=null){
                    if(isChecked){
                        CameraUtil.turnLightOn(mCamera);
                    }else{
                        CameraUtil.turnLightOff(mCamera);
                    }
                }
            }
        });
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        mHolder = surfaceView.getHolder();
        mHolder.addCallback(this);
        rlBottom = (RelativeLayout) findViewById(R.id.rlBottom);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        progressBar.setMax(PROGRESS_MAX);
        progressBar.setSecondaryProgress(7000);
        tvTip = (TextView) findViewById(R.id.tvTip);
        tvTip.setText("视频至少要录制7\"");
        tvTip.setVisibility(View.VISIBLE);
        ivRecord = (ImageView) findViewById(R.id.ivRecord);
        ivRecord.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        chbFlash.setVisibility(View.INVISIBLE);
                        chbCameraRevert.setVisibility(View.INVISIBLE);
                        tvCancel.setVisibility(View.INVISIBLE);

                        stop = false;
                        lessThan = false;
                        ivRecord.setImageResource(R.drawable.recording_icon);
                        start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        chbFlash.setVisibility(View.VISIBLE);
                        chbCameraRevert.setVisibility(View.VISIBLE);
                        tvCancel.setVisibility(View.VISIBLE);

                        ivRecord.setImageResource(R.drawable.record_icon);
                        if(progressBar.getProgress()<7000){
                            progressBar.setProgress(0);
                            currentProgress = 0;
                            if(file!=null&&file.exists()){
                                file.delete();
                            }
                            tvTip.setVisibility(View.VISIBLE);
                            tvTip.setText("视频至少要录制7\"");
                            toast("视频至少要录制7\"");
                            lessThan = true;
                            return true;
                        }
                        stop = true;
                        lessThan = false;

                        break;
                    default:
                        break;

                }
                return true;
            }
        });
        chbCameraRevert = (CheckBox) findViewById(R.id.chbCameraRevert);
        if(CameraUtil.isSupportFrontCamera()){
            chbCameraRevert.setVisibility(View.VISIBLE);
            chbCameraRevert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    switchCamera();
                }
            });
        }else{
            chbCameraRevert.setVisibility(View.INVISIBLE);
        }
    }

    protected void start() {
        try {
            pathName = System.currentTimeMillis() + "";
            //视频存储路径
            file = new File(FileUtils.getTempPath() + File.separator + pathName + ".mp4");

            //如果没有要创建
            FileUtils.makeDir(file);

            //初始化一个MediaRecorder
            if (mediaRecorder == null) {
                mediaRecorder = new MediaRecorder();
            } else {
                mediaRecorder.reset();
            }

            mCamera.unlock();
            mediaRecorder.setCamera(mCamera);
            //设置视频输出的方向 很多设备在播放的时候需要设个参数 这算是一个文件属性
            mediaRecorder.setOrientationHint(recorderRotation);

            //视频源类型
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setAudioChannels(2);
            // 设置视频图像的录入源
            // 设置录入媒体的输出格式
//            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            // 设置音频的编码格式
//            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            // 设置视频的编码格式
//            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

            if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P)) {
                profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
            } /*else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_720P)) {
                profile = CamcorderProfile.get(CamcorderProfile.QUALITY_720P);
            } */ else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_1080P)) {
                profile = CamcorderProfile.get(CamcorderProfile.QUALITY_1080P);
            } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_HIGH)) {
                profile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
            } else if (CamcorderProfile.hasProfile(CamcorderProfile.QUALITY_LOW)) {
                profile = CamcorderProfile.get(CamcorderProfile.QUALITY_LOW);
            }

            if (profile != null) {
                profile.audioCodec = MediaRecorder.AudioEncoder.AAC;
                profile.audioChannels = 1;
                profile.audioSampleRate = 16000;

                profile.videoCodec = MediaRecorder.VideoEncoder.H264;
                mediaRecorder.setProfile(profile);
            }

            //视频尺寸
            mediaRecorder.setVideoSize(videoWidth, videoHeight);

            //数值越大 视频质量越高
            mediaRecorder.setVideoEncodingBitRate(2 * 1024 * 1024);

            // 设置视频的采样率，每秒帧数
//            mediaRecorder.setVideoFrameRate(5);

            // 设置录制视频文件的输出路径
            mediaRecorder.setOutputFile(file.getAbsolutePath());
            mediaRecorder.setMaxDuration(15*1000);

            // 设置捕获视频图像的预览界面
            mediaRecorder.setPreviewDisplay(surfaceView.getHolder().getSurface());

            mediaRecorder.setOnErrorListener(new MediaRecorder.OnErrorListener() {

                @Override
                public void onError(MediaRecorder mr, int what, int extra) {
                    // 发生错误，停止录制
                    if (mediaRecorder != null) {
                        try{
                            mediaRecorder.stop();
                        }catch (Exception e){

                        }

                        mediaRecorder.release();
                        mediaRecorder = null;
                    }
                }
            });

            mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {

                @Override
                public void onInfo(MediaRecorder mr, int what, int extra) {
                    //录制完成
                }
            });

            // 准备、开始
            mediaRecorder.prepare();
            mediaRecorder.start();

            handler.postDelayed(mRunnable,20);

        } catch (Exception e) {
            LogUtil.e(TAG,"1"+e.getMessage());
            e.printStackTrace();
        }
    }

    //更新录制进度的handler
    Handler handler = new Handler() ;

    private boolean lessThan;
    private boolean stop;
    private int currentProgress = 0;
    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            if(stop||progressBar.getProgress()>=PROGRESS_MAX){
                if (mediaRecorder != null) {
                    try {
                        mediaRecorder.stop();
                    }catch (Exception e){

                    }
                    mediaRecorder.release();
                    mediaRecorder = null;
                    releaseCamera();
                }
                compressThread(file.getName());
            }else{
                if(!lessThan){
                    currentProgress+=20;
                    progressBar.setProgress(currentProgress);
                    handler.postDelayed(mRunnable,20);
                    if(progressBar.getProgress()%1000==0){
                        tvTip.setVisibility(View.VISIBLE);
                        tvTip.setText(progressBar.getProgress()/1000+"\"");
                    }

                }

            }
        }
    };

    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case R.string.state_compress_end:
                    ProgressDialog.closeProgress();
                    currentProgress = 0;
                    progressBar.setProgress(0);
                    tvTip.setVisibility(View.GONE);
                    Intent intent = new Intent(RecordVideoActivity.this, UploadPreviewVideoActivity.class);
                    intent.putExtra("file1", file.getPath());
                    intent.putExtra("file2", file2.getPath());
                    intent.putExtra("file3", file3.getPath());
                    if (file3.length() > 0) {
                        intent.putExtra("VIDEO_PATH", file3.getPath());
                    } else if (file2.length() > 0) {
                        intent.putExtra("VIDEO_PATH", file2.getPath());
                    } else {
                        intent.putExtra("VIDEO_PATH", file.getPath());
                    }
                    intent.putExtra("TAG", TAG);
                    startActivityForResult(intent, MyConstants.REQUEST_CODE_FOR_VIDEO);
                    break;
                case R.string.state_compress:
                    ProgressDialog.showProgress(RecordVideoActivity.this, "视频正在压缩中，请耐心等待", false);
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            mCamera = getCamera(mCameraId);
            if (mHolder != null && mCamera != null) {
                //开启预览
                startPreview(mCamera, mHolder);
            }
        }

        if (progressBar != null) {
            progressBar.setProgress(0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }

    /**
     * 视频压缩
     *
     * @param fileName
     */
    private void compressThread(final String fileName) {
        new Thread() {
            @Override
            public void run() {
                compressSize();
            }

            /**
             * 视频压缩
             */
            private void compressSize() {
                // 准备压缩
                mHandler.sendEmptyMessage(R.string.state_compress);

                file2 = new File(FileUtils.getVideoPath(), fileName);

                FileUtils.makeDir(file2);

                try {
                    fc.compress_clipVideo(file.getAbsolutePath(),
                            file2.getAbsolutePath(), mCameraId, videoWidth, videoHeight, videoWidth-videoHeight, 0,
                            new ShellUtils.ShellCallback() {

                                @Override
                                public void shellOut(String shellLine) {

                                }

                                @Override
                                public void processComplete(int exitValue) {
                                    file3 = new File(FileUtils.getUploadPath(), fileName);
                                    FileUtils.makeDir(file3);
                                    try {
                                        fc.rotate(file2.getAbsolutePath(),
                                                file3.getAbsolutePath(), mCameraId, new ShellUtils.ShellCallback(){

                                                    @Override
                                                    public void shellOut(String shellLine) {

                                                    }

                                                    @Override
                                                    public void processComplete(int exitValue) {
                                                        mHandler.sendEmptyMessage(R.string.state_compress_end);
                                                    }
                                                });
                                    } catch (Exception e) {
                                        LogUtil.e(TAG,"4"+e.getMessage());
                                    }
                                }
                            });

                } catch (Exception e) {
                    LogUtil.e(TAG,"2"+e.getMessage());
                }
            }

        }.start();
    }

    /**
     * 释放相机资源
     */
    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
    /**
     * 获取Camera实例
     *
     * @return
     */
    private Camera getCamera(int id) {
        Camera camera = null;
        try {
            camera = Camera.open(id);
        } catch (Exception e) {

        }
        return camera;
    }

    public void switchCamera() {
        releaseCamera();
        mCameraId = (mCameraId + 1) % mCamera.getNumberOfCameras();
        mCamera = getCamera(mCameraId);
        if (mHolder != null) {
            startPreview(mCamera, mHolder);
        }
        if(mCameraId == 1){
            chbFlash.setChecked(false);
            chbFlash.setEnabled(false);
            if(mCamera!=null){
                CameraUtil.turnLightOff(mCamera);
            }
        }else{
            chbFlash.setEnabled(true);
        }

    }
    /**
     * 预览相机
     */
    private void startPreview(final Camera camera, final SurfaceHolder holder) {
        surfaceView.post(new Runnable() {

            public void run() {
                init(RecordVideoActivity.this, camera, holder);
            }
        });
    }

    /**
     * 设置
     */
    private void setupCamera(Camera camera) {
        if (camera != null) {
            Camera.Parameters parameters = camera.getParameters();

            List<String> focusModes = parameters.getSupportedFocusModes();
            if (focusModes != null && focusModes.size() > 0) {
                if (focusModes.contains(
                        Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
                    //设置自动对焦
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                }
            }

            List<Camera.Size> videoSiezes = null;
            if (parameters != null) {
                //获取相机所有支持尺寸
                videoSiezes = parameters.getSupportedVideoSizes();
                for (Camera.Size size : videoSiezes) {
                }
            }

            if (videoSiezes != null && videoSiezes.size() > 0) {
                //拿到一个预览宽度最小为720像素的预览值
                Camera.Size videoSize = CameraUtil.getInstance().getPropVideoSize(videoSiezes, 720);
                videoWidth = videoSize.width;
                videoHeight = videoSize.height;
            }

            //这里第三个参数为最小尺寸 getPropPreviewSize方法会对从最小尺寸开始升序排列 取出所有支持尺寸的最小尺寸
            Camera.Size previewSize = CameraUtil.getInstance().getPropPreviewSize(parameters.getSupportedPreviewSizes(), videoWidth);
            parameters.setPreviewSize(previewSize.width, previewSize.height);

            Camera.Size pictrueSize = CameraUtil.getInstance().getPropPictureSize(parameters.getSupportedPictureSizes(), videoWidth);
            parameters.setPictureSize(pictrueSize.width, pictrueSize.height);

            camera.setParameters(parameters);

            /**
             * 设置surfaceView的尺寸 因为camera默认是横屏，所以取得支持尺寸也都是横屏的尺寸
             * 我们在startPreview方法里面把它矫正了过来，但是这里我们设置设置surfaceView的尺寸的时候要注意 previewSize.height<previewSize.width
             * previewSize.width才是surfaceView的高度
             * 一般相机都是屏幕的宽度 这里设置为屏幕宽度 高度自适应 你也可以设置自己想要的大小
             */
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(screenWidth, (screenWidth * videoWidth) / videoHeight);
            //这里当然可以设置拍照位置 比如居中 我这里就置顶了
            surfaceView.setLayoutParams(params);

            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(screenWidth, screenHeight - screenWidth - rlTitle.getHeight() - statusBarHeight);
            layoutParams.addRule(RelativeLayout.BELOW, surfaceView.getId());
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            rlBottom.setLayoutParams(layoutParams);
        }
    }

    private int statusBarHeight;
    private void init(Activity activity, Camera camera, SurfaceHolder holder) {
        Rect rect = new Rect();
        Window window = activity.getWindow();
        rlTitle.getWindowVisibleDisplayFrame(rect);
        // 状态栏的高度
        statusBarHeight = rect.top;
//        // 标题栏跟状态栏的总体高度
//        int contentViewTop = window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
//        // 标题栏的高度：用上面的值减去状态栏的高度及为标题栏高度
//        int titleBarHeight = contentViewTop - statusBarHeight;
//        DisplayMetrics dm = getResources().getDisplayMetrics();
//        screenWidth = dm.widthPixels;
//        screenHeight = dm.heightPixels - contentViewTop;
        try {
            setupCamera(camera);
            camera.setPreviewDisplay(holder);
            //获取相机预览角度， 后面录制视频需要用
            recorderRotation = CameraUtil.getInstance().getRecorderRotation(mCameraId);
            CameraUtil.getInstance().setCameraDisplayOrientation(this, mCameraId, camera);
            camera.startPreview();
        } catch (Exception e) {
            LogUtil.e(TAG,"3"+e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        startPreview(mCamera, mHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if(mCamera != null) {
            try {
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
            }catch (Exception e){
                LogUtil.e(TAG,e.getMessage());
            }

            startPreview(mCamera, holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.tvCancel){
            onBackPressed();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == MyConstants.REQUEST_CODE_FOR_VIDEO){
                if(data!=null){
                    String mode = data.getStringExtra("mode");
                    if("upload".equals(mode)||"cancel".equals(mode)||"rerecord".equals(mode)){
                        setResult(RESULT_OK,data);
                        onBackPressed();
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(handler!=null){
            handler.removeCallbacks(mRunnable);
        }

        if(mediaRecorder!=null){
            mediaRecorder.release();
        }
    }
}
