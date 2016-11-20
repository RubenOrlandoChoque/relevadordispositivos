package com.sistematias.relevadordispositivos.Tasks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.sistematias.relevadordispositivos.activity.SincronizacionActivity;
import com.sistematias.relevadordispositivos.clases.Data;
import com.sistematias.relevadordispositivos.clases.Querys;
import com.sistematias.relevadordispositivos.model.Config;
import com.sistematias.relevadordispositivos.model.FotoNovedad;
import com.sistematias.relevadordispositivos.model.Novedad;
import com.sistematias.relevadordispositivos.model.Producto;
import com.sistematias.relevadordispositivos.model.Ruteo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.ParseException;
import java.util.Date;

/**
 * Created by RUBEN on 23/05/2016.
 */
public class SincronizarDatosTask extends AsyncTask<String, Integer, Boolean> {
    ProgressDialog loading = null;
    private Context activity;

    public SincronizarDatosTask(Context _activity) {
        activity = _activity;
    }

    @Override
    protected Boolean doInBackground(String... params) {
        boolean result = true;
        Config url = new Config();
        url.getConfigByCod("CONFIG_SERVER");
        Config nameSpace = new Config();
        nameSpace.getConfigByCod("CONFIG_NAMESAPCE");
        final String NAMESPACE = nameSpace.getValueConfig();
        final String URL = url.getValueConfig();
        final String METHOD_NAME = "SincronizarDatos";
        final String SOAP_ACTION = NAMESPACE + METHOD_NAME;

        JSONArray jsonArrayNovedades = getNovedades();
        JSONArray jsonArrayRuteos = getRuteos();
        String fechaUltAct = fechaUltimaModificacionProductos();
        try {
            Log.i("novedades", jsonArrayNovedades.toString(5));
            Log.i("ruteos", jsonArrayRuteos.toString(5));
            Log.i("fechaUltAct", fechaUltAct);
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            request.addProperty("novedades", jsonArrayNovedades.toString());
            request.addProperty("ruteos", jsonArrayRuteos.toString());
            request.addProperty("fechaUltAct", fechaUltAct);
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.dotNet = true;
            envelope.setOutputSoapObject(request);

            HttpTransportSE transporte = new HttpTransportSE(URL);
            transporte.call(SOAP_ACTION, envelope);
            SoapObject resultado_xml = (SoapObject) envelope.bodyIn;
            String res = "";
            res = resultado_xml.getProperty(0).toString();
            Log.i("Web Service Result", res);
            JSONObject dataresult = new JSONObject(res);
            JSONArray datosusuarios = dataresult.getJSONArray("novedades");
            JSONArray datosruteo = dataresult.getJSONArray("ruteos");
            JSONArray datosproductos = dataresult.getJSONArray("productos");

            actualizarNovedades(datosusuarios);
            actualizarRuteos(datosruteo);
            actualizarProductos(datosproductos);
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        }
        return result;
    }

