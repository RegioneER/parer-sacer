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

package it.eng.parer.async.ejb;

import it.eng.parer.async.helper.AsyncHelper;
import it.eng.parer.async.helper.CalcoloMonitoraggioHelper;
import it.eng.parer.exception.AsyncException;
import it.eng.parer.exception.ParerErrorSeverity;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.viewEntity.VrsVVersFallitiDaNorisol;
import it.eng.parer.viewEntity.VrsVVersFallitiDaVerif;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Bonora_L
 */
@Stateless(mappedName = "CalcoloMonitoraggioAsync")
@LocalBean
@Interceptors({
        it.eng.parer.async.aop.LockInterceptor.class,
        it.eng.parer.aop.TransactionInterceptor.class })
public class CalcoloMonitoraggioAsync {

    Logger log = LoggerFactory.getLogger(CalcoloMonitoraggioAsync.class);

    @Resource
    private SessionContext context;

    @EJB
    private JobHelper jobHelper;

    @EJB
    private CalcoloMonitoraggioHelper calcoloHelper;

    @EJB
    private AsyncHelper asyncHelper;

    public boolean calcolaStruttura() {
        /*
         * Verifica che non sia già attivo un calcolo struttura, nel qual caso rilancia all'action
         * un eccezione
         */
        Long idLock;
        try {
            idLock = asyncHelper.getLock(JobConstants.JobEnum.CALCOLA_STRUTTURA.name(), null);
            if (idLock != null) {
                jobHelper.writeLogJob(JobConstants.JobEnum.CALCOLA_STRUTTURA.name(),
                        JobConstants.OpTypeEnum.INIZIO_SCHEDULAZIONE.name(), null, null);
                context.getBusinessObject(CalcoloMonitoraggioAsync.class)
                        .eseguiCalcoloStruttura(idLock);
                return true;
            }
        } catch (Exception e) {
            // INUTILI in quanto intercettati
        }
        return false;
    }

    public boolean verificaVersamentiFallitiAsincrono(BigDecimal idStrut,
            Date ultimaRegistrazione) {
        /*
         * Verifica che non sia già attivo un verifica versamenti falliti, nel qual caso rilancia
         * all'action un'eccezione
         */
        Long idLock;
        try {
            asyncHelper.initLockPerStrut(JobConstants.JobEnum.VERIFICA_VERS_FALLITI.name(),
                    idStrut.longValue());
            idLock = asyncHelper.getLock(JobConstants.JobEnum.VERIFICA_VERS_FALLITI.name(),
                    idStrut.longValue());
            if (idLock != null) {
                jobHelper.writeAtomicLogJob(JobConstants.JobEnum.VERIFICA_VERS_FALLITI.name(),
                        JobConstants.OpTypeEnum.INIZIO_SCHEDULAZIONE.name(), null,
                        idStrut.longValue());
                context.getBusinessObject(CalcoloMonitoraggioAsync.class)
                        .eseguiVerificaVersamentiFalliti(idStrut.longValue(), idLock,
                                ultimaRegistrazione);
                return true;
            }
        } catch (Exception e) {
            // INUTILI in quanto intercettati
        }
        return false;
    }

    @Asynchronous
    public void eseguiCalcoloStruttura(Long idLock) throws ParerInternalError {
        try {
            log.info("{} --- Chiamata asincrona per calcolo struttura",
                    CalcoloMonitoraggioAsync.class.getSimpleName());
            log.debug(
                    "Ricerco le sessioni errate rimanenti (non recuperabili tramite User) NON VERIFICATE");
            List<Long> listaSessioniVers = calcoloHelper.getListaSessioniVersByUsr();
            /*
             * Se non ho trovato record dei quali calcolare la struttura versante, tento con la
             * seconda via ovvero cercando nel file XML di richiesta le informazioni mancanti
             */
            for (Long idSes : listaSessioniVers) {
                calcoloHelper.calcolaStrutturaByXml(idSes);
            }
            asyncHelper.writeEndLogLock(idLock, JobConstants.JobEnum.CALCOLA_STRUTTURA.name(),
                    JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(), null);
            log.info("{} --- FINE chiamata asincrona per calcolo struttura",
                    CalcoloMonitoraggioAsync.class.getSimpleName());
        } catch (Exception e) {
            throw new ParerInternalError(ParerErrorSeverity.ERROR,
                    new AsyncException("Eccezione imprevista durante la fase di calcolo struttura",
                            JobConstants.JobEnum.CALCOLA_STRUTTURA.name(), idLock, null, e));
        }
    }

    @Asynchronous
    public void eseguiVerificaVersamentiFalliti(Long idStrut, Long idLock, Date ultimaRegistrazione)
            throws ParerInternalError {
        verificaVersamentiFalliti(idStrut, idLock, ultimaRegistrazione);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void verificaVersamentiFalliti(Long idStrut, Long idLock, Date ultimaRegistrazione)
            throws ParerInternalError {
        try {
            log.info(
                    "{} --- Chiamata asincrona per verifica versamenti falliti per la struttura {}",
                    CalcoloMonitoraggioAsync.class.getSimpleName(), idStrut);
            log.info(
                    "Ricerco i versamenti falliti non risolti e non verificati per la struttura {} con data apertura successiva a {}",
                    idStrut, ultimaRegistrazione);
            List<VrsVVersFallitiDaVerif> listaVersFallitiDaVerif = calcoloHelper
                    .getListaVersFallitiDaVerif(idStrut, ultimaRegistrazione);
            log.info(
                    "Trovati {} versamenti falliti non risolti e non verificati per la struttura {}",
                    listaVersFallitiDaVerif.size(), idStrut);
            for (VrsVVersFallitiDaVerif versFallitoDaVerif : listaVersFallitiDaVerif) {
                calcoloHelper.impostaVersamentoFallitoVerif(
                        versFallitoDaVerif.getIdSessioneVers().longValue());
            }
            log.info(
                    "Ricerco i versamenti falliti non risolti, verificati e risolubili per la struttura {} con data apertura successiva a {}",
                    idStrut, ultimaRegistrazione);
            List<VrsVVersFallitiDaNorisol> listaVersFallitiDaNorisol = calcoloHelper
                    .getListaVersFallitiDaNorisol(idStrut, ultimaRegistrazione);
            for (VrsVVersFallitiDaNorisol versFallitoNorisol : listaVersFallitiDaNorisol) {
                calcoloHelper.impostaVersamentoFallitoNorisol(
                        versFallitoNorisol.getIdSessioneVers().longValue());
            }
            asyncHelper.writeEndLogLock(idLock, JobConstants.JobEnum.VERIFICA_VERS_FALLITI.name(),
                    JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(), null, idStrut);
            log.info(
                    "{} --- FINE chiamata asincrona per verifica versamenti falliti per la struttura {}",
                    CalcoloMonitoraggioAsync.class.getSimpleName(), idStrut);
        } catch (Exception e) {
            throw new ParerInternalError(ParerErrorSeverity.ERROR, new AsyncException(
                    "Eccezione imprevista durante la fase di verifica versamenti falliti per la struttura "
                            + idStrut,
                    JobConstants.JobEnum.VERIFICA_VERS_FALLITI.name(), idLock, idStrut, e));
        }
    }

    public Date getUltimaRegistrazione(String nomeJob, Long idStrut) {
        return calcoloHelper.getUltimaRegistrazione(nomeJob, idStrut);
    }
}
