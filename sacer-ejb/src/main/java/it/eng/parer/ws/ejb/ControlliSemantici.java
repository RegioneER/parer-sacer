/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.ejb;

import it.eng.parer.entity.AplParamApplic;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.DecParteNumeroRegistro;
import it.eng.parer.entity.OrgAmbiente;
import it.eng.parer.entity.OrgEnte;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.CostantiDB.StatoConservazioneUnitaDoc;
import it.eng.parer.ws.utils.KeyOrdUtility;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import it.eng.parer.ws.versamento.dto.ConfigRegAnno;
import java.util.HashMap;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pagani_S (iniziata)
 * @author Fioravanti_F
 */
@Stateless(mappedName = "ControlliSemantici")
@LocalBean
@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
public class ControlliSemantici {

    private static final Logger log = LoggerFactory.getLogger(ControlliSemantici.class);
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    @EJB
    private ConfigurationHelper configurationHelper;

    public enum TipologieComponente {

        COMPONENTE, SOTTOCOMPONENTE
    }

    public enum TipiGestioneUDAnnullate {

        CARICA, CONSIDERA_ASSENTE
    }

    public RispostaControlli caricaDefaultDaDBParametriApplic(String tipoPar) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrLong(-1);
        rispostaControlli.setrBoolean(false);

        HashMap<String, String> tmpDefaults = new HashMap<String, String>();

        // lista entity JPA ritornate dalle Query
        List<AplParamApplic> tmpAplParamApplic = null;

