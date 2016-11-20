package com.sistematias.relevadordispositivos.clases;

//import com.infotec.androidapi.library.DatabaseHandler;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectionChecker {

    private static final String TAG = "ConnectionChecker -->";
    Context mContext;
    private static int timeout = 5 * 1000;
    private int retraso;
    private Boolean isWorking;
    private AsyncTask<String, Integer, Boolean> mAsyncTask;
    private static ConnectionChecker mInstance;
    private static ConnectionCheckerTask mConnectionCheckerTask;

    protected ConnectionChecker() {

    }

    static {
        mInstance = new ConnectionChecker();
    }

    public static ConnectionChecker getInstance() {
        return ConnectionChecker.mInstance;
    }

    /*
    public ConnectionChecker(Context context, AsyncTask<String, Void, Void> asyncTask) {
        mContext = context;
        mAsyncTask = asyncTask;
        isWorking = false;
    }
    */
    public void setAsyncTask(AsyncTask<String, Integer, Boolean> asyncTask) {
        /*mAsyncTask.cancel(true);
        mAsyncTask = null;*/
        mAsyncTask = asyncTask;
    }

    public AsyncTask<String, Integer, Boolean> getAsyncTask() {
        return mAsyncTask;
    }

    public void setIsWorking(Boolean isWorking) {
        this.isWorking = isWorking;
    }

    public Boolean getIsWorking() {
        return isWorking;
    }

    public int getRetraso() {
        return retraso;
    }

    public void setRetraso(int retraso) {
        this.retraso = retraso;
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        NetworkInfo mWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        //if (activeNetwork != null && activeNetwork.isConnected()) {
        if ((mWifi != null && mWifi.isConnected())) { //solo envia si esta conectado por wifi
            try {
                URL url = new URL("http://www.google.com/");
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setRequestProperty("User-Agent", "test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(timeout); // mTimeout is in seconds
                urlc.connect();
                if (urlc.getResponseCode() == 200) {
                    return true;
                } else {
                    return false;
                }
            } catch (IOException e) {
                Log.i("warning", "Error checking internet connection", e);
                return false;
            }
        }else{
            if (activeNetwork != null && activeNetwork.isConnected()) {
                try {
                    URL url = new URL("http://www.google.com/");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setRequestProperty("User-Agent", "test");
                    urlc.setRequestProperty("Connection", "close");
                    urlc.setConnectTimeout(timeout); // mTimeout is in seconds
                    urlc.connect();
                    if (urlc.getResponseCode() == 200) {
                        return true;
                    } else {
                        return false;
                    }
                } catch (IOException e) {
                    Log.i("warning", "Error checking internet connection", e);
                    return false;
                }
            }
        }
        return false;

    }

    public void startConnectionCheck(Context context, AsyncTask<String, Integer, Boolean> asyncTask) {
        Log.i(TAG, "ENTRA A START CONNECTION CHECK. DENTRO DE " + String.valueOf(getRetraso()) + " MILISEGUNDOS SE RALIZARA LA PROXIMA COMPROBACIoN.");
        mContext = context;
        mAsyncTask = asyncTask;
        Log.i("objeto", mAsyncTask.toString());
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                if (mConnectionCheckerTask != null) {
                    Log.i("co", mConnectionCheckerTask.toString());
                    try {
                        mConnectionCheckerTask.cancel(true);
                        mConnectionCheckerTask = null;
                    } catch (Exception e) {
                        Log.e("error", e.getMessage());
                    }
                }else{
                    Log.e("error","mConnectionChecker IsNull");
                }
                Log.i("nueva tarea", "SE EJECUTARA UNA NUEVA TARES CONNECTION CHECKER");
                mConnectionCheckerTask = new ConnectionCheckerTask();
                //mConnectionCheckerTask.execute();
                mConnectionCheckerTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }, getRetraso());
    }

    public static void cancelConnectionCheckerTask() {
        try {
            mConnectionCheckerTask.cancel(true);
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }
    }

    private class ConnectionCheckerTask extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            return isInternetAvailable(mContext); // positive integer on success
        }

        @TargetApi(11)
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            try {
                if (result) {
                                    Log.i(TAG, "La conexion a internet esta disponible.");
                    //Toast.makeText(mContext, "La conexi0n a internet esta disponible.", Toast.LENGTH_SHORT).show();
                    if (getIsWorking()) {
                                            Log.i(TAG, "La conexion a internet esta disponible pero actualmente se esta TRABAJANDO.");
                        //Toast.makeText(mContext, "La conexion a internet esta disponible pero actualmente se esta TRABAJANDO.", Toast.LENGTH_SHORT).show();
                    } else {
                                            Log.i(TAG, "La conexion a internet esta disponible y NO SE ESTA TRABAJANDO.");
                        //Toast.makeText(mContext, "La conexion a internet esta disponible y NO SE ESTA TRABAJANDO.", Toast.LENGTH_SHORT).show();
                        setIsWorking(true);
                        mAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        /*if (Build.VERSION.SDK_INT >= 11) {
                            mAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                            mAsyncTask.execute();
                        }*/
                        //mAsyncTask.execute();
                    }
                } else {
                    //                Log.i(TAG, "La conexi0n a internet NO esta disponible.");
                    //Toast.makeText(mContext, "La conexi0n a internet NO esta disponible.", Toast.LENGTH_SHORT).show();
                }
                startConnectionCheck(mContext, mAsyncTask);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {
            Log.i("cancel", "LA TEREA CONNECTION CHECKER HA SIDO CANCELADA");
        }
    }
}
