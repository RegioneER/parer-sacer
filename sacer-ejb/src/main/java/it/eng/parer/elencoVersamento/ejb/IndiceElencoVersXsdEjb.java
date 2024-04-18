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

package it.eng.parer.elencoVersamento.ejb;

import it.eng.parer.elenco.xml.indice.*;
import it.eng.parer.elenco.xml.indice.AggiornamentoUnitaDocumetariaType;
import it.eng.parer.entity.AroCompDoc;
import it.eng.parer.entity.AroCompUrnCalc;
import it.eng.parer.entity.AroDoc;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.ElvElencoVer;
import it.eng.parer.entity.OrgEnte;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.AroCompUrnCalc.TiUrn;
import it.eng.parer.elencoVersamento.utils.ElencoEnums.*;
import it.eng.parer.elencoVersamento.helper.ElencoVersamentoHelper;
import it.eng.parer.elencoVersamento.helper.IndiceElencoVersHelper;
import it.eng.parer.entity.AroUpdUnitaDoc;
import it.eng.parer.exception.ParerNoResultException;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.job.dto.SessioneVersamentoExt;
import it.eng.parer.job.dto.SessioneVersamentoExt.DatiXml;
import it.eng.parer.viewEntity.ElvVLisModifByUd;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.UnitaDocumentarieHelper;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.ejb.XmlContextCache;
import it.eng.parer.ws.recupero.utils.XmlDateUtility;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import javax.xml.bind.Marshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Agati_D
 */
@Stateless
@LocalBean
public class IndiceElencoVersXsdEjb {

    private static final Logger log = LoggerFactory.getLogger(IndiceElencoVersXsdEjb.class);

    @EJB
    private XmlContextCache xmlcontextCache;

    @EJB
    private IndiceElencoVersHelper indiceHelper;
    @EJB
    private ElencoVersamentoHelper elencoHelper;
    @EJB
    private UnitaDocumentarieHelper unitaDocumentarieHelper;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private GenericHelper genericHelper;

