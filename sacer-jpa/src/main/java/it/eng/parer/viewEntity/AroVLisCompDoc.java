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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the ARO_V_LIS_COMP_DOC database table.
 * 
 */
@Entity
@Table(name = "ARO_V_LIS_COMP_DOC")
public class AroVLisCompDoc implements Serializable {

    private static final long serialVersionUID = 1L;
    private String dsFormatoRapprCalc;
    private String dsMsgEsitoVerifFirme;
    private String dsNomeCompVers;
    private String dsUrnCompCalc;
    private String flCompFirmato;
    private BigDecimal idCompDoc;
    private BigDecimal idDoc;
    private BigDecimal niOrdCompDoc;
    private BigDecimal niOrdCompPadre;
    private BigDecimal niSizeFileCalc;
    private String nmFormatoCalc;
    private String nmFormatoVers;
    private String nmTipoCompDoc;
    private String tiEsitoVerifFirme;
    private String tiSupportoComp;

    public AroVLisCompDoc() {/* Hibernate */
    }

    @Column(name = "DS_FORMATO_RAPPR_CALC")
    public String getDsFormatoRapprCalc() {
        return this.dsFormatoRapprCalc;
    }

    public void setDsFormatoRapprCalc(String dsFormatoRapprCalc) {
        this.dsFormatoRapprCalc = dsFormatoRapprCalc;
    }

    @Column(name = "DS_MSG_ESITO_VERIF_FIRME")
    public String getDsMsgEsitoVerifFirme() {
        return this.dsMsgEsitoVerifFirme;
    }

    public void setDsMsgEsitoVerifFirme(String dsMsgEsitoVerifFirme) {
        this.dsMsgEsitoVerifFirme = dsMsgEsitoVerifFirme;
    }

    @Column(name = "DS_NOME_COMP_VERS")
    public String getDsNomeCompVers() {
        return this.dsNomeCompVers;
    }

    public void setDsNomeCompVers(String dsNomeCompVers) {
        this.dsNomeCompVers = dsNomeCompVers;
    }

    @Column(name = "DS_URN_COMP_CALC")
    public String getDsUrnCompCalc() {
        return this.dsUrnCompCalc;
    }

    public void setDsUrnCompCalc(String dsUrnCompCalc) {
        this.dsUrnCompCalc = dsUrnCompCalc;
    }

    @Column(name = "FL_COMP_FIRMATO", columnDefinition = "char(1)")
    public String getFlCompFirmato() {
        return this.flCompFirmato;
    }

    public void setFlCompFirmato(String flCompFirmato) {
        this.flCompFirmato = flCompFirmato;
    }

    @Id
    @Column(name = "ID_COMP_DOC")
    public BigDecimal getIdCompDoc() {
        return this.idCompDoc;
    }

    public void setIdCompDoc(BigDecimal idCompDoc) {
        this.idCompDoc = idCompDoc;
    }

    @Column(name = "ID_DOC")
    public BigDecimal getIdDoc() {
        return this.idDoc;
    }

    public void setIdDoc(BigDecimal idDoc) {
        this.idDoc = idDoc;
    }

    @Column(name = "NI_ORD_COMP_DOC")
    public BigDecimal getNiOrdCompDoc() {
        return this.niOrdCompDoc;
    }

    public void setNiOrdCompDoc(BigDecimal niOrdCompDoc) {
        this.niOrdCompDoc = niOrdCompDoc;
    }

    @Column(name = "NI_ORD_COMP_PADRE")
    public BigDecimal getNiOrdCompPadre() {
        return this.niOrdCompPadre;
    }

    public void setNiOrdCompPadre(BigDecimal niOrdCompPadre) {
        this.niOrdCompPadre = niOrdCompPadre;
    }

    @Column(name = "NI_SIZE_FILE_CALC")
    public BigDecimal getNiSizeFileCalc() {
        return this.niSizeFileCalc;
    }

    public void setNiSizeFileCalc(BigDecimal niSizeFileCalc) {
        this.niSizeFileCalc = niSizeFileCalc;
    }

    @Column(name = "NM_FORMATO_CALC")
    public String getNmFormatoCalc() {
        return this.nmFormatoCalc;
    }

    public void setNmFormatoCalc(String nmFormatoCalc) {
        this.nmFormatoCalc = nmFormatoCalc;
    }

    @Column(name = "NM_FORMATO_VERS")
    public String getNmFormatoVers() {
        return this.nmFormatoVers;
    }

    public void setNmFormatoVers(String nmFormatoVers) {
        this.nmFormatoVers = nmFormatoVers;
    }

    @Column(name = "NM_TIPO_COMP_DOC")
    public String getNmTipoCompDoc() {
        return this.nmTipoCompDoc;
    }

    public void setNmTipoCompDoc(String nmTipoCompDoc) {
        this.nmTipoCompDoc = nmTipoCompDoc;
    }

    @Column(name = "TI_ESITO_VERIF_FIRME")
    public String getTiEsitoVerifFirme() {
        return this.tiEsitoVerifFirme;
    }

    public void setTiEsitoVerifFirme(String tiEsitoVerifFirme) {
        this.tiEsitoVerifFirme = tiEsitoVerifFirme;
    }

    @Column(name = "TI_SUPPORTO_COMP")
    public String getTiSupportoComp() {
        return this.tiSupportoComp;
    }

    public void setTiSupportoComp(String tiSupportoComp) {
        this.tiSupportoComp = tiSupportoComp;
    }

}
