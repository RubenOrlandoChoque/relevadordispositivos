package com.sistematias.relevadordispositivos.Tasks;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.sistematias.relevadordispositivos.clases.ConnectionChecker;
import com.sistematias.relevadordispositivos.clases.Data;
import com.sistematias.relevadordispositivos.clases.Querys;
import com.sistematias.relevadordispositivos.model.Config;
import com.sistematias.relevadordispositivos.model.Tracking;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by RUBEN on 01/11/2015.
 */
public class TrackingSyncTask extends AsyncTask<String, Integer, Boolean> {
    //ProgressDialog loading = null;
    private Context activity;

    public TrackingSyncTask(Context _activity) {
        activity = _activity;
    }

    @Override
    protected Boolean doInBackground(String... params) {

        TelephonyManager mngr = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        String imei = mngr.getDeviceId();
        Config url = new Config();
        url.getConfigByCod("CONFIG_SERVER");

        Config nameSpace = new Config();
        nameSpace.getConfigByCod("CONFIG_NAMESAPCE");

        final String NAMESPACE = nameSpace.getValueConfig();
        final String URL = url.getValueConfig();
        final String METHOD_NAME = "SincronizarTracking";
        final String SOAP_ACTION = NAMESPACE + METHOD_NAME;

        JSONArray jsonArray = new JSONArray();

        Cursor cursor = null;
        cursor = Data.getConn().executeSelect(Querys.getTracking(), cursor);
        int i = 0;
        while (cursor.moveToNext()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("IdTracking", cursor.getInt(0) + "");
                jsonObject.put("Coordenadas", cursor.getString(1) + ";" + cursor.getString(2));
                jsonObject.put("Rango", ((int) Double.parseDouble(cursor.getString(3))) + "");
                jsonObject.put("CodUsuario", cursor.getString(5));
                jsonObject.put("IMEI", imei==null?"":imei);
                jsonObject.put("FechaCaptura", cursor.getString(7));
                jsonObject.put("CodPuntoVenta", cursor.getString(8));
                jsonObject.put("CodTracking", cursor.getString(9));
                jsonObject.put("CodSucursal", cursor.getString(10));

                jsonArray.put(i, jsonObject);
                i++;
            } catch (JSONException e) {
                Log.i("ERROR", e.getMessage());
                e.printStackTrace();
            }
        }

        try {
            Log.i("data", jsonArray.toString(5));
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
                        System.out.println(json.getString("CodTracking"));
                        System.out.println(json.getString("FechaRegistro"));
                        Tracking tracking = new Tracking();
                        tracking.getTrackingByCodTracking(json.getString("CodTracking"));
                        tracking.setFechaRegistro(json.getString("FechaRegistro"));
                        tracking.setData();
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
        /*loading = new ProgressDialog(activity);
        loading.setTitle("Sincronizando Tracking");
        loading.setMessage("Esto puede demorar algunos minutos.\nAguarde por favor...");
        loading.setCancelable(false);
        loading.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TrackingSyncTask.this.cancel(true);
                Intent intent  =new Intent("TRACKING_SYNC_FINISH");
                Data.setSyncAll(false);
                activity.getApplicationContext().sendBroadcast(intent);
                dialog.dismiss();
            }
        });
        loading.show();*/

    }

    @Override
    protected void onPostExecute(Boolean result) {
        /*try{
            loading.hide();
            loading.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }
        Intent intent  =new Intent("TRACKING_SYNC_FINISH");
        activity.getApplicationContext().sendBroadcast(intent);
        if(Data.isSyncAll()){
            //new ProductosSyncTask(activity).execute();
            ProductosSyncTask asyncTask =new ProductosSyncTask(activity);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }*/

        super.onPostExecute(result);
        ConnectionChecker.getInstance().setIsWorking(false);
        ConnectionChecker.getInstance().getAsyncTask().cancel(true);
        ConnectionChecker.getInstance().setAsyncTask(null);
        ConnectionChecker.getInstance().setAsyncTask(new TrackingSyncTask(activity));
    }
}