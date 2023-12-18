package com.kiksoftnet.termotest2;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class TCPReceiver extends AsyncTask<Void, Void, String> {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String serverIP;
    private int serverPort;

    private List<String> receivedDataList;
    // Bağlantıyı başlat
//    public void startConnection() {
//        try {
//            socket = new Socket("192.168.2.245", 9876); // TODO DEĞİŞTİRİLECEK
//            out = new PrintWriter(socket.getOutputStream(), true);
//            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//        } catch (IOException e) {
//            Log.e("TcpReceiver", "Bağlantı hatası: " + e.getMessage());
//        }
//    }

    // Bağlantıyı durdur
    public TCPReceiver(String serverIP, int serverPort) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }

    public void stopConnection() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            Log.e("TcpReceiver", "Bağlantı kapatma hatası: " + e.getMessage());
        }
    }

    private Timer timer;
    private Handler handler = new Handler(Looper.getMainLooper());
    @Override
    protected String doInBackground(Void... params) {
        try {
            Socket socket = new Socket("192.168.2.245", 9876); // Sunucu IP ve portunu buraya ekleyin
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            boolean isRecieved = false;



            out.print('m');
            out.flush();

//            while(!isRecieved){
//                String receivedData = in.readLine();
//                if(!receivedData.equals("OK")&&!receivedData.equals(null)){
//                    isRecieved=true;
//                }
//                try {
//                    Thread.sleep(20000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//            }
            List<String> receivedDataList = new ArrayList<>();
            while (!isCancelled()) {
                // Sunucudan gelen veriyi al
                if (in.ready()) {
                    // Sunucudan gelen veriyi al
                    String receivedData = in.readLine();
                    Log.d("TcpReceiver", "Alınan Veri: " + receivedData);

                    // Cevapları listeye ekle
                    receivedDataList.add(receivedData);
                }
                // 10 saniye beklet
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }




            // Sunucudan gelen veriyi al
            //String receivedData = in.readLine();

            //Log.d("TcpReceiver", "Alınan Veri: " + receivedData);

            // Bağlantıları kapat
            in.close();
            out.close();
            socket.close();

            return null;
        } catch (IOException e) {
            Log.e("TcpReceiver", "Hata: " + e.getMessage());
            return null;
        }
    }
    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            // MainActivity'deki bir metodu çağırarak alınan veriyi kullanabilirsiniz
            if(!result.equals("OK")){
                MainActivity mainActivityInstance = new MainActivity();
                mainActivityInstance.handleReceivedData(result);
            }
        }

    }
//    public String startAndReceiveData() {
//        String receivedData = null;
//
//        try {
//            socket = new Socket("192.168.2.245", 9876);
//            out = new PrintWriter(socket.getOutputStream(), true);
//            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//
//            out.print('m');
//            out.flush();
//
//            // Sunucudan gelen veriyi al
//            receivedData = in.readLine();
//            Log.d("TcpReceiver", "Alınan Veri: " + receivedData);
//        } catch (IOException e) {
//            Log.e("TcpReceiver", "Bağlantı veya veri alma hatası: " + e.getMessage());
//        } finally {
////            try {
////                // Bağlantıları kapat
////                if (out != null) out.close();
////                if (in != null) in.close();
////                if (socket != null) socket.close();
////            } catch (IOException e) {
////                Log.e("TcpReceiver", "Bağlantı kapatma hatası: " + e.getMessage());
////            }
//        }
//
//        return receivedData;
//    }

}
