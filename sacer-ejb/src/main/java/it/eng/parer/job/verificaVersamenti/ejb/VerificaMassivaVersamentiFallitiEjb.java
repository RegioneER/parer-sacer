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

package it.eng.parer.job.verificaVersamenti.ejb;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.async.ejb.CalcoloMonitoraggioAsync;
import it.eng.parer.async.helper.AsyncHelper;
import it.eng.parer.async.helper.CalcoloMonitoraggioHelper;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.utils.JobConstants;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "VerificaMassivaVersamentiFallitiEjb")
@LocalBean
public class VerificaMassivaVersamentiFallitiEjb {

    Logger log = LoggerFactory.getLogger(VerificaMassivaVersamentiFallitiEjb.class);
    @EJB
    private CalcoloMonitoraggioHelper calcoloHelper;
    @EJB
    private JobHelper jobHelper;
    @EJB
    private CalcoloMonitoraggioAsync calcoloAsync;
    @EJB
    private AsyncHelper asyncHelper;

    public void verificaMassivaVersamentiFalliti() {
	log.info("{} --- Chiamata per verifica massiva versamenti falliti",
		VerificaMassivaVersamentiFallitiEjb.class.getSimpleName());
	List<OrgStrut> struttureWithProblems = new ArrayList<>();
	jobHelper.writeAtomicLogJob(JobConstants.JobEnum.VERIFICA_MASSIVA_VERS_FALLITI.name(),
		JobConstants.OpTypeEnum.INIZIO_SCHEDULAZIONE.name(), null);
	log.info(
		"Recupero le strutture sulle quali eseguire la verifica massiva versamenti falliti");
	List<OrgStrut> struttureVersanti = calcoloHelper.getStruttureVersanti();
	log.info("Recuperate " + struttureVersanti.size()
		+ " strutture per verifica massiva versamenti falliti");
	/* Recupero l'ultima registrazione di VERIFICA_MASSIVA_VERS_FALLITI */
	Date ultimaRegistrazione = calcoloHelper
		.getUltimaRegistrazione(JobConstants.JobEnum.VERIFICA_MASSIVA_VERS_FALLITI.name());

	for (OrgStrut strutturaVersante : struttureVersanti) {
	    /*
	     * Verifica che non sia già attivo un verifica versamenti falliti, nel qual caso
	     * rilancia all'action un'eccezione
	     */
	    Long idLock;
	    log.info(VerificaMassivaVersamentiFallitiEjb.class.getSimpleName()
		    + " --- Acquisizione lock per verifica versamenti falliti per la struttura: "
		    + strutturaVersante.getIdStrut());
	    asyncHelper.initLockPerStrut(JobConstants.JobEnum.VERIFICA_VERS_FALLITI.name(),
		    strutturaVersante.getIdStrut());
	    idLock = asyncHelper.getLock(JobConstants.JobEnum.VERIFICA_VERS_FALLITI.name(),
		    strutturaVersante.getIdStrut());
	    if (idLock != null) {
		try {
		    log.info(VerificaMassivaVersamentiFallitiEjb.class.getSimpleName()
			    + " --- Chiamata per verifica versamenti falliti per la struttura: "
			    + strutturaVersante.getIdStrut());
		    jobHelper.writeAtomicLogJob(JobConstants.JobEnum.VERIFICA_VERS_FALLITI.name(),
			    JobConstants.OpTypeEnum.INIZIO_SCHEDULAZIONE.name(), null,
			    strutturaVersante.getIdStrut());
		    calcoloAsync.verificaVersamentiFalliti(strutturaVersante.getIdStrut(), idLock,
			    ultimaRegistrazione);
		    log.info(VerificaMassivaVersamentiFallitiEjb.class.getSimpleName()
			    + " --- FINE chiamata per verifica versamenti falliti per la struttura: "
			    + strutturaVersante.getIdStrut());
		} catch (ParerInternalError ex) {
		    // Inutile in quanto già gestita nell'interceptor all'interno della classe
		    // asincrona
		    log.error("Errore nel job di VERIFICA_VERSAMENTI_FALLITI per la struttura: "
			    + strutturaVersante.getIdStrut() + " "
			    + ExceptionUtils.getRootCauseMessage(ex), ex);
		    struttureWithProblems.add(strutturaVersante);
		}
	    } else {
		// Se è fallita l'acquisizione del lock
		log.error(
			"Errore nell'acquisizione del lock nel job di VERIFICA_VERSAMENTI_FALLITI per la struttura: "
				+ strutturaVersante.getIdStrut());
		struttureWithProblems.add(strutturaVersante);
	    }
	}
	if (struttureWithProblems.isEmpty()) {
	    jobHelper.writeAtomicLogJob(JobConstants.JobEnum.VERIFICA_MASSIVA_VERS_FALLITI.name(),
		    JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(), null);
	    log.info(VerificaMassivaVersamentiFallitiEjb.class.getSimpleName()
		    + " --- FINE chiamata per verifica massiva versamenti falliti");
	} else {
	    jobHelper.writeAtomicLogJob(JobConstants.JobEnum.VERIFICA_MASSIVA_VERS_FALLITI.name(),
		    JobConstants.OpTypeEnum.ERRORE.name(), "Problemi sulle seguenti strutture: "
			    + this.esplodiStrutture(struttureWithProblems).toString());
	    log.info(VerificaMassivaVersamentiFallitiEjb.class.getSimpleName()
		    + " --- ERRORE chiamata per verifica massiva versamenti falliti");
	}

    }

    List<String> esplodiStrutture(List<OrgStrut> strutture) {
	List<String> lista = new ArrayList<>();
	for (OrgStrut strut : strutture) {
	    lista.add(strut.getOrgEnte().getOrgAmbiente().getNmAmbiente() + " - "
		    + strut.getOrgEnte().getNmEnte() + " - " + strut.getNmStrut());
	}
	return lista;
    }
}
