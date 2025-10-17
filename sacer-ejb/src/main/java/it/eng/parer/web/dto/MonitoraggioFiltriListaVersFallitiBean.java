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

package it.eng.parer.web.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

/**
 *
 * @author Gilioli_P
 */
public class MonitoraggioFiltriListaVersFallitiBean implements java.io.Serializable {

    private static final long serialVersionUID = -8209594152276959571L;

    private BigDecimal idAmbiente;
    private BigDecimal idEnte;
    private BigDecimal idStrut;
    private BigDecimal idTipoUnitaDoc;
    private BigDecimal idUserIam;
    private String periodoVers;
    private Date giornoVersDa;
    private Date giornoVersA;
    private BigDecimal oreVersDa;
    private BigDecimal minutiVersDa;
    private BigDecimal oreVersA;
    private BigDecimal minutiVersA;
    private Date giornoVersDaValidato;
    private Date giornoVersAValidato;
    private String tipoSes;
    private String risolto;
    private String verificato;
    private String nonRisolubile;
    private String classeErrore;
    private String sottoClasseErrore;
    private String codiceErrore;
    private Set<String> registro;
    private BigDecimal anno;
    private String numero;
    private Set<String> registro_range;
    private BigDecimal anno_range_da;
    private BigDecimal anno_range_a;
    private String numero_range_da;
    private String numero_range_a;
    private String tipiUD;

    public BigDecimal getIdAmbiente() {
	return idAmbiente;
    }

    public void setIdAmbiente(BigDecimal idAmbiente) {
	this.idAmbiente = idAmbiente;
    }

    public BigDecimal getIdEnte() {
	return idEnte;
    }

    public void setIdEnte(BigDecimal idEnte) {
	this.idEnte = idEnte;
    }

    public BigDecimal getIdStrut() {
	return idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
	this.idStrut = idStrut;
    }

    public BigDecimal getIdTipoUnitaDoc() {
	return idTipoUnitaDoc;
    }

    public void setIdTipoUnitaDoc(BigDecimal idTipoUnitaDoc) {
	this.idTipoUnitaDoc = idTipoUnitaDoc;
    }

    public String getPeriodoVers() {
	return periodoVers;
    }

    public void setPeriodoVers(String periodoVers) {
	this.periodoVers = periodoVers;
    }

    public Date getGiornoVersDa() {
	return giornoVersDa;
    }

    public void setGiornoVersDa(Date giornoVersDa) {
	this.giornoVersDa = giornoVersDa;
    }

    public Date getGiornoVersA() {
	return giornoVersA;
    }

    public void setGiornoVersA(Date giornoVersA) {
	this.giornoVersA = giornoVersA;
    }

    public BigDecimal getOreVersDa() {
	return oreVersDa;
    }

    public void setOreVersDa(BigDecimal oreVersDa) {
	this.oreVersDa = oreVersDa;
    }

    public BigDecimal getMinutiVersDa() {
	return minutiVersDa;
    }

    public void setMinutiVersDa(BigDecimal minutiVersDa) {
	this.minutiVersDa = minutiVersDa;
    }

    public BigDecimal getOreVersA() {
	return oreVersA;
    }

    public void setOreVersA(BigDecimal oreVersA) {
	this.oreVersA = oreVersA;
    }

    public BigDecimal getMinutiVersA() {
	return minutiVersA;
    }

    public void setMinutiVersA(BigDecimal minutiVersA) {
	this.minutiVersA = minutiVersA;
    }

    public Date getGiornoVersDaValidato() {
	return giornoVersDaValidato;
    }

    public void setGiornoVersDaValidato(Date giornoVersDaValidato) {
	this.giornoVersDaValidato = giornoVersDaValidato;
    }

    public Date getGiornoVersAValidato() {
	return giornoVersAValidato;
    }

    public void setGiornoVersAValidato(Date giornoVersAValidato) {
	this.giornoVersAValidato = giornoVersAValidato;
    }

    public String getTipoSes() {
	return tipoSes;
    }

    public void setTipoSes(String tipoSes) {
	this.tipoSes = tipoSes;
    }

    public String getRisolto() {
	return risolto;
    }

    public void setRisolto(String risolto) {
	this.risolto = risolto;
    }

    public String getVerificato() {
	return verificato;
    }

    public void setVerificato(String verificato) {
	this.verificato = verificato;
    }

    public String getNonRisolubile() {
	return nonRisolubile;
    }

    public void setNonRisolubile(String nonRisolubile) {
	this.nonRisolubile = nonRisolubile;
    }

    public String getCodiceErrore() {
	return codiceErrore;
    }

    public void setCodiceErrore(String codiceErrore) {
	this.codiceErrore = codiceErrore;
    }

    public String getClasseErrore() {
	return classeErrore;
    }

    public void setClasseErrore(String classeErrore) {
	this.classeErrore = classeErrore;
    }

    public String getSottoClasseErrore() {
	return sottoClasseErrore;
    }

    public void setSottoClasseErrore(String sottoClasseErrore) {
	this.sottoClasseErrore = sottoClasseErrore;
    }

    public Set<String> getRegistro() {
	return registro;
    }

    public void setRegistro(Set<String> registro) {
	this.registro = registro;
    }

    public BigDecimal getAnno() {
	return anno;
    }

    public void setAnno(BigDecimal anno) {
	this.anno = anno;
    }

    public String getNumero() {
	return numero;
    }

    public void setNumero(String numero) {
	this.numero = numero;
    }

    public Set<String> getRegistro_range() {
	return registro_range;
    }

    public void setRegistro_range(Set<String> registro_range) {
	this.registro_range = registro_range;
    }

    public BigDecimal getAnno_range_da() {
	return anno_range_da;
    }

    public void setAnno_range_da(BigDecimal anno_range_da) {
	this.anno_range_da = anno_range_da;
    }

    public BigDecimal getAnno_range_a() {
	return anno_range_a;
    }

    public void setAnno_range_a(BigDecimal anno_range_a) {
	this.anno_range_a = anno_range_a;
    }

    public String getNumero_range_da() {
	return numero_range_da;
    }

    public void setNumero_range_da(String numero_range_da) {
	this.numero_range_da = numero_range_da;
    }

    public String getNumero_range_a() {
	return numero_range_a;
    }

    public void setNumero_range_a(String numero_range_a) {
	this.numero_range_a = numero_range_a;
    }

    public String getTipiUD() {
	return tipiUD;
    }

    public void setTipiUD(String tipiUD) {
	this.tipiUD = tipiUD;
    }

    public BigDecimal getIdUserIam() {
	return idUserIam;
    }

    public void setIdUserIam(BigDecimal idUserIam) {
	this.idUserIam = idUserIam;
    }
}
