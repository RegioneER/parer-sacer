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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.utils;

/**
 *
 * @author Fioravanti_F
 */
public class Costanti {

    //
    public static final String WS_REC_PROVE_CON_VRSN = "1.2";
    public static final String WS_REC_PROVE_CON_IDX_UD_VRSN = "1.2"; // non esiste più
    public static final String WS_INSERIMENTO_UTENTE_VRSN = "1.0"; // inutile, da togliere
    public static final String WS_MODIFICA_UTENTE_VRSN = "1.0"; // inutile, da togliere
    public static final String WS_CANCELLA_UTENTE_VRSN = "1.0"; // inutile, da togliere
    public static final String WS_STATUS_MONITOR_VRSN = "1.0";
    //
    public static final String[] WS_REC_PROVE_CON_IDX_UD_COMP = { "1.0" }; // non esiste più
    public static final String[] WS_INSERIMENTO_UTENTE_COMP = { "1.0" }; // inutile, da togliere
    public static final String[] WS_MODIFICA_UTENTE_COMP = { "1.0" }; // inutile, da togliere
    public static final String[] WS_CANCELLA_UTENTE_COMP = { "1.0" }; // inutile, da togliere
    /**
     * Interna, la si mantiane da codice
     */
    public static final String[] WS_STATUS_MONITOR_COMP = { "1.0" };
    //
    public static final String WS_REC_STATO_CON_NOME = "RecStatoConservazioneSync";
    public static final String WS_REC_UNITA_DOC_NOME = "RecUnitaDocumentariaSync";
    public static final String WS_REC_DIP_ESIBIZIONE_NOME = "RecDIPEsibizioneSync";

    public static final String WS_REC_AIP_UNITA_DOC_NOME = "RecAIPUnitaDocumentariaSync";
    public static final String WS_REC_PROVE_CON_NOME = "RecProveConservSync";
    public static final String WS_REC_PROVE_CON_IDX_UD_NOME = "RecProveConservIdxUdSync"; // non esiste più
    public static final String WS_REC_RAPPORTI_VERS_NOME = "RecDIPRapportiVersSync";
    //
    // EVO#13993
    public static final String WS_REC_FASC_NOME = "RecFascicoloSync";
    public static final String WS_REC_AIP_FASC_NOME = "RecAIPFascicoloSync";
    // end EVO#13993
    //
    public static final String WS_INSERIMENTO_UTENTE_NOME = "InserimentoUtente"; // inutile, da togliere
    public static final String WS_MODIFICA_UTENTE_NOME = "ModificaUtente"; // inutile, da togliere
    public static final String WS_CANCELLA_UTENTE_NOME = "CancellaUtente"; // inutile, da togliere
    //
    public static final String WS_RICHIESTA_ANNULLAMENTO_VERSAMENTI_NOME = "InvioRichiestaAnnullamentoVersamenti";

    public static final String WS_REC_UD_PDF_NOME = "RecDIPComponenteTrasformatoSync";
    public static final String WS_STATUS_MONITOR_NOME = "StatusMonitor";

    //
    // NOTA: i servizi Multimedia hanno nome diverso, ma versioni identiche agli omologhi servizi sincroni
    public static final String WS_REC_UNITA_DOC_MM_NOME = "RecUniDocMultiMedia";
    public static final String WS_REC_PROVE_CON_MM_NOME = "RecPCUniDocMultiMedia";
    //
    public static final String VERSIONE_XSD_INDICE_AIP = "1.0";
    // EVO#20792
    public static final String VERSIONE_XSD_INDICE_AIP_V2 = "2.0";
    // end EVO#20792
    //
    //
    public static final String VERSIONE_ELENCO_INDICE_AIP = "1.1";
    public static final String VERSIONE_ELENCO_INDICE_AIP_FASC = "1.0";
    //
    public static final String VERSIONE_XSD_INDICE_AIP_FASC = "1.0";
    //
    public static final String TPI_DATA_PATH_FMT_STRING = "yyyy_MM_dd";
    //
    public static final String VERSIONE_XML_RECUP_UD = "1.2";
    //
    public static final String UKNOWN_EXT = "unknown";

    public class UrnFormatter {

        private UrnFormatter() {
            throw new IllegalStateException("Utility class");
        }

        public static final char URN_STD_SEPARATOR = ':';

