package com.kiksoftnet.termotest2;


import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Toast;

public class TCPCommunicator {
    private InitTCPClientTask task=null;
    private TCPCommunicator uniqInstance;
    private String serverHost;
    private int serverPort;
    private List<TCPListener> allListeners;
    private BufferedWriter out;
    private BufferedReader in;
    private Socket s;
    private Handler UIHandler;
    private Context appContext;
    private boolean bagli=false;
    private Terminal terminal;
    private String komut="";
    private String gelenveri="";
    //private TCPListener tcpListener;
    public TCPCommunicator()
    {
        //    allListeners = new ArrayList<TCPListener>();
    }
    /*
    public static TCPCommunicator getInstance()
    {
        if(uniqInstance==null)
        {
            uniqInstance = new TCPCommunicator();
        }
        return uniqInstance;
    }

     */
    public  TCPWriterErrors init(String host,int port,Terminal terminal)
    {
        serverHost=host;
        serverPort=port;
//        addListener((TCPListener)terminal);

        this.terminal=terminal;
        try {
            //System.out.println("Analiz cihazına bağlanılıyor");
            try {
                s = new Socket();
                s.setSoTimeout(5000);
                System.out.println(getServerHost());
                s.connect(new InetSocketAddress(getServerHost(), getServerPort()), 5000);
            }catch(Exception ex)
            {

                //System.out.println("Analiz cihazına bağlanılamıyor:"+ex.toString());
                return null;
            }
            if (s.isConnected()) {
                bagli = true;
                //System.out.println("Analiz cihazına bağlandı");
            }else
            {

                //System.out.println("Analiz cihazına bağlanılamıyor 22");
                return null;
            }
            in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));


            //InitTCPClientTask task = new InitTCPClientTask();
            //task.execute(new Void[0]);
            //writeToSocket(":TRACE1:MODE Max" + System.getProperty("line.separator"),UIHandler,appContext,terminal);
            System.out.println("Analiz cihazına bağlanıyor");

            return TCPWriterErrors.OK;

        } catch (IOException e) {
            e.printStackTrace();

            //System.out.println("Analiz cihazına bağlanılamıyor");
            return null;
        } catch (Exception ex)
        {
            return null;
        }


    }

    public boolean isConnected(){
        return bagli;
    }

    public TCPWriterErrors writeToSocket(String obj,Handler handle,Context context,Terminal _terminal)
    {
        System.out.println("writetoSocket girdi");
        UIHandler=handle;
        appContext=context;
        komut=obj;
        terminal=_terminal;

        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try
                {

                    //terminal.setOlcumDegeri(enyuksekdeger);


                        String outMsg = obj ;
                        out.write(outMsg);
                        out.flush();
                        System.out.println(outMsg +" gönderildi");
                        //if (task==null)


                    String inMsg="";
                    while(true)
                    {

                        boolean end = false;
                        while(!end) {
                            bagli=true;
                            int currentBytesRead = in.read();
                            inMsg=inMsg+""+(char)currentBytesRead;


                            if(inMsg.contains("\\n")) {
                                end = true;
                            }
                        }


                        if(inMsg!=null)
                        {

                            gelenveri=gelenveri+inMsg;
                            inMsg="";
                            System.out.println(gelenveri +" geldi");
                            if (gelenveri.indexOf("254")!=-1) {
                                System.out.println("254 geldi");
                                terminal.handleReceivedData(gelenveri);
                                gelenveri="";
                                break;
                            }


                        }
                    }

                       // task = new InitTCPClientTask();
                        //task.execute(new Void[0]);

                        System.out.println("task execute");
/*
                        if (task!=null) {
                            System.out.println("task cancel");
                            task.cancel(true);
                        }
*/
                }
                catch(Exception e)
                {
                    UIHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            //System.out.println("a problem has occured, the app might not be able to reach the server");
                            bagli=false;
                            System.out.println("bağlantı hatası");


                        }
                    });
                }
            }

        };
        try {
            Thread thread = new Thread(runnable);
            thread.start();
        }catch(Exception ex)
        {

            //terminal.getParentActivity().hataGoster();
        }
        return TCPWriterErrors.OK;

    }
    public void updateDeger(String Data)
    {
        System.out.println(Data +" update edildi");

    }
    public  void addListener(TCPListener listener)
    {
        //allListeners.clear();
        //allListeners.add(listener);
    }
    public void removeAllListeners()
    {
        //allListeners.clear();
    }
    public void closeStreams()
    {
        try
        {
            s.close();
            in.close();
            out.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            // System.out.println(e.toString());
        }
    }


    public String getServerHost() {
        return serverHost;
    }
    public void setServerHost(String serverHost) {
        serverHost = serverHost;
    }
    public int getServerPort() {
        return serverPort;
    }
    public void setServerPort(int serverPort) {
        serverPort = serverPort;
    }


    public class InitTCPClientTask extends AsyncTask<Void, Void, Void>
    {

        public InitTCPClientTask()
        {
            System.out.println("initTCPClient çalıştı");
            bagli=true;
        }



        @Override
        protected Void doInBackground(Void... params) {
            // TODO Auto-generated method stub
            System.out.println("Veri alınıyor");
            try
            {

                //for(TCPListener listener:allListeners)
                    //    listener.onTCPConnectionStatusChanged(true);

                String inMsg="";
                while(true)
                {

                    boolean end = false;
                    while(!end) {
                        bagli=true;
                        int currentBytesRead = in.read();
                        inMsg=inMsg+""+(char)currentBytesRead;


                        if(inMsg.contains("\\n")) {
                            end = true;
                        }
                    }


                    if(inMsg!=null)
                    {

                        gelenveri=gelenveri+inMsg;
                        inMsg="";
                        System.out.println(gelenveri +" geldi");
                        if (gelenveri.indexOf("254")!=-1) {
                            System.out.println("254 geldi");
                            terminal.handleReceivedData(gelenveri);
                            gelenveri="";
                            break;
                        }


                        }
                }
                return null;

            } catch (UnknownHostException e) {
                e.printStackTrace();
                System.out.println(e.toString());
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.toString());
            } catch (Exception ex)
            {
                return null;
            }

            return null;

        }
        @Override
        protected void onPostExecute(Void result) {
            try {
                System.out.println(gelenveri +" onpostexecute");
                Runtime.getRuntime().gc();


            }catch(Exception e)
            {


            }
        }
    }
    public enum TCPWriterErrors{UnknownHostException,IOException,otherProblem,OK}
}

