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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlID;

/**
 * The persistent class for the FAS_VALORE_ATTRIB_FASCICOLO database table.
 *
 */
@Entity
@Table(name = "FAS_VALORE_ATTRIB_FASCICOLO")
public class FasValoreAttribFascicolo implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long idValoreAttribFascicolo;
    private DecUsoModelloXsdFasc decUsoModelloXsdFasc;
    private FasFascicolo fasFascicolo;
    private DecAttribFascicolo decAttribFascicolo;
    private String dlValore;
    private DecAaTipoFascicolo decAaTipoFascicolo;

    public FasValoreAttribFascicolo() {
	/* hibernate */
    }

    @Id
    @Column(name = "ID_VALORE_ATTRIB_FASCICOLO")
    @XmlID
    public Long getIdValoreAttribFascicolo() {
	return this.idValoreAttribFascicolo;
    }

    public void setIdValoreAttribFascicolo(Long idValoreAttribFascicolo) {
	this.idValoreAttribFascicolo = idValoreAttribFascicolo;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USO_MODELLO_XSD_FASC")
    public DecUsoModelloXsdFasc getDecUsoModelloXsdFasc() {
	return this.decUsoModelloXsdFasc;
    }

    public void setDecUsoModelloXsdFasc(DecUsoModelloXsdFasc decUsoModelloXsdFasc) {
	this.decUsoModelloXsdFasc = decUsoModelloXsdFasc;
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
    @JoinColumn(name = "ID_ATTRIB_FASCICOLO")
    public DecAttribFascicolo getDecAttribFascicolo() {
	return this.decAttribFascicolo;
    }

    public void setDecAttribFascicolo(DecAttribFascicolo decAttribFascicolo) {
	this.decAttribFascicolo = decAttribFascicolo;
    }

    @Column(name = "DL_VALORE")
    public String getDlValore() {
	return this.dlValore;
    }

    public void setDlValore(String dlValore) {
	this.dlValore = dlValore;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_AA_TIPO_FASCICOLO")
    public DecAaTipoFascicolo getDecAaTipoFascicolo() {
	return this.decAaTipoFascicolo;
    }

    public void setDecAaTipoFascicolo(DecAaTipoFascicolo decAaTipoFascicolo) {
	this.decAaTipoFascicolo = decAaTipoFascicolo;
    }

}
