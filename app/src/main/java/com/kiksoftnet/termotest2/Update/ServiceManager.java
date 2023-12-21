package com.kiksoftnet.termotest2.Update;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class ServiceManager {

    final String NAMESPACE = "";
    final String METHOD_NAME = "VERSIYON_SORGULA";
    private String URL = "http://192.168.1.222:8080/destek.nsf/Versiyon_Kontrol?OpenWebService";
    final String SOAP_ACTION = "VERSIYON_SORGULA";

    SoapObject soapObject;
    SoapSerializationEnvelope soapSerializationEnvelope;
    HttpTransportSE httpTransportSE;

    public String myPushData(String proje_adi,String url){
        try {
            soapObject = new SoapObject(NAMESPACE, METHOD_NAME);
            soapObject.addProperty("PROJE", proje_adi);
            if (!url.equalsIgnoreCase(""))
                URL="http://"+url+"/destek.nsf/Versiyon_Kontrol?OpenWebService";

            soapSerializationEnvelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            soapSerializationEnvelope.dotNet = true;
            soapSerializationEnvelope.setOutputSoapObject(soapObject);

            httpTransportSE = new HttpTransportSE(URL);
            httpTransportSE.debug = true;

            try {
                httpTransportSE.call(SOAP_ACTION, soapSerializationEnvelope);
                SoapPrimitive soapPrimitive = (SoapPrimitive) soapSerializationEnvelope.getResponse();
                return soapPrimitive.toString();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return "-1";
        }catch (Exception e)
        {
            return "-1";
        }
    }


}
