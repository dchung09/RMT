package de.flower.common.model;

/**
 * Marker interface for entity classes.
 * @author flowerrrr
 */
public interface IEntity {

    boolean isTransient();

    Long getId();
}