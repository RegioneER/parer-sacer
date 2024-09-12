/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.helper;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.LockTimeoutException;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
public class GenericHelper implements HelperInterface {

    private static final Logger logger = LoggerFactory.getLogger(GenericHelper.class);

    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public <T> void insertEntity(T entity, boolean forceFlush) {
        if (entity != null) {
            logger.debug("Persisting instance of class " + entity.getClass().getSimpleName());
            entityManager.persist(entity);
            if (forceFlush) {
                entityManager.flush();
            }
        }
    }

    public <T> void detachEntity(T entity) {
        if (entity != null) {
            entityManager.detach(entity);
        }
    }

    public <T> T mergeEntity(T entity) {
        if (entity != null) {
            logger.debug("Persisting instance of class " + entity.getClass().getSimpleName());
            return entityManager.merge(entity);
        }
        return entity;
    }

    @Override
    public <T> void removeEntity(T entity, boolean forceFlush) {
        if (entity != null) {
            logger.debug("Removing instance of class " + entity.getClass().getSimpleName());
            entityManager.remove(entity);
            if (forceFlush) {
                entityManager.flush();
            }
        }
    }

    public <T> T findById(Class<T> entityClass, BigDecimal id) {
        return findById(entityClass, id.longValue());
    }

    @Override
    public <T> T findById(Class<T> entityClass, Serializable id) {
        logger.debug("Getting instance of class " + entityClass.getSimpleName() + " with id: " + id);
        try {
            T instance = entityManager.find(entityClass, id);
            logger.debug("Get successful");
            return instance;
        } catch (RuntimeException re) {
            logger.error("Get failed", re);
            throw re;
        }
    }

    public <T> T findByIdWithLock(Class<T> entityClass, BigDecimal id) {
        return findByIdWithLock(entityClass, id.longValue());
    }

    @Override
    public <T> T findByIdWithLock(Class<T> entityClass, Serializable id) {
        logger.debug("Getting instance of class " + entityClass.getSimpleName() + " with id: " + id
                + ", with exclusive lock");
        T instance = null;
        try {
            instance = entityManager.find(entityClass, id, LockModeType.PESSIMISTIC_WRITE);
            logger.debug("Get successful");
        } catch (LockTimeoutException lte) {
            logger.error(GenericHelper.class.getSimpleName() + " --- Impossibile acquisire il lock", lte);
        }
        return instance;
    }

    @Override
    public <T> T findViewById(Class<T> entityViewClass, Serializable id) {
        logger.debug("Getting instance of class " + entityViewClass.getSimpleName() + " with id: " + id);
        try {
            T instance = entityManager.find(entityViewClass, id);
            logger.debug("Get successful");
            return instance;
        } catch (RuntimeException re) {
            logger.error("Get failed", re);
            throw re;
        }
    }

    protected Date getDataNonAnnullata() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            return sdf.parse("2444-12-31");
        } catch (ParseException e) {
            throw new RuntimeException("Impossibile creare il Date del 31/12/2444");
        }
    }

    protected List<BigDecimal> bigDecimalListFrom(Collection<Long> longList) {
        return longList.stream().map(l -> BigDecimal.valueOf(l)).collect(Collectors.toList());
    }

    protected <T extends Object> T getByColumnName(ResultSet rs, String colName, Class<T> clazz) {
        Assert.notNull(rs, "Il result set non può essere nullo");
        Assert.notNull(colName, "Il none della colonna è obbligatorio");
        Assert.notNull(clazz, "Il tipo di dato da recuperare è obbligatorio");
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            try {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    if (rsmd.getColumnLabel(i).equals(colName)) {
                        return rs.getObject(i, clazz);
                    }
                }
                return null;
            } catch (SQLException se) {
                throw new RuntimeException("Impossibile accedere al resulset " + se.getMessage());
            }
        } catch (SQLException se) {
            throw new RuntimeException("Impossibile accedere ai metadati del resultset " + se.getMessage());
        }

    }

}
