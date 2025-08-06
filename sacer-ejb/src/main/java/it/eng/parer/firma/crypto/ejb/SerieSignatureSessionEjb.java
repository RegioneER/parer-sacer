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

import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.common.signature.Digest;
import it.eng.parer.common.signature.SignatureSession;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.elencoVersamento.utils.ElencoEnums.TipoFirma;
import it.eng.parer.entity.HsmSessioneFirma;
import it.eng.parer.entity.HsmVerSerieSessioneFirma;
import it.eng.parer.entity.IamUser;
import it.eng.parer.entity.SerStatoVerSerie;
import it.eng.parer.entity.SerVerSerie;
import it.eng.parer.entity.SerVerSerieDaElab;
import it.eng.parer.entity.constraint.HsmSessioneFirma.TiEsitoSessioneFirma;
import it.eng.parer.entity.constraint.HsmVerSerieSessioneFirma.TiEsitoFirmaVerSerie;
import it.eng.parer.firma.crypto.helper.SerieSignatureHelper;
import it.eng.parer.firma.crypto.sign.SigningRequest;
import it.eng.parer.serie.ejb.SerieEjb;
import it.eng.parer.serie.helper.SerieHelper;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.CostantiDB.StatoVersioneSerie;

/**
 * Manages the signature session of <code>Serie</code>
 *
 * @author Moretti_Lu
 */
@Stateless
@LocalBean
public class SerieSignatureSessionEjb implements SignatureSessionEjb {

    private static Logger logger = LoggerFactory
	    .getLogger(SerieSignatureSessionEjb.class.getName());

    @EJB
    private SerieEjb serieEjb;
    @EJB
    private SerieHelper serieHelper;
    @EJB
    private SerieSignatureHelper signHlp;

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public HsmSessioneFirma startSessioneFirma(SigningRequest request) {
	HsmSessioneFirma result = null;

	if (request == null) {
	    throw new IllegalArgumentException();
	}

	// Memorizes the HSMSessioneFirma into db
	long sessionId = signHlp.createSessioneFirma(request.getIdUtente());

	for (BigDecimal verSerieId : request.getFiles()) {
	    // Changes the versione status to FIRMA_IN_CORSO
	    SerVerSerie verSerie = signHlp.findByIdWithLock(SerVerSerie.class, verSerieId);
	    SerStatoVerSerie currentStato = signHlp.findById(SerStatoVerSerie.class,
		    verSerie.getIdStatoVerSerieCor());

	    // Checks if the "versione serie" status is DA_FIRMARE
	    if (currentStato.getTiStatoVerSerie().equals(StatoVersioneSerie.DA_FIRMARE.name())) {
		// Creates the new status FIRMA_IN_CORSO that associates to the SerVerSerie
		SerStatoVerSerie statoVerSerie = serieEjb.createSerStatoVerSerie(
			currentStato.getPgStatoVerSerie().add(BigDecimal.ONE),
			StatoVersioneSerie.FIRMA_IN_CORSO.name(),
			"Richiesta firma indice AIP serie", null, request.getIdUtente(), new Date(),
			verSerie.getIdVerSerie());
		signHlp.insertEntity(statoVerSerie, false);
		verSerie.setIdStatoVerSerieCor(new BigDecimal(statoVerSerie.getIdStatoVerSerie()));
		SerVerSerieDaElab verSerieDaElab = serieHelper
			.getSerVerSerieDaElabByIdVerSerie(verSerie.getIdVerSerie());
		verSerieDaElab
			.setTiStatoVerSerie(CostantiDB.StatoVersioneSerie.FIRMA_IN_CORSO.name());

		// Insert the document into the signature session
		signHlp.addFile2SessioneFirma(sessionId, verSerie.getIdVerSerie());
	    } else {
		logger.warn("Versione serie {} non è nello stato {} ma in {}",
			verSerie.getIdVerSerie(), StatoVersioneSerie.DA_FIRMARE,
			currentStato.getTiStatoVerSerie());
	    }
	}
	result = signHlp.findById(HsmSessioneFirma.class, sessionId);
	logger.info("Creata nuova sessione con id {}", sessionId);
	return result;
    }

