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
package it.eng.parer.restWS.util;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.keycloak.representations.AccessToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.ws.dto.IRispostaWS;
import it.eng.parer.ws.utils.AvanzamentoWs;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.versamento.dto.FileBinario;
import it.eng.parer.ws.versamento.dto.SyncFakeSessn;

/**
 * Parser di request di tipo POST/MULTIPART FORM DATA
 *
 * @author Fioravanti_F
 */
public class RequestPrsr extends AbsRequestPrsr {

    private static final Logger log = LoggerFactory.getLogger(RequestPrsr.class);

    public class ReqPrsrConfig {

        private boolean leggiFile;
        private boolean leggindiceMM;
        private AvanzamentoWs avanzamentoWs;
        private SyncFakeSessn sessioneFinta;
        private HttpServletRequest request;
        ServletFileUpload uploadHandler;

        public boolean isLeggiFile() {
            return leggiFile;
        }

        public void setLeggiFile(boolean leggiFile) {
            this.leggiFile = leggiFile;
        }

        public boolean isLeggindiceMM() {
            return leggindiceMM;
        }

        public void setLeggindiceMM(boolean leggindiceMM) {
            this.leggindiceMM = leggindiceMM;
        }

        public AvanzamentoWs getAvanzamentoWs() {
            return avanzamentoWs;
        }

        public void setAvanzamentoWs(AvanzamentoWs avanzamentoWs) {
            this.avanzamentoWs = avanzamentoWs;
        }

        public SyncFakeSessn getSessioneFinta() {
            return sessioneFinta;
        }

        public void setSessioneFinta(SyncFakeSessn sessioneFinta) {
            this.sessioneFinta = sessioneFinta;
        }

        public HttpServletRequest getRequest() {
            return request;
        }

        public void setRequest(HttpServletRequest request) {
            this.request = request;
        }

        public ServletFileUpload getUploadHandler() {
            return uploadHandler;
        }

        public void setUploadHandler(ServletFileUpload uploadHandler) {
            this.uploadHandler = uploadHandler;
        }
    }

    /*
     * Metodo che chiama il parse passando il parametro aggiuntivo true. In questo modo si dice al parsing di verificare
     * il contenuto del CommonName dell'header e di bypassare il controllo su LOGINNAME e PASSWORD.
     */
    // public List<FileItem> parseWithCommonName(IRispostaWS rispostaWs, ReqPrsrConfig configurazione)
    // throws FileUploadException {
    // return parse(rispostaWs, configurazione, null, true);
    // }

    /**
     * Nota bene: E' fondamentale che questo metodo ritorni la collection di FileItem, e che il chiamante ne tenga una
     * copia: la deallocazione del DiskFileItem a causa della GC e la conseguente chiamata del metodo finalize()
     * comporta la cancellazione del file fisico e mantenere una copia dell'istanza della classe File sortisce l'unico
     * effetto di avere un File handler che non punta a nulla; il fenomeno Ã¨ difficile da replicare ma estremamente
     * insidioso. Si manifesta con errori apparentemente casuali in cui le procedure di lettura del file (verifica firma
     * e persistenza) vanno in eccezione per mancanza di file. (File not found exception)
     *
     * @param rispostaWs
     *            interfaccia IRispostaWS
     * @param configurazione
     *            bean ReqPrsrConfig
     *
     * @return lista oggetti di tipo {@link FileItem}
     *
     * @throws FileUploadException
     *             errore generico
     *
     */
    public List<FileItem> parse(IRispostaWS rispostaWs, ReqPrsrConfig configurazione) throws FileUploadException {
        return parse(rispostaWs, configurazione, null, false);
    }

