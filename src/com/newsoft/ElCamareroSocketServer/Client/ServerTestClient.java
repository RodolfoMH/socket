/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newsoft.ElCamareroSocketServer.Client;

import com.google.gson.JsonObject;
import com.newsoft.ElCamareroSocketServer.Entidades.DatosPlato;
import com.newsoft.ElCamareroSocketServer.Entidades.Peticion;
import com.newsoft.ElCamareroSocketServer.Utilities.ServerLogger;
import com.newsoft.ElCamareroSocketServer.Utilities.Utils;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.security.GeneralSecurityException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jmorel
 */
public class ServerTestClient {
    
    private String HOST = "192.168.1.27";
    private int PORT = 5000;//Puerto por defecto.

    private Socket servidor;
    private Thread hilo;

    private DataOutputStream out;
    private DataInputStream in;

    public ServerTestClient() {
    }

    public ServerTestClient(String host, int port) {
        this.PORT = port;
        this.HOST = host;
    }

    public String getHOST() {
        return HOST;
    }

    public void setHOST(String HOST) {
        this.HOST = HOST;
    }

    public int getPORT() {
        return PORT;
    }

    public void setPORT(int PORT) {
        this.PORT = PORT;
    }

    public String sendMensajeAutenticacion(String parUsuario, String parPassword) {

        try {
            //String
            System.out.println("ENVIANDO");
            String cadena = parUsuario + ";" + parPassword + ";501;UQX9REgr8Ao9UaskzhP8V19G103tn4FO";
            Object encode = Utils.codificarMensaje(cadena);
            Peticion peticion = new Peticion(Peticion.Accion.AUTENTICAR, encode, parUsuario, new String[]{"all"});
            sendMessage(Utils.getJsonObject(peticion));
        } catch (Exception ex) {
            return ex.getMessage();
        }
        return "todo bien";
    }

    public static void main(String[] args) throws InterruptedException {

        ServerTestClient cliente = new ServerTestClient();
        
        if(args!=null){
            cliente.initConexion(args[0], args[1], args[2],Integer.parseInt(args[3]));
            //cliente.initConexion("RVALENZUELA", "Ns12345678$", "ov.centroespanol.com", 50519);
            //cliente.initConexion("RVALENZUELA", "1234", "ov.centroespanol", 5059);
        }else{
             //cliente.initConexion("JMORE", "Ns12345678$", "ov.centroespanol.com", 50519);
             cliente.initConexion("RVALENZUELA", "Ns12345678$", "ov.centroespanol.com", 50519);
        }
        //cliente.initConexion(args[0], args[1], args[2],Integer.parseInt(args[3]));
        //cliente.initConexion("RVALENZUELA", "Ns12345678$", "ov.centroespanol.com", 50519);
       
        //Thread.sleep(3000);
        //cliente.enviarMensaje(Peticion.Accion.MENSAJE_TEXTO.toString(), "RVALENZUELA", new String[]{"AVASQUEZ"}, "KLK, COMO ESTAS");
        //Thread.sleep(1000);

//        DatosPlato[] platos = new DatosPlato[]{
//            new DatosPlato(10L, "Espagueti","Jose",1L),
//            new DatosPlato(10L, "Macarrones","Miguel",4L),
//            new DatosPlato(10L, "Filete Encebollado","Raul",0L),
//            //new DatosPlato(10L, "Chicharrones de pollo","Rudelvis",3L),
//        };
        //cliente.enviarMensaje("OBTENER_LISTADO_CLIENTES", "RVALENZUELA", new String[]{"RVALENZUELA"}, "");
        //cliente.enviarMensaje("NOTIFICAR_PLATOS_LISTOS", "RVALENZUELA", new String[]{"AVASQUEZ"}, platos);
        //cliente.enviarMensaje("NOTIFICAR_PLATOS_LISTOS", "RVALENZUELA", new String[]{"AVASQUEZ"}, platos);
        //cliente.enviarMensaje("NOTIFICAR_PLATOS_LISTOS", "RVALENZUELA", new String[]{"AVASQUEZ"}, platos);
        //cliente.enviarMensaje(Peticion.Accion.OBTENER_LISTADO_CLIENTES.toString(), "RVALENZUELA", new String[]{"RVALENZUELA"}, "Desconectame ");
        //Thread.sleep(2000);
         //cliente.enviarMensaje(Peticion.Accion.MENSAJE_TEXTO.toString(), "AVASQUEZ", new String[]{"JMOREL"}, "Text1");
        //cliente.enviarMensaje("DESCONECTAR", "JMOREL", new String[]{"JMOREL"}, "Desconectame");
       // cliente.closeConexion();
        
        Scanner sc = new Scanner(System.in);
        do {
            //System.out.print("Destinatario=> ");
            //String destino = sc.nextLine();
            System.out.println("Ingrese un mensaje =>");
            String mensaje = sc.nextLine();
            cliente.enviarMensaje(Peticion.Accion.MENSAJE_TEXTO.toString(), "RVALENZUELA", new String[]{"JMOREL"}, mensaje);
            //cliente.enviarMensaje("NOTIFICAR_PLATOS_LISTOS", "RVALENZUELA", new String[]{"AVASQUEZ"}, platos);
        } while (true); 
//
//        String[] datos = args != null && args.length > 0 ? args : new String[]{"Hola 1", "Hola 2", "Hola 3"};
//
//        Peticion pt = new Peticion(Peticion.Accion.NOTIFICAR, datos, "AVASQUEZ", new String[]{"RVALENZUELA"});
//        cliente.sendMessage(Utils.getJsonObject(pt));

    }

