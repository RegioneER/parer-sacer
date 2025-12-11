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

package it.eng.parer.amministrazioneStrutture.gestioneTipoUd.helper;

import static it.eng.parer.util.Utils.bigDecimalFromLong;
import static it.eng.parer.util.Utils.longFromBigDecimal;
import static it.eng.parer.util.Utils.longListFrom;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import it.eng.parer.entity.AplSistemaVersante;
import it.eng.parer.entity.DecCategTipoUnitaDoc;
import it.eng.parer.entity.DecEstensioneFile;
import it.eng.parer.entity.DecTipoDocAmmesso;
import it.eng.parer.entity.DecTipoStrutUdReg;
import it.eng.parer.entity.DecTipoStrutUdSisVer;
import it.eng.parer.entity.DecTipoStrutUdXsd;
import it.eng.parer.entity.DecTipoStrutUnitaDoc;
import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.parer.entity.DecTipoUnitaDocAmmesso;
import it.eng.parer.entity.DecXsdDatiSpec;
import it.eng.parer.entity.OrgTipoServizio;
import it.eng.parer.grantedEntity.SIOrgAccordoEnte;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.slite.gen.viewbean.DecVLisTiUniDocAmsRowBean;
import it.eng.parer.viewEntity.DecVCalcTiServOnTipoUd;
import it.eng.parer.viewEntity.DecVLisTiUniDocAms;
import it.eng.parer.viewEntity.OrgVServTiServDaErog;

/**
 * Helper delle tipologie di unit\u00E0 documentaria
 *
 * @author Bonora_L
 */
@SuppressWarnings("unchecked")
@Stateless
@LocalBean
public class TipoUnitaDocHelper extends GenericHelper {

    /**
     * Verifica che esista una tipologia di unità documentaria il cui nome campo
     * <code>nmCampo</code> dato in input sia valorizzato con il valore <code>valoreCampo</code> per
     * la struttura data in input
     *
     * @param nmCampo        Nome campo da verificare
     * @param valoreCampo    Valore che assume il campo <code>nmCampo</code>
     * @param idStrut        Struttura in input
     * @param idTipoUnitaDoc tipo ud da escludere dal controllo
     *
     * @return true se esiste
     */
    public boolean checkTipoUnitaDocByCampoStringa(String nmCampo, String valoreCampo,
            BigDecimal idStrut, BigDecimal idTipoUnitaDoc) {
        Date now = Calendar.getInstance().getTime();
        Query query = getEntityManager().createQuery(
                "SELECT COUNT(tipoUd) FROM DecTipoUnitaDoc tipoUd WHERE tipoUd.orgStrut.idStrut = :idStrut AND tipoUd."
                        + nmCampo
                        + " = :valoreCampo AND tipoUd.dtIstituz <= :filterDate AND tipoUd.dtSoppres >= :filterDate"
                        + (idTipoUnitaDoc != null ? " AND tipoUd.idTipoUnitaDoc != :idTipoUnitaDoc"
                                : ""));
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        query.setParameter("valoreCampo", valoreCampo);
        query.setParameter("filterDate", now);
        if (idTipoUnitaDoc != null) {
            query.setParameter("idTipoUnitaDoc", longFromBigDecimal(idTipoUnitaDoc));
        }
        Long count = (Long) query.getSingleResult();
        return count > 0L;
    }

