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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the SER_FILE_VER_SERIE database table.
 */
@Entity
@Table(name = "SER_FILE_VER_SERIE")

public class SerFileVerSerie implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idFileVerSerie;

    private byte[] blFile;

    private String cdEncodingHashFile;

    private String cdVerXsdFile;

    private String dsAlgoHashFile;

    private String dsHashFile;

    private Date dtCreazione;

    private BigDecimal idStrut;

    private String tiFileVerSerie;

    private String tiFirma;

    private SerVerSerie serVerSerie;

    private BigDecimal idEnteConserv;

    private List<SerUrnFileVerSerie> serUrnFileVerSeries = new ArrayList<>();

    public SerFileVerSerie() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_FILE_VER_SERIE")
    @GenericGenerator(name = "SSER_FILE_VER_SERIE_ID_FILE_VER_SERIE_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
	    @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SSER_FILE_VER_SERIE"),
	    @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SSER_FILE_VER_SERIE_ID_FILE_VER_SERIE_GENERATOR")
    public Long getIdFileVerSerie() {
	return this.idFileVerSerie;
    }

    public void setIdFileVerSerie(Long idFileVerSerie) {
	this.idFileVerSerie = idFileVerSerie;
    }

    @Lob
    @Column(name = "BL_FILE")
    public byte[] getBlFile() {
	return this.blFile;
    }

    public void setBlFile(byte[] blFile) {
	this.blFile = blFile;
    }

    @Column(name = "CD_ENCODING_HASH_FILE")
    public String getCdEncodingHashFile() {
	return this.cdEncodingHashFile;
    }

    public void setCdEncodingHashFile(String cdEncodingHashFile) {
	this.cdEncodingHashFile = cdEncodingHashFile;
    }

    @Column(name = "CD_VER_XSD_FILE")
    public String getCdVerXsdFile() {
	return this.cdVerXsdFile;
    }

    public void setCdVerXsdFile(String cdVerXsdFile) {
	this.cdVerXsdFile = cdVerXsdFile;
    }

    @Column(name = "DS_ALGO_HASH_FILE")
    public String getDsAlgoHashFile() {
	return this.dsAlgoHashFile;
    }

    public void setDsAlgoHashFile(String dsAlgoHashFile) {
	this.dsAlgoHashFile = dsAlgoHashFile;
    }

    @Column(name = "DS_HASH_FILE")
    public String getDsHashFile() {
	return this.dsHashFile;
    }

    public void setDsHashFile(String dsHashFile) {
	this.dsHashFile = dsHashFile;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE")
    public Date getDtCreazione() {
	return this.dtCreazione;
    }

    public void setDtCreazione(Date dtCreazione) {
	this.dtCreazione = dtCreazione;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
	return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
	this.idStrut = idStrut;
    }

    @Column(name = "TI_FILE_VER_SERIE")
    public String getTiFileVerSerie() {
	return this.tiFileVerSerie;
    }

    public void setTiFileVerSerie(String tiFileVerSerie) {
	this.tiFileVerSerie = tiFileVerSerie;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VER_SERIE")
    public SerVerSerie getSerVerSerie() {
	return this.serVerSerie;
    }

    public void setSerVerSerie(SerVerSerie serVerSerie) {
	this.serVerSerie = serVerSerie;
    }

    @Column(name = "ID_ENTE_CONSERV")
    public BigDecimal getIdEnteConserv() {
	return this.idEnteConserv;
    }

    public void setIdEnteConserv(BigDecimal idEnteConserv) {
	this.idEnteConserv = idEnteConserv;
    }

    @Column(name = "TI_FIRMA")
    public String getTiFirma() {
	return tiFirma;
    }

    public void setTiFirma(String tiFirma) {
	this.tiFirma = tiFirma;
    }

    @OneToMany(mappedBy = "serFileVerSerie", cascade = CascadeType.PERSIST)
    public List<SerUrnFileVerSerie> getSerUrnFileVerSeries() {
	return this.serUrnFileVerSeries;
    }

    public void setSerUrnFileVerSeries(List<SerUrnFileVerSerie> serUrnFileVerSeries) {
	this.serUrnFileVerSeries = serUrnFileVerSeries;
    }

    public SerUrnFileVerSerie addSerUrnFileVerSerie(SerUrnFileVerSerie serUrnFileVerSerie) {
	getSerUrnFileVerSeries().add(serUrnFileVerSerie);
	serUrnFileVerSerie.setSerFileVerSerie(this);
	return serUrnFileVerSerie;
    }

    public SerUrnFileVerSerie removeSerUrnFileVerSerie(SerUrnFileVerSerie serUrnFileVerSerie) {
	getSerUrnFileVerSeries().remove(serUrnFileVerSerie);
	serUrnFileVerSerie.setSerFileVerSerie(null);
	return serUrnFileVerSerie;
    }
}
