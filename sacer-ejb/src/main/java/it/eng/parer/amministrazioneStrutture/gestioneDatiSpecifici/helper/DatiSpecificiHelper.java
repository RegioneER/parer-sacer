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

package it.eng.parer.amministrazioneStrutture.gestioneDatiSpecifici.helper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import it.eng.parer.entity.DecAttribDatiSpec;
import it.eng.parer.entity.DecTipoStrutUdXsd;
import it.eng.parer.entity.DecXsdAttribDatiSpec;
import it.eng.parer.entity.DecXsdDatiSpec;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.slite.gen.tablebean.DecXsdDatiSpecRowBean;
import it.eng.parer.ws.utils.CostantiDB;

/**
 * Helper dei dati specifici delle unit\u00E0 documentarie
 *
 * @author Bonora_L
 */
@Stateless
@LocalBean
public class DatiSpecificiHelper extends GenericHelper {

    /**
     * Ritorna, se esiste, l'oggetto DecAttribDatiSpec relativo ai dati forniti in input
     *
     * @param idStrut
     *            id della struttura
     * @param nmAttribDatiSpec
     *            nome dell'attributo
     * @param tiEntitaSacer
     *            tipo di entita di Sacer
     * @param tiUsoAttrib
     *            tipo di uso dell'attributo (MIGRAZ, VERS)
     * @param idTipoUnitaDoc
     *            se esistente, id del tipo ud
     * @param idTipoDoc
     *            se esistente, id del tipo documento
     * @param idTipoCompDoc
     *            se esistente, id del tipo componente
     * @param nmSistemaMigraz
     *            sistema migrazione
     * 
     * @return DecAttribDatiSpec o null se inesistente
     */
    public DecAttribDatiSpec getDecAttribDatiSpecById(Long idStrut, String nmAttribDatiSpec, String tiEntitaSacer,
            String tiUsoAttrib, Long idTipoUnitaDoc, Long idTipoDoc, Long idTipoCompDoc, String nmSistemaMigraz) {
        StringBuilder builder = new StringBuilder("SELECT attribDatiSpec FROM DecAttribDatiSpec attribDatiSpec "
                + "WHERE attribDatiSpec.tiEntitaSacer = :tiEntitaSacer "
                + "AND attribDatiSpec.orgStrut.idStrut = :idStrut "
                + "AND attribDatiSpec.nmAttribDatiSpec = :nmAttribDatiSpec "
                + "AND attribDatiSpec.tiUsoAttrib = :tiUsoAttrib ");

        CostantiDB.TipiEntitaSacer tipoEntita = CostantiDB.TipiEntitaSacer.valueOf(tiEntitaSacer);
        if (StringUtils.isNotBlank(tiUsoAttrib) && (tiUsoAttrib.equals(CostantiDB.TipiUsoDatiSpec.MIGRAZ.name())
                || StringUtils.isNotBlank(nmSistemaMigraz))) {
            builder.append("AND attribDatiSpec.nmSistemaMigraz = :nmSistemaMigraz ");
        } else {
            switch (tipoEntita) {
            case UNI_DOC:
                builder.append("AND attribDatiSpec.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoUnitaDoc ");
                break;
            case DOC:
                builder.append("AND attribDatiSpec.decTipoDoc.idTipoDoc = :idTipoDoc ");
                break;
            case COMP:
            case SUB_COMP:
                builder.append("AND attribDatiSpec.decTipoCompDoc.idTipoCompDoc = :idTipoCompDoc ");
                break;
            }
        }
        Query query = getEntityManager().createQuery(builder.toString());
        query.setParameter("idStrut", idStrut);
        query.setParameter("nmAttribDatiSpec", nmAttribDatiSpec);
        query.setParameter("tiEntitaSacer", tiEntitaSacer);
        query.setParameter("tiUsoAttrib", tiUsoAttrib);
        if (StringUtils.isNotBlank(tiUsoAttrib) && (tiUsoAttrib.equals(CostantiDB.TipiUsoDatiSpec.MIGRAZ.name())
                || StringUtils.isNotBlank(nmSistemaMigraz))) {
            query.setParameter("nmSistemaMigraz", nmSistemaMigraz);
        } else {
            switch (tipoEntita) {
            case UNI_DOC:
                query.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
                break;
            case DOC:
                query.setParameter("idTipoDoc", idTipoDoc);
                break;
            case COMP:
            case SUB_COMP:
                query.setParameter("idTipoCompDoc", idTipoCompDoc);
                break;
            }
        }
        List<DecAttribDatiSpec> attrib = query.getResultList();
        if (attrib.isEmpty()) {
            return null;
        }
        return attrib.get(0);
    }

    public DecAttribDatiSpec getDecAttribDatiSpecById(BigDecimal idStrut, String nmAttribDatiSpec, String tiEntitaSacer,
            String tiUsoAttrib, Long idTipoUnitaDoc, Long idTipoDoc, Long idTipoCompDoc) {
        return getDecAttribDatiSpecById(idStrut.longValue(), nmAttribDatiSpec, tiEntitaSacer, tiUsoAttrib,
                idTipoUnitaDoc, idTipoDoc, idTipoCompDoc, null);
    }

    public DecAttribDatiSpec getDecAttribDatiSpecById(BigDecimal idStrut, String nmAttribDatiSpec, String tiEntitaSacer,
            String tiUsoAttrib, BigDecimal idTipoUnitaDoc, BigDecimal idTipoDoc, BigDecimal idTipoCompDoc) {
        return getDecAttribDatiSpecById(idStrut.longValue(), nmAttribDatiSpec, tiEntitaSacer, tiUsoAttrib,
                (idTipoUnitaDoc != null ? idTipoUnitaDoc.longValue() : null),
                (idTipoDoc != null ? idTipoDoc.longValue() : null),
                (idTipoCompDoc != null ? idTipoCompDoc.longValue() : null), null);
    }

    public DecAttribDatiSpec getDecAttribDatiSpecById(Long idStrut, String nmAttribDatiSpec, String tiEntitaSacer,
            String tiUsoAttrib, BigDecimal idTipoUnitaDoc, BigDecimal idTipoDoc, BigDecimal idTipoCompDoc) {
        return getDecAttribDatiSpecById(idStrut, nmAttribDatiSpec, tiEntitaSacer, tiUsoAttrib,
                (idTipoUnitaDoc != null ? idTipoUnitaDoc.longValue() : null),
                (idTipoDoc != null ? idTipoDoc.longValue() : null),
                (idTipoCompDoc != null ? idTipoCompDoc.longValue() : null), null);
    }

    public DecAttribDatiSpec getDecAttribDatiSpecById(BigDecimal idStrut, String nmAttribDatiSpec, String tiEntitaSacer,
            String tiUsoAttrib, String nmSistemaMigraz) {
        return getDecAttribDatiSpecById(idStrut.longValue(), nmAttribDatiSpec, tiEntitaSacer, tiUsoAttrib, null, null,
                null, nmSistemaMigraz);
    }

    public DecAttribDatiSpec getDecAttribDatiSpecById(Long idStrut, String nmAttribDatiSpec, String tiEntitaSacer,
            String tiUsoAttrib, String nmSistemaMigraz) {
        return getDecAttribDatiSpecById(idStrut, nmAttribDatiSpec, tiEntitaSacer, tiUsoAttrib, null, null, null,
                nmSistemaMigraz);
    }

