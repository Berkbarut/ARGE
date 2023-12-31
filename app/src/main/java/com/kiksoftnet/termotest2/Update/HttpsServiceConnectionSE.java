package com.kiksoftnet.termotest2.Update;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//


import com.kiksoftnet.termotest2.Service.ServiceConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;
import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import org.ksoap2.HeaderProperty;

public class HttpsServiceConnectionSE implements ServiceConnection {
    private HttpsURLConnection connection;

    public HttpsServiceConnectionSE(String host, int port, String file, int timeout) throws IOException {
        this((Proxy)null, host, port, file, timeout, timeout);
    }

    public HttpsServiceConnectionSE(String host, int port, String file, int connectTimeout, int readTimeout) throws IOException {
        this((Proxy)null, host, port, file, connectTimeout, readTimeout);
    }

    public HttpsServiceConnectionSE(Proxy proxy, String host, int port, String file, int connectTimeout, int readTimeout) throws IOException {
        if (proxy == null) {
            this.connection = (HttpsURLConnection)(new URL("https", host, port, file)).openConnection();
        } else {
            this.connection = (HttpsURLConnection)(new URL("https", host, port, file)).openConnection(proxy);
        }

        this.updateConnectionParameters(connectTimeout, readTimeout);
    }

    private void updateConnectionParameters(int connectTimeout, int readTimeout) {
        this.connection.setConnectTimeout(connectTimeout);
        this.connection.setReadTimeout(readTimeout);
        this.connection.setUseCaches(false);
        this.connection.setDoOutput(true);
        this.connection.setDoInput(true);
    }

    public void connect() throws IOException {
        this.connection.connect();
    }

    public void disconnect() {
        this.connection.disconnect();
    }

    public List getResponseProperties() {
        Map properties = this.connection.getHeaderFields();
        Set keys = properties.keySet();
        List retList = new LinkedList();
        Iterator i = keys.iterator();

        while(i.hasNext()) {
            String key = (String)i.next();
            List values = (List)properties.get(key);

            for(int j = 0; j < values.size(); ++j) {
                retList.add(new HeaderProperty(key, (String)values.get(j)));
            }
        }

        return retList;
    }

    public int getResponseCode() throws IOException {
        return this.connection.getResponseCode();
    }

    public void setRequestProperty(String key, String value) {
        this.connection.setRequestProperty(key, value);
    }

    public void setRequestMethod(String requestMethod) throws IOException {
        this.connection.setRequestMethod(requestMethod);
    }

    public void setFixedLengthStreamingMode(int contentLength) {
        this.connection.setFixedLengthStreamingMode(contentLength);
    }

    public void setChunkedStreamingMode() {
        this.connection.setChunkedStreamingMode(0);
    }

    public OutputStream openOutputStream() throws IOException {
        return this.connection.getOutputStream();
    }

    public InputStream openInputStream() throws IOException {
        return this.connection.getInputStream();
    }

    public InputStream getErrorStream() {
        return this.connection.getErrorStream();
    }

    public String getHost() {
        return this.connection.getURL().getHost();
    }

    public int getPort() {
        return this.connection.getURL().getPort();
    }

    public String getPath() {
        return this.connection.getURL().getPath();
    }

    public void setSSLSocketFactory(SSLSocketFactory sf) {
        this.connection.setSSLSocketFactory(sf);
    }
}
