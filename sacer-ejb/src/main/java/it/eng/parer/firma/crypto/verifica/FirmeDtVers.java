package it.eng.parer.firma.crypto.verifica;

import it.eng.parer.crypto.model.CryptoEnums.EsitoControllo;
import it.eng.parer.crypto.model.CryptoEnums.TipoControlli;
import it.eng.parer.crypto.model.CryptoEnums.TipoFileEnum;
import it.eng.parer.crypto.model.ParerCRL;
import it.eng.parer.crypto.model.ParerRevokedCertificate;
import it.eng.parer.crypto.model.ValidationInfos;
import it.eng.parer.crypto.model.exceptions.CryptoParerException;
import it.eng.parer.entity.AroCompDoc;
import it.eng.parer.entity.AroContrFirmaComp;
import it.eng.parer.entity.AroContrVerifFirmaDtVer;
import it.eng.parer.entity.AroFirmaComp;
import it.eng.parer.entity.AroVerifFirmaDtVer;
import it.eng.parer.entity.FirCertifCa;
import it.eng.parer.entity.FirCertifFirmatario;
import it.eng.parer.entity.FirCrl;
import it.eng.parer.entity.FirFilePerFirma;
import it.eng.parer.entity.FirUrlDistribCrl;
import it.eng.parer.exception.CRLNotFoundException;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SignatureException;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.apache.commons.lang3.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestClientException;

/**
 *
 * @author Quaranta_M
 */
@Stateless
@LocalBean
public class FirmeDtVers {

    private static final Logger LOG = LoggerFactory.getLogger(FirmeDtVers.class.getName());

    @Resource
    private SessionContext context;

    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager em;
    private CriteriaBuilder cb;

    @EJB
    private CryptoInvoker cryInvoker;

    @PostConstruct
    private void initialize() {
        cb = em.getCriteriaBuilder();
    }

    public void verificaFirme(AroCompDoc compDoc, Date dataRif) throws SignatureException, CRLNotFoundException {
        LOG.info("Verifica firme: Id componente {} data di riferimento {}", compDoc.getIdCompDoc(), dataRif);
        boolean esitoPositivo = true;
        List<AroFirmaComp> firmeComp = compDoc.getAroFirmaComps();
        try {
            if (firmeComp != null) {

                for (AroFirmaComp firmaComp : firmeComp) {
                    if (firmaComp.getTiEsitoContrConforme().equals(EsitoControllo.FORMATO_NON_CONFORME.name())) {
                        esitoPositivo = false;
                        continue;
                    }
                    ValidationInfos certificateExpiration = new ValidationInfos();
                    ValidationInfos caCertificateExpiration = new ValidationInfos();
                    ValidationInfos crlValidation = new ValidationInfos();

                    HashMap<String, AroContrFirmaComp> controlliVers = new HashMap<>();

                    boolean isCertificatoErrato = false;
                    for (AroContrFirmaComp comp : firmaComp.getAroContrFirmaComps()) {
                        controlliVers.put(comp.getTiContr(), comp);
                        if (comp.getTiContr().equals(TipoControlli.CERTIFICATO.name())) {
                            isCertificatoErrato = comp.getTiEsitoContrFirma()
                                    .equals(EsitoControllo.CERTIFICATO_ERRATO.name());
                        }
                    }
                    this.caCertificateExpiration(caCertificateExpiration, firmaComp, dataRif);
                    this.certificateExpiration(certificateExpiration, firmaComp, dataRif, isCertificatoErrato);
                    // CryproLibrary
                    ParerCRL crl = this.certificateRevocation(crlValidation, firmaComp, dataRif,
                            caCertificateExpiration.isValid(), certificateExpiration.isValid());

                    AroVerifFirmaDtVer firma = this.buildFirma(crl, caCertificateExpiration, certificateExpiration,
                            crlValidation, firmaComp.getFirCertifFirmatario().getFirCertifCa(), controlliVers, true);
                    // gli associo la firma appartenente al componente con lo
                    // stesso Base64
                    firma.setAroFirmaComp(firmaComp);

                    if (firma.getTiEsitoVerifFirma().equals(EsitoControllo.NEGATIVO.name())) {
                        esitoPositivo = false;
                    }
                    firmaComp.getAroVerifFirmaDtVers().add(firma);
                }

                if (esitoPositivo) {
                    compDoc.setDsEsitoVerifFirmeDtVers(EsitoControllo.POSITIVO.message());
                    compDoc.setTiEsitoVerifFirmeDtVers(EsitoControllo.POSITIVO.name());
                } else {
                    compDoc.setDsEsitoVerifFirmeDtVers(EsitoControllo.NEGATIVO.message());
                    compDoc.setTiEsitoVerifFirmeDtVers(EsitoControllo.NEGATIVO.name());
                }
            }
        } catch (CryptoParerException ex) {
            String message = ex.getMessage();
            LOG.error(message, ex);
            throw new SignatureException(message);
        }
    }

