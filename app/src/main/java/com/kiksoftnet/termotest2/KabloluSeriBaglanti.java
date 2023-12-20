package com.kiksoftnet.termotest2;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.kiksoftnet.termotest2.BuildConfig;
import com.kiksoftnet.termotest2.R;

import java.io.IOException;
import java.util.Arrays;


public class KabloluSeriBaglanti  implements SerialInputOutputManager.Listener {
    private MainActivity parentActivity;

    private enum UsbPermission {Unknown, Requested, Granted, Denied}

    private static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";
    private static final int WRITE_WAIT_MILLIS = 2000;
    private static final int READ_WAIT_MILLIS = 5000;
    private String gelen = "";
    private int deviceId, portNum, baudRate;
    private boolean withIoManager;
    private final Handler mainLooper;

    private SerialInputOutputManager usbIoManager;
    private UsbSerialPort usbSerialPort;
    private UsbPermission usbPermission = UsbPermission.Unknown;
    private UsbDevice device;
    private boolean connected = true;

    public KabloluSeriBaglanti(MainActivity parentActivity, UsbDevice device) {

        this.parentActivity = parentActivity;
        this.device = device;
        portNum = 0;
        baudRate = 9600;
        withIoManager = true;

        try {
            connect();
        } catch (Exception ex) {


        }
        mainLooper = new Handler(Looper.getMainLooper());

    }
    /*
     * Serial
     */
    @Override
    public void onNewData(byte[] data) {
        try {
            mainLooper.post(() -> {
                receive(data);
            });
        } catch (Exception ex) {
            //status("onNewDataError");
        }
    }

    @Override
    public void onRunError(Exception e) {
        try {
            mainLooper.post(() -> {

                disconnect();
            });
        } catch (Exception ex) {
            //status("onRunError");
        }
    }

    /*
     * Serial + UI
     */
    public void connect() {
        try {

            UsbSerialDriver driver = UsbSerialProber.getDefaultProber().probeDevice(device);
            UsbManager usbManager = (UsbManager) parentActivity.getSystemService(Context.USB_SERVICE);
            usbSerialPort = driver.getPorts().get(portNum);
            UsbDeviceConnection usbConnection = usbManager.openDevice(driver.getDevice());
            if (usbConnection == null && usbPermission == UsbPermission.Unknown && !usbManager.hasPermission(driver.getDevice())) {
                usbPermission = UsbPermission.Requested;
                PendingIntent usbPermissionIntent = PendingIntent.getBroadcast(parentActivity, 0, new Intent(INTENT_ACTION_GRANT_USB), 0);
                usbManager.requestPermission(driver.getDevice(), usbPermissionIntent);
                return;
            }
            if (usbConnection == null) {
                return;
            }
            try {
                usbSerialPort.open(usbConnection);
                usbSerialPort.setParameters(9600, 8, 1, UsbSerialPort.PARITY_NONE);
                if (withIoManager) {
                    usbIoManager = new SerialInputOutputManager(usbSerialPort, this);
                    usbIoManager.start();
                }
                connected = true;
                //controlLines.start();
            } catch (Exception e) {
                disconnect();
            }
        } catch (Exception exx) {
        }
    }
    public void disconnect() {
        try {
            connected = false;
            if (usbIoManager != null) {
                usbIoManager.setListener(null);
                usbIoManager.stop();
            }
            usbIoManager = null;
            try {
                usbSerialPort.close();
            } catch (IOException ignored) {
            }
            usbSerialPort = null;
        } catch (Exception ex) {
            //status("disconnectError:"+ex.toString());
        }
    }

    public String read() {//BURASI DATA OKUMAK İÇİN
        String hexgelenveri;
        String gelenveri;
        try {
            if (!connected) {
                Toast.makeText(parentActivity, "not connected", Toast.LENGTH_SHORT).show();
                return "not connected";
            }
            try {
                byte[] buffer = new byte[4096];
                int len = usbSerialPort.read(buffer, READ_WAIT_MILLIS);
                hexgelenveri =bytesToHex(buffer);
                System.out.println("Gelen HEX:"+hexgelenveri);
                gelenveri = hexToString(hexgelenveri);
                System.out.println("Gelen:"+gelenveri);
            } catch (IOException e) {


                disconnect();
                return "hata";
            }
        } catch (Exception ex) {

            return "uyarı hatası";
        }
        return gelenveri;
    }
    public static String hexToString(String hex) {// GELEN HEX VERİSİNİ STRINGE ÇEVİRME
        StringBuilder sb = new StringBuilder();
        char[] hexData = hex.toCharArray();
        for (int count = 0; count < hexData.length - 1; count += 2) {
            int firstDigit = Character.digit(hexData[count], 16);
            int lastDigit = Character.digit(hexData[count + 1], 16);
            int decimal = firstDigit * 16 + lastDigit;
            sb.append((char) decimal);
        }
        return sb.toString();
    }
    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes();
    public static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
    private void receive(byte[] data) {
        try {
            String deger = bytesToHex(data);
        } catch (Exception ex) {
        }
    }

}
