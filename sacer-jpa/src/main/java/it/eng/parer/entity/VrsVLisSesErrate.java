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
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the VRS_SESSIONE_VERS database table.
 */
@Entity
@Table(name = "VRS_V_LIS_SES_ERRATE")
@SuppressWarnings("unused")
// usata in jpql
public class VrsVLisSesErrate implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idSessioneVers;
    private LocalDateTime dtChiusura;
    private String flSessioneErrVerif;
    private String dsErr;
    private String cdErr;

    @Id
    @Column(name = "ID_SESSIONE_VERS")
    public Long getIdSessioneVers() {
        return this.idSessioneVers;
    }

    public void setIdSessioneVers(Long idSessioneVers) {
        this.idSessioneVers = idSessioneVers;
    }

    @Column(name = "DT_CHIUSURA")
    public LocalDateTime getDtChiusura() {
        return this.dtChiusura;
    }

    public void setDtChiusura(LocalDateTime dtChiusura) {
        this.dtChiusura = dtChiusura;
    }

    @Column(name = "DS_ERR")
    public String getDsErr() {
        return dsErr;
    }

    public void setDsErr(String dsErr) {
        this.dsErr = dsErr;
    }

    @Column(name = "CD_ERR")
    public String getCdErr() {
        return cdErr;
    }

    public void setCdErr(String cdErr) {
        this.cdErr = cdErr;
    }

    @Column(name = "FL_SESSIONE_ERR_VERIF", columnDefinition = "char")
    public String getFlSessioneErrVerif() {
        return flSessioneErrVerif;
    }

    public void setFlSessioneErrVerif(String flSessioneErrVerif) {
        this.flSessioneErrVerif = flSessioneErrVerif;
    }
}
