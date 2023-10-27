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
import android.os.StrictMode;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
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
    String heatCoolString="";
    String fonksiyonString="";
    String urunString="";
    String islemciString="";
    String tipString="";



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
                }
            }
        });

        buttonIncreaseCalibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calibrationValue < 8.0) {
                    calibrationValue+=0.1;
                    editTextCalibration.setText(String.format("%.1f",calibrationValue));
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
                }
            }
        });

        buttonIncreasePozitif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (histerisizPozitifValue < 2.0) {
                    histerisizPozitifValue += 0.1;
                    editTextPozitif.setText(String.format("%.1f",histerisizPozitifValue));
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
                }
            }
        });

        buttonIncreaseNegatif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (histerisizNegatifValue < -0.1) {
                    histerisizNegatifValue += 0.1;
                    editTextNegatif.setText(String.format("%.1f",histerisizNegatifValue));
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
                String selectedFunction = (String) spinnerFunction.getSelectedItem();
                if (selectedFunction.equals("Kablolu")) {
                    // Kablolu seçildiğinde yapılacak işlemler
                    tipString="Kablolu";
                } else if (selectedFunction.equals("Kablosuz")) {
                    // Kablosuz seçildiğinde yapılacak işlemler
                    tipString="Kablosuz";
                } else if (selectedFunction.equals("Wifi")) {
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


        textureView = findViewById(R.id.textureView);
        startButton = findViewById(R.id.startCameraButton);
        stopButton = findViewById(R.id.stopCameraButton);

        // Start butonuna tıklama olayı ekleyin
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCameraStarted) {
                    openCamera();
                    // Kamera başlatıldıktan sonra gerekli işlemleri yapın
                    isCameraStarted = true;
                    startButton.setEnabled(false);
                    stopButton.setEnabled(true);
                }
            }
        });

        // Durdur butonuna tıklama olayı ekleyin
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCameraStarted) {
                    // Kamerayı durdurma işlemini yapın
                    closeCamera();
                    isCameraStarted = false;
                    startButton.setEnabled(true);
                    stopButton.setEnabled(false);
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




    }
    private void saveDataToMSSQL(float calibrationValue, float pozitifValue, float negatifValue, String heatCoolValue, String functionValue, String productValue, String islemciValue, String tipValue) {
        Connection connection = null;
        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.INTERNET}, PackageManager.PERMISSION_GRANTED);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            String jdbcUrl = "jdbc:sqlserver:192.168.1.2:1433;databaseName=Arge;user=kiksoft;password=kik++--**123";
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcUrl);

            // Saklı prosedürü çağırma
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

    private void openCamera() {
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0]; // Kamera kimliğini seçin (birinci kamera)
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
                return;
            }
            cameraManager.openCamera(cameraId, stateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void closeCamera() {
        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }




}
