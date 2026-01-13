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

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Copia modificata della entity JPA DecCriterioAttrib per la gestione dei dati specifici in fase di
 * costruzione query
 *
 * @author Gilioli_P
 */
public class DecCriterioAttribBean implements Serializable, Comparable<DecCriterioAttribBean> {

    private static final long serialVersionUID = 1L;
    private long idCriterioAttrib;
    private String dsListaVersioniXsd;
    private String nmSistemaMigraz;
    private String nmTipoDoc;
    private String nmTipoUnitaDoc;
    private String tiEntitaSacer;
    private BigDecimal idAttribDatiSpec;
    private BigDecimal ordine;
    private DecCriterioDatiSpecBean decCriterioDatiSpec;

    public DecCriterioAttribBean() {
    }

    public long getIdCriterioAttrib() {
        return this.idCriterioAttrib;
    }

    public void setIdCriterioAttrib(long idCriterioAttrib) {
        this.idCriterioAttrib = idCriterioAttrib;
    }

    public String getDsListaVersioniXsd() {
        return this.dsListaVersioniXsd;
    }

    public void setDsListaVersioniXsd(String dsListaVersioniXsd) {
        this.dsListaVersioniXsd = dsListaVersioniXsd;
    }

    public String getNmSistemaMigraz() {
        return this.nmSistemaMigraz;
    }

    public void setNmSistemaMigraz(String nmSistemaMigraz) {
        this.nmSistemaMigraz = nmSistemaMigraz;
    }

    public String getNmTipoDoc() {
        return this.nmTipoDoc;
    }

    public void setNmTipoDoc(String nmTipoDoc) {
        this.nmTipoDoc = nmTipoDoc;
    }

    public String getNmTipoUnitaDoc() {
        return this.nmTipoUnitaDoc;
    }

    public void setNmTipoUnitaDoc(String nmTipoUnitaDoc) {
        this.nmTipoUnitaDoc = nmTipoUnitaDoc;
    }

    public String getTiEntitaSacer() {
        return this.tiEntitaSacer;
    }

    public void setTiEntitaSacer(String tiEntitaSacer) {
        this.tiEntitaSacer = tiEntitaSacer;
    }

    public DecCriterioDatiSpecBean getDecCriterioDatiSpec() {
        return this.decCriterioDatiSpec;
    }

    public void setDecCriterioDatiSpec(DecCriterioDatiSpecBean decCriterioDatiSpec) {
        this.decCriterioDatiSpec = decCriterioDatiSpec;
    }

    public BigDecimal getIdAttribDatiSpec() {
        return idAttribDatiSpec;
    }

    public void setIdAttribDatiSpec(BigDecimal idAttribDatiSpec) {
        this.idAttribDatiSpec = idAttribDatiSpec;
    }

    public BigDecimal getOrdine() {
        return ordine;
    }

    public void setOrdine(BigDecimal ordine) {
        this.ordine = ordine;
    }

    @Override
    public int compareTo(DecCriterioAttribBean o) {
        // Se sono dello stesso tipo
        if (this.ordine.compareTo(o.ordine) == 0) {
            // Se sono tipi unità documentaria
            if (this.nmTipoUnitaDoc != null) {
                return this.nmTipoUnitaDoc.compareTo(o.nmTipoUnitaDoc);
            } else if (this.nmTipoDoc != null) {
                return this.nmTipoDoc.compareTo(o.nmTipoDoc);
            } else {
                return this.nmSistemaMigraz.compareTo(o.nmSistemaMigraz);
            }
        } else {
            return this.ordine.compareTo(o.ordine);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ordine == null) ? 0 : ordine.hashCode());
        result = prime * result + ((nmSistemaMigraz == null) ? 0 : nmSistemaMigraz.hashCode());
        result = prime * result + ((nmTipoDoc == null) ? 0 : nmTipoDoc.hashCode());
        result = prime * result + ((nmTipoUnitaDoc == null) ? 0 : nmTipoUnitaDoc.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass().equals(DecCriterioAttribBean.class)) {
            DecCriterioAttribBean o = (DecCriterioAttribBean) obj;
            if (!this.ordine.equals(o.ordine)) {
                if (this.nmTipoUnitaDoc != null) {
                    return this.nmTipoUnitaDoc.equals(o.nmTipoUnitaDoc);
                } else if (this.nmTipoDoc != null) {
                    return this.nmTipoDoc.equals(o.nmTipoDoc);
                } else {
                    return this.nmSistemaMigraz.equals(o.nmSistemaMigraz);
                }
            }
        }
        return false;
    }

}
