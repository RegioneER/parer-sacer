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
package it.eng.parer.ws.utils;

import java.text.MessageFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.CSChiaveFasc;
import it.eng.parer.ws.dto.CSChiaveSerie;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.utils.Costanti.CategoriaDocumento;

/**
 *
 * @author Fioravanti_F
 */
public class MessaggiWSFormat {

    private MessaggiWSFormat() {
        throw new IllegalStateException("Utility class");
    }

    public static String bonificaUrnPerNomeFile(String urn) {
        return urn.replaceAll("[^A-Za-z0-9\\. _-]", "_");
    }

    public static String bonificaID(String iD) {
        return iD.replace(" ", "_");
    }

    public static String formattaUrnPartVersatore(CSVersatore versatore) {
        return formattaUrnPartVersatore(versatore, false, Costanti.UrnFormatter.VERS_FMT_STRING);
    }

    public static String formattaUrnPartVersatore(CSVersatore versatore, boolean toNormalize,
            String fmtUsed) {
        if (!toNormalize) {
            return MessageFormat.format(fmtUsed,
                    StringUtils.isNotBlank(versatore.getSistemaConservazione())
                            ? versatore.getSistemaConservazione()
                            : versatore.getAmbiente(),
                    versatore.getEnte(), versatore.getStruttura());
        } else {
            return MessageFormat.format(fmtUsed,
                    StringUtils.isNotBlank(versatore.getSistemaConservazione())
                            ? MessaggiWSFormat.normalizingKey(versatore.getSistemaConservazione())
                            : MessaggiWSFormat.normalizingKey(versatore.getAmbiente()),
                    MessaggiWSFormat.normalizingKey(versatore.getEnte()),
                    MessaggiWSFormat.normalizingKey(versatore.getStruttura()));
        }
    }

    public static String formattaUrnPartVersatoreSistema(String sistema, CSVersatore versatore) {
        return MessageFormat.format(Costanti.UrnFormatter.VERS_FMT_STRING, sistema,
                versatore.getEnte(), versatore.getStruttura());
    }

    public static String formattaUrnPartUnitaDoc(CSChiave chiave) {
        return formattaUrnPartUnitaDoc(chiave, false, Costanti.UrnFormatter.UD_FMT_STRING);
    }

    public static String formattaUrnPartUnitaDoc(CSChiave chiave, boolean toNormalize,
            String fmtUsed) {
        if (!toNormalize) {
            return MessageFormat.format(fmtUsed, chiave.getTipoRegistro(),
                    chiave.getAnno().toString(), chiave.getNumero());
        } else {
            return MessageFormat.format(fmtUsed,
                    MessaggiWSFormat.normalizingKey(chiave.getTipoRegistro()),
                    chiave.getAnno().toString(),
                    MessaggiWSFormat.normalizingKey(chiave.getNumero()));
        }
    }

    // old urn
    public static String formattaUrnPartDocumento(CategoriaDocumento categoria, int progressivo) {
        return MessageFormat.format(Costanti.UrnFormatter.DOC_FMT_STRING, categoria.getValoreDb(),
                progressivo);
    }

    //
    public static String formattaUrnPartDocumento(CategoriaDocumento categoria, int progressivo,
            boolean pgpad, String fmtUsed, String padfmtUsed) {
        return MessageFormat.format(fmtUsed, categoria.getValoreDb(),
                pgpad ? String.format(padfmtUsed, progressivo) : progressivo);
    }

    //
    public static String formattaChiaveDocumento(String chiaveUd, CategoriaDocumento categoria,
            int progressivo) {
        return MessageFormat.format(Costanti.UrnFormatter.CHIAVE_DOC_FMT_STRING, chiaveUd,
                formattaUrnPartDocumento(categoria, progressivo));
    }

    public static String formattaChiaveComponente(String chiaveDoc, int progressivoStrutDoc,
            long progressivoComp) {
        return MessageFormat.format(Costanti.UrnFormatter.CHIAVE_COMP_FMT_STRING, chiaveDoc,
                progressivoStrutDoc, progressivoComp);
    }

