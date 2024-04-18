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
 * The persistent class for the APL_VAL_PARAM_APPLIC_MULTI database table.
 */
@Entity
@Table(name = "APL_VAL_PARAM_APPLIC_MULTI")
public class AplValParamApplicMulti implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idValParamApplicMulti;

    private String dsValoreParamApplic;

    private AplParamApplic aplParamApplic;

    private OrgAmbiente orgAmbiente;

    public AplValParamApplicMulti() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_VAL_PARAM_APPLIC_MULTI")
    @GenericGenerator(name = "SAPL_VAL_PARAM_APPLIC_MULTI_ID_VAL_PARAM_APPLIC_MULTI_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SAPL_VAL_PARAM_APPLIC_MULTI"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SAPL_VAL_PARAM_APPLIC_MULTI_ID_VAL_PARAM_APPLIC_MULTI_GENERATOR")
    public Long getIdValParamApplicMulti() {
        return this.idValParamApplicMulti;
    }

    public void setIdValParamApplicMulti(Long idValParamApplicMulti) {
        this.idValParamApplicMulti = idValParamApplicMulti;
    }

    @Column(name = "DS_VALORE_PARAM_APPLIC")
    public String getDsValoreParamApplic() {
        return this.dsValoreParamApplic;
    }

    public void setDsValoreParamApplic(String dsValoreParamApplic) {
        this.dsValoreParamApplic = dsValoreParamApplic;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PARAM_APPLIC")
    public AplParamApplic getAplParamApplic() {
        return this.aplParamApplic;
    }

    public void setAplParamApplic(AplParamApplic aplParamApplic) {
        this.aplParamApplic = aplParamApplic;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_AMBIENTE")
    public OrgAmbiente getOrgAmbiente() {
        return this.orgAmbiente;
    }

    public void setOrgAmbiente(OrgAmbiente orgAmbiente) {
        this.orgAmbiente = orgAmbiente;
    }

}
