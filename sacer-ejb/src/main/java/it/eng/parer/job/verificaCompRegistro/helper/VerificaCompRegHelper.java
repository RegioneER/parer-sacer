/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.job.verificaCompRegistro.helper;

import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.DecAaRegistroUnitaDoc;
import it.eng.parer.entity.DecErrAaRegistroUnitaDoc;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fioravanti_f
 */
@Stateless(mappedName = "VerificaCompRegHelper")
@LocalBean
public class VerificaCompRegHelper {

    private static final Logger log = LoggerFactory.getLogger(VerificaCompRegHelper.class);
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    public List<DecAaRegistroUnitaDoc> getAaRegistroUnitaDocDaElab() {
        List<DecAaRegistroUnitaDoc> aaRegDaElabList;
        String queryStr = "SELECT u FROM DecAaRegistroUnitaDoc u " + "where u.flUpdFmtNumero = '1' ";
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        aaRegDaElabList = (List<DecAaRegistroUnitaDoc>) query.getResultList();
        return aaRegDaElabList;
    }

    public List<AroUnitaDoc> getListaUdDaVerificare(Long idRegistroUnitaDoc, List<Long> idSubStruts, Long anno) {
        List<AroUnitaDoc> aroUnitaDocs;

        String queryStr = "select u from AroUnitaDoc u " + "where " + "u.orgSubStrut.idSubStrut in :idSubStruts "
                + "and u.decRegistroUnitaDoc.idRegistroUnitaDoc = :idRegistroUnitaDocIn "
                + "and u.aaKeyUnitaDoc = :aaKeyUnitaDocIn " + "and u.dtAnnul > :dataDiOggiIn"; // esclude dalla ricerca
                                                                                               // le UD annullate
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        query.setParameter("idSubStruts", idSubStruts);
        query.setParameter("idRegistroUnitaDocIn", idRegistroUnitaDoc);
        query.setParameter("aaKeyUnitaDocIn", new BigDecimal(anno));
        query.setParameter("dataDiOggiIn", new Date());
        aroUnitaDocs = (List<AroUnitaDoc>) query.getResultList();
        return aroUnitaDocs;
    }

    public void sbloccaAaRegistroUnitaDoc(long idAaRegistroUnitaDoc) {
        String queryStr = "update DecAaRegistroUnitaDoc u " + "set u.flUpdFmtNumero = '0' "
                + "where u.idAaRegistroUnitaDoc = :idAaRegistroUnitaDocIn ";
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        query.setParameter("idAaRegistroUnitaDocIn", idAaRegistroUnitaDoc);
        query.executeUpdate();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void scriviErrorePerAnnoNewTrans(long idAaRegistroUnitaDoc, long anno, long idUnitaDoc,
            String errMessaggio) {

        this.rimuoviErroriRegAnno(idAaRegistroUnitaDoc, anno);
        //
        this.rimuoviWarningRegAnno(idAaRegistroUnitaDoc, anno);
        //
        DecErrAaRegistroUnitaDoc err = new DecErrAaRegistroUnitaDoc();
        err.setAaRegistroUnitaDoc(new BigDecimal(anno));
        err.setDsErrFmtNumero(errMessaggio);
        err.setIdUnitaDocErrFmtNumero(new BigDecimal(idUnitaDoc));
        err.setDecAaRegistroUnitaDoc(entityManager.find(DecAaRegistroUnitaDoc.class, idAaRegistroUnitaDoc));
        entityManager.persist(err);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void pulisciErroriRegAnnoNewTrans(long idAaRegistroUnitaDoc, long anno) {

        this.rimuoviErroriRegAnno(idAaRegistroUnitaDoc, anno);
        //
        this.rimuoviWarningRegAnno(idAaRegistroUnitaDoc, anno);
        //
    }

    private void rimuoviErroriRegAnno(long idAaRegistroUnitaDoc, long anno) {
        String queryStr = "delete from DecErrAaRegistroUnitaDoc u "
                + "where u.decAaRegistroUnitaDoc.idAaRegistroUnitaDoc = :idAaRegistroUnitaDocIn "
                + "and u.aaRegistroUnitaDoc = :aaRegistroUnitaDocIn ";
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        query.setParameter("idAaRegistroUnitaDocIn", idAaRegistroUnitaDoc);
        query.setParameter("aaRegistroUnitaDocIn", new BigDecimal(anno));
        query.executeUpdate();
    }

    private void rimuoviWarningRegAnno(long idAaRegistroUnitaDoc, long anno) {
        String queryStr = "update DecWarnAaRegistroUd al "
                + "set al.flWarnAaRegistroUnitaDoc = :flWarnAaRegistroUnitaDocIn "
                + "where al.decAaRegistroUnitaDoc.idAaRegistroUnitaDoc = :idAaRegistroUnitaDocIn "
                + "and al.aaRegistroUnitaDoc = :aaRegistroUnitaDocIn ";
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        query.setParameter("flWarnAaRegistroUnitaDocIn", "0");
        query.setParameter("idAaRegistroUnitaDocIn", idAaRegistroUnitaDoc);
        query.setParameter("aaRegistroUnitaDocIn", new BigDecimal(anno));
        query.executeUpdate();
    }
}
