package de.flower.rmt.ui.page.event.manager.invitations;

import de.flower.common.ui.ajax.event.AjaxEventListener;
import de.flower.common.ui.ajax.markup.html.AjaxLink;
import de.flower.common.ui.markup.html.list.EntityListView;
import de.flower.common.ui.modal.ModalDialogWindow;
import de.flower.common.util.Check;
import de.flower.rmt.model.Invitation;
import de.flower.rmt.model.Invitation_;
import de.flower.rmt.model.RSVPStatus;
import de.flower.rmt.model.event.Event;
import de.flower.rmt.service.IInvitationManager;
import de.flower.rmt.ui.app.Links;
import de.flower.rmt.ui.panel.BasePanel;
import de.flower.rmt.ui.panel.DropDownMenuPanel;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.AbstractLink;
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
        this(null, model);
    }

    public InvitationListPanel(String id, IModel<Event> model) {
        super(id);
        Check.notNull(model);
        add(createListView("acceptedList", RSVPStatus.ACCEPTED, model, true));
        add(createListView("unsureList", RSVPStatus.UNSURE, model, true));
        add(createListView("declinedList", RSVPStatus.DECLINED, model, true));
        add(createListView("noresponseList", RSVPStatus.NORESPONSE, model, false));
        add(new AjaxEventListener(Invitation.class));
    }

    private Component createListView(String id, RSVPStatus status, IModel<Event> model, final boolean printOrder) {
        ListView list = new EntityListView<Invitation>(id, getInvitationList(model, status)) {
            @Override
            protected void populateItem(ListItem<Invitation> item) {
                item.add(createInvitationFragement(item, printOrder));
            }
        };
        return list;
    }

    private Component createInvitationFragement(ListItem<Invitation> item, final boolean printOrder) {
        final Invitation invitation = item.getModelObject();
        Fragment frag = new Fragment("itemPanel", "itemFragment", this);
        frag.add(new Label("name", invitation.getName()));
        final Label label = new Label("position", "" + (item.getIndex() + 1));
        label.setVisible(printOrder);
        frag.add(label);
        frag.add(DateLabel.forDateStyle("date", Model.of(invitation.getDate()), "SS"));
        frag.add(new Label("comment", invitation.getComment()).setVisible(!StringUtils.isBlank(invitation.getComment())));
        frag.add(new Label("managerComment", invitation.getManagerComment()).setVisible(!StringUtils.isBlank(invitation.getManagerComment())));
        // now the dropdown menu
        DropDownMenuPanel menuPanel = new DropDownMenuPanel();
        menuPanel.addLink(createEditLink("link", item), "button.edit");
        if (invitation.hasEmail()) {
            menuPanel.addLink(Links.mailLink("link", invitation.getEmail(), null), "button.email");
        }
        frag.add(menuPanel);
        return frag;
    }

    private AbstractLink createEditLink(String id, ListItem<Invitation> item) {
        return new AjaxLink<Invitation>(id, item.getModel()) {

            @Override
            public void onClick(final AjaxRequestTarget target) {
                InvitationEditPanel content = new InvitationEditPanel(getModel());
                ModalDialogWindow.showContent(this, content, 5);
            }
        };
    }

    private IModel<List<Invitation>> getInvitationList(final IModel<Event> model, final RSVPStatus rsvpStatus) {
        return new LoadableDetachableModel<List<Invitation>>() {
            @Override
            protected List<Invitation> load() {
                return invitationManager.findAllByEventAndStatus(model.getObject(), rsvpStatus, Invitation_.user);
            }
        };
    }
}