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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import javax.persistence.Table;

/**
 * The persistent class for the USR_DICH_ABIL_ORGANIZ database table.
 */
@Entity
@Table(schema = "SACER_IAM", name = "USR_DICH_ABIL_ORGANIZ")

public class UsrDichAbilOrganiz implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idDichAbilOrganiz;

    private String dsCausaleDich;

    private String tiScopoDichAbilOrganiz;

    private SIUsrOrganizIam usrOrganizIam;

    private UsrUsoUserApplic usrUsoUserApplic;

    public UsrDichAbilOrganiz() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_DICH_ABIL_ORGANIZ")
    public Long getIdDichAbilOrganiz() {
	return this.idDichAbilOrganiz;
    }

    public void setIdDichAbilOrganiz(Long idDichAbilOrganiz) {
	this.idDichAbilOrganiz = idDichAbilOrganiz;
    }

    @Column(name = "DS_CAUSALE_DICH")
    public String getDsCausaleDich() {
	return this.dsCausaleDich;
    }

    public void setDsCausaleDich(String dsCausaleDich) {
	this.dsCausaleDich = dsCausaleDich;
    }

    @Column(name = "TI_SCOPO_DICH_ABIL_ORGANIZ")
    public String getTiScopoDichAbilOrganiz() {
	return this.tiScopoDichAbilOrganiz;
    }

    public void setTiScopoDichAbilOrganiz(String tiScopoDichAbilOrganiz) {
	this.tiScopoDichAbilOrganiz = tiScopoDichAbilOrganiz;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ORGANIZ_IAM")
    public SIUsrOrganizIam getUsrOrganizIam() {
	return this.usrOrganizIam;
    }

    public void setUsrOrganizIam(SIUsrOrganizIam usrOrganizIam) {
	this.usrOrganizIam = usrOrganizIam;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USO_USER_APPLIC")
    public UsrUsoUserApplic getUsrUsoUserApplic() {
	return this.usrUsoUserApplic;
    }

    public void setUsrUsoUserApplic(UsrUsoUserApplic usrUsoUserApplic) {
	this.usrUsoUserApplic = usrUsoUserApplic;
    }
}