    public byte[] createIndex(ElvElencoVer elenco, boolean manualClosing) throws ParerNoResultException {
        log.debug("createIndex");
        OrgEnte orgEnte = elenco.getOrgStrut().getOrgEnte();
        OrgStrut orgStrut = elenco.getOrgStrut();
        // Creo elemento Elencoversamento
        it.eng.parer.elenco.xml.indice.Elencoversamento elencoVersamento = new it.eng.parer.elenco.xml.indice.Elencoversamento();
        elencoVersamento.setVersioneElenco(ElencoInfo.VERSIONE_ELENCO.message());

        // creo elemento enteProduttore
        Elencoversamento.EnteProduttore enteProduttore = new Elencoversamento.EnteProduttore();
        String ambiente = orgEnte.getOrgAmbiente().getNmAmbiente();
        String ente = orgEnte.getNmEnte();
        String nomeStruttura = orgStrut.getNmStrut();
        log.debug("createIndex - getUtentiVersatori idElenco " + elenco.getIdElencoVers());
        String utentiVersatori = indiceHelper.getUtentiVersatori(elenco);
        // setto i campi dell'elemento EnteProduttore
        enteProduttore.setAmbiente(ambiente);
        enteProduttore.setEnte(ente);
        enteProduttore.setStruttura(nomeStruttura);
        enteProduttore.setUserID(utentiVersatori);
        // set EnteProduttore a ElencoVersamento
        elencoVersamento.setEnteProduttore(enteProduttore);
        // creo elemento DescrizioneElencoVersamento
        Elencoversamento.DescrizioneElencoVersamento descrizioneElencoVersamento = new Elencoversamento.DescrizioneElencoVersamento();
        // EVO#16486
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        // sistema (new URN)
        String sistema = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);
        // calcolo urn ORIGINALE
        // urn:<sistema>:<nome ente>:<nome struttura>:ElencoVers-UD-<data creazione>-<id elenco>
        String urnElenco = MessaggiWSFormat.formattaUrnElencoVersamento(sistema, ente, nomeStruttura,
                sdf.format(elenco.getDtCreazioneElenco()), Long.toString(elenco.getIdElencoVers()));
        // end EVO#16486
        descrizioneElencoVersamento.setUrnElenco(urnElenco);
        descrizioneElencoVersamento.setIdElenco(BigInteger.valueOf(elenco.getIdElencoVers()));
        descrizioneElencoVersamento.setNomeElenco(elenco.getNmElenco());
        descrizioneElencoVersamento
                .setDataChiusuraElenco(XmlDateUtility.dateToXMLGregorianCalendar(elenco.getDtChius()));
        log.debug("createIndex - getDateVersamento idElenco " + elenco.getIdElencoVers());
        Map<String, Date> dataVersamento = indiceHelper.retrieveDateVersamento(elenco);
        Date dataVersamentoIniziale = dataVersamento.get("dataVersamentoIniziale");
        if (dataVersamentoIniziale != null) {
            descrizioneElencoVersamento
                    .setDataVersamentoIniziale(XmlDateUtility.dateToXMLGregorianCalendar(dataVersamentoIniziale));
        }
        Date dataVersamentoFinale = dataVersamento.get("dataVersamentoFinale");
        if (dataVersamentoFinale != null) {
            descrizioneElencoVersamento
                    .setDataVersamentoFinale(XmlDateUtility.dateToXMLGregorianCalendar(dataVersamentoFinale));
        }
        descrizioneElencoVersamento.setDescrizioneCriterio(elenco.getDecCriterioRaggr().getDsCriterioRaggr());
        if (manualClosing) {
            descrizioneElencoVersamento.setMotivoChiusura("Manuale");
        } else if (elenco.getDlMotivoChius().equals(MotivazioneChiusura.ELENCO_FULL.message())) {
            descrizioneElencoVersamento.setMotivoChiusura("Raggiunto_numero_massimo_componenti");
        } else if (elenco.getDlMotivoChius().equals(MotivazioneChiusura.ELENCO_EXPIRED.message())) {
            descrizioneElencoVersamento.setMotivoChiusura("Scadenza_limite_temporale");
        }
        descrizioneElencoVersamento.setNoteElenco(elenco.getNtIndiceElenco());
        // set DescrizioneElencoVersamento a ElencoVersamento
        elencoVersamento.setDescrizioneElencoVersamento(descrizioneElencoVersamento);

        /*
         * |--- UnitaDocumentarieVersate ContenutoSinteticoElenco ---| |--- UnitaDocumentarieAggiornate
         */
        // creo elemento ContenutoSinteticoElenco
        Elencoversamento.ContenutoSinteticoElenco contenutoSinteticoElenco = new Elencoversamento.ContenutoSinteticoElenco();

        // creo elemento UnitaDocumentarieVersate di ContenutoSinteticoElenco
        ContenutoSinteticoUDVersateType unitaDocumentarieVersateSintetico = new ContenutoSinteticoUDVersateType();
        unitaDocumentarieVersateSintetico
                .setNumeroUnitaDocumentarieVersate(BigInteger.valueOf(elenco.getNiUnitaDocVersElenco().longValue()));

        log.debug("createIndex - getTipologie (ud, docprincip, reg) idElenco " + elenco.getIdElencoVers());
        String tipologieUnitaDocumentaria = indiceHelper.getTipologieUnitaDocumentaria(elenco);
        unitaDocumentarieVersateSintetico.setTipologieUnitaDocumentaria(tipologieUnitaDocumentaria);

        String tipologieDocumentoPrincipaleElv = indiceHelper.getTipologieDocumentoPrincipaleElv(elenco);
        unitaDocumentarieVersateSintetico.setTipologieDocumentoPrincipale(tipologieDocumentoPrincipaleElv);

