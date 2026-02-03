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
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.job.codaIndiceAip.ejb;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.elencoVersamento.helper.ElencoVersamentoHelper;
import it.eng.parer.elencoVersamento.utils.AggiornamentoInElenco;
import it.eng.parer.elencoVersamento.utils.ComponenteInElenco;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.entity.AroIndiceAipUdDaElab;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.ElvElencoVersDaElab;
import it.eng.parer.entity.constraint.AroUpdUnitaDoc.AroUpdUDTiStatoUpdElencoVers;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.viewEntity.ElvVLisUdByStato;

/**
 *
 * @author DiLorenzo_F
 */
@Stateless
@LocalBean
public class ElaboraCodaIndiciAipDaElabEjb {

    private static final String DESC_CONSUMER = "Consumer coda indici aip unità documentarie da elaborare";

    Logger log = LoggerFactory.getLogger(ElaboraCodaIndiciAipDaElabEjb.class);

    @EJB
    private ElencoVersamentoHelper evHelper;

    /**
     * Elaboro l'elenco nel consumer job Coda Indici Aip unità documentarie da elaborare – consumer
     * della fase 2 del precedente job di Verifica Firme. In questo caso ci sono 2 logiche diverse a
     * seconda che ci siano o meno documenti aggiunti.
     *
     * @param unitaInElenco - Unità documentaria in elenco
     * @param elencoDaElab  - elenco versamento da elaborare
     *
     * @throws ParerUserError - eccezione per passare all'ud successiva.
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void elaboraUDFase2(ElvVLisUdByStato unitaInElenco, ElvElencoVersDaElab elencoDaElab)
            throws ParerUserError {
        AroUnitaDoc aud = evHelper
                .retrieveAndLockUnitaDocById(unitaInElenco.getIdUnitaDoc().longValue());
        if ("VERS_UD".equals(unitaInElenco.getTiMotivo())) {
            // (2) se l'unità documentaria non è presente solo a causa di aggiunta doc e/o di
            // aggiornamento metadati
            aggiornaElencoSeNonCiSonoSoloDocAggiunti_Upd(aud, elencoDaElab);
        } else {
            // (3) se l'unità documentaria e' presente a causa di aggiunta doc e/o per aggiornamento
            // metadati
            aggiornaElencoSeCiSonoDocAggiunti_Upd(aud, elencoDaElab);
        }

    }

    /**
     * Caso più complesso: ci sono solo documenti aggiunti e/o aggiornamenti metadati
     *
     * @param aud    - Ud di cui è stato eseguito il lock
     * @param elenco - Elenco di versamento
     *
     * @throws ParerUserError - eccezione che fa passare alla ud successiva
     */
    private void aggiornaElencoSeCiSonoDocAggiunti_Upd(AroUnitaDoc aud,
            ElvElencoVersDaElab elencoDaElab) throws ParerUserError {
        // Setto indicatore che segnala che la unità doc deve essere nuovamente verificata
        boolean ripetiVerifica = false;
        Date systemDate = new Date();

        long idElencoVers = elencoDaElab.getElvElencoVer().getIdElencoVers();
        // (a) il sistema determina la data minima dei documenti dell'unità doc presenti nell'elenco
        Date dataMinimaDocInElenco = evHelper.getDataMinimaDocInElenco(aud.getIdUnitaDoc(),
                idElencoVers);
        if (dataMinimaDocInElenco != null) {
            // throw new ParerUserError("Non è possibile completare la verifica dell'UD (con id " +
            // aud.getIdUnitaDoc()
            // + ") in caso di documenti aggiunti, la data minima dei documenti in elenco è
            // nulla.");

            /*
             * (b) il sistema determina i documenti dell'unità doc, non in elenco o in un elenco
             * diverso da quello corrente e con stato diverso da uno dei seguenti valori
             * IN_ELENCO_IN_CODA_INDICE_AIP, IN_ELENCO_CON_INDICI_AIP_GENERATI,
             * IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO, IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO,
             * IN_ELENCO_CON_ELENCO_INDICI_AIP_ERR_MARCA, IN_ELENCO_COMPLETATO, versati in data
             * inferiore alla data di creazione minima dei documenti dell'unità doc presenti
             * nell'elenco
             */
            Set<Long> retrieveDocNonInElenco = evHelper.retrieveDocNonInElenco(aud.getIdUnitaDoc(),
                    dataMinimaDocInElenco, idElencoVers);
            if (!retrieveDocNonInElenco.isEmpty()) {
                String idDocList = StringUtils.join(retrieveDocNonInElenco, ',');
                log.info(DESC_CONSUMER
                        + " - Non è possibile completare la verifica dell'UD (con id "
                        + aud.getIdUnitaDoc() + ") in caso di documenti aggiunti [" + idDocList
                        + "], ci sono documenti non in elenco o elenco diverso da quello corrente");
                // throw new ParerUserError("Non è possibile completare la verifica dell'UD (con id
                // " +
                // aud.getIdUnitaDoc() + ") in caso di documenti aggiunti [" + idDocList + "], ci
                // sono documenti non in
                // elenco o elenco diverso da quello corrente");
                ripetiVerifica = true;
            }
        }

        // (c) il sistema determina il progressivo aggiornamento minimo degli aggiornamenti
        // dell'unità doc presenti
        // nell’elenco
        BigDecimal pgMinimoUpdInElenco = evHelper.getPgMinimoUpdInElenco(aud.getIdUnitaDoc(),
                idElencoVers);
        if (pgMinimoUpdInElenco != null && pgMinimoUpdInElenco.intValue() > 0) {
            /*
             * il sistema determina gli aggiornamenti dell'unità doc, non in elenco o in un elenco
             * diverso da quello corrente e con stato diverso da uno dei seguenti valori
             * IN_ELENCO_IN_CODA_INDICE_AIP, IN_ELENCO_CON_INDICI_AIP_GENERATI,
             * IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO, IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO,
             * IN_ELENCO_CON_ELENCO_INDICI_AIP_ERR_MARCA, IN_ELENCO_COMPLETATO; si verifica se tali
             * aggiornamenti hanno progressivo inferiore al progressivo minimo degli aggiornamenti
             * in elenco
             */
            Set<Long> retrieveUpdNonInElenco = evHelper.retrieveUpdNonInElenco(aud.getIdUnitaDoc(),
                    pgMinimoUpdInElenco, idElencoVers,
                    AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_IN_CODA_INDICE_AIP,
                    AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CON_INDICI_AIP_GENERATI,
                    AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO,
                    AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO,
                    AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CON_ELENCO_INDICI_AIP_ERR_MARCA,
                    AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_COMPLETATO);
            if (!retrieveUpdNonInElenco.isEmpty()) {
                String idUpdList = StringUtils.join(retrieveUpdNonInElenco, ',');
                log.info(DESC_CONSUMER
                        + " - Non è possibile completare la verifica dell'UD (con id "
                        + aud.getIdUnitaDoc() + ") in caso di aggiornamenti metadati [" + idUpdList
                        + "], ci sono aggiornamenti non in elenco o elenco diverso da quello corrente");
                // throw new ParerUserError("Non è possibile completare la verifica dell'UD (con id
                // " +
                // aud.getIdUnitaDoc() + ") in caso di aggiornamenti metadati [" + idUpdList + "],
                // ci sono aggiornamenti
                // non in elenco o elenco diverso da quello corrente");
                ripetiVerifica = true;
            }
        }

        if (!ripetiVerifica) {
            // (d) il sistema registra in ARO_INDICE_AIP_UD_DA_ELAB che per l'unità documentaria
            // deve essere generato
            // l'AIP, specificando
            AroIndiceAipUdDaElab aroIndiceAipUdDaElab = evHelper.registraInAroIndiceAipUdDaElab(aud,
                    idElencoVers, systemDate, true);

            /**
             * (e) il sistema determina i componenti dell'unità doc presenti nell'elenco (quelli
             * derivanti da aggiunta doc presenti nell'elenco) uniti ai componenti dell'unità doc
             * versati o aggiunti con stato = IN_ELENCO_IN_CODA_INDICE_AIP o
             * IN_ELENCO_CON_INDICI_AIP_GENERATI o IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO o
             * IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO o IN_ELENCO_CON_ELENCO_INDICI_AIP_ERR_MARCA o
             * IN_ELENCO_COMPLETATO
             */
            Set<ComponenteInElenco> compInElenco = evHelper
                    .retrieveCompInElenco(aud.getIdUnitaDoc(), idElencoVers);

            log.debug(DESC_CONSUMER + " - registro i componenti dell'unita documentaria "
                    + aud.getIdUnitaDoc()
                    + " nel caso in cui ci siano documenti aggiunti e/o aggiornamenti metadati");

            for (ComponenteInElenco componenteInElenco : compInElenco) {
                evHelper.registraInAroCompIndiceAipUdDaElab(componenteInElenco.getIdCompDoc(),
                        aroIndiceAipUdDaElab);
            }

            // (h) il sistema aggiorna i documenti appartenenti all'unità doc presenti nell'elenco
            // assegnando stato
            // relativo all'elenco = IN_ELENCO_IN_CODA_INDICE_AIP
            evHelper.aggiornaStatoDocInElenco(aud.getIdUnitaDoc(),
                    elencoDaElab.getElvElencoVer().getIdElencoVers(),
                    ElencoEnums.DocStatusEnum.IN_ELENCO_IN_CODA_INDICE_AIP.name(), systemDate, null,
                    null);

            /**
             * il sistema determina gli aggiornamenti dell'unità doc presenti nell'elenco uniti agli
             * aggiornamenti dell'unità doc con stato = IN_ELENCO_IN_CODA_INDICE_AIP o
             * IN_ELENCO_CON_INDICI_AIP_GENERATI o IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO o
             * IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO o IN_ELENCO_CON_ELENCO_INDICI_AIP_ERR_MARCA o
             * IN_ELENCO_COMPLETATO
             */
            Set<AggiornamentoInElenco> updInElenco = evHelper.retrieveUpdInElenco(
                    aud.getIdUnitaDoc(), idElencoVers,
                    AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_IN_CODA_INDICE_AIP,
                    AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CON_INDICI_AIP_GENERATI,
                    AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CON_ELENCO_INDICI_AIP_CREATO,
                    AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CON_ELENCO_INDICI_AIP_FIRMATO,
                    AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_CON_ELENCO_INDICI_AIP_ERR_MARCA,
                    AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_COMPLETATO);

            for (AggiornamentoInElenco aggiornamentoInElenco : updInElenco) {
                evHelper.registraInAroUpdUdIndiceAipUdDaElab(
                        aggiornamentoInElenco.getIdUpdUnitaDoc(), aroIndiceAipUdDaElab);
            }

            // il sistema aggiorna gli aggiornamenti unità doc appartenenti all'unità doc presenti
            // nell'elenco
            // assegnando stato relativo all'elenco = IN_ELENCO_IN_CODA_INDICE_AIP
            evHelper.aggiornaStatoUpdInElenco(aud.getIdUnitaDoc(),
                    elencoDaElab.getElvElencoVer().getIdElencoVers(),
                    AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_IN_CODA_INDICE_AIP.name(), systemDate,
                    null, null);
        } else {
            Date lastResetSystemDate = new Date();
            // Recupero l'ultimo valore del numero di reset per i documenti aggiunti appartenenti
            // all'unità doc presenti
            // nell'elenco
            BigDecimal niResetStatoDoc = evHelper.getNiResetStatoDocInElenco(aud.getIdUnitaDoc(),
                    elencoDaElab.getElvElencoVer().getIdElencoVers());
            niResetStatoDoc = niResetStatoDoc.add(BigDecimal.ONE);
            // il sistema aggiorna i documenti appartenenti all'unità doc presenti nell'elenco
            // assegnando stato relativo
            // all'elenco = IN_ELENCO_VALIDATO
            evHelper.aggiornaStatoDocInElenco(aud.getIdUnitaDoc(),
                    elencoDaElab.getElvElencoVer().getIdElencoVers(),
                    ElencoEnums.DocStatusEnum.IN_ELENCO_VALIDATO.name(), null, lastResetSystemDate,
                    niResetStatoDoc);
            // Recupero l'ultimo valore del numero di reset per gli aggiornamenti metadati relativi
            // all'unità doc
            // presenti nell'elenco
            BigDecimal niResetStatoUpd = evHelper.getNiResetStatoUpdInElenco(aud.getIdUnitaDoc(),
                    elencoDaElab.getElvElencoVer().getIdElencoVers());
            niResetStatoUpd = niResetStatoUpd.add(BigDecimal.ONE);
            // il sistema aggiorna gli aggiornamenti unità doc appartenenti all'unità doc presenti
            // nell'elenco
            // assegnando stato relativo all'elenco = IN_ELENCO_VALIDATO
            evHelper.aggiornaStatoUpdInElenco(aud.getIdUnitaDoc(),
                    elencoDaElab.getElvElencoVer().getIdElencoVers(),
                    AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_VALIDATO.name(), null,
                    lastResetSystemDate, niResetStatoUpd);
        }
    }

