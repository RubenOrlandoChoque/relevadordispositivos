package com.sistematias.relevadordispositivos.clases;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by RUBEN on 29/08/2016.
 */
public class Device {
    private String numero = "-1";
    private String cuil ="";
    private String apellido ="";
    private String secion ="";
    private String turnoOrigen ="";
    private String turnoActual ="";
    private String firm ="";
    private String marca ="";
    private String hid ="";
    private String nroSerie ="";
    private String dni ="";
    private String estado ="";
    private String migracion ="";
    private String reclamo ="";
    private String denunciaRobo ="";
    private String reasignadoA ="";
    private String fReasignado ="";

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getCuil() {
        return cuil;
    }

    public void setCuil(String cuil) {
        this.cuil = cuil;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getSecion() {
        return secion;
    }

    public void setSecion(String secion) {
        this.secion = secion;
    }

    public String getTurnoOrigen() {
        return turnoOrigen;
    }

    public void setTurnoOrigen(String turnoOrigen) {
        this.turnoOrigen = turnoOrigen;
    }

    public String getTurnoActual() {
        return turnoActual;
    }

    public void setTurnoActual(String turnoActual) {
        this.turnoActual = turnoActual;
    }

    public String getFirm() {
        return firm;
    }

    public void setFirm(String firm) {
        this.firm = firm;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getHid() {
        return hid;
    }

    public void setHid(String hid) {
        this.hid = hid;
    }

    public String getNroSerie() {
        return nroSerie;
    }

    public void setNroSerie(String nroSerie) {
        this.nroSerie = nroSerie;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMigracion() {
        return migracion;
    }

    public void setMigracion(String migracion) {
        this.migracion = migracion;
    }

    public String getReclamo() {
        return reclamo;
    }

    public void setReclamo(String reclamo) {
        this.reclamo = reclamo;
    }

    public String getDenunciaRobo() {
        return denunciaRobo;
    }

    public void setDenunciaRobo(String denunciaRobo) {
        this.denunciaRobo = denunciaRobo;
    }

    public String getReasignadoA() {
        return reasignadoA;
    }

    public void setReasignadoA(String reasignadoA) {
        this.reasignadoA = reasignadoA;
    }

    public String getfReasignado() {
        return fReasignado;
    }

    public void setfReasignado(String fReasignado) {
        this.fReasignado = fReasignado;
    }


    public static boolean existeDispositivo(String numero){
        return Data.getConn().getIntSelect("select count(idDevice) from devices where numero='"+numero+"'")>0;
    }

    public static String getCuil(String numero){
        return Data.getConn().getStringSelect("select distinct cuil from devices where numero='"+numero+"'");
    }

    public static boolean existeDispositivoByCuil(String numero){
        return Data.getConn().getIntSelect("select cuil, from devices where cuil like '"+numero+"'")>0;
    }

    public static ArrayList<Device> getDispositivosByCuil(String numero){
        ArrayList<Device> dispositivos = new ArrayList<>();
        Cursor cursor = null;
        cursor = Data.getConn().executeSelect("select cuil,apellido from devices where cuil like '%"+numero+"%'",cursor);
        while (cursor.moveToNext()){
            Device d = new Device();
            d.setCuil(cursor.getString(0));
            d.setApellido(cursor.getString(1));
            dispositivos.add(d);
        }
        return dispositivos;
    }



    public static Device getDeviceByCuil(String cuil){
        Device device=new Device();
        String q = "select * from devices where cuil='"+cuil+"'";
        Cursor cursor = null;
        cursor = Data.getConn().executeSelect(q,cursor);
        if(cursor.moveToNext()){
            device.setNumero(cursor.getString(cursor.getColumnIndex("numero")));
            device.setCuil(cursor.getString(cursor.getColumnIndex("cuil")));
            device.setApellido(cursor.getString(cursor.getColumnIndex("apellido")));
            device.setSecion(cursor.getString(cursor.getColumnIndex("seccion")));
            device.setTurnoOrigen(cursor.getString(cursor.getColumnIndex("turnoOrigen")));
            device.setTurnoActual(cursor.getString(cursor.getColumnIndex("turnoActual")));
            device.setMarca(cursor.getString(cursor.getColumnIndex("marca")));
            device.setFirm(cursor.getString(cursor.getColumnIndex("firm")));
            device.setHid(cursor.getString(cursor.getColumnIndex("hid")));
            device.setNroSerie(cursor.getString(cursor.getColumnIndex("nroSerie")));
            device.setDni(cursor.getString(cursor.getColumnIndex("dni")));
            device.setEstado(cursor.getString(cursor.getColumnIndex("estado")));
            device.setMigracion(cursor.getString(cursor.getColumnIndex("migracion")));
            device.setReclamo(cursor.getString(cursor.getColumnIndex("reclamo")));
            device.setDenunciaRobo(cursor.getString(cursor.getColumnIndex("denunciaRobo")));
            device.setReasignadoA(cursor.getString(cursor.getColumnIndex("reasignadoA")));
            device.setfReasignado(cursor.getString(cursor.getColumnIndex("FReasignado")));
        }
        return device;
    }

    public void save(){
        int existe = Data.getConn().getIntifExists("select count(idDevice) from devices where cuil='"+this.cuil+"'");
        ContentValues values = new ContentValues();
        values.put("numero", this.numero);
        values.put("cuil", this.cuil);
        values.put("apellido", this.apellido);
        values.put("seccion", this.secion);
        values.put("turnoOrigen", this.turnoOrigen);
        values.put("turnoActual", this.turnoActual);
        values.put("firm",this.firm);
        values.put("marca", this.marca);
        values.put("hid",this.hid);
        values.put("nroSerie",this.nroSerie);
        values.put("dni",this.dni);
        values.put("estado",this.estado);
        values.put("migracion",this.migracion);
        values.put("reclamo",this.reclamo);
        values.put("denunciaRobo",this.denunciaRobo);
        values.put("reasignadoA",this.reasignadoA);
        values.put("FReasignado",this.fReasignado);
        if(existe>0){
            Data.getConn().update("Devices", values, "cuil" + "='" + this.cuil+"'", null);
        }else {
            long id = Data.getConn().insert("Devices",values);
        }
    }
}
