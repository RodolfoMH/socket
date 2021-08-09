/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newsoft.ElCamareroSocketServer.Utilities;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 *
 * @author jmorel
 */
public class ListaGenerica<X> implements ParameterizedType{
    
    private Class<?> wrapped;
    
    public ListaGenerica(Class<X> wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return new Type[] {wrapped};
    }

    @Override
    public Type getRawType() {
         return ArrayList.class;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }
    
}
