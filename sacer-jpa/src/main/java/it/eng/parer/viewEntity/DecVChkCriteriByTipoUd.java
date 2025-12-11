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

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import javax.persistence.Table;

/**
 * The persistent class for the DEC_V_CHK_CRITERI_BY_TIPO_UD database table.
 */
@Entity
@Table(name = "DEC_V_CHK_CRITERI_BY_TIPO_UD")

public class DecVChkCriteriByTipoUd implements Serializable {

    private static final long serialVersionUID = 1L;

    private String dsCriterioNonCoerente;

    private String flCriterioCoerente;

    private String flCriterioRaggrFisc;

    private String flCriterioRaggrStandard;

    private String nmCriterioRaggr;

    private String nmTipoUnitaDoc;

    public DecVChkCriteriByTipoUd() {/* Hibernate */
    }

    @Column(name = "DS_CRITERIO_NON_COERENTE")
    public String getDsCriterioNonCoerente() {
        return this.dsCriterioNonCoerente;
    }

    public void setDsCriterioNonCoerente(String dsCriterioNonCoerente) {
        this.dsCriterioNonCoerente = dsCriterioNonCoerente;
    }

    @Column(name = "FL_CRITERIO_COERENTE", columnDefinition = "char(1)")
    public String getFlCriterioCoerente() {
        return this.flCriterioCoerente;
    }

    public void setFlCriterioCoerente(String flCriterioCoerente) {
        this.flCriterioCoerente = flCriterioCoerente;
    }

    @Column(name = "FL_CRITERIO_RAGGR_FISC", columnDefinition = "char(1)")
    public String getFlCriterioRaggrFisc() {
        return this.flCriterioRaggrFisc;
    }

    public void setFlCriterioRaggrFisc(String flCriterioRaggrFisc) {
        this.flCriterioRaggrFisc = flCriterioRaggrFisc;
    }

    @Column(name = "FL_CRITERIO_RAGGR_STANDARD", columnDefinition = "char(1)")
    public String getFlCriterioRaggrStandard() {
        return this.flCriterioRaggrStandard;
    }

    public void setFlCriterioRaggrStandard(String flCriterioRaggrStandard) {
        this.flCriterioRaggrStandard = flCriterioRaggrStandard;
    }

    @Column(name = "NM_CRITERIO_RAGGR")
    public String getNmCriterioRaggr() {
        return this.nmCriterioRaggr;
    }

    public void setNmCriterioRaggr(String nmCriterioRaggr) {
        this.nmCriterioRaggr = nmCriterioRaggr;
    }

    @Column(name = "NM_TIPO_UNITA_DOC")
    public String getNmTipoUnitaDoc() {
        return this.nmTipoUnitaDoc;
    }

    public void setNmTipoUnitaDoc(String nmTipoUnitaDoc) {
        this.nmTipoUnitaDoc = nmTipoUnitaDoc;
    }

    private DecVChkCriteriByTipoUdId decVChkCriteriByTipoUdId;

    @EmbeddedId()
    public DecVChkCriteriByTipoUdId getDecVChkCriteriByTipoUdId() {
        return decVChkCriteriByTipoUdId;
    }

    public void setDecVChkCriteriByTipoUdId(DecVChkCriteriByTipoUdId decVChkCriteriByTipoUdId) {
        this.decVChkCriteriByTipoUdId = decVChkCriteriByTipoUdId;
    }
}