    // protected AroVerifFirmaDtVer buildFirma(X509CRL crl, ValidationInfos caCertificateExpiration,
    protected AroVerifFirmaDtVer buildFirma(ParerCRL crl, ValidationInfos caCertificateExpiration,
            ValidationInfos certificateExpiration, ValidationInfos crlValidation, FirCertifCa certificatoCa,
            HashMap<String, AroContrFirmaComp> controlliVers, boolean flagExceptionNoCrl)
            throws CryptoParerException, CRLNotFoundException {
        boolean esitoVerifiche = true;
        CriteriaQuery<FirCrl> c;
        Root<FirCrl> root;
        // CRL + BLOB
        FirCrl firCrl = null;
        // (Controllo che la crl esiste perchè l'esito del controllo di validità
        // potrebbe aver dato esito negativo)
        if (crl != null) {
            c = cb.createQuery(FirCrl.class);
            root = c.from(FirCrl.class);
            c.select(root).where(cb.and(cb.equal(root.get("dtIniCrl"), crl.getThisUpdate()),
                    cb.equal(root.get("firCertifCa"), certificatoCa)));
            List<FirCrl> resultList = em.createQuery(c).getResultList();
            firCrl = resultList.isEmpty() ? null : resultList.get(0);
            if (firCrl == null) {
                firCrl = new FirCrl();
                BigInteger crlNum = crl.getCrlNum();
                firCrl.setDtIniCrl(crl.getThisUpdate());
                firCrl.setDtScadCrl(crl.getNextUpdate());
                firCrl.setNiSerialCrl(crlNum != null ? new BigDecimal(crlNum) : null);
                firCrl.setFirCertifCa(certificatoCa);
                certificatoCa.getFirCrls().add(firCrl);
                FirFilePerFirma blobCrl = new FirFilePerFirma();
                blobCrl.setTiFilePerFirma(TipoFileEnum.CRL.name());
                blobCrl.setBlFilePerFirma(crl.getEncoded());
                blobCrl.setFirCrl(firCrl);
                firCrl.setFirFilePerFirma(blobCrl);
            }
        }

        // FIRMA
        AroVerifFirmaDtVer firma = new AroVerifFirmaDtVer();
        firma.setAroContrVerifFirmaDtVers(new ArrayList<>());

        // CONTROLLI FIRMA - CRITTOGRAFICO signatureValidations
        AroContrVerifFirmaDtVer controlloCrittografico = new AroContrVerifFirmaDtVer();
        controlloCrittografico.setAroVerifFirmaDtVer(firma);
        firma.getAroContrVerifFirmaDtVers().add(controlloCrittografico);
        controlloCrittografico.setTiContr(TipoControlli.CRITTOGRAFICO.name());

        // CONTROLLI FIRMA - CATENA_TRUSTED CertificateAssociation &&
        // CertificateReliability
        AroContrVerifFirmaDtVer controlliCatenaTrusted = new AroContrVerifFirmaDtVer();
        controlliCatenaTrusted.setAroVerifFirmaDtVer(firma);
        firma.getAroContrVerifFirmaDtVers().add(controlliCatenaTrusted);
        controlliCatenaTrusted.setTiContr(TipoControlli.CATENA_TRUSTED.name());

        // CONTROLLI FIRMA - CERTIFICATO CertificateExpiration
        AroContrVerifFirmaDtVer controlliCertificato = new AroContrVerifFirmaDtVer();
        controlliCertificato.setAroVerifFirmaDtVer(firma);
        firma.getAroContrVerifFirmaDtVers().add(controlliCertificato);
        controlliCertificato.setTiContr(TipoControlli.CERTIFICATO.name());

        // CONTROLLI FIRMA - CRL CertificateRevocation
        AroContrVerifFirmaDtVer controlliCRL = new AroContrVerifFirmaDtVer();
        controlliCRL.setAroVerifFirmaDtVer(firma);
        firma.getAroContrVerifFirmaDtVers().add(controlliCRL);
        controlliCRL.setTiContr(TipoControlli.CRL.name());
        controlliCRL.setFirCrl(firCrl);

        // CONTROLLI FIRMA - CRITTOGRAFICO
        String esito;
        String msg;
        if (controlliVers.get(TipoControlli.CRITTOGRAFICO_ABILITATO.name()) != null) { // TEST
            // NECESSARIO PER IL PREGRESSO
            esito = controlliVers.get(TipoControlli.CRITTOGRAFICO_ABILITATO.name()).getTiEsitoContrFirma();
            msg = controlliVers.get(TipoControlli.CRITTOGRAFICO_ABILITATO.name()).getDsMsgEsitoContrFirma();
        } else {
            esito = controlliVers.get(TipoControlli.CRITTOGRAFICO.name()).getTiEsitoContrFirma();
            msg = controlliVers.get(TipoControlli.CRITTOGRAFICO.name()).getDsMsgEsitoContrFirma();
        }
        controlloCrittografico.setTiEsitoContrVerif(esito);
        controlloCrittografico.setDsMsgContrVerif(msg);
        if (esito.equals(EsitoControllo.NEGATIVO.name())) {
            esitoVerifiche = false;
        }

        // CONTROLLI FIRMA - CATENA_TRUSTED
        if (controlliVers.get(TipoControlli.CATENA_TRUSTED_ABILITATO.name()) != null) { // TEST
            // NECESSARIO PER IL PREGRESSO
            esito = controlliVers.get(TipoControlli.CATENA_TRUSTED_ABILITATO.name()).getTiEsitoContrFirma();
            msg = controlliVers.get(TipoControlli.CATENA_TRUSTED_ABILITATO.name()).getDsMsgEsitoContrFirma();
        } else {
            esito = controlliVers.get(TipoControlli.CATENA_TRUSTED.name()).getTiEsitoContrFirma();
            msg = controlliVers.get(TipoControlli.CATENA_TRUSTED.name()).getDsMsgEsitoContrFirma();
        }
        controlliCatenaTrusted.setTiEsitoContrVerif(esito);
        controlliCatenaTrusted.setDsMsgContrVerif(msg);
        if (esito.equals(EsitoControllo.NEGATIVO.name())) {
            esitoVerifiche = false;
        } else {
            if (caCertificateExpiration != null) {
                if (caCertificateExpiration.isValid()) {
                    controlliCertificato.setTiEsitoContrVerif(EsitoControllo.POSITIVO.name());
                    controlliCertificato.setDsMsgContrVerif(EsitoControllo.POSITIVO.message());

                } else {
                    esitoVerifiche = false;
                    controlliCertificato.setTiEsitoContrVerif(caCertificateExpiration.getEsito().name());
                    controlliCertificato.setDsMsgContrVerif(caCertificateExpiration.getEsito().message() + " : "
                            + caCertificateExpiration.getErrorsString());
                }
            }

        }

        if (certificateExpiration != null) {
            if (certificateExpiration.isValid()) {
                controlliCertificato.setTiEsitoContrVerif(EsitoControllo.POSITIVO.name());
                controlliCertificato.setDsMsgContrVerif(EsitoControllo.POSITIVO.message());

            } else {
                esitoVerifiche = false;
                controlliCertificato.setTiEsitoContrVerif(certificateExpiration.getEsito().name());
                controlliCertificato.setDsMsgContrVerif(
                        certificateExpiration.getEsito().message() + " : " + certificateExpiration.getErrorsString());
            }
        }

        /*
         * CONTROLLI FIRMA - CRL nb: la nuova chiamata alla libreria di chiusura dovrà lanciare un'eccezione se l'esito
         * di verifica CRL è pari a CRL_SCADUTA o CRL_NON_SCARICABILE e la CA risulta attiva (ie. la CA ha un
         * certificato in corso di validità)
         *
         */
        boolean isCRLWarning = false;
        if (crlValidation != null) {

            if (crlValidation.isValid()) {
                controlliCRL.setTiEsitoContrVerif(EsitoControllo.POSITIVO.name());
                controlliCRL.setDsMsgContrVerif(EsitoControllo.POSITIVO.message());
            } else {
                if (crlValidation.getEsito().equals(EsitoControllo.CERTIFICATO_SCADUTO_3_12_2009)) {
                    isCRLWarning = true;
                } else if (crlValidation.getEsito().equals(EsitoControllo.CRL_SCADUTA)
                        || crlValidation.getEsito().equals(EsitoControllo.CRL_NON_SCARICABILE)) {
                    Date now = new Date();
                    /*
                     * Lancio l'eccezione se il flag è attivo, il certificato della CA è attivo e il punto di
                     * distribuzione è definito Questo controllo non è presente al versamento.
                     */
                    if (flagExceptionNoCrl && certificatoCa.getDtIniValCertifCa().before(now)
                            && certificatoCa.getDtFinValCertifCa().after(now)
                            && certificatoCa.getFirUrlDistribCrls() != null
                            && !certificatoCa.getFirUrlDistribCrls().isEmpty()) {
                        throw new CRLNotFoundException("CRL scaduta o non scaricabile e la CA ("
                                + certificatoCa.getDlDnIssuerCertifCa() + ") è ancora attiva");
                    }

                } else {
                    esitoVerifiche = false;
                }
                controlliCRL.setTiEsitoContrVerif(crlValidation.getEsito().name());
                controlliCRL.setDsMsgContrVerif(
                        crlValidation.getEsito().message() + " : " + crlValidation.getErrorsString());
            }
        }
        String tiEsito;
        String tiEsitoMsg;
        if (esitoVerifiche) {
            tiEsito = (isCRLWarning ? EsitoControllo.WARNING.name() : EsitoControllo.POSITIVO.name());
            tiEsitoMsg = (isCRLWarning ? EsitoControllo.WARNING.message() : EsitoControllo.POSITIVO.message());
        } else {
            tiEsito = EsitoControllo.NEGATIVO.name();
            tiEsitoMsg = EsitoControllo.NEGATIVO.message();
        }

        firma.setTiEsitoVerifFirma(tiEsito);
        firma.setDsMsgEsitoVerifFirma(tiEsitoMsg);
        return firma;

    }