    //
    public static String formattaUrnPartComponente(String urnBase, long ordinePresentazione,
            String fmtUsed, String padfmtUsed) {
        return MessageFormat.format(fmtUsed, urnBase,
                String.format(padfmtUsed, ordinePresentazione));
    }

    //
    public static String formattaSubPathData(Date data) {
        SimpleDateFormat f = new SimpleDateFormat(Costanti.UrnFormatter.SPATH_DATA_FMT_STRING);
        return f.format(data);
    }

    public static String formattaSubPathVersatoreArk(CSVersatore versatore) {
        return MessageFormat.format(Costanti.UrnFormatter.SPATH_VERS_FMT_STRING,
                versatore.getAmbiente(), versatore.getEnte(), versatore.getStruttura());
    }

    public static String formattaSubPathUnitaDocArk(CSChiave chiave) {
        return MessageFormat.format(Costanti.UrnFormatter.SPATH_UD_FMT_STRING,
                chiave.getTipoRegistro(), chiave.getAnno().toString(), chiave.getNumero());
    }

    public static String formattaNomeFileArk(CategoriaDocumento categoria, int progressivo,
            int progressivoStrutDoc, long progressivoComp) {
        return MessageFormat.format(Costanti.UrnFormatter.SPATH_COMP_FMT_STRING,
                categoria.getValoreDb(), progressivo, progressivoStrutDoc, progressivoComp);
    }

    public static String formattaFilePathArk(String root, String rootVers, String pathData,
            String pathVersatore, String pathUd, String nomeFile) {
        return MessageFormat.format(Costanti.UrnFormatter.SPATH_FILE_FMT_STRING, root, rootVers,
                pathData, pathVersatore, pathUd, nomeFile);
    }

    public static String formattaFileLogRetrieve(CSVersatore versatore, CSChiave chiave) {
        return MessageFormat.format(Costanti.UrnFormatter.FNAME_LOG_TSM_RETRIEVE,
                versatore.getAmbiente(), versatore.getEnte(), versatore.getStruttura(),
                chiave.getTipoRegistro(), chiave.getAnno().toString(), chiave.getNumero());
    }

