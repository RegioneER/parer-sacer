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

/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.ws.recuperoTpi.dto;

import it.eng.parer.entity.RecUnitaDocRecup;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.job.utils.JobConstants.StatoDtVersRecupEnum;
import it.eng.parer.job.utils.JobConstants.StatoSessioniRecupEnum;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author Fioravanti_F
 */
public class DatiSessioneRecupero {

    private Collection<Date> dateDocumenti;
    long idRecSessioneRecupero;
    RecUnitaDocRecup recUnitaDocRecup;
    JobConstants.StatoSessioniRecupEnum statoSess;
    JobConstants.StatoDtVersRecupEnum statoDtVers;
    String errorCode;
    String errorMessage;
    boolean chiudiSessione = false;
    Date dataFineUsoBlob;

    public Collection<Date> getDateDocumenti() {
        return dateDocumenti;
    }

    public void setDateDocumenti(Collection<Date> dateDocumenti) {
        this.dateDocumenti = dateDocumenti;
    }

    public long getIdRecSessioneRecupero() {
        return idRecSessioneRecupero;
    }

    public void setIdRecSessioneRecupero(long idRecSessioneRecupero) {
        this.idRecSessioneRecupero = idRecSessioneRecupero;
    }

    public RecUnitaDocRecup getRecUnitaDocRecup() {
        return recUnitaDocRecup;
    }

    public void setRecUnitaDocRecup(RecUnitaDocRecup recUnitaDocRecup) {
        this.recUnitaDocRecup = recUnitaDocRecup;
    }

    public StatoSessioniRecupEnum getStatoSess() {
        return statoSess;
    }

    public void setStatoSess(StatoSessioniRecupEnum statoSess) {
        this.statoSess = statoSess;
    }

    public StatoDtVersRecupEnum getStatoDtVers() {
        return statoDtVers;
    }

    public void setStatoDtVers(StatoDtVersRecupEnum statoDtVers) {
        this.statoDtVers = statoDtVers;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public boolean isChiudiSessione() {
        return chiudiSessione;
    }

    public void setChiudiSessione(boolean chiudiSessione) {
        this.chiudiSessione = chiudiSessione;
    }

    public Date getDataFineUsoBlob() {
        return dataFineUsoBlob;
    }

    public void setDataFineUsoBlob(Date dataFineUsoBlob) {
        this.dataFineUsoBlob = dataFineUsoBlob;
    }
}
