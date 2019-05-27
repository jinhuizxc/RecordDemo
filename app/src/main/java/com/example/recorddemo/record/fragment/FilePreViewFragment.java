package com.example.recorddemo.record.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.support.annotation.NonNull;
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
public class FilePreViewFragment extends Fragment {

    private static final String POSITION = "position";
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.rl_file_preview)
    RelativeLayout rlFilePreview;

    Unbinder unbinder;

//    private int position;

    private FilePreViewAdapter filePreViewAdapter;

    public static Fragment newsInstance(int position) {
        FilePreViewFragment filePreViewFragment = new FilePreViewFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(POSITION, position);
        filePreViewFragment.setArguments(bundle);
        return filePreViewFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_file_preview, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        observer.startWatching();

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

    FileObserver observer =
            new FileObserver(Environment.getExternalStorageDirectory().toString()
                    + "/SoundRecorder") {
                // set up a file observer to watch this directory on sd card
                @Override
                public void onEvent(int event, String file) {
                    if (event == FileObserver.DELETE) {
                        // user deletes a recording file out of the app

                        String filePath = Environment.getExternalStorageDirectory().toString()
                                + "/SoundRecorder" + file + "]";

//                        Logger.d("File deleted ["
//                                + Environment.getExternalStorageDirectory().toString()
//                                + "/SoundRecorder" + file + "]");
                        // remove file from database and recyclerview
                        filePreViewAdapter.removeOutOfApp(filePath);
                    }
                }
            };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
