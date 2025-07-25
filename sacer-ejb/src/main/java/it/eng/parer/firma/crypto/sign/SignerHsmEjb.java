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

package it.eng.parer.firma.crypto.sign;

import java.util.Date;
import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.hsm.AuthenticationException;
import it.eng.hsm.ClientHSM;
import it.eng.hsm.HSM;
import it.eng.hsm.HSM.AMBIENTE_HSM;
import it.eng.hsm.HSMException;
import it.eng.hsm.OTPException;
import it.eng.hsm.UserBlockedException;
import it.eng.hsm.beans.HSMSignatureSession;
import it.eng.hsm.beans.HSMUser;
import it.eng.parer.common.signature.Signature;
import it.eng.parer.common.signature.SignatureSession.CdErr;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.entity.HsmSessioneFirma;
import it.eng.parer.entity.constraint.HsmSessioneFirma.TiSessioneFirma;
import it.eng.parer.firma.crypto.ejb.ElencoFascSignatureSessionEjb;
import it.eng.parer.firma.crypto.ejb.ElencoIndiciAipFascSignatureSessionEjb;
import it.eng.parer.firma.crypto.ejb.ElencoIndiciAipSignatureSessionEjb;
import it.eng.parer.firma.crypto.ejb.ElencoSignatureSessionEjb;
import it.eng.parer.firma.crypto.ejb.SerieSignatureSessionEjb;
import it.eng.parer.firma.crypto.ejb.SignatureSessionEjb;
import it.eng.parer.web.helper.ConfigurationHelper;

/**
 *
 *
 * @author Moretti_Lu
 */
@Stateless
@LocalBean
public class SignerHsmEjb {

    private final static Logger logger = LoggerFactory.getLogger(SignerHsmEjb.class.getName());

    @EJB(mappedName = "java:app/Parer-ejb/ConfigurationHelper")
    private ConfigurationHelper configurationHelper;
    @EJB
    private ElencoSignatureSessionEjb elencoSignEjb;
    @EJB
    private SerieSignatureSessionEjb serieSignEjb;
    @EJB
    private ElencoIndiciAipSignatureSessionEjb elencoIndiciAipSignEjb;
    @EJB
    private ElencoFascSignatureSessionEjb elencoFascSignEjb;
    @EJB
    private ElencoIndiciAipFascSignatureSessionEjb elencoIndiciAipFascSignEjb;
    @Resource
    private SessionContext context;

    private ClientHSM initClient() throws HSMException {
        ClientHSM clientHsm;
        AMBIENTE_HSM ambiente = null;

        String value = configurationHelper.getValoreParamApplicByApplic(Signature.SISTEMA_FIRMA);
        if (value.equals(Signature.SistemaFirma.HSM_TEST.name())) {
            ambiente = AMBIENTE_HSM.TEST;
        } else if (value.equals(Signature.SistemaFirma.HSM_PROD.name())) {
            ambiente = AMBIENTE_HSM.PRODUZIONE;
        } else {
            throw new IllegalArgumentException("Variabile \"SISTEMA_FIRMA\" non valida");
        }

        try {
            clientHsm = HSM.getInstance(ambiente);
            logger.info("Creato client HSM - {}", ambiente);
        } catch (HSMException e) {
            logger.error("Errore nella inizializzazione del client HSM - {}", ambiente.name());
            throw e;
        }

        return clientHsm;
    }

    private SignatureSessionEjb getSignatureEjb(TiSessioneFirma type) {
        SignatureSessionEjb result = null;
        switch (type) {
        case ELENCHI:
            result = elencoSignEjb;
            break;
        case SERIE:
            result = serieSignEjb;
            break;
        case ELENCO_INDICI_AIP:
            result = elencoIndiciAipSignEjb;
            break;
        case ELENCHI_FASC:
            result = elencoFascSignEjb;
            break;
        case ELENCHI_INDICI_AIP_FASC:
            result = elencoIndiciAipFascSignEjb;
            break;
        default:
            break;
        }
        return result;
    }

