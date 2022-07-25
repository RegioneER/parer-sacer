/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.job.verificaCompTipoFasc.helper;

import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.DecAaRegistroUnitaDoc;
import it.eng.parer.entity.DecAaTipoFascicolo;
import it.eng.parer.entity.DecErrAaRegistroUnitaDoc;
import it.eng.parer.entity.DecErrAaTipoFascicolo;
import it.eng.parer.entity.FasFascicolo;

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
 * @author sinatti_s
 */
@Stateless(mappedName = "VerificaCompTipoFascHelper")
@LocalBean
public class VerificaCompTipoFascHelper {

    private static final Logger log = LoggerFactory.getLogger(VerificaCompTipoFascHelper.class);
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    public List<DecAaTipoFascicolo> getAaTipoFascicoloDaElab() {
        List<DecAaTipoFascicolo> aaTipoFascicolos;
        String queryStr = "SELECT d FROM DecAaTipoFascicolo d " + "where d.flUpdFmtNumero = '1' ";
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        aaTipoFascicolos = (List<DecAaTipoFascicolo>) query.getResultList();
        return aaTipoFascicolos;
    }

    public List<FasFascicolo> getListaFasFascicoloDaVerificare(long idTipoFascicolo, long idStrut, Long anno) {
        List<FasFascicolo> fasFascicolos;

        String queryStr = "select f from FasFascicolo f " + "where " + "f.orgStrut.idStrut = :idStrut "
                + "and f.decTipoFascicolo.idTipoFascicolo = :idTipoFascicolo " + "and f.aaFascicolo = :aaFascicolo "
                + "and f.dtAnnull > :dataDiOggiIn"; // esclude dalla ricerca i fascicoli annullati
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        query.setParameter("idStrut", idStrut);
        query.setParameter("idTipoFascicolo", idTipoFascicolo);
        query.setParameter("aaFascicolo", new BigDecimal(anno));
        query.setParameter("dataDiOggiIn", new Date());
        fasFascicolos = (List<FasFascicolo>) query.getResultList();
        return fasFascicolos;
    }

    public void sbloccaAaTipoFascicolo(long idAaTipoFasc) {
        String queryStr = "update DecAaTipoFascicolo d " + "set d.flUpdFmtNumero = '0' "
                + "where d.idAaTipoFascicolo = :idAaTipoFascicolo ";
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        query.setParameter("idAaTipoFascicolo", idAaTipoFasc);
        query.executeUpdate();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void scriviErrorePerAnnoNewTrans(long idAaTipoFasc, long anno, long idFascicolo, String errMessaggio) {

        this.rimuoviErroriTipoFascAnno(idAaTipoFasc, anno);
        //
        this.rimuoviWarningTipoFascAnno(idAaTipoFasc, anno);
        //
        DecErrAaTipoFascicolo err = new DecErrAaTipoFascicolo();
        err.setAaFascicolo(new BigDecimal(anno));
        err.setDsErrFmtNumero(errMessaggio);
        err.setDecAaTipoFascicolo(entityManager.find(DecAaTipoFascicolo.class, idAaTipoFasc));
        err.setIdFascicoloErrFmtNumero(new BigDecimal(idFascicolo));
        entityManager.persist(err);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void pulisciErroriRegAnnoNewTrans(long idAaRegistroUnitaDoc, long anno) {

        this.rimuoviErroriTipoFascAnno(idAaRegistroUnitaDoc, anno);
        //
        this.rimuoviWarningTipoFascAnno(idAaRegistroUnitaDoc, anno);
        //
    }

    private void rimuoviErroriTipoFascAnno(long idAaTipoFascicolo, long anno) {
        String queryStr = "delete from DecErrAaTipoFascicolo d "
                + "where d.decAaTipoFascicolo.idAaTipoFascicolo = :idAaTipoFascicolo "
                + "and d.aaFascicolo = :aaFascicolo ";
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        query.setParameter("idAaTipoFascicolo", idAaTipoFascicolo);
        query.setParameter("aaFascicolo", new BigDecimal(anno));
        query.executeUpdate();
    }

    private void rimuoviWarningTipoFascAnno(long idAaTipoFascicolo, long anno) {
        String queryStr = "update DecWarnAaTipoFascicolo d " + "set d.flWarnAaTipoFascicolo = :flWarnAaTipoFascicolo "
                + "where d.decAaTipoFascicolo.idAaTipoFascicolo = :idAaTipoFascicolo "
                + "and d.aaTipoFascicolo = :aaTipoFascicolo ";
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        query.setParameter("flWarnAaTipoFascicolo", "0");
        query.setParameter("idAaTipoFascicolo", idAaTipoFascicolo);
        query.setParameter("aaTipoFascicolo", new BigDecimal(anno));
        query.executeUpdate();
    }
}
