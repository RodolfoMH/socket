/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newsoft.ElCamareroSocketServer.Utilities;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import static com.newsoft.ElCamareroSocketServer.Utilities.Utils.convertirStringBase64;
import static com.newsoft.ElCamareroSocketServer.Utilities.Utils.getJsonObject;
import static com.newsoft.ElCamareroSocketServer.Utilities.Utils.stackTraceArrayToString;
import com.newsoft.newsoft.utils.helpers.JsfUtil;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import lombok.Data;

/**
 *
 * @author jmorel
 */
@Data
public class ClienteRestful<T> {
    
    private static String servidor = "http://192.168.1.70";
    private static String puerto = "8383";
    private static String codigoApp = "1";

    private static final String IDObtenerToken = "501";
    private static final String tokenAutorizacion = "UQX9REgr8Ao9UaskzhP8V19G103tn4FO";
    
    private String uriBase;
    private String endPoint;

    //private final Class<T> claseID;
    public final Class<T> type;
    //public Class<T> claseID = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];

    public ClienteRestful(String endpoint, Class<T> type) {
        this.endPoint = endpoint;
        
        String[] uri = endpoint.split("/");
        if(uri.length>=2){
            this.uriBase = uri[0] + "/" + uri[1];
        }
        this.type = type;
    }
    
    //setter
    public static void SetCodigoAPP(String codigo) {
        ClienteRestful.codigoApp = codigo;
    }

    public static void SetServidor(String servidor) {
        ClienteRestful.servidor = servidor;
    }

    public static void SetPuerto(String puerto) {
        ClienteRestful.puerto = puerto;
    }
    
    //getter
    public static String getCodigoAPP() {
        return ClienteRestful.codigoApp;
    }

    public static String getServidor() {
        return ClienteRestful.servidor;
    }

    public static String getPuerto() {
        return ClienteRestful.puerto;
    }

//    public static void main(String[] args) throws InterruptedException, GeneralSecurityException, IOException {
//         
//        StringBuilder b= new StringBuilder("JMOREL;1234;501;UQX9REgr8Ao9UaskzhP8V19G103tn4FO");
//        
//        byte[] stringCodificar = b.reverse().toString().getBytes();
//        String encode = new String(Base64.getEncoder().encode(stringCodificar));
//        
//        System.out.println(encode);
//        
//
//    }

    //Method
    public static Invocation.Builder asignarCredencialesTarget(Invocation.Builder parTarget, String IDTrasaccion, String[] atributosAdicionales) {
        String token = convertirStringBase64(IDObtenerToken + ":" + tokenAutorizacion, false);;
        MultivaluedMap<String, Object> headerAtributos = new MultivaluedHashMap<>();
        headerAtributos.add("Authorization", "Basic " + token);
        headerAtributos.add("Cache-Control", "no-cache");
        headerAtributos.add("Pragma", "no-cache");
        headerAtributos.add("Plataforma", "windows");
        if (IDTrasaccion != null) {
            headerAtributos.add("IDTransaccion", IDTrasaccion);
            ServerLogger.log("IDTransaccion=> " + IDTrasaccion, Level.INFO, "logs\\ServerLog.log");
            //System.out.println("IDTransaccion=> " + IDTrasaccion);
        }
        
        if(atributosAdicionales!=null){
            if(atributosAdicionales.length>0){
                
                StringBuilder builder = new StringBuilder();
                for (String atributo : atributosAdicionales) {
                    builder.append("("+atributo+")");
                }
                
                headerAtributos.add("AtributosIncluir", builder.toString());
            }
        }

        parTarget.headers(headerAtributos);

        //parTarget.header("Authorization", "Basic " + tokenAutorizacion);
        //System.out.println("asignarCredencialesTarget : " + tokenAutorizacion);
        
        return parTarget;
    }
    
    public static Invocation.Builder asignarCredencialesTarget(Invocation.Builder parTarget, String IDTrasaccion){
       return asignarCredencialesTarget(parTarget, IDTrasaccion, null);
    }

