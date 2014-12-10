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

    public static void saveEplPattern(String query, Context context) {
        context.startService(makeIntentWithEplQuery(query, context));
    }

    public static void saveEplPatternWithSurveyToTrigger(String query, String patternName, String surveyName, Context context) {
        context.startService(makeIntentWithEplQueryAndSurveyId(query, patternName, surveyName, context));
    }

    public static void deleteEplPatternById(int id, Context context){
        context.startService(makeIntentForDeletingQuery(id, context));
    }

    public static void deleteAllEplPatterns(Context context){
        context.startService(makeIntentForDeletingAllQueries(context));
    }

    public static void addSurveyToPattern(String patternName, String surveyName, Context context){
        context.startService(makeIntentForAddingSurveyToPattern(patternName, surveyName, context));
    }

    public static void getPatternList(Context context){
        makeIntentForGettingPatternList(context);
    }

    protected static Intent makeIntentForGettingPatternList(Context context){
        Intent intent = createEventServiceIntent(context);
        intent.putExtra("command", EventService.GET_STATEMENTS);
        return intent;

    }

    protected static Intent makeIntentForAddingSurveyToPattern(String patternName, String surveyName, Context context){
        Intent intent = createEventServiceIntent(context);
        intent.putExtra("command", EventService.ADD_SURVEY_TO_PATTERN);
        intent.putExtra("queryId", patternName);
        intent.putExtra("surveyId", surveyName);
        return intent;
    }

    protected static Intent makeIntentForDeletingAllQueries(Context context){
        Intent intent = createEventServiceIntent(context);
        intent.putExtra("command", EventService.DELETE_ALL_PATTERNS);
        return intent;
    }

    protected static Intent makeIntentForDeletingQuery(int queryId, Context context){
        Intent intent = createEventServiceIntent(context);
        intent.putExtra("command", EventService.DELETE_PATTERN);
        intent.putExtra("queryId", queryId);
        return intent;
    }

    protected static Intent makeIntentWithEvent(Parcelable event, Context context) {
        Intent intent = createEventServiceIntent(context);
        intent.putExtra("command", EventService.PROCESS_EVENT_OBJECT);
        intent.putExtra("event", event);
        return intent;
    }

    protected static Intent makeIntentWithEventMap(HashMap event, String name, Context context){
        Intent intent = createEventServiceIntent(context);
        intent.putExtra("command", EventService.PROCESS_EVENT_MAP);
        intent.putExtra("event", event);
        intent.putExtra("name", name);
        return intent;
    }

    protected static Intent makeIntentWithEplQuery(String query, Context context) {
        Intent intent = createEventServiceIntent(context);
        intent.putExtra("command", EventService.SAVE_EPL_PATTERN);
        intent.putExtra("query", query);
        return intent;
    }

    protected static Intent makeIntentWithEplQueryAndSurveyId(String query, String queryName, String surveyName, Context context) {
        Intent intent = createEventServiceIntent(context);
        intent.putExtra("command", EventService.SAVE_EPL_PATTERN_AND_TRIGGER_SURVEY);
        intent.putExtra("query", query);
        intent.putExtra("id", queryName);
        intent.putExtra("survey", surveyName);
        return intent;
    }

    protected static Intent createEventServiceIntent(Context context) {
        return new Intent(context, EventService.class);
    }
}