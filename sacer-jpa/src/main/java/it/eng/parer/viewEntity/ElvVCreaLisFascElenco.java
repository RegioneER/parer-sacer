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
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the ELV_V_CREA_LIS_FASC_ELENCO database table.
 * 
 */
@Entity
@Table(name = "ELV_V_CREA_LIS_FASC_ELENCO")

public class ElvVCreaLisFascElenco implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigDecimal aaFascicolo;
    private String cdEncodingHashFascicolo;
    private String cdKeyFascicolo;
    private String dsAlgoHashFascicolo;
    private String dsHashFascicolo;
    private String dsOggettoFascicolo;
    private String dsUrnFascicolo;
    private String dsUrnSipFascicolo;
    private String dsUrnRappVers;
    private Date dtApeFascicolo;
    private Date dtChiuFascicolo;
    private BigDecimal niUnitaDoc;
    private String nmTipoFascicolo;
    private Timestamp tsIniSes;

    public ElvVCreaLisFascElenco() {/* Hibernate */
    }

    @Column(name = "AA_FASCICOLO")
    public BigDecimal getAaFascicolo() {
        return this.aaFascicolo;
    }

    public void setAaFascicolo(BigDecimal aaFascicolo) {
        this.aaFascicolo = aaFascicolo;
    }

    @Column(name = "CD_ENCODING_HASH_FASCICOLO")
    public String getCdEncodingHashFascicolo() {
        return this.cdEncodingHashFascicolo;
    }

    public void setCdEncodingHashFascicolo(String cdEncodingHashFascicolo) {
        this.cdEncodingHashFascicolo = cdEncodingHashFascicolo;
    }

    @Column(name = "CD_KEY_FASCICOLO")
    public String getCdKeyFascicolo() {
        return this.cdKeyFascicolo;
    }

    public void setCdKeyFascicolo(String cdKeyFascicolo) {
        this.cdKeyFascicolo = cdKeyFascicolo;
    }

    @Column(name = "DS_ALGO_HASH_FASCICOLO")
    public String getDsAlgoHashFascicolo() {
        return this.dsAlgoHashFascicolo;
    }

    public void setDsAlgoHashFascicolo(String dsAlgoHashFascicolo) {
        this.dsAlgoHashFascicolo = dsAlgoHashFascicolo;
    }

    @Column(name = "DS_HASH_FASCICOLO")
    public String getDsHashFascicolo() {
        return this.dsHashFascicolo;
    }

    public void setDsHashFascicolo(String dsHashFascicolo) {
        this.dsHashFascicolo = dsHashFascicolo;
    }

    @Column(name = "DS_OGGETTO_FASCICOLO")
    public String getDsOggettoFascicolo() {
        return this.dsOggettoFascicolo;
    }

    public void setDsOggettoFascicolo(String dsOggettoFascicolo) {
        this.dsOggettoFascicolo = dsOggettoFascicolo;
    }

    @Column(name = "DS_URN_FASCICOLO")
    public String getDsUrnFascicolo() {
        return this.dsUrnFascicolo;
    }

    public void setDsUrnFascicolo(String dsUrnFascicolo) {
        this.dsUrnFascicolo = dsUrnFascicolo;
    }

    @Column(name = "DS_URN_SIP_FASCICOLO")
    public String getDsUrnSipFascicolo() {
        return this.dsUrnSipFascicolo;
    }

    public void setDsUrnSipFascicolo(String dsUrnSipFascicolo) {
        this.dsUrnSipFascicolo = dsUrnSipFascicolo;
    }

    @Column(name = "DS_URN_RAPP_VERS")
    public String getDsUrnRappVers() {
        return this.dsUrnRappVers;
    }

    public void setDsUrnRappVers(String dsUrnRappVers) {
        this.dsUrnRappVers = dsUrnRappVers;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_APE_FASCICOLO")
    public Date getDtApeFascicolo() {
        return this.dtApeFascicolo;
    }

    public void setDtApeFascicolo(Date dtApeFascicolo) {
        this.dtApeFascicolo = dtApeFascicolo;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_CHIU_FASCICOLO")
    public Date getDtChiuFascicolo() {
        return this.dtChiuFascicolo;
    }

    public void setDtChiuFascicolo(Date dtChiuFascicolo) {
        this.dtChiuFascicolo = dtChiuFascicolo;
    }

    @Column(name = "NI_UNITA_DOC")
    public BigDecimal getNiUnitaDoc() {
        return this.niUnitaDoc;
    }

    public void setNiUnitaDoc(BigDecimal niUnitaDoc) {
        this.niUnitaDoc = niUnitaDoc;
    }

    @Column(name = "NM_TIPO_FASCICOLO")
    public String getNmTipoFascicolo() {
        return this.nmTipoFascicolo;
    }

    public void setNmTipoFascicolo(String nmTipoFascicolo) {
        this.nmTipoFascicolo = nmTipoFascicolo;
    }

    @Column(name = "TS_INI_SES")
    public Timestamp getTsIniSes() {
        return this.tsIniSes;
    }

    public void setTsIniSes(Timestamp tsIniSes) {
        this.tsIniSes = tsIniSes;
    }

    private ElvVCreaLisFascElencoId elvVCreaLisFascElencoId;

    @EmbeddedId()
    public ElvVCreaLisFascElencoId getElvVCreaLisFascElencoId() {
        return elvVCreaLisFascElencoId;
    }

    public void setElvVCreaLisFascElencoId(ElvVCreaLisFascElencoId elvVCreaLisFascElencoId) {
        this.elvVCreaLisFascElencoId = elvVCreaLisFascElencoId;
    }
}
