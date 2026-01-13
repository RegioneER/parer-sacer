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
import it.eng.parer.firma.crypto.helper.ElenchiFascSignatureHelper;
import it.eng.parer.firma.crypto.sign.SigningRequest;
import it.eng.parer.ws.utils.CostantiDB.TipiEncBinari;
import it.eng.parer.ws.utils.CostantiDB.TipiHash;

/**
 * Manages the signature session of <code>Elenco</code>
 *
 * @author DiLorenzo_F
 */
@Stateless
@LocalBean
public class ElencoFascSignatureSessionEjb implements SignatureSessionEjb {

    private static Logger logger = LoggerFactory
            .getLogger(ElencoFascSignatureSessionEjb.class.getName());

    @EJB
    private ElencoVersFascicoliHelper elencoHlp;
    @EJB
    private ElenchiVersFascicoliEjb elencoEjb;
    @EJB
    private ElenchiFascSignatureHelper signHlp;

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
            // Changes the elenco status to FIRMA_IN_CORSO
            ElvElencoVersFasc elenco = elencoHlp.findByIdWithLock(ElvElencoVersFasc.class,
                    elencoId);
            ElvStatoElencoVersFasc statoElenco = elencoHlp.findByIdWithLock(
                    ElvStatoElencoVersFasc.class, elenco.getIdStatoElencoVersFascCor());

