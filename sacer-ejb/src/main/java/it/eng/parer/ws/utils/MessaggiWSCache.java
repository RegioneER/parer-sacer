/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.utils;

import it.eng.parer.entity.DecErrSacer;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fioravanti_f
 */
@Startup
@Singleton(mappedName = "MessaggiWSCache")
public class MessaggiWSCache {

    private static Logger log = LoggerFactory.getLogger(MessaggiWSCache.class);

    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    Map<String, String> errorMap;

    @PostConstruct
    public void initSingleton() {
        log.info("Inizializzazione singleton MessaggiWSCache...");
        try {
            String qlString = "SELECT e FROM DecErrSacer e ";
            Query query = entityManager.createQuery(qlString);

            List<DecErrSacer> list = query.getResultList();
            errorMap = new HashMap<>();
            for (DecErrSacer err : list) {
                errorMap.put(err.getCdErr(), err.getDsErr());
            }
        } catch (RuntimeException ex) {
            // log.fatal("Inizializzazione singleton MessaggiWSCache fallita! ", ex);
            log.error("Inizializzazione singleton MessaggiWSCache fallita! ", ex);
            throw ex;
        }
        log.info("Inizializzazione singleton MessaggiWSCache... completata.");
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String getString(String key) {
        return StringEscapeUtils.unescapeJava(errorMap.get(key));
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String getString(String key, Object... params) {
        return StringEscapeUtils.unescapeJava(MessageFormat.format(errorMap.get(key), params));
    }

}
