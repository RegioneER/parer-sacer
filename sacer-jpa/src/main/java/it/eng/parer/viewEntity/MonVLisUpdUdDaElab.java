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

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the MON_V_LIS_UPD_UD_DA_ELAB database table.
 *
 */
@Entity
@Table(name = "MON_V_LIS_UPD_UD_DA_ELAB")

public class MonVLisUpdUdDaElab implements MonVLisUpdUdInterface {

    private static final long serialVersionUID = 1L;
    private BigDecimal aaKeyUnitaDoc;
    private String cdKeyUnitaDoc;
    private String cdRegistroKeyUnitaDoc;
    private String dsEnteStrut;
    private String dsTsIniSes;
    private String dsUnitaDoc;
    private String flForzaUpd;
    private String flSesUpdKoRisolti;
    private BigDecimal idAmbiente;
    private BigDecimal idEnte;
    private BigDecimal idRegistroUnitaDoc;
    private BigDecimal idStrut;
    private BigDecimal idTipoDocPrinc;
    private BigDecimal idTipoUnitaDoc;
    private BigDecimal idUnitaDoc;
    private BigDecimal idUpdUnitaDoc;
    private BigDecimal idUserIamCor;
    private String nmAmbiente;
    private String nmEnte;
    private String nmStrut;
    private String nmTipoDocPrinc;
    private String nmTipoUnitaDoc;
    private String ntUpd;
    private BigDecimal pgUpdUnitaDoc;
    private String tiStatoUpdElencoVers;
    private Date tsIniSes;

    public MonVLisUpdUdDaElab() {/* Hibernate */
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    @Override
    public BigDecimal getAaKeyUnitaDoc() {
        return this.aaKeyUnitaDoc;
    }

    @Override
    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    @Column(name = "CD_KEY_UNITA_DOC")
    @Override
    public String getCdKeyUnitaDoc() {
        return this.cdKeyUnitaDoc;
    }

    @Override
    public void setCdKeyUnitaDoc(String cdKeyUnitaDoc) {
        this.cdKeyUnitaDoc = cdKeyUnitaDoc;
    }

    @Column(name = "CD_REGISTRO_KEY_UNITA_DOC")
    @Override
    public String getCdRegistroKeyUnitaDoc() {
        return this.cdRegistroKeyUnitaDoc;
    }

    @Override
    public void setCdRegistroKeyUnitaDoc(String cdRegistroKeyUnitaDoc) {
        this.cdRegistroKeyUnitaDoc = cdRegistroKeyUnitaDoc;
    }

    @Column(name = "DS_ENTE_STRUT")
    @Override
    public String getDsEnteStrut() {
        return this.dsEnteStrut;
    }

    @Override
    public void setDsEnteStrut(String dsEnteStrut) {
        this.dsEnteStrut = dsEnteStrut;
    }

    @Column(name = "DS_TS_INI_SES")
    @Override
    public String getDsTsIniSes() {
        return this.dsTsIniSes;
    }

    @Override
    public void setDsTsIniSes(String dsTsIniSes) {
        this.dsTsIniSes = dsTsIniSes;
    }

    @Column(name = "DS_UNITA_DOC")
    @Override
    public String getDsUnitaDoc() {
        return this.dsUnitaDoc;
    }

    @Override
    public void setDsUnitaDoc(String dsUnitaDoc) {
        this.dsUnitaDoc = dsUnitaDoc;
    }

    @Column(name = "FL_FORZA_UPD", columnDefinition = "char(1)")
    @Override
    public String getFlForzaUpd() {
        return this.flForzaUpd;
    }

    @Override
    public void setFlForzaUpd(String flForzaUpd) {
        this.flForzaUpd = flForzaUpd;
    }

    @Column(name = "FL_SES_UPD_KO_RISOLTI", columnDefinition = "char(1)")
    @Override
    public String getFlSesUpdKoRisolti() {
        return this.flSesUpdKoRisolti;
    }

    @Override
    public void setFlSesUpdKoRisolti(String flSesUpdKoRisolti) {
        this.flSesUpdKoRisolti = flSesUpdKoRisolti;
    }

    @Column(name = "ID_AMBIENTE")
    @Override
    public BigDecimal getIdAmbiente() {
        return this.idAmbiente;
    }

    @Override
    public void setIdAmbiente(BigDecimal idAmbiente) {
        this.idAmbiente = idAmbiente;
    }

    @Column(name = "ID_ENTE")
    @Override
    public BigDecimal getIdEnte() {
        return this.idEnte;
    }

    @Override
    public void setIdEnte(BigDecimal idEnte) {
        this.idEnte = idEnte;
    }

    @Column(name = "ID_REGISTRO_UNITA_DOC")
    @Override
    public BigDecimal getIdRegistroUnitaDoc() {
        return this.idRegistroUnitaDoc;
    }

    @Override
    public void setIdRegistroUnitaDoc(BigDecimal idRegistroUnitaDoc) {
        this.idRegistroUnitaDoc = idRegistroUnitaDoc;
    }

    @Column(name = "ID_STRUT")
    @Override
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    @Override
    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    @Column(name = "ID_TIPO_DOC_PRINC")
    @Override
    public BigDecimal getIdTipoDocPrinc() {
        return this.idTipoDocPrinc;
    }

    @Override
    public void setIdTipoDocPrinc(BigDecimal idTipoDocPrinc) {
        this.idTipoDocPrinc = idTipoDocPrinc;
    }

    @Column(name = "ID_TIPO_UNITA_DOC")
    @Override
    public BigDecimal getIdTipoUnitaDoc() {
        return this.idTipoUnitaDoc;
    }

    @Override
    public void setIdTipoUnitaDoc(BigDecimal idTipoUnitaDoc) {
        this.idTipoUnitaDoc = idTipoUnitaDoc;
    }

    @Column(name = "ID_UNITA_DOC")
    @Override
    public BigDecimal getIdUnitaDoc() {
        return this.idUnitaDoc;
    }

    @Override
    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
        this.idUnitaDoc = idUnitaDoc;
    }

