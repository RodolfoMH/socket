package com.newsoft.ElCamareroSocketServer.Socket;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.newsoft.ElCamareroSocketServer.Utilities.OnConexionclosed;
import com.newsoft.ElCamareroSocketServer.Utilities.OnMessageReceived;
import com.newsoft.ElCamareroSocketServer.Entidades.Peticion;
import com.newsoft.ElCamareroSocketServer.Server.Server;
import com.newsoft.ElCamareroSocketServer.Utilities.NotificacionPendEntregar;
import com.newsoft.ElCamareroSocketServer.Utilities.OnSocketLogged;
import com.newsoft.ElCamareroSocketServer.Utilities.ServerLogger;
import com.newsoft.ElCamareroSocketServer.Utilities.Utils;
import static com.newsoft.ElCamareroSocketServer.Utilities.Utils.stackTraceArrayToString;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;

/**
 *
 * @author jmorel
 */
/*
    Esta clase es la que contendra la instancia de cada cliente que se conecte al servidor de notificaciones
 */
public class SocketThread extends Thread {

    //Conexion TCP
    private Socket conexion;
    private boolean running = false;

    //Objetos I/O
    private DataInputStream in;
    private DataOutputStream out;

    //Esta interfaz contiene un evento que se ejecutara en el servidor al recibir un mensaje del cliente.
    private OnMessageReceived messageListener;
    //Esta interfaz contiene un evento que se ejecutara en el servidor al cerrarse la conexion ante un error o desconexion.
    private OnConexionclosed conexionClosedListener;
    //Esta interfaz contiene un evento que se ejecutara en el servidor al momento de que el usurio se haya autenticado exitosamente.
    private OnSocketLogged socketLoggedListener;

    //instancia del servidor, le permite al socket comunicarse con el servidor para obtener infomraciones, como el listado de conexiones
    private Server servidor;

    //ID del socket se utiliza para identificar cada socket dentro de la lista de sockets del servcidor 
    private String ID;
    private JsonObject userData;
    private boolean autenticado = false;
    
    public SocketThread(Socket conexion, Server servidor, OnMessageReceived onMessageReceived) {
        this.conexion = conexion;
        //this.logEntity = logentity;
        this.servidor = servidor;
        this.messageListener = onMessageReceived;
        
        getStreams();
    }
    
    public SocketThread(Socket conexion, Server servidor) {
        this(conexion, servidor, null);
    }
    
    public SocketThread(String ID) {
        this.ID = ID;
    }
    
    public boolean isRunning() {
        return running;
    }
    
    private void setID(String id) {
        this.ID = id;
    }
    
    public JsonObject getUserData() {
        if (userData == null) {
            return null;
        };
        return userData;
    }
    
    public void setUserData(JsonObject userData) {
        this.userData = userData;
    }
    
    public String getID() {
        return this.ID;
    }
    
    public OnMessageReceived getMessageListener() {
        return messageListener;
    }
    
    public void setOnMessageReceivedListener(OnMessageReceived messageListener) {
        this.messageListener = messageListener;
    }
    
    public void setOnConecxionClosedListener(OnConexionclosed conexionClosedListener) {
        this.conexionClosedListener = conexionClosedListener;
    }
    
    public void setOnSocketLoggedListener(OnSocketLogged socketLoggedListener) {
        this.socketLoggedListener = socketLoggedListener;
    }

    //Obtine los objetos relacionados al flujo de datos entre el cliente y el servidor.
    public void getStreams() {
        try {
            ServerLogger.log("Nueva conexion entrante.", Level.INFO, "logs\\ServerLog.log");
            ServerLogger.log("Obteniendo Objectos de Stream I/O", Level.INFO, "logs\\ServerLog.log");
            //System.out.println(logEntity+" => Obteniendo Objectos de Stream I/O");
            //ServerLogger.log(logEntity+" => Obteniendo Objectos de Stream I/O", Level.INFO, "../.logs\\ServerLog.log");
            //Stream de Salida obtenido.
            out = new DataOutputStream(conexion.getOutputStream());
            out.flush();
            //Stream de Entrda obtenido.
            in = new DataInputStream(conexion.getInputStream());
            //System.out.println(logEntity+" => Objetos I/O Strams obtenidos de ("+conexion.toString()+")");
            ServerLogger.log("Objetos I/O Strams obtenidos exitosamente de (" + conexion.toString() + ")", Level.INFO, "logs\\ServerLog.log");
            //ServerLogger.log(logEntity+" => Objetos I/O Strams obtenidos de ("+conexion.toString()+")", Level.INFO, "../.logs\\ServerLog.log");
        } catch (IOException e) {
            //System.err.println("Ocurrio un error al obtener los objetos Stream I/O");
            ServerLogger.log("Ocurrio un error al obtener los objetos Stream I/O", Level.WARNING, "logs\\ServerLog.log");
            ServerLogger.log(stackTraceArrayToString(e), Level.WARNING, "logs\\ServerLog.log");
        }
    }//End getStreams
    
