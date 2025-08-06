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
import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the SER_V_VIS_CONTENUTO_SERIE_UD database table.
 *
 */
@Entity
@Table(name = "SER_V_VIS_CONTENUTO_SERIE_UD")

public class SerVVisContenutoSerieUd implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigDecimal aaSerie;
    private String cdCompositoSerie;
    private String cdDocConsistVerSerie;
    private String cdFirstUnitaDoc;
    private String cdFirstUnitaDocAttesa;
    private String cdLastUnitaDoc;
    private String cdLastUnitaDocAttesa;
    private String cdVerSerie;
    private String dsDocConsistVerSerie;
    private String dsMsgSerieDaRigenera;
    private String dsSerie;
    private Date dtFineSelSerie;
    private Date dtFirstUnitaDoc;
    private Date dtInizioSelSerie;
    private Date dtLastUnitaDoc;
    private Date dtStatoContenutoVerSerie;
    private String flErrContenuto;
    private String flErrContenutoFile;
    private String flPresenzaLacune;
    private BigDecimal idAmbiente;
    private BigDecimal idConsistVerSerie;
    private BigDecimal idContenutoVerSerie;
    private BigDecimal idEnte;
    private BigDecimal idSerie;
    private BigDecimal idStatoSerieCor;
    private BigDecimal idStatoVerSerieCor;
    private BigDecimal idStrut;
    private BigDecimal idTipoSerie;
    private BigDecimal idUserIamConsist;
    private BigDecimal idVerSerie;
    private BigDecimal niUnitaDoc;
    private BigDecimal niUnitaDocAttese;
    private BigDecimal niVolVerSerie;
    private String nmAmbiente;
    private String nmEnte;
    private String nmStrut;
    private String nmTipoSerie;
    private String nmUseridConsist;
    private String tiContenutoVerSerie;
    private String tiModConsistFirstLast;
    private String tiStatoContenutoVerSerie;
    private String tiStatoSerie;
    private String tiStatoVerSerie;

    public SerVVisContenutoSerieUd() {/* Hibernate */
    }

    @Column(name = "AA_SERIE")
    public BigDecimal getAaSerie() {
	return this.aaSerie;
    }

    public void setAaSerie(BigDecimal aaSerie) {
	this.aaSerie = aaSerie;
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

    @Column(name = "CD_FIRST_UNITA_DOC")
    public String getCdFirstUnitaDoc() {
	return this.cdFirstUnitaDoc;
    }

    public void setCdFirstUnitaDoc(String cdFirstUnitaDoc) {
	this.cdFirstUnitaDoc = cdFirstUnitaDoc;
    }

    @Column(name = "CD_FIRST_UNITA_DOC_ATTESA")
    public String getCdFirstUnitaDocAttesa() {
	return this.cdFirstUnitaDocAttesa;
    }

    public void setCdFirstUnitaDocAttesa(String cdFirstUnitaDocAttesa) {
	this.cdFirstUnitaDocAttesa = cdFirstUnitaDocAttesa;
    }

    @Column(name = "CD_LAST_UNITA_DOC")
    public String getCdLastUnitaDoc() {
	return this.cdLastUnitaDoc;
    }

    public void setCdLastUnitaDoc(String cdLastUnitaDoc) {
	this.cdLastUnitaDoc = cdLastUnitaDoc;
    }

    @Column(name = "CD_LAST_UNITA_DOC_ATTESA")
    public String getCdLastUnitaDocAttesa() {
	return this.cdLastUnitaDocAttesa;
    }

    public void setCdLastUnitaDocAttesa(String cdLastUnitaDocAttesa) {
	this.cdLastUnitaDocAttesa = cdLastUnitaDocAttesa;
    }

    @Column(name = "CD_VER_SERIE")
    public String getCdVerSerie() {
	return this.cdVerSerie;
    }

    public void setCdVerSerie(String cdVerSerie) {
	this.cdVerSerie = cdVerSerie;
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
    @Column(name = "DT_FINE_SEL_SERIE")
    public Date getDtFineSelSerie() {
	return this.dtFineSelSerie;
    }

    public void setDtFineSelSerie(Date dtFineSelSerie) {
	this.dtFineSelSerie = dtFineSelSerie;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIRST_UNITA_DOC")
    public Date getDtFirstUnitaDoc() {
	return this.dtFirstUnitaDoc;
    }

    public void setDtFirstUnitaDoc(Date dtFirstUnitaDoc) {
	this.dtFirstUnitaDoc = dtFirstUnitaDoc;
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
    @Column(name = "DT_LAST_UNITA_DOC")
    public Date getDtLastUnitaDoc() {
	return this.dtLastUnitaDoc;
    }

    public void setDtLastUnitaDoc(Date dtLastUnitaDoc) {
	this.dtLastUnitaDoc = dtLastUnitaDoc;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_STATO_CONTENUTO_VER_SERIE")
    public Date getDtStatoContenutoVerSerie() {
	return this.dtStatoContenutoVerSerie;
    }

    public void setDtStatoContenutoVerSerie(Date dtStatoContenutoVerSerie) {
	this.dtStatoContenutoVerSerie = dtStatoContenutoVerSerie;
    }

    @Column(name = "FL_ERR_CONTENUTO", columnDefinition = "char(1)")
    public String getFlErrContenuto() {
	return this.flErrContenuto;
    }

    public void setFlErrContenuto(String flErrContenuto) {
	this.flErrContenuto = flErrContenuto;
    }

    @Column(name = "FL_ERR_CONTENUTO_FILE", columnDefinition = "char(1)")
    public String getFlErrContenutoFile() {
	return this.flErrContenutoFile;
    }

    public void setFlErrContenutoFile(String flErrContenutoFile) {
	this.flErrContenutoFile = flErrContenutoFile;
    }

    @Column(name = "FL_PRESENZA_LACUNE", columnDefinition = "char(1)")
    public String getFlPresenzaLacune() {
	return this.flPresenzaLacune;
    }

    public void setFlPresenzaLacune(String flPresenzaLacune) {
	this.flPresenzaLacune = flPresenzaLacune;
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

    @Id
    @Column(name = "ID_CONTENUTO_VER_SERIE")
    public BigDecimal getIdContenutoVerSerie() {
	return this.idContenutoVerSerie;
    }

    public void setIdContenutoVerSerie(BigDecimal idContenutoVerSerie) {
	this.idContenutoVerSerie = idContenutoVerSerie;
    }

    @Column(name = "ID_ENTE")
    public BigDecimal getIdEnte() {
	return this.idEnte;
    }

    public void setIdEnte(BigDecimal idEnte) {
	this.idEnte = idEnte;
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

    @Column(name = "ID_VER_SERIE")
    public BigDecimal getIdVerSerie() {
	return this.idVerSerie;
    }

    public void setIdVerSerie(BigDecimal idVerSerie) {
	this.idVerSerie = idVerSerie;
    }

    @Column(name = "NI_UNITA_DOC")
    public BigDecimal getNiUnitaDoc() {
	return this.niUnitaDoc;
    }

    public void setNiUnitaDoc(BigDecimal niUnitaDoc) {
	this.niUnitaDoc = niUnitaDoc;
    }

    @Column(name = "NI_UNITA_DOC_ATTESE")
    public BigDecimal getNiUnitaDocAttese() {
	return this.niUnitaDocAttese;
    }

    public void setNiUnitaDocAttese(BigDecimal niUnitaDocAttese) {
	this.niUnitaDocAttese = niUnitaDocAttese;
    }

    @Column(name = "NI_VOL_VER_SERIE")
    public BigDecimal getNiVolVerSerie() {
	return this.niVolVerSerie;
    }

    public void setNiVolVerSerie(BigDecimal niVolVerSerie) {
	this.niVolVerSerie = niVolVerSerie;
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

    @Column(name = "TI_CONTENUTO_VER_SERIE")
    public String getTiContenutoVerSerie() {
	return this.tiContenutoVerSerie;
    }

    public void setTiContenutoVerSerie(String tiContenutoVerSerie) {
	this.tiContenutoVerSerie = tiContenutoVerSerie;
    }

    @Column(name = "TI_MOD_CONSIST_FIRST_LAST")
    public String getTiModConsistFirstLast() {
	return this.tiModConsistFirstLast;
    }

    public void setTiModConsistFirstLast(String tiModConsistFirstLast) {
	this.tiModConsistFirstLast = tiModConsistFirstLast;
    }

    @Column(name = "TI_STATO_CONTENUTO_VER_SERIE")
    public String getTiStatoContenutoVerSerie() {
	return this.tiStatoContenutoVerSerie;
    }

    public void setTiStatoContenutoVerSerie(String tiStatoContenutoVerSerie) {
	this.tiStatoContenutoVerSerie = tiStatoContenutoVerSerie;
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