    public static Long formattaKeyPartAnnoMeseVers(Date dtVersamento) {
        Calendar date = Calendar.getInstance();
        date.setTime(dtVersamento);
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH) + 1;
        return (long) year * 100 + month;
    }

    //
    public static String formattaBaseUrnUnitaDoc(String versatore, String unitaDoc) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_UD_FMT_STRING, versatore, unitaDoc);
    }

    // old urn
    public static String formattaBaseUrnDoc(String versatore, String unitaDoc, String documento) {
        return formattaBaseUrnDoc(versatore, unitaDoc, documento,
                Costanti.UrnFormatter.URN_DOC_FMT_STRING);
    }

    //
    public static String formattaBaseUrnDoc(String versatore, String unitaDoc, String documento,
            String fmtUsed) {
        return MessageFormat.format(fmtUsed, versatore, unitaDoc, documento);
    }

    public static String formattaBaseUrnUpd(String versatore, String unitaDoc, String progressivo) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_UPD_FMT_STRING, versatore, unitaDoc,
                progressivo);
    }

    //
    public static String formattaUrnDocUniDoc(String urnBase) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_DOC_UNI_DOC_FMT_STRING, urnBase);
    }

    // old URN
    public static String formattaUrnIndiceSip(String urnBase) {
        return formattaUrnIndiceSip(urnBase, Costanti.UrnFormatter.URN_INDICE_SIP_FMT_STRING); // default
    }

    //
    public static String formattaUrnIndiceSip(String urnBase, String fmtUsed) {
        return MessageFormat.format(fmtUsed, urnBase);
    }

    public static String formattaUrnPiSip(String urnBase) {
        return formattaUrnPiSip(urnBase, Costanti.UrnFormatter.URN_PI_SIP_FMT_STRING);
    }

    public static String formattaUrnPiSip(String urnBase, String fmtUsed) {
        return MessageFormat.format(fmtUsed, urnBase);
    }

    public static String formattaUrnEsitoVers(String urnBase) {
        return formattaUrnEsitoVers(urnBase, Costanti.UrnFormatter.URN_ESITO_VERS_FMT_STRING);
    }

    public static String formattaUrnEsitoVers(String urnBase, String fmtUsed) {
        return MessageFormat.format(fmtUsed, urnBase);
    }

    // old urn
    public static String formattaUrnRappVers(String urnBase) {
        return formattaUrnRappVers(urnBase, Costanti.UrnFormatter.URN_RAPP_VERS_FMT_STRING);
    }

    //
    public static String formattaUrnRappVers(String urnBase, String fmtUsed) {
        return MessageFormat.format(fmtUsed, urnBase);
    }

    // MEV#23176
    public static String formattaUrnSip(String urnBase, String fmtUsed) {
        return MessageFormat.format(fmtUsed, urnBase);
    }
    // end MEV#23176

    //
    public static String formattaUrnUpdUniDoc(String urnBase) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_UPD_UNI_DOC_FMT_STRING, urnBase);
    }

    // old urn
    public static String formattaUrnIndiceAIP(String urnBase, String versioneAip) {
        return formattaUrnIndiceAIP(urnBase, versioneAip,
                Costanti.UrnFormatter.URN_INDICE_AIP_FMT_STRING);
    }

    //
    public static String formattaUrnIndiceAIP(String urnBase, String versioneAip, String fmtUsed) {
        return MessageFormat.format(fmtUsed, urnBase, versioneAip);
    }

    //
    public static String formattaUrnElencoVersamento(String sistema, String ente, String struttura,
            String dtCreazioneElenco, String idElenco) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_ELENCO_VERSAMENTO_FMT_STRING_V2,
                sistema, ente, struttura, dtCreazioneElenco, idElenco);
    }

    //
    public static String formattaUrnElencoIndice(String urnBase, String dtCreazioneElenco,
            String idElenco) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_ELENCO_INDICE_FMT_STRING, urnBase,
                dtCreazioneElenco, idElenco);
    }

    //
    public static String formattaUrnElencoIndiceFirmato(String urnBase, String dtCreazioneElenco,
            String idElenco) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_ELENCO_INDICE_FIRMATO_FMT_STRING,
                urnBase, dtCreazioneElenco, idElenco);
    }

    // old urn
    public static String formattaUrnElencoIndiciAIP(String sistema, String ente, String struttura,
            String idElenco) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_ELENCO_INDICI_AIP_FMT_STRING, sistema,
                ente, struttura, idElenco);
    }

    //
    public static String formattaUrnElencoIndiciAIP(String urnBase, String dtCreazioneElenco,
            String idElenco) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_ELENCO_INDICI_AIP_FMT_STRING_V2,
                urnBase, dtCreazioneElenco, idElenco);
    }

    // old urn
    public static String formattaUrnElencoIndiciAIPFirmati(String sistema, String ente,
            String struttura, String idElenco) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_ELENCO_AIP_FIRMATI_FMT_STRING,
                sistema, ente, struttura, idElenco);
    }

    //
    public static String formattaUrnElencoIndiciAIPFirmati(String urnBase, String dtCreazioneElenco,
            String idElenco) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_ELENCO_AIP_FIRMATI_FMT_STRING_V2,
                urnBase, dtCreazioneElenco, idElenco);
    }

    //
    public static String formattaUrnElencoIndiciAIPNonFirmati(String urnBase,
            String dtCreazioneElenco, String idElenco) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_ELV_INDICI_AIP_NON_FIRMATI_FMT_STRING,
                urnBase, dtCreazioneElenco, idElenco);
    }

    // old urn
    public static String formattaUrnFirmaElencoIndiciAIP(String sistema, String ente,
            String struttura, String idElenco) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_FIRMA_ELV_INDICI_AIP_FMT_STRING,
                sistema, ente, struttura, idElenco);
    }

    //
    public static String formattaUrnFirmaElencoIndiciAIP(String urnBase, String dtCreazioneElenco,
            String idElenco) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_FIRMA_ELV_INDICI_AIP_FMT_STRING_V2,
                urnBase, dtCreazioneElenco, idElenco);
    }

    // old urn
    public static String formattaUrnMarcaElencoIndiciAIP(String sistema, String ente,
            String struttura, String idElenco) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_MARCA_ELV_INDICI_AIP_FMT_STRING,
                sistema, ente, struttura, idElenco);
    }

    //
    public static String formattaUrnMarcaElencoIndiciAIP(String urnBase, String dtCreazioneElenco,
            String idElenco) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_MARCA_ELV_INDICI_AIP_FMT_STRING_V2,
                urnBase, dtCreazioneElenco, idElenco);
    }

    //
    // VOLUME
    //
    public static String formattaUrnIndiceVolumeConserv(String versatore, String idVolume) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_INDICE_VOLUME_CONSERV_FMT_STRING,
                versatore, idVolume);
    }

    //
    // SERIE
    //
    // old urn
    public static String formattaUrnIndiceVolumeSerie1(String codiceSerie, String versione,
            String progressivoVolume) {
        return formattaUrnIndiceVolumeSerie1(codiceSerie, versione, progressivoVolume,
                Costanti.UrnFormatter.URN_INDICE_VOLUME_FMT_STRING1);
    }

    //
    public static String formattaUrnIndiceVolumeSerie1(String urnBase, String versione,
            String progressivoVolume, String fmtUsed) {
        return MessageFormat.format(fmtUsed, urnBase, versione, progressivoVolume);
    }

    // old urn
    public static String formattaUrnIndiceAIPSerieUD(String versioneSerie, String versatore,
            String codiceSerie) {
        return formattaUrnIndiceAIPSerieUD1(versatore, codiceSerie, versioneSerie,
                Costanti.UrnFormatter.URN_INDICE_AIP_SERIE_UD_FMT_STRING);
    }

    //
    public static String formattaUrnIndiceAIPSerieUD1(String versatore, String codiceSerie,
            String versioneSerie, String fmtUsed) {
        return MessageFormat.format(fmtUsed, versatore, codiceSerie, versioneSerie);
    }

    //
    public static String formattaUrnIndiceAIPSerieUDNonFirmate(String urnBase,
            String versioneSerie) {
        return MessageFormat.format(
                Costanti.UrnFormatter.URN_INDICE_AIP_SERIE_UD_NON_FIRMATI_FMT_STRING, urnBase,
                versioneSerie);
    }

    // old urn
    public static String formattaUrnIndiceAIPSerieUDFir(String versatore, String codiceSerie,
            String versioneSerie) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_INDICE_AIP_SERIE_UD_FIR_FMT_STRING,
                versatore, codiceSerie, versioneSerie);
    }

    //
    public static String formattaUrnIndiceAIPSerieUDFir(String urnBase, String versioneSerie) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_INDICE_AIP_SERIE_UD_FIR_FMT_STRING_V2,
                urnBase, versioneSerie);
    }

    // old urn
    public static String formattaUrnIndiceAIPSerieUDMarca(String versatore, String codiceSerie,
            String versioneSerie) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_INDICE_AIP_SERIE_UD_MARCA_FMT_STRING,
                versatore, codiceSerie, versioneSerie);
    }

    //
    public static String formattaUrnIndiceAIPSerieUDMarca(String urnBase, String versioneSerie) {
        return MessageFormat.format(
                Costanti.UrnFormatter.URN_INDICE_AIP_SERIE_UD_MARCA_FMT_STRING_V2, urnBase,
                versioneSerie);
    }

    public static String formattaBaseUrnSerie(String versatore, String codiceSerie) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_VERS_SERIE_FMT_STRING, versatore,
                codiceSerie);
    }

    public static String formattaUrnPartSerie(CSChiaveSerie chiave) {
        return formattaUrnPartSerie(chiave, false, Costanti.UrnFormatter.SERIE_FMT_STRING);
    }

    public static String formattaUrnPartSerie(CSChiaveSerie chiave, boolean toNormalize,
            String fmtUsed) {
        if (!toNormalize) {
            return MessageFormat.format(fmtUsed, chiave.getAnno().toString(), chiave.getNumero());
        } else {
            return MessageFormat.format(fmtUsed, chiave.getAnno().toString(),
                    MessaggiWSFormat.normalizingKey(chiave.getNumero()));
        }
    }

    //
    // FASCICOLI
    //
    public static String formattaUrnPartFasc(CSChiaveFasc chiave) {
        return formattaUrnPartFasc(chiave, false, Costanti.UrnFormatter.FASC_FMT_STRING);
    }

    public static String formattaUrnPartFasc(CSChiaveFasc chiave, boolean toNormalize,
            String fmtUsed) {
        if (!toNormalize) {
            return MessageFormat.format(fmtUsed, chiave.getAnno().toString(), chiave.getNumero());
        } else {
            return MessageFormat.format(fmtUsed, chiave.getAnno().toString(),
                    MessaggiWSFormat.normalizingKey(chiave.getNumero()));
        }
    }

    public static String formattaBaseUrnFascicolo(String versatore, String fascicolo) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_FASC_FMT_STRING, versatore,
                fascicolo);
    }

    public static String formattaChiaveFascicolo(CSVersatore versatore, CSChiaveFasc chiave) {
        return MessageFormat.format(Costanti.UrnFormatter.CHIAVE_FASC_FMT_STRING,
                versatore.getSistemaConservazione(), versatore.getEnte(), versatore.getStruttura(),
                chiave.getAnno().toString(), chiave.getNumero());
    }

    public static String formattaChiaveUdFull(CSVersatore versatore, CSChiave chiave) {
        return MessageFormat.format(Costanti.UrnFormatter.CHIAVE_UD_FULL_FMT_STRING,
                versatore.getSistemaConservazione(), versatore.getEnte(), versatore.getStruttura(),
                chiave.getTipoRegistro(), chiave.getAnno().toString(), chiave.getNumero());
    }

    public static String formattaChiaveUd(CSChiave chiave) {
        return MessageFormat.format(Costanti.UrnFormatter.CHIAVE_UD_FMT_STRING,
                chiave.getTipoRegistro(), chiave.getAnno().toString(), chiave.getNumero());
    }

    public static String formattaUrnIndiceSipFasc(String urnBase) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_INDICE_SIP_FASC_FMT_STRING, urnBase);
    }

    public static String formattaUrnAipUdPartVersatore(CSVersatore versatore) {
        return MessageFormat.format(Costanti.UrnFormatter.VERS_AIP_UD_FMT_STRING,
                versatore.getSistemaConservazione(), versatore.getEnte(), versatore.getStruttura());
    }

    //
    public static String formattaUrnAipUdAip(String versatore, String chiave) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_AIP_UD_FMT_STRING, versatore, chiave);
    }

    //
    public static String formattaUrnElencoVersFascicoli(String sistema, String ente,
            String struttura, String dtCreazioneElenco, String idElenco) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_ELENCO_VERS_FASC_FMT_STRING, sistema,
                ente, struttura, dtCreazioneElenco, idElenco);
    }

    //
    public static String formattaUrnIndiceElencoVersFascicoli(String sistema, String ente,
            String struttura, String dtCreazioneElenco, String idElenco) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_INDICE_ELENCO_VERS_FASC_FMT_STRING,
                sistema, ente, struttura, dtCreazioneElenco, idElenco);
    }

    public static String formattaUrnIndiceElencoVersFascicoliFirmati(String sistema, String ente,
            String struttura, String dtCreazioneElenco, String idElenco) {
        return MessageFormat.format(
                Costanti.UrnFormatter.URN_INDICE_ELENCO_VERS_FASC_FIRMATI_FMT_STRING, sistema, ente,
                struttura, dtCreazioneElenco, idElenco);
    }
    //

    public static String formattaUrnElencoIndiciAIPFascicoliFirmati(String versatore,
            String idElenco, String dtCreazioneElenco) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_ELENCO_AIP_FASC_FIRMATI_FMT_STRING_V2,
                versatore, idElenco, dtCreazioneElenco);
    }

    //
    public static String formattaUrnElencoIndiciAIPFascicoliNonFirmati(String versatore,
            String idElenco, String dtCreazioneElenco) {
        return MessageFormat.format(
                Costanti.UrnFormatter.URN_ELENCO_AIP_FASC_NON_FIRMATI_FMT_STRING_V2, versatore,
                idElenco, dtCreazioneElenco);
    }

    //
    public static String formattaUrnElencoIndiciAIPFascicoli(String versatore, String idElenco,
            String dtCreazioneElenco) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_ELENCO_INDICI_AIP_FASC_FMT_STRING_V2,
                versatore, idElenco, dtCreazioneElenco);
    }

    //
    public static String formattaUrnIndiceEdvFascicoli(String versatore, String idElenco,
            String dtCreazioneElenco) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_INDICE_EDV_FASC_FMT_STRING_V2,
                versatore, idElenco, dtCreazioneElenco);
    }

    public static String formattaUrnIndiceAipElenchiFascicoli(String sistema, String ente,
            String struttura, String anno, String numero, String versione) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_INDICE_AIP_ELENCHI_FASC_FMT_STRING,
                sistema, ente, struttura, anno, numero, versione);
    }

    // old urn
    public static String formattaUrnIndiceAipFascicoli(String versione) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_INDICE_AIP_FASC_FMT_STRING, versione);
    }

    //
    public static String formattaUrnIndiceAipFascicoli(String urnBase, String versione,
            String fmtUsed) {
        return MessageFormat.format(fmtUsed, urnBase, versione);
    }

    public static String formattaUrnAipFascicolo(String urnBase) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_AIP_FASC_FMT_STRING, urnBase);
    }

    //
    public static String formattaUrnAipMetaFascicolo(String urnBase, String versione) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_AIP_META_FASC_FMT_STRING, urnBase,
                versione);
    }

    //
    public static String formattaUrnAipMetaFascicoloXsd(String urnBase, String versione) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_AIP_META_FASC_XSD_FMT_STRING, urnBase,
                versione);
    }

    public static String formattaUrnAipUdAipFascicolo(String versatore, String chiave) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_AIP_UD_FMT_STRING, versatore, chiave);
    }

    public static String formattaUrnAggiornamentoMetadati(String sistema, String ente,
            String struttura, String registro, String anno, String numero,
            String progressivoAggiornamento) {
        return MessageFormat.format(Costanti.UrnFormatter.URN_UPD_UD_FMT_STRING, sistema, ente,
                struttura, registro, anno, numero, progressivoAggiornamento);
    }

    // AGGIORNAMENTO UD
    public static String formattaBaseUrnUpdUnitaDoc(String versatore, String unitaDoc) {
        return formattaBaseUrnUpdUnitaDoc(versatore, unitaDoc,
                Costanti.UrnFormatter.UPD_FMT_STRING);
    }

    public static String formattaBaseUrnUpdUnitaDoc(String versatore, String unitaDoc,
            String fmtUsed) {
        return MessageFormat.format(fmtUsed, versatore, unitaDoc, -1);
    }

    // OLD URN
    public static String formattaBaseUrnUpdUnitaDoc(String versatore, String unitaDoc,
            long progressivo) {
        return formattaBaseUrnUpdUnitaDoc(versatore, unitaDoc, progressivo, false);
    }

    // NEW URN
    /*
     * Al passaggio dei nuovi URN (V2) sarà sufficiente cambiare il valore del boolean (vedi sopra)
     */
    public static String formattaBaseUrnUpdUnitaDoc(String versatore, String unitaDoc,
            long progressivo, boolean pgpad) {
        return formattaBaseUrnUpdUnitaDoc(versatore, unitaDoc, progressivo, pgpad,
                Costanti.UrnFormatter.UPD_FMT_STRING, Costanti.UrnFormatter.PADNODIGITS_FMT);
    }

    public static String formattaBaseUrnUpdUnitaDoc(String versatore, String unitaDoc,
            long progressivo, boolean pgpad, String fmtUsed, String padFmtUsed) {
        return MessageFormat.format(fmtUsed, versatore, unitaDoc,
                pgpad ? String.format(padFmtUsed, progressivo) : progressivo);
    }

    public static String formattaUrnPartRappVersUpd(String urnBase) {
        return formattaUrnPartRappVersUpd(urnBase, Costanti.UrnFormatter.URN_RAPP_VERS_FMT_STRING);
    }

    public static String formattaUrnPartRappVersUpd(String urnBase, String fmtUsed) {
        return MessageFormat.format(fmtUsed, urnBase);
    }

    public static String formattaUrnIndiceSipUpd(String urnBase) {
        return formattaUrnIndiceSipUpd(urnBase, Costanti.UrnFormatter.URN_INDICE_SIP_FMT_STRING);
    }

    public static String formattaUrnIndiceSipUpd(String urnBase, String fmtUsed) {
        return MessageFormat.format(fmtUsed, urnBase);
    }

    /*
     * Restituisce una stringa normalizzata secondo le regole del codice UD normalizzato sostituendo
     * tutti i caratteri accentati con i corrispondenti non accentati e ammettendo solo lettere,
     * numeri, '.', '-' e '_'. Tutto il resto viene convertito in '_'.
     */
    public static String normalizingKey(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFD).replace(" ", "_")
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .replaceAll("[^A-Za-z0-9\\. _-]", "_");
    }

    /*
     * Restituisce una stringa normalizzata per nomi file e path accettando solo caratteri validi
     * per la rappresentazione su un generico filesystem. Tutti i backslash vengono sostituiti con
     * lo slash. Tutti i caratteri diversi da questi: [#$%&*,:;<>?|] vengono sostituiti con un
     * underscore. Funzione introdotta con la MEV#22921 Parametrizzazione servizi di recupero.
     */
    public static String normalizingFileName(String value) {
        return Normalizer.normalize(value, Normalizer.Form.NFC).replaceAll("\\\\", "/")
                .replaceAll("[#$%&\\*,:;<>?\\|]", "_");
    }

    /**
     *
     * Estrazione, a partire dall'urn, del nome da assegnare al componente (file, per il recupero).
     * Si utilizzano espressioni regolari per il parsing e quindi l'estrazione.
     *
     * Nuovi URN vedere {@link Costanti.UrnFormatter#SPATH_COMP_FILENAME_REGEXP_V2}
     *
     * Vecchi URN vedere {@link Costanti.UrnFormatter#SPATH_COMP_FILENAME_REGEXP}
     *
     * Se l'estrazione con espressione regolare ha successo, si effettua la sostituzione secondo
     * standard previsto ossia, il seperatore ':' con '_'
     *
     *
     * @param urn urn completo
     *
     * @return risultato estrazione
     */
    public static String estraiNomeFileBreve(String urn) {
        // new urn
        String compName = findCompNameFromUrn(urn,
                Costanti.UrnFormatter.SPATH_COMP_FILENAME_REGEXP_V2);
        if (StringUtils.isBlank(compName)) {
            // old urn
            compName = findCompNameFromUrn(urn, Costanti.UrnFormatter.SPATH_COMP_FILENAME_REGEXP);
        }
        if (StringUtils.isBlank(compName)) {
            return Costanti.UrnFormatter.SPATH_FILENAME_STANDARD_ERR; // default "err"
        }

        // replace ':' with file name compatible '_'
        return compName.replace(Costanti.UrnFormatter.URN_STD_SEPARATOR,
                Costanti.UrnFormatter.SPATH_FILE_STD_SEPARATOR_V2);
    }

    /**
     *
     * Estrazione, a partire dall'urn, del nome da assegnare al componente "tivoli", ossia qualora
     * il file sia stato archiviato su nastro (quindi recuperato da filesystem).
     *
     * La logica prevede di effettuare il parsing dell'urn iniziale (se esiste) per estrarre il nome
     * del file del componente recuperato da nastro, per poi passare all'estrazione con espressione
     * regolare, dal nuovo urn.
     *
     * Nuovi URN vedere {@link Costanti.UrnFormatter#SPATH_COMP_FILENAME_REGEXP_V2}
     *
     * Vecchi URN vedere {@link Costanti.UrnFormatter#SPATH_COMP_FILENAME_REGEXP}
     *
     * Se l'estrazione con espressione regolare ha successo, si effettua la sostituzione secondo
     * standard previsto ossia, il seperatore ':' nel caso dei vecchi urn previsto è '-' mentre sui
     * nuovi '_'.
     *
     * @param urnIniziale urn iniziale (pre URN v2)
     * @param urn         urn completo
     *
     * @return file name
     */
    public static String estraiNomeFilePerTivoli(String urnIniziale, String urn) {
        // old urn
        String compName = findCompNameFromUrn(urnIniziale,
                Costanti.UrnFormatter.SPATH_COMP_FILENAME_REGEXP);
        if (StringUtils.isNotBlank(compName)) {
            return compName.replace(Costanti.UrnFormatter.URN_STD_SEPARATOR,
                    Costanti.UrnFormatter.SPATH_FILE_STD_SEPARATOR);
        }

        // new urn
        compName = findCompNameFromUrn(urn, Costanti.UrnFormatter.SPATH_COMP_FILENAME_REGEXP_V2);
        if (StringUtils.isNotBlank(compName)) {
            return compName.replace(Costanti.UrnFormatter.URN_STD_SEPARATOR,
                    Costanti.UrnFormatter.SPATH_FILE_STD_SEPARATOR_V2);
        }

        return Costanti.UrnFormatter.SPATH_FILENAME_STANDARD_ERR; // default "err"
    }

    // estrae dall'urn completo il nome del componente
    private static String findCompNameFromUrn(String urn, String pattern) {
        if (StringUtils.isBlank(urn)) {
            return StringUtils.EMPTY;
        }
        // compile
        Pattern p = Pattern.compile(pattern);
        // match
        Matcher m = p.matcher(urn);
        // find
        if (m.find()) {
            return m.group(1);
        }
        return StringUtils.EMPTY;
    }

    /**
     *
     * Estrazione da urn completo di un nome compliant per file
     *
     * @param urn urn completo
     *
     * @return estrazione nome file da urn
     */
    public static String estraiNomeFileCompleto(String urn) {

        if (StringUtils.isBlank(urn)) {
            return Costanti.UrnFormatter.SPATH_FILENAME_STANDARD_ERR; // default "err"
        }
        //
        String tmpStr;
        Pattern oldNew = Pattern.compile("^urn:(.+)$");
        Matcher mOldNew = oldNew.matcher(urn);
        if (mOldNew.find()) {
            tmpStr = mOldNew.group(1);
        } else {
            tmpStr = urn;
        }
        return bonificaUrnPerNomeFile(tmpStr);
    }

    // MAC #25915
    // confronta urn con un pattern
    public static Boolean isUrnMatchesSafely(String urn, String pattern) {
        if (StringUtils.isBlank(urn)) {
            return false;
        }
        // compile
        Pattern p = Pattern.compile(pattern);
        // match
        Matcher m = p.matcher(urn);
        // find
        return m.find();
    }
    // end MAC #25915

    /**
     *
     * Estrazione, a partire dall'urn del report di verifica firma, il nome da assegnare al file da
     * recuperare. Si utilizzano espressioni regolari per il parsing e quindi l'estrazione.
     *
     * @param urn urn completo report verifica firma
     *
     * @return risultato estrazione
     */
    public static String estraiNomeFileBrevePerReportvf(String urn) {
        // new urn
        String compName = findCompNameFromUrn(urn,
                Costanti.UrnFormatter.SPATH_COMP_REPORVF_FILENAME_REGEXP);
        if (StringUtils.isBlank(compName)) {
            return Costanti.UrnFormatter.SPATH_FILENAME_STANDARD_ERR; // default "err"
        }

        // replace ':' with file name compatible '_'
        return compName.replace(Costanti.UrnFormatter.URN_STD_SEPARATOR,
                Costanti.UrnFormatter.SPATH_FILE_STD_SEPARATOR_V2);
    }
}
