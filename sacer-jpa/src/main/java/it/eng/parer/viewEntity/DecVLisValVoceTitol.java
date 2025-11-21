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
 * The persistent class for the DEC_V_LIS_VAL_VOCE_TITOL database table.
 *
 */
@Entity
@Table(name = "DEC_V_LIS_VAL_VOCE_TITOL")

public class DecVLisValVoceTitol implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigDecimal aaDocInvioFin;
    private BigDecimal aaDocInvioIni;
    private String cdDocInvioFin;
    private String cdDocInvioIni;
    private String cdRegistroDocInvioFin;
    private String cdRegistroDocInvioIni;
    private String dlNote;
    private String dsVoceTitol;
    private Date dtDocInvioFin;
    private Date dtDocInvioIni;
    private Date dtFinVal;
    private Date dtIniVal;
    private Date dtValOperVoceTitolFin;
    private Date dtValOperVoceTitolIni;
    private String flUsoClassif;
    private BigDecimal idValVoceTitol;
    private BigDecimal idVoceTitol;
    private BigDecimal niAnniConserv;
    private String tiOperTitolFin;
    private String tiOperTitolIni;
    private String tiOperVoceTitolFin;
    private String tiOperVoceTitolIni;

    public DecVLisValVoceTitol() {/* Hibernate */
    }

    @Column(name = "AA_DOC_INVIO_FIN")
    public BigDecimal getAaDocInvioFin() {
        return this.aaDocInvioFin;
    }

    public void setAaDocInvioFin(BigDecimal aaDocInvioFin) {
        this.aaDocInvioFin = aaDocInvioFin;
    }

    @Column(name = "AA_DOC_INVIO_INI")
    public BigDecimal getAaDocInvioIni() {
        return this.aaDocInvioIni;
    }

    public void setAaDocInvioIni(BigDecimal aaDocInvioIni) {
        this.aaDocInvioIni = aaDocInvioIni;
    }

    @Column(name = "CD_DOC_INVIO_FIN")
    public String getCdDocInvioFin() {
        return this.cdDocInvioFin;
    }

    public void setCdDocInvioFin(String cdDocInvioFin) {
        this.cdDocInvioFin = cdDocInvioFin;
    }

    @Column(name = "CD_DOC_INVIO_INI")
    public String getCdDocInvioIni() {
        return this.cdDocInvioIni;
    }

    public void setCdDocInvioIni(String cdDocInvioIni) {
        this.cdDocInvioIni = cdDocInvioIni;
    }

    @Column(name = "CD_REGISTRO_DOC_INVIO_FIN")
    public String getCdRegistroDocInvioFin() {
        return this.cdRegistroDocInvioFin;
    }

    public void setCdRegistroDocInvioFin(String cdRegistroDocInvioFin) {
        this.cdRegistroDocInvioFin = cdRegistroDocInvioFin;
    }

    @Column(name = "CD_REGISTRO_DOC_INVIO_INI")
    public String getCdRegistroDocInvioIni() {
        return this.cdRegistroDocInvioIni;
    }

    public void setCdRegistroDocInvioIni(String cdRegistroDocInvioIni) {
        this.cdRegistroDocInvioIni = cdRegistroDocInvioIni;
    }

    @Column(name = "DL_NOTE")
    public String getDlNote() {
        return this.dlNote;
    }

    public void setDlNote(String dlNote) {
        this.dlNote = dlNote;
    }

    @Column(name = "DS_VOCE_TITOL")
    public String getDsVoceTitol() {
        return this.dsVoceTitol;
    }

    public void setDsVoceTitol(String dsVoceTitol) {
        this.dsVoceTitol = dsVoceTitol;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_DOC_INVIO_FIN")
    public Date getDtDocInvioFin() {
        return this.dtDocInvioFin;
    }

    public void setDtDocInvioFin(Date dtDocInvioFin) {
        this.dtDocInvioFin = dtDocInvioFin;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_DOC_INVIO_INI")
    public Date getDtDocInvioIni() {
        return this.dtDocInvioIni;
    }

    public void setDtDocInvioIni(Date dtDocInvioIni) {
        this.dtDocInvioIni = dtDocInvioIni;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIN_VAL")
    public Date getDtFinVal() {
        return this.dtFinVal;
    }

    public void setDtFinVal(Date dtFinVal) {
        this.dtFinVal = dtFinVal;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INI_VAL")
    public Date getDtIniVal() {
        return this.dtIniVal;
    }

    public void setDtIniVal(Date dtIniVal) {
        this.dtIniVal = dtIniVal;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_VAL_OPER_VOCE_TITOL_FIN")
    public Date getDtValOperVoceTitolFin() {
        return this.dtValOperVoceTitolFin;
    }

    public void setDtValOperVoceTitolFin(Date dtValOperVoceTitolFin) {
        this.dtValOperVoceTitolFin = dtValOperVoceTitolFin;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_VAL_OPER_VOCE_TITOL_INI")
    public Date getDtValOperVoceTitolIni() {
        return this.dtValOperVoceTitolIni;
    }

    public void setDtValOperVoceTitolIni(Date dtValOperVoceTitolIni) {
        this.dtValOperVoceTitolIni = dtValOperVoceTitolIni;
    }

    @Column(name = "FL_USO_CLASSIF", columnDefinition = "char(1)")
    public String getFlUsoClassif() {
        return this.flUsoClassif;
    }

    public void setFlUsoClassif(String flUsoClassif) {
        this.flUsoClassif = flUsoClassif;
    }

    @Id
    @Column(name = "ID_VAL_VOCE_TITOL")
    public BigDecimal getIdValVoceTitol() {
        return this.idValVoceTitol;
    }

    public void setIdValVoceTitol(BigDecimal idValVoceTitol) {
        this.idValVoceTitol = idValVoceTitol;
    }

    @Column(name = "ID_VOCE_TITOL")
    public BigDecimal getIdVoceTitol() {
        return this.idVoceTitol;
    }

    public void setIdVoceTitol(BigDecimal idVoceTitol) {
        this.idVoceTitol = idVoceTitol;
    }

    @Column(name = "NI_ANNI_CONSERV")
    public BigDecimal getNiAnniConserv() {
        return this.niAnniConserv;
    }

    public void setNiAnniConserv(BigDecimal niAnniConserv) {
        this.niAnniConserv = niAnniConserv;
    }

    @Column(name = "TI_OPER_TITOL_FIN")
    public String getTiOperTitolFin() {
        return this.tiOperTitolFin;
    }

    public void setTiOperTitolFin(String tiOperTitolFin) {
        this.tiOperTitolFin = tiOperTitolFin;
    }

    @Column(name = "TI_OPER_TITOL_INI")
    public String getTiOperTitolIni() {
        return this.tiOperTitolIni;
    }

    public void setTiOperTitolIni(String tiOperTitolIni) {
        this.tiOperTitolIni = tiOperTitolIni;
    }

    @Column(name = "TI_OPER_VOCE_TITOL_FIN")
    public String getTiOperVoceTitolFin() {
        return this.tiOperVoceTitolFin;
    }

    public void setTiOperVoceTitolFin(String tiOperVoceTitolFin) {
        this.tiOperVoceTitolFin = tiOperVoceTitolFin;
    }

    @Column(name = "TI_OPER_VOCE_TITOL_INI")
    public String getTiOperVoceTitolIni() {
        return this.tiOperVoceTitolIni;
    }

    public void setTiOperVoceTitolIni(String tiOperVoceTitolIni) {
        this.tiOperVoceTitolIni = tiOperVoceTitolIni;
    }

}
