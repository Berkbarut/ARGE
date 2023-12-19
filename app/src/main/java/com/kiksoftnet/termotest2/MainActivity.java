package com.kiksoftnet.termotest2;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private EditText editTextCalibration;
    private Button buttonDecreaseCalibration;
    private Button buttonIncreaseCalibration;
    double calibrationValue = 0;


    private EditText editTextPozitif;
    private Button buttonDecreasePozitif;
    private Button buttonIncreasePozitif;
    double histerisizPozitifValue = 0.1;

    private EditText editTextNegatif;
    private Button buttonDecreaseNegatif;
    private Button buttonIncreaseNegatif;
    double histerisizNegatifValue = -0.1;

    private RadioGroup radioGroupHeatCool;
    private RadioButton radioButtonHeat;
    private RadioButton radioButtonCool;

    private Spinner spinnerFunction;
    private EditText editTextProduct;
    private EditText editTextIslemci;
    private Spinner spinnerTip;
    private WebView webView;

    private TextView textViewStartTime;
    private TextView textViewEndTime;

    private EditText editTextAralik;

    private Button createChartButton;
    private Button reportButton;

    private TextureView textureView;
    private Button startButton;
    private Button stopButton;
    private CameraDevice cameraDevice; // Kamerayı başlatırken kullanılır
    private boolean isCameraStarted = false; // Kameranın durumu
    private String cameraId; // Kameranın kimliği
    private CameraCaptureSession cameraCaptureSessions;
    private CaptureRequest captureRequest;
    private CameraDevice.StateCallback stateCallback;
    private CameraManager cameraManager;
    private static final int CAMERA_REQUEST_CODE = 1001; // Kamera isteği için bir istek kodu
    String heatCoolString="Heat";
    String fonksiyonString="TPI";
    String urunString="";
    String islemciString="";
    String tipString="Kablolu";
    private TCPReceiver tcpReceiver;
    private String receivedData;

    private static String ip = "192.168.1.223";// this is the host ip that your data base exists on you can use 10.0.2.2 for local host                                                    found on your pc. use if config for windows to find the ip if the database exists on                                                    your pc
    private static String port = "1433";// the port sql server runs on
    private static String Classes = "net.sourceforge.jtds.jdbc.Driver";// the driver that is required for this connection use                                                                           "org.postgresql.Driver" for connecting to postgresql
    private static String database = "Arge";// the data base name
    private static String username = "kiksoft";// the user name
    private static String password = "kik++--**123";// the password
    private static String url = "jdbc:jtds:sqlserver://"+ip+":"+port+"/"+database; // the connection url string
    private Connection connection = null;
    private static final String SERVER_IP = "192.168.2.245"; // Sunucu IP'si
    private static final int SERVER_PORT = 9876; // Sunucu portu
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Kalibrasyon
        editTextCalibration = findViewById(R.id.editTextCalibration);
        buttonDecreaseCalibration = findViewById(R.id.buttonDecreaseCalibration);
        buttonIncreaseCalibration = findViewById(R.id.buttonIncreaseCalibration);

        // Histerisiz Pozitif
        editTextPozitif = findViewById(R.id.editTextPozitif);
        buttonDecreasePozitif = findViewById(R.id.buttonDecreasePozitif);
        buttonIncreasePozitif = findViewById(R.id.buttonIncreasePozitif);

        // Histerisiz Negatif
        editTextNegatif = findViewById(R.id.editTextNegatif);
        buttonDecreaseNegatif = findViewById(R.id.buttonDecreaseNegatif);
        buttonIncreaseNegatif = findViewById(R.id.buttonIncreaseNegatif);

        // Heat/Cool
        radioGroupHeatCool = findViewById(R.id.radioGroupHeatCool);
        radioButtonHeat = findViewById(R.id.radioButtonHeat);
        radioButtonCool = findViewById(R.id.radioButtonCool);

        // Fonksiyon
        spinnerFunction = findViewById(R.id.FonksiyonSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.function_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFunction.setAdapter(adapter);

        // Ürün
        editTextProduct = findViewById(R.id.editTextUrun);


        // İşlemci
        editTextIslemci = findViewById(R.id.editTextIslemci);

        // Tip
        spinnerTip = findViewById(R.id.spinnerTip);
        ArrayAdapter<CharSequence> tipAdapter = ArrayAdapter.createFromResource(this, R.array.tip_options, android.R.layout.simple_spinner_item);
        tipAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTip.setAdapter(tipAdapter);

        //Aralık
        editTextAralik = findViewById(R.id.editTextAralik);

        editTextCalibration.setText(String.valueOf(calibrationValue));
        buttonDecreaseCalibration.setOnClickListener(new View.OnClickListener() { //Kalibrasyon kısmında değer girme
            @Override
            public void onClick(View v) {
                if (calibrationValue > -8.0) {
                    calibrationValue-=0.1;

                    editTextCalibration.setText(String.format("%.1f",calibrationValue));
                    String temp=String.format("%.1f",calibrationValue);
                    temp=temp.replace(",", ".");
                    calibrationValue=Double.parseDouble(temp);
                }
            }
        });

        buttonIncreaseCalibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calibrationValue < 8.0) {
                    calibrationValue+=0.1;
                    editTextCalibration.setText(String.format("%.1f",calibrationValue));
                    String temp=String.format("%.1f",calibrationValue);
                    temp=temp.replace(",", ".");
                    calibrationValue=Double.parseDouble(temp);

                }
            }
        });

        editTextPozitif.setText(String.valueOf(histerisizPozitifValue));
        buttonDecreasePozitif.setOnClickListener(new View.OnClickListener() {//Histerisiz pozitif değer girme
            @Override
            public void onClick(View v) {
                if (histerisizPozitifValue > 0.1) {
                    histerisizPozitifValue -= 0.1;
                    editTextPozitif.setText(String.format("%.1f",histerisizPozitifValue));
                    String temp=String.format("%.1f",histerisizPozitifValue);
                    temp=temp.replace(",", ".");
                    histerisizPozitifValue=Double.parseDouble(temp);
                }
            }
        });

        buttonIncreasePozitif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (histerisizPozitifValue < 2.0) {
                    histerisizPozitifValue += 0.1;
                    editTextPozitif.setText(String.format("%.1f",histerisizPozitifValue));
                    String temp=String.format("%.1f",histerisizPozitifValue);
                    temp=temp.replace(",", ".");
                    histerisizPozitifValue=Double.parseDouble(temp);

                }
            }
        });

        editTextNegatif.setText(String.valueOf(histerisizNegatifValue));
        buttonDecreaseNegatif.setOnClickListener(new View.OnClickListener() {//Histerisiz negatif değer girme
            @Override
            public void onClick(View v) {
                if (histerisizNegatifValue > -2.0) {
                    histerisizNegatifValue -= 0.1;
                    editTextNegatif.setText(String.format("%.1f",histerisizNegatifValue));
                    String temp=String.format("%.1f",histerisizNegatifValue);
                    temp=temp.replace(",", ".");
                    histerisizNegatifValue=Double.parseDouble(temp);

                }
            }
        });

        buttonIncreaseNegatif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (histerisizNegatifValue < -0.1) {
                    histerisizNegatifValue += 0.1;
                    editTextNegatif.setText(String.format("%.1f",histerisizNegatifValue));
                    String temp=String.format("%.1f",histerisizNegatifValue);
                    temp=temp.replace(",", ".");
                    histerisizNegatifValue=Double.parseDouble(temp);

                }
            }
        });

        radioGroupHeatCool.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { //Heat/Cool seçim
            @Override

            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButtonHeat) {
                    // Sıcak seçildi
                    heatCoolString="Heat";
                } else if (checkedId == R.id.radioButtonCool) {
                    // Soğuk seçildi
                    heatCoolString="Cool";
                }
            }
        });

        spinnerFunction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { //Fonksiyon seçme spinner
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedFunction = (String) spinnerFunction.getSelectedItem();
                if (selectedFunction.equals("TPI")) {
                    // TPI seçildiğinde yapılacak işlemler
                    fonksiyonString="TPI";
                } else if (selectedFunction.equals("Modülasyon")) {
                    // Modülasyon seçildiğinde yapılacak işlemler
                    fonksiyonString="Modulasyon";
                } else if (selectedFunction.equals("ON/OFF")) {
                    // ON/OFF seçildiğinde yapılacak işlemler
                    fonksiyonString="ON/OFF";
                } else if (selectedFunction.equals("Akıllı")) {
                    // Akıllı seçildiğinde yapılacak işlemler
                    fonksiyonString="Akilli";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Bir şey seçilmediğinde yapılacak işlemler
            }
        });

        spinnerTip.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { //Tip seçme spinner
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedTip = (String) spinnerTip.getSelectedItem();
                if (selectedTip.equals("Kablolu")) {
                    // Kablolu seçildiğinde yapılacak işlemler
                    tipString="Kablolu";
                } else if (selectedTip.equals("Kablosuz")) {
                    // Kablosuz seçildiğinde yapılacak işlemler
                    tipString="Kablosuz";
                } else if (selectedTip.equals("Wifi")) {
                    // Wifi seçildiğinde yapılacak işlemler
                    tipString="Wifi";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Bir şey seçilmediğinde yapılacak işlemler
            }
        });
        textViewStartTime = findViewById(R.id.textViewStartTime);
        textViewEndTime = findViewById(R.id.textViewEndTime);

        textViewStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(true);
            }
        });

        textViewEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(false);
            }
        });

        createChartButton = findViewById(R.id.createChartButton);
        createChartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Grafiği oluşturmak için gerekli kod
            }
        });
        reportButton = findViewById(R.id.reportButton);

        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(reportButton); // Pop-up menüyü çağırma
            }
        });


        startButton = findViewById(R.id.startCameraButton);
        stopButton = findViewById(R.id.stopCameraButton);


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCameraStarted) {

                    isCameraStarted = true;
                    startRefreshTimer();

//
//                    websiteRefreshTimer = new Timer();
//                    websiteRefreshTimer.scheduleAtFixedRate(new TimerTask() {
//                        @Override
//                        public void run() {
//                            handler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Log.d("KAMERA: ", "YENİLENDİ");
//
//                                    loadWebsite();
//
//                                    new TCPReceiver(SERVER_IP,SERVER_PORT,MainActivity.this).execute();
//
//
//
//                                }
//                            });
//                        }
//                    }, 0, WEBSITE_REFRESH_INTERVAL);

                }
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCameraStarted) {
                    isCameraStarted = false;

                    // TCPReceiver'ı durdur
                    stopRefreshTimer();
                }
            }
        });




        Button buttonSave = findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                urunString=editTextProduct.getText().toString();
                islemciString=editTextIslemci.getText().toString();

                // Kullanıcının girdiği verileri al

                // Verileri MSSQL veritabanına kaydet
                saveDataToMSSQL((float)calibrationValue,(float) histerisizPozitifValue,(float)histerisizNegatifValue,heatCoolString,fonksiyonString,urunString,islemciString,tipString);
            }
        });

        webView = findViewById(R.id.webView);

        configureWebView();

       // loadWebsite();
