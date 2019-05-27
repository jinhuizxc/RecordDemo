package com.example.recorddemo.record_new.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.recorddemo.R;
import com.example.recorddemo.adapter.FilePreViewAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 文件预览页面
 */
public class FilePreViewFragmentNew extends Fragment {

    private static final String POSITION = "position";

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.rl_file_preview)
    RelativeLayout rlFilePreview;
    Unbinder unbinder;


    private FilePreViewAdapter filePreViewAdapter;


    public static Fragment newsInstance(int position) {
        FilePreViewFragmentNew filePreViewFragment = new FilePreViewFragmentNew();
        Bundle bundle = new Bundle();
        bundle.putInt(POSITION, position);
        filePreViewFragment.setArguments(bundle);
        return filePreViewFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_preview_new, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        //newest to oldest order (database stores from oldest to newest)
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        filePreViewAdapter = new FilePreViewAdapter(getActivity(), linearLayoutManager);
        recyclerView.setAdapter(filePreViewAdapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
