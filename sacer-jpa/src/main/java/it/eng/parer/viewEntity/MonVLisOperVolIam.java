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
 * The persistent class for the MON_V_LIS_OPER_VOL_IAM database table.
 * 
 */
@Entity
@Table(name = "MON_V_LIS_OPER_VOL_IAM")
public class MonVLisOperVolIam implements Serializable {
    private static final long serialVersionUID = 1L;
    private String cdDoc;
    private String dlMotivoChius;
    private Date dtOper;
    private Date dtScadChius;
    private BigDecimal idAmbiente;
    private BigDecimal idDoc;
    private BigDecimal idEnte;
    private BigDecimal idLogJob;
    private BigDecimal idLogOper;
    private BigDecimal idStrut;
    private BigDecimal idUnitaDoc;
    private BigDecimal idVolumeConserv;
    private BigDecimal niMaxComp;
    private BigDecimal niMaxUnitaDoc;
    private String nmAmbiente;
    private String nmCognomeFirmatario;
    private String nmEnte;
    private String nmNomeFirmatario;
    private String nmStrut;
    private String nmVolumeConserv;
    private String tiModOper;
    private String tiOper;

    public MonVLisOperVolIam() {/* Hibernate */
    }

    @Column(name = "CD_DOC")
    public String getCdDoc() {
        return this.cdDoc;
    }

    public void setCdDoc(String cdDoc) {
        this.cdDoc = cdDoc;
    }

    @Column(name = "DL_MOTIVO_CHIUS")
    public String getDlMotivoChius() {
        return this.dlMotivoChius;
    }

    public void setDlMotivoChius(String dlMotivoChius) {
        this.dlMotivoChius = dlMotivoChius;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_OPER")
    public Date getDtOper() {
        return this.dtOper;
    }

    public void setDtOper(Date dtOper) {
        this.dtOper = dtOper;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_SCAD_CHIUS")
    public Date getDtScadChius() {
        return this.dtScadChius;
    }

    public void setDtScadChius(Date dtScadChius) {
        this.dtScadChius = dtScadChius;
    }

    @Column(name = "ID_AMBIENTE")
    public BigDecimal getIdAmbiente() {
        return this.idAmbiente;
    }

    public void setIdAmbiente(BigDecimal idAmbiente) {
        this.idAmbiente = idAmbiente;
    }

    @Column(name = "ID_DOC")
    public BigDecimal getIdDoc() {
        return this.idDoc;
    }

    public void setIdDoc(BigDecimal idDoc) {
        this.idDoc = idDoc;
    }

    @Column(name = "ID_ENTE")
    public BigDecimal getIdEnte() {
        return this.idEnte;
    }

    public void setIdEnte(BigDecimal idEnte) {
        this.idEnte = idEnte;
    }

    @Column(name = "ID_LOG_JOB")
    public BigDecimal getIdLogJob() {
        return this.idLogJob;
    }

    public void setIdLogJob(BigDecimal idLogJob) {
        this.idLogJob = idLogJob;
    }

    @Id
    @Column(name = "ID_LOG_OPER")
    public BigDecimal getIdLogOper() {
        return this.idLogOper;
    }

    public void setIdLogOper(BigDecimal idLogOper) {
        this.idLogOper = idLogOper;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    @Column(name = "ID_UNITA_DOC")
    public BigDecimal getIdUnitaDoc() {
        return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
        this.idUnitaDoc = idUnitaDoc;
    }

    @Column(name = "ID_VOLUME_CONSERV")
    public BigDecimal getIdVolumeConserv() {
        return this.idVolumeConserv;
    }

    public void setIdVolumeConserv(BigDecimal idVolumeConserv) {
        this.idVolumeConserv = idVolumeConserv;
    }

    @Column(name = "NI_MAX_COMP")
    public BigDecimal getNiMaxComp() {
        return this.niMaxComp;
    }

    public void setNiMaxComp(BigDecimal niMaxComp) {
        this.niMaxComp = niMaxComp;
    }

    @Column(name = "NI_MAX_UNITA_DOC")
    public BigDecimal getNiMaxUnitaDoc() {
        return this.niMaxUnitaDoc;
    }

    public void setNiMaxUnitaDoc(BigDecimal niMaxUnitaDoc) {
        this.niMaxUnitaDoc = niMaxUnitaDoc;
    }

    @Column(name = "NM_AMBIENTE")
    public String getNmAmbiente() {
        return this.nmAmbiente;
    }

    public void setNmAmbiente(String nmAmbiente) {
        this.nmAmbiente = nmAmbiente;
    }

    @Column(name = "NM_COGNOME_FIRMATARIO")
    public String getNmCognomeFirmatario() {
        return this.nmCognomeFirmatario;
    }

    public void setNmCognomeFirmatario(String nmCognomeFirmatario) {
        this.nmCognomeFirmatario = nmCognomeFirmatario;
    }

    @Column(name = "NM_ENTE")
    public String getNmEnte() {
        return this.nmEnte;
    }

    public void setNmEnte(String nmEnte) {
        this.nmEnte = nmEnte;
    }

    @Column(name = "NM_NOME_FIRMATARIO")
    public String getNmNomeFirmatario() {
        return this.nmNomeFirmatario;
    }

    public void setNmNomeFirmatario(String nmNomeFirmatario) {
        this.nmNomeFirmatario = nmNomeFirmatario;
    }

    @Column(name = "NM_STRUT")
    public String getNmStrut() {
        return this.nmStrut;
    }

    public void setNmStrut(String nmStrut) {
        this.nmStrut = nmStrut;
    }

    @Column(name = "NM_VOLUME_CONSERV")
    public String getNmVolumeConserv() {
        return this.nmVolumeConserv;
    }

    public void setNmVolumeConserv(String nmVolumeConserv) {
        this.nmVolumeConserv = nmVolumeConserv;
    }

    @Column(name = "TI_MOD_OPER")
    public String getTiModOper() {
        return this.tiModOper;
    }

    public void setTiModOper(String tiModOper) {
        this.tiModOper = tiModOper;
    }

    @Column(name = "TI_OPER")
    public String getTiOper() {
        return this.tiOper;
    }

    public void setTiOper(String tiOper) {
        this.tiOper = tiOper;
    }

}
