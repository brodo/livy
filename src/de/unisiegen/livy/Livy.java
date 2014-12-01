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
        context.startService(makeIntentWithEvent(event, context));
    }

    public static void processEvent(HashMap event, String name, Context context) {
        context.startService(makeIntentWithEventMap(event, name, context));
    }

    public static void saveSurveyId(int surveyId, Context context) {
        context.startService(makeIntentWithSurveyId(surveyId, context));
    }

    public static void saveEplPattern(String query, Context context) {
        context.startService(makeIntentWithEplQuery(query, context));
    }

    public static void saveEplPatternWithSurveyToTrigger(String query, int queryId, int surveyId, Context context) {
        context.startService(makeIntentWithEplQueryAndSurveyId(query, queryId, surveyId, context));
    }

    public static void deleteEplPatternById(int id, Context context){
        context.startService(makeIntentForDeletingQuery(id, context));
    }

    public static void deleteSurveyId(int id, Context context){
        context.startService(makeIntentForDeletingSurveyWithId(id, context));
    }

    public static void deleteAllEplPatternsBesides(String[] exceptionIds, Context context){
        context.startService(makeIntentForDeletingAllQueriesBesides(exceptionIds, context));
    }


    private static Intent makeIntentForDeletingAllQueriesBesides(String[] queries, Context context){
        Intent intent = createEventServiceIntent(context);
        intent.putExtra("command", EventService.DELETE_ALL_PATTERNS_BESIDES);
        intent.putExtra("queries", queries);
        return intent;
    }

    private static Intent makeIntentForDeletingQuery(int queryId, Context context){
        Intent intent = createEventServiceIntent(context);
        intent.putExtra("command", EventService.DELETE_PATTERN);
        intent.putExtra("queryId", queryId);
        return intent;
    }

    private static Intent makeIntentForDeletingSurveyWithId(int surveyId, Context context){
        Intent intent = createEventServiceIntent(context);
        intent.putExtra("command", EventService.DELETE_SURVEY);
        intent.putExtra("survey", surveyId);
        return intent;
    }

    private static Intent makeIntentWithSurveyId(int surveyId, Context context) {
        Intent intent = createEventServiceIntent(context);
        intent.putExtra("command", EventService.REGISTER_SURVEY);
        intent.putExtra("survey", surveyId);
        return intent;
    }

    private static Intent makeIntentWithEvent(Parcelable event, Context context) {
        Intent intent = createEventServiceIntent(context);
        intent.putExtra("command", EventService.PROCESS_EVENT_OBJECT);
        intent.putExtra("event", event);
        return intent;
    }

    private static Intent makeIntentWithEventMap(HashMap event, String name, Context context){
        Intent intent = createEventServiceIntent(context);

        intent.putExtra("command", EventService.PROCESS_EVENT_MAP);
        intent.putExtra("event", event);
        intent.putExtra("name", name);
        return intent;
    }

    private static Intent makeIntentWithEplQuery(String query, Context context) {
        Intent intent = createEventServiceIntent(context);
        intent.putExtra("command", EventService.SAVE_EPL_PATTERN);
        intent.putExtra("query", query);
        return intent;
    }

    private static Intent makeIntentWithEplQueryAndSurveyId(String query, int queryId, int surveyId, Context context) {
        Intent intent = createEventServiceIntent(context);
        intent.putExtra("command", EventService.SAVE_EPL_PATTERN_AND_TRIGGER_SURVEY);
        intent.putExtra("query", query);
        intent.putExtra("id", queryId);
        intent.putExtra("survey", surveyId);
        return intent;
    }

    private static Intent createEventServiceIntent(Context context) {
        return new Intent(context, EventService.class);
    }


}
