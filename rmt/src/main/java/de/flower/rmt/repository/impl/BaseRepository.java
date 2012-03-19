package de.flower.rmt.repository.impl;

import de.flower.common.model.AbstractBaseEntity;
import de.flower.common.model.AbstractBaseEntity_;
import de.flower.common.model.ObjectStatus;
import de.flower.common.spring.SpringApplicationContextBridge;
import de.flower.common.util.Check;
import de.flower.rmt.model.AbstractClubRelatedEntity;
import de.flower.rmt.model.AbstractClubRelatedEntity_;
import de.flower.rmt.model.Club;
import de.flower.rmt.repository.IRepository;
import de.flower.rmt.repository.Specs;
import de.flower.rmt.service.security.ISecurityService;
import org.hibernate.LockOptions;
import org.hibernate.Session;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.domain.Specifications;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

/**
 * @author flowerrrr
 */
public class BaseRepository<T extends AbstractBaseEntity, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements IRepository<T, ID> {

    private EntityManager em;

    private JpaEntityInformation<T, ?> entityInformation;

    private ISecurityService securityService;

    public BaseRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.em = entityManager;
        this.entityInformation = entityInformation;
        this.securityService = (ISecurityService) SpringApplicationContextBridge.getInstance().getBean("securityService");
    }

    public void reattach(T entity) {
        Check.notNull(entity);
        Session session = (Session) em.getDelegate();
        session.buildLockRequest(LockOptions.NONE).lock(entity);
    }

    //***************************************************************
    // Filtering of database reads.
    // Filters out all deleted entities.
    // Multi tenancy support.
    // Add club of currently logged in user to query where clause.
    // Filtering is still suboptimal, as only findAll calls gets filtered.
    //***************************************************************

    @Override
    public List<T> findAll() {
        return findAll(Specifications.where(getNotDeleted()));
    }

    @Override
    public List<T> findAll(final Specification<T> spec) {
        Specifications<T> specs = Specifications.where(getNotDeleted()).and(spec);
        Class<T> domainClass = entityInformation.getJavaType();
        if (AbstractClubRelatedEntity.class.isAssignableFrom(domainClass)) {
            specs = specs.and(getHasClub());
        }
        return super.findAll(specs);
    }

    private Specification<T> getNotDeleted() {
        Specification notDeleted = Specifications.not(Specs.eq(AbstractBaseEntity_.objectStatus, ObjectStatus.DELETED));
        return notDeleted;
    }

    private Specification<T> getHasClub() {
        Specification hasClub = Specs.eq(AbstractClubRelatedEntity_.club, getClub());
        return hasClub;
    }

    private Club getClub() {
        return securityService.getUser().getClub();
    }
}
