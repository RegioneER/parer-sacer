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

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The persistent class for the DEC_FORMATO_PROPRIETA database table.
 *
 */
@Entity
@Cacheable(true)
@Table(name = "DEC_FORMATO_PROPRIETA")
public class DecFormatoProprieta implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idFormatoProprieta;
    private String nmFormatoProprieta;
    private Integer niPunteggioDefault;
    private DecFormatoGruppoProprieta decFormatoGruppoProprieta;

    public DecFormatoProprieta() {/* Hibernate */
    }

    @Id
    @SequenceGenerator(name = "DEC_FORMATO_PROPRIETA_GENERATOR", sequenceName = "SDEC_FORMATO_PROPRIETA", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DEC_FORMATO_PROPRIETA_GENERATOR")
    @Column(name = "ID_FORMATO_PROPRIETA")
    public Long getIdFormatoProprieta() {
        return idFormatoProprieta;
    }

    public void setIdFormatoProprieta(Long idFormatoProprieta) {
        this.idFormatoProprieta = idFormatoProprieta;
    }

    @Column(name = "NM_FORMATO_PROPRIETA")
    public String getNmFormatoProprieta() {
        return nmFormatoProprieta;
    }

    public void setNmFormatoProprieta(String nmFormatoProprieta) {
        this.nmFormatoProprieta = nmFormatoProprieta;
    }

    @Column(name = "NI_PUNTEGGIO_DEFAULT")
    public Integer getNiPunteggioDefault() {
        return niPunteggioDefault;
    }

    public void setNiPunteggioDefault(Integer niPunteggioDefault) {
        this.niPunteggioDefault = niPunteggioDefault;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_FORMATO_GRUPPO_PROPRIETA", nullable = false)
    public DecFormatoGruppoProprieta getDecFormatoGruppoProprieta() {
        return decFormatoGruppoProprieta;
    }

    public void setDecFormatoGruppoProprieta(DecFormatoGruppoProprieta decFormatoGruppoProprieta) {
        this.decFormatoGruppoProprieta = decFormatoGruppoProprieta;
    }

}
