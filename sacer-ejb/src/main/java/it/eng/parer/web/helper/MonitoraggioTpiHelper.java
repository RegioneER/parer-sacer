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

package it.eng.parer.web.helper;

import static it.eng.parer.util.Utils.longFromBigDecimal;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

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

import it.eng.parer.entity.TpiErrArk;
import it.eng.parer.entity.TpiPathElab;
import it.eng.parer.entity.TpiSchedJob;
import it.eng.parer.entity.VrsArkPathDtVers;
import it.eng.parer.entity.VrsDtVers;
import it.eng.parer.entity.VrsFileNoarkPathDtVers;
import it.eng.parer.entity.VrsPathDtVers;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.slite.gen.tablebean.TpiErrArkTableBean;
import it.eng.parer.slite.gen.tablebean.TpiPathElabRowBean;
import it.eng.parer.slite.gen.tablebean.TpiPathElabTableBean;
import it.eng.parer.slite.gen.tablebean.TpiSchedJobRowBean;
import it.eng.parer.slite.gen.tablebean.TpiSchedJobTableBean;
import it.eng.parer.slite.gen.tablebean.VrsDtVersRowBean;
import it.eng.parer.slite.gen.tablebean.VrsDtVersTableBean;
import it.eng.parer.slite.gen.tablebean.VrsFileNoarkPathDtVersTableBean;
import it.eng.parer.slite.gen.tablebean.VrsPathDtVersRowBean;
import it.eng.parer.slite.gen.tablebean.VrsPathDtVersTableBean;
import it.eng.parer.slite.gen.viewbean.TpiVLisDtSchedTableBean;
import it.eng.parer.slite.gen.viewbean.TpiVVisDtSchedRowBean;
import it.eng.parer.viewEntity.TpiVLisDtSched;
import it.eng.parer.viewEntity.TpiVVisDtSched;
import it.eng.parer.web.util.Transform;

/**
 *
 * @author Bonora_L
 */
@SuppressWarnings("unchecked")
@Stateless
@LocalBean
public class MonitoraggioTpiHelper {

