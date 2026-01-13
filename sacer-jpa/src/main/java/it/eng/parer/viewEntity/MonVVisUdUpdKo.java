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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import javax.persistence.Table;

/**
 * The persistent class for the MON_V_VIS_UD_UPD_KO database table.
 *
 */
@Entity
@Table(name = "MON_V_VIS_UD_UPD_KO")

public class MonVVisUdUpdKo implements Serializable {
    private static final long serialVersionUID = 1L;
    private String blXmlRichLast;
    private String blXmlRispLast;
    private String cdControlloWsPrinc;
    private String cdErrPrinc;
    private String cdVersioneWs;
    private String dsEnteStrut;
    private String dsErrPrinc;
    private String dsTsFineLastSes;
    private String dsTsIniLastSes;
    private String dsUnitaDoc;
    private BigDecimal idControlloWsPrinc;
    private BigDecimal idEnte;
    private BigDecimal idErrSacerPrinc;
    private BigDecimal idRegistroUnitaDocLast;
    private BigDecimal idSesUpdUdKoLast;
    private BigDecimal idStrut;
    private BigDecimal idTipoDocPrincLast;
    private BigDecimal idTipoUnitaDocLast;
    private BigDecimal idUpdUnitaDocKo;
    private BigDecimal idUserIam;
    private BigDecimal idXmlSesUpdKoRichLast;
    private BigDecimal idXmlSesUpdKoRispLast;
    private String nmTipoDocPrincLast;
    private String nmTipoUnitaDocLast;
    private String nmUserid;
    private String tiStatoUpdUdKo;

    public MonVVisUdUpdKo() {/* Hibernate */
    }

    @Lob
    @Column(name = "BL_XML_RICH_LAST")
    public String getBlXmlRichLast() {
        return this.blXmlRichLast;
    }

    public void setBlXmlRichLast(String blXmlRichLast) {
        this.blXmlRichLast = blXmlRichLast;
    }

    @Lob
    @Column(name = "BL_XML_RISP_LAST")
    public String getBlXmlRispLast() {
        return this.blXmlRispLast;
    }

    public void setBlXmlRispLast(String blXmlRispLast) {
        this.blXmlRispLast = blXmlRispLast;
    }

    @Column(name = "CD_CONTROLLO_WS_PRINC")
    public String getCdControlloWsPrinc() {
        return this.cdControlloWsPrinc;
    }

    public void setCdControlloWsPrinc(String cdControlloWsPrinc) {
        this.cdControlloWsPrinc = cdControlloWsPrinc;
    }

    @Column(name = "CD_ERR_PRINC")
    public String getCdErrPrinc() {
        return this.cdErrPrinc;
    }

    public void setCdErrPrinc(String cdErrPrinc) {
        this.cdErrPrinc = cdErrPrinc;
    }

    @Column(name = "CD_VERSIONE_WS")
    public String getCdVersioneWs() {
        return this.cdVersioneWs;
    }

    public void setCdVersioneWs(String cdVersioneWs) {
        this.cdVersioneWs = cdVersioneWs;
    }

    @Column(name = "DS_ENTE_STRUT")
    public String getDsEnteStrut() {
        return this.dsEnteStrut;
    }

    public void setDsEnteStrut(String dsEnteStrut) {
        this.dsEnteStrut = dsEnteStrut;
    }

    @Column(name = "DS_ERR_PRINC")
    public String getDsErrPrinc() {
        return this.dsErrPrinc;
    }

    public void setDsErrPrinc(String dsErrPrinc) {
        this.dsErrPrinc = dsErrPrinc;
    }

    @Column(name = "DS_TS_FINE_LAST_SES")
    public String getDsTsFineLastSes() {
        return this.dsTsFineLastSes;
    }

    public void setDsTsFineLastSes(String dsTsFineLastSes) {
        this.dsTsFineLastSes = dsTsFineLastSes;
    }

    @Column(name = "DS_TS_INI_LAST_SES")
    public String getDsTsIniLastSes() {
        return this.dsTsIniLastSes;
    }

    public void setDsTsIniLastSes(String dsTsIniLastSes) {
        this.dsTsIniLastSes = dsTsIniLastSes;
    }

    @Column(name = "DS_UNITA_DOC")
    public String getDsUnitaDoc() {
        return this.dsUnitaDoc;
    }

    public void setDsUnitaDoc(String dsUnitaDoc) {
        this.dsUnitaDoc = dsUnitaDoc;
    }

    @Column(name = "ID_CONTROLLO_WS_PRINC")
    public BigDecimal getIdControlloWsPrinc() {
        return this.idControlloWsPrinc;
    }

    public void setIdControlloWsPrinc(BigDecimal idControlloWsPrinc) {
        this.idControlloWsPrinc = idControlloWsPrinc;
    }

