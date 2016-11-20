package com.sistematias.relevadordispositivos.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.sistematias.relevadordispositivos.clases.Data;
import com.sistematias.relevadordispositivos.clases.Querys;

import java.util.Date;

/**
 * Created by samuel on 21/10/2015.
 */
public class Novedad {
    private int idNovedad = 0;
    private String codNovedad = "";
    private String codProducto = "";
    private String codRepositor = "";
    private String codPuntoVenta = "";
    private String codTipoNovedad = "";
    private String observacion = "";
    private String fechaCreacion = "";
    private String fechaRegistro = "";
    private String coordenadas = "";
    private double precio = 0;
    private String fechaElaboracion = "";
    private String horaElaboracion = "";
    private String numLote = "";
    private int cantidad = 0;
    private int rango=0;
    private String codSucursal = "";

    public String getCodSucursal() {
        return codSucursal;
    }

    public void setCodSucursal(String codSucursal) {
        this.codSucursal = codSucursal;
    }

    public int getRango() {
        return rango;
    }

    public void setRango(int rango) {
        this.rango = rango;
    }

    public int getIdNovedad() {
        return idNovedad;
    }

    public void setIdNovedad(int idNovedad) {
        this.idNovedad = idNovedad;
    }

    public String getCodNovedad() {
        return codNovedad;
    }

    public void setCodNovedad(String codNovedad) {
        this.codNovedad = codNovedad;
    }

    public String getCodProducto() {
        return codProducto;
    }

    public void setCodProducto(String codProducto) {
        this.codProducto = codProducto;
    }

    public String getCodRepositor() {
        return codRepositor;
    }

    public void setCodRepositor(String codRepositor) {
        this.codRepositor = codRepositor;
    }

    public String getCodPuntoVenta() {
        return codPuntoVenta;
    }

    public void setCodPuntoVenta(String codPuntoVenta) {
        this.codPuntoVenta = codPuntoVenta;
    }

    public String getCodTipoNovedad() {
        return codTipoNovedad;
    }

    public void setCodTipoNovedad(String codTipoNovedad) {
        this.codTipoNovedad = codTipoNovedad;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getCoordenadas() {
        return coordenadas;
    }

    public void setCoordenadas(String coordenadas) {
        this.coordenadas = coordenadas;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getFechaElaboracion() {
        return fechaElaboracion;
    }

    public void setFechaElaboracion(String fechaElaboracion) {
        this.fechaElaboracion = fechaElaboracion;
    }

    public String getHoraElaboracion() {
        return horaElaboracion;
    }

    public void setHoraElaboracion(String horaElaboracion) {
        this.horaElaboracion = horaElaboracion;
    }

    public String getNumLote() {
        return numLote;
    }

    public void setNumLote(String numLote) {
        this.numLote = numLote;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void getNovedadByCod(String codNovedad) {
        this.codNovedad = codNovedad;
        Cursor cursor= null;
        cursor = Data.getConn().executeSelect(Querys.getNovedad(codNovedad),cursor);
        while (cursor.moveToNext()) {
            this.idNovedad = cursor.getInt(0);
            this.codNovedad = cursor.getString(1);
            this.codProducto = cursor.getString(2);
            this.codRepositor = cursor.getString(3);
            this.codPuntoVenta = cursor.getString(4);
            this.codTipoNovedad = cursor.getString(5);
            this.observacion = cursor.getString(6);
            this.fechaCreacion = cursor.getString(7);
            this.fechaRegistro = cursor.getString(8);
            this.coordenadas = cursor.getString(9);
            this.precio = cursor.getDouble(10);
            this.fechaElaboracion = cursor.getString(11);
            this.horaElaboracion = cursor.getString(12);
            this.numLote = cursor.getString(13);
            this.cantidad = cursor.getInt(14);
            this.rango = cursor.getInt(15);
            this.codSucursal = cursor.getString(16);
        }
        cursor.close();
    }

    public String saveData() {
        ContentValues values = new ContentValues();
        values.put("CodNovedad", this.codProducto);
        values.put("CodProducto", this.codProducto);
        values.put("CodRepositor", this.codRepositor);
        values.put("CodPuntoVenta", this.codPuntoVenta);
        values.put("CodTipoNovedad", this.codTipoNovedad);
        values.put("Observacion", this.observacion);
        values.put("FechaCreacion",this.fechaCreacion);
        values.put("FechaRegistro", this.fechaRegistro);
        values.put("Coordenadas",this.coordenadas);
        values.put("Precio",this.precio);
        values.put("FechaElaboracion",this.fechaElaboracion);
        values.put("HoraElaboracion",this.horaElaboracion);
        values.put("NumLote",this.numLote);
        values.put("Cantidad",this.cantidad);
        values.put("Rango",this.rango);
        values.put("CodSucursal",this.codSucursal);

        long id = Data.getConn().insert("Novedades",values);
        ContentValues updateValues = new ContentValues();
        String codNovedad = "NOVEDAD_"+id+"_"+Data.FECHA_FULL_FORMAT.format(new Date());
        updateValues.put("CodNovedad", codNovedad);
        Data.getConn().update("Novedades",updateValues, "idNovedad" + "='" + id + "'", null);
        return codNovedad;
    }

    public void setData(){
        ContentValues values = new ContentValues();
        values.put("CodNovedad", this.codNovedad);
        values.put("CodProducto", this.codProducto);
        values.put("CodRepositor", this.codRepositor);
        values.put("CodPuntoVenta", this.codPuntoVenta);
        values.put("CodTipoNovedad", this.codTipoNovedad);
        values.put("Observacion", this.observacion);
        values.put("FechaCreacion",this.fechaCreacion);
        values.put("FechaRegistro", this.fechaRegistro);
        values.put("Coordenadas",this.coordenadas);
        values.put("Precio",this.precio);
        values.put("FechaElaboracion",this.fechaElaboracion);
        values.put("HoraElaboracion",this.horaElaboracion);
        values.put("NumLote",this.numLote);
        values.put("Cantidad",this.cantidad);
        values.put("Rango",this.rango);
        values.put("CodSucursal",this.codSucursal);
        Data.getConn().update("Novedades", values, "codNovedad" + "='" + this.codNovedad + "'", null);
    }

    public static void updateCodNovedadInNovedades(String codNovedad,String codNewNovedad){
        Data.getConn().executeSQLQuery(Querys.updateCodNovedadInNovedades(codNovedad,codNewNovedad));
    }
}
