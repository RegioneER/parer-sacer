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
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlID;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import it.eng.parer.entity.constraint.ElvElencoVer.TiModValidElenco;
import it.eng.parer.entity.constraint.ElvElencoVer.TiValidElenco;

/**
 * The persistent class for the ELV_ELENCO_VERS database table.
 */
@Entity
@Table(name = "ELV_ELENCO_VERS")

public class ElvElencoVer implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idElencoVers;

    private BigDecimal aaKeyUnitaDoc;

    private String dlMotivoChius;

    private String dsElenco;

    private Date dtChius;

    private Date dtCreazioneElenco;

    private Date dtCreazioneElencoIxAip;

    private Date dtFirmaElencoIxAip;

    private Date dtFirmaIndice;

    private Date dtMarcaElencoIxAip;

    private Date dtScadChius;

    private String flElencoFisc;

    private String flElencoStandard;

    private BigDecimal idStatoElencoVersCor;

    private BigDecimal niCompAggElenco;

    private BigDecimal niCompVersElenco;

    private BigDecimal niDocAggElenco;

    private BigDecimal niDocVersElenco;

    private BigDecimal niIndiciAip;

    private BigDecimal niMaxComp;

    private BigDecimal niSizeAggElenco;

    private BigDecimal niSizeVersElenco;

    private BigDecimal niTempoScadChius;

    private BigDecimal niUnitaDocModElenco;

    private BigDecimal niUnitaDocVersElenco;

    private BigDecimal niUpdUnitaDoc;

    private String nmElenco;

    private String ntElencoChiuso;

    private String ntIndiceElenco;

    private String tiGestElenco;

    private String tiScadChius;

    private String tiStatoElenco;

    private String tiTempoScadChius;

    private TiValidElenco tiValidElenco;

    private TiModValidElenco tiModValidElenco;

    private List<AroDoc> aroDocs = new ArrayList<>();

    private List<AroIndiceAipUdDaElab> aroIndiceAipUdDaElabs = new ArrayList<>();

    private List<AroUnitaDoc> aroUnitaDocs = new ArrayList<>();

    private List<AroVerIndiceAipUd> aroVerIndiceAipUds = new ArrayList<>();

    private List<AroUpdUnitaDoc> aroUpdUnitaDocs = new ArrayList<>();

    private DecCriterioRaggr decCriterioRaggr;

    private IamUser iamUserFirmaIndice;

    private IamUser iamUserChiusoElenco;

    private OrgStrut orgStrut;

    private List<ElvElencoVersDaElab> elvElencoVersDaElabs = new ArrayList<>();

    private List<ElvElencoVersUdAnnul> elvElencoVersUdAnnuls = new ArrayList<>();

    private List<ElvFileElencoVer> elvFileElencoVers = new ArrayList<>();

    private List<HsmElencoSessioneFirma> hsmElencoSessioneFirmas = new ArrayList<>();

    private List<ElvUrnElencoVers> elvUrnElencoVerss = new ArrayList<>();

    public ElvElencoVer() {
        // hibernate
    }

    @Id

    @Column(name = "ID_ELENCO_VERS")
    @XmlID
    @GenericGenerator(name = "SELV_ELENCO_VERS_ID_ELENCO_VERS_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SELV_ELENCO_VERS"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SELV_ELENCO_VERS_ID_ELENCO_VERS_GENERATOR")
    public Long getIdElencoVers() {
        return this.idElencoVers;
    }

    public void setIdElencoVers(Long idElencoVers) {
        this.idElencoVers = idElencoVers;
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    public BigDecimal getAaKeyUnitaDoc() {
        return this.aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    @Column(name = "DL_MOTIVO_CHIUS")
    public String getDlMotivoChius() {
        return this.dlMotivoChius;
    }

    public void setDlMotivoChius(String dlMotivoChius) {
        this.dlMotivoChius = dlMotivoChius;
    }

    @Column(name = "DS_ELENCO")
    public String getDsElenco() {
        return this.dsElenco;
    }

    public void setDsElenco(String dsElenco) {
        this.dsElenco = dsElenco;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CHIUS")
    public Date getDtChius() {
        return this.dtChius;
    }

    public void setDtChius(Date dtChius) {
        this.dtChius = dtChius;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE_ELENCO")
    public Date getDtCreazioneElenco() {
        return this.dtCreazioneElenco;
    }

    public void setDtCreazioneElenco(Date dtCreazioneElenco) {
        this.dtCreazioneElenco = dtCreazioneElenco;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE_ELENCO_IX_AIP")
    public Date getDtCreazioneElencoIxAip() {
        return this.dtCreazioneElencoIxAip;
    }

    public void setDtCreazioneElencoIxAip(Date dtCreazioneElencoIxAip) {
        this.dtCreazioneElencoIxAip = dtCreazioneElencoIxAip;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIRMA_ELENCO_IX_AIP")
    public Date getDtFirmaElencoIxAip() {
        return this.dtFirmaElencoIxAip;
    }

    public void setDtFirmaElencoIxAip(Date dtFirmaElencoIxAip) {
        this.dtFirmaElencoIxAip = dtFirmaElencoIxAip;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIRMA_INDICE")
    public Date getDtFirmaIndice() {
        return this.dtFirmaIndice;
    }

    public void setDtFirmaIndice(Date dtFirmaIndice) {
        this.dtFirmaIndice = dtFirmaIndice;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_MARCA_ELENCO_IX_AIP")
    public Date getDtMarcaElencoIxAip() {
        return this.dtMarcaElencoIxAip;
    }

    public void setDtMarcaElencoIxAip(Date dtMarcaElencoIxAip) {
        this.dtMarcaElencoIxAip = dtMarcaElencoIxAip;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_SCAD_CHIUS")
    public Date getDtScadChius() {
        return this.dtScadChius;
    }

    public void setDtScadChius(Date dtScadChius) {
        this.dtScadChius = dtScadChius;
    }

    @Column(name = "FL_ELENCO_FISC", columnDefinition = "char(1)")
    public String getFlElencoFisc() {
        return this.flElencoFisc;
    }

    public void setFlElencoFisc(String flElencoFisc) {
        this.flElencoFisc = flElencoFisc;
    }

    @Column(name = "FL_ELENCO_STANDARD", columnDefinition = "char(1)")
    public String getFlElencoStandard() {
        return this.flElencoStandard;
    }

    public void setFlElencoStandard(String flElencoStandard) {
        this.flElencoStandard = flElencoStandard;
    }

    @Column(name = "ID_STATO_ELENCO_VERS_COR")
    public BigDecimal getIdStatoElencoVersCor() {
        return this.idStatoElencoVersCor;
    }

    public void setIdStatoElencoVersCor(BigDecimal idStatoElencoVersCor) {
        this.idStatoElencoVersCor = idStatoElencoVersCor;
    }

    @Column(name = "NI_COMP_AGG_ELENCO")
    public BigDecimal getNiCompAggElenco() {
        return this.niCompAggElenco;
    }

    public void setNiCompAggElenco(BigDecimal niCompAggElenco) {
        this.niCompAggElenco = niCompAggElenco;
    }

    @Column(name = "NI_COMP_VERS_ELENCO")
    public BigDecimal getNiCompVersElenco() {
        return this.niCompVersElenco;
    }

    public void setNiCompVersElenco(BigDecimal niCompVersElenco) {
        this.niCompVersElenco = niCompVersElenco;
    }

    @Column(name = "NI_DOC_AGG_ELENCO")
    public BigDecimal getNiDocAggElenco() {
        return this.niDocAggElenco;
    }

    public void setNiDocAggElenco(BigDecimal niDocAggElenco) {
        this.niDocAggElenco = niDocAggElenco;
    }

    @Column(name = "NI_DOC_VERS_ELENCO")
    public BigDecimal getNiDocVersElenco() {
        return this.niDocVersElenco;
    }

    public void setNiDocVersElenco(BigDecimal niDocVersElenco) {
        this.niDocVersElenco = niDocVersElenco;
    }

    @Column(name = "NI_INDICI_AIP")
    public BigDecimal getNiIndiciAip() {
        return this.niIndiciAip;
    }

    public void setNiIndiciAip(BigDecimal niIndiciAip) {
        this.niIndiciAip = niIndiciAip;
    }

    @Column(name = "NI_MAX_COMP")
    public BigDecimal getNiMaxComp() {
        return this.niMaxComp;
    }

    public void setNiMaxComp(BigDecimal niMaxComp) {
        this.niMaxComp = niMaxComp;
    }

    @Column(name = "NI_SIZE_AGG_ELENCO")
    public BigDecimal getNiSizeAggElenco() {
        return this.niSizeAggElenco;
    }

    public void setNiSizeAggElenco(BigDecimal niSizeAggElenco) {
        this.niSizeAggElenco = niSizeAggElenco;
    }

    @Column(name = "NI_SIZE_VERS_ELENCO")
    public BigDecimal getNiSizeVersElenco() {
        return this.niSizeVersElenco;
    }

    public void setNiSizeVersElenco(BigDecimal niSizeVersElenco) {
        this.niSizeVersElenco = niSizeVersElenco;
    }

    @Column(name = "NI_TEMPO_SCAD_CHIUS")
    public BigDecimal getNiTempoScadChius() {
        return this.niTempoScadChius;
    }

    public void setNiTempoScadChius(BigDecimal niTempoScadChius) {
        this.niTempoScadChius = niTempoScadChius;
    }

    @Column(name = "NI_UNITA_DOC_MOD_ELENCO")
    public BigDecimal getNiUnitaDocModElenco() {
        return this.niUnitaDocModElenco;
    }

    public void setNiUnitaDocModElenco(BigDecimal niUnitaDocModElenco) {
        this.niUnitaDocModElenco = niUnitaDocModElenco;
    }

    @Column(name = "NI_UNITA_DOC_VERS_ELENCO")
    public BigDecimal getNiUnitaDocVersElenco() {
        return this.niUnitaDocVersElenco;
    }

    public void setNiUnitaDocVersElenco(BigDecimal niUnitaDocVersElenco) {
        this.niUnitaDocVersElenco = niUnitaDocVersElenco;
    }

    @Column(name = "NI_UPD_UNITA_DOC")
    public BigDecimal getNiUpdUnitaDoc() {
        return this.niUpdUnitaDoc;
    }

    public void setNiUpdUnitaDoc(BigDecimal niUpdUnitaDoc) {
        this.niUpdUnitaDoc = niUpdUnitaDoc;
    }

    @Column(name = "NM_ELENCO")
    public String getNmElenco() {
        return this.nmElenco;
    }

    public void setNmElenco(String nmElenco) {
        this.nmElenco = nmElenco;
    }

    @Column(name = "NT_ELENCO_CHIUSO")
    public String getNtElencoChiuso() {
        return this.ntElencoChiuso;
    }

    public void setNtElencoChiuso(String ntElencoChiuso) {
        this.ntElencoChiuso = ntElencoChiuso;
    }

    @Column(name = "NT_INDICE_ELENCO")
    public String getNtIndiceElenco() {
        return this.ntIndiceElenco;
    }

    public void setNtIndiceElenco(String ntIndiceElenco) {
        this.ntIndiceElenco = ntIndiceElenco;
    }

    @Column(name = "TI_GEST_ELENCO")
    public String getTiGestElenco() {
        return this.tiGestElenco;
    }

    public void setTiGestElenco(String tiGestElenco) {
        this.tiGestElenco = tiGestElenco;
    }

    @Column(name = "TI_SCAD_CHIUS")
    public String getTiScadChius() {
        return this.tiScadChius;
    }

    public void setTiScadChius(String tiScadChius) {
        this.tiScadChius = tiScadChius;
    }

    @Column(name = "TI_STATO_ELENCO")
    public String getTiStatoElenco() {
        return this.tiStatoElenco;
    }

    public void setTiStatoElenco(String tiStatoElenco) {
        this.tiStatoElenco = tiStatoElenco;
    }

    @Column(name = "TI_TEMPO_SCAD_CHIUS")
    public String getTiTempoScadChius() {
        return this.tiTempoScadChius;
    }

    public void setTiTempoScadChius(String tiTempoScadChius) {
        this.tiTempoScadChius = tiTempoScadChius;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "TI_VALID_ELENCO")
    public TiValidElenco getTiValidElenco() {
        return this.tiValidElenco;
    }

    public void setTiValidElenco(TiValidElenco tiValidElenco) {
        this.tiValidElenco = tiValidElenco;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "TI_MOD_VALID_ELENCO")
    public TiModValidElenco getTiModValidElenco() {
        return this.tiModValidElenco;
    }

    public void setTiModValidElenco(TiModValidElenco tiModValidElenco) {
        this.tiModValidElenco = tiModValidElenco;
    }

    @OneToMany(mappedBy = "elvElencoVer")
    public List<AroDoc> getAroDocs() {
        return this.aroDocs;
    }

    public void setAroDocs(List<AroDoc> aroDocs) {
        this.aroDocs = aroDocs;
    }

    public AroDoc addAroDoc(AroDoc aroDoc) {
        getAroDocs().add(aroDoc);
        aroDoc.setElvElencoVer(this);
        return aroDoc;
    }

    public AroDoc removeAroDoc(AroDoc aroDoc) {
        getAroDocs().remove(aroDoc);
        aroDoc.setElvElencoVer(null);
        return aroDoc;
    }

    @OneToMany(mappedBy = "elvElencoVer")
    public List<AroIndiceAipUdDaElab> getAroIndiceAipUdDaElabs() {
        return this.aroIndiceAipUdDaElabs;
    }

    public void setAroIndiceAipUdDaElabs(List<AroIndiceAipUdDaElab> aroIndiceAipUdDaElabs) {
        this.aroIndiceAipUdDaElabs = aroIndiceAipUdDaElabs;
    }

    public AroIndiceAipUdDaElab addAroIndiceAipUdDaElab(AroIndiceAipUdDaElab aroIndiceAipUdDaElab) {
        getAroIndiceAipUdDaElabs().add(aroIndiceAipUdDaElab);
        aroIndiceAipUdDaElab.setElvElencoVer(this);
        return aroIndiceAipUdDaElab;
    }

    public AroIndiceAipUdDaElab removeAroIndiceAipUdDaElab(
            AroIndiceAipUdDaElab aroIndiceAipUdDaElab) {
        getAroIndiceAipUdDaElabs().remove(aroIndiceAipUdDaElab);
        aroIndiceAipUdDaElab.setElvElencoVer(null);
        return aroIndiceAipUdDaElab;
    }

    @OneToMany(mappedBy = "elvElencoVer")
    public List<AroUnitaDoc> getAroUnitaDocs() {
        return this.aroUnitaDocs;
    }

    public void setAroUnitaDocs(List<AroUnitaDoc> aroUnitaDocs) {
        this.aroUnitaDocs = aroUnitaDocs;
    }

    public AroUnitaDoc addAroUnitaDoc(AroUnitaDoc aroUnitaDoc) {
        getAroUnitaDocs().add(aroUnitaDoc);
        aroUnitaDoc.setElvElencoVer(this);
        return aroUnitaDoc;
    }

    public AroUnitaDoc removeAroUnitaDoc(AroUnitaDoc aroUnitaDoc) {
        getAroUnitaDocs().remove(aroUnitaDoc);
        aroUnitaDoc.setElvElencoVer(null);
        return aroUnitaDoc;
    }

    @OneToMany(mappedBy = "elvElencoVer")
    public List<AroVerIndiceAipUd> getAroVerIndiceAipUds() {
        return this.aroVerIndiceAipUds;
    }

    public void setAroVerIndiceAipUds(List<AroVerIndiceAipUd> aroVerIndiceAipUds) {
        this.aroVerIndiceAipUds = aroVerIndiceAipUds;
    }

    public AroVerIndiceAipUd addAroVerIndiceAipUd(AroVerIndiceAipUd aroVerIndiceAipUd) {
        getAroVerIndiceAipUds().add(aroVerIndiceAipUd);
        aroVerIndiceAipUd.setElvElencoVer(this);
        return aroVerIndiceAipUd;
    }

    public AroVerIndiceAipUd removeAroVerIndiceAipUd(AroVerIndiceAipUd aroVerIndiceAipUd) {
        getAroVerIndiceAipUds().remove(aroVerIndiceAipUd);
        aroVerIndiceAipUd.setElvElencoVer(null);
        return aroVerIndiceAipUd;
    }

    @OneToMany(mappedBy = "elvElencoVer")
    public List<AroUpdUnitaDoc> getAroUpdUnitaDocs() {
        return this.aroUpdUnitaDocs;
    }

    public void setAroUpdUnitaDocs(List<AroUpdUnitaDoc> aroUpdUnitaDocs) {
        this.aroUpdUnitaDocs = aroUpdUnitaDocs;
    }

    public AroUpdUnitaDoc addAroUpdUnitaDoc(AroUpdUnitaDoc aroUpdUnitaDoc) {
        getAroUpdUnitaDocs().add(aroUpdUnitaDoc);
        aroUpdUnitaDoc.setElvElencoVer(this);
        return aroUpdUnitaDoc;
    }

    public AroUpdUnitaDoc removeAroUpdUnitaDoc(AroUpdUnitaDoc aroUpdUnitaDoc) {
        getAroUpdUnitaDocs().remove(aroUpdUnitaDoc);
        aroUpdUnitaDoc.setElvElencoVer(null);
        return aroUpdUnitaDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CRITERIO_RAGGR")
    public DecCriterioRaggr getDecCriterioRaggr() {
        return this.decCriterioRaggr;
    }

    public void setDecCriterioRaggr(DecCriterioRaggr decCriterioRaggr) {
        this.decCriterioRaggr = decCriterioRaggr;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER_FIRMA_INDICE")
    public IamUser getIamUserFirmaIndice() {
        return this.iamUserFirmaIndice;
    }

    public void setIamUserFirmaIndice(IamUser iamUserFirmaIndice) {
        this.iamUserFirmaIndice = iamUserFirmaIndice;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER_CHIUSO_ELENCO")
    public IamUser getIamUserChiusoElenco() {
        return this.iamUserChiusoElenco;
    }

    public void setIamUserChiusoElenco(IamUser iamUserChiusoElenco) {
        this.iamUserChiusoElenco = iamUserChiusoElenco;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STRUT")
    public OrgStrut getOrgStrut() {
        return this.orgStrut;
    }

    public void setOrgStrut(OrgStrut orgStrut) {
        this.orgStrut = orgStrut;
    }

    @OneToMany(mappedBy = "elvElencoVer", cascade = {
            CascadeType.PERSIST, CascadeType.REMOVE })
    public List<ElvElencoVersDaElab> getElvElencoVersDaElabs() {
        return this.elvElencoVersDaElabs;
    }

    public void setElvElencoVersDaElabs(List<ElvElencoVersDaElab> elvElencoVersDaElabs) {
        this.elvElencoVersDaElabs = elvElencoVersDaElabs;
    }

    public ElvElencoVersDaElab addElvElencoVersDaElab(ElvElencoVersDaElab elvElencoVersDaElab) {
        getElvElencoVersDaElabs().add(elvElencoVersDaElab);
        elvElencoVersDaElab.setElvElencoVer(this);
        return elvElencoVersDaElab;
    }

    public ElvElencoVersDaElab removeElvElencoVersDaElab(ElvElencoVersDaElab elvElencoVersDaElab) {
        getElvElencoVersDaElabs().remove(elvElencoVersDaElab);
        elvElencoVersDaElab.setElvElencoVer(null);
        return elvElencoVersDaElab;
    }

    @OneToMany(mappedBy = "elvElencoVer")
    public List<ElvElencoVersUdAnnul> getElvElencoVersUdAnnuls() {
        return this.elvElencoVersUdAnnuls;
    }

    public void setElvElencoVersUdAnnuls(List<ElvElencoVersUdAnnul> elvElencoVersUdAnnuls) {
        this.elvElencoVersUdAnnuls = elvElencoVersUdAnnuls;
    }

    public ElvElencoVersUdAnnul addElvElencoVersUdAnnul(ElvElencoVersUdAnnul elvElencoVersUdAnnul) {
        getElvElencoVersUdAnnuls().add(elvElencoVersUdAnnul);
        elvElencoVersUdAnnul.setElvElencoVer(this);
        return elvElencoVersUdAnnul;
    }

    public ElvElencoVersUdAnnul removeElvElencoVersUdAnnul(
            ElvElencoVersUdAnnul elvElencoVersUdAnnul) {
        getElvElencoVersUdAnnuls().remove(elvElencoVersUdAnnul);
        elvElencoVersUdAnnul.setElvElencoVer(null);
        return elvElencoVersUdAnnul;
    }

    @OneToMany(mappedBy = "elvElencoVer", cascade = {
            CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH })
    public List<ElvFileElencoVer> getElvFileElencoVers() {
        return this.elvFileElencoVers;
    }

    public void setElvFileElencoVers(List<ElvFileElencoVer> elvFileElencoVers) {
        this.elvFileElencoVers = elvFileElencoVers;
    }

    public ElvFileElencoVer addElvFileElencoVer(ElvFileElencoVer elvFileElencoVer) {
        getElvFileElencoVers().add(elvFileElencoVer);
        elvFileElencoVer.setElvElencoVer(this);
        return elvFileElencoVer;
    }

    public ElvFileElencoVer removeElvFileElencoVer(ElvFileElencoVer elvFileElencoVer) {
        getElvFileElencoVers().remove(elvFileElencoVer);
        elvFileElencoVer.setElvElencoVer(null);
        return elvFileElencoVer;
    }

    @OneToMany(mappedBy = "elvElencoVer")
    public List<HsmElencoSessioneFirma> getHsmElencoSessioneFirmas() {
        return this.hsmElencoSessioneFirmas;
    }

    public void setHsmElencoSessioneFirmas(List<HsmElencoSessioneFirma> hsmElencoSessioneFirmas) {
        this.hsmElencoSessioneFirmas = hsmElencoSessioneFirmas;
    }

    @OneToMany(mappedBy = "elvElencoVers", cascade = CascadeType.PERSIST)
    public List<ElvUrnElencoVers> getElvUrnElencoVerss() {
        return this.elvUrnElencoVerss;
    }

    public void setElvUrnElencoVerss(List<ElvUrnElencoVers> elvUrnElencoVerss) {
        this.elvUrnElencoVerss = elvUrnElencoVerss;
    }

    public ElvUrnElencoVers addElvUrnElencoVers(ElvUrnElencoVers elvUrnElencoVers) {
        getElvUrnElencoVerss().add(elvUrnElencoVers);
        elvUrnElencoVers.setElvElencoVers(this);
        return elvUrnElencoVers;
    }

    public ElvUrnElencoVers removeElvUrnElencoVers(ElvUrnElencoVers elvUrnElencoVers) {
        getElvUrnElencoVerss().remove(elvUrnElencoVers);
        elvUrnElencoVers.setElvElencoVers(null);
        return elvUrnElencoVers;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.nmElenco);
        hash = 29 * hash + Objects.hashCode(this.orgStrut);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ElvElencoVer other = (ElvElencoVer) obj;
        if (!Objects.equals(this.nmElenco, other.nmElenco)) {
            return false;
        }
        return Objects.equals(this.orgStrut, other.orgStrut);
    }
}
