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

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the ORG_SUB_PARTITION database table.
 */
@Entity
@Table(name = "ORG_SUB_PARTITION")
public class OrgSubPartition implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idSubPartition;

    private String cdSubPartition;

    private OrgPartition orgPartition;

    private List<OrgValSubPartition> orgValSubPartitions = new ArrayList<>();

    public OrgSubPartition() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_SUB_PARTITION")
    @GenericGenerator(name = "SORG_SUB_PARTITION_ID_SUB_PARTITION_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SORG_SUB_PARTITION"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SORG_SUB_PARTITION_ID_SUB_PARTITION_GENERATOR")
    public Long getIdSubPartition() {
	return this.idSubPartition;
    }

    public void setIdSubPartition(Long idSubPartition) {
	this.idSubPartition = idSubPartition;
    }

    @Column(name = "CD_SUB_PARTITION")
    public String getCdSubPartition() {
	return this.cdSubPartition;
    }

    public void setCdSubPartition(String cdSubPartition) {
	this.cdSubPartition = cdSubPartition;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PARTITION")
    public OrgPartition getOrgPartition() {
	return this.orgPartition;
    }

    public void setOrgPartition(OrgPartition orgPartition) {
	this.orgPartition = orgPartition;
    }

    @OneToMany(mappedBy = "orgSubPartition")
    public List<OrgValSubPartition> getOrgValSubPartitions() {
	return this.orgValSubPartitions;
    }

    public void setOrgValSubPartitions(List<OrgValSubPartition> orgValSubPartitions) {
	this.orgValSubPartitions = orgValSubPartitions;
    }

}
