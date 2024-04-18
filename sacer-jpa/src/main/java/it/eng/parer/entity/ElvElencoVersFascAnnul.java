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
 * The persistent class for the ELV_ELENCO_VERS_FASC_ANNUL database table.
 */
@Entity
@Table(name = "ELV_ELENCO_VERS_FASC_ANNUL")

public class ElvElencoVersFascAnnul implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idElencoVersFascAnnul;

    private String dsUrnFascicoloAnnul;

    private FasFascicolo fasFascicolo;

    private ElvElencoVersFasc elvElencoVersFasc;

    public ElvElencoVersFascAnnul() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_ELENCO_VERS_FASC_ANNUL")
    @GenericGenerator(name = "SELV_ELENCO_VERS_FASC_ANNUL_ID_ELENCO_VERS_FASC_ANNUL_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SELV_ELENCO_VERS_FASC_ANNUL"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SELV_ELENCO_VERS_FASC_ANNUL_ID_ELENCO_VERS_FASC_ANNUL_GENERATOR")
    public Long getIdElencoVersFascAnnul() {
        return this.idElencoVersFascAnnul;
    }

    public void setIdElencoVersFascAnnul(Long idElencoVersFascAnnul) {
        this.idElencoVersFascAnnul = idElencoVersFascAnnul;
    }

    @Column(name = "DS_URN_FASCICOLO_ANNUL")
    public String getDsUrnFascicoloAnnul() {
        return this.dsUrnFascicoloAnnul;
    }

    public void setDsUrnFascicoloAnnul(String dsUrnFascicoloAnnul) {
        this.dsUrnFascicoloAnnul = dsUrnFascicoloAnnul;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_FASCICOLO")
    public FasFascicolo getFasFascicolo() {
        return this.fasFascicolo;
    }

    public void setFasFascicolo(FasFascicolo fasFascicolo) {
        this.fasFascicolo = fasFascicolo;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ELENCO_VERS_FASC")
    public ElvElencoVersFasc getElvElencoVersFasc() {
        return this.elvElencoVersFasc;
    }

    public void setElvElencoVersFasc(ElvElencoVersFasc elvElencoVerFasc) {
        this.elvElencoVersFasc = elvElencoVerFasc;
    }
}
