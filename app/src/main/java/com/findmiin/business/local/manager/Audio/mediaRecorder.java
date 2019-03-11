package com.findmiin.business.local.manager.Audio;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;


import com.findmiin.business.local.manager.utils.Const;
import com.findmiin.business.local.manager.utils.MessageUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by JonIC on 2017-01-30.
 */
public class mediaRecorder {
    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
    private static final String AUDIO_RECORDER_FILE_EXT_MP3 = ".mp3";
    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
    private static final String AUDIO_RECORDER_FILE_EXT_AAC = ".aac";
    private static final String AUDIO_RECORDER_FILE_EXT_AMR = ".amr";

    private MediaRecorder recorder = null;
    private int currentFormat = 0;
    private int output_formats[] = {MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.OutputFormat.THREE_GPP};
    private String file_exts[] = {AUDIO_RECORDER_FILE_EXT_WAV,  AUDIO_RECORDER_FILE_EXT_MP3, AUDIO_RECORDER_FILE_EXT_AAC, AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP};

    MediaPlayer mPlayer=null;
    String m_filePath = null;
    String m_fileName  = null;
    Context context;

    int _currentPosition;
    public boolean isPlaying = false;
    public mediaRecorder(Context context){
        this.context = context;
        onCreate();
    }

    private void onCreate(){
        _currentPosition = 0;
    }
    private String getFilename() {

        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, Const.TEMP_RECORDER_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }
        File file2 = new File(filepath, Const.AUDIO_RECORDER_FOLDER);
        if (!file2.exists()) {
            file2.mkdirs();
        }

        File file3 = new File(filepath, Const.AUDIO_SAVE_FOLDER);
        if (!file3.exists()) {
            file3.mkdirs();
        }
        return (file2.getAbsolutePath() + "/" + m_fileName + AUDIO_RECORDER_FILE_EXT_AMR);
    }

    public String getRecFileName(){
        return m_fileName + AUDIO_RECORDER_FILE_EXT_AMR;
    }
    public void startRecording(String fileName) {
        if(recorder != null){
            recorder.release();
        }
        this.m_fileName = fileName;

        m_filePath = getFilename();
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);//RAW_AMR
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setAudioSamplingRate(8000);
        recorder.setAudioEncodingBitRate(8000);
        recorder.setOutputFile(m_filePath);
        recorder.setOnErrorListener(errorListener);
        recorder.setOnInfoListener(infoListener);

        try {
            recorder.prepare();
            recorder.start();

        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            MessageUtils.showMessageDialog(context, "Error");
//            AppLog.logString("Error: " + what + ", " + extra);
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            MessageUtils.showMessageDialog(context, "Warning");
//            AppLog.logString("Warning: " + what + ", " + extra);
        }
    };

    public void stopRecording() {
        if (recorder != null) {
            try{
                recorder.stop();
                recorder.reset();
                recorder.release();
                recorder = null;
                _currentPosition = 0;
            }catch (IllegalStateException e){

            }
        }
    }
    public int getSessionID(){
        return mPlayer.getAudioSessionId();
    }
    public void playMedia(String url){
        if(isPlaying){
            return;
        }
        isPlaying = true;
        mPlayer = MediaPlayer.create(context, Uri.parse(url));
        mPlayer.setLooping(false);
        try {
            mPlayer.prepare();
        } catch (IllegalStateException e) {
            //Toast.makeText(context, "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Toast.makeText(context, "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
        }
        setOnMyCompleteLister(completionListener);
        mPlayer.start();
    }

    public void setOnMyCompleteLister(MediaPlayer.OnCompletionListener completeLister){
        mPlayer.setOnCompletionListener(completeLister);
    }
    MediaPlayer.OnCompletionListener completionListener
            = new MediaPlayer.OnCompletionListener(){
        @Override
        public void onCompletion(MediaPlayer arg0) {
            isPlaying = false;
        }
    };

    public void stopMedia(){
        if(!isPlaying){
            return;
        }
        mPlayer.stop();
        mPlayer.release();
        isPlaying = false;
    }
    public int getDuration(){
        if(mPlayer != null)
            return mPlayer.getDuration();
        return 0;
    }

    public void seekTo(int msec){
        if(isPlaying != false)
            mPlayer.seekTo(msec);
    }

    public int getCurrentPosition(){
        if(isPlaying != false){
            return mPlayer.getCurrentPosition();
        }
        return 0;
    }

    public void pause(){
        if(isPlaying != false){
            _currentPosition = getCurrentPosition();
            mPlayer.pause();
        }
    }


}

