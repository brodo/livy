package de.unisiegen.livy.esperwrapper.core;


import dalvik.system.DexClassLoader;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * Created by Julian Dax on 26/02/14.
 */
public class EsperWrapper{
    private final HashMap<String,IComplexEventListener> listeners = new HashMap<String, IComplexEventListener>();
    private final HashMap<String,List<Object>> statements = new HashMap<String, List<Object>>();
    private AsperLoader loader;
    private Object administrator;
    private Object runtime;
    private DexClassLoader asperClassLoader;


    public EsperWrapper(AsperLoader asperLoader){
        runtime = asperLoader.getEPRuntime();
        administrator = asperLoader.getEPAdministrator();
        asperClassLoader = asperLoader.getDexClassLoader();
    }

    public interface QueryDefinition{
        public void onQuery(String query, int queryId);
    }

    public void unregisterQuestionnaire(String questionnaire){
        listeners.remove(questionnaire);
        List<Object> statementsForQuestionnaire = statements.get(questionnaire);
        if(statementsForQuestionnaire != null){
            for(Object s : statementsForQuestionnaire){
                try {
                    Method removeAllListeners = s.getClass().getMethod("removeAllListeners");
                    removeAllListeners.invoke(s);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

            }
        }
        statementsForQuestionnaire.remove(questionnaire);
    }

    public void removeQuery(int queryId){
        removeQuery(Integer.valueOf(queryId).toString());
    }

    public void removeQuery(String queryId){
        try {
            Method getStatement = administrator.getClass().getMethod("getStatement", String.class);
            Object statement = getStatement.invoke(administrator, queryId);
            Method destroy = statement.getClass().getMethod("destroy");
            destroy.invoke(statement);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    public void removeAllQueriesBesides(String[] exceptions){
        try {
            LinkedList<String> exceptionList = new LinkedList<String>(Arrays.asList(exceptions));
            Method getStatementNames = administrator.getClass().getMethod("getStatementNames");
            List<String> statements = (List<String>) getStatementNames.invoke(administrator);
            for(String name : statements){
                if(!exceptionList.contains(name)) removeQuery(name);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    public void removeAllSurveysBesides(String[] surveyIds){
        List<String> exceptionList = Arrays.asList(surveyIds);
        for(String survey : listeners.keySet()){
            if(!exceptionList.contains(survey)) unregisterQuestionnaire(survey);
        }
    }

    public void registerQuestionnaire(String questionnaire, IComplexEventListener listener) {
        listeners.put(questionnaire, listener);
    }
    public IComplexEventListener getListenerForQuestionnaire(String questionnaire){
        return listeners.get(questionnaire);
    }

    public void doEplQuery(String epl){
        try {
            Method createEPL = administrator.getClass().getMethod("createEPL", String.class);
            createEPL.invoke(administrator, epl);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public QueryDefinition triggerQuestionnaire(final String questionnaireName) {

        return new QueryDefinition(){
            @Override
            public void onQuery(String query, int queryId) {
                try {
                    final String queryName = Integer.valueOf(queryId).toString();
                    Method getStatement = null;
                    getStatement = administrator.getClass().getMethod("getStatement", String.class);
                    Object statement = getStatement.invoke(administrator, queryName);

                    if(statementExistsAndNeedsUpdate(statement,query)){
                        Method destroy = statement.getClass().getMethod("destroy");
                        destroy.invoke(statement);
                        statement = null;
                    }
                    statement = (statement == null) ? statementFromStringWithName(query, queryName) : statement;
                    if(statement == null) return;
                    Method removeAllListeners = statement.getClass().getMethod("removeAllListeners");
                    removeAllListeners.invoke(statement);



                    Class updateListenerClass = asperClassLoader.loadClass("com.espertech.esper.client.UpdateListener");
                    Method addListener = statement.getClass().getMethod("addListener", updateListenerClass);

                    Object updateListener = Proxy.newProxyInstance(statement.getClass().getClassLoader(),
                            new Class[]{updateListenerClass}, new InvocationHandler() {
                                @Override
                                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                    IComplexEventListener listener = listeners.get(questionnaireName);
                                    listener.eventOccurred();
                                    return null;
                                }
                            });
                    addListener.invoke(statement, updateListener);

                    Method start = statement.getClass().getMethod("start");
                    start.invoke(statement);
                    addEplStatementToStatementMap(questionnaireName, statement);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }


            }
        };
    }

    public void eventHappened(Object event){
        try {
            Method sendEvent = runtime.getClass().getMethod("sendEvent", Object.class);
            sendEvent.invoke(runtime, event);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void eventHappened(HashMap event, String name) {
        try {
            Method sendEvent = runtime.getClass().getMethod("sendEvent", Map.class, String.class);
            sendEvent.invoke(runtime, event, name);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    private boolean statementExistsAndNeedsUpdate(Object statement, String query){
        if(statement == null) return false;
        try {
            Method getText = statement.getClass().getMethod("getText");
            String text = (String) getText.invoke(statement);
            return text.equals(query);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return false;
    }


    private Object statementFromStringWithName(String query, String name){
        Method createEPL = null;
        try {
            createEPL = administrator.getClass().getMethod("createEPL", String.class, String.class);
            return createEPL.invoke(administrator, query, name);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addEplStatementToStatementMap(String questionnaireName, Object statement){
        List<Object> statementsForQuestionnaire = statements.get(questionnaireName);
        if(statementsForQuestionnaire == null) statementsForQuestionnaire = new LinkedList<Object>();
        statementsForQuestionnaire.add(statement);
        statements.put(questionnaireName,statementsForQuestionnaire);
    }

    private Object statementFormString(String query){
        try {
            Method createEpl = administrator.getClass().getMethod("createEPL", String.class);
            return createEpl.invoke(administrator, query);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}