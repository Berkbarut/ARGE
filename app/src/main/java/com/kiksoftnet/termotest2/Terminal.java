package com.kiksoftnet.termotest2;

import android.os.StrictMode;
import android.util.Log;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Terminal {
    private String tempGelen;

    private int insertedID;

    public int getInsertedID() {
        return insertedID;
    }

    public void setInsertedID(int insertedID) {
        this.insertedID = insertedID;
    }

    public String gettempGelen() {
        return tempGelen;
    }

    public void setTempgelen(String tempgelen) {
        this.tempGelen = tempgelen;
    }

    private static String ip = "192.168.1.223";// this is the host ip that your data base exists on you can use 10.0.2.2 for local host                                                    found on your pc. use if config for windows to find the ip if the database exists on                                                    your pc
    private static String port = "1433";// the port sql server runs on
    private static String Classes = "net.sourceforge.jtds.jdbc.Driver";// the driver that is required for this connection use                                                                           "org.postgresql.Driver" for connecting to postgresql
    private static String database = "Arge";// the data base name
    private static String username = "kiksoft";// the user name
    private static String password = "kik++--**123";// the password
    private static String url = "jdbc:jtds:sqlserver://"+ip+":"+port+"/"+database; // the connection url string
    private Connection connection = null;

    public void handleReceivedData(String receivedDataString) {
//        Log.d("MainActivity", "Gelen Veri: " + receivedDataString);
//        System.out.println(receivedDataString+" handleRe3c");
        StringBuilder receivedData = new StringBuilder(receivedDataString);

        String roomTemp = getSubstringBetween(receivedData, "86", "254");
        if(roomTemp.length()>2){
            String roomTempValue = roomTemp.substring(0, roomTemp.length() - 1) + "." + roomTemp.charAt(roomTemp.length() - 1);
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

}
