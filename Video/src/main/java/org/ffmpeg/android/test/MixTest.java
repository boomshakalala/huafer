package org.ffmpeg.android.test;

import org.ffmpeg.android.Clip;
import org.ffmpeg.android.FFMPEGController;
import org.ffmpeg.android.ShellUtils;

import java.io.File;

public class MixTest {

	public static void test (String fileTmpPath, String videoClipPath, String audioClipPath, Clip clipOut) throws Exception
	{
		File fileTmp = new File(fileTmpPath);
		File fileAppRoot = new File("");
		
		FFMPEGController fc = new FFMPEGController(null, fileTmp);
		
		Clip clipVideo = new Clip(videoClipPath);
		//fc.getInfo(clipVideo);
		
		Clip clipAudio = new Clip(audioClipPath);
		//fc.getInfo(clipAudio);
		
		fc.combineAudioAndVideo(clipVideo, clipAudio, clipOut, new ShellUtils.ShellCallback() {
			
			@Override
			public void shellOut(String shellLine) {
			//	System.out.println("MIX> " + shellLine);
			}
			
			@Override
			public void processComplete(int exitValue) {
				
				if (exitValue != 0)
					System.err.println("concat non-zero exit: " + exitValue);
			}
		});
		
	}

}
