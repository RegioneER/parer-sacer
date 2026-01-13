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
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.EmbeddedId;

/**
 * The persistent class for the FAS_V_LIS_UD_BY_FASC database table.
 */
@Entity
@Table(name = "FAS_V_LIS_UD_BY_FASC")
public class FasVLisUdByFasc implements Serializable {

    private static final long serialVersionUID = 1L;

    private String cdKeyUnitaDocNormaliz;

    private Date dtVersMax;

    public FasVLisUdByFasc() {
        /* Hibernate */
    }

    @Column(name = "CD_KEY_UNITA_DOC_NORMALIZ")
    public String getCdKeyUnitaDocNormaliz() {
        return this.cdKeyUnitaDocNormaliz;
    }

    public void setCdKeyUnitaDocNormaliz(String cdKeyUnitaDocNormaliz) {
        this.cdKeyUnitaDocNormaliz = cdKeyUnitaDocNormaliz;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_VERS_MAX")
    public Date getDtVersMax() {
        return this.dtVersMax;
    }

    public void setDtVersMax(Date dtVersMax) {
        this.dtVersMax = dtVersMax;
    }

    private FasVLisUdByFascId id;

    @EmbeddedId()
    public FasVLisUdByFascId getId() {
        return id;
    }

    public void setId(FasVLisUdByFascId id) {
        this.id = id;
    }
}