        public static final String VERS_FMT_STRING = "{0}:{1}:{2}";
        public static final String UD_FMT_STRING = "{0}-{1}-{2}";
        public static final String DOC_FMT_STRING = "{0}-{1}";
        //
        public static final String CHIAVE_DOC_FMT_STRING = "{0}-{1}";
        public static final String CHIAVE_COMP_FMT_STRING = "{0}:{1}:{2}";
        //
        public static final String SPATH_DATA_FMT_STRING = "yyyy_MM_dd";
        public static final String SPATH_VERS_FMT_STRING = "{0}-{1}-{2}";
        public static final String SPATH_UD_FMT_STRING = "{0}-{1}-{2}";
        public static final String SPATH_COMP_FMT_STRING = "{0}-{1}-{2}-{3}";
        public static final String SPATH_FILE_FMT_STRING = "{0}/{1}/{2}/{3}/{4}/{5}";
        public static final char SPATH_FILE_STD_SEPARATOR = '-';
        public static final char SPATH_FILE_STD_SEPARATOR_V2 = '_';

        public static final String FNAME_LOG_TSM_RETRIEVE = "OutputTSM_RETRIEVE_{0}-{1}-{2}_{3}-{4}-{5}.txt";

        //
        public static final String URN_UD_FMT_STRING = "{0}:{1}"; // VERS_FMT_STRING + UD_FMT_STRING
        public static final String URN_DOC_FMT_STRING = "{0}:{1}-{2}"; // VERS_FMT_STRING + UD_FMT_STRING +
                                                                       // DOC_FMT_STRING

        public static final String URN_UPD_FMT_STRING = "{0}:{1}:{2}"; // VERS_FMT_STRING + UD_FMT_STRING
        public static final String URN_UPD_FMT_STRING_V2 = "{0}:{1}:UPD{2}"; // VERS_FMT_STRING + UD_FMT_STRING
        public static final String URN_UPD_FMT_STRING_V3 = "{0}:{1}:AGG_MD{2}"; // VERS_FMT_STRING + UD_FMT_STRING

        //
        public static final String URN_DOC_UNI_DOC_FMT_STRING = "urn:{0}"; // URN_UD_FMT_STRING oppure
                                                                           // URN_DOC_FMT_STRING
        public static final String URN_UPD_UNI_DOC_FMT_STRING = "urn:{0}"; // URN_UPD_FMT_STRING

        //
        public static final String URN_INDICE_SIP_FMT_STRING = "urn:IndiceSIP:{0}"; // URN_UD_FMT_STRING oppure
                                                                                    // URN_DOC_FMT_STRING
        public static final String URN_PI_SIP_FMT_STRING = "urn:PISIP:{0}"; // URN_UD_FMT_STRING oppure //
                                                                            // URN_DOC_FMT_STRING
                                                                            // URN_DOC_FMT_STRING
        public static final String URN_ESITO_VERS_FMT_STRING = "urn:EsitoVersamento:{0}"; // URN_UD_FMT_STRING oppure
                                                                                          // URN_DOC_FMT_STRING
        //
        public static final String URN_RAPP_VERS_FMT_STRING = "urn:RapportoVersamento:{0}"; // URN_UD_FMT_STRING oppure
                                                                                            // URN_DOC_FMT_STRING
        //
        public static final String URN_INDICE_AIP_FMT_STRING = "urn:IndiceAIP-{0}:{1}"; // versione AIP +
                                                                                        // URN_UD_FMT_STRING
        // urn:<sistemaconservazione>:<ente>:<struttura>:<registro>-<anno>-<numero>:IndiceAIP-UD-<versione>
        public static final String URN_INDICE_AIP_FMT_STRING_V2 = "urn:{0}:IndiceAIP-UD-{1}"; // URN_UD_FMT_STRING +
                                                                                              // versione AIP
        //
        // VOLUMI
        //
        // urn:<sistemaconservazione>:<ente>:<struttura>:Volume:<IDVolume>
        public static final String URN_INDICE_VOLUME_CONSERV_FMT_STRING = "urn:{0}:Volume:{1}"; // UD_FMT_STRING +
                                                                                                // ID Volume
        //
        // SERIE
        //
        public static final String URN_INDICE_VOLUME_FMT_STRING = "urn:IndiceVolumeSerie-{0}:{1}";
        public static final String URN_INDICE_VOLUME_FMT_STRING1 = "urn:{0}:IndiceAIP-SE-:{1}:{2}";
        // urn:<sistemaconservazione>:<ente>:<struttura>:<codice serie>:IndiceAIP-SE-<codice
        // versione>:IndiceVolumeSE-<progressivo volume>
        public static final String URN_INDICE_VOLUME_FMT_STRING_V2 = "urn:{0}:IndiceAIP-SE-{1}:IndiceVolumeSE-{2}";
        //
        public static final String URN_VERS_SERIE_FMT_STRING = "{0}:{1}";
        public static final String URN_INDICE_AIP_SERIE_UD_FMT_STRING = "urn:IndiceAIPSerieUD-{0}:{1}:{2}"; // versione
                                                                                                            // AIP +
                                                                                                            // URN_SERIE_FMT_STRING
        public static final String URN_INDICE_AIP_SERIE_UD_FMT_STRING1 = "urn:{0}:{1}:IndiceAIP-SE-{2}-NonFirmato";
        // urn:<sistemaconservazione>:<ente>:<struttura>:<codice serie>:IndiceAIP-SE-<codice versione>-NonFirmato
        public static final String URN_INDICE_AIP_SERIE_UD_NON_FIRMATI_FMT_STRING = "urn:{0}:IndiceAIP-SE-{1}-NonFirmato";
        //
        public static final String URN_INDICE_AIP_SERIE_UD_FIR_FMT_STRING = "urn:{0}:{1}:IndiceAIP-SE-{2}";
        // urn:<sistemaconservazione>:<ente>:<struttura>:<codice serie>:IndiceAIP-SE-<codice versione>
        public static final String URN_INDICE_AIP_SERIE_UD_FIR_FMT_STRING_V2 = "urn:{0}:IndiceAIP-SE-{1}";

