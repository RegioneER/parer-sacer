package it.eng.parer.ws.recupero.ejb;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.elencoVersamento.helper.ElencoVersamentoHelper;
import it.eng.parer.entity.AplParamApplic;
import it.eng.parer.entity.AplValParamApplicMulti;
import it.eng.parer.entity.AroCompDoc;
import it.eng.parer.entity.AroCompUrnCalc;
import it.eng.parer.entity.AroDoc;
import it.eng.parer.entity.AroFileVerIndiceAipUd;
import it.eng.parer.entity.AroUdAppartVerSerie;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.AroUpdUnitaDoc;
import it.eng.parer.entity.AroVerIndiceAipUd;
import it.eng.parer.entity.AroXmlUpdUnitaDoc;
import it.eng.parer.entity.DecReportServizioVerificaCompDoc;
import it.eng.parer.entity.DecTipoDoc;
import it.eng.parer.entity.ElvFileElencoVer;
import it.eng.parer.entity.FasUnitaDocFascicolo;
import it.eng.parer.entity.VolVolumeConserv;
import it.eng.parer.entity.VrsXmlDatiSessioneVers;
import it.eng.parer.entity.constraint.AroCompUrnCalc.TiUrn;
import it.eng.parer.entity.constraint.FiUrnReport.TiUrnReport;
import it.eng.parer.grantedEntity.UsrUser;
import it.eng.parer.util.helper.UniformResourceNameUtilHelper;
import it.eng.parer.viewEntity.AroVDtVersMaxByUnitaDoc;
import it.eng.parer.viewEntity.AroVLisaipudSistemaMigraz;
import it.eng.parer.web.helper.AmministrazioneHelper;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.UnitaDocumentarieHelper;
import it.eng.parer.web.helper.UserHelper;
import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.recupero.dto.ComponenteRec;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import java.util.Collection;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

/**
 *
 * @author Fioravanti_F
 */
@Stateless(mappedName = "ControlliRecupero")
@LocalBean
@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
public class ControlliRecupero {

    private static final Logger log = LoggerFactory.getLogger(ControlliRecupero.class);
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;
    @EJB
    private UnitaDocumentarieHelper unitaDocumentarieHelper;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private ElencoVersamentoHelper evHelper;
    @EJB
    private UniformResourceNameUtilHelper urnHelper;
    @EJB
    private UserHelper userHelper;
    @EJB
    private AmministrazioneHelper amministrazioneHelper;

    @Resource
    EJBContext context;

    public RispostaControlli leggiUnitaDoc(long idUnitaDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        AroUnitaDoc tmpUnitaDoc = null;

        try {
            tmpUnitaDoc = entityManager.find(AroUnitaDoc.class, idUnitaDoc);
            rispostaControlli.setrObject(tmpUnitaDoc);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecupero.leggiUnitaDoc " + e.getMessage()));
            log.error("Eccezione nella lettura  della tabella delle unità doc ", e);
        }
        return rispostaControlli;
    }

    // EVO#20972
    public RispostaControlli leggiVolumeConserv(long idVolumeConserv) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        VolVolumeConserv volume = null;

