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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.viewEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Id;

/**
 *
 * @author DiLorenzo_F
 */
public interface MonVLisUdUpdKoInterface extends Serializable {

    @Column(name = "AA_KEY_UNITA_DOC")
    BigDecimal getAaKeyUnitaDoc();

    @Column(name = "CD_CONTROLLO_WS_PRINC")
    String getCdControlloWsPrinc();

    @Column(name = "CD_CONTROLLO_WS_PRINC_LAST")
    String getCdControlloWsPrincLast();

    @Column(name = "CD_ERR_PRINC")
    String getCdErrPrinc();

    @Column(name = "CD_ERR_PRINC_LAST")
    String getCdErrPrincLast();

    @Column(name = "CD_KEY_UNITA_DOC")
    String getCdKeyUnitaDoc();

    @Column(name = "CD_REGISTRO_KEY_UNITA_DOC")
    String getCdRegistroKeyUnitaDoc();

    @Column(name = "DS_ENTE_STRUT")
    String getDsEnteStrut();

    @Column(name = "DS_ERR_PRINC")
    String getDsErrPrinc();

    @Column(name = "DS_ERR_PRINC_LAST")
    String getDsErrPrincLast();

    @Column(name = "DS_TS_INI_LAST_SES")
    String getDsTsIniLastSes();

    @Column(name = "DS_TS_INI_SES")
    String getDsTsIniSes();

    @Column(name = "DS_UNITA_DOC")
    String getDsUnitaDoc();

    @Column(name = "ID_AMBIENTE")
    BigDecimal getIdAmbiente();

    @Column(name = "ID_CONTROLLO_WS_PRINC")
    BigDecimal getIdControlloWsPrinc();

    @Column(name = "ID_CONTROLLO_WS_PRINC_LAST")
    BigDecimal getIdControlloWsPrincLast();

    @Column(name = "ID_ENTE")
    BigDecimal getIdEnte();

    @Column(name = "ID_ERR_SACER_PRINC")
    BigDecimal getIdErrSacerPrinc();

    @Column(name = "ID_ERR_SACER_PRINC_LAST")
    BigDecimal getIdErrSacerPrincLast();

    @Column(name = "ID_REGISTRO_UNITA_DOC")
    BigDecimal getIdRegistroUnitaDoc();

    @Column(name = "ID_SES_UPD_UD_KO_LAST")
    BigDecimal getIdSesUpdUdKoLast();

    @Id
    @Column(name = "ID_SES_UPD_UNITA_DOC_KO")
    BigDecimal getIdSesUpdUnitaDocKo();

    @Column(name = "ID_STRUT")
    BigDecimal getIdStrut();

    @Column(name = "ID_TIPO_DOC_PRINC")
    BigDecimal getIdTipoDocPrinc();

    @Column(name = "ID_TIPO_DOC_PRINC_LAST")
    BigDecimal getIdTipoDocPrincLast();

    @Column(name = "ID_TIPO_UNITA_DOC")
    BigDecimal getIdTipoUnitaDoc();

    @Column(name = "ID_TIPO_UNITA_DOC_LAST")
    BigDecimal getIdTipoUnitaDocLast();

    @Column(name = "ID_UPD_UNITA_DOC_KO")
    BigDecimal getIdUpdUnitaDocKo();

    @Column(name = "ID_USER_IAM_COR")
    BigDecimal getIdUserIamCor();

    @Column(name = "NM_AMBIENTE")
    String getNmAmbiente();

    @Column(name = "NM_ENTE")
    String getNmEnte();

    @Column(name = "NM_STRUT")
    String getNmStrut();

    @Column(name = "NM_TIPO_DOC_PRINC")
    String getNmTipoDocPrinc();

    @Column(name = "NM_TIPO_DOC_PRINC_LAST")
    String getNmTipoDocPrincLast();

    @Column(name = "NM_TIPO_UNITA_DOC")
    String getNmTipoUnitaDoc();

    @Column(name = "NM_TIPO_UNITA_DOC_LAST")
    String getNmTipoUnitaDocLast();

    @Column(name = "TI_STATO_SES_UPD_KO")
    String getTiStatoSesUpdKo();

    @Column(name = "TI_STATO_UPD_UD_KO")
    String getTiStatoUpdUdKo();

    @Column(name = "TS_INI_SES")
    Timestamp getTsIniSes();

    @Column(name = "TS_INI_LAST_SES")
    Timestamp getTsIniLastSes();

    void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc);

    void setCdControlloWsPrinc(String cdControlloWsPrinc);

    void setCdControlloWsPrincLast(String cdControlloWsPrincLast);

    void setCdErrPrinc(String cdErrPrinc);

    void setCdErrPrincLast(String cdErrPrincLast);

    void setCdKeyUnitaDoc(String cdKeyUnitaDoc);

    void setCdRegistroKeyUnitaDoc(String cdRegistroKeyUnitaDoc);

    void setDsEnteStrut(String dsEnteStrut);

    void setDsErrPrinc(String dsErrPrinc);

    void setDsErrPrincLast(String dsErrPrincLast);

    void setDsTsIniLastSes(String dsTsIniLastSes);

    void setDsTsIniSes(String dsTsIniSes);

    void setDsUnitaDoc(String dsUnitaDoc);

    void setIdAmbiente(BigDecimal idAmbiente);

    void setIdControlloWsPrinc(BigDecimal idControlloWsPrinc);

    void setIdControlloWsPrincLast(BigDecimal idControlloWsPrincLast);

    void setIdEnte(BigDecimal idEnte);

    void setIdErrSacerPrinc(BigDecimal idErrSacerPrinc);

    void setIdErrSacerPrincLast(BigDecimal idErrSacerPrincLast);

    void setIdRegistroUnitaDoc(BigDecimal idRegistroUnitaDoc);

    void setIdSesUpdUdKoLast(BigDecimal idSesUpdUdKoLast);

    void setIdSesUpdUnitaDocKo(BigDecimal idSesUpdUnitaDocKo);

    void setIdStrut(BigDecimal idStrut);

    void setIdTipoDocPrinc(BigDecimal idTipoDocPrinc);

    void setIdTipoDocPrincLast(BigDecimal idTipoDocPrincLast);

    void setIdTipoUnitaDoc(BigDecimal idTipoUnitaDoc);

    void setIdTipoUnitaDocLast(BigDecimal idTipoUnitaDocLast);

    void setIdUpdUnitaDocKo(BigDecimal idUpdUnitaDocKo);

    void setIdUserIamCor(BigDecimal idUserIamCor);

    void setNmAmbiente(String nmAmbiente);

    void setNmEnte(String nmEnte);

    void setNmStrut(String nmStrut);

    void setNmTipoDocPrinc(String nmTipoDocPrinc);

    void setNmTipoDocPrincLast(String nmTipoDocPrincLast);

    void setNmTipoUnitaDoc(String nmTipoUnitaDoc);

    void setNmTipoUnitaDocLast(String nmTipoUnitaDocLast);

    void setTiStatoSesUpdKo(String tiStatoSesUpdKo);

    void setTiStatoUpdUdKo(String tiStatoUpdUdKo);

    void setTsIniLastSes(Timestamp tsIniLastSes);

    void setTsIniSes(Timestamp tsIniSes);

}
