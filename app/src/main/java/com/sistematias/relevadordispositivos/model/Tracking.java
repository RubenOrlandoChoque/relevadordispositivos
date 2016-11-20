package com.sistematias.relevadordispositivos.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.sistematias.relevadordispositivos.clases.Data;
import com.sistematias.relevadordispositivos.clases.Querys;

import java.util.Date;

/**
 * Created by RUBEN on 01/11/2015.
 */
public class Tracking {
    private int idTracking=0;
    private String latitud = "";
    private String fechaCaptura = "";
    private String imei = "";
    private String codPuntoVenta = "";
    private String codRepositor = "";
    private String fechaRegistro = "";
    private String longitud = "";
    private int precision = 0;
    private String codTracking = "";
    private String codSucursal = "";

    public String getCodSucursal() {
        return codSucursal;
    }

    public void setCodSucursal(String codSucursal) {
        this.codSucursal = codSucursal;
    }

    public String getCodTracking() {
        return codTracking;
    }

    public void setCodTracking(String codTracking) {
        this.codTracking = codTracking;
    }

    public String getCodRepositor() {
        return codRepositor;
    }

    public void setCodRepositor(String codRepositor) {
        this.codRepositor = codRepositor;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public int getIdTracking() {
        return idTracking;
    }

    public void setIdTracking(int idTracking) {
        this.idTracking = idTracking;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }

    public String getFechaCaptura() {
        return fechaCaptura;
    }

    public void setFechaCaptura(String fechaCaptura) {
        this.fechaCaptura = fechaCaptura;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getCodPuntoVenta() {
        return codPuntoVenta;
    }

    public void setCodPuntoVenta(String codPuntoVenta) {
        this.codPuntoVenta = codPuntoVenta;
    }

    public int getPrecision() {
        return precision;
    }

    public void setPrecision(int precision) {
        this.precision = precision;
    }

    public String saveData() {
        ContentValues values = new ContentValues();
        values.put("Latitud", this.latitud);
        values.put("Longitud", this.longitud);
        values.put("Precision", this.precision);
        values.put("FechaRegistro", this.fechaRegistro);
        values.put("CodRepositor", this.codRepositor);
        values.put("IMEI", this.imei);
        values.put("FechaCaptura", this.fechaCaptura);
        values.put("CodPuntoVenta", this.codPuntoVenta);
        values.put("CodSucursal",this.codSucursal);


        long id = Data.getConn().insert("Tracking", values);
        ContentValues updateValues = new ContentValues();
        String codTracking = "TRACKING"+id+"_"+Data.FECHA_FULL_FORMAT.format(new Date());
        updateValues.put("CodTracking", codTracking);
        Data.getConn().update("Tracking", updateValues, "IdTracking" + "='" + id + "'", null);
        return codTracking;
    }

    public void getTrackingByCodTracking(String codTracking) {
        this.codTracking = codTracking;
        Cursor cursor= null;
        cursor = Data.getConn().executeSelect(Querys.getTracking(codTracking),cursor);
        while (cursor.moveToNext()) {
            this.idTracking = cursor.getInt(0);
            this.latitud = cursor.getString(1);
            this.longitud = cursor.getString(2);
            this.precision = cursor.getInt(3);
            this.fechaRegistro = cursor.getString(4);
            this.codRepositor = cursor.getString(5);
            this.imei = cursor.getString(6);
            this.fechaCaptura = cursor.getString(7);
            this.codPuntoVenta = cursor.getString(8);
            this.codTracking = cursor.getString(9);
            this.codSucursal = cursor.getString(10);
        }
        cursor.close();
    }

    public void setData(){
        ContentValues values = new ContentValues();
        values.put("Latitud", this.latitud);
        values.put("Longitud", this.longitud);
        values.put("Precision", this.precision);
        values.put("FechaRegistro", this.fechaRegistro);
        values.put("CodRepositor", this.codRepositor);
        values.put("IMEI", this.imei);
        values.put("FechaCaptura", this.fechaCaptura);
        values.put("CodPuntoVenta", this.codPuntoVenta);
        values.put("CodSucursal",this.codSucursal);
        Data.getConn().update("Tracking", values, "CodTracking" + "='" + this.codTracking + "'", null);
    }

}
