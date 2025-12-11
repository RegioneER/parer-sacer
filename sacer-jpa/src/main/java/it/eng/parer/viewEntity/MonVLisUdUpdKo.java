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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.Table;

/**
 * The persistent class for the MON_V_LIS_UD_UPD_KO database table.
 *
 */
@Entity
@Table(name = "MON_V_LIS_UD_UPD_KO")

public class MonVLisUdUpdKo implements MonVLisUdUpdKoInterface {
    private static final long serialVersionUID = 1L;
    private BigDecimal aaKeyUnitaDoc;
    private String cdControlloWsPrinc;
    private String cdControlloWsPrincLast;
    private String cdErrPrinc;
    private String cdErrPrincLast;
    private String cdKeyUnitaDoc;
    private String cdRegistroKeyUnitaDoc;
    private String dsEnteStrut;
    private String dsErrPrinc;
    private String dsErrPrincLast;
    private String dsTsIniLastSes;
    private String dsTsIniSes;
    private String dsUnitaDoc;
    private BigDecimal idAmbiente;
    private BigDecimal idControlloWsPrinc;
    private BigDecimal idControlloWsPrincLast;
    private BigDecimal idEnte;
    private BigDecimal idErrSacerPrinc;
    private BigDecimal idErrSacerPrincLast;
    private BigDecimal idRegistroUnitaDoc;
    private BigDecimal idSesUpdUdKoLast;
    private BigDecimal idSesUpdUnitaDocKo;
    private BigDecimal idStrut;
    private BigDecimal idTipoDocPrinc;
    private BigDecimal idTipoDocPrincLast;
    private BigDecimal idTipoUnitaDoc;
    private BigDecimal idTipoUnitaDocLast;
    private BigDecimal idUpdUnitaDocKo;
    private BigDecimal idUserIamCor;
    private String nmAmbiente;
    private String nmEnte;
    private String nmStrut;
    private String nmTipoDocPrinc;
    private String nmTipoDocPrincLast;
    private String nmTipoUnitaDoc;
    private String nmTipoUnitaDocLast;
    private String tiStatoSesUpdKo;
    private String tiStatoUpdUdKo;
    private Timestamp tsIniSes;
    private Timestamp tsIniLastSes;

    public MonVLisUdUpdKo() {/* Hibernate */
    }

    public MonVLisUdUpdKo(BigDecimal idUserIamCor, BigDecimal idAmbiente, String nmAmbiente,
            BigDecimal idEnte, String nmEnte, BigDecimal idStrut, String nmStrut,
            BigDecimal idTipoUnitaDocLast, String nmTipoUnitaDocLast, String cdRegistroKeyUnitaDoc,
            BigDecimal idTipoDocPrincLast, String nmTipoDocPrincLast, BigDecimal aaKeyUnitaDoc,
            String cdKeyUnitaDoc, String tiStatoUpdUdKo, Timestamp tsIniLastSes,
            BigDecimal idSesUpdUdKoLast, BigDecimal idUpdUnitaDocKo, String dsTsIniLastSes,
            String dsEnteStrut, String dsUnitaDoc, String dsErrPrincLast, String cdErrPrincLast,
            String cdControlloWsPrincLast) {
        this.idUserIamCor = idUserIamCor;
        this.idAmbiente = idAmbiente;
        this.nmAmbiente = nmAmbiente;
        this.idEnte = idEnte;
        this.nmEnte = nmEnte;
        this.idStrut = idStrut;
        this.nmStrut = nmStrut;
        this.idTipoUnitaDocLast = idTipoUnitaDocLast;
        this.nmTipoUnitaDocLast = nmTipoUnitaDocLast;
        this.cdRegistroKeyUnitaDoc = cdRegistroKeyUnitaDoc;
        this.idTipoDocPrincLast = idTipoDocPrincLast;
        this.nmTipoDocPrincLast = nmTipoDocPrincLast;
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
        this.cdKeyUnitaDoc = cdKeyUnitaDoc;
        this.tiStatoUpdUdKo = tiStatoUpdUdKo;
        this.tsIniLastSes = tsIniLastSes;
        this.idSesUpdUdKoLast = idSesUpdUdKoLast;
        this.idUpdUnitaDocKo = idUpdUnitaDocKo;
        this.dsTsIniLastSes = dsTsIniLastSes;
        this.dsEnteStrut = dsEnteStrut;
        this.dsUnitaDoc = dsUnitaDoc;
        this.dsErrPrincLast = dsErrPrincLast;
        this.cdErrPrincLast = cdErrPrincLast;
        this.cdControlloWsPrincLast = cdControlloWsPrincLast;
    }

