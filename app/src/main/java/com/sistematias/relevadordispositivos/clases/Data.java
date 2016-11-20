package com.sistematias.relevadordispositivos.clases;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.sistematias.relevadordispositivos.activity.MyApp;
import com.sistematias.relevadordispositivos.model.Persistencia;

import java.text.SimpleDateFormat;

/**
 * Created by usuario on 26/05/2015.
 */
public class Data {
    private static boolean syncAll = false;
    private static boolean serviceActive = false;
    public static final String DIRECTORIO_FOTO = "ruteo/fotos";
    public static Repository conn;

    public static String getImei() {
        if(imei==null || imei.equals("")){
            TelephonyManager mngr = (TelephonyManager)MyApp.getAppContext().getSystemService(Context.TELEPHONY_SERVICE);
            imei = mngr.getDeviceId();
        }
        return imei;
    }

    private static String imei = "";

    public static final SimpleDateFormat FECHA_DATABASE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat FECHA_SQL_SEVER_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    public static final SimpleDateFormat FECHA_FULL_FORMAT = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    public static final SimpleDateFormat FECHA_SHORT_FORMAT = new SimpleDateFormat("dd-MM-yyyy");
    public static final SimpleDateFormat FECHA_SHORT_FORMAT_SLASH = new SimpleDateFormat("dd/MM/yyyy");
    public static final SimpleDateFormat FECHA_LAST_SESSION = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    public static final SimpleDateFormat FECHA_FILE_NAME = new SimpleDateFormat("yyyyMMdd_HHmmss");


    public static boolean isSyncAll() {
        return syncAll;
    }

    public static void setSyncAll(boolean syncAll) {
        Data.syncAll = syncAll;
    }

    public static boolean isServiceActive() {
        return serviceActive;
    }

    public static void setServiceActive(boolean serviceActive) {
        Data.serviceActive = serviceActive;
    }


    public static Repository getConn() {
        if(conn==null){
            conn = new Repository(MyApp.getAppContext());
        }
        return conn;
    }

    public static void setConn(Repository conn) {
        Data.conn = conn;
    }

}
