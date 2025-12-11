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
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import it.eng.parer.entity.constraint.ElvStatoElencoVer.TiStatoElenco;

/**
 * The persistent class for the ELV_STATO_ELENCO_VERS database table.
 *
 */
@Entity
@Table(name = "ELV_STATO_ELENCO_VERS")

public class ElvStatoElencoVer implements Serializable {

    private static final long serialVersionUID = 1L;
    private long idStatoElencoVers;
    private String dsCondStatoElencoVers;
    private BigDecimal idTiEveStatoElencoVers;
    private BigDecimal pgStatoElencoVers;
    private TiStatoElenco tiStatoElenco;
    private Date tsStatoElencoVers;
    private ElvElencoVer elvElencoVer;
    private IamUser iamUser;

    public ElvStatoElencoVer() {
        // hibernate
    }

    @Id
    @SequenceGenerator(name = "ELV_STATO_ELENCO_VERS_IDSTATOELENCOVERS_GENERATOR", sequenceName = "SELV_STATO_ELENCO_VERS", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ELV_STATO_ELENCO_VERS_IDSTATOELENCOVERS_GENERATOR")
    @Column(name = "ID_STATO_ELENCO_VERS")
    public long getIdStatoElencoVers() {
        return this.idStatoElencoVers;
    }

    public void setIdStatoElencoVers(long idStatoElencoVers) {
        this.idStatoElencoVers = idStatoElencoVers;
    }

    @Column(name = "DS_COND_STATO_ELENCO_VERS")
    public String getDsCondStatoElencoVers() {
        return this.dsCondStatoElencoVers;
    }

    public void setDsCondStatoElencoVers(String dsCondStatoElencoVers) {
        this.dsCondStatoElencoVers = dsCondStatoElencoVers;
    }

    @Column(name = "ID_TI_EVE_STATO_ELENCO_VERS")
    public BigDecimal getIdTiEveStatoElencoVers() {
        return this.idTiEveStatoElencoVers;
    }

    public void setIdTiEveStatoElencoVers(BigDecimal idTiEveStatoElencoVers) {
        this.idTiEveStatoElencoVers = idTiEveStatoElencoVers;
    }

    @Column(name = "PG_STATO_ELENCO_VERS")
    public BigDecimal getPgStatoElencoVers() {
        return this.pgStatoElencoVers;
    }

    public void setPgStatoElencoVers(BigDecimal pgStatoElencoVers) {
        this.pgStatoElencoVers = pgStatoElencoVers;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "TI_STATO_ELENCO")
    public TiStatoElenco getTiStatoElenco() {
        return this.tiStatoElenco;
    }

    public void setTiStatoElenco(TiStatoElenco tiStatoElenco) {
        this.tiStatoElenco = tiStatoElenco;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TS_STATO_ELENCO_VERS")
    public Date getTsStatoElencoVers() {
        return this.tsStatoElencoVers;
    }

    public void setTsStatoElencoVers(Date tsStatoElencoVers) {
        this.tsStatoElencoVers = tsStatoElencoVers;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST })
    @JoinColumn(name = "ID_ELENCO_VERS")
    public ElvElencoVer getElvElencoVer() {
        return this.elvElencoVer;
    }

    public void setElvElencoVer(ElvElencoVer elvElencoVer) {
        this.elvElencoVer = elvElencoVer;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER_IAM")
    public IamUser getIamUser() {
        return this.iamUser;
    }

    public void setIamUser(IamUser iamUser) {
        this.iamUser = iamUser;
    }

}
