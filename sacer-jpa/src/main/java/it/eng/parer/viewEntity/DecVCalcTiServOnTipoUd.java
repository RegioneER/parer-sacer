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

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.Table;

/**
 * The persistent class for the DEC_V_CALC_TI_SERV_ON_TIPO_UD database table.
 *
 */
@Entity
@Table(name = "DEC_V_CALC_TI_SERV_ON_TIPO_UD")

public class DecVCalcTiServOnTipoUd implements Serializable {

    private static final long serialVersionUID = 1L;
    private DecVCalcTiServOnTipoUdId decVCalcTiServOnTipoUdId;
    private BigDecimal idTipoServizioAttiv;
    private BigDecimal idTipoServizioConserv;
    private String cdAlgoTariffario;

    @EmbeddedId
    public DecVCalcTiServOnTipoUdId getDecVCalcTiServOnTipoUdId() {
        return decVCalcTiServOnTipoUdId;
    }

    public void setDecVCalcTiServOnTipoUdId(DecVCalcTiServOnTipoUdId decVCalcTiServOnTipoUdId) {
        this.decVCalcTiServOnTipoUdId = decVCalcTiServOnTipoUdId;
    }

    public DecVCalcTiServOnTipoUd() {/* Hibernate */
    }

    @Column(name = "ID_TIPO_SERVIZIO_ATTIV")
    public BigDecimal getIdTipoServizioAttiv() {
        return this.idTipoServizioAttiv;
    }

    public void setIdTipoServizioAttiv(BigDecimal idTipoServizioAttiv) {
        this.idTipoServizioAttiv = idTipoServizioAttiv;
    }

    @Column(name = "ID_TIPO_SERVIZIO_CONSERV")
    public BigDecimal getIdTipoServizioConserv() {
        return this.idTipoServizioConserv;
    }

    public void setIdTipoServizioConserv(BigDecimal idTipoServizioConserv) {
        this.idTipoServizioConserv = idTipoServizioConserv;
    }

    @Id
    @Column(name = "CD_ALGO_TARIFFARIO")
    public String getCdAlgoTariffario() {
        return this.cdAlgoTariffario;
    }

    public void setCdAlgoTariffario(String cdAlgoTariffario) {
        this.cdAlgoTariffario = cdAlgoTariffario;
    }

}
