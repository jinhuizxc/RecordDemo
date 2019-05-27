package com.example.recorddemo.record_new.utils;

import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.blankj.utilcode.util.ToastUtils;
import com.example.recorddemo.App;
import com.example.recorddemo.R;
import com.example.recorddemo.db.DBHelper;
import com.example.recorddemo.record_new.fragment.RecordFragmentNew;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RecordUtil {

    private static final String TAG = "RecordUtil";

    private static RecordUtil instance;

    private DBHelper mDatabase;


    private MediaRecorder mRecorder = null;

    private long limitTime = 1000;// 录音文件最短事件1秒
    private long currentTime;
    // 文件名
    private String fileName = null;
    // 文件路径
    private String rootPath = null;
    private static String filePath = null;

    private String deleteStr = null; // 列表中要删除的文件名

    private ArrayList<String> mLypdList = new ArrayList<String>();// 待合成的录音片段
    public ArrayList<String> final_list = new ArrayList<String>();// 已合成的录音片段

    public static final String ROOT_PATH = getRootPath();


    public static RecordUtil getInstance() {
        if (instance == null) {
            instance = new RecordUtil();
        }
        return instance;
    }

    private RecordUtil() {
    }

    public void createDataBase() {
        mDatabase = new DBHelper(App.getApp());
    }

//    public void createFileAndPath() {
////        filePath = getRootPath() + "/RecordUtil";
////        createFile();// 创造文件路径
//        fileName = getTime() + ".mp3";
//        filePath = Environment.getExternalStorageDirectory().getAbsolutePath()
//                + "/SoundRecorder/" + fileName;
//        Log.e(TAG, "文件名: " + fileName + ", " + "文件路径: " + filePath);
//    }

    public void createFileAndPath() {
        rootPath = getRootPath() + "/SoundRecorder";
        createFile();// 创造文件路径
        Log.e(TAG, "文件名: " + fileName + ", " + "文件路径: " + rootPath);
    }


    public static String getRootPath() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().getPath();
        } else {
            return "/sdcard";
        }
    }

    // 创建文件夹
    public static void createFile() {
        try {
            File file = new File(ROOT_PATH);
            if (!file.exists()) {
                file.mkdir();
            }
            file = new File(filePath);
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
        }
    }

    /**
     * 暂停录音
     */
    public void stopRecord() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            // 将录音片段加入列表
            if (System.currentTimeMillis() - currentTime < limitTime) {
                // 录音少于一秒不保存
//                ToastUtils.showShort("录音少于一秒");
                File pdFile = new File(filePath);
                if (filePath != null && pdFile.exists()) {
                    pdFile.delete();
                }
            } else {
                if (filePath != null && mLypdList != null) {
                    mLypdList.add(filePath);
                }
                Log.e(TAG, "片段:---->" + filePath);
            }

        }
    }

    /**
     * 开始录音
     * 片段:---->/storage/emulated/0/SoundRecorder/2019052717-29-39.mp3
     * 片段:---->/storage/emulated/0/SoundRecorder/2019052717-29-56.mp3
     * 保存片段:---->/storage/emulated/0/SoundRecorder/2019052717-29-56.mp3
     * 最后合成的音频文件: /storage/emulated/0/SoundRecorder/2019052717-30-07.mp3
     */
    public void startRecord() {
        currentTime = System.currentTimeMillis();
//        setFileNameAndPath();
        filePath = rootPath + "/" + getFileName();
        ;
        Log.e(TAG, "文件名: " + fileName + ", " + "文件路径: " + filePath);
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 选择amr格式
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        mRecorder.setOutputFile(filePath);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
        } catch (Exception e) {
            // 若录音器启动失败就需要重启应用，屏蔽掉按钮的点击事件。 否则会出现各种异常。
            ToastUtils.showShort("录音器启动失败，请返回重试！");
            mRecorder.release();
            mRecorder = null;
        }
        if (mRecorder != null) {
            mRecorder.start();
        }
    }


    /**
     * E/RecordUtil: 文件名: /storage/emulated/0/SoundRecorder/myRecording_1.mp3
     */
    public void setFileNameAndPath() {
        int count = 0;
        File file;
        do {
            count++;
            fileName = App.getApp().getString(R.string.default_file_name)
                    + "_" + (mDatabase.getCount() + count) + ".mp3";
            filePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                    + "/SoundRecorder/" + fileName;
            Log.e(TAG, "文件名: " + fileName + ", " + "文件路径: " + filePath);
            file = new File(filePath);
        } while (file.exists() && !file.isDirectory());
    }

    // 获得当前时间
    private String getTime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHH-mm-ss");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        String time = formatter.format(curDate);
        return time;
    }

    // 删除录音文件
    public void deleteRecord() {
        // 删除所选中的录音文件
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
            final_list.remove(deleteStr);
        } else {
            final_list.remove(deleteStr);
        }
        filePath = null;
        ToastUtils.showShort("删除成功");
        // 录音结束 、时间归零
    }

    /**
     * 将录音时间传过来;
     *
     * @param recordingTime 录音的时间
     */
    public void saveRecord(long recordingTime) {
        Log.e(TAG, "recordingTime: " + recordingTime);
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
        if (RecordFragmentNew.isPause) {  // 正在暂停期间
            // 保存已经有的录音片段
        } else {
            if (System.currentTimeMillis() - currentTime < limitTime) {
                // 录音少于一秒不保存
                ToastUtils.showShort("录音少于一秒");
                File pdFile = new File(filePath);
                if (filePath != null && pdFile.exists()) {
                    pdFile.delete();
                }
            } else {
                if (filePath != null && mLypdList != null) {
                    mLypdList.add(filePath);
                }
                Log.e(TAG, "保存片段:---->" + filePath);
            }
        }
        // 最后合成的音频文件
        filePath = rootPath + "/" + getFinalFileName();
        Log.e(TAG, "最后合成的音频文件: " + filePath);
        String fileName1 = getTime() + ".mp3";
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        FileInputStream fileInputStream = null;
        try {
            Log.e(TAG, "录音片段的长度---->" + mLypdList.size());
            for (int i = 0; i < mLypdList.size(); i++) {
                File file = new File(mLypdList.get(i));
                // 把因为暂停所录出的多段录音进行读取
                fileInputStream = new FileInputStream(file);
                byte[] mByte = new byte[fileInputStream.available()];
                int length = mByte.length;
                // 第一个录音文件的前六位是不需要删除的
                if (i == 0) {
                    while (fileInputStream.read(mByte) != -1) {
                        fileOutputStream.write(mByte, 0, length);
                    }
                }
                // 之后的文件，去掉前六位
                else {
                    while (fileInputStream.read(mByte) != -1) {
                        fileOutputStream.write(mByte, 6, length - 6);
                    }
                }
            }
            if (mLypdList.size() > 0) {
                final_list.add(fileName1);
//                mLyAdapter.notifyDataSetChanged();
                Log.e(TAG, "合成成功");
                try {
                    mDatabase.addRecording(fileName, filePath, recordingTime);
                    ToastUtils.showShort("录音成功");
                } catch (Exception e) {
                    Log.e(TAG, "数据库异常: " + "添加数据库错误");
                }
            }
        } catch (Exception e) {
            // 这里捕获流的IO异常，万一系统错误需要提示用户
            e.printStackTrace();
            ToastUtils.showShort("录音出现错误，请重试！");
            for (int i = 0; i < mLypdList.size(); i++) {
                File file = new File(mLypdList.get(i));
                if (file.exists()) {
                    file.delete();
                }
            }
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.flush();
                }

                if (fileInputStream != null) {
                    fileInputStream.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            // 录音结束 、时间归零
        }
        // 不管合成是否成功、删除录音片段
        for (int i = 0; i < mLypdList.size(); i++) {
            File file = new File(mLypdList.get(i));
            if (file.exists()) {
                file.delete();
            }
        }
        mLypdList.clear();
    }


    private String getFileName() {
        fileName = getTime() + ".mp3";
        return fileName;
    }
    /**
     * 最终保存的文件名
     *
     * @return
     */
    private String getFinalFileName() {
        fileName = App.getApp().getString(R.string.default_file_name)
                + "_" + (mDatabase.getCount() + 1) + ".mp3";
        return fileName;
    }

}
