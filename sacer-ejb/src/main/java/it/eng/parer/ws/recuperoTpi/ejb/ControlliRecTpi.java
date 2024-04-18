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
package it.eng.parer.ws.recuperoTpi.ejb;

import it.eng.parer.entity.AroCompDoc;
import it.eng.parer.entity.AroDoc;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.IamUser;
import it.eng.parer.entity.RecDtVersRecup;
import it.eng.parer.entity.RecSessioneRecup;
import it.eng.parer.entity.RecUnitaDocRecup;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.recupero.dto.ParametriRecupero;
import it.eng.parer.ws.recupero.dto.RecuperoExt;
import it.eng.parer.ws.recuperoTpi.dto.DatiSessioneRecupero;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBContext;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Fioravanti_F
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "ControlliRecTpi")
@LocalBean
public class ControlliRecTpi {

    private static final Logger log = LoggerFactory.getLogger(ControlliRecTpi.class);
    @Resource
    EJBContext context;
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;
    @EJB
    RecuperoCompFS recuperoCompFS;

    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public RispostaControlli caricaDateDocumenti(ParametriRecupero parametri) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);

        String queryStr;
        StringBuilder querySb = new StringBuilder();
        javax.persistence.Query query = null;
        LinkedHashSet<Date> dates = new LinkedHashSet<>();

        // determina l'insieme delle date da recuperare e per le quali creare i record in rec_dt_vers_recup
        List<AroDoc> aroDocs;
        try {
            switch (parametri.getTipoEntitaSacer()) {
            case UNI_DOC:
            case UNI_DOC_DIP:
            case UNI_DOC_UNISYNCRO:
                // EVO#20972
            case UNI_DOC_UNISYNCRO_V2:
                // end EVO#20972
            case UNI_DOC_DIP_ESIBIZIONE: // di fatto sono lo stesso oggetto
                querySb.append("select t from AroDoc t " + "where t.aroUnitaDoc.idUnitaDoc = :idParametroIn ");
                if (parametri.getIdTipoDoc() != null) {
                    querySb.append("and t.decTipoDoc.idTipoDoc = :idTipoDoc ");
                }
                query = entityManager.createQuery(querySb.toString());
                query.setParameter("idParametroIn", parametri.getIdUnitaDoc());
                if (parametri.getIdTipoDoc() != null) {
                    query.setParameter("idTipoDoc", parametri.getIdTipoDoc());
                }
                break;
            case DOC:
            case DOC_DIP:
            case DOC_DIP_ESIBIZIONE:
                queryStr = "select t from AroDoc  t " + "where t.idDoc = :idParametroIn ";
                query = entityManager.createQuery(queryStr);
                query.setParameter("idParametroIn", parametri.getIdDocumento());
                break;
            case COMP:
            case COMP_DIP:
            case COMP_DIP_ESIBIZIONE:
            case SUB_COMP:
                queryStr = "select t from AroDoc t " + "join t.aroStrutDocs sd " + "join sd.aroCompDocs cd "
                        + "where cd.idCompDoc = :idParametroIn ";
                query = entityManager.createQuery(queryStr);
                query.setParameter("idParametroIn", parametri.getIdComponente());
                break;
            default:
                throw new IllegalArgumentException("Tipo entità non supportato");
            }
            /*
             * tronco le date mantenenendo solo la parte data, e le inserisco in un LinkedHashSet in modo che i
             * documenti versati nello stesso giorno producano una sola entry. Questo per compensare l'impossibilità di
             * effettuare una operazione DISTINCT via JPQL solo sulla parte "data" di una serie di timestamp.
             */
            aroDocs = query.getResultList();
            if (aroDocs.size() > 0) {
                for (AroDoc doc : aroDocs) {
                    dates.add(DateUtils.truncate(doc.getDtCreazione(), Calendar.DAY_OF_MONTH));
                }
                rispostaControlli.setrObject(dates);
            }
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecTpi.caricaDateDocumenti " + e.getMessage()));
            log.error("Eccezione nel recupero delle date dei documenti dell'UD da recuperare ", e);
        }

        return rispostaControlli;
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public RispostaControlli verificaPrenotaRecupero(ParametriRecupero parametri,
            DatiSessioneRecupero datiSessDaCreare) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<RecUnitaDocRecup> recUnitaDocRecups;
        List<RecSessioneRecup> recSessioneRecups;
        RecUnitaDocRecup tmpRecUnitaDocRecup = null;
        RecSessioneRecup tmpRecSessioneRecup;
        boolean creaSessioneRec = false;
        //
        try {
            //
            String queryStr;
            javax.persistence.Query query;
            queryStr = "select t from RecUnitaDocRecup t where " + "t.aroUnitaDoc.idUnitaDoc = :idUnitaDocIn";
            query = entityManager.createQuery(queryStr);
            query.setParameter("idUnitaDocIn", parametri.getIdUnitaDoc());
            recUnitaDocRecups = query.setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .setHint("javax.persistence.lock.timeout", 25000).getResultList();
            if (recUnitaDocRecups.size() > 0) {
                tmpRecUnitaDocRecup = recUnitaDocRecups.get(0);
                queryStr = "select t from RecSessioneRecup t where  " + "t.recUnitaDocRecup = :recUnitaDocRecupIn "
                        + "order by t.dtApertura DESC";
                query = entityManager.createQuery(queryStr);
                query.setParameter("recUnitaDocRecupIn", tmpRecUnitaDocRecup);
                recSessioneRecups = query.getResultList();
                if (recSessioneRecups.size() > 0) {
                    //
                    tmpRecSessioneRecup = recSessioneRecups.get(0);
                    datiSessDaCreare.setIdRecSessioneRecupero(tmpRecSessioneRecup.getIdSessioneRecup());
                    switch (JobConstants.StatoSessioniRecupEnum
                            .valueOf(tmpRecSessioneRecup.getTiStatoSessioneRecup())) {
                    case CHIUSO_OK:
                        creaSessioneRec = false;
                        rispostaControlli.setrBoolean(true);
                        //
                        this.calcolaErrore(rispostaControlli, tmpRecSessioneRecup, parametri);
                        break;
                    case CHIUSO_ERR:
                        /*
                         * nota rendo un codice errore ed un messaggio, ma pongo a true il valore boolean di risposta.
                         * In questo modo il chiamante può segnalare una notifica di Warning invece che di Errore.
                         */
                        creaSessioneRec = true;
                        //
                        /**
                         * La precedente richiesta di recupero dei file dell'unità documentaria {0} è in errore: [{1}].
                         * SACER proverà nuovamente a rendere disponibili i file dell'unità documentaria non appena
                         * possibile
                         */
                        rispostaControlli.setrBoolean(true);
                        rispostaControlli.setCodErr(MessaggiWSBundle.SESREC_001_002);
                        rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.SESREC_001_002,
                                parametri.getDescUnitaDoc(), String.format("%s - %s", tmpRecSessioneRecup.getCdErr(),
                                        tmpRecSessioneRecup.getDlErr())));
                        break;
                    case ELIMINATO:
                        /*
                         * creo la sessione di recupero inoltre nello step successivo produco un messaggio di warning
                         * per indicare che i dati saranno pronti il prima possibile
                         */
                        creaSessioneRec = true;
                        break;
                    case IN_CORSO:
                        // rende warning e non crea la sessione nuova
                        creaSessioneRec = false;
                        //
                        /**
                         * Il recupero dei file dell'unità documentaria {0} è in corso
                         */
                        rispostaControlli.setrBoolean(true);
                        rispostaControlli.setCodErr(MessaggiWSBundle.SESREC_002_001);
                        rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.SESREC_002_001,
                                parametri.getDescUnitaDoc()));
                        //
                        this.calcolaErrore(rispostaControlli, tmpRecSessioneRecup, parametri);
                        break;
                    }
                } else {
                    /*
                     * creo la sessione di recupero inoltre nello step successivo produco un messaggio di warning per
                     * indicare che i dati saranno pronti il prima possibile
                     */
                    creaSessioneRec = true;
                }
            } else {
                /*
                 * creo l'UD recupero e la sessione di recupero; inoltre nello step successivo produco un messaggio di
                 * warning per indicare che i dati saranno pronti il prima possibile
                 */
                tmpRecUnitaDocRecup = new RecUnitaDocRecup();
                tmpRecUnitaDocRecup.setAroUnitaDoc(entityManager.find(AroUnitaDoc.class, parametri.getIdUnitaDoc()));
                entityManager.persist(tmpRecUnitaDocRecup);
                entityManager.flush();
                creaSessioneRec = true;
            }
            //
            if (creaSessioneRec) {
                if (rispostaControlli.getCodErr() == null || rispostaControlli.getCodErr().isEmpty()) {
                    /*
                     * nota: rendo un codice errore ed un messaggio, ma pongo a true il valore boolean di risposta. In
                     * questo modo il chiamante può segnalare una notifica di Warning invece che di Errore.
                     */
                    /**
                     * Il recupero dei file dell'Unità Documentaria {0} sarà disponibile il prima possibile
                     */
                    rispostaControlli.setrBoolean(true);
                    rispostaControlli.setCodErr(MessaggiWSBundle.SESREC_001_001);
                    rispostaControlli.setDsErr(
                            MessaggiWSBundle.getString(MessaggiWSBundle.SESREC_001_001, parametri.getDescUnitaDoc()));
                }
                // crea riga sessione
                // crea righe date
                datiSessDaCreare.setRecUnitaDocRecup(tmpRecUnitaDocRecup);
                datiSessDaCreare.setStatoSess(JobConstants.StatoSessioniRecupEnum.IN_CORSO);
                datiSessDaCreare.setStatoDtVers(JobConstants.StatoDtVersRecupEnum.DA_RECUPERARE);
                datiSessDaCreare.setChiudiSessione(false);
                //
                this.creaSessioneRecupero(parametri, datiSessDaCreare);
                //
            }
        } catch (Exception e) {
            context.setRollbackOnly();
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecTpi.verificaPrenotaRecupero " + e.getMessage()));
            log.error("Eccezione nella ControlliRecTpi.verificaPrenotaRecupero ", e);
        }

        return rispostaControlli;
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public RispostaControlli creaSessRecuperoChiusa(ParametriRecupero parametri,
            DatiSessioneRecupero datiSessDaCreare) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<RecUnitaDocRecup> recUnitaDocRecups;
        RecUnitaDocRecup tmpRecUnitaDocRecup = null;

        try {
            //
            String queryStr;
            javax.persistence.Query query;
            queryStr = "select t from RecUnitaDocRecup t where " + "t.aroUnitaDoc.idUnitaDoc = :idUnitaDocIn";
            query = entityManager.createQuery(queryStr);
            query.setParameter("idUnitaDocIn", parametri.getIdUnitaDoc());
            recUnitaDocRecups = query.setLockMode(LockModeType.PESSIMISTIC_WRITE)
                    .setHint("javax.persistence.lock.timeout", 25000).getResultList();
            if (recUnitaDocRecups.size() > 0) {
                tmpRecUnitaDocRecup = recUnitaDocRecups.get(0);
            } else {
                tmpRecUnitaDocRecup = new RecUnitaDocRecup();
                tmpRecUnitaDocRecup.setAroUnitaDoc(entityManager.find(AroUnitaDoc.class, parametri.getIdUnitaDoc()));
                entityManager.persist(tmpRecUnitaDocRecup);
                entityManager.flush();
            }
            datiSessDaCreare.setRecUnitaDocRecup(tmpRecUnitaDocRecup);
            datiSessDaCreare.setChiudiSessione(true);
            //
            this.creaSessioneRecupero(parametri, datiSessDaCreare);
            //
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            context.setRollbackOnly();
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecTpi.creaSessRecuperoChiusa " + e.getMessage()));
            log.error("Eccezione nella ControlliRecTpi.creaSessRecuperoChiusa ", e);
        }

        return rispostaControlli;
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public RispostaControlli chiudiSessRecupero(RecuperoExt recupero) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        long numRigheAgg;
        DatiSessioneRecupero datiSessioneRecupero = recupero.getDatiSessioneRecupero();
        try {
            //
            String queryStr;
            javax.persistence.Query query;
            queryStr = "update RecSessioneRecup t " + "set t.dtChiusura = :dtChiusuraIn, "
                    + "t.tiStatoSessioneRecup = :tiStatoSessioneRecupIn, " + "t.cdErr = :cdErrIn, "
                    + "t.dlErr = :dlErrIn " + "where t.idSessioneRecup = :idSessioneRecupIn";
            query = entityManager.createQuery(queryStr);
            query.setParameter("dtChiusuraIn", new Date());
            query.setParameter("cdErrIn", datiSessioneRecupero.getErrorCode());
            query.setParameter("dlErrIn", datiSessioneRecupero.getErrorMessage());
            query.setParameter("tiStatoSessioneRecupIn", datiSessioneRecupero.getStatoSess().name());
            query.setParameter("idSessioneRecupIn", datiSessioneRecupero.getIdRecSessioneRecupero());
            numRigheAgg = query.executeUpdate();

            // rimozione dei file, solo se esistono e se il TPI è attivo.
            // da notare che questo test è inutile, visto che il metodo in caso
            // contrario non viene invocato.
            if (recupero.getTipoSalvataggioFile() == CostantiDB.TipoSalvataggioFile.FILE && recupero.isTpiAbilitato()) {
                rispostaControlli = recuperoCompFS.eliminaFileTempRecuperoTPI(recupero);
            } else {
                rispostaControlli.setrBoolean(true);
            }

        } catch (Exception e) {
            context.setRollbackOnly();
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecTpi.chiudiSessRecupero " + e.getMessage()));
            log.error("Eccezione nella ControlliRecTpi.chiudiSessRecupero ", e);
        }

        return rispostaControlli;
    }

    private void calcolaErrore(RispostaControlli rc, RecSessioneRecup rsr, ParametriRecupero par) {
        if (JobConstants.TipoSessioniRecupEnum.valueOf(rsr.getTiSessioneRecup()) != par.getTipoRichiedente()) {
            if (par.getTipoRichiedente() == JobConstants.TipoSessioniRecupEnum.DOWNLOAD) {
                /**
                 * Il recupero dei file dell'unità documentaria {0} è in corso da parte del servizio di recupero; per il
                 * momento non è possibile richiedere il recupero dell?unità documentaria mediante download on-line
                 */
                rc.setrBoolean(false);
                rc.setCodErr(MessaggiWSBundle.SESREC_002_005);
                rc.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.SESREC_002_005, par.getDescUnitaDoc()));
            } else {
                /**
                 * Il recupero dei file dell'unità documentaria {0} è in corso da parte di un operatore on-line; per il
                 * momento non è possibile richiedere il recupero dell?unità documentaria mediante servizio web
                 */
                rc.setrBoolean(false);
                rc.setCodErr(MessaggiWSBundle.SESREC_002_004);
                rc.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.SESREC_002_004, par.getDescUnitaDoc()));
            }
        } else if (rsr.getIamUser().getIdUserIam() != par.getUtente().getIdUtente()) {
            /**
             * E' già in corso il recupero di file dell'unità documentaria {0} da parte di un altro utente
             */
            rc.setrBoolean(false);
            rc.setCodErr(MessaggiWSBundle.SESREC_002_003);
            rc.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.SESREC_002_003, par.getDescUnitaDoc()));
        } else if (CostantiDB.TipiEntitaRecupero.valueOf(rsr.getTiOutputRecup()) != par.getTipoEntitaSacer()) {
            /**
             * L'utente ha già in corso il recupero di file dell'unità documentaria {0}: si deve aspettare che tale
             * recupero sia terminato
             */
            rc.setrBoolean(false);
            rc.setCodErr(MessaggiWSBundle.SESREC_002_002);
            rc.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.SESREC_002_002, par.getDescUnitaDoc()));
        }
    }

    private RispostaControlli creaSessioneRecupero(ParametriRecupero parametri,
            DatiSessioneRecupero datiSessioneRecupero) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);

        /*
         * nota bene: non c'è un blocco try... catch perché in caso di errore l'eccezione deve essere sollevata al
         * metodo chiamante, provocando il rollback di tutto.
         */
        RecSessioneRecup tmpRecSessioneRecup = new RecSessioneRecup();
        tmpRecSessioneRecup.setDtApertura(new Date());
        if (datiSessioneRecupero.isChiudiSessione()) {
            tmpRecSessioneRecup.setDtChiusura(new Date());
        }
        tmpRecSessioneRecup.setTiOutputRecup(parametri.getTipoEntitaSacer().name());
        tmpRecSessioneRecup.setTiSessioneRecup(parametri.getTipoRichiedente().name());
        tmpRecSessioneRecup.setTiStatoSessioneRecup(datiSessioneRecupero.getStatoSess().name());
        tmpRecSessioneRecup.setCdErr(datiSessioneRecupero.getErrorCode());
        tmpRecSessioneRecup.setDlErr(datiSessioneRecupero.getErrorMessage());
        tmpRecSessioneRecup.setRecUnitaDocRecup(datiSessioneRecupero.getRecUnitaDocRecup());
        tmpRecSessioneRecup.setIamUser(entityManager.find(IamUser.class, parametri.getUtente().getIdUtente()));

        // nel caso sia richiesta una unità doc, normale, DIP, DIP per esibizione o per unisyncro, non vengono indicati
        // documento e componente
        if (parametri.getTipoEntitaSacer() == CostantiDB.TipiEntitaRecupero.DOC
                || parametri.getTipoEntitaSacer() == CostantiDB.TipiEntitaRecupero.DOC_DIP
                || parametri.getTipoEntitaSacer() == CostantiDB.TipiEntitaRecupero.DOC_DIP_ESIBIZIONE) {
            tmpRecSessioneRecup.setAroDoc(entityManager.find(AroDoc.class, parametri.getIdDocumento()));
        } else if (parametri.getTipoEntitaSacer() == CostantiDB.TipiEntitaRecupero.COMP
                || parametri.getTipoEntitaSacer() == CostantiDB.TipiEntitaRecupero.SUB_COMP
                || parametri.getTipoEntitaSacer() == CostantiDB.TipiEntitaRecupero.COMP_DIP
                || parametri.getTipoEntitaSacer() == CostantiDB.TipiEntitaRecupero.COMP_DIP_ESIBIZIONE) {
            tmpRecSessioneRecup.setAroCompDoc(entityManager.find(AroCompDoc.class, parametri.getIdComponente()));
        }
        entityManager.persist(tmpRecSessioneRecup);
        entityManager.flush();
        //
        datiSessioneRecupero.setIdRecSessioneRecupero(tmpRecSessioneRecup.getIdSessioneRecup());
        //
        if (datiSessioneRecupero.getDateDocumenti() != null) {
            for (Date data : datiSessioneRecupero.getDateDocumenti()) {
                RecDtVersRecup tmpDtVersRecup = new RecDtVersRecup();
                tmpDtVersRecup.setRecSessioneRecup(tmpRecSessioneRecup);
                // MAC#27666
                tmpDtVersRecup.setDtVers(data.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                // end MAC#27666
                tmpDtVersRecup.setTiStatoDtVersRecup(datiSessioneRecupero.getStatoDtVers().name());
                /*
                 * per ogni record è definito l'indicatore che segnala se la data di versamento è definita per
                 * migrazione in funzione del fatto che sia antecedente o pari al parametro dataFineUsoBlob, oppure che
                 * sia successiva
                 */
                if (data.after(datiSessioneRecupero.getDataFineUsoBlob())) {
                    tmpDtVersRecup.setFlMigraz("0");
                } else {
                    tmpDtVersRecup.setFlMigraz("1");
                }
                tmpDtVersRecup.setDtStatoDtVersRecup(new Date());
                entityManager.persist(tmpDtVersRecup);
            }
        }

        rispostaControlli.setrBoolean(true);

        return rispostaControlli;
    }
}
