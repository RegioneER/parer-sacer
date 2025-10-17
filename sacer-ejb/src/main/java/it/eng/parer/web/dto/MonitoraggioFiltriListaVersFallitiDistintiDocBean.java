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

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

/**
 *
 * @author Gilioli_P
 */
public class MonitoraggioFiltriListaVersFallitiDistintiDocBean implements java.io.Serializable {

    private static final long serialVersionUID = -3565696620448136827L;

    private BigDecimal idAmbiente;
    private BigDecimal idEnte;
    private BigDecimal idStrut;
    private BigDecimal idUserIam;
    private String tipoLista;
    private String flVerificato;
    private String flNonRisolub;
    private Set<String> registro;
    private BigDecimal anno;
    private String numero;
    private BigDecimal anno_range_da;
    private BigDecimal anno_range_a;
    private String numero_range_da;
    private String numero_range_a;
    private String classeErrore;
    private String sottoClasseErrore;
    private String codiceErrore;
    private Date giornoFirstVersDa;
    private Date giornoFirstVersA;
    private BigDecimal oreFirstVersDa;
    private BigDecimal minutiFirstVersDa;
    private BigDecimal oreFirstVersA;
    private BigDecimal minutiFirstVersA;
    private Date giornoFirstVersDaValidato;
    private Date giornoFirstVersAValidato;

    private Date giornoLastVersDa;
    private Date giornoLastVersA;
    private BigDecimal oreLastVersDa;
    private BigDecimal minutiLastVersDa;
    private BigDecimal oreLastVersA;
    private BigDecimal minutiLastVersA;
    private Date giornoLastVersDaValidato;
    private Date giornoLastVersAValidato;

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

    public String getTipoLista() {
	return tipoLista;
    }

    public void setTipoLista(String tipoLista) {
	this.tipoLista = tipoLista;
    }

    public String getFlVerificato() {
	return flVerificato;
    }

    public void setFlVerificato(String flVerificato) {
	this.flVerificato = flVerificato;
    }

    public String getFlNonRisolub() {
	return flNonRisolub;
    }

    public void setFlNonRisolub(String flNonRisolub) {
	this.flNonRisolub = flNonRisolub;
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

    public BigDecimal getIdUserIam() {
	return idUserIam;
    }

    public void setIdUserIam(BigDecimal idUserIam) {
	this.idUserIam = idUserIam;
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

    public String getCodiceErrore() {
	return codiceErrore;
    }

    public void setCodiceErrore(String codiceErrore) {
	this.codiceErrore = codiceErrore;
    }

    public Date getGiornoFirstVersDa() {
	return giornoFirstVersDa;
    }

    public void setGiornoFirstVersDa(Date giornoFirstVersDa) {
	this.giornoFirstVersDa = giornoFirstVersDa;
    }

    public Date getGiornoFirstVersA() {
	return giornoFirstVersA;
    }

    public void setGiornoFirstVersA(Date giornoFirstVersA) {
	this.giornoFirstVersA = giornoFirstVersA;
    }

    public BigDecimal getOreFirstVersDa() {
	return oreFirstVersDa;
    }

    public void setOreFirstVersDa(BigDecimal oreFirstVersDa) {
	this.oreFirstVersDa = oreFirstVersDa;
    }

    public BigDecimal getMinutiFirstVersDa() {
	return minutiFirstVersDa;
    }

    public void setMinutiFirstVersDa(BigDecimal minutiFirstVersDa) {
	this.minutiFirstVersDa = minutiFirstVersDa;
    }

    public BigDecimal getOreFirstVersA() {
	return oreFirstVersA;
    }

    public void setOreFirstVersA(BigDecimal oreFirstVersA) {
	this.oreFirstVersA = oreFirstVersA;
    }

    public BigDecimal getMinutiFirstVersA() {
	return minutiFirstVersA;
    }

    public void setMinutiFirstVersA(BigDecimal minutiFirstVersA) {
	this.minutiFirstVersA = minutiFirstVersA;
    }

    public Date getGiornoFirstVersDaValidato() {
	return giornoFirstVersDaValidato;
    }

    public void setGiornoFirstVersDaValidato(Date giornoFirstVersDaValidato) {
	this.giornoFirstVersDaValidato = giornoFirstVersDaValidato;
    }

    public Date getGiornoFirstVersAValidato() {
	return giornoFirstVersAValidato;
    }

    public void setGiornoFirstVersAValidato(Date giornoFirstVersAValidato) {
	this.giornoFirstVersAValidato = giornoFirstVersAValidato;
    }

    public Date getGiornoLastVersDa() {
	return giornoLastVersDa;
    }

    public void setGiornoLastVersDa(Date giornoLastVersDa) {
	this.giornoLastVersDa = giornoLastVersDa;
    }

    public Date getGiornoLastVersA() {
	return giornoLastVersA;
    }

    public void setGiornoLastVersA(Date giornoLastVersA) {
	this.giornoLastVersA = giornoLastVersA;
    }

    public BigDecimal getOreLastVersDa() {
	return oreLastVersDa;
    }

    public void setOreLastVersDa(BigDecimal oreLastVersDa) {
	this.oreLastVersDa = oreLastVersDa;
    }

    public BigDecimal getMinutiLastVersDa() {
	return minutiLastVersDa;
    }

    public void setMinutiLastVersDa(BigDecimal minutiLastVersDa) {
	this.minutiLastVersDa = minutiLastVersDa;
    }

    public BigDecimal getOreLastVersA() {
	return oreLastVersA;
    }

    public void setOreLastVersA(BigDecimal oreLastVersA) {
	this.oreLastVersA = oreLastVersA;
    }

    public BigDecimal getMinutiLastVersA() {
	return minutiLastVersA;
    }

    public void setMinutiLastVersA(BigDecimal minutiLastVersA) {
	this.minutiLastVersA = minutiLastVersA;
    }

    public Date getGiornoLastVersDaValidato() {
	return giornoLastVersDaValidato;
    }

    public void setGiornoLastVersDaValidato(Date giornoLastVersDaValidato) {
	this.giornoLastVersDaValidato = giornoLastVersDaValidato;
    }

    public Date getGiornoLastVersAValidato() {
	return giornoLastVersAValidato;
    }

    public void setGiornoLastVersAValidato(Date giornoLastVersAValidato) {
	this.giornoLastVersAValidato = giornoLastVersAValidato;
    }

}
