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

package it.eng.parer.jboss.timers.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import it.eng.parer.jboss.timer.common.JbossJobTimer;
import it.eng.parer.job.timer.AggiornaStatoArchiviazioneTimer;
import it.eng.parer.job.timer.AllineamentoEntiConvenzionatiTimer;
import it.eng.parer.job.timer.AllineamentoOrganizzazioniTimer;
import it.eng.parer.job.timer.AnnullamentoVersamentiTimer;
import it.eng.parer.job.timer.CalcoloConsistenzaTimer;
import it.eng.parer.job.timer.CalcoloContenutoAggMetaTimer;
import it.eng.parer.job.timer.CalcoloContenutoFascicoliTimer;
import it.eng.parer.job.timer.CalcoloContenutoTimer;
import it.eng.parer.job.timer.CalcoloStrutturaTimer;
import it.eng.parer.job.timer.ControllaMigrazioneSubpartizioneTimer;
import it.eng.parer.job.timer.ControlloContenutoEffettivoSerieTimer;
import it.eng.parer.job.timer.CreateElencoTimer;
import it.eng.parer.job.timer.CreateIndexTimer;
import it.eng.parer.job.timer.CreazioneElenchiIndiciAipFascicoliTimer;
import it.eng.parer.job.timer.CreazioneElenchiIndiciAipTimer;
import it.eng.parer.job.timer.CreazioneElencoVersFascicoliTimer;
import it.eng.parer.job.timer.CreazioneIndiceAipFascicoliTimer;
import it.eng.parer.job.timer.CreazioneIndiceAipSerieUdTimer;
import it.eng.parer.job.timer.CreazioneIndiceAipTimer;
import it.eng.parer.job.timer.CreazioneIndiceVersFascicoliTimer;
import it.eng.parer.job.timer.CreazioneSerieTimer;
import it.eng.parer.job.timer.ElaboraSessioniRecuperoTimer;
import it.eng.parer.job.timer.GenerazioneContenutoEffettivoSerieTimer;
import it.eng.parer.job.timer.PreparaPartizioneDaMigrareTimer1;
import it.eng.parer.job.timer.PreparaPartizioneDaMigrareTimer2;
import it.eng.parer.job.timer.PreparaPartizioneDaMigrareTimer3;
import it.eng.parer.job.timer.PreparaPartizioneDaMigrareTimer4;
import it.eng.parer.job.timer.ProducerCodaDaMigrareTimer1;
import it.eng.parer.job.timer.ProducerCodaDaMigrareTimer2;
import it.eng.parer.job.timer.ProducerCodaDaMigrareTimer3;
import it.eng.parer.job.timer.ProducerCodaDaMigrareTimer4;
import it.eng.parer.job.timer.ProducerCodaIndiciAipDaElabTimer;
import it.eng.parer.job.timer.RegistraSchedulazioniJobTPITimer;
import it.eng.parer.job.timer.RestituzioneArchivioTimer;
import it.eng.parer.job.timer.SigilloTimer;
import it.eng.parer.job.timer.ValidazioneFascicoliTimer;
import it.eng.parer.job.timer.VerificaFirmeTimer;
import it.eng.parer.job.timer.VerificaMassivaVersamentiFallitiTimer;
import it.eng.parer.job.timer.VerificaMigrazioneSubPartizioneTimer;
import it.eng.parer.job.timer.VerificaPeriodoRegistroTimer;
import it.eng.parer.job.timer.VerificaPeriodoTipoFascTimer;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.sacerlog.job.SacerLogAllineamentoTimer;
import it.eng.parer.sacerlog.job.SacerLogTimer;

/**
 * Singleton utilizzato per censire tutti i timer di Sacer.
 *
 * @author Snidero_L
 */
@Singleton
public class TimerRepository {