        public static final String URN_INDICE_AIP_SERIE_UD_MARCA_FMT_STRING = "urn:{0}:{1}:IndiceAIP-SE-{2}:Indice-Marca";
        // urn:<sistemaconservazione>:<ente>:<struttura>:<codice serie>:IndiceAIP-SE-<codice versione>:Indice-Marca
        public static final String URN_INDICE_AIP_SERIE_UD_MARCA_FMT_STRING_V2 = "urn:{0}:IndiceAIP-SE-{1}:Indice-Marca";

        //
        public static final String URN_ELENCO_VERSAMENTO_FMT_STRING = "urn:{0}:{1}:{2}:ElencoVers-UD-{3}";
        // urn:<sistema>:<nome ente>:<nome struttura>:ElencoVers-UD-<data creazione>-<id elenco>
        public static final String URN_ELENCO_VERSAMENTO_FMT_STRING_V2 = "urn:{0}:{1}:{2}:ElencoVers-UD-{3}-{4}";

        // urn:<sistemaconservazione>:<ente>:<struttura>:ElencoVers-UD-<dataCreazione>-<id elenco>:Indice
        public static final String URN_ELENCO_INDICE_FMT_STRING = "urn:{0}:ElencoVers-UD-{1}-{2}:Indice";

        // urn:<sistemaconservazione>:<ente>:<struttura>:ElencoVers-UD-<dataCreazione>-<id elenco>:IndiceFirmato
        public static final String URN_ELENCO_INDICE_FIRMATO_FMT_STRING = "urn:{0}:ElencoVers-UD-{1}-{2}:IndiceFirmato";

        // urn:<sistemaconservazione>:<ente>:<struttura>:ElencoIndiciAIP-UD-<dataCreazione>-<id elenco>
        public static final String URN_ELENCO_INDICI_AIP_FMT_STRING = "urn:{0}:{1}:{2}:ElencoIndiciAIP-UD-{3}";
        public static final String URN_ELENCO_INDICI_AIP_FMT_STRING_V2 = "urn:{0}:ElencoIndiciAIP-UD-{1}-{2}";

        // urn:<sistemaconservazione>:<ente>:<struttura>:ElencoIndiciAIP-UD-<dataCreazione>-<id elenco>:Indice
        public static final String URN_ELENCO_AIP_FIRMATI_FMT_STRING = "urn:{0}:{1}:{2}:ElencoIndiciAIP-UD-{3}:Indice";
        public static final String URN_ELENCO_AIP_FIRMATI_FMT_STRING_V2 = "urn:{0}:ElencoIndiciAIP-UD-{1}-{2}:Indice";
        //
        public static final String URN_FIRMA_ELV_INDICI_AIP_FMT_STRING = "urn:{0}:{1}:{2}:ElencoIndiciAIP-UD-{3}:Indice";
        public static final String URN_FIRMA_ELV_INDICI_AIP_FMT_STRING_V2 = "urn:{0}:ElencoIndiciAIP-UD-{1}-{2}:Indice";

