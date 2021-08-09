/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newsoft.ElCamareroSocketServer.Utilities;

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
public class TipoNotificacionPendEntrega {
     
    private Integer codigoTipoNotificacion;
    private String descripcionCorta;
    private String descripcionLarga;
    private String accionAplicar;
    private String fechaCreacion;
    private String usuario;
    private Integer codigoSistema;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codigoTipoNotificacion != null ? codigoTipoNotificacion.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof TipoNotificacionPendEntrega)) {
            return false;
        }
        TipoNotificacionPendEntrega other = (TipoNotificacionPendEntrega) object;
        if ((this.codigoTipoNotificacion == null && other.codigoTipoNotificacion != null) || (this.codigoTipoNotificacion != null && !this.codigoTipoNotificacion.equals(other.codigoTipoNotificacion))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "TipoNotificacionPendEntrega{" + "codigoTipoNotificacion=" + codigoTipoNotificacion + ", descripcionCorta=" + descripcionCorta + ", descripcionLarga=" + descripcionLarga + ", accionAplicar=" + accionAplicar + ", fechaCreacion=" + fechaCreacion + ", Usuario=" + usuario + ", codigoSistema=" + codigoSistema + '}';
    }


    
    
}
