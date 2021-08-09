/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newsoft.ElCamareroSocketServer.Utilities;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author jmorel
 */
public class ServerLogger {
    
      Date fecha = new Date();
      private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy (hh-mm-ss a)");
      private final static Logger logger;
      
      static {
          System.setProperty("java.util.logging.SimpleFormatter.format","%1$td/%1$tm/%1$tY %1$tI:%1$tM:%1$tS %1$tp [%4$-7s] %5$s %n");
          logger = Logger.getLogger("com.newsoft.ElCamareroSocketServer.Server.ServerLogger");
      }

      public synchronized static void log(String mensaje, Level nivel){
          
        String file = "logs\\chats.log";
        
          try {
                //Verifico el tamaño del log y creo otro archivo 
                verificarSizeLog(file);

                //Handler para modificar el archivo.log
                Handler fileHandler = new FileHandler(file, true);

                // Para formatera el text de entrada, si no usamos esto el log estara en formato xml por defecto
                SimpleFormatter simpleFormatter = new SimpleFormatter();
                // Se especifica que formateador usara el manejador (handler) de archivo
                fileHandler.setFormatter(simpleFormatter);
                // Asignamos el handler previamente declarados al loggerpara que pueda almacenar en el archivo.
                logger.addHandler(fileHandler);
                 // Indicamos a partir de que nivel deseamos mostrar los logs, podemos especificar un nivel en especifico
                fileHandler.setLevel(Level.ALL);
                //envio el mensaje al log
                logger.log(nivel, mensaje);

                fileHandler.close();
        
          } catch (Exception e) {
              e.printStackTrace();
          }

      }
      
      public static void verificarSizeLog(String file){
          
          File log = new File(file);
     
          if(log.exists()){
              
              long size = log.length();
              //System.out.println("Size => "+size);
              //aproximadamente 2 mb
              if(size>2097000){
                  
                  String newFileName = file.replaceFirst("[.][^.]+$", "") +" "+ dtf.format(LocalDateTime.now()) + ".log";
                  File newFile = new File(newFileName);
                  
                  if(!newFile.exists()){
                      //System.out.println("Nombre sustituido");
                      log.renameTo(newFile);
                  }else{
                      
                  }                                 
              }
          }
      }
      
      public synchronized static void log(String mensaje, Level nivel, String file) {
       
          try {
                verificarSizeLog(file);
                //Handler para modificar el archivo.log
                Handler fileHandler = new FileHandler(file, true);
                // Para formatera el text de entrada, si no usamos esto el log estara en formato xml por defecto
                SimpleFormatter simpleFormatter = new SimpleFormatter();
                // Se especifica que formateador usara el manejador (handler) de archivo
                fileHandler.setFormatter(simpleFormatter);
                // Asignamos el handler previamente declarados al loggerpara que pueda almacenar en el archivo.
                logger.addHandler(fileHandler);
                 // Indicamos a partir de que nivel deseamos mostrar los logs, podemos especificar un nivel en especifico
                fileHandler.setLevel(Level.ALL);
                //envio el mensaje al log
                logger.log(nivel, mensaje);
                fileHandler.close();

          } catch (Exception e) {
              e.printStackTrace();
          }
      }
}
