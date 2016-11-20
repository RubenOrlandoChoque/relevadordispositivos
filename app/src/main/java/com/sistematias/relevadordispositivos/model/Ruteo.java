package com.sistematias.relevadordispositivos.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.sistematias.relevadordispositivos.clases.Data;
import com.sistematias.relevadordispositivos.clases.Querys;

import java.util.Date;

/**
 * Created by samuel on 22/10/2015.
 */
public class Ruteo {
    private int idRuteo = 0;
    private String codRuteo = "";
    private String codProducto = "";
    private String codRepositor = "";
    private String codPuntoVenta = "";
    private String observacion = "";
    private String fechaCreacion = "";
    private String fechaRegistro = "";
    private String coordenadas = "";
    private double precio = 0;
    private String imagen = "";
    private int rango = 0;
    private String fechaRegistroFoto;
    private String codSucursal = "";

    public String getCodSucursal() {
        return codSucursal;
    }

    public void setCodSucursal(String codSucursal) {
        this.codSucursal = codSucursal;
    }

    public String getFechaRegistroFoto() {
        return fechaRegistroFoto;
    }

    public void setFechaRegistroFoto(String fechaRegistroFoto) {
        this.fechaRegistroFoto = fechaRegistroFoto;
    }

    public int getRango() {
        return rango;
    }

    public void setRango(int rango) {
        this.rango = rango;
    }

    public int getIdRuteo() {
        return idRuteo;
    }

    public void setIdRuteo(int idRuteo) {
        this.idRuteo = idRuteo;
    }

    public String getCodRuteo() {
        return codRuteo;
    }

    public void setCodRuteo(String codRuteo) {
        this.codRuteo = codRuteo;
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

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public void getRuteoByCodRuteo(String codRuteo) {
        this.codRuteo = codRuteo;
        Cursor cursor= null;
        cursor = Data.getConn().executeSelect(Querys.getRuteo(codRuteo),cursor);
        while (cursor.moveToNext()) {
            this.idRuteo = cursor.getInt(0);
            this.codRuteo = cursor.getString(1);
            this.codProducto = cursor.getString(2);
            this.codRepositor = cursor.getString(3);
            this.codPuntoVenta = cursor.getString(4);
            this.observacion = cursor.getString(5);
            this.fechaCreacion = cursor.getString(6);
            this.fechaRegistro = cursor.getString(7);
            this.coordenadas = cursor.getString(8);
            this.precio = cursor.getDouble(9);
            this.imagen = cursor.getString(10);
            this.rango = cursor.getInt(11);
            this.fechaRegistroFoto = cursor.getString(12);
            this.codSucursal = cursor.getString(13);

        }
        cursor.close();
    }

    public String saveData() {
        ContentValues values = new ContentValues();
        values.put("CodProducto", this.codProducto);
        values.put("CodRepositor", this.codRepositor);
        values.put("CodPuntoVenta", this.codPuntoVenta);
        values.put("Observacion", this.observacion);
        values.put("FechaCreacion", this.fechaCreacion);
        values.put("FechaRegistro", "");
        values.put("Coordenadas",this.coordenadas);
        values.put("Precio", this.precio);
        values.put("Imagen",this.imagen);
        values.put("Rango",this.rango);
        values.put("FechaRegistroFoto",this.fechaRegistroFoto);
        values.put("CodSucursal",this.codSucursal);
        long id = Data.getConn().insert("Ruteos",values);

        ContentValues updateValues = new ContentValues();
        String codEncuesta = "RUTEO_"+id+"_"+Data.FECHA_FULL_FORMAT.format(new Date());
        updateValues.put("CodRuteo", codEncuesta);
        Data.getConn().update("Ruteos",updateValues, "idRuteo" + "='" + id + "'", null);
        return codEncuesta;
    }

    public void setData(){
        ContentValues values = new ContentValues();
        values.put("CodProducto", this.codProducto);
        values.put("CodRepositor", this.codRepositor);
        values.put("CodPuntoVenta", this.codPuntoVenta);
        values.put("Observacion", this.observacion);
        values.put("FechaCreacion", this.fechaCreacion);
        values.put("FechaRegistro", this.fechaRegistro);
        values.put("Coordenadas",this.coordenadas);
        values.put("Precio", this.precio);
        values.put("Imagen",this.imagen);
        values.put("Rango",this.rango);
        values.put("FechaRegistroFoto",this.fechaRegistroFoto);
        values.put("CodSucursal",this.codSucursal);
        Data.getConn().update("Ruteos", values, "codRuteo" + "='" + this.codRuteo + "'", null);
    }
}
