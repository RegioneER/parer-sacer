/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.firma.crypto.ejb;

import it.eng.parer.elencoVersamento.helper.ElencoVersamentoHelper;
import it.eng.parer.elencoVersamento.utils.ComponenteDaVerificare;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.entity.AroCompDoc;
import it.eng.parer.entity.constraint.AroUpdUnitaDoc.AroUpdUDTiStatoUpdElencoVers;
import it.eng.parer.exception.CRLNotFoundException;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.firma.crypto.verifica.FirmeDtVers;
import java.math.BigDecimal;
import java.security.SignatureException;
import java.util.Collection;
import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Iacolucci_M
 */
@Stateless
@LocalBean
public class ElaboraCodaVerificaFirmeEjb {

    private static final String DESC_CONSUMER = "Consumer coda verifica firma";

    Logger log = LoggerFactory.getLogger(ElaboraCodaVerificaFirmeEjb.class);

    @EJB
    private ElencoVersamentoHelper evHelper;
    @EJB
    private FirmeDtVers firmeDtVers;

    /**
     * Elaboro l'elenco nel consumer job Verifica firme a Data Vers – consumer della fase 1 del precedente job di
     * Verifica Firme.
     *
     * @param idUd
     *            - Id unità documentaria in elenco
     * @param idElenco
     *            - Id elenco
     * @param dtFirmaElenco
     *            - Data firma elenco
     * 
     * @throws ParerUserError
     *             - Eccezione che fa passare all'unità documentaria successiva
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void elaboraUDFase1(Long idUd, Long idElenco, Date dtFirmaElenco) throws ParerUserError {
        // Reset indicatore che segnala che la unità doc deve essere nuovamente verificata
        boolean ripetiVerifica = false;
        Date systemDate = new Date();
        // Mette in lock l'UD e la rilascerà al termine di questo metodo.
        Collection<ComponenteDaVerificare> componentiDaVerificare = evHelper.retrieveCompsToVerify(idElenco, idUd);

        // se non ho componenti esco
        if (componentiDaVerificare.isEmpty()) {
            log.warn(DESC_CONSUMER + " - L'ud con id " + idUd
                    + " non contiene componenti da verificare nè da aggiungere in coda per l'AIP");
        }

        log.debug(DESC_CONSUMER + " - verifico i componenti dell'unita documentaria " + idUd);

        // elaboro i componenti
        for (ComponenteDaVerificare compDaVerif : componentiDaVerificare) {
            AroCompDoc comp = evHelper.retrieveCompDocById(compDaVerif.getIdCompDoc());
            if (compDaVerif.isFlCompFirmato()) {
                log.debug(DESC_CONSUMER + " - verifico le firme del componente " + compDaVerif.getIdCompDoc());
                try {
                    firmeDtVers.verificaFirme(comp, compDaVerif.getDtCreazione());
                    // FIXME Per il momento generica
                    // } catch (SignatureException ex) {
                } catch (RuntimeException ex) {
                    log.error(DESC_CONSUMER + " - SignatureException = " + ex.getMessage()
                            + ", termino l'esecuzione e abortisco la transazione attiva");
                    ripetiVerifica = true;
                    // throw new ParerUserError(ex.getMessage());
                    // FIXME Per il momento generica
                    // } catch (CRLNotFoundException ex) {
                } catch (Exception ex) {
                    // nell'online blocco l'esecuzione. Nel batch passo al volume successivo
                    log.error(DESC_CONSUMER + " - CRLNotFoundException = " + ex.getMessage());
                    // throw new ParerUserError("Non è possibile completare la verifica dell'UD, una CRL non è
                    // scaricabile o è scaduta. Abortisco la transazione");
                    ripetiVerifica = true;
                }
            }
        }

        if (!ripetiVerifica) {
            // il sistema aggiorna l'unità doc presente nell'elenco, assegnando stato relativo all'elenco =
            // IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS
            evHelper.aggiornaStatoUnitaDocInElenco(idUd, idElenco,
                    ElencoEnums.UdDocStatusEnum.IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS.name(), systemDate, null, null);
            // il sistema aggiorna i documenti aggiunti appartenenti all'unità doc presenti nell'elenco assegnando stato
            // relativo all'elenco = IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS
            evHelper.aggiornaStatoDocInElenco(idUd, idElenco,
                    ElencoEnums.DocStatusEnum.IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS.name(), systemDate, null, null);
            // il sistema aggiorna gli aggiornamenti metadati relativi all'unità doc presenti nell'elenco assegnando
            // stato relativo all'elenco = IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS
            evHelper.aggiornaStatoUpdInElenco(idUd, idElenco,
                    AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS.name(), systemDate, null, null);
        } else {
            Date lastResetSystemDate = new Date();
            // Recupero l'ultimo valore del numero di reset per l'unità doc presente nell'elenco
            BigDecimal niResetStatoUd = evHelper.getNiResetStatoUnitaDocInElenco(idUd, idElenco);
            niResetStatoUd = niResetStatoUd.add(BigDecimal.ONE);
            // il sistema aggiorna l'unità doc presente nell'elenco, assegnando stato relativo all'elenco =
            // IN_ELENCO_VALIDATO
            evHelper.aggiornaStatoUnitaDocInElenco(idUd, idElenco,
                    ElencoEnums.UdDocStatusEnum.IN_ELENCO_VALIDATO.name(), null, lastResetSystemDate, niResetStatoUd);
            // Recupero l'ultimo valore del numero di reset per i documenti aggiunti appartenenti all'unità doc presenti
            // nell'elenco
            BigDecimal niResetStatoDoc = evHelper.getNiResetStatoDocInElenco(idUd, idElenco);
            niResetStatoDoc = niResetStatoDoc.add(BigDecimal.ONE);
            // il sistema aggiorna i documenti aggiunti appartenenti all'unità doc presenti nell'elenco assegnando stato
            // relativo all'elenco = IN_ELENCO_VALIDATO
            evHelper.aggiornaStatoDocInElenco(idUd, idElenco, ElencoEnums.DocStatusEnum.IN_ELENCO_VALIDATO.name(), null,
                    lastResetSystemDate, niResetStatoDoc);
            // Recupero l'ultimo valore del numero di reset per gli aggiornamenti metadati relativi all'unità doc
            // presenti nell'elenco
            BigDecimal niResetStatoUpd = evHelper.getNiResetStatoUpdInElenco(idUd, idElenco);
            niResetStatoUpd = niResetStatoUpd.add(BigDecimal.ONE);
            // il sistema aggiorna gli aggiornamenti metadati relativi all'unità doc presenti nell'elenco assegnando
            // stato relativo all'elenco = IN_ELENCO_VALIDATO
            evHelper.aggiornaStatoUpdInElenco(idUd, idElenco, AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_VALIDATO.name(),
                    null, lastResetSystemDate, niResetStatoUpd);
        }
    }

}
