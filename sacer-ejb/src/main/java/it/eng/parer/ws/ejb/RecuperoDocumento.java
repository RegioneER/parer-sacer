
package it.eng.parer.ws.ejb;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.objectstorage.dto.RecuperoDocBean;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.recupero.ejb.objectStorage.RecObjectStorage;
import it.eng.parer.ws.recupero.ejb.oracleBlb.RecBlbOracle;

/**
 *
 * @author Sinatti_S
 */
@Stateless(mappedName = "RecuperoDocumento")
@LocalBean
@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
public class RecuperoDocumento {

    private static final Logger log = LoggerFactory.getLogger(RecuperoDocumento.class);

    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;
    //
    @EJB
    RecBlbOracle recBlbOracle;
    //
    @EJB
    RecObjectStorage recObjectStorage;

    /**
     * Passaggio di un wrapper con l'oggetto interessato dall'object storing
     * 
     * @param dto
     *            dot recupero
     * 
     * @return RispostaControlli risposta con esito controlli
     */
    public RispostaControlli callRecuperoDocSuStream(RecuperoDocBean dto) {
        // verifica esistenza object storage
        if (this.existInObjectStorage(dto)) {
            log.debug("RecuperoDocumento.callRecuperoDocSuStream : recupero from ObjectStorage, doc = " + dto);
            return recObjectStorage.recuperaObjectStorageSuStream(dto);
        }
        log.debug("RecuperoDocumento.callRecuperoDocSuStream : recupero from BlbOracle, doc = " + dto);
        // default (ASIS : pre object storage)
        return recBlbOracle.recuperaBlobCompSuStream(dto.getId(), dto.getOs(), dto.getTabellaBlobDaLeggere());
    }

    private boolean existInObjectStorage(RecuperoDocBean doc) {
        boolean result = false;

        switch (doc.getTipo()) {
        case COMP_DOC:
            result = this.compInObjectStorage(doc.getId());
            break;
        case REPORTVF:
            result = this.reportvfInObjectStorage(doc.getId());
            break;
        default:
            break;
        }

        return result;
    }

    private boolean compInObjectStorage(long id) {
        // recupero AroCompObjectStorage
        String queryStr = "select count(os) from AroCompObjectStorage os "
                + "where os.aroCompDoc.idCompDoc = :idCompDoc ";
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        query.setParameter("idCompDoc", id);
        long tmpNumOs = (Long) query.getSingleResult();
        return tmpNumOs > 0;
    }

    private boolean reportvfInObjectStorage(long id) {
        // recupero AroCompObjectStorage
        String queryStr = "select count(f) from FirReport f "
                + "where f.aroCompDoc.idCompDoc = :idCompDoc and f.nmBucket is not null and f.cdKeyFile is not null ";
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        query.setParameter("idCompDoc", id);
        long tmpNumOs = (Long) query.getSingleResult();
        return tmpNumOs > 0;
    }
}
