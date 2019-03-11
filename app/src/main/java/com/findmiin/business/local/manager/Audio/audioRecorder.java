package com.findmiin.business.local.manager.Audio;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;


import com.findmiin.business.local.manager.utils.Const;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by JonIC on 2017-01-30.
 */
public class audioRecorder {
    RecordAudio recordTask;
    PlayAudio playTask;
    final int CUSTOM_FREQ_SOAP = 2;;
    File recordingFile;

    boolean isRecording = false;
    boolean isPlaying = false;

    int frequency = 8000;//44100 11025
    int outfrequency = frequency*CUSTOM_FREQ_SOAP;
    int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
//    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    Context context;
    public audioRecorder(Context context)
    {
        this.context = context;

    }

    public void onCreate(String filename) {
        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+ Const.TEMP_RECORDER_FOLDER+"/");
        path.mkdirs();

        try {
            recordingFile = File.createTempFile(filename, ".pcm", path);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't create file on SD card", e);
        }
    }

    public void play() {
        playTask = new PlayAudio();
        playTask.execute();
    }

    public void stopPlaying() {
        isPlaying = false;
    }

    public void record(String filename) {
        File path = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+ Const.TEMP_RECORDER_FOLDER+"/");
        path.mkdirs();

        try {
            recordingFile = File.createTempFile(filename, ".pcm", path);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't create file on SD card", e);
        }


        recordTask = new RecordAudio();
        recordTask.execute();
    }

    public String stopRecording() {
        isRecording = false;
        Toast.makeText(context, recordingFile.getName(), Toast.LENGTH_SHORT).show();
        return recordingFile.getName();
    }

    private class RecordAudio extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            isRecording = true;

            try {
                DataOutputStream dos = new DataOutputStream(
                        new BufferedOutputStream(new FileOutputStream(
                                recordingFile)));

                int bufferSize = AudioRecord.getMinBufferSize(frequency,
                        channelConfiguration, audioEncoding);

                AudioRecord audioRecord = new AudioRecord(
                        MediaRecorder.AudioSource.MIC, frequency,
                        channelConfiguration, audioEncoding, bufferSize);

                short[] buffer = new short[bufferSize];
                audioRecord.startRecording();


                while (isRecording) {
                    int bufferReadResult = audioRecord.read(buffer, 0,
                            bufferSize);
                    for (int i = 0; i < bufferReadResult; i++) {
                        dos.writeShort(buffer[i]);
                    }
                }

                audioRecord.stop();
                dos.close();
            } catch (Throwable t) {
                Log.e("AudioRecord", "Recording Failed");
            }

            return null;
        }



        protected void onPostExecute(Void result) {
        }
    }
    private class PlayAudio extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            isPlaying = true;

            int bufferSize = AudioTrack.getMinBufferSize(outfrequency,
                    channelConfiguration, audioEncoding);
            short[] audiodata = new short[bufferSize / 4];

            try {
                DataInputStream dis = new DataInputStream(
                        new BufferedInputStream(new FileInputStream(
                                recordingFile)));

                AudioTrack audioTrack = new AudioTrack(
                        AudioManager.STREAM_MUSIC, outfrequency,
                        channelConfiguration, audioEncoding, bufferSize,
                        AudioTrack.MODE_STREAM);

                audioTrack.play();

                while (isPlaying && dis.available() > 0) {
                    int i = 0;
                    while (dis.available() > 0 && i < audiodata.length) {
                        audiodata[i] = dis.readShort();
                        i++;
                    }
                    audioTrack.write(audiodata, 0, audiodata.length);
                }

                dis.close();

            } catch (Throwable t) {
                Log.e("AudioTrack", "Playback Failed");
            }

            return null;
        }
    }
}