    /**
     * Ritorna, se esiste, l'oggetto DecAttribDatiSpec relativo ai dati forniti in input
     *
     * @param idStrut
     *            id della struttura
     * @param nmAttribDatiSpec
     *            nome dell'attributo
     * @param tiEntitaSacer
     *            tipo di entita di Sacer
     * @param tiUsoAttrib
     *            tipo di uso dell'attributo (MIGRAZ, VERS)
     * @param nmTipoUnitaDoc
     *            se esistente, nome del tipo ud
     * @param nmTipoDoc
     *            se esistente, nome del tipo documento
     * @param nmTipoCompDoc
     *            se esistente, nome del tipo componente
     * @param nmSistemaMigraz
     *            sistema migrazione
     * 
     * @return DecAttribDatiSpec o null se inesistente
     */
    public DecAttribDatiSpec getDecAttribDatiSpecByName(Long idStrut, String nmAttribDatiSpec, String tiEntitaSacer,
            String tiUsoAttrib, String nmTipoUnitaDoc, String nmTipoDoc, String nmTipoCompDoc, String nmSistemaMigraz) {
        StringBuilder builder = new StringBuilder("SELECT attribDatiSpec FROM DecXsdAttribDatiSpec xsdAttrib "
                + "JOIN xsdAttrib.decAttribDatiSpec attribDatiSpec "
                + "WHERE attribDatiSpec.tiEntitaSacer = :tiEntitaSacer "
                + "AND attribDatiSpec.orgStrut.idStrut = :idStrut "
                + "AND attribDatiSpec.nmAttribDatiSpec = :nmAttribDatiSpec "
                + "AND attribDatiSpec.tiUsoAttrib = :tiUsoAttrib ");
        CostantiDB.TipiEntitaSacer tipoEntita = CostantiDB.TipiEntitaSacer.valueOf(tiEntitaSacer);
        if (StringUtils.isNotBlank(tiUsoAttrib) && (tiUsoAttrib.equals(CostantiDB.TipiUsoDatiSpec.MIGRAZ.name())
                || StringUtils.isNotBlank(nmSistemaMigraz))) {
            builder.append("AND attribDatiSpec.nmSistemaMigraz = :nmSistemaMigraz ");
        } else {
            switch (tipoEntita) {
            case UNI_DOC:
                builder.append("AND attribDatiSpec.decTipoUnitaDoc.nmTipoUnitaDoc = :nmTipoUnitaDoc ");
                break;
            case DOC:
                builder.append("AND attribDatiSpec.decTipoDoc.nmTipoDoc = :nmTipoDoc ");
                break;
            case COMP:
            case SUB_COMP:
                builder.append("AND attribDatiSpec.decTipoCompDoc.nmTipoCompDoc = :nmTipoCompDoc ");
                break;
            }
        }
        Query query = getEntityManager().createQuery(builder.toString());
        query.setParameter("idStrut", idStrut);
        query.setParameter("nmAttribDatiSpec", nmAttribDatiSpec);
        query.setParameter("tiEntitaSacer", tiEntitaSacer);
        query.setParameter("tiUsoAttrib", tiUsoAttrib);
        if (StringUtils.isNotBlank(tiUsoAttrib) && (tiUsoAttrib.equals(CostantiDB.TipiUsoDatiSpec.MIGRAZ.name())
                || StringUtils.isNotBlank(nmSistemaMigraz))) {
            query.setParameter("nmSistemaMigraz", nmSistemaMigraz);
        } else {
            switch (tipoEntita) {
            case UNI_DOC:
                query.setParameter("nmTipoUnitaDoc", nmTipoUnitaDoc);
                break;
            case DOC:
                query.setParameter("nmTipoDoc", nmTipoDoc);
                break;
            case COMP:
            case SUB_COMP:
                query.setParameter("nmTipoCompDoc", nmTipoCompDoc);
                break;
            }
        }
        List<DecAttribDatiSpec> attrib = query.getResultList();
        if (attrib.isEmpty()) {
            return null;
        }
        return attrib.get(0);
    }

    public DecAttribDatiSpec getDecAttribDatiSpecByName(BigDecimal idStrutCorrente, String tiUsoAttrib,
            String tiEntitaSacer, String nmTipoUnitaDoc, String nmTipoDoc, String nmTipoCompDoc, String nmTipoStrutDoc,
            String nmAttribDatiSpec) {
        String queryStr = "SELECT attribDatiSpec FROM DecAttribDatiSpec attribDatiSpec ";

        if (nmTipoUnitaDoc != null) {
            queryStr = queryStr + "WHERE attribDatiSpec.decTipoUnitaDoc.orgStrut.idStrut = :idStrutCorrente "
                    + "AND attribDatiSpec.decTipoUnitaDoc.nmTipoUnitaDoc = :nmTipoUnitaDoc ";
        }

        if (nmTipoDoc != null) {
            queryStr = queryStr + "WHERE attribDatiSpec.decTipoDoc.orgStrut.idStrut = :idStrutCorrente "
                    + "AND attribDatiSpec.decTipoDoc.nmTipoDoc = :nmTipoDoc ";
        }

        if (nmTipoCompDoc != null) {
            queryStr = queryStr
                    + "WHERE attribDatiSpec.decTipoCompDoc.decTipoStrutDoc.orgStrut.idStrut = :idStrutCorrente "
                    + "AND attribDatiSpec.decTipoCompDoc.decTipoStrutDoc.nmTipoStrutDoc = :nmTipoStrutDoc "
                    + "AND attribDatiSpec.decTipoCompDoc.nmTipoCompDoc = :nmTipoCompDoc ";
        }

        if (tiUsoAttrib.equals("MIGRAZ")) {
            queryStr = queryStr + "AND attribDatiSpec.nmSistemaMigraz = :nmSistemaMigraz ";
        } else {
            queryStr = queryStr + "AND attribDatiSpec.nmSistemaMigraz IS NULL ";
        }

        queryStr = queryStr + "AND attribDatiSpec.tiUsoAttrib = :tiUsoAttrib "
                + "AND attribDatiSpec.tiEntitaSacer = :tiEntitaSacer "
                + "AND attribDatiSpec.nmAttribDatiSpec = :nmAttribDatiSpec ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrutCorrente", longFromBigDecimal(idStrutCorrente));
        if (nmTipoUnitaDoc != null) {
            query.setParameter("nmTipoUnitaDoc", nmTipoUnitaDoc);
        }
        if (nmTipoDoc != null) {
            query.setParameter("nmTipoDoc", nmTipoDoc);
        }
        if (nmTipoCompDoc != null) {
            query.setParameter("nmTipoCompDoc", nmTipoCompDoc);
            query.setParameter("nmTipoStrutDoc", nmTipoStrutDoc);
        }
        query.setParameter("tiUsoAttrib", tiUsoAttrib);
        query.setParameter("tiEntitaSacer", tiEntitaSacer);
        query.setParameter("nmAttribDatiSpec", nmAttribDatiSpec);
        if (tiUsoAttrib.equals("MIGRAZ")) {
            query.setParameter("tiEntitaSacer", tiEntitaSacer);
        }
        List<DecAttribDatiSpec> list = query.getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public DecAttribDatiSpec getDecAttribDatiSpecByName(BigDecimal idStrut, String nmAttribDatiSpec,
            String tiEntitaSacer, String tiUsoAttrib, String nmTipoUnitaDoc, String nmTipoDoc, String nmTipoCompDoc) {
        return DatiSpecificiHelper.this.getDecAttribDatiSpecByName(idStrut.longValue(), nmAttribDatiSpec, tiEntitaSacer,
                tiUsoAttrib, nmTipoUnitaDoc, nmTipoDoc, nmTipoCompDoc, null);
    }

