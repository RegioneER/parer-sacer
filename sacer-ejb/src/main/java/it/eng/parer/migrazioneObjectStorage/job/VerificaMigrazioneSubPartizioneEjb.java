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

package it.eng.parer.migrazioneObjectStorage.job;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.OstMigrazFile;
import it.eng.parer.entity.OstMigrazSubPart;
import it.eng.parer.entity.OstStatoMigrazSubPart;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.job.helper.JobHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.migrazioneObjectStorage.helper.VerificaMigrazioneSubPartizioniHelper;
import it.eng.parer.migrazioneObjectStorage.utils.MsgUtil;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.ws.utils.CostantiDB;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "VerificaMigrazioneSubPartizioneEjb")
@LocalBean
@Interceptors({
        it.eng.parer.aop.TransactionInterceptor.class })
public class VerificaMigrazioneSubPartizioneEjb {

    Logger log = LoggerFactory.getLogger(VerificaMigrazioneSubPartizioneEjb.class);

    @EJB
    private VerificaMigrazioneSubPartizioniHelper vmspHelper;
    @EJB
    private JobHelper jobHelper;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private VerificaMigrazioneSubPartizioneEjb me;

    private String OST_004 = "OST-004";
    private String OST_005 = "OST-005";

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void verificaMigrazioneSubPartizione() throws ParerInternalError {
        log.info(VerificaMigrazioneSubPartizioneEjb.class.getSimpleName()
                + " --- Verifica migrazione sub partizione --- Inizio job");

        StringBuilder errori = new StringBuilder();

        // Nuova funzione di calcolo conclusivo dei totalizzatori in una nuova transazione
        aggiornaTotaliDelleSubpartizioni();

        /* Determino se i microservizi sono in funzione */
        // TODO: DETERMINA SE MICRO IN ESE */
        boolean microserviziInEsecuzione = true;
        // fine TODO
        if (microserviziInEsecuzione) {
            log.info(VerificaMigrazioneSubPartizioneEjb.class.getSimpleName()
                    + " --- Verifica migrazione sub partizione - Microservizi in esecuzione");

            log.info(VerificaMigrazioneSubPartizioneEjb.class.getSimpleName()
                    + " --- Verifica migrazione sub partizione - Eseguo verifica sub partizioni in corso di migrazione");
            // mofificato dal prof.
            me.verificaSubPartizioniInCorsoDiMigrazione();

            log.info(VerificaMigrazioneSubPartizioneEjb.class.getSimpleName()
                    + " --- Verifica migrazione sub partizione - Eseguo verifica sub partizioni in errore di migrazione");
            // modificato dal prof
            boolean nessunFileErroreResettabile = me.verificaSubPartizioniInErroreDiMigrazione();

            if (nessunFileErroreResettabile) {
                errori.append(MsgUtil.getMessage(OST_004));
            }
        } else {
            log.info(VerificaMigrazioneSubPartizioneEjb.class.getSimpleName()
                    + " --- Verifica migrazione sub partizione - Microservizi NON in esecuzione");
        }

        log.info(VerificaMigrazioneSubPartizioneEjb.class.getSimpleName()
                + " --- Verifica migrazione sub partizione - Eseguo verifica sub partizioni migrate");
        // mofificato dal prof.
        me.verificaSubPartizioniMigrate();

        List<String> tiStato = new ArrayList<>();
        tiStato.add(it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.TBS_NON_ELIMINABILE
                .name());
        if (!vmspHelper.getOstMigrazSubPartList(tiStato, null).isEmpty()) {
            errori.append(MsgUtil.getMessage(OST_005));
        }

        /* Scrivo nel LogJob */
        if (errori.length() > 0) {
            jobHelper.writeAtomicLogJob(
                    JobConstants.JobEnum.VERIFICA_MIGRAZIONE_SUBPARTIZIONE.name(),
                    JobConstants.OpTypeEnum.ERRORE.name(), errori.toString());
        } else {
            jobHelper.writeAtomicLogJob(
                    JobConstants.JobEnum.VERIFICA_MIGRAZIONE_SUBPARTIZIONE.name(),
                    JobConstants.OpTypeEnum.FINE_SCHEDULAZIONE.name(), null);
        }

        log.info(VerificaMigrazioneSubPartizioneEjb.class.getSimpleName()
                + " --- Verifica migrazione sub partizione - Esecuzione job terminata con successo!");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void verificaSubPartizioniInCorsoDiMigrazione() {
        List<String> tiStato = new ArrayList<>();
        tiStato.add(it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.MIGRAZ_IN_CORSO
                .name());
        tiStato.add(it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.MIGRAZ_IN_ERRORE
                .name());
        List<OstMigrazSubPart> migrazSubPartList = vmspHelper.getOstMigrazSubPartList(tiStato,
                "niFileMigrazInCorso");

        String numGgMigrazInCorso = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NUM_GG_MIGRAZ_IN_CORSO);
        for (OstMigrazSubPart migrazSubPart : migrazSubPartList) {
            /* Determino i file della subpartizione */
            List<OstMigrazFile> migrazFileList = vmspHelper.getOstMigrazFileBeforeNumGiorni(
                    migrazSubPart.getIdMigrazSubPart(),
                    it.eng.parer.entity.constraint.OstMigrazFile.TiStatoCor.MIGRAZ_IN_CORSO.name(),
                    new BigDecimal(numGgMigrazInCorso));

            for (OstMigrazFile migrazFile : migrazFileList) {
                // mofificato dal prof.
                me.gestisciFileInCorsoMigrazione(migrazSubPart.getIdMigrazSubPart(),
                        migrazFile.getIdMigrazFile());
            }
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void gestisciFileInCorsoMigrazione(long idMigrazSubPart, long idMigrazFile) {
        // Lock esclusivo su subpartizione e file
        OstMigrazSubPart migrazSubPart = vmspHelper.findByIdWithLock(OstMigrazSubPart.class,
                idMigrazSubPart);
        OstMigrazFile migrazFile = vmspHelper.findByIdWithLock(OstMigrazFile.class, idMigrazFile);

        if (migrazFile.getTiStatoCor().equals(
                it.eng.parer.entity.constraint.OstMigrazFile.TiStatoCor.MIGRAZ_IN_CORSO.name())) {
            migrazFile.setTiStatoCor(
                    it.eng.parer.entity.constraint.OstMigrazFile.TiStatoCor.DA_MIGRARE.name());
            migrazFile.setTsRegStatoCor(new Date());
            migrazSubPart
                    .setNiFileDaMigrare(migrazSubPart.getNiFileDaMigrare().add(BigDecimal.ONE));
            migrazSubPart.setNiFileMigrazInCorso(
                    migrazSubPart.getNiFileMigrazInCorso().subtract(BigDecimal.ONE));

            // Recupero lo stato corrente della sub-partizione
            OstStatoMigrazSubPart statoMigrazSubPartCor = vmspHelper.findById(
                    OstStatoMigrazSubPart.class, migrazSubPart.getIdStatoMigrazSubPartCor());
            if (statoMigrazSubPartCor.getTiStato().equals(
                    it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.MIGRAZ_IN_CORSO
                            .name())
                    && migrazSubPart.getNiFileMigrazInCorso().compareTo(BigDecimal.ZERO) == 0
                    && migrazSubPart.getNiFileMigrati().longValueExact() == 0) {
                // Registro lo stato di migrazione
                registraOstStatoMigrazSubPart(migrazSubPart.getIdMigrazSubPart(), new Date(),
                        it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.DA_MIGRARE
                                .name());
            }
        }
    }

    public void registraOstStatoMigrazSubPart(long idMigrazSubPart, Date dataCorrente,
            String tiStato) {
        OstMigrazSubPart migrazSubPart = vmspHelper.findById(OstMigrazSubPart.class,
                idMigrazSubPart);
        OstStatoMigrazSubPart statoMigrazSubPart = new OstStatoMigrazSubPart();
        statoMigrazSubPart.setOstMigrazSubPart(migrazSubPart);
        statoMigrazSubPart.setTsRegStato(dataCorrente);
        statoMigrazSubPart.setTiStato(tiStato);
        vmspHelper.insertEntity(statoMigrazSubPart, true);
        // Aggiorno lo stato corrente della sub partizione
        migrazSubPart.setIdStatoMigrazSubPartCor(
                BigDecimal.valueOf(statoMigrazSubPart.getIdStatoMigrazSubPart()));
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public boolean verificaSubPartizioniInErroreDiMigrazione() {
        int contaFileResettati = 0;

        List<String> tiStato = new ArrayList<>();
        tiStato.add(it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.MIGRAZ_IN_ERRORE
                .name());
        List<OstMigrazSubPart> migrazSubPartList = vmspHelper.getOstMigrazSubPartList(tiStato,
                null);

        String numMaxErr = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NUM_MAX_ERR);
        for (OstMigrazSubPart migrazSubPart : migrazSubPartList) {
            /* Determino i file della subpartizione */
            List<OstMigrazFile> migrazFileList = vmspHelper.getOstMigrazFilePerNumErrori(
                    migrazSubPart.getIdMigrazSubPart(),
                    it.eng.parer.entity.constraint.OstMigrazFile.TiStatoCor.MIGRAZ_IN_ERRORE.name(),
                    new BigDecimal(numMaxErr));

            for (OstMigrazFile migrazFile : migrazFileList) {
                // modificato dal prof
                contaFileResettati += me.gestisciFileErroreMigrazione(
                        migrazSubPart.getIdMigrazSubPart(), migrazFile.getIdMigrazFile());
            }
        }

        if (contaFileResettati == 0) {
            // Se esiste almeno una sub partizione con stato corrente MIGRAZ_IN_ERRORE
            if (!vmspHelper.getOstMigrazSubPartList(tiStato, null).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int gestisciFileErroreMigrazione(long idMigrazSubPart, long idMigrazFile) {
        int count = 0;
        // Lock esclusivo su subpartizione e file
        OstMigrazSubPart migrazSubPart = vmspHelper.findByIdWithLock(OstMigrazSubPart.class,
                idMigrazSubPart);
        OstMigrazFile migrazFile = vmspHelper.findByIdWithLock(OstMigrazFile.class, idMigrazFile);

        if (migrazFile.getTiStatoCor().equals(
                it.eng.parer.entity.constraint.OstMigrazFile.TiStatoCor.MIGRAZ_IN_ERRORE.name())) {
            migrazFile.setTiStatoCor(
                    it.eng.parer.entity.constraint.OstMigrazFile.TiStatoCor.DA_MIGRARE.name());
            migrazFile.setTsRegStatoCor(new Date());
            migrazFile.setTsMigrato(null);
            migrazSubPart
                    .setNiFileDaMigrare(migrazSubPart.getNiFileDaMigrare().add(BigDecimal.ONE));
            migrazSubPart.setNiFileMigrazInErrore(
                    migrazSubPart.getNiFileMigrazInErrore().subtract(BigDecimal.ONE));

            count++;

            // Recupero lo stato corrente della sub-partizione
            if (migrazSubPart.getNiFileMigrazInErrore().compareTo(BigDecimal.ZERO) == 0) {
                String stato = null;
                if (migrazSubPart.getNiFileMigrazInCorso().compareTo(BigDecimal.ZERO) > 0
                        || migrazSubPart.getNiFileMigrati().compareTo(BigDecimal.ZERO) > 0) {
                    stato = it.eng.parer.entity.constraint.OstMigrazFile.TiStatoCor.MIGRAZ_IN_CORSO
                            .name();
                } else {
                    stato = it.eng.parer.entity.constraint.OstMigrazFile.TiStatoCor.DA_MIGRARE
                            .name();
                }
                // Registro lo stato di migrazione
                registraOstStatoMigrazSubPart(migrazSubPart.getIdMigrazSubPart(), new Date(),
                        stato);
            }
        }
        return count;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void verificaSubPartizioniMigrate() {
        // Determino i tablespace
        List<String> tabellaSpazioList = vmspHelper.getTableSpaceList(
                it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.MIGRATA.name());
        Date dataCorrente = new Date();

        // Per ogni tablespace
        for (String tabellaSpazio : tabellaSpazioList) {
            // Verifico che tutte le sub-partizioni che usano il tablespace abbiano stato MIGRATA
            boolean tuttePartizioniTabellaSpazio = vmspHelper
                    .checkAllSubPartitionsWithTableSpaceAndState(tabellaSpazio,
                            it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.MIGRATA
                                    .name());

            if (tuttePartizioniTabellaSpazio) {
                String tiStato = null;
                // Verifica se tablespace usato solo dalla tabella su cui sono definite le
                // subpartizioni
                if (vmspHelper.usoTbsOK(tabellaSpazio)) {
                    tiStato = it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.DA_ELIMINARE
                            .name();
                } else {
                    tiStato = it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.TBS_NON_ELIMINABILE
                            .name();
                }

                List<OstMigrazSubPart> migrazSubPartTableSpace = vmspHelper
                        .getOstMigrazSubPartByTablespace(tabellaSpazio);

                for (OstMigrazSubPart migrazSubPart : migrazSubPartTableSpace) {
                    registraOstStatoMigrazSubPart(migrazSubPart.getIdMigrazSubPart(), dataCorrente,
                            tiStato);
                }
            }
        }
    }

    /*
     * Metodo finale che aggiorna i totalizzatori delle subpartizioni. Questa fase era all'interno
     * dei consumer ma succedeva che si accavallavano tutti i lock sulle subpartizioni e si bloccava
     * tutto.
     */
    public void aggiornaTotaliDelleSubpartizioni() {
        List<OstMigrazSubPart> lista = vmspHelper.getSubPartitionsMigrazInCorsoErroreMoreThanZero();
        for (OstMigrazSubPart ostMigrazSubPart : lista) {
            me.aggiornaTotaliDellaSubpartizioneInNewTransaction(
                    ostMigrazSubPart.getIdMigrazSubPart());
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void aggiornaTotaliDellaSubpartizioneInNewTransaction(long idSubPart) {
        // Locca la subpartizione
        OstMigrazSubPart ostMigrazSubPart = vmspHelper.findByIdWithLock(OstMigrazSubPart.class,
                idSubPart);
        long totMigrati = vmspHelper.getCountComponentsSubPartAndState(ostMigrazSubPart,
                it.eng.parer.entity.constraint.OstMigrazFile.TiStatoCor.MIGRATO.name());
        long totMigrazInCorso = vmspHelper.getCountComponentsSubPartAndState(ostMigrazSubPart,
                it.eng.parer.entity.constraint.OstMigrazFile.TiStatoCor.MIGRAZ_IN_CORSO.name());
        long totMigrazInErrore = vmspHelper.getCountComponentsSubPartAndState(ostMigrazSubPart,
                it.eng.parer.entity.constraint.OstMigrazFile.TiStatoCor.MIGRAZ_IN_ERRORE.name());
        BigDecimal totDaMigrare = ostMigrazSubPart.getNiFileDaMigrare(); // Si deve prendere dal
        // totalizzatore della
        // subpartizione non dal
        // conteggio effettivo
        // sui file!
        ostMigrazSubPart.setNiFileMigrati(new BigDecimal(totMigrati));
        ostMigrazSubPart.setNiFileMigrazInCorso(new BigDecimal(totMigrazInCorso));
        ostMigrazSubPart.setNiFileMigrazInErrore(new BigDecimal(totMigrazInErrore));
        ArrayList<String> al = new ArrayList<>();
        al.add(it.eng.parer.entity.constraint.OstMigrazFile.TiStatoCor.DA_MIGRARE.name());
        al.add(it.eng.parer.entity.constraint.OstMigrazFile.TiStatoCor.MIGRAZ_IN_CORSO.name());
        al.add(it.eng.parer.entity.constraint.OstMigrazFile.TiStatoCor.MIGRAZ_IN_ERRORE.name());
        // MAC#17912 - Errore settaggio stato MIGRATA
        // long totResidui = vmspHelper.getCountComponentsSubPartAndState(ostMigrazSubPart, al);
        long totResidui = totDaMigrare.longValueExact() + totMigrazInCorso + totMigrazInErrore;
        // se tutti i componenti sono nello stato MIGRATO
        if (totResidui == 0) {
            // Registro in OST_STATO_MIGRAZ_SUB_PART
            registraOstStatoMigrazSubPart(ostMigrazSubPart.getIdMigrazSubPart(), new Date(),
                    it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.MIGRATA.name());
        }
        // Se numero di file con migraz in errore e stato MIGRAZ_IN_CORSO...
        OstStatoMigrazSubPart statoMigraz = vmspHelper.findById(OstStatoMigrazSubPart.class,
                ostMigrazSubPart.getIdStatoMigrazSubPartCor());
        if (statoMigraz.getTiStato().equals(
                it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.MIGRAZ_IN_CORSO.name())
                && totMigrazInErrore > 0) {
            registraOstStatoMigrazSubPart(ostMigrazSubPart.getIdMigrazSubPart(), new Date(),
                    it.eng.parer.entity.constraint.OstStatoMigrazSubPart.TiStato.MIGRAZ_IN_ERRORE
                            .name());
        }
    }
}
