package de.flower.rmt.ui.markup.html.form.renderer;

import de.flower.rmt.model.db.entity.event.Event;
import de.flower.rmt.model.db.type.EventType;
import de.flower.rmt.util.Dates;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.model.ResourceModel;

/**
 * @author flowerrrr
 */
public class EventRenderer implements IChoiceRenderer<Event> {

    @Override
    public Object getDisplayValue(final Event event) {
        return getDateTeamTypeSummary(event);
    }

    @Override
    public String getIdValue(final Event object, final int index) {
        if (object.getId() != null) {
            return "" + object.getId();
        } else {
            return "index-" + index;
        }
    }

    public static String getDateTeamTypeSummary(final Event event) {
        String team = event.getTeam().getName();
        String eventType = new ResourceModel(EventType.from(event).getResourceKey()).getObject();
        String date = Dates.formatDateTimeShortWithWeekday(event.getDateTimeAsDate());
        return date + " - " + team + " - " + eventType + " - " + event.getSummary();
    }
}
