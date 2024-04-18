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

package it.eng.parer.web.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.table.AbstractBaseTable;

/*
 * @author Quaranta_M
 * 
 * La trasformazione funziona purchè siano soddisatte le seguenti condizioni:
 *  - nomeRowBean = nome Entity + "RowBean" (NB: Dali tronca una eventuale 's' posta alla fine del nome della entity)
 *  - nomeTableBean = nome Entity + "RowBean"
 *  - i nomi delle variabili degli oggetti referenziati dalle entity (e le PK di tali oggetti) hanno la parte finale del nome uguale alle FK dei rowBean
 *  - le PK Embeddable nelle entity devono avere il nome "id"
 * 
 */
public class Transform {

    static Logger log = LoggerFactory.getLogger(Transform.class);
    static final String[] excludedProps = new String[] { "class", "tableDescriptor", "iteratorColumnContained",
            "numrecords", "rnum", "rownum" };

    private Transform() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Metodo di trasformazione da lista di entity a tableBean
     *
     * @param entities
     *            la lista di entity da trasformare
     * 
     * @return il rowBean trasformato
     * 
     * @throws ClassNotFoundException
     *             errore generico
     * @throws NoSuchMethodException
     *             errore generico
     * @throws InstantiationException
     *             errore generico
     * @throws IllegalAccessException
     *             errore generico
     * @throws IllegalArgumentException
     *             errore generico
     * @throws InvocationTargetException
     *             errore generico
     */
    @SuppressWarnings("rawtypes")
    public static AbstractBaseTable entities2TableBean(List<?> entities)
            throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        String entityClassName = entities.get(0).getClass().getName()
                .substring(entities.get(0).getClass().getName().lastIndexOf("."));
        String tbClassName = null;
        if (entities.get(0).getClass().getName().startsWith(Constants.ENTITY_PACKAGE_NAME)
                || entities.get(0).getClass().getName().startsWith(Constants.GRANTED_ENTITY_PACKAGE_NAME)) {
            tbClassName = Constants.ROWBEAN_PACKAGE_NAME + entityClassName + "TableBean";
        } else {
            tbClassName = Constants.VIEWROWBEAN_PACKAGE_NAME + entityClassName + "TableBean";
        }
        Class<?> clazz = Class.forName(tbClassName);
        Constructor<?> constructor = clazz.getConstructor();
        AbstractBaseTable<?> tableBean = (AbstractBaseTable<?>) constructor.newInstance();
        for (Object entity : entities) {
            tableBean.add(Transform.entity2RowBean(entity));
        }
        return tableBean;
    }

    private static String getClassName(Object obj) {
        final List<String> proxySuffixes = Arrays.asList("_$$", "$HibernateProxy");
        String className = obj.getClass().getSimpleName();
        for (String s : proxySuffixes) {
            if (className.contains(s)) {
                className = className.substring(0, className.indexOf(s));
                break;
            }
        }
        return className;
    }

    /**
     * Metodo di trasformazione da entity a rowBean
     *
     * @param entity
     *            jpa generica
     * 
     * @return il rowBean trasformato
     * 
     * @throws ClassNotFoundException
     *             errore generico
     * @throws NoSuchMethodException
     *             errore generico
     * @throws InstantiationException
     *             errore generico
     * @throws IllegalAccessException
     *             errore generico
     * @throws IllegalArgumentException
     *             errore generico
     * @throws InvocationTargetException
     *             errore generico
     */
    public static BaseRowInterface entity2RowBean(Object entity) throws ClassNotFoundException, NoSuchMethodException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String entityClassName = getClassName(entity);
        String rbClassName = null;
        if (entity.getClass().getName().startsWith(Constants.ENTITY_PACKAGE_NAME)
                || entity.getClass().getName().startsWith(Constants.GRANTED_ENTITY_PACKAGE_NAME)) {
            rbClassName = Constants.ROWBEAN_PACKAGE_NAME + "." + entityClassName + "RowBean";
        } else {
            rbClassName = Constants.VIEWROWBEAN_PACKAGE_NAME + "." + entityClassName + "RowBean";
        }
        Class<?> clazz = Class.forName(rbClassName);
        Constructor<?> constructor = clazz.getConstructor();
        Object rowBean = constructor.newInstance();
        ((JEEBaseRowInterface) rowBean).entityToRowBean(entity);
        return (BaseRowInterface) rowBean;
    }

    /**
     * Metodo di trasformazione da rowBean a entity.
     *
     * <strong>UTILIZZARE ESCLUSIVAMENTE IN CASO DI NUOVI INSERIMENTI (PERSIST). NON UTILIZZARE IN CASO DI
     * UPDATE</strong>
     *
     * @param rowBean
     *            bean generico
     * 
     * @return l'entity trasformata
     */
    public static Object rowBean2Entity(Object rowBean) {
        return ((JEEBaseRowInterface) rowBean).rowBeanToEntity();
    }
}
