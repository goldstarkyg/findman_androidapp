package com.findmiin.business.local.manager.utils;

import android.media.MediaPlayer;
import android.media.MediaRecorder;

import java.io.File;
import java.io.IOException;

public class AudioRecorder {
	final MediaRecorder recorder = new MediaRecorder();
	private String path;
	
	public AudioRecorder(String path) {
	    this.path = path;
	}
	
	public void start() throws IOException {
	    String state = android.os.Environment.getExternalStorageState();
	    if (!state.equals(android.os.Environment.MEDIA_MOUNTED)) {
	        throw new IOException("SD Card is not mounted.  It is " + state
	                + ".");
	    }
	
	    // make sure the directory we plan to store the recording in exists
	    new File(path).delete();
	    File directory = new File(path).getParentFile();
	    if (!directory.exists() && !directory.mkdirs()) {
	        throw new IOException("Path to file could not be created.");
	    }
	
	    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	    recorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
	    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
	    recorder.setAudioChannels(1);
	    recorder.setAudioSamplingRate(8000);
	    recorder.setOutputFile(path);
	    recorder.prepare();
	    recorder.start();
	}
	
	public void stop() throws IOException {
	    recorder.stop();
	    recorder.release();
	}
	
	public void playarcoding(String path) throws IOException {
	    MediaPlayer mp = new MediaPlayer();
	    mp.setDataSource(path);
	    mp.prepare();
	    mp.start();
	    mp.setVolume(10, 10);
	}
	
	public String getPath()
	{
		return path;
	}
}
