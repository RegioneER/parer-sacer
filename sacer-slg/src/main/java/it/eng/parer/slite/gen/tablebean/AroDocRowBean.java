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

import it.eng.parer.entity.AroDoc;
import it.eng.parer.entity.AroUnitaDoc;
import it.eng.parer.entity.DecTipoDoc;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Aro_Doc
 *
 */
public class AroDocRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Tuesday, 11 March 2014 18:25" )
     */
    private static final long serialVersionUID = 1L;

    public static AroDocTableDescriptor TABLE_DESCRIPTOR = new AroDocTableDescriptor();

    public AroDocRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdDoc() {
        return getBigDecimal("id_doc");
    }

    public void setIdDoc(BigDecimal idDoc) {
        setObject("id_doc", idDoc);
    }

    public BigDecimal getIdUnitaDoc() {
        return getBigDecimal("id_unita_doc");
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
        setObject("id_unita_doc", idUnitaDoc);
    }

    public BigDecimal getPgDoc() {
        return getBigDecimal("pg_doc");
    }

    public void setPgDoc(BigDecimal pgDoc) {
        setObject("pg_doc", pgDoc);
    }

    public BigDecimal getIdTipoDoc() {
        return getBigDecimal("id_tipo_doc");
    }

    public void setIdTipoDoc(BigDecimal idTipoDoc) {
        setObject("id_tipo_doc", idTipoDoc);
    }

    public String getTiStatoDoc() {
        return getString("ti_stato_doc");
    }

    public void setTiStatoDoc(String tiStatoDoc) {
        setObject("ti_stato_doc", tiStatoDoc);
    }

    public Timestamp getDtCreazione() {
        return getTimestamp("dt_creazione");
    }

    public void setDtCreazione(Timestamp dtCreazione) {
        setObject("dt_creazione", dtCreazione);
    }

    public String getCdKeyDocVers() {
        return getString("cd_key_doc_vers");
    }

    public void setCdKeyDocVers(String cdKeyDocVers) {
        setObject("cd_key_doc_vers", cdKeyDocVers);
    }

    public String getDlDoc() {
        return getString("dl_doc");
    }

    public void setDlDoc(String dlDoc) {
        setObject("dl_doc", dlDoc);
    }

    public String getDsAutoreDoc() {
        return getString("ds_autore_doc");
    }

    public void setDsAutoreDoc(String dsAutoreDoc) {
        setObject("ds_autore_doc", dsAutoreDoc);
    }

    public String getFlDocFisc() {
        return getString("fl_doc_fisc");
    }

    public void setFlDocFisc(String flDocFisc) {
        setObject("fl_doc_fisc", flDocFisc);
    }

    public String getFlDocFirmato() {
        return getString("fl_doc_firmato");
    }

    public void setFlDocFirmato(String flDocFirmato) {
        setObject("fl_doc_firmato", flDocFirmato);
    }

    public String getTiEsitoVerifFirme() {
        return getString("ti_esito_verif_firme");
    }

    public void setTiEsitoVerifFirme(String tiEsitoVerifFirme) {
        setObject("ti_esito_verif_firme", tiEsitoVerifFirme);
    }

    public String getDsMsgEsitoVerifFirme() {
        return getString("ds_msg_esito_verif_firme");
    }

    public void setDsMsgEsitoVerifFirme(String dsMsgEsitoVerifFirme) {
        setObject("ds_msg_esito_verif_firme", dsMsgEsitoVerifFirme);
    }

    public BigDecimal getIdStrut() {
        return getBigDecimal("id_strut");
    }

    public void setIdStrut(BigDecimal idStrut) {
        setObject("id_strut", idStrut);
    }

    public String getTiDoc() {
        return getString("ti_doc");
    }

    public void setTiDoc(String tiDoc) {
        setObject("ti_doc", tiDoc);
    }

    public String getFlForzaAccettazione() {
        return getString("fl_forza_accettazione");
    }

    public void setFlForzaAccettazione(String flForzaAccettazione) {
        setObject("fl_forza_accettazione", flForzaAccettazione);
    }

    public String getFlForzaConservazione() {
        return getString("fl_forza_conservazione");
    }

    public void setFlForzaConservazione(String flForzaConservazione) {
        setObject("fl_forza_conservazione", flForzaConservazione);
    }

    public String getTiConservazione() {
        return getString("ti_conservazione");
    }

    public void setTiConservazione(String tiConservazione) {
        setObject("ti_conservazione", tiConservazione);
    }

    public String getNmSistemaMigraz() {
        return getString("nm_sistema_migraz");
    }

    public void setNmSistemaMigraz(String nmSistemaMigraz) {
        setObject("nm_sistema_migraz", nmSistemaMigraz);
    }

    @Override
    public void entityToRowBean(Object obj) {
        AroDoc entity = (AroDoc) obj;
        this.setIdDoc(entity.getIdDoc() == null ? null : BigDecimal.valueOf(entity.getIdDoc()));

        if (entity.getAroUnitaDoc() != null) {
            this.setIdUnitaDoc(new BigDecimal(entity.getAroUnitaDoc().getIdUnitaDoc()));
        }

        this.setPgDoc(entity.getPgDoc());

        if (entity.getDecTipoDoc() != null) {
            this.setIdTipoDoc(new BigDecimal(entity.getDecTipoDoc().getIdTipoDoc()));
        }

        this.setTiStatoDoc(entity.getTiStatoDoc());
        if (entity.getDtCreazione() != null) {
            this.setDtCreazione(new Timestamp(entity.getDtCreazione().getTime()));
        }
        this.setCdKeyDocVers(entity.getCdKeyDocVers());
        this.setDlDoc(entity.getDlDoc());
        this.setDsAutoreDoc(entity.getDsAutoreDoc());
        this.setFlDocFisc(entity.getFlDocFisc());
        this.setFlDocFirmato(entity.getFlDocFirmato());
        this.setTiEsitoVerifFirme(entity.getTiEsitoVerifFirme());
        this.setDsMsgEsitoVerifFirme(entity.getDsMsgEsitoVerifFirme());
        this.setIdStrut(entity.getIdStrut());
        this.setTiDoc(entity.getTiDoc());
        this.setFlForzaAccettazione(entity.getFlForzaAccettazione());
        this.setFlForzaConservazione(entity.getFlForzaConservazione());
        this.setTiConservazione(entity.getTiConservazione());
        this.setNmSistemaMigraz(entity.getNmSistemaMigraz());
    }

    @Override
    public AroDoc rowBeanToEntity() {
        AroDoc entity = new AroDoc();
        if (this.getIdDoc() != null) {
            entity.setIdDoc(this.getIdDoc().longValue());
        }
        if (this.getIdUnitaDoc() != null) {
            if (entity.getAroUnitaDoc() == null) {
                entity.setAroUnitaDoc(new AroUnitaDoc());
            }
            entity.getAroUnitaDoc().setIdUnitaDoc(this.getIdUnitaDoc().longValue());
        }
        entity.setPgDoc(this.getPgDoc());
        if (this.getIdTipoDoc() != null) {
            if (entity.getDecTipoDoc() == null) {
                entity.setDecTipoDoc(new DecTipoDoc());
            }
            entity.getDecTipoDoc().setIdTipoDoc(this.getIdTipoDoc().longValue());
        }
        entity.setTiStatoDoc(this.getTiStatoDoc());
        entity.setDtCreazione(this.getDtCreazione());
        entity.setCdKeyDocVers(this.getCdKeyDocVers());
        entity.setDlDoc(this.getDlDoc());
        entity.setDsAutoreDoc(this.getDsAutoreDoc());
        entity.setFlDocFisc(this.getFlDocFisc());
        entity.setFlDocFirmato(this.getFlDocFirmato());
        entity.setTiEsitoVerifFirme(this.getTiEsitoVerifFirme());
        entity.setDsMsgEsitoVerifFirme(this.getDsMsgEsitoVerifFirme());
        entity.setIdStrut(this.getIdStrut());
        entity.setTiDoc(this.getTiDoc());
        entity.setFlForzaAccettazione(this.getFlForzaAccettazione());
        entity.setFlForzaConservazione(this.getFlForzaConservazione());
        entity.setTiConservazione(this.getTiConservazione());
        entity.setNmSistemaMigraz(this.getNmSistemaMigraz());
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
