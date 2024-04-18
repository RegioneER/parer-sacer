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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The persistent class for the DEC_FORMATO_GRUPPO_PROPRIETA database table.
 *
 */
@Entity
@Cacheable(true)
@Table(name = "DEC_FORMATO_GRUPPO_PROPRIETA")
public class DecFormatoGruppoProprieta implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idFormatoGruppoProprieta;
    private String nmFormatoGruppoProprieta;
    private List<DecFormatoProprieta> decFormatoProprietas = new ArrayList<>();

    public DecFormatoGruppoProprieta() {/* Hibernate */
    }

    @Id
    @SequenceGenerator(name = "DEC_FORMATO_GRUPPO_PROPRIETA_GENERATOR", sequenceName = "SDEC_FORMATO_GRUPPO_PROPRIETA", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DEC_FORMATO_GRUPPO_PROPRIETA_GENERATOR")
    @Column(name = "ID_FORMATO_GRUPPO_PROPRIETA")
    public Long getIdFormatoGruppoProprieta() {
        return idFormatoGruppoProprieta;
    }

    public void setIdFormatoGruppoProprieta(Long idFormatoGruppoProprieta) {
        this.idFormatoGruppoProprieta = idFormatoGruppoProprieta;
    }

    @Column(name = "NM_FORMATO_GRUPPO_PROPRIETA")
    public String getNmFormatoGruppoProprieta() {
        return nmFormatoGruppoProprieta;
    }

    public void setNmFormatoGruppoProprieta(String nmFormatoGruppoProprieta) {
        this.nmFormatoGruppoProprieta = nmFormatoGruppoProprieta;
    }

    @OneToMany(mappedBy = "decFormatoGruppoProprieta")
    public List<DecFormatoProprieta> getDecFormatoProprietas() {
        return decFormatoProprietas;
    }

    public void setDecFormatoProprietas(List<DecFormatoProprieta> decFormatoProprietas) {
        this.decFormatoProprietas = decFormatoProprietas;
    }

}
