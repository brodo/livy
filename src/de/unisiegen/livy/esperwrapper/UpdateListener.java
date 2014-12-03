/**
 * Created by Julian Dax on 21/02/14.
 */
package de.unisiegen.livy.esperwrapper;

import java.util.List;

public interface UpdateListener {
    public void update(List<EventBeanProxy> newEvents, List<EventBeanProxy> oldEvents);
}
