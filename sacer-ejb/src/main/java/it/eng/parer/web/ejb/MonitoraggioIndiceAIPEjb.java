package it.eng.parer.web.ejb;

import it.eng.parer.web.helper.MonitoraggioIndiceAIPHelper;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//IndiceA

/**
 *
 * @author Gilioli_P
 */
@Stateless(mappedName = "MonitoraggioIndiceAIPEjb")
@LocalBean
public class MonitoraggioIndiceAIPEjb {

    @EJB(mappedName = "java:app/Parer-ejb/MonitoraggioIndiceAIPHelper")
    private MonitoraggioIndiceAIPHelper monitoraggioIndiceAIPHelper;

    private static final Logger logger = LoggerFactory.getLogger(MonitoraggioIndiceAIPEjb.class);

    public BaseTable calcolaRiepilogoProcessoGenerazioneIndiceAIP(BigDecimal idAmbiente, BigDecimal idEnte,
            BigDecimal idStrut, BigDecimal niGgStato) {
        BaseTable tabella = new BaseTable();

        List<Object[]> risultati = monitoraggioIndiceAIPHelper.getRiepilogo(idAmbiente, idEnte, idStrut, niGgStato);
        risultati.forEach((objArr) -> {
            BaseRow riga = new BaseRow();
            riga.setString("cd_ti_eve_stato_elenco_vers", (String) objArr[0]);
            riga.setBigDecimal("ni_elenchi_fisc", (BigDecimal) objArr[2]);
            riga.setBigDecimal("ni_elenchi_no_fisc", (BigDecimal) objArr[3]);
            riga.setBigDecimal("ni_elenchi_total", (BigDecimal) objArr[1]);
            tabella.add(riga);
        });

        return tabella;
    }

    public BaseTable calcolaTotaliListaStruttureIndiceAIP(BigDecimal idAmbiente, BigDecimal idEnte,
            BigDecimal niGgStato, String cdTiEveStatoElencoVers) {
        BaseTable tabella = new BaseTable();

        List<Object[]> risultati = monitoraggioIndiceAIPHelper.getRiepilogoStrutture(idAmbiente, idEnte, niGgStato,
                cdTiEveStatoElencoVers);
        risultati.forEach((objArr) -> {
            BaseRow riga = new BaseRow();
            riga.setString("nm_strut", (String) objArr[0]);
            riga.setBigDecimal("id_strut", (BigDecimal) objArr[1]);
            riga.setBigDecimal("ni_elenchi_fisc", (BigDecimal) objArr[6]);
            riga.setBigDecimal("ni_elenchi_no_fisc", (BigDecimal) objArr[7]);
            riga.setBigDecimal("ni_elenchi_total", (BigDecimal) objArr[5]);
            riga.setBigDecimal("id_ambiente", (BigDecimal) objArr[3]);
            riga.setBigDecimal("id_ente", (BigDecimal) objArr[4]);
            tabella.add(riga);
        });

        return tabella;
    }

    public BaseTable getStatiElenco() {
        List<String> listaStati = monitoraggioIndiceAIPHelper.getStatiElencoNoCompletato();
        // List<Object[]> listaStati = monitoraggioIndiceAIPHelper.getRiepilogo(idAmbiente, idEnte, idStrut, niGgStato);
        BaseTable tabella = new BaseTable();
        for (String stato : listaStati) {
            BaseRow riga = new BaseRow();
            riga.setString("cd_ti_eve_stato_elenco_vers", stato);
            tabella.add(riga);
        }

        return tabella;
    }

