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
 * The persistent class for the OST_NO_MIGRAZ_FILE database table.
 */
@Entity
@Table(name = "OST_NO_MIGRAZ_FILE")

public class OstNoMigrazFile implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idNoMigrazFile;

    private OstMigrazSubPart ostMigrazSubPart;

    private String nmTabellaIdOggetto;

    private BigDecimal idOggetto;

    private BigDecimal idStrut;

    private BigDecimal mmFile;

    private String tiSupportoComp;

    private String tiSaveFile;

    private String tiCausaleNoMigraz;

    public OstNoMigrazFile() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_NO_MIGRAZ_FILE")
    @GenericGenerator(name = "SOST_NO_MIGRAZ_FILE_ID_NO_MIGRAZ_FILE_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SOST_NO_MIGRAZ_FILE"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SOST_NO_MIGRAZ_FILE_ID_NO_MIGRAZ_FILE_GENERATOR")
    public Long getIdNoMigrazFile() {
        return this.idNoMigrazFile;
    }

    public void setIdNoMigrazFile(Long idNoMigrazFile) {
        this.idNoMigrazFile = idNoMigrazFile;
    }

    @Column(name = "ID_OGGETTO")
    public BigDecimal getIdOggetto() {
        return this.idOggetto;
    }

    public void setIdOggetto(BigDecimal idOggetto) {
        this.idOggetto = idOggetto;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    @Column(name = "MM_FILE")
    public BigDecimal getMmFile() {
        return this.mmFile;
    }

    public void setMmFile(BigDecimal mmFile) {
        this.mmFile = mmFile;
    }

    @Column(name = "NM_TABELLA_ID_OGGETTO")
    public String getNmTabellaIdOggetto() {
        return this.nmTabellaIdOggetto;
    }

    public void setNmTabellaIdOggetto(String nmTabellaIdOggetto) {
        this.nmTabellaIdOggetto = nmTabellaIdOggetto;
    }

    @Column(name = "TI_SUPPORTO_COMP")
    public String getTiSupportoComp() {
        return this.tiSupportoComp;
    }

    public void setTiSupportoComp(String tiSupportoComp) {
        this.tiSupportoComp = tiSupportoComp;
    }

    @Column(name = "TI_SAVE_FILE")
    public String getTiSaveFile() {
        return this.tiSaveFile;
    }

    public void setTiSaveFile(String tiSaveFile) {
        this.tiSaveFile = tiSaveFile;
    }

    @Column(name = "TI_CAUSALE_NO_MIGRAZ")
    public String getTiCausaleNoMigraz() {
        return this.tiCausaleNoMigraz;
    }

    public void setTiCausaleNoMigraz(String tiCausaleNoMigraz) {
        this.tiCausaleNoMigraz = tiCausaleNoMigraz;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_MIGRAZ_SUB_PART")
    public OstMigrazSubPart getOstMigrazSubPart() {
        return this.ostMigrazSubPart;
    }

    public void setOstMigrazSubPart(OstMigrazSubPart ostMigrazSubPart) {
        this.ostMigrazSubPart = ostMigrazSubPart;
    }
}
