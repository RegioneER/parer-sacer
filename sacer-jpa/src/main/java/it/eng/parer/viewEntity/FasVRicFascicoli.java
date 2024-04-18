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

import javax.persistence.*;

/**
 * The persistent class for the FAS_V_RIC_FASCICOLI database table.
 *
 */
@Entity
@Table(name = "FAS_V_RIC_FASCICOLI")

@NamedQuery(name = "FasVRicFascicoli.findById", query = "SELECT f FROM FasVRicFascicoli f WHERE f.idFascicolo = :idFascicolo")
public class FasVRicFascicoli implements Serializable {

    private static final long serialVersionUID = 1L;
    private BigDecimal aaFascicolo;
    private BigDecimal aaFascicoloPadre;
    private String cdCompositoVoceTitol;
    private String cdKeyFascicolo;
    private String cdKeyFascicoloPadre;
    private String cdLivelloRiserv;
    private String cdProcAmmin;
    private String cdVersioneXmlSip;
    private String dsOggettoFascicolo;
    private String dsOggettoFascicoloPadre;
    private String dsProcAmmin;
    private Date dtApeFascicolo;
    private Date dtChiuFascicolo;
    private String flForzaContrClassif;
    private String flForzaContrColleg;
    private String flForzaContrNumero;
    private BigDecimal idFascicolo;
    private BigDecimal idSistemaVersante;
    private BigDecimal idStrut;
    private BigDecimal idTipoFascicolo;
    private BigDecimal idUserIamCorrente;
    private BigDecimal idUserIamVers;
    private BigDecimal idVoceTitol;
    private BigDecimal niAaConservazione;
    private BigDecimal niUnitaDoc;
    private String nmSistemaVersante;
    private String nmTipoFascicolo;
    private String nmUserid;
    private String tiConservazione;
    private String tiEsito;
    private String tiStatoConservazione;
    private String tiStatoFascElencoVers;
    private Date tsVersFascicolo;
    private String cdVersioneXsd;
    private String tiModelloXsd;

    public FasVRicFascicoli() {/* Hibernate */
    }

    public FasVRicFascicoli(BigDecimal aaFascicolo, BigDecimal aaFascicoloPadre, String cdCompositoVoceTitol,
            String cdKeyFascicolo, String cdKeyFascicoloPadre, String cdLivelloRiserv, String cdProcAmmin,
            String dsOggettoFascicolo, String dsOggettoFascicoloPadre, String dsProcAmmin, Date dtApeFascicolo,
            Date dtChiuFascicolo, String flForzaContrClassif, String flForzaContrColleg, String flForzaContrNumero,
            BigDecimal idFascicolo, BigDecimal idSistemaVersante, BigDecimal idStrut, BigDecimal idTipoFascicolo,
            BigDecimal idUserIamCorrente, BigDecimal idUserIamVers, BigDecimal idVoceTitol,
            BigDecimal niAaConservazione, BigDecimal niUnitaDoc, String nmSistemaVersante, String nmTipoFascicolo,
            String nmUserid, String tiConservazione, String tiEsito, String tiStatoConservazione,
            String tiStatoFascElencoVers, Date tsVersFascicolo) {
        this.aaFascicolo = aaFascicolo;
        this.aaFascicoloPadre = aaFascicoloPadre;
        this.cdCompositoVoceTitol = cdCompositoVoceTitol;
        this.cdKeyFascicolo = cdKeyFascicolo;
        this.cdKeyFascicoloPadre = cdKeyFascicoloPadre;
        this.cdLivelloRiserv = cdLivelloRiserv;
        this.cdProcAmmin = cdProcAmmin;
        this.dsOggettoFascicolo = dsOggettoFascicolo;
        this.dsOggettoFascicoloPadre = dsOggettoFascicoloPadre;
        this.dsProcAmmin = dsProcAmmin;
        this.dtApeFascicolo = dtApeFascicolo;
        this.dtChiuFascicolo = dtChiuFascicolo;
        this.flForzaContrClassif = flForzaContrClassif;
        this.flForzaContrColleg = flForzaContrColleg;
        this.flForzaContrNumero = flForzaContrNumero;
        this.idFascicolo = idFascicolo;
        this.idSistemaVersante = idSistemaVersante;
        this.idStrut = idStrut;
        this.idTipoFascicolo = idTipoFascicolo;
        this.idUserIamCorrente = idUserIamCorrente;
        this.idUserIamVers = idUserIamVers;
        this.idVoceTitol = idVoceTitol;
        this.niAaConservazione = niAaConservazione;
        this.niUnitaDoc = niUnitaDoc;
        this.nmSistemaVersante = nmSistemaVersante;
        this.nmTipoFascicolo = nmTipoFascicolo;
        this.nmUserid = nmUserid;
        this.tiConservazione = tiConservazione;
        this.tiEsito = tiEsito;
        this.tiStatoConservazione = tiStatoConservazione;
        this.tiStatoFascElencoVers = tiStatoFascElencoVers;
        this.tsVersFascicolo = tsVersFascicolo;
    }

