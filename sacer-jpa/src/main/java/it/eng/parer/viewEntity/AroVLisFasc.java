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
 * The persistent class for the ARO_V_LIS_FASC database table.
 */
@Entity
@Table(name = "ARO_V_LIS_FASC")

public class AroVLisFasc implements Serializable {

    private static final long serialVersionUID = 1L;

    private String cdKeyFascicolo;

    private String cdCompositoVoceTitol;

    private String tiEsito;

    private Date dtApeFascicolo;

    private Date dtChiuFascicolo;

    private String dsOggettoFascicolo;

    private BigDecimal idStrut;

    private BigDecimal idTipoFascicolo;

    private BigDecimal idVoceTitol;

    private String nmTipoFascicolo;

    private BigDecimal aaFascicolo;

    private BigDecimal niUnitaDoc;

    private BigDecimal idUserIamCorrente;

    private String tiStatoConservazione;

    private String tiStatoFascElencoVers;

    private Date tsIniSes;

    public AroVLisFasc(BigDecimal idFascicolo, BigDecimal aaFascicolo, String cdKeyFascicolo,
            String nmTipoFascicolo, Date dtApeFascicolo, Date dtChiuFascicolo,
            String cdCompositoVoceTitol, BigDecimal niUnitaDoc, Date tsIniSes, String tiEsito,
            String tiStatoFascElencoVers, String tiStatoConservazione) {
        this.aroVLisFascId = new AroVLisFascId();
        aroVLisFascId.setIdFascicolo(idFascicolo);
        this.aaFascicolo = aaFascicolo;
        this.cdKeyFascicolo = cdKeyFascicolo;
        this.nmTipoFascicolo = nmTipoFascicolo;
        this.dtApeFascicolo = dtApeFascicolo;
        this.dtChiuFascicolo = dtChiuFascicolo;
        this.cdCompositoVoceTitol = cdCompositoVoceTitol;
        this.niUnitaDoc = niUnitaDoc;
        this.tsIniSes = tsIniSes;
        this.tiEsito = tiEsito;
        this.tiStatoFascElencoVers = tiStatoFascElencoVers;
        this.tiStatoConservazione = tiStatoConservazione;
    }

    public AroVLisFasc() {/* Hibernate */
    }

    @Column(name = "CD_KEY_FASCICOLO")
    public String getCdKeyFascicolo() {
        return this.cdKeyFascicolo;
    }

    public void setCdKeyFascicolo(String cdKeyFascicolo) {
        this.cdKeyFascicolo = cdKeyFascicolo;
    }

    @Column(name = "CD_COMPOSITO_VOCE_TITOL")
    public String getCdCompositoVoceTitol() {
        return this.cdCompositoVoceTitol;
    }

    public void setCdCompositoVoceTitol(String cdCompositoVoceTitol) {
        this.cdCompositoVoceTitol = cdCompositoVoceTitol;
    }

    @Column(name = "TI_ESITO")
    public String getTiEsito() {
        return this.tiEsito;
    }

    public void setTiEsito(String tiEsito) {
        this.tiEsito = tiEsito;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_APE_FASCICOLO")
    public Date getDtApeFascicolo() {
        return this.dtApeFascicolo;
    }

    public void setDtApeFascicolo(Date dtApeFascicolo) {
        this.dtApeFascicolo = dtApeFascicolo;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CHIU_FASCICOLO")
    public Date getDtChiuFascicolo() {
        return this.dtChiuFascicolo;
    }

    public void setDtChiuFascicolo(Date dtChiuFascicolo) {
        this.dtChiuFascicolo = dtChiuFascicolo;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TS_INI_SES")
    public Date getTsIniSes() {
        return this.tsIniSes;
    }

    public void setTsIniSes(Date tsIniSes) {
        this.tsIniSes = tsIniSes;
    }

    @Column(name = "DS_OGGETTO_FASCICOLO")
    public String getDsOggettoFascicolo() {
        return this.dsOggettoFascicolo;
    }

    public void setDsOggettoFascicolo(String dsOggettoFascicolo) {
        this.dsOggettoFascicolo = dsOggettoFascicolo;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    @Column(name = "NM_TIPO_FASCICOLO")
    public String getNmTipoFascicolo() {
        return this.nmTipoFascicolo;
    }

    public void setNmTipoFascicolo(String nmTipoFascicolo) {
        this.nmTipoFascicolo = nmTipoFascicolo;
    }

    @Column(name = "TI_STATO_CONSERVAZIONE")
    public String getTiStatoConservazione() {
        return this.tiStatoConservazione;
    }

    public void setTiStatoConservazione(String tiStatoConservazione) {
        this.tiStatoConservazione = tiStatoConservazione;
    }

    @Column(name = "TI_STATO_FASC_ELENCO_VERS")
    public String getTiStatoFascElencoVers() {
        return this.tiStatoFascElencoVers;
    }

    public void setTiStatoFascElencoVers(String tiStatoFascElencoVers) {
        this.tiStatoFascElencoVers = tiStatoFascElencoVers;
    }

    @Column(name = "ID_TIPO_FASCICOLO")
    public BigDecimal getIdTipoFascicolo() {
        return this.idTipoFascicolo;
    }

    public void setIdTipoFascicolo(BigDecimal idTipoFascicolo) {
        this.idTipoFascicolo = idTipoFascicolo;
    }

    @Column(name = "ID_VOCE_TITOL")
    public BigDecimal getIdVoceTitol() {
        return this.idVoceTitol;
    }

    public void setIdVoceTitol(BigDecimal idVoceTitol) {
        this.idVoceTitol = idVoceTitol;
    }

    @Column(name = "ID_USER_IAM_CORRENTE")
    public BigDecimal getIdUserIamCorrente() {
        return this.idUserIamCorrente;
    }

    public void setIdUserIamCorrente(BigDecimal idUserIamCorrente) {
        this.idUserIamCorrente = idUserIamCorrente;
    }

    @Column(name = "AA_FASCICOLO")
    public BigDecimal getAaFascicolo() {
        return this.aaFascicolo;
    }

    public void setAaFascicolo(BigDecimal aaFascicolo) {
        this.aaFascicolo = aaFascicolo;
    }

    @Column(name = "NI_UNITA_DOC")
    public BigDecimal getNiUnitaDoc() {
        return this.niUnitaDoc;
    }

    public void setNiUnitaDoc(BigDecimal niUnitaDoc) {
        this.niUnitaDoc = niUnitaDoc;
    }

    private AroVLisFascId aroVLisFascId;

    @EmbeddedId()
    public AroVLisFascId getAroVLisFascId() {
        return aroVLisFascId;
    }

    public void setAroVLisFascId(AroVLisFascId aroVLisFascId) {
        this.aroVLisFascId = aroVLisFascId;
    }
}
