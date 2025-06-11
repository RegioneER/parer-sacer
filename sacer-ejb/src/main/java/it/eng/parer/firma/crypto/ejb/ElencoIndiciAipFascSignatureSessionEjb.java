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

package it.eng.parer.firma.crypto.ejb;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.common.signature.SignatureSession;
import it.eng.parer.elencoVersFascicoli.helper.ElencoVersFascicoliHelper;
import it.eng.parer.elencoVersFascicoli.utils.ElencoEnums;
import it.eng.parer.elencoVersFascicoli.utils.ElencoEnums.ElencoStatusEnum;
import it.eng.parer.entity.ElvElencoVersFasc;
import it.eng.parer.entity.ElvElencoVersFascDaElab;
import it.eng.parer.entity.ElvFileElencoVersFasc;
import it.eng.parer.entity.ElvStatoElencoVersFasc;
import it.eng.parer.entity.HsmElencoFascSesFirma;
import it.eng.parer.entity.HsmSessioneFirma;
import it.eng.parer.entity.IamUser;
import it.eng.parer.entity.constraint.ElvElencoVersFascDaElab.TiStatoElencoFascDaElab;
import it.eng.parer.entity.constraint.ElvStatoElencoVersFasc.TiStatoElencoFasc;
import it.eng.parer.entity.constraint.HsmElencoFascSesFirma.TiEsitoFirmaElencoFasc;
import it.eng.parer.entity.constraint.HsmSessioneFirma.TiEsitoSessioneFirma;
import it.eng.parer.fascicoli.ejb.ElenchiVersFascicoliEjb;
import it.eng.parer.firma.crypto.helper.ElenchiIndiciAipFascSignatureHelper;
import it.eng.parer.firma.crypto.sign.SigningRequest;
import it.eng.parer.objectstorage.dto.BackendStorage;
import it.eng.parer.objectstorage.dto.ObjectStorageResource;
import it.eng.parer.objectstorage.ejb.ObjectStorageService;
import it.eng.parer.ws.utils.CostantiDB.TipiEncBinari;
import it.eng.parer.ws.utils.CostantiDB.TipiHash;

/**
 * Manages the signature session of <code>Elenco Indici AIP fascicoli</code>
 *
 * @author DiLorenzo_F
 */
@Stateless
@LocalBean
public class ElencoIndiciAipFascSignatureSessionEjb implements SignatureSessionEjb {

    private static Logger logger = LoggerFactory.getLogger(ElencoIndiciAipFascSignatureSessionEjb.class.getName());

    @EJB
    private ElencoVersFascicoliHelper elencoHlp;
    @EJB
    private ElenchiVersFascicoliEjb elencoEjb;
    @EJB
    private ElenchiIndiciAipFascSignatureHelper signHlp;
    // MEV#30399
    @EJB
    private ObjectStorageService objectStorageService;
    // end MEV#30399

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public HsmSessioneFirma startSessioneFirma(SigningRequest request) {
        HsmSessioneFirma result = null;

        if (request == null) {
            throw new IllegalArgumentException();
        }

        // Memorizes the HSMSessioneFirma into db
        long sessionId = signHlp.createSessioneFirma(request.getIdUtente());

        for (BigDecimal elencoId : request.getFiles()) {
            // Changes the elenco status to ELENCO_INDICI_AIP_FIRMA_IN_CORSO
            ElvElencoVersFasc elenco = elencoHlp.findByIdWithLock(ElvElencoVersFasc.class, elencoId);
            ElvStatoElencoVersFasc statoElenco = elencoHlp.findByIdWithLock(ElvStatoElencoVersFasc.class,
                    elenco.getIdStatoElencoVersFascCor());

            // Checks if the elenco status is ELENCO_INDICI_AIP_CREATO
            if (statoElenco.getTiStato().equals(TiStatoElencoFasc.ELENCO_INDICI_AIP_CREATO)) {
                // registro un nuovo stato = ELENCO_INDICI_AIP_FIRMA_IN_CORSO
                ElvStatoElencoVersFasc statoElencoVersFasc = new ElvStatoElencoVersFasc();
                statoElencoVersFasc.setElvElencoVersFasc(elenco);
                statoElencoVersFasc.setTsStato(new Date());
                statoElencoVersFasc.setTiStato(TiStatoElencoFasc.ELENCO_INDICI_AIP_FIRMA_IN_CORSO);

                elenco.getElvStatoElencoVersFascicoli().add(statoElencoVersFasc);

                // aggiorno l’elenco da elaborare assegnando stato = ELENCO_INDICI_AIP_FIRMA_IN_CORSO
                ElvElencoVersFascDaElab elencoVersFascDaElab = elencoHlp
                        .getElvElencoVersFascDaElabByIdElencoVersFasc(elenco.getIdElencoVersFasc());
                elencoVersFascDaElab.setTiStato(TiStatoElencoFascDaElab.ELENCO_INDICI_AIP_FIRMA_IN_CORSO);

                statoElencoVersFasc = elencoHlp.writeNewStatoElenco(statoElencoVersFasc);

                // aggiorno l’elenco specificando l’identificatore dello stato corrente
                elenco.setIdStatoElencoVersFascCor(new BigDecimal(statoElencoVersFasc.getIdStatoElencoVersFasc()));

                // TODO: verificare, Logs the status changement
                /*
                 * elencoHlp.writeLogElencoVers(elenco, elenco.getOrgStrut(), request.getIdUtente(),
                 * ElencoEnums.OpTypeEnum.FIRMA_ELENCO_INDICI_AIP_IN_CORSO.name());
                 */

                // Insert the document into the signature session
                signHlp.addFile2SessioneFirma(sessionId, elenco.getIdElencoVersFasc());
            } else {
                logger.warn("Elenco (id: " + elenco.getIdElencoVersFasc() + ") non è nello stato "
                        + ElencoStatusEnum.ELENCO_INDICI_AIP_CREATO.name() + " ma in " + statoElenco.getTiStato());
            }
        }
        result = signHlp.findById(HsmSessioneFirma.class, sessionId);
        logger.info("Creata nuova sessione con id " + sessionId);
        return result;
    }

