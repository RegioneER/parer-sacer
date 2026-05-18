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
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import it.eng.parer.xml.xsd.helper.XsdRepositoryHelper;

/**
 *
 * @author DiLorenzo_F
 */
@Stateless
@LocalBean
@Interceptors({
        it.eng.parer.aop.TransactionInterceptor.class })
public class ModelliFascicoliEjb {

    private static final Logger logger = LoggerFactory.getLogger(ModelliFascicoliEjb.class);

    @EJB
    private ModelliFascicoliHelper helper;
    @EJB
    private SacerLogEjb sacerLogEjb;
    @EJB
    private XsdRepositoryHelper xsdRepositoryHelper;

    /**
     * Ritorna il tableBean con la lista dei modelli xsd abilitati per l'ambiente
     *
     * @param filtriModelliXsdTipiFasc filtri modelli xsd tipo fascicolo
     * @param idAmbientiToFind         id ambiente da ricercare
     * @param tiUsoModelloXsd          tipo modello xsd in uso
     * @param filterValid              true per prendere i record attivi attualmente
     *
     * @return entity DecModelloXsdFascicoloTableBean
     *
     * @throws EMFError errore generico
     */
    public DecModelloXsdFascicoloTableBean getDecModelloXsdTipoFascicoliAbilitatiAmbienteTableBean(
            FiltriModelliXsdTipiFascicolo filtriModelliXsdTipiFasc,
            List<BigDecimal> idAmbientiToFind, String tiUsoModelloXsd, boolean filterValid)
            throws EMFError {
        DecModelloXsdFascicoloTableBean table = new DecModelloXsdFascicoloTableBean();
        List<DecModelloXsdFascicolo> allModelli = helper.retrieveDecModelloXsdTipoFascicolo(
                filtriModelliXsdTipiFasc, idAmbientiToFind, tiUsoModelloXsd, filterValid);
        if (!allModelli.isEmpty()) {
            try {

                Set<DecModelloXsdFascicolo> modelli = new HashSet<>();
                modelli.addAll(allModelli);
                for (DecModelloXsdFascicolo modello : modelli) {
                    DecModelloXsdFascicoloRowBean row = (DecModelloXsdFascicoloRowBean) Transform
                            .entity2RowBean(modello);
                    row.setString("nm_ambiente", modello.getOrgAmbiente().getNmAmbiente());
                    if (modello.getDtIstituz().before(new Date())
                            && modello.getDtSoppres().after(new Date())) {
                        row.setString("fl_attivo", "1");
                    } else {
                        row.setString("fl_attivo", "0");
                    }
                    table.add(row);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                    | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException ex) {
                logger.error(
                        "Errore durante il recupero della lista di modelli xsd abilitati per l'ambiente "
                                + ExceptionUtils.getRootCauseMessage(ex),
                        ex);
            }
        }
        return table;
    }

    /**
     * Ritorna il tableBean contenente la lista di ambienti associati al modello dato in input
     *
     * @param idModelloXsdFascicolo id modello
     *
     * @return il tableBean della lista
     *
     * @throws ParerUserError errore generico
     */
    public DecUsoModelloXsdFascTableBean getDecUsoModelloXsdFascTableBean(
            BigDecimal idModelloXsdFascicolo) throws ParerUserError {
        DecUsoModelloXsdFascTableBean table = new DecUsoModelloXsdFascTableBean();
        List<DecUsoModelloXsdFasc> list = helper
                .retrieveDecUsoModelloXsdFasc(idModelloXsdFascicolo);
        if (list != null && !list.isEmpty()) {
            try {
                for (DecUsoModelloXsdFasc uso : list) {
                    DecUsoModelloXsdFascRowBean row = (DecUsoModelloXsdFascRowBean) Transform
                            .entity2RowBean(uso);
                    table.add(row);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                    | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException ex) {
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
     * @param idModelloXsdFascicolo id modello xsd fascicolo
     *
     * @return il rowBean contenente i dati del modello xsd
     *
     * @throws ParerUserError errore generico
     */
    public DecModelloXsdFascicoloRowBean getDecModelloXsdFascicoloRowBean(
            BigDecimal idModelloXsdFascicolo) throws ParerUserError {
        DecModelloXsdFascicolo modello = helper.findById(DecModelloXsdFascicolo.class,
                idModelloXsdFascicolo);
        DecModelloXsdFascicoloRowBean row = null;
        try {
            row = (DecModelloXsdFascicoloRowBean) Transform.entity2RowBean(modello);
            row.setString("nm_ambiente", modello.getOrgAmbiente().getNmAmbiente());
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException ex) {
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
     * @param idAmbiente      id ambiente
     * @param tiModelloXsd    tipo modello xsd
     * @param tiUsoModelloXsd tipo modello in uso xsd
     * @param cdXsd           codice xsd
     *
     * @return il rowBean contenente i dati del modello
     *
     * @throws ParerUserError errore generico
     */
    public DecModelloXsdFascicoloRowBean getDecModelloXsdFascicoloRowBean(BigDecimal idAmbiente,
            String tiModelloXsd, String tiUsoModelloXsd, String cdXsd) throws ParerUserError {
        DecModelloXsdFascicolo modello = helper.getDecModelloXsdFascicolo(idAmbiente, tiModelloXsd,
                tiUsoModelloXsd, cdXsd);
        DecModelloXsdFascicoloRowBean row = null;
        if (modello != null) {
            try {
                row = (DecModelloXsdFascicoloRowBean) Transform.entity2RowBean(modello);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                    | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException ex) {
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
     * @param idAmbiente   id ambiente
     * @param tiModelloXsd tipo modello xsd
     *
     * @return lista oggetti di tipo {@link DecModelloXsdFascicolo}
     */
    public List<DecModelloXsdFascicolo> checkModelliXsdAttiviInUse(BigDecimal idAmbiente,
            String tiModelloXsd) {
        return helper.retrieveDecModelloXsdFascicolo(idAmbiente, tiModelloXsd, true);
    }

    public boolean isModelloXsdInUseInTipologieFascicolo(BigDecimal idModelloXsdFascicolo) {
        return helper.existDecUsoModelloXsdFasc(idModelloXsdFascicolo);
    }

    /**
     * Metodo di insert di un modello xsd di tipo fascicolo
     *
     * @param param                      paramentri per il logging
     * @param modelloXsdFascicoloRowBean decodifica xsd fascicolo
     *
     * @return id del nuovo modello xsd
     *
     * @throws ParerUserError errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long saveModelloXsdFascicolo(LogParam param,
            DecModelloXsdFascicoloRowBean modelloXsdFascicoloRowBean) throws ParerUserError {
        logger.info("Eseguo il salvataggio del modello xsd di tipo fascicolo");
        Long idModelloXsdFascicolo = null;
        try {
            DecModelloXsdFascicolo modelloXsdFascicolo;

            // TiModelloXsd è sempre impostato dall'action (incluso RICHIAMABILE)
            // Usa la conversione standard
            modelloXsdFascicolo = (DecModelloXsdFascicolo) Transform
                    .rowBean2Entity(modelloXsdFascicoloRowBean);

            helper.insertEntity(modelloXsdFascicolo, true);

            logger.info("Salvataggio del modello xsd di tipo fascicolo completato");
            idModelloXsdFascicolo = modelloXsdFascicolo.getIdModelloXsdFascicolo();
            // Inserito per loggare la foto del Modello xsd di tipo fascicolo
            /*
             * TODO: MEV#26576, verificare sacerLogEjb.log(param.getTransactionLogContext(),
             * param.getNomeApplicazione(), param.getNomeUtente(), param.getNomeAzione(),
             * SacerLogConstants.TIPO_OGGETTO_MODELLO_TIPO_FASCICOLO, new
             * BigDecimal(idModelloXsdFascicolo), param.getNomePagina());
             */
        } catch (Exception ex) {
            logger.error(
                    "Errore imprevisto durante il salvataggio del modello xsd di tipologia fascicolo : "
                            + ExceptionUtils.getRootCauseMessage(ex),
                    ex);
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
        DecModelloXsdFascicolo modelloXsdFasc = (modelloXsdFascicoloRowBean
                .getIdModelloXsdFascicolo() != null
                        ? helper.findById(DecModelloXsdFascicolo.class,
                                modelloXsdFascicoloRowBean.getIdModelloXsdFascicolo())
                        : helper.findById(DecModelloXsdFascicolo.class, idModelloXsdFascicolo));

        if (modelloXsdFasc != null) {
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(),
                    param.getNomeUtente(), param.getNomeAzione(),
                    SacerLogConstants.TIPO_OGGETTO_MODELLO_TIPO_FASCICOLO,
                    new BigDecimal(modelloXsdFasc.getIdModelloXsdFascicolo()),
                    param.getNomePagina());
        }
    }

    private void updateDecModelloXsdFascicolo(BigDecimal idModelloXsdFascicolo,
            DecModelloXsdFascicoloRowBean modelloXsdFascicoloRowBean) {
        DecModelloXsdFascicolo modelloXsdFasc = helper.findById(DecModelloXsdFascicolo.class,
                idModelloXsdFascicolo);
        if (StringUtils.isNotBlank(modelloXsdFascicoloRowBean.getBlXsd())) {
            modelloXsdFasc.setBlXsd(modelloXsdFascicoloRowBean.getBlXsd());
        }
        modelloXsdFasc.setDtSoppres(modelloXsdFascicoloRowBean.getDtSoppres());
        modelloXsdFasc.setDsXsd(modelloXsdFascicoloRowBean.getDsXsd());
        modelloXsdFasc.setFlDefault(modelloXsdFascicoloRowBean.getFlDefault());

        // Propaga la data di soppressione alle dipendenze (DecModelloXsdFascRif)
        List<it.eng.parer.entity.DecModelloXsdFascRif> dipendenze = helper
                .retrieveDipendenzaXsd(idModelloXsdFascicolo);
        if (!dipendenze.isEmpty()) {
            for (it.eng.parer.entity.DecModelloXsdFascRif rif : dipendenze) {
                rif.setDtSoppres(modelloXsdFascicoloRowBean.getDtSoppres());
            }
            logger.info("Propagata data di soppressione a {} dipendenze del modello ID {}",
                    dipendenze.size(), idModelloXsdFascicolo);
        }
    }

    /**
     * Esegue l'eliminazione della associazione dell'ambiente al modello xsd dato in input
     *
     * @param param                 parametri per il logging
     * @param idModelloXsdFascicolo id modello xsd fascicolo
     *
     * @throws ParerUserError errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteDecModelloXsdFascicolo(LogParam param, BigDecimal idModelloXsdFascicolo)
            throws ParerUserError {
        logger.debug("Eseguo l'eliminazione di un modello xsd");
        try {
            DecModelloXsdFascicolo modello = helper.findById(DecModelloXsdFascicolo.class,
                    idModelloXsdFascicolo);
            // Inserito per loggare la foto del Modello xsd di tipo fascicolo
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(),
                    param.getNomeUtente(), param.getNomeAzione(),
                    SacerLogConstants.TIPO_OGGETTO_MODELLO_TIPO_FASCICOLO, idModelloXsdFascicolo,
                    param.getNomePagina());
            helper.removeEntity(modello, true);
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista nell'eliminazione del modello xsd ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            logger.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }
    }

    /**
     * Verifica se esiste già un modello standard
     *
     * @param idAmbiente      id ambiente
     * @param idModelloXsd    id modello
     * @param tiModelloXsd    tipo modello
     * @param tiUsoModelloXsd tipo uso modello
     *
     * @return true se esiste modello standard previsto per l'ambiente
     */
    public boolean existAnotherDecModelloXsdStd(BigDecimal idAmbiente, BigDecimal idModelloXsd,
            String tiModelloXsd, String tiUsoModelloXsd) {
        List<DecModelloXsdFascicolo> result = helper.retrieveDecModelliXsd4AmbAndTiModelloDefXsd(
                idAmbiente, tiModelloXsd, tiUsoModelloXsd, CostantiDB.Flag.TRUE, false);
        return result.stream()
                .filter(m -> idModelloXsd != null
                        && m.getIdModelloXsdFascicolo() != idModelloXsd.longValue()
                        || idModelloXsd == null)
                .count() != 0;
    }

    /**
     * Restituisce la lista delle dipendenze XSD per un modello padre
     *
     * @param idPadre id del modello padre
     *
     * @return tablebean con le dipendenze
     *
     * @throws ParerUserError errore generico
     */
    public it.eng.parer.slite.gen.tablebean.DecModelloXsdFascRifTableBean getDipendenzaXsdTableBean(
            BigDecimal idPadre) throws ParerUserError {
        it.eng.parer.slite.gen.tablebean.DecModelloXsdFascRifTableBean table = new it.eng.parer.slite.gen.tablebean.DecModelloXsdFascRifTableBean();
        List<it.eng.parer.entity.DecModelloXsdFascRif> list = helper
                .retrieveDipendenzaXsdAttive(idPadre);
        if (list != null && !list.isEmpty()) {
            try {
                for (it.eng.parer.entity.DecModelloXsdFascRif rif : list) {
                    it.eng.parer.slite.gen.tablebean.DecModelloXsdFascRifRowBean row = (it.eng.parer.slite.gen.tablebean.DecModelloXsdFascRifRowBean) Transform
                            .entity2RowBean(rif);
                    // Aggiungi il codice XSD del target per visualizzazione
                    if (rif.getDecModelloXsdFascicoloTarget() != null) {
                        row.setCdXsdTarget(rif.getDecModelloXsdFascicoloTarget().getCdXsd());
                    }
                    table.add(row);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                    | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException ex) {
                String msg = "Errore durante il recupero della lista di dipendenze XSD "
                        + ExceptionUtils.getRootCauseMessage(ex);
                logger.error(msg, ex);
                throw new ParerUserError(msg);
            }
        }
        return table;
    }

    /**
     * Restituisce la lista dei modelli XSD richiamabili (ti_modello_xsd='RICHIAMABILE')
     *
     * @return tablebean con i modelli richiamabili
     *
     * @throws ParerUserError errore generico
     */
    public it.eng.parer.slite.gen.tablebean.DecModelloXsdFascicoloTableBean getModelliRichiamabili()
            throws ParerUserError {
        it.eng.parer.slite.gen.tablebean.DecModelloXsdFascicoloTableBean table = new it.eng.parer.slite.gen.tablebean.DecModelloXsdFascicoloTableBean();
        List<DecModelloXsdFascicolo> list = helper.retrieveModelliRichiamabili();
        if (list != null && !list.isEmpty()) {
            try {
                for (DecModelloXsdFascicolo modello : list) {
                    DecModelloXsdFascicoloRowBean row = (DecModelloXsdFascicoloRowBean) Transform
                            .entity2RowBean(modello);
                    table.add(row);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                    | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException ex) {
                String msg = "Errore durante il recupero della lista di modelli richiamabili "
                        + ExceptionUtils.getRootCauseMessage(ex);
                logger.error(msg, ex);
                throw new ParerUserError(msg);
            }
        }
        return table;
    }

    /**
     * Salva le dipendenze XSD per un modello padre
     *
     * @param idPadre    id del modello padre
     * @param dipendenze tablebean con le dipendenze da salvare
     *
     * @throws ParerUserError errore generico
     */

    /**
     * Elimina tutte le dipendenze XSD di un modello padre (usato quando si sostituisce l'XSD).
     *
     * @param idPadre id del modello XSD padre
     * @throws ParerUserError errore nel salvataggio
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteAllDipendenze(BigDecimal idPadre) throws ParerUserError {
        try {
            List<it.eng.parer.entity.DecModelloXsdFascRif> existingDeps = helper
                    .retrieveDipendenzaXsd(idPadre);
            for (it.eng.parer.entity.DecModelloXsdFascRif dep : existingDeps) {
                helper.removeEntity(dep, true);
            }
        } catch (Exception ex) {
            String msg = "Errore durante l'eliminazione delle dipendenze XSD: "
                    + ExceptionUtils.getRootCauseMessage(ex);
            logger.error(msg, ex);
            throw new ParerUserError(msg);
        }
    }

    /**
     * Elimina una singola dipendenza XSD per id.
     *
     * @param idDipendenza id della dipendenza da eliminare
     * @throws ParerUserError errore nel salvataggio
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void deleteDipendenza(BigDecimal idDipendenza) throws ParerUserError {
        try {
            it.eng.parer.entity.DecModelloXsdFascRif rif = helper
                    .findById(it.eng.parer.entity.DecModelloXsdFascRif.class, idDipendenza);
            if (rif != null) {
                helper.removeEntity(rif, true);
            }
        } catch (Exception ex) {
            String msg = "Errore durante l'eliminazione della dipendenza XSD: "
                    + ExceptionUtils.getRootCauseMessage(ex);
            logger.error(msg, ex);
            throw new ParerUserError(msg);
        }
    }

    /**
     * Inserisce una singola dipendenza XSD per il modello padre. La validazione di coerenza tra le
     * dipendenze e l'XSD e' delegata all'Action.
     *
     * @param idPadre id del modello XSD padre
     * @param dep     singola dipendenza da inserire
     * @throws ParerUserError errore nel salvataggio
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void saveDipendenze(BigDecimal idPadre,
            it.eng.parer.slite.gen.tablebean.DecModelloXsdFascRifRowBean dep)
            throws ParerUserError {
        try {
            DecModelloXsdFascicolo padre = helper.findById(DecModelloXsdFascicolo.class, idPadre);

            // Parse XSD una sola volta: serve per validare il tipo e per contare i
            // riferimenti
            org.w3c.dom.Document xsdDoc = parseXsdDoc(padre.getBlXsd());

            // Valida subito che tiRiferimento corrisponda al tag reale nell'XSD
            // (IMPORT/INCLUDE)
            validaTipoRiferimento(xsdDoc, dep.getTiRiferimento(), dep.getSchemaLocation());

            it.eng.parer.entity.DecModelloXsdFascRif rif = new it.eng.parer.entity.DecModelloXsdFascRif();
            rif.setDecModelloXsdFascicoloPadre(padre);

            DecModelloXsdFascicolo target = helper.findById(DecModelloXsdFascicolo.class,
                    dep.getIdModelloXsdFascicoloTarget());
            rif.setDecModelloXsdFascicoloTarget(target);

            rif.setTiRiferimento(it.eng.parer.entity.constraint.DecModelloXsdFascRif.TiRiferimento
                    .valueOf(dep.getTiRiferimento()));
            rif.setNamespaceUri(dep.getNamespaceUri());
            rif.setSchemaLocation(dep.getSchemaLocation());
            rif.setDtIstituz(dep.getDtIstituz());
            rif.setDtSoppres(dep.getDtSoppres());

            helper.insertEntity(rif, true);
            helper.getEntityManager().flush();

            // Chiama il resolver solo se tutte le deps sono configurate.
            // Il resolver compila l'intero schema e deve risolvere tutti i riferimenti
            int totaleRiferimentiXsd = contaRiferimentiXsd(xsdDoc);
            List<it.eng.parer.entity.DecModelloXsdFascRif> depsPresenti = helper
                    .retrieveDipendenzaXsdAttive(idPadre);
            if (depsPresenti.size() == totaleRiferimentiXsd) {
                xsdResourceResolver(padre.getBlXsd(), idPadre.longValue());

                logger.info("Validazione XSD modulare completata con successo per modello ID {}",
                        idPadre);
            }

        } catch (IllegalStateException ex) {
            throw ex;
        } catch (Exception ex) {
            String rootMsg = ExceptionUtils.getRootCauseMessage(ex);
            throw new IllegalStateException(
                    "Errore durante il salvataggio della dipendenza XSD: " + rootMsg, ex);
        }
    }

    /**
     * Parsa l'XSD in un Document DOM con configurazione sicura (XXE-safe). Restituisce null se il
     * parsing fallisce.
     */
    private org.w3c.dom.Document parseXsdDoc(String blXsd) {
        if (StringUtils.isBlank(blXsd)) {
            return null;
        }
        try {
            javax.xml.parsers.DocumentBuilderFactory dbf = javax.xml.parsers.DocumentBuilderFactory
                    .newInstance();
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
            dbf.setXIncludeAware(false);
            dbf.setExpandEntityReferences(false);
            dbf.setFeature(javax.xml.XMLConstants.FEATURE_SECURE_PROCESSING, true);
            dbf.setNamespaceAware(true);
            javax.xml.parsers.DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(new java.io.ByteArrayInputStream(
                    blXsd.getBytes(java.nio.charset.StandardCharsets.UTF_8)));
        } catch (Exception e) {
            logger.warn("Impossibile fare il parsing dell'XSD", e);
            return null;
        }
    }

    /**
     * Conta xs:import + xs:include dal Document già parsato.
     */
    private int contaRiferimentiXsd(org.w3c.dom.Document doc) {
        if (doc == null) {
            return 0;
        }
        return doc.getElementsByTagNameNS(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, "import")
                .getLength()
                + doc.getElementsByTagNameNS(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI,
                        "include").getLength();
    }

    /**
     * Verifica che tiRiferimento (IMPORT/INCLUDE) corrisponda al tag reale nell'XSD per la
     * schemaLocation indicata. Lancia IllegalStateException se c'è mismatch o se la schemaLocation
     * non è dichiarata nell'XSD. Non si usa il namespace per distinguere i tipi perché xs:import
     * può non averlo.
     */
    private void validaTipoRiferimento(org.w3c.dom.Document doc, String tiRiferimento,
            String schemaLocation) {
        if (doc == null || StringUtils.isBlank(schemaLocation)) {
            return;
        }
        String locNorm = schemaLocation.trim();
        String tagTrovato = trovaTipoTagPerSchemaLocation(doc, locNorm);
        if (tagTrovato == null) {
            throw new IllegalStateException("La schemaLocation '" + schemaLocation
                    + "' non è dichiarata in nessun xs:import o xs:include dell'XSD root.");
        }
        if (!tagTrovato.equalsIgnoreCase(tiRiferimento)) {
            throw new IllegalStateException("La schemaLocation '" + schemaLocation
                    + "' è dichiarata come xs:" + tagTrovato.toLowerCase()
                    + " nell'XSD.");
        }
    }

    /**
     * Cerca la schemaLocation tra xs:import e xs:include del Document. Restituisce "IMPORT",
     * "INCLUDE", o null se non trovata.
     */
    private String trovaTipoTagPerSchemaLocation(org.w3c.dom.Document doc, String schemaLocation) {
        if (trovaNeiTag(doc, "import", schemaLocation)) {
            return "IMPORT";
        }
        if (trovaNeiTag(doc, "include", schemaLocation)) {
            return "INCLUDE";
        }
        return null;
    }

    private boolean trovaNeiTag(org.w3c.dom.Document doc, String localName, String schemaLocation) {
        org.w3c.dom.NodeList nodes = doc
                .getElementsByTagNameNS(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, localName);
        for (int i = 0; i < nodes.getLength(); i++) {
            String loc = ((org.w3c.dom.Element) nodes.item(i)).getAttribute("schemaLocation");
            if (schemaLocation.equals(loc != null ? loc.trim() : "")) {
                return true;
            }
        }
        return false;
    }

    /**
     * Verifica se un modello XSD può essere validato
     *
     * @param idModello id del modello XSD da verificare
     * @return true se il modello può essere validato, false altrimenti
     * @throws ParerUserError se la validazione del modello fallisce
     */
    public boolean isModelloUtilizzabile(BigDecimal idModello) throws ParerUserError {
        if (idModello == null) {
            return false;
        }
        DecModelloXsdFascicoloRowBean modello = getDecModelloXsdFascicoloRowBean(idModello);
        if (modello == null) {
            return false;
        }
        // Richiamabile (ti_modello_xsd = RICHIAMABILE): è un modulo, sempre
        // utilizzabile
        if (it.eng.parer.entity.constraint.DecModelloXsdFascicolo.TiModelloXsd.RICHIAMABILE.name()
                .equals(modello.getTiModelloXsd())) {
            return true;
        }
        // Non richiamabile: utilizzabile se autonomo o se tutte le deps sono
        // configurate
        String blXsd = modello.getBlXsd();
        int totaleRiferimenti = contaRiferimentiXsd(parseXsdDoc(blXsd));
        if (totaleRiferimenti == 0) {
            // Autonomo: nessuna dipendenza richiesta
            return true;
        }
        // Modulare: verifica che tutte le deps siano configurate e attive
        List<it.eng.parer.entity.DecModelloXsdFascRif> deps = helper
                .retrieveDipendenzaXsdAttive(idModello);
        return deps != null && deps.size() == totaleRiferimenti;
    }

    /**
     * Valida uno schema XSD modulare con DbXsdResourceResolver
     *
     * @param blXsd   contenuto dello schema XSD root da validare
     * @param idPadre id del modello XSD padre
     */
    public void xsdResourceResolver(String blXsd, Long idPadre) { // MODIFICATO
        if (StringUtils.isBlank(blXsd)) {
            return;
        }
        try {
            javax.xml.transform.stream.StreamSource xsdSource = new javax.xml.transform.stream.StreamSource(
                    new java.io.StringReader(blXsd));
            it.eng.parer.xml.xsd.DbXsdResourceResolver resolver = new it.eng.parer.xml.xsd.DbXsdResourceResolver(
                    xsdRepositoryHelper, idPadre);
            it.eng.parer.xml.utils.XmlUtils.getSchemaValidationWithResolver(xsdSource, resolver);

        } catch (org.xml.sax.SAXException ex) {
            Throwable rootCause = ExceptionUtils.getRootCause(ex);
            String msg = rootCause != null ? rootCause.getMessage() : ex.getMessage();
            throw new IllegalStateException(msg, ex);
        }
    }

    /**
     * Verifica se un modello è referenziato come target da altri modelli
     *
     * @param idModello id del modello da verificare
     *
     * @return true se il modello è referenziato da almeno un altro modello
     */
    public boolean isModelloReferenziato(BigDecimal idModello) {
        return helper.isModelloReferenziato(idModello);
    }

    /**
     * Recupera i modelli padre che referenziano un modello target
     *
     * @param idModelloTarget id del modello target
     *
     * @return lista dei codici XSD dei modelli che referenziano il target
     */
    public List<String> retrieveModelliPadreReferenzianti(BigDecimal idModelloTarget) {
        return helper.retrieveModelliPadreReferenzianti(idModelloTarget);
    }

    /**
     * Conta il numero di dipendenze di un modello
     *
     * @param idModello id del modello
     *
     * @return numero di dipendenze configurate
     */
    public int countDipendenze(BigDecimal idModello) {
        List<it.eng.parer.entity.DecModelloXsdFascRif> dipendenze = helper
                .retrieveDipendenzaXsdAttive(idModello);
        return dipendenze != null ? dipendenze.size() : 0;
    }
}