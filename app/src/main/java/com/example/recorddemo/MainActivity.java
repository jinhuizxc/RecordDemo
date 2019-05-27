package com.example.recorddemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.blankj.utilcode.util.ActivityUtils;
import com.example.recorddemo.record.RecordActivity;
import com.example.recorddemo.record_new.RecordActivityNew;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.btn_record)
    Button btnRecord;
    @BindView(R.id.btn_record_new)
    Button btnRecordNew;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_record, R.id.btn_record_new})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_record:
                ActivityUtils.startActivity(RecordActivity.class);
                break;
            case R.id.btn_record_new:
                ActivityUtils.startActivity(RecordActivityNew.class);
                break;
        }
    }
}
