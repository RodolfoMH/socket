/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newsoft.ElCamareroSocketServer.Server;

import com.newsoft.ElCamareroSocketServer.Utilities.ClienteRestful;
import com.newsoft.ElCamareroSocketServer.Utilities.ServerLogger;
import com.newsoft.ElCamareroSocketServer.Utilities.Utils;
import java.io.IOException;
import java.util.logging.Level;

/**
 *
 * @author jmorel
 */
public class ServiceLauncher {
    
    private static Server servidor;
    private static ServerLogger logger = new ServerLogger();
    
    public static void startServer(final String args[]) throws IOException{

        //System.out.println("Iniciado...");
        
        try 
        {
            ServerLogger.log("Iniciando configuracion de los parametros...", Level.INFO, "logs\\ServerLog.log");
            Server servidor;
            if(args!=null && args.length>=4){
                //System.out.println("Iniciando configuracion de los parametros...");
                ClienteRestful.SetCodigoAPP(args[2]);
                ClienteRestful.SetServidor(args[3]);
                ClienteRestful.SetPuerto(args[4]);
                
                servidor = new Server(Integer.parseInt(args[1]));
                //System.out.println("Obteniendo listado con los tipos de Notificaciones de la base de datos...");
            }else{
                servidor = new Server(5000);                 
//                if(Utils.obtenerListadoTipoNotificaciones()){
//                    System.out.println("Listado obtenido exitosamente!");
//                }
//                servidor.start();            
            }
            
            ServerLogger.log("Obteniendo listado con los tipos de Notificaciones de la base de datos...", Level.INFO, "logs\\ServerLog.log");
            if(Utils.obtenerListadoTipoNotificaciones()){
                    //System.out.println("Listado obtenido exitosamente!");
                    //System.out.println("configuracion de los parametros Finalizada");
                    ServerLogger.log("Listado obtenido exitosamente!", Level.INFO, "logs\\ServerLog.log");
                    ServerLogger.log("configuracion de los parametros Finalizada", Level.INFO, "logs\\ServerLog.log");
                    
                    servidor.start();               
                }else{
                    ServerLogger.log("Ocurrio un error al conectarse a la base de datos.", Level.WARNING, "logs\\ServerLog.log");
                    ServerLogger.log("Deteniendo Servidor...", Level.WARNING, "logs\\ServerLog.log");
                }
        } catch (Exception e) {
            //e.printStackTrace();
            ServerLogger.log(Utils.stackTraceArrayToString(e), Level.SEVERE, "logs\\ServerLog.log");
        }
        
    }
        
        
    public static void stopServer(final String args[]) throws IOException {

        ServerLogger.log("Deteniendo Servidor...", Level.INFO, "logs\\ServerLog.log");
        System.exit(0);
    }

    
    /**
     * Este método inicia el servidor de notificaciones desde los servicios de windows.
     * 
     * @param args Aqui se envian un array de comandos al servidor, para iniciar el servidor 
     * se deben enviar 4 parametros en total ["start","Puerto a escuchar", "CodigoApp", "Servidor Restful", "Puerto Servidor Restful"]
     * ejemplo: ["start","5000","1","http://192.168.1.27","8383"]
     * para detener el servicio solo hay que mandar 1 parametro ["stop"].
     */
    public static void main(String[] args) throws InterruptedException, IOException {
        
        String command = "start";
        //String[] args2 = new String[]{"start","5000","1","http://192.168.1.27","8383"};
        //args = new String[]{"start","5025","1","http://192.168.1.70","8383"};
        if(args!=null && args.length>0){
            command = args[0];
            
        }
        
        if(command.equals("start")){
            if(args.length>=5){ServerLogger.log("Comando: "+args[0] +", Puerto: "+args[1]+", CodigoApp: "+args[2]+", Servidor: "+args[3]+", Puerto Servidor: "+args[4], Level.INFO, "logs\\ServerLog.log");}
            ServerLogger.log("Iniciando servidor de notificaciones...", Level.INFO, "logs\\ServerLog.log");
            startServer(args);
         
        }else if(command.equals("stop")){
            stopServer(args);
        }
    }
    
}//End Class
