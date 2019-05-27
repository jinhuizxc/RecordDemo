package com.example.recorddemo.record_new.fragment;

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
import com.example.recorddemo.App;
import com.example.recorddemo.Constants;
import com.example.recorddemo.R;
import com.example.recorddemo.record_new.utils.RecordUtil;
import com.melnykov.fab.FloatingActionButton;

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
 * <p>
 * Chronometer的用法很简单，它支持如下用法：
 * <p>
 * getBase()：返回时间。
 * setBase(long base)：设置计时器的起始时间。
 * start()：开始计时。
 * stop()：停止计时。
 * setFormat(String format)：设置显示时间的格式。
 * setOnChronometerTickListener(Chronometer.OnChronometerTickListener listener)：为计时器绑定监听事件。
 */
public class RecordFragmentNew extends Fragment implements EasyPermissions.PermissionCallbacks {

    private static final String POSITION = "position";

    @BindView(R.id.chronometer)
    Chronometer chronometer;
    @BindView(R.id.recordProgressBar)
    ProgressBar recordProgressBar;
    @BindView(R.id.iv_sound)
    ImageView ivSound;
    @BindView(R.id.iv_record_ok)
    ImageView ivRecordOk;
    @BindView(R.id.btn_Record)
    FloatingActionButton btnRecord;
    @BindView(R.id.iv_record_clear)
    ImageView ivRecordClear;
    @BindView(R.id.rl_bottom_layout)
    RelativeLayout rlBottomLayout;
    @BindView(R.id.rl_record)
    RelativeLayout rlRecord;
    @BindView(R.id.tv_state)
    TextView tvState;

    Unbinder unbinder;

    private long recordingTime = 0;// 记录下来的总时间

    public static boolean isPause = true;// 当前录音是否处于暂停状态

    public static Fragment newsInstance(int position) {
        RecordFragmentNew recordFragment = new RecordFragmentNew();
        Bundle bundle = new Bundle();
        bundle.putInt(POSITION, position);
        recordFragment.setArguments(bundle);
        return recordFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_new, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecordUtil.getInstance().createDataBase();
        RecordUtil.getInstance().createFileAndPath();

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


//    @Override
//    public void onRequestPermissionsResult(int i, @NonNull String[] strings, @NonNull int[] ints) {
//
//    }

    @Override
    public void onRequestPermissionsResult(int i, @NonNull String[] strings, @NonNull int[] ints) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.iv_record_ok, R.id.btn_Record, R.id.iv_record_clear})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_record_ok:
                // 录音完成
                RecordUtil.getInstance().saveRecord(recordingTime);
                onRecordStop();
                resetView();
                break;
            case R.id.btn_Record:
                // 开始录音
                // 判断SD卡是否存在
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    ToastUtils.showShort("SD卡状态异常，请检查后重试!");
                    break;
                }
                // 点击录音加载gif, 点击暂停切换图片;
                if (!isPause) {
                    isPause = true;
                    Glide.with(getActivity())
                            .load(R.drawable.icon_record_sound_normal)
                            .into(ivSound);
                    ivRecordOk.setVisibility(View.VISIBLE);
                    ivRecordClear.setVisibility(View.VISIBLE);
                    tvState.setText(App.getApp().getString(R.string.str_record_pause));
                    btnRecord.setImageResource(R.drawable.icon_record_normal);
                    // 暂停录音，暂停计时
                    RecordUtil.getInstance().stopRecord();
                    onRecordPause();
                } else {
//                    // 判断SD卡是否存在
//                    if (!Environment.getExternalStorageState().equals(
//                            Environment.MEDIA_MOUNTED)) {
//                        ToastUtils.showShort("SD卡状态异常，请检查后重试！");
//                        return;
//                    }
                    // 开始录音
                    isPause = false;
                    Glide.with(getActivity())
                            .load(R.drawable.icon_record_sound_gif)
                            .into(ivSound);
                    ivRecordOk.setVisibility(View.GONE);
                    ivRecordClear.setVisibility(View.GONE);
                    btnRecord.setImageResource(R.drawable.icon_record_pause);
                    tvState.setText(App.getApp().getString(R.string.str_recording));
                    RecordUtil.getInstance().startRecord();
                    onRecordStart();
                }
                break;
            case R.id.iv_record_clear:
                // 清空当前录音
                RecordUtil.getInstance().deleteRecord();
                onRecordStop();
                resetView();
                break;
        }
    }

    // 重置布局的默认状态
    private void resetView() {
        tvState.setText(App.getApp().getString(R.string.str_record));
        btnRecord.setImageResource(R.drawable.icon_record_normal);
        ivRecordClear.setVisibility(View.GONE);
        ivRecordOk.setVisibility(View.GONE);
    }

    public void onRecordStart() {
        chronometer.setBase(SystemClock.elapsedRealtime() - recordingTime);// 跳过已经记录了的时间，起到继续计时的作用
        chronometer.start();
    }

    public void onRecordPause() {
        chronometer.stop();
        recordingTime = SystemClock.elapsedRealtime() - chronometer.getBase();// 保存这次记录了的时间
    }

    public void onRecordStop() {
        recordingTime = 0;
        chronometer.setBase(SystemClock.elapsedRealtime());
        chronometer.stop();
    }



}
