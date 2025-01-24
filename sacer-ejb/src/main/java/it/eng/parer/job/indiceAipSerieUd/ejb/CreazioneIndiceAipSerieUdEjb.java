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

package it.eng.parer.job.indiceAipSerieUd.ejb;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.SerVerSerieDaElab;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.indiceAipSerieUd.helper.CreazioneIndiceAipSerieUdHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.serie.helper.SerieHelper;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.ws.utils.CostantiDB;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "CreazioneIndiceAipSerieUdEjb")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
/**
 * Classe contenente il metodo richiamato dal job per la creazione indice AIP versione serie ud
 *
 */
public class CreazioneIndiceAipSerieUdEjb {

    Logger log = LoggerFactory.getLogger(CreazioneIndiceAipSerieUdEjb.class);
    @EJB
    private ElaborazioneRigaVersioneSerieUdDaElab elaborazioneRigaIndiceAip;
    @EJB
    private CreazioneIndiceAipSerieUdHelper ciaHelper;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private SerieHelper serieHelper;
    @EJB
    private JobHelper jobHelper;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void creazioneIndiceAipSerieUd() throws Exception {
        log.info("{} --- Creazione Indice Aip Versione Serie Ud --- Inizio transazione di creazione indice",
                CreazioneIndiceAipSerieUdEjb.class.getSimpleName());
        List<OrgStrut> strutture = serieHelper.retrieveStrutture();

        // Per ogni struttura versante
        for (OrgStrut struttura : strutture) {
            manageStrut(new BigDecimal(struttura.getIdStrut()));
        }

        /* Scrivo nel LogJob la fine corretta dell'esecuzione del job di creazione indice AIP Serie Ud */
        jobHelper.writeAtomicLogJob(JobConstants.JobEnum.CREAZIONE_INDICE_AIP_SERIE_UD.name(),
                JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(), null);
        log.info("{}--- Creazione Indice Aip Versione Serie Ud --- - Chiusura transazione di creazione indice",
                this.getClass().getSimpleName());
    }

    public void manageStrut(BigDecimal idStrut) throws Exception {

        /* Recupero parametro CREATING_APPLICATION_PRODUCER */
        String creatingApplicationProducer = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.CREATING_APPLICATION_PRODUCER);

        /*
         * Il sistema determina le versioni correnti delle serie con stato corrente = VALIDATA della struttura versante
         */
        List<SerVerSerieDaElab> verSerieDaElabList = ciaHelper.getSerVerSerieDaElab(idStrut,
                CostantiDB.StatoVersioneSerieDaElab.VALIDATA.name());
        log.info(
                "{} --- Creazione Indice Aip Versione Serie Ud --- Ottenute {} versioni serie da elaborare per la struttura {}",
                this.getClass().getSimpleName(), verSerieDaElabList.size(), idStrut);

        /* Per ogni VERSIONE serie recuperata */
        try {
            for (SerVerSerieDaElab verSerieDaElab : verSerieDaElabList) {
                elaborazioneRigaIndiceAip.gestisciIndiceAipSerieUdDaElab(verSerieDaElab, creatingApplicationProducer);
            }
            log.info(
                    "{} --- Creazione Indice Aip Versione Serie Ud --- Elaborate {} versioni serie con successo per la struttura {}",
                    this.getClass().getSimpleName(), verSerieDaElabList.size(), idStrut);
        } catch (Exception ex) {
            log.error("{}--- Creazione Indice Aip Versione Serie Ud --- Errore: {}", this.getClass().getSimpleName(),
                    ExceptionUtils.getRootCauseMessage(ex));
            throw new ParerInternalError(CreazioneIndiceAipSerieUdEjb.class.getSimpleName()
                    + "--- Creazione Indice Aip Versione Serie Ud --- Errore: "
                    + ExceptionUtils.getRootCauseMessage(ex));
        }
    }

}
