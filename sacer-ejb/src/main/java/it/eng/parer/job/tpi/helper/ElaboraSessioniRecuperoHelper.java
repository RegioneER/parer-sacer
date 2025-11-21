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

package it.eng.parer.job.tpi.helper;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.RecDtVersRecup;
import it.eng.parer.entity.RecSessioneRecup;
import it.eng.parer.entity.VrsDtVers;
import it.eng.parer.job.utils.JobConstants;

/**
 *
 * @author Bonora_L
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "ElaboraSessioniRecuperoHelper")
@LocalBean
@Interceptors({
        it.eng.parer.aop.TransactionInterceptor.class })
public class ElaboraSessioniRecuperoHelper {

    Logger log = LoggerFactory.getLogger(ElaboraSessioniRecuperoHelper.class);
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    @EJB
    private ElaboraSessioniRecuperoHelper me;

    public List<RecSessioneRecup> getSessioniRecuperoInCorso() {
        javax.persistence.Query query = entityManager.createQuery(
                "SELECT s FROM RecSessioneRecup s WHERE s.tiStatoSessioneRecup = :stato");
        query.setParameter("stato", JobConstants.StatoSessioniRecupEnum.IN_CORSO.name());
        return query.getResultList();
    }

    public List<VrsDtVers> getVrsDtVersByDate(/* MAC#27666 */LocalDate dtVers/* end MAC#27666 */) {
        javax.persistence.Query query = entityManager
                .createQuery("SELECT v FROM VrsDtVers v WHERE v.dtVers = :data");
        query.setParameter("data", dtVers);

        return query.getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void retrieveFileUnitaDoc(Long idRecDtVersRecup, int countRecuperate,
            int sizeRecDtVersRecup) {
        RecDtVersRecup recDtVersRecup = entityManager.find(RecDtVersRecup.class, idRecDtVersRecup);
        me.setStatoRecuperata(recDtVersRecup, countRecuperate == sizeRecDtVersRecup);
    }

    public void setStatoRecuperata(RecDtVersRecup recDtVersRecup, boolean closeSession) {
        recDtVersRecup.setTiStatoDtVersRecup(JobConstants.StatoDtVersRecupEnum.RECUPERATA.name());
        if (closeSession) {
            recDtVersRecup.getRecSessioneRecup()
                    .setTiStatoSessioneRecup(JobConstants.StatoSessioniRecupEnum.CHIUSO_OK.name());
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setStatoSessErrata(Long idRecDtVersRecup, String causa) {
        RecDtVersRecup recDtVersRecup = entityManager.find(RecDtVersRecup.class, idRecDtVersRecup);
        recDtVersRecup.setTiStatoDtVersRecup(JobConstants.StatoDtVersRecupEnum.ERRORE.name());
        recDtVersRecup.getRecSessioneRecup().setDtChiusura(new Date());
        recDtVersRecup.getRecSessioneRecup()
                .setTiStatoSessioneRecup(JobConstants.StatoSessioniRecupEnum.CHIUSO_ERR.name());
        recDtVersRecup.getRecSessioneRecup().setDlErr(causa);
        recDtVersRecup.getRecSessioneRecup().setCdErr("--");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setSessioneChiusoOk(Long idRecSessioneRecup) {
        RecSessioneRecup recSessioneRecup = entityManager.find(RecSessioneRecup.class,
                idRecSessioneRecup);
        recSessioneRecup
                .setTiStatoSessioneRecup(JobConstants.StatoSessioniRecupEnum.CHIUSO_OK.name());
    }

}
