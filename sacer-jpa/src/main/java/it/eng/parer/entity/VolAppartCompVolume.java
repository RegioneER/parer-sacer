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
 * The persistent class for the VOL_APPART_COMP_VOLUME database table.
 */
@Entity
@Table(name = "VOL_APPART_COMP_VOLUME")
public class VolAppartCompVolume implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idAppartCompVolume;

    private String dsEsitoVerifFirmeChius;

    private BigDecimal idStrut;

    private String tiEsitoVerifFirmeChius;

    private AroCompDoc aroCompDoc;

    private VolAppartDocVolume volAppartDocVolume;

    private List<VolAppartFirmaVolume> volAppartFirmaVolumes = new ArrayList<>();

    public VolAppartCompVolume() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_APPART_COMP_VOLUME")
    @GenericGenerator(name = "SVOL_APPART_COMP_VOLUME_ID_APPART_COMP_VOLUME_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SVOL_APPART_COMP_VOLUME"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SVOL_APPART_COMP_VOLUME_ID_APPART_COMP_VOLUME_GENERATOR")
    public Long getIdAppartCompVolume() {
        return this.idAppartCompVolume;
    }

    public void setIdAppartCompVolume(Long idAppartCompVolume) {
        this.idAppartCompVolume = idAppartCompVolume;
    }

    @Column(name = "DS_ESITO_VERIF_FIRME_CHIUS")
    public String getDsEsitoVerifFirmeChius() {
        return this.dsEsitoVerifFirmeChius;
    }

    public void setDsEsitoVerifFirmeChius(String dsEsitoVerifFirmeChius) {
        this.dsEsitoVerifFirmeChius = dsEsitoVerifFirmeChius;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    @Column(name = "TI_ESITO_VERIF_FIRME_CHIUS")
    public String getTiEsitoVerifFirmeChius() {
        return this.tiEsitoVerifFirmeChius;
    }

    public void setTiEsitoVerifFirmeChius(String tiEsitoVerifFirmeChius) {
        this.tiEsitoVerifFirmeChius = tiEsitoVerifFirmeChius;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.DETACH })
    @JoinColumn(name = "ID_COMP_DOC")
    public AroCompDoc getAroCompDoc() {
        return this.aroCompDoc;
    }

    public void setAroCompDoc(AroCompDoc aroCompDoc) {
        this.aroCompDoc = aroCompDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.DETACH })
    @JoinColumn(name = "ID_APPART_DOC_VOLUME")
    public VolAppartDocVolume getVolAppartDocVolume() {
        return this.volAppartDocVolume;
    }

    public void setVolAppartDocVolume(VolAppartDocVolume volAppartDocVolume) {
        this.volAppartDocVolume = volAppartDocVolume;
    }

    @OneToMany(mappedBy = "volAppartCompVolume", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH,
            CascadeType.REFRESH })
    public List<VolAppartFirmaVolume> getVolAppartFirmaVolumes() {
        return this.volAppartFirmaVolumes;
    }

    public void setVolAppartFirmaVolumes(List<VolAppartFirmaVolume> volAppartFirmaVolumes) {
        this.volAppartFirmaVolumes = volAppartFirmaVolumes;
    }

}
