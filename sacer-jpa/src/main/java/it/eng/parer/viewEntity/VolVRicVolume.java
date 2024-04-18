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

package it.eng.parer.viewEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the VOL_V_RIC_VOLUME database table.
 * 
 */
@Entity
@Table(name = "VOL_V_RIC_VOLUME")
public class VolVRicVolume implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal aaKeyUnitaDoc;
    private String cdKeyUnitaDoc;
    private String cdRegistroKeyUnitaDoc;
    private String cdVersioneIndice;
    private String dlMotivoChius;
    private String dsVolumeConserv;
    private Date dtChius;
    private Date dtCreazione;
    private Date dtFirmaMarca;
    private Date dtScadChius;
    private Long idAppartUnitaDocVolume;
    private BigDecimal idCriterioRaggr;
    private BigDecimal idStrutCriterio;
    private BigDecimal idStrutUnitaDoc;
    private BigDecimal idStrutVolume;
    private BigDecimal idUnitaDoc;
    private BigDecimal idVolumeConserv;
    private BigDecimal niCompVolume;
    private BigDecimal niKbSize;
    private BigDecimal niMaxComp;
    private BigDecimal niMaxUnitaDoc;
    private BigDecimal niTempoScadChius;
    private BigDecimal niTempoScadChiusFirme;
    private BigDecimal niUnitaDocVolume;
    private String nmCriterioRaggr;
    private String nmVolumeConserv;
    private String ntIndiceVolume;
    private String ntVolumeChiuso;
    private String tiPresenzaFirme;
    private String tiScadChiusVolume;
    private String tiStatoVolumeConserv;
    private String tiTempoScadChius;
    private String tiTempoScadChiusFirme;
    private String tiValFirme;

    public VolVRicVolume() {/* Hibernate */
    }

    public VolVRicVolume(Date dtChius, Date dtCreazione, Date dtFirmaMarca, Date dtScadChius,
            BigDecimal idVolumeConserv, BigDecimal niCompVolume, BigDecimal niKbSize, BigDecimal niMaxComp,
            BigDecimal niMaxUnitaDoc, BigDecimal niTempoScadChius, BigDecimal niTempoScadChiusFirme,
            BigDecimal niUnitaDocVolume, String nmCriterioRaggr, String nmVolumeConserv, String dsVolumeConserv,
            String tiPresenzaFirme, String tiScadChiusVolume, String tiStatoVolumeConserv, String tiTempoScadChius,
            String tiTempoScadChiusFirme, String tiValFirme, String ntIndiceVolume, String dlMotivoChius,
            String cdVersioneIndice, String ntVolumeChiuso, BigDecimal idStrutVolume) {
        this.dtChius = dtChius;
        this.dtCreazione = dtCreazione;
        this.dtFirmaMarca = dtFirmaMarca;
        this.dtScadChius = dtScadChius;
        this.idVolumeConserv = idVolumeConserv;
        this.niCompVolume = niCompVolume;
        this.niKbSize = niKbSize;
        this.niMaxComp = niMaxComp;
        this.niMaxUnitaDoc = niMaxUnitaDoc;
        this.niTempoScadChius = niTempoScadChius;
        this.niTempoScadChiusFirme = niTempoScadChiusFirme;
        this.niUnitaDocVolume = niUnitaDocVolume;
        this.nmCriterioRaggr = nmCriterioRaggr;
        this.nmVolumeConserv = nmVolumeConserv;
        this.dsVolumeConserv = dsVolumeConserv;
        this.tiPresenzaFirme = tiPresenzaFirme;
        this.tiScadChiusVolume = tiScadChiusVolume;
        this.tiStatoVolumeConserv = tiStatoVolumeConserv;
        this.tiTempoScadChius = tiTempoScadChius;
        this.tiTempoScadChiusFirme = tiTempoScadChiusFirme;
        this.tiValFirme = tiValFirme;
        this.ntIndiceVolume = ntIndiceVolume;
        this.dlMotivoChius = dlMotivoChius;
        this.cdVersioneIndice = cdVersioneIndice;
        this.ntVolumeChiuso = ntVolumeChiuso;
        this.idStrutVolume = idStrutVolume;
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    public BigDecimal getAaKeyUnitaDoc() {
        return this.aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
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

    @Column(name = "DS_VOLUME_CONSERV")
    public String getDsVolumeConserv() {
        return dsVolumeConserv;
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

    @Id
    @Column(name = "ID_APPART_UNITA_DOC_VOLUME")
    public Long getIdAppartUnitaDocVolume() {
        return this.idAppartUnitaDocVolume;
    }

    public void setIdAppartUnitaDocVolume(Long idAppartUnitaDocVolume) {
        this.idAppartUnitaDocVolume = idAppartUnitaDocVolume;
    }

    @Column(name = "ID_CRITERIO_RAGGR")
    public BigDecimal getIdCriterioRaggr() {
        return this.idCriterioRaggr;
    }

    public void setIdCriterioRaggr(BigDecimal idCriterioRaggr) {
        this.idCriterioRaggr = idCriterioRaggr;
    }

    @Column(name = "ID_STRUT_CRITERIO")
    public BigDecimal getIdStrutCriterio() {
        return this.idStrutCriterio;
    }

    public void setIdStrutCriterio(BigDecimal idStrutCriterio) {
        this.idStrutCriterio = idStrutCriterio;
    }

    @Column(name = "ID_STRUT_UNITA_DOC")
    public BigDecimal getIdStrutUnitaDoc() {
        return this.idStrutUnitaDoc;
    }

    public void setIdStrutUnitaDoc(BigDecimal idStrutUnitaDoc) {
        this.idStrutUnitaDoc = idStrutUnitaDoc;
    }

    @Column(name = "ID_STRUT_VOLUME")
    public BigDecimal getIdStrutVolume() {
        return this.idStrutVolume;
    }

    public void setIdStrutVolume(BigDecimal idStrutVolume) {
        this.idStrutVolume = idStrutVolume;
    }

    @Column(name = "ID_UNITA_DOC")
    public BigDecimal getIdUnitaDoc() {
        return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
        this.idUnitaDoc = idUnitaDoc;
    }

    @Column(name = "ID_VOLUME_CONSERV")
    public BigDecimal getIdVolumeConserv() {
        return this.idVolumeConserv;
    }

    public void setIdVolumeConserv(BigDecimal idVolumeConserv) {
        this.idVolumeConserv = idVolumeConserv;
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

    @Column(name = "NM_CRITERIO_RAGGR")
    public String getNmCriterioRaggr() {
        return this.nmCriterioRaggr;
    }

    public void setNmCriterioRaggr(String nmCriterioRaggr) {
        this.nmCriterioRaggr = nmCriterioRaggr;
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
}