    public Future<SigningResponse> signP7M(SigningRequest request) {
        Future<SigningResponse> result;

        if (request != null) {
            SignatureSessionEjb ejb = getSignatureEjb(request.getType());
            if (ejb != null) {
                if (!ejb.hasUserActiveSessions(request.getIdUtente())) {
                    HsmSessioneFirma sessione = ejb.startSessioneFirma(request);
                    result = context.getBusinessObject(SignerHsmEjb.class).signP7m(sessione, request.getUserHSM());
                } else {
                    result = new AsyncResult<>(SigningResponse.ACTIVE_SESSION_YET);
                }
            } else {
                logger.error("SignatureSessionEjb non trovato per il tipo: {}", request.getType());
                result = new AsyncResult<>(SigningResponse.UNKNOWN_ERROR);
            }
        } else {
            result = new AsyncResult<>(SigningResponse.UNKNOWN_ERROR);
        }
        return result;
    }

    @Asynchronous
    public Future<SigningResponse> signP7m(HsmSessioneFirma sessione, HSMUser user) {
        SigningResponse result = SigningResponse.UNKNOWN_ERROR;
        int fileNum;
        CdErr codErr = null;
        SignatureSessionEjb ejb = getSignatureEjb(sessione.getTiSessioneFirma());
        ClientHSM clientHsm;

        try {
            HSMSignatureSession hsmSession;

            try {
                clientHsm = initClient();
            } catch (Exception ex) {
                codErr = CdErr.HSM_NOT_RESPOND;
                throw ex;
            }

            // Open sign session
            try {
                hsmSession = clientHsm.openSignatureSession(user);
            } catch (HSMException ex) {
                logger.error("Error during the opening of HSM session", ex);
                codErr = CdErr.ERROR_SESSION_CREATION;
                throw ex;
            }

            // Signs all the files
            fileNum = sessione.getNumFile();

            for (int index = 0; index < fileNum; index++) {
                Long idFile = sessione.getIdFile(index);

                byte[] file2sign = ejb.getFile2Sign(idFile);
                try {
                    // byte[] signedFile = clientHsm.signP7M(hsmSession, file2sign);

                    // MAC#35254 - Correzione delle anomalie nella fase di marcatura temporale embedded negli elenchi
                    // indici aip UD
                    byte[] signedFile = null;
                    /*
                     * Nel caso in cui si tratti di gestione elenchi indici AIP la cui gestione può essere MARCA o
                     * SIGILLO con anche MARCA allora il parametro generico di entrata "marcaTemporale" viene ignorato e
                     * si richiede per XADES la sola firma oppure anche la marca in base al tipo gestione dell'elenco di
                     * versamento.
                     */
                    if (sessione.getTiSessioneFirma().equals(sessione.getTiSessioneFirma().ELENCO_INDICI_AIP)) {
                        String tipoGestione = elencoIndiciAipSignEjb.getTipoGestioneElenco(idFile,
                                sessione.getIamUser().getIdUserIam());
                        if (tipoGestione != null
                                && (tipoGestione.equals(ElencoEnums.GestioneElencoEnum.MARCA_FIRMA.name())
                                        || tipoGestione.equals(ElencoEnums.GestioneElencoEnum.MARCA_SIGILLO.name()))) {
                            signedFile = clientHsm.signP7M(hsmSession, file2sign, true);
                        } else {
                            signedFile = clientHsm.signP7M(hsmSession, file2sign, false);
                        }
                    } else if (sessione.getTiSessioneFirma().equals(sessione.getTiSessioneFirma().SERIE)) {
                        /*
                         * Per le serie si è deciso che la firma è sempre Xades senza marca in quanto il tipo di
                         * conservazione fiscale probabilmente diventerà deprecato, quindi quella parte di codice che
                         * gestisce lo stato della serie rimane che lo stato lo mette a FIRMATA_NO_MARCA
                         */
                        signedFile = clientHsm.signP7M(hsmSession, file2sign, false);
                    } else if (sessione.getTiSessioneFirma()
                            .equals(sessione.getTiSessioneFirma().ELENCHI_INDICI_AIP_FASC)) {
                        signedFile = clientHsm.signP7M(hsmSession, file2sign, false); // Richiede sempre firma senza
                        // marca
                    } else {
                        signedFile = clientHsm.signP7M(hsmSession, file2sign, false); // Richiede sempre firma senza
                        // marca in tutti gli altri casi
                    }
                    // Fine MAC

                    if (signedFile != null) {
                        Date signingDate = new Date();

                        // TODO eseguire la verifica della firma realizzata
                        boolean verificaOk = true;

                        if (verificaOk) {
                            // TODO Ottenere il file sbustato
                            byte[] fileSbustato = null;

                            // Questo controllo serve a garantire che il documento firmato fosse realmente quello
                            // inviato
                            boolean hashOk = ejb.isFileEquals(idFile, fileSbustato);
                            // TODO Da rimuovere dopo aver realizzato i due TODO sopra
                            hashOk = true;

                            if (hashOk) {
                                ejb.storeSignature(sessione, idFile, signedFile, signingDate,
                                        it.eng.parer.elencoVersamento.utils.ElencoEnums.TipoFirma.CADES);
                            } else {
                                // TODO da sistemare
                                ejb.errorFile(sessione, idFile, CdErr.ERROR_SIGNING.name(),
                                        "File firmato differente da quello originale");
                            }
                        } else {
                            // TODO da sistemare
                            ejb.errorFile(sessione, idFile, CdErr.ERROR_SIGNING.name(),
                                    "File firmato non correttamente");
                        }
                    }
                } catch (AuthenticationException ex) {
                    logger.warn("The HSM credentials are wrong");
                    codErr = CdErr.AUTH_WRONG;
                    throw ex;
                } catch (OTPException ex) {
                    if (index == 0) {
                        logger.warn("The OTP is wrong");
                        codErr = CdErr.OTP_WRONG;
                    } else {
                        logger.warn("The OTP is expired");
                        codErr = CdErr.OTP_EXPIRED;
                    }
                    throw ex;
                } catch (UserBlockedException ex) {
                    logger.warn("The user {} is blocked", user.getUsername());
                    codErr = CdErr.USER_BLOCKED;
                    throw ex;
                } catch (HSMException ex) {
                    /*
                     * LM 23/08/2017 Se il metodo di firma lancia questa eccezione invece che una di quelle soprastanti,
                     * bisogna controllare se "wrappa" correttamente le eccezioni provenienti dal HSM (vd documentazione
                     * su Wiki)
                     */
                    throw ex;
                }
            }

            // Close sign session
            try {
                clientHsm.closeSignatureSession(hsmSession);
            } catch (HSMException e) {
                logger.error("Error during the closing of HSM session", e);
            }

            boolean isOk = ejb.closeSessioneFirma(sessione);

            if (isOk) {
                result = SigningResponse.OK;
            } else {
                result = SigningResponse.WARNING;
            }
        } catch (HSMException e_hsm) {
            if (codErr != null) {
                switch (codErr) {
                case AUTH_WRONG:
                    result = SigningResponse.AUTH_WRONG;
                    break;
                case OTP_WRONG:
                    result = SigningResponse.OTP_WRONG;
                    break;
                case OTP_EXPIRED:
                    result = SigningResponse.OTP_EXPIRED;
                    break;
                case USER_BLOCKED:
                    result = SigningResponse.USER_BLOCKED;
                    break;
                case HSM_NOT_RESPOND:
                case ERROR_SESSION_CREATION:
                    result = SigningResponse.HSM_ERROR;
                    break;
                default:
                    break;
                }
            } else {
                codErr = CdErr.UNKNOWN_ERROR;
            }
            ejb.errorSessioneFirma(sessione, codErr, e_hsm.getErrorDescription());
        } catch (Exception e) {
            ejb.errorSessioneFirma(sessione, CdErr.UNKNOWN_ERROR);
            logger.error("Errore imprevisto durante sessione di firma: ", e);
        } finally {
            // Cleans the user information: deletes password and otp values
            user.cleanUser();
        }

        return new AsyncResult<>(result);
    }

