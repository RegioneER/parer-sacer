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

package it.eng.parer.elencoVersFascicoli.ejb;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.springframework.util.StringUtils;

import it.eng.parer.elencoFascicoli.xml.indice.ContenutoSinteticoElencoType;
import it.eng.parer.elencoFascicoli.xml.indice.ElencoversamentoFascicoli;
import it.eng.parer.elencoFascicoli.xml.indice.FascicoliType;
import it.eng.parer.elencoFascicoli.xml.indice.FascicoloType;
import it.eng.parer.elencoVersFascicoli.helper.IndiceElencoVersFascHelper;
import it.eng.parer.elencoVersFascicoli.utils.ElencoEnums.ElencoInfo;
import it.eng.parer.entity.ElvElencoVersFasc;
import it.eng.parer.entity.FasFascicolo;
import it.eng.parer.viewEntity.ElvVCreaIxElencoFasc;
import it.eng.parer.viewEntity.ElvVCreaLisFascElenco;
import it.eng.parer.ws.ejb.XmlContextCache;

/**
 *
 * @author DiLorenzo_F
 */
@Stateless
@LocalBean
public class IndiceElencoVersFascXsdEjb {
    @EJB
    private IndiceElencoVersFascHelper indiceHelper;

    @EJB(mappedName = "java:app/Parer-ejb/XmlContextCache")
    private XmlContextCache xmlContextCache;

    SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS");

