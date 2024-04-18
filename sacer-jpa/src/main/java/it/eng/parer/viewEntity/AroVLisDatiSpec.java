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

package it.eng.parer.viewEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the ARO_V_LIS_DATI_SPEC database table.
 *
 */
@Entity
@Table(name = "ARO_V_LIS_DATI_SPEC")
public class AroVLisDatiSpec implements Serializable {

    private static final long serialVersionUID = 1L;
    private String cdVersioneXsd;
    private String dlValore;
    private String dsAttribDatiSpec;
    private String dsVersioneXsd;
    private Date dtIstituz;
    private Date dtSoppres;
    private BigDecimal idCompDoc;
    private BigDecimal idDoc;
    private BigDecimal idUnitaDoc;
    private BigDecimal idUsoXsdDatiSpec;
    private BigDecimal idValoreAttribDatiSpec;
    private BigDecimal niOrdAttrib;
    private String nmAttribDatiSpec;
    private String nmSistemaMigraz;
    private String tiEntitaSacer;
    private String tiUsoXsd;

    public AroVLisDatiSpec() {/* Hibernate */
    }

    @Column(name = "CD_VERSIONE_XSD")
    public String getCdVersioneXsd() {
        return this.cdVersioneXsd;
    }

    public void setCdVersioneXsd(String cdVersioneXsd) {
        this.cdVersioneXsd = cdVersioneXsd;
    }

    @Column(name = "DL_VALORE")
    public String getDlValore() {
        return this.dlValore;
    }

    public void setDlValore(String dlValore) {
        this.dlValore = dlValore;
    }

    @Column(name = "DS_ATTRIB_DATI_SPEC")
    public String getDsAttribDatiSpec() {
        return this.dsAttribDatiSpec;
    }

    public void setDsAttribDatiSpec(String dsAttribDatiSpec) {
        this.dsAttribDatiSpec = dsAttribDatiSpec;
    }

    @Column(name = "DS_VERSIONE_XSD")
    public String getDsVersioneXsd() {
        return this.dsVersioneXsd;
    }

    public void setDsVersioneXsd(String dsVersioneXsd) {
        this.dsVersioneXsd = dsVersioneXsd;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_ISTITUZ")
    public Date getDtIstituz() {
        return this.dtIstituz;
    }

    public void setDtIstituz(Date dtIstituz) {
        this.dtIstituz = dtIstituz;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_SOPPRES")
    public Date getDtSoppres() {
        return this.dtSoppres;
    }

    public void setDtSoppres(Date dtSoppres) {
        this.dtSoppres = dtSoppres;
    }

    @Column(name = "ID_COMP_DOC")
    public BigDecimal getIdCompDoc() {
        return this.idCompDoc;
    }

    public void setIdCompDoc(BigDecimal idCompDoc) {
        this.idCompDoc = idCompDoc;
    }

    @Column(name = "ID_DOC")
    public BigDecimal getIdDoc() {
        return this.idDoc;
    }

    public void setIdDoc(BigDecimal idDoc) {
        this.idDoc = idDoc;
    }

    @Column(name = "ID_UNITA_DOC")
    public BigDecimal getIdUnitaDoc() {
        return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
        this.idUnitaDoc = idUnitaDoc;
    }

    @Column(name = "ID_USO_XSD_DATI_SPEC")
    public BigDecimal getIdUsoXsdDatiSpec() {
        return this.idUsoXsdDatiSpec;
    }

    public void setIdUsoXsdDatiSpec(BigDecimal idUsoXsdDatiSpec) {
        this.idUsoXsdDatiSpec = idUsoXsdDatiSpec;
    }

    @Id
    @Column(name = "ID_VALORE_ATTRIB_DATI_SPEC")
    public BigDecimal getIdValoreAttribDatiSpec() {
        return this.idValoreAttribDatiSpec;
    }

    public void setIdValoreAttribDatiSpec(BigDecimal idValoreAttribDatiSpec) {
        this.idValoreAttribDatiSpec = idValoreAttribDatiSpec;
    }

    @Column(name = "NI_ORD_ATTRIB")
    public BigDecimal getNiOrdAttrib() {
        return this.niOrdAttrib;
    }

    public void setNiOrdAttrib(BigDecimal niOrdAttrib) {
        this.niOrdAttrib = niOrdAttrib;
    }

    @Column(name = "NM_ATTRIB_DATI_SPEC")
    public String getNmAttribDatiSpec() {
        return this.nmAttribDatiSpec;
    }

    public void setNmAttribDatiSpec(String nmAttribDatiSpec) {
        this.nmAttribDatiSpec = nmAttribDatiSpec;
    }

    @Column(name = "NM_SISTEMA_MIGRAZ")
    public String getNmSistemaMigraz() {
        return this.nmSistemaMigraz;
    }

    public void setNmSistemaMigraz(String nmSistemaMigraz) {
        this.nmSistemaMigraz = nmSistemaMigraz;
    }

    @Column(name = "TI_ENTITA_SACER")
    public String getTiEntitaSacer() {
        return this.tiEntitaSacer;
    }

    public void setTiEntitaSacer(String tiEntitaSacer) {
        this.tiEntitaSacer = tiEntitaSacer;
    }

    @Column(name = "TI_USO_XSD")
    public String getTiUsoXsd() {
        return this.tiUsoXsd;
    }

    public void setTiUsoXsd(String tiUsoXsd) {
        this.tiUsoXsd = tiUsoXsd;
    }
}
