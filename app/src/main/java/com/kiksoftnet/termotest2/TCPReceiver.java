package com.kiksoftnet.termotest2;

import android.os.AsyncTask;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TCPReceiver extends AsyncTask<Void, Void, String> {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

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
    public void stopConnection() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            Log.e("TcpReceiver", "Bağlantı kapatma hatası: " + e.getMessage());
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        try {
            Socket socket = new Socket("192.168.2.245", 9876); // Sunucu IP ve portunu buraya ekleyin
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // 'g' harfini gönder
            out.print('g');
            out.flush();

            // Sunucudan gelen veriyi al
            String receivedData = in.readLine();
            Log.d("TcpReceiver", "Alınan Veri: " + receivedData);

            // Bağlantıları kapat
            in.close();
            out.close();
            socket.close();

            return receivedData;
        } catch (IOException e) {
            Log.e("TcpReceiver", "Hata: " + e.getMessage());
            return null;
        }
    }
    @Override
    protected void onPostExecute(String result) {
        // Yapılacak işlemleri burada gerçekleştirin
        // Örneğin, MainActivity içinde bir metod çağırabilirsiniz
    }
    public String startAndReceiveData() {
        String receivedData = null;

        try {
            socket = new Socket("192.168.2.245", 9876);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.print('m');
            out.flush();

            // Sunucudan gelen veriyi al
            receivedData = in.readLine();
            Log.d("TcpReceiver", "Alınan Veri: " + receivedData);
        } catch (IOException e) {
            Log.e("TcpReceiver", "Bağlantı veya veri alma hatası: " + e.getMessage());
        } finally {
            try {
                // Bağlantıları kapat
                if (out != null) out.close();
                if (in != null) in.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                Log.e("TcpReceiver", "Bağlantı kapatma hatası: " + e.getMessage());
            }
        }

        return receivedData;
    }

}