    @Override
    public byte[] getFile2Sign(long idFile) {
	byte[] result = serieEjb.getSerFileVerSerieBlob(idFile,
		CostantiDB.TipoFileVerSerie.IX_AIP_UNISINCRO);
	return result;
    }

    @Override
    public boolean isFileEquals(long idFile, byte[] fileSbustato) {
	boolean result = false;
	if (fileSbustato != null) {
	    byte[] hashFileSbustato = DigestUtils.sha256(fileSbustato);

	    byte[] hashFileOriginale = serieEjb.getSerFileVerSerieHash(idFile,
		    CostantiDB.TipoFileVerSerie.IX_AIP_UNISINCRO, Digest.DigestAlgorithm.SHA256);
	    if (hashFileOriginale == null) {
		// Calculates the digest with the algorithm SHA256 but doesn't store it in db
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
	    ElencoEnums.TipoFirma tipoFirma) throws Exception {
	HsmSessioneFirma session = signHlp.findById(HsmSessioneFirma.class, sessionId);
	// Doesn't open a new transaction
	this.storeSignature(session, idFile, signedFile, signingDate, tipoFirma);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void storeSignature(HsmSessioneFirma session, long idFile, byte[] signedFile,
	    Date signingDate, ElencoEnums.TipoFirma tipoFirma) throws Exception {
	// Sets the "log" of the HSMSessionFirma
	HsmVerSerieSessioneFirma serieSession = signHlp.findSerieSessione(session, idFile);
	serieSession.setTiEsito(TiEsitoFirmaVerSerie.OK);
	serieSession.setTsEsito(new Date());

	// The signingDate in this method isn't used because the time is detech inside the method
	// storeFirma
	serieEjb.storeFirmaMandatoryTransaction(idFile, signedFile,
		session.getIamUser().getIdUserIam(), tipoFirma);

	logger.info("Firmata serie (id: {}) nella sessione con id {}", idFile,
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
	// Sets the "log" of the HSMSessionFirma
	HsmVerSerieSessioneFirma serieSession = signHlp.findSerieSessione(session, idFile);
	serieSession.setTiEsito(TiEsitoFirmaVerSerie.IN_ERRORE);
	serieSession.setTsEsito(new Date());
	serieSession.setCdErr(codError);
	serieSession.setDsErr(descrError);

	SerVerSerie verSerie = signHlp.findByIdWithLock(SerVerSerie.class, idFile);
	SerStatoVerSerie currentStato = signHlp.findById(SerStatoVerSerie.class,
		verSerie.getIdStatoVerSerieCor());

	if (currentStato.getTiStatoVerSerie()
		.equals(CostantiDB.StatoVersioneSerie.FIRMA_IN_CORSO.name())) {
	    // Creates the new status DA_FIRMARE that associates to the SerVerSerie
	    SerStatoVerSerie statoVerSerie = serieEjb.createSerStatoVerSerie(
		    currentStato.getPgStatoVerSerie().add(BigDecimal.ONE),
		    CostantiDB.StatoVersioneSerie.DA_FIRMARE.name(),
		    "Richiesta firma indice AIP serie fallita", null,
		    session.getIamUser().getIdUserIam(), new Date(), verSerie.getIdVerSerie());
	    signHlp.insertEntity(statoVerSerie, false);
	    verSerie.setIdStatoVerSerieCor(new BigDecimal(statoVerSerie.getIdStatoVerSerie()));

	    SerVerSerieDaElab verSerieDaElab = serieHelper
		    .getSerVerSerieDaElabByIdVerSerie(verSerie.getIdVerSerie());
	    verSerieDaElab.setTiStatoVerSerie(CostantiDB.StatoVersioneSerie.DA_FIRMARE.name());

	    logger.info("Errore nella firma della serie (id: {}) nella sessione con id {}", idFile,
		    session.getIdSessioneFirma());
	}
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

	for (HsmVerSerieSessioneFirma verSerieSess : session.getHsmVerSerieSessioneFirmas()) {
	    if (verSerieSess.is2sign()) {
		// Doesn't open a new transaction
		this.errorFile(session, verSerieSess.getSerVerSerie().getIdVerSerie(),
			codError.name(), descrError);
	    }
	}
	session.setTsFine(new Date());
	logger.info("Sessione con id {} andata in errore ({})", session.getIdSessioneFirma(),
		codError);
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
