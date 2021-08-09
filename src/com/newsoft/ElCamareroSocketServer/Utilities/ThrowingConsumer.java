/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newsoft.ElCamareroSocketServer.Utilities;

/**
 *
 * @author jmorel
 */
@FunctionalInterface
public interface ThrowingConsumer <T, E extends Exception> {
    void accept(T t) throws E;
}