    @Override
    public void run() {
        
        super.run();
        running = true;
        //System.out.println(logEntity+" => Conectado a ("+conexion.toString()+")");
        
        try {
            //ServerLogger.log(logEntity+" => Conectado a ("+conexion.toString()+")", Level.INFO, "../.logs\\ServerLog.log");
            // in this while we wait to receive messages from client (it's an infinite loop)
            // this while it's like a listener for messages
            JsonObject entrada;
            boolean connected = true;
            //cuando en cliente envia un '*' se cierra la conexion.
            while (conexion.isConnected() && connected) {

                //me quedo esperando a que llegue algun dato del cliente.
                int b = 0;
                StringBuilder sb = new StringBuilder();
                
                try {
                    //Recorro el buffer hasta que se detecte un error -1, el caracter 'EOF' (4) o se termine los bytes el buffer 
                    while ((b = in.read()) != -1 && b != 4) {
                        sb.append((char) b);
                        if (in.available() == 0) {
                            break;
                        }
                    }
                } catch (SocketException e) {
                    ServerLogger.log(e.getMessage(), Level.WARNING, "logs\\ServerLog.log");
                    break;
                }

                //verifico la cantidad de datos que llegaron
//                    int bytesCount = in.available();
//                    //creo un array de bytes donde se guardaran los datos, le sumo +1 para incluir el primer byte leido
//                    byte[] packet = new byte[bytesCount+1];
//                    //Leo los datos y los inserto en el array, debo desplazar el array 1 posicion para dejar la prmiera posicion del array basia para insetar el primer byte que se leyo.
//                    int bytes = in.read(packet,1,(bytesCount));
//                    
//                    packet[0]=(byte)b;
                //System.out.println(logEntity+" => Mensaje recibido de "+getID());
                ServerLogger.log("-------------------------------------------------------------", Level.INFO, "logs\\ServerLog.log");
                ServerLogger.log("Mensaje recibido de " + (getID() == null ? conexion.toString() : getID()), Level.INFO, "logs\\ServerLog.log");
                //System.out.println("bytes recividos=>"+bytes);
                ServerLogger.log("Bytes recividos => " + sb.length(), Level.INFO, "logs\\ServerLog.log");
                if (b == 4) {
                    ServerLogger.log("END OF TRANSMISSION CHARACTER FOUND => " + b, Level.INFO, "logs\\ServerLog.log");
                }

                //System.out.println(sb.toString());
//                    String str = Utils.aes.getDecryptAES(sb.toString());
//                    ServerLogger.log("Mensaje Desencriptado => "+str, Level.INFO, "logs\\ServerLog.log");                        
                if (b == -1) {
                    connected = false;
                    break;
                } else {

                    //Debo concatear el primer byte que se recibio el cual no se incluyo en el array debido a que ya se leyo.
                    //String str = Utils.bytesArrayToString(packet, packet.length);
                    //System.out.println("Mensaje Encriptado=> "+str);
                    ServerLogger.log("Mensaje Encriptado => " + sb.toString(), Level.INFO, "logs\\ServerLog.log");
                    //Desencriptando mensaje

                    //str = Utils.aes.getDecryptAES(str);
                    //ServerLogger.log("Mensaje Desencriptado => "+str, Level.INFO, "logs\\ServerLog.log");
                    try {
                        String str = Utils.aes.getDecryptAES(sb.toString());
                        ServerLogger.log("Mensaje Desencriptado => " + str, Level.INFO, "logs\\ServerLog.log");

                        //Convirtiendo a JSON
                        entrada = Utils.getJsonObject(str);
                        
//                        if (this.autenticado) {
//                            //System.out.println("Agregando cabecera al mensaje => "+getID());
//                            entrada.addProperty("emisor", getID());
//                        }

                        if (!autenticado && !entrada.get("accion").getAsString().equals(Peticion.Accion.AUTENTICAR.toString())) {
                            ServerLogger.log("Usuario no autenticado. Cerrando la conexion...", Level.INFO, "logs\\ServerLog.log");
                            //closeConexion();
                            connected=false;
                            break;
                        }else if(entrada != null){
                            ServerLogger.log("Procesando Mensaje", Level.INFO, "logs\\ServerLog.log");
                            entrada.addProperty("emisor", getID());
                            procesarMensaje(entrada, getUserData());
                        }
                        
//                        if (entrada != null) {
//                            //System.out.println("Procesando Entrada");
//                            ServerLogger.log("Procesando Mensaje", Level.INFO, "logs\\ServerLog.log");
//                            entrada.addProperty("emisor", getID());
//                            procesarMensaje(entrada, getUserData());
//                        }
                        
//                        if (!autenticado) {
//                            ServerLogger.log("Usuario no autenticado. Cerrando la conexion...", Level.INFO, "logs\\ServerLog.log");
//                            closeConexion();
//                            connected=false;
//                            break;
//                        }
                        
                    } catch (GeneralSecurityException | JsonSyntaxException e) {
                        ServerLogger.log(stackTraceArrayToString(e), Level.SEVERE, "logs\\ServerLog.log");
                    }

                    //System.out.println("JSON =>"+entrada);
                    //ServerLogger.log("JSON =>"+entrada, Level.INFO, "logs\\ServerLog.log");
                }
                
            }//End While
            //System.out.println(logEntity+" => Termino la conexion con ("+conexion.toString()+")");
            //ServerLogger.log(logEntity+" => Termino la conexion con ("+conexion.toString()+")", Level.WARNING, "logs\\ServerLog.log");
        } catch (IOException e) {
            //e.printStackTrace();
            //System.err.println(logEntity+" => Error:  "+stackTraceArrayToString(e));
            //System.out.println(logEntity+" => Termino la conexion con ("+conexion.toString()+")");
            
            ServerLogger.log(stackTraceArrayToString(e), Level.WARNING, "logs\\ServerLog.log"); //closeConexion();
            //e.printStackTrace();
            //ServerLogger.log(logEntity+" => Termino la conexion con ("+conexion.toString()+")", Level.WARNING, "logs\\ServerLog.log");
        } finally {
            if(conexion!=null) closeConexion();          
            //System.out.println(logEntity+" => Done.");
        }
        
    }//End Run

