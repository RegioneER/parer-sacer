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
import java.util.List;

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
 * The persistent class for the OST_MIGRAZ_SUB_PART database table.
 */
@Entity
@Table(name = "OST_MIGRAZ_SUB_PART")

public class OstMigrazSubPart implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idMigrazSubPart;

    private BigDecimal idStatoMigrazSubPartCor;

    private BigDecimal mmMax;

    private BigDecimal niByteSize;

    private BigDecimal niFileDaMigrare;

    private BigDecimal niFileErroreNormaliz;

    private BigDecimal niFileMigrati;

    private BigDecimal niFileMigrazInCorso;

    private BigDecimal niFileMigrazInErrore;

    private String nmColonnaBlobFile;

    private String nmColonnaIdFile;

    private String nmTabellaFile;

    private String nmTablespace;

    private BigDecimal niIstanzaJobPrepara;

    private BigDecimal niIstanzaJobProducer;

    private BigDecimal niFileSubPart;

    private List<OstMigrazFile> ostMigrazFiles = new ArrayList<>();

    private List<OstMigrazStrutMese> ostMigrazStrutMeses = new ArrayList<>();

    private OrgSubPartition orgSubPartition;

    private List<OstStatoMigrazSubPart> ostStatoMigrazSubParts = new ArrayList<>();

    public OstMigrazSubPart() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_MIGRAZ_SUB_PART")
    @GenericGenerator(name = "SOST_MIGRAZ_SUB_PART_ID_MIGRAZ_SUB_PART_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SOST_MIGRAZ_SUB_PART"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SOST_MIGRAZ_SUB_PART_ID_MIGRAZ_SUB_PART_GENERATOR")
    public Long getIdMigrazSubPart() {
        return this.idMigrazSubPart;
    }

    public void setIdMigrazSubPart(Long idMigrazSubPart) {
        this.idMigrazSubPart = idMigrazSubPart;
    }

    @Column(name = "ID_STATO_MIGRAZ_SUB_PART_COR")
    public BigDecimal getIdStatoMigrazSubPartCor() {
        return this.idStatoMigrazSubPartCor;
    }

    public void setIdStatoMigrazSubPartCor(BigDecimal idStatoMigrazSubPartCor) {
        this.idStatoMigrazSubPartCor = idStatoMigrazSubPartCor;
    }

    @Column(name = "MM_MAX")
    public BigDecimal getMmMax() {
        return this.mmMax;
    }

    public void setMmMax(BigDecimal mmMax) {
        this.mmMax = mmMax;
    }

    @Column(name = "NI_BYTE_SIZE")
    public BigDecimal getNiByteSize() {
        return this.niByteSize;
    }

    public void setNiByteSize(BigDecimal niByteSize) {
        this.niByteSize = niByteSize;
    }

    @Column(name = "NI_FILE_DA_MIGRARE")
    public BigDecimal getNiFileDaMigrare() {
        return this.niFileDaMigrare;
    }

    public void setNiFileDaMigrare(BigDecimal niFileDaMigrare) {
        this.niFileDaMigrare = niFileDaMigrare;
    }

    @Column(name = "NI_FILE_ERRORE_NORMALIZ")
    public BigDecimal getNiFileErroreNormaliz() {
        return this.niFileErroreNormaliz;
    }

    public void setNiFileErroreNormaliz(BigDecimal niFileErroreNormaliz) {
        this.niFileErroreNormaliz = niFileErroreNormaliz;
    }

    @Column(name = "NI_FILE_MIGRATI")
    public BigDecimal getNiFileMigrati() {
        return this.niFileMigrati;
    }

    public void setNiFileMigrati(BigDecimal niFileMigrati) {
        this.niFileMigrati = niFileMigrati;
    }

    @Column(name = "NI_FILE_MIGRAZ_IN_CORSO")
    public BigDecimal getNiFileMigrazInCorso() {
        return this.niFileMigrazInCorso;
    }

    public void setNiFileMigrazInCorso(BigDecimal niFileMigrazInCorso) {
        this.niFileMigrazInCorso = niFileMigrazInCorso;
    }

    @Column(name = "NI_FILE_MIGRAZ_IN_ERRORE")
    public BigDecimal getNiFileMigrazInErrore() {
        return this.niFileMigrazInErrore;
    }

    public void setNiFileMigrazInErrore(BigDecimal niFileMigrazInErrore) {
        this.niFileMigrazInErrore = niFileMigrazInErrore;
    }

    @Column(name = "NM_COLONNA_BLOB_FILE")
    public String getNmColonnaBlobFile() {
        return this.nmColonnaBlobFile;
    }

    public void setNmColonnaBlobFile(String nmColonnaBlobFile) {
        this.nmColonnaBlobFile = nmColonnaBlobFile;
    }

    @Column(name = "NM_COLONNA_ID_FILE")
    public String getNmColonnaIdFile() {
        return this.nmColonnaIdFile;
    }

    public void setNmColonnaIdFile(String nmColonnaIdFile) {
        this.nmColonnaIdFile = nmColonnaIdFile;
    }

    @Column(name = "NM_TABELLA_FILE")
    public String getNmTabellaFile() {
        return this.nmTabellaFile;
    }

    public void setNmTabellaFile(String nmTabellaFile) {
        this.nmTabellaFile = nmTabellaFile;
    }

    @Column(name = "NM_TABLESPACE")
    public String getNmTablespace() {
        return this.nmTablespace;
    }

    public void setNmTablespace(String nmTablespace) {
        this.nmTablespace = nmTablespace;
    }

    @Column(name = "NI_ISTANZA_JOB_PREPARA")
    public BigDecimal getNiIstanzaJobPrepara() {
        return this.niIstanzaJobPrepara;
    }

    public void setNiIstanzaJobPrepara(BigDecimal niIstanzaJobPrepara) {
        this.niIstanzaJobPrepara = niIstanzaJobPrepara;
    }

    @Column(name = "NI_ISTANZA_JOB_PRODUCER")
    public BigDecimal getNiIstanzaJobProducer() {
        return this.niIstanzaJobProducer;
    }

    public void setNiIstanzaJobProducer(BigDecimal niIstanzaJobProducer) {
        this.niIstanzaJobProducer = niIstanzaJobProducer;
    }

    @Column(name = "NI_FILE_SUB_PART")
    public BigDecimal getNiFileSubPart() {
        return this.niFileSubPart;
    }

    public void setNiFileSubPart(BigDecimal niFileSubPart) {
        this.niFileSubPart = niFileSubPart;
    }

    @OneToMany(mappedBy = "ostMigrazSubPart")
    public List<OstMigrazFile> getOstMigrazFiles() {
        return this.ostMigrazFiles;
    }

    public void setOstMigrazFiles(List<OstMigrazFile> ostMigrazFiles) {
        this.ostMigrazFiles = ostMigrazFiles;
    }

    public OstMigrazFile addOstMigrazFile(OstMigrazFile ostMigrazFile) {
        getOstMigrazFiles().add(ostMigrazFile);
        ostMigrazFile.setOstMigrazSubPart(this);
        return ostMigrazFile;
    }

    public OstMigrazFile removeOstMigrazFile(OstMigrazFile ostMigrazFile) {
        getOstMigrazFiles().remove(ostMigrazFile);
        ostMigrazFile.setOstMigrazSubPart(null);
        return ostMigrazFile;
    }

    @OneToMany(mappedBy = "ostMigrazSubPart")
    public List<OstMigrazStrutMese> getOstMigrazStrutMeses() {
        return this.ostMigrazStrutMeses;
    }

    public void setOstMigrazStrutMeses(List<OstMigrazStrutMese> ostMigrazStrutMeses) {
        this.ostMigrazStrutMeses = ostMigrazStrutMeses;
    }

    public OstMigrazStrutMese addOstMigrazStrutMes(OstMigrazStrutMese ostMigrazStrutMes) {
        getOstMigrazStrutMeses().add(ostMigrazStrutMes);
        ostMigrazStrutMes.setOstMigrazSubPart(this);
        return ostMigrazStrutMes;
    }

    public OstMigrazStrutMese removeOstMigrazStrutMes(OstMigrazStrutMese ostMigrazStrutMes) {
        getOstMigrazStrutMeses().remove(ostMigrazStrutMes);
        ostMigrazStrutMes.setOstMigrazSubPart(null);
        return ostMigrazStrutMes;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SUB_PARTITION")
    public OrgSubPartition getOrgSubPartition() {
        return this.orgSubPartition;
    }

    public void setOrgSubPartition(OrgSubPartition orgSubPartition) {
        this.orgSubPartition = orgSubPartition;
    }

    @OneToMany(mappedBy = "ostMigrazSubPart")
    public List<OstStatoMigrazSubPart> getOstStatoMigrazSubParts() {
        return this.ostStatoMigrazSubParts;
    }

    public void setOstStatoMigrazSubParts(List<OstStatoMigrazSubPart> ostStatoMigrazSubParts) {
        this.ostStatoMigrazSubParts = ostStatoMigrazSubParts;
    }

    public OstStatoMigrazSubPart addOstStatoMigrazSubPart(
            OstStatoMigrazSubPart ostStatoMigrazSubPart) {
        getOstStatoMigrazSubParts().add(ostStatoMigrazSubPart);
        ostStatoMigrazSubPart.setOstMigrazSubPart(this);
        return ostStatoMigrazSubPart;
    }

    public OstStatoMigrazSubPart removeOstStatoMigrazSubPart(
            OstStatoMigrazSubPart ostStatoMigrazSubPart) {
        getOstStatoMigrazSubParts().remove(ostStatoMigrazSubPart);
        ostStatoMigrazSubPart.setOstMigrazSubPart(null);
        return ostStatoMigrazSubPart;
    }
}