        String tipologieRegistro = indiceHelper.getTipologieRegistro(elenco);
        unitaDocumentarieVersateSintetico.setTipologieRegistro(tipologieRegistro);

        unitaDocumentarieVersateSintetico
                .setNumeroDocumentiVersati(BigInteger.valueOf(elenco.getNiDocVersElenco().longValue()));
        unitaDocumentarieVersateSintetico
                .setNumeroComponentiVersati(BigInteger.valueOf(elenco.getNiCompVersElenco().longValue()));

        // set UnitaDocumentarieVersate a ContenutoSinteticoElenco
        contenutoSinteticoElenco.setUnitaDocumentarieVersate(unitaDocumentarieVersateSintetico);

        // creo elemento UnitaDocumentarieAggiornate di ContenutoSinteticoElenco
        ContenutoSinteticoUDAggiornateType unitaDocumentarieAggiornateSintetico = new ContenutoSinteticoUDAggiornateType();

        unitaDocumentarieAggiornateSintetico
                .setNumeroUnitaDocumentarieAggiornate(BigInteger.valueOf(elenco.getNiUnitaDocModElenco().longValue()));
        unitaDocumentarieAggiornateSintetico
                .setNumeroDocumentiAggiunti(BigInteger.valueOf(elenco.getNiDocAggElenco().longValue()));
        unitaDocumentarieAggiornateSintetico
                .setNumeroComponentiDocumentiAggiunti(BigInteger.valueOf(elenco.getNiCompAggElenco().longValue()));
        unitaDocumentarieAggiornateSintetico.setNumeroAggiornamentiMetadatiUnitaDocumentaria(
                BigInteger.valueOf(elenco.getNiUpdUnitaDoc().longValue()));

        // set UnitaDocumentarieAggiornate a ContenutoSinteticoElenco
        contenutoSinteticoElenco.setUnitaDocumentarieAggiornate(unitaDocumentarieAggiornateSintetico);

        // set ContenutoSinteticoElenco a ElencoVersamento
        elencoVersamento.setContenutoSinteticoElenco(contenutoSinteticoElenco);

        /*
         * |--- UnitaDocumentarieVersate ContenutoAnaliticoElenco ---| |--- UnitaDocumentarieAggiornate
         */
        // creo elemento ContenutoAnaliticoElenco
        Elencoversamento.ContenutoAnaliticoElenco contenutoAnaliticoElenco = new Elencoversamento.ContenutoAnaliticoElenco();

        // creo elemento UnitaDocumentarieVersate di ContenutoAnaliticoElenco
        UnitaDocumentarieType unitaDocumentarieVersateAnalitico = new UnitaDocumentarieType();

        List<AroUnitaDoc> aroUnitaDocs = elenco.getAroUnitaDocs();

        // ordino le unità doc dell'elenco in ordine di data versamento
        Collections.sort(aroUnitaDocs, new Comparator<AroUnitaDoc>() {
            @Override
            public int compare(AroUnitaDoc ud1, AroUnitaDoc ud2) {
                return ud1.getDtCreazione().compareTo(ud2.getDtCreazione());
            }
        });
        // costruisco tutti i UnitaDocumentariaVersata e li aggiungo a unitaDocumentarieVersate
        // (unitaDocumentarieVersateAnalitico)
        for (AroUnitaDoc ud : aroUnitaDocs) {
            // creo elemento UnitaDocumentariaVersata
            UnitaDocumentariaType unitaDocumentariaVersata = buildUnitaDocumentariaVersata(ud, ambiente, ente,
                    nomeStruttura, sistema);
            unitaDocumentarieVersateAnalitico.getUnitaDocumentariaVersata().add(unitaDocumentariaVersata);
        }
        // set unitaDocumentarieVersate a ContenutoAnaliticoElenco se non vuoto
        if (!unitaDocumentarieVersateAnalitico.getUnitaDocumentariaVersata().isEmpty()) {
            contenutoAnaliticoElenco.setUnitaDocumentarieVersate(unitaDocumentarieVersateAnalitico);
        }

