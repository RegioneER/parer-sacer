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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.Table;

/**
 * The persistent class for the MON_V_LIS_FASC_KO database table.
 *
 */
@Entity
@Table(name = "MON_V_LIS_FASC_KO")

public class MonVLisFascKo implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal aaFascicolo;
    private String cdErrPrinc;
    private String cdKeyFascicolo;
    private String dsErrPrinc;
    private BigDecimal idAmbiente;
    private BigDecimal idEnte;
    private BigDecimal idErrSacerPrinc;
    private BigDecimal idFascicoloKo;
    private BigDecimal idSesFascicoloKoFirst;
    private BigDecimal idSesFascicoloKoLast;
    private BigDecimal idStrut;
    private BigDecimal idTipoFascicolo;
    private BigDecimal idUserIam;
    private String nmAmbiente;
    private String nmEnte;
    private String nmStrut;
    private String nmTipoFascicolo;
    private String tiStatoFascicoloKo;
    private Timestamp tsIniFirstSes;
    private Timestamp tsIniLastSes;

    public MonVLisFascKo() {/* Hibernate */
    }

    @Column(name = "AA_FASCICOLO")
    public BigDecimal getAaFascicolo() {
        return this.aaFascicolo;
    }

    public void setAaFascicolo(BigDecimal aaFascicolo) {
        this.aaFascicolo = aaFascicolo;
    }

    @Column(name = "CD_ERR_PRINC")
    public String getCdErrPrinc() {
        return this.cdErrPrinc;
    }

    public void setCdErrPrinc(String cdErrPrinc) {
        this.cdErrPrinc = cdErrPrinc;
    }

    @Column(name = "CD_KEY_FASCICOLO")
    public String getCdKeyFascicolo() {
        return this.cdKeyFascicolo;
    }

    public void setCdKeyFascicolo(String cdKeyFascicolo) {
        this.cdKeyFascicolo = cdKeyFascicolo;
    }

    @Column(name = "DS_ERR_PRINC")
    public String getDsErrPrinc() {
        return this.dsErrPrinc;
    }

    public void setDsErrPrinc(String dsErrPrinc) {
        this.dsErrPrinc = dsErrPrinc;
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

    @Column(name = "ID_ERR_SACER_PRINC")
    public BigDecimal getIdErrSacerPrinc() {
        return this.idErrSacerPrinc;
    }

    public void setIdErrSacerPrinc(BigDecimal idErrSacerPrinc) {
        this.idErrSacerPrinc = idErrSacerPrinc;
    }

    @Id
    @Column(name = "ID_FASCICOLO_KO")
    public BigDecimal getIdFascicoloKo() {
        return this.idFascicoloKo;
    }

    public void setIdFascicoloKo(BigDecimal idFascicoloKo) {
        this.idFascicoloKo = idFascicoloKo;
    }

    @Column(name = "ID_SES_FASCICOLO_KO_FIRST")
    public BigDecimal getIdSesFascicoloKoFirst() {
        return this.idSesFascicoloKoFirst;
    }

    public void setIdSesFascicoloKoFirst(BigDecimal idSesFascicoloKoFirst) {
        this.idSesFascicoloKoFirst = idSesFascicoloKoFirst;
    }

    @Column(name = "ID_SES_FASCICOLO_KO_LAST")
    public BigDecimal getIdSesFascicoloKoLast() {
        return this.idSesFascicoloKoLast;
    }

    public void setIdSesFascicoloKoLast(BigDecimal idSesFascicoloKoLast) {
        this.idSesFascicoloKoLast = idSesFascicoloKoLast;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    @Column(name = "ID_TIPO_FASCICOLO")
    public BigDecimal getIdTipoFascicolo() {
        return this.idTipoFascicolo;
    }

    public void setIdTipoFascicolo(BigDecimal idTipoFascicolo) {
        this.idTipoFascicolo = idTipoFascicolo;
    }

    @Column(name = "ID_USER_IAM")
    public BigDecimal getIdUserIam() {
        return this.idUserIam;
    }

    public void setIdUserIam(BigDecimal idUserIam) {
        this.idUserIam = idUserIam;
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

    @Column(name = "NM_TIPO_FASCICOLO")
    public String getNmTipoFascicolo() {
        return this.nmTipoFascicolo;
    }

    public void setNmTipoFascicolo(String nmTipoFascicolo) {
        this.nmTipoFascicolo = nmTipoFascicolo;
    }

    @Column(name = "TI_STATO_FASCICOLO_KO")
    public String getTiStatoFascicoloKo() {
        return this.tiStatoFascicoloKo;
    }

    public void setTiStatoFascicoloKo(String tiStatoFascicoloKo) {
        this.tiStatoFascicoloKo = tiStatoFascicoloKo;
    }

    @Column(name = "TS_INI_FIRST_SES")
    public Timestamp getTsIniFirstSes() {
        return this.tsIniFirstSes;
    }

    public void setTsIniFirstSes(Timestamp tsIniFirstSes) {
        this.tsIniFirstSes = tsIniFirstSes;
    }

    @Column(name = "TS_INI_LAST_SES")
    public Timestamp getTsIniLastSes() {
        return this.tsIniLastSes;
    }

    public void setTsIniLastSes(Timestamp tsIniLastSes) {
        this.tsIniLastSes = tsIniLastSes;
    }

}
