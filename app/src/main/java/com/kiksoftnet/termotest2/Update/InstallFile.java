package com.kiksoftnet.termotest2.Update;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.core.content.FileProvider;

import com.kiksoftnet.termotest2.BuildConfig;


import java.io.File;
import java.util.Objects;

public class InstallFile {

    private final Context context;

    public InstallFile(Context context) {
        this.context = context;
    }

    public void installFile() {
        File file = new File(context.getFilesDir()+"/arge.apk");
        Intent promptInstall = new Intent(Intent.ACTION_VIEW);
        Uri fileUri =
                FileProvider.getUriForFile(Objects.requireNonNull(context.getApplicationContext()),
                        BuildConfig.APPLICATION_ID + ".provider", file);
        promptInstall.setDataAndType(fileUri, "application/vnd.android.package-archive");
        //promptInstall.setAction("com.example.deneme.InstallFile");
        promptInstall.addFlags(Intent
         .FLAG_GRANT_READ_URI_PERMISSION);
        promptInstall.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        promptInstall.addFlags(Intent.FLAG_ACTIVITY_NO_USER_ACTION);
        promptInstall.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE,true);

        promptInstall.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(promptInstall);
        System.out.println("aktivite çalıştı.");
    }
}
