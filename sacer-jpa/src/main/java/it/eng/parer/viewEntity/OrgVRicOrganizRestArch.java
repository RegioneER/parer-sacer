/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.viewEntity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * The persistent class for the ORG_V_RIC_ORGANIZ_REST_ARCH database table.
 *
 */
@Entity
@Table(name = "ORG_V_RIC_ORGANIZ_REST_ARCH")
@NamedQuery(name = "OrgVRicOrganizRestArch.findAll", query = "SELECT o FROM OrgVRicOrganizRestArch o")
public class OrgVRicOrganizRestArch implements Serializable {
    private static final long serialVersionUID = 1L;
    private Date dtFineVal;
    private Date dtIniVal;
    private String flAssociazioneScaduta;
    private String flAssociazioniFuture;
    private BigDecimal idEnteConvenz;
    private BigDecimal idEnteConvenzOrg;
    private BigDecimal idOrganizIam;

    public OrgVRicOrganizRestArch() {
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_FINE_VAL")
    public Date getDtFineVal() {
        return this.dtFineVal;
    }

    public void setDtFineVal(Date dtFineVal) {
        this.dtFineVal = dtFineVal;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_INI_VAL")
    public Date getDtIniVal() {
        return this.dtIniVal;
    }

    public void setDtIniVal(Date dtIniVal) {
        this.dtIniVal = dtIniVal;
    }

    @Column(name = "FL_ASSOCIAZIONE_SCADUTA")
    public String getFlAssociazioneScaduta() {
        return this.flAssociazioneScaduta;
    }

    public void setFlAssociazioneScaduta(String flAssociazioneScaduta) {
        this.flAssociazioneScaduta = flAssociazioneScaduta;
    }

    @Column(name = "FL_ASSOCIAZIONI_FUTURE")
    public String getFlAssociazioniFuture() {
        return this.flAssociazioniFuture;
    }

    public void setFlAssociazioniFuture(String flAssociazioniFuture) {
        this.flAssociazioniFuture = flAssociazioniFuture;
    }

    @Column(name = "ID_ENTE_CONVENZ")
    public BigDecimal getIdEnteConvenz() {
        return this.idEnteConvenz;
    }

    public void setIdEnteConvenz(BigDecimal idEnteConvenz) {
        this.idEnteConvenz = idEnteConvenz;
    }

    @Column(name = "ID_ENTE_CONVENZ_ORG")
    public BigDecimal getIdEnteConvenzOrg() {
        return this.idEnteConvenzOrg;
    }

    public void setIdEnteConvenzOrg(BigDecimal idEnteConvenzOrg) {
        this.idEnteConvenzOrg = idEnteConvenzOrg;
    }

    @Id
    @Column(name = "ID_ORGANIZ_IAM")
    public BigDecimal getIdOrganizIam() {
        return this.idOrganizIam;
    }

    public void setIdOrganizIam(BigDecimal idOrganizIam) {
        this.idOrganizIam = idOrganizIam;
    }

}
