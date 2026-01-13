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

package it.eng.parer.job.indiceAipFascicoli.elenchi;

import it.eng.parer.aipelencoFascicoli.xml.indice.ContenutoSinteticoType;
import it.eng.parer.aipelencoFascicoli.xml.indice.DescrizioneElencoIndiciAIPType;
import it.eng.parer.aipelencoFascicoli.xml.indice.ElencoIndiciAIP;
import it.eng.parer.aipelencoFascicoli.xml.indice.ElencoIndiciAIP.ContenutoAnalitico;
import it.eng.parer.aipelencoFascicoli.xml.indice.ElencoVersamentoDiOrigineType;
import it.eng.parer.aipelencoFascicoli.xml.indice.IndiceAIPType;
import it.eng.parer.aipelencoFascicoli.xml.indice.VersatoreType;
import it.eng.parer.elencoVersFascicoli.helper.ElencoVersFascicoliHelper;
import it.eng.parer.elencoVersFascicoli.utils.ElencoEnums;
import it.eng.parer.entity.ElvElencoVersFasc;
import it.eng.parer.entity.ElvElencoVersFascDaElab;
import it.eng.parer.entity.ElvFileElencoVersFasc;
import it.eng.parer.entity.ElvStatoElencoVersFasc;
import it.eng.parer.entity.FasFascicolo;
import it.eng.parer.entity.FasStatoFascicoloElenco;
import it.eng.parer.entity.FasVerAipFascicolo;
import it.eng.parer.entity.OrgAmbiente;
import it.eng.parer.entity.OrgEnte;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.ElvElencoVersFascDaElab.TiStatoElencoFascDaElab;
import it.eng.parer.entity.constraint.ElvStatoElencoVersFasc.TiStatoElencoFasc;
import it.eng.parer.entity.constraint.FasFascicolo.TiStatoFascElencoVers;
import it.eng.parer.entity.constraint.FasStatoFascicoloElenco.TiStatoFascElenco;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.job.indiceAipFascicoli.helper.CreazioneIndiceAipFascicoliHelper;
import it.eng.parer.viewEntity.ElvVChkSoloFascAnnul;
import it.eng.parer.viewEntity.ElvVLisIxAipFascByEle;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.ejb.XmlContextCache;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.HashCalculator;
import it.eng.parer.ws.utils.CostantiDB.TipiEncBinari;
import it.eng.parer.ws.utils.CostantiDB.TipiHash;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author DiLorenzo_F
 */
@Stateless
@LocalBean
public class ElaborazioneElencoIndiceAipFascicoli {

    private static final Logger logger = LoggerFactory
            .getLogger(ElaborazioneElencoIndiceAipFascicoli.class);

