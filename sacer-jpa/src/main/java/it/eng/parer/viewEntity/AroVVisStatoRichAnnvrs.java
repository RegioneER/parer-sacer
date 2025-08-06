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
 * The persistent class for the ARO_V_VIS_STATO_RICH_ANNVRS database table.
 *
 */
@Entity
@Table(name = "ARO_V_VIS_STATO_RICH_ANNVRS")

public class AroVVisStatoRichAnnvrs implements Serializable {
    private static final long serialVersionUID = 1L;
    private String cdRichAnnulVers;
    private String dsNotaRichAnnulVers;
    private String dsRichAnnulVers;
    private Date dtRegStatoRichAnnulVers;
    private BigDecimal idAmbiente;
    private BigDecimal idEnte;
    private BigDecimal idRichAnnulVers;
    private BigDecimal idStatoRichAnnulVers;
    private BigDecimal idStrut;
    private String nmAmbiente;
    private String nmEnte;
    private String nmStrut;
    private String nmUseridStato;
    private BigDecimal pgStatoRichAnnulVers;
    private String tiStatoRichAnnulVers;
    private String tiStatoRichAnnulVersCor;

    public AroVVisStatoRichAnnvrs() {/* Hibernate */
    }

    @Column(name = "CD_RICH_ANNUL_VERS")
    public String getCdRichAnnulVers() {
	return this.cdRichAnnulVers;
    }

    public void setCdRichAnnulVers(String cdRichAnnulVers) {
	this.cdRichAnnulVers = cdRichAnnulVers;
    }

    @Column(name = "DS_NOTA_RICH_ANNUL_VERS")
    public String getDsNotaRichAnnulVers() {
	return this.dsNotaRichAnnulVers;
    }

    public void setDsNotaRichAnnulVers(String dsNotaRichAnnulVers) {
	this.dsNotaRichAnnulVers = dsNotaRichAnnulVers;
    }

    @Column(name = "DS_RICH_ANNUL_VERS")
    public String getDsRichAnnulVers() {
	return this.dsRichAnnulVers;
    }

    public void setDsRichAnnulVers(String dsRichAnnulVers) {
	this.dsRichAnnulVers = dsRichAnnulVers;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_REG_STATO_RICH_ANNUL_VERS")
    public Date getDtRegStatoRichAnnulVers() {
	return this.dtRegStatoRichAnnulVers;
    }

    public void setDtRegStatoRichAnnulVers(Date dtRegStatoRichAnnulVers) {
	this.dtRegStatoRichAnnulVers = dtRegStatoRichAnnulVers;
    }

    @Column(name = "ID_AMBIENTE")
    public BigDecimal getIdAmbiente() {
	return this.idAmbiente;
    }

    public void setIdAmbiente(BigDecimal idAmbiente) {
	this.idAmbiente = idAmbiente;
    }

    @Column(name = "ID_ENTE")
    public BigDecimal getIdEnte() {
	return this.idEnte;
    }

    public void setIdEnte(BigDecimal idEnte) {
	this.idEnte = idEnte;
    }

    @Column(name = "ID_RICH_ANNUL_VERS")
    public BigDecimal getIdRichAnnulVers() {
	return this.idRichAnnulVers;
    }

    public void setIdRichAnnulVers(BigDecimal idRichAnnulVers) {
	this.idRichAnnulVers = idRichAnnulVers;
    }

    @Id
    @Column(name = "ID_STATO_RICH_ANNUL_VERS")
    public BigDecimal getIdStatoRichAnnulVers() {
	return this.idStatoRichAnnulVers;
    }

    public void setIdStatoRichAnnulVers(BigDecimal idStatoRichAnnulVers) {
	this.idStatoRichAnnulVers = idStatoRichAnnulVers;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
	return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
	this.idStrut = idStrut;
    }

    @Column(name = "NM_AMBIENTE")
    public String getNmAmbiente() {
	return this.nmAmbiente;
    }

    public void setNmAmbiente(String nmAmbiente) {
	this.nmAmbiente = nmAmbiente;
    }

    @Column(name = "NM_ENTE")
    public String getNmEnte() {
	return this.nmEnte;
    }

    public void setNmEnte(String nmEnte) {
	this.nmEnte = nmEnte;
    }

    @Column(name = "NM_STRUT")
    public String getNmStrut() {
	return this.nmStrut;
    }

    public void setNmStrut(String nmStrut) {
	this.nmStrut = nmStrut;
    }

    @Column(name = "NM_USERID_STATO")
    public String getNmUseridStato() {
	return this.nmUseridStato;
    }

    public void setNmUseridStato(String nmUseridStato) {
	this.nmUseridStato = nmUseridStato;
    }

    @Column(name = "PG_STATO_RICH_ANNUL_VERS")
    public BigDecimal getPgStatoRichAnnulVers() {
	return this.pgStatoRichAnnulVers;
    }

    public void setPgStatoRichAnnulVers(BigDecimal pgStatoRichAnnulVers) {
	this.pgStatoRichAnnulVers = pgStatoRichAnnulVers;
    }

    @Column(name = "TI_STATO_RICH_ANNUL_VERS")
    public String getTiStatoRichAnnulVers() {
	return this.tiStatoRichAnnulVers;
    }

    public void setTiStatoRichAnnulVers(String tiStatoRichAnnulVers) {
	this.tiStatoRichAnnulVers = tiStatoRichAnnulVers;
    }

    @Column(name = "TI_STATO_RICH_ANNUL_VERS_COR")
    public String getTiStatoRichAnnulVersCor() {
	return this.tiStatoRichAnnulVersCor;
    }

    public void setTiStatoRichAnnulVersCor(String tiStatoRichAnnulVersCor) {
	this.tiStatoRichAnnulVersCor = tiStatoRichAnnulVersCor;
    }

}
