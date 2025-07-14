/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.job.verificaCompRegistro.helper;

import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.DecAaRegistroUnitaDoc;
import it.eng.parer.entity.DecErrAaRegistroUnitaDoc;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *
 * @author fioravanti_f
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "VerificaCompRegHelper")
@LocalBean
public class VerificaCompRegHelper {
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    public List<DecAaRegistroUnitaDoc> getAaRegistroUnitaDocDaElab() {
	List<DecAaRegistroUnitaDoc> aaRegDaElabList;
	String queryStr = "SELECT u FROM DecAaRegistroUnitaDoc u "
		+ "where u.flUpdFmtNumero = '1' ";
	javax.persistence.Query query = entityManager.createQuery(queryStr);
	aaRegDaElabList = query.getResultList();
	return aaRegDaElabList;
    }

    public List<AroUnitaDoc> getListaUdDaVerificare(Long idRegistroUnitaDoc, List<Long> idSubStruts,
	    Long anno) {
	List<AroUnitaDoc> aroUnitaDocs;

	String queryStr = "select u from AroUnitaDoc u " + "where "
		+ "u.orgSubStrut.idSubStrut in :idSubStruts "
		+ "and u.decRegistroUnitaDoc.idRegistroUnitaDoc = :idRegistroUnitaDocIn "
		+ "and u.aaKeyUnitaDoc = :aaKeyUnitaDocIn " + "and u.dtAnnul > :dataDiOggiIn"; // esclude
											       // dalla
											       // ricerca
											       // le
											       // UD
											       // annullate
	javax.persistence.Query query = entityManager.createQuery(queryStr);
	query.setParameter("idSubStruts", idSubStruts);
	query.setParameter("idRegistroUnitaDocIn", idRegistroUnitaDoc);
	query.setParameter("aaKeyUnitaDocIn", new BigDecimal(anno));
	query.setParameter("dataDiOggiIn", new Date());
	aroUnitaDocs = query.getResultList();
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
	err.setDecAaRegistroUnitaDoc(
		entityManager.find(DecAaRegistroUnitaDoc.class, idAaRegistroUnitaDoc));
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