    @EJB
    private CreateElencoTimer createElencoTimer;
    @EJB
    private CreateIndexTimer createIndiceTimer;
    @EJB
    private VerificaFirmeTimer verificaFirmeTimer;
    @EJB
    private ProducerCodaIndiciAipDaElabTimer producerCodaIndiciAipDaElabTimer;
    @EJB
    private CalcoloContenutoTimer calcoloContenutoTimer;
    @EJB
    private CalcoloConsistenzaTimer calcoloConsistenzaTimer;
    @EJB
    private AllineamentoOrganizzazioniTimer allineamentoOrganizzazioniTimer;
    @EJB
    private AggiornaStatoArchiviazioneTimer aggiornaStatoArchiviazioneTimer;
    @EJB
    private ElaboraSessioniRecuperoTimer elaboraSessioniRecuperoTimer;
    @EJB
    private RegistraSchedulazioniJobTPITimer registraSchedulazioniJobTpiTimer;
    @EJB
    private CreazioneIndiceAipTimer creazioneIndiceAipTimer;
    @EJB
    private VerificaMassivaVersamentiFallitiTimer verificaMassivaVersamentiFallitiTimer;
    @EJB
    private VerificaPeriodoRegistroTimer verificaPeriodoRegistroTimer;
    @EJB
    private CreazioneSerieTimer creazioneSerieTimer;
    @EJB
    private CreazioneIndiceAipSerieUdTimer creazioneIndiceAipSerieUdTimer;
    @EJB
    private GenerazioneContenutoEffettivoSerieTimer generazioneContenutoEffettivoSerieTimer;
    @EJB
    private ControlloContenutoEffettivoSerieTimer controlloContenutoEffettivoSerieTimer;
    @EJB
    private AnnullamentoVersamentiTimer annullamentoVersamentiTimer;
    @EJB
    private CalcoloStrutturaTimer calcoloStrutturaTimer;
    @EJB
    private RestituzioneArchivioTimer restituzioneArchivioTimer;
    @EJB
    private AllineamentoEntiConvenzionatiTimer allineamentoEntiConvenzionatiTimer;
    @EJB
    private SigilloTimer sigilloTimer;
    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogTimer")
    private SacerLogTimer sacerLogTimer;
    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogAllineamentoTimer")
    private SacerLogAllineamentoTimer sacerLogAllineamentoTimer;
    @EJB
    private CreazioneElenchiIndiciAipTimer creazioneElenchiIndiciAipTimer;
    @EJB
    private CalcoloContenutoFascicoliTimer calcoloContenutoFascicoliTimer;
    @EJB
    private VerificaPeriodoTipoFascTimer verificaPeriodoTipoFascTimer;
    @EJB
    private CreazioneElencoVersFascicoliTimer creazioneElencoVersFascicoliTimer;
    @EJB
    private CreazioneIndiceVersFascicoliTimer creazioneIndiceVersFascicoliTimer;
    @EJB
    private ValidazioneFascicoliTimer validazioneFascicoliTimer;
    @EJB
    private CreazioneIndiceAipFascicoliTimer creazioneIndiceAipFascicoliTimer;
    @EJB
    private CreazioneElenchiIndiciAipFascicoliTimer creazioneElenchiIndiciAipFascicoliTimer;
    @EJB
    private ProducerCodaDaMigrareTimer1 producerCodaDaMigrareTimer1;
    @EJB
    private ProducerCodaDaMigrareTimer2 producerCodaDaMigrareTimer2;
    @EJB
    private ProducerCodaDaMigrareTimer3 producerCodaDaMigrareTimer3;
    @EJB
    private ProducerCodaDaMigrareTimer4 producerCodaDaMigrareTimer4;
    @EJB
    private PreparaPartizioneDaMigrareTimer1 preparaPartizioneDaMigrareTimer1;
    @EJB
    private PreparaPartizioneDaMigrareTimer2 preparaPartizioneDaMigrareTimer2;
    @EJB
    private PreparaPartizioneDaMigrareTimer3 preparaPartizioneDaMigrareTimer3;
    @EJB
    private PreparaPartizioneDaMigrareTimer4 preparaPartizioneDaMigrareTimer4;
    @EJB
    private VerificaMigrazioneSubPartizioneTimer verificaMigrazioneSubPartizioneTimer;
    @EJB
    private ControllaMigrazioneSubpartizioneTimer controllaMigrazioneSubpartizioneTimer;
    @EJB
    private CalcoloContenutoAggMetaTimer calcoloContenutoAggMetaTimer;

