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

package it.eng.parer.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the VRS_ARK_PATH_DT_VERS database table.
 */
@Entity
@Table(name = "VRS_ARK_PATH_DT_VERS")
public class VrsArkPathDtVers implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idArkPathDtVers;

    private String dsArk;

    private Date dtArkPathDtVers;

    private String tiArkPath;

    private VrsPathDtVers vrsPathDtVers;

    public VrsArkPathDtVers() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_ARK_PATH_DT_VERS")
    @GenericGenerator(name = "SVRS_ARK_PATH_DT_VERS_ID_ARK_PATH_DT_VERS_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SVRS_ARK_PATH_DT_VERS"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SVRS_ARK_PATH_DT_VERS_ID_ARK_PATH_DT_VERS_GENERATOR")
    public Long getIdArkPathDtVers() {
        return this.idArkPathDtVers;
    }

    public void setIdArkPathDtVers(Long idArkPathDtVers) {
        this.idArkPathDtVers = idArkPathDtVers;
    }

    @Column(name = "DS_ARK")
    public String getDsArk() {
        return this.dsArk;
    }

    public void setDsArk(String dsArk) {
        this.dsArk = dsArk;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_ARK_PATH_DT_VERS")
    public Date getDtArkPathDtVers() {
        return this.dtArkPathDtVers;
    }

    public void setDtArkPathDtVers(Date dtArkPathDtVers) {
        this.dtArkPathDtVers = dtArkPathDtVers;
    }

    @Column(name = "TI_ARK_PATH")
    public String getTiArkPath() {
        return this.tiArkPath;
    }

    public void setTiArkPath(String tiArkPath) {
        this.tiArkPath = tiArkPath;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PATH_DT_VERS")
    public VrsPathDtVers getVrsPathDtVers() {
        return this.vrsPathDtVers;
    }

    public void setVrsPathDtVers(VrsPathDtVers vrsPathDtVers) {
        this.vrsPathDtVers = vrsPathDtVers;
    }

}
