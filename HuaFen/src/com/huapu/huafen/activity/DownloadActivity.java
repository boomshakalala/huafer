package com.huapu.huafen.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huapu.huafen.R;
import com.huapu.huafen.common.MyConstants;
import com.huapu.huafen.http.ProgressDownloader;
import com.huapu.huafen.http.ProgressResponseBody;
import com.huapu.huafen.utils.FileUtils;
import com.huapu.huafen.utils.LogUtil;
import com.huapu.huafen.utils.ToastUtil;

import java.io.File;

/**
 * Created by Administrator on 2016/10/24.
 */
public class DownloadActivity extends BaseActivity implements ProgressResponseBody.ProgressListener {

    public static final String TAG = "DownloadActivity";
//    public static final String PACKAGE_URL = "http://imgs.huafer.cc/download/huafer.apk";
    ProgressBar progressBar;
    private long breakPoints;
    private ProgressDownloader downloader;
    private File file;
    private long totalBytes;
    private long contentLength;
    private TextView tvDialogContent, tvDialogTitle;
    private Button btnDialogLeft, btnDialogRight;
    private String appDownloadUrl;
    private String appContent;
    private int appLevel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        if(getIntent().hasExtra(MyConstants.EXTRA_APP_LEVEL)) {
            appLevel = getIntent().getIntExtra(MyConstants.EXTRA_APP_LEVEL, 0);
        }
        if(getIntent().hasExtra(MyConstants.EXTRA_APP_URL)) {
            appDownloadUrl = getIntent().getStringExtra(MyConstants.EXTRA_APP_URL);
        }
        if(getIntent().hasExtra(MyConstants.EXTRA_APP_CONTENT)) {
            appContent = getIntent().getStringExtra(MyConstants.EXTRA_APP_CONTENT);
        }
        tvDialogContent = (TextView) findViewById(R.id.tvDialogContent);
        tvDialogTitle = (TextView) findViewById(R.id.tvDialogTitle);
        btnDialogLeft = (Button) findViewById(R.id.btnDialogLeft);
        btnDialogRight = (Button) findViewById(R.id.btnDialogRight);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tvDialogContent.setMovementMethod(new ScrollingMovementMethod());
        btnDialogLeft.setOnClickListener(this);
        btnDialogRight.setOnClickListener(this);

        if(appLevel == 3) {
            btnDialogLeft.setVisibility(View.GONE);
        }
        tvDialogContent.setText(appContent);
        tvDialogTitle.setText("版本更新");
        btnDialogLeft.setText("取消");
        btnDialogRight.setText("确定");
    }

    private boolean isDownload = false;
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnDialogLeft:
                finish();
                break;
            case R.id.btnDialogRight:
                if(!isDownload) {
                    if(TextUtils.isEmpty(appDownloadUrl)) {
                        tvDialogContent.setText("下载地址异常，请返回重试。");
                        return;
                    }
                    isDownload = true;
                    tvDialogTitle.setText("下载中");
                    progressBar.setVisibility(View.VISIBLE);
                    tvDialogContent.setVisibility(View.GONE);
                    btnDialogLeft.setVisibility(View.GONE);
                    // 新下载前清空断点信息
                    breakPoints = 0L;
                    if(FileUtils.isSDCardExist()) {
//                        file = new File("/sdcard", "huafen.apk");
//                        file = new File(FileUtils.getUpdateDir(), "huafen.apk");
                        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "huafen.apk");
                        if(file.exists()) {
                            file.delete();
                        }
                        downloader = new ProgressDownloader(appDownloadUrl, file, this);
                        downloader.download(0L);
                    } else {
                        toast("请安装SD卡");
                        finish();
                    }
                } else {
                    toast("下载中，请不要重复点击");
                }
                break;

//            case R.id.pause_button: // 暂停
//                downloader.pause();
//                Toast.makeText(this, "下载暂停", Toast.LENGTH_SHORT).show();
//                // 存储此时的totalBytes，即断点位置。
//                breakPoints = totalBytes;
//                break;
//            case R.id.continue_button: // 继续
//                downloader.download(breakPoints);
//                break;

        }
    }

    @Override
    public void onPreExecute(long contentLength) {
        // 文件总长只需记录一次，要注意断点续传后的contentLength只是剩余部分的长度
        if(contentLength <= 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                    tvDialogContent.setVisibility(View.VISIBLE);
                    btnDialogRight.setVisibility(View.GONE);
                    btnDialogLeft.setVisibility(View.VISIBLE);
                    btnDialogLeft.setText("关闭");
                    tvDialogContent.setText("文件已被损坏。");
                }
            });
            return;
        }
        if (this.contentLength == 0L) {
            this.contentLength = contentLength;
            progressBar.setMax((int) (contentLength / 1024));
        }
    }

    @Override
    public void update(long totalBytes, boolean done) {
        // 注意加上断点的长度
        this.totalBytes = totalBytes + breakPoints;
        LogUtil.e("liang", "total:"+(totalBytes + breakPoints));
        LogUtil.e("liang", "done:"+done);
        progressBar.setProgress((int) (totalBytes + breakPoints) / 1024);
        if (done) {//开始安装
            if(file != null && file.isFile()) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                startActivity(intent);
            } else {
                // 切换到主线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.toast(DownloadActivity.this, "文件异常，请退出重试");
                    }
                });
            }
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if(appLevel < 3) {
            super.onBackPressed();
        }
    }
}
