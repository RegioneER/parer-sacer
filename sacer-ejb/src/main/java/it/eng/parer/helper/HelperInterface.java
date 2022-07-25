package it.eng.parer.helper;

import java.io.Serializable;
import javax.ejb.Local;
import javax.persistence.EntityManager;

/**
 *
 * @author Bonora_L
 */
public interface HelperInterface {

    public void setEntityManager(EntityManager entityManager);

    public EntityManager getEntityManager();

    public <T> void insertEntity(T entity, boolean forceFlush);

    public <T> void removeEntity(T entity, boolean forceFlush);

    public <T> T findById(Class<T> entityClass, Serializable id);

    public <T> T findByIdWithLock(Class<T> entityClass, Serializable id);

    public <T> T findViewById(Class<T> viewEntityClass, Serializable id);
}