    public void enviarMensaje(String parAccion, String emisor, String[] receptores, Object mensaje) {
        try {

            Peticion pt = new Peticion(Peticion.getAccionFromString(parAccion), mensaje, emisor, receptores);
            this.sendMessage(Utils.getJsonObject(pt));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // cliente.closeConecxion();
    }

    public void initConexion(String parUsuario, String parPassword, String parServidor, int parPuerto) {

        hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Cliente => iniciando cliente...");
                    servidor = new Socket(parServidor, parPuerto);
                    getStream();

                    JsonObject entrada;
                    sendMensajeAutenticacion(parUsuario, parPassword);
                    while (servidor.isConnected()) {

//                        int b = in.read();
//                        byte[] packetData = new byte[in.available()];
//                        int bytes = in.read(packetData);
                        //String str = in.readFully(b);
                        //entrada = new JSONObject(str);
                        
                        int b=0;
                        StringBuilder sb = new StringBuilder();
                    
                        try {

                            while((b=in.read())!=-1 && b != 4){
                                sb.append((char)b);
                                if(in.available()==0){
                                    break;
                                }
                            }
                            
                            //System.out.println("b => "+b);
                         
                        } catch (SocketException e) {
                            ServerLogger.log(e.getMessage(), Level.WARNING, "logs\\ServerLog.log");
                            break;
                        }
                        //System.out.println("BS========> " + sb);

                        System.out.println("CLIENTE => Mensaje recibido de (" + servidor.toString() + ")");
                        System.out.println("bytes recividos=>" + sb.length());
                        if(b==4){System.out.println("ENF OF TEXT CHAR FOUND => "+b);}

                        if (b == -1) {
                            closeConexion();
                            break;
                        } else {
                           // String txtDecryt = Utils.aes.getDecryptAES( ((char)(byte)b) + Utils.bytesArrayToString(packetData, bytes));
                            String txtDecryt = Utils.aes.getDecryptAES(sb.toString());
                            entrada = Utils.getJsonObject(txtDecryt);
                            System.out.println("Mensaje recibido =>" + entrada);
                            System.out.println("Procesando Entrada");
                            procesarMensaje(entrada);
                        }

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (GeneralSecurityException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        hilo.start();

    }

    public void procesarMensaje(JsonObject entrada) {
        if (entrada != null) {
            if (entrada.get("accion").getAsString().equals(Peticion.Accion.AUTENTICAR.toString())) {
                //  sendMensajeAutenticacion();
            }
            if (entrada.get("accion").getAsString().equals(Peticion.Accion.DESCONECTAR.toString())) {
                closeConexion();
            }
            if (entrada.get("accion").getAsString().equals(Peticion.Accion.LISTADO_CLIENTES.toString())) {
                System.out.println(entrada.get("datos"));
            }
            if (entrada.get("accion").getAsString().equals(Peticion.Accion.COORDENADAS_POLARES.toString())) {
                System.out.println(entrada.get("datos"));
            }
            if (entrada.get("accion").getAsString().equals(Peticion.Accion.NOTIFICAR.toString())) {
                System.out.println(entrada.get("datos"));
            }
            if (entrada.get("accion").getAsString().equals(Peticion.Accion.MENSAJE_TEXTO.toString())) {
                //System.out.println(entrada.get("datos"));
                System.out.println("Mensaje => "+entrada.get("datos"));
            }
        }
    }

    public void sendMessage(JsonObject salida) {
        if (out != null & servidor != null) {
            try {

                System.out.println("CLIENTE => Enviando mensaje => '" + salida + "' a " + servidor.toString());
                // Here you can connect with database or else you can do what you want with static message  
                System.out.println("MSG=>" + salida);
                out.flush();
                String txtEncrypt = Utils.aes.getEncryptAES(salida.toString()) + (char)4;
                out.write(Utils.StringToBytes(txtEncrypt));
                out.flush();
                System.out.println("CLIENTE => Mensaje Enviado!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Aun no se ha iniciado la conexion!");
        }
    }

    public void getStream() {
        try {
            System.out.println("CLIENTE => Obteniendo Objectos de Stream I/O");
            //Stream de Salida obtenido.
            out = new DataOutputStream(servidor.getOutputStream());
            out.flush();
            //Stream de Entrda obtenido.
            in = new DataInputStream(servidor.getInputStream());
            System.out.println("CLIENTE => Objetos I/O Strams obtenidos de (" + servidor.toString() + ")");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConexion() {
        try {
            servidor.close();
            in.close();
            out.close();
            servidor = null;
            in = null;
            out = null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
}
