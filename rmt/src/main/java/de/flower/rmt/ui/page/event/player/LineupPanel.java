package de.flower.rmt.ui.page.event.player;

import de.flower.common.ui.panel.BasePanel;
import de.flower.common.util.Check;
import de.flower.rmt.model.db.entity.Lineup;
import de.flower.rmt.model.db.entity.LineupItem;
import de.flower.rmt.model.db.entity.QLineupItem;
import de.flower.rmt.model.db.entity.event.Event;
import de.flower.rmt.service.ILineupManager;
import de.flower.rmt.ui.page.event.manager.lineup.DraggablePlayerPanel;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * read-only version of LineupEditPanel.
 *
 * @author flowerrrr
 */
public class LineupPanel extends BasePanel {

    private final static Logger log = LoggerFactory.getLogger(LineupPanel.class);

    @SpringBean
    private ILineupManager lineupManager;

    public LineupPanel(String id, IModel<Event> model) {
        super(id);
        Check.notNull(model);

        final IModel<Lineup> lineupModel = getLineupModel(model);

        final WebMarkupContainer grid = new WebMarkupContainer("grid");
        add(grid);

        final WebMarkupContainer itemContainer = new WebMarkupContainer("itemContainer");
        grid.add(itemContainer);

        // render existing lineup items
        final IModel<List<LineupItem>> listModel = getListModel(model);
        final ListView<LineupItem> items = new ListView<LineupItem>("items", listModel) {
            @Override
            protected void populateItem(final ListItem<LineupItem> item) {
                LineupItem lineupItem = item.getModelObject();

                DraggablePlayerPanel draggablePlayer = new DraggablePlayerPanel(lineupItem.getInvitation(), lineupItem, false) {
                    @Override
                    protected boolean isDraggable() {
                        return false; // fixed, read-only view
                    }
                };

                item.add(draggablePlayer);
            }

            @Override
            public boolean isVisible() {
                final Lineup lineup = lineupModel.getObject();
                return lineup != null && lineup.isPublished();
            }
        };
        itemContainer.add(items);

        grid.add(new WebMarkupContainer("noLineup") {
            @Override
            public boolean isVisible() {
                return !items.isVisible();
            }
        });

    }

    private IModel<List<LineupItem>> getListModel(final IModel<Event> model) {
        return new LoadableDetachableModel<List<LineupItem>>() {
            @Override
            protected List<LineupItem> load() {
                return lineupManager.findLineupItems(model.getObject(), QLineupItem.lineupItem.invitation);
            }
        };
    }

    private IModel<Lineup> getLineupModel(final IModel<Event> model) {
        return new LoadableDetachableModel<Lineup>() {
            @Override
            protected Lineup load() {
                return lineupManager.findLineup(model.getObject());
            }
        };
    }
}
