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

package it.eng.parer.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import it.eng.parer.entity.constraint.HsmSessioneFirma.TiEsitoSessioneFirma;
import it.eng.parer.entity.constraint.HsmSessioneFirma.TiSessioneFirma;

/**
 * The persistent class for the HSM_SESSIONE_FIRMA database table.
 */
@Entity
@Table(name = "HSM_SESSIONE_FIRMA")

public class HsmSessioneFirma implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idSessioneFirma;

    private String cdErr;

    private String dsErr;

    private TiEsitoSessioneFirma tiEsitoSessioneFirma;

    private TiSessioneFirma tiSessioneFirma;

    private Date tsFine;

    private Date tsInizio;

    private List<HsmElencoSessioneFirma> hsmElencoSessioneFirmas = new ArrayList<>();

    private IamUser iamUser;

    private List<HsmVerSerieSessioneFirma> hsmVerSerieSessioneFirmas = new ArrayList<>();

    private List<HsmElencoFascSesFirma> hsmElencoFascSesFirmas = new ArrayList<>();

    public HsmSessioneFirma() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_SESSIONE_FIRMA")
    @GenericGenerator(name = "SHSM_SESSIONE_FIRMA_ID_SESSIONE_FIRMA_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SHSM_SESSIONE_FIRMA"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SHSM_SESSIONE_FIRMA_ID_SESSIONE_FIRMA_GENERATOR")
    public Long getIdSessioneFirma() {
	return this.idSessioneFirma;
    }

    public void setIdSessioneFirma(Long idSessioneFirma) {
	this.idSessioneFirma = idSessioneFirma;
    }

    @Column(name = "CD_ERR")
    public String getCdErr() {
	return this.cdErr;
    }

    public void setCdErr(String cdErr) {
	this.cdErr = cdErr;
    }

    @Column(name = "DS_ERR")
    public String getDsErr() {
	return this.dsErr;
    }

    public void setDsErr(String dsErr) {
	this.dsErr = dsErr;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "TI_ESITO_SESSIONE_FIRMA")
    public TiEsitoSessioneFirma getTiEsitoSessioneFirma() {
	return this.tiEsitoSessioneFirma;
    }

    public void setTiEsitoSessioneFirma(TiEsitoSessioneFirma tiEsitoSessioneFirma) {
	this.tiEsitoSessioneFirma = tiEsitoSessioneFirma;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "TI_SESSIONE_FIRMA")
    public TiSessioneFirma getTiSessioneFirma() {
	return this.tiSessioneFirma;
    }

    public void setTiSessioneFirma(TiSessioneFirma tiSessioneFirma) {
	this.tiSessioneFirma = tiSessioneFirma;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TS_FINE")
    public Date getTsFine() {
	return this.tsFine;
    }

    public void setTsFine(Date tsFine) {
	this.tsFine = tsFine;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TS_INIZIO")
    public Date getTsInizio() {
	return this.tsInizio;
    }

    public void setTsInizio(Date tsInizio) {
	this.tsInizio = tsInizio;
    }

    @OneToMany(mappedBy = "hsmSessioneFirma", cascade = {
	    CascadeType.PERSIST })
    public List<HsmElencoSessioneFirma> getHsmElencoSessioneFirmas() {
	return this.hsmElencoSessioneFirmas;
    }

    public void setHsmElencoSessioneFirmas(List<HsmElencoSessioneFirma> hsmElencoSessioneFirmas) {
	this.hsmElencoSessioneFirmas = hsmElencoSessioneFirmas;
    }

    public HsmElencoSessioneFirma addHsmElencoSessioneFirma(
	    HsmElencoSessioneFirma hsmElencoSessioneFirma) {
	getHsmElencoSessioneFirmas().add(hsmElencoSessioneFirma);
	hsmElencoSessioneFirma.setHsmSessioneFirma(this);
	return hsmElencoSessioneFirma;
    }

    public HsmElencoSessioneFirma removeHsmElencoSessioneFirma(
	    HsmElencoSessioneFirma hsmElencoSessioneFirma) {
	getHsmElencoSessioneFirmas().remove(hsmElencoSessioneFirma);
	hsmElencoSessioneFirma.setHsmSessioneFirma(null);
	return hsmElencoSessioneFirma;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER_IAM")
    public IamUser getIamUser() {
	return this.iamUser;
    }

    public void setIamUser(IamUser iamUser) {
	this.iamUser = iamUser;
    }

    @OneToMany(mappedBy = "hsmSessioneFirma", cascade = {
	    CascadeType.PERSIST })
    public List<HsmVerSerieSessioneFirma> getHsmVerSerieSessioneFirmas() {
	return this.hsmVerSerieSessioneFirmas;
    }

    public void setHsmVerSerieSessioneFirmas(
	    List<HsmVerSerieSessioneFirma> hsmVerSerieSessioneFirmas) {
	this.hsmVerSerieSessioneFirmas = hsmVerSerieSessioneFirmas;
    }

    public HsmVerSerieSessioneFirma addHsmVerSerieSessioneFirma(
	    HsmVerSerieSessioneFirma hsmVerSerieSessioneFirma) {
	getHsmVerSerieSessioneFirmas().add(hsmVerSerieSessioneFirma);
	hsmVerSerieSessioneFirma.setHsmSessioneFirma(this);
	return hsmVerSerieSessioneFirma;
    }

    public HsmVerSerieSessioneFirma removeHsmVerSerieSessioneFirma(
	    HsmVerSerieSessioneFirma hsmVerSerieSessioneFirma) {
	getHsmVerSerieSessioneFirmas().remove(hsmVerSerieSessioneFirma);
	hsmVerSerieSessioneFirma.setHsmSessioneFirma(null);
	return hsmVerSerieSessioneFirma;
    }

    @OneToMany(mappedBy = "hsmSessioneFirma", cascade = {
	    CascadeType.PERSIST })
    public List<HsmElencoFascSesFirma> getHsmElencoFascSesFirmas() {
	return this.hsmElencoFascSesFirmas;
    }

    public void setHsmElencoFascSesFirmas(List<HsmElencoFascSesFirma> hsmElencoFascSesFirmas) {
	this.hsmElencoFascSesFirmas = hsmElencoFascSesFirmas;
    }

    public HsmElencoFascSesFirma addHsmElencoFascSesFirma(
	    HsmElencoFascSesFirma hsmElencoFascSesFirma) {
	getHsmElencoFascSesFirmas().add(hsmElencoFascSesFirma);
	hsmElencoFascSesFirma.setHsmSessioneFirma(this);
	return hsmElencoFascSesFirma;
    }

    public HsmElencoFascSesFirma removeHsmElencoFascSesFirma(
	    HsmElencoFascSesFirma hsmElencoFascSesFirma) {
	getHsmElencoFascSesFirmas().remove(hsmElencoFascSesFirma);
	hsmElencoFascSesFirma.setHsmSessioneFirma(null);
	return hsmElencoFascSesFirma;
    }

    @Transient
    public boolean isActive() {
	return (tsInizio != null && tsFine == null);
    }

    @Transient
    public boolean isClose() {
	return (tsInizio != null && tsFine != null);
    }

    @Transient
    public boolean isCorrectlyTerminated() {
	return (isClose() && tiEsitoSessioneFirma.equals(TiEsitoSessioneFirma.OK));
    }

    @Transient
    public boolean isTerminatedWithWarning() {
	return (isClose() && tiEsitoSessioneFirma.equals(TiEsitoSessioneFirma.WARNING));
    }

    @Transient
    public boolean isWronglyTerminated() {
	return (isClose() && tiEsitoSessioneFirma.equals(TiEsitoSessioneFirma.ERRORE));
    }

    /**
     * Returns the number of files which this session has.
     *
     * @return numero file
     */
    @Transient
    public int getNumFile() {
	int result = 0;
	switch (getTiSessioneFirma()) {
	case ELENCHI:
	case ELENCO_INDICI_AIP:
	    if (getHsmElencoSessioneFirmas() != null) {
		result = getHsmElencoSessioneFirmas().size();
	    }
	    break;
	case SERIE:
	    if (getHsmVerSerieSessioneFirmas() != null) {
		result = getHsmVerSerieSessioneFirmas().size();
	    }
	    break;
	case ELENCHI_FASC:
	case ELENCHI_INDICI_AIP_FASC:
	    if (getHsmElencoFascSesFirmas() != null) {
		result = getHsmElencoFascSesFirmas().size();
	    }
	    break;
	}
	return result;
    }

    /**
     * Ottieni l'id del file
     *
     * @param index indice per la sessione di firma
     *
     * @return id file PK
     */
    @Transient
    public Long getIdFile(int index) {
	Long result = null;
	if (getTiSessioneFirma().equals(TiSessioneFirma.ELENCHI)
		|| getTiSessioneFirma().equals(TiSessioneFirma.ELENCO_INDICI_AIP)) {
	    List<HsmElencoSessioneFirma> list = getHsmElencoSessioneFirmas();
	    if (list != null && (index >= 0 && index < list.size())) {
		result = list.get(index).getElvElencoVer().getIdElencoVers();
	    }
	} else if (getTiSessioneFirma().equals(TiSessioneFirma.SERIE)) {
	    List<HsmVerSerieSessioneFirma> list = getHsmVerSerieSessioneFirmas();
	    if (list != null && (index >= 0 && index < list.size())) {
		result = list.get(index).getSerVerSerie().getIdVerSerie();
	    }
	} else if (getTiSessioneFirma().equals(TiSessioneFirma.ELENCHI_FASC)
		|| getTiSessioneFirma().equals(TiSessioneFirma.ELENCHI_INDICI_AIP_FASC)) {
	    List<HsmElencoFascSesFirma> list = getHsmElencoFascSesFirmas();
	    if (list != null && (index >= 0 && index < list.size())) {
		result = list.get(index).getElvElencoVersFasc().getIdElencoVersFasc();
	    }
	}
	return result;
    }
}
