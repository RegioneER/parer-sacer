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
 * The persistent class for the IAM_VALORE_PARAM_APPLIC database table.
 *
 */
@Entity
@Table(name = "IAM_VALORE_PARAM_APPLIC", schema = "SACER_IAM")

public class IamValoreParamApplic implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idValoreParamApplic;
    private String dsValoreParamApplic;
    private String tiAppart;
    private IamParamApplic iamParamApplic;
    private SIOrgAmbienteEnteConvenz orgAmbienteEnteConvenz;
    private SIOrgEnteSiam orgEnteSiam;

    public IamValoreParamApplic() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_VALORE_PARAM_APPLIC")
    public Long getIdValoreParamApplic() {
        return this.idValoreParamApplic;
    }

    public void setIdValoreParamApplic(Long idValoreParamApplic) {
        this.idValoreParamApplic = idValoreParamApplic;
    }

    @Column(name = "DS_VALORE_PARAM_APPLIC")
    public String getDsValoreParamApplic() {
        return this.dsValoreParamApplic;
    }

    public void setDsValoreParamApplic(String dsValoreParamApplic) {
        this.dsValoreParamApplic = dsValoreParamApplic;
    }

    @Column(name = "TI_APPART")
    public String getTiAppart() {
        return this.tiAppart;
    }

    public void setTiAppart(String tiAppart) {
        this.tiAppart = tiAppart;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PARAM_APPLIC")
    public IamParamApplic getIamParamApplic() {
        return this.iamParamApplic;
    }

    public void setIamParamApplic(IamParamApplic iamParamApplic) {
        this.iamParamApplic = iamParamApplic;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_AMBIENTE_ENTE_CONVENZ")
    public SIOrgAmbienteEnteConvenz getOrgAmbienteEnteConvenz() {
        return this.orgAmbienteEnteConvenz;
    }

    public void setOrgAmbienteEnteConvenz(SIOrgAmbienteEnteConvenz orgAmbienteEnteConvenz) {
        this.orgAmbienteEnteConvenz = orgAmbienteEnteConvenz;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ENTE_CONVENZ")
    public SIOrgEnteSiam getOrgEnteSiam() {
        return this.orgEnteSiam;
    }

    public void setOrgEnteSiam(SIOrgEnteSiam orgEnteSiam) {
        this.orgEnteSiam = orgEnteSiam;
    }

}
