package it.eng.parer.web.helper;

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
import java.math.BigDecimal;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless
@LocalBean
public class AggiornamentiHelper extends GenericHelper {

    private static final Logger log = LoggerFactory.getLogger(AggiornamentiHelper.class.getName());

    public AroUpdUnitaDocRowBean getAroUpdUnitaDocRowBean(BigDecimal idUpdUnitaDoc, BigDecimal idStrut) {
        String queryStr = "SELECT u FROM AroUpdUnitaDoc u where u.idUpdUnitaDoc = :idupdunitadoc and u.idStrut = :idstrut";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createQuery(queryStr);
        query.setParameter("idupdunitadoc", idUpdUnitaDoc);
        query.setParameter("idstrut", idStrut);

        // ESEGUO LA QUERY E INSERISCO I RISULTATI IN UNA LISTA DI "Aggiornamenti"
        List<AroUpdUnitaDoc> aggiornamentiList = query.getResultList();

        AroUpdUnitaDocRowBean aggiornamentoRb = new AroUpdUnitaDocRowBean();

        try {
            if (aggiornamentiList != null && aggiornamentiList.size() == 1) {
                aggiornamentoRb = (AroUpdUnitaDocRowBean) Transform.entity2RowBean(aggiornamentiList.get(0));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return aggiornamentoRb;
    }

    public ElvVLisUpdUdTableBean getElvVLisUpdUdViewBean(BigDecimal idElencoVers,
            ElenchiVersamentoForm.ComponentiFiltri filtri) throws EMFError {
        String whereWord = "AND ";
        StringBuilder queryStr = new StringBuilder("SELECT DISTINCT new it.eng.parer.viewEntity.ElvVLisUpdUd"
                + "(u.idElencoVers, u.idUpdUnitaDoc, u.dsUrnUpdUnitaDoc, u.dsKeyOrd, u.tsIniSes, "
                + "u.idUnitaDoc, u.cdRegistroKeyUnitaDoc, u.tiStatoUpdElencoVers, u.tiStatoConservazione) "
                + "FROM ElvVLisUpdUd u WHERE u.idElencoVers = :idElencoVers ");

        // Inserimento nella query del filtro CHIAVE AGGIORNAMENTO
        String registro = filtri.getCd_registro_key_unita_doc().parse();
        BigDecimal anno = filtri.getAa_key_unita_doc().parse();
        String codice = filtri.getCd_key_unita_doc().parse();
        BigDecimal anno_range_da = filtri.getAa_key_unita_doc_da().parse();
        BigDecimal anno_range_a = filtri.getAa_key_unita_doc_a().parse();
        String codice_range_da = filtri.getCd_key_unita_doc_da().parse();
        String codice_range_a = filtri.getCd_key_unita_doc_a().parse();

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

        if (anno_range_da != null && anno_range_a != null) {
            queryStr.append(whereWord).append("(u.aaKeyUnitaDoc BETWEEN :annoin_da AND :annoin_a) ");
            whereWord = " AND ";
        }

        if (codice_range_da != null && codice_range_a != null) {
            codice_range_da = StringPadding.padString(codice_range_da, "0", 12, StringPadding.PADDING_LEFT);
            codice_range_a = StringPadding.padString(codice_range_a, "0", 12, StringPadding.PADDING_LEFT);
            queryStr.append(whereWord)
                    .append("FUNC('lpad', u.cdKeyUnitaDoc, 12, '0') BETWEEN :codicein_da AND :codicein_a ");
            whereWord = " AND ";
        }

        queryStr.append("ORDER BY u.dsKeyOrd, u.pgUpdUnitaDoc ");

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

        if (anno_range_da != null && anno_range_a != null) {
            query.setParameter("annoin_da", anno_range_da);
            query.setParameter("annoin_a", anno_range_a);
        }

        if (codice_range_da != null && codice_range_a != null) {
            query.setParameter("codicein_da", codice_range_da);
            query.setParameter("codicein_a", codice_range_a);
        }

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        List<ElvVLisUpdUd> listaAggiornamenti = query.getResultList();

        ElvVLisUpdUdTableBean aggiornamentiTableBean = new ElvVLisUpdUdTableBean();
        try {
            if (listaAggiornamenti != null && !listaAggiornamenti.isEmpty()) {
                aggiornamentiTableBean = (ElvVLisUpdUdTableBean) Transform.entities2TableBean(listaAggiornamenti);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        // setta il campo relativo alla checkbox select_upd non ceccato
        for (int i = 0; i < aggiornamentiTableBean.size(); i++) {
            ElvVLisUpdUdRowBean row = aggiornamentiTableBean.getRow(i);
            row.setString("select_upd", "0");
        }

        return aggiornamentiTableBean;
    }
}