    private Map<String, JbossJobTimer> map;

    @PostConstruct
    public void initialize() {
        map = new HashMap<>();
        map.put(JobConstants.JobEnum.CREAZIONE_ELENCHI_VERS.name(), createElencoTimer);
        map.put(JobConstants.JobEnum.CREAZIONE_INDICI_ELENCHI_VERS.name(), createIndiceTimer);
        map.put(JobConstants.JobEnum.VERIFICA_FIRME_A_DATA_VERS.name(), verificaFirmeTimer);
        map.put(JobConstants.JobEnum.PRODUCER_CODA_INDICI_AIP_DA_ELAB.name(), producerCodaIndiciAipDaElabTimer);
        map.put(JobConstants.JobEnum.CALCOLO_CONTENUTO_SACER.name(), calcoloContenutoTimer);
        map.put(JobConstants.JobEnum.CALCOLO_CONSISTENZA.name(), calcoloConsistenzaTimer);
        map.put(JobConstants.JobEnum.ALLINEAMENTO_ORGANIZZAZIONI.name(), allineamentoOrganizzazioniTimer);
        map.put(JobConstants.JobEnum.AGGIORNA_STATO_ARCHIVIAZIONE.name(), aggiornaStatoArchiviazioneTimer);
        map.put(JobConstants.JobEnum.ELABORA_SESSIONI_RECUPERO.name(), elaboraSessioniRecuperoTimer);
        map.put(JobConstants.JobEnum.REGISTRA_SCHEDULAZIONI_JOB_TPI.name(), registraSchedulazioniJobTpiTimer);
        map.put(JobConstants.JobEnum.CREAZIONE_INDICE_AIP.name(), creazioneIndiceAipTimer);
        map.put(JobConstants.JobEnum.VERIFICA_MASSIVA_VERS_FALLITI.name(), verificaMassivaVersamentiFallitiTimer);
        map.put(JobConstants.JobEnum.VERIFICA_COMPATIBILITA_REGISTRO.name(), verificaPeriodoRegistroTimer);
        map.put(JobConstants.JobEnum.CREAZIONE_AUTOMATICA_SERIE.name(), creazioneSerieTimer);
        map.put(JobConstants.JobEnum.CREAZIONE_INDICE_AIP_SERIE_UD.name(), creazioneIndiceAipSerieUdTimer);
        map.put(JobConstants.JobEnum.GENERAZIONE_AUTOMATICA_CONTENUTO_EFFETTIVO.name(),
                generazioneContenutoEffettivoSerieTimer);
        map.put(JobConstants.JobEnum.CONTROLLO_AUTOMATICO_CONTENUTO_EFFETTIVO.name(),
                controlloContenutoEffettivoSerieTimer);
        map.put(JobConstants.JobEnum.EVASIONE_RICH_ANNUL_VERS.name(), annullamentoVersamentiTimer);
        map.put(JobConstants.JobEnum.CALCOLA_STRUTTURA_JOB.name(), calcoloStrutturaTimer);
        map.put(JobConstants.JobEnum.EVASIONE_RICH_REST_ARCH.name(), restituzioneArchivioTimer);
        map.put(JobConstants.JobEnum.ALLINEA_ENTI_CONVENZIONATI.name(), allineamentoEntiConvenzionatiTimer);
        map.put(JobConstants.JobEnum.SIGILLO.name(), sigilloTimer);
        map.put(it.eng.parer.sacerlog.job.Constants.NomiJob.INIZIALIZZAZIONE_LOG.name(), sacerLogTimer);
        map.put(it.eng.parer.sacerlog.job.Constants.NomiJob.ALLINEAMENTO_LOG.name(), sacerLogAllineamentoTimer);
        map.put(JobConstants.JobEnum.CREAZIONE_ELENCHI_INDICI_AIP_UD.name(), creazioneElenchiIndiciAipTimer);
        map.put(JobConstants.JobEnum.CALCOLO_CONTENUTO_FASCICOLI.name(), calcoloContenutoFascicoliTimer);
        map.put(JobConstants.JobEnum.VERIFICA_COMPATIBILITA_TIPO_FASC.name(), verificaPeriodoTipoFascTimer);
        map.put(JobConstants.JobEnum.CREAZIONE_ELENCHI_VERS_FASCICOLI.name(), creazioneElencoVersFascicoliTimer);
        map.put(JobConstants.JobEnum.CREAZIONE_INDICI_ELENCHI_VERS_FASC.name(), creazioneIndiceVersFascicoliTimer);
        map.put(JobConstants.JobEnum.VALIDAZIONE_FASCICOLI.name(), validazioneFascicoliTimer);
        map.put(JobConstants.JobEnum.CREAZIONE_INDICE_AIP_FASC.name(), creazioneIndiceAipFascicoliTimer);
        map.put(JobConstants.JobEnum.CREAZIONE_ELENCHI_INDICI_AIP_FASC.name(), creazioneElenchiIndiciAipFascicoliTimer);
        map.put(JobConstants.JobEnum.PREPARA_PARTIZIONE_DA_MIGRARE_1.name(), preparaPartizioneDaMigrareTimer1);
        map.put(JobConstants.JobEnum.PREPARA_PARTIZIONE_DA_MIGRARE_2.name(), preparaPartizioneDaMigrareTimer2);
        map.put(JobConstants.JobEnum.PREPARA_PARTIZIONE_DA_MIGRARE_3.name(), preparaPartizioneDaMigrareTimer3);
        map.put(JobConstants.JobEnum.PREPARA_PARTIZIONE_DA_MIGRARE_4.name(), preparaPartizioneDaMigrareTimer4);
        map.put(JobConstants.JobEnum.PRODUCER_CODA_DA_MIGRARE_1.name(), producerCodaDaMigrareTimer1);
        map.put(JobConstants.JobEnum.PRODUCER_CODA_DA_MIGRARE_2.name(), producerCodaDaMigrareTimer2);
        map.put(JobConstants.JobEnum.PRODUCER_CODA_DA_MIGRARE_3.name(), producerCodaDaMigrareTimer3);
        map.put(JobConstants.JobEnum.PRODUCER_CODA_DA_MIGRARE_4.name(), producerCodaDaMigrareTimer4);
        map.put(JobConstants.JobEnum.VERIFICA_MIGRAZIONE_SUBPARTIZIONE.name(), verificaMigrazioneSubPartizioneTimer);
        map.put(JobConstants.JobEnum.CONTROLLA_MIGRAZIONE_SUBPARTIZIONE.name(), controllaMigrazioneSubpartizioneTimer);
        map.put(JobConstants.JobEnum.CALCOLO_CONTENUTO_AGGIORNAMENTI_METADATI.name(), calcoloContenutoAggMetaTimer);
    }

    /**
     * Ottieni i nomi di tutti i timer configurati su Sacer.
     *
     * @return insieme dei nomi di tutti i timer.
     */
    @Lock(LockType.READ)
    public Set<String> getConfiguredTimersName() {
        return map.keySet();
    }

    /**
     * Ottieni l'implementazione del timer definito.
     *
     * @param jobName
     *            nome del job
     * 
     * @return istanza del timer oppure null
     */
    @Lock(LockType.READ)
    public JbossJobTimer getConfiguredTimer(String jobName) {
        return map.get(jobName);
    }

}
