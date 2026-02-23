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

package it.eng.parer.util.helper;

import static it.eng.parer.util.Utils.bigDecimalFromLong;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;

import it.eng.parer.entity.AroCompDoc;
import it.eng.parer.entity.AroCompUrnCalc;
import it.eng.parer.entity.AroDoc;
import it.eng.parer.entity.AroStrutDoc;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.AroUpdUnitaDoc;
import it.eng.parer.entity.AroUrnVerIndiceAipUd;
import it.eng.parer.entity.AroVerIndiceAipUd;
import it.eng.parer.entity.AroXmlUpdUnitaDoc;
import it.eng.parer.entity.ElvElencoVer;
import it.eng.parer.entity.ElvFileElencoVer;
import it.eng.parer.entity.ElvUrnElencoVers;
import it.eng.parer.entity.ElvUrnFileElencoVers;
import it.eng.parer.entity.SerIxVolVerSerie;
import it.eng.parer.entity.SerUrnIxVolVerSerie;
import it.eng.parer.entity.VrsUrnXmlSessioneVers;
import it.eng.parer.entity.VrsXmlDatiSessioneVers;
import it.eng.parer.entity.constraint.AroCompUrnCalc.TiUrn;
import it.eng.parer.entity.constraint.AroUrnVerIndiceAipUd.TiUrnVerIxAipUd;
import it.eng.parer.entity.constraint.ElvUrnElencoVers.TiUrnElenco;
import it.eng.parer.entity.constraint.ElvUrnFileElencoVers.TiUrnFileElenco;
import it.eng.parer.entity.constraint.SerUrnIxVolVerSerie.TiUrnIxVolVerSerie;
import it.eng.parer.entity.constraint.VrsUrnXmlSessioneVers.TiUrnXmlSessioneVers;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.viewEntity.AroVLisaipudUrndacalcByud;
import it.eng.parer.viewEntity.VrsVLisXmlDocUrnDaCalc;
import it.eng.parer.viewEntity.VrsVLisXmlUdUrnDaCalc;
import it.eng.parer.viewEntity.VrsVLisXmlUpdUrnDaCalc;
import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.MessaggiWSFormat;

/**
 *
 * @author DiLorenzo_F
 */
@SuppressWarnings({
        "unchecked" })
@Stateless(mappedName = "UniformResourceNameUtilHelper")
@LocalBean
@Interceptors({
        it.eng.parer.aop.TransactionInterceptor.class })
public class UniformResourceNameUtilHelper extends GenericHelper {

    public boolean existsCdKeyNormalized(long idRegistro, BigDecimal aaKeyUnitaDoc,
            String cdKeyUnitaDoc, String cdKeyUnitaDocNormaliz) {
        String queryStr = "select count(ud) from AroUnitaDoc ud "
                + "where ud.decRegistroUnitaDoc.idRegistroUnitaDoc = :idRegistro "
                + " and ud.aaKeyUnitaDoc = :aaKeyUnitaDoc "
                + " and ud.cdKeyUnitaDoc != :cdKeyUnitaDoc "
                + " and ud.cdKeyUnitaDocNormaliz = :cdKeyUnitaDocNormaliz ";

        javax.persistence.Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idRegistro", idRegistro);
        query.setParameter("aaKeyUnitaDoc", aaKeyUnitaDoc);
        query.setParameter("cdKeyUnitaDoc", cdKeyUnitaDoc);
        query.setParameter("cdKeyUnitaDocNormaliz", cdKeyUnitaDocNormaliz);

        return (Long) query.getSingleResult() > 0;
    }

    public void scriviUrnCompPreg(AroUnitaDoc tmpAroUnitaDoc, CSVersatore versatore,
            CSChiave chiave) {
        // calcolo (se necessario) niOrdDoc secondo la logica di ordinamento
        this.getAndSetAroDocOrderedByTypeAndDateProg(tmpAroUnitaDoc);
        // per ogni documento
        for (AroDoc tmpAroDoc : tmpAroUnitaDoc.getAroDocs()) {
            // per ogni componente
            for (Iterator<AroStrutDoc> it = tmpAroDoc.getAroStrutDocs().iterator(); it.hasNext();) {
                AroStrutDoc tmpAroStrutDoc = it.next();
                for (AroCompDoc tmpAroCompDoc : tmpAroStrutDoc.getAroCompDocs()) {
                    //
                    this.salvaURNComponente(versatore, chiave, tmpAroDoc, tmpAroCompDoc,
                            Arrays.asList(TiUrn.ORIGINALE, TiUrn.NORMALIZZATO, TiUrn.INIZIALE));
                }
            }
        }
    }

