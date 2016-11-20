package com.sistematias.relevadordispositivos.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.sistematias.relevadordispositivos.R;
import com.sistematias.relevadordispositivos.Tasks.FotosNovedadesSyncTask;
import com.sistematias.relevadordispositivos.Tasks.FotosRuteosSyncTask;
import com.sistematias.relevadordispositivos.Tasks.NovedadesSyncTask;
import com.sistematias.relevadordispositivos.Tasks.ProductosSyncTask;
import com.sistematias.relevadordispositivos.Tasks.RuteosSyncTask;
import com.sistematias.relevadordispositivos.Tasks.SincronizarDatosTask;
import com.sistematias.relevadordispositivos.Tasks.SincronizarFotosTask;
import com.sistematias.relevadordispositivos.Tasks.TrackingSyncTask;
import com.sistematias.relevadordispositivos.clases.Data;
import com.sistematias.relevadordispositivos.clases.Querys;
import com.sistematias.relevadordispositivos.model.Config;

import java.util.Date;


public class SincronizacionActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        finalizadoSincronizacionDatos = false;
        setContentView(R.layout.activity_sincronizacion);

        /*((TextView)findViewById(R.id.txtRuteoTotal)).setText("Total: "+Data.getConn().getStringSelect(Querys.getTotalRuteo()));
        ((TextView)findViewById(R.id.txtRuteoSync)).setText("Sincronizadas: "+Data.getConn().getStringSelect(Querys.getSyncRuteo()));

        ((TextView)findViewById(R.id.txtNovedadesTotal)).setText("Total: "+Data.getConn().getStringSelect(Querys.getTotalNovedades()));
        ((TextView)findViewById(R.id.txtNovedadesSync)).setText("Sincronizadas: "+Data.getConn().getStringSelect(Querys.getSyncNovedades()));

        ((TextView)findViewById(R.id.txtFotosTotal)).setText("Total: "+Data.getConn().getStringSelect(Querys.getTotalFotos()));
        ((TextView)findViewById(R.id.txtFotosSync)).setText("Sincronizadas: "+Data.getConn().getStringSelect(Querys.getSyncFotos()));

        ((TextView)findViewById(R.id.txtTrackingTotal)).setText("Total: "+Data.getConn().getStringSelect(Querys.getTotalTracking()));
        ((TextView)findViewById(R.id.txtTrackingSync)).setText("Sincronizadas: "+Data.getConn().getStringSelect(Querys.getSyncTracking()));

        ((TextView)findViewById(R.id.txtFotosRuteoTotal)).setText("Total: "+Data.getConn().getStringSelect(Querys.getTotalFotosRuteo()));
        ((TextView)findViewById(R.id.txtFotosRuteoSync)).setText("Sincronizadas: "+Data.getConn().getStringSelect(Querys.getSyncFotosRuteo()));

        ((TextView)findViewById(R.id.txtProductosTotal)).setText("Existentes: "+Data.getConn().getStringSelect(Querys.getTotalProductos()));
        */

        ((TextView)findViewById(R.id.totalnovedades)).setText(Data.getConn().getStringSelect(Querys.getTotalNovedades()));
        ((TextView)findViewById(R.id.sincronizadasnovedades)).setText(Data.getConn().getStringSelect(Querys.getSyncNovedades()));
        ((TextView)findViewById(R.id.totalruteos)).setText(Data.getConn().getStringSelect(Querys.getTotalRuteo()));
        ((TextView)findViewById(R.id.sincronizadasruteos)).setText(Data.getConn().getStringSelect(Querys.getSyncRuteo()));
        int fotostotalnovedades = Data.getConn().getIntSelect(Querys.getTotalFotos());
        int fotossincronizadassnovedades = Data.getConn().getIntSelect(Querys.getSyncFotos());
        int fotostotalruteo = Data.getConn().getIntSelect(Querys.getTotalFotosRuteo());
        int fotossincronizadasruteo = Data.getConn().getIntSelect(Querys.getSyncFotosRuteo());
        ((TextView)findViewById(R.id.totalfotos)).setText(Integer.toString(fotostotalnovedades + fotostotalruteo));
        ((TextView)findViewById(R.id.sincronizadasfotos)).setText(Integer.toString(fotossincronizadassnovedades + fotossincronizadasruteo));
        ((TextView)findViewById(R.id.totalproductos)).setText(Data.getConn().getStringSelect(Querys.getTotalProductos()));

        Config server = new Config();
        server.getConfigByCod("CONFIG_LAST_SYNC_DATE");
        String d = server.getValueConfig();
        ((TextView)findViewById(R.id.ultimaactualizacion)).setText("Ultima actualización: "+d);
        getSupportActionBar().hide();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sincronizacion, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initEvent(){

    }

    public void setBtnSyncRuteoOnClickListener(View v){
        Data.setSyncAll(false);
        //new RuteosSyncTask(SincronizacionActivity.this).execute();
        RuteosSyncTask asyncTask =new RuteosSyncTask(SincronizacionActivity.this);
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void setBtnSyncNovedadesOnClickListener(View v){
        Data.setSyncAll(false);
        //new NovedadesSyncTask(SincronizacionActivity.this).execute();
        NovedadesSyncTask asyncTask =new NovedadesSyncTask(SincronizacionActivity.this);
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void setBtnSyncFotosOnClickListener(View v){
        Data.setSyncAll(false);
//        new FotosNovedadesSyncTask(SincronizacionActivity.this).execute();
        FotosNovedadesSyncTask asyncTask =new FotosNovedadesSyncTask(SincronizacionActivity.this);
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void setBtnSyncFotosRuteosOnClickListener(View v){
        Data.setSyncAll(false);
        //new FotosRuteosSyncTask(SincronizacionActivity.this).execute();
        FotosRuteosSyncTask asyncTask =new FotosRuteosSyncTask(SincronizacionActivity.this);
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void setBtnSyncProductosOnClickListener(View v){
        Data.setSyncAll(false);
        //new ProductosSyncTask(SincronizacionActivity.this).execute();
        ProductosSyncTask asyncTask =new ProductosSyncTask(SincronizacionActivity.this);
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void setBtnSyncTrackingsOnClickListener(View v){
        Data.setSyncAll(false);
        //new TrackingSyncTask(SincronizacionActivity.this).execute();
        TrackingSyncTask asyncTask =new TrackingSyncTask(SincronizacionActivity.this);
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void btnSincronizarTodoOnClickListener(View v){
        Data.setSyncAll(true);
        SincronizarDatosTask asyncTask =new SincronizarDatosTask(SincronizacionActivity.this);
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void btnCancelarSincronizacionOnclickListener(View v){
        finish();
    }


    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("action result", action);
//            if(intent.getAction().equals("TRACKING_SYNC_FINISH")){
//                txtTrackingTotal.setText("Total: "+Data.getConn().getStringSelect(Querys.getTotalTracking()));
//                txtTrackingSync.setText("Sincronizadas: "+Data.getConn().getStringSelect(Querys.getSyncTracking()));
//            }
            switch (action){
                case "RUTEOS_SYNC_FINISH":
                    //((TextView)findViewById(R.id.txtRuteoTotal)).setText("Total: "+Data.getConn().getStringSelect(Querys.getTotalRuteo()));
                    //((TextView)findViewById(R.id.txtRuteoSync)).setText("Sincronizadas: "+Data.getConn().getStringSelect(Querys.getSyncRuteo()));
                    break;
                case "NOVEDADES_SYNC_FINISH":
                    //Log.i("sincronizo","sicronizo novedades, deveria actualizar");
                    //((TextView)findViewById(R.id.txtNovedadesTotal)).setText("Total: "+Data.getConn().getStringSelect(Querys.getTotalNovedades()));
                    //((TextView)findViewById(R.id.txtNovedadesSync)).setText("Sincronizadas: "+Data.getConn().getStringSelect(Querys.getSyncNovedades()));
                    break;
                case "FOTOS_NOVEDADES_SYNC_FINISH":
                    //((TextView)findViewById(R.id.txtFotosTotal)).setText("Total: "+Data.getConn().getStringSelect(Querys.getTotalFotos()));
                    //((TextView)findViewById(R.id.txtFotosSync)).setText("Sincronizadas: "+Data.getConn().getStringSelect(Querys.getSyncFotos()));
                    break;
                case "FOTOS_RUTEOS_SYNC_FINISH":
                    //((TextView)findViewById(R.id.txtFotosRuteoTotal)).setText("Total: "+Data.getConn().getStringSelect(Querys.getTotalFotosRuteo()));
                    //((TextView)findViewById(R.id.txtFotosRuteoSync)).setText("Sincronizadas: "+Data.getConn().getStringSelect(Querys.getSyncFotosRuteo()));
                    break;
                case "TRACKING_SYNC_FINISH":
                    //((TextView)findViewById(R.id.txtTrackingTotal)).setText("Total: "+Data.getConn().getStringSelect(Querys.getTotalTracking()));
                    //((TextView)findViewById(R.id.txtTrackingSync)).setText("Sincronizadas: "+Data.getConn().getStringSelect(Querys.getSyncTracking()));
                    break;
                case "PRODUCTOS_SYNC_FINISH":
                    //((TextView)findViewById(R.id.txtProductosTotal)).setText("Existentes: "+Data.getConn().getStringSelect(Querys.getTotalProductos()));
                    break;
                case "DATOS_SYNC_FINISH":
                    ((TextView)findViewById(R.id.totalnovedades)).setText(Data.getConn().getStringSelect(Querys.getTotalNovedades()));
                    ((TextView)findViewById(R.id.sincronizadasnovedades)).setText(Data.getConn().getStringSelect(Querys.getSyncNovedades()));
                    ((TextView)findViewById(R.id.totalruteos)).setText(Data.getConn().getStringSelect(Querys.getTotalRuteo()));
                    ((TextView)findViewById(R.id.sincronizadasruteos)).setText(Data.getConn().getStringSelect(Querys.getSyncRuteo()));

                    ((TextView)findViewById(R.id.totalproductos)).setText(Data.getConn().getStringSelect(Querys.getTotalProductos()));

                    Config server = new Config();
                    server.getConfigByCodEmpty("CONFIG_LAST_SYNC_DATE");
                    server.setValueConfig(Data.FECHA_LAST_SESSION.format(new Date()));
                    if(server.getCodConfig().isEmpty()){
                        server.setCodConfig("CONFIG_LAST_SYNC_DATE");
                        server.saveData();
                    }else{
                        server.setData();
                    }
                    String d = server.getValueConfig();
                    ((TextView)findViewById(R.id.ultimaactualizacion)).setText("Ultima actualización: "+d);
                    break;
                case "FOTOS_SYNC_FINISH":
                    int fotostotalnovedades = Data.getConn().getIntSelect(Querys.getTotalFotos());
                    int fotossincronizadassnovedades = Data.getConn().getIntSelect(Querys.getSyncFotos());
                    int fotostotalruteo = Data.getConn().getIntSelect(Querys.getTotalFotosRuteo());
                    int fotossincronizadasruteo = Data.getConn().getIntSelect(Querys.getSyncFotosRuteo());
                    ((TextView)findViewById(R.id.totalfotos)).setText(Integer.toString(fotostotalnovedades+fotostotalruteo));
                    ((TextView)findViewById(R.id.sincronizadasfotos)).setText(Integer.toString(fotossincronizadassnovedades + fotossincronizadasruteo));
                    Config serverw = new Config();
                    serverw.getConfigByCodEmpty("CONFIG_LAST_SYNC_DATE");
                    serverw.setValueConfig(Data.FECHA_LAST_SESSION.format(new Date()));
                    if(serverw.getCodConfig().isEmpty()){
                        serverw.setCodConfig("CONFIG_LAST_SYNC_DATE");
                        serverw.saveData();
                    }else{
                        serverw.setData();
                    }
                    String dd = serverw.getValueConfig();
                    ((TextView)findViewById(R.id.ultimaactualizacion)).setText("Ultima actualización: "+dd);
                    break;
            }
        }
    };

    @Override
    public void onResume(){
        super.onResume();
        IntentFilter filter = new IntentFilter("TRACKING_SYNC_FINISH");
        filter.addAction("RUTEOS_SYNC_FINISH");
        filter.addAction("NOVEDADES_SYNC_FINISH");
        filter.addAction("FOTOS_NOVEDADES_SYNC_FINISH");
        filter.addAction("FOTOS_RUTEOS_SYNC_FINISH");
        filter.addAction("PRODUCTOS_SYNC_FINISH");
        filter.addAction("DATOS_SYNC_FINISH");
        filter.addAction("FOTOS_SYNC_FINISH");
        this.registerReceiver(broadcastReceiver,filter);
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        try{
            this.unregisterReceiver(broadcastReceiver);
        }catch (Exception e){

        }
    }

    public void sincronizarDatosListener(View v){
        SincronizarDatosTask asyncTask =new SincronizarDatosTask(SincronizacionActivity.this);
        asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public void sincronizarFotosListener(View v){
        if(finalizadoSincronizacionDatos){
            SincronizarFotosTask asyncTask =new SincronizarFotosTask(SincronizacionActivity.this);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else{
            Data.setSyncAll(true);
            SincronizarDatosTask asyncTask =new SincronizarDatosTask(SincronizacionActivity.this);
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

    }

    public static boolean finalizadoSincronizacionDatos = false;

}
