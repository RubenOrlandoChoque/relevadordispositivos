package com.sistematias.relevadordispositivos.clases;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by Sebastian on 08/06/2015.
 */
public class Repository extends SQLiteOpenHelper {

    private static final int VERSION = 10;
    private static final String DATABASE_NAME = "relevamientodispositivos.sqlite";
    private static File DATABASE_FILE;

    // This is an indicator if we need to copy the
    // database file.
    private boolean mInvalidDatabaseFile = false;
    private boolean mIsUpgraded = false;
    private Context mContext;

    /**
     * number of users of the database connection.
     * */
    private int mOpenConnections = 1;

    private static Repository mInstance;

    synchronized static public Repository getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Repository(context);
        }
        return mInstance;
    }

    public Repository(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.mContext = context;
        SQLiteDatabase db = null;
        try {
            db = getReadableDatabase();
            if (db != null) {
                db.close();
            }
            DATABASE_FILE = context.getDatabasePath(DATABASE_NAME);
            Log.i("database path",context.getDatabasePath(DATABASE_NAME).getPath());
            if (mInvalidDatabaseFile) {
                copyDatabase();
            }
            if (mIsUpgraded) {
                copyDatabase();
                //doUpgrade();
            }

        } catch (SQLiteException e) {
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public Cursor executeSelect(String _consulta,Cursor cursor) {
        SQLiteDatabase db = this.getReadableDatabase();
        if(cursor!=null){
            cursor.close();
        }
        cursor = db.rawQuery(_consulta, null);
        return  cursor;
    }

    public void executeSelect2(String _consulta,Cursor cursor) {
        SQLiteDatabase db = this.getReadableDatabase();
        if(cursor!=null){
            cursor.close();
        }
        cursor = db.rawQuery(_consulta, null);
    }


    public String getStringSelect(String _consulta) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(_consulta, null);
        if(cursor.moveToNext()){
            String cad = cursor.getString(0);
            cursor.close();
            return  cad;
        }else{
            return "";
        }

    }

    public int getIntSelect(String _consulta) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(_consulta, null);
        if(cursor.moveToNext()){
            int res = cursor.getInt(0);
            cursor.close();
            return res;
        }else{
            return 0;
        }
    }

    public int getIntifExists(String _consulta) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(_consulta, null);
        if(cursor.moveToNext()){
            int res = cursor.getInt(0);
            cursor.close();
            return res;
        }else{
            return -1;
        }
    }

    public long insert(String tabla,ContentValues values){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.insert(tabla, null, values);
    }

    public long update(String tabla,ContentValues values, String whereClause, String[] whereArgs){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.update(tabla, values, whereClause, whereArgs);
    }

    public void delete(String consulta){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(consulta);
    }

    public void executeSQLQuery(String consulta){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(consulta);
    }

    public int getDataBaseVersion(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.getVersion();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        mInvalidDatabaseFile = true;
        Log.w("SQL","se esta creando por primeravez la base de datos");
    }

    @Override
    public void onUpgrade(SQLiteDatabase database,int old_version, int new_version) {
        Log.w("SQL", "Actualizando la Base de Datos desde la version " + old_version + " a " + new_version + ",");
        mIsUpgraded = true;
    }

    /**
     * called if a database upgrade is needed
     */
    private void doUpgrade() {
        // implement the database upgrade here.
        Log.w("ACTUALIZANDO","se actualizara la base de datos");
        updateDatabase();
    }

    @Override
    public synchronized void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        // increment the number of users of the database connection.
        mOpenConnections++;
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }

    /**
     * implementation to avoid closing the database connection while it is in
     * use by others.
     */
    @Override
    public synchronized void close() {
        mOpenConnections--;
        if (mOpenConnections == 0) {
            super.close();
        }
    }

    private void copyDatabase() {
        Log.w("CREANDO","Se esta creando por primera vez la base de datos");
        AssetManager assetManager = mContext.getResources().getAssets();
        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(DATABASE_NAME);
            //in = new FileInputStream(DATABASE_NAME);
            out = new FileOutputStream(DATABASE_FILE);
            //out = new FileOutputStream(Environment.getExternalStorageDirectory().toString() + File.separator + "bd" + File.separator + DATABASE_NAME);
            byte[] buffer = new byte[1024];
            int read = 0;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {}
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {}
            }
        }
        setDatabaseVersion();
        mInvalidDatabaseFile = false;
    }

    private void setDatabaseVersion() {
        SQLiteDatabase db = null;
        try {
            db = SQLiteDatabase.openDatabase(DATABASE_FILE.getAbsolutePath(), null,
                    SQLiteDatabase.OPEN_READWRITE);
            db.execSQL("PRAGMA user_version = " + VERSION);
        } catch (SQLiteException e ) {
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {

            File file = new File(DATABASE_FILE.getAbsolutePath());
            if(file.exists()){
                //Do somehting
                Log.i("d","database exist yet.");
            return true;}
            else{
            Log.i("d","database doesn't exist yet.");
                return false;}
                // Do something else.
//            checkDB = SQLiteDatabase.openDatabase(DATABASE_FILE.getAbsolutePath(), null,
//                    SQLiteDatabase.OPEN_READONLY);
//            checkDB.close();
        } catch (SQLiteException e) {
            // database doesn't exist yet.
            Log.i("d","database doesn't exist yet.");
        }
        return false;
    }

    public void copyScript(){
        try
        {
            AssetManager am = mContext.getAssets();
            InputStream in = am.open("actualizaciones/680.txt");
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line);
            }
            Log.i("datos",total.toString());
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }


    public void updateDatabase(){
        //copyDatabaseToSD();
        /*creamos la tabla updates  si aun no esta cread*/
        executeSQLQuery("create table if not exists updates(id integer PRIMARY KEY,version integer,ejecutado integer)\n");
        try {
            AssetManager am = mContext.getAssets();
            String[] list = am.list("actualizaciones");
            File[] files = new  File[list.length];
            if (list != null){
                for (int i = 0; i < list.length; ++i) {
                    File file = new File(list[i]);
                    files[i]=file;
                }
            }

            /*ordenar por fecha*/
//            if (files != null && files.length > 1) {
//                Arrays.sort(files, new Comparator<File>() {
//                    @Override
//                    public int compare(File object1, File object2) {
//                        return (int) ((object1.lastModified() > object2.lastModified()) ? object1.lastModified() : object2.lastModified());
//                    }
//                });
//            }

            if(files!=null){
                for (int j=0;j<files.length;j++){
                    File file = files[j];
                    if (file.isDirectory()) {
                        Log.d("Assets:", file.getPath() + " is Directory");
                    } else {

                        Log.d("Assets:", file.getPath() + " is File");
                        Log.w("archivo",file.getPath());
                        //InputStream in = am.open("actualizaciones/680.txt");
                        String fileName = file.getPath();
                        int version = Integer.parseInt(fileName.substring(0,fileName.lastIndexOf(".")));
                        int versionDB = getDataBaseVersion();

                        Log.i("version",Integer.toString(version));
                        Log.i("versionDB",Integer.toString(versionDB));

                        //if(version>versionDB){//es una actualizacion
                        if(true){
                            //Log.w("update","la version es mayor que la de la bd");
                            int ejecutado = ejecutado(version);
                            if(ejecutado==-1 || ejecutado==0){//
                                Log.w("update","se actualizara la base de datos");
                                InputStream in = am.open("actualizaciones/"+fileName);

                                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                                StringBuilder total = new StringBuilder();
                                String line;
                                while ((line = r.readLine()) != null) {
                                    total.append(" "+line+" ");
                                }

                                String[] script = total.toString().split(";");
                                for(int i=0;i<script.length;i++){
                                    if(script[i].trim().length()>0){
                                        Log.i("query","ejecuntado el siguiente script: "+script[i]);
                                        try{
                                            executeSQLQuery(script[i]);
                                            Log.w("exito","ejecucion de script");
                                        }catch(Exception ex){
                                            Log.e("fracaso","hubo un problema al ejecutar la actualizacion de la base de datos");
                                        }
                                    }
                                }

                                if(ejecutado==-1){
                                    executeSQLQuery("insert into updates(version,ejecutado) values("+version+",1)");
                                }else{
                                    executeSQLQuery("update updates set ejecutado=0 where version="+version+"\n");
                                }


                            }else{
                                Log.w("update","esta en base de datos y esta actualizado");
                            }
                        }else {
                            //Log.w("update","no es necesario actualizar");
                        }
                    }
                }

                /*actualizar estado de la actualizacion*/
                Cursor cursor=null;
                cursor = executeSelect("select * from updates",cursor);
                while (cursor.moveToNext()){
                    Log.i("version",cursor.getInt(1)+"");
                    Log.i("ejecutado",cursor.getInt(2)+"");
                }
            }
            setDatabaseVersion();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

    }

    public int ejecutado(int version){
        int ejecutado = getIntifExists("select ejecutado from updates where version="+version);
        return ejecutado;
    }

    public void copyDatabaseToSD(){
        try {
            String PACKAGE_NAME = mContext.getApplicationContext().getPackageName();
            Log.i("PACKAGE_NAME",PACKAGE_NAME);
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (sd.canWrite()) {
                String currentDBPath = "/data/"+PACKAGE_NAME+"/databases/"+DATABASE_NAME;
                String backupDBPath = "backTECHO.sqlite";
                File currentDB = new File(data, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
            }

        } catch (Exception e) {

        }
    }


    public static String getDataBaseName (){
        return DATABASE_NAME;
    }
}