    public List<FileItem> parse(IRispostaWS rispostaWs, ReqPrsrConfig configurazione, AccessToken accessToken,
            boolean commonNamePassato) throws FileUploadException {
        Iterator<FileItem> tmpIterator = null;
        DiskFileItem tmpFileItem = null;
        List<FileItem> fileItems = null;
        FileBinario tmpFileBinario;

        // lettura configurazione;
        AvanzamentoWs tmpAvanzamento = configurazione.getAvanzamentoWs();
        SyncFakeSessn sessioneFinta = configurazione.getSessioneFinta();
        HttpServletRequest request = configurazione.getRequest();
        ServletFileUpload upload = configurazione.getUploadHandler();

        // nella riga sotto arrivano i dati e vengono scritti su disco, la sua esecuzione puÃ² richiedere parecchio
        // tempo
        fileItems = upload.parseRequest(request);
        tmpIterator = fileItems.iterator();
        tmpAvanzamento.setCheckPoint(AvanzamentoWs.CheckPoints.TrasferimentoPayloadIn).setFase("Payload ricevuto")
                .logAvanzamento();

        //
        tmpAvanzamento.setCheckPoint(AvanzamentoWs.CheckPoints.VerificaStrutturaChiamataWs).setFase("")
                .logAvanzamento();

        /*
         * verifica della struttura della chiamata al WS: non Ã¨ un WS SOAP perciÃ² la signature del WS va controllata a
         * mano, leggendo quanto effettivamente versato.
         */
        // verifica strutturale del campo VERSIONE e memorizzazione dello stesso nella sessione finta
        tmpFileItem = (DiskFileItem) tmpIterator.next();
        if (tmpFileItem.isFormField()) {
            if (tmpFileItem.getFieldName().equals("VERSIONE")) {
                sessioneFinta.setVersioneWS(tmpFileItem.getString());
            } else {
                rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                rispostaWs.setErrorType(IRispostaWS.ErrorTypeEnum.WS_SIGNATURE);
                rispostaWs.setErrorMessage(
                        MessaggiWSBundle.getString(MessaggiWSBundle.WS_CHECK, "Manca il campo VERSIONE"));
                rispostaWs.setErrorCode(MessaggiWSBundle.WS_CHECK);
            }
        } else {
            rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
            rispostaWs.setErrorType(IRispostaWS.ErrorTypeEnum.WS_SIGNATURE);
            rispostaWs.setErrorMessage(MessaggiWSBundle.getString(MessaggiWSBundle.WS_CHECK,
                    "Il campo VERSIONE deve essere di tipo FORM"));
            rispostaWs.setErrorCode(MessaggiWSBundle.WS_CHECK);
        }
        // String commonName = this.leggiCertCommonName(request);
        // verifica strutturale del campo LOGINNAME e memorizzazione dello stesso nella sessione finta
        // Il controllo sullo username viene fatto se non è presente il CommonName nell'Header HTTP
        if (commonNamePassato) {
            log.info("Il controllo su LOGINNAME e PASSWORD non viene fatto perché è presente il parametro header [{}]",
                    AbsRequestPrsr.NOME_HEADER_CERTIFICATO);
        } else {
            log.info("Il controllo su LOGINNAME e PASSWORD viene fatto perché NON è presente il parametro header [{}]",
                    AbsRequestPrsr.NOME_HEADER_CERTIFICATO);
        }
        if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
            if (Objects.isNull(accessToken)) {
                if (!commonNamePassato) { // In caso di chiamata tradizionale con controllo LOGINNAME E PASSWORD
                    tmpFileItem = (DiskFileItem) tmpIterator.next();
                    if (tmpFileItem.isFormField()) {
                        if (tmpFileItem.getFieldName().equals("LOGINNAME")) {
                            log.info("LOGINNAME {}", tmpFileItem.getString());
                            sessioneFinta.setLoginName(tmpFileItem.getString());
                            tmpAvanzamento.setVrsUser(tmpFileItem.getString()).logAvanzamento();
                        } else {
                            rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                            rispostaWs.setErrorType(IRispostaWS.ErrorTypeEnum.WS_SIGNATURE);
                            rispostaWs.setErrorMessage(
                                    MessaggiWSBundle.getString(MessaggiWSBundle.WS_CHECK, "Manca il campo LOGINNAME"));
                            rispostaWs.setErrorCode(MessaggiWSBundle.WS_CHECK);
                        }
                    } else {
                        rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                        rispostaWs.setErrorType(IRispostaWS.ErrorTypeEnum.WS_SIGNATURE);
                        rispostaWs.setErrorMessage(MessaggiWSBundle.getString(MessaggiWSBundle.WS_CHECK,
                                "Il campo LOGINNAME deve essere di tipo FORM"));
                        rispostaWs.setErrorCode(MessaggiWSBundle.WS_CHECK);
                    }
                }
            } else {
                String oauth2PreferredUsername = accessToken.getPreferredUsername();

                log.info("LOGINNAME OUATH2 {}", oauth2PreferredUsername);
                sessioneFinta.setLoginName(oauth2PreferredUsername);
                tmpAvanzamento.setVrsUser(oauth2PreferredUsername).logAvanzamento();
            }
        }

