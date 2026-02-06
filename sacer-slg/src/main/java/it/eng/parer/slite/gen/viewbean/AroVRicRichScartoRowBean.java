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

package it.eng.parer.slite.gen.viewbean;

import java.math.BigDecimal;
import java.sql.Timestamp;

import it.eng.parer.viewEntity.AroVRicRichScarto;
import it.eng.parer.viewEntity.AroVRicRichScartoId;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Aro_V_Ric_Rich_Scarto
 *
 */
public class AroVRicRichScartoRowBean extends BaseRow implements JEEBaseRowInterface {

    private static final long serialVersionUID = 1L;

    public static AroVRicRichScartoTableDescriptor TABLE_DESCRIPTOR = new AroVRicRichScartoTableDescriptor();

    public AroVRicRichScartoRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    public BigDecimal getIdUserIam() {
        return getBigDecimal("id_user_iam");
    }

    public void setIdUserIam(BigDecimal idUserIam) {
        setObject("id_user_iam", idUserIam);
    }

    public BigDecimal getIdAmbiente() {
        return getBigDecimal("id_ambiente");
    }

    public void setIdAmbiente(BigDecimal idAmbiente) {
        setObject("id_ambiente", idAmbiente);
    }

    public String getNmAmbiente() {
        return getString("nm_ambiente");
    }

    public void setNmAmbiente(String nmAmbiente) {
        setObject("nm_ambiente", nmAmbiente);
    }

    public BigDecimal getIdEnte() {
        return getBigDecimal("id_ente");
    }

    public void setIdEnte(BigDecimal idEnte) {
        setObject("id_ente", idEnte);
    }

    public String getNmEnte() {
        return getString("nm_ente");
    }

    public void setNmEnte(String nmEnte) {
        setObject("nm_ente", nmEnte);
    }

    public BigDecimal getIdStrut() {
        return getBigDecimal("id_strut");
    }

    public void setIdStrut(BigDecimal idStrut) {
        setObject("id_strut", idStrut);
    }

    public String getNmStrut() {
        return getString("nm_strut");
    }

    public void setNmStrut(String nmStrut) {
        setObject("nm_strut", nmStrut);
    }

    public BigDecimal getIdRichScartoVers() {
        return getBigDecimal("id_rich_scarto_vers");
    }

    public void setIdRichScartoVers(BigDecimal idRichScartoVers) {
        setObject("id_rich_scarto_vers", idRichScartoVers);
    }

    public String getCdRichScartoVers() {
        return getString("cd_rich_scarto_vers");
    }

    public void setCdRichScartoVers(String cdRichScartoVers) {
        setObject("cd_rich_scarto_vers", cdRichScartoVers);
    }

    public String getDsRichScartoVers() {
        return getString("ds_rich_scarto_vers");
    }

    public void setDsRichScartoVers(String dsRichScartoVers) {
        setObject("ds_rich_scarto_vers", dsRichScartoVers);
    }

    public String getNtRichScartoVers() {
        return getString("nt_rich_scarto_vers");
    }

    public void setNtRichScartoVers(String ntRichScartoVers) {
        setObject("nt_rich_scarto_vers", ntRichScartoVers);
    }

    public Timestamp getDtCreazioneRichScartoVers() {
        return getTimestamp("dt_creazione_rich_scarto_vers");
    }

    public void setDtCreazioneRichScartoVers(Timestamp dtCreazioneRichScartoVers) {
        setObject("dt_creazione_rich_scarto_vers", dtCreazioneRichScartoVers);
    }

    public String getTiCreazioneRichScartoVers() {
        return getString("ti_creazione_rich_scarto_vers");
    }

    public void setTiCreazioneRichScartoVers(String tiCreazioneRichScartoVers) {
        setObject("ti_creazione_rich_scarto_vers", tiCreazioneRichScartoVers);
    }

    public String getTiStatoRichScartoVersCor() {
        return getString("ti_stato_rich_scarto_vers_cor");
    }

    public void setTiStatoRichScartoVersCor(String tiStatoRichScartoVersCor) {
        setObject("ti_stato_rich_scarto_vers_cor", tiStatoRichScartoVersCor);
    }

    public BigDecimal getIdItemRichScartoVers() {
        return getBigDecimal("id_item_rich_scarto_vers");
    }

    public void setIdItemRichScartoVers(BigDecimal idItemRichScartoVers) {
        setObject("id_item_rich_scarto_vers", idItemRichScartoVers);
    }

    public String getCdRegistroKeyUnitaDoc() {
        return getString("cd_registro_key_unita_doc");
    }

    public void setCdRegistroKeyUnitaDoc(String cdRegistroKeyUnitaDoc) {
        setObject("cd_registro_key_unita_doc", cdRegistroKeyUnitaDoc);
    }

    public BigDecimal getAaKeyUnitaDoc() {
        return getBigDecimal("aa_key_unita_doc");
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        setObject("aa_key_unita_doc", aaKeyUnitaDoc);
    }

    public String getCdKeyUnitaDoc() {
        return getString("cd_key_unita_doc");
    }

    public void setCdKeyUnitaDoc(String cdKeyUnitaDoc) {
        setObject("cd_key_unita_doc", cdKeyUnitaDoc);
    }

