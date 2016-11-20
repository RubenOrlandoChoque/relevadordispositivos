package com.sistematias.relevadordispositivos.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.sistematias.relevadordispositivos.clases.Data;
import com.sistematias.relevadordispositivos.clases.Querys;

/**
 * Created by samuel on 06/11/2015.
 */
public class Persistencia {
    private int idPersistencia=0;
    private String codPersistencia = "";
    private String valuePersistencia = "";

    public static final String COD_FECHA_ULTIMO_INICIO_SESION = "COD_FECHA_ULTIMO_INICIO_SESION";
    public static final String COD_NOMBRE_PUNTO_VENTA_ACTUAL = "COD_NOMBRE_PUNTO_VENTA_ACTUAL";
    public static final String COD_PUNTO_VENTA_ACTUAL = "COD_PUNTO_VENTA_ACTUAL";
    public static final String COD_REPOSITOR = "COD_REPOSITOR";
    public static final String COD_TIPO_USUARIO = "COD_TIPO_USUARIO";
    public static final String COD_SUCURSAL = "COD_SUCURSAL";

    public int getIdPersistencia() {
        return idPersistencia;
    }

    public void setIdPersistencia(int idPersistencia) {
        this.idPersistencia = idPersistencia;
    }

    public String getCodPersistencia() {
        return codPersistencia;
    }

    public void setCodPersistencia(String codPersistencia) {
        this.codPersistencia = codPersistencia;
    }

    public String getValuePersistencia() {
        return valuePersistencia;
    }

    public void setValuePersistencia(String valuePersistencia) {
        this.valuePersistencia = valuePersistencia;
    }

    public void getPersistenciaByCod(String codPersistencia) {
        this.codPersistencia = codPersistencia;
        Cursor cursor= null;
        cursor = Data.getConn().executeSelect(Querys.getPersistencia(codPersistencia),cursor);
        while (cursor.moveToNext()) {
            this.idPersistencia = cursor.getInt(0);
            this.codPersistencia = cursor.getString(1);
            this.valuePersistencia = cursor.getString(2);
        }
        cursor.close();
    }

    public void setData(){
        ContentValues values = new ContentValues();
        values.put("ValuePersistencia", this.valuePersistencia);
        Data.getConn().update("Persistencia", values, "CodPersistencia" + "='" + this.codPersistencia + "'", null);
    }

    public static void limpiarPersistencia(){
        Data.getConn().executeSQLQuery(Querys.limpiarPersistencia());
    }
}
