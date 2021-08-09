/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newsoft.ElCamareroSocketServer.Utilities;

import java.util.Objects;
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
public class AplicacionApp {
    
    private Integer codigoAplicacionApp;
    private String descripcionCorta;    
    private String descripcionLarga;   
    private String fechaCreacion;    
    private String usuario;   
    private short codigoSistema;
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.codigoAplicacionApp);
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
        final AplicacionApp other = (AplicacionApp) obj;
        if (!Objects.equals(this.codigoAplicacionApp, other.codigoAplicacionApp)) {
            return false;
        }
        return true;
    }
}
