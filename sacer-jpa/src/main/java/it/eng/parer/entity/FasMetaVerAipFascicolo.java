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
 * The persistent class for the FAS_META_VER_AIP_FASCICOLO database table.
 */
@Entity
@Table(name = "FAS_META_VER_AIP_FASCICOLO")
public class FasMetaVerAipFascicolo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idMetaVerAipFascicolo;

    private String nmMeta;

    private String tiMeta;

    private String tiFormatoMeta;

    private String dsHashFile;

    private String dsAlgoHashFile;

    private String cdEncodingHashFile;

    private FasVerAipFascicolo fasVerAipFascicolo;

    private List<FasFileMetaVerAipFasc> fasFileMetaVerAipFascs = new ArrayList<>();

    private List<FasXsdMetaVerAipFasc> fasXsdMetaVerAipFascs = new ArrayList<>();

    private String dsUrnMetaFascicolo;

    private String dsUrnNormalizMetaFascicolo;

    public FasMetaVerAipFascicolo() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_META_VER_AIP_FASCICOLO")
    @GenericGenerator(name = "SFAS_META_VER_AIP_FASCICOLO_ID_META_VER_AIP_FASCICOLO_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SFAS_META_VER_AIP_FASCICOLO"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SFAS_META_VER_AIP_FASCICOLO_ID_META_VER_AIP_FASCICOLO_GENERATOR")
    public Long getIdMetaVerAipFascicolo() {
        return this.idMetaVerAipFascicolo;
    }

    public void setIdMetaVerAipFascicolo(Long idMetaVerAipFascicolo) {
        this.idMetaVerAipFascicolo = idMetaVerAipFascicolo;
    }

    @Column(name = "NM_META")
    public String getNmMeta() {
        return this.nmMeta;
    }

    public void setNmMeta(String nmMeta) {
        this.nmMeta = nmMeta;
    }

    @Column(name = "TI_META")
    public String getTiMeta() {
        return this.tiMeta;
    }

    public void setTiMeta(String tiMeta) {
        this.tiMeta = tiMeta;
    }

    @Column(name = "TI_FORMATO_META")
    public String getTiFormatoMeta() {
        return this.tiFormatoMeta;
    }

    public void setTiFormatoMeta(String tiFormatoMeta) {
        this.tiFormatoMeta = tiFormatoMeta;
    }

    @Column(name = "DS_HASH_FILE")
    public String getDsHashFile() {
        return this.dsHashFile;
    }

    public void setDsHashFile(String dsHashFile) {
        this.dsHashFile = dsHashFile;
    }

    @Column(name = "DS_ALGO_HASH_FILE")
    public String getDsAlgoHashFile() {
        return this.dsAlgoHashFile;
    }

    public void setDsAlgoHashFile(String dsAlgoHashFile) {
        this.dsAlgoHashFile = dsAlgoHashFile;
    }

    @Column(name = "CD_ENCODING_HASH_FILE")
    public String getCdEncodingHashFile() {
        return this.cdEncodingHashFile;
    }

    public void setCdEncodingHashFile(String cdEncodingHashFile) {
        this.cdEncodingHashFile = cdEncodingHashFile;
    }

    @Column(name = "DS_URN_META_FASCICOLO")
    public String getDsUrnMetaFascicolo() {
        return this.dsUrnMetaFascicolo;
    }

    public void setDsUrnMetaFascicolo(String dsUrnMetaFascicolo) {
        this.dsUrnMetaFascicolo = dsUrnMetaFascicolo;
    }

    @Column(name = "DS_URN_NORMALIZ_META_FASCICOLO")
    public String getDsUrnNormalizMetaFascicolo() {
        return this.dsUrnNormalizMetaFascicolo;
    }

    public void setDsUrnNormalizMetaFascicolo(String dsUrnNormalizMetaFascicolo) {
        this.dsUrnNormalizMetaFascicolo = dsUrnNormalizMetaFascicolo;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_VER_AIP_FASCICOLO")
    public FasVerAipFascicolo getFasVerAipFascicolo() {
        return this.fasVerAipFascicolo;
    }

    public void setFasVerAipFascicolo(FasVerAipFascicolo fasVerAipFascicolo) {
        this.fasVerAipFascicolo = fasVerAipFascicolo;
    }

    @OneToMany(mappedBy = "fasMetaVerAipFascicolo")
    public List<FasFileMetaVerAipFasc> getFasFileMetaVerAipFascs() {
        return this.fasFileMetaVerAipFascs;
    }

    public void setFasFileMetaVerAipFascs(List<FasFileMetaVerAipFasc> fasFileMetaVerAipFascs) {
        this.fasFileMetaVerAipFascs = fasFileMetaVerAipFascs;
    }

    @OneToMany(mappedBy = "fasMetaVerAipFascicolo")
    public List<FasXsdMetaVerAipFasc> getFasXsdMetaVerAipFascs() {
        return this.fasXsdMetaVerAipFascs;
    }

    public void setFasXsdMetaVerAipFascs(List<FasXsdMetaVerAipFasc> fasXsdMetaVerAipFascs) {
        this.fasXsdMetaVerAipFascs = fasXsdMetaVerAipFascs;
    }
}
