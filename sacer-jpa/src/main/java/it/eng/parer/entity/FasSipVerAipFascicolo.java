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
 * The persistent class for the FAS_SIP_VER_AIP_FASCICOLO database table.
 */
@Entity
@Table(name = "FAS_SIP_VER_AIP_FASCICOLO")
public class FasSipVerAipFascicolo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idSipVerAipFascicolo;

    private FasVerAipFascicolo fasVerAipFascicolo;

    private String nmSip;

    private String tiSip;

    private FasXmlVersFascicolo fasXmlVersFascicoloRich;

    private FasXmlVersFascicolo fasXmlVersFascicoloRisp;

    public FasSipVerAipFascicolo() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_SIP_VER_AIP_FASCICOLO")
    @GenericGenerator(name = "SFAS_SIP_VER_AIP_FASCICOLO_ID_SIP_VER_AIP_FASCICOLO_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SFAS_SIP_VER_AIP_FASCICOLO"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SFAS_SIP_VER_AIP_FASCICOLO_ID_SIP_VER_AIP_FASCICOLO_GENERATOR")
    public Long getIdSipVerAipFascicolo() {
        return this.idSipVerAipFascicolo;
    }

    public void setIdSipVerAipFascicolo(Long idSipVerAipFascicolo) {
        this.idSipVerAipFascicolo = idSipVerAipFascicolo;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VER_AIP_FASCICOLO")
    public FasVerAipFascicolo getFasVerAipFascicolo() {
        return this.fasVerAipFascicolo;
    }

    public void setFasVerAipFascicolo(FasVerAipFascicolo fasVerAipFascicolo) {
        this.fasVerAipFascicolo = fasVerAipFascicolo;
    }

    @Column(name = "NM_SIP")
    public String getNmSip() {
        return this.nmSip;
    }

    public void setNmSip(String nmSip) {
        this.nmSip = nmSip;
    }

    @Column(name = "TI_SIP")
    public String getTiSip() {
        return this.tiSip;
    }

    public void setTiSip(String tiSip) {
        this.tiSip = tiSip;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_XML_VERS_FASCICOLO_RICH")
    public FasXmlVersFascicolo getFasXmlVersFascicoloRich() {
        return this.fasXmlVersFascicoloRich;
    }

    public void setFasXmlVersFascicoloRich(FasXmlVersFascicolo fasXmlVersFascicoloRich) {
        this.fasXmlVersFascicoloRich = fasXmlVersFascicoloRich;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_XML_VERS_FASCICOLO_RISP")
    public FasXmlVersFascicolo getFasXmlVersFascicoloRisp() {
        return this.fasXmlVersFascicoloRisp;
    }

    public void setFasXmlVersFascicoloRisp(FasXmlVersFascicolo fasXmlVersFascicoloRisp) {
        this.fasXmlVersFascicoloRisp = fasXmlVersFascicoloRisp;
    }
}
