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

package it.eng.parer.web.ejb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.eng.paginator.helper.LazyListHelper;
import it.eng.parer.datamart.dto.ConteggioStatoUdDto;
import it.eng.parer.datamart.dto.RichiestaDataMartDTO;
import it.eng.parer.datamart.dto.StatoAvanzamentoCancellazioneFisicaDTO;
import it.eng.parer.datamart.dto.StatoAvanzamentoCancellazioneLogicaDTO;
import it.eng.parer.entity.DmUdDel;
import it.eng.parer.entity.DmUdDelRichieste;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.OrgEnte;
import it.eng.parer.entity.OrgStrut;
import it.eng.parer.exception.PreparazioneFisicaException;
import it.eng.parer.slite.gen.form.UnitaDocumentarieForm.FiltriRicercaDataMart;
import it.eng.parer.slite.gen.tablebean.DmUdDelRichiesteRowBean;
import it.eng.parer.slite.gen.tablebean.DmUdDelRichiesteTableBean;
import it.eng.parer.slite.gen.tablebean.DmUdDelRichiesteTableDescriptor;
import it.eng.parer.slite.gen.tablebean.DmUdDelRowBean;
import it.eng.parer.slite.gen.tablebean.DmUdDelTableBean;
import it.eng.parer.slite.gen.tablebean.OrgEnteTableBean;
import it.eng.parer.slite.gen.viewbean.AroVChkStatoCorRichSoftDeleteRowBean;
import it.eng.parer.viewEntity.AroVChkStatoCorRichSoftDelete;
import it.eng.parer.web.helper.DataMartHelper;
import it.eng.parer.web.util.Transform;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import it.eng.spagoLite.db.base.table.BaseTable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import javax.ejb.Asynchronous;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.Query;

/**
 *
 * @author gilioli_p
 */
@Stateless(mappedName = "DataMartEjb")
@LocalBean
public class DataMartEjb {

    private static final Logger logger = LoggerFactory.getLogger(DataMartEjb.class);

    @EJB
    private DataMartHelper dataMartHelper;

    @EJB
    private JobCancellazioneFisicaStarterEjb jobCancellazioneFisicaStarterEjb;

    @EJB(mappedName = "java:app/paginator/LazyListHelper")
    protected LazyListHelper lazyListHelper;

