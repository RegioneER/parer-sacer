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

/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.web.dto;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author Gilioli_P
 */
public class MonitoraggioAttributiVersFallitiDaDocNonVersati implements Serializable {

    private static final long serialVersionUID = 1L;

    private String cdRegistroKeyUnitaDoc;
    private BigDecimal aaKeyUnitaDoc;
    private String cdKeyUnitaDoc;
    private String cdKeyDocVers;
    private BigDecimal idStrut;
    private String tipoVers;

    /**
     * @return the cdRegistroKeyUnitaDoc
     */
    public String getCdRegistroKeyUnitaDoc() {
        return cdRegistroKeyUnitaDoc;
    }

    /**
     * @param cdRegistroKeyUnitaDoc the cdRegistroKeyUnitaDoc to set
     */
    public void setCdRegistroKeyUnitaDoc(String cdRegistroKeyUnitaDoc) {
        this.cdRegistroKeyUnitaDoc = cdRegistroKeyUnitaDoc;
    }

    /**
     * @return the aaKeyUnitaDoc
     */
    public BigDecimal getAaKeyUnitaDoc() {
        return aaKeyUnitaDoc;
    }

    /**
     * @param aaKeyUnitaDoc the aaKeyUnitaDoc to set
     */
    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    /**
     * @return the cdKeyUnitaDoc
     */
    public String getCdKeyUnitaDoc() {
        return cdKeyUnitaDoc;
    }

    /**
     * @param cdKeyUnitaDoc the cdKeyUnitaDoc to set
     */
    public void setCdKeyUnitaDoc(String cdKeyUnitaDoc) {
        this.cdKeyUnitaDoc = cdKeyUnitaDoc;
    }

    /**
     * @return the cdKeyDocVers
     */
    public String getCdKeyDocVers() {
        return cdKeyDocVers;
    }

    /**
     * @param cdKeyDocVers the cdKeyDocVers to set
     */
    public void setCdKeyDocVers(String cdKeyDocVers) {
        this.cdKeyDocVers = cdKeyDocVers;
    }

    /**
     * @return the idStrut
     */
    public BigDecimal getIdStrut() {
        return idStrut;
    }

    /**
     * @param idStrut the idStrut to set
     */
    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    /**
     * @return the tipoVers
     */
    public String getTipoVers() {
        return tipoVers;
    }

    /**
     * @param tipoVers the tipoVers to set
     */
    public void setTipoVers(String tipoVers) {
        this.tipoVers = tipoVers;
    }

}