    public DecAttribDatiSpec getDecAttribDatiSpecByName(BigDecimal idStrut, String nmAttribDatiSpec,
            String tiEntitaSacer, String tiUsoAttrib, String nmSistemaMigraz) {
        return DatiSpecificiHelper.this.getDecAttribDatiSpecByName(idStrut.longValue(), nmAttribDatiSpec, tiEntitaSacer,
                tiUsoAttrib, null, null, null, nmSistemaMigraz);
    }

    public List<DecAttribDatiSpec> getPreviousVersionAttributesList(BigDecimal idXsdDatiSpec, BigDecimal idTipoUnitaDoc,
            BigDecimal idTipoDoc, BigDecimal idTipoCompDoc) {
        return getWhichAttributesList(idXsdDatiSpec, idTipoUnitaDoc, idTipoDoc, idTipoCompDoc, true);
    }

    public List<DecAttribDatiSpec> getWhichAttributesList(BigDecimal idXsdDatiSpec, BigDecimal idTipoUnitaDoc,
            BigDecimal idTipoDoc, BigDecimal idTipoCompDoc, boolean previous) {
        String operand = (previous ? "<>" : "=");
        StringBuilder queryStr = new StringBuilder(
                "SELECT attribDatiSpec FROM DecXsdAttribDatiSpec xsdAttribDatiSpec JOIN xsdAttribDatiSpec.decAttribDatiSpec attribDatiSpec"
                        + " WHERE xsdAttribDatiSpec.decXsdDatiSpec.idXsdDatiSpec ");
        queryStr.append(operand);
        queryStr.append(" :idXsdDatiSpec ");

        if (idTipoDoc != null) {
            queryStr.append(" AND attribDatiSpec.decTipoDoc.idTipoDoc= :idTipoDoc");
        } else if (idTipoUnitaDoc != null) {
            queryStr.append(" AND attribDatiSpec.decTipoUnitaDoc.idTipoUnitaDoc= :idTipoUnitaDoc");
        } else if (idTipoCompDoc != null) {
            queryStr.append(" AND attribDatiSpec.decTipoCompDoc.idTipoCompDoc= :idTipoCompDoc");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());

        query.setParameter("idXsdDatiSpec", longFromBigDecimal(idXsdDatiSpec));

        if (idTipoDoc != null) {
            query.setParameter("idTipoDoc", longFromBigDecimal(idTipoDoc));
        } else if (idTipoUnitaDoc != null) {
            query.setParameter("idTipoUnitaDoc", longFromBigDecimal(idTipoUnitaDoc));
        } else if (idTipoCompDoc != null) {
            query.setParameter("idTipoCompDoc", longFromBigDecimal(idTipoCompDoc));
        }

        return query.getResultList();
    }

    public List<String> getNmAttribDatiSpecList(BigDecimal idStrut, BigDecimal idTipoUnitaDoc, BigDecimal idTipoDoc,
            BigDecimal idTipoCompDoc, String tiEntitaSacer, String nmSistemaMigraz) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT DISTINCT(attribDatiSpec.nmAttribDatiSpec) FROM DecAttribDatiSpec attribDatiSpec WHERE attribDatiSpec.orgStrut.idStrut = :idStrut AND attribDatiSpec.tiEntitaSacer = :tiEntitaSacer");
        String clause = " AND ";
        if (idTipoDoc != null) {
            queryStr.append(clause).append("attribDatiSpec.decTipoDoc.idTipoDoc= :idTipoDoc");
        } else if (idTipoUnitaDoc != null) {
            queryStr.append(clause).append("attribDatiSpec.decTipoUnitaDoc.idTipoUnitaDoc= :idTipoUnitaDoc");
        } else if (idTipoCompDoc != null) {
            queryStr.append(clause).append("attribDatiSpec.decTipoCompDoc.idTipoCompDoc= :idTipoCompDoc");
        } else if (StringUtils.isNotBlank(nmSistemaMigraz)) {
            queryStr.append(clause).append("attribDatiSpec.nmSistemaMigraz = :nmSistemaMigraz");
        } else {
            throw new IllegalArgumentException("Parametri per ottenere gli attributi dati specifici non corretti");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());

        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        query.setParameter("tiEntitaSacer", tiEntitaSacer);
        if (idTipoDoc != null) {
            query.setParameter("idTipoDoc", longFromBigDecimal(idTipoDoc));
        } else if (idTipoUnitaDoc != null) {
            query.setParameter("idTipoUnitaDoc", longFromBigDecimal(idTipoUnitaDoc));
        } else if (idTipoCompDoc != null) {
            query.setParameter("idTipoCompDoc", longFromBigDecimal(idTipoCompDoc));
        } else if (StringUtils.isNotBlank(nmSistemaMigraz)) {
            query.setParameter("nmSistemaMigraz", nmSistemaMigraz);
        }