        /*
         * creo elemento UnitaDocumentarieAggiornate di ContenutoAnaliticoElenco
         */
        UnitaDocumentarieAggiornateType unitaDocumentarieAggiornateAnalitico = new UnitaDocumentarieAggiornateType();

        // lista delle unità doc modificate per aggiunta documenti o aggiornamenti metadati inclusi nell’elenco,
        // in ordine di data versamento del primo documento aggiunto o aggiornamento metadati
        log.debug(
                "createIndex - lista delle unità doc modificate per aggiunta documenti o aggiornamenti metadati inclusi nell'elenco idElenco "
                        + elenco.getIdElencoVers());
        List<AroUnitaDoc> uds = elencoHelper.retrieveUdsModifDocAggUpdInElenco(elenco.getIdElencoVers());
        // costruisco tutti i UnitaDocumentariaAggiornata e li aggiungo a unitaDocumentarieAggiornata
        // (unitaDocumentarieAggiornataAnalitico)
        for (AroUnitaDoc ud : uds) {
            // creo elemento UnitaDocumentariaAggiornata
            UnitaDocumentariaAggiornataType unitaDocumentariaAggiornata = buildUnitaDocumentariaAggiornata(
                    elenco.getIdElencoVers(), ud, ambiente, ente, nomeStruttura, sistema);
            unitaDocumentarieAggiornateAnalitico.getUnitaDocumentariaAggiornata().add(unitaDocumentariaAggiornata);
        }

        // set unitaDocumentarieAggiornate a ContenutoAnaliticoElenco se non vuoto
        if (!unitaDocumentarieAggiornateAnalitico.getUnitaDocumentariaAggiornata().isEmpty()) {
            contenutoAnaliticoElenco.setUnitaDocumentarieAggiornate(unitaDocumentarieAggiornateAnalitico);
        }

        // set ContenutoAnaliticoElenco a ElencoVersamento
        elencoVersamento.setContenutoAnaliticoElenco(contenutoAnaliticoElenco);