    /*
     * Data una UD torna la lista dei suoi AroDoc ordinati per: - Tipo documento principale e poi
     * tutti gli altri - Tutti gli altri ordinati per Data Creazione e nella stessa data per tipo
     * documento e nello stesso tipo per progressivo
     */
    private void getAndSetAroDocOrderedByTypeAndDateProg(AroUnitaDoc aroUnitaDoc) {
        BigDecimal prog = BigDecimal.ONE;
        // recupero documenti
        List<AroDoc> listaDoc = loadAroDocs(aroUnitaDoc);
        ArrayList<AroDoc> alDef = null;
        if (listaDoc != null) {
            AroDoc aroDocPrinc = null;
            ArrayList<AroDoc> alNew = new ArrayList<>();
            for (AroDoc aroDoc : listaDoc) {
                if (aroDoc.getTiDoc()
                        .equals(Costanti.CategoriaDocumento.Principale.getValoreDb())) {
                    aroDocPrinc = aroDoc; // memorizza per dopo il doc PRINCIPALE
                } else {
                    alNew.add(aroDoc);
                }
            }
            // Ordina gli elementi tranne il PRINCIPALE...
            Collections.sort(alNew, (doc1, doc2) -> {
                int comparazionePerData = doc1.getDtCreazione().compareTo(doc2.getDtCreazione());
                if (comparazionePerData == 0) {
                    int comparazionePerTipo = doc1.getTiDoc().compareTo(doc2.getTiDoc());
                    if (comparazionePerTipo == 0) {
                        return doc1.getPgDoc().compareTo(doc2.getPgDoc());
                    } else {
                        return comparazionePerTipo;
                    }
                } else {
                    return comparazionePerData;
                }
            });
            // PRINCIPALE FIRST
            alDef = new ArrayList<>();
            if (aroDocPrinc != null) {
                alDef.add(aroDocPrinc);
            }
            for (AroDoc aroDocZ : alNew) {
                alDef.add(aroDocZ);
            }
            // E poi tutti gli altri già ordinati di seguito
            for (AroDoc aroDocx : alDef) {
                // assegno solo se non presente
                if (aroDocx.getNiOrdDoc() == null) {
                    aroDocx.setNiOrdDoc(prog);

                    getEntityManager().merge(aroDocx);
                }
                // incremento
                prog = prog.add(BigDecimal.ONE);
            }
        }
    }

    private List<AroDoc> loadAroDocs(AroUnitaDoc aroUnitaDoc) {
        final TypedQuery<AroDoc> query = getEntityManager().createQuery(
                "SELECT a FROM AroDoc a WHERE a.aroUnitaDoc=:aroUnitaDoc", AroDoc.class);
        query.setParameter("aroUnitaDoc", aroUnitaDoc);
        aroUnitaDoc.setAroDocs(query.getResultList());
        return aroUnitaDoc.getAroDocs();
    }

    private void salvaURNComponente(CSVersatore versatore, CSChiave chiave, AroDoc tmpAroDoc,
            AroCompDoc tmpAroCompDoc, List<TiUrn> tiUrnToCalculate) {
        // for each tiUrn
        for (TiUrn tmpTiUrn : tiUrnToCalculate) {
            String tmpUrnDoc = null;
            // find with that TiUrn
            long count = new ArrayList<>(tmpAroCompDoc.getAroAroCompUrnCalcs()).stream()
                    .filter(c -> c.getTiUrn().equals(tmpTiUrn)).count();
            // se non esiste
            if (count == 0) {
                // DOCXXXXXX
                String tmpUrnPartDoc = MessaggiWSFormat.formattaUrnPartDocumento(
                        Costanti.CategoriaDocumento.Documento, tmpAroDoc.getNiOrdDoc().intValue(),
                        true, Costanti.UrnFormatter.DOC_FMT_STRING_V2,
                        Costanti.UrnFormatter.PAD5DIGITS_FMT);
                if (tmpAroCompDoc.getAroCompDoc() != null) {
                    // E' UN SOTTOCOMPONENTE
                    // DOCXXXXX:NNNNN
                    tmpUrnDoc = MessaggiWSFormat.formattaUrnPartComponente(tmpUrnPartDoc,
                            tmpAroCompDoc.getAroCompDoc().getNiOrdCompDoc().intValue(),
                            Costanti.UrnFormatter.COMP_FMT_STRING_V2,
                            Costanti.UrnFormatter.PAD5DIGITS_FMT);
                    // DOCXXXXX:NNNNN:KK
                    tmpUrnDoc = MessaggiWSFormat.formattaUrnPartComponente(tmpUrnDoc,
                            tmpAroCompDoc.getNiOrdCompDoc().intValue(),
                            Costanti.UrnFormatter.COMP_FMT_STRING_V2,
                            Costanti.UrnFormatter.PAD5DIGITS_FMT);
                } else {
                    // DOCXXXXX:NNNNN
                    tmpUrnDoc = MessaggiWSFormat.formattaUrnPartComponente(tmpUrnPartDoc,
                            tmpAroCompDoc.getNiOrdCompDoc().intValue(),
                            Costanti.UrnFormatter.COMP_FMT_STRING_V2,
                            Costanti.UrnFormatter.PAD5DIGITS_FMT);
                }
                // calculate urn
                String tmpUrn = null;
                switch (tmpTiUrn) {
                case ORIGINALE:
                    String urnPartVersatore = MessaggiWSFormat.formattaUrnPartVersatore(versatore);
                    String urnPartChiaveUd = MessaggiWSFormat.formattaUrnPartUnitaDoc(chiave);
                    tmpUrn = MessaggiWSFormat.formattaBaseUrnDoc(urnPartVersatore, urnPartChiaveUd,
                            tmpUrnDoc, Costanti.UrnFormatter.URN_COMP_FMT_STRING);
                    break;
                case NORMALIZZATO:
                    String urnPartVersatoreNorm = MessaggiWSFormat.formattaUrnPartVersatore(
                            versatore, true, Costanti.UrnFormatter.VERS_FMT_STRING);
                    String urnPartChiaveUdNorm = MessaggiWSFormat.formattaUrnPartUnitaDoc(chiave,
                            true, Costanti.UrnFormatter.UD_FMT_STRING);
                    tmpUrn = MessaggiWSFormat.formattaBaseUrnDoc(urnPartVersatoreNorm,
                            urnPartChiaveUdNorm, tmpUrnDoc,
                            Costanti.UrnFormatter.URN_COMP_FMT_STRING);
                    break;
                default:
                    tmpUrn = tmpAroCompDoc.getDsUrnCompCalc();
                    break;
                }

                if (StringUtils.isNotBlank(tmpUrn)) {
                    this.salvaCompUrnCalc(tmpAroCompDoc, tmpUrn, tmpTiUrn);
                }
            }
        }
    }

