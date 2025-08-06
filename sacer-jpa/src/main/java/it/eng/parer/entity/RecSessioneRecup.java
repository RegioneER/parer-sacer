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

import javax.persistence.Column;
import javax.persistence.Entity;
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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the REC_SESSIONE_RECUP database table.
 */
@Entity
@Table(name = "REC_SESSIONE_RECUP")
public class RecSessioneRecup implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idSessioneRecup;

    private String cdErr;

    private String dlErr;

    private Date dtApertura;

    private Date dtChiusura;

    private String tiOutputRecup;

    private String tiSessioneRecup;

    private String tiStatoSessioneRecup;

    private List<RecDtVersRecup> recDtVersRecups = new ArrayList<>();

    private AroCompDoc aroCompDoc;

    private AroDoc aroDoc;

    private RecUnitaDocRecup recUnitaDocRecup;

    private IamUser iamUser;

    public RecSessioneRecup() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_SESSIONE_RECUP")
    @GenericGenerator(name = "SREC_SESSIONE_RECUP_ID_SESSIONE_RECUP_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SREC_SESSIONE_RECUP"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SREC_SESSIONE_RECUP_ID_SESSIONE_RECUP_GENERATOR")
    public Long getIdSessioneRecup() {
	return this.idSessioneRecup;
    }

    public void setIdSessioneRecup(Long idSessioneRecup) {
	this.idSessioneRecup = idSessioneRecup;
    }

    @Column(name = "CD_ERR")
    public String getCdErr() {
	return this.cdErr;
    }

    public void setCdErr(String cdErr) {
	this.cdErr = cdErr;
    }

    @Column(name = "DL_ERR")
    public String getDlErr() {
	return this.dlErr;
    }

    public void setDlErr(String dlErr) {
	this.dlErr = dlErr;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_APERTURA")
    public Date getDtApertura() {
	return this.dtApertura;
    }

    public void setDtApertura(Date dtApertura) {
	this.dtApertura = dtApertura;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CHIUSURA")
    public Date getDtChiusura() {
	return this.dtChiusura;
    }

    public void setDtChiusura(Date dtChiusura) {
	this.dtChiusura = dtChiusura;
    }

    @Column(name = "TI_OUTPUT_RECUP")
    public String getTiOutputRecup() {
	return tiOutputRecup;
    }

    public void setTiOutputRecup(String tiOutputRecup) {
	this.tiOutputRecup = tiOutputRecup;
    }

    @Column(name = "TI_SESSIONE_RECUP")
    public String getTiSessioneRecup() {
	return this.tiSessioneRecup;
    }

    public void setTiSessioneRecup(String tiSessioneRecup) {
	this.tiSessioneRecup = tiSessioneRecup;
    }

    @Column(name = "TI_STATO_SESSIONE_RECUP")
    public String getTiStatoSessioneRecup() {
	return this.tiStatoSessioneRecup;
    }

    public void setTiStatoSessioneRecup(String tiStatoSessioneRecup) {
	this.tiStatoSessioneRecup = tiStatoSessioneRecup;
    }

    @OneToMany(mappedBy = "recSessioneRecup")
    public List<RecDtVersRecup> getRecDtVersRecups() {
	return this.recDtVersRecups;
    }

    public void setRecDtVersRecups(List<RecDtVersRecup> recDtVersRecups) {
	this.recDtVersRecups = recDtVersRecups;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_COMP_DOC")
    public AroCompDoc getAroCompDoc() {
	return this.aroCompDoc;
    }

    public void setAroCompDoc(AroCompDoc aroCompDoc) {
	this.aroCompDoc = aroCompDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_DOC")
    public AroDoc getAroDoc() {
	return this.aroDoc;
    }

    public void setAroDoc(AroDoc aroDoc) {
	this.aroDoc = aroDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UNITA_DOC_RECUP")
    public RecUnitaDocRecup getRecUnitaDocRecup() {
	return this.recUnitaDocRecup;
    }

    public void setRecUnitaDocRecup(RecUnitaDocRecup recUnitaDocRecup) {
	this.recUnitaDocRecup = recUnitaDocRecup;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER")
    public IamUser getIamUser() {
	return this.iamUser;
    }

    public void setIamUser(IamUser iamUser) {
	this.iamUser = iamUser;
    }
}
