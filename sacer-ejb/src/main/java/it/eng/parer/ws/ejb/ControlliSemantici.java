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

/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.ws.ejb;

import java.math.BigDecimal;
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

import it.eng.paginator.util.HibernateUtils;
import it.eng.parer.entity.AplParamApplic;
import it.eng.parer.entity.AroCompDoc;
import it.eng.parer.entity.AroCompUrnCalc;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.DecParteNumeroRegistro;
import it.eng.parer.entity.FasFascicolo;
import it.eng.parer.entity.OrgAmbiente;
import it.eng.parer.entity.OrgEnte;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.AroCompUrnCalc.TiUrn;
import it.eng.parer.entity.constraint.FasFascicolo.TiStatoConservazione;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.CSChiaveFasc;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.recupero.dto.ParametriRecupero;
import it.eng.parer.ws.recupero.dto.RecuperoExt;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.CostantiDB.StatoConservazioneUnitaDoc;
import it.eng.parer.ws.utils.KeyOrdUtility;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import it.eng.parer.ws.versamento.dto.ConfigRegAnno;
import it.eng.parer.ws.xml.versReqStato.ChiaveType;
import it.eng.parer.ws.xml.versReqStato.VersatoreType;

/**
 *
 * @author Pagani_S (iniziata)
 * @author Fioravanti_F
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "ControlliSemantici")
@LocalBean
@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
public class ControlliSemantici {

    private static final Logger log = LoggerFactory.getLogger(ControlliSemantici.class);
    public static final String ERRORE_TABELLA_DECODIFICA = "Eccezione nella lettura  della tabella di decodifica ";
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

    // EVO#13993
    public enum TipiGestioneFASCAnnullati {

        CARICA, CONSIDERA_ASSENTE
    }
    // end EVO#13993

    public RispostaControlli caricaDefaultDaDBParametriApplic(String tipoPar) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrLong(-1);
        rispostaControlli.setrBoolean(false);

        HashMap<String, String> tmpDefaults = new HashMap<>();

        // lista entity JPA ritornate dalle Query
        List<AplParamApplic> tmpAplParamApplic = null;

        final String methodName = "ControlliSemantici.caricaDefaultDaDBParametriApplic: ";
        try {
            // carico i parametri applicativi
            String queryStr = "select tpa " + "from AplParamApplic tpa "
                    + "where tpa.tiParamApplic = :tiParamApplicIn";

            javax.persistence.Query query = entityManager.createQuery(queryStr,
                    AplParamApplic.class);
            query.setParameter("tiParamApplicIn", tipoPar);
            tmpAplParamApplic = query.getResultList();

            if (!tmpAplParamApplic.isEmpty()) {
                for (AplParamApplic tud : tmpAplParamApplic) {
                    // Prendo l'unico valore presente, quello di tipo APPLIC
                    tmpDefaults.put(tud.getNmParamApplic(), configurationHelper
                            .getValoreParamApplicByApplic(tud.getNmParamApplic()));
                }
                rispostaControlli.setrObject(tmpDefaults);
                rispostaControlli.setrBoolean(true);
            } else {
                rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
                rispostaControlli
                        .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666, methodName
                                + "Applicativo chiamante non correttamente configurato nella tabella AplParamApplic"));
                log.error(
                        "{} Applicativo chiamante non correttamente configurato nella tabella AplParamApplic",
                        methodName);
            }
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    methodName + e.getMessage()));
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
        final String methodName = "ControlliSemantici.checkIdStrut: ";
        try {
            // controllo ambiente
            String queryStr = "select amb " + "from OrgAmbiente amb "
                    + "where amb.nmAmbiente =  :nmAmbienteIn";
            javax.persistence.Query query = entityManager.createQuery(queryStr, OrgAmbiente.class);
            query.setParameter("nmAmbienteIn", amb);
            orgAmbienteS = query.getResultList();

            // assente
            if (orgAmbienteS.isEmpty()) {
                switch (tipows) {
                case VERSAMENTO_RECUPERO:
                    // EVO#13993
                case VERSAMENTO_RECUPERO_FASC:
                    // end EVO#13993
                    rispostaControlli.setCodErr(MessaggiWSBundle.UD_001_001);
                    rispostaControlli
                            .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.UD_001_001, amb));
                    break;
                case ANNULLAMENTO:
                    rispostaControlli.setCodErr(MessaggiWSBundle.RICH_ANN_VERS_004);
                    rispostaControlli.setDsErr(
                            MessaggiWSBundle.getString(MessaggiWSBundle.RICH_ANN_VERS_004));
                    break;
                }
                return rispostaControlli;
            }
            // too many rows
            if (orgAmbienteS.size() > 1) {
                rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
                rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                        methodName + "Ambiente duplicato"));
                log.error("{} Ambiente duplicato", methodName);
                return rispostaControlli;
            }
            for (OrgAmbiente a : orgAmbienteS) {
                idAmb = a.getIdAmbiente();
            }

            // controllo ente
            queryStr = "select ente " + "from OrgEnte ente "
                    + "where ente.orgAmbiente.idAmbiente = :idAmbienteIn "
                    + " and ente.nmEnte = :nmEnteIn ";
            query = entityManager.createQuery(queryStr, OrgEnte.class);
            query.setParameter("idAmbienteIn", idAmb);
            query.setParameter("nmEnteIn", ente);
            orgEnteS = query.getResultList();
            // assente
            if (orgEnteS.isEmpty()) {
                switch (tipows) {
                case VERSAMENTO_RECUPERO:
                    // EVO#13993
                case VERSAMENTO_RECUPERO_FASC:
                    // end EVO#13993
                    rispostaControlli.setCodErr(MessaggiWSBundle.UD_001_002);
                    rispostaControlli.setDsErr(
                            MessaggiWSBundle.getString(MessaggiWSBundle.UD_001_002, ente));
                    break;
                case ANNULLAMENTO:
                    rispostaControlli.setCodErr(MessaggiWSBundle.RICH_ANN_VERS_005);
                    rispostaControlli.setDsErr(
                            MessaggiWSBundle.getString(MessaggiWSBundle.RICH_ANN_VERS_005));
                    break;
                }
                return rispostaControlli;
            }
            // too many rows
            if (orgEnteS.size() > 1) {
                rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
                rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                        methodName + "Ente duplicato"));
                log.error("{} Ente duplicato", methodName);
                return rispostaControlli;
            }
            for (OrgEnte e : orgEnteS) {
                idEnte = e.getIdEnte();
            }

            // controllo struttura
            queryStr = "select strut " + "from OrgStrut strut "
                    + "where strut.orgEnte.idEnte = :idEnteIn "
                    + " and strut.nmStrut = :nmStrutIn ";
            query = entityManager.createQuery(queryStr, OrgStrut.class);
            query.setParameter("idEnteIn", idEnte);
            query.setParameter("nmStrutIn", strut);
            orgStrutS = query.getResultList();
            // assente
            if (orgStrutS.isEmpty()) {
                switch (tipows) {
                case VERSAMENTO_RECUPERO:
                    // EVO#13993
                case VERSAMENTO_RECUPERO_FASC:
                    // end EVO#13993
                    rispostaControlli.setCodErr(MessaggiWSBundle.UD_001_003);
                    rispostaControlli.setDsErr(
                            MessaggiWSBundle.getString(MessaggiWSBundle.UD_001_003, strut));
                    break;
                case ANNULLAMENTO:
                    rispostaControlli.setCodErr(MessaggiWSBundle.RICH_ANN_VERS_006);
                    rispostaControlli.setDsErr(
                            MessaggiWSBundle.getString(MessaggiWSBundle.RICH_ANN_VERS_006));
                    break;
                }
                return rispostaControlli;
            }
            // too many rows
            if (orgStrutS.size() > 1) {
                rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
                rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                        methodName + "Struttura duplicata"));
                log.error("{} Struttura duplicata", methodName);
                return rispostaControlli;
            }
            if ((tipows == Costanti.TipiWSPerControlli.VERSAMENTO_RECUPERO
                    // EVO#13993
                    || tipows == Costanti.TipiWSPerControlli.VERSAMENTO_RECUPERO_FASC)
                    // end EVO#13993
                    && orgStrutS.get(0).getFlTemplate().equals("1")) {
                rispostaControlli.setCodErr(MessaggiWSBundle.UD_001_015);
                rispostaControlli
                        .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.UD_001_015, strut));
                return rispostaControlli;
            }
            //
            rispostaControlli.setrLong(orgStrutS.get(0).getIdStrut());
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    methodName + e.getMessage()));
            log.error(ERRORE_TABELLA_DECODIFICA, e);
        }

        return rispostaControlli;
    }

    public RispostaControlli checkChiave(CSChiave key, long idStruttura,
            TipiGestioneUDAnnullate tguda) {
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
            String queryStr = "select ud " + "from AroUnitaDoc ud "
                    + "where ud.orgStrut.idStrut = :idStrutIn "
                    + " and ud.cdKeyUnitaDoc = :cdKeyUnitaDocIn "
                    + " and ud.aaKeyUnitaDoc = :aaKeyUnitaDocIn "
                    + " and ud.cdRegistroKeyUnitaDoc = :cdRegistroKeyUnitaDocIn "
                    + " order by ud.dtCreazione desc";

            javax.persistence.Query query = entityManager.createQuery(queryStr, AroUnitaDoc.class);
            query.setParameter("idStrutIn", idStruttura);
            query.setParameter("cdKeyUnitaDocIn", numero);
            query.setParameter("aaKeyUnitaDocIn", HibernateUtils.bigDecimalFrom(anno));
            query.setParameter("cdRegistroKeyUnitaDocIn", tipoReg);
            unitaDocS = query.getResultList();

            // chiave già presente (uno o più righe trovate, mi interessa solo l'ultima - più
            // recente)
            if (!unitaDocS.isEmpty()) {
                StatoConservazioneUnitaDoc scud = StatoConservazioneUnitaDoc
                        .valueOf(unitaDocS.get(0).getTiStatoConservazione());
                if (scud == StatoConservazioneUnitaDoc.ANNULLATA
                        && tguda == TipiGestioneUDAnnullate.CONSIDERA_ASSENTE) {
                    // commuto l'errore in UD annullata e rendo true come risposta: in pratica come
                    // se non
                    // avesse trovato l'UD ma con un errore diverso: è lo stesso comportamento della
                    // vecchia versione
                    // del metodo
                    rispostaControlli.setCodErr(MessaggiWSBundle.UD_012_002);
                    rispostaControlli
                            .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.UD_012_002,
                                    MessaggiWSFormat.formattaUrnPartUnitaDoc(key)));
                    rispostaControlli.setrBoolean(true);
                } else {
                    // gestione normale: ho trovato l'UD e non è annullata.
                    // Oppure è annullata e voglio caricarla lo stesso (il solo caso è nel ws
                    // recupero stato UD)
                    // intanto rendo l'errore di chiave già presente
                    rispostaControlli.setCodErr(MessaggiWSBundle.UD_002_001);
                    rispostaControlli
                            .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.UD_002_001,
                                    MessaggiWSFormat.formattaUrnPartUnitaDoc(key)));

                    rispostaControlli.setrLong(unitaDocS.get(0).getIdUnitaDoc());
                    // se la chiave è già presente, oltre all'id dell'UD trovata,
                    // recupero il tipo di salvataggio. Mi serve sia nell'aggiunta documenti
                    // che nel recupero UD
                    rispostaControlli
                            .setrString(unitaDocS.get(0).getDecTipoUnitaDoc().getTiSaveFile());
                    //
                    rispostaControlli.setrStringExtended(
                            unitaDocS.get(0).getDecTipoUnitaDoc().getNmTipoUnitaDoc());
                    // lo stato conservazione viene usato per l'aggiunta doc:
                    // non posso aggiungere doc se l'ud è nello stato sbagliato
                    rispostaControlli.setrObject(scud);
                    rispostaControlli.setrLongExtended(unitaDocS.get(0).getIdDecRegistroUnitaDoc());
                    // **************
                    // EXTENDED VALUES
                    // **************
                    // recupero chiave normalizzata (se esiste)
                    rispostaControlli.getrMap().put(
                            RispostaControlli.ValuesOnrMap.CD_KEY_NORMALIZED.name(),
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
            log.error(ERRORE_TABELLA_DECODIFICA, e);
        }

        return rispostaControlli;
    }

    public RispostaControlli checkUrnComponente(String urn, boolean tipoUrnOriginale,
            RecuperoExt recupero) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrLong(-1);
        rispostaControlli.setrBoolean(false);

        // lista entity JPA ritornate dalle Query
        List<Object[]> risultato;
        List<AroCompUrnCalc> nativeResult;
        // lancio query di controllo
        try {
            String sql = "SELECT /*+ index(SACER.ARO_COMP_URN_CALC IDX_DS_URN) */ * "
                    + "FROM SACER.ARO_COMP_URN_CALC " + "WHERE TI_URN = :tiUrn "
                    + "AND DS_URN = :dsUrn " + "FETCH FIRST 2 ROWS ONLY";

            nativeResult = entityManager.createNativeQuery(sql, AroCompUrnCalc.class)
                    .setParameter("tiUrn",
                            tipoUrnOriginale ? TiUrn.ORIGINALE.name() : TiUrn.NORMALIZZATO.name())
                    .setParameter("dsUrn", urn).getResultList();

            if (nativeResult.isEmpty()) {
                // Solleva errore record non trovato!!!
                rispostaControlli.setCodErr(MessaggiWSBundle.COMP_005_003);
                rispostaControlli
                        .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.COMP_005_003, urn));
                rispostaControlli.setrBoolean(true);
            } else if (nativeResult.size() > 1) {
                // Solleva errore - troppe occorrenze!!!
                rispostaControlli.setCodErr(MessaggiWSBundle.COMP_005_004);
                rispostaControlli
                        .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.COMP_005_004, urn));
                rispostaControlli.setrBoolean(true);
            } else {
                AroCompUrnCalc calc = nativeResult.get(0);
                String queryStr = "select   comp, struttura, ente, ambiente, ud "
                        + "from     AroCompDoc comp " + "join     comp.aroStrutDoc str "
                        + "join     str.aroDoc doc " + "join     doc.aroUnitaDoc ud "
                        + "join     ud.orgStrut struttura " + "join     struttura.orgEnte ente "
                        + "join     ente.orgAmbiente ambiente "
                        + "WHERE    comp.idCompDoc = :idCompDoc ";
                javax.persistence.Query query = entityManager.createQuery(queryStr);
                query.setParameter("idCompDoc", calc.getAroCompDoc().getIdCompDoc());
                risultato = query.getResultList();
                // il record deve sicuramente esserci!!!
                rispostaControlli.setrLong(((AroCompDoc) risultato.get(0)[0]).getIdCompDoc());
                ParametriRecupero p = recupero.getParametriRecupero();
                VersatoreType vers = recupero.getStrutturaRecupero().getVersatore();
                ChiaveType chia = recupero.getStrutturaRecupero().getChiave();
                vers.setAmbiente(((OrgAmbiente) risultato.get(0)[3]).getNmAmbiente());
                vers.setEnte(((OrgEnte) risultato.get(0)[2]).getNmEnte());
                vers.setStruttura(((OrgStrut) risultato.get(0)[1]).getNmStrut());

                AroUnitaDoc ud = (AroUnitaDoc) risultato.get(0)[4];
                chia.setAnno(ud.getAaKeyUnitaDoc().toBigInteger());
                chia.setNumero(ud.getCdKeyUnitaDoc());
                chia.setTipoRegistro(ud.getDecRegistroUnitaDoc().getCdRegistroUnitaDoc());
                p.setIdComponente(((AroCompDoc) risultato.get(0)[0]).getIdCompDoc());
                p.setTipoEntitaSacer(CostantiDB.TipiEntitaRecupero.COMP);

                return rispostaControlli;
            }

        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "ControlliSemantici.checkUrnComponente: " + e.getMessage()));
            log.error(ERRORE_TABELLA_DECODIFICA, e);
        }

        return rispostaControlli;
    }

    // EVO#13993
    public RispostaControlli checkChiaveFasc(CSChiaveFasc key, long idStruttura,
            TipiGestioneFASCAnnullati tgfasca) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrLong(-1);
        rispostaControlli.setrBoolean(false);

        // prendo i paramentri dell'xml
        String numero = key.getNumero();
        Integer anno = key.getAnno();

        // lista entity JPA ritornate dalle Query
        List<FasFascicolo> ffS = null;

        // lancio query di controllo
        try {
            // ricavo i fascicoli presenti in base ai parametri impostati
            String queryStr = "select ff " + "from FasFascicolo ff "
                    + "where ff.orgStrut.idStrut = :idStrutIn "
                    + " and ff.cdKeyFascicolo = :cdKeyFascicoloIn "
                    + " and ff.aaFascicolo = :aaFascicoloIn " + " order by ff.dtApeFascicolo desc";

            javax.persistence.Query query = entityManager.createQuery(queryStr, FasFascicolo.class);
            query.setParameter("idStrutIn", idStruttura);
            query.setParameter("cdKeyFascicoloIn", numero);
            query.setParameter("aaFascicoloIn", BigDecimal.valueOf(anno));
            ffS = query.getResultList();

            // chiave fasc già presente (uno o più righe trovate, mi interessa solo l'ultima - più
            // recente)
            if (!ffS.isEmpty()) {
                TiStatoConservazione scff = ffS.get(0).getTiStatoConservazione();
                if (scff.equals(TiStatoConservazione.ANNULLATO)
                        && tgfasca == TipiGestioneFASCAnnullati.CONSIDERA_ASSENTE) {
                    // commuto l'errore in FASCICOLO annullato e rendo true come risposta: in
                    // pratica come se non
                    // avesse trovato il fascicolo ma con un errore diverso: è lo stesso
                    // comportamento della vecchia
                    // versione
                    // del metodo
                    rispostaControlli.setCodErr(MessaggiWSBundle.UD_012_002);
                    rispostaControlli
                            .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.UD_012_002,
                                    MessaggiWSFormat.formattaUrnPartFasc(key)));
                    rispostaControlli.setrBoolean(true);
                } else {
                    // gestione normale: ho trovato il FASCICOLO e non è annullato.
                    // Oppure è annullato e voglio caricarlo lo stesso (il solo caso è nel ws
                    // recupero stato FASC)
                    // intanto rendo l'errore di chiave già presente
                    rispostaControlli.setCodErr(MessaggiWSBundle.UD_002_001);
                    rispostaControlli
                            .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.UD_002_001,
                                    MessaggiWSFormat.formattaUrnPartFasc(key)));

                    rispostaControlli.setrLong(ffS.get(0).getIdFascicolo());
                    //
                    rispostaControlli.setrStringExtended(
                            ffS.get(0).getDecTipoFascicolo().getNmTipoFascicolo());
                    // salvo lo stato conservazione
                    rispostaControlli.setrObject(scff);
                    // **************
                    // EXTENDED VALUES
                    // **************
                    // recupero chiave normalizzata del fascicolo (se esiste)
                    rispostaControlli.getrMap().put(
                            RispostaControlli.ValuesOnrMap.CD_KEY_NORMALIZED.name(),
                            ffS.get(0).getCdKeyFascicolo());
                }
                return rispostaControlli;
            }

            // Chiave del fascicolo non trovata
            rispostaControlli.setCodErr(MessaggiWSBundle.RECFAS_001_002);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.RECFAS_001_002,
                    MessaggiWSFormat.formattaUrnPartFasc(key)));
            rispostaControlli.setrBoolean(true);

        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "ControlliSemantici.checkChiaveFasc: " + e.getMessage()));
            log.error(ERRORE_TABELLA_DECODIFICA, e);
        }

        return rispostaControlli;
    }
    // end EVO#13993

    // chiamata anche dal job, rende la configurazione per validare i numeri/chiave
    // in base all'anno del registro
    public ConfigRegAnno caricaPartiAARegistro(long idAaRegistroUnitaDoc) {
        ConfigRegAnno tmpConfAnno = new ConfigRegAnno(idAaRegistroUnitaDoc);

        String queryStr = "select t from DecParteNumeroRegistro t "
                + "where t.decAaRegistroUnitaDoc.idAaRegistroUnitaDoc " + "= :idAaRegistroUnitaDoc "
                + "order by t.niParteNumeroRegistro";
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        query.setParameter("idAaRegistroUnitaDoc", idAaRegistroUnitaDoc);
        List<DecParteNumeroRegistro> tmpLstP = query.getResultList();

        for (DecParteNumeroRegistro tmpParte : tmpLstP) {
            ConfigRegAnno.ParteRegAnno tmpPRanno = tmpConfAnno.aggiungiParte();
            tmpPRanno.setNumParte(tmpParte.getNiParteNumeroRegistro().intValue());
            tmpPRanno.setNomeParte(tmpParte.getNmParteNumeroRegistro());
            tmpPRanno.setMaxLen(
                    tmpParte.getNiMaxCharParte() != null ? tmpParte.getNiMaxCharParte().longValue()
                            : -1);
            tmpPRanno.setMinLen(
                    tmpParte.getNiMinCharParte() != null ? tmpParte.getNiMinCharParte().longValue()
                            : 0);

            //
            tmpPRanno.setSeparatore(
                    (tmpParte.getTiCharSep() != null && tmpParte.getTiCharSep().isEmpty()) ? " "
                            : tmpParte.getTiCharSep());
            /*
             * se il separatore è una stringa non-nulla ma vuota, il valore viene letto come uno
             * spazio. nel DB è memorizzato come CHAR(1), pad-dato -al salvataggio- da Oracle, e che
             * al momento della lettura viene trim-ato da eclipselink. Quindi con questo sistema
             * ricostruisco il valore originale se questo era uno spazio
             */
            //
            // Nota: nel DB la variabile tiParte ha tre valori mutualmente esclusivi.
            // in questo caso, vengono gestiti come 3 flag separati perché i test relativi
            // vengono effettuati in parti diverse del codice
            tmpPRanno.setMatchAnnoChiave(tmpParte.getTiParte() != null
                    && tmpParte.getTiParte().equals(ConfigRegAnno.TiParte.ANNO.name()));
            tmpPRanno.setMatchRegistro(tmpParte.getTiParte() != null
                    && tmpParte.getTiParte().equals(ConfigRegAnno.TiParte.REGISTRO.name()));
            tmpPRanno.setUsaComeProgressivo(tmpParte.getTiParte() != null
                    && tmpParte.getTiParte().equals(ConfigRegAnno.TiParte.PROGR.name()));
            tmpPRanno.setTipoCalcolo(KeyOrdUtility.TipiCalcolo.valueOf(tmpParte.getTiCharParte()));
            tmpPRanno.setTiPadding(tmpParte.getTiPadSxParte() != null
                    ? ConfigRegAnno.TipiPadding.valueOf(tmpParte.getTiPadSxParte())
                    : ConfigRegAnno.TipiPadding.NESSUNO);
            ConfigRegAnno.impostaValoriAccettabili(tmpPRanno, tmpParte.getDlValoriParte());
        }
        tmpConfAnno.ElaboraParti();
        return tmpConfAnno;
    }

    /**
     * Verifica se data id struttura risulta cessata
     *
     * @param idStrut id struttura
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
                rispostaControlli.setDsErr(
                        "Eccezione ControlliRecupero.checkStruttCessata: non presente per id "
                                + idStrut);
            } else {
                //
                boolean result = StringUtils.isNotBlank(orgStrut.getFlCessato())
                        && orgStrut.getFlCessato().equalsIgnoreCase(CostantiDB.Flag.TRUE);
                if (result) {
                    rispostaControlli.setrBoolean(false);
                    rispostaControlli.setCodErr(MessaggiWSBundle.UD_001_020);
                    rispostaControlli.setDsErr(MessaggiWSBundle
                            .getString(MessaggiWSBundle.UD_001_020, orgStrut.getNmStrut()));
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
