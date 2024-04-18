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

package it.eng.parer.migrazioneObjectStorage.job;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.AroContenutoComp;
import it.eng.parer.entity.OrgSubPartition;
import it.eng.parer.entity.OstMigrazSubPart;
import it.eng.parer.entity.OstNoMigrazFile;
import it.eng.parer.entity.OstStatoMigrazSubPart;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.migrazioneObjectStorage.helper.VerificaMigrazioneSubPartizioniHelper;

/**
 *
 * @author Iacolucci_M
 */
@Stateless(mappedName = "ControllaMigrazioneSubpartizioneEjb")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class ControllaMigrazioneSubpartizioneEjb {

    Logger log = LoggerFactory.getLogger(ControllaMigrazioneSubpartizioneEjb.class);

    @EJB
    private JobHelper jobHelper;
    @EJB
    private VerificaMigrazioneSubPartizioniHelper verificaMigrazioneSubPartizioniHelper;
    @EJB
    private ControllaMigrazioneSubpartizioneEjb me;
    @PersistenceContext
    private EntityManager entityManager;

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void eseguiControlloJob() throws ParerInternalError {
        int fileNonMigrati = 0;
        List<Object[]> listaSub = verificaMigrazioneSubPartizioniHelper.getOstMigrazSubPartDaEliminareListOrdered();
        for (Object[] oggetti : listaSub) {
            OstMigrazSubPart ostMigrazSubPart = (OstMigrazSubPart) oggetti[0];
            OrgSubPartition orgSubPartition = (OrgSubPartition) oggetti[1];
            List<BigDecimal> listaContenutoComp = verificaMigrazioneSubPartizioniHelper
                    .findIdAroContenutoCompByCodiceIdentificativo(orgSubPartition.getCdSubPartition());
            for (BigDecimal id : listaContenutoComp) {
                me.registraFileNonMigrato(ostMigrazSubPart, id.longValueExact());
                fileNonMigrati++;
            }
            // 8.1 5 b) MEV#18420
            BigDecimal numRecAroContenutoComp = verificaMigrazioneSubPartizioniHelper
                    .countAroContenutoCompByCodiceIdentificativo(orgSubPartition.getCdSubPartition());
            ostMigrazSubPart.setNiFileSubPart(numRecAroContenutoComp);
            ostMigrazSubPart = me.aggiornaStatoSubPartizione(ostMigrazSubPart, numRecAroContenutoComp); // 8.1.2
            eliminaMigrazioneFile(ostMigrazSubPart); // 8.1.3
        }
        /* Scrivo nel LogJob la fine corretta dell'esecuzione del job di creazione indice AIP */
        jobHelper.writeAtomicLogJob(JobConstants.JobEnum.CONTROLLA_MIGRAZIONE_SUBPARTIZIONE.name(),
                JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(),
                "File non migrati registrati: [" + fileNonMigrati + "] su [" + listaSub.size() + "] subpartizioni.");
        log.debug(String.format("%s - Chiusura transazione di ControllaMigrazioneSubpartizioneEjb",
                JobConstants.JobEnum.CONTROLLA_MIGRAZIONE_SUBPARTIZIONE.name()));
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void registraFileNonMigrato(OstMigrazSubPart ostMigrazSubPart, Long idContenutoComp)
            throws ParerInternalError {
        AroContenutoComp aroContenutoComp = entityManager.find(AroContenutoComp.class, idContenutoComp);
        OstNoMigrazFile no = new OstNoMigrazFile();
        no.setOstMigrazSubPart(ostMigrazSubPart);
        no.setIdStrut(aroContenutoComp.getIdStrut());
        no.setTiCausaleNoMigraz(
                it.eng.parer.entity.constraint.OstNoMigrazFile.TiCausaleNoMigraz.CONTENUTO_NON_SELEZIONATO.name());
        no.setMmFile(aroContenutoComp.getMmVers());
        no.setTiSupportoComp(it.eng.parer.entity.constraint.OstNoMigrazFile.TiSupportoComp.FILE.name());
        no.setTiSaveFile(it.eng.parer.entity.constraint.OstNoMigrazFile.TiSaveFile.BLOB.name());
        no.setNmTabellaIdOggetto("ARO_COMP_DOC");
        no.setIdOggetto(new BigDecimal(aroContenutoComp.getAroCompDoc().getIdCompDoc()));
        entityManager.persist(no);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public OstMigrazSubPart aggiornaStatoSubPartizione(OstMigrazSubPart ostMigrazSubPart,
            BigDecimal numRecAroContenutoComp) throws ParerInternalError {
        OstMigrazSubPart subPart = entityManager.find(OstMigrazSubPart.class, ostMigrazSubPart.getIdMigrazSubPart());
        String stato = null;
        if (verificaMigrazioneSubPartizioniHelper.existsNoMigrazFileWithTiCausaleNoMigraz(subPart,
                it.eng.parer.entity.constraint.OstNoMigrazFile.TiCausaleNoMigraz.CONTENUTO_NON_SELEZIONATO.name())) {
            stato = it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.MIGRAZ_NON_COMPLETA.name();
        } else {
            // MEV#18420
            if (subPart.getNiFileMigrati().equals(numRecAroContenutoComp)) {
                stato = it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.MIGRAZ_COMPLETA.name();
            } else {
                stato = it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.MIGRAZ_DA_CONTROLLARE.name();
            }
        }
        OstStatoMigrazSubPart ostStatoMigrazSubPart = new OstStatoMigrazSubPart();
        ostStatoMigrazSubPart.setOstMigrazSubPart(subPart);
        ostStatoMigrazSubPart.setTiStato(stato);
        ostStatoMigrazSubPart.setTsRegStato(new Date());
        entityManager.persist(ostStatoMigrazSubPart);
        entityManager.flush();
        subPart.setIdStatoMigrazSubPartCor(new BigDecimal(ostStatoMigrazSubPart.getIdStatoMigrazSubPart()));
        entityManager.flush(); // Serve?
        return subPart;
    }

    /*
     * Nel caso in cui lo stato della migrazione sub part sia MIGRAZIONE_COMPLETA cancella tutti i file di migrazione e
     * i file non migrati eventualmente rimasti.
     */
    public void eliminaMigrazioneFile(OstMigrazSubPart ostMigrazSubPart) throws ParerInternalError {
        OstStatoMigrazSubPart stato = entityManager.find(OstStatoMigrazSubPart.class,
                ostMigrazSubPart.getIdStatoMigrazSubPartCor().longValueExact());
        if (stato != null && (stato.getTiStato()
                .equals(it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.MIGRAZ_COMPLETA.name())
                || stato.getTiStato().equals(
                        it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.MIGRAZ_DA_CONTROLLARE.name()))) {
            // OstMigrazFile
            List<Long> l = verificaMigrazioneSubPartizioniHelper
                    .findOstMigrazFileIdByOstMigrazSubPart(ostMigrazSubPart);
            for (Long id : l) {
                verificaMigrazioneSubPartizioniHelper.eliminaMigrazioneFileInNewTransaction(id);
            }
            // OstNoMigrazFile
            l = verificaMigrazioneSubPartizioniHelper.findOstNoMigrazFileIdByOstMigrazSubPart(ostMigrazSubPart);
            for (Long id : l) {
                verificaMigrazioneSubPartizioniHelper.eliminaNoMigrazioneFileInNewTransaction(id);
            }
        }
    }

}