    public String getFlNonScarto() {
        return getString("fl_non_scarto");
    }

    public void setFlNonScarto(String flNonScarto) {
        setObject("fl_non_scarto", flNonScarto);
    }

    public BigDecimal getNiItem() {
        return getBigDecimal("ni_item");
    }

    public void setNiItem(BigDecimal niItem) {
        setObject("ni_item", niItem);
    }

    public BigDecimal getNiItemNonScartati() {
        return getBigDecimal("ni_item_non_scartati");
    }

    public void setNiItemNonScartati(BigDecimal niItemNonScartati) {
        setObject("ni_item_non_scartati", niItemNonScartati);
    }

    public String getFlNonScartabile() {
        return getString("fl_non_scartabile");
    }

    public void setFlNonScartabile(String flNonScartabile) {
        setObject("fl_non_scartabile", flNonScartabile);
    }

    @Override
    public void entityToRowBean(Object obj) {
        AroVRicRichScarto entity = (AroVRicRichScarto) obj;
        if (entity.getAroVRicRichScartoId() != null) {
            this.setIdUserIam(entity.getAroVRicRichScartoId().getIdUserIam());
            this.setIdRichScartoVers(entity.getAroVRicRichScartoId().getIdRichScartoVers());
        }
        this.setIdAmbiente(entity.getIdAmbiente());
        this.setNmAmbiente(entity.getNmAmbiente());
        this.setIdEnte(entity.getIdEnte());
        this.setNmEnte(entity.getNmEnte());
        this.setIdStrut(entity.getIdStrut());
        this.setNmStrut(entity.getNmStrut());
        this.setCdRichScartoVers(entity.getCdRichScartoVers());
        this.setDsRichScartoVers(entity.getDsRichScartoVers());
        this.setNtRichScartoVers(entity.getNtRichScartoVers());
        if (entity.getDtCreazioneRichScartoVers() != null) {
            this.setDtCreazioneRichScartoVers(
                    new Timestamp(entity.getDtCreazioneRichScartoVers().getTime()));
        }
        this.setTiCreazioneRichScartoVers(entity.getTiCreazioneRichScartoVers());
        this.setTiStatoRichScartoVersCor(entity.getTiStatoRichScartoVersCor());
        this.setIdItemRichScartoVers(entity.getIdItemRichScartoVers());
        this.setCdRegistroKeyUnitaDoc(entity.getCdRegistroKeyUnitaDoc());
        this.setAaKeyUnitaDoc(entity.getAaKeyUnitaDoc());
        this.setCdKeyUnitaDoc(entity.getCdKeyUnitaDoc());
        this.setNiItem(entity.getNiItem());
        this.setNiItemNonScartati(entity.getNiItemNonScartati());
        this.setFlNonScartabile(entity.getFlNonScartabile());
    }

    @Override
    public AroVRicRichScarto rowBeanToEntity() {
        AroVRicRichScarto entity = new AroVRicRichScarto();
        entity.setAroVRicRichScartoId(new AroVRicRichScartoId());
        entity.getAroVRicRichScartoId().setIdUserIam(this.getIdUserIam());
        entity.getAroVRicRichScartoId().setIdRichScartoVers(this.getIdRichScartoVers());
        entity.setIdAmbiente(this.getIdAmbiente());
        entity.setNmAmbiente(this.getNmAmbiente());
        entity.setIdEnte(this.getIdEnte());
        entity.setNmEnte(this.getNmEnte());
        entity.setIdStrut(this.getIdStrut());
        entity.setNmStrut(this.getNmStrut());
        entity.setCdRichScartoVers(this.getCdRichScartoVers());
        entity.setDsRichScartoVers(this.getDsRichScartoVers());
        entity.setNtRichScartoVers(this.getNtRichScartoVers());
        entity.setDtCreazioneRichScartoVers(this.getDtCreazioneRichScartoVers());
        entity.setTiCreazioneRichScartoVers(this.getTiCreazioneRichScartoVers());
        entity.setTiStatoRichScartoVersCor(this.getTiStatoRichScartoVersCor());
        entity.setIdItemRichScartoVers(this.getIdItemRichScartoVers());
        entity.setCdRegistroKeyUnitaDoc(this.getCdRegistroKeyUnitaDoc());
        entity.setAaKeyUnitaDoc(this.getAaKeyUnitaDoc());
        entity.setCdKeyUnitaDoc(this.getCdKeyUnitaDoc());
        entity.setNiItem(this.getNiItem());
        entity.setNiItemNonScartati(this.getNiItemNonScartati());
        entity.setFlNonScartabile(this.getFlNonScartabile());
        return entity;
    }

    // gestione della paginazione
    public void setRownum(Integer rownum) {
        setObject("rownum", rownum);
    }

    public Integer getRownum() {
        return Integer.parseInt(getObject("rownum").toString());
    }

    public void setRnum(Integer rnum) {
        setObject("rnum", rnum);
    }

    public Integer getRnum() {
        return Integer.parseInt(getObject("rnum").toString());
    }

    public void setNumrecords(Integer numRecords) {
        setObject("numrecords", numRecords);
    }

    public Integer getNumrecords() {
        return Integer.parseInt(getObject("numrecords").toString());
    }

}