        // verifica strutturale del campo PASSWORD e memorizzazione dello stesso nella sessione finta
        // Il controllo sulla password viene fatto se non Ã¨ presente il CommonName nell'Header HTTP
        if (Objects.isNull(accessToken) && rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
            if (!commonNamePassato) { // In caso di chiamata tradizionale con controllo LOGINNAME E PASSWORD
                tmpFileItem = (DiskFileItem) tmpIterator.next();
                if (tmpFileItem.isFormField()) {
                    if (tmpFileItem.getFieldName().equals("PASSWORD")) {
                        sessioneFinta.setPassword(tmpFileItem.getString());
                    } else {
                        rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                        rispostaWs.setErrorType(IRispostaWS.ErrorTypeEnum.WS_SIGNATURE);
                        rispostaWs.setErrorMessage(
                                MessaggiWSBundle.getString(MessaggiWSBundle.WS_CHECK, "Manca il campo PASSWORD"));
                        rispostaWs.setErrorCode(MessaggiWSBundle.WS_CHECK);
                    }
                } else {
                    rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                    rispostaWs.setErrorType(IRispostaWS.ErrorTypeEnum.WS_SIGNATURE);
                    rispostaWs.setErrorMessage(MessaggiWSBundle.getString(MessaggiWSBundle.WS_CHECK,
                            "Il campo PASSWORD deve essere di tipo FORM"));
                    rispostaWs.setErrorCode(MessaggiWSBundle.WS_CHECK);
                }
            }
        }

