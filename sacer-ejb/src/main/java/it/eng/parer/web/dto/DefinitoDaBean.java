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

package it.eng.parer.web.dto;

import java.math.BigDecimal;

/**
 *
 * @author Gilioli_P
 */
public class DefinitoDaBean {
    private BigDecimal idAttribDatiSpec;
    private String tiEntitaSacer;
    private String nmTipoDoc;
    private String nmTipoUnitaDoc;
    private String nmSistemaMigraz;
    private String nmAttribDatiSpec;
    private String tiOper;
    private String dlValore;

    public BigDecimal getIdAttribDatiSpec() {
        return idAttribDatiSpec;
    }

    public void setIdAttribDatiSpec(BigDecimal idAttribDatiSpec) {
        this.idAttribDatiSpec = idAttribDatiSpec;
    }

    public String getTiEntitaSacer() {
        return tiEntitaSacer;
    }

    public void setTiEntitaSacer(String tiEntitaSacer) {
        this.tiEntitaSacer = tiEntitaSacer;
    }

    public String getNmTipoDoc() {
        return nmTipoDoc;
    }

    public void setNmTipoDoc(String nmTipoDoc) {
        this.nmTipoDoc = nmTipoDoc;
    }

    public String getNmTipoUnitaDoc() {
        return nmTipoUnitaDoc;
    }

    public void setNmTipoUnitaDoc(String nmTipoUnitaDoc) {
        this.nmTipoUnitaDoc = nmTipoUnitaDoc;
    }

    public String getNmSistemaMigraz() {
        return nmSistemaMigraz;
    }

    public void setNmSistemaMigraz(String nmSistemaMigraz) {
        this.nmSistemaMigraz = nmSistemaMigraz;
    }

    public String getNmAttribDatiSpec() {
        return nmAttribDatiSpec;
    }

    public void setNmAttribDatiSpec(String nmAttribDatiSpec) {
        this.nmAttribDatiSpec = nmAttribDatiSpec;
    }

    public String getTiOper() {
        return tiOper;
    }

    public void setTiOper(String tiOper) {
        this.tiOper = tiOper;
    }

    public String getDlValore() {
        return dlValore;
    }

    public void setDlValore(String dlValore) {
        this.dlValore = dlValore;
    }
}
