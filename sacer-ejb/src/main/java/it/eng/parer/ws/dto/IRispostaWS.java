/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.dto;

import it.eng.parer.ws.utils.AvanzamentoWs;
import java.io.Serializable;

/**
 *
 * @author Fioravanti_F
 */
public interface IRispostaWS extends Serializable {

    public enum SeverityEnum {

        OK, WARNING, ERROR
    }

    public enum ErrorTypeEnum {

        NOERROR, WS_DATA, WS_SIGNATURE, DB_FATAL
    }

    String getErrorCode();

    String getErrorMessage();

    ErrorTypeEnum getErrorType();

    SeverityEnum getSeverity();

    AvanzamentoWs getAvanzamento();

    void setErrorCode(String errorCode);

    void setErrorMessage(String errorMessage);

    void setErrorType(ErrorTypeEnum errorType);

    void setSeverity(SeverityEnum severity);

    void setAvanzamento(AvanzamentoWs avanzamento);

    void setEsitoWsErrBundle(String errCode, Object... params);

    void setEsitoWsErrBundle(String errCode);

    void setEsitoWsWarnBundle(String errCode, Object... params);

    void setEsitoWsWarnBundle(String errCode);

    void setEsitoWsError(String errCode, String errMessage);

    void setEsitoWsWarning(String errCode, String errMessage);
}