    private void salvaCompUrnCalc(AroCompDoc aroCompDoc, String tmpUrn, TiUrn tiUrn) {

        AroCompUrnCalc tmpTabCDUrnComponenteCalc = new AroCompUrnCalc();
        tmpTabCDUrnComponenteCalc.setAroCompDoc(aroCompDoc);
        tmpTabCDUrnComponenteCalc.setDsUrn(tmpUrn);
        tmpTabCDUrnComponenteCalc.setTiUrn(tiUrn);

        // persist
        getEntityManager().persist(tmpTabCDUrnComponenteCalc);

        if (aroCompDoc.getAroAroCompUrnCalcs() == null) {
            aroCompDoc.setAroAroCompUrnCalcs(new ArrayList<>());
        }
        aroCompDoc.getAroAroCompUrnCalcs().add(tmpTabCDUrnComponenteCalc);
    }

    public void scriviUrnSipUdPreg(AroUnitaDoc tmpAroUnitaDoc, CSVersatore versatore,
            CSChiave chiave) {

        List<VrsVLisXmlUdUrnDaCalc> vrsVLisXmlUdUrnDaCalcs = this
                .retrieveVrsVLisXmlUdUrnDaCalcByUd(tmpAroUnitaDoc.getIdUnitaDoc());

        for (VrsVLisXmlUdUrnDaCalc vrs : vrsVLisXmlUdUrnDaCalcs) {

            //
            VrsXmlDatiSessioneVers xmlDatiSessioneVers = getEntityManager()
                    .find(VrsXmlDatiSessioneVers.class, vrs.getIdXmlDatiSessioneVers().longValue());

            // calcolo parte urn ORIGINALE
            String tmpUrn = MessaggiWSFormat.formattaBaseUrnUnitaDoc(
                    MessaggiWSFormat.formattaUrnPartVersatore(versatore),
                    MessaggiWSFormat.formattaUrnPartUnitaDoc(chiave));

            // calcolo parte urn NORMALIZZATO
            String tmpUrnNorm = MessaggiWSFormat.formattaBaseUrnUnitaDoc(
                    MessaggiWSFormat.formattaUrnPartVersatore(versatore, true,
                            Costanti.UrnFormatter.VERS_FMT_STRING),
                    MessaggiWSFormat.formattaUrnPartUnitaDoc(chiave, true,
                            Costanti.UrnFormatter.UD_FMT_STRING));
            switch (vrs.getTiXmlDati()) {
            case CostantiDB.TipiXmlDati.RICHIESTA:
                // salvo ORIGINALE
                this.salvaUrnXmlSessioneVers(xmlDatiSessioneVers,
                        MessaggiWSFormat.formattaUrnIndiceSip(tmpUrn,
                                Costanti.UrnFormatter.URN_INDICE_SIP_V2),
                        TiUrnXmlSessioneVers.ORIGINALE, vrs.getAaChiusuraSess());

                // salvo NORMALIZZATO
                this.salvaUrnXmlSessioneVers(xmlDatiSessioneVers,
                        MessaggiWSFormat.formattaUrnIndiceSip(tmpUrnNorm,
                                Costanti.UrnFormatter.URN_INDICE_SIP_V2),
                        TiUrnXmlSessioneVers.NORMALIZZATO, vrs.getAaChiusuraSess());
                break;
            case CostantiDB.TipiXmlDati.RISPOSTA:
                // salvo ORIGINALE
                this.salvaUrnXmlSessioneVers(xmlDatiSessioneVers,
                        MessaggiWSFormat.formattaUrnEsitoVers(tmpUrn,
                                Costanti.UrnFormatter.URN_ESITO_VERS_V2),
                        TiUrnXmlSessioneVers.ORIGINALE, vrs.getAaChiusuraSess());

                // salvo NORMALIZZATO
                this.salvaUrnXmlSessioneVers(xmlDatiSessioneVers,
                        MessaggiWSFormat.formattaUrnEsitoVers(tmpUrnNorm,
                                Costanti.UrnFormatter.URN_ESITO_VERS_V2),
                        TiUrnXmlSessioneVers.NORMALIZZATO, vrs.getAaChiusuraSess());
                break;
            case CostantiDB.TipiXmlDati.RAPP_VERS:
                // salvo ORIGINALE
                this.salvaUrnXmlSessioneVers(xmlDatiSessioneVers,
                        MessaggiWSFormat.formattaUrnRappVers(tmpUrn,
                                Costanti.UrnFormatter.URN_RAPP_VERS_V2),
                        TiUrnXmlSessioneVers.ORIGINALE, vrs.getAaChiusuraSess());

                // salvo NORMALIZZATO
                this.salvaUrnXmlSessioneVers(xmlDatiSessioneVers,
                        MessaggiWSFormat.formattaUrnRappVers(tmpUrnNorm,
                                Costanti.UrnFormatter.URN_RAPP_VERS_V2),
                        TiUrnXmlSessioneVers.NORMALIZZATO, vrs.getAaChiusuraSess());
                break;
            case CostantiDB.TipiXmlDati.INDICE_FILE:
                // salvo ORIGINALE
                this.salvaUrnXmlSessioneVers(xmlDatiSessioneVers,
                        MessaggiWSFormat.formattaUrnPiSip(tmpUrn,
                                Costanti.UrnFormatter.URN_PI_SIP_V2),
                        TiUrnXmlSessioneVers.ORIGINALE, vrs.getAaChiusuraSess());

                // salvo NORMALIZZATO
                this.salvaUrnXmlSessioneVers(xmlDatiSessioneVers,
                        MessaggiWSFormat.formattaUrnPiSip(tmpUrnNorm,
                                Costanti.UrnFormatter.URN_PI_SIP_V2),
                        TiUrnXmlSessioneVers.NORMALIZZATO, vrs.getAaChiusuraSess());
                break;
            default:
                break;
            }
            // salvo INIZIALE
            String dsUrnXmlVers = StringUtils.isNotBlank(xmlDatiSessioneVers.getDsUrnXmlVers())
                    ? xmlDatiSessioneVers.getDsUrnXmlVers()
                    : "Non disponibile";
            this.salvaUrnXmlSessioneVers(xmlDatiSessioneVers, dsUrnXmlVers,
                    TiUrnXmlSessioneVers.INIZIALE, vrs.getAaChiusuraSess());
        }
    }

