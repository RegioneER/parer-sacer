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
 * The persistent class for the ARO_V_LIS_FIRMA_COMP database table.
 *
 */
@Entity
@Table(name = "ARO_V_LIS_FIRMA_COMP")

public class AroVLisFirmaComp implements Serializable {
    private static final long serialVersionUID = 1L;
    private String cdFirmatario;
    private String dsMsgEsitoVerifFirma;
    private String dsMsgVerifFirmaDtVers;
    private BigDecimal idCompDoc;
    private BigDecimal idFirmaComp;
    private String nmCognomeFirmatario;
    private String nmFirmatario;
    private BigDecimal pgFirma;
    private String tiEsitoContrConforme;
    private String tiEsitoVerifFirma;
    private String tiEsitoVerifFirmaDtVers;
    private String tiFormatoFirma;
    private String tiRifTempUsato;
    private Date tmRifTempUsato;

    public AroVLisFirmaComp() {/* Hibernate */
    }

    @Column(name = "CD_FIRMATARIO")
    public String getCdFirmatario() {
        return this.cdFirmatario;
    }

    public void setCdFirmatario(String cdFirmatario) {
        this.cdFirmatario = cdFirmatario;
    }

    @Column(name = "DS_MSG_ESITO_VERIF_FIRMA")
    public String getDsMsgEsitoVerifFirma() {
        return this.dsMsgEsitoVerifFirma;
    }

    public void setDsMsgEsitoVerifFirma(String dsMsgEsitoVerifFirma) {
        this.dsMsgEsitoVerifFirma = dsMsgEsitoVerifFirma;
    }

    @Column(name = "DS_MSG_VERIF_FIRMA_DT_VERS")
    public String getDsMsgVerifFirmaDtVers() {
        return this.dsMsgVerifFirmaDtVers;
    }

    public void setDsMsgVerifFirmaDtVers(String dsMsgVerifFirmaDtVers) {
        this.dsMsgVerifFirmaDtVers = dsMsgVerifFirmaDtVers;
    }

    @Column(name = "ID_COMP_DOC")
    public BigDecimal getIdCompDoc() {
        return this.idCompDoc;
    }

    public void setIdCompDoc(BigDecimal idCompDoc) {
        this.idCompDoc = idCompDoc;
    }

    @Id
    @Column(name = "ID_FIRMA_COMP")
    public BigDecimal getIdFirmaComp() {
        return this.idFirmaComp;
    }

    public void setIdFirmaComp(BigDecimal idFirmaComp) {
        this.idFirmaComp = idFirmaComp;
    }

    @Column(name = "NM_COGNOME_FIRMATARIO")
    public String getNmCognomeFirmatario() {
        return this.nmCognomeFirmatario;
    }

    public void setNmCognomeFirmatario(String nmCognomeFirmatario) {
        this.nmCognomeFirmatario = nmCognomeFirmatario;
    }

    @Column(name = "NM_FIRMATARIO")
    public String getNmFirmatario() {
        return this.nmFirmatario;
    }

    public void setNmFirmatario(String nmFirmatario) {
        this.nmFirmatario = nmFirmatario;
    }

    @Column(name = "PG_FIRMA")
    public BigDecimal getPgFirma() {
        return this.pgFirma;
    }

    public void setPgFirma(BigDecimal pgFirma) {
        this.pgFirma = pgFirma;
    }

    @Column(name = "TI_ESITO_CONTR_CONFORME")
    public String getTiEsitoContrConforme() {
        return this.tiEsitoContrConforme;
    }

    public void setTiEsitoContrConforme(String tiEsitoContrConforme) {
        this.tiEsitoContrConforme = tiEsitoContrConforme;
    }

    @Column(name = "TI_ESITO_VERIF_FIRMA")
    public String getTiEsitoVerifFirma() {
        return this.tiEsitoVerifFirma;
    }

    public void setTiEsitoVerifFirma(String tiEsitoVerifFirma) {
        this.tiEsitoVerifFirma = tiEsitoVerifFirma;
    }

    @Column(name = "TI_ESITO_VERIF_FIRMA_DT_VERS")
    public String getTiEsitoVerifFirmaDtVers() {
        return this.tiEsitoVerifFirmaDtVers;
    }

    public void setTiEsitoVerifFirmaDtVers(String tiEsitoVerifFirmaDtVers) {
        this.tiEsitoVerifFirmaDtVers = tiEsitoVerifFirmaDtVers;
    }

    @Column(name = "TI_FORMATO_FIRMA")
    public String getTiFormatoFirma() {
        return this.tiFormatoFirma;
    }

    public void setTiFormatoFirma(String tiFormatoFirma) {
        this.tiFormatoFirma = tiFormatoFirma;
    }

    @Column(name = "TI_RIF_TEMP_USATO")
    public String getTiRifTempUsato() {
        return this.tiRifTempUsato;
    }

    public void setTiRifTempUsato(String tiRifTempUsato) {
        this.tiRifTempUsato = tiRifTempUsato;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TM_RIF_TEMP_USATO")
    public Date getTmRifTempUsato() {
        return this.tmRifTempUsato;
    }

    public void setTmRifTempUsato(Date tmRifTempUsato) {
        this.tmRifTempUsato = tmRifTempUsato;
    }

}