        // urn:<sistemaconservazione>:<ente>:<struttura>:ElencoIndiciAIP-UD-<dataCreazione>-<id elenco>:IndiceNonFirmato
        public static final String URN_ELV_INDICI_AIP_NON_FIRMATI_FMT_STRING = "urn:{0}:ElencoIndiciAIP-UD-{1}-{2}:IndiceNonFirmato";

        // urn:<sistemaconservazione>:<ente>:<struttura>:ElencoIndiciAIP-UD-<dataCreazione>-<id elenco>:Indice-Marca
        public static final String URN_MARCA_ELV_INDICI_AIP_FMT_STRING = "urn:{0}:{1}:{2}:ElencoIndiciAIP-UD-{3}:Indice-Marca";
        public static final String URN_MARCA_ELV_INDICI_AIP_FMT_STRING_V2 = "urn:{0}:ElencoIndiciAIP-UD-{1}-{2}:Indice-Marca";

        // urn:<ambiente>:<ente>:<struttura>:<registro>-<anno>-<numero>:<progressivo aggiornamento>
        public static final String URN_UPD_UD_FMT_STRING = "urn:{0}:{1}:{2}:{3}-{4}-{5}:{6}";
        // urn:<sistemaconservazione>:<ente>:<struttura>:<registro>-<anno>-<numero>:UPD<progressivo aggiornamento>
        public static final String URN_UPD_UD_FMT_STRING_V2 = "urn:{0}:{1}:{2}:{3}-{4}-{5}:UPD{6}";
        // urn:<sistemaconservazione>:<ente>:<struttura>:<registro>-<anno>-<numero>:AGG_MD<progressivo aggiornamento>
        public static final String URN_UPD_UD_FMT_STRING_V3 = "urn:{0}:{1}:{2}:{3}-{4}-{5}:AGG_MD{6}";
        public static final String SERIE_FMT_STRING = "{0}-{1}";

        //
        // FASCICOLI
        //
        public static final String FASC_FMT_STRING = "{0}-{1}";
        public static final String VERS_AIP_UD_FMT_STRING = "{0}:{1}:{2}";
        public static final String URN_FASC_FMT_STRING = "{0}:{1}"; // VERS_FMT_STRING + FASC_FMT_STRING

        // urn:<sistemaconservazione>:<ente>:<struttura>:<anno>-<numero>
        public static final String CHIAVE_FASC_FMT_STRING = "{0}:{1}:{2}:{3}-{4}";
        public static final String CHIAVE_UD_FULL_FMT_STRING = "{0}_{1}_{2}_{3}-{4}-{5}";
        public static final String CHIAVE_UD_FMT_STRING = "{0}-{1}-{2}";

        public static final String URN_INDICE_SIP_FASC_FMT_STRING = "urn:{0}:IndiceSIP"; // CHIAVE_FASC_FMT_STRING

