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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the USR_USO_USER_APPLIC database table.
 */
@Entity
@Table(schema = "SACER_IAM", name = "USR_USO_USER_APPLIC")
public class UsrUsoUserApplic implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idUsoUserApplic;

    private List<UsrDichAbilOrganiz> usrDichAbilOrganizs = new ArrayList<>();

    private SIAplApplic aplApplic;

    private UsrUser usrUser;

    public UsrUsoUserApplic() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_USO_USER_APPLIC")
    public Long getIdUsoUserApplic() {
	return this.idUsoUserApplic;
    }

    public void setIdUsoUserApplic(Long idUsoUserApplic) {
	this.idUsoUserApplic = idUsoUserApplic;
    }

    @OneToMany(mappedBy = "usrUsoUserApplic", cascade = {
	    CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH, CascadeType.REMOVE })
    public List<UsrDichAbilOrganiz> getUsrDichAbilOrganizs() {
	return this.usrDichAbilOrganizs;
    }

    public void setUsrDichAbilOrganizs(List<UsrDichAbilOrganiz> usrDichAbilOrganizs) {
	this.usrDichAbilOrganizs = usrDichAbilOrganizs;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_APPLIC")
    public SIAplApplic getAplApplic() {
	return this.aplApplic;
    }

    public void setAplApplic(SIAplApplic aplApplic) {
	this.aplApplic = aplApplic;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER_IAM")
    public UsrUser getUsrUser() {
	return this.usrUser;
    }

    public void setUsrUser(UsrUser usrUser) {
	this.usrUser = usrUser;
    }
}