    @Override
    protected void onPreExecute() {
        SincronizacionActivity.finalizadoSincronizacionDatos = false;
        loading = new ProgressDialog(activity);
        loading.setTitle("Sincronizando Datos");
        loading.setMessage("Esto puede demorar algunos minutos.\nAguarde por favor...");
        loading.setCancelable(false);
        loading.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Data.setSyncAll(false);
                SincronizarDatosTask.this.cancel(true);
                dialog.dismiss();
            }
        });
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

            Intent intent  =new Intent("DATOS_SYNC_FINISH");
            intent.putExtra("resultado", true);
            activity.sendBroadcast(intent);

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.activity);
            if (result) {
                SincronizacionActivity.finalizadoSincronizacionDatos = true;
                if(Data.isSyncAll()){
                    Data.setSyncAll(false);
                    SincronizarFotosTask asyncTask =new SincronizarFotosTask(activity);
                    asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }else{
                    alertDialog.setTitle("Éxito");
                    alertDialog.setMessage("Sincronización realizada con éxito!");
                    alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
            } else {
                alertDialog.setTitle("Error");
                alertDialog.setMessage("La sincronización no se realizó. Verifique conexión de internet o comuníquese con el administrador.");
                alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            }

        }
    }

    private JSONArray getNovedades() {
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
        return jsonArray;
    }

    private JSONArray getRuteos() {
        JSONArray jsonArray = new JSONArray();
        Cursor cursor = null;
        cursor = Data.getConn().executeSelect(Querys.getRuteos(), cursor);
        int i = 0;
        while (cursor.moveToNext()) {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("IdRuteo", cursor.getInt(0) + "");
                jsonObject.put("CodRuteo", cursor.getString(1));
                jsonObject.put("CodProducto", cursor.getString(2));
                jsonObject.put("CodRepositor", cursor.getString(3));
                jsonObject.put("CodPuntoVenta", cursor.getString(4));
                jsonObject.put("Observacion", cursor.getString(5));
                jsonObject.put("FechaCreacion", cursor.getString(6));
                jsonObject.put("FechaRegistro", cursor.getString(7));
                jsonObject.put("Coordenadas", cursor.getString(8));
                jsonObject.put("Precio", Double.toString(cursor.getDouble(9)).replace(".", ","));
                jsonObject.put("Imagen", cursor.getString(10));
                jsonObject.put("Rango", cursor.getString(11));
                jsonObject.put("CodSucursal", cursor.getString(13));
                jsonArray.put(i, jsonObject);
                i++;
            } catch (JSONException e) {
                Log.i("ERROR", e.getMessage());
                e.printStackTrace();
            }
        }
        return jsonArray;
    }

    private String fechaUltimaModificacionProductos() {
        String ultFechaAct = Data.getConn().getStringSelect(Querys.getLastFechaModificacionProducto());
        Date ultFecha;
        try {
            if (ultFechaAct.trim().equals("")) {
                ultFecha = Data.FECHA_DATABASE_FORMAT.parse("1980-01-01 00:00:00");
            } else {
                ultFecha = Data.FECHA_DATABASE_FORMAT.parse(ultFechaAct);
            }
            ultFechaAct = Data.FECHA_SHORT_FORMAT.format(ultFecha);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ultFechaAct;
    }

    private void actualizarNovedades(JSONArray datos) throws JSONException {
        for (int i = 0; i < datos.length(); i++) {
            JSONObject json = datos.getJSONObject(i);
            Novedad novedad = new Novedad();
            novedad.getNovedadByCod(json.getString("CodNovedad"));
            novedad.setFechaRegistro(json.getString("FechaRegistro"));
            novedad.setData();
            Novedad.updateCodNovedadInNovedades(json.getString("CodNovedad"), json.getString("CodNewNovedad"));
            FotoNovedad.updateCodNovedad(json.getString("CodNovedad"), json.getString("CodNewNovedad"));
        }
    }

    private void actualizarRuteos(JSONArray datos) throws JSONException {
        for (int i = 0; i < datos.length(); i++) {
            JSONObject json = datos.getJSONObject(i);
            System.out.println(json.getString("CodRuteo"));
            System.out.println(json.getString("FechaRegistro"));
            Ruteo ruteo = new Ruteo();
            ruteo.getRuteoByCodRuteo(json.getString("CodRuteo"));
            ruteo.setFechaRegistro(json.getString("FechaRegistro"));
            ruteo.setData();
        }
    }

    private void actualizarProductos(JSONArray datos) throws JSONException, ParseException {
        for (int i = 0; i < datos.length(); i++) {
            JSONObject prod = datos.getJSONObject(i);
            /*System.out.println(prod.getString("CodProducto"));
            System.out.println(prod.getString("EAN"));
            System.out.println(prod.getString("Nombre"));
            System.out.println(prod.getString("Marca"));
            System.out.println(prod.getString("Presentacion"));
            System.out.println(prod.getString("Imagen"));
            System.out.println(prod.getBoolean("Propio"));
            System.out.println(prod.getBoolean("Habilitado"));
            System.out.println(prod.getString("CodEmpresa"));*/

            Producto producto = new Producto();
            producto.setCodProducto(prod.getString("CodProducto").trim());
            producto.setEan(prod.getString("EAN").trim());
            producto.setNombre(prod.getString("Nombre").trim());
            producto.setMarca(prod.getString("Marca").trim());
            producto.setPresentacion(prod.getString("Presentacion").trim());
            producto.setImagen(prod.getString("Imagen").trim());
            producto.setPropio(prod.getBoolean("Propio"));
            producto.setHabilitado(prod.getBoolean("Habilitado"));
            producto.setCodEmpresa(prod.getString("CodEmpresa").trim());
            producto.setFamilia(prod.getString("Familia").trim());
            Date d = Data.FECHA_SQL_SEVER_FORMAT.parse(prod.getString("FechaUltMod").trim());
            producto.setFechaModificacion(Data.FECHA_DATABASE_FORMAT.format(d));
            producto.saveData();

                        /*intentar traer la imagen del producto*/
//                        try {
//                            URL urlImage = new URL(prod.getString("Imagen"));
//                            HttpURLConnection connection = (HttpURLConnection) urlImage.openConnection();
//                            connection.setDoInput(true);
//                            connection.connect();
//                            InputStream input = connection.getInputStream();
//                            Bitmap myBitmap = BitmapFactory.decodeStream(input);
//
//                            File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), Data.DIRECTORIO_FOTO);
//                            Log.d(tag, "Find " + mediaStorageDir.getAbsolutePath());
//                            if (! mediaStorageDir.exists()){
//                                if (! mediaStorageDir.mkdirs()){
//                                    Log.e(tag,"Error intentando crear el directorio: "+Data.DIRECTORIO_FOTO);
//                                }else{
//                                    Log.e(tag,"Creado el directorio: "+Data.DIRECTORIO_FOTO);
//                                }
//                            }
//
//                            File image = new File(mediaStorageDir,"7791762055096.jpg");
//                            FileOutputStream out = null;
//                            try {
//                                out = new FileOutputStream(image);
//                                myBitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            } finally {
//                                try {
//                                    if (out != null) {
//                                        out.close();
//                                    }
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//                        } catch (IOException e) {
//                            // Log exception
//                            return null;
//                        }
        }
    }
}