    public byte[] createIndex(ElvElencoVersFasc elenco, boolean manualClosing)
            throws DatatypeConfigurationException {
        // Accedo all’elenco mediante la vista ELV_V_CREA_IX_ELENCO_FASC
        // che fornisce le informazioni per valorizzare i tag EnteProduttore,
        // DescrizioneElencoVersamento e
        // ContenutoSinteticoElencoType
        ElvVCreaIxElencoFasc indiceElenco = indiceHelper.retrieveElvVCreaIxElencoFasc(elenco);
        // Creo elemento ElencoversamentoFascicoli
        ElencoversamentoFascicoli elencoVersamentoFascicoli = new ElencoversamentoFascicoli();
        elencoVersamentoFascicoli.setVersioneElenco(ElencoInfo.VERSIONE_ELENCO.message());
        // Creo elemento EnteProduttore
        ElencoversamentoFascicoli.EnteProduttore enteProduttore = new ElencoversamentoFascicoli.EnteProduttore();
        String ambiente = indiceElenco.getNmAmbiente();
        String ente = indiceElenco.getNmEnte();
        String nomeStruttura = indiceElenco.getNmStrut();
        String utentiVersatori = null;
        if (indiceElenco.getLisNmUserid() != null) {
            String delim = "; ";
            Set<String> setNmUserId = new HashSet<>();
            Collections.addAll(setNmUserId, indiceElenco.getLisNmUserid().split(delim));
            utentiVersatori = StringUtils.collectionToDelimitedString(setNmUserId, delim);
        }
        // setto i campi dell'elemento EnteProduttore
        enteProduttore.setAmbiente(ambiente);
        enteProduttore.setEnte(ente);
        enteProduttore.setStruttura(nomeStruttura);
        enteProduttore.setUserID(utentiVersatori);
        // set EnteProduttore a ElencoversamentoFascicoli
        elencoVersamentoFascicoli.setEnteProduttore(enteProduttore);

        // Creo elemento DescrizioneElencoVersamento
        ElencoversamentoFascicoli.DescrizioneElencoVersamento descrizioneElencoVersamento = new ElencoversamentoFascicoli.DescrizioneElencoVersamento();
        // urn:ElencoVersamentoFascicoli:Ambiente:Ente:Struttura:ID elenco
        String urnElenco = indiceElenco.getDsUrnElenco();
        descrizioneElencoVersamento.setUrnElenco(urnElenco);
        descrizioneElencoVersamento.setIdElenco(indiceElenco.getIdElencoVersFasc().toBigInteger());

        XMLGregorianCalendar dataAperturaElenco = null;
        XMLGregorianCalendar dataChiusuraElenco = null;
        XMLGregorianCalendar dataVersamentoIniziale = null;
        XMLGregorianCalendar dataVersamentoFinale = null;
        if (elenco.getTsCreazioneElenco() != null) { // TODO: verificare
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(elenco.getTsCreazioneElenco());
            dataAperturaElenco = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        }
        if (indiceElenco.getTsChiusuraElenco() != null) {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(indiceElenco.getTsChiusuraElenco());
            dataChiusuraElenco = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        }
        if (indiceElenco.getTsVersIni() != null) {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(indiceElenco.getTsVersIni());
            dataVersamentoIniziale = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        }
        if (indiceElenco.getTsVersFin() != null) {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(indiceElenco.getTsVersFin());
            dataVersamentoFinale = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        }
        descrizioneElencoVersamento.setDataAperturaElenco(dataAperturaElenco);
        descrizioneElencoVersamento.setDataChiusuraElenco(dataChiusuraElenco);
        descrizioneElencoVersamento.setDataVersamentoIniziale(dataVersamentoIniziale);
        descrizioneElencoVersamento.setDataVersamentoFinale(dataVersamentoFinale);
        descrizioneElencoVersamento.setCriterioDiRaggruppamento(indiceElenco.getNmCriterioRaggr());
        descrizioneElencoVersamento.setDescrizioneCriterio(indiceElenco.getDsCriterioRaggr());
        descrizioneElencoVersamento.setMotivoChiusura(indiceElenco.getDlMotivoChius());
        descrizioneElencoVersamento.setNoteElenco(indiceElenco.getNtIndiceElenco());
        // set DescrizioneElencoVersamento a ElencoversamentoFascicoli
        elencoVersamentoFascicoli.setDescrizioneElencoVersamento(descrizioneElencoVersamento);

        // Creo elemento ContenutoSinteticoElencoType
        ContenutoSinteticoElencoType contenutoSinteticoElenco = new ContenutoSinteticoElencoType();
        // set NumeroFascicoliVersati a ContenutoSinteticoElencoType
        contenutoSinteticoElenco
                .setNumeroFascicoliVersati(indiceElenco.getNiFascVersElenco().toBigInteger());
        // set TipologieFascicoloVersate a ContenutoSinteticoElencoType
        if (indiceElenco.getLisNmTipoFascicolo() != null) {
            String delim = "; ";
            Set<String> setNmTipoFascicolo = new HashSet<>();
            Collections.addAll(setNmTipoFascicolo,
                    indiceElenco.getLisNmTipoFascicolo().split(delim));
            contenutoSinteticoElenco.setTipologieFascicoloVersate(
                    StringUtils.collectionToDelimitedString(setNmTipoFascicolo, delim));
        }
        // set ContenutoSinteticoElencoType a ElencoversamentoFascicoli
        elencoVersamentoFascicoli.setContenutoSinteticoElenco(contenutoSinteticoElenco);

        /*
         * ContenutoAnaliticoElenco ---> FascicoliVersati
         */
        // Creo elemento ContenutoAnaliticoElenco
        ElencoversamentoFascicoli.ContenutoAnaliticoElenco contenutoAnaliticoElenco = new ElencoversamentoFascicoli.ContenutoAnaliticoElenco();
        // Creo elemento FascicoliType di ContenutoAnaliticoElenco
        FascicoliType fascicoliVersatiAnalitico = new FascicoliType();
        // costruisco tutti i FascicoloType e li aggiungo a fascicoliVersatiAnalitico
        // (FascicoliType)
        for (FasFascicolo ff : elenco.getFasFascicoli()) {
            // Creo elemento FascicoloType
            FascicoloType fascicoloVersato = buildFascicoloVersato(ff);
            fascicoliVersatiAnalitico.getFascicoloVersato().add(fascicoloVersato);
        }
        // set fascicoliVersatiAnalitico (FascicoliType) a ContenutoAnaliticoElenco se non vuoto
        if (!fascicoliVersatiAnalitico.getFascicoloVersato().isEmpty()) {
            contenutoAnaliticoElenco.setFascicoliVersati(fascicoliVersatiAnalitico);
        }
        // set ContenutoAnaliticoElenco a ElencoversamentoFascicoli
        elencoVersamentoFascicoli.setContenutoAnaliticoElenco(contenutoAnaliticoElenco);

        StringWriter tmpStrWrtIndice = new StringWriter();
        byte[] byteIndice;
        try {
            Marshaller jaxbMarshaller = xmlContextCache.getElencoversamentoFascicoliCtx()
                    .createMarshaller();
            jaxbMarshaller.marshal(elencoVersamentoFascicoli, tmpStrWrtIndice);
            tmpStrWrtIndice.flush();
            byteIndice = tmpStrWrtIndice.toString().getBytes("UTF-8");
        } catch (JAXBException | UnsupportedEncodingException ex) {
            throw new EJBException();
        }

        return byteIndice;
    }

