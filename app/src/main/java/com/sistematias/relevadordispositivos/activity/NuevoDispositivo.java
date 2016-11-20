package com.sistematias.relevadordispositivos.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.sistematias.relevadordispositivos.Adapters.ArrayAdapterTipoNovedad;
import com.sistematias.relevadordispositivos.R;
import com.sistematias.relevadordispositivos.clases.Device;
import com.sistematias.relevadordispositivos.model.TipoNovedad;

import java.util.ArrayList;


public class NuevoDispositivo extends ActionBarActivity {
    LinearLayout llBotoneraNovedades;
    ScrollView svNovedades;
    EditText txtCodigoProducto;
    public static String codigo;
    public static Context context;

    private EditText txtNumeroDispositivo;
    private EditText txtApellido;
    private EditText txtCUIL;
    private EditText txtSeccion;
    private EditText txtTurnoOrigen;
    private EditText txtTrunoActual;
    private EditText txtMarca;
    private EditText txtContratoFirmado;
    private EditText txthdi;
    private EditText txtNumeroDeSerie;
    private EditText txtdni;
    private EditText txtEstado;
    private EditText txtMigracion;
    private EditText txtReclamo;
    private EditText txtDenunciaRobo;
    private EditText txtReasignadoA;
    private EditText txtFueReasignado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_novedad);

        txtNumeroDispositivo = (EditText) findViewById(R.id.txtNumeroDispositivo);
        txtApellido = (EditText) findViewById(R.id.txtApellido);
        txtCUIL = (EditText) findViewById(R.id.txtCUIL);
        txtSeccion = (EditText) findViewById(R.id.txtSeccion);
        txtTurnoOrigen = (EditText) findViewById(R.id.txtTurnoOrigen);
        txtTrunoActual = (EditText) findViewById(R.id.txtTrunoActual);
        txtMarca = (EditText) findViewById(R.id.txtMarca);
        txtContratoFirmado = (EditText) findViewById(R.id.txtContratoFirmado);
        txthdi = (EditText) findViewById(R.id.txthdi);
        txtNumeroDeSerie = (EditText) findViewById(R.id.txtNumeroDeSerie);
        txtdni = (EditText) findViewById(R.id.txtdni);
        txtEstado = (EditText) findViewById(R.id.txtEstado);
        txtMigracion = (EditText) findViewById(R.id.txtMigracion);
        txtReclamo = (EditText) findViewById(R.id.txtReclamo);
        txtDenunciaRobo = (EditText) findViewById(R.id.txtDenunciaRobo);
        txtReasignadoA = (EditText) findViewById(R.id.txtReasignadoA);
        txtFueReasignado = (EditText) findViewById(R.id.txtFueReasignado);


        getControls();
        getSupportActionBar().hide();
        Intent intent = getIntent();
        String cuil = intent.getStringExtra("cuil");
        if (cuil == null) cuil = "";

        String codigo = intent.getStringExtra("codigo");
        if (codigo == null) cuil = "";
        Device device = Device.getDeviceByCuil(cuil);
        txtNumeroDispositivo.setText(codigo);
        txtApellido.setText(device.getApellido());
        txtCUIL.setText(device.getCuil());
        txtSeccion.setText(device.getSecion());
        txtTurnoOrigen.setText(device.getTurnoOrigen());
        txtTrunoActual.setText(device.getTurnoActual());
        txtMarca.setText(device.getMarca());
        txtContratoFirmado.setText(device.getFirm());
        txthdi.setText(device.getHid());
        txtNumeroDeSerie.setText(device.getNroSerie());
        txtdni.setText(device.getDni());
        txtEstado.setText(device.getEstado());
        txtMigracion.setText(device.getMigracion());
        txtReclamo.setText(device.getReclamo());
        txtDenunciaRobo.setText(device.getDenunciaRobo());
        txtReasignadoA.setText(device.getReasignadoA());
        txtFueReasignado.setText(device.getfReasignado());

    }

    public void getControls() {
        llBotoneraNovedades = (LinearLayout) findViewById(R.id.llBotoneraNovedades);
        svNovedades = (ScrollView) findViewById(R.id.svNovedades);
        txtCodigoProducto = (EditText) findViewById(R.id.txtCodigoProducto);
    }


    public void btnBuscarEanOnClickListener(View v){
        getProductoBySearch(txtCodigoProducto.getText().toString().trim());
    }

    public void btnGuardarNovedadOnClickListener(View v) {
        String validar = validar();
        if(!validar.equals("EXITO")){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Aviso");
            builder.setMessage(validar);
            builder.setPositiveButton("Aceptar",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return;
        }else{
            guardar();

            return;
        }
    }

    public void guardar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Guardar");
        builder.setMessage("¿Guardar los cambios?");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Device device = new Device();
                device.setNumero(txtNumeroDispositivo.getText().toString().trim());
                device.setCuil(txtCUIL.getText().toString().trim());
                device.setApellido(txtApellido.getText().toString().trim());
                device.setSecion(txtSeccion.getText().toString().trim());
                device.setTurnoOrigen(txtTurnoOrigen.getText().toString().trim());
                device.setTurnoActual(txtTrunoActual.getText().toString().trim());
                device.setFirm(txtContratoFirmado.getText().toString().trim());
                device.setMarca(txtMarca.getText().toString().trim());
                device.setHid(txthdi.getText().toString().trim());
                device.setNroSerie(txtNumeroDeSerie.getText().toString().trim());
                device.setDni(txtdni.getText().toString().trim());
                device.setEstado(txtEstado.getText().toString().trim());
                device.setMigracion(txtMigracion.getText().toString().trim());
                device.setReclamo(txtReclamo.getText().toString().trim());
                device.setDenunciaRobo(txtDenunciaRobo.getText().toString().trim());
                device.setReasignadoA(txtReasignadoA.getText().toString().trim());
                device.setfReasignado(txtFueReasignado.getText().toString().trim());
                device.save();

                AlertDialog.Builder builder = new AlertDialog.Builder(NuevoDispositivo.this);
                builder.setMessage("Los cambios se han guardado correctamente")
                        .setCancelable(false)
                        .setPositiveButton("Nuevo Relevamiento", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                Intent intent = new Intent();
                                intent.setAction("NUEVO");
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        })
                        .setNegativeButton("Volver a Menu", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                Intent intent = new Intent();
                                intent.setAction("SALIR");
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
        builder.create().show();
    }

    public void btnCancelarNovedadOnClickListener(View v) {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_novedad, menu);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            String res =scanResult.getContents();
            if(res!=null){

            }
        }
    }

    public void  getProductoBySearch(String ean){
        codigo = ean;
    }

    public void showOnBackPressedMessage() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                NuevoDispositivo.this);
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
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public String validar(){
        return "EXITO";
    }
}
