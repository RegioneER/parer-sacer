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

package it.eng.parer.serie.dto;

import java.math.BigDecimal;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Bonora_L
 */
public class RegistroTipoUnitaDoc {

    BigDecimal idRegistroUnitaDoc;
    String cdRegistroUnitaDoc;
    BigDecimal idTipoUnitaDoc;
    String nmTipoUnitaDoc;

    public RegistroTipoUnitaDoc(BigDecimal idRegistroUnitaDoc, String cdRegistroUnitaDoc,
	    BigDecimal idTipoUnitaDoc, String nmTipoUnitaDoc) {
	this.idRegistroUnitaDoc = idRegistroUnitaDoc;
	this.cdRegistroUnitaDoc = cdRegistroUnitaDoc;
	this.idTipoUnitaDoc = idTipoUnitaDoc;
	this.nmTipoUnitaDoc = nmTipoUnitaDoc;
    }

    public BigDecimal getIdRegistroUnitaDoc() {
	return idRegistroUnitaDoc;
    }

    public void setIdRegistroUnitaDoc(BigDecimal idRegistroUnitaDoc) {
	this.idRegistroUnitaDoc = idRegistroUnitaDoc;
    }

    public BigDecimal getIdTipoUnitaDoc() {
	return idTipoUnitaDoc;
    }

    public void setIdTipoUnitaDoc(BigDecimal idTipoUnitaDoc) {
	this.idTipoUnitaDoc = idTipoUnitaDoc;
    }

    public String getCdRegistroUnitaDoc() {
	return cdRegistroUnitaDoc;
    }

    public void setCdRegistroUnitaDoc(String cdRegistroUnitaDoc) {
	this.cdRegistroUnitaDoc = cdRegistroUnitaDoc;
    }

    public String getNmTipoUnitaDoc() {
	return nmTipoUnitaDoc;
    }

    public void setNmTipoUnitaDoc(String nmTipoUnitaDoc) {
	this.nmTipoUnitaDoc = nmTipoUnitaDoc;
    }

    @Override
    public int hashCode() {
	int hash = 3;
	hash = 41 * hash + Objects.hashCode(this.idRegistroUnitaDoc);
	hash = 41 * hash + Objects.hashCode(this.cdRegistroUnitaDoc);
	hash = 41 * hash + Objects.hashCode(this.idTipoUnitaDoc);
	hash = 41 * hash + Objects.hashCode(this.nmTipoUnitaDoc);
	return hash;
    }

    @Override
    public boolean equals(Object obj) {
	if (this == obj) {
	    return true;
	}
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final RegistroTipoUnitaDoc other = (RegistroTipoUnitaDoc) obj;
	if (this.getIdRegistroUnitaDoc().compareTo(other.getIdRegistroUnitaDoc()) != 0) {
	    return false;
	}
	if (this.getIdTipoUnitaDoc().compareTo(other.getIdTipoUnitaDoc()) != 0) {
	    return false;
	}
	if (!StringUtils.equals(this.getCdRegistroUnitaDoc(), other.getCdRegistroUnitaDoc())) {
	    return false;
	}
	if (!StringUtils.equals(this.getNmTipoUnitaDoc(), other.getNmTipoUnitaDoc())) {
	    return false;
	}

	return true;
    }

}
