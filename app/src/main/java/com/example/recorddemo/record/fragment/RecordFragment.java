package com.example.recorddemo.record.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.ToastUtils;
import com.bumptech.glide.Glide;
import com.example.recorddemo.Constants;
import com.example.recorddemo.R;
import com.example.recorddemo.record.utils.RecordUtil;
import com.melnykov.fab.FloatingActionButton;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 录音fragment
 */
public class RecordFragment extends Fragment implements EasyPermissions.PermissionCallbacks {

    private static final String POSITION = "position";
    @BindView(R.id.btn_Record)
    FloatingActionButton recordButton;
    @BindView(R.id.chronometer)
    Chronometer chronometer;
    @BindView(R.id.recordProgressBar)
    ProgressBar recordProgressBar;
    @BindView(R.id.recording_status_text)
    TextView recordingStatusText;
    @BindView(R.id.rl_record)
    RelativeLayout rlRecord;
    @BindView(R.id.iv_record_ok)
    ImageView ivRecordOk;
    @BindView(R.id.iv_record_clear)
    ImageView ivRecordClear;
    @BindView(R.id.iv_sound)
    ImageView ivSound;
    @BindView(R.id.rl_bottom_layout)
    RelativeLayout rlBottomLayout;

    Unbinder unbinder;

//    private int position;

    private int mRecordPromptCount = 0;

    private boolean mStartRecording = true;
    private boolean mPauseRecording = true;

    long timeWhenPaused = 0; //stores time when user clicks


    public static Fragment newsInstance(int position) {
        RecordFragment recordFragment = new RecordFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(POSITION, position);
        recordFragment.setArguments(bundle);
        return recordFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recordButton.setColorNormal(getResources().getColor(R.color.colorPrimary));
        recordButton.setColorPressed(getResources().getColor(R.color.colorPrimary));


//        // 启动service;
//        Intent intent = new Intent(getActivity(), RecordingService.class);
//        getActivity().startService(intent);
//        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        RecordUtil.getInstance().createDataBase();

        requiresPermission();
    }


    @AfterPermissionGranted(Constants.Permission.REQUEST_CODE_STORE_AUDIO)
    private void requiresPermission() {
        if (hasPermission()) {
            // Already have permission, do the thing
//            ToastUtils.showShort("可以录音");
        } else {
            // Do not have permissions, request them now
            // 弹出弹框
//            new CircleDialog.Builder()
//                    .setTitle("权限申请")
//                    .setTitleColor(getResources().getColor(R.color.black))
//                    .setWidth(0.8f)
//                    .setCancelable(false)
//                    .setText(getResources().getString(R.string.request_permissions))
//                    .setPositive("确定", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            EasyPermissions.requestPermissions(mContext, "使用卓讯通需要以下权限,需要您授权,以正常使用卓讯通功能",
//                                    Constants.Permission.REQUEST_CODE_STORE_LOCATION_AUDIO,
//                                    Constants.Permission.PERMS_STORE_LOCATION_AUDIO);
//                        }
//                    })
//                    .setNegative("取消", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            finish();
//                        }
//                    })
//                    .show(getSupportFragmentManager());
            // 申请权限
            requestPermission();
        }
    }

    private void requestPermission() {
        EasyPermissions.requestPermissions(this, "使用App需要以下权限,需要您授权,以正常使用功能",
                Constants.Permission.REQUEST_CODE_STORE_AUDIO,
                Constants.Permission.PERMS_STORE_AUDIO);

    }

    private boolean hasPermission() {
        return EasyPermissions.hasPermissions(getActivity(), Constants.Permission.PERMS_STORE_AUDIO);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }


    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        new AppSettingsDialog.Builder(this)
//                .setRationale("没有"+permissionsHint+"权限，此应用程序无法正常工作。打开应用设置屏幕以修改应用权限")
                .setTitle("App需要该权限")
                .build()
                .show();
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AppSettingsDialog.DEFAULT_SETTINGS_REQ_CODE) {
            if (hasPermission()) {
                // 开始录音
//                ToastUtils.showShort("可以录音");
            } else {
                getActivity().finish();
            }
        }

    }


    @OnClick({R.id.btn_Record, R.id.iv_record_ok})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_record_ok:
                // 完成录音
                stopRecording();
                break;
            case R.id.btn_Record:
                // 需要先判断录音、存储权限, 开始录音
                onRecord(mStartRecording);
                mStartRecording = !mStartRecording;
                break;
        }
    }

    private void stopRecording() {
        //stop recording
        recordButton.setImageResource(R.drawable.icon_record_pause);
        //mPauseButton.setVisibility(View.GONE);
        chronometer.stop();
        chronometer.setBase(SystemClock.elapsedRealtime());
        timeWhenPaused = 0;
        recordingStatusText.setText(getString(R.string.str_tab_start_record));

    }

    // TODO 待优化测试
