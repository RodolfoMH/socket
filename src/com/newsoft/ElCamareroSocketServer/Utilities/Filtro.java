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
public class Filtro {
    
    String columna;
    String tipo;
    String value;

    @Override
    public String toString() {
        return tipo +"="+ columna +"="+ value ;
    }
    
    
    
}