    /**
     * Ritorna l'oggetto DecTipoUnitaDoc dato il tipo ud e la struttura di riferimento
     *
     * @param nmTipoUnitaDoc nome tipo unita doc
     * @param idStrut        id struttura
     *
     * @return l'oggetto DecTipoUnitaDoc o null se inesistente
     */
    public DecTipoUnitaDoc getDecTipoUnitaDocByName(String nmTipoUnitaDoc, BigDecimal idStrut) {
        Query query = getEntityManager().createQuery(
                "SELECT tipoUnitaDoc FROM DecTipoUnitaDoc tipoUnitaDoc WHERE UPPER(tipoUnitaDoc.nmTipoUnitaDoc) = :nmTipoUnitaDoc AND tipoUnitaDoc.orgStrut.idStrut=:idStrut");
        query.setParameter("nmTipoUnitaDoc", nmTipoUnitaDoc.toUpperCase());
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        List<DecTipoUnitaDoc> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public DecTipoUnitaDocAmmesso getDecTipoUnitaDocAmmessoByName(BigDecimal idStrut,
            String nmTipoUnitaDoc, String cdRegistroUnitaDoc) {
        StringBuilder queryStr = new StringBuilder("SELECT u FROM DecTipoUnitaDocAmmesso u ");
        String whereWord = "WHERE ";

        if (idStrut != null) {
            queryStr.append(whereWord).append("u.decTipoUnitaDoc.orgStrut.idStrut = :idStrut ");
            whereWord = "AND ";
        }

        if (nmTipoUnitaDoc != null) {
            queryStr.append(whereWord)
                    .append("u.decTipoUnitaDoc.nmTipoUnitaDoc = :nmTipoUnitaDoc ");
            whereWord = "AND ";
        }

        if (cdRegistroUnitaDoc != null) {
            queryStr.append(whereWord)
                    .append("u.decRegistroUnitaDoc.cdRegistroUnitaDoc = :cdRegistroUnitaDoc ");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idStrut != null) {
            query.setParameter("idStrut", longFromBigDecimal(idStrut));
        }

        if (nmTipoUnitaDoc != null) {
            query.setParameter("nmTipoUnitaDoc", nmTipoUnitaDoc);

        }

        if (cdRegistroUnitaDoc != null) {
            query.setParameter("cdRegistroUnitaDoc", cdRegistroUnitaDoc);

        }

        List<DecTipoUnitaDocAmmesso> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    /**
     * Ritorna l'oggetto DecTipoUnitaDocAmmesso data l'associazione idRegistroUnitaDoc e
     * idTipoUnitaDoc
     *
     * @param idRegistroUnitaDoc id registro unita doc
     * @param idTipoUnitaDoc     id tipo unita doc
     *
     * @return l'oggetto DecTipoUnitaDocAmmesso o null se inesistente
     */
    public DecTipoUnitaDocAmmesso getDecTipoUnitaDocAmmessoByParentId(BigDecimal idRegistroUnitaDoc,
            BigDecimal idTipoUnitaDoc) {
        Query query = getEntityManager().createQuery(
                "SELECT tipoUnitaDocAmmesso FROM DecTipoUnitaDocAmmesso tipoUnitaDocAmmesso WHERE tipoUnitaDocAmmesso.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoUnitaDoc AND tipoUnitaDocAmmesso.decRegistroUnitaDoc.idRegistroUnitaDoc = :idRegistroUnitaDoc");
        query.setParameter("idTipoUnitaDoc", longFromBigDecimal(idTipoUnitaDoc));
        query.setParameter("idRegistroUnitaDoc", longFromBigDecimal(idRegistroUnitaDoc));

        List<DecTipoUnitaDocAmmesso> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }

    public List<DecTipoUnitaDocAmmesso> getDecTipoUnitaDocAmmessoByTipoUnitaDoc(
            BigDecimal idTipoUnitaDoc) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT tipoUnitaDocAmmesso FROM DecTipoUnitaDocAmmesso tipoUnitaDocAmmesso");
        String whereWord = " WHERE ";

        if (idTipoUnitaDoc != null) {
            queryStr.append(whereWord).append(
                    "tipoUnitaDocAmmesso.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoUnitaDoc ");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idTipoUnitaDoc != null) {
            query.setParameter("idTipoUnitaDoc", longFromBigDecimal(idTipoUnitaDoc));
        }

        return query.getResultList();
    }

    public List<DecXsdDatiSpec> getDecXsdDatiSpecByTipoUnitaDoc(BigDecimal idTipoUnitaDoc) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT xsdDatiSpec FROM DecXsdDatiSpec xsdDatiSpec ");
        String whereWord = "WHERE ";

        if (idTipoUnitaDoc != null) {
            queryStr.append(whereWord)
                    .append("xsdDatiSpec.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoUnitaDoc ");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idTipoUnitaDoc != null) {
            query.setParameter("idTipoUnitaDoc", longFromBigDecimal(idTipoUnitaDoc));
        }

        return query.getResultList();
    }

    /**
     * Recupero il tipo unità doc in base alle abilitazioni
     *
     * @param idUtente    id utente
     * @param idStruttura id struttura
     *
     * @return DecTipoUnitaDocTableBean bean entity DecTipoUnitaDoc
     */
    public List<DecTipoUnitaDoc> getTipiUnitaDocAbilitati(long idUtente, BigDecimal idStruttura) {
        List<BigDecimal> idStrutList = new ArrayList<>();
        idStrutList.add(idStruttura);
        return retrieveTipiUnitaDocAbilitatiDaStrutturaList(idUtente, idStrutList);
    }

    public List<DecTipoUnitaDoc> retrieveTipiUnitaDocAbilitatiDaStrutturaList(long idUtente,
            List<BigDecimal> idStrutturaList) {
        StringBuilder queryStr = new StringBuilder(
                "select distinct u from DecTipoUnitaDoc u, IamAbilTipoDato iatd where iatd.idTipoDatoApplic = u.idTipoUnitaDoc");
        queryStr.append(
                " and iatd.nmClasseTipoDato = 'TIPO_UNITA_DOC' and iatd.iamAbilOrganiz.iamUser.idUserIam = :idUtente");
        if (!idStrutturaList.isEmpty()) {
            queryStr.append(" and iatd.iamAbilOrganiz.idOrganizApplic IN (:idStrutturaList)");
        }
        queryStr.append(" ORDER BY u.nmTipoUnitaDoc");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idUtente", idUtente);
        if (!idStrutturaList.isEmpty()) {
            query.setParameter("idStrutturaList", idStrutturaList);
        }
        return query.getResultList();
    }

