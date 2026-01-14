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

import it.eng.parer.entity.DecDocProcessoConserv;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Org_Doc_Processo_Conserv
 *
 */
public class DecDocProcessoConservRowBean extends BaseRow implements JEEBaseRowInterface {

    public static DecDocProcessoConservTableDescriptor TABLE_DESCRIPTOR = new DecDocProcessoConservTableDescriptor();

    public DecDocProcessoConservRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // ===================================================================================
    // Nome colonna : ID_DOC_PROCESSO_CONSERV
    // Tipo colonna : NUMBER(22)
    // Default DB :
    // ===================================================================================
    public void setIdDocProcessoConserv(BigDecimal idDocProcessoConserv) {
        setObject("id_doc_processo_conserv", idDocProcessoConserv);
    }

    public BigDecimal getIdDocProcessoConserv() {
        return getBigDecimal("id_doc_processo_conserv");
    }

    // ===================================================================================
    // Nome colonna : AA_DOC_PROCESSO_CONSERV
    // Tipo colonna : NUMBER(22)
    // Default DB :
    // ===================================================================================
    public void setAaDocProcessoConserv(BigDecimal aaDocProcessoConserv) {
        setObject("aa_doc_processo_conserv", aaDocProcessoConserv);
    }

    public BigDecimal getAaDocProcessoConserv() {
        return getBigDecimal("aa_doc_processo_conserv");
    }

    // ===================================================================================
    // Nome colonna : CD_KEY_DOC_PROCESSO_CONSERV
    // Tipo colonna : VARCHAR2(100 CHAR)
    // Default DB :
    // ===================================================================================
    public void setCdKeyDocProcessoConserv(String cdKeyDocProcessoConserv) {
        setObject("cd_key_doc_processo_conserv", cdKeyDocProcessoConserv);
    }

    public String getCdKeyDocProcessoConserv() {
        return getString("cd_key_doc_processo_conserv");
    }

    // ===================================================================================
    // Nome colonna : CD_REGISTRO_DOC_PROCESSO_CONSERV
    // Tipo colonna : VARCHAR2(100 CHAR)
    // Default DB :
    // ===================================================================================
    public void setCdRegistroDocProcessoConserv(String cdRegistroDocProcessoConserv) {
        setObject("cd_registro_doc_processo_conserv", cdRegistroDocProcessoConserv);
    }

    public String getCdRegistroDocProcessoConserv() {
        return getString("cd_registro_doc_processo_conserv");
    }

    // ===================================================================================
    // Nome colonna : DS_DOC_PROCESSO_CONSERV
    // Tipo colonna : VARCHAR2(4000 CHAR)
    // Default DB :
    // ===================================================================================
    public void setDsDocProcessoConserv(String dsDocProcessoConserv) {
        setObject("ds_doc_processo_conserv", dsDocProcessoConserv);
    }

    public String getDsDocProcessoConserv() {
        return getString("ds_doc_processo_conserv");
    }

    // ===================================================================================
    // Nome colonna : DT_DOC_PROCESSO_CONSERV
    // Tipo colonna : DATE(7)
    // Default DB :
    // ===================================================================================
    public void setDtDocProcessoConserv(Timestamp dtDocProcessoConserv) {
        setObject("dt_doc_processo_conserv", dtDocProcessoConserv);
    }

    public Timestamp getDtDocProcessoConserv() {
        return getTimestamp("dt_doc_processo_conserv");
    }

    // ===================================================================================
    // Nome colonna : NM_FILE_DOC_PROCESSO_CONSERV
    // Tipo colonna : VARCHAR2(254 CHAR)
    // Default DB :
    // ===================================================================================
    public void setNmFileDocProcessoConserv(String nmFileDocProcessoConserv) {
        setObject("nm_file_doc_processo_conserv", nmFileDocProcessoConserv);
    }

    public String getNmFileDocProcessoConserv() {
        return getString("nm_file_doc_processo_conserv");
    }

    // ===================================================================================
    // Nome colonna : ENTE_DOC_PROCESSO_CONSERV
    // Tipo colonna : VARCHAR2(100 CHAR)
    // Default DB :
    // ===================================================================================
    public void setEnteDocProcessoConserv(String enteDocProcessoConserv) {
        setObject("ente_doc_processo_conserv", enteDocProcessoConserv);
    }

    public String getEnteDocProcessoConserv() {
        return getString("ente_doc_processo_conserv");
    }

    // ===================================================================================
    // Nome colonna : STRUTTURA_DOC_PROCESSO_CONSERV
    // Tipo colonna : VARCHAR2(100 CHAR)
    // Default DB :
    // ===================================================================================
    public void setStrutturaDocProcessoConserv(String strutturaDocProcessoConserv) {
        setObject("struttura_doc_processo_conserv", strutturaDocProcessoConserv);
    }

    public String getStrutturaDocProcessoConserv() {
        return getString("struttura_doc_processo_conserv");
    }

    // ===================================================================================
    // Nome colonna : MIMETYPE
    // Tipo colonna : VARCHAR2(100 CHAR)
    // Default DB :
    // ===================================================================================
    public void setMimeType(String mimeType) {
        setObject("mimetype", mimeType);
    }

    public String getMimeType() {
        return getString("mimetype");
    }

    // ===================================================================================
    // Nome colonna : PG_DOC_PROCESSO_CONSERV
    // Tipo colonna : NUMBER(22)
    // Default DB :
    // ===================================================================================
    public void setPgDocProcessoConserv(BigDecimal pgDocProcessoConserv) {
        setObject("pg_doc_processo_conserv", pgDocProcessoConserv);
    }

    public BigDecimal getPgDocProcessoConserv() {
        return getBigDecimal("pg_doc_processo_conserv");
    }

