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

import it.eng.parer.entity.ElvLogElencoVer;
import it.eng.parer.entity.IamUser;
import it.eng.parer.entity.LogJob;
import it.eng.parer.entity.OrgStrut;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Elv_Log_Elenco_Vers
 *
 */
public class ElvLogElencoVerRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Tuesday, 31 March 2015 14:33" )
     */
    private static final long serialVersionUID = 1L;

    public static ElvLogElencoVerTableDescriptor TABLE_DESCRIPTOR = new ElvLogElencoVerTableDescriptor();

    public ElvLogElencoVerRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdLogElencoVers() {
        return getBigDecimal("id_log_elenco_vers");
    }

    public void setIdLogElencoVers(BigDecimal idLogElencoVers) {
        setObject("id_log_elenco_vers", idLogElencoVers);
    }

    public BigDecimal getIdUser() {
        return getBigDecimal("id_user");
    }

    public void setIdUser(BigDecimal idUser) {
        setObject("id_user", idUser);
    }

    public BigDecimal getIdElencoVers() {
        return getBigDecimal("id_elenco_vers");
    }

    public void setIdElencoVers(BigDecimal idElencoVers) {
        setObject("id_elenco_vers", idElencoVers);
    }

    public Timestamp getTmOper() {
        return getTimestamp("tm_oper");
    }

    public void setTmOper(Timestamp tmOper) {
        setObject("tm_oper", tmOper);
    }

    public String getTiOper() {
        return getString("ti_oper");
    }

    public void setTiOper(String tiOper) {
        setObject("ti_oper", tiOper);
    }

    public String getNmElenco() {
        return getString("nm_elenco");
    }

    public void setNmElenco(String nmElenco) {
        setObject("nm_elenco", nmElenco);
    }

    public BigDecimal getIdStrut() {
        return getBigDecimal("id_strut");
    }

    public void setIdStrut(BigDecimal idStrut) {
        setObject("id_strut", idStrut);
    }

    public BigDecimal getIdLogJob() {
        return getBigDecimal("id_log_job");
    }

    public void setIdLogJob(BigDecimal idLogJob) {
        setObject("id_log_job", idLogJob);
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

    public String getTiDoc() {
        return getString("ti_doc");
    }

    public void setTiDoc(String tiDoc) {
        setObject("ti_doc", tiDoc);
    }

    public BigDecimal getPgDoc() {
        return getBigDecimal("pg_doc");
    }

    public void setPgDoc(BigDecimal pgDoc) {
        setObject("pg_doc", pgDoc);
    }

    public BigDecimal getPgUpdUnitaDoc() {
        return getBigDecimal("pg_upd_unita_doc");
    }

    public void setPgUpdUnitaDoc(BigDecimal pgUpdUnitaDoc) {
        setObject("pg_upd_unita_doc", pgUpdUnitaDoc);
    }

    @Override
    public void entityToRowBean(Object obj) {
        ElvLogElencoVer entity = (ElvLogElencoVer) obj;

        this.setIdLogElencoVers(
                entity.getIdLogElencoVers() == null ? null : BigDecimal.valueOf(entity.getIdLogElencoVers()));

        if (entity.getIamUser() != null) {
            this.setIdUser(new BigDecimal(entity.getIamUser().getIdUserIam()));
        }

        this.setIdElencoVers(entity.getIdElencoVers());
        if (entity.getTmOper() != null) {
            this.setTmOper(new Timestamp(entity.getTmOper().getTime()));
        }
        this.setTiOper(entity.getTiOper());
        this.setNmElenco(entity.getNmElenco());

        if (entity.getOrgStrut() != null) {
            this.setIdStrut(new BigDecimal(entity.getOrgStrut().getIdStrut()));
        }

        if (entity.getLogJob() != null) {
            this.setIdLogJob(new BigDecimal(entity.getLogJob().getIdLogJob()));
        }

        this.setCdRegistroKeyUnitaDoc(entity.getCdRegistroKeyUnitaDoc());
        this.setAaKeyUnitaDoc(entity.getAaKeyUnitaDoc());
        this.setCdKeyUnitaDoc(entity.getCdKeyUnitaDoc());
        this.setTiDoc(entity.getTiDoc());
        this.setPgDoc(entity.getPgDoc());
        this.setPgUpdUnitaDoc(entity.getPgUpdUnitaDoc());
    }

    @Override
    public ElvLogElencoVer rowBeanToEntity() {
        ElvLogElencoVer entity = new ElvLogElencoVer();
        if (this.getIdLogElencoVers() != null) {
            entity.setIdLogElencoVers(this.getIdLogElencoVers().longValue());
        }
        if (this.getIdUser() != null) {
            if (entity.getIamUser() == null) {
                entity.setIamUser(new IamUser());
            }
            entity.getIamUser().setIdUserIam(this.getIdUser().longValue());
        }
        entity.setIdElencoVers(this.getIdElencoVers());
        entity.setTmOper(this.getTmOper());
        entity.setTiOper(this.getTiOper());
        entity.setNmElenco(this.getNmElenco());
        if (this.getIdStrut() != null) {
            if (entity.getOrgStrut() == null) {
                entity.setOrgStrut(new OrgStrut());
            }
            entity.getOrgStrut().setIdStrut(this.getIdStrut().longValue());
        }
        if (this.getIdLogJob() != null) {
            if (entity.getLogJob() == null) {
                entity.setLogJob(new LogJob());
            }
            entity.getLogJob().setIdLogJob(this.getIdLogJob().longValue());
        }
        entity.setCdRegistroKeyUnitaDoc(this.getCdRegistroKeyUnitaDoc());
        entity.setAaKeyUnitaDoc(this.getAaKeyUnitaDoc());
        entity.setCdKeyUnitaDoc(this.getCdKeyUnitaDoc());
        entity.setTiDoc(this.getTiDoc());
        entity.setPgDoc(this.getPgDoc());
        entity.setPgUpdUnitaDoc(this.getPgUpdUnitaDoc());
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
