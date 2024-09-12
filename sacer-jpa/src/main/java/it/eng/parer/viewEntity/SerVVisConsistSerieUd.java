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
 * The persistent class for the SER_V_VIS_CONSIST_SERIE_UD database table.
 *
 */
@Entity
@Table(name = "SER_V_VIS_CONSIST_SERIE_UD")

public class SerVVisConsistSerieUd implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigDecimal aaSerie;
    private BigDecimal aaUnitaDocFirst;
    private BigDecimal aaUnitaDocLast;
    private String cdCompositoSerie;
    private String cdDocConsistVerSerie;
    private String cdFirstUnitaDocAttesa;
    private String cdLastUnitaDocAttesa;
    private String cdRegistroFirst;
    private String cdRegistroLast;
    private String cdUnitaDocFirst;
    private String cdUnitaDocLast;
    private String cdVerSerie;
    private String dlLacunaMancanti;
    private String dlLacunaNonProdotte;
    private String dlNotaLacunaMancanti;
    private String dlNotaLacunaNonProdotte;
    private String dsDocConsistVerSerie;
    private String dsMsgSerieDaRigenera;
    private String dsSerie;
    private Date dtComunicConsistVerSerie;
    private BigDecimal idAmbiente;
    private BigDecimal idConsistVerSerie;
    private BigDecimal idEnte;
    private BigDecimal idLacunaMancanti;
    private BigDecimal idLacunaNonProdotte;
    private BigDecimal idSerie;
    private BigDecimal idStatoVerSerieCor;
    private BigDecimal idStrut;
    private BigDecimal idTipoSerie;
    private BigDecimal idUserIamConsist;
    private BigDecimal idVerSerie;
    private BigDecimal niUnitaDocAttese;
    private String nmAmbiente;
    private String nmEnte;
    private String nmStrut;
    private String nmTipoSerie;
    private String nmUseridConsist;
    private String tiModConsistFirstLast;
    private String tiStatoSerie;
    private String tiStatoVerSerie;

    public SerVVisConsistSerieUd() {/* Hibernate */
    }

    @Column(name = "AA_SERIE")
    public BigDecimal getAaSerie() {
        return this.aaSerie;
    }

    public void setAaSerie(BigDecimal aaSerie) {
        this.aaSerie = aaSerie;
    }

    @Column(name = "AA_UNITA_DOC_FIRST")
    public BigDecimal getAaUnitaDocFirst() {
        return this.aaUnitaDocFirst;
    }

    public void setAaUnitaDocFirst(BigDecimal aaUnitaDocFirst) {
        this.aaUnitaDocFirst = aaUnitaDocFirst;
    }

    @Column(name = "AA_UNITA_DOC_LAST")
    public BigDecimal getAaUnitaDocLast() {
        return this.aaUnitaDocLast;
    }

    public void setAaUnitaDocLast(BigDecimal aaUnitaDocLast) {
        this.aaUnitaDocLast = aaUnitaDocLast;
    }

    @Column(name = "CD_COMPOSITO_SERIE")
    public String getCdCompositoSerie() {
        return this.cdCompositoSerie;
    }

    public void setCdCompositoSerie(String cdCompositoSerie) {
        this.cdCompositoSerie = cdCompositoSerie;
    }

    @Column(name = "CD_DOC_CONSIST_VER_SERIE")
    public String getCdDocConsistVerSerie() {
        return this.cdDocConsistVerSerie;
    }

    public void setCdDocConsistVerSerie(String cdDocConsistVerSerie) {
        this.cdDocConsistVerSerie = cdDocConsistVerSerie;
    }

    @Column(name = "CD_FIRST_UNITA_DOC_ATTESA")
    public String getCdFirstUnitaDocAttesa() {
        return this.cdFirstUnitaDocAttesa;
    }

    public void setCdFirstUnitaDocAttesa(String cdFirstUnitaDocAttesa) {
        this.cdFirstUnitaDocAttesa = cdFirstUnitaDocAttesa;
    }

    @Column(name = "CD_LAST_UNITA_DOC_ATTESA")
    public String getCdLastUnitaDocAttesa() {
        return this.cdLastUnitaDocAttesa;
    }

    public void setCdLastUnitaDocAttesa(String cdLastUnitaDocAttesa) {
        this.cdLastUnitaDocAttesa = cdLastUnitaDocAttesa;
    }

    @Column(name = "CD_REGISTRO_FIRST")
    public String getCdRegistroFirst() {
        return this.cdRegistroFirst;
    }

    public void setCdRegistroFirst(String cdRegistroFirst) {
        this.cdRegistroFirst = cdRegistroFirst;
    }

    @Column(name = "CD_REGISTRO_LAST")
    public String getCdRegistroLast() {
        return this.cdRegistroLast;
    }

    public void setCdRegistroLast(String cdRegistroLast) {
        this.cdRegistroLast = cdRegistroLast;
    }

    @Column(name = "CD_UNITA_DOC_FIRST")
    public String getCdUnitaDocFirst() {
        return this.cdUnitaDocFirst;
    }

    public void setCdUnitaDocFirst(String cdUnitaDocFirst) {
        this.cdUnitaDocFirst = cdUnitaDocFirst;
    }

    @Column(name = "CD_UNITA_DOC_LAST")
    public String getCdUnitaDocLast() {
        return this.cdUnitaDocLast;
    }

    public void setCdUnitaDocLast(String cdUnitaDocLast) {
        this.cdUnitaDocLast = cdUnitaDocLast;
    }

    @Column(name = "CD_VER_SERIE")
    public String getCdVerSerie() {
        return this.cdVerSerie;
    }

    public void setCdVerSerie(String cdVerSerie) {
        this.cdVerSerie = cdVerSerie;
    }

    @Column(name = "DL_LACUNA_MANCANTI")
    public String getDlLacunaMancanti() {
        return this.dlLacunaMancanti;
    }

    public void setDlLacunaMancanti(String dlLacunaMancanti) {
        this.dlLacunaMancanti = dlLacunaMancanti;
    }

    @Column(name = "DL_LACUNA_NON_PRODOTTE")
    public String getDlLacunaNonProdotte() {
        return this.dlLacunaNonProdotte;
    }

    public void setDlLacunaNonProdotte(String dlLacunaNonProdotte) {
        this.dlLacunaNonProdotte = dlLacunaNonProdotte;
    }

    @Column(name = "DL_NOTA_LACUNA_MANCANTI")
    public String getDlNotaLacunaMancanti() {
        return this.dlNotaLacunaMancanti;
    }

    public void setDlNotaLacunaMancanti(String dlNotaLacunaMancanti) {
        this.dlNotaLacunaMancanti = dlNotaLacunaMancanti;
    }

    @Column(name = "DL_NOTA_LACUNA_NON_PRODOTTE")
    public String getDlNotaLacunaNonProdotte() {
        return this.dlNotaLacunaNonProdotte;
    }

    public void setDlNotaLacunaNonProdotte(String dlNotaLacunaNonProdotte) {
        this.dlNotaLacunaNonProdotte = dlNotaLacunaNonProdotte;
    }

    @Column(name = "DS_DOC_CONSIST_VER_SERIE")
    public String getDsDocConsistVerSerie() {
        return this.dsDocConsistVerSerie;
    }

    public void setDsDocConsistVerSerie(String dsDocConsistVerSerie) {
        this.dsDocConsistVerSerie = dsDocConsistVerSerie;
    }

    @Column(name = "DS_MSG_SERIE_DA_RIGENERA")
    public String getDsMsgSerieDaRigenera() {
        return this.dsMsgSerieDaRigenera;
    }

    public void setDsMsgSerieDaRigenera(String dsMsgSerieDaRigenera) {
        this.dsMsgSerieDaRigenera = dsMsgSerieDaRigenera;
    }

    @Column(name = "DS_SERIE")
    public String getDsSerie() {
        return this.dsSerie;
    }

    public void setDsSerie(String dsSerie) {
        this.dsSerie = dsSerie;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_COMUNIC_CONSIST_VER_SERIE")
    public Date getDtComunicConsistVerSerie() {
        return this.dtComunicConsistVerSerie;
    }

    public void setDtComunicConsistVerSerie(Date dtComunicConsistVerSerie) {
        this.dtComunicConsistVerSerie = dtComunicConsistVerSerie;
    }

    @Column(name = "ID_AMBIENTE")
    public BigDecimal getIdAmbiente() {
        return this.idAmbiente;
    }

    public void setIdAmbiente(BigDecimal idAmbiente) {
        this.idAmbiente = idAmbiente;
    }

    @Id
    @Column(name = "ID_CONSIST_VER_SERIE")
    public BigDecimal getIdConsistVerSerie() {
        return this.idConsistVerSerie;
    }

    public void setIdConsistVerSerie(BigDecimal idConsistVerSerie) {
        this.idConsistVerSerie = idConsistVerSerie;
    }

    @Column(name = "ID_ENTE")
    public BigDecimal getIdEnte() {
        return this.idEnte;
    }

    public void setIdEnte(BigDecimal idEnte) {
        this.idEnte = idEnte;
    }

    @Column(name = "ID_LACUNA_MANCANTI")
    public BigDecimal getIdLacunaMancanti() {
        return this.idLacunaMancanti;
    }

    public void setIdLacunaMancanti(BigDecimal idLacunaMancanti) {
        this.idLacunaMancanti = idLacunaMancanti;
    }

    @Column(name = "ID_LACUNA_NON_PRODOTTE")
    public BigDecimal getIdLacunaNonProdotte() {
        return this.idLacunaNonProdotte;
    }

    public void setIdLacunaNonProdotte(BigDecimal idLacunaNonProdotte) {
        this.idLacunaNonProdotte = idLacunaNonProdotte;
    }

    @Column(name = "ID_SERIE")
    public BigDecimal getIdSerie() {
        return this.idSerie;
    }

    public void setIdSerie(BigDecimal idSerie) {
        this.idSerie = idSerie;
    }

    @Column(name = "ID_STATO_VER_SERIE_COR")
    public BigDecimal getIdStatoVerSerieCor() {
        return this.idStatoVerSerieCor;
    }

    public void setIdStatoVerSerieCor(BigDecimal idStatoVerSerieCor) {
        this.idStatoVerSerieCor = idStatoVerSerieCor;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    @Column(name = "ID_TIPO_SERIE")
    public BigDecimal getIdTipoSerie() {
        return this.idTipoSerie;
    }

    public void setIdTipoSerie(BigDecimal idTipoSerie) {
        this.idTipoSerie = idTipoSerie;
    }

    @Column(name = "ID_USER_IAM_CONSIST")
    public BigDecimal getIdUserIamConsist() {
        return this.idUserIamConsist;
    }

    public void setIdUserIamConsist(BigDecimal idUserIamConsist) {
        this.idUserIamConsist = idUserIamConsist;
    }

    @Column(name = "ID_VER_SERIE")
    public BigDecimal getIdVerSerie() {
        return this.idVerSerie;
    }

    public void setIdVerSerie(BigDecimal idVerSerie) {
        this.idVerSerie = idVerSerie;
    }

    @Column(name = "NI_UNITA_DOC_ATTESE")
    public BigDecimal getNiUnitaDocAttese() {
        return this.niUnitaDocAttese;
    }

    public void setNiUnitaDocAttese(BigDecimal niUnitaDocAttese) {
        this.niUnitaDocAttese = niUnitaDocAttese;
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

    @Column(name = "NM_TIPO_SERIE")
    public String getNmTipoSerie() {
        return this.nmTipoSerie;
    }

    public void setNmTipoSerie(String nmTipoSerie) {
        this.nmTipoSerie = nmTipoSerie;
    }

    @Column(name = "NM_USERID_CONSIST")
    public String getNmUseridConsist() {
        return this.nmUseridConsist;
    }

    public void setNmUseridConsist(String nmUseridConsist) {
        this.nmUseridConsist = nmUseridConsist;
    }

    @Column(name = "TI_MOD_CONSIST_FIRST_LAST")
    public String getTiModConsistFirstLast() {
        return this.tiModConsistFirstLast;
    }

    public void setTiModConsistFirstLast(String tiModConsistFirstLast) {
        this.tiModConsistFirstLast = tiModConsistFirstLast;
    }

    @Column(name = "TI_STATO_SERIE")
    public String getTiStatoSerie() {
        return this.tiStatoSerie;
    }

    public void setTiStatoSerie(String tiStatoSerie) {
        this.tiStatoSerie = tiStatoSerie;
    }

    @Column(name = "TI_STATO_VER_SERIE")
    public String getTiStatoVerSerie() {
        return this.tiStatoVerSerie;
    }

    public void setTiStatoVerSerie(String tiStatoVerSerie) {
        this.tiStatoVerSerie = tiStatoVerSerie;
    }

}
