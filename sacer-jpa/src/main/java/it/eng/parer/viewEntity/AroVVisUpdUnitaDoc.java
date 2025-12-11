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
import javax.persistence.Lob;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the ARO_V_VIS_UPD_UNITA_DOC database table.
 *
 */
@Entity
@Table(name = "ARO_V_VIS_UPD_UNITA_DOC")

public class AroVVisUpdUnitaDoc implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigDecimal aaKeyUnitaDoc;
    private String blXmlRich;
    private String blXmlRisp;
    private String cdEncodingHashXmlRich;
    private String cdEncodingHashXmlRisp;
    private String cdKeyUnitaDoc;
    private String cdRegistroKeyUnitaDoc;
    private String cdVersioneXml;
    private String dsAlgoHashXmlRich;
    private String dsAlgoHashXmlRisp;
    private String dsHashXmlRich;
    private String dsHashXmlRisp;
    private String dsUrnUpdUnitaDoc;
    private String dsUrnXmlRich;
    private String dsUrnNormalizXmlRich;
    private String dsUrnXmlRisp;
    private String dsUrnNormalizXmlRisp;
    private String flForzaUpd;
    private String flUpdDatiSpec;
    private String flUpdDatiSpecMigraz;
    private String flUpdFascicoliSec;
    private String flUpdFascicoloPrinc;
    private String flUpdLinkUnitaDoc;
    private String flUpdProfiloArchiv;
    private String flUpdProfiloUnitaDoc;
    private BigDecimal idElencoVers;
    private BigDecimal idRegistroUnitaDoc;
    private BigDecimal idStrut;
    private BigDecimal idTipoDocPrinc;
    private BigDecimal idTipoUnitaDoc;
    private BigDecimal idUnitaDoc;
    private BigDecimal idUpdUnitaDoc;
    private String nmAmbiente;
    private String nmEnte;
    private String nmStrut;
    private String nmTipoDocPrinc;
    private String nmTipoUnitaDoc;
    private String nmUserid;
    private String ntUpd;
    private BigDecimal pgUpdUnitaDoc;
    private String tipoUpdUnitaDoc;
    private String tiStatoConservazione;
    private String tiStatoUpdElencoVers;
    private Date tsIniSes;
    private String flUpdProfiloNormativo;

    public AroVVisUpdUnitaDoc() {/* Hibernate */
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    public BigDecimal getAaKeyUnitaDoc() {
        return this.aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    @Lob
    @Column(name = "BL_XML_RICH")
    public String getBlXmlRich() {
        return this.blXmlRich;
    }

    public void setBlXmlRich(String blXmlRich) {
        this.blXmlRich = blXmlRich;
    }

    @Lob()
    @Column(name = "BL_XML_RISP")
    public String getBlXmlRisp() {
        return this.blXmlRisp;
    }

    public void setBlXmlRisp(String blXmlRisp) {
        this.blXmlRisp = blXmlRisp;
    }

    @Column(name = "CD_ENCODING_HASH_XML_RICH")
    public String getCdEncodingHashXmlRich() {
        return this.cdEncodingHashXmlRich;
    }

    public void setCdEncodingHashXmlRich(String cdEncodingHashXmlRich) {
        this.cdEncodingHashXmlRich = cdEncodingHashXmlRich;
    }

    @Column(name = "CD_ENCODING_HASH_XML_RISP")
    public String getCdEncodingHashXmlRisp() {
        return this.cdEncodingHashXmlRisp;
    }

    public void setCdEncodingHashXmlRisp(String cdEncodingHashXmlRisp) {
        this.cdEncodingHashXmlRisp = cdEncodingHashXmlRisp;
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

    @Column(name = "CD_VERSIONE_XML")
    public String getCdVersioneXml() {
        return this.cdVersioneXml;
    }

    public void setCdVersioneXml(String cdVersioneXml) {
        this.cdVersioneXml = cdVersioneXml;
    }

    @Column(name = "DS_ALGO_HASH_XML_RICH")
    public String getDsAlgoHashXmlRich() {
        return this.dsAlgoHashXmlRich;
    }

    public void setDsAlgoHashXmlRich(String dsAlgoHashXmlRich) {
        this.dsAlgoHashXmlRich = dsAlgoHashXmlRich;
    }

    @Column(name = "DS_ALGO_HASH_XML_RISP")
    public String getDsAlgoHashXmlRisp() {
        return this.dsAlgoHashXmlRisp;
    }

    public void setDsAlgoHashXmlRisp(String dsAlgoHashXmlRisp) {
        this.dsAlgoHashXmlRisp = dsAlgoHashXmlRisp;
    }

    @Column(name = "DS_HASH_XML_RICH")
    public String getDsHashXmlRich() {
        return this.dsHashXmlRich;
    }

    public void setDsHashXmlRich(String dsHashXmlRich) {
        this.dsHashXmlRich = dsHashXmlRich;
    }

    @Column(name = "DS_HASH_XML_RISP")
    public String getDsHashXmlRisp() {
        return this.dsHashXmlRisp;
    }

    public void setDsHashXmlRisp(String dsHashXmlRisp) {
        this.dsHashXmlRisp = dsHashXmlRisp;
    }

    @Column(name = "DS_URN_UPD_UNITA_DOC")
    public String getDsUrnUpdUnitaDoc() {
        return this.dsUrnUpdUnitaDoc;
    }

    public void setDsUrnUpdUnitaDoc(String dsUrnUpdUnitaDoc) {
        this.dsUrnUpdUnitaDoc = dsUrnUpdUnitaDoc;
    }

    @Column(name = "DS_URN_XML_RICH")
    public String getDsUrnXmlRich() {
        return this.dsUrnXmlRich;
    }

    public void setDsUrnXmlRich(String dsUrnXmlRich) {
        this.dsUrnXmlRich = dsUrnXmlRich;
    }

    @Column(name = "DS_URN_NORMALIZ_XML_RICH")
    public String getDsUrnNormalizXmlRich() {
        return this.dsUrnNormalizXmlRich;
    }

    public void setDsUrnNormalizXmlRich(String dsUrnNormalizXmlRich) {
        this.dsUrnNormalizXmlRich = dsUrnNormalizXmlRich;
    }

    @Column(name = "DS_URN_XML_RISP")
    public String getDsUrnXmlRisp() {
        return this.dsUrnXmlRisp;
    }

    public void setDsUrnXmlRisp(String dsUrnXmlRisp) {
        this.dsUrnXmlRisp = dsUrnXmlRisp;
    }

    @Column(name = "DS_URN_NORMALIZ_XML_RISP")
    public String getDsUrnNormalizXmlRisp() {
        return this.dsUrnNormalizXmlRisp;
    }

    public void setDsUrnNormalizXmlRisp(String dsUrnNormalizXmlRisp) {
        this.dsUrnNormalizXmlRisp = dsUrnNormalizXmlRisp;
    }

    @Column(name = "FL_FORZA_UPD", columnDefinition = "char(1)")
    public String getFlForzaUpd() {
        return this.flForzaUpd;
    }

    public void setFlForzaUpd(String flForzaUpd) {
        this.flForzaUpd = flForzaUpd;
    }

    @Column(name = "FL_UPD_DATI_SPEC", columnDefinition = "char(1)")
    public String getFlUpdDatiSpec() {
        return this.flUpdDatiSpec;
    }

    public void setFlUpdDatiSpec(String flUpdDatiSpec) {
        this.flUpdDatiSpec = flUpdDatiSpec;
    }

    @Column(name = "FL_UPD_DATI_SPEC_MIGRAZ", columnDefinition = "char(1)")
    public String getFlUpdDatiSpecMigraz() {
        return this.flUpdDatiSpecMigraz;
    }

    public void setFlUpdDatiSpecMigraz(String flUpdDatiSpecMigraz) {
        this.flUpdDatiSpecMigraz = flUpdDatiSpecMigraz;
    }

    @Column(name = "FL_UPD_FASCICOLI_SEC", columnDefinition = "char(1)")
    public String getFlUpdFascicoliSec() {
        return this.flUpdFascicoliSec;
    }

    public void setFlUpdFascicoliSec(String flUpdFascicoliSec) {
        this.flUpdFascicoliSec = flUpdFascicoliSec;
    }

    @Column(name = "FL_UPD_FASCICOLO_PRINC", columnDefinition = "char(1)")
    public String getFlUpdFascicoloPrinc() {
        return this.flUpdFascicoloPrinc;
    }

    public void setFlUpdFascicoloPrinc(String flUpdFascicoloPrinc) {
        this.flUpdFascicoloPrinc = flUpdFascicoloPrinc;
    }

    @Column(name = "FL_UPD_LINK_UNITA_DOC", columnDefinition = "char(1)")
    public String getFlUpdLinkUnitaDoc() {
        return this.flUpdLinkUnitaDoc;
    }

    public void setFlUpdLinkUnitaDoc(String flUpdLinkUnitaDoc) {
        this.flUpdLinkUnitaDoc = flUpdLinkUnitaDoc;
    }

    @Column(name = "FL_UPD_PROFILO_ARCHIV", columnDefinition = "char(1)")
    public String getFlUpdProfiloArchiv() {
        return this.flUpdProfiloArchiv;
    }

    public void setFlUpdProfiloArchiv(String flUpdProfiloArchiv) {
        this.flUpdProfiloArchiv = flUpdProfiloArchiv;
    }

    @Column(name = "FL_UPD_PROFILO_UNITA_DOC", columnDefinition = "char(1)")
    public String getFlUpdProfiloUnitaDoc() {
        return this.flUpdProfiloUnitaDoc;
    }

    public void setFlUpdProfiloUnitaDoc(String flUpdProfiloUnitaDoc) {
        this.flUpdProfiloUnitaDoc = flUpdProfiloUnitaDoc;
    }

    @Column(name = "ID_ELENCO_VERS")
    public BigDecimal getIdElencoVers() {
        return this.idElencoVers;
    }

    public void setIdElencoVers(BigDecimal idElencoVers) {
        this.idElencoVers = idElencoVers;
    }

    @Column(name = "ID_REGISTRO_UNITA_DOC")
    public BigDecimal getIdRegistroUnitaDoc() {
        return this.idRegistroUnitaDoc;
    }

    public void setIdRegistroUnitaDoc(BigDecimal idRegistroUnitaDoc) {
        this.idRegistroUnitaDoc = idRegistroUnitaDoc;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    @Column(name = "ID_TIPO_DOC_PRINC")
    public BigDecimal getIdTipoDocPrinc() {
        return this.idTipoDocPrinc;
    }

    public void setIdTipoDocPrinc(BigDecimal idTipoDocPrinc) {
        this.idTipoDocPrinc = idTipoDocPrinc;
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

    @Id
    @Column(name = "ID_UPD_UNITA_DOC")
    public BigDecimal getIdUpdUnitaDoc() {
        return this.idUpdUnitaDoc;
    }

    public void setIdUpdUnitaDoc(BigDecimal idUpdUnitaDoc) {
        this.idUpdUnitaDoc = idUpdUnitaDoc;
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

    @Column(name = "NM_USERID")
    public String getNmUserid() {
        return this.nmUserid;
    }

    public void setNmUserid(String nmUserid) {
        this.nmUserid = nmUserid;
    }

    @Column(name = "NT_UPD")
    public String getNtUpd() {
        return this.ntUpd;
    }

    public void setNtUpd(String ntUpd) {
        this.ntUpd = ntUpd;
    }

    @Column(name = "PG_UPD_UNITA_DOC")
    public BigDecimal getPgUpdUnitaDoc() {
        return this.pgUpdUnitaDoc;
    }

    public void setPgUpdUnitaDoc(BigDecimal pgUpdUnitaDoc) {
        this.pgUpdUnitaDoc = pgUpdUnitaDoc;
    }

    @Column(name = "TIPO_UPD_UNITA_DOC")
    public String getTipoUpdUnitaDoc() {
        return this.tipoUpdUnitaDoc;
    }

    public void setTipoUpdUnitaDoc(String tipoUpdUnitaDoc) {
        this.tipoUpdUnitaDoc = tipoUpdUnitaDoc;
    }

    @Column(name = "TI_STATO_CONSERVAZIONE")
    public String getTiStatoConservazione() {
        return this.tiStatoConservazione;
    }

    public void setTiStatoConservazione(String tiStatoConservazione) {
        this.tiStatoConservazione = tiStatoConservazione;
    }

    @Column(name = "TI_STATO_UPD_ELENCO_VERS")
    public String getTiStatoUpdElencoVers() {
        return this.tiStatoUpdElencoVers;
    }

    public void setTiStatoUpdElencoVers(String tiStatoUpdElencoVers) {
        this.tiStatoUpdElencoVers = tiStatoUpdElencoVers;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TS_INI_SES")
    public Date getTsIniSes() {
        return this.tsIniSes;
    }

    public void setTsIniSes(Date tsIniSes) {
        this.tsIniSes = tsIniSes;
    }

    @Column(name = "FL_UPD_PROFILO_NORMATIVO", columnDefinition = "char(1)")
    public String getFlUpdProfiloNormativo() {
        return flUpdProfiloNormativo;
    }

    public void setFlUpdProfiloNormativo(String flUpdProfiloNormativo) {
        this.flUpdProfiloNormativo = flUpdProfiloNormativo;
    }

}