//import android.content.Context;
//import android.media.MediaPlayer;
//import android.media.MediaRecorder;
//import android.net.Uri;
//import android.os.Environment;
//import android.widget.Toast;
//
//import com.reveal.xcaltaptt.manager.utils.Const;
//import com.reveal.xcaltaptt.manager.utils.MessageUtils;
//
//import java.io.File;
//import java.io.IOException;
//
///**
// * Created by JonIC on 2017-01-30.
// */
//public class mediaRecorder {
//    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
//    private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
//    private static final String AUDIO_RECORDER_FILE_EXT_MP3 = ".mp3";
//    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
//    private static final String AUDIO_RECORDER_FILE_EXT_AAC = ".aac";
//    private static final String AUDIO_RECORDER_FILE_EXT_AMR = ".amr";
//
//    private MediaRecorder recorder = null;
//    private int currentFormat = 0;
//    private int output_formats[] = {MediaRecorder.OutputFormat.MPEG_4, MediaRecorder.OutputFormat.THREE_GPP};
//    private String file_exts[] = {AUDIO_RECORDER_FILE_EXT_WAV,  AUDIO_RECORDER_FILE_EXT_MP3, AUDIO_RECORDER_FILE_EXT_AAC, AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP};
//
//    MediaPlayer mPlayer=null;
//    String m_filePath = null;
//    String m_fileName  = null;
//    Context context;
//
//    int _currentPosition;
//    public boolean isPlaying = false;
//    public mediaRecorder(Context context){
//        this.context = context;
//        onCreate();
//    }
//
//    private void onCreate(){
//        _currentPosition = 0;
//    }
//    private String getFilename() {
//
//        String filepath = Environment.getExternalStorageDirectory().getPath();
//        File file = new File(filepath, Const.TEMP_RECORDER_FOLDER);
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        File file2 = new File(filepath, Const.AUDIO_RECORDER_FOLDER);
//        if (!file2.exists()) {
//            file2.mkdirs();
//        }
//
//        File file3 = new File(filepath, Const.AUDIO_SAVE_FOLDER);
//        if (!file3.exists()) {
//            file3.mkdirs();
//        }
//        return (file2.getAbsolutePath() + "/" + m_fileName + AUDIO_RECORDER_FILE_EXT_AMR);
//    }
//
//    public String getRecFileName(){
//        return m_fileName + AUDIO_RECORDER_FILE_EXT_AMR;
//    }
//    public void startRecording(String fileName) {
//        if(recorder != null){
//            recorder.release();
//        }
//        this.m_fileName = fileName;
//
//        m_filePath = getFilename();
//        recorder = new MediaRecorder();
//        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);//RAW_AMR
//        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//        recorder.setAudioSamplingRate(8000);
//        recorder.setAudioEncodingBitRate(8000);
//        recorder.setOutputFile(m_filePath);
//        recorder.setOnErrorListener(errorListener);
//        recorder.setOnInfoListener(infoListener);
//
//        try {
//            recorder.prepare();
//            recorder.start();
//
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
//        @Override
//        public void onError(MediaRecorder mr, int what, int extra) {
//            MessageUtils.showMessageDialog(context, "Error");
////            AppLog.logString("Error: " + what + ", " + extra);
//        }
//    };
//
//    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
//        @Override
//        public void onInfo(MediaRecorder mr, int what, int extra) {
//            MessageUtils.showMessageDialog(context, "Warning");
////            AppLog.logString("Warning: " + what + ", " + extra);
//        }
//    };
//
//    public void stopRecording() {
//        if (recorder != null) {
//            try{
//                recorder.stop();
//                recorder.reset();
//                recorder.release();
//                recorder = null;
//                _currentPosition = 0;
//            }catch (IllegalStateException e){
//
//            }
//        }
//    }
//    public void playMedia(String url){
//        if(isPlaying){
//            return;
//        }
//        isPlaying = true;
//        try {
//            mPlayer = MediaPlayer.create(context, Uri.parse(url));
//            mPlayer.setLooping(false);
//            mPlayer.prepare();
//            setOnMyCompleteLister(completionListener);
//            mPlayer.start();
//        } catch (IllegalStateException e) {
//            Toast.makeText(context, "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
//        } catch (IOException e) {
//            Toast.makeText(context, "You might not set the URI correctly!", Toast.LENGTH_LONG).show();
//        }
//    }
//
//    public void setOnMyCompleteLister(MediaPlayer.OnCompletionListener completeLister){
//        mPlayer.setOnCompletionListener(completeLister);
//    }
//    MediaPlayer.OnCompletionListener completionListener
//            = new MediaPlayer.OnCompletionListener(){
//        @Override
//        public void onCompletion(MediaPlayer arg0) {
//            isPlaying = false;
//        }
//    };
//
//    public void stopMedia(){
//        if(!isPlaying){
//            return;
//        }
//        mPlayer.stop();
//        mPlayer.release();
//        isPlaying = false;
//    }
//    public int getDuration(){
//        if(mPlayer != null)
//            return mPlayer.getDuration();
//        return 0;
//    }
//
//    public void seekTo(int msec){
//        if(isPlaying != false)
//            mPlayer.seekTo(msec);
//    }
//
//    public int getCurrentPosition(){
//        if(isPlaying != false){
//            return mPlayer.getCurrentPosition();
//        }
//        return 0;
//    }
//
//    public void pause(){
//        if(isPlaying != false){
//            _currentPosition = getCurrentPosition();
//            mPlayer.pause();
//        }
//    }
//
//
//}
