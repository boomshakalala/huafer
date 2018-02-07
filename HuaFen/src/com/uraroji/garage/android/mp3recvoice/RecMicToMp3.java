/* 
 * Copyright (c) 2011-2012 Yuichi Hirano
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.uraroji.garage.android.mp3recvoice;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidParameterException;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;

import com.example.lamemp3.MP3Recorder;
import com.huapu.huafen.utils.LogUtil;

/**
 * マイクから取得した音声をMP3に保存する
 * 
 * 別スレッドでマイクからの録音、MP3への変換を行う
 */
public class RecMicToMp3 {

	static {
		System.loadLibrary("mp3lame");
	}

	/**
	 * 保存MP3文件的文件路径
	 */
	private String mFilePath;

	/**
	 * 取样率
	 */
	private int mSampleRate;

	/**
	 * 录音中
	 */
	private boolean mIsRecording = false;

	/**
	 * 通知录音的状态变化的处理程序
	 * 
	 * @see RecMicToMp3#MSG_REC_STARTED
	 * @see RecMicToMp3#MSG_REC_STOPPED
	 * @see RecMicToMp3#MSG_ERROR_GET_MIN_BUFFERSIZE
	 * @see RecMicToMp3#MSG_ERROR_CREATE_FILE
	 * @see RecMicToMp3#MSG_ERROR_REC_START
	 * @see RecMicToMp3#MSG_ERROR_AUDIO_RECORD
	 * @see RecMicToMp3#MSG_ERROR_AUDIO_ENCODE
	 * @see RecMicToMp3#MSG_ERROR_WRITE_FILE
	 * @see RecMicToMp3#MSG_ERROR_CLOSE_FILE
	 */
	private Handler mHandler;

	/**
	 * 录音开始了
	 */
	public static final int MSG_REC_STARTED = 0;

	/**
	 * 录音结束了
	 */
	public static final int MSG_REC_STOPPED = 1;

	/**
	 * バッファサイズが取得できない。サンプリングレート等の設定を端末がサポートしていない可能性がある。
	 * 没有缓冲器的。终端等设置的终端没有终端支持的可能性。
	 */
	public static final int MSG_ERROR_GET_MIN_BUFFERSIZE = 2;

	/**
	 * 文件生成不了
	 */
	public static final int MSG_ERROR_CREATE_FILE = 3;

	/**
	 * 录音的开始失败了
	 */
	public static final int MSG_ERROR_REC_START = 4;
	
	/**
	 * 不能录音。录音中开始发行。
	 */
	public static final int MSG_ERROR_AUDIO_RECORD = 5;

	/**
	 * 编码失败了。录音中开始发行。
	 */
	public static final int MSG_ERROR_AUDIO_ENCODE = 6;

	/**
	 * 文件的开头失败了。录音中开始发行。
	 */
	public static final int MSG_ERROR_WRITE_FILE = 7;

	/**
	 *文件的关闭失败了。录音中开始发行。
	 */
	public static final int MSG_ERROR_CLOSE_FILE = 8;

