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

package it.eng.parer.job.calcoloContenutoFascicoli.ejb;

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

import it.eng.parer.entity.DecTipoFascicolo;
import it.eng.parer.entity.IamUser;
import it.eng.parer.entity.MonContaFascicoli;
import it.eng.parer.entity.MonContaFascicoliKo;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.fascicoli.helper.FascicoliHelper;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.util.Utils;

/**
 *
 * @author Iacolucci_M
 */
@Stateless(mappedName = "CalcoloContenutoFascicoliEjb")
@LocalBean
@Interceptors({
	it.eng.parer.aop.TransactionInterceptor.class })
public class CalcoloContenutoFascicoliEjb {

    private final org.slf4j.Logger logger = LoggerFactory
	    .getLogger(CalcoloContenutoFascicoliEjb.class);

    @EJB
    private JobHelper jobHelper;
    @EJB
    private FascicoliHelper fascicoliHelper;

    @Resource
    private SessionContext context;

    public void calcolaContenutoFascicoli() throws ParerInternalError {
	Date dataJob = new Date();
	Date ultimaDataEsecuzione = jobHelper.findUltimaAttivazioneByJob(
		JobConstants.JobEnum.CALCOLO_CONTENUTO_FASCICOLI.name());
	jobHelper.writeAtomicLogJob(JobConstants.JobEnum.CALCOLO_CONTENUTO_FASCICOLI.name(),
		JobConstants.OpTypeEnum.INIZIO_SCHEDULAZIONE.name());
	logger.info("{} - Inizio esecuzione job - {}",
		JobConstants.JobEnum.CALCOLO_CONTENUTO_FASCICOLI.name(), dataJob);
	CalcoloContenutoFascicoliEjb me = context
		.getBusinessObject(CalcoloContenutoFascicoliEjb.class);
	if (ultimaDataEsecuzione == null) {
	    Calendar calendar = new GregorianCalendar();
	    calendar.set(Calendar.YEAR, 2018);
	    calendar.set(Calendar.MONTH, Calendar.JANUARY);
	    calendar.set(Calendar.DAY_OF_MONTH, 1);
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
		me.calcolaFascicoliDelGiorno(data);
	    }
	}
	jobHelper.writeAtomicLogJob(JobConstants.JobEnum.CALCOLO_CONTENUTO_FASCICOLI.name(),
		JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(),
		date != null ? ("Processate " + date.size() + " date.") : null);
	dataJob = new Date();
	logger.info("{} - Fine esecuzione job - {}",
		JobConstants.JobEnum.CALCOLO_CONTENUTO_FASCICOLI.name(), dataJob);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void calcolaFascicoliDelGiorno(Date data) {
	calcolaFascicoliVersatiDelGiorno(data);
	calcolaFascicoliNonVersatiDelGiorno(data);
    }

    private void calcolaFascicoliVersatiDelGiorno(Date data) {
	List<Object[]> l = fascicoliHelper.findCountFascicoliVersatiNelGiorno(data);
	if (l != null && !l.isEmpty()) {
	    for (Iterator<Object[]> iterator = l.iterator(); iterator.hasNext();) {
		Object[] ogg = iterator.next();
		BigDecimal idStrut = new BigDecimal((long) ogg[0]),
			idTipoFascicolo = new BigDecimal((long) ogg[1]);
		BigDecimal aaFascicolo = (BigDecimal) ogg[2],
			idUser = new BigDecimal((long) ogg[3]),
			conteggio = new BigDecimal((long) ogg[4]);
		List<MonContaFascicoli> lm = fascicoliHelper
			.retrieveMonContaFascicoliByChiaveTotaliz(idStrut, data, idTipoFascicolo,
				aaFascicolo, idUser);
		MonContaFascicoli m = null;
		if ((lm != null) && (!lm.isEmpty())) {
		    m = lm.get(0);
		} else {
		    m = new MonContaFascicoli();
		}
		m.setOrgStrut(fascicoliHelper.findById(OrgStrut.class, idStrut));
		m.setDecTipoFascicolo(
			fascicoliHelper.findById(DecTipoFascicolo.class, idTipoFascicolo));
		m.setDtRifConta(data);
		m.setAaFascicolo(aaFascicolo);
		m.setIamUser(fascicoliHelper.findById(IamUser.class, idUser));
		m.setNiFascicoliVers(conteggio);
		fascicoliHelper.getEntityManager().persist(m);
	    }
	}
    }

    private void calcolaFascicoliNonVersatiDelGiorno(Date data) {
	List<Object[]> l = fascicoliHelper.findCountFascicoliNonVersatiNelGiorno(data);
	if (l != null && !l.isEmpty()) {
	    for (Iterator<Object[]> iterator = l.iterator(); iterator.hasNext();) {
		Object[] ogg = iterator.next();
		BigDecimal idStrut = new BigDecimal((long) ogg[0]),
			idTipoFascicolo = new BigDecimal((long) ogg[1]);
		BigDecimal aaFascicolo = (BigDecimal) ogg[2],
			conteggio = new BigDecimal((long) ogg[3]);
		String statoFascicoloNonVersato = (String) ogg[4];
		List<MonContaFascicoliKo> lm = fascicoliHelper
			.retrieveMonContaFascicoliNonVersByChiaveTotaliz(idStrut, data,
				idTipoFascicolo, aaFascicolo, statoFascicoloNonVersato);
		MonContaFascicoliKo m = null;
		if ((lm != null) && (!lm.isEmpty())) {
		    m = lm.get(0);
		} else {
		    // non fa nulla
		    m = new MonContaFascicoliKo();
		}
		m.setOrgStrut(fascicoliHelper.findById(OrgStrut.class, idStrut));
		m.setDecTipoFascicolo(
			fascicoliHelper.findById(DecTipoFascicolo.class, idTipoFascicolo));
		m.setDtRifConta(data);
		m.setAaFascicolo(aaFascicolo);
		m.setTiStatoFascicoloKo(statoFascicoloNonVersato);
		m.setNiFascicoliKo(conteggio);
		fascicoliHelper.getEntityManager().persist(m);
	    }
	}
    }

}
