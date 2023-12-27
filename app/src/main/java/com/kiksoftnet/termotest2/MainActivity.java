package com.kiksoftnet.termotest2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.os.SystemClock;
import android.text.InputType;
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
import android.widget.Chronometer;
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

import com.hoho.android.usbserial.driver.SerialTerminalFragment;
import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
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
    private Spinner islemciSpinner;
    private Spinner urunSpinner;

    private WebView webView;
    private WebView webView2;

    private Spinner spinnerAralik;
    private boolean aralikbirim;
    private List<String> urunList;
    private List<String> islemciList;



    private TextView textViewStartTime;
    private TextView textViewEndTime;

    private TextView textViewVersionCheck;

    private EditText editTextAralik;

    private Button createChartButton;
    private Button reportButton;

    private Chronometer chronometer;
    private boolean isRunning = false;
    private long pauseOffset = 0;

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
    String aralikBirimString = "sn";
    private TCPReceiver tcpReceiver;
    private String receivedData;
    private TextView olculenSic;
    private TextView sicaklikTest;

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
    private long startTime = 0;
    private boolean isTimerRunning = false;

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
        //editTextProduct = findViewById(R.id.editTextUrun);


        Button buttonSave = findViewById(R.id.buttonSave);


        // İşlemci
        //editTextIslemci = findViewById(R.id.editTextIslemci);

        // Tip
        spinnerTip = findViewById(R.id.spinnerTip);
        ArrayAdapter<CharSequence> tipAdapter = ArrayAdapter.createFromResource(this, R.array.tip_options, android.R.layout.simple_spinner_item);
        tipAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTip.setAdapter(tipAdapter);

        //Aralık
        editTextAralik = findViewById(R.id.editTextAralik);
        spinnerAralik=findViewById(R.id.spinnerAralikBirim);
        ArrayAdapter<CharSequence> aralikAdapter = ArrayAdapter.createFromResource(this, R.array.aralik_options, android.R.layout.simple_spinner_item);
        aralikAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAralik.setAdapter(aralikAdapter);


        editTextCalibration.setText(String.valueOf(calibrationValue));

        chronometer = findViewById(R.id.chronometer);

        olculenSic = findViewById(R.id.textViewOlculenSıcaklıK);
        olculenSic.setVisibility(View.INVISIBLE);
        //sicaklikTest.setVisibility(View.INVISIBLE);
        textViewVersionCheck = findViewById(R.id.textViewVersionCheck);
        textViewVersionCheck.setText(ValueHelper.version);



        getLists();

        getDataFromMSSQL();

        buttonDecreaseCalibration.setOnClickListener(new View.OnClickListener() { //Kalibrasyon kısmında değer girme
            @Override
            public void onClick(View v) {
                if (calibrationValue > -8.0) {
                    calibrationValue-=0.1;

                    editTextCalibration.setText(String.format("%.1f",calibrationValue));
                    String temp=String.format("%.1f",calibrationValue);
                    temp=temp.replace(",", ".");
                    calibrationValue=Double.parseDouble(temp);
                    buttonSave.setEnabled(true);
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

                    buttonSave.setEnabled(true);
                }
            }
        });

        editTextPozitif.setText(String.format("%.1f",histerisizPozitifValue));
        buttonDecreasePozitif.setOnClickListener(new View.OnClickListener() {//Histerisiz pozitif değer girme
            @Override
            public void onClick(View v) {
                if (histerisizPozitifValue > 0.1) {
                    histerisizPozitifValue -= 0.1;
                    editTextPozitif.setText(String.format("%.1f",histerisizPozitifValue));
                    String temp=String.format("%.1f",histerisizPozitifValue);
                    temp=temp.replace(",", ".");
                    histerisizPozitifValue=Double.parseDouble(temp);

                    buttonSave.setEnabled(true);
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

                    buttonSave.setEnabled(true);
                }
            }
        });

        editTextNegatif.setText(String.format("%.1f",histerisizNegatifValue));
        buttonDecreaseNegatif.setOnClickListener(new View.OnClickListener() {//Histerisiz negatif değer girme
            @Override
            public void onClick(View v) {
                if (histerisizNegatifValue > -2.0) {
                    histerisizNegatifValue -= 0.1;
                    editTextNegatif.setText(String.format("%.1f",histerisizNegatifValue));
                    String temp=String.format("%.1f",histerisizNegatifValue);
                    temp=temp.replace(",", ".");
                    histerisizNegatifValue=Double.parseDouble(temp);

                    buttonSave.setEnabled(true);
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

                    buttonSave.setEnabled(true);
                }
            }
        });

        radioGroupHeatCool.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() { //Heat/Cool seçim
            @Override

            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioButtonHeat) {
                    // Sıcak seçildi
                    heatCoolString="Heat";
                    buttonSave.setEnabled(true);
                } else if (checkedId == R.id.radioButtonCool) {
                    // Soğuk seçildi
                    heatCoolString="Cool";
                    buttonSave.setEnabled(true);
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

                    editTextPozitif.setEnabled(false);
                    buttonDecreasePozitif.setEnabled(false);
                    buttonIncreasePozitif.setEnabled(false);

                    editTextNegatif.setEnabled(false);
                    buttonDecreaseNegatif.setEnabled(false);
                    buttonIncreaseNegatif.setEnabled(false);

                    buttonSave.setEnabled(true);

//                    editTextPozitif.setText("");
//                    editTextNegatif.setText("");

                } else if (selectedFunction.equals("Modülasyon")) {
                    // Modülasyon seçildiğinde yapılacak işlemler
                    fonksiyonString="Modulasyon";

                    editTextPozitif.setEnabled(false);
                    buttonDecreasePozitif.setEnabled(false);
                    buttonIncreasePozitif.setEnabled(false);

                    editTextNegatif.setEnabled(false);
                    buttonDecreaseNegatif.setEnabled(false);
                    buttonIncreaseNegatif.setEnabled(false);

                    buttonSave.setEnabled(true);

//                    editTextPozitif.setText("");
//                    editTextNegatif.setText("");
                } else if (selectedFunction.equals("ON/OFF")) {
                    // ON/OFF seçildiğinde yapılacak işlemler
                    fonksiyonString="ON/OFF";

                    editTextPozitif.setEnabled(false);
                    buttonDecreasePozitif.setEnabled(true);
                    buttonIncreasePozitif.setEnabled(true);

                    editTextNegatif.setEnabled(false);
                    buttonDecreaseNegatif.setEnabled(true);
                    buttonIncreaseNegatif.setEnabled(true);

                    buttonSave.setEnabled(true);
//                    editTextPozitif.setText(String.format("%.1f",histerisizPozitifValue));
//                    editTextNegatif.setText(String.format("%.1f",histerisizNegatifValue));

                } else if (selectedFunction.equals("Akıllı")) {
                    // Akıllı seçildiğinde yapılacak işlemler
                    fonksiyonString="Akilli";

                    editTextPozitif.setEnabled(false);
                    buttonDecreasePozitif.setEnabled(true);
                    buttonIncreasePozitif.setEnabled(true);

                    editTextNegatif.setEnabled(false);
                    buttonDecreaseNegatif.setEnabled(true);
                    buttonIncreaseNegatif.setEnabled(true);

                    buttonSave.setEnabled(true);

//
//                    editTextPozitif.setText(String.format("%.1f",histerisizPozitifValue));
//                    editTextNegatif.setText(String.format("%.1f",histerisizNegatifValue));

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Bir şey seçilmediğinde yapılacak işlemler
            }
        });

        spinnerAralik.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedAralik = (String) spinnerAralik.getSelectedItem();
                if(selectedAralik.equals("Saniye")){
                    aralikbirim=true;
                    aralikBirimString="sn";
                    buttonSave.setEnabled(true);
                }
                else if(selectedAralik.equals("Dakika")){
                    aralikbirim=false;
                    aralikBirimString="dak";
                    buttonSave.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerTip.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() { //Tip seçme spinner
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedTip = (String) spinnerTip.getSelectedItem();
                if (selectedTip.equals("Kablolu")) {
                    // Kablolu seçildiğinde yapılacak işlemler
                    tipString="Kablolu";
                    buttonSave.setEnabled(true);
                } else if (selectedTip.equals("Kablosuz")) {
                    // Kablosuz seçildiğinde yapılacak işlemler
                    tipString="Kablosuz";
                    buttonSave.setEnabled(true);
                } else if (selectedTip.equals("Wifi")) {
                    // Wifi seçildiğinde yapılacak işlemler
                    tipString="Wifi";
                    buttonSave.setEnabled(true);
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
                buttonSave.setEnabled(true);
            }
        });

        textViewEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker(false);
                buttonSave.setEnabled(true);
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
                    startChronometer();
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
                    pauseChronometer();
                    stopRefreshTimer();
                }
            }
        });

        editTextAralik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonSave.setEnabled(true);
            }
        });












        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // urunString=editTextProduct.getText().toString();
                //islemciString=editTextIslemci.getText().toString();

                // Kullanıcının girdiği verileri al

                // Verileri MSSQL veritabanına kaydet
                saveDataToMSSQL((float)calibrationValue,(float) histerisizPozitifValue,(float)histerisizNegatifValue,heatCoolString,fonksiyonString,urunString,islemciString,tipString,baslangicDate,bitisDate,Integer.parseInt(editTextAralik.getText().toString()),aralikBirimString);
                Toast.makeText(getApplicationContext(),"KAYDETME BAŞARILI",Toast.LENGTH_LONG).show();
                buttonSave.setEnabled(false);

            }
        });

        webView = findViewById(R.id.webView);
        webView2 = findViewById(R.id.webView2);

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



        urunSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                buttonSave.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        islemciSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                buttonSave.setEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });









        Button getInputIslemciButton = findViewById(R.id.buttonGetInputIslemci);
        getInputIslemciButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialogIslemci();
            }
        });
        Button getInputUrunButton = findViewById(R.id.buttonGetInputUrun);
        getInputUrunButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialogUrun();
            }
        });









        Button buttonReset = findViewById(R.id.buttonReset);
        buttonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Uygulamayı yeniden başlatmak için Intent kullanabilirsiniz
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

    }

    private void showInputDialogUrun() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("ÜRÜN EKLEYİN");

        final EditText inputEditText = new EditText(this);
        inputEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(inputEditText);

        builder.setPositiveButton("EKLE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userInput = inputEditText.getText().toString();

                if(!userInput.equals("")){
                    saveUrunDataToMSSQL(userInput);
                    getLists();

                }
                else
                    Toast.makeText(getApplicationContext(),"LÜTFEN ALANI BOŞ BIRAKMAYINIZ",Toast.LENGTH_LONG).show();


            }
        });

        builder.setNegativeButton("İPTAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void showInputDialogIslemci() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("IŞLEMCİ EKLEYİN");

        final EditText inputEditText = new EditText(this);
        inputEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(inputEditText);

        builder.setPositiveButton("EKLE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String userInput = inputEditText.getText().toString();
                if(!userInput.equals("")){
                    saveIslemciDataToMSSQL(userInput);
                    getLists();
                }
                else
                    Toast.makeText(getApplicationContext(),"LÜTFEN ALANI BOŞ BIRAKMAYINIZ",Toast.LENGTH_LONG).show();

            }
        });

        builder.setNegativeButton("İPTAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    private void startChronometer() {
        if (!isRunning) {
            chronometer.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            chronometer.start();
            isRunning = true;
        }
    }

    private void pauseChronometer() {
        if (isRunning) {
            chronometer.stop();
            pauseOffset = SystemClock.elapsedRealtime() - chronometer.getBase();
            isRunning = false;
        }
    }
    private void getLists(){

        urunSpinner = findViewById(R.id.editTextUrun);
        urunList=getUrunFromMSSQL();
        ArrayAdapter<String> urunAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, urunList);
        urunSpinner.setAdapter(urunAdapter);
        urunSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                urunString=urunList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        islemciSpinner = findViewById(R.id.editTextIslemci);
        islemciList=getIslemciFromMSSQL();
        ArrayAdapter<String> islemciAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, islemciList);
        islemciSpinner.setAdapter(islemciAdapter);
        islemciSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                islemciString=islemciList.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }



   public static boolean stopControl=false;

    private void startRefreshTimer() {
        if(!editTextAralik.getText().toString().equals("")){
            WEBSITE_REFRESH_INTERVAL = Integer.parseInt(editTextAralik.getText().toString());
            WEBSITE_REFRESH_INTERVAL=WEBSITE_REFRESH_INTERVAL*1000;
            if(!aralikbirim){
                WEBSITE_REFRESH_INTERVAL=WEBSITE_REFRESH_INTERVAL*60;
            }
            editTextAralik.setEnabled(false);
            stopControl=true;
            if(WEBSITE_REFRESH_INTERVAL > 0){
                websiteRefreshTimer = new Timer();
                websiteRefreshTimer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("KAMERA: ", "YENİLENDİ");

                                readSerialData();


                                Log.d("SERİ DATA: "," "+tempGelen);

                                loadWebsite();

                                new TCPReceiver(SERVER_IP, SERVER_PORT, MainActivity.this).execute();



                                if(!tempGelen.equals("0")){

                                    int equalsIndex = tempGelen.indexOf("=");
                                    String tempDeger=tempGelen.substring(equalsIndex+1);

                                    if(!roomTempControl){
                                        olculenSic.setTextColor(Color.parseColor("#FF0000"));
                                        //sicaklikTest.setTextColor(Color.parseColor("#FF0000"));
                                    }
                                    olculenSic.setText(tempDeger);
                                    //sicaklikTest.setVisibility(View.VISIBLE);
                                    //olculenSic.setVisibility(View.VISIBLE);

                                }

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
            stopControl=false;

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
        webSettings.setJavaScriptEnabled(true);
        WebSettings webSettings2 = webView2.getSettings();
        webSettings2.setJavaScriptEnabled(true);
        // Diğer WebView ayarlarını burada konfigure edebilirsiniz
    }

    private void loadWebsite() {
        String websiteUrl = "http://192.168.1.222:8080/uretim/uretimg.nsf/Arge.xsp";
        webView.loadUrl(websiteUrl);
        String websiteUrl2 = "http://192.168.1.222:8080/uretim/uretimg.nsf/Arge_Grafik.xsp";
        webView2.loadUrl(websiteUrl2);
    }



    boolean roomTempControl=true;


    public void handleReceivedData(String receivedDataString) {
        Log.d("MainActivity", "Gelen Veri: " + receivedDataString);

        StringBuilder receivedData = new StringBuilder(receivedDataString);

        String roomTemp = getSubstringBetween(receivedData, "86", "254");
        if(roomTemp.length()>2){
            String roomTempValue = roomTemp.substring(0, roomTemp.length() - 1) + "." + roomTemp.charAt(roomTemp.length() - 1);
            if(!roomTempValue.equals(tempGelen)){
                roomTempControl=false;
            }
            else
                roomTempControl=true;
        }

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
        String wifiStatus = getSubstringBetween(receivedData, "134", "254");
        String detectStatus = getSubstringBetween(receivedData, "154", "254");




        saveTcpDataToMSSQL(roomTemp,setTemp,batteryLevel,comfortMode,programMode,ecoMode,minute,hour,weekday,activeProgram,lockStatus,segmentStatus, systemOnOff, wifiStatus,detectStatus,tempGelen);



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

        if (startIndex != -1 && endIndex != -1 && startIndex + start.length() < endIndex) {
            String result = original.substring(startIndex + start.length(), endIndex);
            original.delete(startIndex, endIndex + end.length()); // Alt dizgiyi temizle
            return result;
        }else if(startIndex + start.length() >= endIndex) {
            return end;
        }
        else {
            return "";
        }
    }





    UsbDevice device;
    KabloluSeriBaglanti kabloluSeriBaglanti = new KabloluSeriBaglanti(this,device);
    SerialTerminalFragment serialTerminalFragment = new SerialTerminalFragment(this,device);

    String tempGelen="0";
    public void showReceivedData(String data){
    tempGelen=data;
    }

    public void readSerialData() {
        kabloluTestBaglan();

        serialTerminalFragment.connect();
        serialTerminalFragment.read();
       //String seriDeger = kabloluSeriBaglanti.read();
        //return seriDeger;
    }

    public void kabloluTestBaglan() {
        try {
            UsbManager usbManager;
            usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
            HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
            UsbSerialProber usbDefaultProber = UsbSerialProber.getDefaultProber();
            UsbSerialProber usbCustomProber = CustomProber.getCustomProber();
            while (deviceIterator.hasNext()) {
                UsbDevice device = deviceIterator.next();
                UsbSerialDriver driver = usbDefaultProber.probeDevice(device);
                if (driver == null) {
                    driver = usbCustomProber.probeDevice(device);
                    //return;
                }
                if (driver != null) {
                    kabloluSeriBaglanti = new KabloluSeriBaglanti(this, device);
                    serialTerminalFragment = new SerialTerminalFragment(this,device);
                    return;
                }
            }
        } catch (Exception ex) {
        }
    }


    private void saveIslemciDataToMSSQL(String islemciAdi) {

        //ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            try {
                Class.forName(Classes);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                connection = DriverManager.getConnection(url,username,password);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            String insertQuery = "INSERT INTO TBL_ISLEMCI (Islemci) VALUES (?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, islemciAdi);
                preparedStatement.executeUpdate();
                // Prosedür başarıyla çağrıldı
                Toast.makeText(this, "VERİLER GÖNDERİLDİ", Toast.LENGTH_SHORT);
            } catch (SQLException e) {
                System.out.println("SQL Server Hatası");
                e.printStackTrace();
            }
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


    private void saveUrunDataToMSSQL(String urunAdi) {

        //ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            try {
                Class.forName(Classes);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                connection = DriverManager.getConnection(url,username,password);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            String insertQuery = "INSERT INTO TBL_URUN (Urun) VALUES (?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
                preparedStatement.setString(1, urunAdi);
                preparedStatement.executeUpdate();
                // Prosedür başarıyla çağrıldı
                Toast.makeText(this, "VERİLER GÖNDERİLDİ", Toast.LENGTH_SHORT);
            } catch (SQLException e) {
                System.out.println("SQL Server Hatası");
                e.printStackTrace();
            }
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


    private List<String> getIslemciFromMSSQL() {
        List<String> islemciList = new ArrayList<>();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            Class.forName(Classes);
            connection = DriverManager.getConnection(url, username, password);

            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM TBL_ISLEMCI");
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String islemciAdi = resultSet.getString("Islemci");
                    islemciList.add(islemciAdi);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (ClassNotFoundException | SQLException e) {
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

        return islemciList;
    }
    private List<String> getUrunFromMSSQL() {
        List<String> urunList = new ArrayList<>();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        try {
            Class.forName(Classes);
            connection = DriverManager.getConnection(url, username, password);

            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM TBL_URUN");
                 ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String urunAdi = resultSet.getString("Urun");
                    urunList.add(urunAdi);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        } catch (ClassNotFoundException | SQLException e) {
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

        return urunList;
    }





    private void getDataFromMSSQL() {

        //ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            try {
                Class.forName(Classes);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                connection = DriverManager.getConnection(url,username,password);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try (PreparedStatement statement1 = connection.prepareStatement("SELECT TOP 1 * FROM TBL_TEST ORDER BY ID DESC");
                 ResultSet resultSet1 = statement1.executeQuery()) {
                if (resultSet1.next()) {
                    float kalibrasyon = resultSet1.getFloat("Kalibrasyon");
                    editTextCalibration.setText(String.valueOf(kalibrasyon));
                    float histerisiz_poz = resultSet1.getFloat("Histerisiz_Pozitif");
                    histerisizPozitifValue=histerisiz_poz;
                    float histerisiz_neg = resultSet1.getFloat("Histerisiz_Negatif");
                    histerisizNegatifValue=histerisiz_neg;
                    String heat_cool = resultSet1.getString("Heat_Cool");
                    if(heat_cool.equals("Heat")){
                        radioButtonHeat.setSelected(true);
                        radioButtonCool.setSelected(false);
                    }
                    else if(heat_cool.equals("Cool")){
                        radioButtonCool.setSelected(true);
                        radioButtonHeat.setSelected(false);
                    }
                    String fonksiyon = resultSet1.getString("Fonksiyon");
                    if(fonksiyon.equals("TPI")){
                        spinnerFunction.setSelection(0);
                    }
                    else if(fonksiyon.equals("Modülasyon")||fonksiyon.equals("Modulasyon")){
                        spinnerFunction.setSelection(1);
                    }
                    else if(fonksiyon.equals("ON/OFF")){
                        spinnerFunction.setSelection(2);
                    }
                    else if(fonksiyon.equals("Akıllı")||fonksiyon.equals("Akilli")){
                        spinnerFunction.setSelection(3);
                    }
                    String urun = resultSet1.getString("Urun");
                    //editTextProduct.setText(urun); //TODO İLK BAŞTA NASIL GELECEK O BELİRLENMELİ
                    int urunIndex = -1;
                    for (int i = 0; i < urunList.size(); i++) {
                        if (urunList.get(i).equals(urun)) {
                            urunIndex = i;
                            break;
                        }
                    }
                    if (urunIndex != -1) {
                        urunSpinner.setSelection(urunIndex);
                    }
                    String islemci = resultSet1.getString("Islemci");
                    int islemciIndex = -1;
                    for (int i = 0; i < islemciList.size(); i++) {
                        if (islemciList.get(i).equals(islemci)) {
                            islemciIndex = i;
                            break;
                        }
                    }
                    if (islemciIndex != -1) {
                        islemciSpinner.setSelection(islemciIndex);
                    }






                    String tip = resultSet1.getString("Tip");
                    if(tip.equals("Kablolu")){
                        spinnerTip.setSelection(0);
                    }
                    else if(tip.equals("Kablosuz")){
                        spinnerTip.setSelection(1);
                    }
                    else if(tip.equals("Wifi")){
                        spinnerTip.setSelection(2);
                    }


                    Date baslama = resultSet1.getTimestamp("Baslama_Zamani");
                    if(baslama!=null){
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy 'Saat:' HH:mm");
                        String formattedDate = dateFormat.format(baslama);
                        TextView textViewStartTime = findViewById(R.id.textViewStartTime);
                        textViewStartTime.append(": "+formattedDate);

                        String tempbaslangic=textViewStartTime.getText().toString().replace("Başlangıç Zamanı: ", "");
                        tempbaslangic=tempbaslangic.replace("Saat: ", "");

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                        baslangicDate = sdf.parse(tempbaslangic);
                    }



                    Date bitis = resultSet1.getTimestamp("Bitis_Zamani");
                    if(bitis!=null){
                        SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd/MM/yyyy 'Saat:' HH:mm");
                        String formattedDate2 = dateFormat2.format(bitis);
                        TextView textViewEndTime = findViewById(R.id.textViewEndTime);
                        textViewEndTime.append(": "+formattedDate2);

                        String tempbitis=textViewEndTime.getText().toString().replace("Bitiş Zamanı: ", "");
                        tempbitis=tempbitis.replace("Saat: ", "");
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                        bitisDate = sdf.parse(tempbitis);



                    }


                    String aralik = resultSet1.getString("Aralik");
                    editTextAralik.setText(aralik);


                    int lastPrimaryKey1 = resultSet1.getInt("ID");
                    insertedID=lastPrimaryKey1;

                }
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }


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






    private void saveTcpDataToMSSQL(String roomTemp, String setTemp, String batteryLevel, String comfortMode, String programMode, String ecoMode, String minute, String hour, String weekday, String activeProgram, String lockStatus, String segmentStatus, String systemOnOff, String wifiStatus, String detectStatus, String tempGelen) {


        //ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);





        try {
            Class.forName(Classes);
            connection = DriverManager.getConnection(url,username,password);
            String insertProcedure = "{call InsertArge(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";



            try (PreparedStatement statement1 = connection.prepareStatement("SELECT TOP 1 ID FROM TBL_TEST ORDER BY ID DESC");
                 ResultSet resultSet1 = statement1.executeQuery()) {
                if (resultSet1.next()) {
                    int lastPrimaryKey1 = resultSet1.getInt("ID");
                    insertedID=lastPrimaryKey1;
                }
            }


            try (CallableStatement callableStatement = connection.prepareCall(insertProcedure)) {
                callableStatement.setObject(1,null );
                callableStatement.setObject(2, null);
                callableStatement.setFloat(3, Float.parseFloat(setTemp)/10);
                callableStatement.setFloat(4, Float.parseFloat(roomTemp)/10);
                callableStatement.setObject(5, null);
                callableStatement.setObject(6, null);
                callableStatement.setInt(7, Integer.parseInt(batteryLevel));
                callableStatement.setInt(8, Integer.parseInt(wifiStatus));
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
                callableStatement.setInt(20, insertedID);
                callableStatement.setInt(21, Integer.parseInt(detectStatus));

                int equalsIndex = tempGelen.indexOf("=");
                String tempDeger=tempGelen.substring(equalsIndex+1);
                callableStatement.setFloat(22, Float.parseFloat(tempDeger));


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




    private int insertedID;

    private void saveDataToMSSQL(float calibrationValue, float pozitifValue, float negatifValue, String heatCoolValue, String functionValue, String productValue, String islemciValue, String tipValue, Date baslamaZamani, Date bitisZamani, int aralik, String aralikBirimi) {

        //ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            try {
                Class.forName(Classes);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                connection = DriverManager.getConnection(url,username,password);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            String insertProcedure = "{call InsertData(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";












            try (CallableStatement callableStatement = connection.prepareCall(insertProcedure /*Statement.RETURN_GENERATED_KEYS*/)) {
                callableStatement.setFloat(1, calibrationValue);
                callableStatement.setFloat(2, pozitifValue);
                callableStatement.setFloat(3, negatifValue);
                callableStatement.setString(4, heatCoolValue);
                callableStatement.setString(5, functionValue);
                callableStatement.setString(6, productValue);
                callableStatement.setString(7, islemciValue);
                callableStatement.setString(8, tipValue);
                if(baslamaZamani!=null){
                    java.util.Date utilDate = baslamaZamani;
                    java.sql.Timestamp sqlTS = new java.sql.Timestamp(utilDate.getTime());
                    callableStatement.setTimestamp(9, sqlTS);
                }
                else{
                    callableStatement.setObject(9, null);
                }
                if(bitisZamani!=null){

                    java.util.Date utilDate2 = bitisZamani;
                    java.sql.Timestamp sqlTS2 = new java.sql.Timestamp(utilDate2.getTime());
                    callableStatement.setTimestamp(10, sqlTS2);
                }
                else{
                    callableStatement.setObject(10, null);
                }
                callableStatement.setInt(11, aralik);
                callableStatement.setString(12,aralikBirimi);
                //callableStatement.registerOutParameter(12, Types.INTEGER);

                callableStatement.execute();

                try (ResultSet generatedKeys = callableStatement.getGeneratedKeys()) {
                    while (generatedKeys.next()) {
                        insertedID = generatedKeys.getInt(1);
                    }
                }
                // Prosedür başarıyla çağrıldı
                Toast.makeText(this, "VERİLER GÖNDERİLDİ", Toast.LENGTH_SHORT);
            } catch (SQLException e) {
                System.out.println("SQL Server Hatası");
                e.printStackTrace();
            }
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

    private Date baslangicDate;
    private Date bitisDate;
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


                    try {
                        String tempbaslangic=textViewStartTime.getText().toString().replace("Başlangıç Zamanı: ", "");
                        tempbaslangic=tempbaslangic.replace("Saat: ", "");

                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                        baslangicDate = sdf.parse(tempbaslangic);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                } else {

                    TextView textViewEndTime = findViewById(R.id.textViewEndTime);
                    textViewEndTime.append(" Saat: " + selectedTime);
                    try {
                        String tempbitis=textViewEndTime.getText().toString().replace("Bitiş Zamanı: ", "");
                        tempbitis=tempbitis.replace("Saat: ", "");
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                        bitisDate = sdf.parse(tempbitis);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

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
