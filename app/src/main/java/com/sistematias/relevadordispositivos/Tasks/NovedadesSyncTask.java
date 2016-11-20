package com.sistematias.relevadordispositivos.Tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.sistematias.relevadordispositivos.clases.Data;
import com.sistematias.relevadordispositivos.clases.Querys;
import com.sistematias.relevadordispositivos.model.Config;
import com.sistematias.relevadordispositivos.model.FotoNovedad;
import com.sistematias.relevadordispositivos.model.Novedad;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by samuel on 31/10/2015.
 */
public class NovedadesSyncTask extends AsyncTask<String, Integer, Boolean> {
    ProgressDialog loading = null;
    private Context activity;

    public NovedadesSyncTask(Context _activity) {
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
        final String METHOD_NAME = "SincronizarNovedades";
        final String SOAP_ACTION = NAMESPACE + METHOD_NAME;

        JSONArray jsonArray = new JSONArray();

        Cursor cursor = null;
        cursor = Data.getConn().executeSelect(Querys.getNovedades(), cursor);
        int i = 0;
        while (cursor.moveToNext()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("IdNovedad", cursor.getInt(0) + "");
                jsonObject.put("CodNovedad", cursor.getString(1));
                jsonObject.put("CodProducto", cursor.getString(2));
                jsonObject.put("CodRepositor", cursor.getString(3));
                jsonObject.put("CodPuntoVenta", cursor.getString(4));
                jsonObject.put("CodTipoNovedad", cursor.getString(5));
                jsonObject.put("Observacion", cursor.getString(6));
                jsonObject.put("FechaCreacion", cursor.getString(7));
                jsonObject.put("FechaRegistro", cursor.getString(8));
                jsonObject.put("Coordenadas", cursor.getString(9));
                jsonObject.put("Precio", Double.toString(cursor.getDouble(10)).replace(".", ","));
                jsonObject.put("FechaElaboracion", cursor.getString(11));
                jsonObject.put("HoraElaboracion", cursor.getString(12));
                jsonObject.put("NumLote", cursor.getString(13));
                jsonObject.put("Cantidad", cursor.getInt(14));
                jsonObject.put("Rango", cursor.getInt(15));
                jsonObject.put("CodSucursal", cursor.getString(16));
                jsonArray.put(i, jsonObject);
                i++;
            } catch (JSONException e) {
                Log.i("ERROR", e.getMessage());
                e.printStackTrace();
            }
        }

        try {
            Log.i("datos",jsonArray.toString(5));
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("jsonData", jsonArray.toString());
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);
            try {
                HttpTransportSE transporte = new HttpTransportSE(URL);
                transporte.call(SOAP_ACTION, envelope);
                SoapObject resultado_xml = (SoapObject) envelope.bodyIn;
                String res = "";
                try {
                    res = resultado_xml.getProperty(0).toString();
                    Log.i("Web Service Result", res);
                    JSONArray array = new JSONArray(res);
                    for (int iz = 0; iz < array.length(); iz++) {
                        JSONObject json = array.getJSONObject(iz);
                        Novedad novedad = new Novedad();
                        novedad.getNovedadByCod(json.getString("CodNovedad"));
                        novedad.setFechaRegistro(json.getString("FechaRegistro"));
                        novedad.setData();
                        Novedad.updateCodNovedadInNovedades(json.getString("CodNovedad"),json.getString("CodNewNovedad"));
                        FotoNovedad.updateCodNovedad(json.getString("CodNovedad"),json.getString("CodNewNovedad"));
                    }

                } catch (Exception e) {
                    Log.i("ERROR", e.getMessage());
                    e.printStackTrace();
                }
            } catch (Exception e) {
                Log.i("ERROR", e.getMessage());
                e.printStackTrace();
            }

        } catch (Exception e) {

            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onPreExecute() {
        loading = new ProgressDialog(activity);
        loading.setTitle("Sincronizando novedades");
        loading.setMessage("Esto puede demorar algunos minutos.\nAguarde por favor...");
        loading.setCancelable(false);
        loading.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                NovedadesSyncTask.this.cancel(true);
                Data.setSyncAll(false);
                Intent intent  =new Intent("NOVEDADES_SYNC_FINISH");
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
        Intent intent  =new Intent("NOVEDADES_SYNC_FINISH");
        activity.sendBroadcast(intent);
        if(Data.isSyncAll()){
            //new FotosNovedadesSyncTask(activity).execute();
            FotosNovedadesSyncTask asyncTask =new FotosNovedadesSyncTask(activity);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }
}