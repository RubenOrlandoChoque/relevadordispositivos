package com.sistematias.relevadordispositivos.Tasks;

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

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;

/**
 * Created by samuel on 31/10/2015.
 */
public class FotosNovedadesSyncTask extends AsyncTask<String, Integer, Boolean> {
    ProgressDialog loading = null;
    private Context activity;

    public FotosNovedadesSyncTask(Context _activity) {
        activity = _activity;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        Config url = new Config();
        url.getConfigByCod("CONFIG_SERVER");

        Config nameSpace = new Config();
        nameSpace.getConfigByCod("CONFIG_NAMESAPCE");

        final String NAMESPACE = nameSpace.getValueConfig();
        final String URL = url.getValueConfig();
        final String METHOD_NAME = "SyncFiles";
        final String SOAP_ACTION = NAMESPACE + METHOD_NAME;


        boolean resul = true;
        String codFileTmp = "";
        Cursor cursor = null;
        cursor = Data.getConn().executeSelect(Querys.getFotosNovedades(), cursor);
        while (cursor.moveToNext()) {
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("nameFile",cursor.getString(3));
            codFileTmp = cursor.getString(1);

            File file = new File(Environment.getExternalStorageDirectory()+"/"+Data.DIRECTORIO_FOTO +"/"+ cursor.getString(3));
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
                int content;
                //while ((content = fis.read()) != -1) {
                  //  System.out.print((char) content);
                //}
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
            request.addProperty("codNovedad", cursor.getString(4));
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            HttpTransportSE transporte = new HttpTransportSE(URL);
            try {
                transporte.call(SOAP_ACTION, envelope);
                SoapObject resultado_xml = (SoapObject) envelope.bodyIn;
                //SoapFault resultado_xml = (SoapFault) envelope.bodyIn;
                //resultado_xml.printStackTrace();
                String res = resultado_xml.getProperty(0).toString();
                try {
                    Log.i("Resultado WS-----", res);
                } catch (Exception ex) {

                }
                if (res.equals("true")) {
                    Log.i("correcto","correcto");
                    FotoNovedad fotoNovedad = new FotoNovedad();
                    fotoNovedad.getFotoNovedadByCod(cursor.getString(1));
                    String fechaRegistroFoto = Data.FECHA_DATABASE_FORMAT.format(new Date());
                    fotoNovedad.setFechaRegistro(fechaRegistroFoto);
                    fotoNovedad.setData();
                } else {

                }
            } catch (Exception e) {
                Log.i("message",e.getMessage());
                e.printStackTrace();
            }
        }

        return true;
    }

    @Override
    protected void onPreExecute() {
        loading = new ProgressDialog(activity);
        loading.setTitle("Sincronizando fotos de novedades");
        loading.setMessage("Esto puede demorar algunos minutos.\nAguarde por favor...");
        loading.setCancelable(false);
        loading.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FotosNovedadesSyncTask.this.cancel(true);
                Data.setSyncAll(false);
                Intent intent  =new Intent("FOTOS_NOVEDADES_SYNC_FINISH");
                activity.getApplicationContext().sendBroadcast(intent);
                dialog.dismiss();
            }
        });
        loading.show();
    }

    @Override
    protected void onPostExecute(Boolean result) {
        try {
            loading.hide();
            loading.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Intent intent  =new Intent("FOTOS_NOVEDADES_SYNC_FINISH");
        activity.sendBroadcast(intent);
        if(Data.isSyncAll()){
            //new FotosRuteosSyncTask(activity).execute();
            FotosRuteosSyncTask asyncTask =new FotosRuteosSyncTask(activity);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        }
    }
}
