/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package com.newsoft.ElCamareroSocketServer.Utilities;
//
//import jakarta.ws.rs.ext.ContextResolver;
//import jakarta.ws.rs.ext.Provider;
//import java.util.HashMap;
//import java.util.Map;
//import org.glassfish.jersey.moxy.json.MoxyJsonConfig;
//
///**
// *
// * @author jmorel
// */
//@Provider
//public class JsonMoxyConfigurationContextResolver implements ContextResolver<MoxyJsonConfig> {
//
//    private final MoxyJsonConfig config;
//
//    public JsonMoxyConfigurationContextResolver() {
//        System.out.println("LLEGO AQUIIIIIIIIIIIIII");
//        final Map<String, String> namespacePrefixMapper = new HashMap<String, String>();
//        namespacePrefixMapper.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");
//        config = new MoxyJsonConfig()
//                .setNamespacePrefixMapper(namespacePrefixMapper)
//                .setNamespaceSeparator(':');
//    }
//
//    @Override
//    public MoxyJsonConfig getContext(Class<?> objectType) {
//        return config;
//    }
//}