    // --------------------------------------------------------------------------------
    // MEV#15967 - Attivazione della firma Xades e XadesT
    // --------------------------------------------------------------------------------

    public Future<SigningResponse> signXades(SigningRequest request) {
        Future<SigningResponse> result;

        if (request != null) {
            SignatureSessionEjb ejb = getSignatureEjb(request.getType());

            if (!ejb.hasUserActiveSessions(request.getIdUtente())) {
                HsmSessioneFirma sessione = ejb.startSessioneFirma(request);
                result = context.getBusinessObject(SignerHsmEjb.class).signXades(sessione, request.getUserHSM());
            } else {
                result = new AsyncResult<>(SigningResponse.ACTIVE_SESSION_YET);
            }
        } else {
            result = new AsyncResult<>(SigningResponse.UNKNOWN_ERROR);
        }
        return result;
    }

    @Asynchronous
    public Future<SigningResponse> signXades(HsmSessioneFirma sessione, HSMUser user) {
        SigningResponse result = SigningResponse.UNKNOWN_ERROR;
        int fileNum;
        CdErr codErr = null;
        SignatureSessionEjb ejb = getSignatureEjb(sessione.getTiSessioneFirma());
        ClientHSM clientHsm;

        try {
            HSMSignatureSession hsmSession;

            try {
                clientHsm = initClient();
            } catch (Exception ex) {
                codErr = CdErr.HSM_NOT_RESPOND;
                throw ex;
            }

            // Open sign session
            try {
                hsmSession = clientHsm.openSignatureSession(user);
            } catch (HSMException ex) {
                logger.error("Error during the opening of HSM session", ex);
                codErr = CdErr.ERROR_SESSION_CREATION;
                throw ex;
            }

            // Signs all the files
            fileNum = sessione.getNumFile();

            for (int index = 0; index < fileNum; index++) {
                Long idFile = sessione.getIdFile(index);

                byte[] file2sign = ejb.getFile2Sign(idFile);
                try {
                    byte[] signedFile = null;
                    // MEV#15967 - Attivazione della firma Xades e XadesT
                    /*
                     * Nel caso in cui si tratti di gestione elenchi indici AIP la cui gestione può essere MARCA o
                     * SIGILLO con anche MARCA allora il parametro generico di entrata "marcaTemporale" viene ignorato e
                     * si richiede per XADES la sola firma oppure anche la marca in base al tipo gestione dell'elenco di
                     * versamento.
                     */
                    if (sessione.getTiSessioneFirma().equals(sessione.getTiSessioneFirma().ELENCO_INDICI_AIP)) {
                        String tipoGestione = elencoIndiciAipSignEjb.getTipoGestioneElenco(idFile,
                                sessione.getIamUser().getIdUserIam());
                        if (tipoGestione != null
                                && (tipoGestione.equals(ElencoEnums.GestioneElencoEnum.MARCA_FIRMA.name())
                                        || tipoGestione.equals(ElencoEnums.GestioneElencoEnum.MARCA_SIGILLO.name()))) {
                            signedFile = clientHsm.signXAdES(hsmSession, file2sign, true);
                        } else {
                            signedFile = clientHsm.signXAdES(hsmSession, file2sign, false);
                        }
                    } else if (sessione.getTiSessioneFirma().equals(sessione.getTiSessioneFirma().SERIE)) {
                        /*
                         * Per le serie si è deciso che la firma è sempre senza marca in quanto il tipo di conservazione
                         * fiscale probabilmente diventerà deprecato, quindi quella parte di codice che gestisce lo
                         * stato della serie rimane che lo stato lo mette a FIRMATA_NO_MARCA
                         */
                        signedFile = clientHsm.signXAdES(hsmSession, file2sign, false);
                    } else if (sessione.getTiSessioneFirma()
                            .equals(sessione.getTiSessioneFirma().ELENCHI_INDICI_AIP_FASC)) {
                        signedFile = clientHsm.signXAdES(hsmSession, file2sign, false); // Richiede sempre firma senza
                                                                                        // marca
                    } else {
                        signedFile = clientHsm.signXAdES(hsmSession, file2sign, false); // Richiede sempre firma senza
                                                                                        // marca in tutti gli altri casi
                    }

                    if (signedFile != null) {
                        Date signingDate = new Date();

                        // TODO eseguire la verifica della firma realizzata
                        boolean verificaOk = true;

                        if (verificaOk) {
                            // TODO Ottenere il file sbustato
                            byte[] fileSbustato = null;

                            // Questo controllo serve a garantire che il documento firmato fosse realmente quello
                            // inviato
                            boolean hashOk = ejb.isFileEquals(idFile, fileSbustato);
                            // TODO Da rimuovere dopo aver realizzato i due TODO sopra
                            hashOk = true;

                            if (hashOk) {
                                ejb.storeSignature(sessione, idFile, signedFile, signingDate,
                                        it.eng.parer.elencoVersamento.utils.ElencoEnums.TipoFirma.XADES);
                            } else {
                                // TODO da sistemare
                                ejb.errorFile(sessione, idFile, CdErr.ERROR_SIGNING.name(),
                                        "File firmato differente da quello originale");
                            }
                        } else {
                            // TODO da sistemare
                            ejb.errorFile(sessione, idFile, CdErr.ERROR_SIGNING.name(),
                                    "File firmato non correttamente");
                        }
                    }
                } catch (AuthenticationException ex) {
                    logger.warn("The HSM credentials are wrong");
                    codErr = CdErr.AUTH_WRONG;
                    throw ex;
                } catch (OTPException ex) {
                    if (index == 0) {
                        logger.warn("The OTP is wrong");
                        codErr = CdErr.OTP_WRONG;
                    } else {
                        logger.warn("The OTP is expired");
                        codErr = CdErr.OTP_EXPIRED;
                    }
                    throw ex;
                } catch (UserBlockedException ex) {
                    logger.warn("The user {} is blocked", user.getUsername());
                    codErr = CdErr.USER_BLOCKED;
                    throw ex;
                } catch (HSMException ex) {
                    /*
                     * LM 23/08/2017 Se il metodo di firma lancia questa eccezione invece che una di quelle soprastanti,
                     * bisogna controllare se "wrappa" correttamente le eccezioni provenienti dal HSM (vd documentazione
                     * su Wiki)
                     */
                    throw ex;
                }
            }

            // Close sign session
            try {
                clientHsm.closeSignatureSession(hsmSession);
            } catch (HSMException e) {
                logger.error("Error during the closing of HSM session", e);
            }

            boolean isOk = ejb.closeSessioneFirma(sessione);

            if (isOk) {
                result = SigningResponse.OK;
            } else {
                result = SigningResponse.WARNING;
            }
        } catch (HSMException e_hsm) {
            if (codErr != null) {
                switch (codErr) {
                case AUTH_WRONG:
                    result = SigningResponse.AUTH_WRONG;
                    break;
                case OTP_WRONG:
                    result = SigningResponse.OTP_WRONG;
                    break;
                case OTP_EXPIRED:
                    result = SigningResponse.OTP_EXPIRED;
                    break;
                case USER_BLOCKED:
                    result = SigningResponse.USER_BLOCKED;
                    break;
                case HSM_NOT_RESPOND:
                case ERROR_SESSION_CREATION:
                    result = SigningResponse.HSM_ERROR;
                    break;
                default:
                    break;
                }
            } else {
                codErr = CdErr.UNKNOWN_ERROR;
            }
            ejb.errorSessioneFirma(sessione, codErr, e_hsm.getErrorDescription());
        } catch (Exception e) {
            ejb.errorSessioneFirma(sessione, CdErr.UNKNOWN_ERROR);
            logger.error("Errore imprevisto durante sessione di firma: ", e);
        } finally {
            // Cleans the user information: deletes password and otp values
            user.cleanUser();
        }

        return new AsyncResult<>(result);
    }

    // --------------------------------------------------------------------------------
    // MEV#15967 - Attivazione della firma Xades e XadesT
    // --------------------------------------------------------------------------------

}
