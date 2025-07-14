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

package it.eng.parer.viewEntity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import javax.persistence.Table;

/**
 * The persistent class for the MON_V_CHK_DOC_NONVERS_AMB database table.
 */
@Entity
@Table(name = "MON_V_CHK_DOC_NONVERS_AMB")

public class MonVChkDocNonversAmb implements Serializable {

    private static final long serialVersionUID = 1L;

    private String flDocNonversNorisolub;

    private String flDocNonversNoverif;

    private String flDocNonversVerif;

    public MonVChkDocNonversAmb() {/* Hibernate */
    }

    @Column(name = "FL_DOC_NONVERS_NORISOLUB", columnDefinition = "char(1)")
    public String getFlDocNonversNorisolub() {
	return this.flDocNonversNorisolub;
    }

    public void setFlDocNonversNorisolub(String flDocNonversNorisolub) {
	this.flDocNonversNorisolub = flDocNonversNorisolub;
    }

    @Column(name = "FL_DOC_NONVERS_NOVERIF", columnDefinition = "char(1)")
    public String getFlDocNonversNoverif() {
	return this.flDocNonversNoverif;
    }

    public void setFlDocNonversNoverif(String flDocNonversNoverif) {
	this.flDocNonversNoverif = flDocNonversNoverif;
    }

    @Column(name = "FL_DOC_NONVERS_VERIF", columnDefinition = "char(1)")
    public String getFlDocNonversVerif() {
	return this.flDocNonversVerif;
    }

    public void setFlDocNonversVerif(String flDocNonversVerif) {
	this.flDocNonversVerif = flDocNonversVerif;
    }

    private MonVChkDocNonversAmbId monVChkDocNonversAmbId;

    @EmbeddedId()
    public MonVChkDocNonversAmbId getMonVChkDocNonversAmbId() {
	return monVChkDocNonversAmbId;
    }

    public void setMonVChkDocNonversAmbId(MonVChkDocNonversAmbId monVChkDocNonversAmbId) {
	this.monVChkDocNonversAmbId = monVChkDocNonversAmbId;
    }
}
