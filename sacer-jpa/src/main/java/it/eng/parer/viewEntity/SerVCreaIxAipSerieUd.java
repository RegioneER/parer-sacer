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
 * The persistent class for the SER_V_CREA_IX_AIP_SERIE_UD database table.
 *
 */
@Entity
@Table(name = "SER_V_CREA_IX_AIP_SERIE_UD")

public class SerVCreaIxAipSerieUd implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal aaSelezioneUd;
    private String cdFirstUnitaDoc;
    private String cdLastUnitaDoc;
    private String dsAutoreAcqInfo;
    private String dsAutoreAmbConten;
    private String dsAutoreConserv;
    private String dsAutoreCritOrd;
    private String dsNotaAcqInfo;
    private String dsNotaAmbConten;
    private String dsNotaConserv;
    private String dsNotaCritOrd;
    private Date dtFineSelSerie;
    private Date dtFirstUnitaDoc;
    private Date dtInizioSelSerie;
    private Date dtLastUnitaDoc;
    private Date dtNotaAcqInfo;
    private Date dtNotaAmbConten;
    private Date dtNotaConserv;
    private Date dtNotaCritOrd;
    private Date dtScarto;
    private BigDecimal idVerSerie;
    private BigDecimal niAnniConserv;
    private BigDecimal niPeriodoSelSerie;
    private BigDecimal niUnitaDoc;
    private String tiConserv;
    private String tiPeriodoSelSerie;
    private String tiSelezioneUd;

    public SerVCreaIxAipSerieUd() {/* Hibernate */
    }

    @Column(name = "AA_SELEZIONE_UD")
    public BigDecimal getAaSelezioneUd() {
	return this.aaSelezioneUd;
    }

    public void setAaSelezioneUd(BigDecimal aaSelezioneUd) {
	this.aaSelezioneUd = aaSelezioneUd;
    }

    @Column(name = "CD_FIRST_UNITA_DOC")
    public String getCdFirstUnitaDoc() {
	return this.cdFirstUnitaDoc;
    }

    public void setCdFirstUnitaDoc(String cdFirstUnitaDoc) {
	this.cdFirstUnitaDoc = cdFirstUnitaDoc;
    }

    @Column(name = "CD_LAST_UNITA_DOC")
    public String getCdLastUnitaDoc() {
	return this.cdLastUnitaDoc;
    }

    public void setCdLastUnitaDoc(String cdLastUnitaDoc) {
	this.cdLastUnitaDoc = cdLastUnitaDoc;
    }

    @Column(name = "DS_AUTORE_ACQ_INFO")
    public String getDsAutoreAcqInfo() {
	return this.dsAutoreAcqInfo;
    }

    public void setDsAutoreAcqInfo(String dsAutoreAcqInfo) {
	this.dsAutoreAcqInfo = dsAutoreAcqInfo;
    }

    @Column(name = "DS_AUTORE_AMB_CONTEN")
    public String getDsAutoreAmbConten() {
	return this.dsAutoreAmbConten;
    }

    public void setDsAutoreAmbConten(String dsAutoreAmbConten) {
	this.dsAutoreAmbConten = dsAutoreAmbConten;
    }

    @Column(name = "DS_AUTORE_CONSERV")
    public String getDsAutoreConserv() {
	return this.dsAutoreConserv;
    }

    public void setDsAutoreConserv(String dsAutoreConserv) {
	this.dsAutoreConserv = dsAutoreConserv;
    }

    @Column(name = "DS_AUTORE_CRIT_ORD")
    public String getDsAutoreCritOrd() {
	return this.dsAutoreCritOrd;
    }

    public void setDsAutoreCritOrd(String dsAutoreCritOrd) {
	this.dsAutoreCritOrd = dsAutoreCritOrd;
    }

    @Column(name = "DS_NOTA_ACQ_INFO")
    public String getDsNotaAcqInfo() {
	return this.dsNotaAcqInfo;
    }

    public void setDsNotaAcqInfo(String dsNotaAcqInfo) {
	this.dsNotaAcqInfo = dsNotaAcqInfo;
    }

    @Column(name = "DS_NOTA_AMB_CONTEN")
    public String getDsNotaAmbConten() {
	return this.dsNotaAmbConten;
    }

    public void setDsNotaAmbConten(String dsNotaAmbConten) {
	this.dsNotaAmbConten = dsNotaAmbConten;
    }

    @Column(name = "DS_NOTA_CONSERV")
    public String getDsNotaConserv() {
	return this.dsNotaConserv;
    }

    public void setDsNotaConserv(String dsNotaConserv) {
	this.dsNotaConserv = dsNotaConserv;
    }

    @Column(name = "DS_NOTA_CRIT_ORD")
    public String getDsNotaCritOrd() {
	return this.dsNotaCritOrd;
    }

    public void setDsNotaCritOrd(String dsNotaCritOrd) {
	this.dsNotaCritOrd = dsNotaCritOrd;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FINE_SEL_SERIE")
    public Date getDtFineSelSerie() {
	return this.dtFineSelSerie;
    }

    public void setDtFineSelSerie(Date dtFineSelSerie) {
	this.dtFineSelSerie = dtFineSelSerie;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIRST_UNITA_DOC")
    public Date getDtFirstUnitaDoc() {
	return this.dtFirstUnitaDoc;
    }

    public void setDtFirstUnitaDoc(Date dtFirstUnitaDoc) {
	this.dtFirstUnitaDoc = dtFirstUnitaDoc;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INIZIO_SEL_SERIE")
    public Date getDtInizioSelSerie() {
	return this.dtInizioSelSerie;
    }

    public void setDtInizioSelSerie(Date dtInizioSelSerie) {
	this.dtInizioSelSerie = dtInizioSelSerie;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_LAST_UNITA_DOC")
    public Date getDtLastUnitaDoc() {
	return this.dtLastUnitaDoc;
    }

    public void setDtLastUnitaDoc(Date dtLastUnitaDoc) {
	this.dtLastUnitaDoc = dtLastUnitaDoc;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_NOTA_ACQ_INFO")
    public Date getDtNotaAcqInfo() {
	return this.dtNotaAcqInfo;
    }

    public void setDtNotaAcqInfo(Date dtNotaAcqInfo) {
	this.dtNotaAcqInfo = dtNotaAcqInfo;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_NOTA_AMB_CONTEN")
    public Date getDtNotaAmbConten() {
	return this.dtNotaAmbConten;
    }

    public void setDtNotaAmbConten(Date dtNotaAmbConten) {
	this.dtNotaAmbConten = dtNotaAmbConten;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_NOTA_CONSERV")
    public Date getDtNotaConserv() {
	return this.dtNotaConserv;
    }

    public void setDtNotaConserv(Date dtNotaConserv) {
	this.dtNotaConserv = dtNotaConserv;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_NOTA_CRIT_ORD")
    public Date getDtNotaCritOrd() {
	return this.dtNotaCritOrd;
    }

    public void setDtNotaCritOrd(Date dtNotaCritOrd) {
	this.dtNotaCritOrd = dtNotaCritOrd;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_SCARTO")
    public Date getDtScarto() {
	return this.dtScarto;
    }

    public void setDtScarto(Date dtScarto) {
	this.dtScarto = dtScarto;
    }

    @Id
    @Column(name = "ID_VER_SERIE")
    public BigDecimal getIdVerSerie() {
	return this.idVerSerie;
    }

    public void setIdVerSerie(BigDecimal idVerSerie) {
	this.idVerSerie = idVerSerie;
    }

    @Column(name = "NI_ANNI_CONSERV")
    public BigDecimal getNiAnniConserv() {
	return this.niAnniConserv;
    }

    public void setNiAnniConserv(BigDecimal niAnniConserv) {
	this.niAnniConserv = niAnniConserv;
    }

    @Column(name = "NI_PERIODO_SEL_SERIE")
    public BigDecimal getNiPeriodoSelSerie() {
	return this.niPeriodoSelSerie;
    }

    public void setNiPeriodoSelSerie(BigDecimal niPeriodoSelSerie) {
	this.niPeriodoSelSerie = niPeriodoSelSerie;
    }

    @Column(name = "NI_UNITA_DOC")
    public BigDecimal getNiUnitaDoc() {
	return this.niUnitaDoc;
    }

    public void setNiUnitaDoc(BigDecimal niUnitaDoc) {
	this.niUnitaDoc = niUnitaDoc;
    }

    @Column(name = "TI_CONSERV")
    public String getTiConserv() {
	return this.tiConserv;
    }

    public void setTiConserv(String tiConserv) {
	this.tiConserv = tiConserv;
    }

    @Column(name = "TI_PERIODO_SEL_SERIE")
    public String getTiPeriodoSelSerie() {
	return this.tiPeriodoSelSerie;
    }

    public void setTiPeriodoSelSerie(String tiPeriodoSelSerie) {
	this.tiPeriodoSelSerie = tiPeriodoSelSerie;
    }

    @Column(name = "TI_SELEZIONE_UD")
    public String getTiSelezioneUd() {
	return this.tiSelezioneUd;
    }

    public void setTiSelezioneUd(String tiSelezioneUd) {
	this.tiSelezioneUd = tiSelezioneUd;
    }

}
