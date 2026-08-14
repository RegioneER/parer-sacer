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

package it.eng.parer.objectstorage.helper;

import java.math.BigDecimal;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import it.eng.parer.entity.DecAaTipoFascicolo;
import it.eng.parer.entity.DecBackend;
import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.exception.ParamApplicNotFoundException;
import it.eng.parer.objectstorage.dto.BackendStorage;
import it.eng.parer.objectstorage.ejb.ObjectStorageConfigCache;
import it.eng.parer.objectstorage.exceptions.BackendException;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.ws.utils.CostantiDB.ParametroAppl;

@Stateless(mappedName = "BackendHelper")
@LocalBean
public class BackendHelper {

    private static final String NO_PARAMETER = "Impossibile ottenere il parametro {0}";
    private static final String NOME_BACKEND_PARAMETER = "nomeBackend";

    @EJB
    protected ConfigurationHelper configurationHelper;

    @EJB
    protected ObjectStorageConfigCache configCache;

    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    public enum BACKEND_VERSAMENTO {
        DATABASE, OBJECT_STORAGE
    }

    /**
     * Ottieni la tipologia di backend per salvare i BLOB relativi al versamento sincrono
     *
     * @param idTipoUnitaDoc id della tipologia dell'UD
     * @param paramName      nome del parametro
     *
     * @return Configurazione del backend. Può essere, per esempio OBJECT_STORAGE_STAGING oppure
     *         DATABASE_PRIMARIO
     *
     * @throws BackendException in caso di errore
     */
    public String getBackendByParamName(long idTipoUnitaDoc, String paramName)
            throws BackendException {
        String backendDatiVersamento = null;
        try {
            return getParameter(idTipoUnitaDoc, paramName);

        } catch (ParamApplicNotFoundException | IllegalArgumentException e) {
            throw BackendException.builder().message(
                    "Impossibile ottenere il parametro {0} con id tipo unita doc {1} e tipo creazione {2}",
                    backendDatiVersamento, idTipoUnitaDoc, paramName).cause(e).build();
        }
    }

    private String getParameter(long idTipoUnitaDoc, String parameterName) {
        DecTipoUnitaDoc tipoUd = entityManager.find(DecTipoUnitaDoc.class, idTipoUnitaDoc);
        long idStrut = tipoUd.getOrgStrut().getIdStrut();

        long idAmbiente = tipoUd.getOrgStrut().getOrgEnte().getOrgAmbiente().getIdAmbiente();

        return configurationHelper.getValoreParamApplicByTipoUd(parameterName,
                BigDecimal.valueOf(idAmbiente), BigDecimal.valueOf(idStrut),
                BigDecimal.valueOf(idTipoUnitaDoc));
    }

    // MEV#30397
    /**
     * Ottieni la configurazione applicativa relativa alla tipologia di Backend per il salvataggio
     * degli elenchi indici aip
     *
     * @param idStrut id struttura
     *
     * @return configurazione del backend. Può essere, per esempio OBJECT_STORAGE_STAGING oppure
     *         DATABASE_PRIMARIO
     *
     * @throws BackendException in caso di errore di recupero del parametro
     */
    public String getBackendElenchiIndiciAip(long idStrut) throws BackendException {
        try {
            OrgStrut strut = entityManager.find(OrgStrut.class, idStrut);

            long idAmbiente = strut.getOrgEnte().getOrgAmbiente().getIdAmbiente();
            return configurationHelper.getValoreParamApplicByStrut(
                    ParametroAppl.BACKEND_ELENCHI_INDICI_AIP, BigDecimal.valueOf(idAmbiente),
                    BigDecimal.valueOf(idStrut));

        } catch (ParamApplicNotFoundException | IllegalArgumentException e) {
            throw BackendException.builder()
                    .message(NO_PARAMETER, ParametroAppl.BACKEND_ELENCHI_INDICI_AIP).cause(e)
                    .build();
        }
    }
    // end MEV#30397

    // MEV#30400
    /**
     * Ottieni la configurazione applicativa relativa alla tipologia di Backend per il salvataggio
     * degli indici aip di serie di ud
     *
     * @param idStrut id struttura
     *
     * @return configurazione del backend. Può essere, per esempio OBJECT_STORAGE_STAGING oppure
     *         DATABASE_PRIMARIO
     *
     * @throws BackendException in caso di errore di recupero del parametro
     */
    public String getBackendIndiciAipSerieUD(long idStrut) throws BackendException {
        try {
            OrgStrut strut = entityManager.find(OrgStrut.class, idStrut);

            long idAmbiente = strut.getOrgEnte().getOrgAmbiente().getIdAmbiente();
            return configurationHelper.getValoreParamApplicByStrut(
                    ParametroAppl.BACKEND_INDICI_AIP_SERIE_UD, BigDecimal.valueOf(idAmbiente),
                    BigDecimal.valueOf(idStrut));

        } catch (ParamApplicNotFoundException | IllegalArgumentException e) {
            throw BackendException.builder()
                    .message(NO_PARAMETER, ParametroAppl.BACKEND_INDICI_AIP_SERIE_UD).cause(e)
                    .build();
        }
    }
    // end MEV#30400

