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

package it.eng.parer.job.indiceAipSerieUd.ejb;

import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import javax.naming.NamingException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.AroUdAppartVerSerie;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.AroVerIndiceAipUd;
import it.eng.parer.entity.SerIxVolVerSerie;
import it.eng.parer.entity.SerVerSerie;
import it.eng.parer.entity.SerVolVerSerie;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.job.indiceAipSerieUd.helper.CreazioneIndiceVolumeSerieUdHelper;
import it.eng.parer.job.indiceAipSerieUd.utils.CreazioneIndiceVolumeSerieUdUtil;
import it.eng.parer.serie.xml.indiceVolumeSerie.IndiceVolumeSerie;
import it.eng.parer.util.helper.UniformResourceNameUtilHelper;
import it.eng.parer.viewEntity.AroVDtVersMaxByUnitaDoc;
import it.eng.parer.viewEntity.SerVCreaIxVolSerieUd;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.ejb.XmlContextCache;
import it.eng.parer.ws.utils.Costanti;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.CostantiDB.TipiHash;
import it.eng.parer.ws.utils.HashCalculator;
import it.eng.parer.ws.utils.MessaggiWSFormat;

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "ElaborazioneRigaIndiceVolumeSerieUd")
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class ElaborazioneRigaIndiceVolumeSerieUd {

    @EJB
    private CreazioneIndiceVolumeSerieUdHelper civsudHelper;
    @EJB
    private ConfigurationHelper configurationHelper;
    @EJB
    private UniformResourceNameUtilHelper urnHelper;
    @EJB
    private XmlContextCache xmlContextCache;
    Logger log = LoggerFactory.getLogger(ElaborazioneRigaIndiceVolumeSerieUd.class);

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void creaVolumeVerSerie(Long idVerSerie, BigDecimal niUnitaDocVol, BigDecimal numeroTotaleVolumi)
            throws DatatypeConfigurationException, IOException, JAXBException, NoSuchAlgorithmException,
            NamingException, ParseException, ParerInternalError {
        String sistemaConservazione = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);
        boolean indiceCreato = false;
        int progressivoVolume = 0;

        SerVerSerie verSerie = civsudHelper.findById(SerVerSerie.class, idVerSerie);
        String ambiente = verSerie.getSerSerie().getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente();
        String ente = verSerie.getSerSerie().getOrgStrut().getOrgEnte().getNmEnte();
        String struttura = verSerie.getSerSerie().getOrgStrut().getNmStrut();
        long idStrut = verSerie.getSerSerie().getOrgStrut().getIdStrut();

        /*
         * Determina le unità documentarie appartenenti al contenuto di tipo EFFETTIVO della versione serie per le quali
         * la foreign key al volume sia null (in pratica quelle ancora non appartenenti ad un volume...), ordinandole
         */
        List<AroUdAppartVerSerie> udAppartList = civsudHelper.getUdEffettiveSenzaVolume(idVerSerie);

        if (!udAppartList.isEmpty()) {
            /* Crea volume per la versione serie */
            log.info("{} --- Creazione Indice Aip Versione Serie Ud --- Creazione Volume Versione Serie ",
                    ElaborazioneRigaIndiceVolumeSerieUd.class.getSimpleName());
            SerVolVerSerie volVerSerie = civsudHelper.registraVolVerSerie(idVerSerie);

            boolean primaAppartenenzaInserita = false;

            for (AroUdAppartVerSerie udAppart : udAppartList) {
                // Se è la PRIMA APPARTENENZA dell'ud al contenuto serie
                CSVersatore versatore = this.getVersatoreUd(udAppart.getAroUnitaDoc(), sistemaConservazione);
                CSChiave chiave = this.getChiaveUd(udAppart.getAroUnitaDoc());
                /*
                 *
                 * Gestione KEY NORMALIZED / URN PREGRESSI
                 *
                 *
                 */
                // 1. se il numero normalizzato sull’unità doc nel DB è nullo ->
                // il sistema aggiorna ARO_UNITA_DOC
                DateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT_DATE_TYPE);
                String dataInizioParam = configurationHelper
                        .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.DATA_INIZIO_CALC_NUOVI_URN);
                Date dataInizio = dateFormat.parse(dataInizioParam);

                // controllo : dtCreazione <= dataInizioCalcNuoviUrn
                AroVerIndiceAipUd aroVerIndiceAipUd = civsudHelper.findById(AroVerIndiceAipUd.class,
                        udAppart.getIdVerIndiceAipUd().longValue());
                if (!aroVerIndiceAipUd.getDtCreazione().after(dataInizio)) {

                    // controllo : dtVersMax <= dataInizioCalcNuoviUrn
                    AroVDtVersMaxByUnitaDoc aroVDtVersMaxByUd = civsudHelper
                            .getAroVDtVersMaxByUd(udAppart.getAroUnitaDoc().getIdUnitaDoc());
                    if (!aroVDtVersMaxByUd.getDtVersMax().after(dataInizio)
                            && StringUtils.isBlank(udAppart.getAroUnitaDoc().getCdKeyUnitaDocNormaliz())) {
                        // calcola e verifica la chiave normalizzata
                        String cdKeyNormalized = MessaggiWSFormat
                                .normalizingKey(udAppart.getAroUnitaDoc().getCdKeyUnitaDoc()); // base
                        if (urnHelper.existsCdKeyNormalized(
                                udAppart.getAroUnitaDoc().getDecRegistroUnitaDoc().getIdRegistroUnitaDoc(),
                                udAppart.getAroUnitaDoc().getAaKeyUnitaDoc(),
                                udAppart.getAroUnitaDoc().getCdKeyUnitaDoc(), cdKeyNormalized)) {
                            // urn normalizzato già presente su sistema
                            throw new ParerInternalError("Il numero normalizzato per l'unità documentaria "
                                    + MessaggiWSFormat.formattaUrnPartUnitaDoc(chiave) + " della struttura "
                                    + versatore.getEnte() + "/" + versatore.getStruttura()
                                    + " contenuta in un fascicolo è già presente");
                        } else {
                            // cd key normalized (se calcolato)
                            if (StringUtils.isBlank(udAppart.getAroUnitaDoc().getCdKeyUnitaDocNormaliz())) {
                                udAppart.getAroUnitaDoc().setCdKeyUnitaDocNormaliz(cdKeyNormalized);
                            }
                        }
                    }

                    // 2. verifica pregresso
                    // A. check data massima versamento recuperata in precedenza rispetto parametro
                    // su db
                    if (!aroVDtVersMaxByUd.getDtVersMax().after(dataInizio)) {
                        // B. eseguo registra urn comp pregressi
                        urnHelper.scriviUrnCompPreg(udAppart.getAroUnitaDoc(), versatore, chiave);
                        // C. eseguo registra urn sip pregressi
                        // C.1. eseguo registra urn sip pregressi ud
                        urnHelper.scriviUrnSipUdPreg(udAppart.getAroUnitaDoc(), versatore, chiave);
                        // C.2. eseguo registra urn sip pregressi documenti aggiunti
                        urnHelper.scriviUrnSipDocAggPreg(udAppart.getAroUnitaDoc(), versatore, chiave);
                        // C.3. eseguo registra urn pregressi upd
                        urnHelper.scriviUrnSipUpdPreg(udAppart.getAroUnitaDoc(), versatore, chiave);
                    }

                    // 3. eseguo registra urn aip pregressi
                    urnHelper.scriviUrnAipUdPreg(udAppart.getAroUnitaDoc(), versatore, chiave);
                }
                if (!primaAppartenenzaInserita) {
                    volVerSerie.setIdFirstUdAppartVol(new BigDecimal(udAppart.getIdUdAppartVerSerie()));
                    primaAppartenenzaInserita = true;
                }

                if (volVerSerie.getAroUdAppartVerSeries() == null) {
                    volVerSerie.setAroUdAppartVerSeries(new ArrayList<>());
                }

                // Registro l'unità documentaria appartenente al contenuto di tipo effettivo
                volVerSerie.getAroUdAppartVerSeries().add(udAppart);
                // Incremento di 1 il numero di unità documentarie
                volVerSerie.setNiUnitaDocVol(volVerSerie.getNiUnitaDocVol().add(BigDecimal.ONE));
                udAppart.setSerVolVerSerie(volVerSerie);

                /*
                 * Se per il volume corrente, il numero di unità doc appartenenti al volume corrente è pari al numero di
                 * unità doc con cui creare i volumi
                 */
                if (volVerSerie.getNiUnitaDocVol().compareTo(niUnitaDocVol) == 0) {
                    volVerSerie.setIdLastUdAppartVol(new BigDecimal(udAppart.getIdUdAppartVerSerie()));

                    progressivoVolume = creaIndice(volVerSerie.getIdVolVerSerie(), numeroTotaleVolumi, ambiente, ente,
                            struttura, volVerSerie.getSerVerSerie().getSerSerie().getCdCompositoSerie(), idStrut);
                    indiceCreato = true;
                    break;
                }
            } // Fine appartenenza ud al contenuto serie

            if (!indiceCreato) {
                AroUdAppartVerSerie lastUdAppartVerSerie = udAppartList.get(udAppartList.size() - 1);
                volVerSerie.setIdLastUdAppartVol(new BigDecimal(lastUdAppartVerSerie.getIdUdAppartVerSerie()));
                progressivoVolume = creaIndice(volVerSerie.getIdVolVerSerie(), numeroTotaleVolumi, ambiente, ente,
                        struttura, volVerSerie.getSerVerSerie().getSerSerie().getCdCompositoSerie(), idStrut);
            }
        }
        log.info(
                "{} --- Creazione Indice Aip Versione Serie Ud --- Volume numero {} e relativo indice creati con successo!",
                ElaborazioneRigaIndiceVolumeSerieUd.class.getSimpleName(), progressivoVolume);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public int creaIndice(long idVolVerSerie, BigDecimal numeroTotaleVolumi, String ambiente, String ente,
            String struttura, String cdCompositoSerie, long idStrut) throws NamingException, IOException, JAXBException,
            NoSuchAlgorithmException, DatatypeConfigurationException {
        CreazioneIndiceVolumeSerieUdUtil indiceVolumeSerieUtil = new CreazioneIndiceVolumeSerieUdUtil();

        log.info("{} --- Creazione Indice Aip Versione Serie Ud --- Creazione XML Volume",
                ElaborazioneRigaIndiceVolumeSerieUd.class.getSimpleName());

        // Recupero il volume versione serie per il quale creare l'indice
        SerVCreaIxVolSerieUd creaVol = civsudHelper.getSerVCreaIxVolSerieUd(idVolVerSerie);

        IndiceVolumeSerie indiceVolumeSerie = indiceVolumeSerieUtil.generaIndiceVolumeSerie(creaVol,
                numeroTotaleVolumi);

        Long progressivoVolume = indiceVolumeSerie.getProgressivoVolume().longValue();

        /* Eseguo il marshalling degli oggetti creati nell'util per salvarli */
        StringWriter tmpWriter = new StringWriter();
        Marshaller tmpMarshaller = xmlContextCache.getCreazioneIndiceVolumeSerieCtx_IndiceVolumeSerie()
                .createMarshaller();
        tmpMarshaller.marshal(indiceVolumeSerie, tmpWriter);
        tmpMarshaller.setSchema(xmlContextCache.getSchemaOfcreazioneIndiceVolumeSerie());

        /* Calcolo l'hash in SHA-256 ed hexBinary */
        String hash = new HashCalculator().calculateHashSHAX(tmpWriter.toString(), TipiHash.SHA_256).toHexBinary();

        log.info("{} --- Creazione Indice Aip Versione Serie Ud --- Registrazione Indice Volume",
                ElaborazioneRigaIndiceVolumeSerieUd.class.getSimpleName());
        /* Persisto nella tabella relativa all'indice volume, vale a dire SER_IX_VOL_VER_SERIE */
        SerIxVolVerSerie serIxVolVerSerie = civsudHelper.registraSerIxVolVerSerie(idVolVerSerie,
                indiceVolumeSerie.getVersioneXSDIndiceVolumeSerie(), tmpWriter.toString(), hash,
                new BigDecimal(idStrut));
        // EVO#16492
        CSVersatore csv = new CSVersatore();
        csv.setStruttura(struttura);
        csv.setEnte(ente);
        csv.setAmbiente(ambiente);
        // sistema (new URN)
        String sistemaConservazione = configurationHelper
                .getValoreParamApplicByApplic(CostantiDB.ParametroAppl.NM_SISTEMACONSERVAZIONE);
        csv.setSistemaConservazione(sistemaConservazione);

        // calcolo parte urn NORMALIZZATO
        String tmpUrnNorm = MessaggiWSFormat.formattaUrnPartVersatore(csv, true, Costanti.UrnFormatter.VERS_FMT_STRING);

        /* Calcolo e persisto urn del volume della serie */
        String urnBaseNorm = MessaggiWSFormat.formattaBaseUrnSerie(tmpUrnNorm,
                MessaggiWSFormat.normalizingKey(cdCompositoSerie));

        urnHelper.scriviSerUrnIxVolVerSerie(serIxVolVerSerie, creaVol.getDsIdSerie(), urnBaseNorm,
                creaVol.getCdVerSerie(), progressivoVolume.intValue());

        return progressivoVolume.intValue();
    }

    public CSChiave getChiaveUd(AroUnitaDoc ud) {
        CSChiave csc = new CSChiave();
        csc.setTipoRegistro(ud.getCdRegistroKeyUnitaDoc());
        csc.setAnno(ud.getAaKeyUnitaDoc().longValue());
        csc.setNumero(ud.getCdKeyUnitaDoc());

        return csc;
    }

    public CSVersatore getVersatoreUd(AroUnitaDoc ud, String sistemaConservazione) {
        CSVersatore csv = new CSVersatore();
        csv.setStruttura(ud.getOrgStrut().getNmStrut());
        csv.setEnte(ud.getOrgStrut().getOrgEnte().getNmEnte());
        csv.setAmbiente(ud.getOrgStrut().getOrgEnte().getOrgAmbiente().getNmAmbiente());
        // sistema (new URN)
        csv.setSistemaConservazione(sistemaConservazione);

        return csv;
    }
}
