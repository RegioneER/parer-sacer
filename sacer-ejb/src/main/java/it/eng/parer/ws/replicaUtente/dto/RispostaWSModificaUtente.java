package it.eng.parer.ws.replicaUtente.dto;

import it.eng.parer.ws.dto.IRispostaWS;
import it.eng.parer.ws.dto.IRispostaWS.ErrorTypeEnum;
import it.eng.parer.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.integriam.server.ws.reputente.ModificaUtenteRisposta;
import it.eng.parer.ws.utils.AvanzamentoWs;

/**
 *
 * @author Gilioli_P
 */
public class RispostaWSModificaUtente implements IRispostaWS {
    private SeverityEnum severity = SeverityEnum.OK;
    private ErrorTypeEnum errorType = ErrorTypeEnum.NOERROR;
    private String errorMessage;
    private String errorCode;
    private ModificaUtenteRisposta modificaUtenteRisposta;

    public SeverityEnum getSeverity() {
        return severity;
    }

    public void setSeverity(SeverityEnum severity) {
        this.severity = severity;
    }

    public ErrorTypeEnum getErrorType() {
        return errorType;
    }

    public void setErrorType(ErrorTypeEnum errorType) {
        this.errorType = errorType;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public ModificaUtenteRisposta getModificaUtenteRisposta() {
        return modificaUtenteRisposta;
    }

    public void setModificaUtenteRisposta(ModificaUtenteRisposta modificaUtenteRisposta) {
        this.modificaUtenteRisposta = modificaUtenteRisposta;
    }

    @Override
    public AvanzamentoWs getAvanzamento() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setAvanzamento(AvanzamentoWs avanzamento) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setEsitoWsErrBundle(String errCode, Object... params) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setEsitoWsErrBundle(String errCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setEsitoWsWarnBundle(String errCode, Object... params) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setEsitoWsWarnBundle(String errCode) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setEsitoWsError(String errCode, String errMessage) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setEsitoWsWarning(String errCode, String errMessage) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
