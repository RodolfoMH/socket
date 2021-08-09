/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.newsoft.ElCamareroSocketServer.Utilities;

import com.newsoft.newsoft.modelos.dao.genericos.DAORestFulFactory;
import com.newsoft.newsoft.modelos.dao.genericos.GenericRestFulDAO;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
//import org.glassfish.jersey.moxy.json.MoxyJsonFeature;

/**
 *
 * @author jmorel
 * @param <T>
 * @param <ID>
 */
public abstract class FactoriaDAORestFul<T, ID extends Serializable> {

    private final Class<T> clasePersistente;
    private final Class<T> claseID;
    private DAORestFulGenerico genericRestFulDAO;
    private final DAORestFulFactory dAORestFulFactory;

    public FactoriaDAORestFul() {
        this.clasePersistente = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        this.claseID = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[1];

        System.out.println("clasePersistente==> " + clasePersistente);

        dAORestFulFactory = new DAORestFulFactory();
    }

    public GenericRestFulDAO<T, ID> getDAORestFulEntidad() {
        Map metodosID = null;
        return this.getDAORestFulEntidad("", metodosID);
    }

    public GenericRestFulDAO<T, ID> getDAORestFulEntidad(String parURIBase, String parMetodoGetID) {

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("", parMetodoGetID);
        return this.getDAORestFulEntidad(parURIBase, hashMap);
    }

    @SuppressWarnings("ResultOfObjectAllocationIgnored")
    public GenericRestFulDAO<T, ID> getDAORestFulEntidad(String parURIBase, Map<String, String> parIDEntidad) {

        if (genericRestFulDAO == null) {

            genericRestFulDAO = new DAORestFulGenerico();
            //genericRestFulDAO.getClient().register(MoxyJsonFeature.class);
            //genericRestFulDAO.getClient().register(JsonMoxyConfigurationContextResolver.class);
                        
         
            Map<String, String> mappingURIs = new HashMap<>();

            // Asignar Los URIs Buscar por ID. Se utiliza Solo Miembro Familias, debido a que el Map: pathMethodsValuesIDEntidad
            // Contiene el URI para la busqueda en especifíco. Fue necesario por los ID compuesto, los cuales están formados
            // por otra clase.
            String[] metodosBuscar = {"buscarPorID", "buscarPorIDProceso", "refresh"};
            String URIBuscar = parURIBase;
            this.mappingURIsMetodos(mappingURIs, URIBuscar, metodosBuscar);

            // Asignar Los URIs BUSCAR TODOS
            String[] metodosBuscarTodos = {"buscarTodosOrdenado", "buscarTodos", "filtrarPorColumnas"};
            String URIBuscarTodos = parURIBase;
            this.mappingURIsMetodos(mappingURIs, URIBuscarTodos, metodosBuscarTodos);

            // Asignar Los URIs BUSCAR RANGO
            String[] metodosRango = {"buscarRango", "filtrarPorRango"};
            String URIBuscarRango = parURIBase;
            this.mappingURIsMetodos(mappingURIs, URIBuscarRango, metodosRango);

            // Asignar Los URIs COUNT
            String[] metodosCount = {"count", "countByQuery"};
            String URICount = parURIBase + "/count";
            this.mappingURIsMetodos(mappingURIs, URICount, metodosCount);

            // Asignar los URIs para INSERTAR y/o ACTUALIZAR, Siempre se utilizará merge, por eso el URI es el mismo
            String[] metodosInsertUpdates = {"save", "saveProceso", "saveOrUpdate", "saveOrUpdateProceso", "update",
                "updateProceso", "merge", "mergeProceso", "persistProceso"};
            String URIInsertUpdates = parURIBase;
            this.mappingURIsMetodos(mappingURIs, URIInsertUpdates, metodosInsertUpdates);

            // Asignar los URIs para BORRAR UNA ENTIDAD. La otra Ruta del Delete la tiene el Map: pathMethodsIDEntidad
            String[] metodosDelete = {"delete", "deleteProceso"};
            String URIDelete = parURIBase;
            this.mappingURIsMetodos(mappingURIs, URIDelete, metodosDelete);

            // Asignar los URIs NECESARIOS PARA CADA OPERACION A RELIAZAR SOBRE AL ENTIDAD
            this.genericRestFulDAO.asignarURIsWebAPI(mappingURIs);
        }

        return genericRestFulDAO;
    }

    public void mappingURIsMetodos(Map<String, String> parListadoURIs, String parURI, String[] parMetodos) {
        if (parListadoURIs == null || parMetodos == null || parMetodos.length <= 0) {
            return;
        }

        for (String metodo : parMetodos) {
            parListadoURIs.put(metodo, parURI);
        }
    }

    public void asignarValorKeyDependeEntidad(Map<String, String> parValoresKey) {

        this.genericRestFulDAO.setRutaKeyDependeEntidad("");
        Map<String, String> valoresKeyDependeEntidad = this.genericRestFulDAO.getValoresKeyDependeEntidad();
        if (parValoresKey == null || parValoresKey.size() <= 0) {
            valoresKeyDependeEntidad.put("\\{}", "0");
            return;
        }
        for (String key : parValoresKey.keySet()) {
            valoresKeyDependeEntidad.put(key, parValoresKey.get(key));
        }
    }

    public void asignarValorKeyDependeEntidad(Long parValorKey) {
        Map<String, String> valoresKey = new LinkedHashMap<>(1);
        valoresKey.put("\\{}", parValorKey + "");
        this.asignarValorKeyDependeEntidad(valoresKey);
    }

    class DAORestFulGenerico extends GenericRestFulDAO<T, ID> {

        public DAORestFulGenerico() {
            //super(clasePersistente);
        }

    }
}
