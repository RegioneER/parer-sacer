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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the ARO_CONTR_VERIF_FIRMA_DT_VERS database table.
 */
@Entity
@Table(name = "ARO_CONTR_VERIF_FIRMA_DT_VERS")

public class AroContrVerifFirmaDtVer implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idContrVerifFirmaDtVers;

    private String dsMsgContrVerif;

    private FirCrl firCrl;

    private String tiContr;

    private String tiEsitoContrVerif;

    private AroVerifFirmaDtVer aroVerifFirmaDtVer;

    public AroContrVerifFirmaDtVer() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_CONTR_VERIF_FIRMA_DT_VERS")
    @GenericGenerator(name = "SARO_CONTR_VERIF_FIRMA_DT_VERS_ID_CONTR_VERIF_FIRMA_DT_VERS_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SARO_CONTR_VERIF_FIRMA_DT_VERS"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SARO_CONTR_VERIF_FIRMA_DT_VERS_ID_CONTR_VERIF_FIRMA_DT_VERS_GENERATOR")
    public Long getIdContrVerifFirmaDtVers() {
	return this.idContrVerifFirmaDtVers;
    }

    public void setIdContrVerifFirmaDtVers(Long idContrVerifFirmaDtVers) {
	this.idContrVerifFirmaDtVers = idContrVerifFirmaDtVers;
    }

    @Column(name = "DS_MSG_CONTR_VERIF")
    public String getDsMsgContrVerif() {
	return this.dsMsgContrVerif;
    }

    public void setDsMsgContrVerif(String dsMsgContrVerif) {
	this.dsMsgContrVerif = dsMsgContrVerif;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = {
	    CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })
    @JoinColumn(name = "ID_CRL_USATA")
    public FirCrl getFirCrl() {
	return this.firCrl;
    }

    public void setFirCrl(FirCrl firCrl) {
	this.firCrl = firCrl;
    }

    @Column(name = "TI_CONTR")
    public String getTiContr() {
	return this.tiContr;
    }

    public void setTiContr(String tiContr) {
	this.tiContr = tiContr;
    }

    @Column(name = "TI_ESITO_CONTR_VERIF")
    public String getTiEsitoContrVerif() {
	return this.tiEsitoContrVerif;
    }

    public void setTiEsitoContrVerif(String tiEsitoContrVerif) {
	this.tiEsitoContrVerif = tiEsitoContrVerif;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VERIF_FIRMA_DT_VERS")
    public AroVerifFirmaDtVer getAroVerifFirmaDtVer() {
	return this.aroVerifFirmaDtVer;
    }

    public void setAroVerifFirmaDtVer(AroVerifFirmaDtVer aroVerifFirmaDtVer) {
	this.aroVerifFirmaDtVer = aroVerifFirmaDtVer;
    }
}
