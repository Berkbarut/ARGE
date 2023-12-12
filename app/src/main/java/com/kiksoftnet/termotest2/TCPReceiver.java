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
    public void startConnection() {
        try {
            socket = new Socket("server_ip", 12345); // Sunucu IP ve portunu buraya ekleyin
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            Log.e("TcpReceiver", "Bağlantı hatası: " + e.getMessage());
        }
    }

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
        // G harfini gönder
        out.print('g');
        out.flush();

        // Sunucudan gelen veriyi oku
        try {
            String receivedData = in.readLine();
            Log.d("TcpReceiver", "Alınan Veri: " + receivedData);
            return receivedData;
        } catch (IOException e) {
            Log.e("TcpReceiver", "Veri okuma hatası: " + e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(String result) {
        // Yapılacak işlemleri burada gerçekleştirin
        // Örneğin, MainActivity içinde bir metod çağırabilirsiniz
    }
}