    /**
     *
     * @return il table bean degli enti sacer presenti nel data mart
     */
    public OrgEnteTableBean getEntiDataMart() {
        OrgEnteTableBean enteTableBean = new OrgEnteTableBean();
        List<OrgEnte> enteList = dataMartHelper.getOrgEnteDataMartList();
        if (enteList != null && !enteList.isEmpty()) {
            try {
                enteTableBean = (OrgEnteTableBean) Transform.entities2TableBean(enteList);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                    | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException ex) {
                logger.error("Errore inatteso nel recupero degli enti per la ricerca data mart "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
                throw new IllegalArgumentException(
                        "Errore inatteso nel recupero degli enti per la ricerca data mart");
            }
        }
        return enteTableBean;
    }

    /**
     *
     * @param filtriDataMart i filtri di ricerca
     *
     * @return il table bean con le richieste nel data mart sulla base dei filtri di ricerca
     *
     * @throws it.eng.spagoCore.error.EMFError errore
     */
    public BaseTable getRichiesteDataMartBaseTable(FiltriRicercaDataMart filtriDataMart)
            throws EMFError {
        BaseTable richiestaBaseTable = new BaseTable();
        List<Object[]> richiesteList = dataMartHelper.getRichiesteDataMartList(
                filtriDataMart.getTi_mot_cancellazione().parse(),
                filtriDataMart.getTi_stato_richiesta().parse(), filtriDataMart.getId_ente().parse(),
                filtriDataMart.getId_strut().parse(),
                filtriDataMart.getCd_registro_key_unita_doc().parse(),
                filtriDataMart.getAa_key_unita_doc().parse(),
                filtriDataMart.getCd_key_unita_doc().parse());
        Map<String, Map<String, BigDecimal>> resultMap = new HashMap<>();
        for (Object[] richiesta : richiesteList) {
            String chiave = (BigDecimal) richiesta[0] + ";" + (String) richiesta[1] + ";"
                    + (String) richiesta[2] + ";" + (String) richiesta[3];
            Map<String, BigDecimal> totaliDivisi = resultMap.get(chiave);
            if (totaliDivisi == null) {
                totaliDivisi = new HashMap<String, BigDecimal>();
            }
            totaliDivisi.put((String) richiesta[4], (BigDecimal) richiesta[5]);
            resultMap.put(chiave, totaliDivisi);
        }
        for (Map.Entry<String, Map<String, BigDecimal>> richiestaMap : resultMap.entrySet()) {
            String[] richiesta = richiestaMap.getKey().split(";", -1);
            BaseRow richiestaBaseRow = new BaseRow();
            richiestaBaseRow.setBigDecimal("id_richiesta", new BigDecimal(richiesta[0]));
            richiestaBaseRow.setString("ti_mot_cancellazione", (String) richiesta[1]);
            richiestaBaseRow.setString("ds_mot_cancellazione", (String) richiesta[2]);
            richiestaBaseRow.setString("ti_stato_richiesta", (String) richiesta[3]);
            Map<String, BigDecimal> totaliMap = richiestaMap.getValue();
            richiestaBaseRow.setObject("mappa_totali", totaliMap);
            int niUd = totaliMap.values() // Ottieni una Collection dei valori (Integer)
                    .stream() // Crea uno Stream<Integer>
                    .mapToInt(BigDecimal::intValue) // Converte lo Stream<Integer> in IntStream //
                    // (piÃ¹ efficiente per // somme)
                    .sum(); // Calcola la somma
            richiestaBaseRow.setBigDecimal("ni_ud", BigDecimal.valueOf(niUd));

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonTotali = "";
            try {
                jsonTotali = objectMapper.writeValueAsString(totaliMap);
            } catch (JsonProcessingException ex) {
                java.util.logging.Logger.getLogger(DataMartEjb.class.getName()).log(Level.SEVERE,
                        null, ex);
            }
            richiestaBaseRow.setString("json_totali_ud", jsonTotali);
            richiestaBaseTable.add(richiestaBaseRow);
        }
        return richiestaBaseTable;
    }

    /**
     * Restituisce il table bean con la lista delle richieste datamart
     *
     * @param filtriDataMart i filtri di ricerca
     *
     * @return il table bean con le richieste nel data mart sulla base dei filtri di ricerca
     *
     * @throws it.eng.spagoCore.error.EMFError errore
     */
    public DmUdDelRichiesteTableBean getDmUdDelRichiesteTableBean(
            FiltriRicercaDataMart filtriDataMart) throws EMFError {
        DmUdDelRichiesteTableBean richiesteTableBean = new DmUdDelRichiesteTableBean();
        List<RichiestaDataMartDTO> richiesteList = dataMartHelper.getRichiesteDataMartDtoList(
                filtriDataMart.getTi_mot_cancellazione().parse(),
                filtriDataMart.getTi_stato_richiesta().parse(), filtriDataMart.getId_ente().parse(),
                filtriDataMart.getId_strut().parse(),
                filtriDataMart.getCd_registro_key_unita_doc().parse(),
                filtriDataMart.getAa_key_unita_doc().parse(),
                filtriDataMart.getCd_key_unita_doc().parse(),
                filtriDataMart.getDt_creazione_da().parse(),
                filtriDataMart.getDt_creazione_a().parse());
        for (RichiestaDataMartDTO richiesta : richiesteList) {
            DmUdDelRichiesteRowBean richiestaRowBean = new DmUdDelRichiesteRowBean();
            richiestaRowBean
                    .setIdUdDelRichiesta(BigDecimal.valueOf(richiesta.getIdUdDelRichiesta()));
            richiestaRowBean.setIdRichiesta(richiesta.getIdRichiesta());
            richiestaRowBean.setCdRichiesta(richiesta.getCdRichiesta());
            richiestaRowBean.setTiMotCancellazione(richiesta.getTiMotCancellazione());
            richiestaRowBean.setString("ds_mot_cancellazione", richiesta.getDsMotCancellazione());
            richiestaRowBean.setDtCreazione(new Timestamp(richiesta.getDtCreazione().getTime()));
            richiestaRowBean.setTiStatoRichiesta(richiesta.getTiStatoRichiesta());
            richiestaRowBean.setBigDecimal("ni_ud",
                    BigDecimal.valueOf(richiesta.getTotalUnitaDocumentarie()));
            richiesteTableBean.add(richiestaRowBean);
        }
        SortingRule[] regoleOrdinamento = new SortingRule[] {
                SortingRule.getAscending(DmUdDelRichiesteTableDescriptor.COL_TI_MOT_CANCELLAZIONE),
                SortingRule.getDescending(DmUdDelRichiesteTableDescriptor.COL_DT_CREAZIONE) };
        richiesteTableBean.addSortingRule(regoleOrdinamento);
        richiesteTableBean.sort();
        return richiesteTableBean;
    }

    public DmUdDelRichiesteRowBean getDmUdDelRichiesteRowBean(BigDecimal idUdDelRichiesta) {
        DmUdDelRichiesteRowBean richiestaRowBean = new DmUdDelRichiesteRowBean();
        RichiestaDataMartDTO richiestaDto = dataMartHelper.getRichiestaDataMart(idUdDelRichiesta);
        if (richiestaDto != null) {
            richiestaRowBean
                    .setIdUdDelRichiesta(BigDecimal.valueOf(richiestaDto.getIdUdDelRichiesta()));
            richiestaRowBean.setIdRichiesta(richiestaDto.getIdRichiesta());
            richiestaRowBean.setCdRichiesta(richiestaDto.getCdRichiesta());
            richiestaRowBean.setTiMotCancellazione(richiestaDto.getTiMotCancellazione());
            richiestaRowBean.setString("ds_mot_cancellazione",
                    richiestaDto.getDsMotCancellazione());
            richiestaRowBean.setDtCreazione(new Timestamp(richiestaDto.getDtCreazione().getTime()));
            richiestaRowBean.setTiStatoRichiesta(richiestaDto.getTiStatoRichiesta());
            richiestaRowBean.setBigDecimal("ni_ud",
                    BigDecimal.valueOf(richiestaDto.getTotalUnitaDocumentarie()));
        }
        return richiestaRowBean;
    }

    public DmUdDelRichiesteRowBean getDmUdDelRichiesteForPollingRowBean(
            BigDecimal idUdDelRichiesta) {
        DmUdDelRichiesteRowBean richiestaRowBean = new DmUdDelRichiesteRowBean();
        DmUdDelRichieste udDelRichieste = dataMartHelper.getEntityManager()
                .find(DmUdDelRichieste.class, idUdDelRichiesta.longValue());
        if (udDelRichieste != null) {
            try {
                richiestaRowBean = (DmUdDelRichiesteRowBean) Transform
                        .entity2RowBean(udDelRichieste);
            } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException
                    | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException ex) {
                logger.error("Errore durante il recupero della richiesta datamart "
                        + ExceptionUtils.getRootCauseMessage(ex), ex);
                throw new IllegalStateException(
                        "Errore durante il recupero della richiesta datamart");
            }
        }
        return richiestaRowBean;
    }

