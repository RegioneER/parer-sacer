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

package it.eng.parer.viewEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the ARO_V_RIC_RICH_ANNVRS database table.
 */
@Entity
@Table(name = "ARO_V_RIC_RICH_ANNVRS")

public class AroVRicRichAnnvrs implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal aaFascicolo;

    private BigDecimal aaKeyUnitaDoc;

    private String cdKeyFascicolo;

    private String cdKeyUnitaDoc;

    private String cdRegistroKeyUnitaDoc;

    private String cdRichAnnulVers;

    private String dsRichAnnulVers;

    private Date dtCreazioneRichAnnulVers;

    private String flAnnulPing;

    private String flImmediata;

    private String flNonAnnul;

    private BigDecimal idAmbiente;

    private BigDecimal idEnte;

    private BigDecimal idItemRichAnnulVers;

    private BigDecimal idStrut;

    private BigDecimal niItem;

    private BigDecimal niItemNonAnnul;

    private BigDecimal niItemPing;

    private String nmAmbiente;

    private String nmEnte;

    private String nmStrut;

    private String ntRichAnnulVers;

    private String tiCreazioneRichAnnulVers;

    private String tiRichAnnulVers;

    private String tiStatoRichAnnulVersCor;

    private String tiAnnullamento;

    public AroVRicRichAnnvrs() {/* Hibernate */
    }

    public AroVRicRichAnnvrs(String cdRichAnnulVers, String dsRichAnnulVers,
            Date dtCreazioneRichAnnulVers, String flAnnulPing, String flImmediata,
            String flNonAnnul, BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idRichAnnulVers,
            BigDecimal idStrut, BigDecimal idUserIam, BigDecimal niItem, BigDecimal niItemNonAnnul,
            BigDecimal niItemPing, String nmAmbiente, String nmEnte, String nmStrut,
            String ntRichAnnulVers, String tiCreazioneRichAnnulVers, String tiStatoRichAnnulVersCor,
            String tiAnnullamento) {
        this.cdRichAnnulVers = cdRichAnnulVers;
        this.dsRichAnnulVers = dsRichAnnulVers;
        this.dtCreazioneRichAnnulVers = dtCreazioneRichAnnulVers;
        this.flAnnulPing = flAnnulPing;
        this.flImmediata = flImmediata;
        this.flNonAnnul = flNonAnnul;
        this.idAmbiente = idAmbiente;
        this.idEnte = idEnte;
        this.aroVRicRichAnnvrsId = new AroVRicRichAnnvrsId();
        this.aroVRicRichAnnvrsId.setIdRichAnnulVers(idRichAnnulVers);
        this.aroVRicRichAnnvrsId.setIdUserIam(idUserIam);
        this.idStrut = idStrut;
        this.niItem = niItem;
        this.niItemNonAnnul = niItemNonAnnul;
        this.niItemPing = niItemPing;
        this.nmAmbiente = nmAmbiente;
        this.nmEnte = nmEnte;
        this.nmStrut = nmStrut;
        this.ntRichAnnulVers = ntRichAnnulVers;
        this.tiCreazioneRichAnnulVers = tiCreazioneRichAnnulVers;
        this.tiStatoRichAnnulVersCor = tiStatoRichAnnulVersCor;
        this.tiAnnullamento = tiAnnullamento;
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

    @Column(name = "CD_RICH_ANNUL_VERS")
    public String getCdRichAnnulVers() {
        return this.cdRichAnnulVers;
    }

    public void setCdRichAnnulVers(String cdRichAnnulVers) {
        this.cdRichAnnulVers = cdRichAnnulVers;
    }

    @Column(name = "DS_RICH_ANNUL_VERS")
    public String getDsRichAnnulVers() {
        return this.dsRichAnnulVers;
    }

    public void setDsRichAnnulVers(String dsRichAnnulVers) {
        this.dsRichAnnulVers = dsRichAnnulVers;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE_RICH_ANNUL_VERS")
    public Date getDtCreazioneRichAnnulVers() {
        return this.dtCreazioneRichAnnulVers;
    }

    public void setDtCreazioneRichAnnulVers(Date dtCreazioneRichAnnulVers) {
        this.dtCreazioneRichAnnulVers = dtCreazioneRichAnnulVers;
    }

    @Column(name = "FL_ANNUL_PING", columnDefinition = "char(1)")
    public String getFlAnnulPing() {
        return this.flAnnulPing;
    }

    public void setFlAnnulPing(String flAnnulPing) {
        this.flAnnulPing = flAnnulPing;
    }

    @Column(name = "FL_IMMEDIATA", columnDefinition = "char(1)")
    public String getFlImmediata() {
        return this.flImmediata;
    }

    public void setFlImmediata(String flImmediata) {
        this.flImmediata = flImmediata;
    }

    @Column(name = "FL_NON_ANNUL", columnDefinition = "char(1)")
    public String getFlNonAnnul() {
        return this.flNonAnnul;
    }

    public void setFlNonAnnul(String flNonAnnul) {
        this.flNonAnnul = flNonAnnul;
    }

    @Column(name = "ID_AMBIENTE")
    public BigDecimal getIdAmbiente() {
        return this.idAmbiente;
    }

    public void setIdAmbiente(BigDecimal idAmbiente) {
        this.idAmbiente = idAmbiente;
    }

    @Column(name = "ID_ENTE")
    public BigDecimal getIdEnte() {
        return this.idEnte;
    }

    public void setIdEnte(BigDecimal idEnte) {
        this.idEnte = idEnte;
    }

    @Column(name = "ID_ITEM_RICH_ANNUL_VERS")
    public BigDecimal getIdItemRichAnnulVers() {
        return this.idItemRichAnnulVers;
    }

    public void setIdItemRichAnnulVers(BigDecimal idItemRichAnnulVers) {
        this.idItemRichAnnulVers = idItemRichAnnulVers;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    @Column(name = "NI_ITEM")
    public BigDecimal getNiItem() {
        return this.niItem;
    }

    public void setNiItem(BigDecimal niItem) {
        this.niItem = niItem;
    }

    @Column(name = "NI_ITEM_NON_ANNUL")
    public BigDecimal getNiItemNonAnnul() {
        return this.niItemNonAnnul;
    }

    public void setNiItemNonAnnul(BigDecimal niItemNonAnnul) {
        this.niItemNonAnnul = niItemNonAnnul;
    }

    @Column(name = "NI_ITEM_PING")
    public BigDecimal getNiItemPing() {
        return this.niItemPing;
    }

    public void setNiItemPing(BigDecimal niItemPing) {
        this.niItemPing = niItemPing;
    }

    @Column(name = "NM_AMBIENTE")
    public String getNmAmbiente() {
        return this.nmAmbiente;
    }

    public void setNmAmbiente(String nmAmbiente) {
        this.nmAmbiente = nmAmbiente;
    }

    @Column(name = "NM_ENTE")
    public String getNmEnte() {
        return this.nmEnte;
    }

    public void setNmEnte(String nmEnte) {
        this.nmEnte = nmEnte;
    }

    @Column(name = "NM_STRUT")
    public String getNmStrut() {
        return this.nmStrut;
    }

    public void setNmStrut(String nmStrut) {
        this.nmStrut = nmStrut;
    }

    @Column(name = "NT_RICH_ANNUL_VERS")
    public String getNtRichAnnulVers() {
        return this.ntRichAnnulVers;
    }

    public void setNtRichAnnulVers(String ntRichAnnulVers) {
        this.ntRichAnnulVers = ntRichAnnulVers;
    }

    @Column(name = "TI_CREAZIONE_RICH_ANNUL_VERS")
    public String getTiCreazioneRichAnnulVers() {
        return this.tiCreazioneRichAnnulVers;
    }

    public void setTiCreazioneRichAnnulVers(String tiCreazioneRichAnnulVers) {
        this.tiCreazioneRichAnnulVers = tiCreazioneRichAnnulVers;
    }

    @Column(name = "TI_RICH_ANNUL_VERS")
    public String getTiRichAnnulVers() {
        return this.tiRichAnnulVers;
    }

    public void setTiRichAnnulVers(String tiRichAnnulVers) {
        this.tiRichAnnulVers = tiRichAnnulVers;
    }

    @Column(name = "TI_STATO_RICH_ANNUL_VERS_COR")
    public String getTiStatoRichAnnulVersCor() {
        return this.tiStatoRichAnnulVersCor;
    }

    public void setTiStatoRichAnnulVersCor(String tiStatoRichAnnulVersCor) {
        this.tiStatoRichAnnulVersCor = tiStatoRichAnnulVersCor;
    }

    @Column(name = "TI_ANNULLAMENTO")
    public String getTiAnnullamento() {
        return this.tiAnnullamento;
    }

    public void setTiAnnullamento(String tiAnnullamento) {
        this.tiAnnullamento = tiAnnullamento;
    }

    private AroVRicRichAnnvrsId aroVRicRichAnnvrsId;

    @EmbeddedId()
    public AroVRicRichAnnvrsId getAroVRicRichAnnvrsId() {
        return aroVRicRichAnnvrsId;
    }

    public void setAroVRicRichAnnvrsId(AroVRicRichAnnvrsId aroVRicRichAnnvrsId) {
        this.aroVRicRichAnnvrsId = aroVRicRichAnnvrsId;
    }
}