    public ArrayList<T> getResfultFiltrado(String uri, int desde, int hasta, List<Filtro> filtros) {

        javax.ws.rs.client.Client client = JsfUtil.getInstanceClientRestFul(false);

        String URI = servidor + ":" + puerto + "/" + uri;

        if (desde != -1 && hasta != -1) {
            URI += '/' + desde + '/' + hasta + '?';
        } else {
            URI += "?";
        }

        if (filtros != null) {
            int posicion = 0;
            for (Filtro filtro : filtros) {
                if (filtro.getValue() != null && !filtro.getValue().isEmpty()) {
                    if (posicion > 0) {
                        URI += '&';
                    } else {
                        posicion++;
                    }
                    URI += filtro.toString();
                }
            }
        }

        //System.out.println("URI: " + URI);
        ServerLogger.log("URI => " + URI, Level.INFO, "logs\\ServerLog.log");

        try {
            WebTarget target = client.target(URI);

            Invocation.Builder request = target.request(MediaType.APPLICATION_JSON);
            asignarCredencialesTarget(request, null);

            Response response = request.get(Response.class);

            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                ServerLogger.log("Response = " + response, Level.INFO, "logs\\ServerLog.log");
                //System.out.println("Response = " + response);

            }
            String retorno = response.readEntity(String.class);            
            ServerLogger.log("retorno ==> " + retorno, Level.INFO, "logs\\ServerLog.log");

            //System.out.println("retorno ==> " + retorno);
            try{
                return Utils.convertidor.fromJson(retorno, new ListaGenerica<T>(type));
            }catch(JsonSyntaxException e){
                //e.printStackTrace();
                ServerLogger.log(stackTraceArrayToString(e), Level.WARNING, "logs\\ServerLog.log");
                return null;
            }

        } catch (Exception e) {
            //e.printStackTrace();
            //System.err.println(stackTraceArrayToString(e));
            ServerLogger.log(stackTraceArrayToString(e), Level.WARNING, "logs\\ServerLog.log");
            return null;
        }
    }
    
    public ArrayList<T> getResfultFiltrado(int desde, int hasta, List<Filtro> filtros) {
        return getResfultFiltrado(endPoint, desde, hasta, filtros);
    }
    
    public ArrayList<T> getResfultFiltrado(String uri, List<Filtro> filtros) {
        return getResfultFiltrado(uri, -1,-1, filtros);
    }
    
    public ArrayList<T> getResfultFiltrado(List<Filtro> filtros) {
        return getResfultFiltrado(-1,-1, filtros);
    }
    
    public JsonObject postResfult(String uri, Entity entity, String IDTransaccion) {

        javax.ws.rs.client.Client client = JsfUtil.getInstanceClientRestFul(false);

        String URI = servidor + ":" + puerto +"/"+ uri;
        //System.out.println("URI: " + URI);
        ServerLogger.log("URI: " + URI, Level.INFO, "logs\\ServerLog.log");

        try {
            WebTarget target = client.target(URI);

            Invocation.Builder request = target.request(MediaType.APPLICATION_JSON);
            asignarCredencialesTarget(request, IDTransaccion);

            Response response = request.post(entity, Response.class);

            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                //System.out.println("Response = " + response);
                ServerLogger.log("Response = " + response, Level.INFO, "logs\\ServerLog.log");

            }
            String retorno = response.readEntity(String.class);
            //System.out.println("retorno ==> " + retorno);
            ServerLogger.log("Retorno ==> " + retorno, Level.INFO, "logs\\ServerLog.log");
            return getJsonObject(retorno);
            
        } catch (Exception e) {
            //e.printStackTrace();
            //System.err.println(stackTraceArrayToString(e));
            ServerLogger.log(stackTraceArrayToString(e), Level.INFO, "logs\\ServerLog.log");
            return null;
        }
    }
    
    public JsonObject postResfult(Entity entity, String IDTransaccion) {
        return postResfult(endPoint, entity, IDTransaccion);
    }
    
    public JsonObject putResfult(String uri, Entity entity, String IDTransaccion, String[] camposActualizar) {

        javax.ws.rs.client.Client client = JsfUtil.getInstanceClientRestFul(false);

        String URI = servidor + ":" + puerto +"/"+ uri;
        //System.out.println("URI: " + URI);
        ServerLogger.log("URI: " + URI, Level.INFO, "logs\\ServerLog.log");

        try {
            WebTarget target = client.target(URI);

            Invocation.Builder request = target.request(MediaType.APPLICATION_JSON);
            asignarCredencialesTarget(request, IDTransaccion, camposActualizar);

            Response response = request.put(entity, Response.class);

            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                //System.out.println("Response = " + response);
                ServerLogger.log("Response = " + response, Level.INFO, "logs\\ServerLog.log");

            }
            String retorno = response.readEntity(String.class);
            //System.out.println("retorno ==> " + retorno);
            ServerLogger.log("retorno ==> " + retorno, Level.INFO, "logs\\ServerLog.log");
            return getJsonObject(retorno);
            
        } catch (Exception e) {
            //e.printStackTrace();
            //System.err.println(stackTraceArrayToString(e));
            ServerLogger.log(stackTraceArrayToString(e), Level.INFO, "logs\\ServerLog.log");
            return null;
        }
    }
    
    public JsonObject putResfult(Entity entity, String IDTransaccion, String[] camposIncluir) {
        return putResfult(endPoint, entity, IDTransaccion, camposIncluir);
    }
    
    public JsonObject deleteResfult(String uri, String id ,String IDTransaccion) {

        javax.ws.rs.client.Client client = JsfUtil.getInstanceClientRestFul(false);

        String URI = servidor + ":" + puerto +"/"+ uri + "/"+id;
        //System.out.println("URI: " + URI);
        ServerLogger.log("URI: " + URI, Level.INFO, "logs\\ServerLog.log");

        try {
            WebTarget target = client.target(URI);

            Invocation.Builder request = target.request(MediaType.APPLICATION_JSON);
            asignarCredencialesTarget(request, IDTransaccion);

            Response response = request.delete(Response.class);

            if (response.getStatus() != Response.Status.OK.getStatusCode()) {
                //System.out.println("Response = " + response);
                ServerLogger.log("Response = " + response, Level.INFO, "logs\\ServerLog.log");

            }
            String retorno = response.readEntity(String.class);
            //System.out.println("retorno ==> " + retorno);
            ServerLogger.log("retorno ==> " + retorno, Level.INFO, "logs\\ServerLog.log");
            return getJsonObject(retorno);
            
        } catch (Exception e) {
            //e.printStackTrace();
            //System.err.println(stackTraceArrayToString(e));
            ServerLogger.log(stackTraceArrayToString(e), Level.INFO, "logs\\ServerLog.log");
            return null;
        }
    }
    
    public JsonObject deleteResfult(String id, String IDTransaccion){
        return deleteResfult(endPoint, id, IDTransaccion);
    }
    
    public JsonObject obtenerTransaccion() {

        String uri = uriBase + "/transacciones/transaccion";
        String body = "{\"timeOutTransaccion\": 300}";

        return postResfult(uri, Entity.entity(body, MediaType.APPLICATION_JSON), null);
    }

    public JsonObject commitTransaccion(String IDTransaccion) {

        String uri = uriBase+"/transacciones/transaccion/commit";
        String body = "{\"IDTransaccion\": " + IDTransaccion + ", \"liberarTransaccion\": true}";

        return postResfult(uri, Entity.entity(body, MediaType.APPLICATION_JSON), null);

    }

    public JsonObject rollbackTransaccion(String IDTransaccion) {

        String uri = uriBase+"/transacciones/transaccion/rollback";
        String body = "{\"IDTransaccion\": " + IDTransaccion + ", \"liberarTransaccion\": true}";

        return postResfult(uri, Entity.entity(body, MediaType.APPLICATION_JSON), null);

    }
    
    public JsonObject sendAutenticationRequest(String usuario, String clave, String idToken, String token) {

        StringBuilder sb = new StringBuilder(usuario + ";" + clave + ";" + idToken + ";" + token);
        //ServerLogger.log("Autenticacion data => "+sb.toString(), Level.INFO, "logs\\ServerLog.log");
        //System.out.println("uath data=>" + sb.toString());
        byte[] stringCodificar = sb.reverse().toString().getBytes();
        String encode = new String(Base64.getEncoder().encode(stringCodificar));
        
        //System.out.println("encode => "+encode);
        ServerLogger.log("Encode => "+encode, Level.INFO, "logs\\ServerLog.log");

        Entity entity = Entity.entity(encode, MediaType.TEXT_PLAIN);
        //String URI = "http://192.168.1.27:8383/Facturacion/webAPI/usuarioApp/autenticarUsuarioCamarero/1";
        String URI = uriBase + "/usuarioApp/autenticarUsuarioCamarero/" + codigoApp;
        return postResfult(URI, entity, null);
    }
    
}
