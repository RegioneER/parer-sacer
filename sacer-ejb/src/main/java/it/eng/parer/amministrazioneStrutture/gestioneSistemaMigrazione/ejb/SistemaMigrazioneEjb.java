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

package it.eng.parer.amministrazioneStrutture.gestioneSistemaMigrazione.ejb;

import it.eng.parer.amministrazioneStrutture.gestioneSistemaMigrazione.helper.SistemaMigrazioneHelper;
import it.eng.parer.aop.TransactionInterceptor;
import it.eng.parer.entity.AplSistemaMigraz;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.OrgUsoSistemaMigraz;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.slite.gen.tablebean.AplSistemaMigrazRowBean;
import it.eng.parer.slite.gen.tablebean.AplSistemaMigrazTableBean;
import it.eng.parer.slite.gen.tablebean.OrgUsoSistemaMigrazRowBean;
import it.eng.parer.slite.gen.tablebean.OrgUsoSistemaMigrazTableBean;
import it.eng.parer.web.util.Transform;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.Interceptors;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EJB di gestione dei dati del sistema di migrazione
 *
 * {@link it.eng.parer.amministrazioneStrutture.gestioneSistemaMigrazione}
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
@Interceptors({ TransactionInterceptor.class })
public class SistemaMigrazioneEjb {

    private static final Logger logger = LoggerFactory.getLogger(SistemaMigrazioneEjb.class);

    @Resource
    private SessionContext context;
    @EJB
    private SistemaMigrazioneHelper helper;

