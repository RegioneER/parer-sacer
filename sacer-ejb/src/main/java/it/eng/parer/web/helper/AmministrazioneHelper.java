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

package it.eng.parer.web.helper;

import static it.eng.parer.util.Utils.longFromBigDecimal;

import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.AplParamApplic;
import it.eng.parer.entity.AplValParamApplicMulti;
import it.eng.parer.entity.AplValoreParamApplic;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.sacer.util.SacerLogConstants;
import it.eng.parer.sacerlog.ejb.SacerLogEjb;
import it.eng.parer.sacerlog.util.LogParam;
import it.eng.parer.slite.gen.tablebean.AplParamApplicRowBean;
import it.eng.parer.slite.gen.tablebean.AplParamApplicTableBean;
import it.eng.parer.slite.gen.tablebean.AplParamApplicTableDescriptor;
import it.eng.parer.web.util.Transform;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;

/**
 * Session Bean implementation class AmministrazioneHelper. Contiene i metodi per la gestione della
 * persistenza su DB per le operazioni CRUD sui parametri applicativi.
 *
 */
@SuppressWarnings("unchecked")
@Stateless
@LocalBean
public class AmministrazioneHelper extends GenericHelper {

    public AmministrazioneHelper() {
        /* Default constructor */
    }

    private static final Logger log = LoggerFactory
            .getLogger(AmministrazioneHelper.class.getName());

    @EJB
    private SacerLogEjb sacerLogEjb;

    /**
     * Metodo che ritorna i tipi di parametri di configurazione dell'applicazione
     *
     * @return il tablebean contenente la lista di tipi parametri di configurazione
     */
    public BaseTable getConfigurationTypes() {
        String queryStr = "SELECT DISTINCT config.tiParamApplic FROM AplParamApplic config ";
        Query q = getEntityManager().createQuery(queryStr);
        List<String> params = q.getResultList();
        BaseTable tb = new BaseTable();
        if (params != null && !params.isEmpty()) {
            for (String row : params) {
                BaseRowInterface r = new BaseRow();
                r.setString(AplParamApplicTableDescriptor.COL_TI_PARAM_APPLIC, row);
                tb.add(r);
            }
        }
        return tb;
    }

