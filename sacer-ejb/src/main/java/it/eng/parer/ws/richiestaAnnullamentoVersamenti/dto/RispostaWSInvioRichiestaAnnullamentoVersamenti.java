package it.eng.parer.ws.richiestaAnnullamentoVersamenti.dto;

import it.eng.parer.ws.dto.IRispostaWS;
import it.eng.parer.ws.dto.IRispostaWS.ErrorTypeEnum;
import it.eng.parer.ws.dto.IRispostaWS.SeverityEnum;
import it.eng.parer.ws.utils.AvanzamentoWs;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import it.eng.parer.ws.xml.esitoRichAnnullVers.EsitoRichiestaAnnullamentoVersamenti;
import it.eng.parer.ws.xml.esitoRichAnnullVers.CodiceEsitoType;

/**
 *
 * @author Gilioli_P
 */
public class RispostaWSInvioRichiestaAnnullamentoVersamenti implements IRispostaWS {
    private SeverityEnum severity = SeverityEnum.OK;
    private ErrorTypeEnum errorType = ErrorTypeEnum.NOERROR;
    private String errorMessage;
    private String errorCode;
    private EsitoRichiestaAnnullamentoVersamenti esitoRichiestaAnnullamentoVersamenti;

    public RispostaWSInvioRichiestaAnnullamentoVersamenti() {
    }

    public EsitoRichiestaAnnullamentoVersamenti getEsitoRichiestaAnnullamentoVersamenti() {
        return esitoRichiestaAnnullamentoVersamenti;
    }

    public void setEsitoRichiestaAnnullamentoVersamenti(
            EsitoRichiestaAnnullamentoVersamenti esitoRichiestaAnnullamentoVersamenti) {
        this.esitoRichiestaAnnullamentoVersamenti = esitoRichiestaAnnullamentoVersamenti;
    }

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
    public void setEsitoWsErrBundle(String errCode, Object... params) {
        esitoRichiestaAnnullamentoVersamenti.getEsitoRichiesta().setCodiceEsito(CodiceEsitoType.NEGATIVO);
        esitoRichiestaAnnullamentoVersamenti.getEsitoRichiesta().setCodiceErrore(errCode);
        esitoRichiestaAnnullamentoVersamenti.getEsitoRichiesta()
                .setMessaggioErrore(MessaggiWSBundle.getString(errCode, params));
        this.setRispostaWsError();
    }

    @Override
    public void setEsitoWsErrBundle(String errCode) {
        esitoRichiestaAnnullamentoVersamenti.getEsitoRichiesta().setCodiceEsito(CodiceEsitoType.NEGATIVO);
        esitoRichiestaAnnullamentoVersamenti.getEsitoRichiesta().setCodiceErrore(errCode);
        esitoRichiestaAnnullamentoVersamenti.getEsitoRichiesta()
                .setMessaggioErrore(MessaggiWSBundle.getString(errCode));
        this.setRispostaWsError();
    }

    @Override
    public void setEsitoWsWarnBundle(String errCode, Object... params) {
        esitoRichiestaAnnullamentoVersamenti.getEsitoRichiesta().setCodiceEsito(CodiceEsitoType.WARNING);
        esitoRichiestaAnnullamentoVersamenti.getEsitoRichiesta().setCodiceErrore(errCode);
        esitoRichiestaAnnullamentoVersamenti.getEsitoRichiesta()
                .setMessaggioErrore(MessaggiWSBundle.getString(errCode, params));
        this.setRispostaWsWarning();
    }

    @Override
    public void setEsitoWsWarnBundle(String errCode) {
        esitoRichiestaAnnullamentoVersamenti.getEsitoRichiesta().setCodiceEsito(CodiceEsitoType.WARNING);
        esitoRichiestaAnnullamentoVersamenti.getEsitoRichiesta().setCodiceErrore(errCode);
        esitoRichiestaAnnullamentoVersamenti.getEsitoRichiesta()
                .setMessaggioErrore(MessaggiWSBundle.getString(errCode));
        this.setRispostaWsWarning();
    }

    @Override
    public void setEsitoWsError(String errCode, String errMessage) {
        esitoRichiestaAnnullamentoVersamenti.getEsitoRichiesta().setCodiceEsito(CodiceEsitoType.NEGATIVO);
        esitoRichiestaAnnullamentoVersamenti.getEsitoRichiesta().setCodiceErrore(errCode);
        esitoRichiestaAnnullamentoVersamenti.getEsitoRichiesta().setMessaggioErrore(errMessage);
        this.setRispostaWsError();
    }

    @Override
    public void setEsitoWsWarning(String errCode, String errMessage) {
        esitoRichiestaAnnullamentoVersamenti.getEsitoRichiesta().setCodiceEsito(CodiceEsitoType.WARNING);
        esitoRichiestaAnnullamentoVersamenti.getEsitoRichiesta().setCodiceErrore(errCode);
        esitoRichiestaAnnullamentoVersamenti.getEsitoRichiesta().setMessaggioErrore(errMessage);
        this.setRispostaWsWarning();
    }

    private void setRispostaWsError() {
        this.severity = IRispostaWS.SeverityEnum.ERROR;
        this.errorCode = esitoRichiestaAnnullamentoVersamenti.getEsitoRichiesta().getCodiceErrore();
        this.errorMessage = esitoRichiestaAnnullamentoVersamenti.getEsitoRichiesta().getMessaggioErrore();
    }

    private void setRispostaWsWarning() {
        this.severity = IRispostaWS.SeverityEnum.WARNING;
        this.errorCode = esitoRichiestaAnnullamentoVersamenti.getEsitoRichiesta().getCodiceErrore();
        this.errorMessage = esitoRichiestaAnnullamentoVersamenti.getEsitoRichiesta().getMessaggioErrore();
    }

    @Override
    public AvanzamentoWs getAvanzamento() {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
                                                                       // Tools | Templates.
    }

    @Override
    public void setAvanzamento(AvanzamentoWs avanzamento) {
        throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
                                                                       // Tools | Templates.
    }

}
