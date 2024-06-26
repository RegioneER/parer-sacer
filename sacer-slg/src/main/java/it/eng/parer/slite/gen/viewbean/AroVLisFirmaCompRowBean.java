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

package it.eng.parer.slite.gen.viewbean;

import java.math.BigDecimal;
import java.sql.Timestamp;

import it.eng.parer.viewEntity.AroVLisFirmaComp;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Aro_V_Lis_Firma_Comp
 *
 */
public class AroVLisFirmaCompRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$ViewBeanWriter", comments =
     * "This class was generated by OraTool", date = "Monday, 6 July 2015 12:42" )
     */
    private static final long serialVersionUID = 1L;

    public static AroVLisFirmaCompTableDescriptor TABLE_DESCRIPTOR = new AroVLisFirmaCompTableDescriptor();

    public AroVLisFirmaCompRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    public BigDecimal getIdCompDoc() {
        return getBigDecimal("id_comp_doc");
    }

    public void setIdCompDoc(BigDecimal idCompDoc) {
        setObject("id_comp_doc", idCompDoc);
    }

    public BigDecimal getIdFirmaComp() {
        return getBigDecimal("id_firma_comp");
    }

    public void setIdFirmaComp(BigDecimal idFirmaComp) {
        setObject("id_firma_comp", idFirmaComp);
    }

    public BigDecimal getPgFirma() {
        return getBigDecimal("pg_firma");
    }

    public void setPgFirma(BigDecimal pgFirma) {
        setObject("pg_firma", pgFirma);
    }

    public String getCdFirmatario() {
        return getString("cd_firmatario");
    }

    public void setCdFirmatario(String cdFirmatario) {
        setObject("cd_firmatario", cdFirmatario);
    }

    public String getNmCognomeFirmatario() {
        return getString("nm_cognome_firmatario");
    }

    public void setNmCognomeFirmatario(String nmCognomeFirmatario) {
        setObject("nm_cognome_firmatario", nmCognomeFirmatario);
    }

    public String getNmFirmatario() {
        return getString("nm_firmatario");
    }

    public void setNmFirmatario(String nmFirmatario) {
        setObject("nm_firmatario", nmFirmatario);
    }

    public String getTiFormatoFirma() {
        return getString("ti_formato_firma");
    }

    public void setTiFormatoFirma(String tiFormatoFirma) {
        setObject("ti_formato_firma", tiFormatoFirma);
    }

    public String getTiEsitoContrConforme() {
        return getString("ti_esito_contr_conforme");
    }

    public void setTiEsitoContrConforme(String tiEsitoContrConforme) {
        setObject("ti_esito_contr_conforme", tiEsitoContrConforme);
    }

    public Timestamp getTmRifTempUsato() {
        return getTimestamp("tm_rif_temp_usato");
    }

    public void setTmRifTempUsato(Timestamp tmRifTempUsato) {
        setObject("tm_rif_temp_usato", tmRifTempUsato);
    }

    public String getTiRifTempUsato() {
        return getString("ti_rif_temp_usato");
    }

    public void setTiRifTempUsato(String tiRifTempUsato) {
        setObject("ti_rif_temp_usato", tiRifTempUsato);
    }

    public String getTiEsitoVerifFirma() {
        return getString("ti_esito_verif_firma");
    }

    public void setTiEsitoVerifFirma(String tiEsitoVerifFirma) {
        setObject("ti_esito_verif_firma", tiEsitoVerifFirma);
    }

    public String getDsMsgEsitoVerifFirma() {
        return getString("ds_msg_esito_verif_firma");
    }

    public void setDsMsgEsitoVerifFirma(String dsMsgEsitoVerifFirma) {
        setObject("ds_msg_esito_verif_firma", dsMsgEsitoVerifFirma);
    }

    public String getTiEsitoVerifFirmaDtVers() {
        return getString("ti_esito_verif_firma_dt_vers");
    }

    public void setTiEsitoVerifFirmaDtVers(String tiEsitoVerifFirmaDtVers) {
        setObject("ti_esito_verif_firma_dt_vers", tiEsitoVerifFirmaDtVers);
    }

    public String getDsMsgVerifFirmaDtVers() {
        return getString("ds_msg_verif_firma_dt_vers");
    }

    public void setDsMsgVerifFirmaDtVers(String dsMsgVerifFirmaDtVers) {
        setObject("ds_msg_verif_firma_dt_vers", dsMsgVerifFirmaDtVers);
    }

    @Override
    public void entityToRowBean(Object obj) {
        AroVLisFirmaComp entity = (AroVLisFirmaComp) obj;
        this.setIdCompDoc(entity.getIdCompDoc());
        this.setIdFirmaComp(entity.getIdFirmaComp());
        this.setPgFirma(entity.getPgFirma());
        this.setCdFirmatario(entity.getCdFirmatario());
        this.setNmCognomeFirmatario(entity.getNmCognomeFirmatario());
        this.setNmFirmatario(entity.getNmFirmatario());
        this.setTiFormatoFirma(entity.getTiFormatoFirma());
        this.setTiEsitoContrConforme(entity.getTiEsitoContrConforme());
        if (entity.getTmRifTempUsato() != null) {
            this.setTmRifTempUsato(new Timestamp(entity.getTmRifTempUsato().getTime()));
        }
        this.setTiRifTempUsato(entity.getTiRifTempUsato());
        this.setTiEsitoVerifFirma(entity.getTiEsitoVerifFirma());
        this.setDsMsgEsitoVerifFirma(entity.getDsMsgEsitoVerifFirma());
        this.setTiEsitoVerifFirmaDtVers(entity.getTiEsitoVerifFirmaDtVers());
        this.setDsMsgVerifFirmaDtVers(entity.getDsMsgVerifFirmaDtVers());
    }

    @Override
    public AroVLisFirmaComp rowBeanToEntity() {
        AroVLisFirmaComp entity = new AroVLisFirmaComp();
        entity.setIdCompDoc(this.getIdCompDoc());
        entity.setIdFirmaComp(this.getIdFirmaComp());
        entity.setPgFirma(this.getPgFirma());
        entity.setCdFirmatario(this.getCdFirmatario());
        entity.setNmCognomeFirmatario(this.getNmCognomeFirmatario());
        entity.setNmFirmatario(this.getNmFirmatario());
        entity.setTiFormatoFirma(this.getTiFormatoFirma());
        entity.setTiEsitoContrConforme(this.getTiEsitoContrConforme());
        entity.setTmRifTempUsato(this.getTmRifTempUsato());
        entity.setTiRifTempUsato(this.getTiRifTempUsato());
        entity.setTiEsitoVerifFirma(this.getTiEsitoVerifFirma());
        entity.setDsMsgEsitoVerifFirma(this.getDsMsgEsitoVerifFirma());
        entity.setTiEsitoVerifFirmaDtVers(this.getTiEsitoVerifFirmaDtVers());
        entity.setDsMsgVerifFirmaDtVers(this.getDsMsgVerifFirmaDtVers());
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
