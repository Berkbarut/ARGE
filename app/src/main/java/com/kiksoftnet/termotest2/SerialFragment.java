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
import android.widget.TextView;
import android.widget.Toast;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import com.hoho.android.usbserial.util.SerialInputOutputManager;
import com.kiksoftnet.termotest2.BuildConfig;
import java.io.IOException;
import java.util.Arrays;


public class SerialFragment  implements SerialInputOutputManager.Listener {
    private MainActivity parentActivity;

    private enum UsbPermission {Unknown, Requested, Granted, Denied}


    private static final String INTENT_ACTION_GRANT_USB = BuildConfig.APPLICATION_ID + ".GRANT_USB";
    private static final int WRITE_WAIT_MILLIS = 2000;
    private static final int READ_WAIT_MILLIS = 2000;
    private String gelen = "";
    private int deviceId, portNum, baudRate;
    private boolean withIoManager;
    private TextView olcumView;
    private TextView durumView;
    private TextView isEmriView;
    private TextView islemSayisi;
    //    private final BroadcastReceiver broadcastReceiver;
    private final Handler mainLooper;
    //private TextView receiveText;
    //private ControlLines controlLines;

    private SerialInputOutputManager usbIoManager;
    private UsbSerialPort usbSerialPort;
    private UsbPermission usbPermission = UsbPermission.Unknown;
    private UsbDevice device;
    private double tartiDegeri = 0;
    private boolean connected = true;

    public SerialFragment(MainActivity parentActivity, int deviceId, UsbDevice device) {

        this.parentActivity = parentActivity;

        this.device = device;
        this.deviceId = device.getDeviceId();
        portNum = 0;
        baudRate = 9600;
        withIoManager = true;
        try {
            connect();
        } catch (Exception ex) {
            status("seri port bağlantı hatası");
        }
        mainLooper = new Handler(Looper.getMainLooper());

    }

    public boolean isConnected() {
        return connected;
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
                status("bağlantı hatası" + e.getMessage());
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
                if (!usbManager.hasPermission(driver.getDevice()))
                    status("yetki hatası");
                else
                    status("bağlantı başarısız");
                return;
            }

            try {
                usbSerialPort.open(usbConnection);

                usbSerialPort.setParameters(baudRate, 8, 1, UsbSerialPort.PARITY_NONE);
                if (withIoManager) {
                    usbIoManager = new SerialInputOutputManager(usbSerialPort, this);
                    usbIoManager.start();
                }
                connected = true;
                //controlLines.start();
                //status("TerBağlandı...");
            } catch (Exception e) {
                status("bağlantı hatası" + e.getMessage());
                disconnect();
            }
        } catch (Exception exx) {
            status("bağlantı hatası");
        }
    }

    public void disconnect() {
        try {
            connected = false;
            //controlLines.stop();
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
        // status("Tartı bağlantısı kesildi");
    }

    private void send(String str) {
        try {
            if (!connected) {
                Toast.makeText(parentActivity, "not connected", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                byte[] data = (str + '\n').getBytes();
                SpannableStringBuilder spn = new SpannableStringBuilder();
                spn.append("send " + data.length + " bytes\n");
                spn.append(HexDump.dumpHexString(data)).append("\n");
                //spn.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorSendText)), 0, spn.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                status(spn.toString());
                usbSerialPort.write(data, WRITE_WAIT_MILLIS);
            } catch (Exception e) {
                onRunError(e);
            }
        } catch (Exception ex) {
            status("sendError");
        }
    }

    private void read() {
        try {
            if (!connected) {
                Toast.makeText(parentActivity, "not connected", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                byte[] buffer = new byte[8192];
                int len = usbSerialPort.read(buffer, READ_WAIT_MILLIS);
                receive(Arrays.copyOf(buffer, len));
            } catch (IOException e) {
                // when using read with timeout, USB bulkTransfer returns -1 on timeout _and_ errors
                // like connection loss, so there is typically no exception thrown here on error
                status("bağlantı hatası" + e.getMessage());
                disconnect();
            }
        } catch (Exception ex) {
            status("okuma hatası");
        }
    }

    private void receive(byte[] data) {
        try {

            SpannableStringBuilder spn = new SpannableStringBuilder();
            //spn.append("receive " + data.length + " bytes\n");
            if (data.length > 0)
                spn.append(HexDump.dumpHexString(data)).append("\n");
            String gelentmp = new String(data);
            gelen = gelen + gelentmp;

            if (gelen.indexOf("g") != -1) {
                gelentmp = gelen.substring(0, gelen.indexOf("g") + 1);
                if (gelentmp.indexOf("S") != -1) {
                    gelentmp = gelentmp.substring(gelentmp.indexOf("S") + 1);
                    if (gelentmp.indexOf("S") != -1) {
                        gelentmp = gelentmp.substring(gelentmp.indexOf("S") + 1);
                    }
                    if (gelentmp.indexOf("k")!=-1)
                        gelentmp = gelentmp.substring(0, gelentmp.indexOf("k"));
                    gelentmp = gelentmp.replace("+", "");
                    gelentmp = gelentmp.replace("-", "");
                    gelentmp = gelentmp.replace("kg", "");
                    gelentmp = gelentmp.replace(" ", "");
                    Double gelentmpdbl=0.00;
                    try {
                        gelentmpdbl = Double.valueOf(gelentmp);
                    }catch(Exception ex)
                    {
                        gelentmpdbl=0.00;
                    }

                    //Olculen değer sınır değerler içerisinde ise kaydın yapılması
                    //ölçümün sonlandırılması
                    //Durumun barkod okuma aşamasına çevrilmesi


                }
                gelen = "";
            }

        } catch (Exception ex) {
            //status("receiveError:"+ex.toString());
        }
        //TextView uyariView = (TextView) parentActivity.findViewById(R.id.islemUyari);
        //uyariView.setText(receiveText.getText());
    }

    void status(String str) {
        try {
            //Toast.makeText(terminal.getParentActivity().getApplicationContext(),str,Toast.LENGTH_SHORT);
            //TextView uyariView = (TextView) parentActivity.findViewById(R.id.islemUyari);
            //uyariView.setText(str);
        } catch (Exception ex) {

        }
    }
}
