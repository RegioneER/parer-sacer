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

package it.eng.parer.job.calcoloContenutoAggMeta.ejb;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.slf4j.LoggerFactory;

import it.eng.parer.entity.MonContaSesUpdUd;
import it.eng.parer.entity.MonContaSesUpdUdKo;
import it.eng.parer.entity.MonKeyTotalUd;
import it.eng.parer.entity.MonKeyTotalUdKo;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.MonContaSesUpdUd.TiStatoUdpUdMonContaSesUpdUd;
import it.eng.parer.entity.constraint.MonContaSesUpdUdKo.TiStatoUdpUdKoMonContaSesUpdUdKo;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.util.Utils;
import it.eng.parer.web.helper.MonitoraggioAggMetaHelper;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "CalcoloContenutoAggMetaEjb")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class CalcoloContenutoAggMetaEjb {

    private final org.slf4j.Logger logger = LoggerFactory.getLogger(CalcoloContenutoAggMetaEjb.class);

    @EJB
    private JobHelper jobHelper;
    @EJB
    private MonitoraggioAggMetaHelper monitoraggioAggMetaHelper;

    @Resource
    private SessionContext context;

    public void calcolaContenutoAggMetaEjb() throws ParerInternalError {
        Date dataJob = new Date();
        Date ultimaDataEsecuzione = jobHelper
                .findUltimaAttivazioneByJob(JobConstants.JobEnum.CALCOLO_CONTENUTO_AGGIORNAMENTI_METADATI.name());
        jobHelper.writeAtomicLogJob(JobConstants.JobEnum.CALCOLO_CONTENUTO_AGGIORNAMENTI_METADATI.name(),
                JobConstants.OpTypeEnum.INIZIO_SCHEDULAZIONE.name());
        logger.info("{} - Inizio esecuzione job - {}",
                JobConstants.JobEnum.CALCOLO_CONTENUTO_AGGIORNAMENTI_METADATI.name(), dataJob);
        CalcoloContenutoAggMetaEjb me = context.getBusinessObject(CalcoloContenutoAggMetaEjb.class);
        if (ultimaDataEsecuzione == null) {
            Calendar calendar = new GregorianCalendar();
            calendar.set(Calendar.YEAR, 2019);
            calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
            calendar.set(Calendar.DAY_OF_MONTH, 30);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            ultimaDataEsecuzione = calendar.getTime();
        }
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(ultimaDataEsecuzione);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date soloDataUltimaEsecuzione = calendar.getTime();
        calendar = new GregorianCalendar();
        calendar.setTime(dataJob);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date soloDataOggi = calendar.getTime();
        List<Date> date = null;
        if (!soloDataUltimaEsecuzione.equals(soloDataOggi)) {
            /* Ricavo l'intervallo di giorni da elaborare dall' ultima esecuzione */
            Date ieri = Utils.getIeri();
            date = Utils.getDatesBetween(ultimaDataEsecuzione, ieri);
            for (Iterator<Date> iterator = date.iterator(); iterator.hasNext();) {
                Date data = iterator.next();
                me.calcolaAggMetaDelGiorno(data);
            }
        }
        jobHelper.writeAtomicLogJob(JobConstants.JobEnum.CALCOLO_CONTENUTO_AGGIORNAMENTI_METADATI.name(),
                JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(),
                date != null ? ("Processate " + date.size() + " date.") : null);
        dataJob = new Date();
        logger.info("{} - Fine esecuzione job - {}",
                JobConstants.JobEnum.CALCOLO_CONTENUTO_AGGIORNAMENTI_METADATI.name(), dataJob);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void calcolaAggMetaDelGiorno(Date data) {
        eseguiCalcoloAggDelGiorno(data);
    }

    private void eseguiCalcoloAggDelGiorno(Date data) {
        List<OrgStrut> strutList = monitoraggioAggMetaHelper.getStruttureVersantiPerAggMeta();
        for (OrgStrut strut : strutList) {
            manageStrut(strut.getIdStrut(), data);
        }
    }

    private void manageStrut(long idStrut, Date data) {
        // Elimina da tabella MON_CONTA_SES_UPD_UD i record eventualmente presenti relativi alla struttura ed al giorno
        // corrente
        int i = monitoraggioAggMetaHelper.deleteMonContaSesUpdUd(idStrut, data);
        logger.info("Calcolo Contenuto Aggiornamenti Metadari - Cancellati " + i
                + " record dalla tabella MON_CONTA_SES_UPD_UD");
        // Seleziona gli aggiornamenti metadati
        List<Object[]> objList = monitoraggioAggMetaHelper.getAggMetaPerCalcoloContenuto3(idStrut, data);

        // Raggruppo per stati
        // Registra i totali giornalieri nella tabella MON_CONTA_SES_UPD_UD
        for (Object[] obj : objList) {
            MonContaSesUpdUd conta = new MonContaSesUpdUd();
            conta.setDtRifConta((Date) obj[2]);
            conta.setMonKeyTotalUd(monitoraggioAggMetaHelper.findById(MonKeyTotalUd.class, (BigDecimal) obj[0]));
            conta.setNiSesUpdUd((BigDecimal) obj[3]);
            String stato = "";
            stato = (String) obj[1];
            conta.setTiStatoUdpUd(TiStatoUdpUdMonContaSesUpdUd.valueOf(stato));
            monitoraggioAggMetaHelper.getEntityManager().persist(conta);
            monitoraggioAggMetaHelper.getEntityManager().flush();
        }
        // Elimina da tabella MON_CONTA_SES_UPD_UD_KO i record eventualmente presenti relativi alla struttura ed al
        // giorno corrente
        i = monitoraggioAggMetaHelper.deleteMonContaSesUpdUdKo(idStrut, data);
        logger.info("Calcolo Contenuto Aggiornamenti Metadari - Cancellati " + i
                + " record dalla tabella MON_CONTA_SES_UPD_UD_KO");
        // Seleziona gli aggiornamenti metadati
        List<Object[]> obj2List = monitoraggioAggMetaHelper.getSesAggMetaPerCalcoloContenuto3(idStrut, data);
        // Registra i totali giornalieri nella tabella MON_CONTA_SES_UPD_UD_KO
        for (Object[] obj : obj2List) {
            MonContaSesUpdUdKo conta = new MonContaSesUpdUdKo();
            conta.setDtRifConta((Date) obj[2]);
            String stato = (String) obj[1];
            conta.setTiStatoUdpUdKo(TiStatoUdpUdKoMonContaSesUpdUdKo.valueOf(stato));
            conta.setMonKeyTotalUdKo(monitoraggioAggMetaHelper.findById(MonKeyTotalUdKo.class, (BigDecimal) obj[0]));
            conta.setNiSesUpdUdKo((BigDecimal) obj[3]);
            monitoraggioAggMetaHelper.getEntityManager().persist(conta);
            monitoraggioAggMetaHelper.getEntityManager().flush();
        }
    }
}
