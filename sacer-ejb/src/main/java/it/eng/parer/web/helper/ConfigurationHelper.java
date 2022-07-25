package it.eng.parer.web.helper;

import it.eng.parer.entity.AplParamApplic;
import it.eng.parer.entity.constraint.AplValoreParamApplic.TiAppart;
import it.eng.parer.exception.ParamApplicNotFoundException;
import it.eng.parer.web.helper.dto.AplVGetValParamDto;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.CostantiDB.TipoAplVGetValAppart;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@LocalBean
public class ConfigurationHelper {

    public static final String URL_ASSOCIAZIONE_UTENTE_CF = "URL_ASSOCIAZIONE_UTENTE_CF";
    public static final String URL_BACK_ASSOCIAZIONE_UTENTE_CF = "URL_BACK_ASSOCIAZIONE_UTENTE_CF";

    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Default constructor.
     */
    private static final Logger log = LoggerFactory.getLogger(ConfigurationHelper.class.getName());

    public Map<String, String> getConfiguration() {
        String queryStr = "SELECT paramApplic.nmParamApplic, valoreParamApplic.dsValoreParamApplic "
                + "FROM AplValoreParamApplic valoreParamApplic " + "JOIN valoreParamApplic.aplParamApplic paramApplic "
                + "WHERE valoreParamApplic.tiAppart = 'APPLIC' ";
        Query query = entityManager.createQuery(queryStr);

        List<Object[]> configurazioni = query.getResultList();
        Map<String, String> config = new HashMap<String, String>();
        for (Object[] configurazione : configurazioni) {
            config.put((String) configurazione[0], (String) configurazione[1]);
        }
        return config;
    }

    public Map<String, String> getTSAParams() {
        String queryStr = "SELECT config FROM AplParamApplic config where config.tiParamApplic = 'Firma e Marca' AND config.flAppartApplic = '1' ";
        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = entityManager.createQuery(queryStr);

        List<AplParamApplic> configurazioni = query.getResultList();
        Map<String, String> config = new HashMap<>();
        for (AplParamApplic configurazione : configurazioni) {
            config.put(configurazione.getNmParamApplic(), getValoreParamApplic(configurazione.getNmParamApplic(), null,
                    null, null, null, CostantiDB.TipoAplVGetValAppart.APPLIC));
        }
        return config;
    }

    public String getValoreParamApplic(String nmParamApplic) {
        String queryStr = "SELECT u.dsValoreParamApplic FROM AplParamApplic u "
                + "WHERE u.nmParamApplic = :nmParamApplic ";
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        query.setParameter("nmParamApplic", nmParamApplic);
        List<String> paramList = (List<String>) query.getResultList();
        if (paramList != null && !paramList.isEmpty()) {
            return paramList.get(0);
        } else {
            return null;
        }
    }

    /**
     * Ritorna la mappa dei valori di una lista parametri passata in ingresso in base al tipo di entità
     *
     * @param nmParamApplicList
     *            lista parametri
     * @param idAmbiente
     *            id ambiente
     * @param idStrut
     *            id struttura
     * @param idTipoUnitaDoc
     *            id tipo unita doc
     * @param idAaTipoFascicolo
     *            id anno tipo fascicolo
     * @param getVal
     *            entity tipo TipoAplVGetValAppart
     *
     * @return mappa chiave/valore di tipo String
     */
    public Map<String, String> getParamApplicMapValue(List<String> nmParamApplicList, BigDecimal idAmbiente,
            BigDecimal idStrut, BigDecimal idTipoUnitaDoc, BigDecimal idAaTipoFascicolo,
            CostantiDB.TipoAplVGetValAppart getVal) {
        Map<String, String> mappaAgenti = new HashMap<>();
        if (!nmParamApplicList.isEmpty()) {
            String queryStr = "SELECT paramApplic FROM AplParamApplic paramApplic "
                    + "WHERE paramApplic.nmParamApplic IN :nmParamApplicList ";
            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("nmParamApplicList", nmParamApplicList);
            List<AplParamApplic> paramApplicList = (List<AplParamApplic>) query.getResultList();
            for (AplParamApplic paramApplic : paramApplicList) {
                mappaAgenti.put(paramApplic.getNmParamApplic(), getValoreParamApplic(paramApplic.getNmParamApplic(),
                        idAmbiente, idStrut, idTipoUnitaDoc, idAaTipoFascicolo, getVal));
            }
        }
        return mappaAgenti;
    }

