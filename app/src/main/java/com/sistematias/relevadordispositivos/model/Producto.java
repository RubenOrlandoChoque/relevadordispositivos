package com.sistematias.relevadordispositivos.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import com.sistematias.relevadordispositivos.clases.Data;
import com.sistematias.relevadordispositivos.clases.Querys;

/**
 * Created by samuel on 19/09/2015.
 */
public class Producto {
    private int idProducto = 0;
    private String codProducto = "";
    private String ean = "";
    private String nombre = "";
    private String marca = "";
    private String presentacion = "";
    private String imagen = "";
    private boolean propio = true;
    private boolean habilitado = true;
    private String codEmpresa = "";
    private String fechaModificacion = "";

    public String getFamilia() {
        return familia;
    }

    public void setFamilia(String familia) {
        this.familia = familia;
    }

    private String familia = "";

    public String getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(String fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getCodProducto() {
        return codProducto;
    }

    public void setCodProducto(String codProducto) {
        this.codProducto = codProducto;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getPresentacion() {
        return presentacion;
    }

    public void setPresentacion(String foto) {
        this.presentacion = foto;
    }

    public boolean isPropio() {
        return propio;
    }

    public void setPropio(boolean propio) {
        this.propio = propio;
    }

    public boolean isHabilitado() {
        return habilitado;
    }

    public void setHabilitado(boolean habilitado) {
        this.habilitado = habilitado;
    }

    public String getImagen() {return imagen;}

    public void setImagen(String imagen) {this.imagen = imagen;}

    public void getProductoByEAN(String codigoEAN){
        Cursor cursor= null;
        Log.i("____", Querys.getProductoByEAN(codigoEAN));
        cursor = Data.getConn().executeSelect(Querys.getProductoByEAN(codigoEAN),cursor);
        while (cursor.moveToNext()) {
            idProducto = cursor.getInt(0);
            codProducto = cursor.getString(1);
            ean = cursor.getString(2);
            nombre = cursor.getString(3);
            marca = cursor.getString(4);
            presentacion = cursor.getString(5);
            imagen = cursor.getString(6);
            propio = cursor.getInt(7)==1;
            habilitado = cursor.getInt(8)==1;
            familia = cursor.getString(11);
        }
        cursor.close();
    }

    public String getCodEmpresa() {
        return codEmpresa;
    }

    public void setCodEmpresa(String codEmpresa) {
        this.codEmpresa = codEmpresa;
    }

    public String saveData() {
        ContentValues values = new ContentValues();
        values.put("CodProducto", this.codProducto);
        values.put("EAN", this.ean);
        values.put("Nombre", this.nombre);
        values.put("Marca", this.marca);
        values.put("Presentacion", this.presentacion);
        values.put("Imagen", this.imagen);
        values.put("Propio", this.propio);
        values.put("Habilitado", this.habilitado);
        values.put("CodEmpresa", this.codEmpresa);
        values.put("FechaModificacion",this.fechaModificacion);
        values.put("Familia",this.familia);

        Data.getConn().insert("Productos", values);
        return this.codProducto;
    }
}