    public DmUdDelTableBean getUdDataMartTableBean(FiltriRicercaDataMart filtriDataMart)
            throws EMFError {
        Query query = dataMartHelper.getUdDataMartQuery(
                filtriDataMart.getTi_mot_cancellazione().parse(),
                filtriDataMart.getId_ente().parse(), filtriDataMart.getId_strut().parse(),
                filtriDataMart.getCd_registro_key_unita_doc().parse(),
                filtriDataMart.getAa_key_unita_doc().parse(),
                filtriDataMart.getCd_key_unita_doc().parse(), null, null);
        return lazyListHelper.getTableBean(query, this::geUdDataMartTableBeanFromResultList);
    }

    public DmUdDelTableBean getDmUdDelTableBeanByStato(BigDecimal idUdDelRichiesta,
            String tiStatoUdCancellate) throws EMFError {
        Query query = dataMartHelper.getDmUdDelQuery(null, null, null, null, null, null, null,
                idUdDelRichiesta, tiStatoUdCancellate);
        return lazyListHelper.getTableBean(query, this::geDmUdDelTableBeanFromResultList);
    }

    public DmUdDelTableBean getDmUdDelTableBeanByStato(BigDecimal idUdDelRichiesta,
            String tiStatoUdCancellate, BigDecimal idStrut) throws EMFError {
        Query query = dataMartHelper.getDmUdDelQuery(null, null, null, idStrut, null, null, null,
                idUdDelRichiesta, tiStatoUdCancellate);
        return lazyListHelper.getTableBean(query, this::geDmUdDelTableBeanFromResultList);
    }

    public DmUdDelTableBean getDmUdDelAnnulVersTableBean(BigDecimal idRichiestaAnnulVers,
            String tiStatoUdCancellate) throws EMFError {
        Query query = dataMartHelper.getDmUdDelAnnulVersQuery(idRichiestaAnnulVers,
                tiStatoUdCancellate);
        return lazyListHelper.getTableBean(query,
                this::geUdDataMartTableBeanAnnulVersFromResultList);
    }

    public DmUdDelTableBean getDmUdDelScartoVersTableBean(BigDecimal idRichiestaScartoVers,
            String tiStatoUdCancellate) throws EMFError {
        Query query = dataMartHelper.getDmUdDelScartoVersQuery(idRichiestaScartoVers,
                tiStatoUdCancellate);
        return lazyListHelper.getTableBean(query,
                this::geUdDataMartTableBeanScartoVersFromResultList);
    }

    private DmUdDelTableBean geUdDataMartTableBeanFromResultList(List<DmUdDel> udList) {
        DmUdDelTableBean udTableBean = new DmUdDelTableBean();
        for (DmUdDel ud : udList) {
            DmUdDelRowBean udRowBean = new DmUdDelRowBean();
            udRowBean.setBigDecimal("id_unita_doc", BigDecimal.valueOf(ud.getIdUnitaDoc()));
            udRowBean.setString("nm_strut", ud.getNmEnte() + " - " + ud.getNmStrut());
            udRowBean.setString("cd_registro_key_unita_doc", ud.getCdRegistroKeyUnitaDoc());
            udRowBean.setBigDecimal("aa_key_unita_doc", ud.getAaKeyUnitaDoc());
            udRowBean.setString("cd_key_unita_doc", ud.getCdKeyUnitaDoc());
            udRowBean.setString("ti_stato_ud_cancellate", ud.getTiStatoUdCancellate());
            udTableBean.add(udRowBean);
        }
        return udTableBean;
    }

    private DmUdDelTableBean geUdDataMartTableBeanAnnulVersFromResultList(List<DmUdDel> udList) {
        DmUdDelTableBean udTableBean = new DmUdDelTableBean();
        int progressivo = 1;
        for (DmUdDel ud : udList) {
            DmUdDelRowBean udRowBean = new DmUdDelRowBean();
            udRowBean.setBigDecimal("pg_item_rich_annul_vers_cancellati",
                    BigDecimal.valueOf(progressivo));
            udRowBean.setString("ds_key_item_cancellato", ud.getCdRegistroKeyUnitaDoc() + "-"
                    + ud.getAaKeyUnitaDoc() + "-" + ud.getCdKeyUnitaDoc());
            if (ud.getDtVersamento() != null) {
                udRowBean.setTimestamp("dt_versamento",
                        new Timestamp(ud.getDtVersamento().getTime()));
            }
            progressivo++;
            udTableBean.add(udRowBean);
        }
        return udTableBean;
    }