    /**
     * Caso semplice: non ci sono solo documenti aggiunti e/o aggiornamenti metadati.
     *
     * @param aud    - Ud di cui è stato eseguito il lock
     * @param elenco - Elenco di versamento
     */
    private void aggiornaElencoSeNonCiSonoSoloDocAggiunti_Upd(AroUnitaDoc aud,
            ElvElencoVersDaElab elencoDaElab) {
        Date systemDate = new Date();
        long idElencoVers = elencoDaElab.getElvElencoVer().getIdElencoVers();
        AroIndiceAipUdDaElab aroIndiceAipUdDaElab = evHelper.registraInAroIndiceAipUdDaElab(aud,
                idElencoVers, systemDate, false);

        Set<ComponenteInElenco> retrieveUdInElenco = evHelper
                .retrieveComponentiInElenco(aud.getIdUnitaDoc(), idElencoVers);

        log.debug(DESC_CONSUMER + " - registro i componenti dell'unita documentaria "
                + aud.getIdUnitaDoc()
                + " nel caso in cui non ci siano solo documenti aggiunti e/o aggiornamenti metadati");

        for (ComponenteInElenco componenteInElenco : retrieveUdInElenco) {
            evHelper.registraInAroCompIndiceAipUdDaElab(componenteInElenco.getIdCompDoc(),
                    aroIndiceAipUdDaElab);
        }

        Set<AggiornamentoInElenco> retrieveUpdUdInElenco = evHelper
                .retrieveAggiornamentiInElenco(aud.getIdUnitaDoc(), idElencoVers);

        for (AggiornamentoInElenco aggiornamentoInElenco : retrieveUpdUdInElenco) {
            evHelper.registraInAroUpdUdIndiceAipUdDaElab(aggiornamentoInElenco.getIdUpdUnitaDoc(),
                    aroIndiceAipUdDaElab);
        }

        // (f) il sistema aggiorna l'unità doc assegnando stato relativo all'elenco =
        // IN_ELENCO_IN_CODA_INDICE_AIP
        evHelper.aggiornaStatoUnitaDocInElenco(aud.getIdUnitaDoc(), idElencoVers,
                ElencoEnums.UdDocStatusEnum.IN_ELENCO_IN_CODA_INDICE_AIP.name(), systemDate, null,
                null);
        // (g) il sistema aggiorna i documenti appartenenti all'unità doc presenti nell'elenco
        // assegnando stato relativo
        // all'elenco = IN_ELENCO_IN_CODA_INDICE_AIP
        evHelper.aggiornaStatoDocInElenco(aud.getIdUnitaDoc(), idElencoVers,
                ElencoEnums.DocStatusEnum.IN_ELENCO_IN_CODA_INDICE_AIP.name(), systemDate, null,
                null);
        // (h) il sistema aggiorna gli aggiornamenti metadati per unità doc presenti nell'elenco
        // assegnando stato
        // relativo all'elenco = IN_ELENCO_IN_CODA_INDICE_AIP
        evHelper.aggiornaStatoUpdInElenco(aud.getIdUnitaDoc(), idElencoVers,
                AroUpdUDTiStatoUpdElencoVers.IN_ELENCO_IN_CODA_INDICE_AIP.name(), systemDate, null,
                null);
    }
}
