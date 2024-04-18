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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.job.helper;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.LogJob;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.util.ejb.AppServerInstance;
import it.eng.parer.viewEntity.LogVVisLastSched;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.ejb.ControlliSemantici;
import it.eng.parer.ws.ejb.ControlliTpi;
import it.eng.parer.ws.utils.CostantiDB;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "JobHelper")
@LocalBean
public class JobHelper implements Serializable {

    private static final long serialVersionUID = 1L;
    Logger log = LoggerFactory.getLogger(JobHelper.class);
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;
    @EJB
    private ControlliSemantici controlli;
    @EJB
    private ControlliTpi controlliTpi;
    @EJB
    private AppServerInstance appServerInstance;

    public LogJob writeLogJob(String job, String opType, String descr, Long idStrut, Long idRecord,
            String nmTabellaRecord, String appServerInstanceName) {
        Date date = new Date();
        LogJob newLogJob = new LogJob();
        newLogJob.setDtRegLogJob(date);
        newLogJob.setNmJob(job);
        newLogJob.setTiRegLogJob(opType);
        OrgStrut orgStrut = null;
        if (idStrut != null) {
            orgStrut = entityManager.find(OrgStrut.class, idStrut);
        }
        newLogJob.setOrgStrut(orgStrut);
        if (descr != null) {
            newLogJob.setDlMsgErr(StringUtils.abbreviate(descr, 1024));
        }
        if (idRecord != null && nmTabellaRecord != null) {
            newLogJob.setIdRecord(new BigDecimal(idRecord));
            newLogJob.setNmEntityRecord(nmTabellaRecord);
        }
        newLogJob.setCdIndServer(appServerInstanceName);

        LogJob logJob = entityManager.merge(newLogJob);
        entityManager.flush();
        return logJob;
    }

    public LogJob writeLogJob(String job, String opType, String descr, Long idStrut, Long idRecord,
            String nmTabellaRecord) {
        return writeLogJob(job, opType, descr, idStrut, idRecord, nmTabellaRecord, appServerInstance.getName());
    }

    public LogJob writeLogJob(String job, String opType, String descr, Long idRecord, String nmTabellaRecord) {
        return writeLogJob(job, opType, descr, null, idRecord, nmTabellaRecord);
    }

    public LogJob writeLogJob(String job, String opType, String descr, Long idStrut) {
        return writeLogJob(job, opType, descr, idStrut, null, null);
    }

    public LogJob writeLogJob(String job, String opType, String descr) {
        return writeLogJob(job, opType, descr, null);
    }

    public LogJob writeLogJob(String job, String opType) {
        return writeLogJob(job, opType, null);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public LogJob writeAtomicLogJob(String job, String opType) {
        return writeLogJob(job, opType, null);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public LogJob writeAtomicLogJob(String job, String opType, String descr) {
        return writeLogJob(job, opType, descr);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public LogJob writeAtomicLogJob(String job, String opType, String descr, Long idStrut) {
        return writeLogJob(job, opType, descr, idStrut);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public LogJob writeAtomicLogJob(String job, String opType, String descr, Long idRecord, String nmTabellaRecord) {
        return writeLogJob(job, opType, descr, idRecord, nmTabellaRecord);
    }

    public Map<String, String> getParamMap() throws ParerInternalError {
        /*
         * NOTA DI FF: il parametro appl è completamente inutile
         */
        RispostaControlli risp = controlli.caricaDefaultDaDBParametriApplic(CostantiDB.TipoParametroAppl.TPI);
        if (risp.isrBoolean()) {
            Map<String, String> map = (Map<String, String>) risp.getrObject();
            RispostaControlli rootRisp = controlliTpi.caricaRootPath();
            if (rootRisp.isrBoolean()) {
                map.put(CostantiDB.ParametroAppl.TPI_ROOT_SACER, rootRisp.getrString());
                return map;
            } else {
                throw new ParerInternalError(rootRisp.getCodErr() + " - " + rootRisp.getDsErr());
            }
        } else {
            throw new ParerInternalError(risp.getCodErr() + " - " + risp.getDsErr());
        }
    }

    public Date findUltimaAttivazioneByJob(String nmJob) {
        Date data = null;
        try {
            Query query = entityManager.createNamedQuery("LogVVisLastSched.findUltimaAttivazioneByJob",
                    LogVVisLastSched.class);
            query.setParameter("nmJob", nmJob);
            List<LogVVisLastSched> l = query.getResultList();
            if (l != null && !l.isEmpty()) {
                data = l.get(0).getDtRegLogJobIni();
            }
        } catch (RuntimeException ex) {
            log.error("Errore nell'estrazione della LogVVisLastSched", ex);
            throw ex;
        }

        return data;
    }
}
