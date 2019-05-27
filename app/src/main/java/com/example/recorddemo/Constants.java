package com.example.recorddemo;

import android.Manifest;

public class Constants {


    public static final String AUTHORITY = "com.example.recorddemo.fileprovider";

    public static class Permission{

        public static final int REQUEST_CODE_STORE_AUDIO = 0x03;  // 存储、语音权限

        public static final String[] PERMS_STORE_AUDIO = {
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        };

    }
}
