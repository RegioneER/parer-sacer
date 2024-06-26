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

package it.eng.parer.slite.gen.tablebean;

import java.math.BigDecimal;
import java.sql.Timestamp;

import it.eng.parer.entity.DecTipoStrutUnitaDoc;
import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Dec_Tipo_Strut_Unita_Doc
 *
 */
public class DecTipoStrutUnitaDocRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Wednesday, 21 November 2018 12:20" )
     */
    private static final long serialVersionUID = 1L;

    public static DecTipoStrutUnitaDocTableDescriptor TABLE_DESCRIPTOR = new DecTipoStrutUnitaDocTableDescriptor();

    public DecTipoStrutUnitaDocRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdTipoStrutUnitaDoc() {
        return getBigDecimal("id_tipo_strut_unita_doc");
    }

    public void setIdTipoStrutUnitaDoc(BigDecimal idTipoStrutUnitaDoc) {
        setObject("id_tipo_strut_unita_doc", idTipoStrutUnitaDoc);
    }

    public BigDecimal getIdTipoUnitaDoc() {
        return getBigDecimal("id_tipo_unita_doc");
    }

    public void setIdTipoUnitaDoc(BigDecimal idTipoUnitaDoc) {
        setObject("id_tipo_unita_doc", idTipoUnitaDoc);
    }

    public String getNmTipoStrutUnitaDoc() {
        return getString("nm_tipo_strut_unita_doc");
    }

    public void setNmTipoStrutUnitaDoc(String nmTipoStrutUnitaDoc) {
        setObject("nm_tipo_strut_unita_doc", nmTipoStrutUnitaDoc);
    }

    public String getDsTipoStrutUnitaDoc() {
        return getString("ds_tipo_strut_unita_doc");
    }

    public void setDsTipoStrutUnitaDoc(String dsTipoStrutUnitaDoc) {
        setObject("ds_tipo_strut_unita_doc", dsTipoStrutUnitaDoc);
    }

    public Timestamp getDtIstituz() {
        return getTimestamp("dt_istituz");
    }

    public void setDtIstituz(Timestamp dtIstituz) {
        setObject("dt_istituz", dtIstituz);
    }

    public Timestamp getDtSoppres() {
        return getTimestamp("dt_soppres");
    }

    public void setDtSoppres(Timestamp dtSoppres) {
        setObject("dt_soppres", dtSoppres);
    }

    public String getDsDataTipoStrutUnitaDoc() {
        return getString("ds_data_tipo_strut_unita_doc");
    }

    public void setDsDataTipoStrutUnitaDoc(String dsDataTipoStrutUnitaDoc) {
        setObject("ds_data_tipo_strut_unita_doc", dsDataTipoStrutUnitaDoc);
    }

    public String getDsOggTipoStrutUnitaDoc() {
        return getString("ds_ogg_tipo_strut_unita_doc");
    }

    public void setDsOggTipoStrutUnitaDoc(String dsOggTipoStrutUnitaDoc) {
        setObject("ds_ogg_tipo_strut_unita_doc", dsOggTipoStrutUnitaDoc);
    }

    public String getDsNumeroTipoStrutUnitaDoc() {
        return getString("ds_numero_tipo_strut_unita_doc");
    }

    public void setDsNumeroTipoStrutUnitaDoc(String dsNumeroTipoStrutUnitaDoc) {
        setObject("ds_numero_tipo_strut_unita_doc", dsNumeroTipoStrutUnitaDoc);
    }

    public String getDsAnnoTipoStrutUnitaDoc() {
        return getString("ds_anno_tipo_strut_unita_doc");
    }

    public void setDsAnnoTipoStrutUnitaDoc(String dsAnnoTipoStrutUnitaDoc) {
        setObject("ds_anno_tipo_strut_unita_doc", dsAnnoTipoStrutUnitaDoc);
    }

    public BigDecimal getAaMinTipoStrutUnitaDoc() {
        return getBigDecimal("aa_min_tipo_strut_unita_doc");
    }

    public void setAaMinTipoStrutUnitaDoc(BigDecimal aaMinTipoStrutUnitaDoc) {
        setObject("aa_min_tipo_strut_unita_doc", aaMinTipoStrutUnitaDoc);
    }

    public BigDecimal getAaMaxTipoStrutUnitaDoc() {
        return getBigDecimal("aa_max_tipo_strut_unita_doc");
    }

    public void setAaMaxTipoStrutUnitaDoc(BigDecimal aaMaxTipoStrutUnitaDoc) {
        setObject("aa_max_tipo_strut_unita_doc", aaMaxTipoStrutUnitaDoc);
    }

    public String getDsRifTempTipoStrutUd() {
        return getString("ds_rif_temp_tipo_strut_ud");
    }

    public void setDsRifTempTipoStrutUd(String dsRifTempTipoStrutUd) {
        setObject("ds_rif_temp_tipo_strut_ud", dsRifTempTipoStrutUd);
    }

    public String getDsCollegamentiUd() {
        return getString("ds_collegamenti_ud");
    }

    public void setDsCollegamentiUd(String dsCollegamentiUd) {
        setObject("ds_collegamenti_ud", dsCollegamentiUd);
    }

    public String getDsPeriodicitaVers() {
        return getString("ds_periodicita_vers");
    }

    public void setDsPeriodicitaVers(String dsPeriodicitaVers) {
        setObject("ds_periodicita_vers", dsPeriodicitaVers);
    }

    public String getDsFirma() {
        return getString("ds_firma");
    }

    public void setDsFirma(String dsFirma) {
        setObject("ds_firma", dsFirma);
    }

    @Override
    public void entityToRowBean(Object obj) {
        DecTipoStrutUnitaDoc entity = (DecTipoStrutUnitaDoc) obj;

        this.setIdTipoStrutUnitaDoc(
                entity.getIdTipoStrutUnitaDoc() == null ? null : BigDecimal.valueOf(entity.getIdTipoStrutUnitaDoc()));

        if (entity.getDecTipoUnitaDoc() != null) {
            this.setIdTipoUnitaDoc(new BigDecimal(entity.getDecTipoUnitaDoc().getIdTipoUnitaDoc()));
        }

        this.setNmTipoStrutUnitaDoc(entity.getNmTipoStrutUnitaDoc());
        this.setDsTipoStrutUnitaDoc(entity.getDsTipoStrutUnitaDoc());
        if (entity.getDtIstituz() != null) {
            this.setDtIstituz(new Timestamp(entity.getDtIstituz().getTime()));
        }
        if (entity.getDtSoppres() != null) {
            this.setDtSoppres(new Timestamp(entity.getDtSoppres().getTime()));
        }
        this.setDsDataTipoStrutUnitaDoc(entity.getDsDataTipoStrutUnitaDoc());
        this.setDsOggTipoStrutUnitaDoc(entity.getDsOggTipoStrutUnitaDoc());
        this.setDsNumeroTipoStrutUnitaDoc(entity.getDsNumeroTipoStrutUnitaDoc());
        this.setDsAnnoTipoStrutUnitaDoc(entity.getDsAnnoTipoStrutUnitaDoc());
        this.setAaMinTipoStrutUnitaDoc(entity.getAaMinTipoStrutUnitaDoc());
        this.setAaMaxTipoStrutUnitaDoc(entity.getAaMaxTipoStrutUnitaDoc());
        this.setDsRifTempTipoStrutUd(entity.getDsRifTempTipoStrutUd());
        this.setDsCollegamentiUd(entity.getDsCollegamentiUd());
        this.setDsPeriodicitaVers(entity.getDsPeriodicitaVers());
        this.setDsFirma(entity.getDsFirma());
    }

    @Override
    public DecTipoStrutUnitaDoc rowBeanToEntity() {
        DecTipoStrutUnitaDoc entity = new DecTipoStrutUnitaDoc();
        if (this.getIdTipoStrutUnitaDoc() != null) {
            entity.setIdTipoStrutUnitaDoc(this.getIdTipoStrutUnitaDoc().longValue());
        }
        if (this.getIdTipoUnitaDoc() != null) {
            if (entity.getDecTipoUnitaDoc() == null) {
                entity.setDecTipoUnitaDoc(new DecTipoUnitaDoc());
            }
            entity.getDecTipoUnitaDoc().setIdTipoUnitaDoc(this.getIdTipoUnitaDoc().longValue());
        }
        entity.setNmTipoStrutUnitaDoc(this.getNmTipoStrutUnitaDoc());
        entity.setDsTipoStrutUnitaDoc(this.getDsTipoStrutUnitaDoc());
        entity.setDtIstituz(this.getDtIstituz());
        entity.setDtSoppres(this.getDtSoppres());
        entity.setDsDataTipoStrutUnitaDoc(this.getDsDataTipoStrutUnitaDoc());
        entity.setDsOggTipoStrutUnitaDoc(this.getDsOggTipoStrutUnitaDoc());
        entity.setDsNumeroTipoStrutUnitaDoc(this.getDsNumeroTipoStrutUnitaDoc());
        entity.setDsAnnoTipoStrutUnitaDoc(this.getDsAnnoTipoStrutUnitaDoc());
        entity.setAaMinTipoStrutUnitaDoc(this.getAaMinTipoStrutUnitaDoc());
        entity.setAaMaxTipoStrutUnitaDoc(this.getAaMaxTipoStrutUnitaDoc());
        entity.setDsRifTempTipoStrutUd(this.getDsRifTempTipoStrutUd());
        entity.setDsCollegamentiUd(this.getDsCollegamentiUd());
        entity.setDsPeriodicitaVers(this.getDsPeriodicitaVers());
        entity.setDsFirma(this.getDsFirma());
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
