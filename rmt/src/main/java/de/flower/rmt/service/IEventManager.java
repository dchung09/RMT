package de.flower.rmt.service;

import com.mysema.query.types.EntityPath;
import de.flower.rmt.model.db.entity.Team;
import de.flower.rmt.model.db.entity.User;
import de.flower.rmt.model.db.entity.event.Event;
import de.flower.rmt.model.db.type.EventType;
import de.flower.rmt.model.dto.Notification;

import javax.persistence.metamodel.Attribute;
import java.util.List;

/**
 * @author flowerrrr
 */
public interface IEventManager {

    /**
     * Saves or updates an event.
     * @param entity
     */
    void save(Event entity);

    /**
     * Creates a new event an also creates invitations for all players of the
     * team of the event.
     * @param entity
     * @param createInvitations
     */
    void create(Event entity, boolean createInvitations);

    Event loadById(Long id, Attribute... attributes);

    List<Event> findAll(Attribute... attributes);

    List<Event> findAllUpcomingAndLastNByUser(User user, int num, EntityPath<?> ... attributes);
    /**
     * Hard deletes an event and all invitations.
     * @param id
     */
    void delete(Long id);

    /**
     * Soft deletes all events of the team.
     * Soft in case deletion of team was human error to be able to recover.
     * @param entity
     */
    void deleteByTeam(Team entity);

    Event newInstance(EventType eventType);

    /**
     * Used for deep links. Loads event and checks if user has access rights to this event.
     * @param id
     * @param user
     * @return
     */
    Event loadByIdAndUser(Long id, User user);

    void sendInvitationMail(Long id, Notification notification);

    @Deprecated // experimental
    Event initAssociations(Event event, Attribute... attributes);

    boolean isEventClosed(Event event);
}
