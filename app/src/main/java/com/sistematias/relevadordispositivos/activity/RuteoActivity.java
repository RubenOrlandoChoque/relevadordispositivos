package com.sistematias.relevadordispositivos.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.sistematias.relevadordispositivos.R;
import com.sistematias.relevadordispositivos.clases.Data;
import com.sistematias.relevadordispositivos.clases.DecimalDigitsInputFilter;
import com.sistematias.relevadordispositivos.clases.Util;
import com.sistematias.relevadordispositivos.model.Producto;
import com.sistematias.relevadordispositivos.model.Ruteo;

import java.io.File;
import java.util.Date;


public class RuteoActivity extends ActionBarActivity {

    public ImageView takePhoto;
    public ImageView fotoRuteo;
    public Button btnGuardarRuteo;
    public Button btnCancelarRuteo;
    public EditText txtPrecioRuteo;
    public EditText txtObservacionRuteo;
    public final int CAMERA_REQUEST = 5000;
    private Uri currentImageUri;
    public static String codigo;
    public static final String tag = "ruteo activity-->";
    public static int lastImageId = -1;

    //private int UPDATE_INTERVAL = 10 * 1000; // Intervalo de actualizacion. Por defecto: 10 segundos.
    //private int FASTEST_INTERVAL = 1 * 1000; // Intervalo de actualizacion rapido. Por defecto: 5 segundos.
    //private int DISPLACEMENT = 200; // Distancia en metros. Por defecto: 10 metros.
    //private int TIMEOUT = 60 * 60 * 1000; // Tiempo máximo de búsqueda. Por defecto: 5 minutos.
    /*public String latLocation = "0",
            longLocation = "0",
            distanceLocation = "0";*/

    private static final String GPS_MANAGER_BROADCAST_RECEIVER = "GPS_MANAGER_BROADCAST_RECEIVER";
    private static final String GPS_MANAGER_TIMEOUT_BROADCAST_RECEIVER = "GPS_MANAGER_TIMEOUT_BROADCAST_RECEIVER";

    /*private final BroadcastReceiver mGPSManagerBroadcastReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    latLocation = intent.getStringExtra("latLocation");
                    if(latLocation == null) latLocation = "0";
                    longLocation = intent.getStringExtra("longLocation");
                    if(longLocation == null) longLocation = "0";
                    distanceLocation = intent.getStringExtra("distanceLocation");
                    if(distanceLocation == null) distanceLocation = "0";

                    Log.i("tag", "NUEVA POSICIÓN ENCONTRADA >>> X: " + latLocation + ", Y: " + longLocation + ", DISTANCIA: " + distanceLocation);

                    Double distance = Double.valueOf(distanceLocation);

                    //((TextView) findViewById(R.id.searchingGPS)).setText("GPS encontrado: lat: " + latLocation + ", long: " + longLocation + ", precision: " + distanceLocation + " mts.");
                    ((TextView) findViewById(R.id.searchingGPS)).setText("GPS encontrado");
                    ((TextView) findViewById(R.id.searchingGPS)).setTextColor(getResources().getColor(R.color.result_points));

                }

            };*/