    private FascicoloType buildFascicoloVersato(FasFascicolo ff)
            throws DatatypeConfigurationException {
        // Accedo al contenuto dell’elenco mediante la vista ELV_CREA_LIS_FASC_ELENCO
        // che fornisce l’insieme dei fascicoli contenuti nell’elenco (da ordinare per timestamp di
        // versamento)
        // e per ogni fascicolo fornisce le informazioni con cui valorizzare il tag FascicoloVersato
        ElvVCreaLisFascElenco fascElenco = indiceHelper.retrieveElvVCreaLisFascElenco(ff);
        // Creo elemento FascicoloType
        FascicoloType fascicoloVersato = new FascicoloType();
        // lettura dell'urn del fascicolo
        String urnFascicolo = fascElenco.getDsUrnFascicolo();
        fascicoloVersato.setUrnFascicolo(urnFascicolo);
        // lettura dell'urn dell'indice sip del fascicolo
        String urnSipFascicolo = fascElenco.getDsUrnSipFascicolo();
        fascicoloVersato.setUrnIndiceSIPFascicolo(urnSipFascicolo);

        // TODO: verificare date
        XMLGregorianCalendar dataVersamento = null;
        XMLGregorianCalendar dataApertura = null;
        XMLGregorianCalendar dataChiusura = null;
        if (fascElenco.getTsIniSes() != null) {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(fascElenco.getTsIniSes());
            dataVersamento = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        }
        if (fascElenco.getDtApeFascicolo() != null) {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(fascElenco.getDtApeFascicolo());
            dataApertura = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        }
        if (fascElenco.getDtChiuFascicolo() != null) {
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(fascElenco.getDtChiuFascicolo());
            dataChiusura = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
        }
        fascicoloVersato.setDataVersamento(dataVersamento);
        fascicoloVersato.setDataApertura(dataApertura);
        fascicoloVersato.setDataChiusura(dataChiusura);
        fascicoloVersato.setAnno(fascElenco.getAaFascicolo().toBigInteger());

        // lettura dell'urn del rapporto di versamento fascicolo
        String urnRappVers = fascElenco.getDsUrnRappVers();
        fascicoloVersato.setUrnRapportoVersamento(urnRappVers);
        fascicoloVersato.setTipoFascicolo(fascElenco.getNmTipoFascicolo());
        fascicoloVersato.setOggetto(fascElenco.getDsOggettoFascicolo());
        fascicoloVersato.setNumero(fascElenco.getCdKeyFascicolo());
        fascicoloVersato.setNumeroUnitaDocumentarie(fascElenco.getNiUnitaDoc().toBigInteger());
        fascicoloVersato.setAlgoritmoHash(fascElenco.getDsAlgoHashFascicolo());
        fascicoloVersato.setEncodingHash(fascElenco.getCdEncodingHashFascicolo());
        fascicoloVersato.setHashIndiceSIP(fascElenco.getDsHashFascicolo());

        return fascicoloVersato;
    }

}
