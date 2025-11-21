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
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.ws.recuperoDip.ejb;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.AroCompDoc;
import it.eng.parer.entity.AroCompUrnCalc;
import it.eng.parer.entity.AroDoc;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.DecImageTrasform;
import it.eng.parer.entity.DecTipoRapprComp;
import it.eng.parer.entity.DecTrasformTipoRappr;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.AroCompUrnCalc.TiUrn;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.firma.crypto.helper.CryptoRestConfiguratorHelper;
import it.eng.parer.objectstorage.dto.RecuperoDocBean;
import it.eng.parer.web.helper.UnitaDocumentarieHelper;
import it.eng.parer.web.util.Constants.TiEntitaSacerObjectStorage;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.dto.IRispostaWS;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.ejb.RecuperoDocumento;
import it.eng.parer.ws.recupero.dto.ParametriRecupero;
import it.eng.parer.ws.recupero.ejb.oracleBlb.RecBlbOracle;
import it.eng.parer.ws.recuperoDip.dto.CompRecDip;
import it.eng.parer.ws.recuperoDip.utils.ICompTransformer;
import it.eng.parer.ws.recuperoDip.utils.ParametriTrasf;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.CostantiDB.StatoFileTrasform;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Fioravanti_F
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "ControlliRecDip")
@LocalBean
public class ControlliRecDip {

    public static final String NOME_DEFAULT = "Default";
    public static final String VERSIONE_DEFAULT = "DefaultVER";
    //
    public static final String REG_EXP_VERSIONE = "^DefaultVER([0-9]{6}+)\\s*$";
    public static final int PAD_NUM_VERSIONE = 6;