    private List<VrsVLisXmlUdUrnDaCalc> retrieveVrsVLisXmlUdUrnDaCalcByUd(long idUnitaDoc) {
        Query query = getEntityManager().createQuery(
                "SELECT vrs FROM VrsVLisXmlUdUrnDaCalc vrs WHERE vrs.idUnitaDoc = :idUnitaDoc ");
        query.setParameter("idUnitaDoc", bigDecimalFromLong(idUnitaDoc));
        return query.getResultList();
    }

    public void scriviUrnSipDocAggPreg(AroUnitaDoc tmpAroUnitaDoc, CSVersatore versatore,
            CSChiave chiave) {
        // per ogni documento aggiunto
        for (AroDoc aroDoc : tmpAroUnitaDoc.getAroDocs()) {
            List<VrsVLisXmlDocUrnDaCalc> vrsVLisXmlDocUrnDaCalc = this
                    .retrieveVrsVLisXmlDocUrnDaCalcByDoc(aroDoc.getIdDoc());
            // per ogni vrsVLisXmlDocUrnDaCalc
            for (VrsVLisXmlDocUrnDaCalc vrs : vrsVLisXmlDocUrnDaCalc) {
                //
                VrsXmlDatiSessioneVers xmlDatiSessioneVers = getEntityManager().find(
                        VrsXmlDatiSessioneVers.class, vrs.getIdXmlDatiSessioneVers().longValue());

                // calcolo parte urn ORIGINALE
                // DOCXXXXXX
                String tmpUrnPartDoc = MessaggiWSFormat.formattaUrnPartDocumento(
                        Costanti.CategoriaDocumento.Documento, aroDoc.getNiOrdDoc().intValue(),
                        true, Costanti.UrnFormatter.DOC_FMT_STRING_V2,
                        Costanti.UrnFormatter.PAD5DIGITS_FMT);

                String tmpUrn = MessaggiWSFormat.formattaBaseUrnDoc(
                        MessaggiWSFormat.formattaUrnPartVersatore(versatore),
                        MessaggiWSFormat.formattaUrnPartUnitaDoc(chiave), tmpUrnPartDoc,
                        Costanti.UrnFormatter.URN_DOC_FMT_STRING_V2);

                // calcolo urn NORMALIZZATO
                String tmpUrnNorm = MessaggiWSFormat.formattaBaseUrnDoc(
                        MessaggiWSFormat.formattaUrnPartVersatore(versatore, true,
                                Costanti.UrnFormatter.VERS_FMT_STRING),
                        MessaggiWSFormat.formattaUrnPartUnitaDoc(chiave, true,
                                Costanti.UrnFormatter.UD_FMT_STRING),
                        tmpUrnPartDoc, Costanti.UrnFormatter.URN_DOC_FMT_STRING_V2);
                switch (vrs.getTiXmlDati()) {
                case CostantiDB.TipiXmlDati.RICHIESTA:
                    // salvo ORIGINALE
                    this.salvaUrnXmlSessioneVers(xmlDatiSessioneVers,
                            MessaggiWSFormat.formattaUrnIndiceSip(tmpUrn,
                                    Costanti.UrnFormatter.URN_INDICE_SIP_V2),
                            TiUrnXmlSessioneVers.ORIGINALE, vrs.getAaChiusuraSess());

                    // salvo NORMALIZZATO
                    this.salvaUrnXmlSessioneVers(xmlDatiSessioneVers,
                            MessaggiWSFormat.formattaUrnIndiceSip(tmpUrnNorm,
                                    Costanti.UrnFormatter.URN_INDICE_SIP_V2),
                            TiUrnXmlSessioneVers.NORMALIZZATO, vrs.getAaChiusuraSess());
                    break;
                case CostantiDB.TipiXmlDati.RISPOSTA:
                    // salvo ORIGINALE
                    this.salvaUrnXmlSessioneVers(xmlDatiSessioneVers,
                            MessaggiWSFormat.formattaUrnEsitoVers(tmpUrn,
                                    Costanti.UrnFormatter.URN_ESITO_VERS_V2),
                            TiUrnXmlSessioneVers.ORIGINALE, vrs.getAaChiusuraSess());

                    // salvo NORMALIZZATO
                    this.salvaUrnXmlSessioneVers(xmlDatiSessioneVers,
                            MessaggiWSFormat.formattaUrnEsitoVers(tmpUrnNorm,
                                    Costanti.UrnFormatter.URN_ESITO_VERS_V2),
                            TiUrnXmlSessioneVers.NORMALIZZATO, vrs.getAaChiusuraSess());
                    break;
                case CostantiDB.TipiXmlDati.RAPP_VERS:
                    // salvo ORIGINALE
                    this.salvaUrnXmlSessioneVers(xmlDatiSessioneVers,
                            MessaggiWSFormat.formattaUrnRappVers(tmpUrn,
                                    Costanti.UrnFormatter.URN_RAPP_VERS_V2),
                            TiUrnXmlSessioneVers.ORIGINALE, vrs.getAaChiusuraSess());

                    // salvo NORMALIZZATO
                    this.salvaUrnXmlSessioneVers(xmlDatiSessioneVers,
                            MessaggiWSFormat.formattaUrnRappVers(tmpUrnNorm,
                                    Costanti.UrnFormatter.URN_RAPP_VERS_V2),
                            TiUrnXmlSessioneVers.NORMALIZZATO, vrs.getAaChiusuraSess());
                    break;
                default:
                    break;
                }
                // salvo INIZIALE
                String dsUrnXmlVers = StringUtils.isNotBlank(xmlDatiSessioneVers.getDsUrnXmlVers())
                        ? xmlDatiSessioneVers.getDsUrnXmlVers()
                        : "Non disponibile";
                this.salvaUrnXmlSessioneVers(xmlDatiSessioneVers, dsUrnXmlVers,
                        TiUrnXmlSessioneVers.INIZIALE, vrs.getAaChiusuraSess());
            }
        }
    }

