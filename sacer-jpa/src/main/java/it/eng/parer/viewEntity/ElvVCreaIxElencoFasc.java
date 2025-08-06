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
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.Table;

/**
 * The persistent class for the ELV_V_CREA_IX_ELENCO_FASC database table.
 *
 */
@Entity
@Table(name = "ELV_V_CREA_IX_ELENCO_FASC")

public class ElvVCreaIxElencoFasc implements Serializable {

    private static final long serialVersionUID = 1L;
    private String dlMotivoChius;
    private String dsCriterioRaggr;
    private String dsUrnElenco;
    private String dsUrnIndiceElenco;
    private String dsUrnIndiceElencoNormaliz;
    private BigDecimal idElencoVersFasc;
    private String lisNmTipoFascicolo;
    private String lisNmUserid;
    private BigDecimal niFascVersElenco;
    private String nmAmbiente;
    private String nmCriterioRaggr;
    private String nmEnte;
    private String nmStrut;
    private String ntIndiceElenco;
    private Timestamp tsChiusuraElenco;
    private Timestamp tsVersFin;
    private Timestamp tsVersIni;

    public ElvVCreaIxElencoFasc() {/* Hibernate */
    }

    @Column(name = "DL_MOTIVO_CHIUS")
    public String getDlMotivoChius() {
	return this.dlMotivoChius;
    }

    public void setDlMotivoChius(String dlMotivoChius) {
	this.dlMotivoChius = dlMotivoChius;
    }

    @Column(name = "DS_CRITERIO_RAGGR")
    public String getDsCriterioRaggr() {
	return this.dsCriterioRaggr;
    }

    public void setDsCriterioRaggr(String dsCriterioRaggr) {
	this.dsCriterioRaggr = dsCriterioRaggr;
    }

    @Column(name = "DS_URN_ELENCO")
    public String getDsUrnElenco() {
	return this.dsUrnElenco;
    }

    public void setDsUrnElenco(String dsUrnElenco) {
	this.dsUrnElenco = dsUrnElenco;
    }

    @Column(name = "DS_URN_INDICE_ELENCO")
    public String getDsUrnIndiceElenco() {
	return this.dsUrnIndiceElenco;
    }

    public void setDsUrnIndiceElenco(String dsUrnIndiceElenco) {
	this.dsUrnIndiceElenco = dsUrnIndiceElenco;
    }

    @Column(name = "DS_URN_INDICE_ELENCO_NORMALIZ")
    public String getDsUrnIndiceElencoNormaliz() {
	return this.dsUrnIndiceElencoNormaliz;
    }

    public void setDsUrnIndiceElencoNormaliz(String dsUrnIndiceElencoNormaliz) {
	this.dsUrnIndiceElencoNormaliz = dsUrnIndiceElencoNormaliz;
    }

    @Id
    @Column(name = "ID_ELENCO_VERS_FASC")
    public BigDecimal getIdElencoVersFasc() {
	return this.idElencoVersFasc;
    }

    public void setIdElencoVersFasc(BigDecimal idElencoVersFasc) {
	this.idElencoVersFasc = idElencoVersFasc;
    }

    @Column(name = "LIS_NM_TIPO_FASCICOLO")
    public String getLisNmTipoFascicolo() {
	return this.lisNmTipoFascicolo;
    }

    public void setLisNmTipoFascicolo(String lisNmTipoFascicolo) {
	this.lisNmTipoFascicolo = lisNmTipoFascicolo;
    }

    @Column(name = "LIS_NM_USERID")
    public String getLisNmUserid() {
	return this.lisNmUserid;
    }

    public void setLisNmUserid(String lisNmUserid) {
	this.lisNmUserid = lisNmUserid;
    }

    @Column(name = "NI_FASC_VERS_ELENCO")
    public BigDecimal getNiFascVersElenco() {
	return this.niFascVersElenco;
    }

    public void setNiFascVersElenco(BigDecimal niFascVersElenco) {
	this.niFascVersElenco = niFascVersElenco;
    }

    @Column(name = "NM_AMBIENTE")
    public String getNmAmbiente() {
	return this.nmAmbiente;
    }

    public void setNmAmbiente(String nmAmbiente) {
	this.nmAmbiente = nmAmbiente;
    }

    @Column(name = "NM_CRITERIO_RAGGR")
    public String getNmCriterioRaggr() {
	return this.nmCriterioRaggr;
    }

    public void setNmCriterioRaggr(String nmCriterioRaggr) {
	this.nmCriterioRaggr = nmCriterioRaggr;
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

    @Column(name = "NT_INDICE_ELENCO")
    public String getNtIndiceElenco() {
	return this.ntIndiceElenco;
    }

    public void setNtIndiceElenco(String ntIndiceElenco) {
	this.ntIndiceElenco = ntIndiceElenco;
    }

    @Column(name = "TS_CHIUSURA_ELENCO")
    public Timestamp getTsChiusuraElenco() {
	return this.tsChiusuraElenco;
    }

    public void setTsChiusuraElenco(Timestamp tsChiusuraElenco) {
	this.tsChiusuraElenco = tsChiusuraElenco;
    }

    @Column(name = "TS_VERS_FIN")
    public Timestamp getTsVersFin() {
	return this.tsVersFin;
    }

    public void setTsVersFin(Timestamp tsVersFin) {
	this.tsVersFin = tsVersFin;
    }

    @Column(name = "TS_VERS_INI")
    public Timestamp getTsVersIni() {
	return this.tsVersIni;
    }

    public void setTsVersIni(Timestamp tsVersIni) {
	this.tsVersIni = tsVersIni;
    }

}
