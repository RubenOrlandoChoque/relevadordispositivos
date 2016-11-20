package com.sistematias.relevadordispositivos.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.sistematias.relevadordispositivos.R;
import com.sistematias.relevadordispositivos.clases.ConnectionChecker;
import com.sistematias.relevadordispositivos.model.Persistencia;


public class MenuPrincipalActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal_admin);
        getSupportActionBar().hide();
    }

    public void menu_item_4_onClickListener(View v) {
        Intent intentNovedadActivity = new Intent(MenuPrincipalActivity.this, NuevoRelevamientoActivity.class);
        startActivity(intentNovedadActivity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_menu_principal, menu);
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

    public boolean isGPSEnabled(Context mContext) {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void exitOnClickListener(View v) {
        showExitMessage();
    }

    public void showExitMessage() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Salir");

        // Setting Dialog Message
        alertDialog.setMessage("¿Desea cerrar la sesión?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {
                    ConnectionChecker.getInstance().setIsWorking(false);
                    ConnectionChecker.getInstance().getAsyncTask().cancel(true);
                    ConnectionChecker.getInstance().setAsyncTask(null);
                } catch (Exception e2) {

                }
                Persistencia.limpiarPersistencia();
                finish();
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        showExitMessage();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }
}