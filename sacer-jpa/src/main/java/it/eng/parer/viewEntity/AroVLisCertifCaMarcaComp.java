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
 * The persistent class for the ARO_V_LIS_CERTIF_CA_MARCA_COMP database table.
 *
 */
@Entity
@Table(name = "ARO_V_LIS_CERTIF_CA_MARCA_COMP")
public class AroVLisCertifCaMarcaComp implements Serializable {
    private static final long serialVersionUID = 1L;

    private String dlDnIssuerCertifCa;
    private String dlDnIssuerCrl;
    private Date dtFinValCertifCa;
    private Date dtIniValCertifCa;
    private Date dtScadCrl;
    private BigDecimal idContrMarcaComp;
    private BigDecimal idMarcaComp;
    private BigDecimal idUsoCertifCaContrMarca;
    private String dsSerialCertifCa;
    private String dsSerialCertifCrl;
    private String dsSerialCrl;
    private BigDecimal pgCertifCa;
    private String dlDnIssuerCertifOcsp;
    private String dsSerialCertifOcsp;
    private Date dtIniValCertifOcsp;
    private Date dtFinValCertifOcsp;
    private String tiContr;

    public AroVLisCertifCaMarcaComp() {/* Hibernate */
    }

    @Column(name = "DL_DN_ISSUER_CERTIF_CA")
    public String getDlDnIssuerCertifCa() {
        return this.dlDnIssuerCertifCa;
    }

    public void setDlDnIssuerCertifCa(String dlDnIssuerCertifCa) {
        this.dlDnIssuerCertifCa = dlDnIssuerCertifCa;
    }

    @Column(name = "DL_DN_ISSUER_CRL")
    public String getDlDnIssuerCrl() {
        return this.dlDnIssuerCrl;
    }

    public void setDlDnIssuerCrl(String dlDnIssuerCrl) {
        this.dlDnIssuerCrl = dlDnIssuerCrl;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIN_VAL_CERTIF_CA")
    public Date getDtFinValCertifCa() {
        return this.dtFinValCertifCa;
    }

    public void setDtFinValCertifCa(Date dtFinValCertifCa) {
        this.dtFinValCertifCa = dtFinValCertifCa;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INI_VAL_CERTIF_CA")
    public Date getDtIniValCertifCa() {
        return this.dtIniValCertifCa;
    }

    public void setDtIniValCertifCa(Date dtIniValCertifCa) {
        this.dtIniValCertifCa = dtIniValCertifCa;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_SCAD_CRL")
    public Date getDtScadCrl() {
        return this.dtScadCrl;
    }

    public void setDtScadCrl(Date dtScadCrl) {
        this.dtScadCrl = dtScadCrl;
    }

    @Column(name = "ID_CONTR_MARCA_COMP")
    public BigDecimal getIdContrMarcaComp() {
        return this.idContrMarcaComp;
    }

    public void setIdContrMarcaComp(BigDecimal idContrMarcaComp) {
        this.idContrMarcaComp = idContrMarcaComp;
    }

    @Column(name = "ID_MARCA_COMP")
    public BigDecimal getIdMarcaComp() {
        return this.idMarcaComp;
    }

    public void setIdMarcaComp(BigDecimal idMarcaComp) {
        this.idMarcaComp = idMarcaComp;
    }

    @Id
    @Column(name = "ID_USO_CERTIF_CA_CONTR_MARCA")
    public BigDecimal getIdUsoCertifCaContrMarca() {
        return this.idUsoCertifCaContrMarca;
    }

    public void setIdUsoCertifCaContrMarca(BigDecimal idUsoCertifCaContrMarca) {
        this.idUsoCertifCaContrMarca = idUsoCertifCaContrMarca;
    }

    @Column(name = "DS_SERIAL_CERTIF_CA")
    public String getDsSerialCertifCa() {
        return this.dsSerialCertifCa;
    }

    public void setDsSerialCertifCa(String dsSerialCertifCa) {
        this.dsSerialCertifCa = dsSerialCertifCa;
    }

    @Column(name = "DS_SERIAL_CERTIF_CRL")
    public String getDsSerialCertifCrl() {
        return this.dsSerialCertifCrl;
    }

    public void setDsSerialCertifCrl(String dsSerialCertifCrl) {
        this.dsSerialCertifCrl = dsSerialCertifCrl;
    }

    @Column(name = "DS_SERIAL_CRL")
    public String getDsSerialCrl() {
        return this.dsSerialCrl;
    }

    public void setDsSerialCrl(String dsSerialCrl) {
        this.dsSerialCrl = dsSerialCrl;
    }

    @Column(name = "PG_CERTIF_CA")
    public BigDecimal getPgCertifCa() {
        return this.pgCertifCa;
    }

    public void setPgCertifCa(BigDecimal pgCertifCa) {
        this.pgCertifCa = pgCertifCa;
    }

    @Column(name = "DL_DN_ISSUER_CERTIF_OCSP")
    public String getDlDnIssuerCertifOcsp() {
        return dlDnIssuerCertifOcsp;
    }

    public void setDlDnIssuerCertifOcsp(String dlDnIssuerCertifOcsp) {
        this.dlDnIssuerCertifOcsp = dlDnIssuerCertifOcsp;
    }

    @Column(name = "DS_SERIAL_CERTIF_OCSP")
    public String getDsSerialCertifOcsp() {
        return dsSerialCertifOcsp;
    }

    public void setDsSerialCertifOcsp(String dsSerialCertifOcsp) {
        this.dsSerialCertifOcsp = dsSerialCertifOcsp;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INI_VAL_CERTIF_OCSP")
    public Date getDtIniValCertifOcsp() {
        return dtIniValCertifOcsp;
    }

    public void setDtIniValCertifOcsp(Date dtIniValCertifOcsp) {
        this.dtIniValCertifOcsp = dtIniValCertifOcsp;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIN_VAL_CERTIF_OCSP")
    public Date getDtFinValCertifOcsp() {
        return dtFinValCertifOcsp;
    }

    public void setDtFinValCertifOcsp(Date dtFinValCertifOcsp) {
        this.dtFinValCertifOcsp = dtFinValCertifOcsp;
    }

    @Column(name = "TI_CONTR")
    public String getTiContr() {
        return tiContr;
    }

    public void setTiContr(String tiContr) {
        this.tiContr = tiContr;
    }

}
