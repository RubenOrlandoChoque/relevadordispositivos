package com.sistematias.relevadordispositivos.activity;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.sistematias.relevadordispositivos.Adapters.ArrayAdapterDispositivos;
import com.sistematias.relevadordispositivos.R;
import com.sistematias.relevadordispositivos.clases.Device;

import java.util.ArrayList;


public class NuevoRelevamientoActivity extends ActionBarActivity {
    public RelativeLayout resultScan;
    public TextView productCode;
    public EditText txtCodigoProducto;
    public ImageView img_cargando;
    public LinearLayout llBotoneraNuevoRuteo;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_ruteo);
        getSupportActionBar().hide();
        resultScan = (RelativeLayout) findViewById(R.id.resultScan);
        img_cargando = (ImageView) findViewById(R.id.img_cargando);
        txtCodigoProducto = (EditText) findViewById(R.id.txtCodigoProducto);
        resultScan.setVisibility(View.GONE);
        llBotoneraNuevoRuteo = (LinearLayout) findViewById(R.id.llBotoneraNuevoRuteo);
        llBotoneraNuevoRuteo.setVisibility(View.GONE);
    }

    public void scanBarcodeCustomLayout() {
        new IntentIntegrator(this).initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String numero = scanResult.getContents();
            codigo = numero;
            txtCodigoProducto.setText(codigo);
            if (numero != null) {
                boolean existeDispostivo = Device.existeDispositivo(numero);
                if (existeDispostivo) {
                    Intent intentNovedadActivity = new Intent(NuevoRelevamientoActivity.this, NuevoDispositivo.class);
                    intentNovedadActivity.putExtra("cuil", Device.getCuil(numero));
                    intentNovedadActivity.putExtra("codigo", codigo);
                    startActivityForResult(intentNovedadActivity, 10);
                } else {
                    resultScan.setVisibility(View.VISIBLE);
                }
            }
        }

        if (requestCode == 10) {
            if (resultCode == RESULT_OK) {
                resultScan.setVisibility(View.GONE);
                llBotoneraNuevoRuteo.setVisibility(View.GONE);
                String action = intent.getAction();
                if(action!=null){
                    if(action.equals("NUEVO")){
                        new IntentIntegrator(this).initiateScan();
                    }
                    if(action.equals("SALIR")){
                        finish();
                    }
                }
            }
            if(resultCode==RESULT_CANCELED){

            }
        }
    }

    public void escanearOnClickListener(View v){
        scanBarcodeCustomLayout();
    }

    public void showNotFoundSearch(String msg) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                NuevoRelevamientoActivity.this);
        alertDialog.setTitle("Aviso");
        alertDialog.setMessage(msg);
        alertDialog.setPositiveButton("Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    public void btnBuscarEanOnClickListener(View v) {
        String numero = txtCodigoProducto.getText().toString().trim();
        boolean existeDispostivo = Device.existeDispositivo(numero);
        if (existeDispostivo) {
            Intent intentNovedadActivity = new Intent(NuevoRelevamientoActivity.this, NuevoDispositivo.class);
            intentNovedadActivity.putExtra("cuil", Device.getCuil(numero));
            intentNovedadActivity.putExtra("codigo", codigo);
            startActivityForResult(intentNovedadActivity,10);
        } else {
            resultScan.setVisibility(View.VISIBLE);
        }
    }

    public void btnBuscarCuilOnClickListener(View v) {
        final ArrayList<Device> devices = Device.getDispositivosByCuil(((EditText) findViewById(R.id.txtcuil)).getText().toString().trim());
        if (devices.size() > 0) {
            if (devices.size() == 1) {
                Device d = devices.get(0);
                Intent intentNovedadActivity = new Intent(NuevoRelevamientoActivity.this, NuevoDispositivo.class);
                intentNovedadActivity.putExtra("cuil", d.getCuil());
                intentNovedadActivity.putExtra("codigo", codigo);
                startActivityForResult(intentNovedadActivity, 10);
            } else {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                ArrayAdapterDispositivos adapter = new ArrayAdapterDispositivos(this, devices);
                alertDialogBuilder
                        .setTitle("Se encontraron varias coincidencias.\nSeleccione una.")
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Device d = devices.get(which);
                                Intent intentNovedadActivity = new Intent(NuevoRelevamientoActivity.this, NuevoDispositivo.class);
                                intentNovedadActivity.putExtra("cuil", d.getCuil());
                                intentNovedadActivity.putExtra("codigo", codigo);
                                startActivityForResult(intentNovedadActivity, 10);
                            }
                        });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        } else {
            showNotFoundSearch("No se encontró ningún registro coincidente con los datos ingresados.");
        }
    }

    String codigo = "";

    public void btnBuscaSerieOnClickListener(View v) {

    }


    public void showOnBackPressedMessage() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                NuevoRelevamientoActivity.this);
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
}

