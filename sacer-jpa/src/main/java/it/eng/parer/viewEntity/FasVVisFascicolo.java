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
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * The persistent class for the FAS_V_VIS_FASCICOLO database table.
 * 
 */
@Entity
@Table(name = "FAS_V_VIS_FASCICOLO")

@NamedQuery(name = "FasVVisFascicolo.find", query = "SELECT f FROM FasVVisFascicolo f WHERE f.idFascicolo = :idFascicolo")
public class FasVVisFascicolo implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigDecimal aaFascicolo;
    private BigDecimal aaFascicoloPadre;
    private BigDecimal aaKeyUnitaDocFirst;
    private BigDecimal aaKeyUnitaDocLast;
    private String blXmlNormativo;
    private String blXmlSegnatura;
    private String blXmlSpecifico;
    private String blXmlVersProfilo;
    private String blXmlVersRapp;
    private String blXmlVersSip;
    private String blXsdNormativo;
    private String blXsdSegnatura;
    private String blXsdSpecifico;
    private String blXsdVersProfilo;
    private String cdCompositoVoceTitol;
    private String cdEncodingHashXmlRapp;
    private String cdEncodingHashXmlSip;
    private String cdEnteConvenz;
    private String cdIndiceClassif;
    private String cdIndIpClient;
    private String cdIndServer;
    private String cdIpaAmminTitol;
    private String cdKeyFascicolo;
    private String cdKeyFascicoloPadre;
    private String cdKeyUnitaDocFirst;
    private String cdKeyUnitaDocLast;
    private String cdLivelloRiserv;
    private String cdProcAmmin;
    private String cdRegKeyUnitaDocFirst;
    private String cdRegKeyUnitaDocLast;
    private String cdVersioneXmlRapp;
    private String cdVersioneXmlSip;
    private String cdXsdNormativo;
    private String cdXsdProfilo;
    private String cdXsdSegnatura;
    private String cdXsdSpecifico;
    private String dsAlgoHashXmlRapp;
    private String dsAlgoHashXmlSip;
    private String dsAmminTitol;
    private String dsHashXmlRapp;
    private String dsHashXmlSip;
    private String dsIndiceClassif;
    private String dsNota;
    private String dsOggettoFascicolo;
    private String dsOggettoFascicoloPadre;
    private String dsProcAmmin;
    private String dsUrnXmlRapp;
    private String dsUrnXmlSip;
    private String dsXsdNormativo;
    private String dsXsdProfilo;
    private String dsXsdSegnatura;
    private String dsXsdSpecifico;
    private Date dtApeFascicolo;
    private Date dtChiuFascicolo;
    private String flDefaultNormativo;
    private String flDefaultProfilo;
    private String flDefaultSegnatura;
    private String flDefaultSpecifico;
    private String flForzaContrClassif;
    private String flForzaContrColleg;
    private String flForzaContrNumero;
    private String flUpdAnnulUnitaDoc;
    private String flUpdModifUnitaDoc;
    private BigDecimal idFascicolo;
    private BigDecimal idModelloXsdProfilo;
    private BigDecimal idModelloXsdSegnatura;
    private BigDecimal idModelloXsdNormativo;
    private BigDecimal idModelloXsdSpecifico;
    private BigDecimal idSistemaVersante;
    private BigDecimal idTipoFascicolo;
    private BigDecimal idUserIamVers;
    private BigDecimal idVoceTitol;
    private BigDecimal niAaConservazione;
    private BigDecimal niUnitaDoc;
    private String nmAmbiente;
    private String nmAmbienteEnteConvenz;
    private String nmEnte;
    private String nmEnteConvenz;
    private String nmSistemaVersante;
    private String nmStrut;
    private String nmTipoFascicolo;
    private String nmUserid;
    private String tiCodiceAmminTitol;
    private String tiConservazione;
    private String tiModelloXsdProfilo;
    private String tiModelloXsdSegnatura;
    private String tiModelloXsdNormativo;
    private String tiModelloXsdSpecifico;
    private String tiStatoConservazione;
    private String tiStatoFascElencoVers;
    private Date tsIniSes;

    public FasVVisFascicolo() {/* Hibernate */
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

    @Column(name = "AA_KEY_UNITA_DOC_FIRST")
    public BigDecimal getAaKeyUnitaDocFirst() {
        return this.aaKeyUnitaDocFirst;
    }

    public void setAaKeyUnitaDocFirst(BigDecimal aaKeyUnitaDocFirst) {
        this.aaKeyUnitaDocFirst = aaKeyUnitaDocFirst;
    }

    @Column(name = "AA_KEY_UNITA_DOC_LAST")
    public BigDecimal getAaKeyUnitaDocLast() {
        return this.aaKeyUnitaDocLast;
    }

    public void setAaKeyUnitaDocLast(BigDecimal aaKeyUnitaDocLast) {
        this.aaKeyUnitaDocLast = aaKeyUnitaDocLast;
    }

    @Lob
    @Column(name = "BL_XML_NORMATIVO")
    public String getBlXmlNormativo() {
        return this.blXmlNormativo;
    }

    public void setBlXmlNormativo(String blXmlNormativo) {
        this.blXmlNormativo = blXmlNormativo;
    }

    @Lob
    @Column(name = "BL_XML_SEGNATURA")
    public String getBlXmlSegnatura() {
        return this.blXmlSegnatura;
    }

    public void setBlXmlSegnatura(String blXmlSegnatura) {
        this.blXmlSegnatura = blXmlSegnatura;
    }

    @Lob
    @Column(name = "BL_XML_SPECIFICO")
    public String getBlXmlSpecifico() {
        return this.blXmlSpecifico;
    }

    public void setBlXmlSpecifico(String blXmlSpecifico) {
        this.blXmlSpecifico = blXmlSpecifico;
    }

    @Lob
    @Column(name = "BL_XML_VERS_PROFILO")
    public String getBlXmlVersProfilo() {
        return this.blXmlVersProfilo;
    }

    public void setBlXmlVersProfilo(String blXmlVersProfilo) {
        this.blXmlVersProfilo = blXmlVersProfilo;
    }

    @Lob
    @Column(name = "BL_XML_VERS_RAPP")
    public String getBlXmlVersRapp() {
        return this.blXmlVersRapp;
    }

    public void setBlXmlVersRapp(String blXmlVersRapp) {
        this.blXmlVersRapp = blXmlVersRapp;
    }

    @Lob
    @Column(name = "BL_XML_VERS_SIP")
    public String getBlXmlVersSip() {
        return this.blXmlVersSip;
    }

    public void setBlXmlVersSip(String blXmlVersSip) {
        this.blXmlVersSip = blXmlVersSip;
    }

    @Lob
    @Column(name = "BL_XSD_NORMATIVO")
    public String getBlXsdNormativo() {
        return this.blXsdNormativo;
    }

    public void setBlXsdNormativo(String blXsdNormativo) {
        this.blXsdNormativo = blXsdNormativo;
    }

    @Lob
    @Column(name = "BL_XSD_SEGNATURA")
    public String getBlXsdSegnatura() {
        return this.blXsdSegnatura;
    }

    public void setBlXsdSegnatura(String blXsdSegnatura) {
        this.blXsdSegnatura = blXsdSegnatura;
    }

    @Lob
    @Column(name = "BL_XSD_SPECIFICO")
    public String getBlXsdSpecifico() {
        return this.blXsdSpecifico;
    }

    public void setBlXsdSpecifico(String blXsdSpecifico) {
        this.blXsdSpecifico = blXsdSpecifico;
    }

    @Lob
    @Column(name = "BL_XSD_VERS_PROFILO")
    public String getBlXsdVersProfilo() {
        return this.blXsdVersProfilo;
    }

    public void setBlXsdVersProfilo(String blXsdVersProfilo) {
        this.blXsdVersProfilo = blXsdVersProfilo;
    }

    @Column(name = "CD_COMPOSITO_VOCE_TITOL")
    public String getCdCompositoVoceTitol() {
        return this.cdCompositoVoceTitol;
    }

    public void setCdCompositoVoceTitol(String cdCompositoVoceTitol) {
        this.cdCompositoVoceTitol = cdCompositoVoceTitol;
    }

    @Column(name = "CD_ENCODING_HASH_XML_RAPP")
    public String getCdEncodingHashXmlRapp() {
        return this.cdEncodingHashXmlRapp;
    }

    public void setCdEncodingHashXmlRapp(String cdEncodingHashXmlRapp) {
        this.cdEncodingHashXmlRapp = cdEncodingHashXmlRapp;
    }

    @Column(name = "CD_ENCODING_HASH_XML_SIP")
    public String getCdEncodingHashXmlSip() {
        return this.cdEncodingHashXmlSip;
    }

    public void setCdEncodingHashXmlSip(String cdEncodingHashXmlSip) {
        this.cdEncodingHashXmlSip = cdEncodingHashXmlSip;
    }

    @Column(name = "CD_ENTE_CONVENZ")
    public String getCdEnteConvenz() {
        return this.cdEnteConvenz;
    }

    public void setCdEnteConvenz(String cdEnteConvenz) {
        this.cdEnteConvenz = cdEnteConvenz;
    }

    @Column(name = "CD_INDICE_CLASSIF")
    public String getCdIndiceClassif() {
        return this.cdIndiceClassif;
    }

    public void setCdIndiceClassif(String cdIndiceClassif) {
        this.cdIndiceClassif = cdIndiceClassif;
    }

    @Column(name = "CD_IND_IP_CLIENT")
    public String getCdIndIpClient() {
        return this.cdIndIpClient;
    }

    public void setCdIndIpClient(String cdIndIpClient) {
        this.cdIndIpClient = cdIndIpClient;
    }

    @Column(name = "CD_IND_SERVER")
    public String getCdIndServer() {
        return this.cdIndServer;
    }

    public void setCdIndServer(String cdIndServer) {
        this.cdIndServer = cdIndServer;
    }

    @Column(name = "CD_IPA_AMMIN_TITOL")
    public String getCdIpaAmminTitol() {
        return this.cdIpaAmminTitol;
    }

    public void setCdIpaAmminTitol(String cdIpaAmminTitol) {
        this.cdIpaAmminTitol = cdIpaAmminTitol;
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

    @Column(name = "CD_KEY_UNITA_DOC_FIRST")
    public String getCdKeyUnitaDocFirst() {
        return this.cdKeyUnitaDocFirst;
    }

    public void setCdKeyUnitaDocFirst(String cdKeyUnitaDocFirst) {
        this.cdKeyUnitaDocFirst = cdKeyUnitaDocFirst;
    }

    @Column(name = "CD_KEY_UNITA_DOC_LAST")
    public String getCdKeyUnitaDocLast() {
        return this.cdKeyUnitaDocLast;
    }

    public void setCdKeyUnitaDocLast(String cdKeyUnitaDocLast) {
        this.cdKeyUnitaDocLast = cdKeyUnitaDocLast;
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

    @Column(name = "CD_REG_KEY_UNITA_DOC_FIRST")
    public String getCdRegKeyUnitaDocFirst() {
        return this.cdRegKeyUnitaDocFirst;
    }

    public void setCdRegKeyUnitaDocFirst(String cdRegKeyUnitaDocFirst) {
        this.cdRegKeyUnitaDocFirst = cdRegKeyUnitaDocFirst;
    }

    @Column(name = "CD_REG_KEY_UNITA_DOC_LAST")
    public String getCdRegKeyUnitaDocLast() {
        return this.cdRegKeyUnitaDocLast;
    }

    public void setCdRegKeyUnitaDocLast(String cdRegKeyUnitaDocLast) {
        this.cdRegKeyUnitaDocLast = cdRegKeyUnitaDocLast;
    }

    @Column(name = "CD_VERSIONE_XML_RAPP")
    public String getCdVersioneXmlRapp() {
        return this.cdVersioneXmlRapp;
    }

    public void setCdVersioneXmlRapp(String cdVersioneXmlRapp) {
        this.cdVersioneXmlRapp = cdVersioneXmlRapp;
    }

    @Column(name = "CD_VERSIONE_XML_SIP")
    public String getCdVersioneXmlSip() {
        return this.cdVersioneXmlSip;
    }

    public void setCdVersioneXmlSip(String cdVersioneXmlSip) {
        this.cdVersioneXmlSip = cdVersioneXmlSip;
    }

    @Column(name = "CD_XSD_NORMATIVO")
    public String getCdXsdNormativo() {
        return this.cdXsdNormativo;
    }

    public void setCdXsdNormativo(String cdXsdNormativo) {
        this.cdXsdNormativo = cdXsdNormativo;
    }

    @Column(name = "CD_XSD_PROFILO")
    public String getCdXsdProfilo() {
        return this.cdXsdProfilo;
    }

    public void setCdXsdProfilo(String cdXsdProfilo) {
        this.cdXsdProfilo = cdXsdProfilo;
    }

    @Column(name = "CD_XSD_SEGNATURA")
    public String getCdXsdSegnatura() {
        return this.cdXsdSegnatura;
    }

    public void setCdXsdSegnatura(String cdXsdSegnatura) {
        this.cdXsdSegnatura = cdXsdSegnatura;
    }

    @Column(name = "CD_XSD_SPECIFICO")
    public String getCdXsdSpecifico() {
        return this.cdXsdSpecifico;
    }

    public void setCdXsdSpecifico(String cdXsdSpecifico) {
        this.cdXsdSpecifico = cdXsdSpecifico;
    }

    @Column(name = "DS_ALGO_HASH_XML_RAPP")
    public String getDsAlgoHashXmlRapp() {
        return this.dsAlgoHashXmlRapp;
    }

    public void setDsAlgoHashXmlRapp(String dsAlgoHashXmlRapp) {
        this.dsAlgoHashXmlRapp = dsAlgoHashXmlRapp;
    }

    @Column(name = "DS_ALGO_HASH_XML_SIP")
    public String getDsAlgoHashXmlSip() {
        return this.dsAlgoHashXmlSip;
    }

    public void setDsAlgoHashXmlSip(String dsAlgoHashXmlSip) {
        this.dsAlgoHashXmlSip = dsAlgoHashXmlSip;
    }

    @Column(name = "DS_AMMIN_TITOL")
    public String getDsAmminTitol() {
        return this.dsAmminTitol;
    }

    public void setDsAmminTitol(String dsAmminTitol) {
        this.dsAmminTitol = dsAmminTitol;
    }

    @Column(name = "DS_HASH_XML_RAPP")
    public String getDsHashXmlRapp() {
        return this.dsHashXmlRapp;
    }

    public void setDsHashXmlRapp(String dsHashXmlRapp) {
        this.dsHashXmlRapp = dsHashXmlRapp;
    }

    @Column(name = "DS_HASH_XML_SIP")
    public String getDsHashXmlSip() {
        return this.dsHashXmlSip;
    }

    public void setDsHashXmlSip(String dsHashXmlSip) {
        this.dsHashXmlSip = dsHashXmlSip;
    }

    @Column(name = "DS_INDICE_CLASSIF")
    public String getDsIndiceClassif() {
        return this.dsIndiceClassif;
    }

    public void setDsIndiceClassif(String dsIndiceClassif) {
        this.dsIndiceClassif = dsIndiceClassif;
    }

    @Column(name = "DS_NOTA")
    public String getDsNota() {
        return this.dsNota;
    }

    public void setDsNota(String dsNota) {
        this.dsNota = dsNota;
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

    @Column(name = "DS_URN_XML_RAPP")
    public String getDsUrnXmlRapp() {
        return this.dsUrnXmlRapp;
    }

    public void setDsUrnXmlRapp(String dsUrnXmlRapp) {
        this.dsUrnXmlRapp = dsUrnXmlRapp;
    }

    @Column(name = "DS_URN_XML_SIP")
    public String getDsUrnXmlSip() {
        return this.dsUrnXmlSip;
    }

    public void setDsUrnXmlSip(String dsUrnXmlSip) {
        this.dsUrnXmlSip = dsUrnXmlSip;
    }

    @Column(name = "DS_XSD_NORMATIVO")
    public String getDsXsdNormativo() {
        return this.dsXsdNormativo;
    }

    public void setDsXsdNormativo(String dsXsdNormativo) {
        this.dsXsdNormativo = dsXsdNormativo;
    }

    @Column(name = "DS_XSD_PROFILO")
    public String getDsXsdProfilo() {
        return this.dsXsdProfilo;
    }

    public void setDsXsdProfilo(String dsXsdProfilo) {
        this.dsXsdProfilo = dsXsdProfilo;
    }

    @Column(name = "DS_XSD_SEGNATURA")
    public String getDsXsdSegnatura() {
        return this.dsXsdSegnatura;
    }

    public void setDsXsdSegnatura(String dsXsdSegnatura) {
        this.dsXsdSegnatura = dsXsdSegnatura;
    }

    @Column(name = "DS_XSD_SPECIFICO")
    public String getDsXsdSpecifico() {
        return this.dsXsdSpecifico;
    }

    public void setDsXsdSpecifico(String dsXsdSpecifico) {
        this.dsXsdSpecifico = dsXsdSpecifico;
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

    @Column(name = "FL_DEFAULT_NORMATIVO", columnDefinition = "char(1)")
    public String getFlDefaultNormativo() {
        return this.flDefaultNormativo;
    }

    public void setFlDefaultNormativo(String flDefaultNormativo) {
        this.flDefaultNormativo = flDefaultNormativo;
    }

    @Column(name = "FL_DEFAULT_PROFILO", columnDefinition = "char(1)")
    public String getFlDefaultProfilo() {
        return this.flDefaultProfilo;
    }

    public void setFlDefaultProfilo(String flDefaultProfilo) {
        this.flDefaultProfilo = flDefaultProfilo;
    }

    @Column(name = "FL_DEFAULT_SEGNATURA", columnDefinition = "char(1)")
    public String getFlDefaultSegnatura() {
        return this.flDefaultSegnatura;
    }

    public void setFlDefaultSegnatura(String flDefaultSegnatura) {
        this.flDefaultSegnatura = flDefaultSegnatura;
    }

    @Column(name = "FL_DEFAULT_SPECIFICO", columnDefinition = "char(1)")
    public String getFlDefaultSpecifico() {
        return this.flDefaultSpecifico;
    }

    public void setFlDefaultSpecifico(String flDefaultSpecifico) {
        this.flDefaultSpecifico = flDefaultSpecifico;
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

    @Id
    @Column(name = "ID_FASCICOLO")
    public BigDecimal getIdFascicolo() {
        return this.idFascicolo;
    }

    public void setIdFascicolo(BigDecimal idFascicolo) {
        this.idFascicolo = idFascicolo;
    }

    @Column(name = "ID_MODELLO_XSD_PROFILO")
    public BigDecimal getIdModelloXsdProfilo() {
        return this.idModelloXsdProfilo;
    }

    public void setIdModelloXsdProfilo(BigDecimal idModelloXsdProfilo) {
        this.idModelloXsdProfilo = idModelloXsdProfilo;
    }

    @Column(name = "ID_MODELLO_XSD_SEGNATURA")
    public BigDecimal getIdModelloXsdSegnatura() {
        return this.idModelloXsdSegnatura;
    }

    public void setIdModelloXsdSegnatura(BigDecimal idModelloXsdSegnatura) {
        this.idModelloXsdSegnatura = idModelloXsdSegnatura;
    }

    @Column(name = "ID_MODELLO_XSD_NORMATIVO")
    public BigDecimal getIdModelloXsdNormativo() {
        return this.idModelloXsdNormativo;
    }

    public void setIdModelloXsdNormativo(BigDecimal idModelloXsdNormativo) {
        this.idModelloXsdNormativo = idModelloXsdNormativo;
    }

    @Column(name = "ID_MODELLO_XSD_SPECIFICO")
    public BigDecimal getIdModelloXsdSpecifico() {
        return this.idModelloXsdSpecifico;
    }

    public void setIdModelloXsdSpecifico(BigDecimal idModelloXsdSpecifico) {
        this.idModelloXsdSpecifico = idModelloXsdSpecifico;
    }

    @Column(name = "ID_SISTEMA_VERSANTE")
    public BigDecimal getIdSistemaVersante() {
        return this.idSistemaVersante;
    }

    public void setIdSistemaVersante(BigDecimal idSistemaVersante) {
        this.idSistemaVersante = idSistemaVersante;
    }

    @Column(name = "ID_TIPO_FASCICOLO")
    public BigDecimal getIdTipoFascicolo() {
        return this.idTipoFascicolo;
    }

    public void setIdTipoFascicolo(BigDecimal idTipoFascicolo) {
        this.idTipoFascicolo = idTipoFascicolo;
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

    @Column(name = "NM_AMBIENTE")
    public String getNmAmbiente() {
        return this.nmAmbiente;
    }

    public void setNmAmbiente(String nmAmbiente) {
        this.nmAmbiente = nmAmbiente;
    }

    @Column(name = "NM_AMBIENTE_ENTE_CONVENZ")
    public String getNmAmbienteEnteConvenz() {
        return this.nmAmbienteEnteConvenz;
    }

    public void setNmAmbienteEnteConvenz(String nmAmbienteEnteConvenz) {
        this.nmAmbienteEnteConvenz = nmAmbienteEnteConvenz;
    }

    @Column(name = "NM_ENTE")
    public String getNmEnte() {
        return this.nmEnte;
    }

    public void setNmEnte(String nmEnte) {
        this.nmEnte = nmEnte;
    }

    @Column(name = "NM_ENTE_CONVENZ")
    public String getNmEnteConvenz() {
        return this.nmEnteConvenz;
    }

    public void setNmEnteConvenz(String nmEnteConvenz) {
        this.nmEnteConvenz = nmEnteConvenz;
    }

    @Column(name = "NM_SISTEMA_VERSANTE")
    public String getNmSistemaVersante() {
        return this.nmSistemaVersante;
    }

    public void setNmSistemaVersante(String nmSistemaVersante) {
        this.nmSistemaVersante = nmSistemaVersante;
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

    @Column(name = "NM_USERID")
    public String getNmUserid() {
        return this.nmUserid;
    }

    public void setNmUserid(String nmUserid) {
        this.nmUserid = nmUserid;
    }

    @Column(name = "TI_CODICE_AMMIN_TITOL")
    public String getTiCodiceAmminTitol() {
        return this.tiCodiceAmminTitol;
    }

    public void setTiCodiceAmminTitol(String tiCodiceAmminTitol) {
        this.tiCodiceAmminTitol = tiCodiceAmminTitol;
    }

    @Column(name = "TI_CONSERVAZIONE")
    public String getTiConservazione() {
        return this.tiConservazione;
    }

    public void setTiConservazione(String tiConservazione) {
        this.tiConservazione = tiConservazione;
    }

    @Column(name = "TI_MODELLO_XSD_PROFILO")
    public String getTiModelloXsdProfilo() {
        return this.tiModelloXsdProfilo;
    }

    public void setTiModelloXsdProfilo(String tiModelloXsdProfilo) {
        this.tiModelloXsdProfilo = tiModelloXsdProfilo;
    }

    @Column(name = "TI_MODELLO_XSD_SEGNATURA")
    public String getTiModelloXsdSegnatura() {
        return this.tiModelloXsdSegnatura;
    }

    public void setTiModelloXsdSegnatura(String tiModelloXsdSegnatura) {
        this.tiModelloXsdSegnatura = tiModelloXsdSegnatura;
    }

    @Column(name = "TI_MODELLO_XSD_NORMATIVO")
    public String getTiModelloXsdNormativo() {
        return this.tiModelloXsdNormativo;
    }

    public void setTiModelloXsdNormativo(String tiModelloXsdNormativo) {
        this.tiModelloXsdNormativo = tiModelloXsdNormativo;
    }

    @Column(name = "TI_MODELLO_XSD_SPECIFICO")
    public String getTiModelloXsdSpecifico() {
        return this.tiModelloXsdSpecifico;
    }

    public void setTiModelloXsdSpecifico(String tiModelloXsdSpecifico) {
        this.tiModelloXsdSpecifico = tiModelloXsdSpecifico;
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
    @Column(name = "TS_INI_SES")
    public Date getTsIniSes() {
        return this.tsIniSes;
    }

    public void setTsIniSes(Date tsIniSes) {
        this.tsIniSes = tsIniSes;
    }

}