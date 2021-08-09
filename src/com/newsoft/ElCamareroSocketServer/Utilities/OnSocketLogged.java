package com.newsoft.ElCamareroSocketServer.Utilities;

import com.google.gson.JsonObject;
import com.newsoft.ElCamareroSocketServer.Socket.SocketThread;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jmorel
 */
public interface OnSocketLogged {
    public void socketLogged(SocketThread socket);
}
