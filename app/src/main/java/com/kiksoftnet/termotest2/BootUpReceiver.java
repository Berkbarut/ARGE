package com.kiksoftnet.termotest2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;

import com.kiksoftnet.termotest2.MainActivity;
import com.kiksoftnet.termotest2.Update.Denetleyici;
import com.kiksoftnet.termotest2.Update.ServiceManager;



public class BootUpReceiver extends BroadcastReceiver {
    MainActivity activity=null;
    ServiceManager serviceManager;
    String serverIP = "terminal.signsoft.com.tr";
    int serverPort = 62221;
    @Override
/*
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context, FullscreenActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        //context.startActivity(new Intent(FullscreenActivity.this, FullscreenActivity.class));
        context.startActivity(i);
    }
*/
    public void onReceive(Context context, Intent intent) {

        //System.out.println("Yeni intent:"+intent.getAction());
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // 10 saniye sonra çalışacak kod

                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    // İnternet bağlantısı hazır
                    try {
                        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
                            Intent i = new Intent(context, Denetleyici.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                            context.startActivity(i);
                        }
                    } catch (Exception ex) {
                        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
                            Intent i = new Intent(context, MainActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                            context.startActivity(i);
                        }
                    }
                } else {
                    // İnternet bağlantısı hala hazır değil, belirli bir süre sonra tekrar deneyin
                    handler.postDelayed(this, 10000); // 10 saniye sonra tekrar dene
                }
            }
        }, 10000);

    }


}
