package de.flower.rmt.ui.page.teams.manager;

import de.flower.rmt.ui.panel.BasePanel;
import org.apache.wicket.markup.html.link.Link;

/**
 * @author flowerrrr
 */
public class TeamsSecondaryPanel extends BasePanel {

    public TeamsSecondaryPanel() {
        add(new Link("newButton") {

            @Override
            public void onClick() {
                setResponsePage(new TeamEditPage());
            }
        });
    }
}