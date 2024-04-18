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

package it.eng.parer.serie.helper;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.DecAttribDatiSpec;
import it.eng.parer.entity.DecCampoInpUd;
import it.eng.parer.entity.DecCampoOutSelUd;
import it.eng.parer.entity.DecFiltroSelUd;
import it.eng.parer.entity.DecFiltroSelUdAttb;
import it.eng.parer.entity.DecFiltroSelUdDato;
import it.eng.parer.entity.DecModelloFiltroTiDoc;
import it.eng.parer.entity.DecNotaTipoSerie;
import it.eng.parer.entity.DecOutSelUd;
import it.eng.parer.entity.DecRegistroUnitaDoc;
import it.eng.parer.entity.DecTipoDoc;
import it.eng.parer.entity.DecTipoNotaSerie;
import it.eng.parer.entity.DecTipoSerie;
import it.eng.parer.entity.DecTipoSerieUd;
import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.parer.entity.SerSerie;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.slite.gen.tablebean.DecAttribDatiSpecRowBean;
import it.eng.parer.slite.gen.tablebean.DecAttribDatiSpecTableBean;
import it.eng.parer.slite.gen.tablebean.DecFiltroSelUdAttbRowBean;
import it.eng.parer.slite.gen.tablebean.DecFiltroSelUdAttbTableBean;
import it.eng.parer.util.Utils;
import it.eng.parer.viewEntity.DecVChkModificaTipoSerie;
import it.eng.parer.web.dto.DecFiltroSelUdAttbBean;
import it.eng.parer.web.dto.DecFiltroSelUdDatoBean;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.Transform;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Stateless
@LocalBean
public class TipoSerieHelper extends GenericHelper {

    private static final Logger log = LoggerFactory.getLogger(TipoSerieHelper.class);
    List<String> blackList = new ArrayList<String>();

    public TipoSerieHelper() {
        /* Default */
    }

