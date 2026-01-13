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

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.AroUpdUnitaDoc;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.slite.gen.form.ElenchiVersamentoForm;
import it.eng.parer.slite.gen.tablebean.AroUpdUnitaDocRowBean;
import it.eng.parer.slite.gen.viewbean.ElvVLisUpdUdRowBean;
import it.eng.parer.slite.gen.viewbean.ElvVLisUpdUdTableBean;
import it.eng.parer.viewEntity.ElvVLisUpdUd;
import it.eng.parer.web.util.StringPadding;
import it.eng.parer.web.util.Transform;
import it.eng.spagoCore.error.EMFError;

@Stateless
@LocalBean
public class AggiornamentiHelper extends GenericHelper {

    private static final Logger LOG = LoggerFactory.getLogger(AggiornamentiHelper.class.getName());

    @SuppressWarnings("unchecked")
    public AroUpdUnitaDocRowBean getAroUpdUnitaDocRowBean(BigDecimal idUpdUnitaDoc,
            BigDecimal idStrut) {
        String queryStr = "SELECT u FROM AroUpdUnitaDoc u where u.idUpdUnitaDoc = :idupdunitadoc and u.orgStrut.idStrut = :idstrut";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idupdunitadoc", longFromBigDecimal(idUpdUnitaDoc));
        query.setParameter("idstrut", longFromBigDecimal(idStrut));

        // ESEGUO LA QUERY E INSERISCO I RISULTATI IN UNA LISTA DI "Aggiornamenti"
        List<AroUpdUnitaDoc> aggiornamentiList = query.getResultList();

        AroUpdUnitaDocRowBean aggiornamentoRb = new AroUpdUnitaDocRowBean();

        try {
            if (aggiornamentiList != null && aggiornamentiList.size() == 1) {
                aggiornamentoRb = (AroUpdUnitaDocRowBean) Transform
                        .entity2RowBean(aggiornamentiList.get(0));
            }
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException
                | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            LOG.error(e.getMessage(), e);
        }
        return aggiornamentoRb;
    }

    public it.eng.parer.slite.gen.viewbean.ElvVLisUpdUdTableBean getElvVLisUpdUdViewBean(
            BigDecimal idElencoVers, ElenchiVersamentoForm.ComponentiFiltri filtri)
            throws it.eng.spagoCore.error.EMFError {
        return getElvVLisUpdUdViewBean(idElencoVers, filtri.getCd_registro_key_unita_doc().parse(),
                filtri.getAa_key_unita_doc().parse(), filtri.getCd_key_unita_doc().parse(),
                filtri.getAa_key_unita_doc_da().parse(), filtri.getAa_key_unita_doc_a().parse(),
                filtri.getCd_key_unita_doc_da().parse(), filtri.getCd_key_unita_doc_a().parse());
    }

    @SuppressWarnings("unchecked")
    public ElvVLisUpdUdTableBean getElvVLisUpdUdViewBean(BigDecimal idElencoVers,
            final String registro, final BigDecimal anno, final String codice,
            final BigDecimal annpRangeDa, final BigDecimal annoRangeA, String codiceRangeDa,
            String codiceRangeA) throws EMFError {
        String whereWord = "AND ";
        StringBuilder queryStr = new StringBuilder(
                "SELECT DISTINCT new it.eng.parer.viewEntity.ElvVLisUpdUd"
                        + "(u.idElencoVers, u.idUpdUnitaDoc, u.dsUrnUpdUnitaDoc, u.dsKeyOrd, u.tsIniSes, "
                        + "u.idUnitaDoc, u.cdRegistroKeyUnitaDoc, u.tiStatoUpdElencoVers, u.tiStatoConservazione) "
                        + "FROM ElvVLisUpdUd u WHERE u.idElencoVers = :idElencoVers ");
        // Inserimento nella query del filtro CHIAVE AGGIORNAMENTO
        if (registro != null) {
            queryStr.append(whereWord).append("u.cdRegistroKeyUnitaDoc = :registroin ");
            whereWord = "and ";
        }
        if (anno != null) {
            queryStr.append(whereWord).append("u.aaKeyUnitaDoc = :annoin ");
            whereWord = "and ";
        }
        if (codice != null) {
            queryStr.append(whereWord).append("u.cdKeyUnitaDoc = :codicein ");
            whereWord = "and ";
        }
        if (annpRangeDa != null && annoRangeA != null) {
            queryStr.append(whereWord)
                    .append("(u.aaKeyUnitaDoc BETWEEN :annoin_da AND :annoin_a) ");
            whereWord = " AND ";
        }
        if (codiceRangeDa != null && codiceRangeA != null) {
            codiceRangeDa = StringPadding.padString(codiceRangeDa, "0", 12,
                    StringPadding.PADDING_LEFT);
            codiceRangeA = StringPadding.padString(codiceRangeA, "0", 12,
                    StringPadding.PADDING_LEFT);
            queryStr.append(whereWord).append(
                    "LPAD( u.cdKeyUnitaDoc, 12, '0') BETWEEN :codicein_da AND :codicein_a ");
        }
        queryStr.append("ORDER BY u.dsKeyOrd");
        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr.toString());
        query.setParameter("idElencoVers", idElencoVers);
        if (registro != null) {
            query.setParameter("registroin", registro);
        }
        if (anno != null) {
            query.setParameter("annoin", anno);
        }
        if (codice != null) {
            query.setParameter("codicein", codice);
        }
        if (annpRangeDa != null && annoRangeA != null) {
            query.setParameter("annoin_da", annpRangeDa);
            query.setParameter("annoin_a", annoRangeA);
        }
        if (codiceRangeDa != null && codiceRangeA != null) {
            query.setParameter("codicein_da", codiceRangeDa);
            query.setParameter("codicein_a", codiceRangeA);
        }
        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA

        List<ElvVLisUpdUd> listaAggiornamenti = query.getResultList();
        // Ordino per pgUpdUnitaDoc che non potevo mettere nella query visto che non è tra i campi
        // della distinct
        Collections.sort(listaAggiornamenti, (ElvVLisUpdUd e1, ElvVLisUpdUd e2) -> {
            BigDecimal b1 = e1.getPgUpdUnitaDoc() == null ? BigDecimal.ZERO : e1.getPgUpdUnitaDoc();
            BigDecimal b2 = e2.getPgUpdUnitaDoc() == null ? BigDecimal.ZERO : e2.getPgUpdUnitaDoc();
            return b1.compareTo(b2);
        });
        ElvVLisUpdUdTableBean aggiornamentiTableBean = new ElvVLisUpdUdTableBean();
        try {
            if (listaAggiornamenti != null && !listaAggiornamenti.isEmpty()) {
                aggiornamentiTableBean = (ElvVLisUpdUdTableBean) Transform
                        .entities2TableBean(listaAggiornamenti);
            }
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException
                | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            LOG.error(e.getMessage(), e);
        }
        // setta il campo relativo alla checkbox select_upd non ceccato
        for (int i = 0; i < aggiornamentiTableBean.size(); i++) {
            ElvVLisUpdUdRowBean row = aggiornamentiTableBean.getRow(i);
            row.setString("select_upd", "0");
        }
        return aggiornamentiTableBean;
    }
}
