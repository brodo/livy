package de.unisiegen.livy.esperwrapper.core;


import com.espertech.esper.client.*;
import com.espertech.esper.client.time.CurrentTimeEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Julian Dax on 26/02/14.
 */
public class EsperWrapper{
    private final HashMap<String,IComplexEventListener> listeners = new HashMap<String, IComplexEventListener>();
    private final HashMap<String,List<EPStatement>> statements = new HashMap<String, List<EPStatement>>();
    private EPAdministrator administrator;
    EPRuntime runtime;

    public EsperWrapper(){
        this(EPServiceProviderManager.getDefaultProvider());
    }

    public EsperWrapper(EPServiceProvider epServiceProvider){
        runtime = epServiceProvider.getEPRuntime();
        administrator = epServiceProvider.getEPAdministrator();
    }

    public interface QueryDefinition{
        public void onQuery(String query, int queryId);
    }

    public void unregisterQuestionnaire(String questionnaire){
        listeners.remove(questionnaire);
        List<EPStatement> statementsForQuestionnaire = statements.get(questionnaire);
        if(statementsForQuestionnaire != null){
            for(EPStatement s : statementsForQuestionnaire){
                s.removeAllListeners();
            }
        }
        statementsForQuestionnaire.remove(questionnaire);
    }

    public void removeQuery(int queryId){
        administrator.getStatement(Integer.valueOf(queryId).toString()).destroy();
    }

    public void removeAllQueriesBesides(String[] exceptions){
        LinkedList<String> exceptionList = new LinkedList<String>(Arrays.asList(exceptions));
        for(String name : administrator.getStatementNames()){
            if(!exceptionList.contains(name)) administrator.getStatement(name).destroy();
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
        administrator.createEPL(epl);
    }

    public QueryDefinition triggerQuestionnaire(final String questionnaireName) {

        return new QueryDefinition(){
            @Override
            public void onQuery(String query, int queryId) {
                final String queryName = Integer.valueOf(queryId).toString();
                EPStatement statement = administrator.getStatement(queryName);
                if(statementExistsAndNeedsUpdate(statement,query)){
                    statement.destroy();
                    statement = null;
                }
                if(statement == null) statement = statementFromStringWithName(query, queryName);

                statement.removeAllListeners();
                statement.addListener(new UpdateListener() {
                    @Override
                    public void update(EventBean[] eventBeans, EventBean[] eventBeans2) {
                        IComplexEventListener listener = listeners.get(questionnaireName);
                        listener.eventOccurred();
                    }
                });
                statement.start();
                addEplStatementToStatementMap(questionnaireName, statement);
            }
        };
    }

    public void eventHappened(Object event){
        runtime.sendEvent(event);
    }

    public void eventHappened(HashMap event, String name) { runtime.sendEvent(event, name);}

    public long getCurrentTime(){
        return runtime.getCurrentTime();
    }

    private boolean statementExistsAndNeedsUpdate(EPStatement statement, String query){
        return (statement != null) && !statement.getText().equals(query);
    }

    public void setCurrentTime(long timestamp){
        CurrentTimeEvent timeEvent = new CurrentTimeEvent(timestamp);
        runtime.sendEvent(timeEvent);
    }

    private EPStatement statementFromStringWithName(String query, String name){
        return administrator.createEPL(query, name);
    }

    private void addEplStatementToStatementMap(String questionnaireName, EPStatement statement){
        List<EPStatement> statementsForQuestionnaire = statements.get(questionnaireName);
        if(statementsForQuestionnaire == null) statementsForQuestionnaire = new LinkedList<EPStatement>();
        statementsForQuestionnaire.add(statement);
        statements.put(questionnaireName,statementsForQuestionnaire);
    }


    private EPStatement statementFormString(String query){
        return administrator.createEPL(query);
    }

}