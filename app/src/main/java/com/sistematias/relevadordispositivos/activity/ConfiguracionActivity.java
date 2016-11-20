package com.sistematias.relevadordispositivos.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.sistematias.relevadordispositivos.R;
import com.sistematias.relevadordispositivos.model.Config;


public class ConfiguracionActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        getSupportActionBar().hide();
        loadConfig();

    }

    public void loadConfig(){
        Config server = new Config();
        server.getConfigByCod("CONFIG_SERVER");
        EditText configServer = (EditText)findViewById(R.id.txtServidorConfig);
        configServer.setText(server.getValueConfig());
        configServer.setSelection(configServer.getText().length());

        Config nameSpace = new Config();
        nameSpace.getConfigByCod("CONFIG_NAMESAPCE");
        EditText configNamespace = (EditText)findViewById(R.id.txtNameSpaceConfig);
        configNamespace.setText(nameSpace.getValueConfig());
        configNamespace.setSelection(configNamespace.getText().length());

        /*Config frecuenciaTracking = new Config();
        frecuenciaTracking.getConfigByCod("CONFIG_FREQUENCY_TRACKING");
        EditText configTimeTracking = (EditText)findViewById(R.id.txtFrecuenciaTracking);
        configTimeTracking.setText(frecuenciaTracking.getValueConfig());
        configTimeTracking.setSelection(configTimeTracking.getText().length());*/
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_configuracion, menu);
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

    public void guardarConfig(View v){
        Config server = new Config();
        server.getConfigByCod("CONFIG_SERVER");
        server.setValueConfig(((EditText) findViewById(R.id.txtServidorConfig)).getText().toString().trim());
        server.setData();

        Config namespace = new Config();
        namespace.getConfigByCod("CONFIG_NAMESAPCE");
        namespace.setValueConfig(((EditText) findViewById(R.id.txtNameSpaceConfig)).getText().toString().trim());
        namespace.setData();

        /*Config frecuenciaTracking = new Config();
        frecuenciaTracking.getConfigByCod("CONFIG_FREQUENCY_TRACKING");
        frecuenciaTracking.setValueConfig(((EditText) findViewById(R.id.txtFrecuenciaTracking)).getText().toString().trim());
        frecuenciaTracking.setData();

        int time = Integer.parseInt(frecuenciaTracking.getValueConfig());

        gpsTracker.stopUsingGPS();
        gpsTracker.getLocation(this, time);*/


        AlertDialog.Builder builder = new AlertDialog.Builder(ConfiguracionActivity.this);
        builder.setMessage("Guardado correcto")
                .setCancelable(false)
                .setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void cancelarConfig(View v){
        finish();
    }

    public void showOnBackPressedMessage() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                ConfiguracionActivity.this);
        alertDialog.setTitle("Aviso");
        alertDialog.setMessage("¿Salir sin guardar?");
        alertDialog.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });

        alertDialog.setNegativeButton("Cancelar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        showOnBackPressedMessage();
    }



    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        //Log.i("service", "service start");
        //Intent intent = new Intent(this, GPSTracker.class);
        //bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    //GPSTracker gpsTracker;
    boolean mBound = false;
    /** Defines callbacks for service binding, passed to bindService() */
    /*private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.i("local binder","at to local binder");
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            GPSTracker.LocalBinder binder = (GPSTracker.LocalBinder) service;
            gpsTracker = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };*/
}
