package it.eng.parer.web.validator;

import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.Message.MessageLevel;
import it.eng.spagoLite.message.MessageBox;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Gilioli_P
 */
public class MonitoraggioValidator extends TypeValidator {

    public MonitoraggioValidator(MessageBox messageBox) {
        super(messageBox);
    }

    /**
     * Metodo di validazione della chiave unita documentaria personalizzato per le sezioni Versamento Fallito e Sessione
     * Errata
     *
     * @param registro
     *            valore registro
     * @param anno
     *            valore anno
     * @param numero
     *            valore numero
     *
     * @throws EMFError
     *             errore generico
     */
    public void validaChiaveUnitaDoc(String registro, BigDecimal anno, String numero) throws EMFError {
        // Se almeno uno dei 3 campi è diverso da null e almeno uno è uguale a null genera errore
        // Per essere corretto o tutti sono diversi da null o tutti sono uguali a null
        if ((registro != null || anno != null || numero != null)
                && (registro == null || anno == null || numero == null)) {
            getMessageBox().addMessage(new Message(MessageLevel.ERR,
                    "Uno dei campi della chiave unità documentaria non è stato impostato"));
        }
    }

    public void validaSceltaPeriodoGiornoVersamento(String periodo, Date data) {
        if (periodo != null && data != null) {
            getMessageBox().addMessage(
                    new Message(MessageLevel.ERR, "Filtri periodo e data versamento entrambi valorizzati!"));
        } else if (periodo == null && data == null) {
            getMessageBox().addMessage(new Message(MessageLevel.ERR,
                    "Attenzione: è necessario valorizzare almeno uno tra i campi periodo e giorno versamento"));
        }
    }

    public void validaSceltaPeriodoGiornoVersamento(String periodo, Date giorno_vers_da, BigDecimal ore_vers_da,
            BigDecimal minuti_vers_da, Date giorno_vers_a, BigDecimal ore_vers_a, BigDecimal minuti_vers_a) {
        if (periodo != null && (giorno_vers_da != null || ore_vers_da != null || minuti_vers_da != null
                || giorno_vers_a != null || ore_vers_a != null || minuti_vers_a != null)) {
            getMessageBox()
                    .addMessage(new Message(MessageLevel.ERR, "Campi periodo e data versamento entrambi valorizzati!"));
        } else if (periodo == null && giorno_vers_da == null && ore_vers_da == null && minuti_vers_da == null
                && giorno_vers_a == null && ore_vers_a == null && minuti_vers_a == null) {
            getMessageBox().addMessage(new Message(MessageLevel.ERR,
                    "Attenzione: è necessario valorizzare almeno uno tra i campi periodo e giorno versamento"));
        }
    }

    public void validaFlagVerificatoNonRisolubile(String verificato, String nonRisolubile) {
        if (verificato.equals("0") && nonRisolubile.equals("1")) {
            getMessageBox().addMessage(new Message(MessageLevel.ERR,
                    "Una sessione può essere definita non risolubile o risolubile solo se è stata verificata"));
        }
    }

    public void validaDateCalcoloContenutoSacer(Date dataRifDa, Date dataRifA) {
        Calendar dataInizio = Calendar.getInstance();
        dataInizio.set(Calendar.YEAR, 2000);
        dataInizio.set(Calendar.MONTH, Calendar.JANUARY);
        dataInizio.set(Calendar.DATE, 1);
        dataInizio.set(Calendar.HOUR_OF_DAY, 0);
        dataInizio.set(Calendar.MINUTE, 0);
        dataInizio.set(Calendar.SECOND, 0);
        dataInizio.set(Calendar.MILLISECOND, 0);
        Calendar ieri = Calendar.getInstance();
        ieri.add(Calendar.DATE, -1);

        if (dataRifDa.after(dataRifA)) {
            getMessageBox()
                    .addMessage(new Message(MessageLevel.ERR, "Data riferimento Da maggiore di Data riferimento A"));
        }
        if (dataRifDa.before(dataInizio.getTime())) {
            getMessageBox()
                    .addMessage(new Message(MessageLevel.ERR, "Data riferimento Da inferiore al 1 gennaio 2000"));
        }
        if (dataRifDa.after(ieri.getTime())) {
            getMessageBox().addMessage(new Message(MessageLevel.ERR, "Data riferimento Da maggiore di ieri"));
        }
        if (dataRifA.before(dataInizio.getTime())) {
            getMessageBox().addMessage(new Message(MessageLevel.ERR, "Data riferimento A inferiore al 1 gennaio 2000"));
        }
        if (dataRifA.after(ieri.getTime())) {
            getMessageBox().addMessage(new Message(MessageLevel.ERR, "Data riferimento A maggiore di ieri"));
        }
    }

    public void validaDataCalcoloConsistenzaSacer(Date dataRifDa) {
        Calendar dataInizio = Calendar.getInstance();
        dataInizio.set(Calendar.YEAR, 2011);
        dataInizio.set(Calendar.MONTH, Calendar.DECEMBER);
        dataInizio.set(Calendar.DATE, 1);
        dataInizio.set(Calendar.HOUR_OF_DAY, 0);
        dataInizio.set(Calendar.MINUTE, 0);
        dataInizio.set(Calendar.SECOND, 0);
        dataInizio.set(Calendar.MILLISECOND, 0);
        if (dataRifDa.before(dataInizio.getTime())) {
            getMessageBox()
                    .addMessage(new Message(MessageLevel.ERR, "Data riferimento Da minima impostabile: 01/12/2011"));
        }
    }
}