    public FasVRicFascicoli(BigDecimal idFascicolo, BigDecimal aaFascicolo, String cdKeyFascicolo,
            String cdCompositoVoceTitol, String nmTipoFascicolo, Date dtApeFascicolo, Date dtChiuFascicolo,
            Date tsVersFascicolo, BigDecimal niUnitaDoc, BigDecimal niAaConservazione, String cdLivelloRiserv,
            String flForzaContrClassif, String flForzaContrNumero, String flForzaContrColleg,
            String tiStatoFascElencoVers, String tiStatoConservazione) {
        this.idFascicolo = idFascicolo;
        this.aaFascicolo = aaFascicolo;
        this.cdKeyFascicolo = cdKeyFascicolo;
        this.cdCompositoVoceTitol = cdCompositoVoceTitol;
        this.nmTipoFascicolo = nmTipoFascicolo;
        this.dtApeFascicolo = dtApeFascicolo;
        this.dtChiuFascicolo = dtChiuFascicolo;
        this.tsVersFascicolo = tsVersFascicolo;
        this.niUnitaDoc = niUnitaDoc;
        this.niAaConservazione = niAaConservazione;
        this.cdLivelloRiserv = cdLivelloRiserv;
        this.flForzaContrClassif = flForzaContrClassif;
        this.flForzaContrColleg = flForzaContrColleg;
        this.flForzaContrNumero = flForzaContrNumero;
        this.tiStatoFascElencoVers = tiStatoFascElencoVers;
        this.tiStatoConservazione = tiStatoConservazione;
    }

    @Column(name = "AA_FASCICOLO")
    public BigDecimal getAaFascicolo() {
        return this.aaFascicolo;
    }

    public void setAaFascicolo(BigDecimal aaFascicolo) {
        this.aaFascicolo = aaFascicolo;
    }

    @Column(name = "AA_FASCICOLO_PADRE")
    public BigDecimal getAaFascicoloPadre() {
        return this.aaFascicoloPadre;
    }

    public void setAaFascicoloPadre(BigDecimal aaFascicoloPadre) {
        this.aaFascicoloPadre = aaFascicoloPadre;
    }

    @Column(name = "CD_COMPOSITO_VOCE_TITOL")
    public String getCdCompositoVoceTitol() {
        return this.cdCompositoVoceTitol;
    }

    public void setCdCompositoVoceTitol(String cdCompositoVoceTitol) {
        this.cdCompositoVoceTitol = cdCompositoVoceTitol;
    }

    @Column(name = "CD_KEY_FASCICOLO")
    public String getCdKeyFascicolo() {
        return this.cdKeyFascicolo;
    }

    public void setCdKeyFascicolo(String cdKeyFascicolo) {
        this.cdKeyFascicolo = cdKeyFascicolo;
    }

    @Column(name = "CD_KEY_FASCICOLO_PADRE")
    public String getCdKeyFascicoloPadre() {
        return this.cdKeyFascicoloPadre;
    }

    public void setCdKeyFascicoloPadre(String cdKeyFascicoloPadre) {
        this.cdKeyFascicoloPadre = cdKeyFascicoloPadre;
    }

    @Column(name = "CD_LIVELLO_RISERV")
    public String getCdLivelloRiserv() {
        return this.cdLivelloRiserv;
    }

    public void setCdLivelloRiserv(String cdLivelloRiserv) {
        this.cdLivelloRiserv = cdLivelloRiserv;
    }

    @Column(name = "CD_PROC_AMMIN")
    public String getCdProcAmmin() {
        return this.cdProcAmmin;
    }

    public void setCdProcAmmin(String cdProcAmmin) {
        this.cdProcAmmin = cdProcAmmin;
    }

    @Column(name = "CD_VERSIONE_XML_SIP")
    public String getCdVersioneXmlSip() {
        return this.cdVersioneXmlSip;
    }

    public void setCdVersioneXmlSip(String cdVersioneXmlSip) {
        this.cdVersioneXmlSip = cdVersioneXmlSip;
    }

    @Column(name = "DS_OGGETTO_FASCICOLO")
    public String getDsOggettoFascicolo() {
        return this.dsOggettoFascicolo;
    }

    public void setDsOggettoFascicolo(String dsOggettoFascicolo) {
        this.dsOggettoFascicolo = dsOggettoFascicolo;
    }

    @Column(name = "DS_OGGETTO_FASCICOLO_PADRE")
    public String getDsOggettoFascicoloPadre() {
        return this.dsOggettoFascicoloPadre;
    }

    public void setDsOggettoFascicoloPadre(String dsOggettoFascicoloPadre) {
        this.dsOggettoFascicoloPadre = dsOggettoFascicoloPadre;
    }

    @Column(name = "DS_PROC_AMMIN")
    public String getDsProcAmmin() {
        return this.dsProcAmmin;
    }