    /**
     * Method to send the messages from server to client
     *
     * @author Jose Rodolfo Morel
     * @param message the message sent by the server
     */
    public Boolean sendMessage(JsonObject salida) throws IOException, GeneralSecurityException {
        
        if (out != null && conexion.isConnected() && running) {
            //System.out.println(logEntity+" => Enviando mensaje => '"+salida+"' a "+conexion.toString()+" \nalias=>"+getID());
            ServerLogger.log("Enviando mensaje a => " + getID(), Level.INFO, "logs\\ServerLog.log"); //closeConexion();
            // Here you can connect with database or else you can do what you want with static message
            String encrytText = Utils.aes.getEncryptAES(salida.toString()) + (char) 4;
            out.write(Utils.StringToBytes(encrytText));
            out.flush();
            //System.out.println(logEntity+" => Mensaje Enviado!");
            ServerLogger.log("Mensaje Enviado exitosamente a " + getID(), Level.INFO, "logs\\ServerLog.log");
            return true;
        } else {
            ServerLogger.log("No se pudo enviar el mensaje a " + getID(), Level.WARNING, "logs\\ServerLog.log");
            throw new IOException("Exception message");
        }
        
    }//End sendMessage
    
    public Boolean sendMessage(String mensajeEncriptado) throws IOException {
        
        if (out != null && conexion.isConnected() && running) {
            //System.out.println(logEntity+" => Enviando mensaje => '"+mensajeEncriptado+"' a "+conexion.toString()+" \nalias=>"+getID());
            ServerLogger.log("Enviando mensaje a => " + getID(), Level.INFO, "logs\\ServerLog.log");
            // Here you can connect with database or else you can do what you want with static message
            //String encrytText = Utils.aes.getEncryptAES(salida.toString());
            out.write(Utils.StringToBytes(mensajeEncriptado + (char) 4));
            out.flush();
            //System.out.println(logEntity+" => Mensaje Enviado!");
            ServerLogger.log("Mensaje Enviado exitosamente a " + getID(), Level.INFO, "logs\\ServerLog.log");
            return true;
        } else {
            ServerLogger.log("No se pudo enviar el mensaje a " + getID(), Level.WARNING, "logs\\ServerLog.log");
            throw new IOException("Exception message");
        }
        
    }//End sendMessage
    
