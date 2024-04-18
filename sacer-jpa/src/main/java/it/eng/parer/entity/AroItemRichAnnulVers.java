/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.entity;

import java.io.Serializable;
import java.math.BigDecimal;
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
 * The persistent class for the ARO_ITEM_RICH_ANNUL_VERS database table.
 */
@Entity
@Table(name = "ARO_ITEM_RICH_ANNUL_VERS")

public class AroItemRichAnnulVers implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idItemRichAnnulVers;

    private BigDecimal aaFascicolo;

    private BigDecimal aaKeyUnitaDoc;

    private String cdKeyFascicolo;

    private String cdKeyUnitaDoc;

    private String cdRegistroKeyUnitaDoc;

    private BigDecimal idStrut;

    private BigDecimal pgItemRichAnnulVers;

    private String tiItemRichAnnulVers;

    private String tiStatoItem;

    private List<AroErrRichAnnulVers> aroErrRichAnnulVers = new ArrayList<>();

    private AroRichAnnulVers aroRichAnnulVers;

    private AroUnitaDoc aroUnitaDoc;

    private FasFascicolo fasFascicolo;

    public AroItemRichAnnulVers() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_ITEM_RICH_ANNUL_VERS")
    @GenericGenerator(name = "SARO_ITEM_RICH_ANNUL_VERS_ID_ITEM_RICH_ANNUL_VERS_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SARO_ITEM_RICH_ANNUL_VERS"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SARO_ITEM_RICH_ANNUL_VERS_ID_ITEM_RICH_ANNUL_VERS_GENERATOR")
    public Long getIdItemRichAnnulVers() {
        return this.idItemRichAnnulVers;
    }

    public void setIdItemRichAnnulVers(Long idItemRichAnnulVers) {
        this.idItemRichAnnulVers = idItemRichAnnulVers;
    }

    @Column(name = "AA_FASCICOLO")
    public BigDecimal getAaFascicolo() {
        return this.aaFascicolo;
    }

    public void setAaFascicolo(BigDecimal aaFascicolo) {
        this.aaFascicolo = aaFascicolo;
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    public BigDecimal getAaKeyUnitaDoc() {
        return this.aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    @Column(name = "CD_KEY_FASCICOLO")
    public String getCdKeyFascicolo() {
        return this.cdKeyFascicolo;
    }

    public void setCdKeyFascicolo(String cdKeyFascicolo) {
        this.cdKeyFascicolo = cdKeyFascicolo;
    }

    @Column(name = "CD_KEY_UNITA_DOC")
    public String getCdKeyUnitaDoc() {
        return this.cdKeyUnitaDoc;
    }

    public void setCdKeyUnitaDoc(String cdKeyUnitaDoc) {
        this.cdKeyUnitaDoc = cdKeyUnitaDoc;
    }

    @Column(name = "CD_REGISTRO_KEY_UNITA_DOC")
    public String getCdRegistroKeyUnitaDoc() {
        return this.cdRegistroKeyUnitaDoc;
    }

    public void setCdRegistroKeyUnitaDoc(String cdRegistroKeyUnitaDoc) {
        this.cdRegistroKeyUnitaDoc = cdRegistroKeyUnitaDoc;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    @Column(name = "PG_ITEM_RICH_ANNUL_VERS")
    public BigDecimal getPgItemRichAnnulVers() {
        return this.pgItemRichAnnulVers;
    }

    public void setPgItemRichAnnulVers(BigDecimal pgItemRichAnnulVers) {
        this.pgItemRichAnnulVers = pgItemRichAnnulVers;
    }

    @Column(name = "TI_ITEM_RICH_ANNUL_VERS")
    public String getTiItemRichAnnulVers() {
        return this.tiItemRichAnnulVers;
    }

    public void setTiItemRichAnnulVers(String tiItemRichAnnulVers) {
        this.tiItemRichAnnulVers = tiItemRichAnnulVers;
    }

    @Column(name = "TI_STATO_ITEM")
    public String getTiStatoItem() {
        return this.tiStatoItem;
    }

    public void setTiStatoItem(String tiStatoItem) {
        this.tiStatoItem = tiStatoItem;
    }

    @OneToMany(mappedBy = "aroItemRichAnnulVers", cascade = CascadeType.PERSIST)
    public List<AroErrRichAnnulVers> getAroErrRichAnnulVers() {
        return this.aroErrRichAnnulVers;
    }

    public void setAroErrRichAnnulVers(List<AroErrRichAnnulVers> aroErrRichAnnulVers) {
        this.aroErrRichAnnulVers = aroErrRichAnnulVers;
    }

    public AroErrRichAnnulVers addAroErrRichAnnulVers(AroErrRichAnnulVers aroErrRichAnnulVers) {
        getAroErrRichAnnulVers().add(aroErrRichAnnulVers);
        aroErrRichAnnulVers.setAroItemRichAnnulVers(this);
        return aroErrRichAnnulVers;
    }

    public AroErrRichAnnulVers removeAroErrRichAnnulVers(AroErrRichAnnulVers aroErrRichAnnulVers) {
        getAroErrRichAnnulVers().remove(aroErrRichAnnulVers);
        aroErrRichAnnulVers.setAroItemRichAnnulVers(null);
        return aroErrRichAnnulVers;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_RICH_ANNUL_VERS")
    public AroRichAnnulVers getAroRichAnnulVers() {
        return this.aroRichAnnulVers;
    }

    public void setAroRichAnnulVers(AroRichAnnulVers aroRichAnnulVers) {
        this.aroRichAnnulVers = aroRichAnnulVers;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UNITA_DOC")
    public AroUnitaDoc getAroUnitaDoc() {
        return this.aroUnitaDoc;
    }

    public void setAroUnitaDoc(AroUnitaDoc aroUnitaDoc) {
        this.aroUnitaDoc = aroUnitaDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_FASCICOLO")
    public FasFascicolo getFasFascicolo() {
        return this.fasFascicolo;
    }

    public void setFasFascicolo(FasFascicolo fasFascicolo) {
        this.fasFascicolo = fasFascicolo;
    }

}
