package com.kiksoftnet.termotest2.Update;

import androidx.appcompat.app.AppCompatActivity;
//import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.kiksoftnet.termotest2.MainActivity;
import com.kiksoftnet.termotest2.ValueHelper;
import com.kiksoftnet.termotest2.R;

public class Denetleyici extends AppCompatActivity {

    //FullscreenActivity activity=null;
//    ServiceManager serviceManager;
    String versiyon= ValueHelper.version;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denetleyici);
        try {
            SharedPreferences sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(this /* Activity context */);
            String sunucu=sharedPreferences.getString("sunucu", "");

            //if (sunucu.equalsIgnoreCase(""))
            //    sunucu="192.168.1.34:8081";
            Toast.makeText(this, "Sunucu:"+sunucu, Toast.LENGTH_LONG).show();
            String versiyon = new Versiyon_Kontrol(sunucu).execute().get();
            if(versiyon.equalsIgnoreCase("0")){
                startActivity(new Intent(Denetleyici.this, MainActivity.class));
            }
            else{
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                DownloadFile dw = new DownloadFile(getApplicationContext(), "uretim");
                dw.execute("http://"+sunucu+"/kurulum/uretim.apk");
            }
        }catch(Exception ex)
        {
            startActivity(new Intent(Denetleyici.this, MainActivity.class));
        }
    }

    private class Versiyon_Kontrol extends AsyncTask<String,Void,String>
    {
        private String Sunucu="";
        public Versiyon_Kontrol(String sunucu)
        {
            Sunucu=sunucu;
        }
        @Override
        protected String doInBackground(String... strings) {
            try {

                ServiceManager serviceManager = new ServiceManager();
                String guncelveriyon = serviceManager.myPushData("URETIM",Sunucu);
                if (guncelveriyon.equalsIgnoreCase("-1")) {
                    return "0";
                }
                if (!guncelveriyon.equalsIgnoreCase(versiyon)) {

                    return "1";
                } else {
                    return "0";
                }
            } catch (Exception ex) {

                return "0";
            }

        }

    }

}