        log.debug("createIndex - inizio creazione XML idElenco " + elenco.getIdElencoVers());
        final StringWriter tmpStrWrtIndice = new StringWriter();
        byte[] byteIndice;
        try {
            Marshaller marshaller = xmlcontextCache.getCreazioneElencoVersamentoCtx_Elencoversamento()
                    .createMarshaller();
            marshaller.marshal(elencoVersamento, tmpStrWrtIndice);
            marshaller.setSchema(xmlcontextCache.getSchemaOfCreazioneElencoVersamento());

            byteIndice = tmpStrWrtIndice.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new EJBException();
        }
        log.debug("createIndex - fine creazione XML idElenco " + elenco.getIdElencoVers());
        return byteIndice;
    }

    private ComponenteType buildComponente(AroCompDoc comp) {
        // creo elemento Componente
        ComponenteType componente = new ComponenteType();
        // urn
        AroCompUrnCalc urn = unitaDocumentarieHelper.findAroCompUrnCalcByType(comp, TiUrn.ORIGINALE);
        componente.setUrnCalcolato(urn.getDsUrn());
        componente.setHash(Objects.toString(comp.getDsHashFileCalc(), ""));
        componente.setAlgoritmoHash(Objects.toString(comp.getDsAlgoHashFileCalc(), ""));
        componente.setEncoding(Objects.toString(comp.getCdEncodingHashFileCalc(), ""));

        // recupero lista sottocomponenti
        List<AroCompDoc> sottocompList = comp.getAroCompDocs();
        // il componente ha sottocomponenti
        if (sottocompList.size() > 0) {
            // creo elemento SottoComponenti
            ComponenteType.SottoComponenti sottoComponentiElenco = new ComponenteType.SottoComponenti();
            for (AroCompDoc sottocomp : sottocompList) {
                // creo elemento SottoComponente
                ComponenteType.SottoComponenti.SottoComponente sottoComponenteElenco = new ComponenteType.SottoComponenti.SottoComponente();
                // urn
                urn = unitaDocumentarieHelper.findAroCompUrnCalcByType(sottocomp, TiUrn.ORIGINALE);
                sottoComponenteElenco.setUrnCalcolato(urn.getDsUrn());
                sottoComponenteElenco.setHash(Objects.toString(sottocomp.getDsHashFileCalc(), ""));
                sottoComponenteElenco.setAlgoritmoHash(Objects.toString(sottocomp.getDsAlgoHashFileCalc(), ""));
                sottoComponenteElenco.setEncoding(Objects.toString(sottocomp.getCdEncodingHashFileCalc(), ""));
                // setto l'elemento sottoComponenteIndice al padre sottoComponentiIndice
                sottoComponentiElenco.getSottoComponente().add(sottoComponenteElenco);
            }
            // setto l'elemento SottoComponenti al padre Componente
            componente.setSottoComponenti(sottoComponentiElenco);
        }
        return componente;
    }

    private List<ComponenteType> buildComponenti(AroDoc doc) {
        List<ComponenteType> componentiDocVers = new ArrayList<>();
        List<AroCompDoc> comps = elencoHelper.retrieveCompsInDoc(doc);
        // ordino i componenti per numero d’ordine del componente
        Collections.sort(comps, new Comparator<AroCompDoc>() {
            @Override
            public int compare(AroCompDoc comp1, AroCompDoc comp2) {
                return comp1.getNiOrdCompDoc().compareTo(comp2.getNiOrdCompDoc());
            }
        });
        for (AroCompDoc comp : comps) {
            // ricavo un componente
            ComponenteType componenteDocVers = buildComponente(comp);
            // aggiungo il componente all'elemento
            componentiDocVers.add(componenteDocVers);
        }
        return componentiDocVers;
    }

    private DocumentoType buildDocUdVersata(AroDoc doc, CSVersatore tmpVers, CSChiave tmpChiave) {
        DocumentoType documentoUdVersata = new DocumentoType();
        // costruisco l'urn del documento
        String urnDoc = buildUrnDoc(doc, tmpVers, tmpChiave);
        documentoUdVersata.setUrnDocumento(urnDoc);
        // creo elemento Componenti
        DocumentoType.Componenti componentiDocVers = new DocumentoType.Componenti();
        componentiDocVers.getComponente().addAll(buildComponenti(doc));
        documentoUdVersata.setComponenti(componentiDocVers);
        return documentoUdVersata;
    }

    private DocumentoAggiuntoType buildDocUdAggiornata(AroDoc doc, String dsUrnDoc, String dsUrnRappVers,
            Date dtVersDoc) {
        DocumentoAggiuntoType documentoUdAggiornata = new DocumentoAggiuntoType();
        documentoUdAggiornata.setUrnDocumento(dsUrnDoc);
        documentoUdAggiornata.setDataVersamento(XmlDateUtility.dateToXMLGregorianCalendar(dtVersDoc));
        documentoUdAggiornata.setUrnRapportoVersamento(dsUrnRappVers);
        // creo elemento Componenti
        DocumentoAggiuntoType.Componenti componentiDocVers = new DocumentoAggiuntoType.Componenti();
        componentiDocVers.getComponente().addAll(buildComponenti(doc));
        // set Componenti a Documento
        documentoUdAggiornata.setComponenti(componentiDocVers);
        return documentoUdAggiornata;
    }

    private AggiornamentoUnitaDocumetariaType buildUpdUdAggiornata(AroUpdUnitaDoc upd, String dsUrnUpd,
            String dsUrnRappVers, Date dtVersUpd, String dsHashRappVers, String dsAlgoHashRappVers,
            String cdEncodingHashRappVers) {
        AggiornamentoUnitaDocumetariaType aggiornamentoUdAggiornata = new AggiornamentoUnitaDocumetariaType();
        aggiornamentoUdAggiornata.setUrnAggiornamentoUnitaDocumentaria(dsUrnUpd);
        aggiornamentoUdAggiornata.setDataVersamento(XmlDateUtility.dateToXMLGregorianCalendar(dtVersUpd));
        aggiornamentoUdAggiornata.setUrnRapportoVersamento(dsUrnRappVers);
        aggiornamentoUdAggiornata.setHash(dsHashRappVers);
        aggiornamentoUdAggiornata.setAlgoritmoHash(dsAlgoHashRappVers);
        aggiornamentoUdAggiornata.setEncoding(cdEncodingHashRappVers);
        return aggiornamentoUdAggiornata;
    }

    private CSVersatore buildCSVersatore(String ambiente, String ente, String nomeStruttura,
            String sistemaConservazione) {
        CSVersatore csVers = new CSVersatore();
        csVers.setStruttura(nomeStruttura);
        csVers.setEnte(ente);
        csVers.setAmbiente(ambiente);
        // sistema (new URN)
        csVers.setSistemaConservazione(sistemaConservazione);

        return csVers;
    }

    private CSChiave buildCSChiave(String tipoRegistro, long anno, String numero) {
        CSChiave tmpChiave = new CSChiave();
        tmpChiave.setTipoRegistro(tipoRegistro);
        tmpChiave.setAnno(anno);
        tmpChiave.setNumero(numero);

        return tmpChiave;
    }

    private String buildUrnDoc(AroDoc doc, CSVersatore tmpVers, CSChiave tmpChiave) {
        String baseUrnDoc = buildBaseUrnDoc(tmpVers, tmpChiave, doc);
        String urnDoc = MessaggiWSFormat.formattaUrnDocUniDoc(baseUrnDoc);
        return urnDoc;
    }

    private String buildBaseUrnDoc(CSVersatore tmpVers, CSChiave tmpChiave, AroDoc doc) {
        // EVO#16486
        // calcolo parte urn ORIGINALE
        // DOCXXXXXX
        String tmpUrnPartDoc = MessaggiWSFormat.formattaUrnPartDocumento(Costanti.CategoriaDocumento.Documento,
                doc.getNiOrdDoc().intValue(), true, Costanti.UrnFormatter.DOC_FMT_STRING_V2,
                Costanti.UrnFormatter.PAD5DIGITS_FMT);

        return MessaggiWSFormat.formattaBaseUrnDoc(MessaggiWSFormat.formattaUrnPartVersatore(tmpVers),
                MessaggiWSFormat.formattaUrnPartUnitaDoc(tmpChiave), tmpUrnPartDoc,
                Costanti.UrnFormatter.URN_DOC_FMT_STRING_V2);
        // end EVO#16486
    }

    private String buildBaseUrnUnitaDoc(CSVersatore tmpVers, CSChiave tmpChiave) {
        // EVO#16486
        // calcolo urn ORIGINALE
        return MessaggiWSFormat.formattaBaseUrnUnitaDoc(MessaggiWSFormat.formattaUrnPartVersatore(tmpVers),
                MessaggiWSFormat.formattaUrnPartUnitaDoc(tmpChiave));
        // end EVO#16486
    }

    private String buildUrnDocUniDoc(String baseUrnUnitaDoc) {
        return MessaggiWSFormat.formattaUrnDocUniDoc(baseUrnUnitaDoc);
    }

    private UnitaDocumentariaType buildUnitaDocumentariaVersata(AroUnitaDoc ud, String ambiente, String ente,
            String nomeStruttura, String sistemaConservazione) throws ParerNoResultException {
        UnitaDocumentariaType unitaDocumentariaVersata = new UnitaDocumentariaType();
        // EVO#16486
        // costruisco l'urn della unita documentaria
        CSVersatore tmpVers = buildCSVersatore(ambiente, ente, nomeStruttura, sistemaConservazione);
        CSChiave tmpChiave = buildCSChiave(ud.getCdRegistroKeyUnitaDoc(), ud.getAaKeyUnitaDoc().longValue(),
                ud.getCdKeyUnitaDoc());
        String baseUrnUnitaDoc = buildUrnDocUniDoc(buildBaseUrnUnitaDoc(tmpVers, tmpChiave));
        // end EVO#16486
        unitaDocumentariaVersata.setUrnUnitaDocumentaria(baseUrnUnitaDoc);
        unitaDocumentariaVersata.setDataVersamento(XmlDateUtility.dateToXMLGregorianCalendar(ud.getDtCreazione()));
        // EVO#16486
        List<SessioneVersamentoExt> sessioniVersamentoList = elencoHelper
                .leggiXmlVersamentiElencoDaUnitaDoc(ud.getIdUnitaDoc(), baseUrnUnitaDoc);
        for (SessioneVersamentoExt sessioneVersamento : sessioniVersamentoList) {
            // Sessione di versamento dell'ud
            boolean isRdv = false;
            for (DatiXml datiXml : sessioneVersamento.getXmlDatiSessioneVers()) {
                // Setto il tag Urn del Rapporto di Versamento
                if (datiXml.getUrn() != null && !datiXml.getUrn().isEmpty()) {
                    switch (datiXml.getTipoXmlDati()) {
                    case CostantiDB.TipiXmlDati.RAPP_VERS:
                        unitaDocumentariaVersata.setUrnRapportoVersamento(datiXml.getUrn());
                        isRdv = true;
                        break;
                    case CostantiDB.TipiXmlDati.RISPOSTA:
                        unitaDocumentariaVersata.setUrnRapportoVersamento(datiXml.getUrn());
                        break;
                    default:
                        break;
                    }
                    if (isRdv) {
                        break;
                    }
                }
            }
        }
        // end EVO#16486
        unitaDocumentariaVersata.setTipologiaRegistro(ud.getCdRegistroKeyUnitaDoc());
        unitaDocumentariaVersata.setTipologiaUnitaDocumentaria(ud.getDecTipoUnitaDoc().getNmTipoUnitaDoc());
        if (ud.getDlOggettoUnitaDoc() != null) {
            unitaDocumentariaVersata.setOggetto(ud.getDlOggettoUnitaDoc());
        }
        if (ud.getDtRegUnitaDoc() != null) {
            unitaDocumentariaVersata
                    .setDataUnitaDocumentaria(XmlDateUtility.dateToXMLGregorianCalendar(ud.getDtRegUnitaDoc()));
        }
        String tipologieDocumentoPrincipaleUd = indiceHelper.getTipologieDocumentoPrincipaleUd(ud);
        unitaDocumentariaVersata.setTipoDocumentoPrincipale(tipologieDocumentoPrincipaleUd);
        unitaDocumentariaVersata.setNumeroAllegati(BigInteger.valueOf(ud.getNiAlleg().longValue()));
        unitaDocumentariaVersata.setNumeroAnnessi(BigInteger.valueOf(ud.getNiAnnessi().longValue()));
        unitaDocumentariaVersata.setNumeroAnnotazioni(BigInteger.valueOf(ud.getNiAnnot().longValue()));

        /* Normalizza UD e Calcolo URN Componenti (se necessario) */
        unitaDocumentarieHelper.normalizzaUDAndCalcUrnOrigNormalizComp(ud.getIdUnitaDoc(),
                Arrays.asList(TiUrn.ORIGINALE, TiUrn.NORMALIZZATO));

        List<AroDoc> docVersList = elencoHelper.retrieveDocVersList(ud);
        if (!docVersList.isEmpty()) {
            // EVO#16486
            // ordino i documenti per numero d’ordine del documento (EVO#16486)
            Collections.sort(docVersList, new Comparator<AroDoc>() {
                @Override
                public int compare(AroDoc comp1, AroDoc comp2) {
                    return comp1.getNiOrdDoc().compareTo(comp2.getNiOrdDoc());
                }
            });
            // end EVO#16486
            // creo elemento Documenti
            UnitaDocumentariaType.Documenti documentiUdVersata = new UnitaDocumentariaType.Documenti();
            for (AroDoc doc : docVersList) {
                // creo elemento Documento
                DocumentoType documentoUdVersata = buildDocUdVersata(doc, tmpVers, tmpChiave);
                documentiUdVersata.getDocumento().add(documentoUdVersata);
            }
            unitaDocumentariaVersata.getDocumenti().add(documentiUdVersata);
        }
        return unitaDocumentariaVersata;
    }

    private UnitaDocumentariaAggiornataType buildUnitaDocumentariaAggiornata(long idElenco, AroUnitaDoc ud,
            String ambiente, String ente, String nomeStruttura, String sistemaConservazione) {
        UnitaDocumentariaAggiornataType unitaDocumentariaAggiornata = new UnitaDocumentariaAggiornataType();
        // EVO#16486
        // costruisco l'urn della unita documentaria
        CSVersatore tmpVers = buildCSVersatore(ambiente, ente, nomeStruttura, sistemaConservazione);
        CSChiave tmpChiave = buildCSChiave(ud.getCdRegistroKeyUnitaDoc(), ud.getAaKeyUnitaDoc().longValue(),
                ud.getCdKeyUnitaDoc());
        String baseUrnUnitaDoc = buildBaseUrnUnitaDoc(tmpVers, tmpChiave);
        String urnDocUniDoc = buildUrnDocUniDoc(baseUrnUnitaDoc);
        // end EVO#16486
        unitaDocumentariaAggiornata.setUrnUnitaDocumentariaAggiornata(urnDocUniDoc);

        /* Normalizza UD e Calcolo URN Componenti (se necessario) */
        unitaDocumentarieHelper.normalizzaUDAndCalcUrnOrigNormalizComp(ud.getIdUnitaDoc(),
                Arrays.asList(TiUrn.ORIGINALE, TiUrn.NORMALIZZATO));

        List<ElvVLisModifByUd> docAggUpdList = elencoHelper.retrieveDocAggUpdList(ud, idElenco);
        for (ElvVLisModifByUd modif : docAggUpdList) {
            // creo elemento Aggiornamenti
            UnitaDocumentariaAggiornataType.Aggiornamenti aggiornamentiUdAggiornata = new UnitaDocumentariaAggiornataType.Aggiornamenti();
            switch (modif.getTiModif()) {
            case "DOC_AGG":
                // creo elemento DocumentoAggiunto
                AroDoc doc = genericHelper.findById(AroDoc.class, modif.getIdModif());
                DocumentoAggiuntoType documentoUdAggiornata = buildDocUdAggiornata(doc, modif.getDsUrnModif(),
                        modif.getDsUrnRappVers(), modif.getDtVers());
                aggiornamentiUdAggiornata.setDocumentoAggiunto(documentoUdAggiornata);
                break;
            case "UPD_UD":
                // creo elemento AggiornamentoUnitaDocumentari
                AroUpdUnitaDoc upd = genericHelper.findById(AroUpdUnitaDoc.class, modif.getIdModif());
                AggiornamentoUnitaDocumetariaType aggiornamentoUdAggiornata = buildUpdUdAggiornata(upd,
                        modif.getDsUrnModif(), modif.getDsUrnRappVers(), modif.getDtVers(), modif.getDsHashRappVers(),
                        modif.getDsAlgoHashRappVers(), modif.getCdEncodingHashRappVers());
                aggiornamentiUdAggiornata.setAggiornamentoUnitaDocumentaria(aggiornamentoUdAggiornata);
                break;
            default:
                break;
            }
            unitaDocumentariaAggiornata.getAggiornamenti().add(aggiornamentiUdAggiornata);
        }

        return unitaDocumentariaAggiornata;
    }
}
