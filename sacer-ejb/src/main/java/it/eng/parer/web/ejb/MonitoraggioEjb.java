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

package it.eng.parer.web.ejb;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.exception.ParerUserError;
import it.eng.parer.objectstorage.ejb.ObjectStorageService;
import it.eng.parer.slite.gen.form.MonitoraggioForm;
import it.eng.parer.slite.gen.viewbean.MonVLisUniDocDaAnnulRowBean;
import it.eng.parer.slite.gen.viewbean.MonVLisUniDocDaAnnulTableBean;
import it.eng.parer.viewEntity.MonVLisUniDocDaAnnul;
import it.eng.parer.web.dto.MonitoraggioFiltriListaDocBean;
import it.eng.parer.web.helper.MonitoraggioHelper;
import it.eng.parer.web.util.Transform;
import it.eng.parer.ws.recupero.ejb.oracleBlb.RecBlbOracle;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.table.AbstractBaseTable;

/**
 *
 * @author Gilioli_P
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "MonitoraggioEjb")
@LocalBean
public class MonitoraggioEjb {

    @Resource
    SessionContext ctx;
    @EJB(mappedName = "java:app/Parer-ejb/MonitoraggioHelper")
    private MonitoraggioHelper monitoraggioHelper;

    @EJB
    private ObjectStorageService objectStorageService;

    @EJB
    private RecBlbOracle recBlbOracle;

    public MonitoraggioEjb() {
        //
    }

    private static final Logger log = LoggerFactory.getLogger(MonitoraggioEjb.class);

    /**
     * Dato un tablebean contenente una list di sessioni, ne modifica i flag "Verificato" e "NonRisolubile"
     *
     * @param verificatiNonRisolubiliModificati
     *            lista verificati
     * @param idSessioneHS
     *            id sessione HS
     * @param idSessioneHSNoRis
     *            id sessione HR non risolta
     * @param tb
     *            bean AbstractBaseTable
     *
     * @return idSesModificate, il set di id delle sessioni modificate (flag "Verificato" o "Non risolubile"
     *
     * @throws ParerUserError
     *             errore generico
     */
    public Set<BigDecimal> aggiornaSessioni(Set<Integer> verificatiNonRisolubiliModificati,
            Set<BigDecimal> idSessioneHS, Set<BigDecimal> idSessioneHSNoRis, AbstractBaseTable<?> tb)
            throws ParerUserError {
        Set<BigDecimal> idSesModificate = new HashSet<>();
        try {
            /* Scorro i flag (Verificato o Non risolubile) modificati */
            for (Integer index : verificatiNonRisolubiliModificati) {
                BigDecimal idSesErr = tb.getRow(index).getBigDecimal("id_sessione_vers");
                idSesModificate.add(idSesErr);

                // Se ho impostato a "1" il flag "verificato"
                if (idSessioneHS.contains(idSesErr)) {
                    // Se ho impostato a "1" il flag "non risolubile"
                    if (idSessioneHSNoRis.contains(idSesErr)) {
                        monitoraggioHelper.saveFlVerificatiNonRisolubili(idSesErr, "1", "1");
                    } else {
                        monitoraggioHelper.saveFlVerificatiNonRisolubili(idSesErr, "1", "0");
                    }
                } else {
                    // Metti il flag flNonRisolubile a "null" visto che è l'unica opzione consentita
                    monitoraggioHelper.saveFlVerificatiNonRisolubili(idSesErr, "0", null);
                }
            }
        } catch (Exception e) {
            /*
             * Il rollback va settato visto che sono in modalità cmt in modo tale da gestire le eccezioni non di tipo
             * RuntimeException (che vengono gestite automaticamente)
             */
            ctx.setRollbackOnly();
            log.error(e.getMessage());
            throw new ParerUserError(
                    "Attenzione: l'operazione non è stata eseguita perchè si è verificato un errore a runtime durante il salvataggio dei flag");
        }
        return idSesModificate;
    }

    public MonVLisUniDocDaAnnulTableBean getMonVLisUniDocDaAnnul(long idUtente,
            MonitoraggioForm.FiltriDocumentiAnnullati filtri, int maxResult) throws EMFError {

        final BigDecimal idEnte = filtri.getId_ente().parse();
        MonitoraggioFiltriListaDocBean filtriListaDoc = new MonitoraggioFiltriListaDocBean();
        filtriListaDoc.setIdAmbiente(filtri.getId_ambiente().parse());
        filtriListaDoc.setIdEnte(idEnte);
        filtriListaDoc.setIdStrut(filtri.getId_strut().parse());
        filtriListaDoc.setIdTipoUnitaDoc(filtri.getId_tipo_unita_doc().parse());
        filtriListaDoc.setCdRegistroKeyUnitaDoc(filtri.getCd_registro_key_unita_doc().parse());
        filtriListaDoc.setAaKeyUnitaDoc(filtri.getAa_key_unita_doc().parse());
        filtriListaDoc.setAaKeyUnitaDocDa(filtri.getAa_key_unita_doc_da().parse());
        filtriListaDoc.setAaKeyUnitaDocA(filtri.getAa_key_unita_doc_a().parse());
        filtriListaDoc.setCdKeyUnitaDoc(filtri.getCd_key_unita_doc().parse());
        filtriListaDoc.setCdKeyUnitaDocDa(filtri.getCd_key_unita_doc_da().parse());
        filtriListaDoc.setCdKeyUnitaDocA(filtri.getCd_key_unita_doc_a().parse());
        filtriListaDoc.setGiornoVersDaValidato(filtri.getGiorno_vers_da_validato().parse());
        filtriListaDoc.setGiornoVersAValidato(filtri.getGiorno_vers_a_validato().parse());
        filtriListaDoc.setIdTipoDoc(filtri.getId_tipo_doc().parse());
        filtriListaDoc.setStatoDoc(filtri.getTi_stato_annul().parse());
        filtriListaDoc.setIdUserIam(new BigDecimal(idUtente));

        return monitoraggioHelper.getMonVLisUniDocDaAnnulViewBean(idUtente, filtriListaDoc, maxResult,
                list -> getMonVLisUniDocDaAnnulTableBeanFrom(list, idEnte));
    }

    private MonVLisUniDocDaAnnulTableBean getMonVLisUniDocDaAnnulTableBeanFrom(List<MonVLisUniDocDaAnnul> listaDoc,
            BigDecimal idEnte) {
        MonVLisUniDocDaAnnulTableBean monTableBean = new MonVLisUniDocDaAnnulTableBean();
        try {
            if (listaDoc != null && !listaDoc.isEmpty()) {
                monTableBean = (MonVLisUniDocDaAnnulTableBean) Transform.entities2TableBean(listaDoc);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        /*
         * "Rielaboro" il campo Struttura per presentarlo a video eventualmente valorizzato anche con ambiente ed ente
         */
        for (MonVLisUniDocDaAnnulRowBean row : monTableBean) {
            if (idEnte == null) {
                row.setNmStrut((row.getNmEnte() != null ? row.getNmEnte() : "") + ", "
                        + (row.getNmStrut() != null ? row.getNmStrut() : ""));
            }
            if (!row.getTiDoc().equals("PRINCIPALE")) {
                row.setTiDoc(row.getTiDoc() + " " + row.getPgDoc());
            }
        }
        return monTableBean;
    }

    /**
     * Conta il numero di componenti con errore di versamento. Se la configurazione del backend di staging è object
     * storage ottengo il valore da lì. Se, però, i versamenti falliti sono ancora su blob li conto dal blob. Non può
     * accadere che siano in entrambi i luoghi.
     *
     * @param idFileSessioneKo
     *            id file sesssione
     *
     * @return numero di componenti con errore di versamento
     */
    public long contaComponentiErroreVersamento(long idFileSessioneKo) {

        long nComponenti = 0L;
        boolean isVersamentoFallitoOnOs = objectStorageService.isComponenteFallitoOnOs(idFileSessioneKo);
        if (isVersamentoFallitoOnOs) {
            nComponenti += 1;
        }

        nComponenti += recBlbOracle.contaBlobErroriVers(idFileSessioneKo);
        return nComponenti;

    }

    /**
     * Salva lo stream del componente versato in errore. Il componente può essere presente sul bucket di staging (se il
     * backend configurato è object storage) oppure sul database. <strong>Nota bene:</strong> Essendo la configurazione
     * del backend di staging a livello di applicazione, il componente si può trovare <em>ancora</em> sul DB nonostante
     * il parametro dica il contrario.
     *
     * @param idFileSessioneKo
     *            id file sessione
     * @param out
     *            stream su cui viene scritto il componente.
     *
     * @return true OutputStream aggiornato con la copia dell'oggetto / false OutputStream non aggiornato, file non
     *         restituito per errore
     */
    public boolean salvaStreamComponenteDaErroreVersamento(long idFileSessioneKo, OutputStream out) {

        boolean isVersamentoFallitoOnOs = objectStorageService.isComponenteFallitoOnOs(idFileSessioneKo);
        //
        if (isVersamentoFallitoOnOs) {
            //
            return objectStorageService.getObjectComponenteInStaging(idFileSessioneKo, out);
        } else { // file è su DB
            return recBlbOracle.recuperaBlobCompSuStream(idFileSessioneKo, out, RecBlbOracle.TabellaBlob.ERRORI_VERS,
                    null);
        }
    }

}