	long time1;
	/**
	 * コンストラクタ
	 * 
	 * @param filePath
	 *            保存的文件路径
	 * @param sampleRate
	 *           记录的抽样率（Hz）
	 */
	public RecMicToMp3(String filePath, int sampleRate) {
		if (sampleRate <= 0) {
			throw new InvalidParameterException(
					"Invalid sample rate specified.");
		}
		this.mFilePath = filePath;
		this.mSampleRate = sampleRate;
	}
	/**
	 * 开始录音
	 */
	public void start() {
		// 录音的情况下，什么也不做
		if (mIsRecording) {
			return;
		}
		// 用不同的录音开始
		new Thread() {
			@Override
			public void run() {
				android.os.Process
						.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
				// 最低限度的缓冲区尺寸
				final int minBufferSize = AudioRecord.getMinBufferSize(
						mSampleRate, AudioFormat.CHANNEL_IN_MONO,
						AudioFormat.ENCODING_PCM_16BIT);

				// バッファサイズが取得できない。サンプリングレート等の設定を端末がサポートしていない可能性がある。（没有缓冲器的。终端等设置的终端没有终端支持的可能性。）
				if (minBufferSize < 0) {
					if (mHandler != null) {
						mHandler.sendEmptyMessage(MSG_ERROR_GET_MIN_BUFFERSIZE);
					}
					return;
				}
				// getMinBufferSizeで取得した値の場合 在取得的值的场合
				// "W/AudioFlinger(75): RecordThread: buffer overflow"が発生するようであるため、少し大きめの値にしている
				AudioRecord audioRecord = new AudioRecord(
						MediaRecorder.AudioSource.MIC, mSampleRate,
						AudioFormat.CHANNEL_IN_MONO,
						AudioFormat.ENCODING_PCM_16BIT, minBufferSize);
				// PCM buffer size (5sec)
				short[] buffer = new short[mSampleRate * (16 / 8) * 1 * 5]; // SampleRate[Hz] * 16bit * Mono * 5sec
				byte[] mp3buffer = new byte[(int) (7200 + buffer.length * 2 * 1.25)];

//				DataOutputStream output = null; 
				FileOutputStream output = null;
				try {
//					output = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(mFilePath))); 
					output = new FileOutputStream(mFilePath);
					time1 = System.currentTimeMillis();  
				} catch (FileNotFoundException e) {
					//文件生成不了
					if (mHandler != null) {
						mHandler.sendEmptyMessage(MSG_ERROR_CREATE_FILE);
					}
					return;
				}

				// Lame init
//				SimpleLame.init(mSampleRate, 1, mSampleRate, 32);
				MP3Recorder.init(mSampleRate, 1, mSampleRate, 32);

				mIsRecording = true; // 开始录音的开始标志
				try {
					try {
						audioRecord.startRecording(); // 开始录音
					} catch (IllegalStateException e) {
						// 录音的开始失败了
						if (mHandler != null) {
							mHandler.sendEmptyMessage(MSG_ERROR_REC_START);
						}
						return;
					}

					try {
						// 录音开始了
						if (mHandler != null) {
							mHandler.sendEmptyMessage(MSG_REC_STARTED);
						}

						int readSize = 0;
						while (mIsRecording) {
							readSize = audioRecord.read(buffer, 0, minBufferSize);
							if (readSize < 0) {
								// 不能录音
								if (mHandler != null) {
									mHandler.sendEmptyMessage(MSG_ERROR_AUDIO_RECORD);
								}
								break;
							}
							// データが読み込めなかった場合は何もしない
							else if (readSize == 0) {
								;
							}
							// データが入っている場合
							else {
//								int encResult = SimpleLame.encode(buffer,
//										buffer, readSize, mp3buffer);
								int encResult = MP3Recorder.encode(buffer,
										buffer, readSize, mp3buffer);
								int v = 0;  
		                        for (int i = 0; i < readSize; i++) {  
//		                            output.writeShort(buffer[i]);  
		                            v += buffer[i]*buffer[i];  
		                        }  
		                        long time2 = System.currentTimeMillis(); 
		                        if (time2-time1 >100) {  
		                        	 if (!String.valueOf(v / (float) readSize).equals("NaN")) {
		                        		 float f = (int) (Math.abs((int)(v /(float)readSize)/10000) >> 1);
		                        		 if (null == callBack) {  
		                                     return;  
		                                 } 
		                        		 if (f<=40) {  
		                                    LogUtil.i("liang","音量 >> 1");  
		                                    callBack.onCurrentVoice(1);  
		                                }else if (f<=80&&f>40) {  
		                                	LogUtil.i("liang", "音量 >> 2");  
		                                    callBack.onCurrentVoice(2);  
		                                }else if (f<=120&&f>80) {  
		                                	LogUtil.i("liang", "音量 >> 3");  
		                                    callBack.onCurrentVoice(3);  
		                                }else if (f<=160&&f>120) {  
		                                	LogUtil.i("liang", "音量 >> 4");  
		                                    callBack.onCurrentVoice(4);  
		                                }else if (f<=200&&f>160) {  
		                                	LogUtil.i("liang", "音量 >> 5");  
		                                    callBack.onCurrentVoice(5);  
		                                }else if (f<=240&&f>200) {  
		                                	LogUtil.i("liang", "音量 >> 6");  
		                                    callBack.onCurrentVoice(6);  
		                                }else if (f>240) {  
		                                	LogUtil.i("liang", "音量 >> 7");  
		                                    callBack.onCurrentVoice(7);  
		                                }  
		                        	 }
		                        	 time1 = time2;  
		                        }
								if (encResult < 0) {
									// 编码失败了
									if (mHandler != null) {
										mHandler.sendEmptyMessage(MSG_ERROR_AUDIO_ENCODE);
									}
									break;
								}
								if (encResult != 0) {
									try {
										output.write(mp3buffer, 0, encResult);
									} catch (IOException e) {
										// 文件的开头失败了
										if (mHandler != null) {
											mHandler.sendEmptyMessage(MSG_ERROR_WRITE_FILE);
										}
										break;
									}
								}
							}
						}

//						int flushResult = SimpleLame.flush(mp3buffer);
						int flushResult = MP3Recorder.flush(mp3buffer);
						if (flushResult < 0) {
							// 编码失败了
							if (mHandler != null) {
								mHandler.sendEmptyMessage(MSG_ERROR_AUDIO_ENCODE);
							}
						}
						if (flushResult > 0) {
							try {
								output.write(mp3buffer, 0, flushResult);
							} catch (IOException e) {
								// 文件的开头失败了
								if (mHandler != null) {
									mHandler.sendEmptyMessage(MSG_ERROR_WRITE_FILE);
								}
							}
						}

						try {
							output.close();
						} catch (IOException e) {
							//文件的关闭失败了
							if (mHandler != null) {
								mHandler.sendEmptyMessage(MSG_ERROR_CLOSE_FILE);
							}
						}
					} finally {
						//停止录制
						try {
							// 防止某些手机崩溃，例如联想
							audioRecord.stop();
							// 彻底释放资源
							audioRecord.release();
							audioRecord = null;
						}catch (IllegalStateException e){
							e.printStackTrace();
						}
					}
				} finally {
//					SimpleLame.close();
					MP3Recorder.close();
					mIsRecording = false; // 开始录音的开始
				}

				// 录音结束了
				if (mHandler != null) {
					mHandler.sendEmptyMessage(MSG_REC_STOPPED);
				}
			}
		}.start();
	}

