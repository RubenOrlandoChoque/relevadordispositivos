package com.sistematias.relevadordispositivos.Tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.sistematias.relevadordispositivos.clases.Data;
import com.sistematias.relevadordispositivos.clases.Querys;
import com.sistematias.relevadordispositivos.model.Config;
import com.sistematias.relevadordispositivos.model.Producto;

import org.json.JSONArray;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Date;

/**
 * Created by RUBEN on 31/10/2015.
 */
public class ProductosSyncTask extends AsyncTask<String, Integer, Boolean> {
    ProgressDialog loading = null;
    private Context activity;
    private static final String tag = "product Sync-->";
    public ProductosSyncTask(Context _activity){
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
        final String METHOD_NAME = "SincronizarProductos";
        final String SOAP_ACTION = NAMESPACE + METHOD_NAME;

        try{
            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
            String ultFechaAct = Data.getConn().getStringSelect(Querys.getLastFechaModificacionProducto());
            Date ultFecha;
            if(ultFechaAct.trim().equals("")){
                ultFecha = Data.FECHA_DATABASE_FORMAT.parse("1980-01-01 00:00:00");
            }else{
                ultFecha = Data.FECHA_DATABASE_FORMAT.parse(ultFechaAct);
            }
            ultFechaAct = Data.FECHA_SHORT_FORMAT.format(ultFecha);
            request.addProperty("fechaUltAct", ultFechaAct);
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
                    //Log.i("Web Service Result", res);
                    //JSONObject data = new JSONObject(res);

                    /*Productos que se elminaran*/
                    /*JSONArray eliminar = new JSONArray(data.getString("Eliminar"));
                    ArrayList<String> codigosProductos = new ArrayList<>();
                    for (int pos=0;pos< eliminar.length(); pos++)
                    {
                        JSONObject prod = eliminar.getJSONObject(pos);
                        System.out.println(prod.getString("CodProducto"));
                        codigosProductos.add("'"+prod.getString("CodProducto")+"'");
                    }
                    String codProdToDelete  = android.text.TextUtils.join(",", codigosProductos);
                    Data.getConn().delete(Querys.deleteProductosByCodProducto(codProdToDelete));
                    */
                    /*Nuevos productos*/
                    //JSONArray nuevos = new JSONArray(data.getString("Nuevos"));
                    JSONArray array = new JSONArray(res);
                    Log.i("json",array.toString(5));
                    for (int p=0;p< array.length(); p++)
                    {
                        JSONObject prod = array.getJSONObject(p);
                        System.out.println(prod.getString("CodProducto"));
                        System.out.println(prod.getString("EAN"));
                        System.out.println(prod.getString("Nombre"));
                        System.out.println(prod.getString("Marca"));
                        System.out.println(prod.getString("Presentacion"));
                        System.out.println(prod.getString("Imagen"));
                        System.out.println(prod.getBoolean("Propio"));
                        System.out.println(prod.getBoolean("Habilitado"));
                        System.out.println(prod.getString("CodEmpresa"));

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

                } catch (Exception e) {
                    Log.i("ERROR",e.getMessage());
                    e.printStackTrace();
                }
            } catch (Exception e) {
                Log.i("ERROR",e.getMessage());
                e.printStackTrace();
            }

        }catch(Exception e){

            e.printStackTrace();
        }
        return true;
    }
    @Override
    protected void onPreExecute() {
        loading = new ProgressDialog(activity);
        loading.setTitle("Sincronizando Productos");
        loading.setMessage("Esto puede demorar algunos minutos.\nAguarde por favor...");
        loading.setCancelable(false);
//        loading.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                ProductosSyncTask.this.cancel(true);
//                Intent intent  =new Intent("PRODUCTOS_SYNC_FINISH");
//                activity.sendBroadcast(intent);
//            }
//        });
        loading.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ProductosSyncTask.this.cancel(true);
                Data.setSyncAll(false);
                Intent intent  =new Intent("PRODUCTOS_SYNC_FINISH");
                activity.getApplicationContext().sendBroadcast(intent);
                dialog.dismiss();
            }
        });
        loading.show();

    }
    @Override
    protected void onPostExecute(Boolean result) {

        try{
            loading.hide();
            loading.dismiss();
        }catch(Exception e){
            e.printStackTrace();
        }
        Intent intent  =new Intent("PRODUCTOS_SYNC_FINISH");
        activity.sendBroadcast(intent);
        Data.setSyncAll(false);
    }

}
