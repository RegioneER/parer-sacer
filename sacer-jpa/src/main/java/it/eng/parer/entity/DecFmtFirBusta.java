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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import javax.persistence.Table;

/**
 * The persistent class for the DEC_FMT_FIR_BUSTA database table.
 *
 */
@Entity
@Table(name = "DEC_FMT_FIR_BUSTA")

public class DecFmtFirBusta implements Serializable {
    private static final long serialVersionUID = 1L;
    private long idFmtFirBusta;
    private String dsFormato;
    private String tiFormato;

    public DecFmtFirBusta() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_FMT_FIR_BUSTA")
    public long getIdFmtFirBusta() {
	return this.idFmtFirBusta;
    }

    public void setIdFmtFirBusta(long idFmtFirBusta) {
	this.idFmtFirBusta = idFmtFirBusta;
    }

    @Column(name = "DS_FORMATO")
    public String getDsFormato() {
	return this.dsFormato;
    }

    public void setDsFormato(String dsFormato) {
	this.dsFormato = dsFormato;
    }

    @Column(name = "TI_FORMATO")
    public String getTiFormato() {
	return this.tiFormato;
    }

    public void setTiFormato(String tiFormato) {
	this.tiFormato = tiFormato;
    }

}
