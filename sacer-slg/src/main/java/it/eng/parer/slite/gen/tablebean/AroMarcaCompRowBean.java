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

import it.eng.parer.entity.AroBustaCrittog;
import it.eng.parer.entity.AroCompDoc;
import it.eng.parer.entity.AroMarcaComp;
import it.eng.parer.entity.FirCertifCa;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Aro_Marca_Comp
 *
 */
public class AroMarcaCompRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Tuesday, 11 March 2014 18:25" )
     */
    private static final long serialVersionUID = 1L;

    public static AroMarcaCompTableDescriptor TABLE_DESCRIPTOR = new AroMarcaCompTableDescriptor();

    public AroMarcaCompRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdMarcaComp() {
        return getBigDecimal("id_marca_comp");
    }

    public void setIdMarcaComp(BigDecimal idMarcaComp) {
        setObject("id_marca_comp", idMarcaComp);
    }

    public BigDecimal getIdCompDoc() {
        return getBigDecimal("id_comp_doc");
    }

    public void setIdCompDoc(BigDecimal idCompDoc) {
        setObject("id_comp_doc", idCompDoc);
    }

    public BigDecimal getPgMarca() {
        return getBigDecimal("pg_marca");
    }

    public void setPgMarca(BigDecimal pgMarca) {
        setObject("pg_marca", pgMarca);
    }

    public String getTiEsitoContrConforme() {
        return getString("ti_esito_contr_conforme");
    }

    public void setTiEsitoContrConforme(String tiEsitoContrConforme) {
        setObject("ti_esito_contr_conforme", tiEsitoContrConforme);
    }

    public String getDsMsgEsitoContrConforme() {
        return getString("ds_msg_esito_contr_conforme");
    }

    public void setDsMsgEsitoContrConforme(String dsMsgEsitoContrConforme) {
        setObject("ds_msg_esito_contr_conforme", dsMsgEsitoContrConforme);
    }

    public String getTiMarcaTemp() {
        return getString("ti_marca_temp");
    }

    public void setTiMarcaTemp(String tiMarcaTemp) {
        setObject("ti_marca_temp", tiMarcaTemp);
    }

    public String getDsMarcaBase64() {
        return getString("ds_marca_base64");
    }

    public void setDsMarcaBase64(String ds_marca_base64) {
        setObject("ds_marca_base64", ds_marca_base64);
    }

    public String getDsAlgoMarca() {
        return getString("ds_algo_marca");
    }

    public void setDsAlgoMarca(String dsAlgoMarca) {
        setObject("ds_algo_marca", dsAlgoMarca);
    }

    public Timestamp getTmMarcaTemp() {
        return getTimestamp("tm_marca_temp");
    }

    public void setTmMarcaTemp(Timestamp tmMarcaTemp) {
        setObject("tm_marca_temp", tmMarcaTemp);
    }

    public String getTiFormatoMarca() {
        return getString("ti_formato_marca");
    }

    public void setTiFormatoMarca(String tiFormatoMarca) {
        setObject("ti_formato_marca", tiFormatoMarca);
    }

    public BigDecimal getIdCertifCa() {
        return getBigDecimal("id_certif_ca");
    }

    public void setIdCertifCa(BigDecimal idCertifCa) {
        setObject("id_certif_ca", idCertifCa);
    }

    public Timestamp getDtScadMarca() {
        return getTimestamp("dt_scad_marca");
    }

    public void setDtScadMarca(Timestamp dtScadMarca) {
        setObject("dt_scad_marca", dtScadMarca);
    }

    public String getTiEsitoVerifMarca() {
        return getString("ti_esito_verif_marca");
    }

    public void setTiEsitoVerifMarca(String tiEsitoVerifMarca) {
        setObject("ti_esito_verif_marca", tiEsitoVerifMarca);
    }

    public String getDsMsgEsitoVerifMarca() {
        return getString("ds_msg_esito_verif_marca");
    }

    public void setDsMsgEsitoVerifMarca(String dsMsgEsitoVerifMarca) {
        setObject("ds_msg_esito_verif_marca", dsMsgEsitoVerifMarca);
    }

    public BigDecimal getPgBusta() {
        return getBigDecimal("pg_busta");
    }

    public void setPgBusta(BigDecimal pgBusta) {
        setObject("pg_busta", pgBusta);
    }

    public BigDecimal getIdBustaCrittog() {
        return getBigDecimal("id_busta_crittog");
    }

    public void setIdBustaCrittog(BigDecimal idBustaCrittog) {
        setObject("id_busta_crittog", idBustaCrittog);
    }

    @Override
    public void entityToRowBean(Object obj) {
        AroMarcaComp entity = (AroMarcaComp) obj;
        this.setIdMarcaComp(entity.getIdMarcaComp() == null ? null : BigDecimal.valueOf(entity.getIdMarcaComp()));

        if (entity.getAroCompDoc() != null) {
            this.setIdCompDoc(new BigDecimal(entity.getAroCompDoc().getIdCompDoc()));
        }

        this.setPgMarca(entity.getPgMarca());
        this.setTiEsitoContrConforme(entity.getTiEsitoContrConforme());
        this.setDsMsgEsitoContrConforme(entity.getDsMsgEsitoContrConforme());
        this.setTiMarcaTemp(entity.getTiMarcaTemp());
        this.setDsMarcaBase64(entity.getDsMarcaBase64());
        this.setDsAlgoMarca(entity.getDsAlgoMarca());
        if (entity.getTmMarcaTemp() != null) {
            this.setTmMarcaTemp(new Timestamp(entity.getTmMarcaTemp().getTime()));
        }
        this.setTiFormatoMarca(entity.getTiFormatoMarca());

        if (entity.getFirCertifCa() != null) {
            this.setIdCertifCa(new BigDecimal(entity.getFirCertifCa().getIdCertifCa()));
        }

        if (entity.getDtScadMarca() != null) {
            this.setDtScadMarca(new Timestamp(entity.getDtScadMarca().getTime()));
        }
        this.setTiEsitoVerifMarca(entity.getTiEsitoVerifMarca());
        this.setDsMsgEsitoVerifMarca(entity.getDsMsgEsitoVerifMarca());
        this.setPgBusta(entity.getPgBusta());

        if (entity.getAroBustaCrittog() != null) {
            this.setIdBustaCrittog(new BigDecimal(entity.getAroBustaCrittog().getIdBustaCrittog()));
        }

    }

    @Override
    public AroMarcaComp rowBeanToEntity() {
        AroMarcaComp entity = new AroMarcaComp();
        if (this.getIdMarcaComp() != null) {
            entity.setIdMarcaComp(this.getIdMarcaComp().longValue());
        }
        if (this.getIdCompDoc() != null) {
            if (entity.getAroCompDoc() == null) {
                entity.setAroCompDoc(new AroCompDoc());
            }
            entity.getAroCompDoc().setIdCompDoc(this.getIdCompDoc().longValue());
        }
        entity.setPgMarca(this.getPgMarca());
        entity.setTiEsitoContrConforme(this.getTiEsitoContrConforme());
        entity.setDsMsgEsitoContrConforme(this.getDsMsgEsitoContrConforme());
        entity.setTiMarcaTemp(this.getTiMarcaTemp());
        entity.setDsMarcaBase64(this.getDsMarcaBase64());
        entity.setDsAlgoMarca(this.getDsAlgoMarca());
        entity.setTmMarcaTemp(this.getTmMarcaTemp());
        entity.setTiFormatoMarca(this.getTiFormatoMarca());
        if (this.getIdCertifCa() != null) {
            if (entity.getFirCertifCa() == null) {
                entity.setFirCertifCa(new FirCertifCa());
            }
            entity.getFirCertifCa().setIdCertifCa(this.getIdCertifCa().longValue());
        }
        entity.setDtScadMarca(this.getDtScadMarca());
        entity.setTiEsitoVerifMarca(this.getTiEsitoVerifMarca());
        entity.setDsMsgEsitoVerifMarca(this.getDsMsgEsitoVerifMarca());
        entity.setPgBusta(this.getPgBusta());
        if (this.getIdBustaCrittog() != null) {
            if (entity.getAroBustaCrittog() == null) {
                entity.setAroBustaCrittog(new AroBustaCrittog());
            }
            entity.getAroBustaCrittog().setIdBustaCrittog(this.getIdBustaCrittog().longValue());
        }
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
