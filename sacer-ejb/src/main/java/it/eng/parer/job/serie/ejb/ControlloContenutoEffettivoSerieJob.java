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

package it.eng.parer.job.serie.ejb;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.SerSerie;
import it.eng.parer.entity.SerVerSerieDaElab;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.serie.ejb.SerieEjb;
import it.eng.parer.serie.helper.SerieHelper;
import it.eng.parer.serie.utils.FutureUtils;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.UserHelper;
import it.eng.parer.ws.utils.CostantiDB;

/**
 *
 * @author bonora_l
 */
@Stateless
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class ControlloContenutoEffettivoSerieJob {

    private final Logger log = LoggerFactory.getLogger(ControlloContenutoEffettivoSerieJob.class);
    @Resource
    private SessionContext context;
    @EJB
    private JobHelper jobHelper;
    @EJB
    private SerieHelper serieHelper;
    @EJB
    private SerieEjb serieEjb;
    @EJB
    private ConfigurationHelper configHelper;
    @EJB
    private UserHelper userHelper;

    public void controllaContenutoEffettivo() throws ParerInternalError {

        log.info(JobConstants.JobEnum.CONTROLLO_AUTOMATICO_CONTENUTO_EFFETTIVO.name()
                + " --- ricerca le versioni serie con stato = DA_CONTROLLARE registrate in SER_VER_SERIE_DA_ELAB");
        List<SerVerSerieDaElab> verSerieDaElabAperte = serieHelper.getSerVerSerieDaElab(null,
                CostantiDB.StatoVersioneSerieDaElab.DA_CONTROLLARE.name());
        log.info(JobConstants.JobEnum.CONTROLLO_AUTOMATICO_CONTENUTO_EFFETTIVO.name() + " --- Trovati "
                + verSerieDaElabAperte.size() + " record");
        Map<String, BigDecimal> idVerSeries = new HashMap<>();
        // String nmUserId = configHelper.getValoreParamApplic("USERID_CREAZIONE_SERIE");
        // IamUser userCreazioneSerie = userHelper.findIamUser(nmUserId);

        for (SerVerSerieDaElab verSerieDaElab : verSerieDaElabAperte) {
            // Preparo per la generazione del contenuto effettivo.
            BigDecimal idVerSerie = new BigDecimal(verSerieDaElab.getSerVerSerie().getIdVerSerie());
            BigDecimal idSerie = new BigDecimal(verSerieDaElab.getSerVerSerie().getSerSerie().getIdSerie());
            log.debug(JobConstants.JobEnum.CONTROLLO_AUTOMATICO_CONTENUTO_EFFETTIVO.name()
                    + " --- preparo il controllo del contenuto");
            boolean result = context.getBusinessObject(ControlloContenutoEffettivoSerieJob.class).prepareCon(idSerie,
                    idVerSerie);
            if (result) {
                SerSerie serSerie = verSerieDaElab.getSerVerSerie().getSerSerie();
                String keyFuture = FutureUtils.buildKeyFuture(CostantiDB.TipoChiamataAsync.CONTROLLO_CONTENUTO.name(),
                        serSerie.getCdCompositoSerie(), serSerie.getAaSerie(), verSerieDaElab.getIdStrut(),
                        idVerSerie.longValue());
                idVerSeries.put(keyFuture, idVerSerie);
            }
        }

        if (!idVerSeries.isEmpty()) {
            serieEjb.callControlloContenutoAsync(idVerSeries);
        }

        log.info(JobConstants.JobEnum.CONTROLLO_AUTOMATICO_CONTENUTO_EFFETTIVO.name() + " --- Fine schedulazione job");
        jobHelper.writeAtomicLogJob(JobConstants.JobEnum.CONTROLLO_AUTOMATICO_CONTENUTO_EFFETTIVO.name(),
                JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name());
    }

    /**
     * Prepara i dati per il controllo del contenuto effettivo
     *
     * @param idSerie
     *            id serie
     * @param idVerSerie
     *            id versamento
     * 
     * @return true se la preparazione va a buon fine
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean prepareCon(BigDecimal idSerie, BigDecimal idVerSerie) {
        boolean result = false;
        if (serieEjb.checkVersione(idVerSerie, CostantiDB.StatoVersioneSerie.APERTA.name(),
                CostantiDB.StatoVersioneSerie.DA_CONTROLLARE.name())
                && serieEjb.checkContenuto(idVerSerie, false, false, true,
                        CostantiDB.StatoContenutoVerSerie.CREATO.name(),
                        CostantiDB.StatoContenutoVerSerie.DA_CONTROLLARE_CONSIST.name())) {
            try {
                serieEjb.initControlloContenuto(idSerie, idVerSerie, CostantiDB.TipoContenutoVerSerie.EFFETTIVO.name());
                result = true;
            } catch (ParerUserError ex) {
                log.debug(JobConstants.JobEnum.CONTROLLO_AUTOMATICO_CONTENUTO_EFFETTIVO.name() + " --- "
                        + ex.getDescription());
            }
        } else {
            log.debug(JobConstants.JobEnum.GENERAZIONE_AUTOMATICA_CONTENUTO_EFFETTIVO.name()
                    + " --- La versione non \u00E8 corrente, non ha stato APERTA o il contenuto CALCOLATO non ha stato CREATO o CONTROLLATA_CONSIST");
            // Elimina il record da serVerSerieDaElab e chiude senza generare il contenuto
            serieHelper.deleteSerVerSerieDaElab(idVerSerie);
        }

        return result;
    }
}
