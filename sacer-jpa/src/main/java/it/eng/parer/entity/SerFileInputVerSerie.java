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
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the SER_FILE_INPUT_VER_SERIE database table.
 */
@Entity
@Table(name = "SER_FILE_INPUT_VER_SERIE")

public class SerFileInputVerSerie implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idFileInputVerSerie;

    private String blFileInputVerSerie;

    private String cdDocFileInputVerSerie;

    private String dsDocFileInputVerSerie;

    private String flFornitoEnte;

    private String tiScopoFileInputVerSerie;

    private List<SerErrFileInput> serErrFileInputs = new ArrayList<>();

    private IamUser iamUser;

    private SerVerSerie serVerSerie;

    public SerFileInputVerSerie() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_FILE_INPUT_VER_SERIE")
    @GenericGenerator(name = "SSER_FILE_INPUT_VER_SERIE_ID_FILE_INPUT_VER_SERIE_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SSER_FILE_INPUT_VER_SERIE"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SSER_FILE_INPUT_VER_SERIE_ID_FILE_INPUT_VER_SERIE_GENERATOR")
    public Long getIdFileInputVerSerie() {
        return this.idFileInputVerSerie;
    }

    public void setIdFileInputVerSerie(Long idFileInputVerSerie) {
        this.idFileInputVerSerie = idFileInputVerSerie;
    }

    @Lob
    @Column(name = "BL_FILE_INPUT_VER_SERIE")
    public String getBlFileInputVerSerie() {
        return this.blFileInputVerSerie;
    }

    public void setBlFileInputVerSerie(String blFileInputVerSerie) {
        this.blFileInputVerSerie = blFileInputVerSerie;
    }

    @Column(name = "CD_DOC_FILE_INPUT_VER_SERIE")
    public String getCdDocFileInputVerSerie() {
        return this.cdDocFileInputVerSerie;
    }

    public void setCdDocFileInputVerSerie(String cdDocFileInputVerSerie) {
        this.cdDocFileInputVerSerie = cdDocFileInputVerSerie;
    }

    @Column(name = "DS_DOC_FILE_INPUT_VER_SERIE")
    public String getDsDocFileInputVerSerie() {
        return this.dsDocFileInputVerSerie;
    }

    public void setDsDocFileInputVerSerie(String dsDocFileInputVerSerie) {
        this.dsDocFileInputVerSerie = dsDocFileInputVerSerie;
    }

    @Column(name = "FL_FORNITO_ENTE", columnDefinition = "char(1)")
    public String getFlFornitoEnte() {
        return this.flFornitoEnte;
    }

    public void setFlFornitoEnte(String flFornitoEnte) {
        this.flFornitoEnte = flFornitoEnte;
    }

    @Column(name = "TI_SCOPO_FILE_INPUT_VER_SERIE")
    public String getTiScopoFileInputVerSerie() {
        return this.tiScopoFileInputVerSerie;
    }

    public void setTiScopoFileInputVerSerie(String tiScopoFileInputVerSerie) {
        this.tiScopoFileInputVerSerie = tiScopoFileInputVerSerie;
    }

    @OneToMany(mappedBy = "serFileInputVerSerie", cascade = CascadeType.PERSIST)
    public List<SerErrFileInput> getSerErrFileInputs() {
        return this.serErrFileInputs;
    }

    public void setSerErrFileInputs(List<SerErrFileInput> serErrFileInputs) {
        this.serErrFileInputs = serErrFileInputs;
    }

    public SerErrFileInput addSerErrFileInput(SerErrFileInput serErrFileInput) {
        getSerErrFileInputs().add(serErrFileInput);
        serErrFileInput.setSerFileInputVerSerie(this);
        return serErrFileInput;
    }

    public SerErrFileInput removeSerErrFileInput(SerErrFileInput serErrFileInput) {
        getSerErrFileInputs().remove(serErrFileInput);
        serErrFileInput.setSerFileInputVerSerie(null);
        return serErrFileInput;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER_IAM")
    public IamUser getIamUser() {
        return this.iamUser;
    }

    public void setIamUser(IamUser iamUser) {
        this.iamUser = iamUser;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VER_SERIE")
    public SerVerSerie getSerVerSerie() {
        return this.serVerSerie;
    }

    public void setSerVerSerie(SerVerSerie serVerSerie) {
        this.serVerSerie = serVerSerie;
    }

}
