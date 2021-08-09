package com.newsoft.ElCamareroSocketServer.Server;

import com.google.gson.JsonObject;
import com.newsoft.ElCamareroSocketServer.Socket.SocketThread;
import com.newsoft.ElCamareroSocketServer.Utilities.ServerLogger;
import com.newsoft.ElCamareroSocketServer.Utilities.OnConexionclosed;
import com.newsoft.ElCamareroSocketServer.Utilities.OnMessageReceived;
import com.newsoft.ElCamareroSocketServer.Entidades.Peticion;
import com.newsoft.ElCamareroSocketServer.Utilities.Utils;
import static com.newsoft.ElCamareroSocketServer.Utilities.Utils.stackTraceArrayToString;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author jmorel
 */
public class Server {

    private ServerSocket servidor;
    private int PORT = 5000;//Puerto por defecto.

    private ArrayList<SocketThread> sockets;

    private Thread waitConnections;
    //private ServerLogger logger;
    
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy hh:mm:ss a"); 

    public Server(int PORT) {
        this.PORT = PORT;
        //this.logger = new ServerLogger();
        sockets = new ArrayList<SocketThread>();

        waitConnections = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    initServer();
                } catch (IOException ex) {
                    ServerLogger.log(stackTraceArrayToString(ex), Level.SEVERE, "logs\\ServerLog.log");
                }
            }
        });
    }

    public void start() {
        waitConnections.start();
    }
    
    public void stop() throws IOException{
        
        synchronized (Server.class)
        {
            if (servidor != null)
            {
                try 
                {
                    servidor.close();
                    ServerLogger.log("Deteniendo el Servidor...", Level.WARNING, "logs\\ServerLog.log");
                } catch (IOException e) {
                    ServerLogger.log(stackTraceArrayToString(e), Level.SEVERE, "logs\\ServerLog.log");
                }
            }
        }
    }

    private void initServer() throws IOException {

        try {
            //System.out.println("SERVER => iniciando servidor...");

            //iniciando el servidor
            servidor = new ServerSocket(PORT);
            
            ServerLogger.log("Esperando conexiones entrantes...", Level.INFO, "logs\\ServerLog.log");

            while (!servidor.isClosed()) {
                
                //System.out.println(servidor.isClosed());
                
                try {
                Socket cliente;
                //Aqui esperamos hasta que el servidor reciba un conexion de un cliente
                //System.out.println("SERVER => Esperando conexion...");
                cliente = servidor.accept();
                //nos ponemos a escucha al cliente en un hilo.
                
                ServerLogger.log("=============================================================", Level.INFO, "logs\\ServerLog.log");
                ServerLogger.log("Se establecio una nueva conexion.", Level.INFO, "logs\\ServerLog.log");
                ServerLogger.log("=============================================================", Level.INFO, "logs\\ServerLog.log");
                SocketThread hilo = new SocketThread(cliente, this);
                //System.out.println("SERVER => Conecxion establecida!");

                
                //Asigno al hilo un evennto que eschuche al servidor y envie una respuesta.
                hilo.setOnMessageReceivedListener(new OnMessageReceived() {
                    @Override
                    public void messageReceived(JsonObject entrada, JsonObject userData) {
                        if (entrada != null) {
                            
                            if (entrada.get("accion").getAsString().equals(Peticion.Accion.OBTENER_LISTADO_CLIENTES.toString())) {
                                
                                ServerLogger.log("Numero de clientes conectados => "+sockets.size(), Level.INFO, "logs\\ServerLog.log");
                                ServerLogger.log("Obteniendo listado de clientes...", Level.INFO, "logs\\ServerLog.log");
                                ArrayList<String[]> clientes = new ArrayList<>();
                                
                                for (SocketThread socketThread : sockets) {
                                    
                                    if(socketThread.getID() != null && socketThread.getUserData() != null ){
                                        clientes.add(new String[]{socketThread.getID(),socketThread.getUserData().get("nombreUsuario").getAsString()});
                                    }
                                    
                                }
                                ServerLogger.log("Tamaño del listado => "+clientes.size(), Level.INFO, "logs\\ServerLog.log");
                                Peticion response = new Peticion(Peticion.Accion.LISTADO_CLIENTES, clientes, "SERVER", Utils.JsonToArray(entrada.get("destinatarios")));

                                ServerLogger.log(Utils.getJsonObject(response).toString(), Level.INFO, "logs\\ServerLog.log");                  
                                sendMensaje(Utils.JsonToArray(entrada.get("destinatarios")), Utils.getJsonObject(response));
                            } else if (entrada.get("accion").getAsString().equals(Peticion.Accion.MENSAJE_TEXTO.toString())) {
 
                                ServerLogger.log("Procesando mensaje de texto...", Level.INFO, "logs\\ServerLog.log");

                                Peticion response = new Peticion(
                                        Peticion.Accion.MENSAJE_TEXTO, 
                                        entrada.get("datos").getAsString(), 
                                        entrada.get("idMensaje")!= null?entrada.get("idMensaje").getAsString():null,
                                        entrada.get("emisor").getAsString(), 
                                        userData.get("nombreUsuario").getAsString(),
                                        Utils.JsonToArray(entrada.get("destinatarios")),
                                        Utils.getISODateString(new Date())
                                );
                                
                                String jsonMensaje =
                                        "{"
                                        + "\"fecha\":\""+ dtf.format(LocalDateTime.now()) +"\", "
                                        + "\"emisor\": \""+ entrada.get("emisor").getAsString() +"\" "
                                        + "\"receptor\": \""+ entrada.get("destinatarios") +"\" "
                                        + "\"mensaje\": \""+ entrada.get("datos").getAsString() +"\" "
                                        +"}";
                                ServerLogger.log(jsonMensaje, Level.INFO);
                                sendMensaje(Utils.JsonToArray(entrada.get("destinatarios")), Utils.getJsonObject(response));
                                
                            }else{
                                //NOTIFICAR, REFRESCAR_MESAS_ABIERTAS, NO_DEFINIDA, PLATOS_LISTOS, OBTENER_COORDENADAS_POLARES,COORDENADAS_POLARES, 
                                if(entrada.get("destinatarios")!=null){
                                    sendMensaje(Utils.JsonToArray(entrada.get("destinatarios")), entrada);
                                }
                            }
                        }
                    }
                });

                hilo.setOnConecxionClosedListener(new OnConexionclosed() {
                    @Override
                    public void conexionClosed(SocketThread socket) {
                        sockets.remove(socket);
                        //System.out.println("SERVER => Socket removido de la lista de conexiones!");
                        ServerLogger.log("Se removio el Socket de la lista de conexiones del servidor!", Level.WARNING, "logs\\ServerLog.log");
                    }
                });
                
                sockets.add(hilo);
                hilo.start();
                } catch (Exception e) {
                    //System.err.println("Ocurrio un error con la conecxion entrante.");
                    ServerLogger.log(stackTraceArrayToString(e), Level.SEVERE, "logs\\ServerLog.log");
                    //e.printStackTrace();
                }
                
            }
        } catch (IOException ex) {
            //System.err.println("Ocurrio un error en el servidor.");          
            ServerLogger.log(stackTraceArrayToString(ex), Level.SEVERE, "logs\\ServerLog.log");
            //ex.printStackTrace();
        }

    }

    public void sendBroadcast(JsonObject entrada) {
        if (sockets.size() >= 1) {
            for (SocketThread socket : sockets) {
                try {
                    SocketThread other = new SocketThread(entrada.get("emisor").getAsString());
                    if (!socket.equals(other)) {
                       boolean enviado = socket.sendMessage(entrada);
                       
                       if(enviado && entrada.get("accion").getAsString().equals(Peticion.Accion.MENSAJE_TEXTO.toString())){
                            String[] destino = new String[]{entrada.get("emisor").getAsString()};
                            Peticion notificacionEntregado = new Peticion(Peticion.Accion.MENSAJE_ENTREGADO,entrada.toString(),destino);
                                                  
                            sendMensaje(destino, Utils.getJsonObject(notificacionEntregado));
                        }
                    }
                } catch (Exception e) {
                    
                    //System.out.println("No se pudo enviar el mensaje a " + socket.toString());
                    ServerLogger.log("No se pudo enviar el mensaje a " + socket.getID(), Level.WARNING, "logs\\ServerLog.log");
                    ServerLogger.log(stackTraceArrayToString(e), Level.SEVERE, "logs\\ServerLog.log");
                    
                    
                    //System.out.println("Intentando agregar el mensaje a la pila de notificaciones pendientes de entregar ...");   
                    ServerLogger.log("Intentando agregar el mensaje a la pila de notificaciones pendientes de entregar ...", Level.INFO, "logs\\ServerLog.log");
                    guardarNotificacionPendienteEntregar(entrada, socket.getID());       
                }
            }
        } else {
            //System.out.println("No hay conexiones establecidas!");
            ServerLogger.log("No se pudo enviar el broadcast, ya que no hay clientes conectados", Level.WARNING, "logs\\ServerLog.log");
        }
    }

    public void sendMensaje(String[] clientes, JsonObject entrada) {
        if (sockets.size() >= 1 && clientes.length >= 1) {
            if (clientes[0].equals("all")) {
                ServerLogger.log("Iniciando Broadcast...", Level.INFO, "logs\\ServerLog.log");
                //System.out.println("Iniciando Broadcast...");
                sendBroadcast(entrada);
            } else {
                
                for (String cliente : clientes) {                                         
                     
                    //final SocketThread other = ;
                    //System.out.println("verify contains");
                    //System.out.println("socket value => "+other.toString());
                    //System.out.println("contains "+sockets.contains(other));
                    if (sockets.contains(new SocketThread(cliente))) {                        

                        sockets.stream().filter( s -> s.getID()==null?false: s.getID().equalsIgnoreCase(cliente))
                        .forEach((entry) -> 
                        {
                            try {
                               boolean enviado =  entry.sendMessage(entrada);                                
                               
                               if(enviado && entrada.get("accion").getAsString().equals(Peticion.Accion.MENSAJE_TEXTO.toString())){
                                   
                                   ServerLogger.log("Notificando al emisor que el mensaje fue entregado...", Level.INFO, "logs\\ServerLog.log");
                                   String[] destino = new String[]{entrada.get("emisor").getAsString()};
                                   Peticion notificacionEntregado = new Peticion(Peticion.Accion.MENSAJE_ENTREGADO,entrada,destino);
                                   
                                   sendMensaje(destino, Utils.getJsonObject(notificacionEntregado));
                               }
                               
                            } catch (Exception e) {
                                ServerLogger.log("Error al enviar el mensaje a "+cliente, Level.WARNING, "logs\\ServerLog.log");
                                ServerLogger.log("Intentando agregar mensaje a la pila de notificaciones pendientes de entregar ...", Level.INFO, "logs\\ServerLog.log");
                                //System.out.println("Error al enviar el mensaje.");
                                //System.out.println("Agregando mensaje a la pila de notificaciones pendientes de entregar ...");                        
                                guardarNotificacionPendienteEntregar(entrada, cliente);  
                            }

                        });


                    } else {
                        //System.out.println("cliente: " + cliente + " no encontrado");
                        //System.out.println("Agregando mensaje a la pila de notificaciones pendientes de entregar ...");
                        
                        
                        ServerLogger.log("cliente: " + cliente + " no encontrado", Level.INFO, "logs\\ServerLog.log");
                        ServerLogger.log("Intentando agregar mensaje a la pila de notificaciones pendientes de entregar ...", Level.INFO, "logs\\ServerLog.log");
                        
                        guardarNotificacionPendienteEntregar(entrada, cliente);                                                
                        
                    }
                }
            }

        } else {
            //System.out.println("No se pudo enviar el mensaje, no hay ninguna conexion o puede que la lista de receptores este vacia!");
            ServerLogger.log("No se pudo enviar el mensaje, no hay clientes conectados o puede que la lista de receptores este vacia!", Level.INFO, "logs\\ServerLog.log");
        }
    }
    
    public void guardarNotificacionPendienteEntregar(JsonObject notificacion, String destinatario){
        
        //System.out.println("Destinatario => "+destinatario);
        //System.out.println("json => "+notificacion.toString());
        try
        {                                    
            JsonObject result =  Utils.postNotificacionPendienteEntregar(notificacion, destinatario);
            if(result.get("success")==null){
                //System.out.println("Se guardo la notificacion correctamente.");
                ServerLogger.log("Se guardo la notificacion correctamente.", Level.INFO, "logs\\ServerLog.log");
            }
        }
        catch(Exception e)
        {
            //System.out.println("Ocurrio un error al guardar la notificacion en la base de datos...");
            //System.out.println(stackTraceArrayToString(e));
            ServerLogger.log("Ocurrio un error al guardar la notificacion en la base de datos...", Level.WARNING, "logs\\ServerLog.log");
            ServerLogger.log(stackTraceArrayToString(e), Level.SEVERE, "logs\\ServerLog.log");
            //e.printStackTrace();
        }
    }

}