    // ===================================================================================
    // Foreign Key : FK_DEC_DOC_PROCESSO_CONSERV_TIPO
    // Tabella di origine : DEC_TIPO_DOC_PROCESSO_CONSERV
    // Tipo oggetto : DecTipoDocProcessoConserv.class
    // Nome metodo :
    // ===================================================================================
    public void setIdTipoDocProcessoConserv(BigDecimal idTipoDocProcessoConserv) {
        setObject("id_tipo_doc_processo_conserv", idTipoDocProcessoConserv);
    }

    public BigDecimal getIdTipoDocProcessoConserv() {
        return getBigDecimal("id_tipo_doc_processo_conserv");
    }

    // Metodo di convenience per accedere al nome del tipo documento
    public String getNmTipoDoc() {
        return getString("nm_tipo_doc");
    }

    public void setNmTipoDoc(String nmTipoDoc) {
        setObject("nm_tipo_doc", nmTipoDoc);
    }

    // ===================================================================================
    // Foreign Key : FK_ORG_DOC_PROCESSO_CONSERV_01
    // Tabella di origine : ORG_ENTE_SIAM
    // Tipo oggetto : OrgEnteSiam.class
    // Nome metodo :
    // ===================================================================================
    public void setIdEnteSiam(BigDecimal idEnteSiam) {
        setObject("id_ente_siam", idEnteSiam);
    }

    public BigDecimal getIdEnteSiam() {
        return getBigDecimal("id_ente_siam");
    }

    // ===================================================================================
    // Foreign Key : FK_ORG_DOC_PROCESSO_CONSERV_02
    // Tabella di origine : USR_ORGANIZ_IAM
    // Tipo oggetto : UsrOrganizIam.class
    // Nome metodo :
    // ===================================================================================
    public void setIdOrganizIam(BigDecimal idOrganizIam) {
        setObject("id_organiz_iam", idOrganizIam);
    }

    public BigDecimal getIdOrganizIam() {
        return getBigDecimal("id_organiz_iam");
    }

    public void setRegistroAnnoNumero(String regAnnoNum) {
        setObject("registro_anno_numero", regAnnoNum);
    }

    public String getRegistroAnnoNumero() {
        return getString("registro_anno_numero");
    }

    @Override
    public void entityToRowBean(Object obj) {
        DecDocProcessoConserv entity = (DecDocProcessoConserv) obj;
        this.setIdDocProcessoConserv(BigDecimal.valueOf(entity.getIdDocProcessoConserv()));
        this.setAaDocProcessoConserv(entity.getAaDocProcessoConserv());
        this.setCdKeyDocProcessoConserv(entity.getCdKeyDocProcessoConserv());
        this.setCdRegistroDocProcessoConserv(entity.getCdRegistroDocProcessoConserv());
        this.setDsDocProcessoConserv(entity.getDsDocProcessoConserv());
        if (entity.getDtDocProcessoConserv() != null) {
            this.setDtDocProcessoConserv(new Timestamp(entity.getDtDocProcessoConserv().getTime()));
        }
        this.setNmFileDocProcessoConserv(entity.getNmFileDocProcessoConserv());
        this.setEnteDocProcessoConserv(entity.getEnteDocProcessoConserv());
        this.setStrutturaDocProcessoConserv(entity.getStrutturaDocProcessoConserv());
        this.setMimeType(entity.getMimeType());
        this.setPgDocProcessoConserv(entity.getPgDocProcessoConserv());

        // Gestione della nuova relazione con DecTipoDocProcessoConserv
        if (entity.getDecTipoDocProcessoConserv() != null) {
            this.setIdTipoDocProcessoConserv(BigDecimal
                    .valueOf(entity.getDecTipoDocProcessoConserv().getIdTipoDocProcessoConserv()));
            this.setNmTipoDoc(entity.getDecTipoDocProcessoConserv().getNmTipoDoc());
        }

        if (entity.getSiOrgEnteSiam() != null) {
            this.setIdEnteSiam(BigDecimal.valueOf(entity.getSiOrgEnteSiam().getIdEnteSiam()));
        }
        if (entity.getSiUsrOrganizIam() != null) {
            this.setIdOrganizIam(BigDecimal.valueOf(entity.getSiUsrOrganizIam().getIdOrganizIam()));
        }
    }

    @Override
    public DecDocProcessoConserv rowBeanToEntity() {
        DecDocProcessoConserv entity = new DecDocProcessoConserv();
        if (this.getIdDocProcessoConserv() != null) {
            entity.setIdDocProcessoConserv(this.getIdDocProcessoConserv().longValue());
        }
        entity.setAaDocProcessoConserv(this.getAaDocProcessoConserv());
        entity.setCdKeyDocProcessoConserv(this.getCdKeyDocProcessoConserv());
        entity.setCdRegistroDocProcessoConserv(this.getCdRegistroDocProcessoConserv());
        entity.setDsDocProcessoConserv(this.getDsDocProcessoConserv());
        if (this.getDtDocProcessoConserv() != null) {
            entity.setDtDocProcessoConserv(this.getDtDocProcessoConserv());
        }
        entity.setNmFileDocProcessoConserv(this.getNmFileDocProcessoConserv());
        entity.setEnteDocProcessoConserv(this.getEnteDocProcessoConserv());
        entity.setStrutturaDocProcessoConserv(this.getStrutturaDocProcessoConserv());
        entity.setMimeType(this.getMimeType());
        entity.setPgDocProcessoConserv(this.getPgDocProcessoConserv());

        // Nota: la relazione con DecTipoDocProcessoConserv deve essere gestita
        // separatamente attraverso l'EntityManager nel layer di business

        return entity;
    }
}