    /**
     * Ritorna la lista dei sistemi di migrazione esistenti nell'applicazione non associati alla struttura
     *
     * @param idStrut
     *            id struttura
     *
     * @return il tableBean contenente la lista
     */
    public AplSistemaMigrazTableBean getAplSistemaMigrazTableBean(BigDecimal idStrut) {
        AplSistemaMigrazTableBean table = new AplSistemaMigrazTableBean();
        List<AplSistemaMigraz> list = helper.retrieveAplSistemaMigraz(idStrut);
        if (list != null && !list.isEmpty()) {
            try {
                table = (AplSistemaMigrazTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore durante il recupero dei sistemi di migrazione "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
                throw new IllegalStateException("Errore durante il recupero dei sistemi di migrazione");
            }
        }
        return table;
    }

    /**
     * Ritorna la lista dei sistemi di migrazione legati alla struttura data come parametro
     *
     * @param idStrut
     *            id struttura
     *
     * @return il tableBean contenente la lista
     */
    public OrgUsoSistemaMigrazTableBean getOrgUsoSistemaMigrazTableBean(BigDecimal idStrut) {
        OrgUsoSistemaMigrazTableBean table = new OrgUsoSistemaMigrazTableBean();
        List<OrgUsoSistemaMigraz> list = helper.retrieveOrgUsoSistemaMigraz(idStrut);
        if (list != null && !list.isEmpty()) {
            try {
                for (OrgUsoSistemaMigraz usoSistemaMigraz : list) {
                    OrgUsoSistemaMigrazRowBean row = (OrgUsoSistemaMigrazRowBean) Transform
                            .entity2RowBean(usoSistemaMigraz);
                    row.setString("nm_sistema_migraz", usoSistemaMigraz.getAplSistemaMigraz().getNmSistemaMigraz());
                    row.setString("ds_sistema_migraz", usoSistemaMigraz.getAplSistemaMigraz().getDsSistemaMigraz());
                    table.add(row);
                }
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore durante il recupero dei sistemi di migrazione per la struttura "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
                throw new IllegalStateException("Errore durante il recupero dei sistemi di migrazione");
            }
        }
        return table;
    }

    /**
     * Esegue il salvataggio della relazione tra sistema di migrazione e struttura
     *
     * @param idStrut
     *            id struttura
     * @param idSistemaMigraz
     *            id sistema migrazione
     *
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void insertOrgUsoSistemaMigraz(BigDecimal idStrut, BigDecimal idSistemaMigraz) throws ParerUserError {
        try {
            OrgStrut strut = helper.findById(OrgStrut.class, idStrut);
            AplSistemaMigraz aplSistemaMigraz = helper.findById(AplSistemaMigraz.class, idSistemaMigraz);

            OrgUsoSistemaMigraz orgUsoSistemaMigraz = new OrgUsoSistemaMigraz();
            orgUsoSistemaMigraz.setOrgStrut(strut);
            orgUsoSistemaMigraz.setAplSistemaMigraz(aplSistemaMigraz);

            helper.insertEntity(orgUsoSistemaMigraz, true);
        } catch (Exception e) {
            String messaggio = "Eccezione imprevista nel salvataggio del sistema di migrazione per la struttura ";
            messaggio += ExceptionUtils.getRootCauseMessage(e);
            logger.error(messaggio, e);
            throw new ParerUserError(messaggio);
        }
    }

    public BaseTableInterface getNmSistemaMigrazTableBean(BigDecimal idStrut) {
        BaseTableInterface tmpTableBean = new BaseTable();
        List<String> listaSistemiMigraz = helper.retrieveNmSistemaMigraz(idStrut);
        try {
            if (!listaSistemiMigraz.isEmpty()) {
                for (String sistema : listaSistemiMigraz) {
                    BaseRow row = new BaseRow();
                    row.setString("nm_sistema_migraz", sistema);
                    tmpTableBean.add(row);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new IllegalStateException("Errore inatteso nel recupero dei sistemi di migrazione");
        }
        return tmpTableBean;
    }

    /**
     * Ritorna la lista dei sistemi di migrazione esistenti nell'applicazione in base ai filtri di ricerca
     *
     * @param nmSistemaMigraz
     *            nome sistema migrazione
     * @param dsSistemaMigraz
     *            descrizione sistema migrazione
     *
     * @return il tableBean contenente la lista
     */
    public AplSistemaMigrazTableBean getAplSistemaMigrazTableBean(String nmSistemaMigraz, String dsSistemaMigraz) {
        AplSistemaMigrazTableBean table = new AplSistemaMigrazTableBean();
        List<AplSistemaMigraz> list = helper.retrieveAplSistemaMigraz(nmSistemaMigraz, dsSistemaMigraz);
        if (list != null && !list.isEmpty()) {
            try {
                table = (AplSistemaMigrazTableBean) Transform.entities2TableBean(list);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore durante il recupero dei sistemi di migrazione "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
                throw new IllegalStateException("Errore durante il recupero dei sistemi di migrazione");
            }
        }
        return table;
    }

    /**
     *
     * @param idSistemaMigraz
     *            id sistema migrazione
     *
     * @return il rowBean del sistema migrazione
     */
    public AplSistemaMigrazRowBean getAplSistemaMigrazRowBean(BigDecimal idSistemaMigraz) {
        AplSistemaMigrazRowBean row = new AplSistemaMigrazRowBean();
        AplSistemaMigraz sistemaMigraz = helper.findById(AplSistemaMigraz.class, idSistemaMigraz);
        if (sistemaMigraz != null) {
            try {
                row = (AplSistemaMigrazRowBean) Transform.entity2RowBean(sistemaMigraz);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException ex) {
                logger.error("Errore durante il recupero del sistema di migrazione "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
                throw new IllegalStateException("Errore durante il recupero del sistema di migrazione");
            }
        }
        return row;
    }

    /**
     * Metodo di insert di un nuovo sistema di migrazione
     *
     * @param nmSistemaMigraz
     *            nome sistema migrazione
     * @param dsSistemaMigraz
     *            descrizione sistema migrazione
     *
     * @return id del nuovo sistema di migrazione
     *
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public Long saveSistemaMigrazione(String nmSistemaMigraz, String dsSistemaMigraz) throws ParerUserError {
        // Controllo esistenza denominazione
        if (helper.getAplSistemaMigraz(nmSistemaMigraz) != null) {
            throw new ParerUserError("Sistema di migrazione gi\u00E0 censito nel sistema");
        }
        Long idSistemaMigraz = null;
        try {
            AplSistemaMigraz sistemaMigraz = new AplSistemaMigraz();
            sistemaMigraz.setNmSistemaMigraz(nmSistemaMigraz);
            sistemaMigraz.setDsSistemaMigraz(dsSistemaMigraz);

            helper.insertEntity(sistemaMigraz, true);

            idSistemaMigraz = sistemaMigraz.getIdSistemaMigraz();
        } catch (Exception ex) {
            logger.error("Errore imprevisto durante il salvataggio del sistema di migrazione : "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
            throw new ParerUserError("Eccezione imprevista durante il salvataggio del sistema di migrazione");
        }
        return idSistemaMigraz;
    }

    /**
     * Metodo di update di un sistema di migrazione
     *
     * @param idSistemaMigraz
     *            id sistema migrazione
     * @param nmSistemaMigraz
     *            nome sistema migrazione
     * @param dsSistemaMigraz
     *            descrizione sistema migrazione
     *
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void saveSistemaMigrazione(BigDecimal idSistemaMigraz, String nmSistemaMigraz, String dsSistemaMigraz)
            throws ParerUserError {
        AplSistemaMigraz sistemaMigraz = helper.findById(AplSistemaMigraz.class, idSistemaMigraz);
        boolean nomeModificato = !sistemaMigraz.getNmSistemaMigraz().equals(nmSistemaMigraz);
        // Controllo esistenza denominazione
        if (helper.getAplSistemaMigraz(nmSistemaMigraz) != null && nomeModificato) {
            throw new ParerUserError("Sistema di migrazione gi\u00E0 censito nel sistema");
        }
        // Controllo associazione ad una struttura
        if (nomeModificato && helper.existsOrgUsoSistemaMigraz(idSistemaMigraz)) {
            throw new ParerUserError(
                    "Sistema di migrazione gi\u00E0 correlato ad almeno una struttura: nome non modificabile");
        }
        try {
            sistemaMigraz.setNmSistemaMigraz(nmSistemaMigraz);
            sistemaMigraz.setDsSistemaMigraz(dsSistemaMigraz);
        } catch (Exception ex) {
            logger.error("Errore imprevisto durante il salvataggio del sistema di migrazione : "
                    + ExceptionUtils.getRootCauseMessage(ex), ex);
            throw new ParerUserError("Eccezione imprevista durante il salvataggio del sistema di migrazione");
        }
    }

    /**
     * Metodo di eliminazione di un sistema di migrazione
     *
     * @param idSistemaMigraz
     *            id sistema migrazione
     *
     * @throws ParerUserError
     *             errore generico
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteAplSistemaMigraz(BigDecimal idSistemaMigraz) throws ParerUserError {
        logger.debug("Eseguo l'eliminazione del sistema di migrazione " + idSistemaMigraz);

        AplSistemaMigraz sistemaMigraz = helper.findById(AplSistemaMigraz.class, idSistemaMigraz);

        // Controllo associazione ad una struttura
        if (helper.existsOrgUsoSistemaMigraz(idSistemaMigraz)) {
            throw new ParerUserError(
                    "Il sistema di migrazione è correlato ad almeno una struttura e non può essere eliminato");
        }

        helper.removeEntity(sistemaMigraz, true);
    }

}