    /*private final BroadcastReceiver mGPSManagerTimeoutBroadcastReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    ((TextView) findViewById(R.id.searchingGPS)).setText("No se econtró ninguna posici+on gps");
                }
            };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ruteo);
        takePhoto = (ImageView) findViewById(R.id.takePhoto);
        fotoRuteo = (ImageView) findViewById(R.id.fotoRuteo);
        btnGuardarRuteo = (Button) findViewById(R.id.btnGuardarRuteo);
        btnCancelarRuteo = (Button) findViewById(R.id.btnCancelarRuteo);
        txtPrecioRuteo = (EditText) findViewById(R.id.txtPrecioRuteo);
        txtObservacionRuteo = (EditText) findViewById(R.id.txtObservacionRuteo);
        txtPrecioRuteo.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(10, 2)});
        Intent in = getIntent();
        codigo = in.getStringExtra("codigo");
        String action = in.getAction();
        if (currentImageUri == null && savedInstanceState == null) {
            currentImageUri = Util.getImageFileUri(codigo);
        }
        fotoRuteo.setVisibility(View.GONE);
        initListeners();
        getSupportActionBar().hide();

        /*if (!isGPSEnabled(MyApp.getAppContext())) {
            ((TextView) findViewById(R.id.searchingGPS)).setText("");
        } else {
            GPSService.getInstance().setParametros(UPDATE_INTERVAL, FASTEST_INTERVAL, DISPLACEMENT, TIMEOUT);
            GPSService.getInstance().iniciarBusquedaGPS2(MyApp.getAppContext());
        }*/
    }

    /*public boolean isGPSEnabled(Context mContext) {
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }*/


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ruteo, menu);
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

    private void initListeners() {
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentImageUri);
                lastImageId = Util.getLastImageId();
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        btnGuardarRuteo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarRuteo();

                /*if (((TextView) findViewById(R.id.searchingGPS)).getText().toString().trim().contains("Buscando")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RuteoActivity.this);
                    builder.setTitle("Aviso");
                    builder.setMessage("GPS no encontrado")
                            .setCancelable(false)
                            .setPositiveButton("Esperar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            })
                            .setNeutralButton("Guardar\nsin GPS", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    guardarRuteo();
                                }
                            })
                            .setNegativeButton("Salir\nsin guardar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    guardarRuteo();
                }*/
            }
        });

        btnCancelarRuteo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        fotoRuteo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RuteoActivity.this);
                builder.setMessage("¿Borrar foto?")
                        .setCancelable(true)
                        .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                fotoRuteo.setTag("");
                                fotoRuteo.setImageBitmap(null);
                                fotoRuteo.setVisibility(View.GONE);
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                return false;
            }
        });
    }

    public void guardarRuteo() {
        Ruteo ruteo = new Ruteo();
        Producto producto = new Producto();
        producto.getProductoByEAN(codigo);

        ruteo.setCodProducto(producto.getCodProducto());
        ruteo.setObservacion(txtObservacionRuteo.getText().toString().trim());
        ruteo.setFechaCreacion(Data.FECHA_DATABASE_FORMAT.format(new Date()));
        ruteo.setFechaRegistroFoto("");
        //Log.w("Relevamiento posicion: ",latLocation + ";" + longLocation);
        ruteo.setCoordenadas("0;0");
        //double d = Double.parseDouble("0.0");
        ruteo.setRango(0);

//                if (mBound) {
//                    if (gpsTracker.canGetLocation()) {
//                        double latitude = gpsTracker.getLatitude();
//                        double longitude = gpsTracker.getLongitude();
//                        double rango = gpsTracker.getAccuracy();
//                        ruteo.setCoordenadas(latitude + ";" + longitude);
//                        ruteo.setRango((int) rango);
//                    } else {
//
//                    }
//                }


        try {
            ruteo.setPrecio(Double.parseDouble(txtPrecioRuteo.getText().toString().trim()));
        } catch (Exception pe) {

        }
        try {
            String path = fotoRuteo.getTag().toString();
            if (!path.trim().equals("")) {
                String[] patharray = path.split(File.separator);
                String name = "";
                if (patharray.length > 0) {
                    name = patharray[patharray.length - 1];
                }
                ruteo.setImagen(name);
            }
        } catch (Exception w) {

        }
        ruteo.saveData();

        AlertDialog.Builder builder = new AlertDialog.Builder(RuteoActivity.this);
        builder.setMessage("Guardado correcto")
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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        File root = new File(Environment.getExternalStorageDirectory(), Data.DIRECTORIO_FOTOS);
//        if (!root.exists()) {
//            root.mkdirs();
//        }
        try {
            if (requestCode == CAMERA_REQUEST) {
                if (resultCode == Activity.RESULT_OK) {
                    fotoRuteo.setImageBitmap(null);
                    Bitmap bm = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(currentImageUri.getPath()), 384, 384);
                    fotoRuteo.setImageBitmap(bm);
                    fotoRuteo.setTag(currentImageUri.getPath().toString());
                    fotoRuteo.setVisibility(View.VISIBLE);
                    int actualLastImageId = Util.getLastImageId();
                    if (actualLastImageId > lastImageId) {
                        Util.removeImage(actualLastImageId);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showOnBackPressedMessage() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                RuteoActivity.this);
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
    //boolean mBound = false;

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    /*private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            Log.i("local binder", "at to local binder");
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
    @Override
    public void onResume() {
        super.onResume();
        /*IntentFilter filter = new IntentFilter(GPS_MANAGER_BROADCAST_RECEIVER);
        this.registerReceiver(mGPSManagerBroadcastReceiver, filter);

        IntentFilter filter2 = new IntentFilter(GPS_MANAGER_TIMEOUT_BROADCAST_RECEIVER);
        this.registerReceiver(mGPSManagerTimeoutBroadcastReceiver, filter2);*/
    }

    @Override
    public void onPause() {
        super.onPause();
        //this.unregisterReceiver(mGPSManagerBroadcastReceiver);
    }
}
