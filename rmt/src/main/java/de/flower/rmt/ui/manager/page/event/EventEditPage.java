package de.flower.rmt.ui.manager.page.event;

import de.flower.rmt.model.event.Event;
import de.flower.rmt.service.IEventManager;
import de.flower.rmt.ui.common.page.event.EventDetailsPanel;
import de.flower.rmt.ui.common.page.event.EventPagerPanel;
import de.flower.rmt.ui.manager.ManagerBasePage;
import de.flower.rmt.ui.manager.NavigationPanel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;

import java.util.List;

/**
 * @author flowerrrr
 */
public class EventEditPage extends ManagerBasePage {

    @SpringBean
    private IEventManager eventManager;

    public EventEditPage(IModel<Event> model) {
        setHeading("manager.event.edit.heading", null);
        final EventTabPanel tabPanel;
        addMainPanel(tabPanel = new EventTabPanel(model) {
            @Override
            protected void onAjaxUpdate(final AjaxRequestTarget target, final int selectedTab) {
                // allow secondary panel to update when tabs change
                target.add(getSecondaryPanel());
            }
        });
        addSecondaryPanel(new EventPagerPanel(model, getListModel()) {

            @Override
            protected void onClick(IModel<Event> model) {
                setResponsePage(new EventEditPage(model));
            }

        });
        getSecondaryPanel().add(new EventDetailsPanel(model) {
            @Override
            public boolean isVisible() {
                return tabPanel.getSelectedTab() == EventTabPanel.INVITATIONS_PANEL_INDEX;
            }
        });
    }

    private IModel<List<Event>> getListModel() {
        return new LoadableDetachableModel<List<Event>>() {
            @Override
            protected List<Event> load() {
                return eventManager.findAll();
            }
        };
    }


    @Override
    public String getActiveTopBarItem() {
        return NavigationPanel.EVENTS;
    }

}