    private DmUdDelTableBean geUdDataMartTableBeanScartoVersFromResultList(List<DmUdDel> udList) {
        DmUdDelTableBean udTableBean = new DmUdDelTableBean();
        int progressivo = 1;
        for (DmUdDel ud : udList) {
            DmUdDelRowBean udRowBean = new DmUdDelRowBean();
            udRowBean.setBigDecimal("pg_item_rich_scarto_vers_cancellati",
                    BigDecimal.valueOf(progressivo));
            udRowBean.setString("ds_key_item_cancellato", ud.getCdRegistroKeyUnitaDoc() + "-"
                    + ud.getAaKeyUnitaDoc() + "-" + ud.getCdKeyUnitaDoc());
            if (ud.getDtVersamento() != null) {
                udRowBean.setTimestamp("dt_versamento",
                        new Timestamp(ud.getDtVersamento().getTime()));
            }
            progressivo++;
            udTableBean.add(udRowBean);
        }
        return udTableBean;
    }

    private DmUdDelTableBean geDmUdDelTableBeanFromResultList(List<DmUdDel> udList) {
        DmUdDelTableBean udTableBean = new DmUdDelTableBean();
        for (DmUdDel ud : udList) {
            DmUdDelRowBean udRowBean = new DmUdDelRowBean();
            udRowBean.setIdUnitaDoc(BigDecimal.valueOf(ud.getIdUnitaDoc()));
            udRowBean.setNmStrut(ud.getNmEnte() + " - " + ud.getNmStrut());
            udRowBean.setCdRegistroKeyUnitaDoc(ud.getCdRegistroKeyUnitaDoc());
            udRowBean.setAaKeyUnitaDoc(ud.getAaKeyUnitaDoc());
            udRowBean.setCdKeyUnitaDoc(ud.getCdKeyUnitaDoc());
            if (ud.getDtVersamento() != null) {
                udRowBean.setDtVersamento(new Timestamp(ud.getDtVersamento().getTime()));
            }
            udRowBean.setTiStatoUdCancellate(ud.getTiStatoUdCancellate());
            udRowBean.setFlAnnul(ud.getFlAnnul());
            udTableBean.add(udRowBean);
        }
        return udTableBean;
    }

    public int insertUdDataMartAnnulVersCentroStella(BigDecimal idRichiesta, String cdRichiesta,
            String tiMotCancellazione, String tiModDel) {
        return dataMartHelper.populateDataMartUdCentroStellaAnnulVers(idRichiesta, cdRichiesta,
                tiMotCancellazione, tiModDel);
    }

    public int insertUdDataMartRestArchCentroStella(BigDecimal idRichiesta, String cdRichiesta,
            String tiModDel) {
        logger.info("Avvio creazione snapshot (foto) per richiesta RA: {}", idRichiesta);
        // MEV 39896 - inserisco le "foto" dei record che finiranno del datamart per la
        // cancellazione, in maniera tale da non "perdere" i dati
        dataMartHelper.insertAroRichRichRaFoto(idRichiesta);
        dataMartHelper.insertAroLisItemRaFoto(idRichiesta);

        return dataMartHelper.populateDataMartUdCentroStellaRestArch(idRichiesta, cdRichiesta,
                tiModDel);
    }

    public int insertUdDataMartScartoVersCentroStella(long idRichiesta, String cdRichiesta,
            String tiMotCancellazione, String tiModDel) {
        return dataMartHelper.populateDataMartScartoUdCentroStella(idRichiesta, cdRichiesta,
                tiMotCancellazione, tiModDel);
    }

    public void insertUdDataMartAnnulVersSatelliti(long idUdDelRichiesta) {
        dataMartHelper.populateDataMartUdSatelliti(idUdDelRichiesta);
    }