    private void certificateExpiration(ValidationInfos validationInfos, AroFirmaComp firmaComp, Date dataRiferimento,
            boolean isCertificatoErrato) {
        if (isCertificatoErrato) {
            validationInfos.addError("Il certificato non supporta l'utilizzo non-repudation");
            validationInfos.setEsito(EsitoControllo.CERTIFICATO_ERRATO);
            return;
        }
        FirCertifFirmatario cerFirma = firmaComp.getFirCertifFirmatario();
        DateFormat dateFormatter = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.ITALY);
        // se la data di riferimento è compresa nell'intervallo di validità del
        // certificato il controllo avrà esito POSITIVO
        if (cerFirma.getDtIniValCertifFirmatario().before(dataRiferimento)
                && cerFirma.getDtFinValCertifFirmatario().after(dataRiferimento)) {
            validationInfos.setEsito(EsitoControllo.POSITIVO);
        } else if (!cerFirma.getDtIniValCertifFirmatario().before(dataRiferimento)) {
            validationInfos.addError("Il certificato è valido a partire dalla data: "
                    + dateFormatter.format(cerFirma.getDtIniValCertifFirmatario())
                    + " successiva al riferimento temporale usato: " + dateFormatter.format(dataRiferimento));
            validationInfos.setEsito(EsitoControllo.CERTIFICATO_NON_VALIDO);
        } else if (!cerFirma.getDtFinValCertifFirmatario().after(dataRiferimento)) {
            validationInfos.addError("Il certificato è scaduto in data: "
                    + dateFormatter.format(cerFirma.getDtFinValCertifFirmatario()));
            validationInfos.setEsito(EsitoControllo.CERTIFICATO_SCADUTO);
        }
    }

    private void caCertificateExpiration(ValidationInfos validationInfos, AroFirmaComp firmaComp,
            Date dataRiferimento) {

        FirCertifCa cerCa = firmaComp.getFirCertifFirmatario().getFirCertifCa();
        DateFormat dateFormatter = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.ITALY);
        // se la data di riferimento è compresa nell'intervallo di validità del
        // certificato il controllo avrà esito POSITIVO
        if (cerCa.getDtIniValCertifCa().before(dataRiferimento) && cerCa.getDtFinValCertifCa().after(dataRiferimento)) {
            validationInfos.setEsito(EsitoControllo.POSITIVO);
        } else if (!cerCa.getDtIniValCertifCa().before(dataRiferimento)) {
            validationInfos.addError("Il certificato di certificazione è entrato in vigore in data: "
                    + dateFormatter.format(cerCa.getDtIniValCertifCa()) + " successivo al riferimento temporale usato: "
                    + dateFormatter.format(dataRiferimento));
            validationInfos.setEsito(EsitoControllo.NEGATIVO);
        } else if (!cerCa.getDtFinValCertifCa().after(dataRiferimento)) {
            validationInfos.addError("Il certificato di certificazione è scaduto in data: "
                    + dateFormatter.format(cerCa.getDtFinValCertifCa()) + " precedente al riferimento temporale: "
                    + dateFormatter.format(dataRiferimento));
            validationInfos.setEsito(EsitoControllo.NEGATIVO);
        }
    }

    private boolean isNull(ParerCRL crl) {
        return crl == null || (crl.getKeyId() == null && crl.getPrincipalName() == null && crl.getSubjectDN() == null);
    }

    private ParerCRL certificateRevocation(ValidationInfos validationInfos, AroFirmaComp firmaComp,
            Date dataRiferimento, boolean isCaAttiva, boolean isCertificatoValido) {
        DateFormat dateFormatter = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, Locale.ITALY);

        FirCertifFirmatario cerFirma = firmaComp.getFirCertifFirmatario();

        String keyId = cerFirma.getFirCertifCa().getDsSubjectKeyId();
        ParerCRL crl = null;

        /*
         * MAC #17718 se il certificato è scaduto non ha senso effettuare il controllo della CRL
         */
        if (!isCertificatoValido) {
            validationInfos.addError(
                    "Il controllo non è necessario: Certificato scaduto alla data del riferimento temporale, verifica CRL non effettuata");
            validationInfos.setEsito(EsitoControllo.NON_NECESSARIO);
            return null;
        }

        try {

            if (keyId == null) {
                crl = cryInvoker.retrieveCRL(cerFirma.getFirFilePerFirma().getBlFilePerFirma());
            } else {
                // Utilizzo il subject se c'è altrimenti l'issuer. (Vedi MEV #27001)
                String dnCa = cerFirma.getFirCertifCa().getDlDnSubjectCertifCa();
                if (dnCa == null || StringUtils.isBlank(dnCa)) {
                    LOG.debug("Per la CA con id {} non e' stato trovato il SubjectDN. Utilizzo l'issuer DN",
                            cerFirma.getFirCertifCa().getIdCertifCa());
                    dnCa = cerFirma.getFirCertifCa().getDlDnIssuerCertifCa();
                }

                crl = cryInvoker.retrieveCRL(dnCa, keyId);
            }
        } catch (EJBException e) {
            dumpEjbException(e);
        } catch (RestClientException e) {
            dumpRestClientException(e);
        } catch (CryptoParerException ex) {
            LOG.error("Errore durante il reperimento della CRL dalla cache/db", ex);
            throw ex;
        }
        /*-
         * Eseguo lo scarico della CRL solo se si verifica una delle seguenti condizioni: 
         * - non ho la CRL su DB 
         * - ho la CRL su DB ma è scaduta e la CA è attiva 
         * - ho la CRL su DB ma è scaduta, la CA è inattiva e la CRL disponibile su DB ha una scadenza precedente alla scadenza del certificato della CA
         */

        if (isNull(crl) || (crl.getNextUpdate().before(dataRiferimento) && isCaAttiva)
                || (crl.getNextUpdate().before(dataRiferimento) && !isCaAttiva
                        && crl.getNextUpdate().before(cerFirma.getFirCertifCa().getDtFinValCertifCa()))) {
            try {
                if (cerFirma.getFirCertifCa().getFirUrlDistribCrls() == null
                        || cerFirma.getFirCertifCa().getFirUrlDistribCrls().isEmpty()) {
                    validationInfos.addError(
                            "CRL non verificabile, non è definito un punto di distribuzione presso il quale recuperare la CRL (CA non trusted)");
                    validationInfos.setEsito(EsitoControllo.CRL_NON_SCARICABILE);
                    return null;
                }
                List<String> urls = new ArrayList<>();
                for (FirUrlDistribCrl firUrl : cerFirma.getFirCertifCa().getFirUrlDistribCrls()) {
                    urls.add(firUrl.getDlUrlDistribCrl());
                }
                crl = cryInvoker.addCrlByURL(urls);
            } catch (EJBException | RestClientException | CryptoParerException ex) {

                if (ex.getClass().equals(EJBException.class)) {
                    dumpEjbException((EJBException) ex);
                }
                if (ex.getClass().equals(RestClientException.class)) {
                    dumpRestClientException((RestClientException) ex);
                }

                // non è stato possibile scaricare la CRL
                FirCertifCa cerCa = cerFirma.getFirCertifCa();

                if (!cerCa.getDtIniValCertifCa().before(dataRiferimento)) {
                    validationInfos.addError(
                            "CRL non verificabile, il certificato dell'issuer risulta non essere ancora valido");
                    validationInfos.setEsito(EsitoControllo.CRL_NON_SCARICABILE);
                } else {
                    validationInfos.addError(
                            "CRL non verificabile, non si dispone di una CRL su cui validare il certificato di firma");
                    Calendar cal = Calendar.getInstance();
                    cal.set(2009, Calendar.DECEMBER, 3);
                    // Se il certificato della CA è scaduto prima del 3 dicembre
                    // 2009 la CRL potrebbe non esserci
                    if (cerCa.getDtFinValCertifCa().before(cal.getTime())) {
                        validationInfos.setEsito(EsitoControllo.CERTIFICATO_SCADUTO_3_12_2009);
                    } else {
                        validationInfos.setEsito(EsitoControllo.CRL_NON_SCARICABILE);
                    }
                }
                return null;

            }
        }

        // crl qui deve essere != null
        if (crl.getNextUpdate().after(dataRiferimento)) {
            ParerRevokedCertificate crlEntry = crl
                    .getRevokedCertificate(cerFirma.getNiSerialCertifFirmatario().toBigInteger());
            if (crlEntry != null) {
                if (dataRiferimento != null && crlEntry.getRevocationDate().before(dataRiferimento)) {
                    validationInfos.addError(
                            "Certificato revocato in data: " + dateFormatter.format(crlEntry.getRevocationDate())
                                    + " (antecedente a: " + dateFormatter.format(dataRiferimento) + ")");
                    validationInfos.setEsito(EsitoControllo.CERTIFICATO_REVOCATO);
                } else if (dataRiferimento == null) {
                    validationInfos.addError(
                            "Certificato già revocato in data: " + dateFormatter.format(crlEntry.getRevocationDate()));
                    validationInfos.setEsito(EsitoControllo.CERTIFICATO_REVOCATO);
                }
            }
        } else {
            validationInfos.addError("La CRL ottenuta dal punto di distribuzione dalla CA scade il: "
                    + dateFormatter.format(crl.getNextUpdate())
                    + " precedente alla data del riferimento temporale usato: "
                    + dateFormatter.format(dataRiferimento));
            validationInfos.setEsito(EsitoControllo.CRL_SCADUTA);
        }
        if (validationInfos.isValid()) {
            validationInfos.setEsito(EsitoControllo.POSITIVO);
        }
        return crl;

    }

    private void dumpEjbException(EJBException e) {
        String tipo = "(generica) " + e.getClass().getName();
        if (e.getCause() != null) {
            tipo = "(innestata) " + e.getCause().getClass().getName();
        }

        LOG.warn("Eccezione di tipo {} invocando le CRL. La transazione è marcata per rollback? {}", tipo,
                context.getRollbackOnly(), e);
    }

    private void dumpRestClientException(RestClientException e) {
        String tipo = "(generica) " + e.getClass().getName();
        if (e.getCause() != null) {
            tipo = "(innestata) " + e.getCause().getClass().getName();
        }

        LOG.warn("Eccezione di tipo {} invocando le CRL. La transazione è marcata per rollback? {}", tipo,
                context.getRollbackOnly(), e);
    }
}