    /**
     * Metodo che ritorna i parametri di configurazione dell'applicazione dato il tipo di parametro
     * da ricercare
     *
     * @param tipoParam tipo parametro
     *
     * @return il tablebean contenente la lista di parametri di configurazione
     */
    public AplParamApplicTableBean getConfigurationViewBean(String tipoParam) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT DISTINCT config FROM AplParamApplic config ");
        if (tipoParam != null) {
            queryStr.append(" WHERE config.tiParamApplic = :tipo");
        }
        Query q = getEntityManager().createQuery(queryStr.toString());
        if (tipoParam != null) {
            q.setParameter("tipo", tipoParam);
        }
        List<AplParamApplic> params = q.getResultList();
        AplParamApplicTableBean tb = new AplParamApplicTableBean();
        try {
            if (params != null && !params.isEmpty()) {
                tb = (AplParamApplicTableBean) Transform.entities2TableBean(params);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return tb;
    }

    /**
     * Rimuove la riga di parametri definita nel rowBean
     *
     * @param param oggetto {@link LogParam}
     * @param row   il parametro da eliminare
     *
     * @return true se eliminato con successo
     */
    public boolean deleteAplParamApplicRowBean(LogParam param, AplParamApplicRowBean row) {
        AplParamApplic config;
        boolean result = false;
        try {
            config = getEntityManager().find(AplParamApplic.class,
                    row.getIdParamApplic().longValue());
            // Rimuovo il record
            getEntityManager().remove(config);
            getEntityManager().flush();

            // Loggo l'intera struttura che sto per cancellare
            sacerLogEjb.log(param.getTransactionLogContext(), param.getNomeApplicazione(),
                    param.getNomeUtente(), param.getNomeAzione(),
                    SacerLogConstants.TIPO_OGGETTO_REGISTRO_PARAMETRI, BigDecimal.ZERO,
                    param.getNomePagina());

            result = true;
        } catch (Exception ex) {
            log.error(ex.getMessage());
            throw ex;
        }
        return result;
    }

    /**
     * Metodo che ritorna i parametri di configurazione
     *
     * @param tiParamApplic           tipo parametro applicativo
     * @param tiGestioneParam         tipo gestione parametro
     * @param flAppartApplic          flag 1/0 (true/false) appartenenza applicazione
     * @param flAppartAmbiente        flag 1/0 (true/false) appartenenza ambiente
     * @param flAppartStrut           flag 1/0 (true/false) appartenenza struttura
     * @param flAppartTipoUnitaDoc    flag 1/0 (true/false) appartenenza tipo unità documentaria
     * @param flAppartAaTipoFascicolo flag 1/0 (true/false) appartenenza anno tipo fascicolo
     * @param cdVersioneAppIni        codice versione applicativo inizio
     * @param cdVersioneAppFine       codice versione applicativo fine
     *
     * @return lista oggetti di tipo {@link AplParamApplic}
     */
    public List<AplParamApplic> getAplParamApplicList(String tiParamApplic, String tiGestioneParam,
            String flAppartApplic, String flAppartAmbiente, String flAppartStrut,
            String flAppartTipoUnitaDoc, String flAppartAaTipoFascicolo, String cdVersioneAppIni,
            String cdVersioneAppFine) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT paramApplic FROM AplParamApplic paramApplic ");
        String whereWord = " WHERE ";
        if (tiParamApplic != null) {
            queryStr.append(whereWord).append("paramApplic.tiParamApplic = :tiParamApplic ");
            whereWord = "AND ";
        }
        if (tiGestioneParam != null) {
            queryStr.append(whereWord).append("paramApplic.tiGestioneParam = :tiGestioneParam ");
            whereWord = "AND ";
        }
        if (flAppartApplic != null) {
            queryStr.append(whereWord).append("paramApplic.flAppartApplic = :flAppartApplic ");
            whereWord = "AND ";
        }
        if (flAppartAmbiente != null) {
            queryStr.append(whereWord).append("paramApplic.flAppartAmbiente = :flAppartAmbiente ");
            whereWord = "AND ";
        }
        if (flAppartStrut != null) {
            queryStr.append(whereWord).append("paramApplic.flAppartStrut = :flAppartStrut ");
            whereWord = "AND ";
        }
        if (flAppartTipoUnitaDoc != null) {
            queryStr.append(whereWord)
                    .append("paramApplic.flAppartTipoUnitaDoc = :flAppartTipoUnitaDoc ");
            whereWord = "AND ";
        }
        if (flAppartAaTipoFascicolo != null) {
            queryStr.append(whereWord)
                    .append("paramApplic.flAppartAaTipoFascicolo = :flAppartAaTipoFascicolo ");
            whereWord = "AND ";
        }
        if (cdVersioneAppIni != null) {
            queryStr.append(whereWord).append("paramApplic.cdVersioneAppIni = :cdVersioneAppIni ");
            whereWord = "AND ";
        }
        if (cdVersioneAppFine != null) {
            queryStr.append(whereWord)
                    .append("paramApplic.cdVersioneAppFine = :cdVersioneAppFine ");
        }
        queryStr.append("ORDER BY paramApplic.tiParamApplic, paramApplic.nmParamApplic ");
        Query q = getEntityManager().createQuery(queryStr.toString());
        if (tiParamApplic != null) {
            q.setParameter("tiParamApplic", tiParamApplic);
        }
        if (tiGestioneParam != null) {
            q.setParameter("tiGestioneParam", tiGestioneParam);
        }
        if (flAppartApplic != null) {
            q.setParameter("flAppartApplic", flAppartApplic);
        }
        if (flAppartAmbiente != null) {
            q.setParameter("flAppartAmbiente", flAppartAmbiente);
        }
        if (flAppartStrut != null) {
            q.setParameter("flAppartStrut", flAppartStrut);
        }
        if (flAppartTipoUnitaDoc != null) {
            q.setParameter("flAppartTipoUnitaDoc", flAppartTipoUnitaDoc);
        }
        if (flAppartAaTipoFascicolo != null) {
            q.setParameter("flAppartAaTipoFascicolo", flAppartAaTipoFascicolo);
        }
        if (cdVersioneAppIni != null) {
            q.setParameter("cdVersioneAppIni", cdVersioneAppIni);
        }
        if (cdVersioneAppFine != null) {
            q.setParameter("cdVersioneAppFine", cdVersioneAppFine);
        }
        return q.getResultList();
    }

