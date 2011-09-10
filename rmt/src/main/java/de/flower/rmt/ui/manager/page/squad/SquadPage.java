package de.flower.rmt.ui.manager.page.squad;

import de.flower.common.ui.FeatureNotImplementedLink;
import de.flower.common.ui.ajax.AjaxLinkWithConfirmation;
import de.flower.common.ui.ajax.MyAjaxLink;
import de.flower.common.ui.ajax.panel.AjaxSlideTogglePanel;
import de.flower.common.ui.ajax.updatebehavior.AjaxRespondListener;
import de.flower.common.ui.ajax.updatebehavior.AjaxUpdateBehavior;
import de.flower.common.ui.ajax.updatebehavior.events.Event;
import de.flower.common.ui.js.JQuery;
import de.flower.rmt.model.Team;
import de.flower.rmt.model.Team2Player;
import de.flower.rmt.model.Users;
import de.flower.rmt.service.ITeamManager;
import de.flower.rmt.ui.manager.ManagerBasePage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * @author oblume
 */
public class SquadPage extends ManagerBasePage {

    @SpringBean
    private ITeamManager teamManager;

    private AjaxSlideTogglePanel addPlayerPanel;

    public SquadPage(final IModel<Team> model) {
        super(model);


        final MyAjaxLink addButton = new MyAjaxLink("addButton") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                // show inline  dialog with squad edit form.
                addPlayerPanel.show(target);
                target.appendJavaScript(JQuery.fadeOut(this, "slow"));
            }
       };
        add(addButton);

        addPlayerPanel = new AjaxSlideTogglePanel("addPlayerPanel", new AddPlayerPanel(model)) {
            @Override
            public void onHide(AjaxRequestTarget target) {
                target.prependJavaScript(JQuery.fadeIn(addButton, "slow"));
            }
        };
        add(addPlayerPanel);

        WebMarkupContainer playerListContainer = new WebMarkupContainer("playerListContainer");
        add(playerListContainer);
        playerListContainer.add(new WebMarkupContainer("noPlayer") {
            @Override
            public boolean isVisible() {
                return getListModel(model).getObject().isEmpty();
            }
        });
        playerListContainer.add(new ListView<Users>("playerList", getListModel(model)) {

            @Override
            public boolean isVisible() {
                return !getList().isEmpty();
            }

            @Override
            protected void populateItem(final ListItem<Users> item) {
                item.add(new Label("name", item.getModelObject().getFullname()));
                item.add(new Label("status", new ResourceModel("player.status." + item.getModelObject().getStatus().toString().toLowerCase())));
                item.add(new FeatureNotImplementedLink("editButton", "Editing players"));
                item.add(new AjaxLinkWithConfirmation("removeButton", new ResourceModel("manager.squad.remove.confirm")) {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        teamManager.removePlayer((Team) SquadPage.this.getDefaultModelObject(), item.getModelObject());
                        target.registerRespondListener(new AjaxRespondListener(Event.EntityDeleted(Team2Player.class)));
                    }
                });
            }
        });
        playerListContainer.add(new AjaxUpdateBehavior(Event.EntityAll(Team2Player.class)));

    }

    private IModel<List<Users>> getListModel(final IModel<Team> model) {
        return new LoadableDetachableModel<List<Users>>() {
            @Override
            protected List<Users> load() {
                return teamManager.getPlayers(model.getObject());
            }
        };
    }

}