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
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlID;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the VOL_VOLUME_CONSERV database table.
 */
@Entity
@Table(name = "VOL_VOLUME_CONSERV")
public class VolVolumeConserv implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idVolumeConserv;

    private String cdEncodingHash;

    private String cdVersioneIndice;

    private String dlMotivoChius;

    private String dsAlgoHash;

    private String dsFirmaMarcaBase64;

    private String dsHashFileFirma;

    private String dsHashFileIndice;

    private String dsHashFileMarca;

    private String dsVolumeConserv;

    private Date dtChius;

    private Date dtCreazione;

    private Date dtFirmaMarca;

    private Date dtScadChius;

    private String flElencoVers;

    private BigDecimal niCompVolume;

    private BigDecimal niKbSize;

    private BigDecimal niMaxComp;

    private BigDecimal niMaxUnitaDoc;

    private BigDecimal niTempoScadChius;

    private BigDecimal niTempoScadChiusFirme;

    private BigDecimal niUnitaDocVolume;

    private String nmVolumeConserv;

    private String ntIndiceVolume;

    private String ntVolumeChiuso;

    private String tiPresenzaFirme;

    private String tiScadChiusVolume;

    private String tiStatoVolumeConserv;

    private String tiTempoScadChius;

    private String tiTempoScadChiusFirme;

    private String tiValFirme;

    private Date tmMarcaFirma;

    private Date tmMarcaIndice;

    private List<VolAppartUnitaDocVolume> volAppartUnitaDocVolumes = new ArrayList<>();

    private List<VolFileVolumeConserv> volFileVolumeConservs = new ArrayList<>();

    private DecCriterioRaggr decCriterioRaggr;

    private OrgStrut orgStrut;

    private IamUser iamUserCreazione;

    private IamUser iamUserFirmaMarca;

    public VolVolumeConserv() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_VOLUME_CONSERV")
    @XmlID
    @GenericGenerator(name = "SVOL_VOLUME_CONSERV_ID_VOLUME_CONSERV_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SVOL_VOLUME_CONSERV"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SVOL_VOLUME_CONSERV_ID_VOLUME_CONSERV_GENERATOR")
    public Long getIdVolumeConserv() {
        return this.idVolumeConserv;
    }

    public void setIdVolumeConserv(Long idVolumeConserv) {
        this.idVolumeConserv = idVolumeConserv;
    }

    @Column(name = "CD_ENCODING_HASH")
    public String getCdEncodingHash() {
        return this.cdEncodingHash;
    }

    public void setCdEncodingHash(String cdEncodingHash) {
        this.cdEncodingHash = cdEncodingHash;
    }

    @Column(name = "CD_VERSIONE_INDICE")
    public String getCdVersioneIndice() {
        return this.cdVersioneIndice;
    }

    public void setCdVersioneIndice(String cdVersioneIndice) {
        this.cdVersioneIndice = cdVersioneIndice;
    }

    @Column(name = "DL_MOTIVO_CHIUS")
    public String getDlMotivoChius() {
        return this.dlMotivoChius;
    }

    public void setDlMotivoChius(String dlMotivoChius) {
        this.dlMotivoChius = dlMotivoChius;
    }

    @Column(name = "DS_ALGO_HASH")
    public String getDsAlgoHash() {
        return this.dsAlgoHash;
    }

    public void setDsAlgoHash(String dsAlgoHash) {
        this.dsAlgoHash = dsAlgoHash;
    }

    @Column(name = "DS_FIRMA_MARCA_BASE64")
    public String getDsFirmaMarcaBase64() {
        return this.dsFirmaMarcaBase64;
    }

    public void setDsFirmaMarcaBase64(String dsFirmaMarcaBase64) {
        this.dsFirmaMarcaBase64 = dsFirmaMarcaBase64;
    }

    @Column(name = "DS_HASH_FILE_FIRMA")
    public String getDsHashFileFirma() {
        return this.dsHashFileFirma;
    }

    public void setDsHashFileFirma(String dsHashFileFirma) {
        this.dsHashFileFirma = dsHashFileFirma;
    }

    @Column(name = "DS_HASH_FILE_INDICE")
    public String getDsHashFileIndice() {
        return this.dsHashFileIndice;
    }

    public void setDsHashFileIndice(String dsHashFileIndice) {
        this.dsHashFileIndice = dsHashFileIndice;
    }

    @Column(name = "DS_HASH_FILE_MARCA")
    public String getDsHashFileMarca() {
        return this.dsHashFileMarca;
    }

    public void setDsHashFileMarca(String dsHashFileMarca) {
        this.dsHashFileMarca = dsHashFileMarca;
    }

    @Column(name = "DS_VOLUME_CONSERV")
    public String getDsVolumeConserv() {
        return this.dsVolumeConserv;
    }

    public void setDsVolumeConserv(String dsVolumeConserv) {
        this.dsVolumeConserv = dsVolumeConserv;
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
    @Column(name = "DT_CREAZIONE")
    public Date getDtCreazione() {
        return this.dtCreazione;
    }

    public void setDtCreazione(Date dtCreazione) {
        this.dtCreazione = dtCreazione;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIRMA_MARCA")
    public Date getDtFirmaMarca() {
        return this.dtFirmaMarca;
    }

    public void setDtFirmaMarca(Date dtFirmaMarca) {
        this.dtFirmaMarca = dtFirmaMarca;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_SCAD_CHIUS")
    public Date getDtScadChius() {
        return this.dtScadChius;
    }

    public void setDtScadChius(Date dtScadChius) {
        this.dtScadChius = dtScadChius;
    }

    @Column(name = "FL_ELENCO_VERS", columnDefinition = "char(1)")
    public String getFlElencoVers() {
        return this.flElencoVers;
    }

    public void setFlElencoVers(String flElencoVers) {
        this.flElencoVers = flElencoVers;
    }

    @Column(name = "NI_COMP_VOLUME")
    public BigDecimal getNiCompVolume() {
        return this.niCompVolume;
    }

    public void setNiCompVolume(BigDecimal niCompVolume) {
        this.niCompVolume = niCompVolume;
    }

    @Column(name = "NI_KB_SIZE")
    public BigDecimal getNiKbSize() {
        return this.niKbSize;
    }

    public void setNiKbSize(BigDecimal niKbSize) {
        this.niKbSize = niKbSize;
    }

    @Column(name = "NI_MAX_COMP")
    public BigDecimal getNiMaxComp() {
        return this.niMaxComp;
    }

    public void setNiMaxComp(BigDecimal niMaxComp) {
        this.niMaxComp = niMaxComp;
    }

    @Column(name = "NI_MAX_UNITA_DOC")
    public BigDecimal getNiMaxUnitaDoc() {
        return this.niMaxUnitaDoc;
    }

    public void setNiMaxUnitaDoc(BigDecimal niMaxUnitaDoc) {
        this.niMaxUnitaDoc = niMaxUnitaDoc;
    }

    @Column(name = "NI_TEMPO_SCAD_CHIUS")
    public BigDecimal getNiTempoScadChius() {
        return this.niTempoScadChius;
    }

    public void setNiTempoScadChius(BigDecimal niTempoScadChius) {
        this.niTempoScadChius = niTempoScadChius;
    }

    @Column(name = "NI_TEMPO_SCAD_CHIUS_FIRME")
    public BigDecimal getNiTempoScadChiusFirme() {
        return this.niTempoScadChiusFirme;
    }

    public void setNiTempoScadChiusFirme(BigDecimal niTempoScadChiusFirme) {
        this.niTempoScadChiusFirme = niTempoScadChiusFirme;
    }

    @Column(name = "NI_UNITA_DOC_VOLUME")
    public BigDecimal getNiUnitaDocVolume() {
        return this.niUnitaDocVolume;
    }

    public void setNiUnitaDocVolume(BigDecimal niUnitaDocVolume) {
        this.niUnitaDocVolume = niUnitaDocVolume;
    }

    @Column(name = "NM_VOLUME_CONSERV")
    public String getNmVolumeConserv() {
        return this.nmVolumeConserv;
    }

    public void setNmVolumeConserv(String nmVolumeConserv) {
        this.nmVolumeConserv = nmVolumeConserv;
    }

    @Column(name = "NT_INDICE_VOLUME")
    public String getNtIndiceVolume() {
        return this.ntIndiceVolume;
    }

    public void setNtIndiceVolume(String ntIndiceVolume) {
        this.ntIndiceVolume = ntIndiceVolume;
    }

    @Column(name = "NT_VOLUME_CHIUSO")
    public String getNtVolumeChiuso() {
        return this.ntVolumeChiuso;
    }

    public void setNtVolumeChiuso(String ntVolumeChiuso) {
        this.ntVolumeChiuso = ntVolumeChiuso;
    }

    @Column(name = "TI_PRESENZA_FIRME")
    public String getTiPresenzaFirme() {
        return this.tiPresenzaFirme;
    }

    public void setTiPresenzaFirme(String tiPresenzaFirme) {
        this.tiPresenzaFirme = tiPresenzaFirme;
    }

    @Column(name = "TI_SCAD_CHIUS_VOLUME")
    public String getTiScadChiusVolume() {
        return this.tiScadChiusVolume;
    }

    public void setTiScadChiusVolume(String tiScadChiusVolume) {
        this.tiScadChiusVolume = tiScadChiusVolume;
    }

    @Column(name = "TI_STATO_VOLUME_CONSERV")
    public String getTiStatoVolumeConserv() {
        return this.tiStatoVolumeConserv;
    }

    public void setTiStatoVolumeConserv(String tiStatoVolumeConserv) {
        this.tiStatoVolumeConserv = tiStatoVolumeConserv;
    }

    @Column(name = "TI_TEMPO_SCAD_CHIUS")
    public String getTiTempoScadChius() {
        return this.tiTempoScadChius;
    }

    public void setTiTempoScadChius(String tiTempoScadChius) {
        this.tiTempoScadChius = tiTempoScadChius;
    }

    @Column(name = "TI_TEMPO_SCAD_CHIUS_FIRME")
    public String getTiTempoScadChiusFirme() {
        return this.tiTempoScadChiusFirme;
    }

    public void setTiTempoScadChiusFirme(String tiTempoScadChiusFirme) {
        this.tiTempoScadChiusFirme = tiTempoScadChiusFirme;
    }

    @Column(name = "TI_VAL_FIRME")
    public String getTiValFirme() {
        return this.tiValFirme;
    }

    public void setTiValFirme(String tiValFirme) {
        this.tiValFirme = tiValFirme;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TM_MARCA_FIRMA")
    public Date getTmMarcaFirma() {
        return this.tmMarcaFirma;
    }

    public void setTmMarcaFirma(Date tmMarcaFirma) {
        this.tmMarcaFirma = tmMarcaFirma;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TM_MARCA_INDICE")
    public Date getTmMarcaIndice() {
        return this.tmMarcaIndice;
    }

    public void setTmMarcaIndice(Date tmMarcaIndice) {
        this.tmMarcaIndice = tmMarcaIndice;
    }

    @OneToMany(mappedBy = "volVolumeConserv", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH,
            CascadeType.REFRESH })
    public List<VolAppartUnitaDocVolume> getVolAppartUnitaDocVolumes() {
        return this.volAppartUnitaDocVolumes;
    }

    public void setVolAppartUnitaDocVolumes(List<VolAppartUnitaDocVolume> volAppartUnitaDocVolumes) {
        this.volAppartUnitaDocVolumes = volAppartUnitaDocVolumes;
    }

    @OneToMany(mappedBy = "volVolumeConserv", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH,
            CascadeType.REFRESH })
    public List<VolFileVolumeConserv> getVolFileVolumeConservs() {
        return this.volFileVolumeConservs;
    }

    public void setVolFileVolumeConservs(List<VolFileVolumeConserv> volFileVolumeConservs) {
        this.volFileVolumeConservs = volFileVolumeConservs;
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
    @JoinColumn(name = "ID_STRUT")
    public OrgStrut getOrgStrut() {
        return this.orgStrut;
    }

    public void setOrgStrut(OrgStrut orgStrut) {
        this.orgStrut = orgStrut;
    }

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH,
            CascadeType.REFRESH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER_CREAZIONE")
    public IamUser getIamUserCreazione() {
        return this.iamUserCreazione;
    }

    public void setIamUserCreazione(IamUser iamUserCreazione) {
        this.iamUserCreazione = iamUserCreazione;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER_FIRMA_MARCA")
    public IamUser getIamUserFirmaMarca() {
        return this.iamUserFirmaMarca;
    }

    public void setIamUserFirmaMarca(IamUser iamUserFirmaMarca) {
        this.iamUserFirmaMarca = iamUserFirmaMarca;
    }
}
