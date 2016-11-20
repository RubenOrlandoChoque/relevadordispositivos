package com.sistematias.relevadordispositivos.model;

import android.database.Cursor;

import com.sistematias.relevadordispositivos.clases.Data;
import com.sistematias.relevadordispositivos.clases.Querys;

import java.util.ArrayList;

/**
 * Created by samuel on 26/10/2015.
 */
public class TipoNovedad {
    private int idTipoNovedad = 0;
    private String codTipoNovedad = "";
    private String descripcion = "";

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCodTipoNovedad() {
        return codTipoNovedad;
    }

    public void setCodTipoNovedad(String codTipoNovedad) {
        this.codTipoNovedad = codTipoNovedad;
    }

    public int getIdTipoNovedad() {
        return idTipoNovedad;
    }

    public void setIdTipoNovedad(int idTipoNovedad) {
        this.idTipoNovedad = idTipoNovedad;
    }

    public static ArrayList<TipoNovedad> getAll(){
        ArrayList<TipoNovedad> tipoNovedades = new ArrayList<>();
        Cursor cursor = null;
        cursor = Data.getConn().executeSelect(Querys.getTiposNovedades(),cursor);
        while(cursor.moveToNext()){
            TipoNovedad tipoNovedad = new TipoNovedad();
            tipoNovedad.setIdTipoNovedad(cursor.getInt(0));
            tipoNovedad.setCodTipoNovedad(cursor.getString(1));
            tipoNovedad.setDescripcion(cursor.getString(2));
            tipoNovedades.add(tipoNovedad);
        }
        return tipoNovedades;
    }

    public static String[] getAllDescripciones(){
        Cursor cursor = null;
        cursor = Data.getConn().executeSelect(Querys.getTiposNovedades(),cursor);
        String[] tipoNovedades = new String[cursor.getCount()];
        int i= 0;
        while(cursor.moveToNext()){
            tipoNovedades[i++] = cursor.getString(2);
        }
        return tipoNovedades;
    }
}
