package com.kiksoftnet.termotest2.Update;

import android.content.Context;
import android.os.AsyncTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadFile extends AsyncTask<String, Integer, String> {
//    public AsyncResponse delegate = null;//C

    /*
        public interface AsyncResponse {
            void processFinish(Object output);
        }
    */
    private Context mContext;
    private String mFileName;
    private String fpath;
    public DownloadFile(Context context, String fileName) { //AsyncResponse asyncResponse,

        mContext = context;
        mFileName=fileName;
        fpath=context.getFilesDir().toString();
//        delegate = asyncResponse;//Assigning call back interface through constructor
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... sUrl) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            File file = new File(fpath+ "/"+mFileName+".apk");
            output = new FileOutputStream(fpath+ "/"+mFileName+".apk");
            URL url = new URL(sUrl[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }
            for (int j=0;j<connection.getHeaderFields().size();j++)
                System.out.println( connection.getHeaderField(j));
            //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            //    FileUtils.copy(connection.getInputStream(),output);
            //}else {


            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();
            System.out.println("Dosya boyutu:"+fileLength);
            // download the file
            input = connection.getInputStream();

//            output = new FileOutputStream("/data/data/com.example.vadym.test1/textfile.txt");

            byte[] data = new byte[1024];
            long total = 0;
            int count = 0;

            System.out.println(fpath);
            //Looper.prepare();
            //Toast.makeText(mContext, "İndirme başlatılıyor...",
            //        Toast.LENGTH_LONG).show();

            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    output.close();
                    file = new File(fpath + "/" + "uretim" + ".apk");
                    if (file.exists()) {
                        file.delete();
                    }
                    return "İptal Edildi";
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }

            InstallFile installFile = new InstallFile(mContext);
            installFile.installFile();

        } catch (Exception e) {
            System.out.println("HATA" + e.toString());
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();

        }

        return "";
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
       //super.onProgressUpdate(values);
       // Log.d("ptg", "onProgressUpdate: " + values[0]);

    }

    @Override
    protected void onPostExecute(String s) {

        //delegate.processFinish(s);
    }


}
//usage
// DownloadVideoAsyncTask async = new DownloadVideoAsyncTask(this);
// async.execute("www.site.com/idvideo.apk");