    private List<VrsVLisXmlDocUrnDaCalc> retrieveVrsVLisXmlDocUrnDaCalcByDoc(long idDoc) {
        Query query = getEntityManager().createQuery(
                "SELECT vrs FROM VrsVLisXmlDocUrnDaCalc vrs WHERE vrs.idDoc = :idDoc ");
        query.setParameter("idDoc", bigDecimalFromLong(idDoc));
        return query.getResultList();
    }

    public void scriviUrnSipUpdPreg(AroUnitaDoc tmpAroUnitaDoc, CSVersatore versatore,
            CSChiave chiave) {
        //
        List<AroUpdUnitaDoc> aroUpdUnitaDocs = retrieveAroUpdUnitaDocByUd(
                tmpAroUnitaDoc.getIdUnitaDoc());
        // per ogni aggiornamento metadati
        for (AroUpdUnitaDoc updUnitaDoc : aroUpdUnitaDocs) {
            //
            List<VrsVLisXmlUpdUrnDaCalc> vrsVLisXmlUpdUrnDaCalc = retrieveVrsVLisXmlUpdUrnDaCalcByUpd(
                    updUnitaDoc.getIdUpdUnitaDoc());
            // per ogni VrsVLisXmlUpdUrnDaCalc
            for (VrsVLisXmlUpdUrnDaCalc vrs : vrsVLisXmlUpdUrnDaCalc) {
                AroXmlUpdUnitaDoc aroXmlUpdUnitaDoc = getEntityManager()
                        .find(AroXmlUpdUnitaDoc.class, vrs.getIdXmlUpdUnitaDoc().longValue());

                // calcolo parte urn ORIGINALE
                String tmpUrn = MessaggiWSFormat.formattaBaseUrnUpdUnitaDoc(
                        MessaggiWSFormat.formattaUrnPartVersatore(versatore),
                        MessaggiWSFormat.formattaUrnPartUnitaDoc(chiave),
                        updUnitaDoc.getPgUpdUnitaDoc().longValue(), true,
                        Costanti.UrnFormatter.UPD_FMT_STRING_V3,
                        Costanti.UrnFormatter.PAD5DIGITS_FMT);

                // calcolo parte urn NORMALIZZATO
                String tmpUrnNorm = MessaggiWSFormat.formattaBaseUrnUpdUnitaDoc(
                        MessaggiWSFormat.formattaUrnPartVersatore(versatore, true,
                                Costanti.UrnFormatter.VERS_FMT_STRING),
                        MessaggiWSFormat.formattaUrnPartUnitaDoc(chiave, true,
                                Costanti.UrnFormatter.UD_FMT_STRING),
                        updUnitaDoc.getPgUpdUnitaDoc().longValue(), true,
                        Costanti.UrnFormatter.UPD_FMT_STRING_V3,
                        Costanti.UrnFormatter.PAD5DIGITS_FMT);

                switch (vrs.getTiXmlUpdUnitaDoc()) {
                case CostantiDB.TipiXmlDati.RICHIESTA:
                    // salvo ORIGINALE
                    aroXmlUpdUnitaDoc.setDsUrnXml(MessaggiWSFormat.formattaUrnIndiceSipUpd(tmpUrn,
                            Costanti.UrnFormatter.URN_INDICE_SIP_V2));
                    // salvo NORMALIZZATO
                    aroXmlUpdUnitaDoc.setDsUrnNormalizXml(MessaggiWSFormat.formattaUrnIndiceSipUpd(
                            tmpUrnNorm, Costanti.UrnFormatter.URN_INDICE_SIP_V2));
                    break;
                case CostantiDB.TipiXmlDati.RISPOSTA:
                    // salvo ORIGINALE
                    aroXmlUpdUnitaDoc.setDsUrnXml(MessaggiWSFormat.formattaUrnPartRappVersUpd(
                            tmpUrn, Costanti.UrnFormatter.URN_RAPP_VERS_V2));
                    // salvo NORMALIZZATO
                    aroXmlUpdUnitaDoc.setDsUrnNormalizXml(
                            MessaggiWSFormat.formattaUrnPartRappVersUpd(tmpUrnNorm,
                                    Costanti.UrnFormatter.URN_RAPP_VERS_V2));
                    break;
                default:
                    break;
                }
            }
        }
    }

