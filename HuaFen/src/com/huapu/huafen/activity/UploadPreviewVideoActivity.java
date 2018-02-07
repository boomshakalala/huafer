package com.huapu.huafen.activity;

import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.huapu.huafen.R;
import com.huapu.huafen.beans.VBanner;
import com.huapu.huafen.dialog.DialogCallback;
import com.huapu.huafen.dialog.TextDialog;
import com.huapu.huafen.fragment.VideoPlayerFragment;
import com.huapu.huafen.utils.CommonUtils;
import com.huapu.huafen.utils.LogUtil;
import java.io.File;

/**
 * Created by admin on 2016/12/20.
 */
public class UploadPreviewVideoActivity extends BaseActivity {

    private final static String TAG = UploadPreviewVideoActivity.class.getSimpleName();
    private TextView tvCancel;
    private TextView tvDelete;
    private FrameLayout flVideoContainer;
    private TextView tvRerecord;
    private TextView tvUpload;
    private String videoPath ;
    private String path1;
    private String path2;
    private String path3;
    private String from;
    private String uploadMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_preview_layout);
        Intent intent = getIntent();
        if(intent.hasExtra("TAG")){
            from = intent.getStringExtra("TAG");
        }
        if(intent.hasExtra("uploadMode")){
            uploadMode = intent.getStringExtra("uploadMode");
        }
        if(intent.hasExtra("VIDEO_PATH")){
            videoPath = intent.getStringExtra("VIDEO_PATH");
        }
        if(intent.hasExtra("file1")){
            path1 = intent.getStringExtra("file1");
        }
        if(intent.hasExtra("file2")){
            path2 = intent.getStringExtra("file2");
        }
        if(intent.hasExtra("file3")){
            path3 = intent.getStringExtra("file3");
        }

        initView();
        if(TextUtils.isEmpty(videoPath)){
            final TextDialog dialog = new TextDialog(this,false);
            dialog.setContentText("找不到视频文件路径");
            dialog.setLeftText("确定");
            dialog.setLeftCall(new DialogCallback() {

                @Override
                public void Click() {
                    if(dialog != null) {
                        dialog.dismiss();
                    }
                    finish();
                }
            });
            dialog.show();
            return;
        }
    }


    private void initView(){
        tvCancel = (TextView) findViewById(R.id.tvCancel);
        tvDelete = (TextView) findViewById(R.id.tvDelete);
        flVideoContainer = (FrameLayout) findViewById(R.id.flVideoContainer);
        tvRerecord = (TextView) findViewById(R.id.tvRerecord);
        tvUpload = (TextView) findViewById(R.id.tvUpload);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)flVideoContainer.getLayoutParams();
        params.width = params.height = CommonUtils.getScreenWidth();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        if(fm.findFragmentByTag(VideoPlayerFragment.TAG)==null){
            Bundle bundle = new Bundle();
            VBanner vBanner = new VBanner(1,"");
            vBanner.videoPath = videoPath;
            bundle.putSerializable("banner",vBanner);
            bundle.putInt("audioStreamType", AudioManager.STREAM_MUSIC);
            VideoPlayerFragment videoPlayerFragment =new VideoPlayerFragment();
            videoPlayerFragment.setArguments(bundle);
            transaction.add(R.id.flVideoContainer,videoPlayerFragment);
            transaction.commitAllowingStateLoss();
        }

        tvCancel.setOnClickListener(this);
        tvDelete.setOnClickListener(this);
        tvRerecord.setOnClickListener(this);
        tvUpload.setOnClickListener(this);

        if(RecordVideoActivity.TAG.equals(from)){
            tvDelete.setVisibility(View.GONE);
            tvRerecord.setVisibility(View.VISIBLE);
            tvUpload.setVisibility(View.VISIBLE);
        }else if(ReleaseActivity.TAG.equals(from)){
            tvDelete.setVisibility(View.VISIBLE);
            tvRerecord.setVisibility(View.GONE);
            if("uploaded".equals(uploadMode)){
                tvUpload.setVisibility(View.GONE);
            }else if("upload_failed".equals(uploadMode)){
                tvUpload.setText("重新上传");
                tvUpload.setVisibility(View.VISIBLE);
            }

        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.tvCancel){//取消
            if(RecordVideoActivity.TAG.equals(from)){
                final TextDialog dialog = new TextDialog(this, false);
                dialog.setContentText("视频还没有上传，确定退出吗？");
                dialog.setLeftText("取消");
                dialog.setLeftCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        dialog.dismiss();
                    }
                });
                dialog.setRightText("确定");
                dialog.setRightCall(new DialogCallback() {

                    @Override
                    public void Click() {
                        try {
                            File file1 = new File(path1);
                            if(file1.exists()){
                                file1.delete();
                            }
                            File file2 = new File(path2);
                            if(file2.exists()){
                                file2.delete();
                            }
                            File file3 = new File(path3);
                            if(file3.exists()){
                                file3.delete();
                            }
                        }catch (Exception e){
                            LogUtil.e(TAG,e.getMessage());
                        }
                        Intent intent = new Intent();
                        intent.putExtra("mode","cancel");
                        intent.putExtra("videoPath",videoPath);
                        setResult(RESULT_OK,intent);
                        onBackPressed();
                    }
                });
                dialog.show();
            }else if(ReleaseActivity.TAG.equals(from)){
                onBackPressed();
            }

        }else if(v.getId() == R.id.tvRerecord){//重新录制
            try {
                File file1 = new File(path1);
                if(file1.exists()){
                    file1.delete();
                }
                File file2 = new File(path2);
                if(file2.exists()){
                    file2.delete();
                }
                File file3 = new File(path3);
                if(file3.exists()){
                    file3.delete();
                }
            }catch (Exception e){
                LogUtil.e(TAG,e.getMessage());
            }

            Intent intent = new Intent();
            intent.putExtra("mode","rerecord");
            setResult(RESULT_OK,intent);
            onBackPressed();
        }else if(v.getId() == R.id.tvDelete){//删除
            final TextDialog dialog = new TextDialog(this, false);
            dialog.setContentText("确认要删除视频吗？");
            dialog.setLeftText("取消");
            dialog.setLeftCall(new DialogCallback() {

                @Override
                public void Click() {
                    dialog.dismiss();
                }
            });
            dialog.setRightText("确定");
            dialog.setRightCall(new DialogCallback() {

                @Override
                public void Click() {
                    Intent intent = new Intent();
                    intent.putExtra("mode","delete");
                    try {
                        File file1 = new File(path1);
                        if(file1.exists()){
                            file1.delete();
                        }
                        File file2 = new File(path2);
                        if(file2.exists()){
                            file2.delete();
                        }
                        File file3 = new File(path3);
                        if(file3.exists()){
                            file3.delete();
                        }
                    }catch (Exception e){
                        LogUtil.e(TAG,e.getMessage());
                    }
                    setResult(RESULT_OK,intent);
                    onBackPressed();
                }
            });
            dialog.show();
        }else if(v.getId() == R.id.tvUpload){//上传
            Intent intent = new Intent();
            intent.putExtra("mode","upload");
            intent.putExtra("videoPath",videoPath);
            intent.putExtra("file1",path1);
            intent.putExtra("file2",path2);
            intent.putExtra("file3",path3);
            setResult(RESULT_OK,intent);
            onBackPressed();
        }
    }



}