    @Id
    @Column(name = "ID_UPD_UNITA_DOC")
    @Override
    public BigDecimal getIdUpdUnitaDoc() {
        return this.idUpdUnitaDoc;
    }

    @Override
    public void setIdUpdUnitaDoc(BigDecimal idUpdUnitaDoc) {
        this.idUpdUnitaDoc = idUpdUnitaDoc;
    }

    @Column(name = "ID_USER_IAM_COR")
    @Override
    public BigDecimal getIdUserIamCor() {
        return this.idUserIamCor;
    }

    @Override
    public void setIdUserIamCor(BigDecimal idUserIamCor) {
        this.idUserIamCor = idUserIamCor;
    }

    @Column(name = "NM_AMBIENTE")
    @Override
    public String getNmAmbiente() {
        return this.nmAmbiente;
    }

    @Override
    public void setNmAmbiente(String nmAmbiente) {
        this.nmAmbiente = nmAmbiente;
    }

    @Column(name = "NM_ENTE")
    @Override
    public String getNmEnte() {
        return this.nmEnte;
    }

    @Override
    public void setNmEnte(String nmEnte) {
        this.nmEnte = nmEnte;
    }

    @Column(name = "NM_STRUT")
    @Override
    public String getNmStrut() {
        return this.nmStrut;
    }

    @Override
    public void setNmStrut(String nmStrut) {
        this.nmStrut = nmStrut;
    }

    @Column(name = "NM_TIPO_DOC_PRINC")
    @Override
    public String getNmTipoDocPrinc() {
        return this.nmTipoDocPrinc;
    }

    @Override
    public void setNmTipoDocPrinc(String nmTipoDocPrinc) {
        this.nmTipoDocPrinc = nmTipoDocPrinc;
    }

    @Column(name = "NM_TIPO_UNITA_DOC")
    @Override
    public String getNmTipoUnitaDoc() {
        return this.nmTipoUnitaDoc;
    }

    @Override
    public void setNmTipoUnitaDoc(String nmTipoUnitaDoc) {
        this.nmTipoUnitaDoc = nmTipoUnitaDoc;
    }

    @Column(name = "NT_UPD")
    @Override
    public String getNtUpd() {
        return this.ntUpd;
    }

    @Override
    public void setNtUpd(String ntUpd) {
        this.ntUpd = ntUpd;
    }

    @Column(name = "PG_UPD_UNITA_DOC")
    @Override
    public BigDecimal getPgUpdUnitaDoc() {
        return this.pgUpdUnitaDoc;
    }

    @Override
    public void setPgUpdUnitaDoc(BigDecimal pgUpdUnitaDoc) {
        this.pgUpdUnitaDoc = pgUpdUnitaDoc;
    }

    @Column(name = "TI_STATO_UPD_ELENCO_VERS")
    @Override
    public String getTiStatoUpdElencoVers() {
        return this.tiStatoUpdElencoVers;
    }

    @Override
    public void setTiStatoUpdElencoVers(String tiStatoUpdElencoVers) {
        this.tiStatoUpdElencoVers = tiStatoUpdElencoVers;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TS_INI_SES")
    @Override
    public Date getTsIniSes() {
        return this.tsIniSes;
    }

    @Override
    public void setTsIniSes(Date tsIniSes) {
        this.tsIniSes = tsIniSes;
    }

}
