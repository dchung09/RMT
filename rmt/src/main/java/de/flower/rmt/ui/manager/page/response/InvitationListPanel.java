package de.flower.rmt.ui.manager.page.response;

import de.flower.common.util.Check;
import de.flower.rmt.model.Invitation;
import de.flower.rmt.model.RSVPStatus;
import de.flower.rmt.model.event.Event;
import de.flower.rmt.service.IInvitationManager;
import de.flower.rmt.ui.common.panel.BasePanel;
import org.apache.wicket.Component;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * @author flowerrrr
 */
// TODO (flowerrrr - 23.10.11) unite with players InvitationListPanel
public class InvitationListPanel extends BasePanel {

    @SpringBean
    private IInvitationManager invitationManager;

    public InvitationListPanel(IModel<Event> model) {
        Check.notNull(model);
        add(createListView("acceptedList", RSVPStatus.ACCEPTED, model));
        add(createListView("unsureList", RSVPStatus.UNSURE, model));
        add(createListView("declinedList", RSVPStatus.DECLINED, model));
        add(createListView("noresponseList", RSVPStatus.NORESPONSE, model));
    }

    private Component createListView(String id, RSVPStatus status, IModel<Event> model) {
        ListView list = new ListView<Invitation>(id, getInvitationList(model, status)) {
            @Override
            protected void populateItem(ListItem<Invitation> item) {
                item.add(createInvitationFragement(item));
            }
        };
        return list;
    }

    private Component createInvitationFragement(ListItem<Invitation> item) {
        final Invitation invitation = item.getModelObject();
        Fragment frag = new Fragment("itemPanel", "itemFragment", this);
        frag.add(new Label("name", invitation.getName()));
        frag.add(DateLabel.forDateStyle("date", Model.of(invitation.getDate()), "SS"));
        frag.add(new Label("comment", invitation.getComment()));
        return frag;
    }

    private IModel<List<Invitation>> getInvitationList(final IModel<Event> model, final RSVPStatus rsvpStatus) {
        return new LoadableDetachableModel<List<Invitation>>() {
            @Override
            protected List<Invitation> load() {
                return invitationManager.findByEventAndStatus(model.getObject(), rsvpStatus);
            }
        };
    }
}