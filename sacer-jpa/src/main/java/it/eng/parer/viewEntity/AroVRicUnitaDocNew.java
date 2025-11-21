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
import javax.persistence.SqlResultSetMapping;
import javax.persistence.ConstructorResult;
import javax.persistence.ColumnResult;
import it.eng.parer.entity.dto.AroVRicUnitaDocNewDTO;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the ARO_V_RIC_UNITA_DOC database table.
 *
 */
@Entity
@Table(name = "ARO_V_RIC_UNITA_DOC_NEW")
// ===================================================================================
// MEV #39292: Definisce come mappare il risultato della query nativa al DTO.
// ===================================================================================
@SqlResultSetMapping(name = "AroVRicUnitaDocNewDTOMapping", classes = @ConstructorResult(targetClass = AroVRicUnitaDocNewDTO.class, columns = {
        @ColumnResult(name = "ID_UNITA_DOC", type = BigDecimal.class),
        @ColumnResult(name = "AA_KEY_UNITA_DOC", type = BigDecimal.class),
        @ColumnResult(name = "CD_KEY_UNITA_DOC", type = String.class),
        @ColumnResult(name = "CD_REGISTRO_KEY_UNITA_DOC", type = String.class),
        @ColumnResult(name = "DT_CREAZIONE", type = Date.class),
        @ColumnResult(name = "DT_REG_UNITA_DOC", type = Date.class),
        @ColumnResult(name = "FL_UNITA_DOC_FIRMATO", type = String.class),
        @ColumnResult(name = "TI_ESITO_VERIF_FIRME", type = String.class),
        @ColumnResult(name = "DS_MSG_ESITO_VERIF_FIRME", type = String.class),
        @ColumnResult(name = "NM_TIPO_UNITA_DOC", type = String.class),
        @ColumnResult(name = "FL_FORZA_ACCETTAZIONE", type = String.class),
        @ColumnResult(name = "FL_FORZA_CONSERVAZIONE", type = String.class),
        @ColumnResult(name = "DS_KEY_ORD", type = String.class),
        @ColumnResult(name = "NI_ALLEG", type = BigDecimal.class),
        @ColumnResult(name = "NI_ANNESSI", type = BigDecimal.class),
        @ColumnResult(name = "NI_ANNOT", type = BigDecimal.class),
        @ColumnResult(name = "NM_TIPO_DOC_PRINC", type = String.class),
        @ColumnResult(name = "DS_LISTA_STATI_ELENCO_VERS", type = String.class),
        @ColumnResult(name = "TI_STATO_CONSERVAZIONE", type = String.class) }))
