package com.example.lamemp3;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;


public class MP3Recorder {

	private String mDir = null;
	private String mFilePath = null;
	private int sampleRate = 0;
	private boolean isRecording = false;
	private boolean isPause = false;
	private Handler handler = null;
	private int mVolume = 1;

	/**
	 * 开始录音
	 */
	public static final int MSG_REC_STARTED = 1;

	/**
	 * 结束录音
	 */
	public static final int MSG_REC_STOPPED = 2;

	/**
	 * 暂停录音
	 */
	public static final int MSG_REC_PAUSE = 3;

	/**
	 * 继续录音
	 */
	public static final int MSG_REC_RESTORE = 4;

	/**
	 * 缓冲区挂了,采样率手机不支持
	 */
	public static final int MSG_ERROR_GET_MIN_BUFFERSIZE = -1;

	/**
	 * 创建文件时扑街了
	 */
	public static final int MSG_ERROR_CREATE_FILE = -2;

	/**
	 * 初始化录音器时扑街了
	 */
	public static final int MSG_ERROR_REC_START = -3;

	/**
	 * 录音的时候出错
	 */
	public static final int MSG_ERROR_AUDIO_RECORD = -4;

	/**
	 * 编码时挂了
	 */
	public static final int MSG_ERROR_AUDIO_ENCODE = -5;

	/**
	 * 写文件时挂了
	 */
	public static final int MSG_ERROR_WRITE_FILE = -6;

	/**
	 * 没法关闭文件流
	 */
	public static final int MSG_ERROR_CLOSE_FILE = -7;

	public MP3Recorder(String dir) {
		this.sampleRate = 8000;
		this.mDir = dir;
	}


	public void stop() {
		isRecording = false;
	}

	public void pause() {
		isPause = true;
	}

	public void restore() {
		isPause = false;
	}

	public boolean isRecording() {
		return isRecording;
	}

	public boolean isPaus() {
		if (!isRecording) {
			return false;
		}
		return isPause;
	}

	public String getFilePath() {
		return mFilePath;
	}
	
	/**
	 * 获取分贝
	 * @return
	 */
	public int getVolume(){
		return mVolume;
	}

	/**
	 * 录音状态管理
	 * 
	 * @see RecMicToMp3#MSG_REC_STARTED
	 * @see RecMicToMp3#MSG_REC_STOPPED
	 * @see RecMicToMp3#MSG_REC_PAUSE
	 * @see RecMicToMp3#MSG_REC_RESTORE
	 * @see RecMicToMp3#MSG_ERROR_GET_MIN_BUFFERSIZE
	 * @see RecMicToMp3#MSG_ERROR_CREATE_FILE
	 * @see RecMicToMp3#MSG_ERROR_REC_START
	 * @see RecMicToMp3#MSG_ERROR_AUDIO_RECORD
	 * @see RecMicToMp3#MSG_ERROR_AUDIO_ENCODE
	 * @see RecMicToMp3#MSG_ERROR_WRITE_FILE
	 * @see RecMicToMp3#MSG_ERROR_CLOSE_FILE
	 */
	public void setHandle(Handler handler) {
		this.handler = handler;
	}

	// 以下为Native部分
	static {
		System.loadLibrary("mp3lame");
	}

	/**
	 * 初始化录制参数
	 */
	public static void init(int inSamplerate, int outChannel,
			int outSamplerate, int outBitrate) {
		init(inSamplerate, outChannel, outSamplerate, outBitrate, 7);
	}

	/**
	 * 初始化录制参数 quality:0=很好很慢 9=很差很快
	 */
	public native static void init(int inSamplerate, int outChannel,
			int outSamplerate, int outBitrate, int quality);

	/**
	 * 音频数据编码(PCM左进,PCM右进,MP3输出)
	 */
	public native static int encode(short[] buffer_l, short[] buffer_r,
			int samples, byte[] mp3buf);

	/**
	 * 刷干净缓冲区
	 */
	public native static int flush(byte[] mp3buf);

	/**
	 * 结束编码
	 */
	public native static void close();
}