    public DecBackend getBackendEntity(String nomeBackend) {
        Long cachedId = configCache.getBackendId(nomeBackend);
        if (cachedId != null) {
            return entityManager.getReference(DecBackend.class, cachedId);
        }
        return loadBackendEntity(nomeBackend);
    }

    private DecBackend loadBackendEntity(String nomeBackend) {
        TypedQuery<DecBackend> query = entityManager.createQuery(
                "Select d from DecBackend d where d.nmBackend = :nomeBackend", DecBackend.class);
        query.setParameter(NOME_BACKEND_PARAMETER, nomeBackend);
        DecBackend backend = query.getSingleResult();
        configCache.putBackendIdIfAbsent(nomeBackend, backend.getIdDecBackend());
        return backend;
    }

    /**
     * Ottieni la configurazione del backend a partire dal nome del backend
     *
     * @param nomeBackend per esempio "OBJECT_STORAGE_PRIMARIO"
     *
     * @return Informazioni sul Backend identificato
     *
     * @throws BackendException in caso di errore
     */
    public BackendStorage getBackend(String nomeBackend) throws BackendException {
        BackendStorage cached = configCache.getBackend(nomeBackend);
        if (cached != null) {
            return cached;
        }
        try {
            DecBackend backend = loadBackendEntity(nomeBackend);
            final BackendStorage.STORAGE_TYPE type = BackendStorage.STORAGE_TYPE
                    .valueOf(backend.getNmTipoBackend());
            final String backendName = backend.getNmBackend();

            BackendStorage loaded = new BackendStorage() {
                private static final long serialVersionUID = 5092016605462729859L;

                @Override
                public BackendStorage.STORAGE_TYPE getType() {
                    return type;
                }

                @Override
                public String getBackendName() {
                    return backendName;
                }
            };
            return configCache.putBackendIfAbsent(nomeBackend, loaded);
        } catch (IllegalArgumentException | NonUniqueResultException e) {
            throw BackendException.builder()
                    .message("Impossibile ottenere le informazioni di backend").cause(e).build();
        }
    }

    // end MAC #37222 - creazione chiave secondo le "linee guida"

    /**
     * Ottieni la tipologia di backend per salvare i BLOB relativi al versamento sincrono
     *
     * @param idAaTipoFascicolo id periodo della tipologia del fascicolo
     * @param paramName         nome del parametro
     *
     * @return Configurazione del backend. Può essere, per esempio OBJECT_STORAGE_STAGING oppure
     *         DATABASE_PRIMARIO
     *
     * @throws BackendException in caso di errore
     */
    public String getBackendByParamNameFasc(long idAaTipoFascicolo, String paramName)
            throws BackendException {
        String backendDatiVersamento = null;
        try {
            return getParameterFasc(idAaTipoFascicolo, paramName);

        } catch (ParamApplicNotFoundException | IllegalArgumentException e) {
            throw BackendException.builder().message(
                    "Impossibile ottenere il parametro {0} con id aa tipo fascicolo {1} e tipo creazione {2}",
                    backendDatiVersamento, idAaTipoFascicolo, paramName).cause(e).build();
        }
    }

    private String getParameterFasc(long idAaTipoFascicolo, String parameterName) {
        DecAaTipoFascicolo aaTipoFasc = entityManager.find(DecAaTipoFascicolo.class,
                idAaTipoFascicolo);
        long idStrut = aaTipoFasc.getDecTipoFascicolo().getOrgStrut().getIdStrut();

        long idAmbiente = aaTipoFasc.getDecTipoFascicolo().getOrgStrut().getOrgEnte()
                .getOrgAmbiente().getIdAmbiente();

        return configurationHelper.getValoreParamApplicByAaTipoFasc(parameterName,
                BigDecimal.valueOf(idAmbiente), BigDecimal.valueOf(idStrut),
                BigDecimal.valueOf(idAaTipoFascicolo));
    }

    // end MEV #30398

    /**
     * Ottieni la configurazione applicativa relativa alla tipologia di Backend per il salvataggio
     * degli elenchi indici aip fascicoli
     *
     * @param idStrut id struttura
     *
     * @return configurazione del backend. Può essere, per esempio OBJECT_STORAGE_STAGING oppure
     *         DATABASE_PRIMARIO
     *
     * @throws BackendException in caso di errore di recupero del parametro
     */
    public String getBackendElenchiIndiciAipFasc(long idStrut) throws BackendException {
        try {
            OrgStrut strut = entityManager.find(OrgStrut.class, idStrut);

            long idAmbiente = strut.getOrgEnte().getOrgAmbiente().getIdAmbiente();
            return configurationHelper.getValoreParamApplicByStrut(
                    ParametroAppl.BACKEND_ELENCHI_INDICI_AIP_FASCICOLI,
                    BigDecimal.valueOf(idAmbiente), BigDecimal.valueOf(idStrut));

        } catch (ParamApplicNotFoundException | IllegalArgumentException e) {
            throw BackendException.builder()
                    .message(NO_PARAMETER, ParametroAppl.BACKEND_ELENCHI_INDICI_AIP_FASCICOLI)
                    .cause(e).build();
        }
    }

}
