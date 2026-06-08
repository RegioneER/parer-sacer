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
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * The persistent class for the ARO_STATO_PROP_SCARTO_VERS database table.
 *
 */
@Entity
@Table(name = "ARO_STATO_PROP_SCARTO_VERS")
@NamedQuery(name = "AroStatoPropScartoVers.findAll", query = "SELECT a FROM AroStatoPropScartoVers a")
public class AroStatoPropScartoVers implements Serializable {
    private static final long serialVersionUID = 1L;
    private long idStatoPropScartoVers;
    private String dsNotaPropScartoVers;
    private Date dtRegStatoPropScartoVers;
    private IamUser iamUser;
    private BigDecimal pgStatoPropScartoVers;
    private String tiStatoPropScartoVers;
    private AroPropScartoVers aroPropScartoVers;

    public AroStatoPropScartoVers() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_STATO_PROP_SCARTO_VERS")
    public long getIdStatoPropScartoVers() {
        return this.idStatoPropScartoVers;
    }

    public void setIdStatoPropScartoVers(long idStatoPropScartoVers) {
        this.idStatoPropScartoVers = idStatoPropScartoVers;
    }

    @Column(name = "DS_NOTA_PROP_SCARTO_VERS")
    public String getDsNotaPropScartoVers() {
        return this.dsNotaPropScartoVers;
    }

    public void setDsNotaPropScartoVers(String dsNotaPropScartoVers) {
        this.dsNotaPropScartoVers = dsNotaPropScartoVers;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_REG_STATO_PROP_SCARTO_VERS")
    public Date getDtRegStatoPropScartoVers() {
        return this.dtRegStatoPropScartoVers;
    }

    public void setDtRegStatoPropScartoVers(Date dtRegStatoPropScartoVers) {
        this.dtRegStatoPropScartoVers = dtRegStatoPropScartoVers;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER_IAM")
    public IamUser getIamUser() {
        return this.iamUser;
    }

    public void setIamUser(IamUser iamUser) {
        this.iamUser = iamUser;
    }

    @Column(name = "PG_STATO_PROP_SCARTO_VERS")
    public BigDecimal getPgStatoPropScartoVers() {
        return this.pgStatoPropScartoVers;
    }

    public void setPgStatoPropScartoVers(BigDecimal pgStatoPropScartoVers) {
        this.pgStatoPropScartoVers = pgStatoPropScartoVers;
    }

    @Column(name = "TI_STATO_PROP_SCARTO_VERS")
    public String getTiStatoPropScartoVers() {
        return this.tiStatoPropScartoVers;
    }

    public void setTiStatoPropScartoVers(String tiStatoPropScartoVers) {
        this.tiStatoPropScartoVers = tiStatoPropScartoVers;
    }

    // bi-directional many-to-one association to AroPropScartoVers
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PROP_SCARTO_VERS")
    public AroPropScartoVers getAroPropScartoVers() {
        return this.aroPropScartoVers;
    }

    public void setAroPropScartoVers(AroPropScartoVers aroPropScartoVers) {
        this.aroPropScartoVers = aroPropScartoVers;
    }

}
