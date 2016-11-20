package com.sistematias.relevadordispositivos.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.sistematias.relevadordispositivos.clases.Data;
import com.sistematias.relevadordispositivos.clases.Querys;

import java.util.Date;

/**
 * Created by samuel on 31/10/2015.
 */
public class FotoNovedad {
    public int getIdFotoNovedad() {
        return idFotoNovedad;
    }

    public void setIdFotoNovedad(int idFotoNovedad) {
        this.idFotoNovedad = idFotoNovedad;
    }

    public String getCodFotoNovedad() {
        return codFotoNovedad;
    }

    public void setCodFotoNovedad(String codFotoNovedad) {
        this.codFotoNovedad = codFotoNovedad;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getCodNovedad() {
        return codNovedad;
    }

    public void setCodNovedad(String codNovedad) {
        this.codNovedad = codNovedad;
    }

    private  int idFotoNovedad=0;
    private String codFotoNovedad="";
    private String fechaRegistro = "";
    private String imagen = "";
    private String codNovedad = "";


    public void getFotoNovedadByCod(String codFotoNovedad) {
        this.codFotoNovedad = codFotoNovedad;
        Cursor cursor= null;
        cursor = Data.getConn().executeSelect(Querys.getFotoNoveadad(codFotoNovedad),cursor);
        while (cursor.moveToNext()) {
            this.idFotoNovedad = cursor.getInt(0);
            this.codFotoNovedad = cursor.getString(1);
            this.fechaRegistro = cursor.getString(2);
            this.imagen = cursor.getString(3);
            this.codNovedad = cursor.getString(4);
        }
        cursor.close();
    }

    public String saveData() {
        ContentValues values = new ContentValues();
        values.put("CodFotoNovedad", this.codFotoNovedad);
        values.put("FechaRegistro", this.fechaRegistro);
        values.put("Imagen", this.imagen);
        values.put("CodNovedad", this.codNovedad);

        long id = Data.getConn().insert("Fotos_Novedades",values);
        ContentValues updateValues = new ContentValues();
        String codFotoNovedad = "FN_"+id+"_"+Data.FECHA_FULL_FORMAT.format(new Date());
        updateValues.put("CodFotoNovedad", codFotoNovedad);
        Data.getConn().update("Fotos_Novedades",updateValues, "idFotoNovedad" + "='" + id + "'", null);
        return codNovedad;
    }

    public void setData(){
        ContentValues values = new ContentValues();
        values.put("CodFotoNovedad", this.codFotoNovedad);
        values.put("FechaRegistro", this.fechaRegistro);
        values.put("Imagen", this.imagen);
        values.put("CodNovedad", this.codNovedad);
        Data.getConn().update("Fotos_Novedades", values, "CodFotoNovedad" + "='" + this.codFotoNovedad + "'", null);
    }

    public static void updateCodNovedad(String codNovedad,String codNewNovedad){
        Data.getConn().executeSQLQuery(Querys.updateCodNovedadInFotosNovedades(codNovedad,codNewNovedad));
    }
}
