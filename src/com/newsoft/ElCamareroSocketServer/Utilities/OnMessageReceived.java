package com.newsoft.ElCamareroSocketServer.Utilities;

import com.google.gson.JsonObject;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jmorel
 */
public interface OnMessageReceived {
    public void messageReceived(JsonObject object, JsonObject userData);
}