    private List<AroUpdUnitaDoc> retrieveAroUpdUnitaDocByUd(long idUnitaDoc) {
        Query query = getEntityManager().createQuery(
                "SELECT upd FROM AroUpdUnitaDoc upd WHERE upd.aroUnitaDoc.idUnitaDoc = :idUnitaDoc ");
        query.setParameter("idUnitaDoc", idUnitaDoc);
        return query.getResultList();
    }

    private List<VrsVLisXmlUpdUrnDaCalc> retrieveVrsVLisXmlUpdUrnDaCalcByUpd(long idUpdUnitaDoc) {
        Query query = getEntityManager().createQuery(
                "SELECT vrs FROM VrsVLisXmlUpdUrnDaCalc vrs WHERE vrs.idUpdUnitaDoc = :idUpdUnitaDoc ");
        query.setParameter("idUpdUnitaDoc", bigDecimalFromLong(idUpdUnitaDoc));
        return query.getResultList();
    }

    private void salvaUrnXmlSessioneVers(VrsXmlDatiSessioneVers xmlDatiSessioneVers, String tmpUrn,
            TiUrnXmlSessioneVers tiUrn, BigDecimal aaChiusuraSess) {

        VrsUrnXmlSessioneVers tmpVrsUrnXmlSessioneVers = new VrsUrnXmlSessioneVers();
        tmpVrsUrnXmlSessioneVers.setDsUrn(tmpUrn);
        tmpVrsUrnXmlSessioneVers.setTiUrn(tiUrn);
        tmpVrsUrnXmlSessioneVers.setIdStrut(xmlDatiSessioneVers.getIdStrut());
        tmpVrsUrnXmlSessioneVers.setAaChiusuraSess(aaChiusuraSess);
        tmpVrsUrnXmlSessioneVers.setVrsXmlDatiSessioneVers(xmlDatiSessioneVers);

        // persist
        getEntityManager().persist(tmpVrsUrnXmlSessioneVers);

        if (xmlDatiSessioneVers.getVrsUrnXmlSessioneVers() == null) {
            xmlDatiSessioneVers.setVrsUrnXmlSessioneVers(new ArrayList<>());
        }
        xmlDatiSessioneVers.getVrsUrnXmlSessioneVers().add(tmpVrsUrnXmlSessioneVers);
    }