    public void procesarMensaje(JsonObject entrada, JsonObject userData) {
        if (entrada != null) {
            
            if (entrada.get("accion").getAsString().equals(Peticion.Accion.AUTENTICAR.toString())) {
                autenticar(entrada.get("datos").getAsString());
            } else if (entrada.get("accion").getAsString().equals(Peticion.Accion.DESCONECTAR.toString())) {
                closeConexion();
            } else {
                if (messageListener != null) {
                    // call the method messageReceived from ServerBoard class
                    //System.out.println("Asignando control al servidor...");
                    messageListener.messageReceived(entrada, userData);
                }
                
            }
            //if(mensaje.getAccion() == Peticion.Accion.OBTENER_COORDENADAS_POLARES){ obtenerCoordenadasPolares((DatosCoordenada)mensaje.getDatos());}  
            //if(mensaje.getAccion() == Peticion.Accion.NOTIFICAR){System.out.println("CLIENTE =>"+ mensaje.toString());}
        }
    }
    
    public synchronized void closeConexion() {
        try {
            //System.out.println(logEntity+" => Cerrando conexion!");
            ServerLogger.log("Cerrando conexion con " + (getID() == null ? conexion.toString() : getID()), Level.WARNING, "logs\\ServerLog.log");
            running = false;
            in.close();
            out.close();
            conexion.close();
            ServerLogger.log("Conexion con " + (getID() == null ? conexion.toString() : getID()) + " cerrada.", Level.WARNING, "logs\\ServerLog.log");
            //System.out.println(logEntity+" => Conecxion cerrada!");
            
            if (conexionClosedListener != null) {
                conexionClosedListener.conexionClosed(this);
            }
        } catch (Exception e) {
            ServerLogger.log(stackTraceArrayToString(e), Level.WARNING, "logs\\ServerLog.log");
            //e.printStackTrace();
        }
    }
    
    private void autenticar(Object mensaje) {

        //System.out.println("Iniciando auntenticacion...");
        ServerLogger.log("Iniciando auntenticacion de " + conexion.toString(), Level.INFO, "logs\\ServerLog.log");
        
        String[] datosAutenticacion = Utils.decodificarMensaje(mensaje);
        /*
            falta validar en la base de datos
         */
        
        if (autenticado && datosAutenticacion[0].equals(getID())) {
            
            //closeConexion();
        } else {
            JsonObject json = Utils.notificactionRestfulClient.sendAutenticationRequest(datosAutenticacion[0], datosAutenticacion[1], datosAutenticacion[2], datosAutenticacion[3]);
            if (json != null && json.get("success").getAsBoolean() == true) {
                setID(datosAutenticacion[0]);
                setUserData(json);
                autenticado = true;
                //System.out.println("Auntentificacion Success!");
                ServerLogger.log("Autenticacion exitosa.", Level.INFO, "logs\\ServerLog.log");

                //System.out.println("Verificando si existen notificaciones pendientes de entregar...");
                ServerLogger.log("Verificando si existen notificaciones pendientes de entregar a " + getID(), Level.INFO, "logs\\ServerLog.log");

                //socketLoggedListener.socketLogged(this);         
                verificarNotificacionesPendientes(datosAutenticacion[0]);               
                
            } else {
                ServerLogger.log("Auntentificacion Failed!", Level.WARNING, "logs\\ServerLog.log");   
                try {
                    conexion.close();
                } catch (IOException e){
                }
                //closeConexion();
            }
        }
        
    }
    
