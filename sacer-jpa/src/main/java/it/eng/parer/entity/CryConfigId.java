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
import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable()
public class CryConfigId implements Serializable {

    public CryConfigId() {/* Hibernate */
    }

    public CryConfigId(String subjectdn, BigDecimal niOrdUrlDistribCrl, String keyId) {
        this.subjectdn = subjectdn;
        this.niOrdUrlDistribCrl = niOrdUrlDistribCrl;
        this.keyId = keyId;
    }

    private String subjectdn;

    public String getSubjectdn() {
        return subjectdn;
    }

    public void setSubjectdn(String subjectdn) {
        this.subjectdn = subjectdn;
    }

    private BigDecimal niOrdUrlDistribCrl;

    @Column(name = "NI_ORD_URL_DISTRIB_CRL")
    public BigDecimal getNiOrdUrlDistribCrl() {
        return niOrdUrlDistribCrl;
    }

    public void setNiOrdUrlDistribCrl(BigDecimal niOrdUrlDistribCrl) {
        this.niOrdUrlDistribCrl = niOrdUrlDistribCrl;
    }

    private String keyId;

    @Column(name = "SUBJECT_KEY_ID")
    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + Objects.hashCode(this.subjectdn);
        hash = 17 * hash + Objects.hashCode(this.niOrdUrlDistribCrl);
        hash = 17 * hash + Objects.hashCode(this.keyId);
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
        final CryConfigId other = (CryConfigId) obj;
        if (!Objects.equals(this.subjectdn, other.subjectdn)) {
            return false;
        }
        if (!Objects.equals(this.keyId, other.keyId)) {
            return false;
        }
        if (!Objects.equals(this.niOrdUrlDistribCrl, other.niOrdUrlDistribCrl)) {
            return false;
        }
        return true;
    }
}