        try {
            String queryStr = "select t  from VolVolumeConserv t " + "where t.idVolumeConserv = :idVolumeConserv";

            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idVolumeConserv", idVolumeConserv);
            volume = (VolVolumeConserv) query.getSingleResult();
            rispostaControlli.setrObject(volume);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecupero.leggiVolumeConserv " + e.getMessage()));
            log.error("Eccezione nella lettura  della tabella dei volumi di conservazione ", e);
        }
        return rispostaControlli;
    }
    // end EVO#20972

    // OK prove conservazione
    public RispostaControlli leggiVolumiUnitaDoc(long idUnitaDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<VolVolumeConserv> lstVolumi = null;

        try {
            String queryStr = "select t  from VolAppartUnitaDocVolume audv " + "join audv.volVolumeConserv t "
                    + "where audv.aroUnitaDoc.idUnitaDoc = :idUnitaDoc";

            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idUnitaDoc", idUnitaDoc);
            lstVolumi = query.getResultList();
            rispostaControlli.setrObject(lstVolumi);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecupero.leggiVolumiUnitaDoc " + e.getMessage()));
            log.error("Eccezione nella lettura  della tabella dei volumi unità doc", e);
        }
        return rispostaControlli;
    }

    /**
     *
     * @param idUnitaDoc
     *            id unita doc
     * 
     * @return istanza di RispostaControlli con i campi valorizzati come segue: se va in errore: rBoolean è false se ok:
     *         rBoolean è true rObject contiene una lista di ComponenteRec corrispondenti ai componenti ed rString
     *         contiene il nome da attribuire alla directory dello zip che conterrà l'unità doc
     */
    // OK
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RispostaControlli leggiCompFileInUD(long idUnitaDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<AroCompDoc> lstComponenti = null;
        List<ComponenteRec> lstComp;

        try {
            lstComp = new ArrayList();

            String queryStr = "select acd from AroCompDoc acd "
                    + "where acd.aroStrutDoc.aroDoc.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                    + "and acd.tiSupportoComp = 'FILE'";

            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idUnitaDoc", idUnitaDoc);

            lstComponenti = query.getResultList();
            for (AroCompDoc tmpCmp : lstComponenti) {
                // urn
                String urnCompletoIniz = null;
                String urnCompleto = unitaDocumentarieHelper.findAroCompUrnCalcByType(tmpCmp, TiUrn.NORMALIZZATO)
                        .getDsUrn();
                // urn iniz
                AroCompUrnCalc compUrnCalc = unitaDocumentarieHelper.findAroCompUrnCalcByType(tmpCmp, TiUrn.INIZIALE);
                if (compUrnCalc != null) {
                    urnCompletoIniz = compUrnCalc.getDsUrn();
                }
                ComponenteRec tmpCRec = new ComponenteRec(urnCompleto, urnCompletoIniz);
                // MEV#22921 Parametrizzazione servizi di recupero
                tmpCRec.setUrnOriginaleVersata(tmpCmp.getDlUrnCompVers());
                tmpCRec.setNomeFileOriginaleVersato(tmpCmp.getDsNomeCompVers());
                tmpCRec.setIdCompDoc(tmpCmp.getIdCompDoc());
                // Gestisco il formato
                String dsFormatoRapprCalc = tmpCmp.getDsFormatoRapprCalc();
                if (dsFormatoRapprCalc != null && !dsFormatoRapprCalc.contains("???")) {
                    tmpCRec.setEstensioneFile(dsFormatoRapprCalc);
                } else {
                    if (tmpCmp.getDecFormatoFileDoc() != null) {
                        tmpCRec.setEstensioneFile(tmpCmp.getDecFormatoFileDoc().getNmFormatoFileDoc());
                    } else {
                        tmpCRec.setEstensioneFile("unknown");
                    }
                }
                lstComp.add(tmpCRec);
            }
            if (lstComp.size() > 0) {
                rispostaControlli.setrString(
                        this.generaNomeFileUD(lstComponenti.get(0).getAroStrutDoc().getAroDoc().getAroUnitaDoc()));
            } else {
                rispostaControlli.setrString(this.generaNomeFileUD(idUnitaDoc));
            }
            rispostaControlli.setrObject(lstComp);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecupero.leggiCompFileInUD " + e.getMessage()));
            log.error("Eccezione nella lettura della tabella dei componenti in UD ", e);
        }
        return rispostaControlli;
    }

    // EVO#20972
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RispostaControlli leggiCompFileInUDAIPV2(long idUnitaDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<AroCompDoc> lstComponenti = null;
        List<ComponenteRec> lstComp;

        try {
            lstComp = new ArrayList();

            String queryStr = "select acd from AroCompDoc acd "
                    + "where acd.aroStrutDoc.aroDoc.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                    + "and acd.tiSupportoComp = 'FILE'";

            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idUnitaDoc", idUnitaDoc);

            lstComponenti = query.getResultList();
            for (AroCompDoc tmpCmp : lstComponenti) {
                // urn
                String urnCompletoIniz = null;
                String urnCompleto = unitaDocumentarieHelper.findAroCompUrnCalcByType(tmpCmp, TiUrn.NORMALIZZATO)
                        .getDsUrn();
                // urn iniz
                AroCompUrnCalc compUrnCalc = unitaDocumentarieHelper.findAroCompUrnCalcByType(tmpCmp, TiUrn.INIZIALE);
                if (compUrnCalc != null) {
                    urnCompletoIniz = compUrnCalc.getDsUrn();
                }
                ComponenteRec tmpCRec = new ComponenteRec(urnCompleto, urnCompletoIniz);
                // MEV#22921 Parametrizzazione servizi di recupero
                tmpCRec.setUrnOriginaleVersata(tmpCmp.getDlUrnCompVers());
                tmpCRec.setNomeFileOriginaleVersato(tmpCmp.getDsNomeCompVers());
                tmpCRec.setIdCompDoc(tmpCmp.getIdCompDoc());
                // Gestisco il formato
                String dsFormatoRapprCalc = tmpCmp.getDsFormatoRapprCalc();
                if (dsFormatoRapprCalc != null && !dsFormatoRapprCalc.contains("???")) {
                    tmpCRec.setEstensioneFile(dsFormatoRapprCalc);
                } else {
                    if (tmpCmp.getDecFormatoFileDoc() != null) {
                        tmpCRec.setEstensioneFile(tmpCmp.getDecFormatoFileDoc().getNmFormatoFileDoc());
                    } else {
                        tmpCRec.setEstensioneFile("unknown");
                    }
                }
                lstComp.add(tmpCRec);
            }
            if (lstComp.size() > 0) {
                rispostaControlli.setrString(
                        this.generaNomeFileAIPV2(lstComponenti.get(0).getAroStrutDoc().getAroDoc().getAroUnitaDoc()));
            } else {
                rispostaControlli.setrString(this.generaNomeFileAIPV2(idUnitaDoc));
            }
            rispostaControlli.setrObject(lstComp);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecupero.leggiCompFileInUDAIPV2 " + e.getMessage()));
            log.error("Eccezione nella lettura della tabella dei componenti in UD ", e);
        }
        return rispostaControlli;
    }
    // end EVO#20972

    public RispostaControlli leggiCompFileInUDVersamentoUd(long idUnitaDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<AroCompDoc> lstComponenti = null;
        List<ComponenteRec> lstComp;

        try {
            lstComp = new ArrayList();

            String queryStr = "SELECT compDoc FROM AroCompDoc compDoc " + "JOIN compDoc.aroStrutDoc strutDoc "
                    + "JOIN strutDoc.aroDoc doc " + "JOIN doc.aroUnitaDoc unitaDoc "
                    + "WHERE unitaDoc.idUnitaDoc = :idUnitaDoc " + "AND doc.tiCreazione = 'VERSAMENTO_UNITA_DOC' "
                    + "AND compDoc.tiSupportoComp = 'FILE'";

            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idUnitaDoc", idUnitaDoc);

            lstComponenti = query.getResultList();
            for (AroCompDoc tmpCmp : lstComponenti) {
                // urn
                String urnCompletoIniz = null;
                String urnCompleto = unitaDocumentarieHelper.findAroCompUrnCalcByType(tmpCmp, TiUrn.NORMALIZZATO)
                        .getDsUrn();
                // urn iniz
                AroCompUrnCalc compUrnCalc = unitaDocumentarieHelper.findAroCompUrnCalcByType(tmpCmp, TiUrn.INIZIALE);
                if (compUrnCalc != null) {
                    urnCompletoIniz = compUrnCalc.getDsUrn();
                }
                ComponenteRec tmpCRec = new ComponenteRec(urnCompleto, urnCompletoIniz);
                tmpCRec.setIdCompDoc(tmpCmp.getIdCompDoc());

                // Gestisco il formato
                String dsFormatoRapprCalc = tmpCmp.getDsFormatoRapprCalc();
                if (dsFormatoRapprCalc != null && !dsFormatoRapprCalc.contains("???")) {
                    tmpCRec.setEstensioneFile(dsFormatoRapprCalc);
                } else {
                    if (tmpCmp.getDecFormatoFileDoc() != null) {
                        tmpCRec.setEstensioneFile(tmpCmp.getDecFormatoFileDoc().getNmFormatoFileDoc());
                    } else {
                        tmpCRec.setEstensioneFile("unknown");
                    }
                }
                lstComp.add(tmpCRec);
            }
            if (lstComp.size() > 0) {
                rispostaControlli.setrString(
                        this.generaNomeFileUD(lstComponenti.get(0).getAroStrutDoc().getAroDoc().getAroUnitaDoc()));
            } else {
                rispostaControlli.setrString(this.generaNomeFileUD(idUnitaDoc));
            }
            rispostaControlli.setrObject(lstComp);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecupero.leggiCompFileInUDVersamentoUd " + e.getMessage()));
            log.error("Eccezione nella lettura della tabella dei componenti in UD ", e);
        }
        return rispostaControlli;
    }

    public RispostaControlli leggiCompFileInUDByTipoDoc(long idUnitaDoc, long idTipoDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<AroCompDoc> lstComponenti = null;
        List<ComponenteRec> lstComp;

        try {
            lstComp = new ArrayList();

            String queryStr = "select acd from AroCompDoc acd "
                    + "where acd.aroStrutDoc.aroDoc.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                    + "and acd.aroStrutDoc.aroDoc.decTipoDoc.idTipoDoc = :idTipoDoc "
                    + "and acd.tiSupportoComp = 'FILE'";

            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idUnitaDoc", idUnitaDoc);
            query.setParameter("idTipoDoc", idTipoDoc);

            lstComponenti = query.getResultList();
            for (AroCompDoc tmpCmp : lstComponenti) {
                // urn
                String urnCompletoIniz = null;
                String urnCompleto = unitaDocumentarieHelper.findAroCompUrnCalcByType(tmpCmp, TiUrn.NORMALIZZATO)
                        .getDsUrn();
                // urn iniz
                AroCompUrnCalc compUrnCalc = unitaDocumentarieHelper.findAroCompUrnCalcByType(tmpCmp, TiUrn.INIZIALE);
                if (compUrnCalc != null) {
                    urnCompletoIniz = compUrnCalc.getDsUrn();
                }
                // dto
                ComponenteRec tmpCRec = new ComponenteRec(urnCompleto, urnCompletoIniz);
                // MEV#22921 Parametrizzazione servizi di recupero
                tmpCRec.setUrnOriginaleVersata(tmpCmp.getDlUrnCompVers());
                tmpCRec.setNomeFileOriginaleVersato(tmpCmp.getDsNomeCompVers());
                tmpCRec.setIdCompDoc(tmpCmp.getIdCompDoc());
                // Gestisco il formato
                String dsFormatoRapprCalc = tmpCmp.getDsFormatoRapprCalc();
                if (dsFormatoRapprCalc != null && !dsFormatoRapprCalc.contains("???")) {
                    tmpCRec.setEstensioneFile(dsFormatoRapprCalc);
                } else {
                    if (tmpCmp.getDecFormatoFileDoc() != null) {
                        tmpCRec.setEstensioneFile(tmpCmp.getDecFormatoFileDoc().getNmFormatoFileDoc());
                    } else {
                        tmpCRec.setEstensioneFile("unknown");
                    }
                }
                lstComp.add(tmpCRec);
            }
            if (lstComp.size() > 0) {
                rispostaControlli.setrString(
                        this.generaNomeFileUD(lstComponenti.get(0).getAroStrutDoc().getAroDoc().getAroUnitaDoc())
                                + this.generaSuffissoTipoDocUD(
                                        lstComponenti.get(0).getAroStrutDoc().getAroDoc().getDecTipoDoc()));
            } else {
                rispostaControlli
                        .setrString(this.generaNomeFileUD(idUnitaDoc) + this.generaSuffissoTipoDocUD(idTipoDoc));
            }
            rispostaControlli.setrObject(lstComp);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecupero.leggiCompFileInUDByTipoDoc " + e.getMessage()));
            log.error("Eccezione nella lettura della tabella dei componenti in UD ", e);
        }
        return rispostaControlli;
    }

    private String generaSuffissoTipoDocUD(long idTipoDoc) {
        DecTipoDoc dtd = entityManager.find(DecTipoDoc.class, idTipoDoc);
        return this.generaSuffissoTipoDocUD(dtd);
    }

    private String generaSuffissoTipoDocUD(DecTipoDoc dtd) {
        StringBuilder tmpString = new StringBuilder();
        tmpString.append("_");
        tmpString.append(dtd.getNmTipoDoc());
        return MessaggiWSFormat.bonificaUrnPerNomeFile(tmpString.toString());
    }

    private String generaNomeFileUD(long idUd) {
        AroUnitaDoc aud = entityManager.find(AroUnitaDoc.class, idUd);
        return this.generaNomeFileUD(aud);
    }

    private String generaNomeFileUD(AroUnitaDoc aud) {
        StringBuilder tmpString = new StringBuilder();
        tmpString.append(aud.getCdRegistroKeyUnitaDoc());
        tmpString.append("-");
        tmpString.append(aud.getAaKeyUnitaDoc());
        tmpString.append("-");
        tmpString.append(aud.getCdKeyUnitaDoc());
        return MessaggiWSFormat.bonificaUrnPerNomeFile(tmpString.toString());
    }

    // EVO#20972
    private String generaNomeFileAIPV2(long idUd) {
        AroUnitaDoc aud = entityManager.find(AroUnitaDoc.class, idUd);
        return this.generaNomeFileAIPV2(aud);
    }

    private String generaNomeFileAIPV2(AroUnitaDoc aud) {
        String sistemaConservazione = configurationHelper.getValoreParamApplic(
                CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE, null, null, null, null,
                CostantiDB.TipoAplVGetValAppart.APPLIC);
        CSVersatore versatore = this.getVersatoreUd(aud, sistemaConservazione);
        CSChiave chiave = this.getChiaveUd(aud);
        String tmpUrn = MessaggiWSFormat.formattaUrnAipUdAip(
                MessaggiWSFormat.formattaUrnPartVersatore(versatore, true, Costanti.UrnFormatter.VERS_FMT_STRING),
                MessaggiWSFormat.formattaUrnPartUnitaDoc(chiave, true, Costanti.UrnFormatter.UD_FMT_STRING));
        return ComponenteRec.estraiNomeFileCompleto(tmpUrn);
    }

    public CSChiave getChiaveUd(AroUnitaDoc ud) {
        CSChiave csc = new CSChiave();
        csc.setTipoRegistro(ud.getCdRegistroKeyUnitaDoc());
        csc.setAnno(ud.getAaKeyUnitaDoc().longValue());
        csc.setNumero(ud.getCdKeyUnitaDoc());

        return csc;
    }

    private CSVersatore getVersatoreUd(AroUnitaDoc ud, String sistemaConservazione) {
        CSVersatore csv = new CSVersatore();
        csv.setStruttura(ud.getOrgStrut().getNmStrut());
        csv.setEnte(ud.getOrgStrut().getOrgEnte().getNmEnte());
        csv.setAmbiente(ud.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
        // sistema (new URN)
        csv.setSistemaConservazione(sistemaConservazione);

        return csv;
    }
    // end EVO#20972

    // OK
    public RispostaControlli leggiCompFileInDoc(long idDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<AroCompDoc> lstComponenti = null;
        List<ComponenteRec> lstComp;

        try {
            lstComp = new ArrayList<>();

            String queryStr = "select acd  from AroCompDoc acd " + "where acd.aroStrutDoc.aroDoc.idDoc = :idDoc "
                    + "and acd.tiSupportoComp = 'FILE'";

            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idDoc", idDoc);

            lstComponenti = query.getResultList();
            for (AroCompDoc tmpCmp : lstComponenti) {
                // urn
                String urnCompletoIniz = null;
                String urnCompleto = unitaDocumentarieHelper.findAroCompUrnCalcByType(tmpCmp, TiUrn.NORMALIZZATO)
                        .getDsUrn();
                // urn iniz
                AroCompUrnCalc compUrnCalc = unitaDocumentarieHelper.findAroCompUrnCalcByType(tmpCmp, TiUrn.INIZIALE);
                if (compUrnCalc != null) {
                    urnCompletoIniz = compUrnCalc.getDsUrn();
                }
                ComponenteRec tmpCRec = new ComponenteRec(urnCompleto, urnCompletoIniz);
                tmpCRec.setIdCompDoc(tmpCmp.getIdCompDoc());
                // Gestisco il formato
                String dsFormatoRapprCalc = tmpCmp.getDsFormatoRapprCalc();
                if (dsFormatoRapprCalc != null && !dsFormatoRapprCalc.contains("???")) {
                    tmpCRec.setEstensioneFile(dsFormatoRapprCalc);
                } else {
                    if (tmpCmp.getDecFormatoFileDoc() != null) {
                        tmpCRec.setEstensioneFile(tmpCmp.getDecFormatoFileDoc().getNmFormatoFileDoc());
                    } else {
                        tmpCRec.setEstensioneFile("unknown");
                    }
                }
                lstComp.add(tmpCRec);
            }
            if (!lstComp.isEmpty()) {
                rispostaControlli.setrString(this.generaNomeFileDOC(lstComponenti.get(0).getAroStrutDoc().getAroDoc()));
            } else {
                rispostaControlli.setrString(this.generaNomeFileDOC(idDoc));
            }
            rispostaControlli.setrObject(lstComp);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecupero.leggiCompFileInDoc " + e.getMessage()));
            log.error("Eccezione nella lettura della tabella dei componenti in DOC ", e);
        }
        return rispostaControlli;
    }

    // OK
    private String generaNomeFileDOC(long idDoc) {
        AroDoc ad = entityManager.find(AroDoc.class, idDoc);
        return this.generaNomeFileDOC(ad);
    }

    // Nota MAC#24837 : ad.getNiOrdDoc() non dovrebbe mai essere null in quanto, ad ogni servizio
    // di recupero precede una logica di gestione del pregresso degli urn che quindi valorizza
    // l'attributo NiOrdDoc
    private String generaNomeFileDOC(AroDoc ad) {
        // MAC#24837
        String urnPartDocumento = (ad.getNiOrdDoc() != null)
                ? MessaggiWSFormat.formattaUrnPartDocumento(Costanti.CategoriaDocumento.Documento,
                        ad.getNiOrdDoc().intValue(), true, Costanti.UrnFormatter.DOC_FMT_STRING_V2,
                        Costanti.UrnFormatter.PAD5DIGITS_FMT)
                : MessaggiWSFormat.formattaUrnPartDocumento(Costanti.CategoriaDocumento.getEnum(ad.getTiDoc()),
                        ad.getPgDoc().intValue());
        // end MAC#24837
        StringBuilder tmpString = new StringBuilder();
        AroUnitaDoc aud = ad.getAroUnitaDoc();
        tmpString.append(aud.getCdRegistroKeyUnitaDoc());
        tmpString.append("-");
        tmpString.append(aud.getAaKeyUnitaDoc());
        tmpString.append("-");
        tmpString.append(aud.getCdKeyUnitaDoc());
        tmpString.append("-");
        tmpString.append(urnPartDocumento);
        return MessaggiWSFormat.bonificaUrnPerNomeFile(tmpString.toString());
    }

    // OK
    public RispostaControlli leggiCompFileInComp(long idComp) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<AroCompDoc> lstComponenti = null;
        List<ComponenteRec> lstComp;

        try {
            lstComp = new ArrayList<>();

            // è un'idiozia, visto che non mi renderà mai più di una riga, ma volevo
            // scriverla
            // in modo uniforme.
            String queryStr = "select acd  from AroCompDoc acd " + "where acd.idCompDoc = :idCompDoc "
                    + "and acd.tiSupportoComp = 'FILE'";

            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idCompDoc", idComp);

            lstComponenti = query.getResultList();
            for (AroCompDoc tmpCmp : lstComponenti) {
                // urn
                String urnCompletoIniz = null;
                String urnCompleto = unitaDocumentarieHelper.findAroCompUrnCalcByType(tmpCmp, TiUrn.NORMALIZZATO)
                        .getDsUrn();
                // urn iniz
                AroCompUrnCalc compUrnCalc = unitaDocumentarieHelper.findAroCompUrnCalcByType(tmpCmp, TiUrn.INIZIALE);
                if (compUrnCalc != null) {
                    urnCompletoIniz = compUrnCalc.getDsUrn();
                }
                ComponenteRec tmpCRec = new ComponenteRec(urnCompleto, urnCompletoIniz);
                // MEV#22921 Parametrizzazione servizi di recupero
                tmpCRec.setUrnOriginaleVersata(tmpCmp.getDlUrnCompVers());
                tmpCRec.setNomeFileOriginaleVersato(tmpCmp.getDsNomeCompVers());
                tmpCRec.setIdCompDoc(tmpCmp.getIdCompDoc());
                // Gestisco il formato
                String dsFormatoRapprCalc = tmpCmp.getDsFormatoRapprCalc();
                if (dsFormatoRapprCalc != null && !dsFormatoRapprCalc.contains("???")) {
                    tmpCRec.setEstensioneFile(dsFormatoRapprCalc);
                } else {
                    if (tmpCmp.getDecFormatoFileDoc() != null) {
                        tmpCRec.setEstensioneFile(tmpCmp.getDecFormatoFileDoc().getNmFormatoFileDoc());
                    } else {
                        tmpCRec.setEstensioneFile("unknown");
                    }
                }
                lstComp.add(tmpCRec);
            }
            if (lstComp.size() > 0) {
                rispostaControlli.setrString(this.generaNomeFileComp(lstComponenti.get(0), lstComp.get(0)));
            } else {
                rispostaControlli.setrString(this.generaNomeFileComp(idComp));
            }

            rispostaControlli.setrObject(lstComp);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecupero.leggiCompFileInComp " + e.getMessage()));
            log.error("Eccezione nella lettura della tabella dei componenti in COMP ", e);
        }
        return rispostaControlli;
    }

    private String generaNomeFileComp(long idComp) {
        AroCompDoc tmpCmp = entityManager.find(AroCompDoc.class, idComp);
        // urn
        String urnCompletoIniz = null;
        String urnCompleto = unitaDocumentarieHelper.findAroCompUrnCalcByType(tmpCmp, TiUrn.NORMALIZZATO).getDsUrn();
        // urn iniz
        AroCompUrnCalc compUrnCalc = unitaDocumentarieHelper.findAroCompUrnCalcByType(tmpCmp, TiUrn.INIZIALE);
        if (compUrnCalc != null) {
            urnCompletoIniz = compUrnCalc.getDsUrn();
        }
        // dto
        ComponenteRec tmpCRec = new ComponenteRec(urnCompleto, urnCompletoIniz);
        tmpCRec.setIdCompDoc(tmpCmp.getIdCompDoc());
        // Gestisco il formato
        String dsFormatoRapprCalc = tmpCmp.getDsFormatoRapprCalc();
        if (dsFormatoRapprCalc != null && !dsFormatoRapprCalc.contains("???")) {
            tmpCRec.setEstensioneFile(dsFormatoRapprCalc);
        } else {
            if (tmpCmp.getDecFormatoFileDoc() != null) {
                tmpCRec.setEstensioneFile(tmpCmp.getDecFormatoFileDoc().getNmFormatoFileDoc());
            } else {
                tmpCRec.setEstensioneFile("unknown");
            }
        }
        return this.generaNomeFileComp(tmpCmp, tmpCRec);
    }

    // OK
    private String generaNomeFileComp(AroCompDoc acd, ComponenteRec comp) {
        StringBuilder tmpString = new StringBuilder();
        AroUnitaDoc aud = acd.getAroStrutDoc().getAroDoc().getAroUnitaDoc();
        tmpString.append(aud.getCdRegistroKeyUnitaDoc());
        tmpString.append("-");
        tmpString.append(aud.getAaKeyUnitaDoc());
        tmpString.append("-");
        tmpString.append(aud.getCdKeyUnitaDoc());
        tmpString.append("-");
        tmpString.append(comp.getNomeFileBreve());
        return MessaggiWSFormat.bonificaUrnPerNomeFile(tmpString.toString());
    }

    // OK - caricaparametri
    public RispostaControlli leggiChiaveUnitaDoc(long idUnitaDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        AroUnitaDoc tmpUnitaDoc = null;
        CSChiave chiave;

        try {
            tmpUnitaDoc = entityManager.find(AroUnitaDoc.class, idUnitaDoc);
            chiave = new CSChiave();
            chiave.setTipoRegistro(tmpUnitaDoc.getCdRegistroKeyUnitaDoc());
            chiave.setAnno(tmpUnitaDoc.getAaKeyUnitaDoc().longValue());
            chiave.setNumero(tmpUnitaDoc.getCdKeyUnitaDoc());
            rispostaControlli.setrObject(chiave);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecupero.leggiChiaveUnitaDoc " + e.getMessage()));
            log.error("Eccezione nella lettura  della tabella delle unità doc ", e);
        }
        return rispostaControlli;
    }

    // OK - caricaparametri
    public RispostaControlli leggiVersatoreUnitaDoc(long idUnitaDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        AroUnitaDoc tmpUnitaDoc = null;
        CSVersatore versatore;

        try {
            tmpUnitaDoc = entityManager.find(AroUnitaDoc.class, idUnitaDoc);
            versatore = new CSVersatore();
            versatore.setStruttura(tmpUnitaDoc.getOrgStrut().getNmStrut());
            versatore.setEnte(tmpUnitaDoc.getOrgStrut().getOrgEnte().getNmEnte());
            versatore.setAmbiente(tmpUnitaDoc.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
            rispostaControlli.setrObject(versatore);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecupero.leggiVersatoreUnitaDoc " + e.getMessage()));
            log.error("Eccezione nella lettura  della tabella delle unità doc ", e);
        }
        return rispostaControlli;
    }

    public RispostaControlli leggiXMLSessioneversUd(long idUnitaDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<VrsXmlDatiSessioneVers> lstDatiSessioneVerses = null;

        try {
            String queryStr = "select xml  from VrsXmlDatiSessioneVers xml "
                    + "where xml.vrsDatiSessioneVers.vrsSessioneVers.aroUnitaDoc.idUnitaDoc  = :idUnitaDoc "
                    + "and xml.vrsDatiSessioneVers.vrsSessioneVers.tiStatoSessioneVers = 'CHIUSA_OK' "
                    + "and xml.vrsDatiSessioneVers.tiDatiSessioneVers = 'XML_DOC' "
                    + "order by xml.vrsDatiSessioneVers.vrsSessioneVers.dtChiusura ";

            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idUnitaDoc", idUnitaDoc);

            lstDatiSessioneVerses = query.getResultList();
            rispostaControlli.setrObject(lstDatiSessioneVerses);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecupero.leggiXMLSessioneversUd " + e.getMessage()));
            log.error("Eccezione nella lettura  della tabella degli XML di versamento UD ", e);
        }
        return rispostaControlli;
    }

    public RispostaControlli leggiXMLSessioneversDoc(long idDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<VrsXmlDatiSessioneVers> lstDatiSessioneVerses = null;

        String queryStr = null;

        try {
            AroDoc tmpAroDoc = entityManager.find(AroDoc.class, idDoc);
            if (tmpAroDoc.getTiCreazione().equals(CostantiDB.TipoCreazioneDoc.AGGIUNTA_DOCUMENTO.name())) {
                // ricavo i documenti XML relativi ad un versameto di aggiunta, relativo al
                // documento richiesto
                queryStr = "select xml  from VrsXmlDatiSessioneVers xml "
                        + "where xml.vrsDatiSessioneVers.vrsSessioneVers.aroDoc.idDoc = :idDoc "
                        + "and xml.vrsDatiSessioneVers.vrsSessioneVers.tiStatoSessioneVers = 'CHIUSA_OK' "
                        + "and xml.vrsDatiSessioneVers.tiDatiSessioneVers = 'XML_DOC' "
                        + "and xml.vrsDatiSessioneVers.vrsSessioneVers.tiSessioneVers = 'AGGIUNGI_DOCUMENTO' "
                        + "order by xml.vrsDatiSessioneVers.vrsSessioneVers.dtChiusura ";
            } else {
                // ricavo i documenti XML relativi al versamento principale, a cui appartiene il
                // documento
                queryStr = "select xml  from VrsXmlDatiSessioneVers xml "
                        + "join xml.vrsDatiSessioneVers.vrsSessioneVers.aroUnitaDoc.aroDocs ad "
                        + "where ad.idDoc = :idDoc "
                        + "and xml.vrsDatiSessioneVers.vrsSessioneVers.tiStatoSessioneVers = 'CHIUSA_OK' "
                        + "and xml.vrsDatiSessioneVers.tiDatiSessioneVers = 'XML_DOC' "
                        + "and xml.vrsDatiSessioneVers.vrsSessioneVers.tiSessioneVers = 'VERSAMENTO' "
                        + "order by xml.vrsDatiSessioneVers.vrsSessioneVers.dtChiusura ";
            }

            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idDoc", idDoc);

            lstDatiSessioneVerses = query.getResultList();
            rispostaControlli.setrObject(lstDatiSessioneVerses);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecupero.leggiXMLSessioneversUd " + e.getMessage()));
            log.error("Eccezione nella lettura  della tabella degli XML di versamento UD ", e);
        }
        return rispostaControlli;
    }

    public RispostaControlli leggiXMLSessioneversComp(long idComp) {
        AroCompDoc tmpAroCompDoc = entityManager.find(AroCompDoc.class, idComp);
        return this.leggiXMLSessioneversDoc(tmpAroCompDoc.getAroStrutDoc().getAroDoc().getIdDoc());
    }

    public RispostaControlli leggiXMLSessioneVersUdPrincipale(long idUnitaDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<VrsXmlDatiSessioneVers> lstDatiSessioneVerses;

        try {
            String queryStr = "SELECT xmlDatiSessioneVers FROM VrsXmlDatiSessioneVers xmlDatiSessioneVers "
                    + "JOIN xmlDatiSessioneVers.vrsDatiSessioneVers datiSessioneVers "
                    + "JOIN datiSessioneVers.vrsSessioneVers sessioneVers " + "JOIN sessioneVers.aroUnitaDoc unitaDoc "
                    + "WHERE unitaDoc.idUnitaDoc  = :idUnitaDoc " + "AND sessioneVers.tiSessioneVers = 'VERSAMENTO' "
                    + "AND sessioneVers.tiStatoSessioneVers = 'CHIUSA_OK' "
                    + "AND datiSessioneVers.tiDatiSessioneVers = 'XML_DOC' "
                    + "AND xmlDatiSessioneVers.tiXmlDati IN ('RICHIESTA', 'INDICE_FILE', 'RISPOSTA') "
                    + "ORDER BY sessioneVers.dtChiusura ";

            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idUnitaDoc", idUnitaDoc);

            lstDatiSessioneVerses = query.getResultList();
            rispostaControlli.setrObject(lstDatiSessioneVerses);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecupero.leggiXMLSessioneversUd " + e.getMessage()));
            log.error("Eccezione nella lettura  della tabella degli XML di versamento UD ", e);
        }
        return rispostaControlli;
    }

    public RispostaControlli leggiXMLSessioneVersDocAggiunto(long idDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<VrsXmlDatiSessioneVers> lstDatiSessioneVerses = null;

        try {
            lstDatiSessioneVerses = new ArrayList<VrsXmlDatiSessioneVers>();
            String queryStr = "SELECT xmlDatiSessioneVers FROM VrsXmlDatiSessioneVers xmlDatiSessioneVers "
                    + "JOIN xmlDatiSessioneVers.vrsDatiSessioneVers datiSessioneVers "
                    + "JOIN datiSessioneVers.vrsSessioneVers sessioneVers " + "JOIN sessioneVers.aroUnitaDoc unitaDoc "
                    + "JOIN sessioneVers.aroDoc doc " + "WHERE doc.idDoc = :idDoc "
                    + "AND sessioneVers.tiSessioneVers = 'AGGIUNGI_DOCUMENTO' "
                    + "AND sessioneVers.tiStatoSessioneVers = 'CHIUSA_OK' "
                    + "AND datiSessioneVers.tiDatiSessioneVers = 'XML_DOC' "
                    + "AND xmlDatiSessioneVers.tiXmlDati IN ('RICHIESTA', 'RISPOSTA') "
                    + "ORDER BY sessioneVers.dtChiusura ";

            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idDoc", idDoc);

            lstDatiSessioneVerses = query.getResultList();
            rispostaControlli.setrObject(lstDatiSessioneVerses);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecupero.leggiXMLSessioneversUd " + e.getMessage()));
            log.error("Eccezione nella lettura  della tabella degli XML di versamento UD ", e);
        }
        return rispostaControlli;
    }

    // EVO#20972
    public RispostaControlli leggiXMLSessioneversUpd(long idUnitaDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<AroXmlUpdUnitaDoc> xmlupds = new ArrayList<>();

        try {
            /*
             * ricavo la lista degli aggiornamenti versati riferiti all'unita doc corrente.
             */
            String queryStr = "SELECT DISTINCT upd FROM AroUpdUnitaDoc upd " + "join upd.aroUnitaDoc ud "
                    + "where ud.idUnitaDoc = :idUnitaDoc " + "order by upd.tsIniSes ";

            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idUnitaDoc", idUnitaDoc);

            List<AroUpdUnitaDoc> upds = query.getResultList();
            for (AroUpdUnitaDoc tmpUpd : upds) {
                queryStr = "select xml from AroXmlUpdUnitaDoc xml "
                        + "where xml.aroUpdUnitaDoc.idUpdUnitaDoc = :idUpdUnitaDoc ";

                query = entityManager.createQuery(queryStr);
                query.setParameter("idUpdUnitaDoc", tmpUpd.getIdUpdUnitaDoc());

                xmlupds.addAll(query.getResultList());
            }
            rispostaControlli.setrObject(xmlupds);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecupero.leggiXMLSessioneversUpd " + e.getMessage()));
            log.error("Eccezione nella lettura xml di versamento aggiornamento metadati ", e);
        }
        return rispostaControlli;
    }
    // end EVO#20972

    public RispostaControlli contaXMLIndiceAIP(long idUnitaDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        try {
            String queryStr = "select count(aro) from AroFileVerIndiceAipUd aro "
                    + "WHERE aro.aroVerIndiceAipUd.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                    + "and aro.aroVerIndiceAipUd.aroIndiceAipUd.tiFormatoIndiceAip = 'UNISYNCRO' ";

            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idUnitaDoc", idUnitaDoc);

            rispostaControlli.setrBoolean(true);
            rispostaControlli.setrLong((Long) query.getSingleResult());
        } catch (Exception e) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecupero.contaXMLIndiceAIP " + e.getMessage()));
            log.error("Eccezione nella lettura della tabella dei file di versione indice AIP ", e);
        }
        return rispostaControlli;
    }

    public RispostaControlli leggiXMLIndiceAIP(long idUnitaDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<AroFileVerIndiceAipUd> listaAroFile = null;

        try {
            String queryStr = "SELECT aro FROM AroFileVerIndiceAipUd aro "
                    + "WHERE aro.aroVerIndiceAipUd.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                    + "and aro.aroVerIndiceAipUd.aroIndiceAipUd.tiFormatoIndiceAip = 'UNISYNCRO' ";

            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idUnitaDoc", idUnitaDoc);

            listaAroFile = query.getResultList();
            rispostaControlli.setrObject(listaAroFile);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecupero.leggiXMLIndiceAIP " + e.getMessage()));
            log.error("Eccezione nella lettura della tabella dei file di versione indice AIP ", e);
        }
        return rispostaControlli;
    }

    // EVO#20972:MEV#20971
    public RispostaControlli leggiXMLIndiceAIPV2(long idUnitaDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<AroFileVerIndiceAipUd> listaAroFile = null;

        try {
            String queryStr = "SELECT aro FROM AroFileVerIndiceAipUd aro JOIN aro.aroVerIndiceAipUd u "
                    + "WHERE u.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                    + "AND u.aroIndiceAipUd.tiFormatoIndiceAip = 'UNISYNCRO' " + "ORDER BY u.pgVerIndiceAip DESC ";

            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idUnitaDoc", idUnitaDoc);

            listaAroFile = query.getResultList();
            rispostaControlli.setrObject(listaAroFile);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecupero.leggiXMLIndiceAIPV2 " + e.getMessage()));
            log.error("Eccezione nella lettura della tabella dei file di versione indice AIP ", e);
        }
        return rispostaControlli;
    }

    public RispostaControlli leggiXMLIndiceAIPExternal(Long idUnitaDoc) {
        RispostaControlli rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        try {
            // Ricavo tutte le versioni precedenti dell'AIP provenienti da altri conservatori
            String queryStr = "SELECT u FROM AroVLisaipudSistemaMigraz u " + "WHERE u.idUnitaDoc = :idUnitaDoc ";
            Query query = entityManager.createQuery(queryStr);
            query.setParameter("idUnitaDoc", idUnitaDoc);
            List<AroVLisaipudSistemaMigraz> versioniPrecedentiExternal = query.getResultList();
            rispostaControlli.setrObject(versioniPrecedentiExternal);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecupero.leggiXMLIndiceAIPExternal " + e.getMessage()));
            log.error(
                    "Eccezione durante il recupero delle versioni precedenti dell'AIP provenienti da altri conservatori "
                            + e);
        }
        return rispostaControlli;
    }
    // end EVO#20972:MEV#20971

    public RispostaControlli leggiElvFileElencoVers(long idUnitaDoc, String tiFileElencoVers,
            String tiFileElencoVers2) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        ElvFileElencoVer fileElencoVers = null;

        try {
            // MAC#25864
            String queryStr = "SELECT fileElencoVers FROM AroVLisElvVer elv, AroVerIndiceAipUd verIndiceAipUd, ElvFileElencoVer fileElencoVers "
                    + "WHERE verIndiceAipUd.aroIndiceAipUd.aroUnitaDoc.idUnitaDoc = elv.idUnitaDoc "
                    + "AND verIndiceAipUd.pgVerIndiceAip = (SELECT MAX(d.pgVerIndiceAip) FROM AroVerIndiceAipUd d WHERE d.aroIndiceAipUd = verIndiceAipUd.aroIndiceAipUd) "
                    + "AND fileElencoVers.elvElencoVer = verIndiceAipUd.elvElencoVer "
                    + "AND elv.idUnitaDoc = :idUnitaDoc " + "AND fileElencoVers.tiFileElencoVers = :tiFileElencoVers ";
            // end MAC#25864
            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idUnitaDoc", idUnitaDoc);
            query.setParameter("tiFileElencoVers", tiFileElencoVers);

            List<ElvFileElencoVer> listaFileElencoVers = query.getResultList();
            if (!listaFileElencoVers.isEmpty()) {
                // Se sto considerando la firma
                if (tiFileElencoVers
                        .equals(it.eng.parer.entity.constraint.ElvFileElencoVer.TiFileElencoVers.FIRMA_ELENCO_INDICI_AIP
                                .name())) {
                    String queryStr2 = "SELECT fileElencoVers FROM AroUnitaDoc unitaDoc "
                            + "JOIN unitaDoc.elvElencoVer elencoVers "
                            + "JOIN elencoVers.elvFileElencoVers fileElencoVers "
                            + "WHERE unitadoc.idUnitaDoc = :idUnitaDoc "
                            + "AND fileElencoVers.tiFileElencoVers = :tiFileElencoVers ";

                    javax.persistence.Query query2 = entityManager.createQuery(queryStr2);
                    query2.setParameter("idUnitaDoc", idUnitaDoc);
                    query2.setParameter("tiFileElencoVers", tiFileElencoVers2);

                    List<ElvFileElencoVer> listaFileElencoVers2 = query.getResultList();
                    if (!listaFileElencoVers2.isEmpty()) {
                        fileElencoVers = listaFileElencoVers2.get(0);
                    }
                } // altrimenti è il caso della marca
                else if (tiFileElencoVers.equals(
                        it.eng.parer.entity.constraint.ElvFileElencoVer.TiFileElencoVers.MARCA_FIRMA_ELENCO_INDICI_AIP
                                .name())) {
                    fileElencoVers = listaFileElencoVers.get(0);
                }
            }
            rispostaControlli.setrObject(fileElencoVers);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecupero.leggiElvFileElencoVers " + e.getMessage()));
            log.error("Eccezione nella lettura della tabella dei file elenco indici AIP ", e);
        }
        return rispostaControlli;
    }

    public RispostaControlli leggiAroUdAppartVerSerie(long idUnitaDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        try {
            String queryStr = "SELECT udAppartVerSerie " + "FROM AroUdAppartVerSerie udAppartVerSerie "
                    + "JOIN udAppartVerSerie.serContenutoVerSerie contenutoVerSerie "
                    + "JOIN contenutoVerSerie.serVerSerie verSerie " + "JOIN verSerie.serSerie serie "
                    + "JOIN verSerie.serStatoVerSeries statoVerSerie " + "JOIN serie.serStatoSeries statoSerie "
                    + "WHERE udAppartVerSerie.aroUnitaDoc.idUnitaDoc = :idUnitaDoc "
                    + "AND contenutoVerSerie.tiContenutoVerSerie = 'EFFETTIVO' "
                    + "AND statoVerSerie.tiStatoVerSerie != 'ANNULLATA' "
                    + "AND verSerie.pgVerSerie = (SELECT MAX(ver.pgVerSerie) FROM SerVerSerie ver WHERE ver.idVerSerie = verSerie.idVerSerie) "
                    + "AND verSerie.idStatoVerSerieCor = statoVerSerie.idStatoVerSerie "
                    + "AND serie.idStatoSerieCor = statoSerie.idStatoSerie " + "ORDER BY serie.aaSerie ASC ";

            Query query = entityManager.createQuery(queryStr);
            query.setParameter("idUnitaDoc", idUnitaDoc);
            List<AroUdAppartVerSerie> uds = (List<AroUdAppartVerSerie>) query.getResultList();
            rispostaControlli.setrObject(uds);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecupero.leggiAroUdAppartVerSerie " + e.getMessage()));
            log.error("Eccezione nella lettura della tabella delle appartenenze ud alla serie ", e);
        }
        return rispostaControlli;
    }

    public RispostaControlli leggiFasUnitaDocFascicolo(long idUnitaDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);

        try {
            String queryStr = "SELECT unitaDocFascicolo FROM FasUnitaDocFascicolo unitaDocFascicolo "
                    + "WHERE unitaDocFascicolo.aroUnitaDoc.idUnitaDoc = :idUnitaDoc ";

            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idUnitaDoc", idUnitaDoc);

            List<FasUnitaDocFascicolo> uds = (List<FasUnitaDocFascicolo>) query.getResultList();
            rispostaControlli.setrObject(uds);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecupero.leggiFasUnitaDocFascicolo " + e.getMessage()));
            log.error("Eccezione nella lettura della tabella delle appartenenze ud al fascicolo ", e);
        }
        return rispostaControlli;
    }

    // da RecuperoPrsr
    public RispostaControlli checkIdDocumentoinUD(long idUnitaDoc, String idDocumento) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);

        List<AroDoc> lstAd;

        try {
            String queryStr = "select t from AroDoc t " + "where t.aroUnitaDoc.idUnitaDoc = :idUnitaDocIn "
                    + "and t.cdKeyDocVers = :cdKeyDocVersIn " + "and t.dtAnnul > :dataDiOggiIn ";
            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idUnitaDocIn", idUnitaDoc);
            query.setParameter("cdKeyDocVersIn", idDocumento);
            query.setParameter("dataDiOggiIn", new Date());

            lstAd = query.getResultList();
            if (lstAd.size() == 1) {
                rispostaControlli.setrLong(lstAd.get(0).getIdDoc());
                rispostaControlli.setrBoolean(true);
            } else {
                rispostaControlli.setCodErr(MessaggiWSBundle.DOC_010_001);
                rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.DOC_010_001, idDocumento));
            }
        } catch (Exception e) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecupero.checkIdDocumentoinUD " + e.getMessage()));
            log.error("Eccezione nella verifica esistenza del documento da recuperare ", e);
        }
        return rispostaControlli;
    }

    // da RecuperoPrsr
    public RispostaControlli checkIdComponenteinDoc(long idDoc, long progressivo) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);

        List<AroCompDoc> lstCompDocs;

        try {
            String queryStr = "select t from AroCompDoc t " + "where t.aroStrutDoc.aroDoc.idDoc = :idDocIn "
                    + "and t.niOrdCompDoc = :niOrdCompDocIn ";
            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idDocIn", idDoc);
            query.setParameter("niOrdCompDocIn", new BigDecimal(progressivo));

            lstCompDocs = query.getResultList();
            if (lstCompDocs.size() == 1) {
                rispostaControlli.setrLong(lstCompDocs.get(0).getIdCompDoc());
                if (lstCompDocs.get(0).getAroCompDoc() != null) {
                    // questo permette di capire se è un sottocomponente
                    rispostaControlli.setrLongExtended(lstCompDocs.get(0).getAroCompDoc().getIdCompDoc());
                }
                rispostaControlli.setrBoolean(true);
            } else {
                rispostaControlli.setCodErr(MessaggiWSBundle.COMP_010_001);
                rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.COMP_010_001, progressivo));
            }
        } catch (Exception e) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecupero.checkIdComponenteinDoc " + e.getMessage()));
            log.error("Eccezione nella verifica esistenza del componenente da recuperare ", e);
        }
        return rispostaControlli;
    }

    // da RecuperoPrsr
    public RispostaControlli checkTipoDocumentoperStrut(long idStrut, String nmTipoDoc, String descChiaveUd) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);

        List<DecTipoDoc> lstTipoDocs;

        try {
            String queryStr = "select t from DecTipoDoc t " + "where t.orgStrut.idStrut = :idStrutIn "
                    + "and t.nmTipoDoc = :nmTipoDocIn ";
            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idStrutIn", idStrut);
            query.setParameter("nmTipoDocIn", nmTipoDoc);

            lstTipoDocs = query.getResultList();
            if (lstTipoDocs.size() == 1) {
                rispostaControlli.setrLong(lstTipoDocs.get(0).getIdTipoDoc());
                rispostaControlli.setrBoolean(true);
            } else {
                rispostaControlli.setCodErr(MessaggiWSBundle.DOC_001_001);
                rispostaControlli
                        .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.DOC_001_001, descChiaveUd, nmTipoDoc)
                                .replaceFirst("Documento", "Unità Documentaria"));
            }
        } catch (Exception e) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecupero.checkTipoDocumentoperStrut " + e.getMessage()));
            log.error("Eccezione nella verifica esistenza del tipo documento per la struttura ", e);
        }
        return rispostaControlli;
    }

    // da RecuperoPrsr
    public RispostaControlli checkTipoDocumentoinUD(long idUnitaDoc, String nmTipoDoc, String descChiaveUd) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);

        List<AroDoc> lstAroDocs;

        try {
            String queryStr = "select t from AroDoc t " + "where t.aroUnitaDoc.idUnitaDoc = :idUnitaDocIn "
                    + "and t.decTipoDoc.nmTipoDoc = :nmTipoDocIn ";
            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idUnitaDocIn", idUnitaDoc);
            query.setParameter("nmTipoDocIn", nmTipoDoc);

            lstAroDocs = query.getResultList();
            if (!lstAroDocs.isEmpty()) {
                rispostaControlli.setrLong(lstAroDocs.get(0).getDecTipoDoc().getIdTipoDoc());
                rispostaControlli.setrBoolean(true);
            } else {
                rispostaControlli.setCodErr(MessaggiWSBundle.DOC_001_004);
                rispostaControlli
                        .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.DOC_001_004, nmTipoDoc, descChiaveUd));
            }
        } catch (Exception e) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecupero.checkTipoDocumentoinUD " + e.getMessage()));
            log.error("Eccezione nella verifica esistenza del tipo documento nella Unità Documentaria ", e);
        }
        return rispostaControlli;
    }

    /**
     * Verifica esistenza chiave normalizzata
     *
     * @param versatore
     *            nome versatore
     * @param chiave
     *            chiave unita doc
     * @param idRegistro
     *            id registro
     * @param idUnitaDoc
     *            id unita doc
     * @param cdKeyNormalized
     *            chiave normalizzata unita doc
     * 
     * @return RispostaControlli con risultato della verifica effettuata
     */
    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public RispostaControlli calcCdKeyNormAndUrnPreg(CSVersatore versatore, CSChiave chiave, long idRegistro,
            long idUnitaDoc, String cdKeyNormalized) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);

        try {
            // recupero parametro DATA_INIZIO_CALC_NUOVI_URN
            String dataInizioParam = configurationHelper.getValoreParamApplic(
                    CostantiDB.ParametroAppl.DATA_INIZIO_CALC_NUOVI_URN, null, null, null, null,
                    CostantiDB.TipoAplVGetValAppart.APPLIC);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date dtInizioCalcNuoviUrn = dateFormat.parse(dataInizioParam);

            rispostaControlli = this.getDtMaxVersMaxByUd(idUnitaDoc);
            if (rispostaControlli.getrLong() != -1) {
                // dtVersMax ottenuto da vista
                Date dtVersMax = rispostaControlli.getrDate();
                // controllo : dtVersMax <= dataInizioCalcNuoviUrn
                if ((dtVersMax.before(dtInizioCalcNuoviUrn) || dtVersMax.equals(dtInizioCalcNuoviUrn))) {
                    // lock ud
                    Map<String, Object> properties = new HashMap<>();
                    properties.put("javax.persistence.lock.timeout", 25);
                    AroUnitaDoc aroUnitaDoc = entityManager.find(AroUnitaDoc.class, idUnitaDoc,
                            LockModeType.PESSIMISTIC_WRITE, properties);
                    //
                    if (StringUtils.isBlank(cdKeyNormalized)) {
                        // calcola e verifica la chiave normalizzata
                        cdKeyNormalized = MessaggiWSFormat.normalizingKey(chiave.getNumero()); // base
                        rispostaControlli = this.checkCdKeyNormalized(idRegistro, chiave, cdKeyNormalized);
                        // 666 error
                        if (rispostaControlli.getCodErr() != null) {
                            return rispostaControlli;
                        }
                        if (rispostaControlli.getrLong() == 0 /* esiste chiave normalizzata */) {
                            // Esiste key normalized
                            rispostaControlli.setCodErr(MessaggiWSBundle.UD_005_005);
                            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.UD_005_005,
                                    MessaggiWSFormat.formattaUrnPartUnitaDoc(chiave)));
                            rispostaControlli.setrBoolean(false);
                            return rispostaControlli;
                        } else {
                            // aggiorno cdKeyNormalized
                            aroUnitaDoc.setCdKeyUnitaDocNormaliz(cdKeyNormalized);
                            entityManager.persist(aroUnitaDoc);
                        }
                    }
                    /*
                     * URN pregressi
                     */
                    // componenti
                    urnHelper.scriviUrnCompPreg(aroUnitaDoc, versatore, chiave);
                    // sip ud
                    urnHelper.scriviUrnSipUdPreg(aroUnitaDoc, versatore, chiave);
                    // sip doc
                    urnHelper.scriviUrnSipDocAggPreg(aroUnitaDoc, versatore, chiave);
                    // sip upd
                    urnHelper.scriviUrnSipUpdPreg(aroUnitaDoc, versatore, chiave);

                    // indice AIP
                    AroVerIndiceAipUd aroVerIndiceAipUd = evHelper
                            .getUltimaVersioneIndiceAip(aroUnitaDoc.getIdUnitaDoc());
                    if (aroVerIndiceAipUd != null && !aroVerIndiceAipUd.getDtCreazione().after(dtInizioCalcNuoviUrn)) {
                        // eseguo registra urn aip pregressi
                        urnHelper.scriviUrnAipUdPreg(aroUnitaDoc, versatore, chiave);
                    }

                    // unlock
                    entityManager.flush();
                } // if dtInizioCalcNuoviUrn
            } // if getDtMaxVersMaxByUd
              // OK
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            context.setRollbackOnly();
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecupero.calcCdKeyNormAndUrnPreg " + e.getMessage()));
            log.error("Eccezione nella verifica esistenza / aggiornamento URN normalizzato ", e);
        }
        return rispostaControlli;
    }

    private RispostaControlli getDtMaxVersMaxByUd(long idUnitaDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrLong(-1); // entity non trovata
        AroVDtVersMaxByUnitaDoc aroVDtVersMaxByUnitaDoc = null;

        try {
            Query query = entityManager
                    .createQuery("SELECT aro FROM AroVDtVersMaxByUnitaDoc aro WHERE aro.idUnitaDoc = :idUnitaDoc ");
            query.setParameter("idUnitaDoc", idUnitaDoc);
            aroVDtVersMaxByUnitaDoc = (AroVDtVersMaxByUnitaDoc) query.getSingleResult();
            if (aroVDtVersMaxByUnitaDoc == null) {
                rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666P);
                rispostaControlli.setDsErr("ControlliSemantici.getDtMaxVersMaxByUd: non presente per id " + idUnitaDoc);
            } else {
                rispostaControlli.setrLong(0);
                rispostaControlli.setrDate(aroVDtVersMaxByUnitaDoc.getDtVersMax());
            }
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "ControlliRecupero.getDtMaxVersMaxByUd: " + e.getMessage()));
            log.error("Eccezione nella lettura della tabella di decodifica", e);
        }
        return rispostaControlli;
    }

    private RispostaControlli checkCdKeyNormalized(long idRegistro, CSChiave key, String cdKeyUnitaDocNormaliz) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrLong(-1);

        Long num;
        try {
            String queryStr = "select count(ud) from AroUnitaDoc ud "
                    + "where ud.decRegistroUnitaDoc.idRegistroUnitaDoc = :idRegistro "
                    + " and ud.aaKeyUnitaDoc = :aaKeyUnitaDoc " + " and ud.cdKeyUnitaDoc != :cdKeyUnitaDoc "
                    + " and ud.cdKeyUnitaDocNormaliz = :cdKeyUnitaDocNormaliz ";

            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idRegistro", idRegistro);
            query.setParameter("aaKeyUnitaDoc", key.getAnno());
            query.setParameter("cdKeyUnitaDoc", key.getNumero());
            query.setParameter("cdKeyUnitaDocNormaliz", cdKeyUnitaDocNormaliz);

            num = (Long) query.getSingleResult();
            if (num > 0) {
                // esiste chiave normalized
                rispostaControlli.setrLong(0);
            }
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "ControlliRecupero.checkCdKeyNormalized: " + e.getMessage()));
            log.error("Eccezione nella lettura della tabella di decodifica ", e);
        }

        return rispostaControlli;
    }

    public RispostaControlli leggiFirReportIdsAndGenZipName(long idCompDoc) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        try {
            // TOFIX: da verificare se nome file componente corretto
            String baseNameFileComp = this.generaNomeFileComp(idCompDoc);

            String queryStr = "select t.idFirReport from FirReport t " + "where t.aroCompDoc.idCompDoc = :idCompDoc";
            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idCompDoc", idCompDoc);

            List<Long> idFirReports = query.getResultList();
            if (!idFirReports.isEmpty()) {
                rispostaControlli.setrString(baseNameFileComp.concat("_ReportVerificaFirma"));// TOFIX:
                // verifica
                // nome
                // zip
                // file
                rispostaControlli.setrObject(idFirReports);
                rispostaControlli.setrBoolean(true);
            } else {
                rispostaControlli.setCodErr(MessaggiWSBundle.COMP_006_003);
                rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.COMP_006_003, baseNameFileComp));
            }

        } catch (Exception e) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecupero.leggiFirReportIdsAndGenZipName " + e.getMessage()));
            log.error("Eccezione nella lettura della tabella dei componenti in UD ", e);
        }
        return rispostaControlli;
    }

    // EVO#20972
    /**
     * Restituisce la lista degli utenti applicativi configurati per la firma HSM dato l'ambiente
     * 
     * @param idAmbiente
     *            id ambiente
     * 
     * @return RispostaControlli con risultato del recupero effettuato
     */
    public RispostaControlli leggiListaUserByHsmUsername(BigDecimal idAmbiente) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);

        List<UsrUser> listaUser = new ArrayList<>();
        try {
            AplParamApplic paramApplic = configurationHelper.getParamApplic("HSM_USERNAME");
            // Ricavo le coppie di valori multipli del parametro HSM_USERNAME sull'ambiente
            List<AplValParamApplicMulti> listaCoppieValoriHsmUsername = amministrazioneHelper
                    .getAplValParamApplicMultiList(BigDecimal.valueOf(paramApplic.getIdParamApplic()), idAmbiente);
            for (AplValParamApplicMulti valParamApplicMulti : listaCoppieValoriHsmUsername) {
                // Splitto la coppia: il primo valore sarà lo user applicativo, il secondo lo user hsm
                String[] chiaveValore = valParamApplicMulti.getDsValoreParamApplic().split(",");
                // Recupero l'utente nella tabella, partendo da nmUserid IAM univoco
                UsrUser user = userHelper.findUsrUser(chiaveValore[0]);
                if (listaUser.stream().noneMatch(t -> t.getNmUserid().equals(user.getNmUserid()))) {
                    listaUser.add(user);
                }
            }

            rispostaControlli.setrObject(listaUser);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "ControlliRecupero.leggiListaUserByHsmUsername: " + e.getMessage()));
            log.error("Eccezione nella verifica esistenza/recupero utenti applicativi configurati per la firma HSM : ",
                    e);
        }

        return rispostaControlli;
    }

    /**
     * Restituisce il Ruolo per l'authorized signer associato all'utente passato in ingresso per l'ambiente specificato
     *
     * @param idUserIamCor
     *            id user Iam corrente
     * @param idAmbiente
     *            id ambiente
     * 
     * @return RispostaControlli con risultato del recupero effettuato
     */
    public RispostaControlli leggiRuoloAuthorizedSigner(long idUserIamCor, BigDecimal idAmbiente) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);

        try {
            AplParamApplic paramApplic = configurationHelper.getParamApplic("AGENT_AUTHORIZED_SIGNER_ROLE");
            UsrUser user = amministrazioneHelper.findById(UsrUser.class, idUserIamCor);
            String[] authSignerRole = null;
            // Ricavo le coppie di valori multipli del parametro AGENT_AUTHORIZED_SIGNER_ROLE sull'ambiente
            List<AplValParamApplicMulti> listaCoppieValoriHsmUsername = amministrazioneHelper
                    .getAplValParamApplicMultiList(BigDecimal.valueOf(paramApplic.getIdParamApplic()), idAmbiente);
            MultiValuedMap<String, String[]> mappaAuthSignerRole = new ArrayListValuedHashMap<>();
            // Per ogni agent gestito dal parametro AGENT_AUTHORIZED_SIGNER_ROLE
            for (AplValParamApplicMulti valParamApplicMulti : listaCoppieValoriHsmUsername) {
                // Splitto la coppia: il primo valore sarà lo user applicativo, il secondo il ruolo dell'authorized
                // signer e, se specificato, il valore del relevant document
                String[] chiaveValore = valParamApplicMulti.getDsValoreParamApplic().split(",");
                if (chiaveValore.length > 2) {
                    // MAC#25904
                    mappaAuthSignerRole.put(chiaveValore[0], new String[] { chiaveValore[1], chiaveValore[2] });
                    // end MAC#25904
                } else {
                    mappaAuthSignerRole.put(chiaveValore[0], new String[] { chiaveValore[1] });
                }
            }
            // Controllo se l'utente corrente è presente come user applicativo in una delle coppie
            Collection<Map.Entry<String, String[]>> entriesASR = mappaAuthSignerRole.entries();
            for (Map.Entry<String, String[]> entry : entriesASR) {
                String chiave = entry.getKey();
                if (chiave.equals(user.getNmUserid())) {
                    authSignerRole = entry.getValue();
                    if (authSignerRole[0].equalsIgnoreCase("PreservationManager")) {
                        // Vince il ruolo "PreservationManager"
                        break;
                    }
                }
            }

            rispostaControlli.setrObject(authSignerRole);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "ControlliRecupero.leggiRuoloAuthorizedSigner: " + e.getMessage()));
            log.error("Eccezione nel recupero del ruolo per l'authorized signer : ", e);
        }

        return rispostaControlli;
    }
    // end EVO#20972

    public RispostaControlli leggiDecReportServizioCompAndGenEntryName(long idFirReport) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(true);
        rispostaControlli.setrLong(-1);

        try {
            String queryStr = "select t.decReportServizioVerificaCompDoc, u.dsUrn from FirReport t inner join t.firUrnReports u "
                    + "where t.idFirReport = :idFirReport and u.tiUrn = :tiUrn";
            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("idFirReport", idFirReport);
            query.setParameter("tiUrn", TiUrnReport.NORMALIZZATO);

            Object[] result = (Object[]) query.getSingleResult();
            if (result != null) {
                DecReportServizioVerificaCompDoc reportServizio = (DecReportServizioVerificaCompDoc) result[0];
                String dsUrn = (String) result[1];
                // esiste report
                rispostaControlli.setrLong(0);
                rispostaControlli.setrString(MessaggiWSFormat.bonificaUrnPerNomeFile(dsUrn));
                rispostaControlli.setrObject(reportServizio);
            } else {
                rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
                rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                        "Eccezione ControlliRecupero.leggiDecReportServizioCompAndGenEntryName "));
            }
        } catch (Exception e) {
            rispostaControlli.setrBoolean(false);
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecupero.leggiDecReportServizioCompAndGenEntryName " + e.getMessage()));
            log.error("Eccezione nella lettura della tabella dei componenti in UD ", e);
        }
        return rispostaControlli;
    }

}
