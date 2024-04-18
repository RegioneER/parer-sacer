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
 * The persistent class for the DEC_FORMATO_VALUTAZIONE database table.
 *
 */
@Entity
@Cacheable(true)
@Table(name = "DEC_FORMATO_VALUTAZIONE")
public class DecFormatoValutazione implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idFormatoValutazione;
    private DecFormatoProprieta decFormatoProprieta;
    private DecFormatoFileStandard decFormatoFileStandard;
    private BigDecimal niPunteggio;
    private Integer flgInteroperabilita;
    private String ntPunteggioValutazione;

    public DecFormatoValutazione() {/* Hibernate */
    }

    @Id
    @SequenceGenerator(name = "DEC_FORMATO_VALUTAZIONE_GENERATOR", sequenceName = "SDEC_FORMATO_VALUTAZIONE", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DEC_FORMATO_VALUTAZIONE_GENERATOR")
    @Column(name = "ID_FORMATO_VALUTAZIONE")
    public Long getIdFormatoValutazione() {
        return idFormatoValutazione;
    }

    public void setIdFormatoValutazione(Long idFormatoValutazione) {
        this.idFormatoValutazione = idFormatoValutazione;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_FORMATO_PROPRIETA")
    public DecFormatoProprieta getDecFormatoProprieta() {
        return decFormatoProprieta;
    }

    public void setDecFormatoProprieta(DecFormatoProprieta decFormatoProprieta) {
        this.decFormatoProprieta = decFormatoProprieta;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_FORMATO_FILE_STANDARD")
    public DecFormatoFileStandard getDecFormatoFileStandard() {
        return decFormatoFileStandard;
    }

    public void setDecFormatoFileStandard(DecFormatoFileStandard decFormatoFileStandard) {
        this.decFormatoFileStandard = decFormatoFileStandard;
    }

    @Column(name = "NI_PUNTEGGIO")
    public BigDecimal getNiPunteggio() {
        return niPunteggio;
    }

    public void setNiPunteggio(BigDecimal niPunteggio) {
        this.niPunteggio = niPunteggio;
    }

    @Column(name = "FLG_INTEROPERABILITA")
    public Integer getFlgInteroperabilita() {
        return flgInteroperabilita;
    }

    public void setFlgInteroperabilita(Integer flgInteroperabilita) {
        this.flgInteroperabilita = flgInteroperabilita;
    }

    @Column(name = "NT_PUNTEGGIO_VALUTAZIONE")
    public String getNtPunteggioValutazione() {
        return ntPunteggioValutazione;
    }

    public void setNtPunteggioValutazione(String ntPunteggioValutazione) {
        this.ntPunteggioValutazione = ntPunteggioValutazione;
    }

}