        //
        // urn:<sistemaconservazione>:<ente>:<struttura>:ElencoVers-FA-<data creazione>-<id elenco>
        public static final String URN_ELENCO_VERS_FASC_FMT_STRING = "urn:{0}:{1}:{2}:ElencoVers-FA-{3}-{4}";
        // urn:<sistemaconservazione>:<ente>:<struttura>:ElencoVers-FA-<data creazione>-<id elenco>:Indice
        public static final String URN_INDICE_ELENCO_VERS_FASC_FMT_STRING = "urn:{0}:{1}:{2}:ElencoVers-FA-{3}-{4}:Indice";
        // urn:<sistemaconservazione>:<ente>:<struttura>:ElencoVers-FA-<data creazione>-<id elenco>:IndiceFirmato
        public static final String URN_INDICE_ELENCO_VERS_FASC_FIRMATI_FMT_STRING = "urn:{0}:{1}:{2}:ElencoVers-FA-{3}-{4}:IndiceFirmato";
        // urn:<sistemaconservazione>:<ente>:<struttura>:ElencoVers-FA-<id elenco>
        public static final String URN_INDICE_EDV_FASC_FMT_STRING = "urn:{0}:{1}:{2}:ElencoVers-FA-{3}";
        // urn:<sistemaconservazione>:<ente>:<struttura>:ElencoVers-FA-<dataCreazione>-<id elenco>
        public static final String URN_INDICE_EDV_FASC_FMT_STRING_V2 = "urn:{0}:ElencoVers-FA-{1}-{2}";
        //
        // urn:<sistemaconservazione>:<ente>:<struttura>:ElencoIndiciAIP-FA-<id elenco>:Indice
        public static final String URN_ELENCO_AIP_FASC_FIRMATI_FMT_STRING = "urn:{0}:{1}:{2}:ElencoIndiciAIP-FA-{3}:Indice";
        // urn:<sistemaconservazione>:<ente>:<struttura>:ElencoIndiciAIP-FA-<dataCreazione>-<id elenco>:Indice
        public static final String URN_ELENCO_AIP_FASC_FIRMATI_FMT_STRING_V2 = "urn:{0}:ElencoIndiciAIP-FA-{1}-{2}:Indice";

        // urn:<sistemaconservazione>:<ente>:<struttura>:ElencoIndiciAIP-FA-<id elenco>:IndiceNonFirmato
        public static final String URN_ELENCO_AIP_FASC_NON_FIRMATI_FMT_STRING = "urn:{0}:{1}:{2}:ElencoIndiciAIP-FA-{3}:IndiceNonFirmato";
        // urn:<sistemaconservazione>:<ente>:<struttura>:ElencoIndiciAIP-FA-<dataCreazione>-<id elenco>:IndiceNonFirmato
        public static final String URN_ELENCO_AIP_FASC_NON_FIRMATI_FMT_STRING_V2 = "urn:{0}:ElencoIndiciAIP-FA-{1}-{2}:IndiceNonFirmato";

        // urn:<sistemaconservazione>:<ente>:<struttura>:ElencoIndiciAIP-FA-<id elenco>
        public static final String URN_ELENCO_INDICI_AIP_FASC_FMT_STRING = "urn:{0}:{1}:{2}:ElencoIndiciAIP-FA-{3}";
        // urn:<sistemaconservazione>:<ente>:<struttura>:ElencoIndiciAIP-FA-<dataCreazione>-<id elenco>
        public static final String URN_ELENCO_INDICI_AIP_FASC_FMT_STRING_V2 = "urn:{0}:ElencoIndiciAIP-FA-{1}-{2}";

        //
        // urn:<sistemaconservazione>:<ente>:<struttura>:<anno>-<numero>:IndiceAIP-FA-<versione>
        public static final String URN_INDICE_AIP_ELENCHI_FASC_FMT_STRING = "urn:{0}:{1}:{2}:{3}-{4}:IndiceAIP-FA-{5}";

        //
        //
        public static final String URN_INDICE_AIP_FASC_FMT_STRING = "urn:IndiceAIP-FA-{0}";
        //
        public static final String URN_INDICE_AIP_FASC_FMT_STRING_V2 = "urn:{0}:IndiceAIP-FA-{1}";
        //
        // urn:<sistemaconservazione>:<ente>:<struttura>:<anno>-<numero>:AIP-FA
        public static final String URN_AIP_FASC_FMT_STRING = "urn:{0}:AIP-FA"; // CHIAVE_FASC_FMT_STRING
        //
        // urn:<sistemaconservazione>:<ente>:<struttura>:<anno>-<numero>:AIP-FA:MetadatiFascicolo-<versione>
        public static final String URN_AIP_META_FASC_FMT_STRING = "urn:{0}:AIP-FA:MetadatiFascicolo-{1}"; // CHIAVE_FASC_FMT_STRING
        // + versione AIP
        // urn:<sistemaconservazione>:<ente>:<struttura>:<anno>-<numero>:AIP-FA:MetadatiFascicoloXSD-<versione>
        public static final String URN_AIP_META_FASC_XSD_FMT_STRING = "urn:{0}:AIP-FA:MetadatiFascicoloXSD-{1}"; // CHIAVE_FASC_FMT_STRING
        // + versione AIP