    public List<DecTipoUnitaDoc> retrieveDecTipoUnitaDocsFromTipoSerie(BigDecimal idStrut,
            BigDecimal idTipoSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT DISTINCT tipoSerieUd.decTipoUnitaDoc FROM DecTipoSerieUd tipoSerieUd JOIN tipoSerieUd.decTipoSerie tipoSerie WHERE tipoSerie.orgStrut.idStrut = :idStrut AND tipoSerie.dtIstituz <= :filterDate AND tipoSerie.dtSoppres >= :filterDate "
                        + (idTipoSerie != null ? " AND tipoSerie.idTipoSerie = :idTipoSerie "
                                : ""));
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        query.setParameter("filterDate", Calendar.getInstance().getTime());
        if (idTipoSerie != null) {
            query.setParameter("idTipoSerie", longFromBigDecimal(idTipoSerie));
        }
        return query.getResultList();
    }

    public long countDecTipoUnitaDoc(BigDecimal idModelloTipoSerie) {
        Query query = getEntityManager().createQuery(
                "SELECT COUNT(tipoUnitaDoc) FROM DecTipoUnitaDoc tipoUnitaDoc WHERE tipoUnitaDoc.decModelloTipoSerie.idModelloTipoSerie = :idModelloTipoSerie");
        query.setParameter("idModelloTipoSerie", longFromBigDecimal(idModelloTipoSerie));
        return (Long) query.getSingleResult();
    }

    public boolean existsServiziErogatiByStrutAndTipoServizio(long idTipoServizio,
            BigDecimal idStrut) {
        Query query = getEntityManager()
                .createQuery("SELECT servTiServDaErog FROM OrgVServTiServDaErog servTiServDaErog "
                        + "WHERE servTiServDaErog.idTipoServizio = :idTipoServizio "
                        + "AND servTiServDaErog.dtErog IS NOT NULL ");
        query.setParameter("idTipoServizio", bigDecimalFromLong(idTipoServizio));
        List<OrgVServTiServDaErog> servTiServDaErogList = query.getResultList();
        for (OrgVServTiServDaErog servTiServDaErog : servTiServDaErogList) {
            // Ricavo la lista degli id delle strutture, splittando le "," e togliendo gli spazi
            // bianchi, per sicurezza
            // anche all'inizio e alla fine
            List<String> idStrutStringList = Arrays
                    .asList(servTiServDaErog.getListStrut().trim().split("\\s*,\\s*"));
            for (String idStrutString : idStrutStringList) {
                if (idStrutString.equals(idStrut.toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    public DecTipoStrutUnitaDoc getDecTipoStrutUnitaDocByName(String nmTipoStrutUnitaDoc,
            BigDecimal idTipoUnitaDoc) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT tipoStrutUnitaDoc FROM DecTipoStrutUnitaDoc tipoStrutUnitaDoc WHERE tipoStrutUnitaDoc.nmTipoStrutUnitaDoc = :nmTipoStrutUnitaDoc");

        if (idTipoUnitaDoc != null) {
            queryStr.append(
                    " AND tipoStrutUnitaDoc.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoUnitaDoc");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("nmTipoStrutUnitaDoc", nmTipoStrutUnitaDoc);

        if (idTipoUnitaDoc != null) {
            query.setParameter("idTipoUnitaDoc", longFromBigDecimal(idTipoUnitaDoc));
        }

        List<DecTipoStrutUnitaDoc> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }

    /*
     * All'interno del parametro gli id delle due tabelle collegate sono mutualmente esclusivi
     */
    public List<DecVLisTiUniDocAms> getDecVLisTiUniDocAmList(
            DecVLisTiUniDocAmsRowBean tipoUnitaDocAmmesso) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT tipoUnitaDocAmmesso FROM DecVLisTiUniDocAms tipoUnitaDocAmmesso ");

        if (tipoUnitaDocAmmesso != null) {
            if (tipoUnitaDocAmmesso.getIdRegistroUnitaDoc() != null) {
                queryStr.append(
                        "WHERE tipoUnitaDocAmmesso.idRegistroUnitaDoc = :idRegistroUnitaDoc");
            } else if (tipoUnitaDocAmmesso.getIdTipoUnitaDoc() != null) {
                queryStr.append("WHERE tipoUnitaDocAmmesso.idTipoUnitaDoc = :idTipoUnitaDoc");
            }
        }
        Query query = getEntityManager().createQuery(queryStr.toString());

        if (tipoUnitaDocAmmesso != null) {
            if (tipoUnitaDocAmmesso.getIdRegistroUnitaDoc() != null) {
                query.setParameter("idRegistroUnitaDoc",
                        tipoUnitaDocAmmesso.getIdRegistroUnitaDoc());

            } else if (tipoUnitaDocAmmesso.getIdTipoUnitaDoc() != null) {
                query.setParameter("idTipoUnitaDoc", tipoUnitaDocAmmesso.getIdTipoUnitaDoc());
            }
        }

        return query.getResultList();
    }

    public List<DecTipoDocAmmesso> getDecTipoDocAmmessoList(BigDecimal idTipoStrutUnitaDoc) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT tipoDocAmmesso FROM DecTipoDocAmmesso tipoDocAmmesso ");
        if (idTipoStrutUnitaDoc != null) {
            queryStr.append(
                    "WHERE tipoDocAmmesso.decTipoStrutUnitaDoc.idTipoStrutUnitaDoc = :idTipoStrutUnitaDoc");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idTipoStrutUnitaDoc != null) {
            query.setParameter("idTipoStrutUnitaDoc", longFromBigDecimal(idTipoStrutUnitaDoc));
        }

        return query.getResultList();
    }

    public DecTipoDocAmmesso getDecTipoDocAmmesso(BigDecimal idTipoDoc,
            BigDecimal idTipoStrutUnitaDoc) {

        StringBuilder queryStr = new StringBuilder(
                "SELECT tipoDocAmmesso FROM DecTipoDocAmmesso tipoDocAmmesso ");

        if (idTipoDoc != BigDecimal.ZERO && idTipoStrutUnitaDoc != BigDecimal.ZERO) {
            queryStr.append("WHERE tipoDocAmmesso.decTipoDoc.idTipoDoc = :idTipoDoc "
                    + "AND tipoDocAmmesso.decTipoStrutUnitaDoc.idTipoStrutUnitaDoc = :idTipoStrutUnitaDoc");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idTipoDoc != BigDecimal.ZERO && idTipoStrutUnitaDoc != BigDecimal.ZERO) {
            query.setParameter("idTipoDoc", longFromBigDecimal(idTipoDoc));
            query.setParameter("idTipoStrutUnitaDoc", longFromBigDecimal(idTipoStrutUnitaDoc));
        }
        List<DecTipoDocAmmesso> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public DecTipoDocAmmesso getDecTipoDocAmmessoByName(BigDecimal idStrut, String nmTipoDoc,
            String nmTipoStrutUnitaDoc) {
        StringBuilder queryStr = new StringBuilder("SELECT u FROM DecTipoDocAmmesso u ");
        String whereWord = "WHERE ";
        if (idStrut != null) {
            queryStr.append(whereWord).append("u.decTipoDoc.orgStrut.idStrut = :idStrut ");
            whereWord = "AND ";
        }
        if (nmTipoDoc != null) {
            queryStr.append(whereWord).append("u.decTipoDoc.nmTipoDoc = :nmTipoDoc ");
            whereWord = "AND ";
        }
        if (nmTipoStrutUnitaDoc != null) {
            queryStr.append(whereWord)
                    .append("u.decTipoStrutUnitaDoc.nmTipoStrutUnitaDoc = :nmTipoStrutUnitaDoc ");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idStrut != null) {
            query.setParameter("idStrut", longFromBigDecimal(idStrut));
        }
        if (nmTipoDoc != null) {
            query.setParameter("nmTipoDoc", nmTipoDoc);

        }
        if (nmTipoStrutUnitaDoc != null) {
            query.setParameter("nmTipoStrutUnitaDoc", nmTipoStrutUnitaDoc);

        }
        List<DecTipoDocAmmesso> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    public boolean existsDecTipoDocAmmesso(BigDecimal idTipoDocAmmesso, BigDecimal idTipoDoc,
            BigDecimal idTipoStrutUnitaDoc, String tipoElemento) {

        StringBuilder queryStr = new StringBuilder(
                "SELECT COUNT(tipoDocAmmesso) FROM DecTipoDocAmmesso tipoDocAmmesso ");
        String whereWord = "WHERE ";

        if (idTipoDocAmmesso != null) {
            queryStr.append(whereWord)
                    .append("tipoDocAmmesso.idTipoDocAmmesso != :idTipoDocAmmesso ");
            whereWord = "AND ";
        }

        if (idTipoDoc != null) {
            queryStr.append(whereWord).append("tipoDocAmmesso.decTipoDoc.idTipoDoc = :idTipoDoc ");
            whereWord = "AND ";
        }

        if (idTipoStrutUnitaDoc != null) {
            queryStr.append(whereWord).append(
                    "tipoDocAmmesso.decTipoStrutUnitaDoc.idTipoStrutUnitaDoc = :idTipoStrutUnitaDoc ");
            whereWord = "AND ";
        }

        if (tipoElemento != null) {
            queryStr.append(whereWord).append("tipoDocAmmesso.tiDoc = :tipoElemento ");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idTipoDocAmmesso != null) {
            query.setParameter("idTipoDocAmmesso", longFromBigDecimal(idTipoDocAmmesso));

        }

        if (idTipoDoc != null) {
            query.setParameter("idTipoDoc", longFromBigDecimal(idTipoDoc));
        }

        if (idTipoStrutUnitaDoc != null) {
            query.setParameter("idTipoStrutUnitaDoc", longFromBigDecimal(idTipoStrutUnitaDoc));
        }

        if (tipoElemento != null) {
            query.setParameter("tipoElemento", tipoElemento);
        }

        Long count = (Long) query.getSingleResult();

        return count > 0;
    }

    public List<DecTipoDocAmmesso> getDecTipoDocAmmessoList(BigDecimal idTipoDocAmmesso,
            BigDecimal idTipoDoc, BigDecimal idTipoStrutUnitaDoc, String tipoElemento) {

        StringBuilder queryStr = new StringBuilder(
                "SELECT tipoDocAmmesso FROM DecTipoDocAmmesso tipoDocAmmesso ");
        String whereWord = "WHERE ";

        if (idTipoDocAmmesso != null) {
            queryStr.append(whereWord)
                    .append("tipoDocAmmesso.idTipoDocAmmesso = :idTipoDocAmmesso ");
            whereWord = "AND ";
        }

        if (idTipoDoc != null) {
            queryStr.append(whereWord).append("tipoDocAmmesso.decTipoDoc.idTipoDoc = :idTipoDoc ");
            whereWord = "AND ";
        }

        if (idTipoStrutUnitaDoc != null) {
            queryStr.append(whereWord).append(
                    "tipoDocAmmesso.decTipoStrutUnitaDoc.idTipoStrutUnitaDoc = :idTipoStrutUnitaDoc ");
            whereWord = "AND ";
        }

        if (tipoElemento != null) {
            queryStr.append(whereWord).append("tipoDocAmmesso.tiDoc = :tipoElemento ");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idTipoDocAmmesso != null) {
            query.setParameter("idTipoDocAmmesso", longFromBigDecimal(idTipoDocAmmesso));

        }

        if (idTipoDoc != null) {
            query.setParameter("idTipoDoc", longFromBigDecimal(idTipoDoc));
        }

        if (idTipoStrutUnitaDoc != null) {
            query.setParameter("idTipoStrutUnitaDoc", longFromBigDecimal(idTipoStrutUnitaDoc));
        }

        if (tipoElemento != null) {
            query.setParameter("tipoElemento", tipoElemento);
        }

        return query.getResultList();
    }

    public boolean checkRelationsAreEmptyForDecTipoStrutUnitaDoc(long idTipoStrutUnitaDoc) {
        boolean result = true;
        String queryStr = " select a from DecTipoStrutUnitaDoc a  "
                + " where a.idTipoStrutUnitaDoc=:idTipoStrutUnitaDoc "
                + " AND NOT EXISTS (select p from DecTipoDocAmmesso p where p.decTipoStrutUnitaDoc.idTipoStrutUnitaDoc=a.idTipoStrutUnitaDoc) ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idTipoStrutUnitaDoc", idTipoStrutUnitaDoc);
        List<Object[]> list = query.getResultList();
        result = list.isEmpty();
        return result;
    }

    public List<DecCategTipoUnitaDoc> getDecCategTipoUnitaDocList(Boolean firstLevel) {
        // Query nativa
        StringBuilder queryStr = new StringBuilder(
                "SELECT * " + " FROM DEC_CATEG_TIPO_UNITA_DOC c");
        if (Boolean.TRUE.equals(firstLevel)) {
            queryStr.append(" WHERE c.id_categ_tipo_unita_doc_padre is null");
        }
        queryStr.append(
                " CONNECT BY PRIOR c.id_categ_tipo_unita_doc =  c.id_categ_tipo_unita_doc_padre"
                        + " START WITH c.id_categ_tipo_unita_doc_padre is null "
                        + " ORDER SIBLINGS BY c.cd_categ_tipo_unita_doc ");

        Query query = getEntityManager().createNativeQuery(queryStr.toString(),
                DecCategTipoUnitaDoc.class);
        List<DecCategTipoUnitaDoc> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }

        return list;
    }

    public List<DecCategTipoUnitaDoc> getDecCategTipoUnitaDocChildList(
            BigDecimal idCategTipoUnitaDoc) {
        Query query = getEntityManager().createQuery("SELECT c " + "FROM DecCategTipoUnitaDoc c "
                + "WHERE c.decCategTipoUnitaDoc.idCategTipoUnitaDoc = :idPadre");
        query.setParameter("idPadre", longFromBigDecimal(idCategTipoUnitaDoc));
        List<DecCategTipoUnitaDoc> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list;
    }

    public List<DecCategTipoUnitaDoc> getDecCategTipoUnitaDocChildList(
            List<BigDecimal> idCategTipoUnitaDocList) {
        Query query = getEntityManager().createQuery("SELECT c " + "FROM DecCategTipoUnitaDoc c "
                + "WHERE c.decCategTipoUnitaDoc.idCategTipoUnitaDoc IN :idPadre");
        query.setParameter("idPadre", longListFrom(idCategTipoUnitaDocList));
        List<DecCategTipoUnitaDoc> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list;
    }

    public DecCategTipoUnitaDoc getDecCategTipoUnitaDocByCode(String cdCategTipoUnitaDoc) {
        return getDecCategTipoUnitaDocByCode(cdCategTipoUnitaDoc, false);
    }

    public DecCategTipoUnitaDoc getDecCategTipoUnitaDocByCodeLike(String cdCategTipoUnitaDoc) {
        return getDecCategTipoUnitaDocByCode(cdCategTipoUnitaDoc, true);
    }

    public DecCategTipoUnitaDoc getDecCategTipoUnitaDocByCode(String cdCategTipoUnitaDoc,
            boolean like) {
        String operation = "=";
        if (like) {
            operation = "LIKE";
        }
        String queryStr = "SELECT c FROM DecCategTipoUnitaDoc c WHERE c.cdCategTipoUnitaDoc "
                + operation + " :cdCategTipoUnitaDoc";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("cdCategTipoUnitaDoc", cdCategTipoUnitaDoc);
        List<DecCategTipoUnitaDoc> list = query.getResultList();

        DecCategTipoUnitaDoc categ = null;
        if (!list.isEmpty()) {
            categ = list.get(0);
        }
        return categ;
    }

    public List<DecTipoUnitaDoc> getListDecCategTipoUnitaDocInUse(long idCategTipoUnitaDoc) {
        Query query = getEntityManager().createQuery(
                "SELECT tipoUd FROM DecTipoUnitaDoc tipoUd WHERE tipoUd.decCategTipoUnitaDoc.idCategTipoUnitaDoc = :idCategTipoUnitaDoc");
        query.setParameter("idCategTipoUnitaDoc", idCategTipoUnitaDoc);
        List<DecTipoUnitaDoc> list = query.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        return list;
    }

    public List<DecTipoUnitaDoc> retrieveDecTipoUnitaDoc(BigDecimal idStrut) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT tipoUnitaDoc FROM DecTipoUnitaDoc tipoUnitaDoc ");
        String whereClause = " WHERE ";
        if (idStrut != null) {
            queryStr.append(whereClause).append("tipoUnitaDoc.orgStrut.idStrut = :idStrut ");
        }
        queryStr.append("ORDER BY tipoUnitaDoc.nmTipoUnitaDoc ");
        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idStrut != null) {
            query.setParameter("idStrut", longFromBigDecimal(idStrut));
        }

        return query.getResultList();
    }

    public DecVCalcTiServOnTipoUd getDecVCalcTiServOnTipoUd(BigDecimal idStrut,
            BigDecimal idCategTipoUnitaDoc, String cdAlgoTariffario) {
        DecVCalcTiServOnTipoUd result = null;
        Query query = getEntityManager()
                .createQuery("SELECT tiServOnTipoUd FROM DecVCalcTiServOnTipoUd tiServOnTipoUd "
                        + "WHERE tiServOnTipoUd.decVCalcTiServOnTipoUdId.idStrut = :idStrut "
                        + "AND tiServOnTipoUd.decVCalcTiServOnTipoUdId.idCategTipoUnitaDoc = :idCategTipoUnitaDoc "
                        + "AND tiServOnTipoUd.cdAlgoTariffario = :cdAlgoTariffario");
        query.setParameter("idStrut", idStrut);
        query.setParameter("idCategTipoUnitaDoc", idCategTipoUnitaDoc);
        query.setParameter("cdAlgoTariffario", cdAlgoTariffario);
        List<DecVCalcTiServOnTipoUd> resultList = query.getResultList();
        if (!resultList.isEmpty()) {
            return resultList.get(0);
        }
        return result;
    }

    public List<DecTipoUnitaDoc> getDecTipoUnitaDocList(BigDecimal idStrut, boolean filterValid) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT tipoUnita FROM DecTipoUnitaDoc tipoUnita ");
        String clause = " WHERE ";
        if (idStrut != null) {
            queryStr.append(clause).append("tipoUnita.orgStrut.idStrut=:idStrut ");
            clause = " AND ";
        }
        if (filterValid) {
            queryStr.append(clause).append(
                    " tipoUnita.dtIstituz <= :filterDate AND tipoUnita.dtSoppres >= :filterDate ");
        }
        queryStr.append("ORDER BY tipoUnita.nmTipoUnitaDoc ");
        Query query = getEntityManager().createQuery(queryStr.toString());

        if (idStrut != null) {
            query.setParameter("idStrut", longFromBigDecimal(idStrut));
        }
        if (filterValid) {
            Date now = Calendar.getInstance().getTime();
            query.setParameter("filterDate", now);
        }

        return query.getResultList();
    }

    /**
     * Restituisce la lista di sistemi versanti associati al tipo unità documentaria
     *
     * @param idTipoUnitaDoc id tipo unita doc
     *
     * @return lista di oggetti di tipo {@link AplSistemaVersante}
     */
    public List<Object[]> retrieveAplSistemaVersanteListPerTipoUd(BigDecimal idTipoUnitaDoc) {
        Query q = getEntityManager().createQuery(
                "SELECT sistemaVersante, dec.dtErog, dec.dtLastErog FROM AplSistemaVersante sistemaVersante, DecVLisSisVersByTipoUd dec "
                        + "WHERE dec.id.idSistemaVersante = sistemaVersante.idSistemaVersante "
                        + "AND dec.id.idTipoUnitaDoc = :idTipoUnitaDoc "
                        + "ORDER BY dec.dtErog ASC");
        q.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
        return q.getResultList();
    }

    /**
     * Restituisce i sistemi versanti, concatenati, associati al tipo unità documentaria.
     *
     * @param idTipoUnitaDoc id tipo unita doc
     *
     * @return String sistemi versanti
     */
    public String getNmSistemiVersantiRaggruppatiPerTipoUd(BigDecimal idTipoUnitaDoc) {
        Query q = getEntityManager()
                .createQuery("SELECT dec.nmSistemaVersante FROM DecVLisSisVersByTipoUd dec "
                        + "WHERE dec.id.idTipoUnitaDoc = :idTipoUnitaDoc "
                        + "ORDER BY dec.nmSistemaVersante ASC");
        q.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
        List<String> sList = q.getResultList();
        return StringUtils.join(sList, ", ");
    }

    /**
     * Restituisce le date di primo e ultimo versamento del tipo unità documentaria.
     *
     * @param idTipoUnitaDoc id tipo unita doc
     *
     * @return Object[] le date
     */
    public Object[] getDtErogSistemiVersantiPerTipoUd(BigDecimal idTipoUnitaDoc) {
        Query q = getEntityManager().createQuery(
                "SELECT MIN(dec.dtErog), MAX(dec.dtLastErog) FROM DecVLisSisVersByTipoUd dec "
                        + "WHERE dec.id.idTipoUnitaDoc = :idTipoUnitaDoc ");
        q.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
        List<Object[]> sList = q.getResultList();
        if (!sList.isEmpty()) {
            return sList.get(0);
        }
        return null;
    }

    public List<AplSistemaVersante> retrieveAplSistemaVersanteList() {
        Query q = getEntityManager()
                .createQuery("SELECT sistemaVersante FROM AplSistemaVersante sistemaVersante ");
        return q.getResultList();
    }

    /**
     * Restituisce tutti i tipi servizio in base alla classe
     *
     * @param tiClasseTipoServizio tipo classe di servizio
     *
     * @return lista di oggetto di tipo OrgTipoServizio
     */
    public List<OrgTipoServizio> retrieveOrgTipoServizioList(String tiClasseTipoServizio) {
        String queryStr = "SELECT tipoServizio FROM OrgTipoServizio tipoServizio ";
        if (tiClasseTipoServizio != null) {
            queryStr = queryStr
                    .concat("WHERE tipoServizio.tiClasseTipoServizio = :tiClasseTipoServizio");
        }
        Query q = getEntityManager().createQuery(queryStr);
        if (tiClasseTipoServizio != null) {
            q.setParameter("tiClasseTipoServizio", tiClasseTipoServizio);
        }
        return q.getResultList();
    }

    public AplSistemaVersante getAplSistemaVersanteByName(String nmSistemaVersante) {
        String queryStr = "SELECT sistemaVersante FROM AplSistemaVersante sistemaVersante WHERE sistemaVersante.nmSistemaVersante = :nmSistemaVersante";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("nmSistemaVersante", nmSistemaVersante);
        List<AplSistemaVersante> list = query.getResultList();
        AplSistemaVersante sistemaVersante = null;
        if (!list.isEmpty()) {
            sistemaVersante = list.get(0);
        }

        return sistemaVersante;
    }

    public OrgTipoServizio getOrgTipoServizioByName(String cdTipoServizio) {
        String queryStr = "SELECT tipoServizio FROM OrgTipoServizio tipoServizio WHERE tipoServizio.cdTipoServizio = :cdTipoServizio";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("cdTipoServizio", cdTipoServizio);
        List<OrgTipoServizio> list = query.getResultList();
        OrgTipoServizio tipoServizio = null;
        if (!list.isEmpty()) {
            tipoServizio = list.get(0);
        }

        return tipoServizio;
    }

    public int bulkDeleteDecTipoStrutUdSisVers(long idTipoStrutUnitaDoc) {
        Query q = getEntityManager()
                .createQuery("DELETE FROM DecTipoStrutUdSisVer tipoStrutUdSisVers "
                        + "WHERE tipoStrutUdSisVers.decTipoStrutUnitaDoc.idTipoStrutUnitaDoc = :idTipoStrutUnitaDoc ");
        q.setParameter("idTipoStrutUnitaDoc", idTipoStrutUnitaDoc);
        int result = q.executeUpdate();
        getEntityManager().flush();
        return result;
    }

    public void deleteDecTipoStrutUdSisVers(long idTipoStrutUnitaDoc,
            BigDecimal idSistemaVersante) {
        Query query = getEntityManager().createQuery(
                "SELECT tipoStrutUdSisVers FROM DecTipoStrutUdSisVer tipoStrutUdSisVers "
                        + "WHERE tipoStrutUdSisVers.decTipoStrutUnitaDoc.idTipoStrutUnitaDoc = :idTipoStrutUnitaDoc "
                        + "AND tipoStrutUdSisVers.aplSistemaVersante.idSistemaVersante = :idSistemaVersante ");
        query.setParameter("idTipoStrutUnitaDoc", idTipoStrutUnitaDoc);
        query.setParameter("idSistemaVersante", longFromBigDecimal(idSistemaVersante));
        List<DecTipoStrutUdSisVer> resultList = query.getResultList();
        getEntityManager().remove(resultList.get(0));
    }

    public int bulkDeleteDecTipoStrutUdReg(long idTipoStrutUnitaDoc) {
        Query q = getEntityManager().createQuery("DELETE FROM DecTipoStrutUdReg tipoStrutUdReg "
                + "WHERE tipoStrutUdReg.decTipoStrutUnitaDoc.idTipoStrutUnitaDoc = :idTipoStrutUnitaDoc ");
        q.setParameter("idTipoStrutUnitaDoc", idTipoStrutUnitaDoc);
        int result = q.executeUpdate();
        getEntityManager().flush();
        return result;
    }

    public void deleteDecTipoStrutUdReg(long idTipoStrutUnitaDoc, long idRegistroUnitaDoc) {
        Query query = getEntityManager()
                .createQuery("SELECT tipoStrutUdReg FROM DecTipoStrutUdReg tipoStrutUdReg "
                        + "WHERE tipoStrutUdReg.decTipoStrutUnitaDoc.idTipoStrutUnitaDoc = :idTipoStrutUnitaDoc "
                        + "AND tipoStrutUdReg.decRegistroUnitaDoc.idRegistroUnitaDoc = :idRegistroUnitaDoc ");
        query.setParameter("idTipoStrutUnitaDoc", idTipoStrutUnitaDoc);
        query.setParameter("idRegistroUnitaDoc", idRegistroUnitaDoc);
        List<DecTipoStrutUdReg> resultList = query.getResultList();
        getEntityManager().remove(resultList.get(0));
    }

    public void deleteDecTipoStrutUdXsd(long idTipoStrutUnitaDoc, long idXsdDatiSpec) {
        Query query = getEntityManager()
                .createQuery("SELECT tipoStrutUdXsd FROM DecTipoStrutUdXsd tipoStrutUdXsd "
                        + "WHERE tipoStrutUdXsd.decTipoStrutUnitaDoc.idTipoStrutUnitaDoc = :idTipoStrutUnitaDoc "
                        + "AND tipoStrutUdXsd.decXsdDatiSpec.idXsdDatiSpec = :idXsdDatiSpec ");
        query.setParameter("idTipoStrutUnitaDoc", idTipoStrutUnitaDoc);
        query.setParameter("idXsdDatiSpec", idXsdDatiSpec);
        List<DecTipoStrutUdXsd> resultList = query.getResultList();
        getEntityManager().remove(resultList.get(0));
    }

    public int bulkDeleteDecTipoStrutUdXsd(long idTipoStrutUnitaDoc) {
        Query q = getEntityManager().createQuery("DELETE FROM DecTipoStrutUdXsd tipoStrutUdXsd "
                + "WHERE tipoStrutUdXsd.decTipoStrutUnitaDoc.idTipoStrutUnitaDoc = :idTipoStrutUnitaDoc ");
        q.setParameter("idTipoStrutUnitaDoc", idTipoStrutUnitaDoc);
        int result = q.executeUpdate();
        getEntityManager().flush();
        return result;
    }

    public DecTipoStrutUdSisVer getDecTipoStrutUdSisVersByName(BigDecimal idStrutCorrente,
            String nmTipoUnitaDoc, String nmTipoStrutUnitaDoc, String nmSistemaVersante) {
        String queryStr = "SELECT tipoStrutUdSisVers FROM DecTipoStrutUdSisVer tipoStrutUdSisVers "
                + "WHERE tipoStrutUdSisVers.decTipoStrutUnitaDoc.decTipoUnitaDoc.orgStrut.idStrut = :idStrutCorrente "
                + "AND tipoStrutUdSisVers.decTipoStrutUnitaDoc.decTipoUnitaDoc.nmTipoUnitaDoc = :nmTipoUnitaDoc "
                + "AND tipoStrutUdSisVers.decTipoStrutUnitaDoc.nmTipoStrutUnitaDoc = :nmTipoStrutUnitaDoc "
                + "AND tipoStrutUdSisVers.aplSistemaVersante.nmSistemaVersante = :nmSistemaVersante ";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrutCorrente", longFromBigDecimal(idStrutCorrente));
        query.setParameter("nmTipoUnitaDoc", nmTipoUnitaDoc);
        query.setParameter("nmTipoStrutUnitaDoc", nmTipoStrutUnitaDoc);
        query.setParameter("nmSistemaVersante", nmSistemaVersante);
        List<DecTipoStrutUdSisVer> list = query.getResultList();
        DecTipoStrutUdSisVer tipoStrutUdSisVer = null;
        if (!list.isEmpty()) {
            tipoStrutUdSisVer = list.get(0);
        }

        return tipoStrutUdSisVer;
    }

    public boolean isAccordoPerNuovaFatturazione(BigDecimal idEnteConvenz) {
        String queryStr = "SELECT accordoEnte FROM SIOrgAccordoEnte accordoEnte "
                + "WHERE accordoEnte.siOrgEnteConvenz.idEnteSiam = :idEnteConvenz "
                + "AND accordoEnte.dtDecAccordo = "
                + "(SELECT MAX(accordo_max.dtDecAccordo) FROM SIOrgAccordoEnte accordo_max "
                + "WHERE accordo_max.siOrgEnteConvenz.idEnteSiam = accordoEnte.siOrgEnteConvenz.idEnteSiam) ";

        Query query = getEntityManager().createQuery(queryStr);

        query.setParameter("idEnteConvenz", longFromBigDecimal(idEnteConvenz));
        List<SIOrgAccordoEnte> lista = query.getResultList();
        if (lista != null && !lista.isEmpty()) {
            String tipoAccordo = lista.get(0).getSIOrgTipoAccordo().getCdTipoAccordo();
            return tipoAccordo.contains("Da fatturare")
                    || tipoAccordo.equals("Accordo enti extra ER");
        }

        return false;
    }

    public boolean existsEstensione(String cdEstensioneFile) {
        Query q = getEntityManager().createQuery("SELECT a FROM DecEstensioneFile a "
                + "WHERE a.cdEstensioneFile = :cdEstensioneFile ");
        q.setParameter("cdEstensioneFile", cdEstensioneFile);
        List<DecEstensioneFile> estensioneList = q.getResultList();
        return !estensioneList.isEmpty();
    }

    public boolean existsAllFormatiStandard(List<String> nmFormatoFileStandard) {
        if (!nmFormatoFileStandard.isEmpty()) {
            // Non eseguo un controllo semplice e ottimizzato con una count e confronto sul numero
            // di elementi della
            // lista in quanto potrei
            // avere formati specifici in cui un elemento è ripetuto. Es. PDF.P7M.P7M
            int i = 0;
            for (String formato : nmFormatoFileStandard) {
                Query q = getEntityManager()
                        .createQuery("SELECT a.nmFormatoFileStandard FROM DecFormatoFileStandard a "
                                + "WHERE a.nmFormatoFileStandard = :formato ");
                q.setParameter("formato", formato);
                if (!q.getResultList().isEmpty()) {
                    i++;
                }
            }
            return i == nmFormatoFileStandard.size();
        }
        return false;

    }

    public void deleteUsoModelloXsdUniDoc(long idTipoUnitaDoc) {
        String queryStr = "DELETE FROM DecUsoModelloXsdUniDoc uso "
                + "WHERE uso.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoUnitaDoc ";
        Query q = getEntityManager().createQuery(queryStr);
        q.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
        q.executeUpdate();
        getEntityManager().flush();
    }

    public boolean existsValoreParamApplicTipoUd(long idTipoUnitaDoc) {
        String queryStr = "SELECT valoreParamApplic FROM AplValoreParamApplic valoreParamApplic "
                + "WHERE valoreParamApplic.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoUnitaDoc ";
        Query q = getEntityManager().createQuery(queryStr);
        q.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
        return !q.getResultList().isEmpty();
    }

}