//
//        websiteRefreshTimer = new Timer();
//        websiteRefreshTimer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        Log.d("KAMERA: ", "YENİLENDİ");
//
//                        loadWebsite();
//
//                        new TCPReceiver(SERVER_IP,SERVER_PORT,MainActivity.this).execute();
//
//
//
//                    }
//                });
//            }
//        }, 0, WEBSITE_REFRESH_INTERVAL);




    }
    private void startRefreshTimer() {
        if(!editTextAralik.getText().toString().equals("")){
            WEBSITE_REFRESH_INTERVAL = Integer.parseInt(editTextAralik.getText().toString());
            WEBSITE_REFRESH_INTERVAL=WEBSITE_REFRESH_INTERVAL*1000;
            editTextAralik.setEnabled(false);
            if(WEBSITE_REFRESH_INTERVAL > 0){
                websiteRefreshTimer = new Timer();
                websiteRefreshTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("KAMERA: ", "YENİLENDİ");

                                loadWebsite();

                                new TCPReceiver(SERVER_IP, SERVER_PORT, MainActivity.this).execute();
                            }
                        });
                    }
                }, 0, WEBSITE_REFRESH_INTERVAL);

                startButton.setEnabled(false);
                stopButton.setEnabled(true);
            }
        }
        else
            Toast.makeText(this, "Aralık değeri girin", Toast.LENGTH_SHORT).show();

    }

    // Timer'ı durduran metod
    private void stopRefreshTimer() {
        if (websiteRefreshTimer != null) {
            websiteRefreshTimer.cancel();
            websiteRefreshTimer.purge();
            websiteRefreshTimer = null;

            startButton.setEnabled(true);
            stopButton.setEnabled(false);
            editTextAralik.setEnabled(true);
        }
    }
    public int WEBSITE_REFRESH_INTERVAL = 0; // 5 saniyede bir yenileme aralığı
    private Timer websiteRefreshTimer;
    private final Handler handler = new Handler(Looper.getMainLooper());

    private void configureWebView() {
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true); // Gerekirse JavaScript'i etkinleştirin
        // Diğer WebView ayarlarını burada konfigure edebilirsiniz
    }

    private void loadWebsite() {
        String websiteUrl = "http://192.168.1.222:8080/uretim/uretimg.nsf/Arge.xsp";
        webView.loadUrl(websiteUrl);
    }





    public void handleReceivedData(String receivedDataString) {
        Log.d("MainActivity", "Gelen Veri: " + receivedDataString);

        StringBuilder receivedData = new StringBuilder(receivedDataString);

        String roomTemp = getSubstringBetween(receivedData, "86", "254");
        String setTemp = getSubstringBetween(receivedData, "72", "254");
        String batteryLevel = getSubstringBetween(receivedData, "84", "254");
        String comfortMode = getSubstringBetween(receivedData, "96", "254");
        String programMode = getSubstringBetween(receivedData, "112", "254");
        String ecoMode = getSubstringBetween(receivedData, "92", "254");
        String minute = getSubstringBetween(receivedData, "184", "254");
        String hour = getSubstringBetween(receivedData, "147", "254");
        String weekday = getSubstringBetween(receivedData, "192", "254");
        String activeProgram = getSubstringBetween(receivedData, "198", "254");
        String lockStatus = getSubstringBetween(receivedData, "220", "254");
        String segmentStatus = getSubstringBetween(receivedData, "246", "254");
        String systemOnOff = getSubstringBetween(receivedData, "126", "254");




        saveTcpDataToMSSQL(roomTemp,setTemp,batteryLevel,comfortMode,programMode,ecoMode,minute,hour,weekday,activeProgram,lockStatus,segmentStatus, systemOnOff);



    }
