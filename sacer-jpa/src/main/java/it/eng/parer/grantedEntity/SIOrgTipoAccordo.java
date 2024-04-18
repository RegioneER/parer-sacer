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

package it.eng.parer.grantedEntity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the ORG_TIPO_ACCORDO database table.
 *
 */
@Entity
@Table(schema = "SACER_IAM", name = "ORG_TIPO_ACCORDO")
public class SIOrgTipoAccordo implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idTipoAccordo;
    private String cdAlgoTariffario;
    private String cdTipoAccordo;
    private String dsTipoAccordo;
    private Date dtIstituz;
    private Date dtSoppres;
    private String flPagamento;

    public SIOrgTipoAccordo() {
    }

    public SIOrgTipoAccordo(long idTipoAccordo) {
        this.idTipoAccordo = idTipoAccordo;
    }

    @Id
    @Column(name = "ID_TIPO_ACCORDO")
    public Long getIdTipoAccordo() {
        return this.idTipoAccordo;
    }

    public void setIdTipoAccordo(Long idTipoAccordo) {
        this.idTipoAccordo = idTipoAccordo;
    }

    @Column(name = "CD_ALGO_TARIFFARIO")
    public String getCdAlgoTariffario() {
        return this.cdAlgoTariffario;
    }

    public void setCdAlgoTariffario(String cdAlgoTariffario) {
        this.cdAlgoTariffario = cdAlgoTariffario;
    }

    @Column(name = "CD_TIPO_ACCORDO")
    public String getCdTipoAccordo() {
        return this.cdTipoAccordo;
    }

    public void setCdTipoAccordo(String cdTipoAccordo) {
        this.cdTipoAccordo = cdTipoAccordo;
    }

    @Column(name = "DS_TIPO_ACCORDO")
    public String getDsTipoAccordo() {
        return this.dsTipoAccordo;
    }

    public void setDsTipoAccordo(String dsTipoAccordo) {
        this.dsTipoAccordo = dsTipoAccordo;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_ISTITUZ")
    public Date getDtIstituz() {
        return this.dtIstituz;
    }

    public void setDtIstituz(Date dtIstituz) {
        this.dtIstituz = dtIstituz;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_SOPPRES")
    public Date getDtSoppres() {
        return this.dtSoppres;
    }

    public void setDtSoppres(Date dtSoppres) {
        this.dtSoppres = dtSoppres;
    }

    @Column(name = "FL_PAGAMENTO", columnDefinition = "char(1)")
    public String getFlPagamento() {
        return this.flPagamento;
    }

    public void setFlPagamento(String flPagamento) {
        this.flPagamento = flPagamento;
    }

}
