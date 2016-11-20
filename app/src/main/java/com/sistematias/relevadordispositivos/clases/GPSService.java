package com.sistematias.relevadordispositivos.clases;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sistematias.relevadordispositivos.activity.MyApp;

import org.apache.http.Header;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

//import android.location.LocationListener;

/*
* PARA USAR ESTA CLASE ES NECESARIO PROPORCIONAR LOS SIGUIETES PERMISOS EN SU ARCHIVO MANIFEST:
*   <uses-permission android:name="android.permission.RECORD_AUDIO" />
*   <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
* */
public class GPSService extends Service implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener {


    public static final String TAG = "GPSService >>>";
    private static final String GPS_MANAGER_BROADCAST_RECEIVER = "GPS_MANAGER_BROADCAST_RECEIVER";
    private static final String GPS_MANAGER_TIMEOUT_BROADCAST_RECEIVER = "GPS_MANAGER_TIMEOUT_BROADCAST_RECEIVER";
    Context mContext;
    private static GPSService instance;
    ArrayList<Location> mLocations;

    private RootUtil _root;

    private static final String startGPSCMD = "startGPS";
    private static final String getBestLocationGPSCMD = "getBestLocationGPS";
    private static final String stopGPSCMD = "stopGPS";
    private static final String startSyncCMD = "startSync";

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1357;
    private Location mLastLocation;
    // Google client to interact with Google API
    private static GoogleApiClient mGoogleApiClient;
    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private int UPDATE_INTERVAL = 10*1000; // Intervalo de actualizacion. Por defecto: 10 segundos.
    private int FASTEST_INTERVAL = 1*1000; // Intervalo de actualizacion rapido. Por defecto: 5 segundos.
    private int FASTEST_INTERVAL_2 = (1*1000) - 800;
    private int DISPLACEMENT = 20; // Distancia en metros. Por defecto: 10 metros.
    private int TIMEOUT = 5*60*1000; // Tiempo máximo de búsqueda. Por defecto: 5 minutos.

    long mTiempoFinalizacion;

    static TimerGPS tGPS;

    private NotificationManager mNotificationManager;
    public int CS_NOTIFICATION_ID;

    public AsyncHttpClient mAsyncHttpClient;
    private String locationPostURL = "http://127.0.0.1:8080/estructura/php/ejecutarAndroid.php";

    private LocationManager locationManager;
    private String provider;
    private MyLocationListener mylistener;
    private Criteria criteria;

    public GPSService() {
        mAsyncHttpClient = new AsyncHttpClient();
    }

    static {
        instance = new GPSService();
    }

    public static GPSService getInstance() {
        return GPSService.instance;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        //Toast.makeText(this, "The new Service was Created", Toast.LENGTH_LONG).show();
        Log.i(TAG, "The new Service was Created.");
    }
    /*
    @Override
    public void onStart(Intent intent, int startId) {
        Log.i(TAG, "Service onStartCommand");
        String cmd = intent.getStringExtra("cmd");
        Log.i(TAG, "Parámetro enviado desde HTML/PHP: " + cmd);
    }
    */

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Service onStartCommand");
        String cmd = intent.getStringExtra("cmd");
        Log.i(TAG, "Parámetro enviado desde HTML/PHP: " + cmd);
        //Toast.makeText(this, "Parámetro enviado desde HTML/PHP: " + cmd, Toast.LENGTH_LONG).show();

        // Comando para INICIAR la búsqueda de ubicaciones desde la consola de comandos ADB:
        // am startservice -n com.mpi.android.commonservices/.GPSService --es cmd startGPS --es updateInterval 10000 --es fastestInterval 5000 --es distance 10 --es timeout 300000
        if (cmd.equals(startGPSCMD)) {
            int _updateIterval = Integer.valueOf(intent.getStringExtra("updateInterval"));
            int _fastestIterval = Integer.valueOf(intent.getStringExtra("fastestInterval"));
            int _distance = Integer.valueOf(intent.getStringExtra("distance"));
            int _timeout = Integer.valueOf(intent.getStringExtra("timeout"));
            Log.i(TAG, "El comando a ejecutar es: " + cmd);
            //Toast.makeText(this, "El comando a ejecutar es: " + cmd, Toast.LENGTH_LONG).show();
            GPSService.getInstance().setParametros(_updateIterval, _fastestIterval, _distance, _timeout);
            GPSService.getInstance().iniciarBusquedaGPS(MyApp.getAppContext());
        }

