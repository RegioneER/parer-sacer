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

import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.AplParamApplic;
import it.eng.parer.entity.AplValParamApplicMulti;
import it.eng.parer.entity.AplValoreParamApplic;
import it.eng.parer.entity.DecAaTipoFascicolo;
import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.parer.entity.OrgAmbiente;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.entity.constraint.AplValoreParamApplic.TiAppart;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.firma.crypto.ejb.SignatureSessionEjb;
import it.eng.parer.grantedEntity.UsrUser;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.slite.gen.tablebean.AplParamApplicRowBean;
import it.eng.parer.slite.gen.tablebean.AplParamApplicTableBean;
import it.eng.parer.slite.gen.tablebean.AplParamApplicTableDescriptor;
import it.eng.parer.slite.gen.tablebean.OrgStrutRowBean;
import it.eng.parer.web.helper.AmministrazioneHelper;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.util.Transform;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;

/**
 *
 * @author gilioli_p
 */
@Stateless(mappedName = "AmministrazioneEjb")
@LocalBean
public class AmministrazioneEjb {

    @EJB
    private AmministrazioneHelper amministrazioneHelper;
    /*
     * @EJB private StrutCache strutCache;
     */
    @EJB
    private SacerLogEjb sacerLogEjb;
    @EJB
    private ConfigurationHelper configurationHelper;

    private static final Logger logger = LoggerFactory.getLogger(AmministrazioneEjb.class);

