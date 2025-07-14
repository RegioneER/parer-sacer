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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.exception.ParamApplicNotFoundException;
import it.eng.parer.grantedEntity.IamParamApplic;
import it.eng.parer.grantedEntity.constraint.IamValoreParamApplic.TiAppart;
import it.eng.parer.web.helper.dto.IamVGetValParamDto;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.CostantiDB.TipoIamVGetValAppart;

/**
 *
 * @author dilorenzo_f
 */
@SuppressWarnings("unchecked")
@Stateless
@LocalBean
public class ParamIamHelper {

    private static final Logger log = LoggerFactory.getLogger(ParamIamHelper.class);

    public static final String URL_ASSOCIAZIONE_UTENTE_CF = "URL_ASSOCIAZIONE_UTENTE_CF";
    public static final String URL_BACK_ASSOCIAZIONE_UTENTE_CF = "URL_BACK_ASSOCIAZIONE_UTENTE_CF";

    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    /**
     * Ritorna la mappa dei valori di una lista parametri passata in ingresso in base al tipo di
     * entità
     *
     * @param nmParamApplicList     lista parametri
     * @param idAmbienteEnteConvenz id ambiente convenzionato
     * @param idEnteSiam            id ente IAM
     * @param getVal                entity tipo TipoIamVGetValAppart
     *
     * @return mappa chiave/valore di tipo String
     */
    public Map<String, String> getParamApplicMapValue(List<String> nmParamApplicList,
	    BigDecimal idAmbienteEnteConvenz, BigDecimal idEnteSiam,
	    CostantiDB.TipoIamVGetValAppart getVal) {
	Map<String, String> mappaAgenti = new HashMap<>();
	if (!nmParamApplicList.isEmpty()) {
	    String queryStr = "SELECT paramApplic FROM IamParamApplic paramApplic "
		    + "WHERE paramApplic.nmParamApplic IN :nmParamApplicList ";
	    javax.persistence.Query query = entityManager.createQuery(queryStr);
	    query.setParameter("nmParamApplicList", nmParamApplicList);
	    List<IamParamApplic> paramApplicList = query.getResultList();
	    for (IamParamApplic paramApplic : paramApplicList) {
		mappaAgenti.put(paramApplic.getNmParamApplic(), getValoreParamApplic(
			paramApplic.getNmParamApplic(), idAmbienteEnteConvenz, idEnteSiam, getVal));
	    }
	}
	return mappaAgenti;
    }

    /*
     * Restituisce il nome dell'applicazione configurato sulla tabella dei parametri
     */
    public String getParamApplicApplicationName() {
	String queryStr = "SELECT valoreParamApplic.dsValoreParamApplic "
		+ "FROM IamValoreParamApplic valoreParamApplic "
		+ "JOIN valoreParamApplic.iamParamApplic paramApplic "
		+ "WHERE paramApplic.nmParamApplic = 'NM_APPLIC' ";
	javax.persistence.Query query = entityManager.createQuery(queryStr);
	List<String> paramList = query.getResultList();
	if (paramList != null && !paramList.isEmpty()) {
	    return paramList.get(0);
	} else {
	    return null;
	}
    }

    private static final String IAMVGETVALPARAMBYCOL = "IamVGetvalParamByCol";
    private static final String IAMVGETVALPARAMBY = "IamVGetvalParamBy";
    private static final String FLAPLPARAMAPPLICAPPART = "flIamParamApplicAppart";
    private static final String IDAPLVGETVALPARAMBY = "idIamVGetvalParamBy";

