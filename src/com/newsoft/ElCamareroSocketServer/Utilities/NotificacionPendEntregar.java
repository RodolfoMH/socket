/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newsoft.ElCamareroSocketServer.Utilities;

import java.io.Serializable;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author jmorel
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificacionPendEntregar implements Serializable{

    private Long codigoNotificacion;
    private String emisor;
    private String receptor;
    private String contenido;
    private String fechaCreacion;
    private String fechaEntregado;
    private Integer codigoAplicacionApp;
    //private AplicacionApp aplicacionApp;
    private TipoNotificacionPendEntrega tipoNotificacion;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codigoNotificacion != null ? codigoNotificacion.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof NotificacionPendEntregar)) {
            return false;
        }
        NotificacionPendEntregar other = (NotificacionPendEntregar) object;
        if ((this.codigoNotificacion == null && other.codigoNotificacion != null) || (this.codigoNotificacion != null && !this.codigoNotificacion.equals(other.codigoNotificacion))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.newsoft.newsoft.entidades.comun.NotificacionPendienteEnviar[ codigoNotificacion=" + codigoNotificacion +", aplicacionApp= "+codigoAplicacionApp +", emisor="+ emisor+", receptor="+ receptor+", contenido="+ contenido+", fechaCreacion="+ fechaCreacion+", fechaEntregado="+ fechaEntregado+", tipoNotificacion="+ tipoNotificacion+" ]";
    }
    
    public String getContenido(){
        return Utils.decodeStringBase64(contenido, false);
    }
    
}
