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
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the ORG_V_SERV_TI_SERV_DA_EROG database table.
 *
 */
@Entity
@Table(schema = "SACER_IAM", name = "ORG_V_SERV_TI_SERV_DA_EROG")

public class OrgVServTiServDaErog implements Serializable {

    private static final long serialVersionUID = 1L;
    private String cdTipoServizio;
    private Date dtErog;
    private Date dtDecAccordo;
    private BigDecimal idAccordoEnte;
    private BigDecimal idEnteConvenz;
    private BigDecimal idServizioErogato;
    private BigDecimal idTipoServizio;
    private String listStrut;
    private String nmEnteConvenz;
    private String nmServizioErogato;

    public OrgVServTiServDaErog() {/* Hibernate */
    }

    @Column(name = "CD_TIPO_SERVIZIO")
    public String getCdTipoServizio() {
        return this.cdTipoServizio;
    }

    public void setCdTipoServizio(String cdTipoServizio) {
        this.cdTipoServizio = cdTipoServizio;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_EROG")
    public Date getDtErog() {
        return this.dtErog;
    }

    public void setDtErog(Date dtErog) {
        this.dtErog = dtErog;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_DEC_ACCORDO")
    public Date getDtDecAccordo() {
        return this.dtDecAccordo;
    }

    public void setDtDecAccordo(Date dtDecAccordo) {
        this.dtDecAccordo = dtDecAccordo;
    }

    @Column(name = "ID_ACCORDO_ENTE")
    public BigDecimal getIdAccordoEnte() {
        return this.idAccordoEnte;
    }

    public void setIdAccordoEnte(BigDecimal idAccordoEnte) {
        this.idAccordoEnte = idAccordoEnte;
    }

    @Column(name = "ID_ENTE_CONVENZ")
    public BigDecimal getIdEnteConvenz() {
        return this.idEnteConvenz;
    }

    public void setIdEnteConvenz(BigDecimal idEnteConvenz) {
        this.idEnteConvenz = idEnteConvenz;
    }

    @Id
    @Column(name = "ID_SERVIZIO_EROGATO")
    public BigDecimal getIdServizioErogato() {
        return this.idServizioErogato;
    }

    public void setIdServizioErogato(BigDecimal idServizioErogato) {
        this.idServizioErogato = idServizioErogato;
    }

    @Column(name = "ID_TIPO_SERVIZIO")
    public BigDecimal getIdTipoServizio() {
        return this.idTipoServizio;
    }

    public void setIdTipoServizio(BigDecimal idTipoServizio) {
        this.idTipoServizio = idTipoServizio;
    }

    @Column(name = "LIST_STRUT")
    public String getListStrut() {
        return this.listStrut;
    }

    public void setListStrut(String listStrut) {
        this.listStrut = listStrut;
    }

    @Column(name = "NM_ENTE_CONVENZ")
    public String getNmEnteConvenz() {
        return this.nmEnteConvenz;
    }

    public void setNmEnteConvenz(String nmEnteConvenz) {
        this.nmEnteConvenz = nmEnteConvenz;
    }

    @Column(name = "NM_SERVIZIO_EROGATO")
    public String getNmServizioErogato() {
        return this.nmServizioErogato;
    }

    public void setNmServizioErogato(String nmServizioErogato) {
        this.nmServizioErogato = nmServizioErogato;
    }

}
