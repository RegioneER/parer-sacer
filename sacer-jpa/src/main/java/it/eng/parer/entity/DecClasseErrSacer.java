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

/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.*;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * @author fioravanti_f
 */
@Entity
@Table(name = "DEC_CLASSE_ERR_SACER")

@NamedQuery(name = "DecClasseErrSacer.findByTipiUsoErr", query = "SELECT d FROM DecClasseErrSacer d WHERE d.tiUsoErr IN :tipiUsoErr ORDER BY d.cdClasseErrSacer")
@NamedQuery(name = "DecClasseErrSacer.findByCodice", query = "SELECT d FROM DecClasseErrSacer d WHERE d.cdClasseErrSacer = :cdClasseErrSacer")
public class DecClasseErrSacer implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idClasseErrSacer;

    private String tiUsoErr;

    private String cdClasseErrSacer;

    private String dsClasseErrSacer;

    private List<DecErrSacer> decErrSacers = new ArrayList<>();

    public DecClasseErrSacer() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_CLASSE_ERR_SACER")
    @GenericGenerator(name = "SDEC_CLASSE_ERR_SACER_ID_CLASSE_ERR_SACER_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SDEC_CLASSE_ERR_SACER"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SDEC_CLASSE_ERR_SACER_ID_CLASSE_ERR_SACER_GENERATOR")
    public Long getIdClasseErrSacer() {
	return idClasseErrSacer;
    }

    public void setIdClasseErrSacer(Long idClasseErrSacer) {
	this.idClasseErrSacer = idClasseErrSacer;
    }

    @Column(name = "TI_USO_ERR")
    public String getTiUsoErr() {
	return tiUsoErr;
    }

    public void setTiUsoErr(String tiUsoErr) {
	this.tiUsoErr = tiUsoErr;
    }

    @Column(name = "CD_CLASSE_ERR")
    public String getCdClasseErrSacer() {
	return cdClasseErrSacer;
    }

    public void setCdClasseErrSacer(String cdClasseErrSacer) {
	this.cdClasseErrSacer = cdClasseErrSacer;
    }

    @Column(name = "DS_CLASSE_ERR")
    public String getDsClasseErrSacer() {
	return dsClasseErrSacer;
    }

    public void setDsClasseErrSacer(String dsClasseErrSacer) {
	this.dsClasseErrSacer = dsClasseErrSacer;
    }

    @OneToMany(mappedBy = "decClasseErrSacer")
    public List<DecErrSacer> getDecErrSacers() {
	return decErrSacers;
    }

    public void setDecErrSacers(List<DecErrSacer> decErrSacers) {
	this.decErrSacers = decErrSacers;
    }
}
