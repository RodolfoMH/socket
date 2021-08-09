/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newsoft.ElCamareroSocketServer.Entidades;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 *
 * @author rvalenzuela
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DatosPlato {
    
    private Long codigoMesa;
    private String descripcionArticulo;
    private String descripcionCliente;
    private Long posicionSilla;
    
}