//    private String getSubstringBetween(String original, String start, String end) {
//        int startIndex = original.indexOf(start);
//        int endIndex = original.indexOf(end, startIndex + start.length());
//
//        if (startIndex != -1 && endIndex != -1) {
//            return original.substring(startIndex + start.length(), endIndex);
//        } else {
//            return "";
//        }
//    }

    private String getSubstringBetween(StringBuilder original, String start, String end) {
        int startIndex = original.indexOf(start);
        int endIndex = original.indexOf(end, startIndex + start.length());

        if (startIndex != -1 && endIndex != -1) {
            String result = original.substring(startIndex + start.length(), endIndex);
            original.delete(startIndex, endIndex + end.length()); // Alt dizgiyi temizle
            return result;
        } else {
            return "";
        }
    }



    private void saveTcpDataToMSSQL(String roomTemp, String setTemp, String batteryLevel, String comfortMode, String programMode, String ecoMode, String minute, String hour, String weekday, String activeProgram, String lockStatus, String segmentStatus, String systemOnOff) {


        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            Class.forName(Classes);
            connection = DriverManager.getConnection(url,username,password);
            String insertProcedure = "{call InsertArge(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

            try (CallableStatement callableStatement = connection.prepareCall(insertProcedure)) {
                callableStatement.setObject(1, null);
                callableStatement.setObject(2, null);
                callableStatement.setFloat(3, Float.parseFloat(setTemp)/10);
                callableStatement.setFloat(4, Float.parseFloat(roomTemp)/10);
                callableStatement.setObject(5, null);
                callableStatement.setObject(6, null);
                callableStatement.setInt(7, Integer.parseInt(batteryLevel));
                callableStatement.setObject(8, null);
                callableStatement.setString(9, activeProgram);
                callableStatement.setObject(10, null);
                callableStatement.setInt(11, Integer.parseInt(comfortMode));
                callableStatement.setInt(12, Integer.parseInt(programMode));
                callableStatement.setInt(13, Integer.parseInt(ecoMode));
                callableStatement.setInt(14, Integer.parseInt(minute));
                callableStatement.setInt(15, Integer.parseInt(hour));
                callableStatement.setInt(16, Integer.parseInt(weekday));
                callableStatement.setInt(17, Integer.parseInt(lockStatus));
                callableStatement.setInt(18, Integer.parseInt(segmentStatus));
                callableStatement.setInt(19, Integer.parseInt(systemOnOff));

                callableStatement.execute();
                // Prosedür başarıyla çağrıldı
                //Toast.makeText(this, "VERİLER GÖNDERİLDİ", Toast.LENGTH_SHORT).show();
            } catch (SQLException e) {
                System.out.println("SQL Server Hatası");
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.out.println("SQL Server Bağlanılamadı");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                connection = null;
            }
        }
    }





    private void saveDataToMSSQL(float calibrationValue, float pozitifValue, float negatifValue, String heatCoolValue, String functionValue, String productValue, String islemciValue, String tipValue) {

        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            Class.forName(Classes);
            connection = DriverManager.getConnection(url,username,password);
            String insertProcedure = "{call InsertData(?, ?, ?, ?, ?, ?, ?, ?)}";

            try (CallableStatement callableStatement = connection.prepareCall(insertProcedure)) {
                callableStatement.setFloat(1, calibrationValue);
                callableStatement.setFloat(2, pozitifValue);
                callableStatement.setFloat(3, negatifValue);
                callableStatement.setString(4, heatCoolValue);
                callableStatement.setString(5, functionValue);
                callableStatement.setString(6, productValue);
                callableStatement.setString(7, islemciValue);
                callableStatement.setString(8, tipValue);

                callableStatement.execute();
                // Prosedür başarıyla çağrıldı
                Toast.makeText(this, "VERİLER GÖNDERİLDİ", Toast.LENGTH_SHORT);
            } catch (SQLException e) {
                System.out.println("SQL Server Hatası");
                e.printStackTrace();
            }
        } catch (SQLException e) {
            System.out.println("SQL Server Bağlanılamadı");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                connection = null;
            }
        }
    }

    private void showDateTimePicker(final boolean isStartTime) {// Zaman seçmek için fonksiyon
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                String selectedDate = dayOfMonth + "/" + (month + 1) + "/" + year; // Örnek bir tarih formatı
                if (isStartTime) {
                    // Başlangıç zamanı seçildiyse ilgili TextView'ı güncelle
                    TextView textViewStartTime = findViewById(R.id.textViewStartTime);
                    textViewStartTime.setText("Başlangıç Zamanı: " + selectedDate);
                } else {
                    // Bitiş zamanı seçildiyse ilgili TextView'ı güncelle
                    TextView textViewEndTime = findViewById(R.id.textViewEndTime);
                    textViewEndTime.setText("Bitiş Zamanı: " + selectedDate);
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String selectedTime = hourOfDay + ":" + minute; //Örnek saat dakika formatı
                if (isStartTime) {
                    TextView textViewStartTime = findViewById(R.id.textViewStartTime);
                    textViewStartTime.append(" Saat: " + selectedTime);
                } else {

                    TextView textViewEndTime = findViewById(R.id.textViewEndTime);
                    textViewEndTime.append(" Saat: " + selectedTime);
                }
            }
        }, hour, minute, true);

        timePickerDialog.show();
        datePickerDialog.show();

    }
    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.report_menu, popupMenu.getMenu());

        // Pop-up menüden bir seçenek seçildiğinde yapılacak işlemi tanımlayın
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.function_report:
                        // Fonksiyon raporu seçildi
                        // Fonksiyon raporu oluşturma işlemleri burada yapılabilir
                        return true;
                    case R.id.temperature_report:
                        // Sıcaklık raporu seçildi
                        // Sıcaklık raporu oluşturma işlemleri burada yapılabilir
                        return true;
                    case R.id.time_date_report:
                        // Saat & Gün raporu seçildi
                        // Saat & Gün raporu oluşturma işlemleri burada yapılabilir
                        return true;
                    case R.id.weekly_program_report:
                        // Haftalık Program raporu seçildi
                        // Haftalık Program raporu oluşturma işlemleri burada yapılabilir
                        return true;
                    case R.id.battery_report:
                        // Batarya raporu seçildi
                        // Batarya raporu oluşturma işlemleri burada yapılabilir
                        return true;
                    case R.id.wifi_report:
                        // Wi-Fi raporu seçildi
                        // Wi-Fi raporu oluşturma işlemleri burada yapılabilir
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Uygulama kapatıldığında timer'ı durdur
        if (websiteRefreshTimer != null) {
            websiteRefreshTimer.cancel();
            websiteRefreshTimer.purge();
        }
    }


}
