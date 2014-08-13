package de.unisiegen.livy;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import java.util.HashMap;

/**
 * Created by Julian Dax on 26/03/14.
 * This util class makes it easy to send intents to the EventService
 *
 * @see de.unisiegen.livy.EventService
 */
public class Livy  {
    public static void processEvent(Parcelable event, Context context) {
        context.sendBroadcast(makeIntentWithEvent(event, context));
    }

    public static void processEvent(HashMap event, String name, Context context) {
        context.startService(makeIntentWithEventMap(event, name, context));
    }

    public static void saveSurveyId(int surveyId, Context context) {
        context.sendBroadcast(makeIntentWithSurveyId(surveyId, context));
    }

    public static void saveEplPattern(String query, Context context) {
        context.sendBroadcast(makeIntentWithEplQuery(query, context));
    }

    public static void saveEplPatternWithSurveyToTrigger(String query, int queryId, int surveyId, Context context) {
        context.sendBroadcast(makeIntentWithEplQueryAndSurveyId(query, queryId, surveyId, context));
    }

    public static void deleteEplPatternById(int id, Context context){
        context.sendBroadcast(makeIntentForDeletingQuery(id, context));
    }

    public static void deleteSurveyId(int id, Context context){
        context.sendBroadcast(makeIntentForDeletingSurveyWithId(id, context));
    }

    public static void deleteAllEplPatternsBesides(String[] exceptionIds, Context context){
        context.sendBroadcast(makeIntentForDeletingAllQueriesBesides(exceptionIds, context));
    }


    private static Intent makeIntentForDeletingAllQueriesBesides(String[] queries, Context context){
        Intent intent = new Intent(EventService.ACTION_CEP_SERVICE);
        intent.putExtra("command", EventService.DELETE_ALL_QUERIES_BESIDES);
        intent.putExtra("queries", queries);
        return intent;
    }

    private static Intent makeIntentForDeletingQuery(int queryId, Context context){
        Intent intent = new Intent(EventService.ACTION_CEP_SERVICE);
        intent.putExtra("command", EventService.DELETE_QUERY);
        intent.putExtra("queryId", queryId);
        return intent;
    }

    private static Intent makeIntentForDeletingSurveyWithId(int surveyId, Context context){
        Intent intent = new Intent(EventService.ACTION_CEP_SERVICE);
        intent.putExtra("command", EventService.DELETE_SURVEY);
        intent.putExtra("survey", surveyId);
        return intent;
    }

    private static Intent makeIntentWithSurveyId(int surveyId, Context context) {
        Intent intent = new Intent(EventService.ACTION_CEP_SERVICE);
        intent.putExtra("command", EventService.REGISTER_SURVEY);
        intent.putExtra("survey", surveyId);
        return intent;
    }

    private static Intent makeIntentWithEvent(Parcelable event, Context context) {
        Intent intent = new Intent(EventService.ACTION_CEP_SERVICE);
        intent.putExtra("command", EventService.ADD_EVENT);
        intent.putExtra("event", event);
        return intent;
    }

    private static Intent makeIntentWithEventMap(HashMap event, String name, Context context){
        Intent intent = new Intent(context, EventService.class);
        intent.putExtra("command", EventService.ADD_EVENT_MAP);
        intent.putExtra("event", event);
        intent.putExtra("name", name);
        return intent;
    }

    private static Intent makeIntentWithEplQuery(String query, Context context) {
        Intent intent = new Intent(EventService.ACTION_CEP_SERVICE);
        intent.putExtra("command", EventService.MAKE_EPL_QUERY);
        intent.putExtra("query", query);
        return intent;
    }

    private static Intent makeIntentWithEplQueryAndSurveyId(String query, int queryId, int surveyId, Context context) {
        Intent intent = new Intent(EventService.ACTION_CEP_SERVICE);
        intent.putExtra("command", EventService.MAKE_EPL_QUERY_AND_TRIGGER_SURVEY);
        intent.putExtra("query", query);
        intent.putExtra("id", queryId);
        intent.putExtra("survey", surveyId);
        return intent;
    }
}
