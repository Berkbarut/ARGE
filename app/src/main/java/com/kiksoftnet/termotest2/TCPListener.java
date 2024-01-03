package com.kiksoftnet.termotest2;

//Analiz cihazı veri aktarımı

public interface TCPListener {
    public void onTCPMessageRecieved(String message);
    public void onTCPConnectionStatusChanged(boolean isConnectedNow);
    public void setOlcumDegeri(double olcumDegeri);
}