    /**
     * Metodo che ritorna i parametri di configurazione con filtro opzionale sulla validità
     *
     * @param tiParamApplic           tipo parametro applicativo
     * @param tiGestioneParam         tipo gestione parametro
     * @param flAppartApplic          flag 1/0 (true/false) appartenenza applicazione
     * @param flAppartAmbiente        flag 1/0 (true/false) appartenenza ambiente
     * @param flAppartStrut           flag 1/0 (true/false) appartenenza struttura
     * @param flAppartTipoUnitaDoc    flag 1/0 (true/false) appartenenza tipo unità documentaria
     * @param flAppartAaTipoFascicolo flag 1/0 (true/false) appartenenza anno tipo fascicolo
     * @param cdVersioneAppIni        codice versione applicativo inizio
     * @param cdVersioneAppFine       codice versione applicativo fine
     * @param filterValid             true per filtrare solo i parametri attivi (sulla base della
     *                                versione applicativo)
     *
     * @return lista oggetti di tipo {@link AplParamApplic}
     */
    public List<AplParamApplic> getAplParamApplicList(String tiParamApplic, String tiGestioneParam,
            String flAppartApplic, String flAppartAmbiente, String flAppartStrut,
            String flAppartTipoUnitaDoc, String flAppartAaTipoFascicolo, String cdVersioneAppIni,
            String cdVersioneAppFine, boolean filterValid) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT paramApplic FROM AplParamApplic paramApplic ");
        String whereWord = " WHERE ";
        if (tiParamApplic != null) {
            queryStr.append(whereWord).append("paramApplic.tiParamApplic = :tiParamApplic ");
            whereWord = "AND ";
        }
        if (tiGestioneParam != null) {
            queryStr.append(whereWord).append("paramApplic.tiGestioneParam = :tiGestioneParam ");
            whereWord = "AND ";
        }
        if (flAppartApplic != null) {
            queryStr.append(whereWord).append("paramApplic.flAppartApplic = :flAppartApplic ");
            whereWord = "AND ";
        }
        if (flAppartAmbiente != null) {
            queryStr.append(whereWord).append("paramApplic.flAppartAmbiente = :flAppartAmbiente ");
            whereWord = "AND ";
        }
        if (flAppartStrut != null) {
            queryStr.append(whereWord).append("paramApplic.flAppartStrut = :flAppartStrut ");
            whereWord = "AND ";
        }
        if (flAppartTipoUnitaDoc != null) {
            queryStr.append(whereWord)
                    .append("paramApplic.flAppartTipoUnitaDoc = :flAppartTipoUnitaDoc ");
            whereWord = "AND ";
        }
        if (flAppartAaTipoFascicolo != null) {
            queryStr.append(whereWord)
                    .append("paramApplic.flAppartAaTipoFascicolo = :flAppartAaTipoFascicolo ");
            whereWord = "AND ";
        }
        if (cdVersioneAppIni != null) {
            queryStr.append(whereWord).append("paramApplic.cdVersioneAppIni = :cdVersioneAppIni ");
            whereWord = "AND ";
        }
        if (cdVersioneAppFine != null) {
            queryStr.append(whereWord)
                    .append("paramApplic.cdVersioneAppFine = :cdVersioneAppFine ");
            whereWord = "AND ";
        }
        if (filterValid) {
            queryStr.append(whereWord).append("paramApplic.cdVersioneAppFine IS NULL ");
        }

