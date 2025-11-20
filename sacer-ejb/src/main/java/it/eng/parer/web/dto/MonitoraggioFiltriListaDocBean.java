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

/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.web.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Gilioli_P
 */
public class MonitoraggioFiltriListaDocBean implements java.io.Serializable {

    private static final long serialVersionUID = 7235128043635341920L;

    private BigDecimal idAmbiente;
    private BigDecimal idEnte;
    private BigDecimal idStrut;
    private BigDecimal idTipoUnitaDoc;
    private BigDecimal idTipoDoc;
    private String cdRegistroKeyUnitaDoc;
    private BigDecimal aaKeyUnitaDoc;
    private BigDecimal aaKeyUnitaDocDa;
    private BigDecimal aaKeyUnitaDocA;
    private String cdKeyUnitaDoc;
    private String cdKeyUnitaDocDa;
    private String cdKeyUnitaDocA;
    private BigDecimal idUserIam;
    private String periodoVers;
    // private Date giornoVers;
    private Date giornoVersDa;
    private Date giornoVersA;
    private BigDecimal oreVersDa;
    private BigDecimal minutiVersDa;
    private BigDecimal oreVersA;
    private BigDecimal minutiVersA;
    private Date giornoVersDaValidato;
    private Date giornoVersAValidato;
    private String tipoDoc;
    private String statoDoc;
    private String statoVol;
    private String tipoCreazione;
    private Date giornoAnnulDaValidato;
    private Date giornoAnnulAValidato;

    public String getCdRegistroKeyUnitaDoc() {
	return cdRegistroKeyUnitaDoc;
    }

    public void setCdRegistroKeyUnitaDoc(String cdRegistroKeyUnitaDoc) {
	this.cdRegistroKeyUnitaDoc = cdRegistroKeyUnitaDoc;
    }

    /**
     * @return the idAmbiente
     */
    public BigDecimal getIdAmbiente() {
	return idAmbiente;
    }

    /**
     * @param idAmbiente the idAmbiente to set
     */
    public void setIdAmbiente(BigDecimal idAmbiente) {
	this.idAmbiente = idAmbiente;
    }

    /**
     * @return the idEnte
     */
    public BigDecimal getIdEnte() {
	return idEnte;
    }

    /**
     * @param idEnte the idEnte to set
     */
    public void setIdEnte(BigDecimal idEnte) {
	this.idEnte = idEnte;
    }

    /**
     * @return the idStrut
     */
    public BigDecimal getIdStrut() {
	return idStrut;
    }

    /**
     * @param idStrut the idStrut to set
     */
    public void setIdStrut(BigDecimal idStrut) {
	this.idStrut = idStrut;
    }

    /**
     * @return the idTipoUnitaDoc
     */
    public BigDecimal getIdTipoUnitaDoc() {
	return idTipoUnitaDoc;
    }

    /**
     * @param idTipoUnitaDoc the idTipoUnitaDoc to set
     */
    public void setIdTipoUnitaDoc(BigDecimal idTipoUnitaDoc) {
	this.idTipoUnitaDoc = idTipoUnitaDoc;
    }

    public BigDecimal getIdUserIam() {
	return idUserIam;
    }

    public void setIdUserIam(BigDecimal idUserIam) {
	this.idUserIam = idUserIam;
    }

    /**
     * @return the tipoDoc
     */
    public String getTipoDoc() {
	return tipoDoc;
    }

    /**
     * @param tipoDoc the tipoDoc to set
     */
    public void setTipoDoc(String tipoDoc) {
	this.tipoDoc = tipoDoc;
    }

    /**
     * @return the statoDoc
     */
    public String getStatoDoc() {
	return statoDoc;
    }

    /**
     * @param statoDoc the statoDoc to set
     */
    public void setStatoDoc(String statoDoc) {
	this.statoDoc = statoDoc;
    }

    /**
     * @return the statoVol
     */
    public String getStatoVol() {
	return statoVol;
    }

    /**
     * @param statoVol the statoVol to set
     */
    public void setStatoVol(String statoVol) {
	this.statoVol = statoVol;
    }

    /**
     * @return the periodoVers
     */
    public String getPeriodoVers() {
	return periodoVers;
    }

    /**
     * @param periodoVers the periodoVers to set
     */
    public void setPeriodoVers(String periodoVers) {
	this.periodoVers = periodoVers;
    }

    // /**
    // * @return the giornoVers
    // */
    // public Date getGiornoVers() {
    // return giornoVers;
    // }
    //
    // /**
    // * @param giornoVers the giornoVers to set
    // */
    // public void setGiornoVers(Date giornoVers) {
    // this.giornoVers = giornoVers;
    // }
    /**
     * @return the giornoVersDa
     */
    public Date getGiornoVersDa() {
	return giornoVersDa;
    }