    private static final String P7MEXTRACTOR_CONTEXT = "/api/file-xml";
    //
    private static final Logger log = LoggerFactory.getLogger(ControlliRecDip.class);
    @Resource
    EJBContext context;
    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;
    @EJB
    private UnitaDocumentarieHelper unitaDocumentarieHelper;
    @EJB
    private RecuperoDocumento recuperoDocumento;
    @EJB
    private CryptoRestConfiguratorHelper cryptoRestConfiguratorHelper;

    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public RispostaControlli contaComponenti(ParametriRecupero parametri) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);

        String queryStr = null;
        javax.persistence.Query query = null;

        try {
            switch (parametri.getTipoEntitaSacer()) {
            case UNI_DOC:
            case UNI_DOC_DIP:
            case UNI_DOC_UNISYNCRO:
                // EVO#20972
            case UNI_DOC_UNISYNCRO_V2:
                // end EVO#20972
            case UNI_DOC_DIP_ESIBIZIONE: // di fatto sono lo stesso oggetto
                queryStr = "select count(t) from AroCompDoc t "
                        + "where t.aroStrutDoc.aroDoc.aroUnitaDoc.idUnitaDoc = :idParametroIn "
                        + "and t.aroCompDoc is null " + "and t.decTipoRapprComp is not null ";
                query = entityManager.createQuery(queryStr);
                query.setParameter("idParametroIn", parametri.getIdUnitaDoc());
                break;
            case DOC:
            case DOC_DIP:
            case DOC_DIP_ESIBIZIONE:
                queryStr = "select count(t) from AroCompDoc t "
                        + "where t.aroStrutDoc.aroDoc.idDoc = :idParametroIn "
                        + "and t.aroCompDoc is null " + "and t.decTipoRapprComp is not null ";
                query = entityManager.createQuery(queryStr);
                query.setParameter("idParametroIn", parametri.getIdDocumento());
                break;
            case COMP:
            case COMP_DIP:
            case COMP_DIP_ESIBIZIONE:
                queryStr = "select count(t) from AroCompDoc t "
                        + "where t.idCompDoc = :idParametroIn " + "and t.aroCompDoc is null "
                        + "and t.decTipoRapprComp is not null ";
                query = entityManager.createQuery(queryStr);
                query.setParameter("idParametroIn", parametri.getIdComponente());
                break;
            case SUB_COMP:
                // il recupero sottocomponente non supporta la conversione DIP:
                // viene in ogni caso restituito 0 come numero di elementi convertibili
                query = null;
                break;
            default:
                throw new IllegalArgumentException("Tipo entità non supportato");
            }
            //
            if (query != null) {
                long numComp = (Long) query.getSingleResult();
                rispostaControlli.setrLong(numComp);

            } else {
                rispostaControlli.setrLong(0);
            }
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecDip.contaComponenti " + e.getMessage()));
            log.error("Eccezione nel conteggio componenti convertibili DIP ", e);
        }

        return rispostaControlli;
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public RispostaControlli caricaComponenti(ParametriRecupero parametri) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        LinkedHashMap<Long, CompRecDip> compRecDips;
        List<AroCompDoc> aroCompDocs;

        String queryStr = null;
        javax.persistence.Query query = null;

        try {
            compRecDips = new LinkedHashMap<>();
            switch (parametri.getTipoEntitaSacer()) {
            case UNI_DOC:
            case UNI_DOC_DIP:
            case UNI_DOC_UNISYNCRO:
                // EVO#20972
            case UNI_DOC_UNISYNCRO_V2:
                // end EVO#20972
            case UNI_DOC_DIP_ESIBIZIONE: // di fatto sono lo stesso oggetto
                queryStr = "select t from AroCompDoc t "
                        + "where t.aroStrutDoc.aroDoc.aroUnitaDoc.idUnitaDoc = :idParametroIn "
                        + "and t.aroCompDoc is null " + "and t.decTipoRapprComp is not null ";
                query = entityManager.createQuery(queryStr);
                query.setParameter("idParametroIn", parametri.getIdUnitaDoc());
                break;
            case DOC:
            case DOC_DIP:
            case DOC_DIP_ESIBIZIONE:
                queryStr = "select t from AroCompDoc t "
                        + "where t.aroStrutDoc.aroDoc.idDoc = :idParametroIn "
                        + "and t.aroCompDoc is null " + "and t.decTipoRapprComp is not null ";
                query = entityManager.createQuery(queryStr);
                query.setParameter("idParametroIn", parametri.getIdDocumento());
                break;
            case COMP:
            case COMP_DIP:
            case COMP_DIP_ESIBIZIONE:
                queryStr = "select t from AroCompDoc t " + "where t.idCompDoc = :idParametroIn "
                        + "and t.aroCompDoc is null " + "and t.decTipoRapprComp is not null ";
                query = entityManager.createQuery(queryStr);
                query.setParameter("idParametroIn", parametri.getIdComponente());
                break;
            default:
                /*
                 * se tenta di caricare i componenti DIP in un recupero SUB_COMP va in eccezione:
                 * non avrebbe mai dovuto chiamarla visto che il metodo contaComponenti in questo
                 * caso rende sempre 0
                 */
                throw new IllegalArgumentException("Tipo entità non supportato");
            }
            //
            aroCompDocs = query.getResultList();

            for (AroCompDoc aroCompDoc : aroCompDocs) {
                // urn
                String urnCompletoIniz = null;
                String urnCompleto = unitaDocumentarieHelper
                        .findAroCompUrnCalcByType(aroCompDoc, TiUrn.NORMALIZZATO).getDsUrn();
                // urn iniz
                AroCompUrnCalc compUrnCalc = unitaDocumentarieHelper
                        .findAroCompUrnCalcByType(aroCompDoc, TiUrn.INIZIALE);
                if (compUrnCalc != null) {
                    urnCompletoIniz = compUrnCalc.getDsUrn();
                }
                // dto
                CompRecDip compRecDip = new CompRecDip(urnCompleto, urnCompletoIniz);
                compRecDip.setIdCompDoc(aroCompDoc.getIdCompDoc());
                //
                compRecDip.setTipoAlgoritmoRappresentazione(CostantiDB.TipoAlgoritmoRappr
                        .fromString(aroCompDoc.getDecTipoRapprComp().getTiAlgoRappr()));
                if (aroCompDoc.getDecTipoRapprComp().getDecFormatoFileStandard() != null) {
                    compRecDip.setEstensioneFile(aroCompDoc.getDecTipoRapprComp()
                            .getDecFormatoFileStandard().getNmFormatoFileStandard());
                    compRecDip.setMimeType(aroCompDoc.getDecTipoRapprComp()
                            .getDecFormatoFileStandard().getNmMimetypeFile());
                } else {
                    compRecDip.setEstensioneFile("_"); // in caso non fosse stato specificato un
                    // formato di output in
                    // fase di configurazione
                    compRecDip.setMimeType("application/octet-stream");
                }
                // algo hash
                compRecDip.setDsAlgoHashFileCalc(aroCompDoc.getDsAlgoHashFileCalc());
                compRecDip.setNomeFormatoRappresentazione(
                        aroCompDoc.getDecTipoRapprComp().getNmTipoRapprComp() + " -> "
                                + compRecDip.getEstensioneFile() + " (" + compRecDip.getMimeType()
                                + ")");

                if (aroCompDoc.getDecTipoRapprComp().getDecFormatoFileDocConv() != null) { // c'è un
                    // convertitore,
                    // devo
                    // recuperarlo
                    compRecDips.put(aroCompDoc.getIdCompDoc(), compRecDip);
                    for (AroCompDoc aroSubComp : aroCompDoc.getAroCompDocs()) {
                        if (aroSubComp.getDecTipoCompDoc().getTiUsoCompDoc()
                                .equals(CostantiDB.TipoUsoComponente.CONVERTITORE)) {
                            // il convertitore è il sottocomponente
                            compRecDip.setIdCompConvertitore(aroSubComp.getIdCompDoc());
                            break;
                        } else if (aroSubComp.getDecTipoCompDoc().getTiUsoCompDoc()
                                .equals(CostantiDB.TipoUsoComponente.RAPPRESENTAZIONE)) {
                            // il convertitore è il componente di tipo FILE del documento principale
                            // dell'UD riferita...
                            javax.persistence.Query queryRif = null;
                            String queryStrRif = "select comp_doc " + "from AroUnitaDoc ud "
                                    + "join ud.aroDocs doc " + "join doc.aroStrutDocs strut_doc "
                                    + "join strut_doc.aroCompDocs comp_doc " + "where "
                                    + " ud.idUnitaDoc = :idUnitaDocIn "
                                    + " and  trim(doc.tiDoc) = 'PRINCIPALE' "
                                    + " and strut_doc.flStrutOrig = '1' "
                                    + " and comp_doc.tiSupportoComp = 'FILE'";
                            queryRif = entityManager.createQuery(queryStrRif);
                            queryRif.setParameter("idUnitaDocIn",
                                    aroSubComp.getAroUnitaDoc().getIdUnitaDoc());
                            AroCompDoc acd = (AroCompDoc) queryRif.getSingleResult();
                            compRecDip.setIdCompConvertitore(acd.getIdCompDoc());
                            break;
                        }
                    }
                    /*
                     * se c'è un convertitore, verifico se sono definiti il formato file calcolato
                     * del componente versato (AroCompDoc) ed il formato file atteso dal formato di
                     * rappresentazione (DecTipoRapprComp) come file di input da convertire. Se
                     * esistono, verifico se sono uguali (possono essere diversi se è stato
                     * effettuato un versamento con forzatura di formato) Se non sono uguali, annoto
                     * il compRecDip con l'indicazione di formato errato. Questa informazione verrà
                     * usata durante la conversione: se il formato è errato, non tento
                     * l'elaborazione e segnalo l'errore.
                     */
                    if (aroCompDoc.getDecFormatoFileStandard() != null && aroCompDoc
                            .getDecTipoRapprComp().getDecFormatoFileDocCont() != null) {
                        compRecDip.setDsFormatoContReale(
                                aroCompDoc.getDecFormatoFileStandard().getNmFormatoFileStandard());
                        compRecDip.setDsFormatoContAtteso(aroCompDoc.getDecTipoRapprComp()
                                .getDecFormatoFileDocCont().getNmFormatoFileDoc());
                        //
                        javax.persistence.Query queryFmt = null;
                        String queryStrFmt = "select count(t) from DecUsoFormatoFileStandard t "
                                + "where t.decFormatoFileDoc.idFormatoFileDoc = :idFormatoFileDoc "
                                + "and t.niOrdUso = 1 "
                                + "and t.decFormatoFileStandard.idFormatoFileStandard = :idFormatoFileStandard";
                        queryFmt = entityManager.createQuery(queryStrFmt);
                        queryFmt.setParameter("idFormatoFileDoc", aroCompDoc.getDecTipoRapprComp()
                                .getDecFormatoFileDocCont().getIdFormatoFileDoc());
                        queryFmt.setParameter("idFormatoFileStandard",
                                aroCompDoc.getDecFormatoFileStandard().getIdFormatoFileStandard());
                        long numComp = (Long) queryFmt.getSingleResult();
                        if (numComp == 0) {
                            compRecDip.setErroreFormatoContenuto(true);
                        }
                    }
                }
            }
            rispostaControlli.setrObject(compRecDips);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecDip.caricaComponenti " + e.getMessage()));
            log.error("Eccezione nel caricamento componenti convertibili DIP ", e);
        }

        return rispostaControlli;
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public RispostaControlli calcolaNomeFileZip(ParametriRecupero parametri,
            Map<Long, CompRecDip> compRecDips) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        AroUnitaDoc aud;
        AroDoc ad;
        AroCompDoc ac;
        StringBuilder tmpString = new StringBuilder();
        switch (parametri.getTipoEntitaSacer()) {
        case UNI_DOC_DIP:
            aud = entityManager.find(AroUnitaDoc.class, parametri.getIdUnitaDoc());
            tmpString.append(aud.getCdRegistroKeyUnitaDoc());
            tmpString.append("-");
            tmpString.append(aud.getAaKeyUnitaDoc());
            tmpString.append("-");
            tmpString.append(aud.getCdKeyUnitaDoc());
            break;
        case DOC_DIP:
            ad = entityManager.find(AroDoc.class, parametri.getIdDocumento());
            aud = ad.getAroUnitaDoc();
            // MAC#24837
            String urnPartDocumento = (ad.getNiOrdDoc() != null)
                    ? MessaggiWSFormat.formattaUrnPartDocumento(
                            Costanti.CategoriaDocumento.Documento, ad.getNiOrdDoc().intValue(),
                            true, Costanti.UrnFormatter.DOC_FMT_STRING_V2,
                            Costanti.UrnFormatter.PAD5DIGITS_FMT)
                    : MessaggiWSFormat.formattaUrnPartDocumento(
                            Costanti.CategoriaDocumento.getEnum(ad.getTiDoc()),
                            ad.getPgDoc().intValue());
            // end MAC#24837
            tmpString.append(aud.getCdRegistroKeyUnitaDoc());
            tmpString.append("-");
            tmpString.append(aud.getAaKeyUnitaDoc());
            tmpString.append("-");
            tmpString.append(aud.getCdKeyUnitaDoc());
            tmpString.append("-");
            tmpString.append(urnPartDocumento);
            break;
        case COMP_DIP:
            ac = entityManager.find(AroCompDoc.class, parametri.getIdComponente());
            aud = ac.getAroStrutDoc().getAroDoc().getAroUnitaDoc();
            CompRecDip comp = compRecDips.get(parametri.getIdComponente());
            //
            tmpString.append(aud.getCdRegistroKeyUnitaDoc());
            tmpString.append("-");
            tmpString.append(aud.getAaKeyUnitaDoc());
            tmpString.append("-");
            tmpString.append(aud.getCdKeyUnitaDoc());
            tmpString.append("-");
            tmpString.append(comp.getNomeFileBreve());
            break;
        default:
            throw new IllegalArgumentException("Tipo entità non supportato");
        }
        rispostaControlli.setrString(MessaggiWSFormat.bonificaUrnPerNomeFile(tmpString.toString()));
        return rispostaControlli;
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public RispostaControlli cercaInsConvertitore(CompRecDip compRecDip, String hash,
            byte[] blConvertitore) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<DecTrasformTipoRappr> decTrasformTipoRapprs;

        String queryStr = null;
        javax.persistence.Query query = null;

        Map<String, Object> properties = new HashMap<>();
        properties.put("javax.persistence.lock.timeout", 25000);
        try {
            DecTipoRapprComp tmpTipoRapprComp = entityManager
                    .find(AroCompDoc.class, compRecDip.getIdCompDoc()).getDecTipoRapprComp();
            entityManager.lock(tmpTipoRapprComp, LockModeType.PESSIMISTIC_WRITE, properties);
            //
            queryStr = "select t from DecTrasformTipoRappr t "
                    + "where t.decTipoRapprComp = :decTipoRapprCompIn "
                    + " and t.dsHashFileTrasform = :dsHashFileTrasformIn ";
            query = entityManager.createQuery(queryStr);
            query.setParameter("decTipoRapprCompIn", tmpTipoRapprComp);
            query.setParameter("dsHashFileTrasformIn", hash);
            decTrasformTipoRapprs = query.getResultList();
            if (decTrasformTipoRapprs.isEmpty()) {
                // non c'è... inseriscilo in tabella e recuperane l'indice
                Date tmpDate = new Date();
                DecTrasformTipoRappr tmpTrasformTipoRappr = new DecTrasformTipoRappr();
                tmpTrasformTipoRappr.setDecTipoRapprComp(tmpTipoRapprComp);
                tmpTrasformTipoRappr.setBlFileTrasform(blConvertitore);
                tmpTrasformTipoRappr.setDsHashFileTrasform(hash);
                tmpTrasformTipoRappr.setDtInsTrasform(tmpDate);
                tmpTrasformTipoRappr.setDtLastModTrasform(tmpDate);
                tmpTrasformTipoRappr.setIdCompDocTest(new BigDecimal(compRecDip.getIdCompDoc()));
                tmpTrasformTipoRappr.setNmTrasform(NOME_DEFAULT);
                tmpTrasformTipoRappr.setTiStatoFileTrasform(StatoFileTrasform.INSERITO.name());

                // cerco l'ultima riga inserita in automatico e incremento il numero di versione
                String tmpVersione = null;
                String queryIntStr = "select t from DecTrasformTipoRappr t "
                        + "where t.decTipoRapprComp = :decTipoRapprCompIn "
                        + " and t.nmTrasform = :nmTrasformIn"
                        + " and  t.cdVersioneTrasform LIKE :cdVersioneTrasformIn "
                        + "order by t.cdVersioneTrasform DESC";
                javax.persistence.Query queryInt = entityManager.createQuery(queryIntStr);
                queryInt.setParameter("decTipoRapprCompIn", tmpTipoRapprComp);
                queryInt.setParameter("nmTrasformIn", NOME_DEFAULT);
                queryInt.setParameter("cdVersioneTrasformIn", VERSIONE_DEFAULT + "%");
                decTrasformTipoRapprs = queryInt.getResultList();
                //
                if (decTrasformTipoRapprs.isEmpty()) {
                    // non ho trovato righe che iniziano per VERSIONE
                    // creo un riga con il primo numero "paddato" su 6 cifre
                    tmpVersione = VERSIONE_DEFAULT
                            + StringUtils.leftPad("1", PAD_NUM_VERSIONE, "0");
                } else {
                    // l'ho trovata, verifico se è conforme con il pattern VERSIONEnnnnnn,
                    Pattern p = Pattern.compile(REG_EXP_VERSIONE);
                    Matcher m = p.matcher(decTrasformTipoRapprs.get(0).getCdVersioneTrasform());
                    if (m.find()) {
                        // coincide, estraggo la parte numerica, la incremento di uno e costruisco
                        // il nuovo cd versione
                        Integer tmpNumVer = Integer.parseInt(m.group(1));
                        tmpNumVer++;
                        tmpVersione = VERSIONE_DEFAULT
                                + StringUtils.leftPad(tmpNumVer.toString(), PAD_NUM_VERSIONE, "0");
                    } else {
                        // non coincide, prendo il cd versione trovato, gli accodo la stringa "-1"
                        tmpVersione = decTrasformTipoRapprs.get(0).getCdVersioneTrasform() + "-1";
                    }
                }
                tmpTrasformTipoRappr.setCdVersioneTrasform(tmpVersione);
                entityManager.persist(tmpTrasformTipoRappr);
                //
                compRecDip.setIdFileTrasform(tmpTrasformTipoRappr.getIdTrasformTipoRappr());
                compRecDip.setNomeConvertitore(NOME_DEFAULT);
                compRecDip.setVersioneConvertitore(tmpVersione);
                compRecDip.setDataUltimoAggiornamento(tmpDate);
                compRecDip.setStatoFileTrasform(StatoFileTrasform.INSERITO);
            } else {
                // è già stato inserito, recupera i dati
                DecTrasformTipoRappr tmpTrasformTipoRappr = decTrasformTipoRapprs.get(0);
                compRecDip.setIdFileTrasform(tmpTrasformTipoRappr.getIdTrasformTipoRappr());
                compRecDip.setNomeConvertitore(tmpTrasformTipoRappr.getNmTrasform());
                compRecDip.setVersioneConvertitore(tmpTrasformTipoRappr.getCdVersioneTrasform());
                compRecDip.setDataUltimoAggiornamento(tmpTrasformTipoRappr.getDtLastModTrasform());
                String tiStatoFileTrasform = tmpTrasformTipoRappr.getTiStatoFileTrasform().trim();
                compRecDip.setStatoFileTrasform(StatoFileTrasform.valueOf(tiStatoFileTrasform));
            }
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecDip.cercaInsConvertitore " + e.getMessage()));
            log.error("Eccezione nel caricamento o inserimento del convertitore ", e);
        }

        return rispostaControlli;
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public RispostaControlli caricaConvertitore(long idConvertitore) {
        //
        CompRecDip compRecDip = null;
        //
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);

        try {
            DecTrasformTipoRappr tmpTrasformTipoRappr = entityManager
                    .find(DecTrasformTipoRappr.class, idConvertitore);
            if (tmpTrasformTipoRappr != null) {
                // urn
                String urnCompleto = null;
                String urnCompletoIniz = null;
                // TODO: come mai DecTrasformTipoRappr non ha fk su AroCompDoc???
                AroCompDoc aroCompDoc = entityManager.find(AroCompDoc.class,
                        tmpTrasformTipoRappr.getIdCompDocTest());
                if (aroCompDoc != null) {
                    // urn
                    urnCompleto = unitaDocumentarieHelper
                            .findAroCompUrnCalcByType(aroCompDoc, TiUrn.NORMALIZZATO).getDsUrn();
                    // urn iniz
                    AroCompUrnCalc compUrnCalc = unitaDocumentarieHelper
                            .findAroCompUrnCalcByType(aroCompDoc, TiUrn.INIZIALE);
                    if (compUrnCalc != null) {
                        urnCompletoIniz = compUrnCalc.getDsUrn();
                    }
                }
                //
                compRecDip = new CompRecDip(urnCompleto, urnCompletoIniz);
                compRecDip.setIdFileTrasform(idConvertitore);

                compRecDip.setIdCompDoc(tmpTrasformTipoRappr.getIdCompDocTest().longValue());
                //
                compRecDip.setTipoAlgoritmoRappresentazione(CostantiDB.TipoAlgoritmoRappr
                        .fromString(tmpTrasformTipoRappr.getDecTipoRapprComp().getTiAlgoRappr()));
                if (tmpTrasformTipoRappr.getDecTipoRapprComp()
                        .getDecFormatoFileStandard() != null) {
                    compRecDip.setEstensioneFile(tmpTrasformTipoRappr.getDecTipoRapprComp()
                            .getDecFormatoFileStandard().getNmFormatoFileStandard());
                    compRecDip.setMimeType(tmpTrasformTipoRappr.getDecTipoRapprComp()
                            .getDecFormatoFileStandard().getNmMimetypeFile());
                } else {
                    compRecDip.setEstensioneFile("_"); // in caso non fosse stato specificato un
                    // formato di output in
                    // fase di configurazione
                    compRecDip.setMimeType("application/octet-stream");
                }
                rispostaControlli.setrObject(compRecDip);
                rispostaControlli.setrBoolean(true);
            } else {
                rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
                rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                        "Errore: ControlliRecDip.caricaCanvertitore: "
                                + "il convertitore non esiste."));
            }
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecDip.caricaCanvertitore " + e.getMessage()));
            log.error("Eccezione nel caricamento del convertitore ", e);
        }

        return rispostaControlli;
    }

    public RispostaControlli estraiImmaginiConv(long idConvertitore, String rootImgDip) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        //
        Map<String, Object> properties = new HashMap<>();
        properties.put("javax.persistence.lock.timeout", 25000);

        try {
            DecTrasformTipoRappr dttr = entityManager.find(DecTrasformTipoRappr.class,
                    idConvertitore, LockModeType.PESSIMISTIC_WRITE, properties);
            if (!dttr.getDecImageTrasforms().isEmpty()) {
                OrgStrut tmpStrut = dttr.getDecTipoRapprComp().getOrgStrut();
                CSVersatore vers = new CSVersatore();
                vers.setStruttura(tmpStrut.getNmStrut());
                vers.setEnte(tmpStrut.getOrgEnte().getNmEnte());
                vers.setAmbiente(tmpStrut.getOrgEnte().getOrgAmbiente().getNmAmbiente());
                String pathVersatore = rootImgDip + "/"
                        + MessaggiWSFormat.formattaSubPathVersatoreArk(vers);
                //
                // verifica se esiste e crea pathVersatore
                File fileRoot = new File(pathVersatore);
                if (!fileRoot.isDirectory()) {
                    fileRoot.mkdirs();
                }
                //
                // verifica se esistono e nel caso crea le immagini
                for (DecImageTrasform dit : dttr.getDecImageTrasforms()) {
                    File fileImg = new File(fileRoot,
                            dttr.getDecTipoRapprComp().getNmTipoRapprComp() + "-"
                                    + dttr.getNmTrasform() + "-" + dttr.getCdVersioneTrasform()
                                    + "-" + dit.getNmImageTrasform());
                    // l'immagine viene salvata se:
                    // non risulta mai stata salvata oppure
                    // non esiste sul disco oppure
                    // la sua data salvataggio è precedente l'ultima modifica
                    if (dit.getDtLastScaricoImageTrasform() == null || (!fileImg.exists())
                            || (fileImg.lastModified() < dit.getDtLastModImageTrasform()
                                    .getTime())) {
                        try (FileOutputStream fop = new FileOutputStream(fileImg)) {
                            fop.write(dit.getBlImageTrasform());
                        }
                        dit.setDtLastScaricoImageTrasform(new Date());
                    }
                }
            }
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecDip.estraiImmaginiConv " + e.getMessage()));
            log.error("Eccezione nell'estrazione dell'immagine per conversione DIP", e);
        }

        return rispostaControlli;
    }

    public RispostaControlli convertiXml(ICompTransformer iTransformer, CompRecDip comp,
            OutputStream out) {
        RispostaControlli risposta;
        risposta = new RispostaControlli();
        risposta.setrBoolean(false);
        //
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            ParametriTrasf parametriTrasf = new ParametriTrasf();

            // recupero documento blob vs object storage
            // build dto per recupero
            RecuperoDocBean csRecuperoDoc = new RecuperoDocBean(TiEntitaSacerObjectStorage.COMP_DOC,
                    comp.getIdCompDoc(), bos, RecBlbOracle.TabellaBlob.BLOB);
            // recupero
            boolean esitoRecupero = recuperoDocumento.callRecuperoDocSuStream(csRecuperoDoc);
            risposta.setrBoolean(esitoRecupero);
            if (!esitoRecupero) {
                throw new ParerInternalError("Errore non gestito nel recupero del file");
            }
            parametriTrasf.setFileXml(new BOMInputStream.Builder()
                    .setInputStream(new ByteArrayInputStream(bos.toByteArray())).get());

            //
            DecTrasformTipoRappr tmpTrasformTipoRappr = entityManager
                    .find(DecTrasformTipoRappr.class, comp.getIdFileTrasform());
            parametriTrasf.setFileXslt(new BOMInputStream.Builder()
                    .setInputStream(
                            new ByteArrayInputStream(tmpTrasformTipoRappr.getBlFileTrasform()))
                    .get());
            //

            ResponseEntity<String> response = null;

            if (comp.getDsFormatoContAtteso().equals("XML.P7M")) {

                // headers
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);
                headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
                // body
                File xmlp7m = File.createTempFile("xmlp7m_", ".xml.p7m",
                        new File(System.getProperty("java.io.tmpdir")));
                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                FileUtils.copyInputStreamToFile(parametriTrasf.getFileXml(), xmlp7m);
                body.add("xml-p7m", new FileSystemResource(xmlp7m));

                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body,
                        headers);

                String serverUrl = cryptoRestConfiguratorHelper.endPoints().get(0)
                        + P7MEXTRACTOR_CONTEXT;

                RestTemplate restTemplate = new RestTemplate();
                HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
                int timeout = 300000;
                clientHttpRequestFactory.setReadTimeout(timeout);
                clientHttpRequestFactory.setConnectTimeout(timeout);
                clientHttpRequestFactory.setConnectionRequestTimeout(timeout);

                restTemplate.setRequestFactory(clientHttpRequestFactory);
                response = restTemplate.postForEntity(serverUrl, requestEntity, String.class);
                parametriTrasf.setFileXml(new ByteArrayInputStream(response.getBody().getBytes()));
                // parametriTrasf.getFileXml().reset();
            }

            parametriTrasf.setFileXmlOut(out);
            //
            RispostaControlli rispostaControlli = iTransformer.convertiSuStream(parametriTrasf);
            if (rispostaControlli.isrBoolean()) {
                risposta.setrBoolean(true);
                tmpTrasformTipoRappr.setTiStatoFileTrasform(StatoFileTrasform.VERIFICATO.name());
                comp.setSeverity(IRispostaWS.SeverityEnum.OK);
                comp.setErrorMessage(null);
            } else {
                /*
                 * se coderr è valorizzato, è un errore applicativo grave (di norma è 666) se coderr
                 * NON è valorizzato è un errore di conversione del componente e va riportato sulla
                 * tabella DecTrasformTipoRappr come ERRATO
                 */
                if (rispostaControlli.getCodErr() == null) {
                    tmpTrasformTipoRappr.setTiStatoFileTrasform(StatoFileTrasform.ERRATO.name());
                    comp.setStatoFileTrasform(StatoFileTrasform.ERRATO);
                }
                comp.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                comp.setErrorMessage(rispostaControlli.getDsErr());
                risposta.setCodErr(rispostaControlli.getCodErr());
                risposta.setDsErr(rispostaControlli.getDsErr());
            }
        } catch (Exception e) {
            risposta.setCodErr(MessaggiWSBundle.ERR_666);
            risposta.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliRecDip.convertiXmlConXslt " + e.getMessage()));
            log.error("Eccezione nella conversione del componente ", e);
        }

        return risposta;
    }

}
