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
 * The persistent class for the ELV_FILE_ELENCO_VERS database table.
 */
@Entity
@Table(name = "ELV_FILE_ELENCO_VERS")

public class ElvFileElencoVer implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idFileElencoVers;

    private byte[] blFileElencoVers;

    private String cdEncodingHashFile;

    private String cdVerXsdFile;

    private String dsAlgoHashFile;

    private String dsHashFile;

    private Date dtCreazioneFile;

    private BigDecimal idStrut;

    private String tiFileElencoVers;

    private String tiFirma;

    private ElvElencoVer elvElencoVer;

    private List<ElvUrnFileElencoVers> elvUrnFileElencoVerss = new ArrayList<>();

    public ElvFileElencoVer() {/* Hibernate */
    }

    public ElvFileElencoVer(byte[] blFileElencoVers, String cdVerXsdFile, String tiFileElencoVers) {
        this.blFileElencoVers = blFileElencoVers;
        this.cdVerXsdFile = cdVerXsdFile;
        this.tiFileElencoVers = tiFileElencoVers;
    }

    public ElvFileElencoVer(byte[] blFileElencoVers, String cdVerXsdFile, String tiFileElencoVers,
            String tiFirma) {
        this.blFileElencoVers = blFileElencoVers;
        this.cdVerXsdFile = cdVerXsdFile;
        this.tiFileElencoVers = tiFileElencoVers;
        this.tiFirma = tiFirma;
    }

    public ElvFileElencoVer(long idFileElencoVers, byte[] blFileElencoVers, String cdVerXsdFile,
            String tiFileElencoVers) {
        this.idFileElencoVers = idFileElencoVers;
        this.blFileElencoVers = blFileElencoVers;
        this.cdVerXsdFile = cdVerXsdFile;
        this.tiFileElencoVers = tiFileElencoVers;
    }

    public ElvFileElencoVer(long idFileElencoVers, byte[] blFileElencoVers, String cdVerXsdFile,
            String tiFileElencoVers, String tiFirma) {
        this.idFileElencoVers = idFileElencoVers;
        this.blFileElencoVers = blFileElencoVers;
        this.cdVerXsdFile = cdVerXsdFile;
        this.tiFileElencoVers = tiFileElencoVers;
        this.tiFirma = tiFirma;
    }

    @Id

    @Column(name = "ID_FILE_ELENCO_VERS")
    @GenericGenerator(name = "SELV_FILE_ELENCO_VERS_ID_FILE_ELENCO_VERS_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SELV_FILE_ELENCO_VERS"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SELV_FILE_ELENCO_VERS_ID_FILE_ELENCO_VERS_GENERATOR")
    public Long getIdFileElencoVers() {
        return this.idFileElencoVers;
    }

    public void setIdFileElencoVers(Long idFileElencoVers) {
        this.idFileElencoVers = idFileElencoVers;
    }

    @Lob
    @Column(name = "BL_FILE_ELENCO_VERS")
    public byte[] getBlFileElencoVers() {
        return this.blFileElencoVers;
    }

    public void setBlFileElencoVers(byte[] blFileElencoVers) {
        this.blFileElencoVers = blFileElencoVers;
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
    @Column(name = "DT_CREAZIONE_FILE")
    public Date getDtCreazioneFile() {
        return this.dtCreazioneFile;
    }

    public void setDtCreazioneFile(Date dtCreazioneFile) {
        this.dtCreazioneFile = dtCreazioneFile;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    @Column(name = "TI_FILE_ELENCO_VERS")
    public String getTiFileElencoVers() {
        return this.tiFileElencoVers;
    }

    public void setTiFileElencoVers(String tiFileElencoVers) {
        this.tiFileElencoVers = tiFileElencoVers;
    }

    @Column(name = "TI_FIRMA")
    public String getTiFirma() {
        return tiFirma;
    }

    public void setTiFirma(String tiFirma) {
        this.tiFirma = tiFirma;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ELENCO_VERS")
    public ElvElencoVer getElvElencoVer() {
        return this.elvElencoVer;
    }

    public void setElvElencoVer(ElvElencoVer elvElencoVer) {
        this.elvElencoVer = elvElencoVer;
    }

    @OneToMany(mappedBy = "elvFileElencoVers", cascade = CascadeType.PERSIST)
    public List<ElvUrnFileElencoVers> getElvUrnFileElencoVerss() {
        return this.elvUrnFileElencoVerss;
    }

    public void setElvUrnFileElencoVerss(List<ElvUrnFileElencoVers> elvUrnFileElencoVerss) {
        this.elvUrnFileElencoVerss = elvUrnFileElencoVerss;
    }

    public ElvUrnFileElencoVers addElvUrnFileElencoVers(ElvUrnFileElencoVers elvUrnFileElencoVers) {
        getElvUrnFileElencoVerss().add(elvUrnFileElencoVers);
        elvUrnFileElencoVers.setElvFileElencoVers(this);
        return elvUrnFileElencoVers;
    }

    public ElvUrnFileElencoVers removeElvUrnFileElencoVers(
            ElvUrnFileElencoVers elvUrnFileElencoVers) {
        getElvUrnFileElencoVerss().remove(elvUrnFileElencoVers);
        elvUrnFileElencoVers.setElvFileElencoVers(null);
        return elvUrnFileElencoVers;
    }
}