    /*
     * Restituisce il nome dell'applicazione configurato sulla tabella dei parametri
     */
    public String getParamApplicApplicationName() {
        String queryStr = "SELECT valoreParamApplic.dsValoreParamApplic "
                + "FROM AplValoreParamApplic valoreParamApplic " + "JOIN valoreParamApplic.aplParamApplic paramApplic "
                + "WHERE paramApplic.nmParamApplic = 'NM_APPLIC' ";
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        List<String> paramList = (List<String>) query.getResultList();
        if (paramList != null && !paramList.isEmpty()) {
            return paramList.get(0);
        } else {
            return null;
        }
    }

    public String getAplValoreParamApplic(String nmParamApplic, String tiAppart, BigDecimal idAmbiente,
            BigDecimal idStrut, BigDecimal idTipoUnitaDoc, BigDecimal idAaTipoFascicolo) {
        String queryStr = "SELECT valoreParamApplic.dsValoreParamApplic "
                + "FROM AplValoreParamApplic valoreParamApplic " + "JOIN valoreParamApplic.aplParamApplic paramApplic "
                + "WHERE paramApplic.nmParamApplic = :nmParamApplic " + "AND valoreParamApplic.tiAppart = :tiAppart ";

        if (idAmbiente != null) {
            queryStr = queryStr + "AND valoreParamApplic.orgAmbiente.idAmbiente = :idAmbiente ";
        }
        if (idStrut != null) {
            queryStr = queryStr + "AND valoreParamApplic.orgStrut.idStrut = :idStrut ";
        }
        if (idTipoUnitaDoc != null) {
            queryStr = queryStr + "AND valoreParamApplic.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoUnitaDoc ";
        }
        if (idAaTipoFascicolo != null) {
            queryStr = queryStr + "AND valoreParamApplic.decAaTipoFascicolo.idAaTipoFascicolo = :idAaTipoFascicolo ";
        }

        javax.persistence.Query query = entityManager.createQuery(queryStr);

        query.setParameter("nmParamApplic", nmParamApplic);
        query.setParameter("tiAppart", tiAppart);

        if (idAmbiente != null) {
            query.setParameter("idAmbiente", idAmbiente);
        }
        if (idStrut != null) {
            query.setParameter("idStrut", idStrut);
        }
        if (idTipoUnitaDoc != null) {
            query.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
        }
        if (idAaTipoFascicolo != null) {
            query.setParameter("idAaTipoFascicolo", idAaTipoFascicolo);
        }

        List<String> paramList = (List<String>) query.getResultList();
        if (paramList != null && !paramList.isEmpty()) {
            return paramList.get(0);
        } else {
            return null;
        }
    }

    public AplParamApplic getParamApplic(String nmParamApplic) {
        String queryStr = "SELECT paramApplic FROM AplParamApplic paramApplic "
                + "WHERE paramApplic.nmParamApplic = :nmParamApplic ";
        javax.persistence.Query query = entityManager.createQuery(queryStr);
        query.setParameter("nmParamApplic", nmParamApplic);
        List<AplParamApplic> paramList = (List<AplParamApplic>) query.getResultList();
        if (paramList != null && !paramList.isEmpty()) {
            return paramList.get(0);
        } else {
            return null;
        }
    }

    private static final String APLVGETVALPARAMBYCOL = "AplVGetvalParamByCol";
    private static final String APLVGETVALPARAMBY = "AplVGetvalParamBy";
    private static final String FLAPLPARAMAPPLICAPPART = "flAplParamApplicAppart";
    private static final String IDAPLVGETVALPARAMBY = "idAplVGetvalParamBy";

    /**
     * Ottieni il valore del parametro indicato dal codice in input. Il valore viene ottenuto filtrando per tipologia
     * <em>APPLIC</em> {@link TipoAplVGetValAppart#APPLIC} .
     *
     * @param nmParamApplic
     *            codice del parametro
     *
     * @return valore del parametro filtrato per tipologia <em>APPLIC</em> .
     */
    public String getValoreParamApplicByApplic(String nmParamApplic) {
        return getValoreParamApplic(nmParamApplic, BigDecimal.valueOf(Integer.MIN_VALUE),
                BigDecimal.valueOf(Integer.MIN_VALUE), BigDecimal.valueOf(Integer.MIN_VALUE),
                BigDecimal.valueOf(Integer.MIN_VALUE), TipoAplVGetValAppart.APPLIC);
    }