        // urn:<sistemaconservazione>:<ente>:<struttura>:<registro>-<anno>-<numero>:AIP-UD
        public static final String URN_AIP_UD_FMT_STRING = "urn:{0}:{1}:AIP-UD"; // VERS_AIP_UD_FMT_STRING +
                                                                                 // CHIAVE_UD_FMT_STRING

        //
        // AGGIORNAMENTO UD
        //
        public static final String UPD_FMT_STRING = "{0}:{1}:{2}"; //
        public static final String UPD_FMT_STRING_V2 = "{0}:{1}:UPD{2}"; //
        public static final String UPD_FMT_STRING_V3 = "{0}:{1}:AGG_MD{2}"; //

        // NEW URN FMT
        public static final String URN_DOC_PREFIX = "DOC";
        public static final String URN_UPD_PREFIX = "AGG_MD";

        public static final String URN_INDICE_SIP_V2 = "urn:{0}:IndiceSIP"; //
        public static final String URN_RAPP_VERS_V2 = "urn:{0}:RdV"; //
        public static final String URN_PI_SIP_V2 = "urn:{0}:PISIP"; //
        public static final String URN_ESITO_VERS_V2 = "urn:{0}:EdV"; //

        //
        public static final String DOC_FMT_STRING_V2 = "{0}{1}";
        public static final String URN_DOC_FMT_STRING_V2 = "{0}:{1}:{2}";

        public static final String COMP_FMT_STRING_V2 = "{0}:{1}";
        public static final String URN_COMP_FMT_STRING = "urn:{0}:{1}:{2}";
        //
        public static final String CHIAVE_COMP_FMT_STRING_V2 = "{0}";
        //
        public static final String PAD5DIGITS_FMT = "%05d";
        public static final String PAD2DIGITS_FMT = "%02d";
        public static final String PADNODIGITS_FMT = "%00d";
        // FILENAME (componente)
        // NEW URN
        public static final String SPATH_COMP_FILENAME_REGEXP_V2 = "(" + URN_DOC_PREFIX
                + "([0-9])+:([0-9])+[:[0-9]+]*)$"; // filename
        // OLD URN
        public static final String SPATH_COMP_FILENAME_REGEXP = "-([A-Z]+-[0-9]+:[0-9]+:[0-9]+[:[0-9]+]*)$"; // filename
        //
        public static final String SPATH_FILENAME_STANDARD_ERR = "err";

        // MAC#25915
        public static final String SPATH_AGG_MD_REGEXP = "(" + URN_UPD_PREFIX + "([0-9]){5}[:[A-Za-z]+]*)$";
        // end MAC#25915

        public static final String SPATH_COMP_REPORVF_FILENAME_REGEXP = "(" + URN_DOC_PREFIX
                + "([0-9])+:([0-9])+[:[0-9]+]*ReportVerificaFirma)$"; // filename

        // MEV#23176
        public static final String URN_SIP_UD = "urn:{0}:SIP-UD"; //
        public static final String URN_SIP_DOC = "urn:{0}:SIP-AGGIUNTA_DOC"; //
        public static final String URN_SIP_UPD = "urn:{0}:SIP-AGGIORNAMENTO_UPD"; //
        public static final String URN_SIP_UPD_V2 = "urn:{0}:SIP-AGG_MD"; //
        // end MEV#23176
    }

    //
    public class AwsConstants {

        private AwsConstants() {
            throw new IllegalStateException("AwsS3Constants Utility class");
        }

        // custom tags
        // public static final String TAG_KEY_AROOBJ_TYPE = "aro-object-type";
        // public static final String TAG_VALUE_AROOBJ_INDICI_AIP = "xml_indici_aip_ud";

        // custom metadata
        public static final String MEATADATA_INGEST_NODE = "ingest-node";
        public static final String MEATADATA_INGEST_TIME = "ingest-time";

    }

    //
    public class JMSMsgProperties {

        private JMSMsgProperties() {
            throw new IllegalStateException("Utility class");
        }

        // msg properties
        public static final String MSG_K_PAYLOADTYPE = "tipoPayload";
        public static final String MSG_K_STATUS = "statoElenco";
        public static final String MSG_K_APP = "fromApplication";

        // msg values
        public static final String MSG_V_APP_SACER = "SACER";
        public static final String MSG_V_APP_SACERWS = "SACERWS";
    }

    //
    //
    //
    public enum ModificatoriWS {

