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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the SER_ERR_FILE_INPUT database table.
 */
@Entity
@Table(name = "SER_ERR_FILE_INPUT")

public class SerErrFileInput implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idErrFileInput;

    private String dsRecErr;

    private BigDecimal niRecErr;

    private String tiErrRec;

    private SerFileInputVerSerie serFileInputVerSerie;

    private List<SerUdErrFileInput> serUdErrFileInputs = new ArrayList<>();

    public SerErrFileInput() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_ERR_FILE_INPUT")
    @GenericGenerator(name = "SSER_ERR_FILE_INPUT_ID_ERR_FILE_INPUT_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SSER_ERR_FILE_INPUT"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SSER_ERR_FILE_INPUT_ID_ERR_FILE_INPUT_GENERATOR")
    public Long getIdErrFileInput() {
        return this.idErrFileInput;
    }

    public void setIdErrFileInput(Long idErrFileInput) {
        this.idErrFileInput = idErrFileInput;
    }

    @Column(name = "DS_REC_ERR")
    public String getDsRecErr() {
        return this.dsRecErr;
    }

    public void setDsRecErr(String dsRecErr) {
        this.dsRecErr = dsRecErr;
    }

    @Column(name = "NI_REC_ERR")
    public BigDecimal getNiRecErr() {
        return this.niRecErr;
    }

    public void setNiRecErr(BigDecimal niRecErr) {
        this.niRecErr = niRecErr;
    }

    @Column(name = "TI_ERR_REC")
    public String getTiErrRec() {
        return this.tiErrRec;
    }

    public void setTiErrRec(String tiErrRec) {
        this.tiErrRec = tiErrRec;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_FILE_INPUT_VER_SERIE")
    public SerFileInputVerSerie getSerFileInputVerSerie() {
        return this.serFileInputVerSerie;
    }

    public void setSerFileInputVerSerie(SerFileInputVerSerie serFileInputVerSerie) {
        this.serFileInputVerSerie = serFileInputVerSerie;
    }

    @OneToMany(mappedBy = "serErrFileInput", cascade = CascadeType.PERSIST)
    public List<SerUdErrFileInput> getSerUdErrFileInputs() {
        return this.serUdErrFileInputs;
    }

    public void setSerUdErrFileInputs(List<SerUdErrFileInput> serUdErrFileInputs) {
        this.serUdErrFileInputs = serUdErrFileInputs;
    }

    public SerUdErrFileInput addSerUdErrFileInput(SerUdErrFileInput serUdErrFileInput) {
        getSerUdErrFileInputs().add(serUdErrFileInput);
        serUdErrFileInput.setSerErrFileInput(this);
        return serUdErrFileInput;
    }

    public SerUdErrFileInput removeSerUdErrFileInput(SerUdErrFileInput serUdErrFileInput) {
        getSerUdErrFileInputs().remove(serUdErrFileInput);
        serUdErrFileInput.setSerErrFileInput(null);
        return serUdErrFileInput;
    }

}
