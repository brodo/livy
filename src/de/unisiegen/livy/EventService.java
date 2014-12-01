package de.unisiegen.livy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import de.unisiegen.livy.esperwrapper.core.AsperLoader;
import de.unisiegen.livy.esperwrapper.EsperWrapper;
import de.unisiegen.livy.esperwrapper.IComplexEventListener;

import java.util.HashMap;


/**
 * Created by Julian Dax on 24/03/14.
 * This service receives events, queries and survey ids and puts them into Esper. When a complex event happens,
 * it sends an intent with the "de.unisiegen.livy.COMPLEX_EVENT" action.
 */
public class EventService extends Service {
    public static String DBG_TAG = "EventService";
    public static final String ACTION_COMPLEX_EVENT = "de.unisiegen.livy.COMPLEX_EVENT";
    public static final String ACTION_CEP_SERVICE = "de.unisiegen.livy.EventService";
    public static final int PROCESS_EVENT_OBJECT = 0;
    public static final int PROCESS_EVENT_MAP = 1;
    public static final int REGISTER_SURVEY = 2;
    public static final int SAVE_EPL_PATTERN = 3;
    public static final int SAVE_EPL_PATTERN_AND_TRIGGER_SURVEY = 4;
    public static final int DELETE_SURVEY = 5;
    public static final int DELETE_PATTERN = 6;
    public static final int DELETE_ALL_PATTERNS_BESIDES = 7;
    public static final int DELETE_ALL_SURVEYS_BESIDES = 8;
    public EsperWrapper esperWrapper;


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
            case REGISTER_SURVEY: registerSurvey(intent); break;
            case SAVE_EPL_PATTERN: saveEplPattern(intent); break;
            case SAVE_EPL_PATTERN_AND_TRIGGER_SURVEY: saveEplPatternAndTriggerSurvey(intent); break;
            case DELETE_SURVEY: deleteSurvey(intent); break;
            case DELETE_PATTERN: deleteEPLPattern(intent); break;
            case DELETE_ALL_PATTERNS_BESIDES: deleteAllPatternsBesides(intent);break;
            case DELETE_ALL_SURVEYS_BESIDES: deleteAllSurveysBesides(intent);break;
            default:
                break;
        }
    }

    private int extractCommandFromIntent(Intent intent) {
        return intent.getIntExtra("command", -1);
    }

    private void deleteAllSurveysBesides(Intent intent) {
        String[] surveyIds = intent.getStringArrayExtra("surveys");
        esperWrapper.removeAllSurveysBesides(surveyIds);
    }

    private void deleteAllPatternsBesides(Intent intent) {
        String[] queries = intent.getStringArrayExtra("queries");
        esperWrapper.removeAllQueriesBesides(queries);
    }

    private void deleteEPLPattern(Intent intent) {
        esperWrapper.removeQuery(getQueryId(intent));
    }

    private void deleteSurvey(Intent intent) {
        esperWrapper.unregisterQuestionnaire(Integer.valueOf(getSurvey(intent)).toString());
    }

    private void saveEplPatternAndTriggerSurvey(Intent intent) {
        esperWrapper.triggerQuestionnaire(getSurveyName(intent))
                .onQuery(getQuery(intent), intent.getIntExtra("id", -1));
    }

    private void saveEplPattern(Intent intent) {
        esperWrapper.doEplQuery(getQuery(intent));
    }

    private void registerSurvey(final Intent intent) {
        esperWrapper.registerQuestionnaire(getSurveyName(intent), new IComplexEventListener() {
            @Override
            public void eventOccurred() {
                Log.d(DBG_TAG, "A Complex event happened");
                int surveyId = getSurvey(intent);
                sendComplexEventOccurredNotification(surveyId);
            }
        });
    }

    private void sendComplexEventOccurredNotification(int surveyId) {
        Intent sendingIntent = new Intent(ACTION_COMPLEX_EVENT);
        sendingIntent.putExtra("survey", surveyId);
        sendBroadcast(sendingIntent);
    }

    private void processEventMap(Intent intent) {
        esperWrapper.eventHappened((HashMap)intent.getSerializableExtra("event"), intent.getStringExtra("name"));
    }

    private void processEventObject(Intent intent) {
        esperWrapper.eventHappened(intent.getParcelableExtra("event"));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(DBG_TAG, "Livy Service Created");
        esperWrapper =  new EsperWrapper(new AsperLoader(this));
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private String getSurveyName(Intent intent) {
        return Integer.toString(intent.getIntExtra("survey", -1));
    }

    private String getQuery(Intent intent) { return intent.getStringExtra("query"); }
    private int getQueryId(Intent intent){ return getIntFromIntent(intent, "queryId"); }
    private int getSurvey(Intent intent) { return  getIntFromIntent(intent, "survey"); }
    private int getIntFromIntent(Intent intent, String name) { return intent.getIntExtra(name, -1); }
}