        TAG_VERIFICA_FORMATI_OLD, TAG_VERIFICA_FORMATI_1_25, TAG_MIGRAZIONE, TAG_DATISPEC_EXT, TAG_ESTESI_1_3_OUT, // ID
                                                                                                                   // documento,
                                                                                                                   // tag
                                                                                                                   // Versatore

        TAG_RAPPORTO_VERS_OUT, // questi tag sono legati alla versione 1.4 del versamento
        TAG_LISTA_ERR_OUT, TAG_INFO_FIRME_EXT_OUT, TAG_CONSERV_ANTIC_ARCH_IN,
        //
        TAG_REC_USR_DOC_COMP, // tag di recupero, abilita i tag opzionali di Utente, IdDocumento, NumOrdComponente e
                              // TipoDocumento
        //
        TAG_ANNUL_FORZA_PING, // tag di annullamento, abilita i tag opzionali di ForzaAnnullamento e RichiestaAPing
        //
        TAG_ANNUL_FASC, // tag di annullamento, abilita l'opzione di Annullamento Fascicolo,

        TAG_ANNUL_TIPO_ANNUL // tag di annullamento che abilita il tag opzionale TipoAnnullamento
    }

    public enum CategoriaDocumento {

        Principale(CostantiDB.TipoDocumento.PRINCIPALE, 1), Allegato(CostantiDB.TipoDocumento.ALLEGATO, 2),
        Annotazione(CostantiDB.TipoDocumento.ANNOTAZIONE, 3), Annesso(CostantiDB.TipoDocumento.ANNESSO, 4),
        Documento(Costanti.UrnFormatter.URN_DOC_PREFIX);// "categoria"
                                                        // speciale,
                                                        // in
                                                        // quanto
                                                        // utilizzata
                                                        // esclusivamente
                                                        // per
                                                        // il
                                                        // supporto
                                                        // legato
                                                        // alla
                                                        // composizione
                                                        // dell'URN

        private String valore;
        private int ordine;

        private CategoriaDocumento(String val) {
            this.valore = val;
        }

        private CategoriaDocumento(String val, int ordine) {
            this.valore = val;
            this.ordine = ordine;
        }

        public String getValoreDb() {
            return valore;
        }

        public int getOrdine() {
            return ordine;
        }

        public static CategoriaDocumento getEnum(String value) {
            if (value == null) {
                return null;
            }
            for (CategoriaDocumento v : values()) {
                if (value.equalsIgnoreCase(v.getValoreDb())) {
                    return v;
                }
            }
            return null;
        }
    }

    //
    public enum EsitoServizio {

        OK, KO, WARN
    }

    public enum TipiWSPerControlli {
        VERSAMENTO_RECUPERO, ANNULLAMENTO, VERSAMENTO_FASCICOLO,
        /* EVO#13993 */ VERSAMENTO_RECUPERO_FASC /*
                                                  * end EVO#13993
                                                  */
    }

    // EVO#20972
    public class SchemeAttributes {

        // schema attributes
        public static final String SCHEME_LOCAL = "local";
    }

    public enum FileGroupDocumento {

        Principale(CostantiDB.TipoDocumento.PRINCIPALE, "Documento principale",
                "File appartenenti al Documento principale dell'Unità documentaria"),
        Allegato(CostantiDB.TipoDocumento.ALLEGATO, "Allegato",
                "File appartenenti a un documento Allegato dell'Unità documentaria"),
        Annotazione(CostantiDB.TipoDocumento.ANNESSO, "Annesso",
                "File appartenenti a un documento Annesso dell'Unità documentaria"),
        Annesso(CostantiDB.TipoDocumento.ANNOTAZIONE, "Annotazione",
                "File appartenenti a un documento Annotazione dell'Unità documentaria");

        private String valore;
        private String label;
        private String desc;

        private FileGroupDocumento(String val, String label, String desc) {
            this.valore = val;
            this.label = label;
            this.desc = desc;
        }

        public String getValoreDb() {
            return valore;
        }

        public String getLabel() {
            return label;
        }

        public String getDesc() {
            return desc;
        }

        public static FileGroupDocumento getEnum(String value) {
            if (value == null) {
                return null;
            }
            for (FileGroupDocumento v : values()) {
                if (value.equalsIgnoreCase(v.getValoreDb())) {
                    return v;
                }
            }
            return null;
        }
    }
    // end EVO#20972
}
