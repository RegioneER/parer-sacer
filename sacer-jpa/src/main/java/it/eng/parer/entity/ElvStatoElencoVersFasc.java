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
import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import it.eng.parer.entity.constraint.ElvStatoElencoVersFasc.TiStatoElencoFasc;

/**
 * The persistent class for the ELV_STATO_ELENCO_VERS_FASC database table.
 */
@Entity
@Table(name = "ELV_STATO_ELENCO_VERS_FASC")

@NamedQuery(name = "ElvStatoElencoVersFasc.findByElencoVersFasc", query = "SELECT e FROM ElvStatoElencoVersFasc e WHERE e.elvElencoVersFasc = :elvElencoVersFasc ORDER BY e.tsStato ASC")
public class ElvStatoElencoVersFasc implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idStatoElencoVersFasc;

    private IamUser iamUser;

    private TiStatoElencoFasc tiStato;

    private Date tsStato;

    private ElvElencoVersFasc elvElencoVersFasc;

    public ElvStatoElencoVersFasc() {
	// Hibernate
    }

    @Id

    @Column(name = "ID_STATO_ELENCO_VERS_FASC")
    @GenericGenerator(name = "SELV_STATO_ELENCO_VERS_FASC_ID_STATO_ELENCO_VERS_FASC_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SELV_STATO_ELENCO_VERS_FASC"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SELV_STATO_ELENCO_VERS_FASC_ID_STATO_ELENCO_VERS_FASC_GENERATOR")
    public Long getIdStatoElencoVersFasc() {
	return this.idStatoElencoVersFasc;
    }

    public void setIdStatoElencoVersFasc(Long idStatoElencoVersFasc) {
	this.idStatoElencoVersFasc = idStatoElencoVersFasc;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "TI_STATO")
    public TiStatoElencoFasc getTiStato() {
	return this.tiStato;
    }

    public void setTiStato(TiStatoElencoFasc tiStato) {
	this.tiStato = tiStato;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER_IAM")
    public IamUser getIamUser() {
	return this.iamUser;
    }

    public void setIamUser(IamUser iamUser) {
	this.iamUser = iamUser;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TS_STATO")
    public Date getTsStato() {
	return this.tsStato;
    }

    public void setTsStato(Date tsStato) {
	this.tsStato = tsStato;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ELENCO_VERS_FASC", nullable = false)
    public ElvElencoVersFasc getElvElencoVersFasc() {
	return this.elvElencoVersFasc;
    }

    public void setElvElencoVersFasc(ElvElencoVersFasc elvElencoVersFasc) {
	this.elvElencoVersFasc = elvElencoVersFasc;
    }
}