	/**
	 * 停止录音
	 */
	public void stop() {
		mIsRecording = false;
	}

	/**
	 * 在录音中
	 * 
	 * @return true的情况录音中，除此以外false
	 */
	public boolean isRecording() {
		return mIsRecording;
	}

	/**
	 * 设置录音状态变化的处理程序
	 * 
	 * @param handler
	 *            通知录音的状态变化的处理程序
	 * 
	 * @see RecMicToMp3#MSG_REC_STARTED
	 * @see RecMicToMp3#MSG_REC_STOPPED
	 * @see RecMicToMp3#MSG_ERROR_GET_MIN_BUFFERSIZE
	 * @see RecMicToMp3#MSG_ERROR_CREATE_FILE
	 * @see RecMicToMp3#MSG_ERROR_REC_START
	 * @see RecMicToMp3#MSG_ERROR_AUDIO_RECORD
	 * @see RecMicToMp3#MSG_ERROR_AUDIO_ENCODE
	 * @see RecMicToMp3#MSG_ERROR_WRITE_FILE
	 * @see RecMicToMp3#MSG_ERROR_CLOSE_FILE
	 */
	public void setHandle(Handler handler) {
		this.mHandler = handler;
	}
	
	  private ThreadCallBack callBack;  
      
	    public ThreadCallBack getCallBack() {  
	        return callBack;  
	    }  
	    public void setCallBack(ThreadCallBack callBack) {  
	        this.callBack = callBack;  
	    } 
	    
	    public interface ThreadCallBack {  
	        public void onCurrentVoice(int currentVolume);  
	    } 
}