        // Comando para DETENER la grabación desde la consola de comandos ADB:
        // am startservice -n com.mpi.android.commonservices/.GPSService --es cmd getBestLocationGPS
        if (cmd.equals(getBestLocationGPSCMD)) {
            Log.i(TAG, "El comando a ejecutar es: " + cmd);
            //Toast.makeText(this, "El comando a ejecutar es: " + cmd, Toast.LENGTH_LONG).show();

        }

        // Comando para DETENER la grabación desde la consola de comandos ADB:
        // am startservice -n com.mpi.android.commonservices/.GPSService --es cmd stopGPS
        if (cmd.equals(stopGPSCMD)) {
            Log.i(TAG, "El comando a ejecutar es: " + cmd);
            //Toast.makeText(this, "El comando a ejecutar es: " + cmd, Toast.LENGTH_LONG).show();
            GPSService.getInstance().detenerBusquedaGPS();
        }

        return Service.START_STICKY;
    }


    @Override
    public void onDestroy() {
        //Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
        Log.i(TAG, "Stop service");
    }

    /**
     * Method to toggle periodic location updates
     * */
    private void togglePeriodicLocationUpdates() {
        if (!mRequestingLocationUpdates) {

            mRequestingLocationUpdates = true;

            // Starting the location updates
            startLocationUpdates();

            Log.d(TAG, "Periodic location updates started!");

        } else {

            mRequestingLocationUpdates = false;

            // Stopping the location updates
            stopLocationUpdates();

            Log.d(TAG, "Periodic location updates stopped!");
        }
    }

    /**
     * Creating google api client object
     * */
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Creating location request object
     * */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    /**
     * Method to verify google play services on the device
     * */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //Toast.makeText(mContext, "Los servicios de Google Play no están instalados o no están configurados correctamente.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Los servicios de Google Play no están instalados o no están configurados correctamente.");
            } else {
                //Toast.makeText(mContext, "Este dispositivo no está soportado.", Toast.LENGTH_LONG).show();
                Log.e(TAG, "Este dispositivo no está soportado.");
            }
            return false;
        }
        return true;
    }

    /**
     * Starting the location updates
     * */
    protected void startLocationUpdates() {

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates() {
        try {
            if (mGoogleApiClient != null) {
                LocationServices.FusedLocationApi.removeLocationUpdates(
                        mGoogleApiClient, this);
            } else {
                Log.e(TAG, "La instancia GoogleApiClient es NULA");
            }
        } catch (Exception e) {
            Log.e(TAG, "Se produjo un error al detener el servicio de actualización de ubicaciones. Detalle: " + e.getMessage());
        }


    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0) {

        // Once connected with google api, get the location
        saveLocationFound();

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }

        tGPS = new TimerGPS();
        tGPS.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    @Override
    public void onConnectionSuspended(int arg0) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        // Assign the new location
        mLastLocation = location;

        //Toast.makeText(mContext, "Nueva ubicación encontrada. Lat: " + location.getLatitude() + ", Long: " + location.getLongitude() + ", Distancia: " + location.getAccuracy(), Toast.LENGTH_LONG).show();

        Log.i(TAG, "Nueva ubicación encontrada. Lat: " + location.getLatitude() + ", Long: " + location.getLongitude() + ", Distancia: " + location.getAccuracy());

        // Displaying the new location on UI
        //saveLocationFound();
        GPSService.getInstance().guardarLocalizacionEncontrada(location);

        Location mBestLocation = GPSService.getInstance().getBestLocation();

        GPSService.getInstance().sendBroadcast(mContext, String.valueOf(mBestLocation.getLatitude()), String.valueOf(mBestLocation.getLongitude()), String.valueOf(mBestLocation.getAccuracy()));

        //GPSService.getInstance().informarNuevaUbicacion(locationPostURL, String.valueOf(mBestLocation.getLatitude()), String.valueOf(mBestLocation.getLongitude()), String.valueOf(mBestLocation.getAccuracy()));

        if (location.getAccuracy() <= DISPLACEMENT || System.currentTimeMillis() > mTiempoFinalizacion) {
            Log.i(TAG, "DETENIENDO BUSQUEDA PORQUUE ENCONTRO UN PUNTO SATISFACTORIO O PORQUE SE TERMINO EL TIEMPO DE BUSCQUEDA.");
            //GPSService.getInstance().detenerBusquedaGPS();
        }

    }

    public void informarNuevaUbicacion(String _postURL, String _latitude, String _longitude, String _distance) {

        RequestParams params = new RequestParams();
        params.put("cmd", "guardarUbicacion");
        params.put("latitude", _latitude);
        params.put("longitude", _longitude);
        params.put("distance", _distance);

        mAsyncHttpClient.post(_postURL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String serverResponseStr = "Respuesta por defecto.";
                try {
                    serverResponseStr = new String(bytes, "UTF-8");
                    Log.i(TAG, "Success Post. Response: " + serverResponseStr);
                    //Toast.makeText(mContext, "Success Post. Response: " + serverResponseStr, Toast.LENGTH_LONG).show();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.i(TAG, "Error: Ocurrio un error en el proceso POST. StackTace: " + throwable.getStackTrace() + ", Message: " + throwable.getMessage());
                //Toast.makeText(mContext, "Error: Ocurrio un error en el proceso POST. StackTace: " + throwable.getStackTrace() + ", Message: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                //sendBroadcast(mContext, _codOperation, toUploadFile.getAbsolutePath(), cod_ErrorLocal, "Error al subir archivo: " + throwable.getMessage());
            }
        });


    }

    /**
     * Method to display the location on UI
     * */
    private void saveLocationFound() {

        mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            mLocations.add(mLastLocation);
        } else {
            Log.e(TAG, "No se pudo obtener ninguna ubicación. Asegurese que el dispositovo está activado.");
        }
    }

    private void guardarLocalizacionEncontrada(Location location) {

        if (location != null) {
            mLocations.add(location);
        } else {
            Log.e(TAG, "No se pudo obtener ninguna ubicación. Asegurese que el dispositovo está activado.");
        }
    }

    public void  setParametros(int _updateInterval,int  _fastestInterval,int _distance, int _timeout){
        UPDATE_INTERVAL = _updateInterval;
        FASTEST_INTERVAL = _fastestInterval;
        DISPLACEMENT = _distance;
        TIMEOUT = _timeout;
    }

    public void iniciarBusquedaGPS(Context context) {

        prenderGPS(context);
        mContext = context;
        Log.i(TAG, "Iniciando busqueda");
        mLocations = new ArrayList<Location>();
        mLocations.clear();
        mTiempoFinalizacion = System.currentTimeMillis() + TIMEOUT;

        if (checkPlayServices()) {
            // Building the GoogleApi client
            buildGoogleApiClient();
            createLocationRequest();
        }

        if (mGoogleApiClient != null) {
            mRequestingLocationUpdates = true;
            mGoogleApiClient.connect();
        }

        //startLocationUpdates();

    }

    private class MyLocationListener implements android.location.LocationListener {

        @Override
        public void onLocationChanged(Location location) {

            Log.i(TAG, "MyLocationListener >>> nuevo evento onLocationChange.");

            mLastLocation = location;

            if (mLastLocation != null) {
                Log.i(TAG, "Nueva ubicación encontrada. Lat: " + mLastLocation.getLatitude() + ", Long: " + mLastLocation.getLongitude() + ", Distancia: " + mLastLocation.getAccuracy());

                // Displaying the new location on UI
                //saveLocationFound();
                GPSService.getInstance().guardarLocalizacionEncontrada(mLastLocation);

                Location mBestLocation = GPSService.getInstance().getBestLocation();

                GPSService.getInstance().sendBroadcast(mContext, String.valueOf(mBestLocation.getLatitude()), String.valueOf(mBestLocation.getLongitude()), String.valueOf(mBestLocation.getAccuracy()));

                //GPSService.getInstance().informarNuevaUbicacion(locationPostURL, String.valueOf(mBestLocation.getLatitude()), String.valueOf(mBestLocation.getLongitude()), String.valueOf(mBestLocation.getAccuracy()));

                if (mLastLocation.getAccuracy() <= DISPLACEMENT || System.currentTimeMillis() > mTiempoFinalizacion) {
                    Log.i(TAG, "DETENIENDO BUSQUEDA PORQUUE ENCONTRO UN PUNTO SATISFACTORIO O PORQUE SE TERMINO EL TIEMPO DE BUSCQUEDA.");
                    //GPSService.getInstance().detenerBusquedaGPS();
                }
            }

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            //Toast.makeText(GPSService.this, provider + "'s status changed to "+status +"!", Toast.LENGTH_SHORT).show();
            Log.i(TAG, provider + "'s status changed to "+status +"!");
        }

        @Override
        public void onProviderEnabled(String provider) {
            //Toast.makeText(GPSService.this, "Provider " + provider + " enabled!", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Provider " + provider + " enabled!");
        }

        @Override
        public void onProviderDisabled(String provider) {
            //Toast.makeText(GPSService.this, "Provider " + provider + " disabled!", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Provider " + provider + " disabled!");
        }
    }

    public void iniciarBusquedaGPS2(Context context) {

        prenderGPS(context);
        mContext = context;
        Log.i(TAG, "Iniciando busqueda");
        mLocations = new ArrayList<Location>();
        mLocations.clear();
        mTiempoFinalizacion = System.currentTimeMillis() + TIMEOUT;

        // Get the location manager
        locationManager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);

        // Define the criteria how to select the location provider
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);	//default
        criteria.setCostAllowed(false);

        // get the best provider depending on the criteria
        provider = locationManager.getBestProvider(criteria, false);

        // the last known location of this provider
        Location location = locationManager.getLastKnownLocation(provider);
        //mLocations.add(location);

        mylistener = new MyLocationListener();

        if (location != null) {
            mylistener.onLocationChanged(null);
        } else {
            // leads to the settings because there is no last known location
//            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            mContext.startActivity(intent);
            mylistener.onLocationChanged(null);
        }
        // location updates: at least 1 meter and 200millsecs change
        locationManager.requestLocationUpdates(provider, FASTEST_INTERVAL_2, DISPLACEMENT, mylistener);

        tGPS = new TimerGPS();
        tGPS.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }

    public void detenerBusquedaGPS() {
        Log.i(TAG, "La búsqueda de puntos GPS se ha detenido.");
        try {
            GPSService.getInstance().tGPS.cancel(true);
            GPSService.getInstance().tGPS = null;
            GPSService.getInstance().stopLocationUpdates();
            GPSService.getInstance().closeGPS();
        } catch (RuntimeException er) {
            Log.e(TAG, "Error al intentar detener la búsqueda GPS. Detalle: " + er.getMessage());
        }
    }

    public void detenerBusquedaGPS2() {
        Log.i(TAG, "La búsqueda de puntos GPS se ha detenido.");
        try {

            mLastLocation = locationManager.getLastKnownLocation(provider);;

            //Toast.makeText(mContext, "Nueva ubicación encontrada. Lat: " + location.getLatitude() + ", Long: " + location.getLongitude() + ", Distancia: " + location.getAccuracy(), Toast.LENGTH_LONG).show();

            Log.i(TAG, "Nueva ubicación encontrada. Lat: " + mLastLocation.getLatitude() + ", Long: " + mLastLocation.getLongitude() + ", Distancia: " + mLastLocation.getAccuracy());

            // Displaying the new location on UI
            //saveLocationFound();

            if (mLastLocation != null) {
                GPSService.getInstance().guardarLocalizacionEncontrada(mLastLocation);

                Location mBestLocation = GPSService.getInstance().getBestLocation();

                GPSService.getInstance().sendBroadcast(mContext, String.valueOf(mBestLocation.getLatitude()), String.valueOf(mBestLocation.getLongitude()), String.valueOf(mBestLocation.getAccuracy()));
            }

            GPSService.getInstance().locationManager.removeUpdates(mylistener);
            GPSService.getInstance().closeGPS();
            GPSService.getInstance().tGPS.cancel(true);
            Thread.currentThread().interrupt();
            GPSService.getInstance().tGPS = null;
            sendBroadcastTimeout(mContext);
        } catch (RuntimeException er) {
            Log.e(TAG, "Error al intentar detener la búsqueda GPS 2. Detalle: " + er.getMessage());
        }
    }

    static void sendBroadcast(Context context, String latLocation, String longLocation, String distanceLocation) {
        Intent intent = new Intent(GPS_MANAGER_BROADCAST_RECEIVER);
        intent.putExtra("latLocation", latLocation);
        intent.putExtra("longLocation", longLocation);
        intent.putExtra("distanceLocation", distanceLocation);
        context.sendBroadcast(intent);
    }

    static void sendBroadcastTimeout(Context context) {
        Intent intent = new Intent(GPS_MANAGER_TIMEOUT_BROADCAST_RECEIVER);
        context.sendBroadcast(intent);
    }

    public Location getBestLocation() {
        //detenerBusquedaGPS();
        Location bestLocation = getBestLocation(mLocations);
        return bestLocation;
    }

    public void prenderGPS(Context _context){
        try {
            Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
            intent.putExtra("enabled", true);
            _context.sendBroadcast(intent);

            String provider = Settings.Secure.getString(_context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            Log.i(TAG, provider);
            if(!provider.contains("gps")){ //if gps is disabled
                final Intent poke = new Intent();
                poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
                poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
                poke.setData(Uri.parse("3"));
                _context.sendBroadcast(poke);
                Log.i(TAG, "iniciando");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error en función prenderGPS: " + e.getMessage());
        }
    }

    private void closeGPS(){
        _root = new RootUtil();
        if(_root.isDeviceRooted()){
            GPSService.getInstance().apagarGPS(mContext);
        }
    }

    public class TimerGPS extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            try {

                int sleepInt = 5*1000;
                while (System.currentTimeMillis() < mTiempoFinalizacion) {

                    double timpoRestante = (mTiempoFinalizacion - System.currentTimeMillis())/1000;
                    Log.i(TAG, "BUSCANDO POSICIONES GPS. TIEMPO RESTANTE: " + String.valueOf(timpoRestante) + " segundos.");
                    Thread.sleep(sleepInt);

                }

                return true;
            } catch(Exception e){
                return false;
                // some error handling if SoundManager.addSound throws exceptions?
            }

        }

        @Override
        protected void onPostExecute(Boolean result){
            super.onPostExecute(result);
            Log.i(TAG, "DETENIENDO BUSQUEDA PORQUUE SE TERMINO EL TIEMPO (FINALIZO TIMER).");
            GPSService.getInstance().detenerBusquedaGPS2();
        }
    }

    public void apagarGPS(Context _context){
        try {
            Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
            intent.putExtra("enabled", false);
            _context.sendBroadcast(intent);
        } catch (RuntimeException re) {
            Log.e(TAG, "Error al intentar apagar GPS. Detalle: " + re.getMessage());
        }
    }

    public ArrayList<Location> getAllLocationsFound() {
        return mLocations;
    }

    private Location getBestLocation(ArrayList<Location> locations) {

        if (locations == null) {
            return null;
        }

        if (locations.size() < 1) {
            return null;
        }

        Log.i(TAG, "OBTENIENDO MEJOR POSICION ENCONTRADA HASTA EL MOMENTO DE " + String.valueOf(locations.size()) + " LOCALIZACIONES ENCONTRADAS.");

        Location minLocation = locations.get(0);
        Log.i(TAG, "LATITUDE: " + minLocation.getLatitude());
        Log.i(TAG, "LONGITUDE: " + minLocation.getLongitude());
        Log.i(TAG, "ACCURACY: " + minLocation.getAccuracy());
        float minAccuracy = minLocation.getAccuracy();
        for (int i = 0; i < locations.size(); i++) {
            if (locations.get(i).getAccuracy() < minAccuracy) {
                minLocation = locations.get(i);
                minAccuracy = minLocation.getAccuracy();
            }
        }

        return minLocation;

    }

    /*
    private void sendNotification(String title, String msg) {

        CS_NOTIFICATION_ID = 1571;

        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, null, 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mContext)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setAutoCancel(true)
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(alarmSound);

        mNotificationManager.notify(CS_NOTIFICATION_ID, mBuilder.build());
    }
    */
}
