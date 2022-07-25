package it.eng.parer.fascicoli.ejb;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.DecModelloXsdFascicolo;
import it.eng.parer.entity.DecUsoModelloXsdFasc;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.fascicoli.helper.ModelliFascicoliHelper;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.slite.gen.form.ModelliFascicoliForm.FiltriModelliXsdTipiFascicolo;
import it.eng.parer.slite.gen.tablebean.DecModelloXsdFascicoloRowBean;
import it.eng.parer.slite.gen.tablebean.DecModelloXsdFascicoloTableBean;
import it.eng.parer.slite.gen.tablebean.DecUsoModelloXsdFascRowBean;
import it.eng.parer.slite.gen.tablebean.DecUsoModelloXsdFascTableBean;
import it.eng.parer.web.util.Transform;
import it.eng.spagoCore.error.EMFError;

/**
 *
 * @author DiLorenzo_F
 */
@Stateless
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class ModelliFascicoliEjb {

    private static final Logger logger = LoggerFactory.getLogger(ModelliFascicoliEjb.class);

    @EJB
    private ModelliFascicoliHelper helper;
    @EJB
    private SacerLogEjb sacerLogEjb;

    /**
     * Ritorna il tableBean con la lista dei modelli xsd abilitati per l'ambiente
     *
     * @param filtriModelliXsdTipiFasc
     *            filtri modelli xsd tipo fascicolo
     * @param idAmbientiToFind
     *            id ambiente da ricercare
     * @param tiUsoModelloXsd
     *            tipo modello xsd in uso
     * @param filterValid
     *            true per prendere i record attivi attualmente
     * 
     * @return entity DecModelloXsdFascicoloTableBean
     * 
     * @throws EMFError
     *             errore generico
     */
    public DecModelloXsdFascicoloTableBean getDecModelloXsdTipoFascicoliAbilitatiAmbienteTableBean(
            FiltriModelliXsdTipiFascicolo filtriModelliXsdTipiFasc, List<BigDecimal> idAmbientiToFind,
            String tiUsoModelloXsd, boolean filterValid) throws EMFError {
        DecModelloXsdFascicoloTableBean table = new DecModelloXsdFascicoloTableBean();
        List<DecModelloXsdFascicolo> allModelli = helper.retrieveDecModelloXsdTipoFascicolo(filtriModelliXsdTipiFasc,
                idAmbientiToFind, tiUsoModelloXsd, filterValid);
        if (!allModelli.isEmpty()) {
            try {

                Set<DecModelloXsdFascicolo> modelli = new HashSet<>();
                modelli.addAll(allModelli);
                for (DecModelloXsdFascicolo modello : modelli) {
                    DecModelloXsdFascicoloRowBean row = (DecModelloXsdFascicoloRowBean) Transform
                            .entity2RowBean(modello);
                    row.setString("nm_ambiente", modello.getOrgAmbiente().getNmAmbiente());
                    if (modello.getDtIstituz().before(new Date()) && modello.getDtSoppres().after(new Date())) {
                        row.setString("fl_attivo", "1");
                    } else {
                        row.setString("fl_attivo", "0");
                    }
                    table.add(row);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore durante il recupero della lista di modelli xsd abilitati per l'ambiente "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
            }
        }
        return table;
    }

    /**
     * Ritorna il tableBean contenente la lista di ambienti associati al modello dato in input
     *
     * @param idModelloXsdFascicolo
     *            id modello
     * 
     * @return il tableBean della lista
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public DecUsoModelloXsdFascTableBean getDecUsoModelloXsdFascTableBean(BigDecimal idModelloXsdFascicolo)
            throws ParerUserError {
        DecUsoModelloXsdFascTableBean table = new DecUsoModelloXsdFascTableBean();
        List<DecUsoModelloXsdFasc> list = helper.retrieveDecUsoModelloXsdFasc(idModelloXsdFascicolo);
        if (list != null && !list.isEmpty()) {
            try {
                for (DecUsoModelloXsdFasc uso : list) {
                    DecUsoModelloXsdFascRowBean row = (DecUsoModelloXsdFascRowBean) Transform.entity2RowBean(uso);
                    table.add(row);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                String msg = "Errore durante il recupero della lista di elementi dei modelli xsd in uso di tipo fascicolo delle tipologie di fascicolo "
                        + ExceptionUtils.getRootCauseMessage(ex);
                logger.error(msg, ex);
                throw new ParerUserError(msg);
            }
        }
        return table;
    }

    /**
     * Ritorna il rowBean del modello xsd richiesto in input tramite id
     *
     * @param idModelloXsdFascicolo
     *            id modello xsd fascicolo
     * 
     * @return il rowBean contenente i dati del modello xsd
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public DecModelloXsdFascicoloRowBean getDecModelloXsdFascicoloRowBean(BigDecimal idModelloXsdFascicolo)
            throws ParerUserError {
        DecModelloXsdFascicolo modello = helper.findById(DecModelloXsdFascicolo.class, idModelloXsdFascicolo);
        DecModelloXsdFascicoloRowBean row = null;
        try {
            row = (DecModelloXsdFascicoloRowBean) Transform.entity2RowBean(modello);
            row.setString("nm_ambiente", modello.getOrgAmbiente().getNmAmbiente());
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException ex) {
            String msg = "Errore durante il recupero del modello xsd del fascicolo "
                    + ExceptionUtils.getRootCauseMessage(ex);
            logger.error(msg, ex);
            throw new ParerUserError(msg);
        }
        return row;
    }

    /**
     * Ritorna il rowBean del modello xsd richiesto in input tramite chiave unique
     * 
     * @param idAmbiente
     *            id ambiente
     * @param tiModelloXsd
     *            tipo modello xsd
     * @param tiUsoModelloXsd
     *            tipo modello in uso xsd
     * @param cdXsd
     *            codice xsd
     * 
     * @return il rowBean contenente i dati del modello
     * 
     * @throws ParerUserError
     *             errore generico
     */
    public DecModelloXsdFascicoloRowBean getDecModelloXsdFascicoloRowBean(BigDecimal idAmbiente, String tiModelloXsd,
            String tiUsoModelloXsd, String cdXsd) throws ParerUserError {
        DecModelloXsdFascicolo modello = helper.getDecModelloXsdFascicolo(idAmbiente, tiModelloXsd, tiUsoModelloXsd,
                cdXsd);
        DecModelloXsdFascicoloRowBean row = null;
        if (modello != null) {
            try {
                row = (DecModelloXsdFascicoloRowBean) Transform.entity2RowBean(modello);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                String msg = "Errore durante il recupero del modello di tipo fascicolo delle tipologie di fascicolo "
                        + ExceptionUtils.getRootCauseMessage(ex);
                logger.error(msg, ex);
                throw new ParerUserError(msg);
            }
        }
        return row;
    }

    /**
     * Ricava i modelli xsd attivi e l'ambiente di appartenenza della struttura
     *
     * @param idAmbiente
     *            id ambiente
     * @param tiModelloXsd
     *            tipo modello xsd
     * 
     * @return lista oggetti di tipo {@link DecModelloXsdFascicolo}
     */
    public List<DecModelloXsdFascicolo> checkModelliXsdAttiviInUse(BigDecimal idAmbiente, String tiModelloXsd) {
        return helper.retrieveDecModelloXsdFascicolo(idAmbiente, tiModelloXsd, true);
    }

    public boolean isModelloXsdInUseInTipologieFascicolo(BigDecimal idModelloXsdFascicolo) {
        return helper.existDecUsoModelloXsdFasc(idModelloXsdFascicolo);
    }

    /**
     * Metodo di insert di un modello xsd di tipo fascicolo
     *
     * @param param
     *            paramentri per il logging
     * @param modelloXsdFascicoloRowBean
     *            decodifica xsd fascicolo
     * 
     * @return id del nuovo modello xsd
     * 
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long saveModelloXsdFascicolo(LogParam param, DecModelloXsdFascicoloRowBean modelloXsdFascicoloRowBean)
            throws ParerUserError {
        logger.info("Eseguo il salvataggio del modello xsd di tipo fascicolo");
        Long idModelloXsdFascicolo = null;
        try {
            DecModelloXsdFascicolo modelloXsdFascicolo = (DecModelloXsdFascicolo) Transform
                    .rowBean2Entity(modelloXsdFascicoloRowBean);
            helper.insertEntity(modelloXsdFascicolo, false);

            logger.info("Salvataggio del modello xsd di tipo fascicolo completato");
            idModelloXsdFascicolo = modelloXsdFascicolo.getIdModelloXsdFascicolo();
            // Inserito per loggare la foto del Modello xsd di tipo fascicolo
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_MODELLO_TIPO_FASCICOLO,
                    new BigDecimal(idModelloXsdFascicolo), param.getNomePagina());
        } catch (Exception ex) {
            logger.error("Errore imprevisto durante il salvataggio del modello xsd di tipologia fascicolo : "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
            throw new ParerUserError(
                    "Eccezione imprevista durante il salvataggio del modello xsd di tipologia fascicolo");
        }
        return idModelloXsdFascicolo;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateModelloXsdFascicolo(LogParam param, BigDecimal idModelloXsdFascicolo,
            DecModelloXsdFascicoloRowBean modelloXsdFascicoloRowBean) throws ParerUserError {
        updateDecModelloXsdFascicolo(idModelloXsdFascicolo, modelloXsdFascicoloRowBean);
        // Parte di logging
        DecModelloXsdFascicolo modelloXsdFasc = (modelloXsdFascicoloRowBean.getIdModelloXsdFascicolo() != null
                ? helper.findById(DecModelloXsdFascicolo.class, modelloXsdFascicoloRowBean.getIdModelloXsdFascicolo())
                : helper.findById(DecModelloXsdFascicolo.class, idModelloXsdFascicolo));

        if (modelloXsdFasc != null) {
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_MODELLO_TIPO_FASCICOLO,
                    new BigDecimal(modelloXsdFasc.getIdModelloXsdFascicolo()), param.getNomePagina());
        }
    }

    private void updateDecModelloXsdFascicolo(BigDecimal idModelloXsdFascicolo,
            DecModelloXsdFascicoloRowBean modelloXsdFascicoloRowBean) {
        DecModelloXsdFascicolo modelloXsdFasc = helper.findById(DecModelloXsdFascicolo.class, idModelloXsdFascicolo);
        if (StringUtils.isNotBlank(modelloXsdFascicoloRowBean.getBlXsd())) {
            modelloXsdFasc.setBlXsd(modelloXsdFascicoloRowBean.getBlXsd());
        }
        modelloXsdFasc.setDtSoppres(modelloXsdFascicoloRowBean.getDtSoppres());
        modelloXsdFasc.setDsXsd(modelloXsdFascicoloRowBean.getDsXsd());

        helper.getEntityManager().flush();
    }

    /**
     * Esegue l'eliminazione della associazione dell'ambiente al modello xsd dato in input
     *
     * @param param
     *            parametri per il logging
     * @param idModelloXsdFascicolo
     *            id modello xsd fascicolo
     * 
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteDecModelloXsdFascicolo(LogParam param, BigDecimal idModelloXsdFascicolo) throws ParerUserError {
        logger.debug("Eseguo l'eliminazione di un modello xsd");
        try {
            DecModelloXsdFascicolo modello = helper.findById(DecModelloXsdFascicolo.class, idModelloXsdFascicolo);
            // Inserito per loggare la foto del Modello xsd di tipo fascicolo
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(), param.getNomeUtente(),
                    param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_MODELLO_TIPO_FASCICOLO, idModelloXsdFascicolo,
                    param.getNomePagina());
            helper.removeEntity(modello, true);
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista nell'eliminazione del modello xsd ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            logger.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }
    }

}
