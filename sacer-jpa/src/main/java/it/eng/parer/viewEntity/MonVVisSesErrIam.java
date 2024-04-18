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
 * The persistent class for the MON_V_VIS_SES_ERR_IAM database table.
 * 
 */
@Entity
@Table(name = "MON_V_VIS_SES_ERR_IAM")
public class MonVVisSesErrIam implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigDecimal aaKeyUnitaDoc;
    private String blXmlIndex;
    private String blXmlRich;
    private String blXmlRisp;
    private String cdErr;
    private String cdKeyDocVers;
    private String cdKeyUnitaDoc;
    private String cdRegistroKeyUnitaDoc;
    private String cdVersioneWs;
    private String dsErr;
    private Date dtApertura;
    private Date dtChiusura;
    private String flSessioneErrVerif;
    private BigDecimal idSessioneVers;
    private String nmAmbiente;
    private String nmCognomeUser;
    private String nmEnte;
    private String nmNomeUser;
    private String nmStrut;
    private String nmUserid;
    private String nmUseridWs;
    private String tiSessioneVers;

    public MonVVisSesErrIam() {/* Hibernate */
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    public BigDecimal getAaKeyUnitaDoc() {
        return this.aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    @Lob()
    @Column(name = "BL_XML_INDEX")
    public String getBlXmlIndex() {
        return this.blXmlIndex;
    }

    public void setBlXmlIndex(String blXmlIndex) {
        this.blXmlIndex = blXmlIndex;
    }

    @Lob()
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

    @Column(name = "CD_ERR")
    public String getCdErr() {
        return this.cdErr;
    }

    public void setCdErr(String cdErr) {
        this.cdErr = cdErr;
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

    @Column(name = "CD_VERSIONE_WS")
    public String getCdVersioneWs() {
        return this.cdVersioneWs;
    }

    public void setCdVersioneWs(String cdVersioneWs) {
        this.cdVersioneWs = cdVersioneWs;
    }

    @Column(name = "DS_ERR")
    public String getDsErr() {
        return this.dsErr;
    }

    public void setDsErr(String dsErr) {
        this.dsErr = dsErr;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_APERTURA")
    public Date getDtApertura() {
        return this.dtApertura;
    }

    public void setDtApertura(Date dtApertura) {
        this.dtApertura = dtApertura;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CHIUSURA")
    public Date getDtChiusura() {
        return this.dtChiusura;
    }

    public void setDtChiusura(Date dtChiusura) {
        this.dtChiusura = dtChiusura;
    }

    @Column(name = "FL_SESSIONE_ERR_VERIF", columnDefinition = "char(1)")
    public String getFlSessioneErrVerif() {
        return this.flSessioneErrVerif;
    }

    public void setFlSessioneErrVerif(String flSessioneErrVerif) {
        this.flSessioneErrVerif = flSessioneErrVerif;
    }

    @Id
    @Column(name = "ID_SESSIONE_VERS")
    public BigDecimal getIdSessioneVers() {
        return this.idSessioneVers;
    }

    public void setIdSessioneVers(BigDecimal idSessioneVers) {
        this.idSessioneVers = idSessioneVers;
    }

    @Column(name = "NM_AMBIENTE")
    public String getNmAmbiente() {
        return this.nmAmbiente;
    }

    public void setNmAmbiente(String nmAmbiente) {
        this.nmAmbiente = nmAmbiente;
    }

    @Column(name = "NM_COGNOME_USER")
    public String getNmCognomeUser() {
        return this.nmCognomeUser;
    }

    public void setNmCognomeUser(String nmCognomeUser) {
        this.nmCognomeUser = nmCognomeUser;
    }

    @Column(name = "NM_ENTE")
    public String getNmEnte() {
        return this.nmEnte;
    }

    public void setNmEnte(String nmEnte) {
        this.nmEnte = nmEnte;
    }

    @Column(name = "NM_NOME_USER")
    public String getNmNomeUser() {
        return this.nmNomeUser;
    }

    public void setNmNomeUser(String nmNomeUser) {
        this.nmNomeUser = nmNomeUser;
    }

    @Column(name = "NM_STRUT")
    public String getNmStrut() {
        return this.nmStrut;
    }

    public void setNmStrut(String nmStrut) {
        this.nmStrut = nmStrut;
    }

    @Column(name = "NM_USERID")
    public String getNmUserid() {
        return this.nmUserid;
    }

    public void setNmUserid(String nmUserid) {
        this.nmUserid = nmUserid;
    }

    @Column(name = "NM_USERID_WS")
    public String getNmUseridWs() {
        return this.nmUseridWs;
    }

    public void setNmUseridWs(String nmUseridWs) {
        this.nmUseridWs = nmUseridWs;
    }

    @Column(name = "TI_SESSIONE_VERS")
    public String getTiSessioneVers() {
        return this.tiSessioneVers;
    }

    public void setTiSessioneVers(String tiSessioneVers) {
        this.tiSessioneVers = tiSessioneVers;
    }

}
