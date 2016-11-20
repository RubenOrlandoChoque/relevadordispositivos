package com.sistematias.relevadordispositivos.clases;

/**
 * Created by samuel on 17/10/2015.
 */
public class Querys {

    public static String getProductoByEAN(String ean) {
        return "select * from productos where ean='" + ean + "'";
    }

    public static String getRuteo(String ruteo) {
        return "select * from Ruteos where CodRuteo='" + ruteo + "'";
    }

    public static String getNovedad(String codNovedad) {
        return "select * from Novedades where CodNovedad='" + codNovedad + "'";
    }

    public static String getTracking(String codTracking) {
        return "select * from Tracking where CodTracking='" + codTracking + "'";
    }

    public static String getFotoNoveadad(String codFotoNovedad) {
        return "select * from Fotos_Novedades where CodFotoNovedad='" + codFotoNovedad+ "'";
    }

    public static String getUsuarioByUser(String user) {
        return "select CodUsuario,Usuario,Pass, ifnull(FechaUltimoInicioSesion,''),CodTipoUsuario,SincronizarTracking from usuarios where Usuario='"+user+"' limit 1";
    }

    public static String getUsuarioByCodUser(String codUser) {
        return "select CodUsuario,Usuario,Pass, ifnull(FechaUltimoInicioSesion,''),CodTipoUsuario,SincronizarTracking from usuarios where CodUsuario='"+codUser+"' limit 1";
    }


    public static String getSucursalesByCodUsuario(String codUsuario) {
        return "select distinct CodPuntoVenta,PuntoVenta,CodSucursal from usuarios where codusuario='"+codUsuario+"'";
    }

    public static String getConfig(String codConfig) {
        return "select * from Config where CodConfig='" + codConfig + "'";
    }

    public static String getPersistencia(String codPersistencia) {
        return "select * from Persistencia where CodPersistencia='" + codPersistencia + "'";
    }

    public static String getProductos() {
        return "select * from productos";
    }

    public static String getRuteos() {
        return "select * from ruteos where fecharegistro =''";
    }

    public static String getRuteosFotos() {
        return "select * from ruteos where FechaRegistroFoto ='' and Imagen<>''";
    }

    public static String getNovedades() {
        return "select * from Novedades where fecharegistro =''";
    }

    public static String getTracking(){
        return "select * from Tracking where FechaRegistro =''";
    }

    public static String getTiposNovedades() {
        return "select idTipoNovedad,CodTipoNovedad,Descripcion " +
                "from Tipos_Novedades";
    }

    public static String getFotosNovedades() {
        return "select * " +
                "from Fotos_Novedades where fecharegistro =''";
    }

    public static String getTotalRuteo() {
        return "select count(codruteo) as total from ruteos";
    }

    public static String getSyncRuteo() {
        return "select count(codruteo) as total from ruteos where fecharegistro <>''";
    }

    public static String getTotalFotosRuteo() {
        return "select count(codruteo) as total from ruteos where Imagen <> ''";
    }

    public static String getSyncFotosRuteo() {
        return "select count(codruteo) as total from ruteos where FechaRegistroFoto <>'' and Imagen <> ''";
    }

    public static String getTotalNovedades() {
        return "select count(CodNovedad) as total from Novedades";
    }

    public static String getSyncNovedades() {
        return "select count(CodNovedad) as total from Novedades where FechaRegistro <>''";
    }

    public static String getTotalFotos() {
        return "select count(CodFotoNovedad) as total from Fotos_Novedades";
    }

    public static String getSyncFotos() {
        return "select count(CodFotoNovedad) as total from Fotos_Novedades where FechaRegistro <>''";
    }

    public static String getTotalProductos() {
        return "select count(CodProducto) as total from Productos";
    }

    public static String getTotalTracking() {
        return "select count(CodTracking) as total from Tracking";
    }

    public static String getSyncTracking() {
        return "select count(CodTracking) as total from Tracking where FechaRegistro <>''";
    }


    public static String deleteUsuarios() {
        return "delete from Usuarios;";
    }

    public static String deleteProductosByCodProducto(String codProducto) {
        return "delete from productos where codproducto in ("+codProducto+");";
    }

    public static String getLastFechaModificacionProducto(){
        return "select ifnull(max(FechaModificacion),'') from productos order by date(FechaModificacion) limit 1";
    }

    public static String limpiarPersistencia(){
        return "update Persistencia set ValuePersistencia=''";
    }

    public static String updateCodNovedadInFotosNovedades(String codNovedad,String codNewNovedad){
        return "update Fotos_Novedades set CodNovedad='"+codNewNovedad+"' where CodNovedad = '"+codNovedad+"'";
    }

    public static String updateCodNovedadInNovedades(String codNovedad,String codNewNovedad){
        return "update Novedades set CodNovedad='"+codNewNovedad+"' where CodNovedad = '"+codNovedad+"'";
    }
}
