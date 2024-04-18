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

package it.eng.parer.job.indiceAip.ejb;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper.StruttureHelper;
import it.eng.parer.elencoVersamento.helper.ElencoVersamentoHelper;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.entity.ElvElencoVer;
import it.eng.parer.entity.ElvElencoVersDaElab;
import it.eng.parer.entity.constraint.ElvStatoElencoVer;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.indiceAip.elenchi.ElaborazioneElencoIndiceAip;
import it.eng.parer.job.indiceAip.helper.CreazioneIndiceAipHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.viewEntity.ElvVChkIxAipUdGen;
import it.eng.parer.web.ejb.ElenchiVersamentoEjb;
import it.eng.parer.web.helper.ConfigurationHelper;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "CreazioneIndiceAipEjb")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class CreazioneIndiceAipEjb {

    Logger log = LoggerFactory.getLogger(CreazioneIndiceAipEjb.class);
    @EJB
    private CreazioneIndiceAipHelper ciaHelper;
    @EJB
    private JobHelper jobHelper;
    @EJB
    private ElaborazioneRigaIndiceAipDaElab elaborazione;
    @EJB
    private ElaborazioneElencoIndiceAip elabElencoIndiciAip;
    @EJB
    private ElencoVersamentoHelper elencoHelper;
    @EJB
    private StruttureHelper struttureHelper;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private CreazioneIndiceAipEjb me;
    @EJB
    private ElenchiVersamentoEjb evEjb;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void creazioneIndiceAip() throws ParerInternalError, ParseException {
        /* Il sistema apre una nuova transazione */
        log.debug("Creazione Indice AIP - Inizio transazione di creazione indice");
        /* Reupero gli indici da elaborare */
        log.debug("Creazione Indice AIP - Recupero gli AroIndiceAipUdDaElab ");
        List<Long> udDaElabList = ciaHelper.getIndexAplIndiceAipUdDaElab();
        log.info("Creazione Indice AIP - Ottenuti {} indici AIP da elaborare", udDaElabList.size());
        /* Per ogni unità documentaria presente nella coda */
        for (Long udDaElab : udDaElabList) {
            elaborazione.gestisciIndiceAipDaElaborareNelJob(udDaElab);
        }
        /* Scrivo nel LogJob la fine corretta dell'esecuzione del job di creazione indice AIP */
        jobHelper.writeAtomicLogJob(JobConstants.JobEnum.CREAZIONE_INDICE_AIP.name(),
                JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(), null);
        log.debug("Creazione Indice AIP - Chiusura transazione di creazione indice");
    }

    /*
     * Questo codice è stato messo in una nuova transazione altrimentirimaneva loccatoil record dell elenco MAC#16424
     * 
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setIndiciAipGeneratiInNewTransaction(long idElenco) {
        ElvElencoVer elenco = ciaHelper.findById(ElvElencoVer.class, idElenco);
        // Vista per verificare che tutte le ud e doc aggiunti e aggiornamenti metadati appartenenti all'indice aip
        // abbiano stato IN_ELENCO_CON_INDICI_AIP_GENERATI
        ElvVChkIxAipUdGen view = ciaHelper.findViewById(ElvVChkIxAipUdGen.class, BigDecimal.valueOf(idElenco));
        if (view.getFlIxAipUdGenOk().equals("1")) {
            log.debug(
                    "Creazione Indice AIP - per l'elenco tutte le ud e i doc aggiunti hanno stato IN_ELENCO_CON_INDICI_AIP_GENERATI - aggiorno l'elenco");
            elenco.setTiStatoElenco(ElencoEnums.ElencoStatusEnum.INDICI_AIP_GENERATI.name());

            // EVO 19304
            evEjb.registraStatoElencoVersamento(BigDecimal.valueOf(idElenco), "ESEGUITA_CREAZIONE_INDICE_AIP",
                    "Tutte unità documentarie non annullate hanno stato = IN_ELENCO_CON_INDICI_AIP_GENERATI",
                    ElvStatoElencoVer.TiStatoElenco.INDICI_AIP_GENERATI, null);

            ElvElencoVersDaElab elencoDaElab = elencoHelper.retrieveElencoInQueue(elenco);
            if (elencoDaElab != null) {
                elencoDaElab.setTiStatoElenco(ElencoEnums.ElencoStatusEnum.INDICI_AIP_GENERATI.name());
                elencoDaElab.setTsStatoElenco(new Date());
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void creazioneElenchiIndiceAip(long idLogJob)
            throws ParerInternalError, IOException, JAXBException, NoSuchAlgorithmException, ParseException {
        // MAC#16424
        List<Long> idElenchiInCoda = elencoHelper
                .retrieveElenchiIndiciAipDaProcessare(ElencoEnums.ElencoStatusEnum.IN_CODA_INDICE_AIP.name());
        if (!idElenchiInCoda.isEmpty()) {
            for (Long idElenco : idElenchiInCoda) {
                me.setIndiciAipGeneratiInNewTransaction(idElenco);
            }
        }
        // Fine MAC#16424

        List<Long> idElenchi = elencoHelper
                .retrieveElenchiIndiciAipDaProcessare(ElencoEnums.ElencoStatusEnum.INDICI_AIP_GENERATI.name());
        Set<Long> struttureVerificate = new HashSet<>();
        if (!idElenchi.isEmpty()) {
            for (Long idElenco : idElenchi) {
                ElvElencoVer elenco = ciaHelper.findById(ElvElencoVer.class, idElenco);
                struttureVerificate.add(elenco.getOrgStrut().getIdStrut());
                elabElencoIndiciAip.creaElencoIndiciAIP(idElenco, idLogJob);
            }
        }
        /* Scrivo nel LogJob la fine corretta dell'esecuzione del job di creazione elenchi indici AIP */
        jobHelper.writeAtomicLogJob(JobConstants.JobEnum.CREAZIONE_ELENCHI_INDICI_AIP_UD.name(),
                JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(), null);
        log.debug("Creazione Indice AIP - Chiusura transazione di creazione elenchi indici AIP");
    }

}
