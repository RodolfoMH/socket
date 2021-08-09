/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newsoft.ElCamareroSocketServer.Utilities;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.newsoft.newsoft.utils.helpers.HelperDeEncriptacion;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.stream.Collectors;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author jmorel
 */
public class Utils {

    public static Gson convertidor = new Gson();
    //private static final String uriAutenticacion = "/Facturacion/webAPI/usuarioApp/autenticarUsuarioCamarero/";

    //para encriptar y descencritar mensajes 
    public static final HelperDeEncriptacion aes = new HelperDeEncriptacion("AES256");
    //public static FactoriaDAORestFul<NotificacionPendEntregar,Long> factoriaDAONotificacion;
    public static final ClienteRestful<NotificacionPendEntregar> notificactionRestfulClient = new ClienteRestful<NotificacionPendEntregar>("Facturacion/webAPI/notificacionPendienteEntregar", NotificacionPendEntregar.class);
    public static final ClienteRestful<TipoNotificacionPendEntrega> tipoNotificactionRestfulClient = new ClienteRestful<TipoNotificacionPendEntrega>("Facturacion/webAPI/tipoNotificacionPendienteEntregar", TipoNotificacionPendEntrega.class);
    
    //Listado de los tipos de noificaciones disponibles
    public static ArrayList<TipoNotificacionPendEntrega> listadoTipoNotificaciones;
    
    public static String[] decodificarMensaje(Object encodedString) {

        //convertimos de Base64 a String
        byte[] decodedBytes = Base64.getDecoder().decode((String) encodedString);
        //Aplicamos un reverse a la cadena
        StringBuilder decodedString = new StringBuilder(new String(decodedBytes)).reverse();
        //Separamos los elemntos de la cadena en un array.
        return decodedString.toString().split(";");
    }

    public static byte[] ByteTobyte(Byte[] Bytes) {

        byte[] bytes = new byte[Bytes.length];

        int i = 0;
        for (Byte b : Bytes) {
            bytes[i++] = b.byteValue();
        }

        return bytes;

    }

    public static Object codificarMensaje(String mensaje) {

        //Aplicamos el metodo reverse a la cadena
        StringBuilder builder = new StringBuilder(mensaje).reverse();
        //convertimos la cadena a base64 y retornamos
        return Base64.getEncoder().encodeToString(builder.toString().getBytes());
    }

    public static void main(String[] args) throws GeneralSecurityException {

        if (1 == 1) {
            
            //obtenerListadoTipoNotificaciones();
            int i=5;
            while(i>0){
             
                
                if(i==3){
                     try {
                        i=1/0;
                    } catch (Exception e) {
                         System.out.println(e.getMessage());
                         i--;
                         continue;
                    }
                }
                
                System.out.println("i=> "+i);
                i--;
                
            }
            
//            if(obtenerListadoTipoNotificaciones()){
//                System.out.println(getCodigoTipoNotificacion(Peticion.Accion.LISTADO_CLIENTES.name()));
//            };

        }

    }
    
    public static String stackTraceArrayToString(Throwable e){
        
        StringBuilder builder = new StringBuilder();
        
        builder.append(e.toString());
        
        for (StackTraceElement stackTraceElement : e.getStackTrace()) {
            builder.append("\r\n\tat ").append(stackTraceElement.toString());
        }
        
        if(e.getCause()!=null){
            builder.append("\r\ncause by: ").append(e.getCause().getMessage()).append("\r\n");
            builder.append(stackTraceArrayToString(e.getCause()));
        }
        
        return builder.toString();
    } 

    public static JsonObject getJsonObject(Object parObject) {
        if (parObject == null) {
            return null;
        }
        String toJson = convertidor.toJson(parObject);
        return getJsonObject(toJson);
    }

    public static JsonObject getJsonObject(String parString) {
        if (parString == null) {
            return null;
        }
        JsonElement fromJson = convertidor.fromJson(parString, JsonElement.class);
        return fromJson.getAsJsonObject();
    }

    public static String bytesArrayToString(byte[] data, int bytes) {

        StringBuilder dataString = new StringBuilder("");

        for (int i = 0; i < bytes; i++) {
            if (data[i] > 0) {
                dataString.append((char) data[i]);
            }
        }
        return dataString.toString();
    }

    public static byte[] StringToBytes(String str) {

        char[] chars = str.toCharArray();
        byte[] bytes = new byte[chars.length];

        for (int i = 0; i < chars.length; i++) {
            bytes[i] = (byte) chars[i];
        }
        return bytes;
    }

    public static String[] JsonToArray(JsonElement json) {
        String array[] = convertidor.fromJson(json, String[].class);
        return array;
    }

    public static String convertirStringBase64(String parStringConvertir, boolean parReverse) {
        String convertido;
        byte[] stringCodificar = null;

        StringBuilder sb = new StringBuilder(parStringConvertir);

        if (parReverse) {
            stringCodificar = sb.reverse().toString().getBytes();
        } else {
            stringCodificar = sb.toString().getBytes();
        }
        convertido = new String(Base64.getEncoder().encode(stringCodificar));
        return convertido;
    }
    
    public static String decodeStringBase64(String parStringConvertir, boolean parReverse) {
        String convertido;
        byte[] stringDecodificar = null;

        StringBuilder sb = new StringBuilder(parStringConvertir);

        if (parReverse) {
            stringDecodificar = sb.reverse().toString().getBytes();
        } else {
            stringDecodificar = sb.toString().getBytes();
        }
        convertido = new String(Base64.getDecoder().decode(stringDecodificar));
        return convertido;
    }