    private void verificarNotificacionesPendientes(String usuario) {
        ArrayList<NotificacionPendEntregar> notificaciones = Utils.getNotificacionPendienteEntregar(usuario);
        
        if (notificaciones != null && notificaciones.size() > 0) {
            //System.out.println("Existen notifiacciones Pendientes!");
            //System.out.println("Notificaciones pendientes => "+notificaciones.size());
            ServerLogger.log("Existen notificaciones Pendientes de entregar...", Level.INFO, "logs\\ServerLog.log");
            ServerLogger.log("Numero de notificaciones pendientes => " + notificaciones.size(), Level.INFO, "logs\\ServerLog.log");

            //System.out.println("Iniciando envio de las notificaciones pendientes...");
            ServerLogger.log("Iniciando envio de las notificaciones pendientes...", Level.INFO, "logs\\ServerLog.log");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    
                    int count = 0;
                    
                    while (count < notificaciones.size()) {
                        
                        try {
                            
                            ServerLogger.log("Enviando notificacion " + (count + 1) + " de " + notificaciones.size(), Level.INFO, "logs\\ServerLog.log");
                            boolean result = sendMessage(notificaciones.get(count).getContenido());
                            
                            if (result) {
                                //Hago el put
                                ServerLogger.log("Notificacion enviada exitosamente.", Level.INFO, "logs\\ServerLog.log");
                                ServerLogger.log("Marcando notificacion como enviada...", Level.INFO, "logs\\ServerLog.log");
                                //System.out.println("Marcando notificacion como enviada");
                                JsonObject resultPut = Utils.marcarNotificacionComoEntregada(notificaciones.get(count));
                                if (resultPut.get("fechaEntregado") != null) {
                                    
                                    ServerLogger.log("Notifiaccion actualizada exitosamente!", Level.INFO, "logs\\ServerLog.log");
                                    //System.out.println("Notifiaccion actualizada exitosamente!");
                                    
                                    if (notificaciones.get(count).getTipoNotificacion().getAccionAplicar().equals(Peticion.Accion.MENSAJE_TEXTO.toString())) {
                                        //System.out.println("Intentando notificar al emisor de la recepcion del mensaje...");
                                        ServerLogger.log("Intentando notificar al emisor de la recepcion del mensaje...", Level.INFO, "logs\\ServerLog.log");
                                        
                                        String[] destino = new String[]{notificaciones.get(count).getEmisor()};
                                        JsonObject mensaje = Utils.getJsonObject(Utils.aes.getDecryptAES(notificaciones.get(count).getContenido()));
                                        //System.out.println("Mensaje => "+mensaje.toString());
                                        Peticion notificacionEntregado = new Peticion(Peticion.Accion.MENSAJE_ENTREGADO, mensaje, destino);
                                        //System.out.println("Notificando al emisor, que se entrego el mensaje");
                                        if (servidor != null) {
                                            servidor.sendMensaje(destino, Utils.getJsonObject(notificacionEntregado));
                                        }
                                        
                                    }
                                    
                                } else {
                                    //System.out.println("Ocurrio un error al actualizar");
                                    ServerLogger.log("Ocurrio un error al marcar la notificacion como enviada.", Level.WARNING, "logs\\ServerLog.log");
                                }
                                count++;
                            } else {
                                break;
                            }

                            //Thread.sleep(700);
                        } catch (Exception e) {
                            //System.out.println("Ocurrio un error al enviar las notificaciones pendientes!");
                            ServerLogger.log("Ocurrio un error al enviar las notificaciones pendientes!", Level.WARNING, "logs\\ServerLog.log");
                            ServerLogger.log(stackTraceArrayToString(e), Level.SEVERE, "logs\\ServerLog.log");
                            break;
                        }                        
                        
                    }
                }
            }).start();
            
        } else {
            //System.out.println("No hay notifiacciones Pendientes!");
            ServerLogger.log("No hay notificaciones Pendientes de entregar", Level.INFO, "logs\\ServerLog.log");
        }
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.ID);
        return hash;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        if (getID() == null) {
            return false;
        }
       
        final SocketThread other = (SocketThread) obj;      
        if(other.getID() == null){
            return false;
        }
        //System.out.println("this getID => " + getID());
        //System.out.println("other getID => " + other.getID());

        return this.ID.equalsIgnoreCase(((SocketThread) obj).ID);
    }
    
    @Override
    public String toString() {
        return "SocketThread{" + "ID=" + ID + '}';
    }
    
}//End Class
