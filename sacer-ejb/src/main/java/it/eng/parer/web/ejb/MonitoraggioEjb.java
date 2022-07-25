package it.eng.parer.web.ejb;

import it.eng.parer.exception.ParerUserError;
import it.eng.parer.slite.gen.form.MonitoraggioForm;
import it.eng.parer.slite.gen.viewbean.MonVLisUniDocDaAnnulRowBean;
import it.eng.parer.slite.gen.viewbean.MonVLisUniDocDaAnnulTableBean;
import it.eng.parer.viewEntity.MonVLisUniDocDaAnnul;
import it.eng.parer.web.dto.MonitoraggioFiltriListaDocBean;
import it.eng.parer.web.helper.MonitoraggioHelper;
import it.eng.parer.web.util.Transform;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.table.AbstractBaseTable;
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

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "MonitoraggioEjb")
@LocalBean
public class MonitoraggioEjb {

    @Resource
    SessionContext ctx;
    @EJB(mappedName = "java:app/Parer-ejb/MonitoraggioHelper")
    private MonitoraggioHelper monitoraggioHelper;

    public MonitoraggioEjb() {
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
            Set<BigDecimal> idSessioneHS, Set<BigDecimal> idSessioneHSNoRis, AbstractBaseTable tb)
            throws ParerUserError {
        Set<BigDecimal> idSesModificate = new HashSet();
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

        MonVLisUniDocDaAnnulTableBean monTableBean = new MonVLisUniDocDaAnnulTableBean();
        MonitoraggioFiltriListaDocBean filtriListaDoc = new MonitoraggioFiltriListaDocBean();
        filtriListaDoc.setIdAmbiente(filtri.getId_ambiente().parse());
        filtriListaDoc.setIdEnte(filtri.getId_ente().parse());
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

        List<MonVLisUniDocDaAnnul> listaDoc = monitoraggioHelper.getMonVLisUniDocDaAnnulViewBean(idUtente,
                filtriListaDoc, maxResult);
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
            if (filtri.getId_ente().parse() == null) {
                row.setNmStrut((row.getNmEnte() != null ? row.getNmEnte() : "") + ", "
                        + (row.getNmStrut() != null ? row.getNmStrut() : ""));
            }
            if (!row.getTiDoc().equals("PRINCIPALE")) {
                row.setTiDoc(row.getTiDoc() + " " + row.getPgDoc());
            }
        }

        return monTableBean;
    }
}