    /**
     * @param giornoVersDa the giornoVersDa to set
     */
    public void setGiornoVersDa(Date giornoVersDa) {
	this.giornoVersDa = giornoVersDa;
    }

    /**
     * @return the giornoVersA
     */
    public Date getGiornoVersA() {
	return giornoVersA;
    }

    /**
     * @param giornoVersA the giornoVersA to set
     */
    public void setGiornoVersA(Date giornoVersA) {
	this.giornoVersA = giornoVersA;
    }

    /**
     * @return the oreVersDa
     */
    public BigDecimal getOreVersDa() {
	return oreVersDa;
    }

    /**
     * @param oreVersDa the oreVersDa to set
     */
    public void setOreVersDa(BigDecimal oreVersDa) {
	this.oreVersDa = oreVersDa;
    }

    /**
     * @return the minutiVersDa
     */
    public BigDecimal getMinutiVersDa() {
	return minutiVersDa;
    }

    /**
     * @param minutiVersDa the minutiVersDa to set
     */
    public void setMinutiVersDa(BigDecimal minutiVersDa) {
	this.minutiVersDa = minutiVersDa;
    }

    /**
     * @return the oreVersA
     */
    public BigDecimal getOreVersA() {
	return oreVersA;
    }

    /**
     * @param oreVersA the oreVersA to set
     */
    public void setOreVersA(BigDecimal oreVersA) {
	this.oreVersA = oreVersA;
    }

    /**
     * @return the minutiVersA
     */
    public BigDecimal getMinutiVersA() {
	return minutiVersA;
    }

    /**
     * @param minutiVersA the minutiVersA to set
     */
    public void setMinutiVersA(BigDecimal minutiVersA) {
	this.minutiVersA = minutiVersA;
    }

    /**
     * @return the giornoVersDaValidato
     */
    public Date getGiornoVersDaValidato() {
	return giornoVersDaValidato;
    }

    /**
     * @param giornoVersDaValidato the giornoVersDaValidato to set
     */
    public void setGiornoVersDaValidato(Date giornoVersDaValidato) {
	this.giornoVersDaValidato = giornoVersDaValidato;
    }

    /**
     * @return the giornoVersAValidato
     */
    public Date getGiornoVersAValidato() {
	return giornoVersAValidato;
    }

    /**
     * @param giornoVersAValidato the giornoVersAValidato to set
     */
    public void setGiornoVersAValidato(Date giornoVersAValidato) {
	this.giornoVersAValidato = giornoVersAValidato;
    }

    public BigDecimal getIdTipoDoc() {
	return idTipoDoc;
    }

    public void setIdTipoDoc(BigDecimal idTipoDoc) {
	this.idTipoDoc = idTipoDoc;
    }

    public BigDecimal getAaKeyUnitaDoc() {
	return aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
	this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    public BigDecimal getAaKeyUnitaDocDa() {
	return aaKeyUnitaDocDa;
    }

    public void setAaKeyUnitaDocDa(BigDecimal aaKeyUnitaDocDa) {
	this.aaKeyUnitaDocDa = aaKeyUnitaDocDa;
    }

    public BigDecimal getAaKeyUnitaDocA() {
	return aaKeyUnitaDocA;
    }

    public void setAaKeyUnitaDocA(BigDecimal aaKeyUnitaDocA) {
	this.aaKeyUnitaDocA = aaKeyUnitaDocA;
    }

    public String getCdKeyUnitaDoc() {
	return cdKeyUnitaDoc;
    }

    public void setCdKeyUnitaDoc(String cdKeyUnitaDoc) {
	this.cdKeyUnitaDoc = cdKeyUnitaDoc;
    }

    public String getCdKeyUnitaDocDa() {
	return cdKeyUnitaDocDa;
    }

    public void setCdKeyUnitaDocDa(String cdKeyUnitaDocDa) {
	this.cdKeyUnitaDocDa = cdKeyUnitaDocDa;
    }

    public String getCdKeyUnitaDocA() {
	return cdKeyUnitaDocA;
    }

    public void setCdKeyUnitaDocA(String cdKeyUnitaDocA) {
	this.cdKeyUnitaDocA = cdKeyUnitaDocA;
    }

    public String getTipoCreazione() {
	return tipoCreazione;
    }

    public void setTipoCreazione(String tipoCreazione) {
	this.tipoCreazione = tipoCreazione;
    }

    public Date getGiornoAnnulDaValidato() {
	return giornoAnnulDaValidato;
    }

    public void setGiornoAnnulDaValidato(Date giornoAnnulDaValidato) {
	this.giornoAnnulDaValidato = giornoAnnulDaValidato;
    }

    public Date getGiornoAnnulAValidato() {
	return giornoAnnulAValidato;
    }

    public void setGiornoAnnulAValidato(Date giornoAnnulAValidato) {
	this.giornoAnnulAValidato = giornoAnnulAValidato;
    }
}
