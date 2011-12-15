package de.flower.rmt.ui.player.page.events;

import de.flower.common.ui.ajax.updatebehavior.AjaxUpdateBehavior;
import de.flower.common.ui.ajax.updatebehavior.events.AjaxEvent;
import de.flower.rmt.model.Invitation;
import de.flower.rmt.model.RSVPStatus;
import de.flower.rmt.model.User;
import de.flower.rmt.model.event.Event;
import de.flower.rmt.model.event.EventType;
import de.flower.rmt.service.IInvitationManager;
import de.flower.rmt.service.IResponseManager;
import de.flower.rmt.ui.app.Links;
import de.flower.rmt.ui.common.panel.BasePanel;
import de.flower.rmt.ui.common.panel.QuickResponseLabel;
import de.flower.rmt.ui.model.EventModel;
import de.flower.rmt.ui.model.UserModel;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * @author flowerrrr
 */
public class EventListPanel extends BasePanel {

    @SpringBean
    private IResponseManager responseManager;

    @SpringBean
    private IInvitationManager invitationManager;

    public EventListPanel(final UserModel userModel, final IModel<List<Event>> listModel) {

        WebMarkupContainer listContainer = new WebMarkupContainer("listContainer");
        add(listContainer);
        listContainer.add(new WebMarkupContainer("noEntry") {
            @Override
            public boolean isVisible() {
                return listModel.getObject().isEmpty();
            }
        });
        listContainer.add(new ListView<Event>("list", listModel) {
            @Override
            public boolean isVisible() {
                return !getList().isEmpty();
            }

            @Override
            protected void populateItem(final ListItem<Event> item) {
                final Event event = item.getModelObject();
                final EventModel eventModel = new EventModel(item.getModel());
                Link link = Links.eventLink("invitationsLink", event.getId());
                item.add(link);
                link.add(DateLabel.forDateStyle("date", Model.of(event.getDate()), "S-"));
                link.add(DateLabel.forDateStyle("time", Model.of(event.getTime().toDateTimeToday().toDate()), "-S"));
                item.add(new Label("type", new ResourceModel(EventType.from(event).getResourceKey())));
                item.add(new Label("team", event.getTeam().getName()));
                item.add(new Label("summary", event.getSummary()));
                final Invitation invitation = getInvitation(event, userModel.getObject());
                item.add(new QuickResponseLabel("rsvpStatus", invitation.getStatus()) {
                    @Override
                    protected void submitStatus(final RSVPStatus status) {
                        responseManager.respond(eventModel.getId(), userModel.getId(), status);
                    }
                });
            }

            private Invitation getInvitation(final Event event, final User user) {
                return invitationManager.loadByEventAndUser(event, user);
            }

         });
        listContainer.add(new AjaxUpdateBehavior(AjaxEvent.EntityAll(Event.class)));
    }

}
