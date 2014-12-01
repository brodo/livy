package de.unisiegen.livy.esperwrapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Julian Dax on 30.11.14.
 */
public class EplAdministratorProxy {
    private Object administrator;
    private Class administratorClass;
    public EplAdministratorProxy(Object administrator) {
        this.administrator = administrator;
        this.administratorClass = administrator.getClass();
    }

    public void doEplQuery(String epl){
        try {
            Method createEPL = administratorClass.getMethod("createEPL", String.class);
            createEPL.invoke(administrator, epl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object getStatementByName(String queryName){
        try {
            Method getStatement = administratorClass.getMethod("getStatement", String.class);
            return getStatement.invoke(administrator, queryName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<String> getStatementNames() {
        Method getStatementNames = null;
        try {
            getStatementNames = administratorClass.getMethod("getStatementNames");
            return (List<String>) getStatementNames.invoke(administrator);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<String>();
    }

    public Object statementFormString(String query){
        try {
            Method createEpl = administratorClass.getMethod("createEPL", String.class);
            return createEpl.invoke(administrator, query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object statementFromStringWithName(String query, String name){
        try {
            Method createEPL =  createEPL = administratorClass.getMethod("createEPL", String.class, String.class);
            return createEPL.invoke(administrator, query, name);
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
