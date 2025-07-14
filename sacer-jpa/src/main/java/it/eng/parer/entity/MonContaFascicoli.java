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
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the MON_CONTA_FASCICOLI database table.
 */
@Entity
@Table(name = "MON_CONTA_FASCICOLI")

@NamedQuery(name = "MonContaFascicoli.findByChiaveTotalizz", query = "SELECT m FROM MonContaFascicoli m WHERE m.orgStrut.idStrut=:idStrut AND TRUNC(m.dtRifConta)=:dtRifConta AND m.decTipoFascicolo.idTipoFascicolo=:idTipoFascicolo AND m.aaFascicolo=:aaFascicolo AND m.iamUser.idUserIam=:idUserIam")
public class MonContaFascicoli implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idContaFascicoli;

    private BigDecimal aaFascicolo;

    private Date dtRifConta;

    private BigDecimal niFascicoliVers;

    private DecTipoFascicolo decTipoFascicolo;

    private IamUser iamUser;

    private OrgStrut orgStrut;

    public MonContaFascicoli() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_CONTA_FASCICOLI")
    @GenericGenerator(name = "SMON_CONTA_FASCICOLI_ID_CONTA_FASCICOLI_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SMON_CONTA_FASCICOLI"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SMON_CONTA_FASCICOLI_ID_CONTA_FASCICOLI_GENERATOR")
    public Long getIdContaFascicoli() {
	return this.idContaFascicoli;
    }

    public void setIdContaFascicoli(Long idContaFascicoli) {
	this.idContaFascicoli = idContaFascicoli;
    }

    @Column(name = "AA_FASCICOLO")
    public BigDecimal getAaFascicolo() {
	return this.aaFascicolo;
    }

    public void setAaFascicolo(BigDecimal aaFascicolo) {
	this.aaFascicolo = aaFascicolo;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_RIF_CONTA")
    public Date getDtRifConta() {
	return this.dtRifConta;
    }

    public void setDtRifConta(Date dtRifConta) {
	this.dtRifConta = dtRifConta;
    }

    @Column(name = "NI_FASCICOLI_VERS")
    public BigDecimal getNiFascicoliVers() {
	return this.niFascicoliVers;
    }

    public void setNiFascicoliVers(BigDecimal niFascicoliVers) {
	this.niFascicoliVers = niFascicoliVers;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_FASCICOLO")
    public DecTipoFascicolo getDecTipoFascicolo() {
	return this.decTipoFascicolo;
    }

    public void setDecTipoFascicolo(DecTipoFascicolo decTipoFascicolo) {
	this.decTipoFascicolo = decTipoFascicolo;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER_IAM")
    public IamUser getIamUser() {
	return this.iamUser;
    }

    public void setIamUser(IamUser iamUser) {
	this.iamUser = iamUser;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STRUT")
    public OrgStrut getOrgStrut() {
	return this.orgStrut;
    }

    public void setOrgStrut(OrgStrut orgStrut) {
	this.orgStrut = orgStrut;
    }
}
