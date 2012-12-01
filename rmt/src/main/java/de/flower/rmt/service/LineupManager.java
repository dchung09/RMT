package de.flower.rmt.service;

import com.google.common.annotations.VisibleForTesting;
import com.mysema.query.types.Path;
import com.mysema.query.types.expr.BooleanExpression;
import de.flower.common.util.Check;
import de.flower.rmt.model.db.entity.Invitation;
import de.flower.rmt.model.db.entity.Invitation_;
import de.flower.rmt.model.db.entity.Lineup;
import de.flower.rmt.model.db.entity.LineupItem;
import de.flower.rmt.model.db.entity.QLineup;
import de.flower.rmt.model.db.entity.QLineupItem;
import de.flower.rmt.model.db.entity.event.Event;
import de.flower.rmt.model.dto.InvitationDto;
import de.flower.rmt.repository.ILineupItemRepo;
import de.flower.rmt.repository.ILineupRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author flowerrrr
 */
@Service
@Transactional(propagation = Propagation.REQUIRED)
public class LineupManager extends AbstractService implements ILineupManager {

    @Autowired
    private ILineupRepo lineupRepo;

    @Autowired
    private ILineupItemRepo lineupItemRepo;

    @Autowired
    private IInvitationManager invitationManager;

    @Autowired
    private IActivityManager activityManager;

    @Override
    public Lineup findLineup(final Event event, Path<?>... attributes) {
        BooleanExpression isEvent = QLineup.lineup.event.eq(event);
        return lineupRepo.findOne(isEvent, attributes);
    }

    @Override
    public List<LineupItem> findLineupItems(final Event event, Path<?>... attributes) {
        Lineup lineup = findLineup(event);
        if (lineup == null) {
            createLineup(event);
        }
        BooleanExpression isEvent = QLineupItem.lineupItem.lineup.event.eq(event);
        List<LineupItem> items = lineupItemRepo.findAll(isEvent, attributes);
        for (int i = 0; i < attributes.length; i++) {
            if (attributes[i].equals(QLineupItem.lineupItem.invitation)) {
                // load invitations with users as we need them later. fetching user name via attributes is not possible
                // once loaded into session we avoid LIE
                invitationManager.findAllByEvent(event, Invitation_.user);
            }
        }
        return items;
    }

    @Override
    public Lineup createLineup(final Event event) {
        Lineup lineup = new Lineup(event);
        lineupRepo.save(lineup);
        return lineup;
    }

    @Override
    public void drop(final InvitationDto dto) {
        Invitation invitation = invitationManager.loadById(dto.invitationId, Invitation_.event);
        Lineup lineup = findLineup(invitation.getEvent());
        Check.notNull(lineup);
        LineupItem item = lineupItemRepo.findByLineupAndInvitation(lineup, invitation);
        if (item == null) {
            item = new LineupItem(lineup, invitation);
        }
        // update positions
        item.setTop(dto.top, dto.height);
        item.setLeft(dto.left, dto.width);
        save(item);
    }

    @Override
    public void removeLineupItem(final Long invitationId) {
        Invitation invitation = invitationManager.loadById(invitationId);
        Lineup lineup = findLineup(invitation.getEvent());
        Check.notNull(lineup);
        LineupItem item = lineupItemRepo.findByLineupAndInvitation(lineup, invitation);
        lineupItemRepo.delete(item);
    }

    @VisibleForTesting
    @Override
    public void save(final LineupItem item) {
        validate(item);
        lineupItemRepo.save(item);
    }

    @Override
    public void delete(final Long lineupId) {
        lineupRepo.delete(lineupId); // delete is cascaded to lineupitems
    }

    @Override
    public void publishLineup(final Event event) {
        Lineup lineup = findLineup(event);
        Check.notNull(lineup);
        lineup.setPublished(true);
        lineupRepo.save(lineup);
        activityManager.onLineupPublished(lineup);
    }
}
