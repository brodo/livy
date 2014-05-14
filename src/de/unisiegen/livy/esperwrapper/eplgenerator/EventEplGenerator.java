package de.unisiegen.livy.esperwrapper.eplgenerator;

import com.google.gson.Gson;

/**
 * Created by Julian Dax on 21/03/14.
 * INSERT INTO test SELECT * from pattern [de.unisiegen.wineme.tests.util.location.FakeLocation(latitude>20) and de.unisiegen.wineme.tests.util.location.FakeLocation(longitude=10)]
 */
public class EventEplGenerator {
    private Gson gson = new Gson();
    public String generateEplFromJson(String json) throws IllegalArgumentException{
        validateJsonInput(json);
        Event event = gson.fromJson(json, Event.class);
        return String.format("%s INSERT INTO %s SELECT * FROM PATTERN [%s]", contextForEvent(event),
                eventNameToTableName(event.name),
                generatePatternForEvent(event));
    }

    private String generatePatternForEvent(Event e){
        StringBuilder stringBuilder =  new StringBuilder();
        stringBuilder.append(conditionStringForCondition(e.firstCondition));
        for (Condition condition : e.optionalConditions)
            stringBuilder.append(conditionStringForCondition(condition));
        return stringBuilder.toString();
    }

    private String contextForEvent(Event e){
        if(e.contextId == null) return "";
        return String.format("CONTEXT %s", e.contextId.toString());
    }

    private String conditionStringForCondition(Condition condition){
        StringBuilder stringBuilder = new StringBuilder();
        if(condition.combinator != null){
            stringBuilder.append(String.format(" %s ", condition.combinator));
        }
        if(condition.measurement.sensorName.equals("internal.timer.atoms")){
            if(condition.measurement.parameterName.equals("interval")) {
                String formatStr = "timer:interval(%s seconds)";
                stringBuilder.append(String.format(formatStr, condition.value));
            } else {
                String formatStr = "timer:at(%s)";
                stringBuilder.append(String.format(formatStr, condition.value));
            }

        } else {
            stringBuilder.append(String.format("%s(%s%s%s)",
                    condition.measurement.sensorName,
                    condition.measurement.parameterName,
                    condition.operator,
                    condition.value
            ));
        }

        return stringBuilder.toString();
    }



    private String eventNameToTableName(String eventName){
        eventName = eventName.replace(" ", "_");
        return eventName;
    }

    private void validateJsonInput(String json) throws IllegalArgumentException {
        Event event = gson.fromJson(json, Event.class);
        if (event == null) throw new IllegalArgumentException("Could not deserialize event");
        if(event.name == null) throw new IllegalArgumentException("Events need a name attribute.");
        if(event.firstCondition == null) throw new IllegalArgumentException("Events need at least one condition.");
        if(event.firstCondition.measurement == null)
            throw new IllegalArgumentException("Every event needs a measurement.");
        if(event.firstCondition.measurement.sensorName == null)
            throw new IllegalArgumentException("Every measurement needs a sensor.");
        if(event.firstCondition.measurement.parameterName == null)
            throw new IllegalArgumentException("Every measurement needs a sensor parameter.");
    }
}