/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.monitoraggio.dto;

import it.eng.parer.ws.dto.IRispostaWS;
import it.eng.parer.ws.monitoraggio.dto.rmonitor.HostMonitor;
import it.eng.parer.ws.utils.AvanzamentoWs;
import it.eng.parer.ws.utils.MessaggiWSBundle;

/**
 *
 * @author fioravanti_f
 */
public class RispostaWSStatusMonitor implements IRispostaWS {

    private static final long serialVersionUID = 1L;
    private SeverityEnum severity = SeverityEnum.OK;
    private ErrorTypeEnum errorType = ErrorTypeEnum.NOERROR;
    private String errorMessage;
    private String errorCode;
    //
    private HostMonitor istanzaEsito;

    @Override
    public SeverityEnum getSeverity() {
        return severity;
    }

    @Override
    public void setSeverity(SeverityEnum severity) {
        this.severity = severity;
    }

    @Override
    public ErrorTypeEnum getErrorType() {
        return errorType;
    }

    @Override
    public void setErrorType(ErrorTypeEnum errorType) {
        this.errorType = errorType;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    @Override
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public AvanzamentoWs getAvanzamento() {
        throw new UnsupportedOperationException("Metodo non implementato");
    }

    @Override
    public void setAvanzamento(AvanzamentoWs avanzamento) {
        throw new UnsupportedOperationException("Metodo non implementato");
    }

    //
    public HostMonitor getIstanzaEsito() {
        return istanzaEsito;
    }

    public void setIstanzaEsito(HostMonitor istanzaEsito) {
        this.istanzaEsito = istanzaEsito;
    }

    //
    @Override
    public void setEsitoWsErrBundle(String errCode, Object... params) {
        istanzaEsito.setCodiceErrore(errCode);
        istanzaEsito.setDescrErrore(MessaggiWSBundle.getString(errCode, params));
        this.setRispostaWsError();
    }

    @Override
    public void setEsitoWsErrBundle(String errCode) {
        istanzaEsito.setCodiceErrore(errCode);
        istanzaEsito.setDescrErrore(MessaggiWSBundle.getString(errCode));
        this.setRispostaWsError();
    }

    @Override
    public void setEsitoWsWarnBundle(String errCode, Object... params) {
        throw new UnsupportedOperationException("Metodo non implementato");
    }

    @Override
    public void setEsitoWsWarnBundle(String errCode) {
        throw new UnsupportedOperationException("Metodo non implementato");
    }

    @Override
    public void setEsitoWsError(String errCode, String errMessage) {
        istanzaEsito.setCodiceErrore(errCode);
        istanzaEsito.setDescrErrore(errMessage);
        this.setRispostaWsError();
    }

    @Override
    public void setEsitoWsWarning(String errCode, String errMessage) {
        throw new UnsupportedOperationException("Metodo non implementato");
    }

    private void setRispostaWsError() {
        this.severity = IRispostaWS.SeverityEnum.ERROR;
        this.errorCode = istanzaEsito.getCodiceErrore();
        this.errorMessage = istanzaEsito.getDescrErrore();
    }

}
