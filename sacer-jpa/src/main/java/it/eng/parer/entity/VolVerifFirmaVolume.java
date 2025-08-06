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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the VOL_VERIF_FIRMA_VOLUME database table.
 */
@Entity
@Table(name = "VOL_VERIF_FIRMA_VOLUME")
public class VolVerifFirmaVolume implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idVerifFirmaVolume;

    private String dsMsgEsitoVerifFirma;

    private String tiEsitoVerifFirma;

    private String tiVerif;

    private List<VolContrVerifFirmaVolume> volContrVerifFirmaVolumes = new ArrayList<>();

    private VolAppartFirmaVolume volAppartFirmaVolume;

    public VolVerifFirmaVolume() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_VERIF_FIRMA_VOLUME")
    @GenericGenerator(name = "SVOL_VERIF_FIRMA_VOLUME_ID_VERIF_FIRMA_VOLUME_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SVOL_VERIF_FIRMA_VOLUME"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SVOL_VERIF_FIRMA_VOLUME_ID_VERIF_FIRMA_VOLUME_GENERATOR")
    public Long getIdVerifFirmaVolume() {
	return this.idVerifFirmaVolume;
    }

    public void setIdVerifFirmaVolume(Long idVerifFirmaVolume) {
	this.idVerifFirmaVolume = idVerifFirmaVolume;
    }

    @Column(name = "DS_MSG_ESITO_VERIF_FIRMA")
    public String getDsMsgEsitoVerifFirma() {
	return this.dsMsgEsitoVerifFirma;
    }

    public void setDsMsgEsitoVerifFirma(String dsMsgEsitoVerifFirma) {
	this.dsMsgEsitoVerifFirma = dsMsgEsitoVerifFirma;
    }

    @Column(name = "TI_ESITO_VERIF_FIRMA")
    public String getTiEsitoVerifFirma() {
	return this.tiEsitoVerifFirma;
    }

    public void setTiEsitoVerifFirma(String tiEsitoVerifFirma) {
	this.tiEsitoVerifFirma = tiEsitoVerifFirma;
    }

    @Column(name = "TI_VERIF")
    public String getTiVerif() {
	return this.tiVerif;
    }

    public void setTiVerif(String tiVerif) {
	this.tiVerif = tiVerif;
    }

    @OneToMany(mappedBy = "volVerifFirmaVolume", cascade = {
	    CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })
    public List<VolContrVerifFirmaVolume> getVolContrVerifFirmaVolumes() {
	return this.volContrVerifFirmaVolumes;
    }

    public void setVolContrVerifFirmaVolumes(
	    List<VolContrVerifFirmaVolume> volContrVerifFirmaVolumes) {
	this.volContrVerifFirmaVolumes = volContrVerifFirmaVolumes;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = {
	    CascadeType.DETACH })
    @JoinColumn(name = "ID_APPART_FIRMA_VOLUME")
    public VolAppartFirmaVolume getVolAppartFirmaVolume() {
	return this.volAppartFirmaVolume;
    }

    public void setVolAppartFirmaVolume(VolAppartFirmaVolume volAppartFirmaVolume) {
	this.volAppartFirmaVolume = volAppartFirmaVolume;
    }

}
