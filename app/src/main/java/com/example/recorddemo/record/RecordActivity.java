package com.example.recorddemo.record;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.example.recorddemo.R;
import com.example.recorddemo.record.adapter.RecordTabAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 原生录音功能模块;
 * <p>
 * h5做这个页面的话需要调用原生录音模块;
 */
public class RecordActivity extends AppCompatActivity {

    //    @BindView(R.id.tabs)
//    PagerSlidingTabStrip tabs;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.tabLayout)
    TabLayout tabLayout;

//    @BindView(R.id.tv_title)
//    TextView tvTitle;
//    @BindView(R.id.iv_back)
//    ImageView ivBack;
//    @BindView(R.id.ll_back)
//    LinearLayout llBack;

    private String[] titles = {"录音", "列表"};

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        ButterKnife.bind(this);

//        ivBack.setVisibility(View.VISIBLE);
//        tvTitle.setText("录音功能");
        viewPager.setAdapter(new RecordTabAdapter(getSupportFragmentManager(), titles));
//        tabs.setViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }


}