        if (configurazione.isLeggindiceMM()) {
            // verifica strutturale del campo XMLINDICE e memorizzazione dello stesso nella sessione finta
            if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
                tmpFileItem = (DiskFileItem) tmpIterator.next();
                /*
                 * Nel caso in cui si entra col certificato ma vengono passati LOGINNAME e/o PASSWORD bisogna comunque
                 * scartarli scorrendoli sequenzialmente
                 */
                if (tmpFileItem.getFieldName().equals("LOGINNAME")) {
                    tmpFileItem = (DiskFileItem) tmpIterator.next();
                }
                if (tmpFileItem.getFieldName().equals("PASSWORD")) {
                    tmpFileItem = (DiskFileItem) tmpIterator.next();
                }
                if (tmpFileItem.isFormField()) {
                    if (tmpFileItem.getFieldName().equals("XMLINDICE")) {
                        sessioneFinta.setDatiPackInfoSipXml(tmpFileItem.getString());
                        //
                    } else {
                        rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                        rispostaWs.setErrorType(IRispostaWS.ErrorTypeEnum.WS_SIGNATURE);
                        rispostaWs.setErrorMessage(
                                MessaggiWSBundle.getString(MessaggiWSBundle.WS_CHECK, "Manca il campo XMLINDICE"));
                        rispostaWs.setErrorCode(MessaggiWSBundle.WS_CHECK);
                    }
                } else {
                    rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                    rispostaWs.setErrorType(IRispostaWS.ErrorTypeEnum.WS_SIGNATURE);
                    rispostaWs.setErrorMessage(MessaggiWSBundle.getString(MessaggiWSBundle.WS_CHECK,
                            "Il campo XMLINDICE deve essere di tipo FORM"));
                    rispostaWs.setErrorCode(MessaggiWSBundle.WS_CHECK);
                }
            }
        }

        // verifica strutturale del campo XMLSIP e memorizzazione dello stesso nella sessione finta
        if (rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
            tmpFileItem = (DiskFileItem) tmpIterator.next();
            /*
             * Nel caso in cui si entra col certificato ma vengono passati LOGINNAME e/o PASSWORD bisogna comunque
             * scartarli scorrendoli sequenzialmente
             */
            if (tmpFileItem.getFieldName().equals("LOGINNAME")) {
                tmpFileItem = (DiskFileItem) tmpIterator.next();
            }
            if (tmpFileItem.getFieldName().equals("PASSWORD")) {
                tmpFileItem = (DiskFileItem) tmpIterator.next();
            }
            if (tmpFileItem.isFormField()) {
                if (tmpFileItem.getFieldName().equals("XMLSIP")) {
                    sessioneFinta.setDatiIndiceSipXml(tmpFileItem.getString());
                    sessioneFinta.setDatiDaSalvareIndiceSip(tmpFileItem.getString());
                } else {
                    rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                    rispostaWs.setErrorType(IRispostaWS.ErrorTypeEnum.WS_SIGNATURE);
                    rispostaWs.setErrorMessage(
                            MessaggiWSBundle.getString(MessaggiWSBundle.WS_CHECK, "Manca il campo XMLSIP"));
                    rispostaWs.setErrorCode(MessaggiWSBundle.WS_CHECK);
                }
            } else {
                rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                rispostaWs.setErrorType(IRispostaWS.ErrorTypeEnum.WS_SIGNATURE);
                rispostaWs.setErrorMessage(MessaggiWSBundle.getString(MessaggiWSBundle.WS_CHECK,
                        "Il campo XMLSIP deve essere di tipo FORM"));
                rispostaWs.setErrorCode(MessaggiWSBundle.WS_CHECK);
            }
        }

        if (configurazione.isLeggiFile()) {
            // verifica strutturale dei campi di tipo file e memorizzazione degli stessi nella sessione finta
            while (tmpIterator.hasNext() && rispostaWs.getSeverity() == IRispostaWS.SeverityEnum.OK) {
                tmpFileItem = (DiskFileItem) tmpIterator.next();
                if (!tmpFileItem.isFormField()) {
                    long sizeInBytes = tmpFileItem.getSize();
                    String fileName = tmpFileItem.getName();
                    if (sizeInBytes > 0 && fileName.length() > 0) {
                        tmpFileBinario = new FileBinario();
                        tmpFileBinario.setId(tmpFileItem.getFieldName());
                        if (tmpFileItem.isInMemory()) {
                            tmpFileBinario.setInMemoria(true);
                            tmpFileBinario.setDati(tmpFileItem.get());
                            tmpFileBinario.setDimensione(sizeInBytes);
                        } else {
                            tmpFileBinario.setInMemoria(false);
                            tmpFileBinario.setFileSuDisco(tmpFileItem.getStoreLocation());
                            tmpFileBinario.setDimensione(sizeInBytes);
                        }
                        sessioneFinta.getFileBinari().add(tmpFileBinario);
                    }
                } else {
                    rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
                    rispostaWs.setErrorType(IRispostaWS.ErrorTypeEnum.WS_SIGNATURE);
                    rispostaWs.setErrorMessage(MessaggiWSBundle.getString(MessaggiWSBundle.WS_CHECK,
                            "I campi usati per rappresentare i componenti devono essere di tipo FILE"));
                    rispostaWs.setErrorCode(MessaggiWSBundle.WS_CHECK);
                }
            }
        }
        return fileItems;
    }
}
