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
 * The persistent class for the ARO_V_LIS_UPD_KO_RISOLTI database table.
 *
 */
@Entity
@Table(name = "ARO_V_LIS_UPD_KO_RISOLTI")
public class AroVLisUpdKoRisolti implements Serializable {

    private static final long serialVersionUID = 1L;
    private String cdControlloWs;
    private String cdErrPrinc;
    private String cdRegistroUnitaDoc;
    private String cdVersioneWs;
    private String dsErrPrinc;
    private BigDecimal idControlloWsPrinc;
    private BigDecimal idErrSacerPrinc;
    private BigDecimal idRegistroUnitaDoc;
    private BigDecimal idSesUpdUnitaDocKo;
    private BigDecimal idTipoDocPrinc;
    private BigDecimal idTipoUnitaDoc;
    private BigDecimal idUpdUnitaDoc;
    private BigDecimal idUserIam;
    private String nmTipoDocPrinc;
    private String nmTipoUnitaDoc;
    private String nmUserid;
    private Date tsIniSes;

    public AroVLisUpdKoRisolti() {/* Hibernate */
    }

    @Column(name = "CD_CONTROLLO_WS")
    public String getCdControlloWs() {
        return this.cdControlloWs;
    }

    public void setCdControlloWs(String cdControlloWs) {
        this.cdControlloWs = cdControlloWs;
    }

    @Column(name = "CD_ERR_PRINC")
    public String getCdErrPrinc() {
        return this.cdErrPrinc;
    }

    public void setCdErrPrinc(String cdErrPrinc) {
        this.cdErrPrinc = cdErrPrinc;
    }

    @Column(name = "CD_REGISTRO_UNITA_DOC")
    public String getCdRegistroUnitaDoc() {
        return this.cdRegistroUnitaDoc;
    }

    public void setCdRegistroUnitaDoc(String cdRegistroUnitaDoc) {
        this.cdRegistroUnitaDoc = cdRegistroUnitaDoc;
    }

    @Column(name = "CD_VERSIONE_WS")
    public String getCdVersioneWs() {
        return this.cdVersioneWs;
    }

    public void setCdVersioneWs(String cdVersioneWs) {
        this.cdVersioneWs = cdVersioneWs;
    }

    @Column(name = "DS_ERR_PRINC")
    public String getDsErrPrinc() {
        return this.dsErrPrinc;
    }

    public void setDsErrPrinc(String dsErrPrinc) {
        this.dsErrPrinc = dsErrPrinc;
    }

    @Column(name = "ID_CONTROLLO_WS_PRINC")
    public BigDecimal getIdControlloWsPrinc() {
        return this.idControlloWsPrinc;
    }

    public void setIdControlloWsPrinc(BigDecimal idControlloWsPrinc) {
        this.idControlloWsPrinc = idControlloWsPrinc;
    }

    @Column(name = "ID_ERR_SACER_PRINC")
    public BigDecimal getIdErrSacerPrinc() {
        return this.idErrSacerPrinc;
    }

    public void setIdErrSacerPrinc(BigDecimal idErrSacerPrinc) {
        this.idErrSacerPrinc = idErrSacerPrinc;
    }

    @Column(name = "ID_REGISTRO_UNITA_DOC")
    public BigDecimal getIdRegistroUnitaDoc() {
        return this.idRegistroUnitaDoc;
    }

    public void setIdRegistroUnitaDoc(BigDecimal idRegistroUnitaDoc) {
        this.idRegistroUnitaDoc = idRegistroUnitaDoc;
    }

    @Id
    @Column(name = "ID_SES_UPD_UNITA_DOC_KO")
    public BigDecimal getIdSesUpdUnitaDocKo() {
        return this.idSesUpdUnitaDocKo;
    }

    public void setIdSesUpdUnitaDocKo(BigDecimal idSesUpdUnitaDocKo) {
        this.idSesUpdUnitaDocKo = idSesUpdUnitaDocKo;
    }

    @Column(name = "ID_TIPO_DOC_PRINC")
    public BigDecimal getIdTipoDocPrinc() {
        return this.idTipoDocPrinc;
    }

    public void setIdTipoDocPrinc(BigDecimal idTipoDocPrinc) {
        this.idTipoDocPrinc = idTipoDocPrinc;
    }

    @Column(name = "ID_TIPO_UNITA_DOC")
    public BigDecimal getIdTipoUnitaDoc() {
        return this.idTipoUnitaDoc;
    }

    public void setIdTipoUnitaDoc(BigDecimal idTipoUnitaDoc) {
        this.idTipoUnitaDoc = idTipoUnitaDoc;
    }

    @Column(name = "ID_UPD_UNITA_DOC")
    public BigDecimal getIdUpdUnitaDoc() {
        return this.idUpdUnitaDoc;
    }

    public void setIdUpdUnitaDoc(BigDecimal idUpdUnitaDoc) {
        this.idUpdUnitaDoc = idUpdUnitaDoc;
    }

    @Column(name = "ID_USER_IAM")
    public BigDecimal getIdUserIam() {
        return this.idUserIam;
    }

    public void setIdUserIam(BigDecimal idUserIam) {
        this.idUserIam = idUserIam;
    }

    @Column(name = "NM_TIPO_DOC_PRINC")
    public String getNmTipoDocPrinc() {
        return this.nmTipoDocPrinc;
    }

    public void setNmTipoDocPrinc(String nmTipoDocPrinc) {
        this.nmTipoDocPrinc = nmTipoDocPrinc;
    }

    @Column(name = "NM_TIPO_UNITA_DOC")
    public String getNmTipoUnitaDoc() {
        return this.nmTipoUnitaDoc;
    }

    public void setNmTipoUnitaDoc(String nmTipoUnitaDoc) {
        this.nmTipoUnitaDoc = nmTipoUnitaDoc;
    }

    @Column(name = "NM_USERID")
    public String getNmUserid() {
        return this.nmUserid;
    }

    public void setNmUserid(String nmUserid) {
        this.nmUserid = nmUserid;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TS_INI_SES")
    public Date getTsIniSes() {
        return this.tsIniSes;
    }

    public void setTsIniSes(Date tsIniSes) {
        this.tsIniSes = tsIniSes;
    }
}
