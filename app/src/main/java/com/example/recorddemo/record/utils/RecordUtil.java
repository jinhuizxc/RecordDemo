package com.example.recorddemo.record.utils;

import android.media.MediaRecorder;
import android.os.Environment;

import com.blankj.utilcode.util.ToastUtils;
import com.example.recorddemo.App;
import com.example.recorddemo.R;
import com.example.recorddemo.db.DBHelper;
import com.example.recorddemo.record.service.RecordingService;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class RecordUtil {

    private static RecordUtil instance;

    private DBHelper mDatabase;

    private String mFileName = null;
    private String mFilePath = null;

    private MediaRecorder mRecorder = null;

    private long mStartingTimeMillis = 0;
    private long mElapsedMillis = 0;
    private int mElapsedSeconds = 0;
    private RecordingService.OnTimerChangedListener onTimerChangedListener = null;
    private static final SimpleDateFormat mTimerFormat = new SimpleDateFormat("mm:ss", Locale.getDefault());

    private Timer mTimer = null;
    private TimerTask mIncrementTimerTask = null;


    public static RecordUtil getInstance(){
        if (instance == null){
            instance = new RecordUtil();
        }
        return instance;
    }

    private RecordUtil(){

    }

    public void createDataBase(){
        mDatabase = new DBHelper(App.getApp());
    }

    public void startRecording(){

            setFileNameAndPath();

            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mRecorder.setOutputFile(mFilePath);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            mRecorder.setAudioChannels(1);
            if (MySharedPreferences.getPrefHighQuality(App.getApp())) {
                mRecorder.setAudioSamplingRate(44100);
                mRecorder.setAudioEncodingBitRate(192000);
            }

            try {
                mRecorder.prepare();
                mRecorder.start();
                mStartingTimeMillis = System.currentTimeMillis();

                //startTimer();
                //startForeground(1, createNotification());

            } catch (IOException e) {
            }
        }

    public void setFileNameAndPath(){
        int count = 0;
        File f;
        do{
            count++;
            mFileName = App.getApp().getString(R.string.default_file_name)
                    + "_" + (mDatabase.getCount() + count) + ".mp3";
            mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            mFilePath += "/SoundRecorder/" + mFileName;

            f = new File(mFilePath);
        }while (f.exists() && !f.isDirectory());
    }

    public void stopRecording() {
        mRecorder.stop();
        mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMillis);
        mRecorder.release();
        ToastUtils.showShort("录音保存成功");
//        Toast.makeText(this, getString(R.string.str_recording_finish) + " " + mFilePath, Toast.LENGTH_LONG).show();

        //remove notification
        if (mIncrementTimerTask != null) {
            mIncrementTimerTask.cancel();
            mIncrementTimerTask = null;
        }

        mRecorder = null;

        try {
            mDatabase.addRecording(mFileName, mFilePath, mElapsedMillis);

        } catch (Exception e){
        }
    }

    public void stopRecord(){
        if (mRecorder != null) {
            stopRecording();
        }

    }



}