    /**
     *
     * @param nmParamApplic
     *            nome parametro applicativo
     * @param idAmbiente
     *            id ambiente
     * @param idStrut
     *            id struttura
     * @param idTipoUnitaDoc
     *            id tipo unita doc
     * @param idAaTipoFascicolo
     *            id anno tipo fascicolo
     * @param tipoAplVGetValAppart
     *            entity TipoAplVGetValAppart
     *
     * @return valore parametro
     */
    @SuppressWarnings("unchecked")
    public String getValoreParamApplic(String nmParamApplic, BigDecimal idAmbiente, BigDecimal idStrut,
            BigDecimal idTipoUnitaDoc, BigDecimal idAaTipoFascicolo, TipoAplVGetValAppart tipoAplVGetValAppart) {

        long id = Integer.MIN_VALUE;// su questo id non troverò alcun elemento value sicuramente null
        List<AplVGetValParamDto> result = null;

        // base query (template)
        Map<String, String> queryData = new HashMap<>();
        String queryStr = null;

        // query template -> create DTO
        String queryStrTempl = "SELECT NEW it.eng.parer.web.helper.dto.AplVGetValParamDto (${" + APLVGETVALPARAMBYCOL
                + "}) " + "FROM AplParamApplic paramApplic, ${" + APLVGETVALPARAMBY + "} getvalParam  "
                + "WHERE paramApplic.nmParamApplic = :nmParamApplic "
                + "AND getvalParam.nmParamApplic = paramApplic.nmParamApplic " + "AND paramApplic.${"
                + FLAPLPARAMAPPLICAPPART + "} = :flAppart ${" + IDAPLVGETVALPARAMBY + "} ";

        // tipo appartenenza
        TiAppart tiAppart = null;

        switch (tipoAplVGetValAppart) {
        case AATIPOFASCICOLO:
            //
            id = idAaTipoFascicolo != null ? idAaTipoFascicolo.longValue() : Integer.MIN_VALUE;
            //
            tiAppart = TiAppart.PERIODO_TIPO_FASC;
            //
            queryData.put(APLVGETVALPARAMBYCOL, "getvalParam.dsValoreParamApplic, getvalParam.tiAppart");
            queryData.put(APLVGETVALPARAMBY, "AplVGetvalParamByAatifasc");
            queryData.put(FLAPLPARAMAPPLICAPPART, "flAppartAaTipoFascicolo");
            queryData.put(IDAPLVGETVALPARAMBY, "AND getvalParam.idAaTipoFascicolo = :id");
            // replace
            queryStr = StringSubstitutor.replace(queryStrTempl, queryData);
            break;
        case TIPOUNITADOC:
            //
            id = idTipoUnitaDoc != null ? idTipoUnitaDoc.longValue() : Integer.MIN_VALUE;
            //
            tiAppart = TiAppart.TIPO_UNITA_DOC;
            //
            queryData.put(APLVGETVALPARAMBYCOL, "getvalParam.dsValoreParamApplic, getvalParam.tiAppart");
            queryData.put(APLVGETVALPARAMBY, "AplVGetvalParamByTiud");
            queryData.put(FLAPLPARAMAPPLICAPPART, "flAppartTipoUnitaDoc");
            queryData.put(IDAPLVGETVALPARAMBY, "AND getvalParam.idTipoUnitaDoc = :id");
            // replace
            queryStr = StringSubstitutor.replace(queryStrTempl, queryData);
            break;
        case STRUT:
            //
            id = idStrut != null ? idStrut.longValue() : Integer.MIN_VALUE;
            //
            tiAppart = TiAppart.STRUT;
            //
            queryData.put(APLVGETVALPARAMBYCOL, "getvalParam.dsValoreParamApplic, getvalParam.tiAppart");
            queryData.put(APLVGETVALPARAMBY, "AplVGetvalParamByStrut");
            queryData.put(FLAPLPARAMAPPLICAPPART, "flAppartStrut");
            queryData.put(IDAPLVGETVALPARAMBY, "AND getvalParam.idStrut = :id");
            // replace
            queryStr = StringSubstitutor.replace(queryStrTempl, queryData);
            break;
        case AMBIENTE:
            //
            id = idAmbiente != null ? idAmbiente.longValue() : Integer.MIN_VALUE;
            //
            tiAppart = TiAppart.AMBIENTE;
            //
            queryData.put(APLVGETVALPARAMBYCOL, "getvalParam.dsValoreParamApplic, getvalParam.tiAppart");
            queryData.put(APLVGETVALPARAMBY, "AplVGetvalParamByAmb");
            queryData.put(FLAPLPARAMAPPLICAPPART, "flAppartAmbiente");
            queryData.put(IDAPLVGETVALPARAMBY, "AND getvalParam.idAmbiente = :id");
            // replace
            queryStr = StringSubstitutor.replace(queryStrTempl, queryData);
            break;
        default:
            //
            tiAppart = TiAppart.APPLIC;
            //
            queryData.put(APLVGETVALPARAMBYCOL, "getvalParam.dsValoreParamApplic");
            queryData.put(APLVGETVALPARAMBY, "AplVGetvalParamByApl");
            queryData.put(FLAPLPARAMAPPLICAPPART, "flAppartApplic");
            queryData.put(IDAPLVGETVALPARAMBY, "");
            // replace
            queryStr = StringSubstitutor.replace(queryStrTempl, queryData);
            break;
        }

        try {
            TypedQuery<AplVGetValParamDto> query = entityManager.createQuery(queryStr, AplVGetValParamDto.class);
            query.setParameter("nmParamApplic", nmParamApplic);
            query.setParameter("flAppart", "1");// fixed
            // solo nel caso in cui contenga la condition sull'ID
            if (StringUtils.isNotBlank(queryData.get(IDAPLVGETVALPARAMBY))) {
                query.setParameter("id", id);
            }
            // get result
            result = query.getResultList();
        } catch (Exception e) {
            // throws Exception
            final String msg = "Errore nella lettura del parametro " + nmParamApplic + " e tipologia "
                    + tipoAplVGetValAppart.name();
            log.error(msg);
            throw new ParamApplicNotFoundException(msg, nmParamApplic);
        }

        if (result != null && !result.isEmpty()) {
            /*
             * if more than one ....
             */
            if (result.size() > 1) {
                /*
                 * Ordine / Priorità TiAppart idAaTipoFascicolo -> idTipoUnitaDoc -> idStrut -> idAmbiente ->
                 * applicazione
                 */
                // filter by getTiAppart
                return getDsValoreParamApplicByTiAppart(nmParamApplic, result, tiAppart);
            } else {
                return result.get(0).getDsValoreParamApplic(); // one is expected
            }
        } else if (CostantiDB.TipoAplVGetValAppart.next(tipoAplVGetValAppart) != null) {
            /*
             * Ordine / Priorità Viste idAaTipoFascicolo <-> idTipoUnitaDoc -> idStrut -> idAmbiente -> applicazione
             */
            return getValoreParamApplic(nmParamApplic, idAmbiente, idStrut, idTipoUnitaDoc, idAaTipoFascicolo,
                    CostantiDB.TipoAplVGetValAppart.next(tipoAplVGetValAppart));
        } else {
            // thorws Exception
            final String msg = "Parametro " + nmParamApplic + " con tipologia " + tipoAplVGetValAppart.name()
                    + " non definito o non valorizzato";
            log.error(msg);
            throw new ParamApplicNotFoundException(msg, nmParamApplic);
        }
    }