    @Column(name = "ID_ENTE")
    public BigDecimal getIdEnte() {
        return this.idEnte;
    }

    public void setIdEnte(BigDecimal idEnte) {
        this.idEnte = idEnte;
    }

    @Column(name = "ID_ERR_SACER_PRINC")
    public BigDecimal getIdErrSacerPrinc() {
        return this.idErrSacerPrinc;
    }

    public void setIdErrSacerPrinc(BigDecimal idErrSacerPrinc) {
        this.idErrSacerPrinc = idErrSacerPrinc;
    }

    @Column(name = "ID_REGISTRO_UNITA_DOC_LAST")
    public BigDecimal getIdRegistroUnitaDocLast() {
        return this.idRegistroUnitaDocLast;
    }

    public void setIdRegistroUnitaDocLast(BigDecimal idRegistroUnitaDocLast) {
        this.idRegistroUnitaDocLast = idRegistroUnitaDocLast;
    }

    @Column(name = "ID_SES_UPD_UD_KO_LAST")
    public BigDecimal getIdSesUpdUdKoLast() {
        return this.idSesUpdUdKoLast;
    }

    public void setIdSesUpdUdKoLast(BigDecimal idSesUpdUdKoLast) {
        this.idSesUpdUdKoLast = idSesUpdUdKoLast;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    @Column(name = "ID_TIPO_DOC_PRINC_LAST")
    public BigDecimal getIdTipoDocPrincLast() {
        return this.idTipoDocPrincLast;
    }

    public void setIdTipoDocPrincLast(BigDecimal idTipoDocPrincLast) {
        this.idTipoDocPrincLast = idTipoDocPrincLast;
    }

    @Column(name = "ID_TIPO_UNITA_DOC_LAST")
    public BigDecimal getIdTipoUnitaDocLast() {
        return this.idTipoUnitaDocLast;
    }

    public void setIdTipoUnitaDocLast(BigDecimal idTipoUnitaDocLast) {
        this.idTipoUnitaDocLast = idTipoUnitaDocLast;
    }

    @Id
    @Column(name = "ID_UPD_UNITA_DOC_KO")
    public BigDecimal getIdUpdUnitaDocKo() {
        return this.idUpdUnitaDocKo;
    }

    public void setIdUpdUnitaDocKo(BigDecimal idUpdUnitaDocKo) {
        this.idUpdUnitaDocKo = idUpdUnitaDocKo;
    }

    @Column(name = "ID_USER_IAM")
    public BigDecimal getIdUserIam() {
        return this.idUserIam;
    }

    public void setIdUserIam(BigDecimal idUserIam) {
        this.idUserIam = idUserIam;
    }

    @Column(name = "ID_XML_SES_UPD_KO_RICH_LAST")
    public BigDecimal getIdXmlSesUpdKoRichLast() {
        return this.idXmlSesUpdKoRichLast;
    }

    public void setIdXmlSesUpdKoRichLast(BigDecimal idXmlSesUpdKoRichLast) {
        this.idXmlSesUpdKoRichLast = idXmlSesUpdKoRichLast;
    }

    @Column(name = "ID_XML_SES_UPD_KO_RISP_LAST")
    public BigDecimal getIdXmlSesUpdKoRispLast() {
        return this.idXmlSesUpdKoRispLast;
    }

    public void setIdXmlSesUpdKoRispLast(BigDecimal idXmlSesUpdKoRispLast) {
        this.idXmlSesUpdKoRispLast = idXmlSesUpdKoRispLast;
    }

    @Column(name = "NM_TIPO_DOC_PRINC_LAST")
    public String getNmTipoDocPrincLast() {
        return this.nmTipoDocPrincLast;
    }

    public void setNmTipoDocPrincLast(String nmTipoDocPrincLast) {
        this.nmTipoDocPrincLast = nmTipoDocPrincLast;
    }

    @Column(name = "NM_TIPO_UNITA_DOC_LAST")
    public String getNmTipoUnitaDocLast() {
        return this.nmTipoUnitaDocLast;
    }

    public void setNmTipoUnitaDocLast(String nmTipoUnitaDocLast) {
        this.nmTipoUnitaDocLast = nmTipoUnitaDocLast;
    }

    @Column(name = "NM_USERID")
    public String getNmUserid() {
        return this.nmUserid;
    }

    public void setNmUserid(String nmUserid) {
        this.nmUserid = nmUserid;
    }

    @Column(name = "TI_STATO_UPD_UD_KO")
    public String getTiStatoUpdUdKo() {
        return this.tiStatoUpdUdKo;
    }

    public void setTiStatoUpdUdKo(String tiStatoUpdUdKo) {
        this.tiStatoUpdUdKo = tiStatoUpdUdKo;
    }

}