    public void setDsProcAmmin(String dsProcAmmin) {
        this.dsProcAmmin = dsProcAmmin;
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

    @Column(name = "FL_FORZA_CONTR_CLASSIF", columnDefinition = "char(1)")
    public String getFlForzaContrClassif() {
        return this.flForzaContrClassif;
    }

    public void setFlForzaContrClassif(String flForzaContrClassif) {
        this.flForzaContrClassif = flForzaContrClassif;
    }

    @Column(name = "FL_FORZA_CONTR_COLLEG", columnDefinition = "char(1)")
    public String getFlForzaContrColleg() {
        return this.flForzaContrColleg;
    }

    public void setFlForzaContrColleg(String flForzaContrColleg) {
        this.flForzaContrColleg = flForzaContrColleg;
    }

    @Column(name = "FL_FORZA_CONTR_NUMERO", columnDefinition = "char(1)")
    public String getFlForzaContrNumero() {
        return this.flForzaContrNumero;
    }

    public void setFlForzaContrNumero(String flForzaContrNumero) {
        this.flForzaContrNumero = flForzaContrNumero;
    }

    @Id
    @Column(name = "ID_FASCICOLO")
    public BigDecimal getIdFascicolo() {
        return this.idFascicolo;
    }

    public void setIdFascicolo(BigDecimal idFascicolo) {
        this.idFascicolo = idFascicolo;
    }

    @Column(name = "ID_SISTEMA_VERSANTE")
    public BigDecimal getIdSistemaVersante() {
        return this.idSistemaVersante;
    }

    public void setIdSistemaVersante(BigDecimal idSistemaVersante) {
        this.idSistemaVersante = idSistemaVersante;
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

    @Column(name = "ID_USER_IAM_CORRENTE")
    public BigDecimal getIdUserIamCorrente() {
        return this.idUserIamCorrente;
    }

    public void setIdUserIamCorrente(BigDecimal idUserIamCorrente) {
        this.idUserIamCorrente = idUserIamCorrente;
    }

    @Column(name = "ID_USER_IAM_VERS")
    public BigDecimal getIdUserIamVers() {
        return this.idUserIamVers;
    }

    public void setIdUserIamVers(BigDecimal idUserIamVers) {
        this.idUserIamVers = idUserIamVers;
    }

    @Column(name = "ID_VOCE_TITOL")
    public BigDecimal getIdVoceTitol() {
        return this.idVoceTitol;
    }

    public void setIdVoceTitol(BigDecimal idVoceTitol) {
        this.idVoceTitol = idVoceTitol;
    }

    @Column(name = "NI_AA_CONSERVAZIONE")
    public BigDecimal getNiAaConservazione() {
        return this.niAaConservazione;
    }

    public void setNiAaConservazione(BigDecimal niAaConservazione) {
        this.niAaConservazione = niAaConservazione;
    }

    @Column(name = "NI_UNITA_DOC")
    public BigDecimal getNiUnitaDoc() {
        return this.niUnitaDoc;
    }

    public void setNiUnitaDoc(BigDecimal niUnitaDoc) {
        this.niUnitaDoc = niUnitaDoc;
    }

    @Column(name = "NM_SISTEMA_VERSANTE")
    public String getNmSistemaVersante() {
        return this.nmSistemaVersante;
    }

    public void setNmSistemaVersante(String nmSistemaVersante) {
        this.nmSistemaVersante = nmSistemaVersante;
    }

    @Column(name = "NM_TIPO_FASCICOLO")
    public String getNmTipoFascicolo() {
        return this.nmTipoFascicolo;
    }

    public void setNmTipoFascicolo(String nmTipoFascicolo) {
        this.nmTipoFascicolo = nmTipoFascicolo;
    }

    @Column(name = "NM_USERID")
    public String getNmUserid() {
        return this.nmUserid;
    }

    public void setNmUserid(String nmUserid) {
        this.nmUserid = nmUserid;
    }

    @Column(name = "TI_CONSERVAZIONE")
    public String getTiConservazione() {
        return this.tiConservazione;
    }

    public void setTiConservazione(String tiConservazione) {
        this.tiConservazione = tiConservazione;
    }

    @Column(name = "TI_ESITO")
    public String getTiEsito() {
        return this.tiEsito;
    }

    public void setTiEsito(String tiEsito) {
        this.tiEsito = tiEsito;
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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TS_VERS_FASCICOLO")
    public Date getTsVersFascicolo() {
        return this.tsVersFascicolo;
    }

    public void setTsVersFascicolo(Date tsVersFascicolo) {
        this.tsVersFascicolo = tsVersFascicolo;
    }

    @Column(name = "CD_VERSIONE_XSD")
    public String getCdVersioneXsd() {
        return cdVersioneXsd;
    }

    public void setCdVersioneXsd(String cdVersioneXsd) {
        this.cdVersioneXsd = cdVersioneXsd;
    }

    @Column(name = "TI_MODELLO_XSD")
    public String getTiModelloXsd() {
        return tiModelloXsd;
    }

    public void setTiModelloXsd(String tiModelloXsd) {
        this.tiModelloXsd = tiModelloXsd;
    }

}