    public void scriviUrnAipUdPreg(AroUnitaDoc tmpAroUnitaDoc, CSVersatore versatore,
            CSChiave chiave) {

        List<AroVLisaipudUrndacalcByud> aroVLisaipudUrndacalcByuds = this
                .retrieveAroVLisaipudUrndacalcByud(tmpAroUnitaDoc.getIdUnitaDoc());

        for (AroVLisaipudUrndacalcByud aro : aroVLisaipudUrndacalcByuds) {

            //
            AroVerIndiceAipUd verIndiceAipUd = getEntityManager().find(AroVerIndiceAipUd.class,
                    aro.getId().getIdVerIndiceAip().longValue());

            // calcolo parte urn ORIGINALE
            String tmpUrn = MessaggiWSFormat.formattaBaseUrnUnitaDoc(
                    MessaggiWSFormat.formattaUrnPartVersatore(versatore),
                    MessaggiWSFormat.formattaUrnPartUnitaDoc(chiave));
            // calcolo parte urn NORMALIZZATO
            String tmpUrnNorm = MessaggiWSFormat.formattaBaseUrnUnitaDoc(
                    MessaggiWSFormat.formattaUrnPartVersatore(versatore, true,
                            Costanti.UrnFormatter.VERS_FMT_STRING),
                    MessaggiWSFormat.formattaUrnPartUnitaDoc(chiave, true,
                            Costanti.UrnFormatter.UD_FMT_STRING));

            // salvo ORIGINALE
            this.salvaUrnVerIndiceAipUd(verIndiceAipUd,
                    MessaggiWSFormat.formattaUrnIndiceAIP(tmpUrn,
                            verIndiceAipUd.getCdVerIndiceAip(),
                            Costanti.UrnFormatter.URN_INDICE_AIP_FMT_STRING_V2),
                    TiUrnVerIxAipUd.ORIGINALE);
            // salvo NORMALIZZATO
            this.salvaUrnVerIndiceAipUd(verIndiceAipUd,
                    MessaggiWSFormat.formattaUrnIndiceAIP(tmpUrnNorm,
                            verIndiceAipUd.getCdVerIndiceAip(),
                            Costanti.UrnFormatter.URN_INDICE_AIP_FMT_STRING_V2),
                    TiUrnVerIxAipUd.NORMALIZZATO);

            // salvo INIZIALE
            this.salvaUrnVerIndiceAipUd(verIndiceAipUd, verIndiceAipUd.getDsUrn(),
                    TiUrnVerIxAipUd.INIZIALE);
        }
    }

    private List<AroVLisaipudUrndacalcByud> retrieveAroVLisaipudUrndacalcByud(long idUnitaDoc) {
        Query query = getEntityManager().createQuery(
                "SELECT aro FROM AroVLisaipudUrndacalcByud aro WHERE aro.id.idUnitaDoc = :idUnitaDoc ");
        query.setParameter("idUnitaDoc", bigDecimalFromLong(idUnitaDoc));
        return query.getResultList();
    }