    /**
     *
     * @param nmParamApplic         nome parametro applicativo
     * @param idAmbienteEnteConvenz id ambiente convenzionato
     * @param idEnteSiam            id ente IAM
     * @param tipoIamVGetValAppart  tipo valore {@link TipoIamVGetValAppart}
     *
     * @return valore
     */
    public String getValoreParamApplic(String nmParamApplic, BigDecimal idAmbienteEnteConvenz,
	    BigDecimal idEnteSiam, TipoIamVGetValAppart tipoIamVGetValAppart) {

	long id = Integer.MIN_VALUE;// su questo id non troverò alcun elemento value sicuramente
				    // null
	List<IamVGetValParamDto> result = null;

	// base query (template)
	Map<String, String> queryData = new HashMap<>();
	String queryStr = null;

	// query template -> create DTO
	String queryStrTempl = "SELECT NEW it.eng.parer.web.helper.dto.IamVGetValParamDto (${"
		+ IAMVGETVALPARAMBYCOL + "}) " + "FROM IamParamApplic paramApplic, ${"
		+ IAMVGETVALPARAMBY + "} getvalParam  "
		+ "WHERE paramApplic.nmParamApplic = :nmParamApplic "
		+ "AND getvalParam.nmParamApplic = paramApplic.nmParamApplic "
		+ "AND paramApplic.${" + FLAPLPARAMAPPLICAPPART + "} = :flAppart ${"
		+ IDAPLVGETVALPARAMBY + "} ";

	// tipo appartenenza
	TiAppart tiAppart = null;

	switch (tipoIamVGetValAppart) {
	case ENTECONVENZ:
	    //
	    id = idEnteSiam != null ? idEnteSiam.longValue() : Integer.MIN_VALUE;
	    //
	    tiAppart = TiAppart.ENTE;
	    //
	    queryData.put(IAMVGETVALPARAMBYCOL,
		    "getvalParam.dsValoreParamApplic, getvalParam.tiAppart");
	    queryData.put(IAMVGETVALPARAMBY, "IamVGetvalParamByEnte");
	    queryData.put(FLAPLPARAMAPPLICAPPART, "flApparteEnte");
	    queryData.put(IDAPLVGETVALPARAMBY, "AND getvalParam.idEnteConvenz = :id");
	    // replace
	    queryStr = StringSubstitutor.replace(queryStrTempl, queryData);
	    break;
	case AMBIENTEENTECONVENZ:
	    //
	    id = idAmbienteEnteConvenz != null ? idAmbienteEnteConvenz.longValue()
		    : Integer.MIN_VALUE;
	    //
	    tiAppart = TiAppart.AMBIENTE;
	    //
	    queryData.put(IAMVGETVALPARAMBYCOL,
		    "getvalParam.dsValoreParamApplic, getvalParam.tiAppart");
	    queryData.put(IAMVGETVALPARAMBY, "IamVGetvalParamByAmb");
	    queryData.put(FLAPLPARAMAPPLICAPPART, "flAppartAmbiente");
	    queryData.put(IDAPLVGETVALPARAMBY, "AND getvalParam.idAmbienteEnteConvenz = :id");
	    // replace
	    queryStr = StringSubstitutor.replace(queryStrTempl, queryData);
	    break;
	default:
	    //
	    tiAppart = TiAppart.APPLIC;
	    //
	    queryData.put(IAMVGETVALPARAMBYCOL, "getvalParam.dsValoreParamApplic");
	    queryData.put(IAMVGETVALPARAMBY, "IamVGetvalParamByApl");
	    queryData.put(FLAPLPARAMAPPLICAPPART, "flAppartApplic");
	    queryData.put(IDAPLVGETVALPARAMBY, "");
	    // replace
	    queryStr = StringSubstitutor.replace(queryStrTempl, queryData);
	    break;
	}

	try {
	    TypedQuery<IamVGetValParamDto> query = entityManager.createQuery(queryStr,
		    IamVGetValParamDto.class);
	    query.setParameter("nmParamApplic", nmParamApplic);
	    query.setParameter("flAppart", "1");// fixed
	    // solo nel caso in cui contenga la condition sull'ID
	    if (StringUtils.isNotBlank(queryData.get(IDAPLVGETVALPARAMBY))) {
		query.setParameter("id", /* bigDecimalFrom(id) */ BigDecimal.valueOf(id));
	    }
	    // get result
	    result = query.getResultList();
	} catch (Exception e) {
	    // throws Exception
	    final String msg = "Errore nella lettura del parametro " + nmParamApplic;
	    log.error(msg);
	    throw new ParamApplicNotFoundException(msg, nmParamApplic);
	}

	if (result != null && !result.isEmpty()) {
	    /*
	     * if more than one ....
	     */
	    if (result.size() > 1) {
		/**
		 * Ordine / Priorità TiAppart idAaTipoFascicolo -> idTipoUnitaDoc -> idStrut ->
		 * idAmbiente -> applicazione
		 */
		// filter by getTiAppart
		return getDsValoreParamApplicByTiAppart(nmParamApplic, result, tiAppart);
	    } else {
		return result.get(0).getDsValoreParamApplic(); // one is expected
	    }
	} else if (CostantiDB.TipoIamVGetValAppart.next(tipoIamVGetValAppart) != null) {
	    /*
	     * Ordine / Priorità Viste idEnteSiam -> idAmbienteEnteConvenz -> applicazione
	     */
	    return getValoreParamApplic(nmParamApplic, idAmbienteEnteConvenz, idEnteSiam,
		    CostantiDB.TipoIamVGetValAppart.next(tipoIamVGetValAppart));
	} else {
	    // thorws Exception
	    final String msg = "Parametro " + nmParamApplic + " non definito o non valorizzato";
	    log.error(msg);
	    throw new ParamApplicNotFoundException(msg, nmParamApplic);
	}
    }

    private String getDsValoreParamApplicByTiAppart(String nmParamApplic,
	    List<IamVGetValParamDto> result, final TiAppart tiAppart) {
	// get entity from list

	// Con lambda expression
	// List<AplVGetValParamDto> resultFiltered = result.stream().filter(e ->
	// e.getTiAppart()!=null &&
	// e.getTiAppart().equals(tiAppart.name())).collect(Collectors.toList());
	List<IamVGetValParamDto> resultFiltered = new ArrayList<>();
	for (IamVGetValParamDto valParam : result) {
	    if (valParam.getTiAppart().equals(tiAppart.name())) {
		resultFiltered.add(valParam);
		break;
	    }
	}

	/* questa condizione non dovrebbe mai verificarsi */
	if (tiAppart.name().equals(TiAppart.APPLIC.name()) && resultFiltered.isEmpty()) {
	    // thorws Exception
	    final String msg = "Parametro " + nmParamApplic + " non definito o non valorizzato";
	    log.error(msg);
	    throw new ParamApplicNotFoundException(msg, nmParamApplic);
	}

	if (resultFiltered.isEmpty()) {
	    TiAppart nextTiAppart = null;
	    switch (tiAppart) {
	    case ENTE:
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
	return getValoreParamApplic(URL_ASSOCIAZIONE_UTENTE_CF, null, null,
		TipoIamVGetValAppart.APPLIC);
    }

    public String getUrlBackAssociazioneUtenteCf() {
	return getValoreParamApplic(URL_BACK_ASSOCIAZIONE_UTENTE_CF, null, null,
		TipoIamVGetValAppart.APPLIC);
    }

}
