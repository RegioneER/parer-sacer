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

package it.eng.parer.viewEntity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the ARO_V_LIS_ARCHIV_UNITA_DOC database table.
 * 
 */
@Entity
@Table(name = "ARO_V_LIS_ARCHIV_UNITA_DOC")
public class AroVLisArchivUnitaDoc implements Serializable {
    private static final long serialVersionUID = 1L;
    private String cdFascic;
    private String cdSottofascic;
    private String dsClassif;
    private String dsOggettoFascic;
    private String dsOggettoSottofascic;
    private BigDecimal idArchivSec;
    private BigDecimal idUnitaDoc;

    public AroVLisArchivUnitaDoc() {/* Hibernate */
    }

    @Column(name = "CD_FASCIC")
    public String getCdFascic() {
        return this.cdFascic;
    }

    public void setCdFascic(String cdFascic) {
        this.cdFascic = cdFascic;
    }

    @Column(name = "CD_SOTTOFASCIC")
    public String getCdSottofascic() {
        return this.cdSottofascic;
    }

    public void setCdSottofascic(String cdSottofascic) {
        this.cdSottofascic = cdSottofascic;
    }

    @Column(name = "DS_CLASSIF")
    public String getDsClassif() {
        return this.dsClassif;
    }

    public void setDsClassif(String dsClassif) {
        this.dsClassif = dsClassif;
    }

    @Column(name = "DS_OGGETTO_FASCIC")
    public String getDsOggettoFascic() {
        return this.dsOggettoFascic;
    }

    public void setDsOggettoFascic(String dsOggettoFascic) {
        this.dsOggettoFascic = dsOggettoFascic;
    }

    @Column(name = "DS_OGGETTO_SOTTOFASCIC")
    public String getDsOggettoSottofascic() {
        return this.dsOggettoSottofascic;
    }

    public void setDsOggettoSottofascic(String dsOggettoSottofascic) {
        this.dsOggettoSottofascic = dsOggettoSottofascic;
    }

    @Id
    @Column(name = "ID_ARCHIV_SEC")
    public BigDecimal getIdArchivSec() {
        return this.idArchivSec;
    }

    public void setIdArchivSec(BigDecimal idArchivSec) {
        this.idArchivSec = idArchivSec;
    }

    @Column(name = "ID_UNITA_DOC")
    public BigDecimal getIdUnitaDoc() {
        return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
        this.idUnitaDoc = idUnitaDoc;
    }

}