    @Resource
    private SessionContext context;
    @EJB
    private CreazioneIndiceAipFascicoliHelper ciafHelper;
    @EJB
    private ElencoVersFascicoliHelper elencoHelper;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private XmlContextCache xmlContextCache;

    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public void creaElencoIndiciAIPFascicoli(long idElencoVersFasc, long idLogJob)
            throws ParerInternalError, IOException, DatatypeConfigurationException, JAXBException,
            ParseException, NoSuchAlgorithmException {
        ElvElencoVersFasc elenco = ciafHelper.findByIdWithLock(ElvElencoVersFasc.class,
                idElencoVersFasc);

        ElvVChkSoloFascAnnul fascSoloAnnul = ciafHelper.findViewById(ElvVChkSoloFascAnnul.class,
                BigDecimal.valueOf(idElencoVersFasc));
        if (fascSoloAnnul.getFlSoloFascAnnul().equals("1")) {
            String nota = elenco.getNtElencoChiuso();
            elenco.setNtElencoChiuso((StringUtils.isNotBlank(nota) ? nota + ";" : "")
                    + "L'elenco contiene solo versamenti annullati");
            context.getBusinessObject(ElaborazioneElencoIndiceAipFascicoli.class)
                    .setElencoCompletatoTxReq(elenco.getIdElencoVersFasc());
        } else {
            ElvElencoVersFascDaElab elencoDaElab = elencoHelper.retrieveElencoInQueue(elenco);
            String sistema = configurationHelper
                    .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);
            ElencoIndiciAIP elencoIndiciAip = creaElencoIndiceAipFascicoliMarshallObject(elenco,
                    sistema);
            String elencoIndiciAipString = marshallElenco(elencoIndiciAip);
            VersatoreType versatoreType = elencoIndiciAip.getVersatore();
            String hash = new HashCalculator()
                    .calculateHashSHAX(elencoIndiciAipString, TipiHash.SHA_256).toHexBinary();

            ElvFileElencoVersFasc fileElencoVersFasc = new ElvFileElencoVersFasc();
            fileElencoVersFasc
                    .setTiFileElencoVers(ElencoEnums.FileTypeEnum.ELENCO_INDICI_AIP.name());
            fileElencoVersFasc.setBlFileElencoVers(elencoIndiciAipString.getBytes("UTF-8"));
            fileElencoVersFasc.setIdStrut(BigDecimal.valueOf(elenco.getOrgStrut().getIdStrut()));

            Date dataCreazione = elencoIndiciAip.getDescrizioneElencoIndiciAIP().getDataCreazione()
                    .toGregorianCalendar().getTime();
            fileElencoVersFasc.setDtCreazioneFile(dataCreazione);
            fileElencoVersFasc.setDsHashFile(hash);
            CSVersatore versatore = new CSVersatore();
            versatore.setSistemaConservazione(sistema);
            versatore.setAmbiente(versatoreType.getAmbiente());
            versatore.setEnte(versatoreType.getEnte());
            versatore.setStruttura(versatoreType.getStruttura());
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            // salvo ORIGINALE
            final String urnElencoIndiciAIP = MessaggiWSFormat
                    .formattaUrnElencoIndiciAIPFascicoliNonFirmati(
                            MessaggiWSFormat.formattaUrnPartVersatore(versatore),
                            Long.toString(elenco.getIdElencoVersFasc()),
                            sdf.format(elenco.getTsCreazioneElenco()));
            fileElencoVersFasc.setDsUrnFile(urnElencoIndiciAIP);
            // salvo NORMALIZZATO
            final String urnNormalizElencoIndiciAIP = MessaggiWSFormat
                    .formattaUrnElencoIndiciAIPFascicoliNonFirmati(
                            MessaggiWSFormat.formattaUrnPartVersatore(versatore, true,
                                    Costanti.UrnFormatter.VERS_FMT_STRING),
                            Long.toString(elenco.getIdElencoVersFasc()),
                            sdf.format(elenco.getTsCreazioneElenco()));
            fileElencoVersFasc.setDsUrnNormalizFile(urnNormalizElencoIndiciAIP);
            fileElencoVersFasc.setDsAlgoHashFile(TipiHash.SHA_256.descrivi());
            fileElencoVersFasc.setCdEncodingHashFile(TipiEncBinari.HEX_BINARY.descrivi());
            fileElencoVersFasc.setCdVerXsdFile(Costanti.VERSIONE_ELENCO_INDICE_AIP_FASC);

            if (elenco.getElvFileElencoVersFasc() == null) {
                elenco.setElvFileElencoVersFasc(new ArrayList<>());
            }
            elenco.addElvFileElencoVersFasc(fileElencoVersFasc);

            // registro un nuovo stato = ELENCO_INDICI_AIP_CREATO
            logger.debug(
                    "Creazione Indice AIP Fascicoli - Registro un nuovo stato per l’elenco a ELENCO_INDICI_AIP_CREATO");
            ElvStatoElencoVersFasc statoElencoVersFasc = new ElvStatoElencoVersFasc();
            statoElencoVersFasc.setElvElencoVersFasc(elenco);
            statoElencoVersFasc.setTsStato(new Date());
            statoElencoVersFasc.setTiStato(TiStatoElencoFasc.ELENCO_INDICI_AIP_CREATO);

            elenco.getElvStatoElencoVersFascicoli().add(statoElencoVersFasc);

            // aggiorno l’elenco da elaborare assegnando stato = ELENCO_INDICI_AIP_CREATO
            elencoDaElab.setTiStato(TiStatoElencoFascDaElab.ELENCO_INDICI_AIP_CREATO);

            // aggiorno l’elenco specificando l’identificatore dello stato corrente
            Long idStatoElencoVersFasc = elencoHelper
                    .retrieveStatoElencoByIdElencoVersFascStato(elenco.getIdElencoVersFasc(),
                            TiStatoElencoFasc.ELENCO_INDICI_AIP_CREATO)
                    .getIdStatoElencoVersFasc();
            elenco.setIdStatoElencoVersFascCor(new BigDecimal(idStatoElencoVersFasc));

            elenco.setNiIndiciAip(
                    new BigDecimal(elencoIndiciAip.getContenutoSintetico().getNumeroIndiciAIP()));

            Set<Long> idFascicoloSet = elencoHelper
                    .retrieveFascVersInElencoAipCreato(elenco.getIdElencoVersFasc());
            for (Long idFascicolo : idFascicoloSet) {
                FasFascicolo ff = ciafHelper.findById(FasFascicolo.class, idFascicolo);
                // Controllo se il fascicolo è annullato
                boolean annullato = elencoHelper.checkFascicoloAnnullato(ff);
                if (!annullato) { // fascicolo non annullato
                    // Lock su ff
                    elencoHelper.lockFasFascicolo(ff);

                    // Aggiorno il fascicolo assegnando stato nell’elenco pari a
                    // IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO
                    if (ff.getElvElencoVersFasc().getIdElencoVersFasc().compareTo(
                            elencoDaElab.getElvElencoVersFasc().getIdElencoVersFasc()) == 0) {
                        logger.debug(
                                "Creazione Indice AIP Fascicolo - Aggiorno lo stato fascicolo a IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO");
                        ff.setTiStatoFascElencoVers(
                                TiStatoFascElencoVers.IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO);
                    }

                    // Registro un nuovo stato nell’elenco del fascicolo specificando stato =
                    // IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO
                    logger.debug(
                            "Creazione Indice AIP Fascicolo - Registro un nuovo stato nell’elenco del fascicolo a IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO");
                    FasStatoFascicoloElenco statoFascicoloElenco = new FasStatoFascicoloElenco();
                    statoFascicoloElenco.setFasFascicolo(ff);
                    statoFascicoloElenco.setTsStato(new Date());
                    statoFascicoloElenco.setTiStatoFascElencoVers(
                            TiStatoFascElenco.IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO);

                    ff.getFasStatoFascicoloElencos().add(statoFascicoloElenco);

                    ciafHelper.insertEntity(statoFascicoloElenco, true);
                }
            }
        }
    }

    private ElencoIndiciAIP creaElencoIndiceAipFascicoliMarshallObject(ElvElencoVersFasc elenco,
            String sistema) throws DatatypeConfigurationException {
        ElencoIndiciAIP objElenco = new ElencoIndiciAIP();
        objElenco.setVersioneElencoIndiciAIP(Costanti.VERSIONE_ELENCO_INDICE_AIP_FASC);

        popolaVersatore(objElenco, elenco.getOrgStrut());
        popolaDescrizioniUrnElenco(objElenco, elenco, sistema);

        objElenco.setContenutoSintetico(new ContenutoSinteticoType());

        List<FasVerAipFascicolo> fasVerAipFascNoAnnul = ciafHelper
                .retrieveFasVerAipFascicoloOrdered(elenco.getIdElencoVersFasc());
        objElenco.getContenutoSintetico()
                .setNumeroIndiciAIP(BigInteger.valueOf(fasVerAipFascNoAnnul.size()));

        List<ElvVLisIxAipFascByEle> elvVLisIxAipFascByEle = ciafHelper
                .retrieveElvVLisIxAipFascByEleOrdered(elenco.getIdElencoVersFasc());

        popolaContenutoAnalitico(objElenco, elvVLisIxAipFascByEle, sistema);

        return objElenco;
    }

    private void popolaVersatore(ElencoIndiciAIP objElenco, OrgStrut strut) {
        OrgEnte ente = strut.getOrgEnte();
        OrgAmbiente ambiente = ente.getOrgAmbiente();

        objElenco.setVersatore(new VersatoreType());
        objElenco.getVersatore().setAmbiente(ambiente.getNmAmbiente());
        objElenco.getVersatore().setEnte(ente.getNmEnte());
        objElenco.getVersatore().setStruttura(strut.getNmStrut());
    }

    private void popolaDescrizioniUrnElenco(ElencoIndiciAIP objElenco, ElvElencoVersFasc elenco,
            String sistema) throws DatatypeConfigurationException {
        VersatoreType versatoreType = objElenco.getVersatore();
        CSVersatore versatore = new CSVersatore();
        versatore.setSistemaConservazione(sistema);
        versatore.setAmbiente(versatoreType.getAmbiente());
        versatore.setEnte(versatoreType.getEnte());
        versatore.setStruttura(versatoreType.getStruttura());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        // urn dell'elenco degli indici AIP
        final String urnElencoIndiciAIP = MessaggiWSFormat.formattaUrnElencoIndiciAIPFascicoli(
                MessaggiWSFormat.formattaUrnPartVersatore(versatore),
                Long.toString(elenco.getIdElencoVersFasc()),
                sdf.format(elenco.getTsCreazioneElenco()));
        // urn dell'elenco di versamento
        final String urnIndiceEdvFascicoli = MessaggiWSFormat.formattaUrnIndiceEdvFascicoli(
                MessaggiWSFormat.formattaUrnPartVersatore(versatore),
                Long.toString(elenco.getIdElencoVersFasc()),
                sdf.format(elenco.getTsCreazioneElenco()));
        objElenco.setDescrizioneElencoIndiciAIP(new DescrizioneElencoIndiciAIPType());
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(Calendar.getInstance().getTime());
        objElenco.getDescrizioneElencoIndiciAIP()
                .setDataCreazione(DatatypeFactory.newInstance().newXMLGregorianCalendar(c));

        objElenco.getDescrizioneElencoIndiciAIP().setUrn(urnElencoIndiciAIP);
        objElenco.setElencoVersamentoDiOrigine(new ElencoVersamentoDiOrigineType());
        objElenco.getElencoVersamentoDiOrigine()
                .setIdElenco(new BigDecimal(elenco.getIdElencoVersFasc()).toBigInteger());
        objElenco.getElencoVersamentoDiOrigine().setUrnElenco(urnIndiceEdvFascicoli);
    }

    private void popolaContenutoAnalitico(ElencoIndiciAIP objElenco,
            List<ElvVLisIxAipFascByEle> indiciAip, String sistema) {
        objElenco.setContenutoAnalitico(new ContenutoAnalitico());
        for (ElvVLisIxAipFascByEle indiceAip : indiciAip) {
            IndiceAIPType indice = new IndiceAIPType();

            String urnIndiceAip = MessaggiWSFormat.formattaUrnIndiceAipElenchiFascicoli(sistema,
                    indiceAip.getNmEnte(), indiceAip.getNmStrut(),
                    indiceAip.getAaFascicolo().toString(), indiceAip.getCdKeyFascicolo(),
                    indiceAip.getCdVerAip());
            indice.setUrnIndiceAIP(urnIndiceAip);
            indice.setHashIndiceAIP(indiceAip.getDsHashFile());

            objElenco.getContenutoAnalitico().getIndiceAIP().add(indice);
        }
    }

    private String marshallElenco(ElencoIndiciAIP elencoIndiciAIP)
            throws IOException, JAXBException {
        // byte[] byteIndice;
        StringWriter tmpWriter = new StringWriter();
        Marshaller jaxbMarshaller = xmlContextCache.getElencoIndiciAipFascicoliCtx()
                .createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        jaxbMarshaller.marshal(elencoIndiciAIP, tmpWriter);
        tmpWriter.flush();
        // byteIndice = tmpWriter.toString().getBytes("UTF-8");
        return tmpWriter.toString();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void setElencoCompletatoTxReq(long idElencoVersFasc) {
        setElencoCompletato(idElencoVersFasc);
    }

    private void setElencoCompletato(long idElencoVersFasc) {
        final ElvElencoVersFasc elenco = elencoHelper.retrieveElencoById(idElencoVersFasc);
        ElvElencoVersFascDaElab elencoDaElab = elencoHelper.retrieveElencoInQueue(elenco);

        // Registro un nuovo stato per l’elenco assegnando stato = COMPLETATO
        ElvStatoElencoVersFasc statoElencoVersFasc = new ElvStatoElencoVersFasc();
        statoElencoVersFasc.setElvElencoVersFasc(elencoDaElab.getElvElencoVersFasc());
        statoElencoVersFasc.setTsStato(new Date());
        statoElencoVersFasc.setTiStato(TiStatoElencoFasc.COMPLETATO);

        elencoDaElab.getElvElencoVersFasc().getElvStatoElencoVersFascicoli()
                .add(statoElencoVersFasc);

        /* Aggiorno l’elenco specificando l’identificatore dello stato corrente */
        Long idStatoElencoVersFasc = elencoHelper.getStatoElencoByIdElencoVersFascStato(
                elencoDaElab.getElvElencoVersFasc().getIdElencoVersFasc(),
                TiStatoElencoFasc.COMPLETATO).getIdStatoElencoVersFasc();
        elencoDaElab.getElvElencoVersFasc()
                .setIdStatoElencoVersFascCor(new BigDecimal(idStatoElencoVersFasc));

        ciafHelper.removeEntity(elencoDaElab, true);
    }
}