        return query.getResultList();
    }

    public List<DecAttribDatiSpec> retrieveDecAttribDatiSpecList(BigDecimal idXsdDatiSpec) {
        Query query = getEntityManager().createQuery(
                "SELECT attribDatiSpec FROM DecXsdAttribDatiSpec xsdAttrib JOIN xsdAttrib.decAttribDatiSpec attribDatiSpec WHERE xsdAttrib.decXsdDatiSpec.idXsdDatiSpec = :idXsdDatiSpec ");
        query.setParameter("idXsdDatiSpec", longFromBigDecimal(idXsdDatiSpec));
        return query.getResultList();
    }

    /**
     * Ritorna l'attributo dei dati specifici per i parametri selezionati, all'ultima versione esistente del xsd
     *
     * @param idStrut
     *            id della struttura
     * @param nmAttribDatiSpec
     *            nome dell'attributo
     * @param tiEntitaSacer
     *            tipo di entita di Sacer
     * @param tiUsoAttrib
     *            tipo di uso dell'attributo (MIGRAZ, VERS)
     * @param nmTipoUnitaDoc
     *            se esistente, nome del tipo ud
     * @param nmTipoDoc
     *            se esistente, nome del tipo documento
     * @param nmTipoCompDoc
     *            se esistente, nome del tipo componente
     * 
     * @return l'attributo
     */
    public DecAttribDatiSpec getDecAttribDatiSpecUniDocAndDoc(Long idStrut, String nmAttribDatiSpec,
            String tiEntitaSacer, String tiUsoAttrib, String nmTipoUnitaDoc, String nmTipoDoc, String nmTipoCompDoc) {
        StringBuilder builder = new StringBuilder("SELECT attribDatiSpec FROM DecXsdAttribDatiSpec xsdAttrib "
                + "JOIN xsdAttrib.decAttribDatiSpec attribDatiSpec "
                + "WHERE attribDatiSpec.tiEntitaSacer = :tiEntitaSacer "
                + "AND attribDatiSpec.orgStrut.idStrut = :idStrut "
                + "AND attribDatiSpec.nmAttribDatiSpec = :nmAttribDatiSpec "
                + "AND attribDatiSpec.tiUsoAttrib = :tiUsoAttrib ");

        CostantiDB.TipiEntitaSacer tipoEntita = CostantiDB.TipiEntitaSacer.valueOf(tiEntitaSacer);
        switch (tipoEntita) {
        case UNI_DOC:
            builder.append("AND attribDatiSpec.decTipoUnitaDoc.nmTipoUnitaDoc = :nmTipoUnitaDoc ");
            break;
        case DOC:
            builder.append("AND attribDatiSpec.decTipoDoc.nmTipoDoc = :nmTipoDoc ");
            break;
        case COMP:
        case SUB_COMP:
            builder.append("AND attribDatiSpec.decTipoCompDoc.nmTipoCompDoc = :nmTipoCompDoc ");
            break;
        }

        builder.append("AND xsdAttrib.decXsdDatiSpec.dtIstituz = (select MAX(d.dtIstituz) FROM DecXsdDatiSpec d "
                + "WHERE d.orgStrut.idStrut = :idStrut AND d.dtIstituz <= :filterDate AND d.dtSoppres >= :filterDate ");
        switch (tipoEntita) {
        case UNI_DOC:
            builder.append("AND d.decTipoUnitaDoc.nmTipoUnitaDoc = :nmTipoUnitaDoc ");
            break;
        case DOC:
            builder.append("AND d.decTipoDoc.nmTipoDoc = :nmTipoDoc ");
            break;
        default:
            break;
        }

        builder.append("AND d.tiUsoXsd = 'VERS' )");

        Query query = getEntityManager().createQuery(builder.toString());
        query.setParameter("idStrut", idStrut);
        query.setParameter("nmAttribDatiSpec", nmAttribDatiSpec);
        query.setParameter("tiEntitaSacer", tiEntitaSacer);
        query.setParameter("tiUsoAttrib", tiUsoAttrib);
        Date now = Calendar.getInstance().getTime();
        query.setParameter("filterDate", now);
        switch (tipoEntita) {
        case UNI_DOC:
            query.setParameter("nmTipoUnitaDoc", nmTipoUnitaDoc);
            break;
        case DOC:
            query.setParameter("nmTipoDoc", nmTipoDoc);
            break;
        case COMP:
        case SUB_COMP:
            query.setParameter("nmTipoCompDoc", nmTipoCompDoc);
            break;
        }

        return (DecAttribDatiSpec) query.getSingleResult();
    }

    /**
     * Ritorna la lista di xsd per i dati specifici dati i parametri in input
     *
     * @param idStrut
     *            id della struttura
     * @param tiUsoXsd
     *            uso xsd
     * @param tiEntitaSacer
     *            tipo di entita di Sacer
     * @param nmSistemaMigraz
     *            sistema migrazione
     * 
     * @return la lista di xsd per i dati specifici
     */
    public List<DecXsdDatiSpec> retrieveDecXsdDatiSpecList(BigDecimal idStrut, String tiUsoXsd, String tiEntitaSacer,
            String nmSistemaMigraz) {
        return retrieveDecXsdDatiSpecList(idStrut, tiUsoXsd, tiEntitaSacer, null, null, null, nmSistemaMigraz);
    }

    public List<DecXsdDatiSpec> retrieveDecXsdDatiSpecList(BigDecimal idStrut, String tiUsoXsd, String tiEntitaSacer,
            BigDecimal idTipoUnitaDoc, BigDecimal idTipoDoc, BigDecimal idTipoCompDoc) {
        return retrieveDecXsdDatiSpecList(idStrut, tiUsoXsd, tiEntitaSacer, idTipoUnitaDoc, idTipoDoc, idTipoCompDoc,
                null);
    }

    public List<DecXsdDatiSpec> retrieveDecXsdDatiSpecList(BigDecimal idStrut, String tiUsoXsd, String tiEntitaSacer,
            BigDecimal idTipoUnitaDoc, BigDecimal idTipoDoc, BigDecimal idTipoCompDoc, String nmSistemaMigraz) {
        String clause = " AND ";
        StringBuilder queryStr = new StringBuilder(
                "SELECT xsdDatiSpec FROM DecXsdDatiSpec xsdDatiSpec " + "WHERE xsdDatiSpec.orgStrut.idStrut = :idStrut "
                        + "AND xsdDatiSpec.tiUsoXsd = :tiUsoXsd " + "AND xsdDatiSpec.tiEntitaSacer = :tiEntitaSacer ");

        if (tiUsoXsd.equals(CostantiDB.TipiUsoDatiSpec.MIGRAZ.name()) && StringUtils.isNotBlank(nmSistemaMigraz)) {
            queryStr.append(clause).append("xsdDatiSpec.nmSistemaMigraz = :nmSistemaMigraz");
        } else {
            if (idTipoDoc != null) {
                queryStr.append(clause).append("xsdDatiSpec.decTipoDoc.idTipoDoc = :idTipoDoc");
            } else if (idTipoUnitaDoc != null) {
                queryStr.append(clause).append("xsdDatiSpec.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoUnitaDoc");
            } else if (idTipoCompDoc != null) {
                queryStr.append(clause).append("xsdDatiSpec.decTipoCompDoc.idTipoCompDoc = :idTipoCompDoc");
            }
        }
        queryStr.append(" ORDER BY xsdDatiSpec.dtIstituz DESC, xsdDatiSpec.cdVersioneXsd DESC");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        query.setParameter("tiUsoXsd", tiUsoXsd);
        query.setParameter("tiEntitaSacer", tiEntitaSacer);
        if (tiUsoXsd.equals(CostantiDB.TipiUsoDatiSpec.MIGRAZ.name()) && StringUtils.isNotBlank(nmSistemaMigraz)) {
            query.setParameter("nmSistemaMigraz", nmSistemaMigraz);
        } else {
            if (idTipoDoc != null) {
                query.setParameter("idTipoDoc", longFromBigDecimal(idTipoDoc));
            } else if (idTipoUnitaDoc != null) {
                query.setParameter("idTipoUnitaDoc", longFromBigDecimal(idTipoUnitaDoc));
            } else if (idTipoCompDoc != null) {
                query.setParameter("idTipoCompDoc", longFromBigDecimal(idTipoCompDoc));
            }
        }

        return query.getResultList();
    }

    public DecXsdDatiSpec getDecXsdDatiSpecByVersion(DecXsdDatiSpecRowBean xsdDatiSpec) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT xsdDatiSpec FROM DecXsdDatiSpec xsdDatiSpec WHERE xsdDatiSpec.cdVersioneXsd = :cdVersioneXsd");

        if (xsdDatiSpec.getNmSistemaMigraz() != null) {
            queryStr.append(
                    " AND xsdDatiSpec.orgStrut.idStrut=:idStrut " + " AND xsdDatiSpec.tiEntitaSacer = :tiSacerType "
                            + " AND xsdDatiSpec.nmSistemaMigraz = :nmSistemaMigraz");
        } else {
            if (xsdDatiSpec.getIdTipoDoc() != null) {
                queryStr.append(" AND xsdDatiSpec.decTipoDoc.idTipoDoc=:idTipoDoc");
            } else if (xsdDatiSpec.getIdTipoUnitaDoc() != null) {
                queryStr.append(" AND xsdDatiSpec.decTipoUnitaDoc.idTipoUnitaDoc=:idTipoUnitaDoc");
            } else if (xsdDatiSpec.getIdTipoCompDoc() != null) {
                queryStr.append(" AND xsdDatiSpec.decTipoCompDoc.idTipoCompDoc=:idTipoCompDoc");
            }
            queryStr.append(" AND xsdDatiSpec.tiUsoXsd != :isMigraz ");
        }
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("cdVersioneXsd", xsdDatiSpec.getCdVersioneXsd());

        if (xsdDatiSpec.getNmSistemaMigraz() != null) {
            query.setParameter("idStrut", longFromBigDecimal(xsdDatiSpec.getIdStrut()));
            query.setParameter("tiSacerType", xsdDatiSpec.getTiEntitaSacer());
            query.setParameter("nmSistemaMigraz", xsdDatiSpec.getNmSistemaMigraz());
        } else {
            if (xsdDatiSpec.getIdTipoDoc() != null) {
                query.setParameter("idTipoDoc", longFromBigDecimal(xsdDatiSpec.getIdTipoDoc()));
            } else if (xsdDatiSpec.getIdTipoUnitaDoc() != null) {
                query.setParameter("idTipoUnitaDoc", longFromBigDecimal(xsdDatiSpec.getIdTipoUnitaDoc()));
            } else if (xsdDatiSpec.getIdTipoCompDoc() != null) {
                query.setParameter("idTipoCompDoc", longFromBigDecimal(xsdDatiSpec.getIdTipoCompDoc()));
            }
            query.setParameter("isMigraz", "MIGRAZ");
        }
        List<DecXsdDatiSpec> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }

        return list.get(0);
    }

    public List<DecXsdDatiSpec> retrieveDecXsdDatiSpecList(OrgStrut strut, String tiUsoXsd, String tiEntitaSacer,
            BigDecimal idTipoUnitaDoc, BigDecimal idTipoDoc, BigDecimal idTipoCompDoc, String nmSistemaMigraz) {
        String clause = " AND ";
        StringBuilder queryStr = new StringBuilder(
                "SELECT xsdDatiSpec FROM DecXsdDatiSpec xsdDatiSpec " + "WHERE xsdDatiSpec.orgStrut = :strut"
                        + "AND xsdDatiSpec.tiUsoXsd = :tiUsoXsd " + "AND xsdDatiSpec.tiEntitaSacer = :tiEntitaSacer ");

        if (tiUsoXsd.equals(CostantiDB.TipiUsoDatiSpec.MIGRAZ.name()) && StringUtils.isNotBlank(nmSistemaMigraz)) {
            queryStr.append(clause).append("xsdDatiSpec.nmSistemaMigraz = :nmSistemaMigraz");
        } else {
            if (idTipoDoc != null) {
                queryStr.append(clause).append("xsdDatiSpec.decTipoDoc.idTipoDoc = :idTipoDoc");
            } else if (idTipoUnitaDoc != null) {
                queryStr.append(clause).append("xsdDatiSpec.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoUnitaDoc");
            } else if (idTipoCompDoc != null) {
                queryStr.append(clause).append("xsdDatiSpec.decTipoCompDoc.idTipoCompDoc = :idTipoCompDoc");
            }
        }
        queryStr.append(" ORDER BY xsdDatiSpec.dtIstituz DESC, xsdDatiSpec.cdVersioneXsd DESC");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("strut", strut);
        query.setParameter("tiUsoXsd", tiUsoXsd);
        query.setParameter("tiEntitaSacer", tiEntitaSacer);
        if (tiUsoXsd.equals(CostantiDB.TipiUsoDatiSpec.MIGRAZ.name()) && StringUtils.isNotBlank(nmSistemaMigraz)) {
            query.setParameter("nmSistemaMigraz", nmSistemaMigraz);
        } else {
            if (idTipoDoc != null) {
                query.setParameter("idTipoDoc", idTipoDoc);
            } else if (idTipoUnitaDoc != null) {
                query.setParameter("idTipoUnitaDoc", idTipoUnitaDoc);
            } else if (idTipoCompDoc != null) {
                query.setParameter("idTipoCompDoc", idTipoCompDoc);
            }
        }

        return query.getResultList();
    }

    public boolean checkRelationsAreEmptyForDecXsdDatiSpec(long idXsdDatiSpec) {
        boolean result = true;

        String queryStr = " select a from DecXsdDatiSpec a  " + " where a.idXsdDatiSpec=:idXsdDatiSpec "
                + " AND NOT EXISTS (select p from AroUsoXsdDatiSpec p where p.decXsdDatiSpec.idXsdDatiSpec=a.idXsdDatiSpec) ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idXsdDatiSpec", idXsdDatiSpec);
        List<Object[]> list = query.getResultList();
        result = list.isEmpty();
        return result;
    }

    public boolean campiRegoleInUso(BigDecimal idXsdDatiSpec) {
        Query query = getEntityManager()
                .createQuery("SELECT COUNT(xsdAttrib.decAttribDatiSpec) FROM OrgCampoValSubStrut campo "
                        + "JOIN campo.decAttribDatiSpec attrib JOIN attrib.decXsdAttribDatiSpecs xsdAttrib "
                        + "JOIN xsdAttrib.decXsdDatiSpec xsdDatiSpec WHERE xsdDatiSpec.idXsdDatiSpec = :idXsdDatiSpec ");
        query.setParameter("idXsdDatiSpec", longFromBigDecimal(idXsdDatiSpec));
        Long count = (Long) query.getSingleResult();
        return count > 0L;

    }

    public boolean getUseOfXsdDatiSpec(Long idXsdDatiSpec) {
        Query query = getEntityManager().createQuery(
                "SELECT count(uso) FROM AroUsoXsdDatiSpec uso WHERE uso.decXsdDatiSpec.idXsdDatiSpec = :idXsdDatiSpec");
        query.setParameter("idXsdDatiSpec", idXsdDatiSpec);
        long result = (Long) query.getSingleResult();

        return result > 0;
    }

    /*
     * modifica, aggiunta del controllo incrociato sia su idXsd che su idAttrib
     */
    public DecXsdAttribDatiSpec getDecXsdAttribDatiSpecByAttrib(BigDecimal idAttribDatiSpec, BigDecimal idXsdDatiSpec) {
        String queryStr = "SELECT xsdAttribDatiSpec FROM DecXsdAttribDatiSpec xsdAttribDatiSpec "
                + "WHERE xsdAttribDatiSpec.decAttribDatiSpec.idAttribDatiSpec = :idAttribDatiSpec "
                + "AND xsdAttribDatiSpec.decXsdDatiSpec.idXsdDatiSpec = :idXsdDatiSpec";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idAttribDatiSpec", longFromBigDecimal(idAttribDatiSpec));
        query.setParameter("idXsdDatiSpec", longFromBigDecimal(idXsdDatiSpec));
        List<DecXsdAttribDatiSpec> list = query.getResultList();

        if (list.isEmpty()) {
            return null;
        }
        return (list.get(0));

    }

    public DecXsdAttribDatiSpec getMigrazDecXsdAttribDatiSpecByNameAndXsdId(String nmAttribDatiSpec, BigDecimal idStrut,
            String nmSistemaMigraz, String tiSacerType, BigDecimal idXsdDatiSpec) {
        String queryStr = "SELECT e FROM DecXsdAttribDatiSpec e JOIN e.decAttribDatiSpec a "
                + "WHERE a.nmAttribDatiSpec= :nmAttribDatiSpec " + "AND a.tiEntitaSacer LIKE :tiSacerType "
                + "AND a.nmSistemaMigraz LIKE :nmSistemaMigraz " + "AND a.orgStrut.idStrut = :idStrut "
                + "AND e.decXsdDatiSpec.idXsdDatiSpec=:idXsdDatiSpec";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("nmAttribDatiSpec", nmAttribDatiSpec);

        query.setParameter("idXsdDatiSpec", longFromBigDecimal(idXsdDatiSpec));
        query.setParameter("tiSacerType", tiSacerType);
        query.setParameter("nmSistemaMigraz", nmSistemaMigraz);
        query.setParameter("idStrut", longFromBigDecimal(idStrut));

        return (DecXsdAttribDatiSpec) query.getSingleResult();
    }

    public DecXsdAttribDatiSpec getDecXsdAttribDatiSpecByNameAndXsdId(String nmAttribDatiSpec, BigDecimal idTipoDoc,
            BigDecimal idTipoUnitaDoc, BigDecimal idTipoCompDoc, BigDecimal idXsdDatiSpec) {
        StringBuilder queryStr = new StringBuilder(
                "SELECT e FROM DecXsdAttribDatiSpec e JOIN e.decAttribDatiSpec a WHERE a.nmAttribDatiSpec= :nmAttribDatiSpec "
                        + "and e.decXsdDatiSpec.idXsdDatiSpec=:idXsdDatiSpec ");

        if (idTipoDoc != null) {
            queryStr.append(" AND a.decTipoDoc.idTipoDoc=:idTipoDoc");
        } else if (idTipoUnitaDoc != null) {
            queryStr.append(" AND a.decTipoUnitaDoc.idTipoUnitaDoc=:idTipoUnitaDoc");
        } else if (idTipoCompDoc != null) {
            queryStr.append(" AND a.decTipoCompDoc.idTipoCompDoc=:idTipoCompDoc");
        }

        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("nmAttribDatiSpec", nmAttribDatiSpec);
        query.setParameter("idXsdDatiSpec", longFromBigDecimal(idXsdDatiSpec));

        if (idTipoDoc != null) {
            query.setParameter("idTipoDoc", longFromBigDecimal(idTipoDoc));
        } else if (idTipoUnitaDoc != null) {
            query.setParameter("idTipoUnitaDoc", longFromBigDecimal(idTipoUnitaDoc));
        } else if (idTipoCompDoc != null) {
            query.setParameter("idTipoCompDoc", longFromBigDecimal(idTipoCompDoc));
        }

        return (DecXsdAttribDatiSpec) query.getSingleResult();
    }

    public void deleteDecXsdDatiSpecMigraz(long idStrut) {
        Query q = getEntityManager().createQuery("DELETE FROM DecXsdDatiSpec xsdDatiSpec "
                + "WHERE xsdDatiSpec.orgStrut.idStrut = :idStrut " + "AND xsdDatiSpec.tiUsoXsd = 'MIGRAZ' ");
        q.setParameter("idStrut", idStrut);
        q.executeUpdate();
        getEntityManager().flush();
    }

    /**
     * Ritorna lo xsd per i dati specifici dati i parametri in input NB. E' brutto creare un rowBean così, purtroppo
     * fare un overloading non era possibile a causa dell'uguaglianza dei tipi parametro
     *
     * @param idStrut
     *            struttura
     * @param idTipoUnitaDoc
     *            tipo unita doc
     * 
     * @return l'ultimo xsd per quel tipo di unita documentaria
     */
    public DecXsdDatiSpec getLastDecXsdDatiSpecForTipoUnitaDoc(Long idStrut, Long idTipoUnitaDoc) {
        DecXsdDatiSpecRowBean xsdDatiSpecRowBean = new DecXsdDatiSpecRowBean();
        xsdDatiSpecRowBean.setIdStrut(new BigDecimal(idStrut));
        xsdDatiSpecRowBean.setIdTipoUnitaDoc(new BigDecimal(idTipoUnitaDoc));
        return getLastDecXsdDatiSpec(xsdDatiSpecRowBean);
    }

    /**
     * Ritorna lo xsd per i dati specifici dati i parametri in input NB. E' brutto creare un rowBean così, purtroppo
     * fare un overloading non era possibile a causa dell'uguaglianza dei tipi parametro
     *
     * @param idStrut
     *            struttura
     * @param idTipoCompDoc
     *            tipo componente
     * 
     * @return l'ultimo xsd per quel tipo di componente
     */
    public DecXsdDatiSpec getLastDecXsdDatiSpecForTipoCompDoc(Long idStrut, Long idTipoCompDoc) {
        DecXsdDatiSpecRowBean xsdDatiSpecRowBean = new DecXsdDatiSpecRowBean();
        xsdDatiSpecRowBean.setIdStrut(new BigDecimal(idStrut));
        xsdDatiSpecRowBean.setIdTipoCompDoc(new BigDecimal(idTipoCompDoc));
        return getLastDecXsdDatiSpec(xsdDatiSpecRowBean);
    }

    /**
     * Ritorna lo xsd per i dati specifici dati i parametri in input NB. E' brutto creare un rowBean così, purtroppo
     * fare un overloading non era possibile a causa dell'uguaglianza dei tipi parametro
     *
     * @param idStrut
     *            struttura
     * @param idTipoDoc
     *            tipo documento
     * 
     * @return l'ultimo xsd per quel tipo di documento
     */
    public DecXsdDatiSpec getLastDecXsdDatiSpecForTipoDoc(Long idStrut, Long idTipoDoc) {
        DecXsdDatiSpecRowBean xsdDatiSpecRowBean = new DecXsdDatiSpecRowBean();
        xsdDatiSpecRowBean.setIdStrut(new BigDecimal(idStrut));
        xsdDatiSpecRowBean.setIdTipoDoc(new BigDecimal(idTipoDoc));
        return getLastDecXsdDatiSpec(xsdDatiSpecRowBean);
    }

    /**
     * Ritorna lo xsd per i dati specifici dati i parametri in input
     *
     * @param xsdDatiSpecRowBean
     *            Bean contenente i parametri
     * 
     * @return la lista di xsd per i dati specifici
     */
    public DecXsdDatiSpec getLastDecXsdDatiSpec(DecXsdDatiSpecRowBean xsdDatiSpecRowBean) {
        String clause = " AND ";
        StringBuilder queryStr = new StringBuilder(
                "SELECT xsdDatiSpec FROM DecXsdDatiSpec xsdDatiSpec WHERE xsdDatiSpec.orgStrut.idStrut = :idStrut ");
        if (StringUtils.isNotBlank(xsdDatiSpecRowBean.getNmSistemaMigraz())) {
            queryStr.append(clause).append("xsdDatiSpec.tiUsoXsd = 'MIGRAZ'");
            queryStr.append(clause).append("xsdDatiSpec.nmSistemaMigraz = :nmSistemaMigraz ");
        } else {
            queryStr.append(clause).append("xsdDatiSpec.tiUsoXsd = 'VERS'");
            if (xsdDatiSpecRowBean.getIdTipoDoc() != null) {
                queryStr.append(clause).append("xsdDatiSpec.tiEntitaSacer = 'DOC'");
                queryStr.append(clause).append("xsdDatiSpec.decTipoDoc.idTipoDoc = :idTipoDoc ");
            } else if (xsdDatiSpecRowBean.getIdTipoUnitaDoc() != null) {
                queryStr.append(clause).append("xsdDatiSpec.tiEntitaSacer = 'UNI_DOC'");
                queryStr.append(clause).append("xsdDatiSpec.decTipoUnitaDoc.idTipoUnitaDoc = :idTipoUnitaDoc ");
            } else if (xsdDatiSpecRowBean.getIdTipoCompDoc() != null) {
                queryStr.append(clause).append("xsdDatiSpec.tiEntitaSacer IN ('COMP', 'SUB_COMP')");
                queryStr.append(clause).append("xsdDatiSpec.decTipoCompDoc.idTipoCompDoc = :idTipoCompDoc ");
            }
        }
        /*
         * Bisogna fare una subQuery con gli stessi dati, per ottenere il record con la massima data. Questa è l'idea
         * più veloce che mi sia venuta, in sostanza alla fine diventerà così :
         *
         * 'AND xsdDatiSpec.dtIstituz = (SELECT MAX(subQuery.dtIstituz) FROM .... *stessi parametri inseriti prima*)
         *
         */
        String subQuery = queryStr.toString()
                .replaceFirst("SELECT xsdDatiSpec FROM", "SELECT MAX(xsdDatiSpec.dtIstituz) FROM")
                .replace("xsdDatiSpec", "subQuery");
        subQuery += " AND subQuery.dtIstituz <= :filterDate AND subQuery.dtSoppres >= :filterDate ";
        String subQuery2 = queryStr.toString()
                .replaceFirst("SELECT xsdDatiSpec FROM", "SELECT MAX(xsdDatiSpec.cdVersioneXsd) FROM")
                .replace("xsdDatiSpec", "subQuery2");
        subQuery2 += " AND subQuery2.dtIstituz <= :filterDate AND subQuery2.dtSoppres >= :filterDate ";
        queryStr.append(" AND xsdDatiSpec.dtIstituz = (").append(subQuery).append(")");

        // Corretto il bug riguardante l'esclusione degli xsd sopressi MAC#11507
        queryStr.append(" AND xsdDatiSpec.dtSoppres >= :filterDate ");

        queryStr.append(" AND xsdDatiSpec.cdVersioneXsd = (").append(subQuery2).append(")");
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idStrut", longFromBigDecimal(xsdDatiSpecRowBean.getIdStrut()));
        if (StringUtils.isNotBlank(xsdDatiSpecRowBean.getNmSistemaMigraz())) {
            query.setParameter("nmSistemaMigraz", xsdDatiSpecRowBean.getNmSistemaMigraz());
        } else {
            if (xsdDatiSpecRowBean.getIdTipoDoc() != null) {
                query.setParameter("idTipoDoc", longFromBigDecimal(xsdDatiSpecRowBean.getIdTipoDoc()));
            } else if (xsdDatiSpecRowBean.getIdTipoUnitaDoc() != null) {
                query.setParameter("idTipoUnitaDoc", longFromBigDecimal(xsdDatiSpecRowBean.getIdTipoUnitaDoc()));
            } else if (xsdDatiSpecRowBean.getIdTipoCompDoc() != null) {
                query.setParameter("idTipoCompDoc", longFromBigDecimal(xsdDatiSpecRowBean.getIdTipoCompDoc()));
            }
        }
        Date now = Calendar.getInstance().getTime();
        query.setParameter("filterDate", now);

        List<DecXsdDatiSpec> list = query.getResultList();
        DecXsdDatiSpec result = null;
        if (!list.isEmpty()) {
            result = list.get(0);
        }
        return result;
    }

    public List<DecAttribDatiSpec> getDecAttribDatiSpecList(BigDecimal idStrut, String tiEntitaSacer,
            BigDecimal idEntity) {
        String entity;
        CostantiDB.TipiEntitaSacer entita = CostantiDB.TipiEntitaSacer.valueOf(tiEntitaSacer);
        switch (entita) {
        case UNI_DOC:
            entity = "decTipoUnitaDoc.idTipoUnitaDoc = ";
            break;
        case DOC:
            entity = "decTipoDoc.idTipoDoc = ";
            break;
        case COMP:
        case SUB_COMP:
        default:
            throw new IllegalArgumentException("Tipo entit\u00E0 sacer inaspettato");
        }
        String queryStr = "SELECT attribDatiSpec FROM DecXsdAttribDatiSpec xsdAttrib "
                + "JOIN xsdAttrib.decAttribDatiSpec attribDatiSpec "
                + "WHERE attribDatiSpec.tiEntitaSacer = :tiEntitaSacer "
                + "AND attribDatiSpec.orgStrut.idStrut = :idStrut " + "AND attribDatiSpec." + entity + ":idEntity "
                + "AND xsdAttrib.decXsdDatiSpec.dtIstituz = " + "(select MAX(d.dtIstituz) FROM DecXsdDatiSpec d "
                + "WHERE d.orgStrut.idStrut = :idStrut " + "AND d." + entity
                + ":idEntity AND d.tiEntitaSacer = :tiEntitaSacer "
                + "AND d.dtIstituz <= :filterDate AND d.dtSoppres >= :filterDate " + "AND d.tiUsoXsd = 'VERS' " + ")";
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrut", longFromBigDecimal(idStrut));
        query.setParameter("idEntity", longFromBigDecimal(idEntity));
        query.setParameter("tiEntitaSacer", tiEntitaSacer);
        Date now = Calendar.getInstance().getTime();
        query.setParameter("filterDate", now);

        return query.getResultList();
    }

    /**
     * Verifico che per qualunque attributo dato specifico riferito all'XSD considerato, esista un campo di tipo come
     * quello/i passato/i nel parametro
     *
     * @param idXsdDatiSpec
     *            xsd dati specifici
     * @param tipiCampo
     *            tipo compo (array opzinale)
     * 
     * @return true/false
     */
    public boolean existsCampoSuXsdDatiSpec(BigDecimal idXsdDatiSpec, String... tipiCampo) {
        String queryStr = "SELECT COUNT(xsdAttribDatiSpec) FROM DecXsdAttribDatiSpec xsdAttribDatiSpec "
                + "JOIN xsdAttribDatiSpec.decAttribDatiSpec attribDatiSpec "
                + "WHERE xsdAttribDatiSpec.decXsdDatiSpec.idXsdDatiSpec = :idXsdDatiSpec "
                + "AND EXISTS (SELECT campo FROM OrgCampoValSubStrut campo WHERE campo.decAttribDatiSpec = attribDatiSpec ";

        if (tipiCampo != null) {
            queryStr = queryStr + "AND campo.tiCampo IN (:tipiCampo)) ";
        } else {
            queryStr = queryStr + ")";
        }

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idXsdDatiSpec", idXsdDatiSpec.longValue());
        if (tipiCampo != null) {
            query.setParameter("tipiCampo", Arrays.asList(tipiCampo));
        }

        Long count = (Long) query.getSingleResult();
        return count > 0L;
    }

    // TODO: Sarebbe da rifare meglio in quanto si dà per scontato che solo uno tra nmTipoUnitaDoc, nmTipoDoc ed
    // nmTipocompdoc sia valorizzato
    public DecXsdDatiSpec getDecXsdDatiSpec(BigDecimal idStrutCorrente, String tiUsoXsd, String tiEntitaSacer,
            String nmTipoUnitaDoc, String nmTipoDoc, String nmTipoCompDoc, String nmTipoStrutDoc,
            String nmSistemaMigraz, String cdVersioneXsd) {
        String whereWord = " WHERE ";
        String queryStr = "SELECT xsdDatiSpec FROM DecXsdDatiSpec xsdDatiSpec ";

        if (nmTipoUnitaDoc != null) {
            queryStr = queryStr + whereWord + " xsdDatiSpec.decTipoUnitaDoc.orgStrut.idStrut = :idStrutCorrente "
                    + "AND xsdDatiSpec.decTipoUnitaDoc.nmTipoUnitaDoc = :nmTipoUnitaDoc ";
            whereWord = " AND ";
        }

        if (nmTipoDoc != null) {
            queryStr = queryStr + whereWord + " xsdDatiSpec.decTipoDoc.orgStrut.idStrut = :idStrutCorrente "
                    + "AND xsdDatiSpec.decTipoDoc.nmTipoDoc = :nmTipoDoc ";
            whereWord = " AND ";
        }

        if (nmTipoCompDoc != null) {
            queryStr = queryStr + whereWord
                    + " xsdDatiSpec.decTipoCompDoc.decTipoStrutDoc.orgStrut.idStrut = :idStrutCorrente "
                    + "AND xsdDatiSpec.decTipoCompDoc.decTipoStrutDoc.nmTipoStrutDoc = :nmTipoStrutDoc "
                    + "AND xsdDatiSpec.decTipoCompDoc.nmTipoCompDoc = :nmTipoCompDoc ";
            whereWord = " AND ";
        }

        if (nmSistemaMigraz != null) {
            queryStr = queryStr + whereWord + " xsdDatiSpec.orgStrut.idStrut = :idStrutCorrente ";
            whereWord = " AND ";
        }

        if (tiUsoXsd.equals("MIGRAZ")) {
            queryStr = queryStr + whereWord + " xsdDatiSpec.nmSistemaMigraz = :nmSistemaMigraz ";
        } else {
            queryStr = queryStr + whereWord + " xsdDatiSpec.nmSistemaMigraz IS NULL ";
        }

        whereWord = " AND ";
        queryStr = queryStr + whereWord + " xsdDatiSpec.tiUsoXsd = :tiUsoXsd "
                + "AND xsdDatiSpec.tiEntitaSacer = :tiEntitaSacer " + "AND xsdDatiSpec.cdVersioneXsd = :cdVersioneXsd ";

        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idStrutCorrente", longFromBigDecimal(idStrutCorrente));
        if (nmTipoUnitaDoc != null) {
            query.setParameter("nmTipoUnitaDoc", nmTipoUnitaDoc);
        }
        if (nmTipoDoc != null) {
            query.setParameter("nmTipoDoc", nmTipoDoc);
        }
        if (nmTipoCompDoc != null) {
            query.setParameter("nmTipoCompDoc", nmTipoCompDoc);
            query.setParameter("nmTipoStrutDoc", nmTipoStrutDoc);
        }
        query.setParameter("tiUsoXsd", tiUsoXsd);
        query.setParameter("tiEntitaSacer", tiEntitaSacer);
        query.setParameter("cdVersioneXsd", cdVersioneXsd);
        if (tiUsoXsd.equals("MIGRAZ")) {
            query.setParameter("nmSistemaMigraz", nmSistemaMigraz);
        }
        List<DecXsdDatiSpec> list = query.getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public DecTipoStrutUdXsd getDecTipoStrutUdXsdByName(BigDecimal idStrutCorrente, String nmTipoStrutUnitaDoc,
            String tiUsoXsd, String tiEntitaSacer, String nmTipoUnitaDoc, String nmSistemaMigraz,
            String cdVersioneXsd) {
        String queryStr = "SELECT tipoStrutUdXsd FROM DecTipoStrutUdXsd tipoStrutUdXsd ";

        if (nmTipoUnitaDoc != null) {
            queryStr = queryStr
                    + "WHERE tipoStrutUdXsd.decTipoStrutUnitaDoc.nmTipoStrutUnitaDoc = :nmTipoStrutUnitaDoc "
                    + "AND tipoStrutUdXsd.decTipoStrutUnitaDoc.decTipoUnitaDoc.nmTipoUnitaDoc = :nmTipoUnitaDoc "
                    + "AND tipoStrutUdXsd.decTipoStrutUnitaDoc.decTipoUnitaDoc.orgStrut.idStrut = :idStrutCorrente ";
        }

        if (tiUsoXsd.equals("MIGRAZ")) {
            queryStr = queryStr + "AND tipoStrutUdXsd.decXsdDatiSpec.nmSistemaMigraz = :nmSistemaMigraz ";
        } else {
            queryStr = queryStr + "AND tipoStrutUdXsd.decXsdDatiSpec.nmSistemaMigraz IS NULL ";
        }

        queryStr = queryStr + "AND tipoStrutUdXsd.decXsdDatiSpec.tiUsoXsd = :tiUsoXsd "
                + "AND tipoStrutUdXsd.decXsdDatiSpec.tiEntitaSacer = :tiEntitaSacer "
                + "AND tipoStrutUdXsd.decXsdDatiSpec.cdVersioneXsd = :cdVersioneXsd ";

        Query query = getEntityManager().createQuery(queryStr);

        if (nmTipoUnitaDoc != null) {
            query.setParameter("nmTipoUnitaDoc", nmTipoUnitaDoc);
            query.setParameter("idStrutCorrente", longFromBigDecimal(idStrutCorrente));
            query.setParameter("nmTipoStrutUnitaDoc", nmTipoStrutUnitaDoc);
        }
        query.setParameter("tiUsoXsd", tiUsoXsd);
        query.setParameter("tiEntitaSacer", tiEntitaSacer);
        query.setParameter("cdVersioneXsd", cdVersioneXsd);
        if (tiUsoXsd.equals("MIGRAZ")) {
            query.setParameter("nmSistemaMigraz", nmSistemaMigraz);
        }
        List<DecTipoStrutUdXsd> list = query.getResultList();
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return null;
        }
    }

    public Long countXsdDatiSpecInUseInTipiSerie(BigDecimal idXsdDatiSpec) {
        String queryStr = "SELECT COUNT(attribDatiSpec) " + "FROM DecXsdAttribDatiSpec xsdAttribDatiSpec "
                + "JOIN xsdAttribDatiSpec.decAttribDatiSpec attribDatiSpec "
                + "WHERE xsdAttribDatiSpec.decXsdDatiSpec.idXsdDatiSpec = :idXsdDatiSpec " + "AND ("
                + "EXISTS (SELECT campoInp FROM DecCampoInpUd campoInp WHERE campoInp.decAttribDatiSpec = attribDatiSpec)"
                + "OR EXISTS (SELECT campoOut FROM DecCampoOutSelUd campoOut WHERE campoOut.decAttribDatiSpec = attribDatiSpec)"
                + "OR EXISTS (SELECT filtro FROM DecFiltroSelUdDato filtro WHERE filtro.decAttribDatiSpec = attribDatiSpec))" // +
                                                                                                                              // ""
        ;
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idXsdDatiSpec", idXsdDatiSpec.longValue());

        return (Long) query.getSingleResult();
    }

}
