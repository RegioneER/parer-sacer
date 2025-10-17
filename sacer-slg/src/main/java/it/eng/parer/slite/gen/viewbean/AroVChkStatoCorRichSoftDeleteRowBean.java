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

import it.eng.parer.viewEntity.AroVChkStatoCorRichSoftDelete;
import java.math.BigDecimal;

import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la vista AroVChkStatoCorRichSoftDelete
 *
 */
public class AroVChkStatoCorRichSoftDeleteRowBean extends BaseRow implements JEEBaseRowInterface {

    private static final long serialVersionUID = 1L;

    public static AroVChkStatoCorRichSoftDeleteTableDescriptor TABLE_DESCRIPTOR = new AroVChkStatoCorRichSoftDeleteTableDescriptor();

    public AroVChkStatoCorRichSoftDeleteRowBean() {
	super();
    }

    public TableDescriptor getTableDescriptor() {
	return TABLE_DESCRIPTOR;
    }

    public BigDecimal getIdRichiestaSacer() {
	return getBigDecimal("id_richiesta_sacer");
    }

    public void setIdRichiestaSacer(BigDecimal idRichiestaSacer) {
	setObject("id_richiesta_sacer", idRichiestaSacer);
    }

    public String getTiItemRichSoftDelete() {
	return getString("ti_item_rich_soft_delete");
    }

    public void setTiItemRichSoftDelete(String tiItemRichSoftDelete) {
	setObject("ti_item_rich_soft_delete", tiItemRichSoftDelete);
    }

    public String getFlRichAcquisizioneKo() {
	return getString("fl_rich_acquisizione_ko");
    }

    public void setFlRichAcquisizioneKo(String flRichAcquisizioneKo) {
	setObject("fl_rich_acquisizione_ko", flRichAcquisizioneKo);
    }

    public String getFlRichErrore() {
	return getString("fl_rich_errore");
    }

    public void setFlRichErrore(String flRichErrore) {
	setObject("fl_rich_errore", flRichErrore);
    }

    public String getFlRichEvasaOk() {
	return getString("fl_rich_evasa_ok");
    }

    public void setFlRichEvasaOk(String flRichEvasaOk) {
	setObject("fl_rich_evasa_ok", flRichEvasaOk);
    }

    public String getFlRichInElaborazione() {
	return getString("fl_rich_in_elaborazione");
    }

    public void setFlRichInElaborazione(String flRichInElaborazione) {
	setObject("fl_rich_in_elaborazione", flRichInElaborazione);
    }

    @Override
    public void entityToRowBean(Object obj) {
	AroVChkStatoCorRichSoftDelete entity = (AroVChkStatoCorRichSoftDelete) obj;
	this.setIdRichiestaSacer(entity.getIdRichiestaSacer());
	this.setFlRichAcquisizioneKo(entity.getFlRichAcquisizioneKo());
	this.setFlRichErrore(entity.getFlRichErrore());
	this.setFlRichEvasaOk(entity.getFlRichEvasaOk());
	this.setFlRichInElaborazione(entity.getFlRichInElaborazione());
	this.setTiItemRichSoftDelete(entity.getTiItemRichSoftDelete());
    }

    @Override
    public AroVChkStatoCorRichSoftDelete rowBeanToEntity() {
	AroVChkStatoCorRichSoftDelete entity = new AroVChkStatoCorRichSoftDelete();
	entity.setIdRichiestaSacer(this.getIdRichiestaSacer());
	entity.setFlRichAcquisizioneKo(this.getFlRichAcquisizioneKo());
	entity.setFlRichErrore(this.getFlRichErrore());
	entity.setFlRichEvasaOk(this.getFlRichEvasaOk());
	entity.setFlRichInElaborazione(this.getFlRichInElaborazione());
	entity.setTiItemRichSoftDelete(this.getTiItemRichSoftDelete());
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