            // Checks if the elenco status is CHIUSO
            if (statoElenco.getTiStato().equals(TiStatoElencoFasc.CHIUSO)) {
                // registro un nuovo stato = FIRMA_IN_CORSO
                ElvStatoElencoVersFasc statoElencoVersFasc = new ElvStatoElencoVersFasc();
                statoElencoVersFasc.setElvElencoVersFasc(elenco);
                statoElencoVersFasc.setTsStato(new Date());
                statoElencoVersFasc.setTiStato(TiStatoElencoFasc.FIRMA_IN_CORSO);

                elenco.getElvStatoElencoVersFascicoli().add(statoElencoVersFasc);

                // aggiorno l’elenco da elaborare assegnando stato = FIRMA_IN_CORSO
                ElvElencoVersFascDaElab elencoVersFascDaElab = elencoHlp
                        .getElvElencoVersFascDaElabByIdElencoVersFasc(elenco.getIdElencoVersFasc());
                elencoVersFascDaElab.setTiStato(TiStatoElencoFascDaElab.FIRMA_IN_CORSO);

                statoElencoVersFasc = elencoHlp.writeNewStatoElenco(statoElencoVersFasc);

                // aggiorno l’elenco specificando l’identificatore dello stato corrente
                elenco.setIdStatoElencoVersFascCor(
                        new BigDecimal(statoElencoVersFasc.getIdStatoElencoVersFasc()));

                // Insert the document into the signature session
                signHlp.addFile2SessioneFirma(sessionId, elenco.getIdElencoVersFasc());
            } else {
                logger.warn("Elenco (id: {}) non è nello stato {} ma in {}",
                        elenco.getIdElencoVersFasc(), TiStatoElencoFasc.CHIUSO,
                        statoElenco.getTiStato());
            }
        }
        result = signHlp.findById(HsmSessioneFirma.class, sessionId);
        logger.info("Creata nuova sessione con id {}", sessionId);
        return result;
    }

    @Override
    public byte[] getFile2Sign(long idFile) {
        byte[] result = elencoEjb.retrieveFileIndiceElenco(idFile,
                ElencoEnums.FileTypeEnum.INDICE_ELENCO.name());
        return result;
    }

    @Override
    public boolean isFileEquals(long idFile, byte[] fileSbustato) {
        boolean result = false;

        if (fileSbustato != null) {
            byte[] hashFileSbustato = DigestUtils.sha256(fileSbustato);
            byte[] hashFileOriginale = null;

            ElvFileElencoVersFasc fileElenco = elencoHlp.retrieveElvFileElencoVersFasc(idFile,
                    ElencoEnums.FileTypeEnum.INDICE_ELENCO.name());
            // TODO modify the constant hexBinary e SHA-256
            if (fileElenco.getCdEncodingHashFile() != null
                    && fileElenco.getCdEncodingHashFile()
                            .equals(TipiEncBinari.HEX_BINARY.descrivi())
                    && fileElenco.getDsAlgoHashFile() != null
                    && fileElenco.getDsAlgoHashFile().equals(TipiHash.SHA_256.descrivi())) {
                try {
                    hashFileOriginale = Hex.decodeHex(fileElenco.getDsHashFile().toCharArray());
                } catch (DecoderException ex) {
                    logger.error(ex.getCause() + ";" + ex.getMessage());
                }
            } else {
                // Calculates the digest with the algorithm SHA-256 but doesn't store it in db
                byte[] fileOrig = this.getFile2Sign(idFile);
                hashFileOriginale = DigestUtils.sha256(fileOrig);
            }
            result = Arrays.equals(hashFileOriginale, hashFileSbustato);
        }

        return result;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void storeSignature(long sessionId, long idFile, byte[] signedFile, Date signingDate,
            it.eng.parer.elencoVersamento.utils.ElencoEnums.TipoFirma tipoFirma) throws Exception {
        HsmSessioneFirma session = signHlp.findById(HsmSessioneFirma.class, sessionId);
        // Doesn't open a new transaction
        this.storeSignature(session, idFile, signedFile, signingDate, tipoFirma);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void storeSignature(HsmSessioneFirma session, long idFile, byte[] signedFile,
            Date signingDate, it.eng.parer.elencoVersamento.utils.ElencoEnums.TipoFirma tipoFirma)
            throws Exception {
        // Sets the "log" of the HSMSessionFirma
        HsmElencoFascSesFirma elencoSession = signHlp.findElencoSessione(session, idFile);
        elencoSession.setTiEsito(TiEsitoFirmaElencoFasc.OK);
        elencoSession.setTsEsito(new Date());
        elencoEjb.storeFirma(idFile, signedFile, signingDate, session.getIamUser().getIdUserIam(),
                tipoFirma);

        logger.info("Firmato elenco (id: {}) nella sessione con id {}", idFile,
                session.getIdSessioneFirma());
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
    public void errorFile(HsmSessioneFirma session, long idFile, String codError,
            String descrError) {
        // Sets the "log" of the HSMSessioneFirma
        HsmElencoFascSesFirma elencoSession = signHlp.findElencoSessione(session, idFile);
        elencoSession.setTiEsito(TiEsitoFirmaElencoFasc.IN_ERRORE);
        elencoSession.setTsEsito(new Date());
        elencoSession.setCdErr(codError);
        elencoSession.setDsErr(descrError);

        ElvElencoVersFasc elenco = elencoHlp.findByIdWithLock(ElvElencoVersFasc.class, idFile);
        // registro un nuovo stato = CHIUSO
        ElvStatoElencoVersFasc statoElencoVersFasc = new ElvStatoElencoVersFasc();
        statoElencoVersFasc.setElvElencoVersFasc(elenco);
        statoElencoVersFasc.setTsStato(new Date());
        statoElencoVersFasc.setTiStato(TiStatoElencoFasc.CHIUSO);

        elenco.getElvStatoElencoVersFascicoli().add(statoElencoVersFasc);

        // aggiorno l’elenco da elaborare assegnando stato = CHIUSO
        ElvElencoVersFascDaElab elencoVersFascDaElab = elencoHlp
                .getElvElencoVersFascDaElabByIdElencoVersFasc(elenco.getIdElencoVersFasc());
        elencoVersFascDaElab.setTiStato(TiStatoElencoFascDaElab.CHIUSO);

        statoElencoVersFasc = elencoHlp.writeNewStatoElenco(statoElencoVersFasc);

        // aggiorno l’elenco specificando l’identificatore dello stato corrente
        elenco.setIdStatoElencoVersFascCor(
                new BigDecimal(statoElencoVersFasc.getIdStatoElencoVersFasc()));
        // Scrivo il motivo di chiusura
        elenco.setDlMotivoChius("Errore nella firma dell'elenco di versamento fascicoli");

        logger.info("Errore nella firma dell'elenco (id: {}) nella sessione con id {}", idFile,
                session.getIdSessioneFirma());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void errorSessioneFirma(HsmSessioneFirma session, SignatureSession.CdErr codError) {
        if (session == null) {
            throw new IllegalArgumentException();
        }

        HsmSessioneFirma sessionNew = signHlp.findById(HsmSessioneFirma.class,
                session.getIdSessioneFirma());
        // Doesn't open a new transaction
        this.signatureSessionInError(sessionNew, codError, null);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void errorSessioneFirma(HsmSessioneFirma session, SignatureSession.CdErr codError,
            String descrError) {
        if (session == null) {
            throw new IllegalArgumentException();
        }

        HsmSessioneFirma sessionNew = signHlp.findById(HsmSessioneFirma.class,
                session.getIdSessioneFirma());
        // Doesn't open a new transaction
        this.signatureSessionInError(sessionNew, codError, descrError);
    }

    private void signatureSessionInError(HsmSessioneFirma session, SignatureSession.CdErr codError,
            String descrError) {
        // If codError is null, sets it with this value
        if (codError == null) {
            codError = SignatureSession.CdErr.UNKNOWN_ERROR;
        }

        session.setTiEsitoSessioneFirma(TiEsitoSessioneFirma.ERRORE);
        session.setCdErr(codError.name());
        session.setDsErr(descrError);

        for (HsmElencoFascSesFirma elencoFascSes : session.getHsmElencoFascSesFirmas()) {
            if (elencoFascSes.is2sign()) {
                // Doesn't open a new transaction
                this.errorFile(session, elencoFascSes.getElvElencoVersFasc().getIdElencoVersFasc(),
                        codError.name(), descrError);
            }
        }
        session.setTsFine(new Date());
        logger.info("Sessione con id " + session.getIdSessioneFirma() + " andata in errore ("
                + codError.name() + ")");
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
        logger.info("Chiusa la sessione (id {}) con esito {}", session.getIdSessioneFirma(),
                session.getTiEsitoSessioneFirma());
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
