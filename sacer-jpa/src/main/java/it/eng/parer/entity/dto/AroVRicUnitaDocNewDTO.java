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

package it.eng.parer.entity.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Data Transfer Object (DTO) per rappresentare il risultato aggregato della ricerca delle Unità
 * Documentarie.
 */
public class AroVRicUnitaDocNewDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal idUnitaDoc;
    private BigDecimal aaKeyUnitaDoc;
    private String cdKeyUnitaDoc;
    private String cdRegistroKeyUnitaDoc;
    private Date dtCreazione;
    private Date dtRegUnitaDoc;
    private String flUnitaDocFirmato;
    private String tiEsitoVerifFirme;
    private String dsMsgEsitoVerifFirme;
    private String nmTipoUnitaDoc;
    private String flForzaAccettazione;
    private String flForzaConservazione;
    private String dsKeyOrd;
    private BigDecimal niAlleg;
    private BigDecimal niAnnessi;
    private BigDecimal niAnnot;
    private String nmTipoDocPrinc;
    private String dsListaStatiElencoVers;
    private String tiStatoConservazione;

    /**
     * Costruttore di default.
     */
    public AroVRicUnitaDocNewDTO() {
        // Costruttore vuoto
    }

    public AroVRicUnitaDocNewDTO(BigDecimal idUnitaDoc, BigDecimal aaKeyUnitaDoc,
            String cdKeyUnitaDoc, String cdRegistroKeyUnitaDoc, Date dtCreazione,
            Date dtRegUnitaDoc, String flUnitaDocFirmato, String tiEsitoVerifFirme,
            String dsMsgEsitoVerifFirme, String nmTipoUnitaDoc, String flForzaAccettazione,
            String flForzaConservazione, String dsKeyOrd, BigDecimal niAlleg, BigDecimal niAnnessi,
            BigDecimal niAnnot, String nmTipoDocPrinc, String dsListaStatiElencoVers,
            String tiStatoConservazione) {
        this.idUnitaDoc = idUnitaDoc;
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
        this.cdKeyUnitaDoc = cdKeyUnitaDoc;
        this.cdRegistroKeyUnitaDoc = cdRegistroKeyUnitaDoc;
        this.dtCreazione = dtCreazione;
        this.dtRegUnitaDoc = dtRegUnitaDoc;
        this.flUnitaDocFirmato = flUnitaDocFirmato;
        this.tiEsitoVerifFirme = tiEsitoVerifFirme;
        this.dsMsgEsitoVerifFirme = dsMsgEsitoVerifFirme;
        this.nmTipoUnitaDoc = nmTipoUnitaDoc;
        this.flForzaAccettazione = flForzaAccettazione;
        this.flForzaConservazione = flForzaConservazione;
        this.dsKeyOrd = dsKeyOrd;
        this.niAlleg = niAlleg;
        this.niAnnessi = niAnnessi;
        this.niAnnot = niAnnot;
        this.nmTipoDocPrinc = nmTipoDocPrinc;
        this.dsListaStatiElencoVers = dsListaStatiElencoVers;
        this.tiStatoConservazione = tiStatoConservazione;
    }

    public BigDecimal getIdUnitaDoc() {
        return idUnitaDoc;
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
        this.idUnitaDoc = idUnitaDoc;
    }

    public BigDecimal getAaKeyUnitaDoc() {
        return aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    public String getCdKeyUnitaDoc() {
        return cdKeyUnitaDoc;
    }

    public void setCdKeyUnitaDoc(String cdKeyUnitaDoc) {
        this.cdKeyUnitaDoc = cdKeyUnitaDoc;
    }

    public String getCdRegistroKeyUnitaDoc() {
        return cdRegistroKeyUnitaDoc;
    }

    public void setCdRegistroKeyUnitaDoc(String cdRegistroKeyUnitaDoc) {
        this.cdRegistroKeyUnitaDoc = cdRegistroKeyUnitaDoc;
    }

    public Date getDtCreazione() {
        return dtCreazione;
    }

    public void setDtCreazione(Date dtCreazione) {
        this.dtCreazione = dtCreazione;
    }

    public Date getDtRegUnitaDoc() {
        return dtRegUnitaDoc;
    }

    public void setDtRegUnitaDoc(Date dtRegUnitaDoc) {
        this.dtRegUnitaDoc = dtRegUnitaDoc;
    }

    public String getFlUnitaDocFirmato() {
        return flUnitaDocFirmato;
    }

    public void setFlUnitaDocFirmato(String flUnitaDocFirmato) {
        this.flUnitaDocFirmato = flUnitaDocFirmato;
    }

    public String getTiEsitoVerifFirme() {
        return tiEsitoVerifFirme;
    }

    public void setTiEsitoVerifFirme(String tiEsitoVerifFirme) {
        this.tiEsitoVerifFirme = tiEsitoVerifFirme;
    }

    public String getDsMsgEsitoVerifFirme() {
        return dsMsgEsitoVerifFirme;
    }

    public void setDsMsgEsitoVerifFirme(String dsMsgEsitoVerifFirme) {
        this.dsMsgEsitoVerifFirme = dsMsgEsitoVerifFirme;
    }

    public String getNmTipoUnitaDoc() {
        return nmTipoUnitaDoc;
    }

    public void setNmTipoUnitaDoc(String nmTipoUnitaDoc) {
        this.nmTipoUnitaDoc = nmTipoUnitaDoc;
    }

    public String getFlForzaAccettazione() {
        return flForzaAccettazione;
    }

    public void setFlForzaAccettazione(String flForzaAccettazione) {
        this.flForzaAccettazione = flForzaAccettazione;
    }

    public String getFlForzaConservazione() {
        return flForzaConservazione;
    }

    public void setFlForzaConservazione(String flForzaConservazione) {
        this.flForzaConservazione = flForzaConservazione;
    }

    public String getDsKeyOrd() {
        return dsKeyOrd;
    }

    public void setDsKeyOrd(String dsKeyOrd) {
        this.dsKeyOrd = dsKeyOrd;
    }

    public BigDecimal getNiAlleg() {
        return niAlleg;
    }

    public void setNiAlleg(BigDecimal niAlleg) {
        this.niAlleg = niAlleg;
    }

    public BigDecimal getNiAnnessi() {
        return niAnnessi;
    }

    public void setNiAnnessi(BigDecimal niAnnessi) {
        this.niAnnessi = niAnnessi;
    }

    public BigDecimal getNiAnnot() {
        return niAnnot;
    }

    public void setNiAnnot(BigDecimal niAnnot) {
        this.niAnnot = niAnnot;
    }

    public String getNmTipoDocPrinc() {
        return nmTipoDocPrinc;
    }

    public void setNmTipoDocPrinc(String nmTipoDocPrinc) {
        this.nmTipoDocPrinc = nmTipoDocPrinc;
    }

    public String getDsListaStatiElencoVers() {
        return dsListaStatiElencoVers;
    }

    public void setDsListaStatiElencoVers(String dsListaStatiElencoVers) {
        this.dsListaStatiElencoVers = dsListaStatiElencoVers;
    }

    public String getTiStatoConservazione() {
        return tiStatoConservazione;
    }

    public void setTiStatoConservazione(String tiStatoConservazione) {
        this.tiStatoConservazione = tiStatoConservazione;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AroVRicUnitaDocNewDTO that = (AroVRicUnitaDocNewDTO) o;
        // Due DTO sono uguali se il loro idUnitaDoc è uguale.
        return java.util.Objects.equals(idUnitaDoc, that.idUnitaDoc);
    }

    @Override
    public int hashCode() {
        // Coerentemente, l'hashCode si basa solo su idUnitaDoc.
        return java.util.Objects.hash(idUnitaDoc);
    }
}