    public AroVChkStatoCorRichSoftDeleteRowBean getAroVChkStatoCorRichSoftDeleteRowBean(
            BigDecimal idRichiesta, String tiMotCancellazione) {
        String tiItemRichSoftDelete = getTiItemRichSoftDelete(tiMotCancellazione);

        AroVChkStatoCorRichSoftDelete check = dataMartHelper.checkRunMicroservizio(idRichiesta,
                tiItemRichSoftDelete);
        AroVChkStatoCorRichSoftDeleteRowBean riga = null;
        try {
            if (check != null) {
                riga = (AroVChkStatoCorRichSoftDeleteRowBean) Transform.entity2RowBean(check);
            }
        } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException
                | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            logger.error(e.getMessage(), e);
        }
        return riga;
    }

    public String getTiItemRichSoftDelete(String tiMotCancellazione) {
        CostantiDB.TiMotCancellazione tiMotCancellazioneEnum = CostantiDB.TiMotCancellazione
                .valueOf(tiMotCancellazione);
        String tiItemRichSoftDelete;
        switch (tiMotCancellazioneEnum) {
        case A:
            tiItemRichSoftDelete = CostantiDB.TiItemRichSoftDelete.ANNUL_VERS.name();
            break;
        case R:
            tiItemRichSoftDelete = CostantiDB.TiItemRichSoftDelete.REST_ARCH.name();
            break;
        default:
            tiItemRichSoftDelete = CostantiDB.TiItemRichSoftDelete.SCARTO_ARCH.name();
            break;
        }
        return tiItemRichSoftDelete;
    }

    public String getDsMotCancellazione(String tiMotCancellazione) {
        CostantiDB.TiMotCancellazione tiMotCancellazioneEnum = CostantiDB.TiMotCancellazione
                .valueOf(tiMotCancellazione);
        String dsMotCancellazione;
        switch (tiMotCancellazioneEnum) {
        case A:
            dsMotCancellazione = "annullamento unità doc";
            break;
        case R:
            dsMotCancellazione = "restituzione archivio";
            break;
        default:
            dsMotCancellazione = "scarto";
            break;
        }
        return dsMotCancellazione;
    }

    public boolean allUdDaCancellareDmUdDel(BigDecimal idRichiesta) {
        return dataMartHelper.allUdDaCancellare(idRichiesta.longValue());
    }

    public String[] getAmbienteEnteStruttura(BigDecimal idStrut) {
        String[] ambEnteStrut = new String[3];
        OrgStrut struttura = dataMartHelper.findById(OrgStrut.class, idStrut);
        ambEnteStrut[0] = struttura.getOrgEnte().getOrgAmbiente().getNmAmbiente();
        ambEnteStrut[1] = struttura.getOrgEnte().getNmEnte();
        ambEnteStrut[2] = struttura.getNmStrut();
        return ambEnteStrut;
    }

    public BaseTable getNumUdDataMartBaseTableGroupedByStato(BigDecimal idRichiesta) {
        List<ConteggioStatoUdDto> risultati = dataMartHelper
                .getUdCountsByStatoForRichiestaDtoJPA(idRichiesta);

        BaseTable numUdDataMart = new BaseTable();
        for (ConteggioStatoUdDto dto : risultati) {
            BaseRow riga = new BaseRow();
            riga.setBigDecimal("id_richiesta", dto.getIdRichiesta());
            riga.setString("ti_mot_cancellazione", dto.getTiMotCancellazione());
            riga.setBigDecimal("id_ente", dto.getIdEnte());
            riga.setString("nm_ente", dto.getNmEnte());
            riga.setBigDecimal("id_strut", dto.getIdStrut());
            riga.setString("nm_ente_strut", dto.getNmEnte() + " - " + dto.getNmStrut());
            riga.setString("ti_stato_ud_cancellate", dto.getTiStatoUdCancellate());
            riga.setBigDecimal("ni_ud_stato", new BigDecimal(dto.getConteggio()));
            riga.setBigDecimal("ni_ud_stato_annullate",
                    new BigDecimal(dto.getNiUdStatoAnnullate()));
            numUdDataMart.add(riga);
        }
        return numUdDataMart;
    }

    public DmUdDelTableBean getDmUdDelGroupedByStato(BigDecimal idUdDelRichiesta) {
        List<ConteggioStatoUdDto> risultati = dataMartHelper
                .getUdCountsByStatoForRichiestaDtoJPA(idUdDelRichiesta);

        DmUdDelTableBean numUdDataMart = new DmUdDelTableBean();
        for (ConteggioStatoUdDto dto : risultati) {
            DmUdDelRowBean riga = new DmUdDelRowBean();
            riga.setIdUdDelRichiesta(BigDecimal.valueOf(dto.getIdUdDelRichiesta()));
            riga.setBigDecimal("id_richiesta", dto.getIdRichiesta());
            riga.setString("ti_mot_cancellazione", dto.getTiMotCancellazione());
            riga.setIdEnte(dto.getIdEnte());
            riga.setNmEnte(dto.getNmEnte());
            riga.setIdStrut(dto.getIdStrut());
            riga.setString("nm_ente_strut", dto.getNmEnte() + " - " + dto.getNmStrut());
            riga.setTiStatoUdCancellate(dto.getTiStatoUdCancellate());
            riga.setBigDecimal("ni_ud_stato", new BigDecimal(dto.getConteggio()));
            riga.setBigDecimal("ni_ud_stato_annullate",
                    new BigDecimal(dto.getNiUdStatoAnnullate()));
            numUdDataMart.add(riga);
        }
        return numUdDataMart;
    }

    public void deleteAroRichSoftDelete(BigDecimal idRichiesta, String tiRichSoftDelete) {
        dataMartHelper.deleteAroRichSoftDelete(idRichiesta, tiRichSoftDelete);
    }

    // =================================================================================
    // METODI PER LA CANCELLAZIONE FISICA
    // =================================================================================

    // --- METODI PER STATO ---
    public String getStatoRichiesta(BigDecimal idUdDelRichiesta) {
        return dataMartHelper.getStatoRichiesta(idUdDelRichiesta);
    }

    public void impostaStatoRichiesta(BigDecimal idRichiesta, String nuovoStato) {
        dataMartHelper.impostaStatoRichiesta(idRichiesta, nuovoStato);
    }

    public String getStatoInternoRichiesta(BigDecimal idUdDelRichiesta) {
        return dataMartHelper.getStatoInternoRichiesta(idUdDelRichiesta);
    }

    public void impostaStatoInternoRichiesta(BigDecimal idUdDelRichiesta, String nuovoStato) {
        dataMartHelper.impostaStatoInternoRichiesta(idUdDelRichiesta, nuovoStato);
    }

    public void impostaStatoInternoRichiesta(BigDecimal idUdDelRichiesta, String nuovoStato,
            String errore) {
        dataMartHelper.impostaStatoInternoRichiesta(idUdDelRichiesta, nuovoStato, errore);
    }

    /**
     * ESEGUE LA PREPARAZIONE COMPLETA E POI AVVIA IL JOB. Contiene un controllo di sicurezza per
     * non essere eseguito se non necessario. In caso di fallimento, lancia
     * PreparazioneFisicaException.
     *
     * @param idUdDelRichiesta richiesta per la quale effettuare la cancellazione fisica
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void preparaEAvviaCancellazioneFisica(BigDecimal idUdDelRichiesta) {
        try {
            // Blocco di sicurezza
            String statoCorrente = dataMartHelper.getStatoInternoRichiesta(idUdDelRichiesta);
            List<String> statiValidi = List.of(
                    CostantiDB.TiStatoInternoRich.PRONTA_PER_FISICA.name(),
                    CostantiDB.TiStatoInternoRich.ERRORE_PREPARAZIONE.name());
            if (!statiValidi.contains(statoCorrente)) {
                logger.warn(
                        "Tentativo di eseguire la preparazione per la richiesta {}, ma il suo stato è '{}'. Operazione saltata.",
                        idUdDelRichiesta, statoCorrente);
                return;
            }

            // Se lo stato è valido, procedi
            dataMartHelper.impostaStatoInternoRichiesta(idUdDelRichiesta,
                    CostantiDB.TiStatoInternoRich.IN_PREPARAZIONE_FISICA.name());
            dataMartHelper.populateDataMartUdSatelliti(idUdDelRichiesta.longValue());
            // dataMartHelper.aggiornaStatoUdaCancellabili(idUdDelRichiesta); --> ci pensa Fabio in
            // precedenza a mettere lo stato CANCELLABILE al termine della Cancellazione logica
            dataMartHelper.aggiornaDtStatoUdCancellabili(idUdDelRichiesta);

            // Chiama il metodo di avvio separato per completare l'operazione
            this.riavviaJobCancellazioneFisica(idUdDelRichiesta);

        } catch (Exception e) {
            // Se la preparazione fallisce, lancia l'eccezione specifica per l'Action
            // Lo stato di errore viene già impostato dall'Action, non qui.
            throw new PreparazioneFisicaException("Fallimento durante la preparazione dei dati.",
                    e);
        }
    }

    /**
     * ESEGUE SOLO IL RIAVVIO DEL JOB ORACLE. Da chiamare quando la preparazione è già stata fatta e
     * il job è fallito. In caso di fallimento, lancia PreparazioneFisicaException.
     *
     * @param idUdDelRichiesta richiesta per la quale riavviare il JOB di cancellazione fisica
     */
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void riavviaJobCancellazioneFisica(BigDecimal idUdDelRichiesta) {
        try {
            String tiModDel = dataMartHelper.getTiModDelRichiesta(idUdDelRichiesta);
            dataMartHelper.impostaStatoInternoRichiesta(idUdDelRichiesta,
                    CostantiDB.TiStatoInternoRich.IN_CODA_CANCELLAZIONE.name());
            jobCancellazioneFisicaStarterEjb.avviaJobCancellazioneFisica(idUdDelRichiesta,
                    tiModDel);
        } catch (Exception e) {
            // Se l'avvio del job fallisce, lancia l'eccezione specifica per l'Action
            throw new PreparazioneFisicaException("Fallimento durante il riavvio del job.", e);
        }
    }

    /**
     * NUOVO METODO ASINCRONO per avviare il job. Essendo asincrono, viene eseguito in un thread e
     * una transazione completamente separati, eliminando qualsiasi potenziale conflitto con la
     * transazione dell'utente o lock residui.
     *
     * @param idUdDelRichiesta richiesta per la quale avviare il JOB di cancellazione fisica
     */
    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW) // Ogni avvio è una sua transazione
    public void avviaJobOracleAsincrono(BigDecimal idUdDelRichiesta) {
        logger.info("Esecuzione asincrona dell'avvio del job Oracle per la richiesta {}",
                idUdDelRichiesta);
        try {
            // Una piccola pausa per essere sicuri che la transazione principale sia terminata.
            Thread.sleep(2000); // 2 secondi di attesa
            String tiModDel = dataMartHelper.getTiModDelRichiesta(idUdDelRichiesta);
            // La chiamata al job starter che usa JDBC puro.
            jobCancellazioneFisicaStarterEjb.avviaJobCancellazioneFisica(idUdDelRichiesta,
                    tiModDel);
        } catch (Exception e) {
            logger.error(
                    "Fallimento critico nell'avvio asincrono del job per la richiesta {}. Imposto stato di errore.",
                    idUdDelRichiesta, e);
            // Se l'avvio stesso fallisce, lo registriamo.
            dataMartHelper.impostaStatoInternoRichiesta(idUdDelRichiesta,
                    CostantiDB.TiStatoInternoRich.ERRORE_AVVIO_JOB.name(), e.getMessage());
        }
    }

    /**
     * Metodo "tuttofare" per la fase logica, chiamato sia dal polling che dall'Action. 1.
     * SINCRONIZZA lo stato interno con quello del processo esterno (Kafka). 2. CALCOLA
     * l'avanzamento. 3. RESTITUISCE un DTO completo per la UI.
     *
     * @param idUdDelRichiesta     richiesta per la quale monitorare la cancellazione logica
     * @param idRichiesta          id richiesta sacer
     * @param tiItemRichSoftDelete tipo item in cancellazione
     *
     * @return il DTO con i dati sui conteggi della cancellazione logica
     */
    public StatoAvanzamentoCancellazioneLogicaDTO sincronizzaEcalcolaStatoLogico(
            BigDecimal idUdDelRichiesta, BigDecimal idRichiesta, String tiItemRichSoftDelete) {

        // --- Sincronizzazione dello stato master ---
        String statoAttualeApp = dataMartHelper.getStatoInternoRichiesta(idUdDelRichiesta);
        List<String> statiLogiciDaSincronizzare = List.of(
                CostantiDB.TiStatoInternoRich.INVIATA_A_MS.name(),
                CostantiDB.TiStatoInternoRich.IN_ELABORAZIONE_LOGICA.name());

        if (statiLogiciDaSincronizzare.contains(statoAttualeApp)) {
            AroVChkStatoCorRichSoftDelete statoMS = dataMartHelper
                    .checkRunMicroservizio(idRichiesta, tiItemRichSoftDelete);
            String nuovoStatoDaImpostare = null;

            if (statoMS != null) {
                if ("1".equals(statoMS.getFlRichErrore())
                        && !CostantiDB.TiStatoInternoRich.ERRORE_LOGICO.name()
                                .equals(statoAttualeApp)) {
                    nuovoStatoDaImpostare = CostantiDB.TiStatoInternoRich.ERRORE_LOGICO.name();
                    // NUOVO CASO: Errore ripristinabile
                } else if ("1".equals(statoMS.getFlRichEvasaKoRecup())
                        && !CostantiDB.TiStatoInternoRich.ERRORE_LOGICO_RIPRISTINABILE.name()
                                .equals(statoAttualeApp)) {
                    nuovoStatoDaImpostare = CostantiDB.TiStatoInternoRich.ERRORE_LOGICO_RIPRISTINABILE
                            .name();
                    // NUOVO CASO: Errore gestito (es. flag archivio restituito sulla
                    // struttura a false)
                } else if ("1".equals(statoMS.getFlRichEvasaKoGest())
                        && !CostantiDB.TiStatoInternoRich.ERRORE_LOGICO_GESTITO.name()
                                .equals(statoAttualeApp)) {
                    nuovoStatoDaImpostare = CostantiDB.TiStatoInternoRich.ERRORE_LOGICO_GESTITO
                            .name();
                } else if ("1".equals(statoMS.getFlRichEvasaOk())
                        && dataMartHelper.isLavoroKafkaCompletato(idUdDelRichiesta)
                        && !CostantiDB.TiStatoInternoRich.PRONTA_PER_FISICA.name()
                                .equals(statoAttualeApp)) {
                    nuovoStatoDaImpostare = CostantiDB.TiStatoInternoRich.PRONTA_PER_FISICA.name();
                } else if (("1".equals(statoMS.getFlRichInElaborazione())
                        || !dataMartHelper.isLavoroKafkaCompletato(idUdDelRichiesta))
                        && !CostantiDB.TiStatoInternoRich.IN_ELABORAZIONE_LOGICA.name()
                                .equals(statoAttualeApp)) {
                    nuovoStatoDaImpostare = CostantiDB.TiStatoInternoRich.IN_ELABORAZIONE_LOGICA
                            .name();
                }
            }

            if (nuovoStatoDaImpostare != null) {
                dataMartHelper.impostaStatoInternoRichiesta(idUdDelRichiesta,
                        nuovoStatoDaImpostare);
                statoAttualeApp = nuovoStatoDaImpostare;
            }
        }

        // --- Calcolo avanzamento e popolazione DTO ---
        StatoAvanzamentoCancellazioneLogicaDTO dto = new StatoAvanzamentoCancellazioneLogicaDTO();
        dto.setStatoRichiesta(dataMartHelper.getStatoRichiesta(idUdDelRichiesta));
        dto.setStatoInternoRichiesta(statoAttualeApp);

        // Usa la TABELLA per i conteggi della tabella, che è la fonte più affidabile
        // Usa l'UNICO metodo corretto per ottenere i conteggi
        List<ConteggioStatoUdDto> conteggiList = dataMartHelper
                .getUdCountsByStatoForRichiestaDtoJPA(idUdDelRichiesta);
        dto.setConteggiDettagliati(conteggiList);

        // Calcola i totali e gli elaborati dalla lista
        long totali = 0;
        long elaborati = 0;
        for (ConteggioStatoUdDto conteggioDto : conteggiList) {
            totali += conteggioDto.getConteggio();
            if (!"DA_CANCELLARE".equalsIgnoreCase(conteggioDto.getTiStatoUdCancellate())) {
                elaborati += conteggioDto.getConteggio();
            }
        }
        dto.setTotali(totali);
        dto.setElaborati(elaborati);

        // GESTIONE DEL CASO FINALE
        if (CostantiDB.TiStatoInternoRich.PRONTA_PER_FISICA.name().equals(statoAttualeApp)) {
            dto.setElaborati(totali);

            // Raggruppa tutti i conteggi per Ente/Struttura sotto lo stato "CANCELLABILE"
            // per garantire che la UI finale sia pulita e coerente.
            Map<String, ConteggioStatoUdDto> raggruppati = new LinkedHashMap<>();
            for (ConteggioStatoUdDto dtoRiga : conteggiList) {
                String chiave = dtoRiga.getIdEnte() + "-" + dtoRiga.getIdStrut();

                ConteggioStatoUdDto rigaAggregata = raggruppati.get(chiave);
                if (rigaAggregata == null) {
                    rigaAggregata = new ConteggioStatoUdDto(dtoRiga.getIdUdDelRichiesta(),
                            dtoRiga.getIdRichiesta(), dtoRiga.getTiMotCancellazione(),
                            dtoRiga.getIdEnte(), dtoRiga.getNmEnte(), dtoRiga.getIdStrut(),
                            dtoRiga.getNmStrut(), "CANCELLABILE", // Forza lo stato finale
                            0L, 0L);
                    raggruppati.put(chiave, rigaAggregata);
                }
                rigaAggregata.setConteggio(rigaAggregata.getConteggio() + dtoRiga.getConteggio());
                rigaAggregata.setNiUdStatoAnnullate(
                        rigaAggregata.getNiUdStatoAnnullate() + dtoRiga.getNiUdStatoAnnullate());
            }
            dto.setConteggiDettagliati(new ArrayList<>(raggruppati.values()));
        }

        return dto;
    }

    /**
     * Metodo chiamato dal Controller per il polling della CANCELLAZIONE FISICA. 1. Legge gli stati
     * (utente e tecnico). 2. Calcola l'avanzamento. 3. Restituisce un DTO completo per la UI.
     *
     * @param idUdDelRichiesta id richiesta per la quale monitorare la cancellazione fisica
     *
     * @return il DTO con i dati sui conteggi della cancellazione fisica
     */
    public StatoAvanzamentoCancellazioneFisicaDTO calcolaStatoAvanzamentoCancellazioneFisica(
            BigDecimal idUdDelRichiesta) {
        StatoAvanzamentoCancellazioneFisicaDTO dto = new StatoAvanzamentoCancellazioneFisicaDTO();

        // PASSO 1: Legge gli stati
        String statoRichiesta = dataMartHelper.getStatoRichiesta(idUdDelRichiesta);
        String statoInterno = dataMartHelper.getStatoInternoRichiesta(idUdDelRichiesta);

        dto.setStatoRichiesta(statoRichiesta);
        dto.setStatoInternoRichiesta(statoInterno);

        List<ConteggioStatoUdDto> conteggiList = dataMartHelper
                .getUdCountsByStatoForRichiestaDtoJPA(idUdDelRichiesta);
        dto.setConteggiDettagliati(conteggiList);

        long totali = 0;
        long cancellate = 0;
        for (ConteggioStatoUdDto conteggioDto : conteggiList) {
            totali += conteggioDto.getConteggio();
            if ("CANCELLATA_DB_SACER".equalsIgnoreCase(conteggioDto.getTiStatoUdCancellate())) {
                cancellate += conteggioDto.getConteggio();
            }
        }
        dto.setTotali(totali);
        dto.setCancellate(cancellate);

        // GESTIONE DEL CASO FINALE
        if (CostantiDB.TiStatoRichiesta.EVASA.name().equals(statoRichiesta)) {
            dto.setCancellate(totali);

            // Raggruppa tutti i conteggi per Ente/Struttura sotto lo stato "CANCELLATA_DB_SACER"
            Map<String, ConteggioStatoUdDto> raggruppati = new LinkedHashMap<>();
            for (ConteggioStatoUdDto dtoRiga : conteggiList) {
                String chiave = dtoRiga.getIdEnte() + "-" + dtoRiga.getIdStrut();

                ConteggioStatoUdDto rigaAggregata = raggruppati.get(chiave);
                if (rigaAggregata == null) {
                    rigaAggregata = new ConteggioStatoUdDto(dtoRiga.getIdUdDelRichiesta(),
                            dtoRiga.getIdRichiesta(), dtoRiga.getTiMotCancellazione(),
                            dtoRiga.getIdEnte(), dtoRiga.getNmEnte(), dtoRiga.getIdStrut(),
                            dtoRiga.getNmStrut(), "CANCELLATA_DB_SACER", // Forza lo stato finale
                            0L, 0L);
                    raggruppati.put(chiave, rigaAggregata);
                }
                rigaAggregata.setConteggio(rigaAggregata.getConteggio() + dtoRiga.getConteggio());
                rigaAggregata.setNiUdStatoAnnullate(
                        rigaAggregata.getNiUdStatoAnnullate() + dtoRiga.getNiUdStatoAnnullate());
            }
            dto.setConteggiDettagliati(new ArrayList<>(raggruppati.values()));
        }

        return dto;
    }

    /**
     * Metodo "wrapper" che invoca la logica di correzione nell'Helper.
     *
     * @param idRichiesta          L'ID della richiesta del datamart.
     * @param tiItemRichSoftDelete motivo cancellazione
     * @param idUserIam            utente che innesca il meccanismo di correzione logica
     */
    public void eseguiCorrezionePerRipresaLogica(BigDecimal idRichiesta,
            String tiItemRichSoftDelete, long idUserIam) {
        dataMartHelper.eseguiCorrezionePerRipresaLogica(idRichiesta, tiItemRichSoftDelete,
                idUserIam);
    }

    public void updateDmUdDelDaCancellare(BigDecimal idUdDelRichiesta) {
        dataMartHelper.updateDmUdDelDaCancellare(idUdDelRichiesta);
    }

    public void reinizializzaRichiesta(BigDecimal idRichiestaSacer, String tiMotCancellazione,
            BigDecimal idUdDelRichiestaSacer) {
        // Cancella richieste o in errore logico o errore logico gestito
        deleteAroRichSoftDelete(idRichiestaSacer, getTiItemRichSoftDelete(tiMotCancellazione));
        // Riporta le ud in DA_CANCELLARE
        updateDmUdDelDaCancellare(idUdDelRichiestaSacer);
    }
}