    public AplParamApplicTableBean getAplParamApplicTableBean(String tiParamApplic, String tiGestioneParam,
            String flAppartApplic, String flAppartAmbiente, String flAppartStrut, String flAppartTipoUnitaDoc,
            String flAppartAaTipoFascicolo) {
        AplParamApplicTableBean paramApplicTableBean = new AplParamApplicTableBean();
        List<AplParamApplic> paramApplicList = amministrazioneHelper.getAplParamApplicList(tiParamApplic,
                tiGestioneParam, flAppartApplic, flAppartAmbiente, flAppartStrut, flAppartTipoUnitaDoc,
                flAppartAaTipoFascicolo);

        try {
            if (paramApplicList != null && !paramApplicList.isEmpty()) {
                for (AplParamApplic paramApplic : paramApplicList) {
                    AplParamApplicRowBean paramApplicRowBean = (AplParamApplicRowBean) Transform
                            .entity2RowBean(paramApplic);
                    paramApplicRowBean.setString("ds_valore_param_applic", "");
                    for (AplValoreParamApplic valoreParamApplic : paramApplic.getAplValoreParamApplics()) {
                        if (valoreParamApplic.getTiAppart().equals("APPLIC")) {
                            paramApplicRowBean.setString("ds_valore_param_applic",
                                    valoreParamApplic.getDsValoreParamApplic());
                        }
                    }
                    paramApplicTableBean.add(paramApplicRowBean);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return paramApplicTableBean;
    }

    public AplParamApplicTableBean getAplParamApplicTableBean(String tiParamApplic, String tiGestioneParam,
            String flAppartApplic, String flAppartAmbiente, String flAppartStrut, String flAppartTipoUnitaDoc,
            String flAppartAaTipoFascicolo, boolean filterValid) {
        AplParamApplicTableBean paramApplicTableBean = new AplParamApplicTableBean();
        List<AplParamApplic> paramApplicList = amministrazioneHelper.getAplParamApplicList(tiParamApplic,
                tiGestioneParam, flAppartApplic, flAppartAmbiente, flAppartStrut, flAppartTipoUnitaDoc,
                flAppartAaTipoFascicolo, filterValid);

        try {
            if (paramApplicList != null && !paramApplicList.isEmpty()) {
                for (AplParamApplic paramApplic : paramApplicList) {
                    AplParamApplicRowBean paramApplicRowBean = (AplParamApplicRowBean) Transform
                            .entity2RowBean(paramApplic);
                    paramApplicRowBean.setString("ds_valore_param_applic", "");
                    for (AplValoreParamApplic valoreParamApplic : paramApplic.getAplValoreParamApplics()) {
                        if (valoreParamApplic.getTiAppart().equals("APPLIC")) {
                            paramApplicRowBean.setString("ds_valore_param_applic",
                                    valoreParamApplic.getDsValoreParamApplic());
                        }
                    }
                    paramApplicTableBean.add(paramApplicRowBean);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return paramApplicTableBean;
    }

    public boolean checkParamApplic(String nmParamApplic, BigDecimal idParamApplic) {
        return amministrazioneHelper.existsAplParamApplic(nmParamApplic, idParamApplic);
    }

    /**
     * Esegue il salvataggio del rowBean del parametro di configurazione
     *
     * @param row
     *            il rowBean da salvare su DB
     *
     * @return true in mancanza di eccezioni
     */
    public boolean saveConfiguration(AplParamApplicRowBean row) {
        boolean result = false;
        AplParamApplic config;
        boolean newRow;

        try {

            if (row.getIdParamApplic() != null) {
                config = amministrazioneHelper.findById(AplParamApplic.class, row.getIdParamApplic().longValue());
                newRow = false;
            } else {
                config = new AplParamApplic();
                newRow = true;
            }

            config.setTiParamApplic(row.getTiParamApplic());
            config.setTiGestioneParam(row.getTiGestioneParam());
            config.setNmParamApplic(row.getNmParamApplic());
            config.setDmParamApplic(row.getDmParamApplic());
            config.setDsListaValoriAmmessi(row.getDsListaValoriAmmessi());
            config.setDsParamApplic(row.getDsParamApplic());
            config.setFlMulti(row.getFlMulti());
            config.setFlAppartApplic(row.getFlAppartApplic());
            config.setFlAppartAmbiente(row.getFlAppartAmbiente());
            config.setFlAppartStrut(row.getFlAppartStrut());
            config.setFlAppartTipoUnitaDoc(row.getFlAppartTipoUnitaDoc());
            config.setFlAppartAaTipoFascicolo(row.getFlAppartAaTipoFascicolo());
            config.setTiValoreParamApplic(row.getTiValoreParamApplic());
            config.setCdVersioneAppIni(row.getCdVersioneAppIni());
            config.setCdVersioneAppFine(row.getCdVersioneAppFine());

            if (newRow) {
                amministrazioneHelper.getEntityManager().persist(config);
                row.setIdParamApplic(BigDecimal.valueOf(config.getIdParamApplic()));
            }
            result = true;
            amministrazioneHelper.getEntityManager().flush();

            // GESTIONE DS_VALORE_PARAM_APPLIC
            // Se è una nuova riga di AplParamApplic, nel caso sia stato inserito il valore, vai a persisterlo
            if (newRow) {
                if (row.getString("ds_valore_param_applic") != null
                        && !row.getString("ds_valore_param_applic").equals("")) {
                    AplValoreParamApplic valore = new AplValoreParamApplic();
                    valore.setAplParamApplic(config);
                    valore.setTiAppart("APPLIC");
                    valore.setOrgAmbiente(null);
                    valore.setOrgStrut(null);
                    valore.setDecTipoUnitaDoc(null);
                    valore.setDecAaTipoFascicolo(null);
                    valore.setDsValoreParamApplic(row.getString("ds_valore_param_applic"));
                    amministrazioneHelper.getEntityManager().persist(valore);
                }
            } else {
                // Se invece la riga di AplParamApplic già esisteva:
                // Se c'è un valore parametro di tipo APPLIC, modificalo
                AplValoreParamApplic valoreParamApplic = amministrazioneHelper
                        .getAplValoreParamApplic(config.getIdParamApplic(), "APPLIC");
                if (valoreParamApplic != null) {
                    if (row.getString("ds_valore_param_applic") != null
                            && !row.getString("ds_valore_param_applic").equals("")) {
                        valoreParamApplic.setDsValoreParamApplic(row.getString("ds_valore_param_applic"));
                    } else {
                        amministrazioneHelper.removeEntity(valoreParamApplic, true);
                    }
                } else {
                    if (row.getString("ds_valore_param_applic") != null
                            && !row.getString("ds_valore_param_applic").equals("")) {
                        AplValoreParamApplic valore = new AplValoreParamApplic();
                        valore.setAplParamApplic(config);
                        valore.setTiAppart("APPLIC");
                        valore.setOrgAmbiente(null);
                        valore.setOrgStrut(null);
                        valore.setDecTipoUnitaDoc(null);
                        valore.setDecAaTipoFascicolo(null);
                        valore.setDsValoreParamApplic(row.getString("ds_valore_param_applic"));
                        amministrazioneHelper.getEntityManager().persist(valore);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            result = false;
        }
        return result;
    }

    public BaseTable getTiParamApplicBaseTable() {
        BaseTable table = new BaseTable();
        List<String> tiParamApplicList = amministrazioneHelper.getTiParamApplic();
        if (tiParamApplicList != null && !tiParamApplicList.isEmpty()) {
            try {
                for (String row : tiParamApplicList) {
                    BaseRowInterface r = new BaseRow();
                    r.setString(AplParamApplicTableDescriptor.COL_TI_PARAM_APPLIC, row);
                    table.add(r);
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return table;
    }

    /**
     * Rimuove la riga di parametri definita nel rowBean
     *
     * @param idValoreParamApplic
     *            id valore parametro applicativo
     *
     * @return true se eliminato con successo
     */
    public boolean deleteParametroAmbiente(BigDecimal idValoreParamApplic) {
        AplValoreParamApplic parametro;
        boolean result = false;
        try {
            parametro = (AplValoreParamApplic) amministrazioneHelper.findById(AplValoreParamApplic.class,
                    idValoreParamApplic);
            // Rimuovo il record
            amministrazioneHelper.getEntityManager().remove(parametro);
            amministrazioneHelper.getEntityManager().flush();
            result = true;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return result;
    }

    /**
     * Rimuove la riga di parametri multipli definita nel rowBean
     *
     * @param idParamApplic
     *            id parametro applicativo
     * @param idAmbiente
     *            id ambiente
     *
     * @return true se eliminato con successo
     */
    public boolean deleteParametroMultiploAmbiente(BigDecimal idParamApplic, BigDecimal idAmbiente) {
        boolean result = false;
        try {
            List<AplValParamApplicMulti> parametri = amministrazioneHelper.getAplValParamApplicMultiList(idParamApplic,
                    idAmbiente);
            for (AplValParamApplicMulti parametro : parametri) {
                // Rimuovo i record
                amministrazioneHelper.getEntityManager().remove(parametro);
                amministrazioneHelper.getEntityManager().flush();
            }
            result = true;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return result;
    }

    /**
     * Restituisce un array di object con i tablebean dei parametri
     *
     * @param idAmbiente
     *            id ambiente
     * @param funzione
     *            lista funzioni
     *
     * @return il tablebean
     *
     * @throws ParerUserError
     *             errore generico
     */
    public Object[] getAplParamApplicAmbiente(BigDecimal idAmbiente, List<String> funzione) throws ParerUserError {
        Object[] parametriObj = new Object[3];
        AplParamApplicTableBean paramApplicAmministrazioneTableBean = new AplParamApplicTableBean();
        AplParamApplicTableBean paramApplicGestioneTableBean = new AplParamApplicTableBean();
        AplParamApplicTableBean paramApplicConservazioneTableBean = new AplParamApplicTableBean();
        // Ricavo la lista dei parametri definiti per l'AMBIENTE
        List<AplParamApplic> paramApplicList = amministrazioneHelper.getAplParamApplicListAmbiente(funzione);
        if (!paramApplicList.isEmpty()) {
            try {
                // Per ogni parametro, popolo i valori su applicazione ed ambiente ricavandoli da
                // APL_VALORE_PARAM_APPLIC
                for (AplParamApplic paramApplic : paramApplicList) {
                    AplParamApplicRowBean paramApplicRowBean = new AplParamApplicRowBean();
                    paramApplicRowBean = (AplParamApplicRowBean) Transform.entity2RowBean(paramApplic);
                    populateParamApplicAmbienteRowBean(paramApplicRowBean, idAmbiente,
                            paramApplic.getTiGestioneParam());
                    switch (paramApplic.getTiGestioneParam()) {
                    case "amministrazione":
                        paramApplicAmministrazioneTableBean.add(paramApplicRowBean);
                        break;
                    case "gestione":
                        paramApplicGestioneTableBean.add(paramApplicRowBean);
                        break;
                    case "conservazione":
                        paramApplicConservazioneTableBean.add(paramApplicRowBean);
                        break;
                    default:
                        break;
                    }
                }
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                    | NoSuchMethodException | InvocationTargetException e) {
                logger.error("Errore durante il recupero dei parametri sull'ambiente "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new ParerUserError("Errore durante il recupero dei parametri sull'ambiente ");
            }
        }
        parametriObj[0] = paramApplicAmministrazioneTableBean;
        parametriObj[1] = paramApplicGestioneTableBean;
        parametriObj[2] = paramApplicConservazioneTableBean;
        return parametriObj;
    }

    /**
     * Restituisce un array di object con i tablebean dei parametri di amministrazione dell'ambiente
     *
     * @param idAmbiente
     *            id ambiente
     * @param funzione
     *            lista funzioni
     * @param filterValid
     *            visualizzare o meno i record parametri cessati
     *
     * @return il tablebean
     *
     * @throws ParerUserError
     *             errore generico
     */
    public AplParamApplicTableBean getAplParamApplicAmministrazioneAmbiente(BigDecimal idAmbiente,
            List<String> funzione, boolean filterValid) throws ParerUserError {
        AplParamApplicTableBean paramApplicAmministrazioneTableBean = new AplParamApplicTableBean();

        // Ricavo la lista dei parametri di amministrazione definiti per la STRUTTURA
        List<AplParamApplic> paramApplicList = amministrazioneHelper.getAplParamApplicListAmbiente(funzione,
                "amministrazione", filterValid);
        if (!paramApplicList.isEmpty()) {
            try {
                // Per ogni parametro, popolo i valori su applicazione e ambiente ricavandoli da
                // APL_VALORE_PARAM_APPLIC
                for (AplParamApplic paramApplic : paramApplicList) {
                    AplParamApplicRowBean paramApplicRowBean = (AplParamApplicRowBean) Transform
                            .entity2RowBean(paramApplic);
                    populateParamApplicAmbienteRowBean(paramApplicRowBean, idAmbiente,
                            paramApplic.getTiGestioneParam());
                    paramApplicAmministrazioneTableBean.add(paramApplicRowBean);
                }
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                    | NoSuchMethodException | InvocationTargetException e) {
                logger.error("Errore durante il recupero dei parametri di amministrazione sull'ambiente "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new ParerUserError("Errore durante il recupero dei parametri di amministrazione sull'ambiente ");
            }
        }
        return paramApplicAmministrazioneTableBean;
    }

    /**
     * Restituisce un array di object con i tablebean dei parametri di gestione dell'ambiente
     *
     * @param idAmbiente
     *            id ambiente
     * @param funzione
     *            lista funzioni
     * @param filterValid
     *            visualizzare o meno i record parametri cessati
     *
     * @return il tablebean
     *
     * @throws ParerUserError
     *             errore generico
     */
    public AplParamApplicTableBean getAplParamApplicGestioneAmbiente(BigDecimal idAmbiente, List<String> funzione,
            boolean filterValid) throws ParerUserError {
        AplParamApplicTableBean paramApplicGestioneTableBean = new AplParamApplicTableBean();

        // Ricavo la lista dei parametri di amministrazione definiti per l'AMBIENTE
        List<AplParamApplic> paramApplicList = amministrazioneHelper.getAplParamApplicListAmbiente(funzione, "gestione",
                filterValid);
        if (!paramApplicList.isEmpty()) {
            try {
                // Per ogni parametro, popolo i valori su applicazione e ambiente ricavandoli da
                // APL_VALORE_PARAM_APPLIC
                for (AplParamApplic paramApplic : paramApplicList) {
                    AplParamApplicRowBean paramApplicRowBean = (AplParamApplicRowBean) Transform
                            .entity2RowBean(paramApplic);
                    populateParamApplicAmbienteRowBean(paramApplicRowBean, idAmbiente,
                            paramApplic.getTiGestioneParam());
                    paramApplicGestioneTableBean.add(paramApplicRowBean);
                }
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                    | NoSuchMethodException | InvocationTargetException e) {
                logger.error("Errore durante il recupero dei parametri di gestione sull'ambiente "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new ParerUserError("Errore durante il recupero dei parametri di gestione sull'ambiente ");
            }
        }
        return paramApplicGestioneTableBean;
    }

    /**
     * Restituisce un array di object con i tablebean dei parametri di conservazione sull'ambiente
     *
     * @param idAmbiente
     *            id ambiente
     * @param funzione
     *            lista funzioni
     * @param filterValid
     *            visualizzare o meno i record parametri cessati
     *
     * @return il tablebean
     *
     * @throws ParerUserError
     *             errore generico
     */
    public AplParamApplicTableBean getAplParamApplicConservazioneAmbiente(BigDecimal idAmbiente, List<String> funzione,
            boolean filterValid) throws ParerUserError {
        AplParamApplicTableBean paramApplicConservazioneTableBean = new AplParamApplicTableBean();

        // Ricavo la lista dei parametri di amministrazione definiti per l'AMBIENTE
        List<AplParamApplic> paramApplicList = amministrazioneHelper.getAplParamApplicListAmbiente(funzione,
                "conservazione", filterValid);
        if (!paramApplicList.isEmpty()) {
            try {
                // Per ogni parametro, popolo i valori su applicazione e ambiente ricavandoli da
                // APL_VALORE_PARAM_APPLIC
                for (AplParamApplic paramApplic : paramApplicList) {
                    AplParamApplicRowBean paramApplicRowBean = (AplParamApplicRowBean) Transform
                            .entity2RowBean(paramApplic);
                    populateParamApplicAmbienteRowBean(paramApplicRowBean, idAmbiente,
                            paramApplic.getTiGestioneParam());
                    paramApplicConservazioneTableBean.add(paramApplicRowBean);
                }
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                    | NoSuchMethodException | InvocationTargetException e) {
                logger.error("Errore durante il recupero dei parametri di conservazione sull'ambiente "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new ParerUserError("Errore durante il recupero dei parametri di conservazione sull'ambiente ");
            }
        }
        return paramApplicConservazioneTableBean;
    }

    private void populateParamApplicAmbienteRowBean(AplParamApplicRowBean paramApplicRowBean, BigDecimal idAmbiente,
            String tiGestioneParam) {
        String nomeCampoAmbiente = tiGestioneParam.equals("amministrazione") ? "ds_valore_param_applic_ambiente_amm"
                : tiGestioneParam.equals("gestione") ? "ds_valore_param_applic_ambiente_gest"
                        : "ds_valore_param_applic_ambiente_cons";

        // Determino i valori su applicazione ed ambiente
        AplValoreParamApplic valoreParamApplicApplic = amministrazioneHelper.getAplValoreParamApplic(
                paramApplicRowBean.getIdParamApplic(), TiAppart.APPLIC.name(), null, null, null, null);
        if (valoreParamApplicApplic != null) {
            paramApplicRowBean.setString("ds_valore_param_applic_applic",
                    valoreParamApplicApplic.getDsValoreParamApplic());
        }

        if (idAmbiente != null) {
            AplValoreParamApplic valoreParamApplicAmbiente = amministrazioneHelper.getAplValoreParamApplic(
                    paramApplicRowBean.getIdParamApplic(), TiAppart.AMBIENTE.name(), idAmbiente, null, null, null);
            if (valoreParamApplicAmbiente != null) {
                paramApplicRowBean.setString(nomeCampoAmbiente, valoreParamApplicAmbiente.getDsValoreParamApplic());
                paramApplicRowBean.setBigDecimal("id_valore_param_applic",
                        BigDecimal.valueOf(valoreParamApplicAmbiente.getIdValoreParamApplic()));
            }
        }
    }

    public void insertAplValoreParamApplic(OrgAmbiente ambiente, OrgStrut strut, DecTipoUnitaDoc tipoUnitaDoc,
            DecAaTipoFascicolo aaTipoFascicolo, BigDecimal idParamApplic, String tiAppart, String dsValoreParamApplic) {
        AplValoreParamApplic valoreParamApplic = new AplValoreParamApplic();
        valoreParamApplic.setAplParamApplic(amministrazioneHelper.findById(AplParamApplic.class, idParamApplic));
        valoreParamApplic.setDsValoreParamApplic(dsValoreParamApplic);
        valoreParamApplic.setTiAppart(tiAppart);
        valoreParamApplic.setOrgAmbiente(ambiente);
        valoreParamApplic.setOrgStrut(strut);
        valoreParamApplic.setDecTipoUnitaDoc(tipoUnitaDoc);
        valoreParamApplic.setDecAaTipoFascicolo(aaTipoFascicolo);
        amministrazioneHelper.insertEntity(valoreParamApplic, true);
    }

    public String checkParametriAmmessi(String nomeCampoPart, AplParamApplicTableBean parametriAmministrazione,
            AplParamApplicTableBean parametriConservazione, AplParamApplicTableBean parametriGestione) {
        String error = "Il valore del parametro non è compreso tra i valori ammessi sul parametro";
        boolean errorFound = false;

        // Controllo valori possibili su ente
        for (AplParamApplicRowBean paramApplicRowBean : parametriAmministrazione) {
            if (paramApplicRowBean.getString("ds_lista_valori_ammessi") != null
                    && !paramApplicRowBean.getString("ds_lista_valori_ammessi").equals("")) {
                if (paramApplicRowBean.getString("ds_valore_param_applic_" + nomeCampoPart + "_amm") != null
                        && !paramApplicRowBean.getString("ds_valore_param_applic_" + nomeCampoPart + "_amm")
                                .equals("")) {
                    if (!inValoriPossibili(
                            paramApplicRowBean.getString("ds_valore_param_applic_" + nomeCampoPart + "_amm"),
                            paramApplicRowBean.getString("ds_lista_valori_ammessi"))) {
                        errorFound = true;
                    }
                }
            }
        }

        for (AplParamApplicRowBean paramApplicRowBean : parametriConservazione) {
            if (paramApplicRowBean.getString("ds_lista_valori_ammessi") != null
                    && !paramApplicRowBean.getString("ds_lista_valori_ammessi").equals("")) {
                if (paramApplicRowBean.getString("ds_valore_param_applic_" + nomeCampoPart + "_cons") != null
                        && !paramApplicRowBean.getString("ds_valore_param_applic_" + nomeCampoPart + "_cons")
                                .equals("")) {
                    if (!inValoriPossibili(
                            paramApplicRowBean.getString("ds_valore_param_applic_" + nomeCampoPart + "_cons"),
                            paramApplicRowBean.getString("ds_lista_valori_ammessi"))) {
                        errorFound = true;
                    }
                }
            }
        }

        for (AplParamApplicRowBean paramApplicRowBean : parametriGestione) {
            if (paramApplicRowBean.getString("ds_lista_valori_ammessi") != null
                    && !paramApplicRowBean.getString("ds_lista_valori_ammessi").equals("")) {
                if (paramApplicRowBean.getString("ds_valore_param_applic_" + nomeCampoPart + "_gest") != null
                        && !paramApplicRowBean.getString("ds_valore_param_applic_" + nomeCampoPart + "_gest")
                                .equals("")) {
                    if (!inValoriPossibili(
                            paramApplicRowBean.getString("ds_valore_param_applic_" + nomeCampoPart + "_gest"),
                            paramApplicRowBean.getString("ds_lista_valori_ammessi"))) {
                        errorFound = true;
                    }
                }
            }
        }

        if (errorFound) {
            return error;
        } else {
            return null;
        }
    }

    private boolean inValoriPossibili(String dsValoreParamApplicEnte, String dsListaValoriAmmessi) {
        String[] tokens = dsListaValoriAmmessi.split("\\|");
        Set<String> mySet = new HashSet<String>(Arrays.asList(tokens));
        return mySet.contains(dsValoreParamApplicEnte);
    }

    /**
     * Restituisce il tablebean dei parametri multipli
     *
     * @param idAmbiente
     *            id ambiente
     *
     * @return il tablebean
     *
     * @throws ParerUserError
     *             errore generico
     */
    public AplParamApplicTableBean getAplParamApplicMultiAmbiente(BigDecimal idAmbiente) throws ParerUserError {
        AplParamApplicTableBean paramApplicMultipliAmbienteTableBean = new AplParamApplicTableBean();
        // Ricavo la lista dei parametri definiti per l'AMBIENTE
        List<AplParamApplic> paramApplicMultipliAmbienteList = amministrazioneHelper
                .getAplParamApplicMultiListAmbiente();
        if (!paramApplicMultipliAmbienteList.isEmpty()) {
            try {
                // Per ogni parametro, popolo i valori su ambiente ricavandoli da APL_VAL_PARAM_APPLIC_MULTI
                for (AplParamApplic paramApplic : paramApplicMultipliAmbienteList) {
                    AplParamApplicRowBean paramApplicRowBean = (AplParamApplicRowBean) Transform
                            .entity2RowBean(paramApplic);
                    populateParamApplicMultiAmbienteRowBean(paramApplicRowBean, idAmbiente);
                    paramApplicMultipliAmbienteTableBean.add(paramApplicRowBean);
                }
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                    | NoSuchMethodException | InvocationTargetException e) {
                logger.error("Errore durante il recupero dei parametri multipli sull'ambiente "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new ParerUserError("Errore durante il recupero dei parametri multipli sull'ambiente ");
            }
        }
        return paramApplicMultipliAmbienteTableBean;
    }

    /**
     * Restituisce il tablebean dei parametri multipli
     *
     * @param idAmbiente
     *            id ambiente
     * @param filterValid
     *            true per mostrare i record non cessati
     *
     * @return il tablebean
     *
     * @throws ParerUserError
     *             errore generico
     */
    public AplParamApplicTableBean getAplParamApplicMultiAmbiente(BigDecimal idAmbiente, boolean filterValid)
            throws ParerUserError {
        AplParamApplicTableBean paramApplicMultipliAmbienteTableBean = new AplParamApplicTableBean();
        // Ricavo la lista dei parametri definiti per l'AMBIENTE
        List<AplParamApplic> paramApplicMultipliAmbienteList = amministrazioneHelper
                .getAplParamApplicMultiListAmbiente(filterValid);
        if (!paramApplicMultipliAmbienteList.isEmpty()) {
            try {
                // Per ogni parametro, popolo i valori su ambiente ricavandoli da APL_VAL_PARAM_APPLIC_MULTI
                for (AplParamApplic paramApplic : paramApplicMultipliAmbienteList) {
                    AplParamApplicRowBean paramApplicRowBean = (AplParamApplicRowBean) Transform
                            .entity2RowBean(paramApplic);
                    populateParamApplicMultiAmbienteRowBean(paramApplicRowBean, idAmbiente);
                    paramApplicMultipliAmbienteTableBean.add(paramApplicRowBean);
                }
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                    | NoSuchMethodException | InvocationTargetException e) {
                logger.error("Errore durante il recupero dei parametri multipli sull'ambiente "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new ParerUserError("Errore durante il recupero dei parametri multipli sull'ambiente ");
            }
        }
        return paramApplicMultipliAmbienteTableBean;
    }

    private void populateParamApplicMultiAmbienteRowBean(AplParamApplicRowBean paramApplicRowBean,
            BigDecimal idAmbiente) {
        // Determino i valori su ambiente
        if (idAmbiente != null) {
            List<AplValParamApplicMulti> valoreParamApplicMultiAmbienteList = amministrazioneHelper
                    .getAplValParamApplicMultiList(paramApplicRowBean.getIdParamApplic(), idAmbiente);
            String dsValoreParamApplicMulti = "";
            for (AplValParamApplicMulti valoreParamApplicMultiAmbiente : valoreParamApplicMultiAmbienteList) {
                dsValoreParamApplicMulti = dsValoreParamApplicMulti
                        + valoreParamApplicMultiAmbiente.getDsValoreParamApplic() + "|";
            }
            if (valoreParamApplicMultiAmbienteList.size() > 0) {
                dsValoreParamApplicMulti = dsValoreParamApplicMulti.substring(0, dsValoreParamApplicMulti.length() - 1);
            }

            paramApplicRowBean.setString("ds_valore_param_applic_multi", dsValoreParamApplicMulti);

        }
    }

    /**
     * Rimuove la riga di parametri definita nel rowBean
     *
     * @param param
     *            parametri per il logging
     * @param idValoreParamApplic
     *            id valore pametro applicativo
     *
     * @return true se eliminato con successo
     */
    public boolean deleteParametroAaTipoFasc(LogParam param, BigDecimal idValoreParamApplic) {
        AplValoreParamApplic parametro;
        boolean result = false;
        try {
            parametro = (AplValoreParamApplic) amministrazioneHelper.findById(AplValoreParamApplic.class,
                    idValoreParamApplic);
            Long idTipoFascicolo = parametro.getDecAaTipoFascicolo().getDecTipoFascicolo().getIdTipoFascicolo();
            // Rimuovo il record
            amministrazioneHelper.getEntityManager().remove(parametro);
            amministrazioneHelper.getEntityManager().flush();
            if (idTipoFascicolo != null) {
                sacerLogEjb.log(param.getTransactionLogContext(), configurationHelper.getParamApplicApplicationName(),
                        param.getNomeUtente(), param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_TIPO_FASCICOLO,
                        new BigDecimal(idTipoFascicolo), param.getNomePagina());
            }
            result = true;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return result;
    }

    /**
     * Restituisce un array di object con i tablebean dei parametri
     *
     * @param idAmbiente
     *            id ambiente
     * @param idStrut
     *            id strutura
     * @param idAaTipoFascicolo
     *            id anno tipo fascicolo
     * @param funzione
     *            lista funzioni
     *
     * @return il tablebean
     *
     * @throws ParerUserError
     *             errore generico
     */
    public Object[] getAplParamApplicAaTipoFasc(BigDecimal idAmbiente, BigDecimal idStrut, BigDecimal idAaTipoFascicolo,
            List<String> funzione) throws ParerUserError {
        Object[] parametriObj = new Object[3];
        AplParamApplicTableBean paramApplicAmministrazioneTableBean = new AplParamApplicTableBean();
        AplParamApplicTableBean paramApplicGestioneTableBean = new AplParamApplicTableBean();
        AplParamApplicTableBean paramApplicConservazioneTableBean = new AplParamApplicTableBean();
        // Ricavo la lista dei parametri definiti per il PERIODO TIPO FASCICOLO
        List<AplParamApplic> paramApplicList = amministrazioneHelper.getAplParamApplicListAaTipoFascicolo(funzione);
        if (!paramApplicList.isEmpty()) {
            try {
                // Per ogni parametro, popolo i valori su applicazione, ambiente, struttura e periodo tipo fascicolo
                // ricavandoli da APL_VALORE_PARAM_APPLIC
                for (AplParamApplic paramApplic : paramApplicList) {
                    AplParamApplicRowBean paramApplicRowBean = new AplParamApplicRowBean();
                    paramApplicRowBean = (AplParamApplicRowBean) Transform.entity2RowBean(paramApplic);
                    populateParamApplicAaTipoFascicoloRowBean(paramApplicRowBean, idAmbiente, idStrut,
                            idAaTipoFascicolo, paramApplic.getTiGestioneParam());
                    switch (paramApplic.getTiGestioneParam()) {
                    case "amministrazione":
                        paramApplicAmministrazioneTableBean.add(paramApplicRowBean);
                        break;
                    case "gestione":
                        paramApplicGestioneTableBean.add(paramApplicRowBean);
                        break;
                    case "conservazione":
                        paramApplicConservazioneTableBean.add(paramApplicRowBean);
                        break;
                    default:
                        break;
                    }
                }
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                    | NoSuchMethodException | InvocationTargetException e) {
                logger.error("Errore durante il recupero dei parametri sul periodo tipo fascicolo "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new ParerUserError("Errore durante il recupero dei parametri sul periodo tipo fascicolo ");
            }
        }
        parametriObj[0] = paramApplicAmministrazioneTableBean;
        parametriObj[1] = paramApplicGestioneTableBean;
        parametriObj[2] = paramApplicConservazioneTableBean;
        return parametriObj;
    }

    /**
     * Restituisce un array di object con i tablebean dei parametri di amministrazione del periodo tipo fascicolo
     *
     * @param idAmbiente
     *            id ambiente
     * @param idStrut
     *            id struttura
     * @param idAaTipoFascicolo
     *            id periodo tipo fascicolo
     * @param funzione
     *            lista funzioni
     * @param filterValid
     *            visualizzare o meno i record parametri cessati
     *
     * @return il tablebean
     *
     * @throws ParerUserError
     *             errore generico
     */
    public AplParamApplicTableBean getAplParamApplicAmministrazioneAaTipoFasc(BigDecimal idAmbiente, BigDecimal idStrut,
            BigDecimal idAaTipoFascicolo, List<String> funzione, boolean filterValid) throws ParerUserError {
        AplParamApplicTableBean paramApplicAmministrazioneTableBean = new AplParamApplicTableBean();

        // Ricavo la lista dei parametri di amministrazione definiti per il PERIODO TIPO FASCICOLO
        List<AplParamApplic> paramApplicList = amministrazioneHelper.getAplParamApplicListAaTipoFascicolo(funzione,
                "amministrazione", filterValid);
        if (!paramApplicList.isEmpty()) {
            try {
                // Per ogni parametro, popolo i valori su applicazione, ambiente, struttura e periodo tipo fascicolo
                // ricavandoli
                // da APL_VALORE_PARAM_APPLIC
                for (AplParamApplic paramApplic : paramApplicList) {
                    AplParamApplicRowBean paramApplicRowBean = (AplParamApplicRowBean) Transform
                            .entity2RowBean(paramApplic);
                    populateParamApplicAaTipoFascicoloRowBean(paramApplicRowBean, idAmbiente, idStrut,
                            idAaTipoFascicolo, paramApplic.getTiGestioneParam());
                    paramApplicAmministrazioneTableBean.add(paramApplicRowBean);
                }
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                    | NoSuchMethodException | InvocationTargetException e) {
                logger.error("Errore durante il recupero dei parametri di amministrazione sul periodo tipo fascicolo "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new ParerUserError(
                        "Errore durante il recupero dei parametri di amministrazione sul periodo tipo fascicolo");
            }
        }
        return paramApplicAmministrazioneTableBean;
    }

    /**
     * Restituisce un array di object con i tablebean dei parametri di conservazione del periodo tipo fascicolo
     *
     * @param idAmbiente
     *            id ambiente
     * @param idStrut
     *            id struttura
     * @param idAaTipoFascicolo
     *            id periodo tipo fascicolo
     * @param funzione
     *            lista funzioni
     * @param filterValid
     *            visualizzare o meno i record parametri cessati
     *
     * @return il tablebean
     *
     * @throws ParerUserError
     *             errore generico
     */
    public AplParamApplicTableBean getAplParamApplicConservazioneAaTipoFasc(BigDecimal idAmbiente, BigDecimal idStrut,
            BigDecimal idAaTipoFascicolo, List<String> funzione, boolean filterValid) throws ParerUserError {
        AplParamApplicTableBean paramApplicConservazioneTableBean = new AplParamApplicTableBean();

        // Ricavo la lista dei parametri di conservazione definiti per il PERIODO TIPO FASCICOLO
        List<AplParamApplic> paramApplicList = amministrazioneHelper.getAplParamApplicListAaTipoFascicolo(funzione,
                "conservazione", filterValid);
        if (!paramApplicList.isEmpty()) {
            try {
                // Per ogni parametro, popolo i valori su applicazione, ambiente, struttura e periodo tipo fascicolo
                // ricavandoli
                // da APL_VALORE_PARAM_APPLIC
                for (AplParamApplic paramApplic : paramApplicList) {
                    AplParamApplicRowBean paramApplicRowBean = (AplParamApplicRowBean) Transform
                            .entity2RowBean(paramApplic);
                    populateParamApplicAaTipoFascicoloRowBean(paramApplicRowBean, idAmbiente, idStrut,
                            idAaTipoFascicolo, paramApplic.getTiGestioneParam());
                    paramApplicConservazioneTableBean.add(paramApplicRowBean);
                }
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                    | NoSuchMethodException | InvocationTargetException e) {
                logger.error("Errore durante il recupero dei parametri di conservazione sul periodo tipo fascicolo "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new ParerUserError(
                        "Errore durante il recupero dei parametri di conservazione sul periodo tipo fascicolo ");
            }
        }
        return paramApplicConservazioneTableBean;
    }

    /**
     * Restituisce il tablebean dei parametri di gestione del periodo tipo fascicolo
     *
     * @param idAmbiente
     *            id ambiente
     * @param idStrut
     *            id struttura
     * @param idAaTipoFascicolo
     *            id periodo tipo fascicolo
     * @param funzione
     *            lista funzioni
     * @param filterValid
     *            visualizzare o meno i record parametri cessati
     *
     * @return il tablebean
     *
     * @throws ParerUserError
     *             errore generico
     */
    public AplParamApplicTableBean getAplParamApplicGestioneAaTipoFasc(BigDecimal idAmbiente, BigDecimal idStrut,
            BigDecimal idAaTipoFascicolo, List<String> funzione, boolean filterValid) throws ParerUserError {
        AplParamApplicTableBean paramApplicGestioneTableBean = new AplParamApplicTableBean();

        // Ricavo la lista dei parametri di gestione definiti per il PERIODO TIPO FASCICOLO
        List<AplParamApplic> paramApplicList = amministrazioneHelper.getAplParamApplicListAaTipoFascicolo(funzione,
                "gestione", filterValid);
        if (!paramApplicList.isEmpty()) {
            try {
                // Per ogni parametro, popolo i valori su applicazione, ambiente, struttura e periodo tipo fascicolo
                // ricavandoli
                // da APL_VALORE_PARAM_APPLIC
                for (AplParamApplic paramApplic : paramApplicList) {
                    AplParamApplicRowBean paramApplicRowBean = (AplParamApplicRowBean) Transform
                            .entity2RowBean(paramApplic);
                    populateParamApplicAaTipoFascicoloRowBean(paramApplicRowBean, idAmbiente, idStrut,
                            idAaTipoFascicolo, paramApplic.getTiGestioneParam());
                    paramApplicGestioneTableBean.add(paramApplicRowBean);
                }
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                    | NoSuchMethodException | InvocationTargetException e) {
                logger.error("Errore durante il recupero dei parametri di gestione sul periodo tipo fascicolo "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new ParerUserError(
                        "Errore durante il recupero dei parametri di gestione sul periodo tipo fascicolo ");
            }
        }
        return paramApplicGestioneTableBean;
    }

    private void populateParamApplicAaTipoFascicoloRowBean(AplParamApplicRowBean paramApplicRowBean,
            BigDecimal idAmbiente, BigDecimal idStrut, BigDecimal idAaTipoFascicolo, String tiGestioneParam) {
        String nomeCampoTipoUd = tiGestioneParam.equals("amministrazione")
                ? "ds_valore_param_applic_aa_tipo_fascicolo_amm"
                : tiGestioneParam.equals("gestione") ? "ds_valore_param_applic_aa_tipo_fascicolo_gest"
                        : "ds_valore_param_applic_aa_tipo_fascicolo_cons";

        // Determino i valori su applicazione, ambiente, struttura e periodo tipo fascicolo
        AplValoreParamApplic valoreParamApplicApplic = amministrazioneHelper.getAplValoreParamApplic(
                paramApplicRowBean.getIdParamApplic(), TiAppart.APPLIC.name(), null, null, null, null);
        if (valoreParamApplicApplic != null) {
            paramApplicRowBean.setString("ds_valore_param_applic_applic",
                    valoreParamApplicApplic.getDsValoreParamApplic());
        }

        if (idAmbiente != null) {
            AplValoreParamApplic valoreParamApplicAmbiente = amministrazioneHelper.getAplValoreParamApplic(
                    paramApplicRowBean.getIdParamApplic(), TiAppart.AMBIENTE.name(), idAmbiente, null, null, null);
            if (valoreParamApplicAmbiente != null) {
                paramApplicRowBean.setString("ds_valore_param_applic_ambiente",
                        valoreParamApplicAmbiente.getDsValoreParamApplic());
            }
        }

        if (idStrut != null) {
            AplValoreParamApplic valoreParamApplicAmbiente = amministrazioneHelper.getAplValoreParamApplic(
                    paramApplicRowBean.getIdParamApplic(), TiAppart.STRUT.name(), null, idStrut, null, null);
            if (valoreParamApplicAmbiente != null) {
                paramApplicRowBean.setString("ds_valore_param_applic_strut",
                        valoreParamApplicAmbiente.getDsValoreParamApplic());
            }
        }

        if (idAaTipoFascicolo != null) {
            AplValoreParamApplic valoreParamApplicStrut = amministrazioneHelper.getAplValoreParamApplic(
                    paramApplicRowBean.getIdParamApplic(), TiAppart.PERIODO_TIPO_FASC.name(), null, null, null,
                    idAaTipoFascicolo);
            if (valoreParamApplicStrut != null) {
                paramApplicRowBean.setString(nomeCampoTipoUd, valoreParamApplicStrut.getDsValoreParamApplic());
                paramApplicRowBean.setBigDecimal("id_valore_param_applic",
                        BigDecimal.valueOf(valoreParamApplicStrut.getIdValoreParamApplic()));
            }
        }
    }

    /**
     * Rimuove la riga di parametri definita nel rowBean
     *
     * @param param
     *            parametri per il logging
     * @param idValoreParamApplic
     *            id valore parametro applicativo
     *
     * @return true se eliminato con successo
     */
    public boolean deleteParametroTipoUd(LogParam param, BigDecimal idValoreParamApplic) {
        AplValoreParamApplic parametro;
        boolean result = false;
        try {
            parametro = (AplValoreParamApplic) amministrazioneHelper.findById(AplValoreParamApplic.class,
                    idValoreParamApplic);
            Long idTipoUnitaDoc = parametro.getDecTipoUnitaDoc().getIdTipoUnitaDoc();
            // Rimuovo il record
            amministrazioneHelper.getEntityManager().remove(parametro);
            amministrazioneHelper.getEntityManager().flush();
            if (idTipoUnitaDoc != null) {
                sacerLogEjb.log(param.getTransactionLogContext(), configurationHelper.getParamApplicApplicationName(),
                        param.getNomeUtente(), param.getNomeAzione(),
                        SacerLogConstants.TIPO_OGGETTO_TIPO_UNITA_DOCUMENTARIA, new BigDecimal(idTipoUnitaDoc),
                        param.getNomePagina());
            }
            result = true;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return result;
    }

    /**
     * Restituisce un array di object con i tablebean dei parametri
     *
     * @param idAmbiente
     *            id ambiente
     * @param idStrut
     *            id struttura
     * @param idTipoUnitaDoc
     *            id tipo unita doc
     * @param funzione
     *            lista funzioni
     *
     * @return il tablebean
     *
     * @throws ParerUserError
     *             errore generico
     */
    public Object[] getAplParamApplicTipoUd(BigDecimal idAmbiente, BigDecimal idStrut, BigDecimal idTipoUnitaDoc,
            List<String> funzione) throws ParerUserError {
        Object[] parametriObj = new Object[3];
        AplParamApplicTableBean paramApplicAmministrazioneTableBean = new AplParamApplicTableBean();
        AplParamApplicTableBean paramApplicGestioneTableBean = new AplParamApplicTableBean();
        AplParamApplicTableBean paramApplicConservazioneTableBean = new AplParamApplicTableBean();
        // Ricavo la lista dei parametri definiti per il tipo unità documentaria
        List<AplParamApplic> paramApplicList = amministrazioneHelper.getAplParamApplicListTipoUd(funzione);
        if (!paramApplicList.isEmpty()) {
            try {
                // Per ogni parametro, popolo i valori su applicazione, ambiente, struttura e tipo ud ricavandoli da
                // APL_VALORE_PARAM_APPLIC
                for (AplParamApplic paramApplic : paramApplicList) {
                    AplParamApplicRowBean paramApplicRowBean = new AplParamApplicRowBean();
                    paramApplicRowBean = (AplParamApplicRowBean) Transform.entity2RowBean(paramApplic);
                    populateParamApplicTipoUdRowBean(paramApplicRowBean, idAmbiente, idStrut, idTipoUnitaDoc,
                            paramApplic.getTiGestioneParam());
                    switch (paramApplic.getTiGestioneParam()) {
                    case "amministrazione":
                        paramApplicAmministrazioneTableBean.add(paramApplicRowBean);
                        break;
                    case "gestione":
                        paramApplicGestioneTableBean.add(paramApplicRowBean);
                        break;
                    case "conservazione":
                        paramApplicConservazioneTableBean.add(paramApplicRowBean);
                        break;
                    default:
                        break;
                    }
                }
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                    | NoSuchMethodException | InvocationTargetException e) {
                logger.error("Errore durante il recupero dei parametri sul tipo unità documentaria "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new ParerUserError("Errore durante il recupero dei parametri sul tipo unità documentaria ");
            }
        }
        parametriObj[0] = paramApplicAmministrazioneTableBean;
        parametriObj[1] = paramApplicGestioneTableBean;
        parametriObj[2] = paramApplicConservazioneTableBean;
        return parametriObj;
    }

    /**
     * Restituisce un array di object con i tablebean dei parametri di amministrazione del tipo ud
     *
     * @param idAmbiente
     *            id ambiente
     * @param idStrut
     *            id struttura
     * @param idTipoUnitaDoc
     *            id tipo unita doc
     * @param funzione
     *            lista funzioni
     * @param filterValid
     *            visualizzare o meno i record parametri cessati
     *
     * @return il tablebean
     *
     * @throws ParerUserError
     *             errore generico
     */
    public AplParamApplicTableBean getAplParamApplicAmministrazioneTipoUd(BigDecimal idAmbiente, BigDecimal idStrut,
            BigDecimal idTipoUnitaDoc, List<String> funzione, boolean filterValid) throws ParerUserError {
        AplParamApplicTableBean paramApplicAmministrazioneTableBean = new AplParamApplicTableBean();

        // Ricavo la lista dei parametri di amministrazione definiti per il TIPO UD
        List<AplParamApplic> paramApplicList = amministrazioneHelper.getAplParamApplicListTipoUd(funzione,
                "amministrazione", filterValid);
        if (!paramApplicList.isEmpty()) {
            try {
                // Per ogni parametro, popolo i valori su applicazione, ambiente, struttura e tipo unità doc ricavandoli
                // da
                // APL_VALORE_PARAM_APPLIC
                for (AplParamApplic paramApplic : paramApplicList) {
                    AplParamApplicRowBean paramApplicRowBean = (AplParamApplicRowBean) Transform
                            .entity2RowBean(paramApplic);
                    populateParamApplicTipoUdRowBean(paramApplicRowBean, idAmbiente, idStrut, idTipoUnitaDoc,
                            paramApplic.getTiGestioneParam());
                    paramApplicAmministrazioneTableBean.add(paramApplicRowBean);
                }
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                    | NoSuchMethodException | InvocationTargetException e) {
                logger.error("Errore durante il recupero dei parametri di amministrazione sul tipo ud"
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new ParerUserError("Errore durante il recupero dei parametri di amministrazione sul tipo ud ");
            }
        }
        return paramApplicAmministrazioneTableBean;
    }

    /**
     * Restituisce un array di object con i tablebean dei parametri di conservazione del tipo ud
     *
     * @param idAmbiente
     *            id ambiente
     * @param idStrut
     *            id struttura
     * @param idTipoUnitaDoc
     *            id tipo unita doc
     * @param funzione
     *            lista funzioni
     * @param filterValid
     *            visualizzare o meno i record parametri cessati
     *
     * @return il tablebean
     *
     * @throws ParerUserError
     *             errore generico
     */
    public AplParamApplicTableBean getAplParamApplicConservazioneTipoUd(BigDecimal idAmbiente, BigDecimal idStrut,
            BigDecimal idTipoUnitaDoc, List<String> funzione, boolean filterValid) throws ParerUserError {
        AplParamApplicTableBean paramApplicConservazioneTableBean = new AplParamApplicTableBean();

        // Ricavo la lista dei parametri di conservazione definiti per il TIPO UD
        List<AplParamApplic> paramApplicList = amministrazioneHelper.getAplParamApplicListTipoUd(funzione,
                "conservazione", filterValid);
        if (!paramApplicList.isEmpty()) {
            try {
                // Per ogni parametro, popolo i valori su applicazione, ambiente, struttura e tipo unità doc ricavandoli
                // da
                // APL_VALORE_PARAM_APPLIC
                for (AplParamApplic paramApplic : paramApplicList) {
                    AplParamApplicRowBean paramApplicRowBean = (AplParamApplicRowBean) Transform
                            .entity2RowBean(paramApplic);
                    populateParamApplicTipoUdRowBean(paramApplicRowBean, idAmbiente, idStrut, idTipoUnitaDoc,
                            paramApplic.getTiGestioneParam());
                    paramApplicConservazioneTableBean.add(paramApplicRowBean);
                }
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                    | NoSuchMethodException | InvocationTargetException e) {
                logger.error("Errore durante il recupero dei parametri di conservazione sul tipo ud"
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new ParerUserError("Errore durante il recupero dei parametri di conservazione sul tipo ud ");
            }
        }
        return paramApplicConservazioneTableBean;
    }

    /**
     * Restituisce il tablebean dei parametri di gestione del tipo ud
     *
     * @param idAmbiente
     *            id ambiente
     * @param idStrut
     *            id struttura
     * @param idTipoUnitaDoc
     *            id tipo unita doc
     * @param funzione
     *            lista funzioni
     * @param filterValid
     *            visualizzare o meno i record parametri cessati
     *
     * @return il tablebean
     *
     * @throws ParerUserError
     *             errore generico
     */
    public AplParamApplicTableBean getAplParamApplicGestioneTipoUd(BigDecimal idAmbiente, BigDecimal idStrut,
            BigDecimal idTipoUnitaDoc, List<String> funzione, boolean filterValid) throws ParerUserError {
        AplParamApplicTableBean paramApplicGestioneTableBean = new AplParamApplicTableBean();

        // Ricavo la lista dei parametri di gestione definiti per il TIPO UD
        List<AplParamApplic> paramApplicList = amministrazioneHelper.getAplParamApplicListTipoUd(funzione, "gestione",
                filterValid);
        if (!paramApplicList.isEmpty()) {
            try {
                // Per ogni parametro, popolo i valori su applicazione, ambiente, struttura e tipo unità doc ricavandoli
                // da
                // APL_VALORE_PARAM_APPLIC
                for (AplParamApplic paramApplic : paramApplicList) {
                    AplParamApplicRowBean paramApplicRowBean = (AplParamApplicRowBean) Transform
                            .entity2RowBean(paramApplic);
                    populateParamApplicTipoUdRowBean(paramApplicRowBean, idAmbiente, idStrut, idTipoUnitaDoc,
                            paramApplic.getTiGestioneParam());
                    paramApplicGestioneTableBean.add(paramApplicRowBean);
                }
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                    | NoSuchMethodException | InvocationTargetException e) {
                logger.error("Errore durante il recupero dei parametri di gestione sul tipo ud"
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new ParerUserError("Errore durante il recupero dei parametri di gestione sul tipo ud ");
            }
        }
        return paramApplicGestioneTableBean;
    }

    private void populateParamApplicTipoUdRowBean(AplParamApplicRowBean paramApplicRowBean, BigDecimal idAmbiente,
            BigDecimal idStrut, BigDecimal idTipoUnitaDoc, String tiGestioneParam) {
        String nomeCampoTipoUd = tiGestioneParam.equals("amministrazione") ? "ds_valore_param_applic_tipo_ud_amm"
                : tiGestioneParam.equals("gestione") ? "ds_valore_param_applic_tipo_ud_gest"
                        : "ds_valore_param_applic_tipo_ud_cons";

        // Determino i valori su applicazione, ambiente, struttura e tipo unità doc
        AplValoreParamApplic valoreParamApplicApplic = amministrazioneHelper.getAplValoreParamApplic(
                paramApplicRowBean.getIdParamApplic(), TiAppart.APPLIC.name(), null, null, null, null);
        if (valoreParamApplicApplic != null) {
            paramApplicRowBean.setString("ds_valore_param_applic_applic",
                    valoreParamApplicApplic.getDsValoreParamApplic());
        }

        if (idAmbiente != null) {
            AplValoreParamApplic valoreParamApplicAmbiente = amministrazioneHelper.getAplValoreParamApplic(
                    paramApplicRowBean.getIdParamApplic(), TiAppart.AMBIENTE.name(), idAmbiente, null, null, null);
            if (valoreParamApplicAmbiente != null) {
                paramApplicRowBean.setString("ds_valore_param_applic_ambiente",
                        valoreParamApplicAmbiente.getDsValoreParamApplic());
            }
        }

        if (idStrut != null) {
            AplValoreParamApplic valoreParamApplicAmbiente = amministrazioneHelper.getAplValoreParamApplic(
                    paramApplicRowBean.getIdParamApplic(), TiAppart.STRUT.name(), null, idStrut, null, null);
            if (valoreParamApplicAmbiente != null) {
                paramApplicRowBean.setString("ds_valore_param_applic_strut",
                        valoreParamApplicAmbiente.getDsValoreParamApplic());
            }
        }

        if (idTipoUnitaDoc != null) {
            AplValoreParamApplic valoreParamApplicStrut = amministrazioneHelper.getAplValoreParamApplic(
                    paramApplicRowBean.getIdParamApplic(), TiAppart.TIPO_UNITA_DOC.name(), null, null, idTipoUnitaDoc,
                    null);
            if (valoreParamApplicStrut != null) {
                paramApplicRowBean.setString(nomeCampoTipoUd, valoreParamApplicStrut.getDsValoreParamApplic());
                paramApplicRowBean.setBigDecimal("id_valore_param_applic",
                        BigDecimal.valueOf(valoreParamApplicStrut.getIdValoreParamApplic()));
            }
        }
    }

    /**
     * Rimuove la riga di parametri definita nel rowBean
     *
     * @param param
     *            parametri per il logging
     * @param idValoreParamApplic
     *            id valore parametro applicativo
     *
     * @return true se eliminato con successo
     */
    public boolean deleteParametroStruttura(LogParam param, BigDecimal idValoreParamApplic) {
        AplValoreParamApplic parametro;
        boolean result = false;
        try {
            parametro = (AplValoreParamApplic) amministrazioneHelper.findById(AplValoreParamApplic.class,
                    idValoreParamApplic);
            Long idStrut = parametro.getOrgStrut().getIdStrut();
            // Rimuovo il record
            amministrazioneHelper.getEntityManager().remove(parametro);
            amministrazioneHelper.getEntityManager().flush();
            if (idStrut != null) {
                sacerLogEjb.log(param.getTransactionLogContext(), configurationHelper.getParamApplicApplicationName(),
                        param.getNomeUtente(), param.getNomeAzione(), SacerLogConstants.TIPO_OGGETTO_STRUTTURA,
                        new BigDecimal(idStrut), param.getNomePagina());
            }
            result = true;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        return result;
    }

    /**
     * Restituisce un array di object con i tablebean dei parametri
     *
     * @param idAmbiente
     *            id ambiente
     * @param idStrut
     *            id struttura
     * @param funzione
     *            lista funzioni
     * @param filterValid
     *            visualizzare o meno i record parametri cessati
     *
     * @return il tablebean
     *
     * @throws ParerUserError
     *             errore generico
     */
    public Object[] getAplParamApplicStruttura(BigDecimal idAmbiente, BigDecimal idStrut, List<String> funzione,
            boolean filterValid) throws ParerUserError {
        Object[] parametriObj = new Object[3];
        AplParamApplicTableBean paramApplicAmministrazioneTableBean = new AplParamApplicTableBean();
        AplParamApplicTableBean paramApplicGestioneTableBean = new AplParamApplicTableBean();
        AplParamApplicTableBean paramApplicConservazioneTableBean = new AplParamApplicTableBean();
        // Ricavo la lista dei parametri definiti per la STRUTTURA
        List<AplParamApplic> paramApplicList = amministrazioneHelper.getAplParamApplicListStruttura(funzione,
                filterValid);
        if (!paramApplicList.isEmpty()) {
            try {
                // Per ogni parametro, popolo i valori su applicazione, ambiente e struttura ricavandoli da
                // APL_VALORE_PARAM_APPLIC
                for (AplParamApplic paramApplic : paramApplicList) {
                    AplParamApplicRowBean paramApplicRowBean = (AplParamApplicRowBean) Transform
                            .entity2RowBean(paramApplic);
                    populateParamApplicStrutturaRowBean(paramApplicRowBean, idAmbiente, idStrut,
                            paramApplic.getTiGestioneParam());
                    switch (paramApplic.getTiGestioneParam()) {
                    case "amministrazione":
                        paramApplicAmministrazioneTableBean.add(paramApplicRowBean);
                        break;
                    case "gestione":
                        paramApplicGestioneTableBean.add(paramApplicRowBean);
                        break;
                    case "conservazione":
                        paramApplicConservazioneTableBean.add(paramApplicRowBean);
                        break;
                    default:
                        break;
                    }
                }
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                    | NoSuchMethodException | InvocationTargetException e) {
                logger.error("Errore durante il recupero dei parametri sulla struttura "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new ParerUserError("Errore durante il recupero dei parametri sulla struttura ");
            }
        }
        parametriObj[0] = paramApplicAmministrazioneTableBean;
        parametriObj[1] = paramApplicGestioneTableBean;
        parametriObj[2] = paramApplicConservazioneTableBean;
        return parametriObj;
    }

    /**
     * Restituisce un array di object con i tablebean dei parametri di amministrazione della struttura
     *
     * @param idAmbiente
     *            id ambiente
     * @param idStrut
     *            id struttura
     * @param funzione
     *            lista funzioni
     * @param filterValid
     *            visualizzare o meno i record parametri cessati
     *
     * @return il tablebean
     *
     * @throws ParerUserError
     *             errore generico
     */
    public AplParamApplicTableBean getAplParamApplicAmministrazioneStruttura(BigDecimal idAmbiente, BigDecimal idStrut,
            List<String> funzione, boolean filterValid) throws ParerUserError {
        AplParamApplicTableBean paramApplicAmministrazioneTableBean = new AplParamApplicTableBean();

        // Ricavo la lista dei parametri di amministrazione definiti per la STRUTTURA
        List<AplParamApplic> paramApplicList = amministrazioneHelper.getAplParamApplicListStruttura(funzione,
                "amministrazione", filterValid);
        if (!paramApplicList.isEmpty()) {
            try {
                // Per ogni parametro, popolo i valori su applicazione, ambiente e struttura ricavandoli da
                // APL_VALORE_PARAM_APPLIC
                for (AplParamApplic paramApplic : paramApplicList) {
                    AplParamApplicRowBean paramApplicRowBean = (AplParamApplicRowBean) Transform
                            .entity2RowBean(paramApplic);
                    populateParamApplicStrutturaRowBean(paramApplicRowBean, idAmbiente, idStrut,
                            paramApplic.getTiGestioneParam());
                    paramApplicAmministrazioneTableBean.add(paramApplicRowBean);
                }
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                    | NoSuchMethodException | InvocationTargetException e) {
                logger.error("Errore durante il recupero dei parametri di amministrazione sulla struttura "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new ParerUserError(
                        "Errore durante il recupero dei parametri di amministrazione sulla struttura ");
            }
        }
        return paramApplicAmministrazioneTableBean;
    }

    /**
     * Restituisce un array di object con i tablebean dei parametri di gestione della struttura
     *
     * @param idAmbiente
     *            id ambiente
     * @param idStrut
     *            id struttura
     * @param funzione
     *            lista funzioni
     * @param filterValid
     *            visualizzare o meno i record parametri cessati
     *
     * @return il tablebean
     *
     * @throws ParerUserError
     *             errore generico
     */
    public AplParamApplicTableBean getAplParamApplicGestioneStruttura(BigDecimal idAmbiente, BigDecimal idStrut,
            List<String> funzione, boolean filterValid) throws ParerUserError {
        AplParamApplicTableBean paramApplicGestioneTableBean = new AplParamApplicTableBean();

        // Ricavo la lista dei parametri di amministrazione definiti per la STRUTTURA
        List<AplParamApplic> paramApplicList = amministrazioneHelper.getAplParamApplicListStruttura(funzione,
                "gestione", filterValid);
        if (!paramApplicList.isEmpty()) {
            try {
                // Per ogni parametro, popolo i valori su applicazione, ambiente e struttura ricavandoli da
                // APL_VALORE_PARAM_APPLIC
                for (AplParamApplic paramApplic : paramApplicList) {
                    AplParamApplicRowBean paramApplicRowBean = (AplParamApplicRowBean) Transform
                            .entity2RowBean(paramApplic);
                    populateParamApplicStrutturaRowBean(paramApplicRowBean, idAmbiente, idStrut,
                            paramApplic.getTiGestioneParam());
                    paramApplicGestioneTableBean.add(paramApplicRowBean);
                }
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                    | NoSuchMethodException | InvocationTargetException e) {
                logger.error("Errore durante il recupero dei parametri di gestione sulla struttura "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new ParerUserError("Errore durante il recupero dei parametri di gestione sulla struttura ");
            }
        }
        return paramApplicGestioneTableBean;
    }

    /**
     * Restituisce un array di object con i tablebean dei parametri
     *
     * @param idAmbiente
     *            id ambiente
     * @param idStrut
     *            id struttura
     * @param funzione
     *            lista funzioni
     * @param filterValid
     *            visualizzare o meno i record parametri cessati
     *
     * @return il tablebean
     *
     * @throws ParerUserError
     *             errore generico
     */
    public AplParamApplicTableBean getAplParamApplicConservazioneStruttura(BigDecimal idAmbiente, BigDecimal idStrut,
            List<String> funzione, boolean filterValid) throws ParerUserError {
        AplParamApplicTableBean paramApplicConservazioneTableBean = new AplParamApplicTableBean();

        // Ricavo la lista dei parametri di amministrazione definiti per la STRUTTURA
        List<AplParamApplic> paramApplicList = amministrazioneHelper.getAplParamApplicListStruttura(funzione,
                "conservazione", filterValid);
        if (!paramApplicList.isEmpty()) {
            try {
                // Per ogni parametro, popolo i valori su applicazione, ambiente e struttura ricavandoli da
                // APL_VALORE_PARAM_APPLIC
                for (AplParamApplic paramApplic : paramApplicList) {
                    AplParamApplicRowBean paramApplicRowBean = new AplParamApplicRowBean();
                    paramApplicRowBean = (AplParamApplicRowBean) Transform.entity2RowBean(paramApplic);
                    populateParamApplicStrutturaRowBean(paramApplicRowBean, idAmbiente, idStrut,
                            paramApplic.getTiGestioneParam());
                    paramApplicConservazioneTableBean.add(paramApplicRowBean);
                }
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                    | NoSuchMethodException | InvocationTargetException e) {
                logger.error("Errore durante il recupero dei parametri di conservazione sulla struttura "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new ParerUserError("Errore durante il recupero dei parametri di conservazione sulla struttura ");
            }
        }
        return paramApplicConservazioneTableBean;
    }

    /*
     * public Object[] getAplParamApplicRowBeansFromStruttura(UUID uuid) throws ParerUserError { OrgStrut
     * strut=strutCache.getOrgStrut(uuid); return getAplParamApplicRowBeansFromStruttura(strut); }
     */
    public Object[] getAplParamApplicRowBeansFromStruttura(OrgStrutRowBean strutRowBean) throws ParerUserError {
        OrgStrut strut = amministrazioneHelper.findById(OrgStrut.class, strutRowBean.getIdStrut());
        return getAplParamApplicRowBeansFromStrutturaEntity(strut);
    }

    public Object[] getAplParamApplicRowBeansFromStrutturaImportata(OrgStrut struttura) throws ParerUserError {
        return getAplParamApplicRowBeansFromStrutturaEntity(struttura);
    }

    private Object[] getAplParamApplicRowBeansFromStrutturaEntity(OrgStrut struttura) throws ParerUserError {
        Object[] parametriObj = new Object[3];
        AplParamApplicTableBean paramApplicAmministrazioneTableBean = new AplParamApplicTableBean();
        AplParamApplicTableBean paramApplicGestioneTableBean = new AplParamApplicTableBean();
        AplParamApplicTableBean paramApplicConservazioneTableBean = new AplParamApplicTableBean();
        // Ricavo la lista dei parametri definiti per la STRUTTURA
        List<AplValoreParamApplic> paramApplicList = struttura.getAplValoreParamApplics();
        if (!paramApplicList.isEmpty()) {
            try {
                for (AplValoreParamApplic aplValoreParamApplic : paramApplicList) {
                    AplParamApplicRowBean paramApplicRowBean = new AplParamApplicRowBean();
                    paramApplicRowBean = (AplParamApplicRowBean) Transform
                            .entity2RowBean(aplValoreParamApplic.getAplParamApplic());
                    if (aplValoreParamApplic.getAplParamApplic().getTiGestioneParam().equals("amministrazione")) {
                        paramApplicRowBean.setString("ds_valore_param_applic_strut_amm",
                                aplValoreParamApplic.getDsValoreParamApplic());
                        paramApplicAmministrazioneTableBean.add(paramApplicRowBean);
                    } else if (aplValoreParamApplic.getAplParamApplic().getTiGestioneParam().equals("gestione")) {
                        paramApplicRowBean.setString("ds_valore_param_applic_strut_gest",
                                aplValoreParamApplic.getDsValoreParamApplic());
                        paramApplicGestioneTableBean.add(paramApplicRowBean);
                    } else {
                        paramApplicRowBean.setString("ds_valore_param_applic_strut_cons",
                                aplValoreParamApplic.getDsValoreParamApplic());
                        paramApplicConservazioneTableBean.add(paramApplicRowBean);
                    }
                }
            } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InstantiationException
                    | NoSuchMethodException | InvocationTargetException e) {
                logger.error("Errore durante il recupero dei parametri sulla struttura "
                        + ExceptionUtils.getRootCauseMessage(e), e);
                throw new ParerUserError("Errore durante il recupero dei parametri sulla struttura ");
            }

        }
        parametriObj[0] = paramApplicAmministrazioneTableBean;
        parametriObj[1] = paramApplicGestioneTableBean;
        parametriObj[2] = paramApplicConservazioneTableBean;
        return parametriObj;
    }

    private void populateParamApplicStrutturaRowBean(AplParamApplicRowBean paramApplicRowBean, BigDecimal idAmbiente,
            BigDecimal idStrut, String tiGestioneParam) {
        String nomeCampoStruttura = tiGestioneParam.equals("amministrazione") ? "ds_valore_param_applic_strut_amm"
                : tiGestioneParam.equals("gestione") ? "ds_valore_param_applic_strut_gest"
                        : "ds_valore_param_applic_strut_cons";

        // Determino i valori su applicazione, ambiente e struttura
        AplValoreParamApplic valoreParamApplicApplic = amministrazioneHelper.getAplValoreParamApplic(
                paramApplicRowBean.getIdParamApplic(), TiAppart.APPLIC.name(), null, null, null, null);
        if (valoreParamApplicApplic != null) {
            paramApplicRowBean.setString("ds_valore_param_applic_applic",
                    valoreParamApplicApplic.getDsValoreParamApplic());
        }

        if (idAmbiente != null) {
            AplValoreParamApplic valoreParamApplicAmbiente = amministrazioneHelper.getAplValoreParamApplic(
                    paramApplicRowBean.getIdParamApplic(), TiAppart.AMBIENTE.name(), idAmbiente, null, null, null);
            if (valoreParamApplicAmbiente != null) {
                paramApplicRowBean.setString("ds_valore_param_applic_ambiente",
                        valoreParamApplicAmbiente.getDsValoreParamApplic());
            }
        }

        if (idStrut != null) {
            AplValoreParamApplic valoreParamApplicStrut = amministrazioneHelper.getAplValoreParamApplic(
                    paramApplicRowBean.getIdParamApplic(), TiAppart.STRUT.name(), null, idStrut, null, null);
            if (valoreParamApplicStrut != null) {
                paramApplicRowBean.setString(nomeCampoStruttura, valoreParamApplicStrut.getDsValoreParamApplic());
                paramApplicRowBean.setBigDecimal("id_valore_param_applic",
                        BigDecimal.valueOf(valoreParamApplicStrut.getIdValoreParamApplic()));
            }
        }
    }

    public void insertAplValParamApplicMulti(BigDecimal idParamApplic, OrgAmbiente ambiente,
            String dsValoreParamApplic) {
        AplValParamApplicMulti valoreParamApplicMulti = new AplValParamApplicMulti();
        valoreParamApplicMulti.setAplParamApplic(amministrazioneHelper.findById(AplParamApplic.class, idParamApplic));
        valoreParamApplicMulti.setDsValoreParamApplic(dsValoreParamApplic);
        valoreParamApplicMulti.setOrgAmbiente(ambiente);
        amministrazioneHelper.insertEntity(valoreParamApplicMulti, true);
    }

    /**
     * Restituisce lo user HSM associato all'utente passato in ingresso per l'ambiente specificato
     *
     * @param idUserIamCor
     *            id user Iam corrente
     * @param idAmbiente
     *            id ambiente
     *
     * @return String utente
     */
    public String getHsmUsername(long idUserIamCor, BigDecimal idAmbiente) {
        AplParamApplic paramApplic = configurationHelper.getParamApplic(CostantiDB.ParametroAppl.HSM_USERNAME);
        UsrUser user = amministrazioneHelper.findById(UsrUser.class, idUserIamCor);
        String hsmUser = null;
        // Ricavo le coppie di valori multipli del parametro HASM_USERNAME sull'ambiente
        List<AplValParamApplicMulti> listaCoppieValoriHsmUsername = amministrazioneHelper
                .getAplValParamApplicMultiList(BigDecimal.valueOf(paramApplic.getIdParamApplic()), idAmbiente);
        Map<String, String> mappaUserHsm = new HashMap<>();
        for (AplValParamApplicMulti valParamApplicMulti : listaCoppieValoriHsmUsername) {
            // Splitto la coppia: il primo valore sarà lo user applicativo, il secondo lo user hsm
            String[] chiaveValore = valParamApplicMulti.getDsValoreParamApplic().split(",");
            mappaUserHsm.put(chiaveValore[0], chiaveValore[1]);
        }
        // Controllo se l'utente corrente è presente come user applicativo in una delle coppie
        Iterator<Entry<String, String>> it = mappaUserHsm.entrySet().iterator();
        while (it.hasNext()) {
            Entry<String, String> entry = it.next();
            String chiave = entry.getKey();
            if (chiave.equals(user.getNmUserid())) {
                hsmUser = entry.getValue();
                break;
            }
        }
        return hsmUser;
    }

    public ElencoEnums.TipoFirma getTipoFirmaPerStruttura(BigDecimal idStrut) {
        BigDecimal idAmbiente = new BigDecimal(
                amministrazioneHelper.findById(OrgStrut.class, idStrut).getOrgEnte().getOrgAmbiente().getIdAmbiente());
        AplParamApplic paramApplic = configurationHelper.getParamApplic(CostantiDB.ParametroAppl.TIPO_FIRMA);
        AplValoreParamApplic valore = amministrazioneHelper.getAplValoreParamApplic(
                new BigDecimal(paramApplic.getIdParamApplic()), TiAppart.AMBIENTE.name(), idAmbiente, null, null, null);
        if (valore == null) {
            valore = amministrazioneHelper.getAplValoreParamApplic(new BigDecimal(paramApplic.getIdParamApplic()),
                    TiAppart.APPLIC.name(), null, null, null, null);
        }
        return valore == null ? null : ElencoEnums.TipoFirma.valueOf(valore.getDsValoreParamApplic());
    }

    public it.eng.parer.elencoVersamento.utils.ElencoEnums.TipoFirma getTipoFirmaPerAmbiente(BigDecimal idAmbiente) {
        AplParamApplic paramApplic = configurationHelper.getParamApplic(CostantiDB.ParametroAppl.TIPO_FIRMA);
        AplValoreParamApplic valore = amministrazioneHelper.getAplValoreParamApplic(
                new BigDecimal(paramApplic.getIdParamApplic()), TiAppart.AMBIENTE.name(), idAmbiente, null, null, null);
        if (valore == null) {
            valore = amministrazioneHelper.getAplValoreParamApplic(new BigDecimal(paramApplic.getIdParamApplic()),
                    TiAppart.APPLIC.name(), null, null, null, null);
        }
        return valore == null ? null
                : it.eng.parer.elencoVersamento.utils.ElencoEnums.TipoFirma.valueOf(valore.getDsValoreParamApplic());
    }
}