        try {
            // carico i parametri applicativi
            String queryStr = "select tpa " + "from AplParamApplic tpa " + "where tpa.tiParamApplic = :tiParamApplicIn";

            javax.persistence.Query query = entityManager.createQuery(queryStr, AplParamApplic.class);
            query.setParameter("tiParamApplicIn", tipoPar);
            tmpAplParamApplic = query.getResultList();

            if (!tmpAplParamApplic.isEmpty()) {
                for (AplParamApplic tud : tmpAplParamApplic) {
                    // Prendo l'unico valore presente, quello di tipo APPLIC
                    tmpDefaults.put(tud.getNmParamApplic(), configurationHelper.getValoreParamApplic(
                            tud.getNmParamApplic(), null, null, null, null, CostantiDB.TipoAplVGetValAppart.APPLIC));
                }
                rispostaControlli.setrObject(tmpDefaults);
                rispostaControlli.setrBoolean(true);
            } else {
                rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
                rispostaControlli.setDsErr(
                        MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666, "ControlliSemantici.caricaDefaultDaDB: "
                                + "Applicativo chiamante non correttamente configurato nella tabella AplParamApplic"));
                log.error("ControlliSemantici.caricaDefaultDaDB: "
                        + "Applicativo chiamante non correttamente configurato nella tabella AplParamApplic");
            }
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "ControlliSemantici.caricaDefaultDaDB: " + e.getMessage()));
            log.error("Eccezione nella lettura  della tabella AplParamApplic ", e);
        }

        return rispostaControlli;
    }

    public RispostaControlli checkIdStrut(CSVersatore vers, Costanti.TipiWSPerControlli tipows) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrLong(-1);

        // return -1 che è il codice di errore
        long idAmb = -1;
        long idEnte = -1;

        // prendo i paramentri dell'xml
        String amb = vers.getAmbiente();
        String ente = vers.getEnte();
        String strut = vers.getStruttura();

        // lista entity JPA ritornate dalle Query
        List<OrgStrut> orgStrutS = null;
        List<OrgEnte> orgEnteS = null;
        List<OrgAmbiente> orgAmbienteS = null;

        // lancio query di controllo
        try {
            // controllo ambiente
            String queryStr = "select amb " + "from OrgAmbiente amb " + "where amb.nmAmbiente =  :nmAmbienteIn";
            javax.persistence.Query query = entityManager.createQuery(queryStr, OrgAmbiente.class);
            query.setParameter("nmAmbienteIn", amb);
            orgAmbienteS = query.getResultList();

            // assente
            if (orgAmbienteS.isEmpty()) {
                switch (tipows) {
                case VERSAMENTO_RECUPERO:
                    rispostaControlli.setCodErr(MessaggiWSBundle.UD_001_001);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.UD_001_001, amb));
                    break;
                case ANNULLAMENTO:
                    rispostaControlli.setCodErr(MessaggiWSBundle.RICH_ANN_VERS_004);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.RICH_ANN_VERS_004));
                    break;
                }
                return rispostaControlli;
            }
            // too many rows
            if (orgAmbienteS.size() > 1) {
                rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
                rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                        "ControlliSemantici.checkIdStrut: " + "Ambiente duplicato"));
                log.error("ControlliSemantici.checkIdStrut: " + "Ambiente duplicato");
                return rispostaControlli;
            }
            for (OrgAmbiente a : orgAmbienteS) {
                idAmb = a.getIdAmbiente();
            }

            // controllo ente
            queryStr = "select ente " + "from OrgEnte ente " + "where ente.orgAmbiente.idAmbiente = :idAmbienteIn "
                    + " and ente.nmEnte = :nmEnteIn ";
            query = entityManager.createQuery(queryStr, OrgEnte.class);
            query.setParameter("idAmbienteIn", idAmb);
            query.setParameter("nmEnteIn", ente);
            orgEnteS = query.getResultList();
            // assente
            if (orgEnteS.isEmpty()) {
                switch (tipows) {
                case VERSAMENTO_RECUPERO:
                    rispostaControlli.setCodErr(MessaggiWSBundle.UD_001_002);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.UD_001_002, ente));
                    break;
                case ANNULLAMENTO:
                    rispostaControlli.setCodErr(MessaggiWSBundle.RICH_ANN_VERS_005);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.RICH_ANN_VERS_005));
                    break;
                }
                return rispostaControlli;
            }
            // too many rows
            if (orgEnteS.size() > 1) {
                rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
                rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                        "ControlliSemantici.checkIdStrut: " + "Ente duplicato"));
                log.error("ControlliSemantici.checkIdStrut: " + "Ente duplicato");
                return rispostaControlli;
            }
            for (OrgEnte e : orgEnteS) {
                idEnte = e.getIdEnte();
            }

            // controllo struttura
            queryStr = "select strut " + "from OrgStrut strut " + "where strut.orgEnte.idEnte = :idEnteIn "
                    + " and strut.nmStrut = :nmStrutIn ";
            query = entityManager.createQuery(queryStr, OrgStrut.class);
            query.setParameter("idEnteIn", idEnte);
            query.setParameter("nmStrutIn", strut);
            orgStrutS = query.getResultList();
            // assente
            if (orgStrutS.isEmpty()) {
                switch (tipows) {
                case VERSAMENTO_RECUPERO:
                    rispostaControlli.setCodErr(MessaggiWSBundle.UD_001_003);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.UD_001_003, strut));
                    break;
                case ANNULLAMENTO:
                    rispostaControlli.setCodErr(MessaggiWSBundle.RICH_ANN_VERS_006);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.RICH_ANN_VERS_006));
                    break;
                }
                return rispostaControlli;
            }
            // too many rows
            if (orgStrutS.size() > 1) {
                rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
                rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                        "ControlliSemantici.checkIdStrut: " + "Struttura duplicata"));
                log.error("ControlliSemantici.checkIdStrut: " + "Struttura duplicata");
                return rispostaControlli;
            }
            if (tipows == Costanti.TipiWSPerControlli.VERSAMENTO_RECUPERO
                    && orgStrutS.get(0).getFlTemplate().equals("1")) {
                rispostaControlli.setCodErr(MessaggiWSBundle.UD_001_015);
                rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.UD_001_015, strut));
                return rispostaControlli;
            }
            //
            rispostaControlli.setrLong(orgStrutS.get(0).getIdStrut());
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "ControlliSemantici.checkIdStrut: " + e.getMessage()));
            log.error("Eccezione nella lettura  della tabella di decodifica ", e);
        }

        return rispostaControlli;
    }

    public RispostaControlli checkChiave(CSChiave key, long idStruttura, TipiGestioneUDAnnullate tguda) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrLong(-1);
        rispostaControlli.setrBoolean(false);

        // prendo i paramentri dell'xml
        String numero = key.getNumero();
        Long anno = key.getAnno();
        String tipoReg = key.getTipoRegistro();

        // lista entity JPA ritornate dalle Query
        List<AroUnitaDoc> unitaDocS = null;

        // lancio query di controllo
        try {
            // ricavo le ud presenti in base ai parametri impostati
            String queryStr = "select ud " + "from AroUnitaDoc ud " + "where ud.orgStrut.idStrut = :idStrutIn "
                    + " and ud.cdKeyUnitaDoc = :cdKeyUnitaDocIn " + " and ud.aaKeyUnitaDoc = :aaKeyUnitaDocIn "
                    + " and ud.cdRegistroKeyUnitaDoc = :cdRegistroKeyUnitaDocIn " + " order by ud.dtCreazione desc";

            javax.persistence.Query query = entityManager.createQuery(queryStr, AroUnitaDoc.class);
            query.setParameter("idStrutIn", idStruttura);
            query.setParameter("cdKeyUnitaDocIn", numero);
            query.setParameter("aaKeyUnitaDocIn", anno);
            query.setParameter("cdRegistroKeyUnitaDocIn", tipoReg);
            unitaDocS = query.getResultList();

            // chiave già presente (uno o più righe trovate, mi interessa solo l'ultima - più recente)
            if (unitaDocS.size() > 0) {
                StatoConservazioneUnitaDoc scud = StatoConservazioneUnitaDoc
                        .valueOf(unitaDocS.get(0).getTiStatoConservazione());
                if (scud == StatoConservazioneUnitaDoc.ANNULLATA
                        && tguda == TipiGestioneUDAnnullate.CONSIDERA_ASSENTE) {
                    // commuto l'errore in UD annullata e rendo true come risposta: in pratica come se non
                    // avesse trovato l'UD ma con un errore diverso: è lo stesso comportamento della vecchia versione
                    // del metodo
                    rispostaControlli.setCodErr(MessaggiWSBundle.UD_012_002);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.UD_012_002,
                            MessaggiWSFormat.formattaUrnPartUnitaDoc(key)));
                    rispostaControlli.setrBoolean(true);
                } else {
                    // gestione normale: ho trovato l'UD e non è annullata.
                    // Oppure è annullata e voglio caricarla lo stesso (il solo caso è nel ws recupero stato UD)
                    // intanto rendo l'errore di chiave già presente
                    rispostaControlli.setCodErr(MessaggiWSBundle.UD_002_001);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.UD_002_001,
                            MessaggiWSFormat.formattaUrnPartUnitaDoc(key)));

                    rispostaControlli.setrLong(unitaDocS.get(0).getIdUnitaDoc());
                    // se la chiave è già presente, oltre all'id dell'UD trovata,
                    // recupero il tipo di salvataggio. Mi serve sia nell'aggiunta documenti
                    // che nel recupero UD
                    rispostaControlli.setrString(unitaDocS.get(0).getDecTipoUnitaDoc().getTiSaveFile());
                    //
                    rispostaControlli.setrStringExtended(unitaDocS.get(0).getDecTipoUnitaDoc().getNmTipoUnitaDoc());
                    // lo stato conservazione viene usato per l'aggiunta doc:
                    // non posso aggiungere doc se l'ud è nello stato sbagliato
                    rispostaControlli.setrObject(scud);
                    rispostaControlli.setrLongExtended(unitaDocS.get(0).getIdDecRegistroUnitaDoc());
                    // **************
                    // EXTENDED VALUES
                    // **************
                    // recupero chiave normalizzata (se esiste)
                    rispostaControlli.getrMap().put(RispostaControlli.ValuesOnrMap.CD_KEY_NORMALIZED.name(),
                            unitaDocS.get(0).getCdKeyUnitaDocNormaliz());
                }
                return rispostaControlli;
            }

            // Chiave non trovata
            rispostaControlli.setCodErr(MessaggiWSBundle.UD_005_001);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.UD_005_001,
                    MessaggiWSFormat.formattaUrnPartUnitaDoc(key)));
            rispostaControlli.setrBoolean(true);

        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "ControlliSemantici.checkChiave: " + e.getMessage()));
            log.error("Eccezione nella lettura  della tabella di decodifica ", e);
        }

        return rispostaControlli;
    }

    // chiamata anche dal job, rende la configurazione per validare i numeri/chiave
    // in base all'anno del registro
    public ConfigRegAnno caricaPartiAARegistro(long idAaRegistroUnitaDoc) {
        ConfigRegAnno tmpConfAnno = new ConfigRegAnno(idAaRegistroUnitaDoc);

        String queryStr = "select t from DecParteNumeroRegistro t "
                + "where t.decAaRegistroUnitaDoc.idAaRegistroUnitaDoc " + "= :idAaRegistroUnitaDoc "
                + "order by t.niParteNumeroRegistro";
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        query.setParameter("idAaRegistroUnitaDoc", idAaRegistroUnitaDoc);
        List<DecParteNumeroRegistro> tmpLstP = (List<DecParteNumeroRegistro>) query.getResultList();

        for (DecParteNumeroRegistro tmpParte : tmpLstP) {
            ConfigRegAnno.ParteRegAnno tmpPRanno = tmpConfAnno.aggiungiParte();
            tmpPRanno.setNumParte(tmpParte.getNiParteNumeroRegistro().intValue());
            tmpPRanno.setNomeParte(tmpParte.getNmParteNumeroRegistro());
            tmpPRanno.setMaxLen(tmpParte.getNiMaxCharParte() != null ? tmpParte.getNiMaxCharParte().longValue() : -1);
            tmpPRanno.setMinLen(tmpParte.getNiMinCharParte() != null ? tmpParte.getNiMinCharParte().longValue() : 0);

            //
            tmpPRanno.setSeparatore((tmpParte.getTiCharSep() != null && tmpParte.getTiCharSep().isEmpty()) ? " "
                    : tmpParte.getTiCharSep());
            /*
             * se il separatore è una stringa non-nulla ma vuota, il valore viene letto come uno spazio. nel DB è
             * memorizzato come CHAR(1), pad-dato -al salvataggio- da Oracle, e che al momento della lettura viene
             * trim-ato da eclipselink. Quindi con questo sistema ricostruisco il valore originale se questo era uno
             * spazio
             */
            //
            // Nota: nel DB la variabile tiParte ha tre valori mutualmente esclusivi.
            // in questo caso, vengono gestiti come 3 flag separati perché i test relativi
            // vengono effettuati in parti diverse del codice
            tmpPRanno.setMatchAnnoChiave(
                    tmpParte.getTiParte() != null && tmpParte.getTiParte().equals(ConfigRegAnno.TiParte.ANNO.name()));
            tmpPRanno.setMatchRegistro(tmpParte.getTiParte() != null
                    && tmpParte.getTiParte().equals(ConfigRegAnno.TiParte.REGISTRO.name()));
            tmpPRanno.setUsaComeProgressivo(
                    tmpParte.getTiParte() != null && tmpParte.getTiParte().equals(ConfigRegAnno.TiParte.PROGR.name()));
            tmpPRanno.setTipoCalcolo(KeyOrdUtility.TipiCalcolo.valueOf(tmpParte.getTiCharParte()));
            tmpPRanno.setTiPadding(
                    tmpParte.getTiPadSxParte() != null ? ConfigRegAnno.TipiPadding.valueOf(tmpParte.getTiPadSxParte())
                            : ConfigRegAnno.TipiPadding.NESSUNO);
            ConfigRegAnno.impostaValoriAccettabili(tmpPRanno, tmpParte.getDlValoriParte());
        }
        tmpConfAnno.ElaboraParti();
        return tmpConfAnno;
    }

    /**
     * Verifica se data id struttura risulta cessata
     * 
     * @param idStrut
     *            id struttura
     * 
     * @return RispostaControlli con risultato della verifica effettuata
     */
    public RispostaControlli checkStrutCessata(long idStrut) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(true); // default : passa il controllo

        try {
            OrgStrut orgStrut = entityManager.find(OrgStrut.class, idStrut);
            if (orgStrut == null) {
                rispostaControlli.setrBoolean(false);
                rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666P);
                rispostaControlli
                        .setDsErr("Eccezione ControlliRecupero.checkStruttCessata: non presente per id " + idStrut);
            } else {
                //
                boolean result = StringUtils.isNotBlank(orgStrut.getFlCessato())
                        && orgStrut.getFlCessato().equalsIgnoreCase(CostantiDB.Flag.TRUE);
                if (result) {
                    rispostaControlli.setrBoolean(false);
                    rispostaControlli.setCodErr(MessaggiWSBundle.UD_001_020);
                    rispostaControlli
                            .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.UD_001_020, orgStrut.getNmStrut()));
                }
            }
        } catch (Exception e) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecupero.checkStruttCessata " + e.getMessage()));
            log.error("Eccezione nella verifica del flag cessato della struttura ", e);
        }
        return rispostaControlli;
    }

}