    public static ArrayList<NotificacionPendEntregar> getNotificacionPendienteEntregar(String cliente) {

        List<Filtro> filtros = new ArrayList<Filtro>();
        filtros.add(new Filtro("fechaEntregado", "filtro", "isnull()"));
        filtros.add(new Filtro("receptor", "filtro", cliente));

        return notificactionRestfulClient.getResfultFiltrado(filtros);

    }

    public static JsonObject postNotificacionPendienteEntregar(JsonObject notificacion, String destinatario) {
        
        //System.out.println("postNotificacionPendienteEntregar============= ");

        NotificacionPendEntregar notificacionPendEntregar = new NotificacionPendEntregar();
        notificacionPendEntregar.setCodigoAplicacionApp(Integer.valueOf(ClienteRestful.getCodigoAPP()));
        try {
            //notificacionPendEntregar.setContenido(Utils.StringToBytes(Utils.aes.getEncryptAES(notificacion.toString())));
            notificacionPendEntregar.setContenido(convertirStringBase64(Utils.aes.getEncryptAES(notificacion.toString()), false));
        } catch (GeneralSecurityException ex) {
            ServerLogger.log(stackTraceArrayToString(ex), Level.INFO, "logs\\ServerLog.log");
            //ex.printStackTrace();
        }
        
        if(notificacion.get("emisor")!=null){
            notificacionPendEntregar.setEmisor(notificacion.get("emisor").getAsString());
        }
        else{
            //System.out.println("nt => "+notificacion.getAsJsonObject("datos").getAsJsonObject("members").get("emisor").getAsString());
            notificacionPendEntregar.setEmisor(notificacion.getAsJsonObject("datos").getAsJsonObject("members").get("emisor").getAsString());
        }
        //String emisor = notificacion.get("emisor").getAsString()==null?:notificacion.get("emisor").getAsString();
        //System.out.println("emisor => "+emisor);
             
        notificacionPendEntregar.setFechaCreacion(getISODateString(new Date()));
        notificacionPendEntregar.setReceptor(destinatario);
        notificacionPendEntregar.setTipoNotificacion(getCodigoTipoNotificacion(notificacion.get("accion").getAsString()));
        
        //System.out.println("convirtiendo a json");
        JsonObject json = getJsonObject(notificacionPendEntregar);
        
        //System.out.println("notificacionpendienteEntregar => "+json.toString());
        ServerLogger.log("notificacionpendienteEntregar => "+json.toString(), Level.INFO, "logs\\ServerLog.log");

        Entity body = Entity.entity(json.toString(), "application/json; charset=UTF-8");

        //System.out.println("Body => " + json.toString());

        JsonObject IDTransaccion = notificactionRestfulClient.obtenerTransaccion();

        if (IDTransaccion != null) {
            //String uri = "/Facturacion/webAPI/notificacionPendienteEntregar";
            JsonObject postResponse = null;
            try {
                postResponse = notificactionRestfulClient.postResfult(body, IDTransaccion.get("IDTransaccion").getAsString());
            } catch (Exception e) {
                ServerLogger.log(stackTraceArrayToString(e), Level.INFO, "logs\\ServerLog.log");
                //e.printStackTrace();
            }
            

            if (postResponse != null) {

                if (postResponse.get("success") != null && postResponse.get("success").getAsBoolean() == false) {
                    notificactionRestfulClient. rollbackTransaccion(IDTransaccion.get("IDTransaccion").getAsString());
                    //System.out.println("Ocurrio un error al guardar la notificacion!");
                    ServerLogger.log("Ocurrio un error al guardar la notificacion!", Level.INFO, "logs\\ServerLog.log");
                    return postResponse;
                } else {
                    JsonObject commitResult = notificactionRestfulClient. commitTransaccion(IDTransaccion.get("IDTransaccion").getAsString());
                    return commitResult;
                }

            } else {
                //System.out.println("Ocurrio un error al guardar la notificacion!");
                ServerLogger.log("Ocurrio un error al guardar la notificacion!", Level.INFO, "logs\\ServerLog.log");
            }
        }
        return getJsonObject("{\"success\": false}");
    }
    
    public static JsonObject marcarNotificacionComoEntregada(NotificacionPendEntregar notificacion) throws GeneralSecurityException {
        
        notificacion.setFechaEntregado(getISODateString(new Date()));
        
        Entity body = Entity.entity(getJsonObject(notificacion).toString(), MediaType.APPLICATION_JSON);
        return notificactionRestfulClient.putResfult(body, null, new String[]{"fechaEntregado"});
    }

    public static String getISODateString(Date date) {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
        df.setTimeZone(tz);
        return df.format(date);
    }
    
    public static boolean obtenerListadoTipoNotificaciones() {
        
        Utils.listadoTipoNotificaciones =  tipoNotificactionRestfulClient.getResfultFiltrado(null);        
        if(listadoTipoNotificaciones!=null){return true;}else{return false;}
    }
    
    public static TipoNotificacionPendEntrega getCodigoTipoNotificacion(String tipoNotificacion){
        if(listadoTipoNotificaciones!=null){
            if(listadoTipoNotificaciones.size()>0){       
                
              List<TipoNotificacionPendEntrega> listadoFiltrado = listadoTipoNotificaciones.stream().filter(s -> s.getAccionAplicar().equals(tipoNotificacion)).collect(Collectors.toList());
              
              if(listadoFiltrado.size()>0){
                  return listadoFiltrado.get(0);
              }
            }
        }
        
        return null;
    }
    
    
    static <T> Consumer<T> throwingConsumerWrapper(ThrowingConsumer<T, Exception> throwingConsumer) {
        return i -> {
            try {
                throwingConsumer.accept(i);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        };
    }

}
