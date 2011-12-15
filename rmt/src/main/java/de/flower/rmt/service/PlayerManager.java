package de.flower.rmt.service;

import de.flower.rmt.model.Player;
import de.flower.rmt.model.Team;
import de.flower.rmt.model.User;
import de.flower.rmt.model.event.Event;
import de.flower.rmt.repository.IEventRepo;
import de.flower.rmt.repository.IPlayerRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author flowerrrr
 */
@Service
@Transactional(readOnly = true, propagation = Propagation.REQUIRED)
public class PlayerManager extends AbstractService implements IPlayerManager {

    @Autowired
    private IPlayerRepo playerRepo;

    @Autowired
    private IEventRepo eventRepo;


    /**
     * Shitty implementation using jpa association to retrieve list of teams.
     * @param team
     * @return
     */
    @Override
    public List<Player> findByTeam(Team team) {
        return playerRepo.findByTeam(team);
    }

    @Override
    public Player findByTeamAndUser(final Team team, final User user) {
        return playerRepo.findByTeamAndUser(team, user);
    }

    @Override
    public Player findByEventAndUser(final Event event, final User user) {
        eventRepo.reattach(event);
        return findByTeamAndUser(event.getTeam(), user);
    }
}
