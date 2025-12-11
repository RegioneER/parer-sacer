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

package it.eng.parer.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the ARO_ERR_RICH_ANNUL_VERS database table.
 */
@Entity
@Table(name = "ARO_ERR_RICH_ANNUL_VERS")

public class AroErrRichAnnulVers implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idErrRichAnnulVrs;

    private String dsErr;

    private BigDecimal pgErr;

    private String tiErr;

    private String tiGravita;

    private AroItemRichAnnulVers aroItemRichAnnulVers;

    public AroErrRichAnnulVers() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_ERR_RICH_ANNUL_VRS")
    @GenericGenerator(name = "SARO_ERR_RICH_ANNUL_VERS_ID_ERR_RICH_ANNUL_VRS_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SARO_ERR_RICH_ANNUL_VERS"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SARO_ERR_RICH_ANNUL_VERS_ID_ERR_RICH_ANNUL_VRS_GENERATOR")
    public Long getIdErrRichAnnulVrs() {
        return this.idErrRichAnnulVrs;
    }

    public void setIdErrRichAnnulVrs(Long idErrRichAnnulVrs) {
        this.idErrRichAnnulVrs = idErrRichAnnulVrs;
    }

    @Column(name = "DS_ERR")
    public String getDsErr() {
        return this.dsErr;
    }

    public void setDsErr(String dsErr) {
        this.dsErr = dsErr;
    }

    @Column(name = "PG_ERR")
    public BigDecimal getPgErr() {
        return this.pgErr;
    }

    public void setPgErr(BigDecimal pgErr) {
        this.pgErr = pgErr;
    }

    @Column(name = "TI_ERR")
    public String getTiErr() {
        return this.tiErr;
    }

    public void setTiErr(String tiErr) {
        this.tiErr = tiErr;
    }

    @Column(name = "TI_GRAVITA")
    public String getTiGravita() {
        return this.tiGravita;
    }

    public void setTiGravita(String tiGravita) {
        this.tiGravita = tiGravita;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ITEM_RICH_ANNUL_VERS")
    public AroItemRichAnnulVers getAroItemRichAnnulVers() {
        return this.aroItemRichAnnulVers;
    }

    public void setAroItemRichAnnulVers(AroItemRichAnnulVers aroItemRichAnnulVers) {
        this.aroItemRichAnnulVers = aroItemRichAnnulVers;
    }

}
