package com.newsoft.ElCamareroSocketServer.Entidades;

import com.newsoft.ElCamareroSocketServer.Utilities.Utils;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author jmorel
 */
@Data
public class Peticion implements Serializable {

    private Accion accion;
    private Object datos;
    private String emisor;
    private String nombreEmisor;
    private String fecha;
    private String[] destinatarios;
    private String idMensaje;

    public Peticion(Accion accion, Object datos,String idMensaje, String emisor, String nombreEmisor, String[] destinatarios, String fecha) {
        this.accion = accion;
        this.nombreEmisor = nombreEmisor;
        this.datos = datos;
        this.idMensaje = idMensaje;
        this.emisor = emisor;
        this.destinatarios = destinatarios;
        this.fecha = fecha;
        
    }
    
    public Peticion(Accion accion, Object datos, String emisor, String[] destinatarios){
        this(accion, datos,null, emisor, null, destinatarios,Utils.getISODateString(new Date()));
    }

    public Peticion(Accion accion, Object datos, String[] destinatarios) {
        this(accion, datos, null, destinatarios);
    }

    public Peticion() {
        this(Accion.NO_DEFINIDA, null, null, null);
    }

    public String[] getEmisorHowArray() {
        return new String[]{emisor};
    }

    public Accion getAccion() {
        return accion != null ? accion : Accion.NO_DEFINIDA;
    }

    public enum Accion {
        AUTENTICAR,
        DESCONECTAR,
        NOTIFICAR,
        OBTENER_COORDENADAS_POLARES,
        COORDENADAS_POLARES,
        NO_DEFINIDA,
        OBTENER_LISTADO_CLIENTES,
        LISTADO_CLIENTES,
        NOTIFICAR_PLATOS_LISTOS,
        PLATOS_LISTOS,
        MENSAJE_TEXTO,
        PROBAR_CONECCION,
        MENSAJE_ENTREGADO,
        REFRESCAR_MESAS_ABIERTAS
    }

    public static Accion getAccionFromString(String parAccion) {
        
        for (Accion value : Peticion.Accion.values()) {
            if(parAccion.equalsIgnoreCase(value.toString())){return value;}
        }
        
        return Accion.NO_DEFINIDA;

    }

    @Override
    public String toString() {
        return "Peticion{" + "accion=" + accion + ", datos=" + datos + ", emisor=" + emisor + ", nombreEmisor=" + nombreEmisor + ", fecha=" + fecha + ", destinatarios=" + destinatarios + '}';
    }


}