    @SuppressWarnings("unchecked")
    private String getDsValoreParamApplicByTiAppart(String nmParamApplic, List<AplVGetValParamDto> result,
            final TiAppart tiAppart) {
        // get entity from list
        List<AplVGetValParamDto> resultFiltered = new ArrayList<>();
        for (AplVGetValParamDto valParam : result) {
            if (valParam.getTiAppart().equals(tiAppart.name())) {
                resultFiltered.add(valParam);
                break;
            }
        }

        /* questa condizione non dovrebbe mai verificarsi */
        if (tiAppart.name().equals(TiAppart.APPLIC.name()) && (resultFiltered == null || resultFiltered.isEmpty())) {
            // thorws Exception
            final String msg = "Parametro " + nmParamApplic + " non definito o non valorizzato";
            log.error(msg);
            throw new ParamApplicNotFoundException(msg, nmParamApplic);
        }

        if (resultFiltered == null || resultFiltered.isEmpty()) {
            TiAppart nextTiAppart = null;
            switch (tiAppart) {
            case PERIODO_TIPO_FASC:
                nextTiAppart = TiAppart.STRUT;
                break;
            case TIPO_UNITA_DOC:
                nextTiAppart = TiAppart.STRUT;
                break;
            case STRUT:
                nextTiAppart = TiAppart.AMBIENTE;
                break;
            default:
                nextTiAppart = TiAppart.APPLIC;
                break;
            }
            return getDsValoreParamApplicByTiAppart(nmParamApplic, result, nextTiAppart);
        } else {
            return resultFiltered.get(0).getDsValoreParamApplic();// expected one
        }
    }

    public String getUrlAssociazioneUtenteCf() {
        return getValoreParamApplicByApplic(URL_ASSOCIAZIONE_UTENTE_CF);
    }

    public String getUrlBackAssociazioneUtenteCf() {
        return getValoreParamApplicByApplic(URL_BACK_ASSOCIAZIONE_UTENTE_CF);
    }

}