public class AroVRicUnitaDocNew implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal aaKeyUnitaDoc;
    private String cdKeyDocVers;
    private String cdKeyUnitaDoc;
    private String cdRegistroKeyUnitaDoc;
    private String dsKeyOrd;
    private String dsListaStatiElencoVers;
    private String dsMsgEsitoVerifFirme;
    private Date dtCreazione;
    private Date dtRegUnitaDoc;
    private String flAggMeta;
    private String flDocAggiunti;
    private String flEsisteProfiloNormativo;
    private String flForzaAccettazione;
    private String flForzaConservazione;
    private String flUnitaDocFirmato;
    private BigDecimal idDoc;
    private BigDecimal idStrutTipoUnitaDoc;
    private BigDecimal idStrutUnitaDoc;
    private BigDecimal idSubStrut;
    private BigDecimal idTipoDoc;
    private BigDecimal idTipoUnitaDoc;
    private BigDecimal idUnitaDoc;
    private BigDecimal niAlleg;
    private BigDecimal niAnnessi;
    private BigDecimal niAnnot;
    private String nmTipoDocPrinc;
    private String nmTipoUnitaDoc;
    private String tiConservazione;
    private String tiDoc;
    private String tiEsitoVerifFirme;
    private String tiStatoConservazione;
    private String tiStatoDoc;
    private String tiStatoDocElencoVers;
    private String tiStatoUdElencoVers;

    public AroVRicUnitaDocNew() {/* Hibernate */
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    public BigDecimal getAaKeyUnitaDoc() {
        return this.aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    @Column(name = "CD_KEY_DOC_VERS")
    public String getCdKeyDocVers() {
        return this.cdKeyDocVers;
    }

    public void setCdKeyDocVers(String cdKeyDocVers) {
        this.cdKeyDocVers = cdKeyDocVers;
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

    @Column(name = "DS_KEY_ORD")
    public String getDsKeyOrd() {
        return this.dsKeyOrd;
    }

    public void setDsKeyOrd(String dsKeyOrd) {
        this.dsKeyOrd = dsKeyOrd;
    }

    @Column(name = "DS_LISTA_STATI_ELENCO_VERS")
    public String getDsListaStatiElencoVers() {
        return this.dsListaStatiElencoVers;
    }

    public void setDsListaStatiElencoVers(String dsListaStatiElencoVers) {
        this.dsListaStatiElencoVers = dsListaStatiElencoVers;
    }

    @Column(name = "DS_MSG_ESITO_VERIF_FIRME")
    public String getDsMsgEsitoVerifFirme() {
        return this.dsMsgEsitoVerifFirme;
    }

    public void setDsMsgEsitoVerifFirme(String dsMsgEsitoVerifFirme) {
        this.dsMsgEsitoVerifFirme = dsMsgEsitoVerifFirme;
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
    @Column(name = "DT_REG_UNITA_DOC")
    public Date getDtRegUnitaDoc() {
        return this.dtRegUnitaDoc;
    }

    public void setDtRegUnitaDoc(Date dtRegUnitaDoc) {
        this.dtRegUnitaDoc = dtRegUnitaDoc;
    }

    @Column(name = "FL_AGG_META", columnDefinition = "char(1)")
    public String getFlAggMeta() {
        return this.flAggMeta;
    }

    public void setFlAggMeta(String flAggMeta) {
        this.flAggMeta = flAggMeta;
    }

    @Column(name = "FL_DOC_AGGIUNTI", columnDefinition = "char(1)")
    public String getFlDocAggiunti() {
        return this.flDocAggiunti;
    }

    public void setFlDocAggiunti(String flDocAggiunti) {
        this.flDocAggiunti = flDocAggiunti;
    }

    @Column(name = "FL_ESISTE_PROFILO_NORMATIVO", columnDefinition = "char(1)")
    public String getFlEsisteProfiloNormativo() {
        return this.flEsisteProfiloNormativo;
    }

    public void setFlEsisteProfiloNormativo(String flEsisteProfiloNormativo) {
        this.flEsisteProfiloNormativo = flEsisteProfiloNormativo;
    }

    @Column(name = "FL_FORZA_ACCETTAZIONE", columnDefinition = "char(1)")
    public String getFlForzaAccettazione() {
        return this.flForzaAccettazione;
    }

    public void setFlForzaAccettazione(String flForzaAccettazione) {
        this.flForzaAccettazione = flForzaAccettazione;
    }

    @Column(name = "FL_FORZA_CONSERVAZIONE", columnDefinition = "char(1)")
    public String getFlForzaConservazione() {
        return this.flForzaConservazione;
    }

    public void setFlForzaConservazione(String flForzaConservazione) {
        this.flForzaConservazione = flForzaConservazione;
    }

    @Column(name = "FL_UNITA_DOC_FIRMATO", columnDefinition = "char(1)")
    public String getFlUnitaDocFirmato() {
        return this.flUnitaDocFirmato;
    }

    public void setFlUnitaDocFirmato(String flUnitaDocFirmato) {
        this.flUnitaDocFirmato = flUnitaDocFirmato;
    }

    @Id
    @Column(name = "ID_DOC")
    public BigDecimal getIdDoc() {
        return this.idDoc;
    }

    public void setIdDoc(BigDecimal idDoc) {
        this.idDoc = idDoc;
    }

    @Column(name = "ID_STRUT_TIPO_UNITA_DOC")
    public BigDecimal getIdStrutTipoUnitaDoc() {
        return this.idStrutTipoUnitaDoc;
    }

    public void setIdStrutTipoUnitaDoc(BigDecimal idStrutTipoUnitaDoc) {
        this.idStrutTipoUnitaDoc = idStrutTipoUnitaDoc;
    }

    @Column(name = "ID_STRUT_UNITA_DOC")
    public BigDecimal getIdStrutUnitaDoc() {
        return this.idStrutUnitaDoc;
    }

    public void setIdStrutUnitaDoc(BigDecimal idStrutUnitaDoc) {
        this.idStrutUnitaDoc = idStrutUnitaDoc;
    }

    @Column(name = "ID_SUB_STRUT")
    public BigDecimal getIdSubStrut() {
        return this.idSubStrut;
    }

    public void setIdSubStrut(BigDecimal idSubStrut) {
        this.idSubStrut = idSubStrut;
    }

    @Column(name = "ID_TIPO_DOC")
    public BigDecimal getIdTipoDoc() {
        return this.idTipoDoc;
    }

    public void setIdTipoDoc(BigDecimal idTipoDoc) {
        this.idTipoDoc = idTipoDoc;
    }

    @Column(name = "ID_TIPO_UNITA_DOC")
    public BigDecimal getIdTipoUnitaDoc() {
        return this.idTipoUnitaDoc;
    }

    public void setIdTipoUnitaDoc(BigDecimal idTipoUnitaDoc) {
        this.idTipoUnitaDoc = idTipoUnitaDoc;
    }

    @Column(name = "ID_UNITA_DOC")
    public BigDecimal getIdUnitaDoc() {
        return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
        this.idUnitaDoc = idUnitaDoc;
    }

    @Column(name = "NI_ALLEG")
    public BigDecimal getNiAlleg() {
        return this.niAlleg;
    }

    public void setNiAlleg(BigDecimal niAlleg) {
        this.niAlleg = niAlleg;
    }

    @Column(name = "NI_ANNESSI")
    public BigDecimal getNiAnnessi() {
        return this.niAnnessi;
    }

    public void setNiAnnessi(BigDecimal niAnnessi) {
        this.niAnnessi = niAnnessi;
    }

    @Column(name = "NI_ANNOT")
    public BigDecimal getNiAnnot() {
        return this.niAnnot;
    }

    public void setNiAnnot(BigDecimal niAnnot) {
        this.niAnnot = niAnnot;
    }

    @Column(name = "NM_TIPO_DOC_PRINC")
    public String getNmTipoDocPrinc() {
        return this.nmTipoDocPrinc;
    }

    public void setNmTipoDocPrinc(String nmTipoDocPrinc) {
        this.nmTipoDocPrinc = nmTipoDocPrinc;
    }

    @Column(name = "NM_TIPO_UNITA_DOC")
    public String getNmTipoUnitaDoc() {
        return this.nmTipoUnitaDoc;
    }

    public void setNmTipoUnitaDoc(String nmTipoUnitaDoc) {
        this.nmTipoUnitaDoc = nmTipoUnitaDoc;
    }

    @Column(name = "TI_CONSERVAZIONE")
    public String getTiConservazione() {
        return this.tiConservazione;
    }

    public void setTiConservazione(String tiConservazione) {
        this.tiConservazione = tiConservazione;
    }

    @Column(name = "TI_DOC")
    public String getTiDoc() {
        return tiDoc;
    }

    public void setTiDoc(String tiDoc) {
        this.tiDoc = tiDoc;
    }

    @Column(name = "TI_ESITO_VERIF_FIRME")
    public String getTiEsitoVerifFirme() {
        return this.tiEsitoVerifFirme;
    }

    public void setTiEsitoVerifFirme(String tiEsitoVerifFirme) {
        this.tiEsitoVerifFirme = tiEsitoVerifFirme;
    }

    @Column(name = "TI_STATO_CONSERVAZIONE")
    public String getTiStatoConservazione() {
        return this.tiStatoConservazione;
    }

    public void setTiStatoConservazione(String tiStatoConservazione) {
        this.tiStatoConservazione = tiStatoConservazione;
    }

    @Column(name = "TI_STATO_DOC")
    public String getTiStatoDoc() {
        return this.tiStatoDoc;
    }

    public void setTiStatoDoc(String tiStatoDoc) {
        this.tiStatoDoc = tiStatoDoc;
    }

    @Column(name = "TI_STATO_DOC_ELENCO_VERS")
    public String getTiStatoDocElencoVers() {
        return this.tiStatoDocElencoVers;
    }

    public void setTiStatoDocElencoVers(String tiStatoDocElencoVers) {
        this.tiStatoDocElencoVers = tiStatoDocElencoVers;
    }

    @Column(name = "TI_STATO_UD_ELENCO_VERS")
    public String getTiStatoUdElencoVers() {
        return this.tiStatoUdElencoVers;
    }

    public void setTiStatoUdElencoVers(String tiStatoUdElencoVers) {
        this.tiStatoUdElencoVers = tiStatoUdElencoVers;
    }

}
