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

package it.eng.parer.job.indiceAipFascicoli.ejb;

import it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper.StruttureHelper;
import it.eng.parer.elencoVersFascicoli.helper.ElencoVersFascicoliHelper;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.indiceAipFascicoli.elenchi.ElaborazioneElencoIndiceAipFascicoli;
import it.eng.parer.job.indiceAipFascicoli.helper.CreazioneIndiceAipFascicoliHelper;
import it.eng.parer.job.utils.JobConstants;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.naming.NamingException;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DiLorenzo_F
 */
@Stateless(mappedName = "CreazioneIndiceAipFascicoliEjb")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class CreazioneIndiceAipFascicoliEjb {

    Logger log = LoggerFactory.getLogger(CreazioneIndiceAipFascicoliEjb.class);
    @EJB
    private CreazioneIndiceAipFascicoliHelper ciafHelper;
    @EJB
    private JobHelper jobHelper;
    @EJB
    private ElaborazioneRigaVersioneFascicoliDaElab elaborazione;
    @EJB
    private ElaborazioneElencoIndiceAipFascicoli elabElencoIndiciAipFascicoli;
    @EJB
    private ElencoVersFascicoliHelper elencoHelper;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void creazioneIndiceAipFascicoli() throws ParerInternalError, Exception {
        /* Il sistema apre una nuova transazione */
        log.debug("Creazione Indice AIP Fascicoli - Inizio transazione di creazione indice");
        /* Reupero gli indici da elaborare */
        log.debug("Creazione Indice AIP Fascicoli - Recupero gli FasAipFascicoloDaElab ");
        List<Long> fascDaElabList = ciafHelper.getIndexFasAipFascicoloDaElab();
        log.info("Creazione Indice AIP Fascicoli - Ottenuti " + fascDaElabList.size() + " indici AIP da elaborare");

        /* Per ogni fascicolo presente nella coda */
        try {
            for (Long fascDaElab : fascDaElabList) {
                elaborazione.gestisciIndiceAipFascicoliDaElab(fascDaElab);
            }
        } catch (IOException | NamingException | NoSuchAlgorithmException ex) {
            log.error("Creazione Indice AIP fascicoli - Errore: " + ex);
            throw new ParerInternalError(ex);
        }

        /* Scrivo nel LogJob la fine corretta dell'esecuzione del job di creazione indice AIP fascicoli */
        jobHelper.writeAtomicLogJob(JobConstants.JobEnum.CREAZIONE_INDICE_AIP_FASC.name(),
                JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(), null);
        log.debug("Creazione Indice AIP Fascicoli - Chiusura transazione di creazione indice");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void creazioneElenchiIndiceAipFascicoli(long idLogJob) throws ParerInternalError, IOException,
            DatatypeConfigurationException, JAXBException, ParseException, NoSuchAlgorithmException {
        List<Long> idElenchi = elencoHelper.retrieveElenchiIndiciAipFascicoliDaProcessare();
        if (!idElenchi.isEmpty()) {
            for (Long idElenco : idElenchi) {
                elabElencoIndiciAipFascicoli.creaElencoIndiciAIPFascicoli(idElenco, idLogJob);
            }
        }
        /* Scrivo nel LogJob la fine corretta dell'esecuzione del job di creazione elenchi indici AIP */
        jobHelper.writeAtomicLogJob(JobConstants.JobEnum.CREAZIONE_ELENCHI_INDICI_AIP_FASC.name(),
                JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(), null);
        log.debug("Creazione Indice AIP Fascicoli - Chiusura transazione di creazione elenchi indici AIP Fascicoli");
    }

}