    Logger log = LoggerFactory.getLogger(MonitoraggioTpiHelper.class);
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    /*
     * GESTIONE DATE VERSAMENTO ARK
     */
    public VrsDtVersTableBean getDateVersamentoArk(Date dateVersDa, Date dateVersA, String flMigraz,
            String statoDtVers) {
        StringBuilder queryStr = new StringBuilder("SELECT vrsDtVers from VrsDtVers vrsDtVers WHERE ");

        if (dateVersDa != null && dateVersA != null) {
            queryStr.append("vrsDtVers.dtVers BETWEEN :dataDa AND :dataA ");
        } else if (dateVersDa != null) {
            queryStr.append("vrsDtVers.dtVers >= :dataDa ");
        } else if (dateVersA != null) {
            queryStr.append("vrsDtVers.dtVers <= :dataA ");
        }

        if (flMigraz != null) {
            queryStr.append("AND vrsDtVers.flMigraz = :migraz ");
        }
        if (statoDtVers != null) {
            queryStr.append("AND vrsDtVers.tiStatoDtVers = :stato ");
        }

        queryStr.append("ORDER BY vrsDtVers.dtVers DESC");
        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr.toString());
        // setParameters
        if (dateVersDa != null) {
            // MAC#27666
            query.setParameter("dataDa", dateVersDa.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            // end MAC#27666
        }
        if (dateVersA != null) {
            // MAC#27666
            query.setParameter("dataA", dateVersA.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
            // end MAC#27666
        }
        if (flMigraz != null) {
            query.setParameter("migraz", flMigraz);
        }
        if (statoDtVers != null) {
            query.setParameter("stato", statoDtVers);
        }
        List<VrsDtVers> dateVers = query.getResultList();
        VrsDtVersTableBean dateVersTableBean = new VrsDtVersTableBean();
        try {
            if (dateVers != null && !dateVers.isEmpty()) {
                dateVersTableBean = (VrsDtVersTableBean) Transform.entities2TableBean(dateVers);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return dateVersTableBean;
    }

    public VrsDtVersRowBean getDataVersamentoArk(BigDecimal idDtVers) {
        VrsDtVers row = entityManager.find(VrsDtVers.class, idDtVers.longValue());
        VrsDtVersRowBean rowBean = null;
        try {
            if (row != null) {
                rowBean = (VrsDtVersRowBean) Transform.entity2RowBean(row);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return rowBean;
    }

    public VrsPathDtVersTableBean getPathsDateVersamentoArk(BigDecimal idVrsDtVers) {
        Query query = entityManager.createQuery(
                "SELECT paths from VrsPathDtVers paths WHERE paths.vrsDtVers.idDtVers = :dtVers ORDER BY paths.dlPath");
        query.setParameter("dtVers", longFromBigDecimal(idVrsDtVers));
        List<VrsPathDtVers> pathsList = query.getResultList();
        VrsPathDtVersTableBean pathsTableBean = new VrsPathDtVersTableBean();
        try {
            if (pathsList != null && !pathsList.isEmpty()) {
                for (VrsPathDtVers path : pathsList) {
                    VrsPathDtVersRowBean pathRowBean = (VrsPathDtVersRowBean) Transform.entity2RowBean(path);
                    /*
                     * Modifica custom per la gestione delle archiviazioni
                     */
                    if (path.getVrsArkPathDtVers() != null && !path.getVrsArkPathDtVers().isEmpty()) {
                        StringBuilder primarioBuilder = new StringBuilder();
                        StringBuilder secondarioBuilder = new StringBuilder();
                        List<VrsArkPathDtVers> arkPathList = path.getVrsArkPathDtVers();
                        boolean primarioAccapo = false;
                        boolean secondarioAccapo = false;
                        for (VrsArkPathDtVers arkPath : arkPathList) {
                            if (arkPath.getTiArkPath().equals(JobConstants.ArkPath.PRIMARIO.name())) {
                                if (primarioAccapo) {
                                    primarioBuilder.append("\n");
                                }
                                primarioBuilder.append(arkPath.getDsArk());
                                primarioAccapo = true;
                            } else if (arkPath.getTiArkPath().equals(JobConstants.ArkPath.SECONDARIO.name())) {
                                if (secondarioAccapo) {
                                    secondarioBuilder.append("\n");
                                }
                                secondarioBuilder.append(arkPath.getDsArk());
                                secondarioAccapo = true;
                            }
                        }
                        pathRowBean.setDlArk(primarioBuilder.length() > 0 ? primarioBuilder.toString() : null);
                        pathRowBean.setDlArkSecondario(
                                secondarioBuilder.length() > 0 ? secondarioBuilder.toString() : null);
                    }
                    pathsTableBean.add(pathRowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return pathsTableBean;
    }

    public VrsFileNoarkPathDtVersTableBean getFilesNoArkPath(BigDecimal idPathDtVers, String tiArkFileNoArk) {
        Query query = entityManager.createQuery(
                "SELECT noArk FROM VrsFileNoarkPathDtVers noArk WHERE noArk.vrsPathDtVers.idPathDtVers = :path AND noArk.tiArkFileNoark = :tipoArk");
        query.setParameter("path", longFromBigDecimal(idPathDtVers));
        query.setParameter("tipoArk", tiArkFileNoArk);
        List<VrsFileNoarkPathDtVers> fileNoArkList = query.getResultList();
        VrsFileNoarkPathDtVersTableBean fileNoArkTableBean = new VrsFileNoarkPathDtVersTableBean();
        try {
            if (fileNoArkList != null && !fileNoArkList.isEmpty()) {
                fileNoArkTableBean = (VrsFileNoarkPathDtVersTableBean) Transform.entities2TableBean(fileNoArkList);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return fileNoArkTableBean;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void setDataVersamentoArkStatus(BigDecimal idDtVers, String tiStatoDtVers) {
        VrsDtVers date = entityManager.find(VrsDtVers.class, idDtVers.longValue());
        date.setTiStatoDtVers(tiStatoDtVers);
    }

    /*
     * GESTIONE DATE SCHEDULAZIONI TPI
     */
    public TpiVLisDtSchedTableBean getDateSchedulazioniTpi(Date dateSchedDa, Date dateSchedA, String flAnomalie,
            String statoDtSched) {
        StringBuilder queryStr = new StringBuilder("SELECT tpiDtSched from TpiVLisDtSched tpiDtSched WHERE ");

        if (dateSchedDa != null && dateSchedA != null) {
            queryStr.append("tpiDtSched.dtSched BETWEEN :dataDa AND :dataA ");
        } else if (dateSchedDa != null) {
            queryStr.append("tpiDtSched.dtSched >= :dataDa ");
        } else if (dateSchedA != null) {
            queryStr.append("tpiDtSched.dtSched <= :dataA ");
        }

        if (flAnomalie != null) {
            queryStr.append("AND (" + "tpiDtSched.flAnomaliaArkPrim = :anomalia OR "
                    + "tpiDtSched.flAnomaliaCopiaPrim = :anomalia OR "
                    + "tpiDtSched.flAnomaliaBackupPrim = :anomalia OR "
                    + "tpiDtSched.flAnomaliaMigratePrim = :anomalia OR "
                    + "tpiDtSched.flAnomaliaRiArkPrim = :anomalia OR "
                    + "tpiDtSched.flAnomaliaArkSecond = :anomalia OR "
                    + "tpiDtSched.flAnomaliaCopiaSecond = :anomalia OR "
                    + "tpiDtSched.flAnomaliaBackupSecond = :anomalia OR "
                    + "tpiDtSched.flAnomaliaMigrateSecond = :anomalia OR "
                    + "tpiDtSched.flAnomaliaRiArkSecond = :anomalia" + ")");
        }
        if (statoDtSched != null) {
            queryStr.append("AND tpiDtSched.tiStatoDtSched = :stato ");
        }

        queryStr.append("ORDER BY tpiDtSched.dtSched DESC");
        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr.toString());
        // setParameters
        if (dateSchedDa != null) {
            query.setParameter("dataDa", dateSchedDa);
        }
        if (dateSchedA != null) {
            query.setParameter("dataA", dateSchedA);
        }
        if (flAnomalie != null) {
            query.setParameter("anomalia", flAnomalie);
        }
        if (statoDtSched != null) {
            query.setParameter("stato", statoDtSched);
        }
        List<TpiVLisDtSched> dateSched = query.getResultList();
        TpiVLisDtSchedTableBean dateSchedViewBean = new TpiVLisDtSchedTableBean();
        try {
            if (dateSched != null && !dateSched.isEmpty()) {
                dateSchedViewBean = (TpiVLisDtSchedTableBean) Transform.entities2TableBean(dateSched);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return dateSchedViewBean;
    }

    public TpiVVisDtSchedRowBean getDataSchedulazioneTpi(BigDecimal idTpiDtSched) {
        String queryStr = "SELECT tpiSched FROM TpiVVisDtSched tpiSched WHERE tpiSched.idDtSched = :idDtSched";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idDtSched", idTpiDtSched);
        List<TpiVVisDtSched> schedList = query.getResultList();
        TpiVVisDtSchedRowBean row = null;
        try {
            if (schedList != null && !schedList.isEmpty()) {
                row = (TpiVVisDtSchedRowBean) Transform.entity2RowBean(schedList.get(0));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return row;
    }

    public TpiSchedJobTableBean getJobList(BigDecimal idTpiDtSched, String flMigraz, String tiTpiSchedJob) {
        StringBuilder queryStr = new StringBuilder("SELECT tpiSched from TpiSchedJob tpiSched ");
        String where = " WHERE ";
        if (idTpiDtSched != null) {
            queryStr.append(where).append("tpiSched.tpiDtSched.idDtSched = :idDtSched ");
            where = " AND ";
        }
        if (flMigraz != null) {
            queryStr.append(where).append("tpiSched.flMigraz = :flMigraz ");
            where = " AND ";
        }
        if (tiTpiSchedJob != null) {
            queryStr.append(where).append("tpiSched.tiTpiSchedJob = :tiTpiSchedJob ");
        }
        queryStr.append(" ORDER BY tpiSched.niOrdSchedJob, tpiSched.dtSchedJob");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr.toString());
        if (idTpiDtSched != null) {
            query.setParameter("idDtSched", longFromBigDecimal(idTpiDtSched));
        }
        if (flMigraz != null) {
            query.setParameter("flMigraz", flMigraz);
        }
        if (tiTpiSchedJob != null) {
            query.setParameter("tiTpiSchedJob", tiTpiSchedJob);
        }
        List<TpiSchedJob> jobSchedList = query.getResultList();
        TpiSchedJobTableBean jobSchedViewBean = new TpiSchedJobTableBean();
        try {
            if (jobSchedList != null && !jobSchedList.isEmpty()) {
                for (TpiSchedJob schedJob : jobSchedList) {
                    TpiSchedJobRowBean row = (TpiSchedJobRowBean) Transform.entity2RowBean(schedJob);
                    if (schedJob.getFlJobOk() != null && schedJob.getFlJobOk().equals(JobConstants.DB_TRUE)
                            && StringUtils.isNotBlank(schedJob.getDlErrJob())) {
                        row.setFlJobOk(JobConstants.DB_WARNING);
                    }
                    if (schedJob.getNmJob().startsWith("ARCHIVIAZIONE_")
                            || schedJob.getNmJob().startsWith("JOB_TSM_")) {
                        row.setObject("fl_err_ark", JobConstants.DB_TRUE);
                    } else {
                        row.setObject("fl_err_ark", JobConstants.DB_FALSE);
                    }
                    if (schedJob.getNmJob().startsWith("ARCHIVIAZIONE_") || schedJob.getNmJob().startsWith("COPIA_")) {
                        row.setObject("fl_path_elab", JobConstants.DB_TRUE);
                    } else {
                        row.setObject("fl_path_elab", JobConstants.DB_FALSE);
                    }
                    if (schedJob.getNmJob().startsWith("JOB_TSM_")) {
                        if (schedJob.getNmJob().contains("BACKUP")) {
                            if (schedJob.getNmJob().endsWith("VERS")) {
                                row.setNmJob("BACKUP_VERSATI");
                            } else if (schedJob.getNmJob().endsWith("MIGRAZ")) {
                                row.setNmJob("BACKUP_MIGRATI");
                            }
                        } else if (schedJob.getNmJob().contains("MIGRATE")) {
                            if (schedJob.getNmJob().endsWith("VERS")) {
                                row.setNmJob("MIGRATE_VERSATI");
                            } else if (schedJob.getNmJob().endsWith("MIGRAZ")) {
                                row.setNmJob("MIGRATE_MIGRATI");
                            }
                        }
                    }
                    jobSchedViewBean.add(row);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return jobSchedViewBean;
    }

    public TpiErrArkTableBean getErrArkJobList(BigDecimal idTpiSchedJob) {
        String queryStr = "SELECT errArk FROM TpiErrArk errArk WHERE errArk.tpiSchedJob.idSchedJob = :idSched";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idSched", longFromBigDecimal(idTpiSchedJob));
        List<TpiErrArk> errArkList = query.getResultList();
        TpiErrArkTableBean errArkViewBean = new TpiErrArkTableBean();
        try {
            if (errArkList != null && !errArkList.isEmpty()) {
                errArkViewBean = (TpiErrArkTableBean) Transform.entities2TableBean(errArkList);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return errArkViewBean;
    }

    public TpiPathElabTableBean getPathElabJobList(BigDecimal idTpiSchedJob) {
        String queryStr = "SELECT paths FROM TpiPathElab paths WHERE paths.tpiSchedJob.idSchedJob = :idSched ORDER BY paths.dtVersElab, paths.dlPathElab";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("idSched", longFromBigDecimal(idTpiSchedJob));
        List<TpiPathElab> pathList = query.getResultList();
        TpiPathElabTableBean pathElabBean = new TpiPathElabTableBean();
        try {
            if (pathList != null && !pathList.isEmpty()) {
                for (TpiPathElab row : pathList) {
                    TpiPathElabRowBean rowBean = (TpiPathElabRowBean) Transform.entity2RowBean(row);
                    if (row.getNiFileDaElab().compareTo(row.getNiFileElab()) != 0) {
                        rowBean.setFlAnomaliaFileElab(JobConstants.DB_TRUE);
                    } else {
                        rowBean.setFlAnomaliaFileElab(JobConstants.DB_FALSE);
                    }
                    pathElabBean.add(rowBean);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return pathElabBean;
    }

}
