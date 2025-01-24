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
import javax.persistence.Lob;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the SER_V_VIS_SERIE_UD database table.
 *
 */
@Entity
@Table(name = "SER_V_VIS_SERIE_UD")

public class SerVVisSerieUd implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal aaSerie;
    private byte[] blFileIxAip;
    private String cdCompositoSerie;
    private String cdDocConsistVerSerie;
    private String cdDocFileInputVerSerie;
    private String cdEncodingHashIxAip;
    private String cdFirstUnitaDocAcq;
    private String cdFirstUnitaDocAttesa;
    private String cdFirstUnitaDocCalc;
    private String cdFirstUnitaDocEff;
    private String cdLastUnitaDocAcq;
    private String cdLastUnitaDocAttesa;
    private String cdLastUnitaDocCalc;
    private String cdLastUnitaDocEff;
    private String cdVerSerie;
    private String cdVerXsdAip;
    private String dsAlgoHashIxAip;
    private String dsAzione;
    private String dsAzioneStatoSerie;
    private String dsDocConsistVerSerie;
    private String dsDocFileInputVerSerie;
    private String dsFmtIxAip;
    private String dsHashIxAip;
    private String dsListaAnniSelSerie;
    private String dsMsgSerieDaRigenera;
    private String dsNotaAzione;
    private String dsNotaAzioneStatoSerie;
    private String dsSerie;
    private Date dtCreazioneIxAip;
    private Date dtCreazioneUnitaDocA;
    private Date dtCreazioneUnitaDocDa;
    private Date dtFineSelSerie;
    private Date dtFirstUnitaDocAcq;
    private Date dtFirstUnitaDocCalc;
    private Date dtFirstUnitaDocEff;
    private Date dtInizioSelSerie;
    private Date dtLastUnitaDocAcq;
    private Date dtLastUnitaDocCalc;
    private Date dtLastUnitaDocEff;
    private Date dtRegStatoSerie;
    private Date dtRegStatoVerSerie;
    private Date dtRegUnitaDocA;
    private Date dtRegUnitaDocDa;
    private Date dtStatoContenutoAcq;
    private Date dtStatoContenutoCalc;
    private Date dtStatoContenutoEff;
    private String flErrContenutoAcq;
    private String flErrContenutoCalc;
    private String flErrContenutoEff;
    private String flErrContenutoFile;
    private String flFornitoEnte;
    private String flPresenzaLacune;
    private String flUpdAnnulUnitaDoc;
    private String flUpdModifUnitaDoc;
    private BigDecimal idAmbiente;
    private BigDecimal idConsistVerSerie;
    private BigDecimal idContenutoAcq;
    private BigDecimal idContenutoCalc;
    private BigDecimal idContenutoEff;
    private BigDecimal idEnte;
    private BigDecimal idEnteConserv;
    private BigDecimal idFileInputVerSerie;
    private BigDecimal idSerie;
    private BigDecimal idStatoSerieCor;
    private BigDecimal idStatoVerSerieCor;
    private BigDecimal idStrut;
    private BigDecimal idTipoSerie;
    private BigDecimal idUserIamConsist;
    private BigDecimal idUserIamStato;
    private BigDecimal idUserIamStatoSerie;
    private BigDecimal idVerSerie;
    private BigDecimal niAnniConserv;
    private BigDecimal niUnitaDocAcq;
    private BigDecimal niUnitaDocAttese;
    private BigDecimal niUnitaDocCalc;
    private BigDecimal niUnitaDocEff;
    private String nmAmbiente;
    private String nmEnte;
    private String nmStrut;
    private String nmTipoSerie;
    private String nmUseridConsist;
    private String nmUseridFile;
    private String nmUseridStato;
    private String nmUseridStatoSerie;
    private String tiModConsistFirstLast;
    private String tiStatoContenutoAcq;
    private String tiStatoContenutoCalc;
    private String tiStatoContenutoEff;
    private String tiStatoSerie;
    private String tiStatoVerSerie;

    public SerVVisSerieUd() {/* Hibernate */
    }

    @Column(name = "AA_SERIE")
    public BigDecimal getAaSerie() {
        return this.aaSerie;
    }

    public void setAaSerie(BigDecimal aaSerie) {
        this.aaSerie = aaSerie;
    }

    @Lob
    @Column(name = "BL_FILE_IX_AIP")
    public byte[] getBlFileIxAip() {
        return this.blFileIxAip;
    }

    public void setBlFileIxAip(byte[] blFileIxAip) {
        this.blFileIxAip = blFileIxAip;
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

    @Column(name = "CD_DOC_FILE_INPUT_VER_SERIE")
    public String getCdDocFileInputVerSerie() {
        return this.cdDocFileInputVerSerie;
    }

    public void setCdDocFileInputVerSerie(String cdDocFileInputVerSerie) {
        this.cdDocFileInputVerSerie = cdDocFileInputVerSerie;
    }

    @Column(name = "CD_ENCODING_HASH_IX_AIP")
    public String getCdEncodingHashIxAip() {
        return this.cdEncodingHashIxAip;
    }

    public void setCdEncodingHashIxAip(String cdEncodingHashIxAip) {
        this.cdEncodingHashIxAip = cdEncodingHashIxAip;
    }

    @Column(name = "CD_FIRST_UNITA_DOC_ACQ")
    public String getCdFirstUnitaDocAcq() {
        return this.cdFirstUnitaDocAcq;
    }

    public void setCdFirstUnitaDocAcq(String cdFirstUnitaDocAcq) {
        this.cdFirstUnitaDocAcq = cdFirstUnitaDocAcq;
    }

    @Column(name = "CD_FIRST_UNITA_DOC_ATTESA")
    public String getCdFirstUnitaDocAttesa() {
        return this.cdFirstUnitaDocAttesa;
    }

    public void setCdFirstUnitaDocAttesa(String cdFirstUnitaDocAttesa) {
        this.cdFirstUnitaDocAttesa = cdFirstUnitaDocAttesa;
    }

    @Column(name = "CD_FIRST_UNITA_DOC_CALC")
    public String getCdFirstUnitaDocCalc() {
        return this.cdFirstUnitaDocCalc;
    }

    public void setCdFirstUnitaDocCalc(String cdFirstUnitaDocCalc) {
        this.cdFirstUnitaDocCalc = cdFirstUnitaDocCalc;
    }

    @Column(name = "CD_FIRST_UNITA_DOC_EFF")
    public String getCdFirstUnitaDocEff() {
        return this.cdFirstUnitaDocEff;
    }

    public void setCdFirstUnitaDocEff(String cdFirstUnitaDocEff) {
        this.cdFirstUnitaDocEff = cdFirstUnitaDocEff;
    }

    @Column(name = "CD_LAST_UNITA_DOC_ACQ")
    public String getCdLastUnitaDocAcq() {
        return this.cdLastUnitaDocAcq;
    }

    public void setCdLastUnitaDocAcq(String cdLastUnitaDocAcq) {
        this.cdLastUnitaDocAcq = cdLastUnitaDocAcq;
    }

    @Column(name = "CD_LAST_UNITA_DOC_ATTESA")
    public String getCdLastUnitaDocAttesa() {
        return this.cdLastUnitaDocAttesa;
    }

    public void setCdLastUnitaDocAttesa(String cdLastUnitaDocAttesa) {
        this.cdLastUnitaDocAttesa = cdLastUnitaDocAttesa;
    }

    @Column(name = "CD_LAST_UNITA_DOC_CALC")
    public String getCdLastUnitaDocCalc() {
        return this.cdLastUnitaDocCalc;
    }

    public void setCdLastUnitaDocCalc(String cdLastUnitaDocCalc) {
        this.cdLastUnitaDocCalc = cdLastUnitaDocCalc;
    }

    @Column(name = "CD_LAST_UNITA_DOC_EFF")
    public String getCdLastUnitaDocEff() {
        return this.cdLastUnitaDocEff;
    }

    public void setCdLastUnitaDocEff(String cdLastUnitaDocEff) {
        this.cdLastUnitaDocEff = cdLastUnitaDocEff;
    }

    @Column(name = "CD_VER_SERIE")
    public String getCdVerSerie() {
        return this.cdVerSerie;
    }

    public void setCdVerSerie(String cdVerSerie) {
        this.cdVerSerie = cdVerSerie;
    }

    @Column(name = "CD_VER_XSD_AIP")
    public String getCdVerXsdAip() {
        return this.cdVerXsdAip;
    }

    public void setCdVerXsdAip(String cdVerXsdAip) {
        this.cdVerXsdAip = cdVerXsdAip;
    }

    @Column(name = "DS_ALGO_HASH_IX_AIP")
    public String getDsAlgoHashIxAip() {
        return this.dsAlgoHashIxAip;
    }

    public void setDsAlgoHashIxAip(String dsAlgoHashIxAip) {
        this.dsAlgoHashIxAip = dsAlgoHashIxAip;
    }

    @Column(name = "DS_AZIONE")
    public String getDsAzione() {
        return this.dsAzione;
    }

    public void setDsAzione(String dsAzione) {
        this.dsAzione = dsAzione;
    }

    @Column(name = "DS_AZIONE_STATO_SERIE")
    public String getDsAzioneStatoSerie() {
        return this.dsAzioneStatoSerie;
    }

    public void setDsAzioneStatoSerie(String dsAzioneStatoSerie) {
        this.dsAzioneStatoSerie = dsAzioneStatoSerie;
    }

    @Column(name = "DS_DOC_CONSIST_VER_SERIE")
    public String getDsDocConsistVerSerie() {
        return this.dsDocConsistVerSerie;
    }

    public void setDsDocConsistVerSerie(String dsDocConsistVerSerie) {
        this.dsDocConsistVerSerie = dsDocConsistVerSerie;
    }

    @Column(name = "DS_DOC_FILE_INPUT_VER_SERIE")
    public String getDsDocFileInputVerSerie() {
        return this.dsDocFileInputVerSerie;
    }

    public void setDsDocFileInputVerSerie(String dsDocFileInputVerSerie) {
        this.dsDocFileInputVerSerie = dsDocFileInputVerSerie;
    }

    @Column(name = "DS_FMT_IX_AIP", columnDefinition = "char")
    public String getDsFmtIxAip() {
        return this.dsFmtIxAip;
    }

    public void setDsFmtIxAip(String dsFmtIxAip) {
        this.dsFmtIxAip = dsFmtIxAip;
    }

    @Column(name = "DS_HASH_IX_AIP")
    public String getDsHashIxAip() {
        return this.dsHashIxAip;
    }

    public void setDsHashIxAip(String dsHashIxAip) {
        this.dsHashIxAip = dsHashIxAip;
    }

    @Column(name = "DS_LISTA_ANNI_SEL_SERIE")
    public String getDsListaAnniSelSerie() {
        return this.dsListaAnniSelSerie;
    }

    public void setDsListaAnniSelSerie(String dsListaAnniSelSerie) {
        this.dsListaAnniSelSerie = dsListaAnniSelSerie;
    }

    @Column(name = "DS_MSG_SERIE_DA_RIGENERA")
    public String getDsMsgSerieDaRigenera() {
        return this.dsMsgSerieDaRigenera;
    }

    public void setDsMsgSerieDaRigenera(String dsMsgSerieDaRigenera) {
        this.dsMsgSerieDaRigenera = dsMsgSerieDaRigenera;
    }

    @Column(name = "DS_NOTA_AZIONE")
    public String getDsNotaAzione() {
        return this.dsNotaAzione;
    }

    public void setDsNotaAzione(String dsNotaAzione) {
        this.dsNotaAzione = dsNotaAzione;
    }

    @Column(name = "DS_NOTA_AZIONE_STATO_SERIE")
    public String getDsNotaAzioneStatoSerie() {
        return this.dsNotaAzioneStatoSerie;
    }

    public void setDsNotaAzioneStatoSerie(String dsNotaAzioneStatoSerie) {
        this.dsNotaAzioneStatoSerie = dsNotaAzioneStatoSerie;
    }

    @Column(name = "DS_SERIE")
    public String getDsSerie() {
        return this.dsSerie;
    }

    public void setDsSerie(String dsSerie) {
        this.dsSerie = dsSerie;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE_IX_AIP")
    public Date getDtCreazioneIxAip() {
        return this.dtCreazioneIxAip;
    }

    public void setDtCreazioneIxAip(Date dtCreazioneIxAip) {
        this.dtCreazioneIxAip = dtCreazioneIxAip;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE_UNITA_DOC_A")
    public Date getDtCreazioneUnitaDocA() {
        return this.dtCreazioneUnitaDocA;
    }

    public void setDtCreazioneUnitaDocA(Date dtCreazioneUnitaDocA) {
        this.dtCreazioneUnitaDocA = dtCreazioneUnitaDocA;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE_UNITA_DOC_DA")
    public Date getDtCreazioneUnitaDocDa() {
        return this.dtCreazioneUnitaDocDa;
    }

    public void setDtCreazioneUnitaDocDa(Date dtCreazioneUnitaDocDa) {
        this.dtCreazioneUnitaDocDa = dtCreazioneUnitaDocDa;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FINE_SEL_SERIE")
    public Date getDtFineSelSerie() {
        return this.dtFineSelSerie;
    }

    public void setDtFineSelSerie(Date dtFineSelSerie) {
        this.dtFineSelSerie = dtFineSelSerie;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIRST_UNITA_DOC_ACQ")
    public Date getDtFirstUnitaDocAcq() {
        return this.dtFirstUnitaDocAcq;
    }

    public void setDtFirstUnitaDocAcq(Date dtFirstUnitaDocAcq) {
        this.dtFirstUnitaDocAcq = dtFirstUnitaDocAcq;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIRST_UNITA_DOC_CALC")
    public Date getDtFirstUnitaDocCalc() {
        return this.dtFirstUnitaDocCalc;
    }

    public void setDtFirstUnitaDocCalc(Date dtFirstUnitaDocCalc) {
        this.dtFirstUnitaDocCalc = dtFirstUnitaDocCalc;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIRST_UNITA_DOC_EFF")
    public Date getDtFirstUnitaDocEff() {
        return this.dtFirstUnitaDocEff;
    }

    public void setDtFirstUnitaDocEff(Date dtFirstUnitaDocEff) {
        this.dtFirstUnitaDocEff = dtFirstUnitaDocEff;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INIZIO_SEL_SERIE")
    public Date getDtInizioSelSerie() {
        return this.dtInizioSelSerie;
    }

    public void setDtInizioSelSerie(Date dtInizioSelSerie) {
        this.dtInizioSelSerie = dtInizioSelSerie;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_LAST_UNITA_DOC_ACQ")
    public Date getDtLastUnitaDocAcq() {
        return this.dtLastUnitaDocAcq;
    }

    public void setDtLastUnitaDocAcq(Date dtLastUnitaDocAcq) {
        this.dtLastUnitaDocAcq = dtLastUnitaDocAcq;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_LAST_UNITA_DOC_CALC")
    public Date getDtLastUnitaDocCalc() {
        return this.dtLastUnitaDocCalc;
    }

    public void setDtLastUnitaDocCalc(Date dtLastUnitaDocCalc) {
        this.dtLastUnitaDocCalc = dtLastUnitaDocCalc;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_LAST_UNITA_DOC_EFF")
    public Date getDtLastUnitaDocEff() {
        return this.dtLastUnitaDocEff;
    }

    public void setDtLastUnitaDocEff(Date dtLastUnitaDocEff) {
        this.dtLastUnitaDocEff = dtLastUnitaDocEff;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_REG_STATO_SERIE")
    public Date getDtRegStatoSerie() {
        return this.dtRegStatoSerie;
    }

    public void setDtRegStatoSerie(Date dtRegStatoSerie) {
        this.dtRegStatoSerie = dtRegStatoSerie;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_REG_STATO_VER_SERIE")
    public Date getDtRegStatoVerSerie() {
        return this.dtRegStatoVerSerie;
    }

    public void setDtRegStatoVerSerie(Date dtRegStatoVerSerie) {
        this.dtRegStatoVerSerie = dtRegStatoVerSerie;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_REG_UNITA_DOC_A")
    public Date getDtRegUnitaDocA() {
        return this.dtRegUnitaDocA;
    }

    public void setDtRegUnitaDocA(Date dtRegUnitaDocA) {
        this.dtRegUnitaDocA = dtRegUnitaDocA;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_REG_UNITA_DOC_DA")
    public Date getDtRegUnitaDocDa() {
        return this.dtRegUnitaDocDa;
    }

    public void setDtRegUnitaDocDa(Date dtRegUnitaDocDa) {
        this.dtRegUnitaDocDa = dtRegUnitaDocDa;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_STATO_CONTENUTO_ACQ")
    public Date getDtStatoContenutoAcq() {
        return this.dtStatoContenutoAcq;
    }

    public void setDtStatoContenutoAcq(Date dtStatoContenutoAcq) {
        this.dtStatoContenutoAcq = dtStatoContenutoAcq;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_STATO_CONTENUTO_CALC")
    public Date getDtStatoContenutoCalc() {
        return this.dtStatoContenutoCalc;
    }

    public void setDtStatoContenutoCalc(Date dtStatoContenutoCalc) {
        this.dtStatoContenutoCalc = dtStatoContenutoCalc;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_STATO_CONTENUTO_EFF")
    public Date getDtStatoContenutoEff() {
        return this.dtStatoContenutoEff;
    }

    public void setDtStatoContenutoEff(Date dtStatoContenutoEff) {
        this.dtStatoContenutoEff = dtStatoContenutoEff;
    }

    @Column(name = "FL_ERR_CONTENUTO_ACQ", columnDefinition = "char(1)")
    public String getFlErrContenutoAcq() {
        return this.flErrContenutoAcq;
    }

    public void setFlErrContenutoAcq(String flErrContenutoAcq) {
        this.flErrContenutoAcq = flErrContenutoAcq;
    }

    @Column(name = "FL_ERR_CONTENUTO_CALC", columnDefinition = "char(1)")
    public String getFlErrContenutoCalc() {
        return this.flErrContenutoCalc;
    }

    public void setFlErrContenutoCalc(String flErrContenutoCalc) {
        this.flErrContenutoCalc = flErrContenutoCalc;
    }

    @Column(name = "FL_ERR_CONTENUTO_EFF", columnDefinition = "char(1)")
    public String getFlErrContenutoEff() {
        return this.flErrContenutoEff;
    }

    public void setFlErrContenutoEff(String flErrContenutoEff) {
        this.flErrContenutoEff = flErrContenutoEff;
    }

    @Column(name = "FL_ERR_CONTENUTO_FILE", columnDefinition = "char(1)")
    public String getFlErrContenutoFile() {
        return this.flErrContenutoFile;
    }

    public void setFlErrContenutoFile(String flErrContenutoFile) {
        this.flErrContenutoFile = flErrContenutoFile;
    }

    @Column(name = "FL_FORNITO_ENTE", columnDefinition = "char(1)")
    public String getFlFornitoEnte() {
        return this.flFornitoEnte;
    }

    public void setFlFornitoEnte(String flFornitoEnte) {
        this.flFornitoEnte = flFornitoEnte;
    }

    @Column(name = "FL_PRESENZA_LACUNE", columnDefinition = "char(1)")
    public String getFlPresenzaLacune() {
        return this.flPresenzaLacune;
    }

    public void setFlPresenzaLacune(String flPresenzaLacune) {
        this.flPresenzaLacune = flPresenzaLacune;
    }

    @Column(name = "FL_UPD_ANNUL_UNITA_DOC", columnDefinition = "char(1)")
    public String getFlUpdAnnulUnitaDoc() {
        return this.flUpdAnnulUnitaDoc;
    }

    public void setFlUpdAnnulUnitaDoc(String flUpdAnnulUnitaDoc) {
        this.flUpdAnnulUnitaDoc = flUpdAnnulUnitaDoc;
    }

    @Column(name = "FL_UPD_MODIF_UNITA_DOC", columnDefinition = "char(1)")
    public String getFlUpdModifUnitaDoc() {
        return this.flUpdModifUnitaDoc;
    }

    public void setFlUpdModifUnitaDoc(String flUpdModifUnitaDoc) {
        this.flUpdModifUnitaDoc = flUpdModifUnitaDoc;
    }

    @Column(name = "ID_AMBIENTE")
    public BigDecimal getIdAmbiente() {
        return this.idAmbiente;
    }

    public void setIdAmbiente(BigDecimal idAmbiente) {
        this.idAmbiente = idAmbiente;
    }

    @Column(name = "ID_CONSIST_VER_SERIE")
    public BigDecimal getIdConsistVerSerie() {
        return this.idConsistVerSerie;
    }

    public void setIdConsistVerSerie(BigDecimal idConsistVerSerie) {
        this.idConsistVerSerie = idConsistVerSerie;
    }

    @Column(name = "ID_CONTENUTO_ACQ")
    public BigDecimal getIdContenutoAcq() {
        return this.idContenutoAcq;
    }

    public void setIdContenutoAcq(BigDecimal idContenutoAcq) {
        this.idContenutoAcq = idContenutoAcq;
    }

    @Column(name = "ID_CONTENUTO_CALC")
    public BigDecimal getIdContenutoCalc() {
        return this.idContenutoCalc;
    }

    public void setIdContenutoCalc(BigDecimal idContenutoCalc) {
        this.idContenutoCalc = idContenutoCalc;
    }

    @Column(name = "ID_CONTENUTO_EFF")
    public BigDecimal getIdContenutoEff() {
        return this.idContenutoEff;
    }

    public void setIdContenutoEff(BigDecimal idContenutoEff) {
        this.idContenutoEff = idContenutoEff;
    }

    @Column(name = "ID_ENTE")
    public BigDecimal getIdEnte() {
        return this.idEnte;
    }

    public void setIdEnte(BigDecimal idEnte) {
        this.idEnte = idEnte;
    }

    @Column(name = "ID_ENTE_CONSERV")
    public BigDecimal getIdEnteConserv() {
        return this.idEnteConserv;
    }

    public void setIdEnteConserv(BigDecimal idEnteConserv) {
        this.idEnteConserv = idEnteConserv;
    }

    @Column(name = "ID_FILE_INPUT_VER_SERIE")
    public BigDecimal getIdFileInputVerSerie() {
        return this.idFileInputVerSerie;
    }

    public void setIdFileInputVerSerie(BigDecimal idFileInputVerSerie) {
        this.idFileInputVerSerie = idFileInputVerSerie;
    }

    @Column(name = "ID_SERIE")
    public BigDecimal getIdSerie() {
        return this.idSerie;
    }

    public void setIdSerie(BigDecimal idSerie) {
        this.idSerie = idSerie;
    }

    @Column(name = "ID_STATO_SERIE_COR")
    public BigDecimal getIdStatoSerieCor() {
        return this.idStatoSerieCor;
    }

    public void setIdStatoSerieCor(BigDecimal idStatoSerieCor) {
        this.idStatoSerieCor = idStatoSerieCor;
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

    @Column(name = "ID_USER_IAM_STATO")
    public BigDecimal getIdUserIamStato() {
        return this.idUserIamStato;
    }

    public void setIdUserIamStato(BigDecimal idUserIamStato) {
        this.idUserIamStato = idUserIamStato;
    }

    @Column(name = "ID_USER_IAM_STATO_SERIE")
    public BigDecimal getIdUserIamStatoSerie() {
        return this.idUserIamStatoSerie;
    }

    public void setIdUserIamStatoSerie(BigDecimal idUserIamStatoSerie) {
        this.idUserIamStatoSerie = idUserIamStatoSerie;
    }

    @Id
    @Column(name = "ID_VER_SERIE")
    public BigDecimal getIdVerSerie() {
        return this.idVerSerie;
    }

    public void setIdVerSerie(BigDecimal idVerSerie) {
        this.idVerSerie = idVerSerie;
    }

    @Column(name = "NI_ANNI_CONSERV")
    public BigDecimal getNiAnniConserv() {
        return this.niAnniConserv;
    }

    public void setNiAnniConserv(BigDecimal niAnniConserv) {
        this.niAnniConserv = niAnniConserv;
    }

    @Column(name = "NI_UNITA_DOC_ACQ")
    public BigDecimal getNiUnitaDocAcq() {
        return this.niUnitaDocAcq;
    }

    public void setNiUnitaDocAcq(BigDecimal niUnitaDocAcq) {
        this.niUnitaDocAcq = niUnitaDocAcq;
    }

    @Column(name = "NI_UNITA_DOC_ATTESE")
    public BigDecimal getNiUnitaDocAttese() {
        return this.niUnitaDocAttese;
    }

    public void setNiUnitaDocAttese(BigDecimal niUnitaDocAttese) {
        this.niUnitaDocAttese = niUnitaDocAttese;
    }

    @Column(name = "NI_UNITA_DOC_CALC")
    public BigDecimal getNiUnitaDocCalc() {
        return this.niUnitaDocCalc;
    }

    public void setNiUnitaDocCalc(BigDecimal niUnitaDocCalc) {
        this.niUnitaDocCalc = niUnitaDocCalc;
    }

    @Column(name = "NI_UNITA_DOC_EFF")
    public BigDecimal getNiUnitaDocEff() {
        return this.niUnitaDocEff;
    }

    public void setNiUnitaDocEff(BigDecimal niUnitaDocEff) {
        this.niUnitaDocEff = niUnitaDocEff;
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

    @Column(name = "NM_USERID_FILE")
    public String getNmUseridFile() {
        return this.nmUseridFile;
    }

    public void setNmUseridFile(String nmUseridFile) {
        this.nmUseridFile = nmUseridFile;
    }

    @Column(name = "NM_USERID_STATO")
    public String getNmUseridStato() {
        return this.nmUseridStato;
    }

    public void setNmUseridStato(String nmUseridStato) {
        this.nmUseridStato = nmUseridStato;
    }

    @Column(name = "NM_USERID_STATO_SERIE")
    public String getNmUseridStatoSerie() {
        return this.nmUseridStatoSerie;
    }

    public void setNmUseridStatoSerie(String nmUseridStatoSerie) {
        this.nmUseridStatoSerie = nmUseridStatoSerie;
    }

    @Column(name = "TI_MOD_CONSIST_FIRST_LAST")
    public String getTiModConsistFirstLast() {
        return this.tiModConsistFirstLast;
    }

    public void setTiModConsistFirstLast(String tiModConsistFirstLast) {
        this.tiModConsistFirstLast = tiModConsistFirstLast;
    }

    @Column(name = "TI_STATO_CONTENUTO_ACQ")
    public String getTiStatoContenutoAcq() {
        return this.tiStatoContenutoAcq;
    }

    public void setTiStatoContenutoAcq(String tiStatoContenutoAcq) {
        this.tiStatoContenutoAcq = tiStatoContenutoAcq;
    }

    @Column(name = "TI_STATO_CONTENUTO_CALC")
    public String getTiStatoContenutoCalc() {
        return this.tiStatoContenutoCalc;
    }

    public void setTiStatoContenutoCalc(String tiStatoContenutoCalc) {
        this.tiStatoContenutoCalc = tiStatoContenutoCalc;
    }

    @Column(name = "TI_STATO_CONTENUTO_EFF")
    public String getTiStatoContenutoEff() {
        return this.tiStatoContenutoEff;
    }

    public void setTiStatoContenutoEff(String tiStatoContenutoEff) {
        this.tiStatoContenutoEff = tiStatoContenutoEff;
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
