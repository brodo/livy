package de.unisiegen.livy.esperwrapper.eplgenerator;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Julian Dax on 23/03/14.
 */
public class Event {
    public Condition firstCondition;
    public String name;
    public int questionnaireId;
    public Integer contextId;
    public List<Condition> optionalConditions = new LinkedList<Condition>();
}
