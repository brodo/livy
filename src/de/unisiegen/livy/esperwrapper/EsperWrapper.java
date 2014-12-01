package de.unisiegen.livy.esperwrapper;


import android.util.Log;
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
    private EplAdministratorProxy administrator;
    private Object runtime;
    private DexClassLoader asperClassLoader;
    private static final String TAG = "Livy";


    public EsperWrapper(AsperLoader asperLoader){
        runtime = asperLoader.getEPRuntime();
        administrator = new EplAdministratorProxy(asperLoader.getEPAdministrator());
        asperClassLoader = asperLoader.getDexClassLoader();
    }

    public interface QueryDefinition{
        public void onQuery(String query, int queryId);
    }

    public void doEplQuery(String epl){
        administrator.doEplQuery(epl);
    }

    public void unregisterQuestionnaire(String questionnaire){
        listeners.remove(questionnaire);
        List<Object> statementsForQuestionnaire = statements.get(questionnaire);
        if(statementsForQuestionnaire != null){
            removeAllInternalListenersForQuestionnaire(statementsForQuestionnaire);
            statementsForQuestionnaire.remove(questionnaire);
        }
    }

    private void removeAllInternalListenersForQuestionnaire(List<Object> statementsForQuestionnaire) {
        for(Object s : statementsForQuestionnaire){
            removeAllInternalListeners(s);
        }
    }

    private void removeAllInternalListeners(Object s) {
        try {
            Method removeAllListeners = s.getClass().getMethod("removeAllInternalListeners");
            removeAllListeners.invoke(s);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeQuery(int queryId){
        removeQuery(intToString(queryId));
    }

    private String intToString(int number) {
        return Integer.valueOf(number).toString();
    }

    public void removeQuery(String queryId){
        try {
            Method getStatement = administrator.getClass().getMethod("getStatement", String.class);
            Object statement = getStatement.invoke(administrator, queryId);
            destroyStatement(statement);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeAllQueriesBesides(String[] exceptions){
        LinkedList<String> exceptionList = new LinkedList<String>(Arrays.asList(exceptions));
        for(String statementName : administrator.getStatementNames()){
            if(!exceptionList.contains(statementName)) removeQuery(statementName);
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

    public QueryDefinition triggerQuestionnaire(final String questionnaireName) {

        return new BaseQueryDefinition(questionnaireName);
    }

    private void destroyStatement(Object statement) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Method destroy = statement.getClass().getMethod("destroy");
        destroy.invoke(statement);
    }

    public void eventHappened(Object event){
        try {
            Method sendEvent = runtime.getClass().getMethod("sendEvent", Object.class);
            sendEvent.invoke(runtime, event);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void eventHappened(HashMap event, String name) {
        try {
            Method sendEvent = runtime.getClass().getMethod("sendEvent", Map.class, String.class);
            sendEvent.invoke(runtime, event, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean statementExistsAndNeedsUpdate(Object statement, String query){
        if(statement == null) return false;
        try {
            Method getText = statement.getClass().getMethod("getText");
            String text = (String) getText.invoke(statement);
            return text.equals(query);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


    private void addEplStatementToStatementMap(String questionnaireName, Object statement){
        List<Object> statementsForQuestionnaire = statements.get(questionnaireName);
        if(statementsForQuestionnaire == null) statementsForQuestionnaire = new LinkedList<Object>();
        statementsForQuestionnaire.add(statement);
        statements.put(questionnaireName,statementsForQuestionnaire);
    }


    private class BaseQueryDefinition implements QueryDefinition {
        private final String questionnaireName;

        public BaseQueryDefinition(String questionnaireName) {
            this.questionnaireName = questionnaireName;
        }

        @Override
        public void onQuery(String query, int queryId) {
            try {
                final String queryName = intToString(queryId);
                Object statement = administrator.getStatementByName(queryName);
                if(statementExistsAndNeedsUpdate(statement,query)){
                    destroyStatement(statement);
                    statement = null;
                }
                statement = (statement == null) ? administrator.statementFromStringWithName(query, queryName) : statement;
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
                                if (listener == null) {
                                    Log.e(TAG, "An event happened, but there is no event listener!");
                                } else {
                                    listener.eventOccurred();
                                }
                                return null;
                            }
                        });
                addListener.invoke(statement, updateListener);

                Method start = statement.getClass().getMethod("start");
                start.invoke(statement);
                addEplStatementToStatementMap(questionnaireName, statement);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }
}