//    private void startRecord() {
//        if (recordUtil.getStatus() == RecordUtil.Status.STATUS_NO_READY) {
//            // 设置状态
////            RecordUtil.setStatus(RecordUtil.Status.STATUS_READY);
//            recordUtil.startRecord();
//            recordButton.setImageResource(R.drawable.icon_recording);
//            Toast.makeText(getActivity(), "开始录音", Toast.LENGTH_SHORT).show();
//            // 创建录音文件路径
//            createFile();
//            //start Chronometer
//            chronometer.setBase(SystemClock.elapsedRealtime());
//            chronometer.start();
//            chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
//                @Override
//                public void onChronometerTick(Chronometer chronometer) {
//                    if (mRecordPromptCount == 0) {
//                        recordingStatusText.setText(getString(R.string.str_record_in_progress) + ".");
//                    } else if (mRecordPromptCount == 1) {
//                        recordingStatusText.setText(getString(R.string.str_record_in_progress) + "..");
//                    } else if (mRecordPromptCount == 2) {
//                        recordingStatusText.setText(getString(R.string.str_record_in_progress) + "...");
//                        mRecordPromptCount = -1;
//                    }
//
//                    mRecordPromptCount++;
//                }
//            });
//
//            recordingStatusText.setText(getString(R.string.str_record_in_progress) + ".");
//            mRecordPromptCount++;
//        } else if (recordUtil.getStatus() == RecordUtil.Status.RECORD_START) {
//            // 暂停录音
//            recordUtil.pauseRecord();
//            Toast.makeText(getActivity(), "暂停录音", Toast.LENGTH_SHORT).show();
//            // 计时停止并显示
//            chronometer.stop();
//
//            ivRecordOk.setVisibility(View.VISIBLE);
//            ivRecordClear.setVisibility(View.VISIBLE);
//        } else if (recordUtil.getStatus() == RecordUtil.Status.RECORD_PAUSE) {
//            Toast.makeText(getActivity(), "继续录音", Toast.LENGTH_SHORT).show();
//            chronometer.start();
//            recordUtil.startRecord();
//            ivRecordOk.setVisibility(View.GONE);
//            ivRecordClear.setVisibility(View.GONE);
//        }
//    }


    private void onRecord(boolean start) {
//        Intent intent = new Intent(getActivity(), RecordingService.class);
        if (start) {
            // start recording
            recordButton.setImageResource(R.drawable.icon_record_pause);
            //mPauseButton.setVisibility(View.VISIBLE);
            ToastUtils.showShort("开始录音");

            Glide.with(getActivity())
                    .load(R.drawable.icon_record_sound_gif)
                    .into(ivSound);

            File folder = new File(Environment.getExternalStorageDirectory() + "/SoundRecorder");
            if (!folder.exists()) {
                //folder /SoundRecorder doesn't exist, create the folder
                folder.mkdir();
            }
            //start Chronometer
            chronometer.setBase(SystemClock.elapsedRealtime());   // 重置时间
            chronometer.start();
            chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    if (mRecordPromptCount == 0) {
                        recordingStatusText.setText(getString(R.string.str_record_in_progress) + ".");
                    } else if (mRecordPromptCount == 1) {
                        recordingStatusText.setText(getString(R.string.str_record_in_progress) + "..");
                    } else if (mRecordPromptCount == 2) {
                        recordingStatusText.setText(getString(R.string.str_record_in_progress) + "...");
                        mRecordPromptCount = -1;
                    }

                    mRecordPromptCount++;
                }
            });

            // 开始录音
            RecordUtil.getInstance().startRecording();

//            getActivity().startService(intent);
//            // TODO 启动录音service, 没有做8.0以上判断;
//            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                getActivity().startForegroundService(intent);
//            } else {
//                getActivity().startService(intent);
//            }
            //keep screen on while recording
//            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            recordingStatusText.setText(getString(R.string.str_record_in_progress) + ".");
            mRecordPromptCount++;

        } else {
            Glide.with(getActivity())
                    .load(R.drawable.icon_record_sound_normal)
                    .into(ivSound);
            //stop recording
            recordButton.setImageResource(R.drawable.icon_record_normal);
            //mPauseButton.setVisibility(View.GONE);
            chronometer.stop();
            chronometer.setBase(SystemClock.elapsedRealtime());
            timeWhenPaused = 0;
            recordingStatusText.setText(getString(R.string.str_tab_start_record));

            RecordUtil.getInstance().stopRecord();
//            getActivity().stopService(intent);
            //allow the screen to turn off again once recording is finished
//            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
//        Intent intent = new Intent(getActivity(), RecordingService.class);
//        getActivity().stopService(intent);
//        //allow the screen to turn off again once recording is finished
//        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