    public MonVLisUdUpdKo(BigDecimal idUserIamCor, BigDecimal idAmbiente, String nmAmbiente,
            BigDecimal idEnte, String nmEnte, BigDecimal idStrut, String nmStrut,
            BigDecimal idTipoUnitaDocLast, String nmTipoUnitaDocLast, String cdRegistroKeyUnitaDoc,
            BigDecimal idTipoDocPrincLast, String nmTipoDocPrincLast, BigDecimal aaKeyUnitaDoc,
            String cdKeyUnitaDoc, String tiStatoUpdUdKo, Date tsIniLastSes,
            BigDecimal idSesUpdUdKoLast, BigDecimal idUpdUnitaDocKo, String dsTsIniLastSes,
            String dsEnteStrut, String dsUnitaDoc, String dsErrPrincLast, String cdErrPrincLast,
            String cdControlloWsPrincLast) {
        this.idUserIamCor = idUserIamCor;
        this.idAmbiente = idAmbiente;
        this.nmAmbiente = nmAmbiente;
        this.idEnte = idEnte;
        this.nmEnte = nmEnte;
        this.idStrut = idStrut;
        this.nmStrut = nmStrut;
        this.idTipoUnitaDocLast = idTipoUnitaDocLast;
        this.nmTipoUnitaDocLast = nmTipoUnitaDocLast;
        this.cdRegistroKeyUnitaDoc = cdRegistroKeyUnitaDoc;
        this.idTipoDocPrincLast = idTipoDocPrincLast;
        this.nmTipoDocPrincLast = nmTipoDocPrincLast;
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
        this.cdKeyUnitaDoc = cdKeyUnitaDoc;
        this.tiStatoUpdUdKo = tiStatoUpdUdKo;
        this.tsIniLastSes = new Timestamp(tsIniLastSes.getTime());
        this.idSesUpdUdKoLast = idSesUpdUdKoLast;
        this.idUpdUnitaDocKo = idUpdUnitaDocKo;
        this.dsTsIniLastSes = dsTsIniLastSes;
        this.dsEnteStrut = dsEnteStrut;
        this.dsUnitaDoc = dsUnitaDoc;
        this.dsErrPrincLast = dsErrPrincLast;
        this.cdErrPrincLast = cdErrPrincLast;
        this.cdControlloWsPrincLast = cdControlloWsPrincLast;
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    @Override
    public BigDecimal getAaKeyUnitaDoc() {
        return this.aaKeyUnitaDoc;
    }

    @Override
    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    @Column(name = "CD_CONTROLLO_WS_PRINC")
    @Override
    public String getCdControlloWsPrinc() {
        return this.cdControlloWsPrinc;
    }

    @Override
    public void setCdControlloWsPrinc(String cdControlloWsPrinc) {
        this.cdControlloWsPrinc = cdControlloWsPrinc;
    }

    @Column(name = "CD_CONTROLLO_WS_PRINC_LAST")
    @Override
    public String getCdControlloWsPrincLast() {
        return this.cdControlloWsPrincLast;
    }

    @Override
    public void setCdControlloWsPrincLast(String cdControlloWsPrincLast) {
        this.cdControlloWsPrincLast = cdControlloWsPrincLast;
    }

    @Column(name = "CD_ERR_PRINC")
    @Override
    public String getCdErrPrinc() {
        return this.cdErrPrinc;
    }

    @Override
    public void setCdErrPrinc(String cdErrPrinc) {
        this.cdErrPrinc = cdErrPrinc;
    }

    @Column(name = "CD_ERR_PRINC_LAST")
    @Override
    public String getCdErrPrincLast() {
        return this.cdErrPrincLast;
    }

    @Override
    public void setCdErrPrincLast(String cdErrPrincLast) {
        this.cdErrPrincLast = cdErrPrincLast;
    }

    @Column(name = "CD_KEY_UNITA_DOC")
    @Override
    public String getCdKeyUnitaDoc() {
        return this.cdKeyUnitaDoc;
    }

    @Override
    public void setCdKeyUnitaDoc(String cdKeyUnitaDoc) {
        this.cdKeyUnitaDoc = cdKeyUnitaDoc;
    }

    @Column(name = "CD_REGISTRO_KEY_UNITA_DOC")
    @Override
    public String getCdRegistroKeyUnitaDoc() {
        return this.cdRegistroKeyUnitaDoc;
    }

    @Override
    public void setCdRegistroKeyUnitaDoc(String cdRegistroKeyUnitaDoc) {
        this.cdRegistroKeyUnitaDoc = cdRegistroKeyUnitaDoc;
    }

    @Column(name = "DS_ENTE_STRUT")
    @Override
    public String getDsEnteStrut() {
        return this.dsEnteStrut;
    }

    @Override
    public void setDsEnteStrut(String dsEnteStrut) {
        this.dsEnteStrut = dsEnteStrut;
    }

    @Column(name = "DS_ERR_PRINC")
    @Override
    public String getDsErrPrinc() {
        return this.dsErrPrinc;
    }

    @Override
    public void setDsErrPrinc(String dsErrPrinc) {
        this.dsErrPrinc = dsErrPrinc;
    }

    @Column(name = "DS_ERR_PRINC_LAST")
    @Override
    public String getDsErrPrincLast() {
        return this.dsErrPrincLast;
    }

    @Override
    public void setDsErrPrincLast(String dsErrPrincLast) {
        this.dsErrPrincLast = dsErrPrincLast;
    }

    @Column(name = "DS_TS_INI_LAST_SES")
    @Override
    public String getDsTsIniLastSes() {
        return this.dsTsIniLastSes;
    }

    @Override
    public void setDsTsIniLastSes(String dsTsIniLastSes) {
        this.dsTsIniLastSes = dsTsIniLastSes;
    }

    @Column(name = "DS_TS_INI_SES")
    @Override
    public String getDsTsIniSes() {
        return this.dsTsIniSes;
    }

    @Override
    public void setDsTsIniSes(String dsTsIniSes) {
        this.dsTsIniSes = dsTsIniSes;
    }

    @Column(name = "DS_UNITA_DOC")
    @Override
    public String getDsUnitaDoc() {
        return this.dsUnitaDoc;
    }

    @Override
    public void setDsUnitaDoc(String dsUnitaDoc) {
        this.dsUnitaDoc = dsUnitaDoc;
    }

    @Column(name = "ID_AMBIENTE")
    @Override
    public BigDecimal getIdAmbiente() {
        return this.idAmbiente;
    }

    @Override
    public void setIdAmbiente(BigDecimal idAmbiente) {
        this.idAmbiente = idAmbiente;
    }

    @Column(name = "ID_CONTROLLO_WS_PRINC")
    @Override
    public BigDecimal getIdControlloWsPrinc() {
        return this.idControlloWsPrinc;
    }

    @Override
    public void setIdControlloWsPrinc(BigDecimal idControlloWsPrinc) {
        this.idControlloWsPrinc = idControlloWsPrinc;
    }

    @Column(name = "ID_CONTROLLO_WS_PRINC_LAST")
    @Override
    public BigDecimal getIdControlloWsPrincLast() {
        return this.idControlloWsPrincLast;
    }

    @Override
    public void setIdControlloWsPrincLast(BigDecimal idControlloWsPrincLast) {
        this.idControlloWsPrincLast = idControlloWsPrincLast;
    }

    @Column(name = "ID_ENTE")
    @Override
    public BigDecimal getIdEnte() {
        return this.idEnte;
    }

    @Override
    public void setIdEnte(BigDecimal idEnte) {
        this.idEnte = idEnte;
    }

    @Column(name = "ID_ERR_SACER_PRINC")
    @Override
    public BigDecimal getIdErrSacerPrinc() {
        return this.idErrSacerPrinc;
    }

    @Override
    public void setIdErrSacerPrinc(BigDecimal idErrSacerPrinc) {
        this.idErrSacerPrinc = idErrSacerPrinc;
    }

    @Column(name = "ID_ERR_SACER_PRINC_LAST")
    @Override
    public BigDecimal getIdErrSacerPrincLast() {
        return this.idErrSacerPrincLast;
    }

    @Override
    public void setIdErrSacerPrincLast(BigDecimal idErrSacerPrincLast) {
        this.idErrSacerPrincLast = idErrSacerPrincLast;
    }

    @Column(name = "ID_REGISTRO_UNITA_DOC")
    @Override
    public BigDecimal getIdRegistroUnitaDoc() {
        return this.idRegistroUnitaDoc;
    }

    @Override
    public void setIdRegistroUnitaDoc(BigDecimal idRegistroUnitaDoc) {
        this.idRegistroUnitaDoc = idRegistroUnitaDoc;
    }

    @Column(name = "ID_SES_UPD_UD_KO_LAST")
    @Override
    public BigDecimal getIdSesUpdUdKoLast() {
        return this.idSesUpdUdKoLast;
    }

    @Override
    public void setIdSesUpdUdKoLast(BigDecimal idSesUpdUdKoLast) {
        this.idSesUpdUdKoLast = idSesUpdUdKoLast;
    }

    @Id
    @Column(name = "ID_SES_UPD_UNITA_DOC_KO")
    @Override
    public BigDecimal getIdSesUpdUnitaDocKo() {
        return this.idSesUpdUnitaDocKo;
    }

    @Override
    public void setIdSesUpdUnitaDocKo(BigDecimal idSesUpdUnitaDocKo) {
        this.idSesUpdUnitaDocKo = idSesUpdUnitaDocKo;
    }

    @Column(name = "ID_STRUT")
    @Override
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    @Override
    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    @Column(name = "ID_TIPO_DOC_PRINC")
    @Override
    public BigDecimal getIdTipoDocPrinc() {
        return this.idTipoDocPrinc;
    }

    @Override
    public void setIdTipoDocPrinc(BigDecimal idTipoDocPrinc) {
        this.idTipoDocPrinc = idTipoDocPrinc;
    }

    @Column(name = "ID_TIPO_DOC_PRINC_LAST")
    @Override
    public BigDecimal getIdTipoDocPrincLast() {
        return this.idTipoDocPrincLast;
    }

    @Override
    public void setIdTipoDocPrincLast(BigDecimal idTipoDocPrincLast) {
        this.idTipoDocPrincLast = idTipoDocPrincLast;
    }

    @Column(name = "ID_TIPO_UNITA_DOC")
    @Override
    public BigDecimal getIdTipoUnitaDoc() {
        return this.idTipoUnitaDoc;
    }

    @Override
    public void setIdTipoUnitaDoc(BigDecimal idTipoUnitaDoc) {
        this.idTipoUnitaDoc = idTipoUnitaDoc;
    }

    @Column(name = "ID_TIPO_UNITA_DOC_LAST")
    @Override
    public BigDecimal getIdTipoUnitaDocLast() {
        return this.idTipoUnitaDocLast;
    }

    @Override
    public void setIdTipoUnitaDocLast(BigDecimal idTipoUnitaDocLast) {
        this.idTipoUnitaDocLast = idTipoUnitaDocLast;
    }

    @Column(name = "ID_UPD_UNITA_DOC_KO")
    @Override
    public BigDecimal getIdUpdUnitaDocKo() {
        return this.idUpdUnitaDocKo;
    }

    @Override
    public void setIdUpdUnitaDocKo(BigDecimal idUpdUnitaDocKo) {
        this.idUpdUnitaDocKo = idUpdUnitaDocKo;
    }

    @Column(name = "ID_USER_IAM_COR")
    @Override
    public BigDecimal getIdUserIamCor() {
        return this.idUserIamCor;
    }

    @Override
    public void setIdUserIamCor(BigDecimal idUserIamCor) {
        this.idUserIamCor = idUserIamCor;
    }

    @Column(name = "NM_AMBIENTE")
    @Override
    public String getNmAmbiente() {
        return this.nmAmbiente;
    }

    @Override
    public void setNmAmbiente(String nmAmbiente) {
        this.nmAmbiente = nmAmbiente;
    }

    @Column(name = "NM_ENTE")
    @Override
    public String getNmEnte() {
        return this.nmEnte;
    }

    @Override
    public void setNmEnte(String nmEnte) {
        this.nmEnte = nmEnte;
    }

    @Column(name = "NM_STRUT")
    @Override
    public String getNmStrut() {
        return this.nmStrut;
    }

    @Override
    public void setNmStrut(String nmStrut) {
        this.nmStrut = nmStrut;
    }

    @Column(name = "NM_TIPO_DOC_PRINC")
    @Override
    public String getNmTipoDocPrinc() {
        return this.nmTipoDocPrinc;
    }

    @Override
    public void setNmTipoDocPrinc(String nmTipoDocPrinc) {
        this.nmTipoDocPrinc = nmTipoDocPrinc;
    }

    @Column(name = "NM_TIPO_DOC_PRINC_LAST")
    @Override
    public String getNmTipoDocPrincLast() {
        return this.nmTipoDocPrincLast;
    }

    @Override
    public void setNmTipoDocPrincLast(String nmTipoDocPrincLast) {
        this.nmTipoDocPrincLast = nmTipoDocPrincLast;
    }

    @Column(name = "NM_TIPO_UNITA_DOC")
    @Override
    public String getNmTipoUnitaDoc() {
        return this.nmTipoUnitaDoc;
    }

    @Override
    public void setNmTipoUnitaDoc(String nmTipoUnitaDoc) {
        this.nmTipoUnitaDoc = nmTipoUnitaDoc;
    }

    @Column(name = "NM_TIPO_UNITA_DOC_LAST")
    @Override
    public String getNmTipoUnitaDocLast() {
        return this.nmTipoUnitaDocLast;
    }

    @Override
    public void setNmTipoUnitaDocLast(String nmTipoUnitaDocLast) {
        this.nmTipoUnitaDocLast = nmTipoUnitaDocLast;
    }

    @Column(name = "TI_STATO_SES_UPD_KO")
    @Override
    public String getTiStatoSesUpdKo() {
        return this.tiStatoSesUpdKo;
    }

    @Override
    public void setTiStatoSesUpdKo(String tiStatoSesUpdKo) {
        this.tiStatoSesUpdKo = tiStatoSesUpdKo;
    }

    @Column(name = "TI_STATO_UPD_UD_KO")
    @Override
    public String getTiStatoUpdUdKo() {
        return this.tiStatoUpdUdKo;
    }

    @Override
    public void setTiStatoUpdUdKo(String tiStatoUpdUdKo) {
        this.tiStatoUpdUdKo = tiStatoUpdUdKo;
    }

    @Column(name = "TS_INI_SES")
    @Override
    public Timestamp getTsIniSes() {
        return this.tsIniSes;
    }

    @Override
    public void setTsIniSes(Timestamp tsIniSes) {
        this.tsIniSes = tsIniSes;
    }

    @Column(name = "TS_INI_LAST_SES")
    @Override
    public Timestamp getTsIniLastSes() {
        return this.tsIniLastSes;
    }

    @Override
    public void setTsIniLastSes(Timestamp tsIniLastSes) {
        this.tsIniLastSes = tsIniLastSes;
    }
}
