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
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import it.eng.parer.entity.OrgTipoServizio;

/**
 * The persistent class for the ORG_SERVIZIO_EROG database table.
 */
@Entity
@Table(schema = "SACER_IAM", name = "ORG_SERVIZIO_EROG")

public class OrgServizioErog implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idServizioErogato;

    private Date dtErog;

    private String flPagamento;

    private BigDecimal imValoreTariffa;

    private String nmServizioErogato;

    private OrgTipoServizio orgTipoServizio;

    private SIOrgAccordoEnte orgAccordoEnte;

    public OrgServizioErog() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_SERVIZIO_EROGATO")
    public Long getIdServizioErogato() {
        return this.idServizioErogato;
    }

    public void setIdServizioErogato(Long idServizioErogato) {
        this.idServizioErogato = idServizioErogato;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_EROG")
    public Date getDtErog() {
        return this.dtErog;
    }

    public void setDtErog(Date dtErog) {
        this.dtErog = dtErog;
    }

    @Column(name = "FL_PAGAMENTO", columnDefinition = "char(1)")
    public String getFlPagamento() {
        return this.flPagamento;
    }

    public void setFlPagamento(String flPagamento) {
        this.flPagamento = flPagamento;
    }

    @Column(name = "IM_VALORE_TARIFFA")
    public BigDecimal getImValoreTariffa() {
        return this.imValoreTariffa;
    }

    public void setImValoreTariffa(BigDecimal imValoreTariffa) {
        this.imValoreTariffa = imValoreTariffa;
    }

    @Column(name = "NM_SERVIZIO_EROGATO")
    public String getNmServizioErogato() {
        return this.nmServizioErogato;
    }

    public void setNmServizioErogato(String nmServizioErogato) {
        this.nmServizioErogato = nmServizioErogato;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_SERVIZIO")
    public OrgTipoServizio getOrgTipoServizio() {
        return this.orgTipoServizio;
    }

    public void setOrgTipoServizio(OrgTipoServizio orgTipoServizio) {
        this.orgTipoServizio = orgTipoServizio;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ACCORDO_ENTE")
    public SIOrgAccordoEnte getOrgAccordoEnte() {
        return this.orgAccordoEnte;
    }

    public void setOrgAccordoEnte(SIOrgAccordoEnte orgAccordoEnte) {
        this.orgAccordoEnte = orgAccordoEnte;
    }
}
