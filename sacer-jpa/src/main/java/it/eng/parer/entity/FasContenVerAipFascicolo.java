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

package it.eng.parer.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the FAS_CONTEN_VER_AIP_FASCICOLO database table.
 */
@Entity
@Table(name = "FAS_CONTEN_VER_AIP_FASCICOLO")
public class FasContenVerAipFascicolo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idContenVerAipFascicolo;

    private String nmConten;

    private String tiConten;

    private AroVerIndiceAipUd aroVerIndiceAipUd;

    private FasVerAipFascicolo fasVerAipFascicolo;

    private FasVerAipFascicolo fasVerAipFascicoloFiglio;

    public FasContenVerAipFascicolo() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_CONTEN_VER_AIP_FASCICOLO")
    @GenericGenerator(name = "SFAS_CONTEN_VER_AIP_FASCICOLO_ID_CONTEN_VER_AIP_FASCICOLO_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SFAS_CONTEN_VER_AIP_FASCICOLO"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SFAS_CONTEN_VER_AIP_FASCICOLO_ID_CONTEN_VER_AIP_FASCICOLO_GENERATOR")
    public Long getIdContenVerAipFascicolo() {
        return this.idContenVerAipFascicolo;
    }

    public void setIdContenVerAipFascicolo(Long idContenVerAipFascicolo) {
        this.idContenVerAipFascicolo = idContenVerAipFascicolo;
    }

    @Column(name = "NM_CONTEN")
    public String getNmConten() {
        return this.nmConten;
    }

    public void setNmConten(String nmConten) {
        this.nmConten = nmConten;
    }

    @Column(name = "TI_CONTEN")
    public String getTiConten() {
        return this.tiConten;
    }

    public void setTiConten(String tiConten) {
        this.tiConten = tiConten;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VER_INDICE_AIP_UD")
    public AroVerIndiceAipUd getAroVerIndiceAipUd() {
        return this.aroVerIndiceAipUd;
    }

    public void setAroVerIndiceAipUd(AroVerIndiceAipUd aroVerIndiceAipUd) {
        this.aroVerIndiceAipUd = aroVerIndiceAipUd;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VER_AIP_FASCICOLO")
    public FasVerAipFascicolo getFasVerAipFascicolo() {
        return this.fasVerAipFascicolo;
    }

    public void setFasVerAipFascicolo(FasVerAipFascicolo fasVerAipFascicolo) {
        this.fasVerAipFascicolo = fasVerAipFascicolo;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VER_AIP_FASCICOLO_FIGLIO")
    public FasVerAipFascicolo getFasVerAipFascicoloFiglio() {
        return this.fasVerAipFascicoloFiglio;
    }

    public void setFasVerAipFascicoloFiglio(FasVerAipFascicolo fasVerAipFascicoloFiglio) {
        this.fasVerAipFascicoloFiglio = fasVerAipFascicoloFiglio;
    }

}
