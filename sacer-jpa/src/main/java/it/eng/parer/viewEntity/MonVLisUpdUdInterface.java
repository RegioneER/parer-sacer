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
import java.util.Date;

/**
 *
 * @author gilioli_p
 */
public interface MonVLisUpdUdInterface extends Serializable {

    BigDecimal getAaKeyUnitaDoc();

    String getCdKeyUnitaDoc();

    String getCdRegistroKeyUnitaDoc();

    String getDsEnteStrut();

    String getDsTsIniSes();

    String getDsUnitaDoc();

    String getFlForzaUpd();

    String getFlSesUpdKoRisolti();

    BigDecimal getIdAmbiente();

    BigDecimal getIdEnte();

    BigDecimal getIdRegistroUnitaDoc();

    BigDecimal getIdStrut();

    BigDecimal getIdTipoDocPrinc();

    BigDecimal getIdTipoUnitaDoc();

    BigDecimal getIdUnitaDoc();

    BigDecimal getIdUpdUnitaDoc();

    BigDecimal getIdUserIamCor();

    String getNmAmbiente();

    String getNmEnte();

    String getNmStrut();

    String getNmTipoDocPrinc();

    String getNmTipoUnitaDoc();

    String getNtUpd();

    BigDecimal getPgUpdUnitaDoc();

    String getTiStatoUpdElencoVers();

    Date getTsIniSes();

    void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc);

    void setCdKeyUnitaDoc(String cdKeyUnitaDoc);

    void setCdRegistroKeyUnitaDoc(String cdRegistroKeyUnitaDoc);

    void setDsEnteStrut(String dsEnteStrut);

    void setDsTsIniSes(String dsTsIniSes);

    void setDsUnitaDoc(String dsUnitaDoc);

    void setFlForzaUpd(String flForzaUpd);

    void setFlSesUpdKoRisolti(String flSesUpdKoRisolti);

    void setIdAmbiente(BigDecimal idAmbiente);

    void setIdEnte(BigDecimal idEnte);

    void setIdRegistroUnitaDoc(BigDecimal idRegistroUnitaDoc);

    void setIdStrut(BigDecimal idStrut);

    void setIdTipoDocPrinc(BigDecimal idTipoDocPrinc);

    void setIdTipoUnitaDoc(BigDecimal idTipoUnitaDoc);

    void setIdUnitaDoc(BigDecimal idUnitaDoc);

    void setIdUpdUnitaDoc(BigDecimal idUpdUnitaDoc);

    void setIdUserIamCor(BigDecimal idUserIamCor);

    void setNmAmbiente(String nmAmbiente);

    void setNmEnte(String nmEnte);

    void setNmStrut(String nmStrut);

    void setNmTipoDocPrinc(String nmTipoDocPrinc);

    void setNmTipoUnitaDoc(String nmTipoUnitaDoc);

    void setNtUpd(String ntUpd);

    void setPgUpdUnitaDoc(BigDecimal pgUpdUnitaDoc);

    void setTiStatoUpdElencoVers(String tiStatoUpdElencoVers);

    void setTsIniSes(Date tsIniSes);

}