    @Override
    public byte[] getFile2Sign(long idFile) {
        byte[] result = elencoEjb.retrieveFileIndiceElenco(idFile, ElencoEnums.FileTypeEnum.ELENCO_INDICI_AIP.name());
        return result;
    }

    @Override
    public boolean isFileEquals(long idFile, byte[] fileSbustato) {
        boolean result = false;
        if (fileSbustato != null) {
            byte[] hashFileSbustato = DigestUtils.sha256(fileSbustato);
            byte[] hashFileOriginale = null;

            List<ElvFileElencoVersFasc> elencoIndiceAipFasc = elencoHlp.retrieveFileIndiceElenco(idFile,
                    new String[] { ElencoEnums.FileTypeEnum.ELENCO_INDICI_AIP.name() });
            if (elencoIndiceAipFasc.size() != 1) {
                throw new IllegalStateException("Impossibile trovare l'elenco indici AIP fascicolo da firmare");
            } else {
                ElvFileElencoVersFasc fileElenco = elencoIndiceAipFasc.get(0);
                // TODO modify the constant hexBinary e SHA-256
                if (fileElenco.getCdEncodingHashFile() != null
                        && fileElenco.getCdEncodingHashFile().equals(TipiEncBinari.HEX_BINARY.descrivi())
                        && fileElenco.getDsAlgoHashFile() != null
                        && fileElenco.getDsAlgoHashFile().equals(TipiHash.SHA_256.descrivi())) {
                    try {
                        hashFileOriginale = Hex.decodeHex(fileElenco.getDsHashFile().toCharArray());
                    } catch (DecoderException e) {
                        logger.error("Impossibile decodificare l'hash del file", e);
                    }
                } else {
                    // Calculates the digest with the algorithm SHA256 but doesn't store it in db
                    byte[] fileOrig = this.getFile2Sign(idFile);
                    hashFileOriginale = DigestUtils.sha256(fileOrig);
                }
                result = Arrays.equals(hashFileOriginale, hashFileSbustato);
            }
        }
        return result;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void storeSignature(long sessionId, long idFile, byte[] signedFile, Date signingDate,
            it.eng.parer.elencoVersamento.utils.ElencoEnums.TipoFirma tipoFirma) {
        HsmSessioneFirma session = signHlp.findById(HsmSessioneFirma.class, sessionId);
        // Doesn't open a new transaction
        this.storeSignature(session, idFile, signedFile, signingDate, tipoFirma);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void storeSignature(HsmSessioneFirma session, long idFile, byte[] signedFile, Date signingDate,
            it.eng.parer.elencoVersamento.utils.ElencoEnums.TipoFirma tipoFirma) {
        // Sets the "log" of the HSMSessionFirma
        HsmElencoFascSesFirma elencoSession = signHlp.findElencoFascSes(session, idFile);
        elencoSession.setTiEsito(TiEsitoFirmaElencoFasc.OK);
        elencoSession.setTsEsito(new Date());
        // elencoEjb.storeFirmaElencoIndiceAipFasc(idFile, signedFile, signingDate, session.getIamUser().getIdUserIam(),
        // tipoFirma);
        // MEV#30399
        BackendStorage backendIndiciAip = objectStorageService
                .lookupBackendElenchiIndiciAipFasc(elencoSession.getElvElencoVersFasc().getOrgStrut().getIdStrut());
        ElvFileElencoVersFasc fileElencoVers = elencoEjb.storeFirmaElencoIndiceAipFasc(idFile, signedFile, signingDate,
                session.getIamUser().getIdUserIam(), backendIndiciAip, tipoFirma);
        /*
         * Se backendMetadata di tipo O.S. si effettua il salvataggio (con link su apposita entity)
         */
        if (backendIndiciAip.isObjectStorage()) {
            // retrieve normalized URN
            final String urn = fileElencoVers.getDsUrnNormalizFile();

            ObjectStorageResource res = objectStorageService.createResourcesInElenchiIndiciAipFasc(urn,
                    backendIndiciAip.getBackendName(), signedFile, fileElencoVers.getIdFileElencoVersFasc(),
                    fileElencoVers.getIdStrut());
            logger.debug("Salvato il file dell'elenco indici aip fascicoli firmato nel bucket {} con chiave {} ",
                    res.getBucket(), res.getKey());
        }
        // end MEV#30399

        // elencoEjb.storeFirmaElencoIndiceAipFasc(idFile, signedFile, signingDate,
        // session.getIamUser().getIdUserIam());

        logger.info("Firmato elenco (id: " + idFile + ") nella sessione con id " + session.getIdSessioneFirma());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void errorFile(long sessionId, long idFile, String codError, String descrError) {
        HsmSessioneFirma session = signHlp.findById(HsmSessioneFirma.class, sessionId);
        // Doesn't open a new transaction
        this.errorFile(session, idFile, codError, descrError);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void errorFile(HsmSessioneFirma session, long idFile, String codError, String descrError) {
        // Sets the "log" of the HSMSessioneFirma
        HsmElencoFascSesFirma elencoSession = signHlp.findElencoFascSes(session, idFile);
        elencoSession.setTiEsito(TiEsitoFirmaElencoFasc.IN_ERRORE);
        elencoSession.setTsEsito(new Date());
        elencoSession.setCdErr(codError);
        elencoSession.setDsErr(descrError);

        ElvElencoVersFasc elenco = elencoHlp.findByIdWithLock(ElvElencoVersFasc.class, idFile);
        // registro un nuovo stato = ELENCO_INDICI_AIP_CREATO
        ElvStatoElencoVersFasc statoElencoVersFasc = new ElvStatoElencoVersFasc();
        statoElencoVersFasc.setElvElencoVersFasc(elenco);
        statoElencoVersFasc.setTsStato(new Date());
        statoElencoVersFasc.setTiStato(TiStatoElencoFasc.ELENCO_INDICI_AIP_CREATO);

        elenco.getElvStatoElencoVersFascicoli().add(statoElencoVersFasc);

        // aggiorno l’elenco da elaborare assegnando stato = CHIUSO
        ElvElencoVersFascDaElab elencoDaElab = elencoHlp
                .getElvElencoVersFascDaElabByIdElencoVersFasc(elenco.getIdElencoVersFasc());
        elencoDaElab.setTiStato(TiStatoElencoFascDaElab.ELENCO_INDICI_AIP_CREATO);

        statoElencoVersFasc = elencoHlp.writeNewStatoElenco(statoElencoVersFasc);

        // aggiorno l’elenco specificando l’identificatore dello stato corrente
        elenco.setIdStatoElencoVersFascCor(new BigDecimal(statoElencoVersFasc.getIdStatoElencoVersFasc()));
        // Scrivo il motivo di chiusura
        elenco.setDlMotivoChius("Errore nella firma dell'elenco indici AIP fascicoli");

        // TODO: verificare, Logs the status changement
        /*
         * elencoHlp.writeLogElencoVers(elenco, elenco.getOrgStrut(), session.getIamUser().getIdUserIam(),
         * ElencoEnums.OpTypeEnum.FIRMA_ELENCO_INDICI_AIP_FALLITA.name());
         */

        logger.info("Errore nella firma dell'elenco (id: " + idFile + ") nella sessione con id "
                + session.getIdSessioneFirma());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void errorSessioneFirma(HsmSessioneFirma session, SignatureSession.CdErr codError) {
        if (session == null) {
            throw new IllegalArgumentException();
        }

        HsmSessioneFirma sessionNew = signHlp.findById(HsmSessioneFirma.class, session.getIdSessioneFirma());
        // Doesn't open a new transaction
        this.signatureSessionInError(sessionNew, codError, null);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void errorSessioneFirma(HsmSessioneFirma session, SignatureSession.CdErr codError, String descrError) {
        if (session == null) {
            throw new IllegalArgumentException();
        }

        HsmSessioneFirma sessionNew = signHlp.findById(HsmSessioneFirma.class, session.getIdSessioneFirma());
        // Doesn't open a new transaction
        this.signatureSessionInError(sessionNew, codError, descrError);
    }

    private void signatureSessionInError(HsmSessioneFirma session, SignatureSession.CdErr codError, String descrError) {
        // If codError is null, sets it with this value
        if (codError == null) {
            codError = SignatureSession.CdErr.UNKNOWN_ERROR;
        }

        session.setTiEsitoSessioneFirma(TiEsitoSessioneFirma.ERRORE);
        session.setCdErr(codError.name());
        session.setDsErr(descrError);

        for (HsmElencoFascSesFirma elencoSess : session.getHsmElencoFascSesFirmas()) {
            if (elencoSess.is2sign()) {
                // Doesn't open a new transaction
                this.errorFile(session, elencoSess.getElvElencoVersFasc().getIdElencoVersFasc(), codError.name(),
                        descrError);
            }
        }
        session.setTsFine(new Date());
        logger.info("Sessione con id " + session.getIdSessioneFirma() + " andata in errore (" + codError.name() + ")");
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean closeSessioneFirma(HsmSessioneFirma session) {
        if (session == null) {
            throw new IllegalArgumentException();
        }

        return this.closeSessioneFirma(session.getIdSessioneFirma());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public boolean closeSessioneFirma(long sessionId) {
        boolean result;
        HsmSessioneFirma session = signHlp.findById(HsmSessioneFirma.class, sessionId);
        if (signHlp.isAllFileSigned(session.getIdSessioneFirma())) {
            session.setTiEsitoSessioneFirma(TiEsitoSessioneFirma.OK);
            result = true;
        } else {
            session.setTiEsitoSessioneFirma(TiEsitoSessioneFirma.WARNING);
            result = false;
        }
        session.setTsFine(new Date());
        logger.info("Chiusa la sessione (id " + session.getIdSessioneFirma() + ") con esito "
                + session.getTiEsitoSessioneFirma().name());
        return result;
    }

    @Override
    public boolean hasUserActiveSessions(IamUser user) {
        boolean result = false;
        if (user != null) {
            result = this.hasUserActiveSessions(user.getIdUserIam());
        }
        return result;
    }

    @Override
    public boolean hasUserActiveSessions(long userId) {
        boolean result = false;
        List<HsmSessioneFirma> sessions = signHlp.getActiveSessionsByUser(userId);
        if (sessions != null && sessions.size() > 0) {
            result = true;
        }
        return result;
    }

    @Override
    public List<HsmSessioneFirma> getActiveSessionsByUser(IamUser user) {
        List<HsmSessioneFirma> result = null;
        if (user != null) {
            result = this.getActiveSessionsByUser(user.getIdUserIam());
        }
        return result;
    }

    @Override
    public List<HsmSessioneFirma> getActiveSessionsByUser(long userId) {
        List<HsmSessioneFirma> result = signHlp.getActiveSessionsByUser(userId);
        return result;
    }

    @Override
    public boolean hasUserBlockedSessions(IamUser user) {
        boolean result = false;
        if (user != null) {
            result = this.hasUserBlockedSessions(user.getIdUserIam());
        }
        return result;
    }

    @Override
    public boolean hasUserBlockedSessions(long userId) {
        boolean result = false;
        List<HsmSessioneFirma> sessions = signHlp.getBlockedSessionsByUser(userId);
        if (sessions != null && sessions.size() > 0) {
            result = true;
        }
        return result;
    }

    @Override
    public void unlockBlockedSessions(IamUser user) {
        if (user == null) {
            throw new IllegalArgumentException();
        }
        this.unlockBlockedSessions(user.getIdUserIam());
    }

    @Override
    public void unlockBlockedSessions(long userId) {
        List<HsmSessioneFirma> sessions = signHlp.getBlockedSessionsByUser(userId);

        for (HsmSessioneFirma session : sessions) {
            this.errorSessioneFirma(session, SignatureSession.CdErr.BLOCKED_SESSION);
        }
    }
}
