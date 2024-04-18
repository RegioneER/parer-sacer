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
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the ORG_V_RIC_AMBIENTE database table.
 *
 */
@Entity
@Table(name = "ORG_V_RIC_AMBIENTE")

public class OrgVRicAmbiente implements Serializable {

    private static final long serialVersionUID = 1L;
    private String dsAmbiente;
    private Date dtFinVal;
    private Date dtIniVal;
    private BigDecimal idAmbiente;
    private BigDecimal idEnteConverv;
    private BigDecimal idEnteGestore;
    private BigDecimal idUserIam;
    private String nmAmbiente;
    private String nmEnteConserv;
    private String nmEnteGestore;

    public OrgVRicAmbiente() {/* Hibernate */
    }

    @Column(name = "DS_AMBIENTE")
    public String getDsAmbiente() {
        return this.dsAmbiente;
    }

    public void setDsAmbiente(String dsAmbiente) {
        this.dsAmbiente = dsAmbiente;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIN_VAL")
    public Date getDtFinVal() {
        return this.dtFinVal;
    }

    public void setDtFinVal(Date dtFinVal) {
        this.dtFinVal = dtFinVal;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INI_VAL")
    public Date getDtIniVal() {
        return this.dtIniVal;
    }

    public void setDtIniVal(Date dtIniVal) {
        this.dtIniVal = dtIniVal;
    }

    @Id
    @Column(name = "ID_AMBIENTE")
    public BigDecimal getIdAmbiente() {
        return this.idAmbiente;
    }

    public void setIdAmbiente(BigDecimal idAmbiente) {
        this.idAmbiente = idAmbiente;
    }

    @Column(name = "ID_ENTE_CONVERV")
    public BigDecimal getIdEnteConverv() {
        return this.idEnteConverv;
    }

    public void setIdEnteConverv(BigDecimal idEnteConverv) {
        this.idEnteConverv = idEnteConverv;
    }

    @Column(name = "ID_ENTE_GESTORE")
    public BigDecimal getIdEnteGestore() {
        return this.idEnteGestore;
    }

    public void setIdEnteGestore(BigDecimal idEnteGestore) {
        this.idEnteGestore = idEnteGestore;
    }

    @Column(name = "ID_USER_IAM")
    public BigDecimal getIdUserIam() {
        return this.idUserIam;
    }

    public void setIdUserIam(BigDecimal idUserIam) {
        this.idUserIam = idUserIam;
    }

    @Column(name = "NM_AMBIENTE")
    public String getNmAmbiente() {
        return this.nmAmbiente;
    }

    public void setNmAmbiente(String nmAmbiente) {
        this.nmAmbiente = nmAmbiente;
    }

    @Column(name = "NM_ENTE_CONSERV")
    public String getNmEnteConserv() {
        return this.nmEnteConserv;
    }

    public void setNmEnteConserv(String nmEnteConserv) {
        this.nmEnteConserv = nmEnteConserv;
    }

    @Column(name = "NM_ENTE_GESTORE")
    public String getNmEnteGestore() {
        return this.nmEnteGestore;
    }

    public void setNmEnteGestore(String nmEnteGestore) {
        this.nmEnteGestore = nmEnteGestore;
    }

}
