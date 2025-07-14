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

package it.eng.parer.grantedEntity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.PersistentIdentifierGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the ORG_DISCIP_STRUT database table.
 */
@Entity
@Table(name = "ORG_DISCIP_STRUT", schema = "SACER_IAM")
public class OrgDiscipStrut implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idDiscipStrut;

    private Long idAccordoEnte;

    private Long idOrganizIam;

    private Date dtDiscipStrut;

    private byte[] blDiscipStrut;

    private Long idEnteConvenz;

    public OrgDiscipStrut() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_DISCIP_STRUT")
    @GenericGenerator(name = "SORG_DISCIP_STRUT_ID_DISCIP_STRUT_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = PersistentIdentifierGenerator.SCHEMA, value = "SACER_IAM"),
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SORG_DISCIP_STRUT"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SORG_DISCIP_STRUT_ID_DISCIP_STRUT_GENERATOR")
    public Long getIdDiscipStrut() {
	return this.idDiscipStrut;
    }

    public void setIdDiscipStrut(Long idDiscipStrut) {
	this.idDiscipStrut = idDiscipStrut;
    }

    @Column(name = "ID_ACCORDO_ENTE")
    public Long getIdAccordoEnte() {
	return this.idAccordoEnte;
    }

    public void setIdAccordoEnte(Long idAccordoEnte) {
	this.idAccordoEnte = idAccordoEnte;
    }

    @Column(name = "ID_ORGANIZ_IAM")
    public Long getIdOrganizIam() {
	return this.idOrganizIam;
    }

    public void setIdOrganizIam(Long idOrganizIam) {
	this.idOrganizIam = idOrganizIam;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_DISCIP_STRUT")
    public Date getDtDiscipStrut() {
	return this.dtDiscipStrut;
    }

    public void setDtDiscipStrut(Date dtDiscipStrut) {
	this.dtDiscipStrut = dtDiscipStrut;
    }

    @Lob
    @Column(name = "BL_DISCIP_STRUT")
    public byte[] getBlDiscipStrut() {
	return this.blDiscipStrut;
    }

    public void setBlDiscipStrut(byte[] blDiscipStrut) {
	this.blDiscipStrut = blDiscipStrut;
    }

    @Column(name = "ID_ENTE_CONVENZ")
    public Long getIdEnteConvenz() {
	return idEnteConvenz;
    }

    public void setIdEnteConvenz(Long idEnteConvenz) {
	this.idEnteConvenz = idEnteConvenz;
    }
}
