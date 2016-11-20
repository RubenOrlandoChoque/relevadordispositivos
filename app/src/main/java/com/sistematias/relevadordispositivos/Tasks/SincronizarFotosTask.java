package com.sistematias.relevadordispositivos.Tasks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.sistematias.relevadordispositivos.clases.Data;
import com.sistematias.relevadordispositivos.clases.Querys;
import com.sistematias.relevadordispositivos.model.Config;
import com.sistematias.relevadordispositivos.model.FotoNovedad;
import com.sistematias.relevadordispositivos.model.Ruteo;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by RUBEN on 24/05/2016.
 */
public class SincronizarFotosTask extends AsyncTask<String, Integer, Boolean> {
    ProgressDialog loading = null;
    private Context activity;
    Cursor cursorFotosRuteos = null;
    Cursor cursorFotosNovedades = null;

    public SincronizarFotosTask(Context _activity) {
        activity = _activity;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        boolean result = true;
        Config url = new Config();
        url.getConfigByCod("CONFIG_SERVER");
        Config nameSpace = new Config();
        nameSpace.getConfigByCod("CONFIG_NAMESAPCE");

        /*sincronizar las fotos de ruteos*/
        String NAMESPACE = nameSpace.getValueConfig();
        String URL = url.getValueConfig();
        String METHOD_NAME = "SyncRuteoFiles";
        String SOAP_ACTION = NAMESPACE + METHOD_NAME;
        int progreso = 1;
        while (cursorFotosRuteos.moveToNext()) {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("nameFile",cursorFotosRuteos.getString(10));
            File file = new File(Environment.getExternalStorageDirectory()+"/"+Data.DIRECTORIO_FOTO +"/"+ cursorFotosRuteos.getString(10));
            FileInputStream fis = null;
            String strAttachmentCoded = "";
            try {
                fis = new FileInputStream(file);
                ByteArrayOutputStream objByteArrayOS = new ByteArrayOutputStream();
                System.out.println("Total file size to read (in bytes) : " + fis.available());
                byte[] byteBufferString = new byte[1024];
                for (int readNum; (readNum = fis.read(byteBufferString)) != -1; ) {
                    objByteArrayOS.write(byteBufferString, 0, readNum);
                    System.out.println("read " + readNum + " bytes,");
                }
                byte[] byteBinaryData = Base64.encode((objByteArrayOS.toByteArray()), Base64.DEFAULT);
                try{
                    strAttachmentCoded = new String(byteBinaryData);
                }catch (RuntimeException exc){
                    exc.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fis != null)
                        fis.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            request.addProperty("file", strAttachmentCoded);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE transporte = new HttpTransportSE(URL);
            try {
                transporte.call(SOAP_ACTION, envelope);
                if(envelope.bodyIn instanceof SoapFault){
                    SoapFault resultado_xml = (SoapFault) envelope.bodyIn;
                    Log.e("SoapFault: ",resultado_xml.getMessage());
                    resultado_xml.printStackTrace();
                    result = false;
                }else{
                    if(envelope.bodyIn instanceof SoapObject){
                        SoapObject resultado_xml = (SoapObject) envelope.bodyIn;
                        String res = resultado_xml.getProperty(0).toString();
                        if (res.equals("true")) {
                            Log.i("correcto fotos","correcto");
                            Ruteo ruteo = new Ruteo();
                            ruteo.getRuteoByCodRuteo(cursorFotosRuteos.getString(1));
                            String fechaRegistroFoto = Data.FECHA_DATABASE_FORMAT.format(new Date());
                            ruteo.setFechaRegistroFoto(fechaRegistroFoto);
                            ruteo.setData();
                        }
                    }else {
                        result = false;
                    }
                }

            } catch (Exception e) {
                result = false;
                Log.i("message",e.getMessage());
                e.printStackTrace();
            }
            loading.setProgress(progreso++);
        }

        /*sincronizar las fotos de novedades*/
        NAMESPACE = nameSpace.getValueConfig();
        URL = url.getValueConfig();
        METHOD_NAME = "SyncFiles";
        SOAP_ACTION = NAMESPACE + METHOD_NAME;

        while (cursorFotosNovedades.moveToNext()) {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("nameFile",cursorFotosNovedades.getString(3));

            File file = new File(Environment.getExternalStorageDirectory()+"/"+Data.DIRECTORIO_FOTO +"/"+ cursorFotosNovedades.getString(3));
            FileInputStream fis = null;
            String strAttachmentCoded = "";
            try {
                fis = new FileInputStream(file);
                ByteArrayOutputStream objByteArrayOS = new ByteArrayOutputStream();
                System.out.println("Total file size to read (in bytes) : " + fis.available());
                byte[] byteBufferString = new byte[1024];
                for (int readNum; (readNum = fis.read(byteBufferString)) != -1; ) {
                    objByteArrayOS.write(byteBufferString, 0, readNum);
                    System.out.println("read " + readNum + " bytes,");
                }
                byte[] byteBinaryData = Base64.encode((objByteArrayOS.toByteArray()), Base64.DEFAULT);
                try{
                    strAttachmentCoded = new String(byteBinaryData);
                }catch (RuntimeException exc){
                    exc.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fis != null)
                        fis.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            request.addProperty("file", strAttachmentCoded);
            request.addProperty("codNovedad", cursorFotosNovedades.getString(4));
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE transporte = new HttpTransportSE(URL);
            try {
                transporte.call(SOAP_ACTION, envelope);
                if(envelope.bodyIn instanceof SoapFault){
                    SoapFault resultado_xml = (SoapFault) envelope.bodyIn;
                    Log.e("SoapFault: ",resultado_xml.getMessage());
                    resultado_xml.printStackTrace();
                    result = false;
                }else{
                    if(envelope.bodyIn instanceof SoapObject){
                        SoapObject resultado_xml = (SoapObject) envelope.bodyIn;
                        String res = resultado_xml.getProperty(0).toString();
                        if (res.equals("true")) {
                            Log.i("correcto","correcto");
                            FotoNovedad fotoNovedad = new FotoNovedad();
                            fotoNovedad.getFotoNovedadByCod(cursorFotosNovedades.getString(1));
                            String fechaRegistroFoto = Data.FECHA_DATABASE_FORMAT.format(new Date());
                            fotoNovedad.setFechaRegistro(fechaRegistroFoto);
                            fotoNovedad.setData();
                        } else {
                            result = false;
                        }
                    }else {
                        result = false;
                    }
                }
            } catch (Exception e) {
                result = false;
                Log.i("message",e.getMessage());
                e.printStackTrace();
            }
            loading.setProgress(progreso++);
        }

        return result;
    }

    @Override
    protected void onPreExecute() {

        cursorFotosRuteos = Data.getConn().executeSelect(Querys.getRuteosFotos(), cursorFotosRuteos);
        int cantFotosRuteos = cursorFotosRuteos.getCount();


        cursorFotosNovedades = Data.getConn().executeSelect(Querys.getFotosNovedades(), cursorFotosNovedades);
        int cantFotosNovedades = cursorFotosNovedades.getCount();

        loading = new ProgressDialog(activity);
        loading.setTitle("Sincronizando Fotos");
        loading.setMessage("Esto puede demorar algunos minutos.\nAguarde por favor...");
        loading.setCancelable(false);
        loading.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SincronizarFotosTask.this.cancel(true);
                dialog.dismiss();
            }
        });
        loading.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        loading.setMax(cantFotosNovedades + cantFotosRuteos);
        loading.setIndeterminate(false);
        loading.setProgress(0);
        loading.show();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (!this.isCancelled()) {
            try {
                loading.hide();
                loading.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Intent intent  =new Intent("FOTOS_SYNC_FINISH");
            intent.putExtra("resultado",true);
            activity.sendBroadcast(intent);

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.activity);
            if (result) {
                alertDialog.setTitle("Éxito");
                alertDialog.setMessage("Sincronización realizada con éxito!");
                alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            } else {
                alertDialog.setTitle("Error");
                alertDialog.setMessage("La sincronización no se realizó. Verifique conexión de internet o comuníquese con el administrador.");
                alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            }
            alertDialog.show();
        }
    }
}
