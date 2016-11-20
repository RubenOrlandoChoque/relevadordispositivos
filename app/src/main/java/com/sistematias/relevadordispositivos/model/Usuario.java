package com.sistematias.relevadordispositivos.model;

import android.content.ContentValues;
import android.database.Cursor;

import com.sistematias.relevadordispositivos.clases.Data;
import com.sistematias.relevadordispositivos.clases.Querys;

import java.security.MessageDigest;
import java.util.Vector;

/**
 * Created by RUBEN on 31/10/2015.
 */
public class Usuario {
    private int idUsuario = 0;
    private String codUsuario = "";
    private String usuario = "";
    private String pass = "";
    private String codPuntoVenta = "";
    private String codTipoUsuario = "";

    public int getSincronizarTracking() {
        return sincronizarTracking;
    }

    public void setSincronizarTracking(int sincronizarTracking) {
        this.sincronizarTracking = sincronizarTracking;
    }

    private int sincronizarTracking = 1;

    private String codSucursal = "";

    public String getCodSucursal() {
        return codSucursal;
    }

    public void setCodSucursal(String codSucursal) {
        this.codSucursal = codSucursal;
    }

    public static final String COD_ADMIN = "ADMIN";
    public static final String COD_REPOSITOR = "REPOSITOR";

    public String getCodTipoUsuario() {
        return codTipoUsuario;
    }

    public void setCodTipoUsuario(String codTipoUsuario) {
        this.codTipoUsuario = codTipoUsuario;
    }

    public String getFechaUltimoInicioSesion() {
        return fechaUltimoInicioSesion;
    }

    public void setFechaUltimoInicioSesion(String fechaUltimoInicioSesion) {
        this.fechaUltimoInicioSesion = fechaUltimoInicioSesion;
    }

    private String fechaUltimoInicioSesion = "";

    public String getPuntoVenta() {
        return puntoVenta;
    }

    public void setPuntoVenta(String puntoVenta) {
        this.puntoVenta = puntoVenta;
    }

    public String getCodPuntoVenta() {
        return codPuntoVenta;
    }

    public void setCodPuntoVenta(String codPuntoVenta) {
        this.codPuntoVenta = codPuntoVenta;
    }

    private String puntoVenta = "";

    public Vector<PuntoVenta> getPuntoVentaDelRepositor() {
        return puntoVentaDelRepositor;
    }

    private Vector<PuntoVenta> puntoVentaDelRepositor = new Vector<>();

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCodUsuario() {
        return codUsuario;
    }

    public void setCodUsuario(String codUsuario) {
        this.codUsuario = codUsuario;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void checkUsuario(String user, String pass) {
        Cursor cursor = null;
        cursor = Data.getConn().executeSelect(Querys.getUsuarioByUser(user), cursor);
        while (cursor.moveToNext()) {
            /*validar por comparacion de claves --> modificar */
            try {
                String original = pass;
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(original.getBytes());
                byte[] digest = md.digest();
                StringBuffer sb = new StringBuffer();
                for (byte b : digest) {
                    sb.append(String.format("%02x", b & 0xff));
                }
                System.out.println("original:" + original);
                System.out.println("digested(hex):" + sb.toString());
                //if (cursor.getString(2).trim().toUpperCase().equals(sb.toString().toUpperCase())) {
                if (true) {
                    this.codUsuario = cursor.getString(0);
                    this.usuario = cursor.getString(1);
                    this.pass = cursor.getString(2);
                    this.fechaUltimoInicioSesion = cursor.getString(3);
                    this.codTipoUsuario = cursor.getString(4);
                    this.sincronizarTracking = cursor.getInt(5);
                }
            } catch (Exception e) {

            }


        }
        cursor.close();
    }

    public static Usuario getUsuarioByCodUser(String codUser){
        Cursor cursor = null;
        cursor = Data.getConn().executeSelect(Querys.getUsuarioByCodUser(codUser), cursor);
        Usuario usuario = new Usuario();
        while (cursor.moveToNext()) {
            usuario.codUsuario = cursor.getString(0);
            usuario.usuario = cursor.getString(1);
            usuario.pass = cursor.getString(2);
            usuario.fechaUltimoInicioSesion = cursor.getString(3);
            usuario.codTipoUsuario = cursor.getString(4);
            usuario.sincronizarTracking = cursor.getInt(5);
        }
        cursor.close();
        return usuario;
    }

    public Vector<PuntoVenta> getPuntosVentas() {
        Cursor cursor = null;
        cursor = Data.getConn().executeSelect(Querys.getSucursalesByCodUsuario(this.codUsuario), cursor);
        while (cursor.moveToNext()) {
            PuntoVenta puntoVenta = new PuntoVenta();
            puntoVenta.setCodPuntoVenta(cursor.getString(0));
            puntoVenta.setNombre(cursor.getString(1).trim());
            puntoVenta.setCodSucursal(cursor.getString(2));
            this.puntoVentaDelRepositor.add(puntoVenta);
        }
        cursor.close();
        return this.puntoVentaDelRepositor;
    }

    public String saveData() {
        ContentValues values = new ContentValues();
        values.put("CodUsuario", this.codUsuario);
        values.put("Usuario", this.usuario);
        values.put("Pass", this.pass);
        values.put("CodPuntoVenta", this.codPuntoVenta);
        values.put("PuntoVenta", this.puntoVenta);
        values.put("FechaUltimoInicioSesion", this.fechaUltimoInicioSesion);
        values.put("CodTipoUsuario", this.codTipoUsuario);
        values.put("CodSucursal", this.codSucursal);
        values.put("SincronizarTracking", this.sincronizarTracking);

        Data.getConn().insert("Usuarios", values);
        return this.codUsuario;
    }

    public void setData() {
        ContentValues values = new ContentValues();
        values.put("CodUsuario", this.codUsuario);
        values.put("Usuario", this.usuario);
        values.put("Pass", this.pass);
        values.put("CodPuntoVenta", this.codPuntoVenta);
        values.put("PuntoVenta", this.puntoVenta);
        values.put("FechaUltimoInicioSesion", this.fechaUltimoInicioSesion);
        values.put("CodTipoUsuario", this.codTipoUsuario);
        values.put("CodSucursal", this.codSucursal);
        values.put("SincronizarTracking", this.sincronizarTracking);
        Data.getConn().update("Usuarios", values, "CodUsuario" + "='" + this.codUsuario + "'", null);
    }


    public void setFechaUltimoInicioSesion() {
        ContentValues values = new ContentValues();
        values.put("FechaUltimoInicioSesion", this.fechaUltimoInicioSesion);
        Data.getConn().update("Usuarios", values, "CodUsuario" + "='" + this.codUsuario + "'", null);
    }
}
