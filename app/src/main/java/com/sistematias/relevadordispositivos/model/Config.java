package com.sistematias.relevadordispositivos.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.sistematias.relevadordispositivos.clases.Data;
import com.sistematias.relevadordispositivos.clases.Querys;

/**
 * Created by samuel on 27/10/2015.
 */
public class Config {
    public int getIdConfig() {
        return idConfig;
    }

    public void setIdConfig(int idConfig) {
        this.idConfig = idConfig;
    }

    public String getCodConfig() {
        return codConfig;
    }

    public void setCodConfig(String codConfig) {
        this.codConfig = codConfig;
    }

    public String getValueConfig() {
        return valueConfig;
    }

    public void setValueConfig(String valueConfig) {
        this.valueConfig = valueConfig;
    }

    public void getConfigByCod(String codConfig) {
        this.codConfig = codConfig;
        Cursor cursor= null;
        cursor = Data.getConn().executeSelect(Querys.getConfig(codConfig),cursor);
        while (cursor.moveToNext()) {
            this.idConfig = cursor.getInt(0);
            this.codConfig = cursor.getString(1);
            this.valueConfig = cursor.getString(2);
        }
        cursor.close();
    }

    public void getConfigByCodEmpty(String codConfig) {
        Cursor cursor= null;
        cursor = Data.getConn().executeSelect(Querys.getConfig(codConfig),cursor);
        while (cursor.moveToNext()) {
            this.idConfig = cursor.getInt(0);
            this.codConfig = cursor.getString(1);
            this.valueConfig = cursor.getString(2);
        }
        cursor.close();
    }

    public String saveData() {
        ContentValues values = new ContentValues();
        values.put("ValueCondig", this.valueConfig);
        values.put("CodConfig", this.codConfig);
        long id = Data.getConn().insert("Config",values);
        return this.codConfig;
    }

    public void setData(){
        ContentValues values = new ContentValues();
        values.put("ValueConfig", this.valueConfig);
        Data.getConn().update("Config", values, "CodConfig" + "='" + this.codConfig + "'", null);
    }

    private int idConfig = 0;
    private String codConfig = "";
    private String valueConfig = "";

}