    public List<DecTipoSerie> retrieveDecTipoSerieList(BigDecimal idStrut, boolean filterValid) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT tipoSerie FROM DecTipoSerie tipoSerie WHERE tipoSerie.orgStrut.idStrut = :idStrut and tipoSerie.flTipoSeriePadre = '0' ");
        if (filterValid) {
            queryStr.append("AND tipoSerie.dtIstituz <= :filterDate AND tipoSerie.dtSoppres >= :filterDate ");
        }
        queryStr.append("ORDER BY tipoSerie.nmTipoSerie");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        if (filterValid) {
            query.setParameter("filterDate", Calendar.getInstance().getTime());
        }
        return query.getResultList();
    }

    public List<DecTipoSerie> retrieveDecTipoSerieList(long idStrut, long idModelloTipoSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT tipoSerie FROM DecTipoSerie tipoSerie WHERE tipoSerie.orgStrut.idStrut = :idStrut AND tipoSerie.decModelloTipoSerie.idModelloTipoSerie = :idModelloTipoSerie AND tipoSerie.flTipoSeriePadre = '0' AND tipoSerie.dtIstituz <= :filterDate AND tipoSerie.dtSoppres >= :filterDate ");
        query.setParameter("idStrut", idStrut);
        query.setParameter("idModelloTipoSerie", idModelloTipoSerie);
        query.setParameter("filterDate", Calendar.getInstance().getTime());
        return query.getResultList();
    }

    public List<DecTipoSerie> retrieveDecTipoSerieList(BigDecimal idStrut, boolean flTipoSeriePadre,
            String tipoContenSerie, boolean filterValids) {
        StringBuilder builder = new StringBuilder(
                "SELECT tipoSerie FROM DecTipoSerie tipoSerie WHERE tipoSerie.orgStrut.idStrut = :idStrut and tipoSerie.flTipoSeriePadre = :flTipoSeriePadre ");
        if (filterValids) {
            builder.append("AND tipoSerie.dtIstituz <= :filterDate AND tipoSerie.dtSoppres >= :filterDate ");
        }
        if (StringUtils.isNotBlank(tipoContenSerie)) {
            builder.append("AND tipoSerie.tipoContenSerie = :tipoContenSerie ");
        }
        builder.append("ORDER BY tipoSerie.nmTipoSerie");
        Query query = getEntityManager().createQuery(builder.toString());
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        query.setParameter("flTipoSeriePadre", flTipoSeriePadre ? JobConstants.DB_TRUE : JobConstants.DB_FALSE);
        if (filterValids) {
            query.setParameter("filterDate", Calendar.getInstance().getTime());
        }
        if (StringUtils.isNotBlank(tipoContenSerie)) {
            query.setParameter("tipoContenSerie", tipoContenSerie);
        }
        return query.getResultList();
    }

    public Long countDecTipoSerie(BigDecimal idModelloTipoSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT COUNT(tipoSerie) FROM DecTipoSerie tipoSerie WHERE tipoSerie.decModelloTipoSerie.idModelloTipoSerie = :idModelloTipoSerie");
        query.setParameter("idModelloTipoSerie", longFromBigDecimal(idModelloTipoSerie));
        return (Long) query.getSingleResult();
    }

    public List<DecTipoSerie> getDecTipoSerie(long idUser, BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut,
            String isAttivo, String tipiSerieNoGenModello, BigDecimal idModelloTipoSerie) {
        StringBuilder queryStr = new StringBuilder("SELECT tipoSerie FROM DecTipoSerie tipoSerie "
                + "JOIN tipoSerie.orgStrut strut, IamAbilOrganiz abilOrganiz "
                + "WHERE strut.idStrut = abilOrganiz.idOrganizApplic " + "AND abilOrganiz.iamUser.idUserIam = :idUser "
                + "AND tipoSerie.flTipoSeriePadre = '0' ");

        if (idAmbiente != null) {
            queryStr.append("AND strut.orgEnte.orgAmbiente.idAmbiente = :idAmbiente ");
        }

        if (idEnte != null) {
            queryStr.append("AND strut.orgEnte.idEnte = :idEnte ");
        }

        if (idStrut != null) {
            queryStr.append("AND strut.idStrut = :idStrut ");
        }

        Date dataOdierna = new Date();
        if (isAttivo != null) {
            if (isAttivo.equals("1")) {
                queryStr.append("AND (tipoSerie.dtIstituz <= :dataOdierna AND tipoSerie.dtSoppres >= :dataOdierna) ");
            } else {
                queryStr.append("AND (tipoSerie.dtIstituz > :dataOdierna OR tipoSerie.dtSoppres < :dataOdierna) ");
            }
        }

        if (tipiSerieNoGenModello != null && tipiSerieNoGenModello.equals("1")) {
            queryStr.append("AND tipoSerie.decModelloTipoSerie IS NULL ");
        }

        if (idModelloTipoSerie != null) {
            queryStr.append("AND tipoSerie.decModelloTipoSerie.idModelloTipoSerie = :idModelloTipoSerie ");
        }

        queryStr.append("ORDER BY tipoSerie.nmTipoSerie ");

        Query query = getEntityManager().createQuery(queryStr.toString());

        query.setParameter("idUser", idUser);

        if (idAmbiente != null) {
            query.setParameter("idAmbiente", longFromBigDecimal(idAmbiente));
        }
        if (idEnte != null) {
            query.setParameter("idEnte", longFromBigDecimal(idEnte));
        }
        if (idStrut != null) {
            query.setParameter("idStrut", longFromBigDecimal(idStrut));
        }
        if (isAttivo != null) {
            query.setParameter("dataOdierna", dataOdierna);
        }

        if (idModelloTipoSerie != null) {
            query.setParameter("idModelloTipoSerie", longFromBigDecimal(idModelloTipoSerie));
        }

        return query.getResultList();
    }

    public List<DecRegistroUnitaDoc> getDecRegistroUnitaDocPerTipoSerie(BigDecimal idTipoSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT tsu.decRegistroUnitaDoc FROM DecTipoSerieUd tsu WHERE tsu.decTipoSerie.idTipoSerie = :idTipoSerie ORDER BY tsu.decRegistroUnitaDoc.cdRegistroUnitaDoc");
        query.setParameter("idTipoSerie", longFromBigDecimal(idTipoSerie));
        return query.getResultList();
    }

    public DecTipoUnitaDoc getDecTipoUnitaDocByIdRegistroIdSerie(long idRegistroUnitaDoc, BigDecimal idTipoSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT tsu.decTipoUnitaDoc FROM DecTipoSerieUd tsu WHERE tsu.decTipoSerie.idTipoSerie = :idTipoSerie and tsu.decRegistroUnitaDoc.idRegistroUnitaDoc=:idRegistroUnitaDoc");
        query.setParameter("idTipoSerie", longFromBigDecimal(idTipoSerie));
        query.setParameter("idRegistroUnitaDoc", idRegistroUnitaDoc);
        List<DecTipoUnitaDoc> lista = query.getResultList();
        return (lista != null && !lista.isEmpty() ? lista.get(0) : null);
    }

    public List<String> getDecXsdDatiSpecByCampoInpUd(BigDecimal id) {

        Query query = this.getEntityManager().createQuery(
                "SELECT DISTINCT v.decXsdDatiSpec.cdVersioneXsd FROM DecXsdAttribDatiSpec v JOIN v.decAttribDatiSpec u JOIN u.decCampoInpUds ciu where ciu.idCampoInpUd=:id",
                String.class);
        query.setParameter("id", longFromBigDecimal(id));

        return query.getResultList();
    }

    public List<DecCampoInpUd> getDecCampoInpUdPerTipoSerie(BigDecimal idTipoSerie, String ti_campo) {
        List<DecCampoInpUd> result = null;
        StringBuilder sQuery = new StringBuilder();
        sQuery.append("Select inp from DecCampoInpUd inp where inp.decTipoSerie.idTipoSerie=:idTipoSerie");
        if (ti_campo != null) {
            sQuery.append(" and inp.tiCampo=:ti_campo");
        }
        Query query = getEntityManager().createQuery(sQuery.toString());
        query.setParameter("idTipoSerie", longFromBigDecimal(idTipoSerie));
        if (ti_campo != null) {
            query.setParameter("ti_campo", ti_campo);
        }
        result = query.getResultList();
        return result;
    }

    public DecTipoSerie getDecTipoSerieByName(String nmTipoSerie, Long idStrut) {
        DecTipoSerie result = null;
        Query query = getEntityManager().createQuery(
                "SELECT tipoSerie FROM DecTipoSerie tipoSerie WHERE tipoSerie.nmTipoSerie = :nmTipoSerie and tipoSerie.orgStrut.idStrut=:idStrut AND tipoSerie.dtIstituz <= :filterDate AND tipoSerie.dtSoppres >= :filterDate");
        query.setParameter("nmTipoSerie", nmTipoSerie);
        query.setParameter("idStrut", idStrut);
        query.setParameter("filterDate", Calendar.getInstance().getTime());
        List<DecTipoSerie> lista = query.getResultList();
        if (lista != null && !lista.isEmpty()) {
            result = lista.get(0);
        }
        return result;
    }

    public boolean checkIsTipoSerieUsed(BigDecimal idTipoSerie, int numOfUses) {
        List<SerSerie> lista = getSeriePerTipoSerie(idTipoSerie);
        return !lista.isEmpty() && lista.size() > numOfUses;
    }

    public List<SerSerie> getSeriePerTipoSerie(BigDecimal idTipoSerie) {
        Query query = getEntityManager()
                .createQuery("Select sser from SerSerie sser where sser.decTipoSerie.idTipoSerie=:idTipoSerie ");
        query.setParameter("idTipoSerie", longFromBigDecimal(idTipoSerie));
        return query.getResultList();
    }

    public List<DecRegistroUnitaDoc> getDecRegistroUnitaDocListPerSerieByIdStrut(long idStrut) {
        String queryStr = "SELECT registroUnitaDoc FROM DecRegistroUnitaDoc registroUnitaDoc "
                + "WHERE registroUnitaDoc.orgStrut.idStrut=:idStrut and registroUnitaDoc.flCreaSerie='1'";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrut", idStrut);

        return query.getResultList();
    }

    public List<DecTipoSerieUd> getDecTipoSerieUd(BigDecimal idTipoSerie, BigDecimal idRegistroUnitaDoc,
            BigDecimal idTipoUnitaDoc) {
        List<DecTipoSerieUd> result = null;
        String tipoSerieUdQuery = "SELECT tipoSerieUd FROM DecTipoSerieUd tipoSerieUd "
                + "WHERE tipoSerieUd.decTipoSerie.idTipoSerie=:idTipoSerie ";
        if (idRegistroUnitaDoc != null) {
            tipoSerieUdQuery += "and tipoSerieUd.decRegistroUnitaDoc.idRegistroUnitaDoc=:idRegistroUnitaDoc ";
        }
        if (idTipoUnitaDoc != null) {
            tipoSerieUdQuery += "and tipoSerieUd.decTipoUnitaDoc.idTipoUnitaDoc=:idTipoUnitaDoc";
        }
        Query query = getEntityManager().createQuery(tipoSerieUdQuery);
        query.setParameter("idTipoSerie", longFromBigDecimal(idTipoSerie));
        if (idRegistroUnitaDoc != null) {
            query.setParameter("idRegistroUnitaDoc", longFromBigDecimal(idRegistroUnitaDoc));
        }
        if (idTipoUnitaDoc != null) {
            query.setParameter("idTipoUnitaDoc", longFromBigDecimal(idTipoUnitaDoc));
        }
        List<DecTipoSerieUd> lista = query.getResultList();
        if (lista != null && !lista.isEmpty()) {
            result = lista;
        }
        return result;
    }

    public List<DecFiltroSelUd> getDecFiltroSelUdList(long idTipoSerieUd) {
        String queryStr = "SELECT decFiltroSelUd from "
                + "DecFiltroSelUd decFiltroSelUd where decFiltroSelUd.decTipoSerieUd.idTipoSerieUd=:idTipoSerieUd";

        Query query = getEntityManager().createQuery(queryStr);

        query.setParameter("idTipoSerieUd", idTipoSerieUd);

        return query.getResultList();
    }

    public List<DecFiltroSelUd> getDecFiltroSelUdList(BigDecimal idTipoSerie, CostantiDB.TipoFiltroSerieUd tiFiltro) {
        String queryStr = "SELECT filtroSelUd FROM DecFiltroSelUd filtroSelUd "
                + "WHERE filtroSelUd.decTipoSerieUd.decTipoSerie.idTipoSerie = :idTipoSerie "
                + "AND filtroSelUd.tiFiltro = :tiFiltro";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idTipoSerie", idTipoSerie.longValue());
        query.setParameter("tiFiltro", tiFiltro.name());
        return query.getResultList();
    }

    public List<DecTipoDoc> getDecTipoDocPrincipalePerTipoUnitaDoc(BigDecimal idTipoUnitaDoc) {

        List<DecTipoDoc> result = null;
        String sQuery = "Select dtda.decTipoDoc from DecTipoDocAmmesso dtda "
                + "where dtda.decTipoDoc.flTipoDocPrincipale='1' "
                + "and dtda.decTipoStrutUnitaDoc.decTipoUnitaDoc.idTipoUnitaDoc=:idTipoUnitaDoc";
        Query query = getEntityManager().createQuery(sQuery);
        query.setParameter("idTipoUnitaDoc", longFromBigDecimal(idTipoUnitaDoc));
        result = query.getResultList();
        return result;
    }

    public void saveFiltrIDatiSpecTipoSerieUd(Long idTipoSerieUd,
            DecFiltroSelUdAttbTableBean filtroSelUdDatiSpecificiCompilati,
            List<DecFiltroSelUdAttbBean> listaDatiSpecOnLine) throws EMFError {
        // Creo una lista di DecFiltroSelUdAttb che verrà passata all'istanza di DecTipoSerieUd
        List<DecFiltroSelUdAttb> listDecFiltroSelUdAttb = new ArrayList();
        // Creo una lista di DecFiltroSelUdDato che verrà passata all'istanza di DecFiltroSelUdAttb
        List<DecFiltroSelUdDato> listDecFiltroSelUdDato = new ArrayList();

        // Ottengo il record criterio di raggruppamento corrispondente all'id passato come parametro
        DecTipoSerieUd decTipoSerieUd = getEntityManager().find(DecTipoSerieUd.class, idTipoSerieUd);

        // Se il criterio già esisteva, aggiorno i dati specifici che potrebbero essere stati modificati
        deleteDecFiltroSelUdAttbByIdTipoSerieUd(idTipoSerieUd);

        try {
            // Scorro DecFiltroSelUdAttbTableBean (lista dati specifici compilati)
            for (DecFiltroSelUdAttbRowBean rigaDecFiltroSelUdAttb : filtroSelUdDatiSpecificiCompilati) {
                // Mi creo l'entity associata alla tabella DecFiltroSelUdAttb da salvare su DB
                DecFiltroSelUdAttb dcds = (DecFiltroSelUdAttb) Transform.rowBean2Entity(rigaDecFiltroSelUdAttb);

                // ASSOCIAZIONE PADRE-FIGLIO
                for (DecFiltroSelUdAttbBean datoSpecBean : listaDatiSpecOnLine) {
                    if (datoSpecBean.getNmAttribDatiSpec().equals(dcds.getNmAttribDatiSpec())) {
                        List<DecFiltroSelUdDatoBean> dcabList = datoSpecBean.getDecFiltroSelUdDatos();
                        for (DecFiltroSelUdDatoBean dcab : dcabList) {
                            // ASSOCIAZIONE FIGLIO-PADRE
                            DecFiltroSelUdDato decFiltroSelUdDato = new DecFiltroSelUdDato();
                            decFiltroSelUdDato.setTiEntitaSacer(dcab.getTiEntitaSacer());
                            decFiltroSelUdDato.setNmTipoUnitaDoc(dcab.getNmTipoUnitaDoc());
                            decFiltroSelUdDato.setNmTipoDoc(dcab.getNmTipoDoc());
                            String dsVers = dcab.getDsListaVersioniXsd();
                            dsVers = dsVers.substring(5);
                            decFiltroSelUdDato.setDsListaVersioniXsd(dsVers);

                            Long id = dcab.getIdAttribDatiSpec().longValue();
                            DecAttribDatiSpec dads = getEntityManager().find(DecAttribDatiSpec.class, id);
                            decFiltroSelUdDato.setDecAttribDatiSpec(dads);
                            decFiltroSelUdDato.setDecFiltroSelUdAttb(dcds);
                            listDecFiltroSelUdDato.add(decFiltroSelUdDato);
                        }
                    }
                }

                // Setto la lista di DecFiltroSelUdDato alla proprietà dell'entity DecFiltroSelUdAttb
                dcds.setDecFiltroSelUdDatos(listDecFiltroSelUdDato);
                // Setto l'oggetto DecTipoSerieUd alla proprietà dell'entity DecFiltroSelUdAttb
                dcds.setDecTipoSerieUd(decTipoSerieUd);
                listDecFiltroSelUdAttb.add(dcds);
            }

            // Setto l'oggetto DecTipoSerieUd con la lista di DecFiltroSelUdAttb
            decTipoSerieUd.setDecFiltroSelUdAttbs(listDecFiltroSelUdAttb);
        } catch (Exception e) {
            log.error("Errore nel salvataggio di DecFiltroSelUdDato:" + e.getMessage(), e);
            throw new EMFError(EMFError.ERROR, e);
        }
    }

    public void deleteDecFiltroSelUdAttbByIdTipoSerieUd(Long idTipoSerieUd) {
        Query q = getEntityManager()
                .createQuery("DELETE FROM DecFiltroSelUdAttb u WHERE u.decTipoSerieUd.idTipoSerieUd = :idTipoSerieUd");
        q.setParameter("idTipoSerieUd", idTipoSerieUd);
        q.executeUpdate();
        getEntityManager().flush();
    }

    public List<DecFiltroSelUdAttb> getDecFiltroSelUdAttbList(BigDecimal idTipoSerieUd) {

        List<DecFiltroSelUdAttb> result = null;
        String sQuery = "Select u FROM DecFiltroSelUdAttb u " + "WHERE u.decTipoSerieUd.idTipoSerieUd = :idTipoSerieUd";
        Query query = getEntityManager().createQuery(sQuery);
        query.setParameter("idTipoSerieUd", longFromBigDecimal(idTipoSerieUd));
        result = query.getResultList();
        return result;
    }

    public List<DecOutSelUd> getDecOutSelUdPerTIpoSerieUd(BigDecimal idTipoSerieUd) {
        List<DecOutSelUd> result = null;
        Query query = getEntityManager().createQuery(
                "Select out from DecOutSelUd out where out.decTipoSerieUd.idTipoSerieUd=:idTipoSerieUd order by CASE  WHEN (out.tiOut = 'KEY_UD_SERIE') THEN 1 WHEN (out.tiOut = 'DT_UD_SERIE') THEN 2 WHEN (out.tiOut = 'INFO_UD_SERIE') THEN 3 WHEN (out.tiOut = 'DS_KEY_ORD_UD_SERIE') THEN 4 WHEN (out.tiOut = 'PG_UD _SERIE') THEN 5 ELSE 6 END");
        query.setParameter("idTipoSerieUd", longFromBigDecimal(idTipoSerieUd));
        result = query.getResultList();
        return result;
    }

    public List<DecCampoOutSelUd> getDecCampoOutSelUdPerDecOutSelUd(BigDecimal idOutSelUd, String ti_campo) {
        List<DecCampoOutSelUd> result = null;
        StringBuilder sQuery = new StringBuilder();
        sQuery.append("Select out from DecCampoOutSelUd out where out.decOutSelUd.idOutSelUd=:idOutSelUd");
        if (ti_campo != null) {
            sQuery.append(" and out.tiCampo=:ti_campo");
        }
        Query query = getEntityManager().createQuery(sQuery.toString());
        query.setParameter("idOutSelUd", longFromBigDecimal(idOutSelUd));
        if (ti_campo != null) {
            query.setParameter("ti_campo", ti_campo);
        }
        result = query.getResultList();
        return result;
    }

    public List<DecCampoOutSelUd> getDecCampoOutSelUd(BigDecimal idOutSelUd, String nmCampo, String tiCampo) {
        List<DecCampoOutSelUd> result = new ArrayList();
        if (idOutSelUd != null && nmCampo != null && tiCampo != null) {
            StringBuilder sQuery = new StringBuilder();
            sQuery.append("SELECT campoOutSelUd FROM DecCampoOutSelUd campoOutSelUd "
                    + "WHERE campoOutSelUd.decOutSelUd.idOutSelUd = :idOutSelUd "
                    + "AND campoOutSelUd.nmCampo = :nmCampo " + "AND campoOutSelUd.tiCampo = :tiCampo ");

            Query query = getEntityManager().createQuery(sQuery.toString());
            query.setParameter("idOutSelUd", longFromBigDecimal(idOutSelUd));
            query.setParameter("nmCampo", nmCampo);
            query.setParameter("tiCampo", tiCampo);
            result = query.getResultList();
        }
        return result;
    }

    public DecAttribDatiSpec getDecAttribDatiSpecByName(String nmAttribDatiSpec, BigDecimal idTipoDoc,
            BigDecimal idTipoUnitaDoc, BigDecimal idTipoCompDoc) {

        StringBuilder queryStr = new StringBuilder(
                "SELECT attribDatiSpec FROM DecAttribDatiSpec attribDatiSpec  WHERE attribDatiSpec.nmAttribDatiSpec= :nmAttribDatiSpec");

        if (idTipoDoc != null) {
            queryStr.append(" AND attribDatiSpec.decTipoDoc.idTipoDoc=:idTipoDoc");
        } else if (idTipoUnitaDoc != null) {
            queryStr.append(" AND attribDatiSpec.decTipoUnitaDoc.idTipoUnitaDoc=:idTipoUnitaDoc");
        } else if (idTipoCompDoc != null) {
            queryStr.append(" AND attribDatiSpec.decTipoCompDoc.idTipoCompDoc=:idTipoCompDoc");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("nmAttribDatiSpec", nmAttribDatiSpec);

        if (idTipoDoc != null) {
            query.setParameter("idTipoDoc", longFromBigDecimal(idTipoDoc));
        } else if (idTipoUnitaDoc != null) {
            query.setParameter("idTipoUnitaDoc", longFromBigDecimal(idTipoUnitaDoc));
        } else if (idTipoCompDoc != null) {
            query.setParameter("idTipoCompDoc", longFromBigDecimal(idTipoCompDoc));
        }

        List<DecAttribDatiSpec> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }

    public DecAttribDatiSpecTableBean getDecAttribDatiSpecTableBean(BigDecimal id,
            Constants.TipoEntitaSacer tipoEntitaSacer) {
        List<DecAttribDatiSpec> listaDatiSpec = getDecAttribDatiSpec(id, tipoEntitaSacer);
        DecAttribDatiSpecTableBean listaDatiSpecTableBean = new DecAttribDatiSpecTableBean();
        try {
            if (listaDatiSpec != null && !listaDatiSpec.isEmpty()) {
                listaDatiSpecTableBean = (DecAttribDatiSpecTableBean) Transform.entities2TableBean(listaDatiSpec);
                for (DecAttribDatiSpecRowBean row : listaDatiSpecTableBean) {
                    String versioniXsd = getVersioniXsd(row.getIdAttribDatiSpec(), id, tipoEntitaSacer);
                    if (!StringUtils.isBlank(versioniXsd)) {

                        String name = row.getNmAttribDatiSpec().concat(" ".concat(versioniXsd));
                        row.setNmAttribDatiSpec(name);
                    }
                }
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            log.error(e.getMessage(), e);
        }
        return listaDatiSpecTableBean;
    }

    public List<DecAttribDatiSpec> getDecAttribDatiSpec(BigDecimal id, Constants.TipoEntitaSacer tipoEntitaSacer) {
        String tmpTipoEntita = null;

        switch (tipoEntitaSacer) {
        case UNI_DOC:
            tmpTipoEntita = "u.decTipoUnitaDoc.idTipoUnitaDoc";
            break;
        case DOC:
            tmpTipoEntita = "u.decTipoDoc.idTipoDoc";
            break;
        case COMP:
            tmpTipoEntita = "u.decTipoCompDoc.idTipoCompDoc";
            break;
        default:
            break;
        }
        String queryStr = String.format(
                "SELECT DISTINCT u.idAttribDatiSpec,v.niOrdAttrib FROM DecXsdAttribDatiSpec v JOIN v.decAttribDatiSpec u "
                        + "WHERE %s = :id " + "ORDER BY v.niOrdAttrib,u.idAttribDatiSpec ",
                tmpTipoEntita);

        Query query = this.getEntityManager().createQuery(queryStr);
        query.setParameter("id", longFromBigDecimal(id));

        final List<Object[]> resultList = query.getResultList();
        final List<DecAttribDatiSpec> decAttribDatiSpecList = resultList.stream().map(record -> {
            Long idDec = Long.class.cast(record[0]);
            return findById(DecAttribDatiSpec.class, idDec.longValue());
        }).collect(Collectors.toList());
        return decAttribDatiSpecList;
    }

    public String getVersioniXsd(BigDecimal idAttribDatiSpec, BigDecimal id,
            Constants.TipoEntitaSacer tipoEntitaSacer) {
        String tmpTipoEntita = null;
        switch (tipoEntitaSacer) {
        case UNI_DOC:
            tmpTipoEntita = "dec.decTipoUnitaDoc.idTipoUnitaDoc";
            break;
        case DOC:
            tmpTipoEntita = "dec.decTipoDoc.idTipoDoc";
            break;
        case COMP:
            tmpTipoEntita = "dec.decTipoCompDoc.idTipoCompDoc";
            break;
        default:
            break;
        }

        String queryStr = String.format("SELECT DISTINCT dec.cdVersioneXsd FROM DecXsdAttribDatiSpec decXsd "
                + " JOIN decXsd.decXsdDatiSpec dec " + " JOIN decXsd.decAttribDatiSpec decAttrib "
                + " WHERE decAttrib.idAttribDatiSpec = :idattribdatispec " + " AND %s = :id", tmpTipoEntita);

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idattribdatispec", longFromBigDecimal(idAttribDatiSpec));
        query.setParameter("id", longFromBigDecimal(id));

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<String> listaVersioni = query.getResultList();

        return Utils.composeVersioniString(listaVersioni);
    }

    public DecAttribDatiSpec getDecAttribDatiSpecById(BigDecimal idAttribDatiSpec) {
        DecAttribDatiSpec result = null;
        String queryStr = "SELECT attribDatiSpec FROM DecAttribDatiSpec attribDatiSpec  WHERE attribDatiSpec.idAttribDatiSpec = :idAttribDatiSpec";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idAttribDatiSpec", longFromBigDecimal(idAttribDatiSpec));
        List<DecAttribDatiSpec> list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            result = list.get(0);
        }
        return result;
    }

    public int deleteDecOutSelUd(BigDecimal idOutSelUd) {
        int deleted = -1;
        deleteDecCampoOutSelUds(idOutSelUd, null);

        Query query = getEntityManager().createQuery("delete from DecOutSelUd u where u.idOutSelUd=:idOutSelUd");
        query.setParameter("idOutSelUd", longFromBigDecimal(idOutSelUd));
        deleted = query.executeUpdate();

        return deleted;
    }

    /**
     * Questa versione del metodo di cancellazione campo nelle regole di rappresentazione necessita anche di
     * "aggiornare" il campo stringa "dl_formato_out" della regola di rappresentazione stessa. Se non ho più valori nel
     * suddetto campo, elimino anche il record della regola di rappresentazione.
     *
     * @param idOutSelUd
     *            id unita doc
     * @param tipoCampo
     *            tipo camp
     * 
     * @return int risultato cancellazione 1/0
     */
    public int deleteDecCampoOutSelUdsForUpdateDaRegoleFiltraggio(BigDecimal idOutSelUd,
            CostantiDB.TipoCampo tipoCampo) {
        // Recupero il/i nome campo che sto cancellando
        String queryStrPre = "SELECT u.nmCampo FROM DecCampoOutSelUd u "
                + "WHERE u.decOutSelUd.idOutSelUd = :idOutSelUd ";
        if (tipoCampo != null) {
            queryStrPre = queryStrPre + "AND u.tiCampo = :tipoCampo ";
        }
        Query queryPre = getEntityManager().createQuery(queryStrPre);
        queryPre.setParameter("idOutSelUd", longFromBigDecimal(idOutSelUd));
        if (tipoCampo != null) {
            queryPre.setParameter("tipoCampo", tipoCampo.name());
        }
        List<String> nmCampoList = queryPre.getResultList();

        // Cancello il campo
        int deleted = -1;
        String queryStr = "DELETE FROM DecCampoOutSelUd u " + "WHERE u.decOutSelUd.idOutSelUd = :idOutSelUd ";
        if (tipoCampo != null) {
            queryStr = queryStr + "AND u.tiCampo = :tipoCampo ";
        }
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idOutSelUd", longFromBigDecimal(idOutSelUd));
        if (tipoCampo != null) {
            query.setParameter("tipoCampo", tipoCampo.name());
        }
        deleted = query.executeUpdate();
        getEntityManager().flush();

        // La cancellazione di uno o più campi comporta anche la cancellazione
        // degli stessi in "dl_formato_out" nella tabella padre decOutSelUd
        DecOutSelUd out = getEntityManager().find(DecOutSelUd.class, idOutSelUd.longValue());
        for (String nmCampo : nmCampoList) {
            if (out.getDlFormatoOut() != null) {
                out.setDlFormatoOut(out.getDlFormatoOut().replaceAll("<" + nmCampo + ">", ""));
            }
        }

        // Se non ho più valori in dl_formato_output, cancello il record
        if (out.getDlFormatoOut().equals("")) {
            getEntityManager().remove(out);
        }

        return deleted;
    }

    public int deleteDecCampoOutSelUds(BigDecimal idOutSelUd, CostantiDB.TipoCampo tipoCampo) {
        int deleted = -1;
        String queryStr = "DELETE FROM DecCampoOutSelUd u " + "WHERE u.decOutSelUd.idOutSelUd = :idOutSelUd ";
        if (tipoCampo != null) {
            queryStr = queryStr + "AND u.tiCampo = :tipoCampo ";
        }
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idOutSelUd", longFromBigDecimal(idOutSelUd));
        if (tipoCampo != null) {
            query.setParameter("tipoCampo", tipoCampo.name());
        }
        deleted = query.executeUpdate();
        getEntityManager().flush();
        return deleted;
    }

    public boolean isDecOutSelUdPresent(BigDecimal idOutSelUd, String tiOut) {
        boolean result = false;
        List<DecOutSelUd> list = null;
        Query query = getEntityManager()
                .createQuery("Select out from DecOutSelUd out where out.idOutSelUd<>:idOutSelUd and out.tiOut=:tiOut");
        query.setParameter("idOutSelUd", longFromBigDecimal(idOutSelUd));
        query.setParameter("tiOut", tiOut);
        list = query.getResultList();
        result = list != null && !list.isEmpty();
        return result;
    }

    public DecAttribDatiSpecTableBean getDecAttribDatiSpecTableBean(Constants.TipoEntitaSacer tipoEntitaSacer,
            List<Long> id) {
        List<DecAttribDatiSpec> listaDatiSpec = getDecAttribDatiSpecs(tipoEntitaSacer, id);

        DecAttribDatiSpecTableBean listaDatiSpecTableBean = new DecAttribDatiSpecTableBean();
        try {
            if (listaDatiSpec != null && !listaDatiSpec.isEmpty()) {

                listaDatiSpecTableBean = (DecAttribDatiSpecTableBean) Transform.entities2TableBean(listaDatiSpec);
            }
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException
                | IllegalArgumentException | InvocationTargetException e) {
            log.error(e.getMessage(), e);
        }
        return listaDatiSpecTableBean;
    }

    public List<DecAttribDatiSpec> getDecAttribDatiSpecs(Constants.TipoEntitaSacer tipoEntitaSacer, List<Long> id) {
        String tmpTipoEntita = null;

        switch (tipoEntitaSacer) {
        case UNI_DOC:
            tmpTipoEntita = "u.decTipoUnitaDoc.idTipoUnitaDoc";
            break;
        case DOC:
            tmpTipoEntita = "u.decTipoDoc.idTipoDoc";
            break;
        case COMP:
            tmpTipoEntita = "u.decTipoCompDoc.idTipoCompDoc";
            break;
        default:
            break;
        }

        String queryStr = String.format(
                "SELECT DISTINCT u.idAttribDatiSpec,v.niOrdAttrib FROM DecXsdAttribDatiSpec v JOIN v.decAttribDatiSpec u "
                        + "WHERE %s in :id ORDER BY v.niOrdAttrib,u.idAttribDatiSpec ",
                tmpTipoEntita);

        Query query = this.getEntityManager().createQuery(queryStr);
        query.setParameter("id", id);

        List<Object[]> resultList = query.getResultList();
        return resultList.stream().map(record -> {
            Long idDec = (Long) record[0];
            return findById(DecAttribDatiSpec.class, idDec);
        }).collect(Collectors.toList());
    }

    public BigDecimal getMaxPgPerTipoFiltro(CostantiDB.TipoFiltroSerieUd tipoFiltro, BigDecimal idTipoSerieUd) {
        Query query = getEntityManager().createQuery(
                "select max(u.pgFiltro) from DecFiltroSelUd u where u.tiFiltro=:tiFiltro AND u.decTipoSerieUd.idTipoSerieUd = :idTipoSerieUd",
                BigDecimal.class);
        query.setParameter("tiFiltro", tipoFiltro.name());
        query.setParameter("idTipoSerieUd", longFromBigDecimal(idTipoSerieUd));
        BigDecimal result = (BigDecimal) query.getSingleResult();
        return (result == null ? BigDecimal.ZERO : result);
    }

    public int deleteDecCampiInpUdPerTipoSerie(BigDecimal idTipoSerie) {
        int deleted = -1;
        Query query = getEntityManager()
                .createQuery("delete from DecCampoInpUd u where u.decTipoSerie.idTipoSerie=:idTipoSerie");
        query.setParameter("idTipoSerie", longFromBigDecimal(idTipoSerie));
        deleted = query.executeUpdate();
        getEntityManager().flush();
        return deleted;
    }

    public List<DecNotaTipoSerie> getDecNoteTipoSerie(BigDecimal idTipoSerie) {
        List<DecNotaTipoSerie> result = null;
        Query query = getEntityManager()
                .createQuery("Select u from DecNotaTipoSerie u where u.decTipoSerie.idTipoSerie=:idTipoSerie");
        query.setParameter("idTipoSerie", longFromBigDecimal(idTipoSerie));
        result = query.getResultList();
        return result;
    }

    public int deleteDecNoteTipoSerieById(BigDecimal idNotaTipoSerie) {
        int deleted = -1;
        Query query = getEntityManager()
                .createQuery("Delete from DecNotaTipoSerie u where u.idNotaTipoSerie=:idNotaTipoSerie");
        query.setParameter("idNotaTipoSerie", longFromBigDecimal(idNotaTipoSerie));
        deleted = query.executeUpdate();
        getEntityManager().flush();
        return deleted;
    }

    public DecNotaTipoSerie getDecNotaTipoSerieById(BigDecimal idDecNotaTipoSerie) {
        return getEntityManager().find(DecNotaTipoSerie.class, idDecNotaTipoSerie.longValueExact());
    }

    public List<DecTipoNotaSerie> getDecTipoNotaSerieList() {

        List<DecTipoNotaSerie> result = null;
        Query query = getEntityManager()
                .createQuery("Select tns from DecTipoNotaSerie tns where tns.flMolt='1' order by tns.cdTipoNotaSerie");
        result = query.getResultList();
        return result;
    }

    public List<DecTipoNotaSerie> getDecTipoNotaSerieNoFlMoltList(BigDecimal idTipoSerie) {

        List<DecTipoNotaSerie> result = null;
        Query query = getEntityManager().createQuery(
                "Select tns from DecTipoNotaSerie tns where tns.flMolt='0' and not exists ( select n from DecNotaTipoSerie n where n.decTipoNotaSerie.idTipoNotaSerie=tns.idTipoNotaSerie and n.decTipoSerie.idTipoSerie=:idTipoSerie) ");
        query.setParameter("idTipoSerie", longFromBigDecimal(idTipoSerie));
        result = query.getResultList();
        return result;
    }

    public List<DecTipoNotaSerie> getDecTipoNotaSerieNotInVerSerie(BigDecimal idVerSerie) {
        Query query = getEntityManager().createQuery(
                "Select tns from DecTipoNotaSerie tns where tns.flMolt='0' AND NOT EXISTS ( select n from SerNotaVerSerie n where n.decTipoNotaSerie.idTipoNotaSerie=tns.idTipoNotaSerie and n.serVerSerie.idVerSerie = :idVerSerie)");
        query.setParameter("idVerSerie", longFromBigDecimal(idVerSerie));
        return query.getResultList();
    }

    public List<DecTipoNotaSerie> getDecTipoNotaSerieNotInModelloSerie(BigDecimal idModelloTipoSerie) {
        Query query = getEntityManager().createQuery(
                "Select tns from DecTipoNotaSerie tns where tns.flMolt='0' AND NOT EXISTS ( select n from DecNotaModelloTipoSerie n where n.decTipoNotaSerie.idTipoNotaSerie=tns.idTipoNotaSerie and n.decModelloTipoSerie.idModelloTipoSerie = :idModelloTipoSerie)");
        query.setParameter("idModelloTipoSerie", longFromBigDecimal(idModelloTipoSerie));
        return query.getResultList();
    }

    public List<DecTipoNotaSerie> getAllDecTipoNotaSerieList() {

        List<DecTipoNotaSerie> result = null;
        Query query = getEntityManager()
                .createQuery("Select tns from DecTipoNotaSerie tns order by tns.cdTipoNotaSerie");
        result = query.getResultList();
        return result;
    }

    public int getMaxPgPerNotatipoSerie(BigDecimal idTipoSerie, BigDecimal idTipoNotaSerie) {
        List<DecNotaTipoSerie> result = null;
        Query query = getEntityManager().createQuery(
                "SELECT tns FROM DecNotaTipoSerie tns " + "WHERE tns.decTipoSerie.idTipoSerie = :idTipoSerie "
                        + "AND tns.decTipoNotaSerie.idTipoNotaSerie = :idTipoNotaSerie "
                        + "ORDER BY tns.pgNotaTipoSerie DESC ");
        query.setParameter("idTipoSerie", longFromBigDecimal(idTipoSerie));
        query.setParameter("idTipoNotaSerie", longFromBigDecimal(idTipoNotaSerie));
        result = query.getResultList();

        if (result.isEmpty()) {
            return 1;
        } else {
            return result.get(0).getPgNotaTipoSerie().intValue() + 1;
        }
    }

    public boolean getVersioniXsdPerTipoEntita(BigDecimal id, Constants.TipoEntitaSacer tipoEntitaSacer) {
        String tmpTipoEntita = null;
        boolean result = false;
        switch (tipoEntitaSacer) {
        case UNI_DOC:
            tmpTipoEntita = "dec.decTipoUnitaDoc.idTipoUnitaDoc";
            break;
        case DOC:
            tmpTipoEntita = "dec.decTipoDoc.idTipoDoc";
            break;
        case COMP:
            tmpTipoEntita = "dec.decTipoCompDoc.idTipoCompDoc";
            break;
        default:
            break;
        }

        String queryStr = String.format("SELECT DISTINCT dec.cdVersioneXsd FROM DecXsdAttribDatiSpec decXsd "
                + " JOIN decXsd.decXsdDatiSpec dec " + " JOIN decXsd.decAttribDatiSpec decAttrib " + " WHERE %s = :id",
                tmpTipoEntita);

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("id", longFromBigDecimal(id));

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<String> listaVersioni = query.getResultList();

        result = listaVersioni != null && !listaVersioni.isEmpty();

        return result;
    }

    public DecFiltroSelUd getFiltroSelUdByIdTipoDoc(BigDecimal idTipoDoc, BigDecimal idTipoSerieUd) {
        DecFiltroSelUd result = null;

        Query query = getEntityManager().createQuery(
                "select f from DecFiltroSelUd f where f.decTipoDoc.idTipoDoc=:idTipoDoc and f.decTipoSerieUd.idTipoSerieUd=:idTipoSerieUd");
        query.setParameter("idTipoDoc", longFromBigDecimal(idTipoDoc));
        query.setParameter("idTipoSerieUd", longFromBigDecimal(idTipoSerieUd));
        List<DecFiltroSelUd> list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            result = list.get(0);
        }
        return result;
    }

    public DecTipoDoc getDecTipoDocFromFiltroSelUdByIdTipoDoc(BigDecimal idTipoDoc, BigDecimal idTipoSerieUd) {
        DecTipoDoc result = null;
        Query query = getEntityManager().createQuery("select f.decTipoDoc from DecFiltroSelUd f "
                + "where f.decTipoDoc.idTipoDoc=:idTipoDoc " + "and f.decTipoSerieUd.idTipoSerieUd=:idTipoSerieUd ");
        query.setParameter("idTipoDoc", longFromBigDecimal(idTipoDoc));
        query.setParameter("idTipoSerieUd", longFromBigDecimal(idTipoSerieUd));
        List<DecTipoDoc> list = query.getResultList();
        if (list != null && !list.isEmpty()) {
            result = list.get(0);
        }
        return result;
    }

    public void deleteDecFiltroSelUdAttb(BigDecimal idFiltroSelUdAttb) {
        Query q = getEntityManager()
                .createQuery("DELETE FROM DecFiltroSelUdAttb u " + "WHERE u.idFiltroSelUdAttb = :idFiltroSelUdAttb ");
        q.setParameter("idFiltroSelUdAttb", longFromBigDecimal(idFiltroSelUdAttb));
        q.executeUpdate();
        getEntityManager().flush();
    }

    public void deleteDecFiltroSelUdAttbByIdTipoSerieENmAttribDatiSpecList(BigDecimal idTipoSerieUd,
            List<String> nmAttribDatiSpecList) {
        if (!nmAttribDatiSpecList.isEmpty()) {
            Query q = getEntityManager().createQuery(
                    "DELETE FROM DecFiltroSelUdAttb u " + "WHERE u.decTipoSerieUd.idTipoSerieUd = :idTipoSerieUd "
                            + "AND u.nmAttribDatiSpec IN (:nmAttribDatiSpecList) ");
            q.setParameter("idTipoSerieUd", longFromBigDecimal(idTipoSerieUd));
            q.setParameter("nmAttribDatiSpecList", nmAttribDatiSpecList);
            q.executeUpdate();
            getEntityManager().flush();
        }
    }

    public void deleteDecCampoInpUd(BigDecimal idCampoInpUd) {
        Query q = getEntityManager()
                .createQuery("DELETE FROM DecCampoInpUd u " + "WHERE u.idCampoInpUd = :idCampoInpUd ");
        q.setParameter("idCampoInpUd", longFromBigDecimal(idCampoInpUd));
        q.executeUpdate();
        getEntityManager().flush();
    }

    public boolean isTipoSerieModificabile(BigDecimal idTipoSerie) {
        final DecVChkModificaTipoSerie decVChkModificaTipoSerie = getEntityManager()
                .find(DecVChkModificaTipoSerie.class, idTipoSerie);
        return decVChkModificaTipoSerie.getFlModificaTipoSerie().equals("1");
    }

    public boolean existDecTipoSerieUdForRegistro(BigDecimal idRegistroUnitaDoc) {
        Query query = getEntityManager().createQuery("SELECT registro FROM DecRegistroUnitaDoc registro "
                + "WHERE registro.idRegistroUnitaDoc = :idRegistroUnitaDoc "
                + "AND EXISTS (SELECT tipoSerieUd FROM DecTipoSerieUd tipoSerieUd WHERE tipoSerieUd.decRegistroUnitaDoc = registro ) ");
        query.setParameter("idRegistroUnitaDoc", longFromBigDecimal(idRegistroUnitaDoc));
        return !query.getResultList().isEmpty();
    }

    public List<DecTipoSerie> retrieveDecTipoSerieForRegistro(BigDecimal idStrut, BigDecimal idRegistroUnitaDoc) {
        Query query = getEntityManager().createQuery(
                "SELECT DISTINCT tipoSerie FROM DecTipoSerieUd tipoSerieUd JOIN tipoSerieUd.decTipoSerie tipoSerie WHERE tipoSerie.orgStrut.idStrut = :idStrut AND tipoSerieUd.decRegistroUnitaDoc.idRegistroUnitaDoc = :idRegistroUnitaDoc AND tipoSerie.decModelloTipoSerie IS NOT NULL ");
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        query.setParameter("idRegistroUnitaDoc", longFromBigDecimal(idRegistroUnitaDoc));
        return query.getResultList();
    }

    /**
     * Controlla se un determinato registro è presente, più di una volta, in qualunque tipo serie attraverso la
     * relazione col tipo ud. Verifica dunque che l'associazione Registro-TipoUd (DecTipoSerieUd) sia presente, divisa
     * per tipi serie, più di una volta.
     *
     * @param idRegistroUnitaDoc
     *            id registro unita doc
     * 
     * @return true/false
     */
    public boolean multipleDecRegistroUnitaDocInTipiSerie(BigDecimal idRegistroUnitaDoc) {
        Query query = getEntityManager().createQuery("SELECT tipoSerie.idTipoSerie "
                + "FROM DecTipoSerieUd tipoSerieUd JOIN tipoSerieUd.decTipoSerie tipoSerie "
                + "JOIN tipoSerieUd.decRegistroUnitaDoc registroUnitaDoc "
                + "WHERE registroUnitaDoc.idRegistroUnitaDoc = :idRegistroUnitaDoc " + "GROUP BY tipoSerie "
                + "HAVING COUNT(registroUnitaDoc) > 1 ");
        query.setParameter("idRegistroUnitaDoc", longFromBigDecimal(idRegistroUnitaDoc));
        return !query.getResultList().isEmpty();
    }

    public boolean existsRelationsWithTipiSerie(long idTipoDato, Constants.TipoDato tipoDato) {
        StringBuilder queryStr = new StringBuilder("SELECT COUNT(tipoSerieUd) FROM DecTipoSerieUd tipoSerieUd ");
        boolean isTipoDoc = false;
        switch (tipoDato) {
        case REGISTRO:
            queryStr.append("WHERE tipoSerieUd.decRegistroUnitaDoc.idRegistroUnitaDoc = :idTipoDato ");
            break;
        case TIPO_UNITA_DOC:
            queryStr.append("WHERE tipoSerieUd.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoDato ");
            break;
        case TIPO_DOC:
            queryStr = new StringBuilder("SELECT COUNT(filtroSelUd) FROM DecFiltroSelUd filtroSelUd ");
            queryStr.append("WHERE filtroSelUd.decTipoDoc.idTipoDoc = :idTipoDato ");
            isTipoDoc = true;
            break;
        default:
            break;
        }
        if (!isTipoDoc) {
            queryStr.append(
                    " AND EXISTS (SELECT serie FROM SerSerie serie WHERE serie.decTipoSerie.idTipoSerie = tipoSerieUd.decTipoSerie.idTipoSerie)");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idTipoDato", idTipoDato);
        return (Long) query.getSingleResult() > 0;
    }

    public List<DecModelloFiltroTiDoc> getDecModelloFiltroTiDoc(BigDecimal idModelloTipoSerie, String nmTipoDoc) {
        List<DecModelloFiltroTiDoc> result = new ArrayList();
        if (idModelloTipoSerie != null && nmTipoDoc != null) {
            StringBuilder sQuery = new StringBuilder();
            sQuery.append("SELECT modelloFiltroTiDoc FROM DecModelloFiltroTiDoc modelloFiltroTiDoc "
                    + "WHERE modelloFiltroTiDoc.decModelloTipoSerie.idModelloTipoSerie = :idModelloTipoSerie "
                    + "AND modelloFiltroTiDoc.nmTipoDoc = :nmTipoDoc ");

            Query query = getEntityManager().createQuery(sQuery.toString());
            query.setParameter("idModelloTipoSerie", longFromBigDecimal(idModelloTipoSerie));
            query.setParameter("nmTipoDoc", nmTipoDoc);
            result = query.getResultList();
        }
        return result;
    }
}