        queryStr.append("ORDER BY paramApplic.tiParamApplic, paramApplic.nmParamApplic ");
        Query q = getEntityManager().createQuery(queryStr.toString());
        if (tiParamApplic != null) {
            q.setParameter("tiParamApplic", tiParamApplic);
        }
        if (tiGestioneParam != null) {
            q.setParameter("tiGestioneParam", tiGestioneParam);
        }
        if (flAppartApplic != null) {
            q.setParameter("flAppartApplic", flAppartApplic);
        }
        if (flAppartAmbiente != null) {
            q.setParameter("flAppartAmbiente", flAppartAmbiente);
        }
        if (flAppartStrut != null) {
            q.setParameter("flAppartStrut", flAppartStrut);
        }
        if (flAppartTipoUnitaDoc != null) {
            q.setParameter("flAppartTipoUnitaDoc", flAppartTipoUnitaDoc);
        }
        if (flAppartAaTipoFascicolo != null) {
            q.setParameter("flAppartAaTipoFascicolo", flAppartAaTipoFascicolo);
        }
        if (cdVersioneAppIni != null) {
            q.setParameter("cdVersioneAppIni", cdVersioneAppIni);
        }
        if (cdVersioneAppFine != null) {
            q.setParameter("cdVersioneAppFine", cdVersioneAppFine);
        }
        return q.getResultList();
    }

    /**
     * Verifica se esiste un parametro applicativo con il nome specificato, escludendo l'id fornito
     *
     * @param nmParamApplic nome parametro applicativo
     * @param idParamApplic id parametro applicativo da escludere
     *
     * @return true se esiste un parametro con il nome specificato
     */
    public boolean existsAplParamApplic(String nmParamApplic, BigDecimal idParamApplic) {
        Query q = getEntityManager()
                .createQuery("SELECT paramApplic FROM AplParamApplic paramApplic "
                        + "WHERE paramApplic.nmParamApplic = :nmParamApplic "
                        + "AND paramApplic.idParamApplic != :idParamApplic ");
        q.setParameter("nmParamApplic", nmParamApplic);
        q.setParameter("idParamApplic", longFromBigDecimal(idParamApplic));
        return !q.getResultList().isEmpty();
    }

    /**
     * Recupera il valore di un parametro applicativo dato l'id del parametro e il tipo appartenenza
     *
     * @param idParamApplic id parametro applicativo
     * @param tiAppart      tipo appartenenza
     *
     * @return valore parametro applicativo o null se non trovato
     */
    public AplValoreParamApplic getAplValoreParamApplic(long idParamApplic, String tiAppart) {
        Query q = getEntityManager()
                .createQuery("SELECT valoreParamApplic FROM AplValoreParamApplic valoreParamApplic "
                        + "WHERE valoreParamApplic.aplParamApplic.idParamApplic = :idParamApplic "
                        + "AND valoreParamApplic.tiAppart = :tiAppart ");
        q.setParameter("idParamApplic", idParamApplic);
        q.setParameter("tiAppart", tiAppart);
        List<AplValoreParamApplic> lista = q.getResultList();
        if (!lista.isEmpty()) {
            return lista.get(0);
        }
        return null;
    }

    /**
     * Metodo che ritorna la lista dei tipi di parametri di configurazione
     *
     * @return lista dei tipi parametri di configurazione
     */
    public List<String> getTiParamApplic() {
        String queryStr = "SELECT DISTINCT config.tiParamApplic FROM AplParamApplic config ORDER BY config.tiParamApplic ";
        Query q = getEntityManager().createQuery(queryStr);
        return q.getResultList();
    }

    /**
     * Metodo che ritorna le versioni di inizio dei parametri di configurazione
     *
     * @return lista delle versioni di inizio dei parametri di configurazione
     */
    public List<String> getCdVersioneAppIni() {
        String queryStr = "SELECT DISTINCT config.cdVersioneAppIni FROM AplParamApplic config WHERE config.cdVersioneAppIni IS NOT NULL ORDER BY config.cdVersioneAppIni ";
        Query q = getEntityManager().createQuery(queryStr);
        return q.getResultList();
    }

    /**
     * Metodo che ritorna le versioni di fine dei parametri di configurazione
     *
     * @return lista delle versioni di fine dei parametri di configurazione
     */
    public List<String> getCdVersioneAppFine() {
        String queryStr = "SELECT DISTINCT config.cdVersioneAppFine FROM AplParamApplic config WHERE config.cdVersioneAppFine IS NOT NULL ORDER BY config.cdVersioneAppFine ";
        Query q = getEntityManager().createQuery(queryStr);
        return q.getResultList();
    }

    /**
     * Recupera i parametri applicativi appartenenti all'ambiente, filtrati opzionalmente per
     * funzione
     *
     * @param funzione lista delle funzioni da filtrare (opzionale)
     *
     * @return lista parametri applicativi ambiente
     */
    public List<AplParamApplic> getAplParamApplicListAmbiente(List<String> funzione) {
        String queryStr = "SELECT paramApplic FROM AplParamApplic paramApplic "
                + "WHERE paramApplic.flAppartAmbiente = '1' ";

        if (funzione != null && !funzione.isEmpty()) {
            queryStr = queryStr + "AND paramApplic.tiParamApplic IN (:funzione) ";
        }
        queryStr = queryStr + "ORDER BY paramApplic.tiParamApplic, paramApplic.nmParamApplic";

        Query q = getEntityManager().createQuery(queryStr);
        if (funzione != null && !funzione.isEmpty()) {
            q.setParameter("funzione", funzione);
        }
        return q.getResultList();

    }

    /**
     * Recupera i parametri applicativi appartenenti all'ambiente, con opzione di filtrare solo
     * quelli validi
     *
     * @param funzione    lista delle funzioni da filtrare (opzionale)
     * @param filterValid true per filtrare solo i parametri validi
     *
     * @return lista parametri applicativi ambiente
     */
    public List<AplParamApplic> getAplParamApplicListAmbiente(List<String> funzione,
            boolean filterValid) {
        String queryStr = "SELECT paramApplic FROM AplParamApplic paramApplic "
                + "WHERE paramApplic.flAppartAmbiente = '1' ";

        if (funzione != null && !funzione.isEmpty()) {
            queryStr = queryStr + "AND paramApplic.tiParamApplic IN (:funzione) ";
        }
        if (filterValid) {
            queryStr = queryStr + "AND paramApplic.cdVersioneAppFine IS NULL ";
        }
        queryStr = queryStr + "ORDER BY paramApplic.tiParamApplic, paramApplic.nmParamApplic";

        Query q = getEntityManager().createQuery(queryStr);
        if (funzione != null && !funzione.isEmpty()) {
            q.setParameter("funzione", funzione);
        }
        return q.getResultList();

    }

    /**
     * Recupera i parametri applicativi appartenenti all'ambiente, filtrati per funzione, tipo
     * gestione e validità
     *
     * @param funzione        lista delle funzioni da filtrare (opzionale)
     * @param tiGestioneParam tipo gestione parametro (opzionale)
     * @param filterValid     true per filtrare solo i parametri validi
     *
     * @return lista parametri applicativi ambiente
     */
    public List<AplParamApplic> getAplParamApplicListAmbiente(List<String> funzione,
            String tiGestioneParam, boolean filterValid) {
        String queryStr = "SELECT paramApplic FROM AplParamApplic paramApplic "
                + "WHERE paramApplic.flAppartAmbiente = '1' ";

        if (funzione != null && !funzione.isEmpty()) {
            queryStr = queryStr + "AND paramApplic.tiParamApplic IN (:funzione) ";
        }
        if (tiGestioneParam != null) {
            queryStr = queryStr + "AND paramApplic.tiGestioneParam = :tiGestioneParam ";
        }
        if (filterValid) {
            queryStr = queryStr + "AND paramApplic.cdVersioneAppFine IS NULL ";
        }
        queryStr = queryStr + "ORDER BY paramApplic.tiParamApplic, paramApplic.nmParamApplic";

        Query q = getEntityManager().createQuery(queryStr);
        if (funzione != null && !funzione.isEmpty()) {
            q.setParameter("funzione", funzione);
        }
        if (tiGestioneParam != null) {
            q.setParameter("tiGestioneParam", tiGestioneParam);
        }
        return q.getResultList();

    }

    /**
     * Recupera i parametri applicativi appartenenti alla struttura, filtrati opzionalmente per
     * funzione
     *
     * @param funzione lista delle funzioni da filtrare (opzionale)
     *
     * @return lista parametri applicativi struttura
     */
    public List<AplParamApplic> getAplParamApplicListStruttura(List<String> funzione) {
        String queryStr = "SELECT paramApplic FROM AplParamApplic paramApplic "
                + "WHERE paramApplic.flAppartStrut = '1' ";

        if (funzione != null && !funzione.isEmpty()) {
            queryStr = queryStr + "AND paramApplic.tiParamApplic IN (:funzione) ";
        }
        queryStr = queryStr + "ORDER BY paramApplic.tiParamApplic, paramApplic.nmParamApplic";

        Query q = getEntityManager().createQuery(queryStr);
        if (funzione != null && !funzione.isEmpty()) {
            q.setParameter("funzione", funzione);
        }
        return q.getResultList();
    }

    /**
     * Recupera i parametri applicativi appartenenti alla struttura, con opzione di filtrare solo
     * quelli validi
     *
     * @param funzione    lista delle funzioni da filtrare (opzionale)
     * @param filterValid true per filtrare solo i parametri validi
     *
     * @return lista parametri applicativi struttura
     */
    public List<AplParamApplic> getAplParamApplicListStruttura(List<String> funzione,
            boolean filterValid) {
        String queryStr = "SELECT paramApplic FROM AplParamApplic paramApplic "
                + "WHERE paramApplic.flAppartStrut = '1' ";

        if (funzione != null && !funzione.isEmpty()) {
            queryStr = queryStr + "AND paramApplic.tiParamApplic IN (:funzione) ";
        }
        if (filterValid) {
            queryStr = queryStr + "AND paramApplic.cdVersioneAppFine IS NULL ";
        }
        queryStr = queryStr + "ORDER BY paramApplic.tiParamApplic, paramApplic.nmParamApplic";

        Query q = getEntityManager().createQuery(queryStr);
        if (funzione != null && !funzione.isEmpty()) {
            q.setParameter("funzione", funzione);
        }
        return q.getResultList();
    }

    /**
     * Recupera i parametri applicativi appartenenti alla struttura, filtrati per funzione, tipo
     * gestione e validità
     *
     * @param funzione        lista delle funzioni da filtrare (opzionale)
     * @param tiGestioneParam tipo gestione parametro (opzionale)
     * @param filterValid     true per filtrare solo i parametri validi
     *
     * @return lista parametri applicativi struttura
     */
    public List<AplParamApplic> getAplParamApplicListStruttura(List<String> funzione,
            String tiGestioneParam, boolean filterValid) {
        String queryStr = "SELECT paramApplic FROM AplParamApplic paramApplic "
                + "WHERE paramApplic.flAppartStrut = '1' ";

        if (funzione != null && !funzione.isEmpty()) {
            queryStr = queryStr + "AND paramApplic.tiParamApplic IN (:funzione) ";
        }
        if (tiGestioneParam != null) {
            queryStr = queryStr + "AND paramApplic.tiGestioneParam = :tiGestioneParam ";
        }
        if (filterValid) {
            queryStr = queryStr + "AND paramApplic.cdVersioneAppFine IS NULL ";
        }
        queryStr = queryStr + "ORDER BY paramApplic.tiParamApplic, paramApplic.nmParamApplic";

        Query q = getEntityManager().createQuery(queryStr);
        if (funzione != null && !funzione.isEmpty()) {
            q.setParameter("funzione", funzione);
        }
        if (tiGestioneParam != null) {
            q.setParameter("tiGestioneParam", tiGestioneParam);
        }
        return q.getResultList();
    }

    /**
     * Recupera i parametri applicativi appartenenti al tipo unità documentaria, filtrati
     * opzionalmente per funzione
     *
     * @param funzione lista delle funzioni da filtrare (opzionale)
     *
     * @return lista parametri applicativi tipo unità documentaria
     */
    public List<AplParamApplic> getAplParamApplicListTipoUd(List<String> funzione) {
        String queryStr = "SELECT paramApplic FROM AplParamApplic paramApplic "
                + "WHERE paramApplic.flAppartTipoUnitaDoc = '1' ";

        if (funzione != null && !funzione.isEmpty()) {
            queryStr = queryStr + "AND paramApplic.tiParamApplic IN (:funzione) ";
        }
        queryStr = queryStr + "ORDER BY paramApplic.tiParamApplic, paramApplic.nmParamApplic";

        Query q = getEntityManager().createQuery(queryStr);
        if (funzione != null && !funzione.isEmpty()) {
            q.setParameter("funzione", funzione);
        }
        return q.getResultList();
    }

    /**
     * Recupera i parametri applicativi appartenenti al tipo unità documentaria, con opzione di
     * filtrare solo quelli validi
     *
     * @param funzione    lista delle funzioni da filtrare (opzionale)
     * @param filterValid true per filtrare solo i parametri validi
     *
     * @return lista parametri applicativi tipo unità documentaria
     */
    public List<AplParamApplic> getAplParamApplicListTipoUd(List<String> funzione,
            boolean filterValid) {
        String queryStr = "SELECT paramApplic FROM AplParamApplic paramApplic "
                + "WHERE paramApplic.flAppartTipoUnitaDoc = '1' ";

        if (funzione != null && !funzione.isEmpty()) {
            queryStr = queryStr + "AND paramApplic.tiParamApplic IN (:funzione) ";
        }
        if (filterValid) {
            queryStr = queryStr + "AND paramApplic.cdVersioneAppFine IS NULL ";
        }
        queryStr = queryStr + "ORDER BY paramApplic.tiParamApplic, paramApplic.nmParamApplic";

        Query q = getEntityManager().createQuery(queryStr);
        if (funzione != null && !funzione.isEmpty()) {
            q.setParameter("funzione", funzione);
        }
        return q.getResultList();
    }

    /**
     * Recupera i parametri applicativi appartenenti al tipo unità documentaria, filtrati per
     * funzione, tipo gestione e validità
     *
     * @param funzione        lista delle funzioni da filtrare (opzionale)
     * @param tiGestioneParam tipo gestione parametro (opzionale)
     * @param filterValid     true per filtrare solo i parametri validi
     *
     * @return lista parametri applicativi tipo unità documentaria
     */
    public List<AplParamApplic> getAplParamApplicListTipoUd(List<String> funzione,
            String tiGestioneParam, boolean filterValid) {
        String queryStr = "SELECT paramApplic FROM AplParamApplic paramApplic "
                + "WHERE paramApplic.flAppartTipoUnitaDoc = '1' ";

        if (funzione != null && !funzione.isEmpty()) {
            queryStr = queryStr + "AND paramApplic.tiParamApplic IN (:funzione) ";
        }
        if (tiGestioneParam != null) {
            queryStr = queryStr + "AND paramApplic.tiGestioneParam = :tiGestioneParam ";
        }
        if (filterValid) {
            queryStr = queryStr + "AND paramApplic.cdVersioneAppFine IS NULL ";
        }
        queryStr = queryStr + "ORDER BY paramApplic.tiParamApplic, paramApplic.nmParamApplic";

        Query q = getEntityManager().createQuery(queryStr);
        if (funzione != null && !funzione.isEmpty()) {
            q.setParameter("funzione", funzione);
        }
        if (tiGestioneParam != null) {
            q.setParameter("tiGestioneParam", tiGestioneParam);
        }
        return q.getResultList();
    }

    /**
     * Recupera i parametri applicativi appartenenti all'anno tipo fascicolo, filtrati opzionalmente
     * per funzione
     *
     * @param funzione lista delle funzioni da filtrare (opzionale)
     *
     * @return lista parametri applicativi anno tipo fascicolo
     */
    public List<AplParamApplic> getAplParamApplicListAaTipoFascicolo(List<String> funzione) {
        String queryStr = "SELECT paramApplic FROM AplParamApplic paramApplic "
                + "WHERE paramApplic.flAppartAaTipoFascicolo = '1' ";

        if (funzione != null && !funzione.isEmpty()) {
            queryStr = queryStr + "AND paramApplic.tiParamApplic IN (:funzione) ";
        }
        queryStr = queryStr + "ORDER BY paramApplic.tiParamApplic, paramApplic.nmParamApplic";

        Query q = getEntityManager().createQuery(queryStr);
        if (funzione != null && !funzione.isEmpty()) {
            q.setParameter("funzione", funzione);
        }
        return q.getResultList();
    }

    /**
     * Recupera i parametri applicativi appartenenti all'anno tipo fascicolo, con opzione di
     * filtrare solo quelli validi
     *
     * @param funzione    lista delle funzioni da filtrare (opzionale)
     * @param filterValid true per filtrare solo i parametri validi
     *
     * @return lista parametri applicativi anno tipo fascicolo
     */
    public List<AplParamApplic> getAplParamApplicListAaTipoFascicolo(List<String> funzione,
            boolean filterValid) {
        String queryStr = "SELECT paramApplic FROM AplParamApplic paramApplic "
                + "WHERE paramApplic.flAppartAaTipoFascicolo = '1' ";

        if (funzione != null && !funzione.isEmpty()) {
            queryStr = queryStr + "AND paramApplic.tiParamApplic IN (:funzione) ";
        }
        if (filterValid) {
            queryStr = queryStr + "AND paramApplic.cdVersioneAppFine IS NULL ";
        }
        queryStr = queryStr + "ORDER BY paramApplic.tiParamApplic, paramApplic.nmParamApplic";

        Query q = getEntityManager().createQuery(queryStr);
        if (funzione != null && !funzione.isEmpty()) {
            q.setParameter("funzione", funzione);
        }
        return q.getResultList();
    }

    /**
     * Recupera i parametri applicativi appartenenti all'anno tipo fascicolo, filtrati per funzione,
     * tipo gestione e validità
     *
     * @param funzione        lista delle funzioni da filtrare (opzionale)
     * @param tiGestioneParam tipo gestione parametro (opzionale)
     * @param filterValid     true per filtrare solo i parametri validi
     *
     * @return lista parametri applicativi anno tipo fascicolo
     */
    public List<AplParamApplic> getAplParamApplicListAaTipoFascicolo(List<String> funzione,
            String tiGestioneParam, boolean filterValid) {
        String queryStr = "SELECT paramApplic FROM AplParamApplic paramApplic "
                + "WHERE paramApplic.flAppartAaTipoFascicolo = '1' ";

        if (funzione != null && !funzione.isEmpty()) {
            queryStr = queryStr + "AND paramApplic.tiParamApplic IN (:funzione) ";
        }
        if (tiGestioneParam != null) {
            queryStr = queryStr + "AND paramApplic.tiGestioneParam = :tiGestioneParam ";
        }
        if (filterValid) {
            queryStr = queryStr + "AND paramApplic.cdVersioneAppFine IS NULL ";
        }
        queryStr = queryStr + "ORDER BY paramApplic.tiParamApplic, paramApplic.nmParamApplic";

        Query q = getEntityManager().createQuery(queryStr);
        if (funzione != null && !funzione.isEmpty()) {
            q.setParameter("funzione", funzione);
        }
        if (tiGestioneParam != null) {
            q.setParameter("tiGestioneParam", tiGestioneParam);
        }
        return q.getResultList();
    }

    /**
     * Recupera i parametri applicativi multi-valore appartenenti all'ambiente
     *
     * @return lista parametri applicativi multi-valore ambiente
     */
    public List<AplParamApplic> getAplParamApplicMultiListAmbiente() {
        Query q = getEntityManager()
                .createQuery("SELECT paramApplic FROM AplParamApplic paramApplic "
                        + "WHERE paramApplic.flAppartAmbiente = '1' "
                        + "AND paramApplic.flMulti = '1' "
                        + "ORDER BY paramApplic.tiParamApplic, paramApplic.nmParamApplic");
        return q.getResultList();
    }

    /**
     * Recupera i parametri applicativi multi-valore appartenenti all'ambiente, con opzione di
     * filtrare solo quelli validi
     *
     * @param filterValid true per filtrare solo i parametri validi
     *
     * @return lista parametri applicativi multi-valore ambiente
     */
    public List<AplParamApplic> getAplParamApplicMultiListAmbiente(boolean filterValid) {
        String queryStr = "SELECT paramApplic FROM AplParamApplic paramApplic "
                + "WHERE paramApplic.flAppartAmbiente = '1' " + "AND paramApplic.flMulti = '1' ";
        if (filterValid) {
            queryStr = queryStr + "AND paramApplic.cdVersioneAppFine IS NULL ";
        }
        queryStr = queryStr + "ORDER BY paramApplic.tiParamApplic, paramApplic.nmParamApplic";
        Query q = getEntityManager().createQuery(queryStr);
        return q.getResultList();
    }

    /**
     * Recupera il valore di un parametro applicativo dato l'id del parametro, il tipo appartenenza
     * e gli identificativi opzionali di ambiente, struttura, tipo unità documentaria e anno tipo
     * fascicolo
     *
     * @param idParamApplic     id parametro applicativo
     * @param tiAppart          tipo appartenenza
     * @param idAmbiente        id ambiente (opzionale)
     * @param idStrut           id struttura (opzionale)
     * @param idTipoUnitaDoc    id tipo unità documentaria (opzionale)
     * @param idAaTipoFascicolo id anno tipo fascicolo (opzionale)
     *
     * @return valore parametro applicativo o null se non trovato
     */
    public AplValoreParamApplic getAplValoreParamApplic(BigDecimal idParamApplic, String tiAppart,
            BigDecimal idAmbiente, BigDecimal idStrut, BigDecimal idTipoUnitaDoc,
            BigDecimal idAaTipoFascicolo) {

        StringBuilder queryStr = new StringBuilder(
                "SELECT valoreParamApplic FROM AplValoreParamApplic valoreParamApplic "
                        + "WHERE valoreParamApplic.tiAppart = :tiAppart "
                        + "AND valoreParamApplic.aplParamApplic.idParamApplic = :idParamApplic ");

        if (idAmbiente != null) {
            queryStr.append("AND valoreParamApplic.orgAmbiente.idAmbiente = :idAmbiente ");
        }
        if (idStrut != null) {
            queryStr.append("AND valoreParamApplic.orgStrut.idStrut = :idStrut ");
        }
        if (idTipoUnitaDoc != null) {
            queryStr.append(
                    "AND valoreParamApplic.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoUnitaDoc ");
        }
        if (idAaTipoFascicolo != null) {
            queryStr.append(
                    "AND valoreParamApplic.decAaTipoFascicolo.idAaTipoFascicolo = :idAaTipoFascicolo ");
        }

        Query q = getEntityManager().createQuery(queryStr.toString());
        q.setParameter("tiAppart", tiAppart);
        q.setParameter("idParamApplic", longFromBigDecimal(idParamApplic));
        if (idAmbiente != null) {
            q.setParameter("idAmbiente", longFromBigDecimal(idAmbiente));
        }
        if (idStrut != null) {
            q.setParameter("idStrut", longFromBigDecimal(idStrut));
        }
        if (idTipoUnitaDoc != null) {
            q.setParameter("idTipoUnitaDoc", longFromBigDecimal(idTipoUnitaDoc));
        }
        if (idAaTipoFascicolo != null) {
            q.setParameter("idAaTipoFascicolo", longFromBigDecimal(idAaTipoFascicolo));
        }
        List<AplValoreParamApplic> lista = q.getResultList();
        if (!lista.isEmpty()) {
            return lista.get(0);
        }
        return null;
    }

    /**
     * Recupera la lista dei valori multi-valore di un parametro applicativo per un ambiente
     *
     * @param idParamApplic id parametro applicativo
     * @param idAmbiente    id ambiente (opzionale)
     *
     * @return lista valori multi-valore del parametro
     */
    public List<AplValParamApplicMulti> getAplValParamApplicMultiList(BigDecimal idParamApplic,
            BigDecimal idAmbiente) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT valoreParamApplicMulti FROM AplValParamApplicMulti valoreParamApplicMulti "
                        + "WHERE valoreParamApplicMulti.aplParamApplic.idParamApplic = :idParamApplic ");

        if (idAmbiente != null) {
            queryStr.append("AND valoreParamApplicMulti.orgAmbiente.idAmbiente = :idAmbiente ");
        }

        queryStr.append("ORDER BY valoreParamApplicMulti.dsValoreParamApplic");

        Query q = getEntityManager().createQuery(queryStr.toString());
        q.setParameter("idParamApplic", longFromBigDecimal(idParamApplic));
        if (idAmbiente != null) {
            q.setParameter("idAmbiente", longFromBigDecimal(idAmbiente));
        }
        return q.getResultList();
    }

    /**
     * Recupera uno specifico valore multi-valore di un parametro applicativo per un ambiente e un
     * token
     *
     * @param idParamApplic id parametro applicativo
     * @param idAmbiente    id ambiente
     * @param token         valore del token da cercare
     *
     * @return valore multi-valore del parametro o null se non trovato
     */
    public AplValParamApplicMulti getAplValParamApplicMulti(BigDecimal idParamApplic,
            BigDecimal idAmbiente, String token) {
        String queryStr = "SELECT valoreParamApplicMulti FROM AplValParamApplicMulti valoreParamApplicMulti "
                + "WHERE valoreParamApplicMulti.aplParamApplic.idParamApplic = :idParamApplic "
                + "AND valoreParamApplicMulti.orgAmbiente.idAmbiente = :idAmbiente "
                + "AND valoreParamApplicMulti.dsValoreParamApplic = :token ";

        Query q = getEntityManager().createQuery(queryStr);
        q.setParameter("idParamApplic", longFromBigDecimal(idParamApplic));
        q.setParameter("idAmbiente", longFromBigDecimal(idAmbiente));
        q.setParameter("token", token);
        List<AplValParamApplicMulti> lista = q.getResultList();
        if (!lista.isEmpty()) {
            return lista.get(0);
        }
        return null;
    }

}
