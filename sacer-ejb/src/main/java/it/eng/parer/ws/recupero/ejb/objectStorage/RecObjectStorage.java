
package it.eng.parer.ws.recupero.ejb.objectStorage;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.s3.model.S3Object;

import it.eng.parer.entity.AroCompObjectStorage;
import it.eng.parer.entity.FirReport;
import it.eng.parer.objectstorage.dto.RecuperoDocBean;
import it.eng.parer.objectstorage.ejb.AwsClientServices;
import it.eng.parer.objectstorage.ejb.ReportvfAwsClientServices;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.utils.MessaggiWSBundle;

/**
 *
 * @author Sinatti_S
 */
@Stateless(mappedName = "RecObjectStorage")
@LocalBean
@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
public class RecObjectStorage {

    private static final Logger log = LoggerFactory.getLogger(RecObjectStorage.class);

    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    @EJB
    private AwsClientServices awsClient;

    @EJB
    private ReportvfAwsClientServices reportVfawsClient;

    private RispostaControlli recuperaObjectStorageCompSuStream(long idCompDoc, OutputStream outputStream) {
        RispostaControlli rc = new RispostaControlli();
        rc.setrLong(-1);
        rc.setrBoolean(false);

        AroCompObjectStorage tmpAroCompObjS = null;

        try {
            // recupero AroCompObjectStorage
            String queryStr = "select t from AroCompObjectStorage t " + "where t.aroCompDoc.idCompDoc = :idCompDoc ";
            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idCompDoc", idCompDoc);

            List<AroCompObjectStorage> lstObjectStorage = query.getResultList();
            if (lstObjectStorage.size() == 1) {
                tmpAroCompObjS = lstObjectStorage.get(0);
                rc.setrLong(0);
            }
            // AroCompObjectStorage founded
            if (tmpAroCompObjS != null && rc.getrLong() != -1) {
                rc = awsClient.getS3Object(tmpAroCompObjS.getNmTenant(), tmpAroCompObjS.getNmBucket(),
                        tmpAroCompObjS.getCdKeyFile());

                if (rc.isrBoolean()) {
                    S3Object tmpS3Object = (S3Object) rc.getrObject();
                    IOUtils.copyLarge(tmpS3Object.getObjectContent(), outputStream);
                } else {
                    return rc; // SdkClientException
                }
            }
            //
            rc.setrBoolean(true);
        } catch (IOException e) {
            rc.setrBoolean(false);
            rc.setCodErr(MessaggiWSBundle.ERR_666);
            rc.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione RecObjectStorage.recuperaObjectAndCopySuStream " + e.getMessage()));
            log.error("Eccezione gestione object storage su stream ", e);
        }

        return rc;
    }

    private RispostaControlli recuperaObjectStorageReportvfSuStream(long idCompDoc, OutputStream outputStream) {
        RispostaControlli rc = new RispostaControlli();
        rc.setrLong(-1);
        rc.setrBoolean(false);

        try {
            // recupero FirReport
            String queryStr = "select t from FirReport t " + "where t.aroCompDoc.idCompDoc = :idCompDoc ";
            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idCompDoc", idCompDoc);

            FirReport firReport = (FirReport) query.getSingleResult();
            // FirReport founded
            if (firReport != null) {
                rc.setrLong(0);
                rc = reportVfawsClient.getS3Object(firReport.getNmBucket(), firReport.getCdKeyFile());

                if (rc.isrBoolean()) {
                    S3Object tmpS3Object = (S3Object) rc.getrObject();
                    rc.setrObject(tmpS3Object);
                    IOUtils.copyLarge(tmpS3Object.getObjectContent(), outputStream);
                } else {
                    return rc; // SdkClientException
                }
            }
            //
            rc.setrBoolean(true);
        } catch (IOException e) {
            rc.setrBoolean(false);
            rc.setCodErr(MessaggiWSBundle.ERR_666);
            rc.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione RecObjectStorage.recuperaObjectAndCopySuStream " + e.getMessage()));
            log.error("Eccezione gestione object storage su stream ", e);
        }

        return rc;
    }

    public RispostaControlli recuperaObjectStorageSuStream(RecuperoDocBean dto) {
        RispostaControlli rc = new RispostaControlli();
        // Nota : va diffenziato per tipo
        switch (dto.getTipo()) {
        case COMP_DOC:
            rc = this.recuperaObjectStorageCompSuStream(dto.getId(), dto.getOs());
            break;
        case REPORTVF:
            rc = this.recuperaObjectStorageReportvfSuStream(dto.getId(), dto.getOs());
            break;
        default:
            break;
        }

        return rc;
    }

}