    public BaseTable calcolaTotaliListaElenchiIndiceAIP(BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut,
            BigDecimal niGgStato, String cdTiEveStatoElencoVers, String fiscali) {
        BaseTable tabella = new BaseTable();

        List<Object[]> risultati = monitoraggioIndiceAIPHelper.getRiepilogoElenchi(idAmbiente, idEnte, idStrut,
                niGgStato, cdTiEveStatoElencoVers, fiscali);
        risultati.forEach((objArr) -> {
            BaseRow riga = new BaseRow();
            riga.setString("struttura", (String) objArr[0]);
            riga.setBigDecimal("id_elenco_vers", (BigDecimal) objArr[1]);
            riga.setString("nm_elenco", (String) objArr[2]);
            riga.setString("ds_elenco", (String) objArr[3]);
            riga.setString("fl_elenco_fisc", (String) objArr[4]);
            riga.setBigDecimal("ni_unita_doc_tot", (BigDecimal) objArr[5]);
            riga.setBigDecimal("ni_unita_doc_vers_elenco", (BigDecimal) objArr[6]);
            riga.setBigDecimal("ni_doc_agg_elenco", (BigDecimal) objArr[7]);
            riga.setBigDecimal("ni_upd_unita_doc", (BigDecimal) objArr[8]);
            riga.setTimestamp("ts_stato_elenco_vers", ((Timestamp) objArr[9]));
            BigDecimal giorni = (BigDecimal) objArr[13];
            BigDecimal ore = (BigDecimal) objArr[14];
            BigDecimal minuti = (BigDecimal) objArr[15];
            riga.setString("permanenza", giorni + "-" + ore + "-" + minuti);
            riga.setBigDecimal("id_ambiente", (BigDecimal) objArr[10]);
            riga.setBigDecimal("id_ente", (BigDecimal) objArr[11]);
            riga.setBigDecimal("id_strut", (BigDecimal) objArr[12]);
            tabella.add(riga);
        });

        return tabella;
    }

    public BaseTable calcolaTotaliListaUdIndiceAIP(BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut,
            String cdRegistroKeyUnitaDoc, BigDecimal aaKeyUnitaDoc, String cdKeyUnitaDoc, BigDecimal nnGgStato,
            String cdTiEveStatoElencoVers, String fiscali, BigDecimal idElencoVers, String tiStatoUdElencoVers) {
        BaseTable tabella = new BaseTable();

        List<Object[]> risultati = monitoraggioIndiceAIPHelper.getRiepilogoUd(idAmbiente, idEnte, idStrut,
                cdRegistroKeyUnitaDoc, aaKeyUnitaDoc, cdKeyUnitaDoc, nnGgStato, cdTiEveStatoElencoVers, fiscali,
                idElencoVers, tiStatoUdElencoVers);
        for (Object[] objArr : risultati) {
            BaseRow riga = new BaseRow();
            riga.setString("struttura", (String) objArr[0]);
            riga.setBigDecimal("id_elenco", (BigDecimal) objArr[1]);
            riga.setString("nm_elenco", (String) objArr[2]);
            riga.setString("ds_elenco", (String) objArr[3]);
            riga.setString("fl_elenco_fisc", (String) objArr[4]);
            riga.setBigDecimal("id_unita_doc", (BigDecimal) objArr[5]);
            riga.setString("cd_registro_key_unita_doc", (String) objArr[6]);
            riga.setBigDecimal("aa_key_unita_doc", (BigDecimal) objArr[7]);
            riga.setString("cd_key_unita_doc", (String) objArr[8]);
            riga.setString("stato_ud", (String) objArr[9]);
            riga.setTimestamp("timestamp_stato", ((Timestamp) objArr[10]));
            BigDecimal giorni = (BigDecimal) objArr[11];
            BigDecimal ore = (BigDecimal) objArr[12];
            BigDecimal minuti = (BigDecimal) objArr[13];
            riga.setString("permanenza", giorni + "-" + ore + "-" + minuti);
            riga.setString("fl_verifica_firma_eseguita", "" + (BigDecimal) objArr[14]);
            riga.setString("fl_indice_aip_creato", "" + (BigDecimal) objArr[15]);
            tabella.add(riga);
        }

        return tabella;
    }

    public BaseTable contaTotaliListaUdIndiceAIP() {
        List<Object[]> risultati = monitoraggioIndiceAIPHelper.contaNumeroMessaggiInCoda();
        BaseTable tabella = new BaseTable();
        risultati.stream().map((objArr) -> {
            BaseRow riga = new BaseRow();
            riga.setBigDecimal("msg_coda", BigDecimal.valueOf((Long) objArr[0]));
            riga.setTimestamp("timestamp_min", ((Timestamp) objArr[1]));
            riga.setTimestamp("timestamp_max", ((Timestamp) objArr[2]));
            return riga;
        }).forEachOrdered((riga) -> {
            tabella.add(riga);
        });
        return tabella;
    }
}
