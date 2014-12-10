package de.unisiegen.livy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import de.unisiegen.livy.esperwrapper.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * Created by Julian Dax on 24/03/14.
 * This service receives events, queries and survey ids and puts them into Esper. When a complex event happens,
 * it sends an intent with the "de.unisiegen.livy.COMPLEX_EVENT" action.
 */
public class EventService extends Service {
    public static String LOG_TAG = "Livy/EventService";
    public static final String ACTION_COMPLEX_EVENT = "de.unisiegen.livy.COMPLEX_EVENT";
    public static final String ACTION_STATEMENT_LIST = "de.unisiegen.livy.STATEMENT_LIST";
    public static final String ACTION_CEP_SERVICE = "de.unisiegen.livy.EventService";
    public static final int PROCESS_EVENT_OBJECT = 0;
    public static final int PROCESS_EVENT_MAP = 1;
    public static final int SAVE_EPL_PATTERN = 2;
    public static final int SAVE_EPL_PATTERN_AND_TRIGGER_SURVEY = 3;
    public static final int DELETE_PATTERN = 4;
    public static final int DELETE_ALL_PATTERNS = 5;
    public static final int ADD_SURVEY_TO_PATTERN = 6;
    public static final int GET_STATEMENTS = 7;

    private AsperLoader loader;
    private EplAdministratorProxy administrator;
    private EPRuntimeProxy runtime;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "Livy Service Created");
        AsperLoader loader = new AsperLoader(this);
        administrator = loader.getEPAdministrator();
        runtime = loader.getEPRuntime();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        if(intent != null && intent.hasExtra("command"))
            handleCommand(intent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void handleCommand(final Intent intent) {
        int commandNumber = extractCommandFromIntent(intent);
        switch (commandNumber) {
            case PROCESS_EVENT_OBJECT: processEventObject(intent); break;
            case PROCESS_EVENT_MAP: processEventMap(intent); break;
            case SAVE_EPL_PATTERN: saveEplPattern(intent); break;
            case SAVE_EPL_PATTERN_AND_TRIGGER_SURVEY: saveEplPatternAndTriggerSurvey(intent); break;
            case DELETE_PATTERN: deleteEPLPattern(intent); break;
            case DELETE_ALL_PATTERNS: deleteAllPatterns();break;
            case ADD_SURVEY_TO_PATTERN: addSurveyToPattern(intent); break;
            case GET_STATEMENTS: sendStatementList(); break;
        }
    }

    private void processEventMap(Intent intent) {
        runtime.sendEvent((Map) intent.getSerializableExtra("event"), intent.getStringExtra("name"));
    }

    private void processEventObject(Intent intent) {
        runtime.sendEvent(intent.getParcelableExtra("event"));
    }

    private int extractCommandFromIntent(Intent intent) {
        return intent.getIntExtra("command", -1);
    }

    private EPStatementProxy saveEplPattern(Intent intent) {
        return getQueryName(intent) == null ?
                administrator.createEPL(getQuery(intent)) :
                administrator.createEPL(getQuery(intent), stringFromInt(getStatementId(intent)));

    }

    private void saveEplPatternAndTriggerSurvey(final Intent intent) {
        EPStatementProxy statement = saveEplPattern(intent);
        statement.addListener(new ComplexEventListener(intent));
        statement.start();
    }

    private void deleteEPLPattern(Intent intent) {
        EPStatementProxy statement = administrator.getStatement(getQueryName(intent));
        if(statement != null){
            statement.removeAllListeners();
            statement.destroy();
        }
    }

    private void deleteAllPatterns() {
       administrator.destroyAllStatements();
    }

    private void addSurveyToPattern(Intent intent){
        EPStatementProxy statement = administrator.getStatement(getQueryName(intent));
        if(statement != null) statement.addListener(new ComplexEventListener(intent));
        else Log.e(LOG_TAG, "Could not find EPL pattern to add survey to");
    }

    private void sendStatementList(){
        ArrayList<String> names = new ArrayList<String>(Arrays.asList(administrator.getStatementNames()));
        Intent sendingIntent = new Intent(ACTION_STATEMENT_LIST);
        sendingIntent.putStringArrayListExtra("statementNames", names);
        sendBroadcast(sendingIntent);
    }

    private void sendComplexEventOccurredNotification(String surveyName, List<EventBeanProxy> newEvents,
                                                      List<EventBeanProxy> oldEvents ) {
        Intent sendingIntent = new Intent(ACTION_COMPLEX_EVENT);
        sendingIntent.putExtra("newEvents", eventBeanProxiesToSerializables(newEvents));
        sendingIntent.putExtra("oldEvents", eventBeanProxiesToSerializables(oldEvents));
        sendingIntent.putExtra("survey", surveyName);
        sendBroadcast(sendingIntent);
    }

    private Serializable[] eventBeanProxiesToSerializables(List<EventBeanProxy> events){
        ArrayList<Serializable> result = new ArrayList<Serializable>();
        for(EventBeanProxy event : events){
            result.add((Serializable)event.getUnderlying());
        }
        return result.toArray(new Serializable[result.size()]);
    }

    private int getStatementId(Intent intent){ return intent.getIntExtra("id", -1);}
    private String stringFromInt(int number){ return Integer.valueOf(number).toString();}
    private String getSurveyName(Intent intent) { return intent.getStringExtra("survey");}
    private String getQuery(Intent intent) { return intent.getStringExtra("query"); }
    private String getQueryName(Intent intent){ return intent.getStringExtra("queryId"); }
    private int getSurvey(Intent intent) { return  getIntFromIntent(intent, "survey"); }
    private int getIntFromIntent(Intent intent, String name) { return intent.getIntExtra(name, -1); }

    private class ComplexEventListener implements UpdateListener {
        private final Intent intent;

        public ComplexEventListener(Intent intent) {
            this.intent = intent;
        }

        @Override
        public void update(List<EventBeanProxy> newEvents, List<EventBeanProxy> oldEvents) {
            sendComplexEventOccurredNotification(getSurveyName(intent), newEvents, oldEvents);
        }
    }
}