    private void salvaUrnVerIndiceAipUd(AroVerIndiceAipUd verIndiceAipUd, String tmpUrn,
            TiUrnVerIxAipUd tiUrn) {

        AroUrnVerIndiceAipUd tmpAroUrnVerIndiceAipUd = new AroUrnVerIndiceAipUd();
        tmpAroUrnVerIndiceAipUd.setDsUrn(tmpUrn);
        tmpAroUrnVerIndiceAipUd.setTiUrn(tiUrn);
        tmpAroUrnVerIndiceAipUd.setAroVerIndiceAipUd(verIndiceAipUd);

        // persist
        getEntityManager().persist(tmpAroUrnVerIndiceAipUd);

        if (verIndiceAipUd.getAroUrnVerIndiceAipUds() == null) {
            verIndiceAipUd.setAroUrnVerIndiceAipUds(new ArrayList<>());
        }
        verIndiceAipUd.getAroUrnVerIndiceAipUds().add(tmpAroUrnVerIndiceAipUd);
    }

    public void salvaUrnElvElencoVers(ElvElencoVer elvElencoVers, String tmpUrn,
            TiUrnElenco tiUrn) {

        ElvUrnElencoVers tmpElvUrnElencoVers = new ElvUrnElencoVers();
        tmpElvUrnElencoVers.setDsUrn(tmpUrn);
        tmpElvUrnElencoVers.setTiUrn(tiUrn);
        tmpElvUrnElencoVers.setElvElencoVers(elvElencoVers);

        // persist
        getEntityManager().persist(tmpElvUrnElencoVers);

        if (elvElencoVers.getElvUrnElencoVerss() == null) {
            elvElencoVers.setElvUrnElencoVerss(new ArrayList<>());
        }
        elvElencoVers.getElvUrnElencoVerss().add(tmpElvUrnElencoVers);
    }

    public void salvaUrnElvFileElencoVers(ElvFileElencoVer elvFileElencoVers, String tmpUrn,
            TiUrnFileElenco tiUrn) {

        ElvUrnFileElencoVers tmpElvUrnFileElencoVers = new ElvUrnFileElencoVers();
        tmpElvUrnFileElencoVers.setDsUrn(tmpUrn);
        tmpElvUrnFileElencoVers.setTiUrn(tiUrn);
        tmpElvUrnFileElencoVers.setElvFileElencoVers(elvFileElencoVers);

        // persist
        getEntityManager().persist(elvFileElencoVers);
        getEntityManager().persist(tmpElvUrnFileElencoVers);

        if (elvFileElencoVers.getElvUrnFileElencoVerss() == null) {
            elvFileElencoVers.setElvUrnFileElencoVerss(new ArrayList<>());
        }
        elvFileElencoVers.getElvUrnFileElencoVerss().add(tmpElvUrnFileElencoVers);
    }

    public void scriviSerUrnIxVolVerSerie(SerIxVolVerSerie tmpSerIxVolVerSerie, String urnBase,
            String urnBaseNorm, String versione, int progressivoVolume) {
        // salvo ORIGINALE
        this.salvaUrnVerIxAipVolVerSerie(tmpSerIxVolVerSerie,
                MessaggiWSFormat.formattaUrnIndiceVolumeSerie1(urnBase, versione,
                        String.format("%05d", progressivoVolume),
                        Costanti.UrnFormatter.URN_INDICE_VOLUME_FMT_STRING_V2),
                TiUrnIxVolVerSerie.ORIGINALE);
        // salvo NORMALIZZATO
        this.salvaUrnVerIxAipVolVerSerie(tmpSerIxVolVerSerie,
                MessaggiWSFormat.formattaUrnIndiceVolumeSerie1(urnBaseNorm, versione,
                        String.format("%05d", progressivoVolume),
                        Costanti.UrnFormatter.URN_INDICE_VOLUME_FMT_STRING_V2),
                TiUrnIxVolVerSerie.NORMALIZZATO);
    }

    private void salvaUrnVerIxAipVolVerSerie(SerIxVolVerSerie serIxVolVerSerie, String tmpUrn,
            TiUrnIxVolVerSerie tiUrn) {

        SerUrnIxVolVerSerie tmpSerUrnIxVolVerSerie = new SerUrnIxVolVerSerie();
        tmpSerUrnIxVolVerSerie.setDsUrn(tmpUrn);
        tmpSerUrnIxVolVerSerie.setTiUrn(tiUrn);
        tmpSerUrnIxVolVerSerie.setSerIxVolVerSerie(serIxVolVerSerie);

        // persist
        getEntityManager().persist(tmpSerUrnIxVolVerSerie);

        if (serIxVolVerSerie.getSerUrnIxVolVerSeries() == null) {
            serIxVolVerSerie.setSerUrnIxVolVerSeries(new ArrayList<>());
        }
        serIxVolVerSerie.getSerUrnIxVolVerSeries().add(tmpSerUrnIxVolVerSerie);
    }
}
