/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.web.validator;

import it.eng.parer.entity.constraint.ElvElencoVer;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.Message.MessageLevel;
import it.eng.spagoLite.message.MessageBox;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Gilioli_P
 */
public class CriteriRaggruppamentoValidator extends TypeValidator {

    public CriteriRaggruppamentoValidator(MessageBox messageBox) {
        super(messageBox);
    }

    public void validaNumMaxUDComp(BigDecimal ni_max_ud, BigDecimal ni_max_comp) {
        if (ni_max_ud == null && ni_max_comp == null) {
            getMessageBox().addMessage(new Message(MessageLevel.ERR,
                    "Almeno uno dei due campi \"Numero massimo unità documentarie\" e \"Numero massimo componenti\" deve essere valorizzato"));
        } else if (ni_max_ud != null && ni_max_comp != null) {
            if ((ni_max_comp.compareTo(ni_max_ud)) < 0) {
                getMessageBox().addMessage(new Message(MessageLevel.ERR,
                        "Il numero massimo dei componenti deve essere maggiore del numero massimo di unità documentarie"));
            }
        }
    }

    public void validaNomeDescrizioneCriterioAutomatico(String nmCriterio, String dsCriterio) {
        // Gli ultimi 4 caratteri di nome e descrizione devono essere numerici e corrispondere all'anno corrente
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        // String regex = "[a-zA-Z_0-9]*["+year+"]{4}";
        String regex = ".*" + year;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(nmCriterio);
        if (!matcher.matches()) {
            getMessageBox().addMessage(new Message(MessageLevel.ERR,
                    "Controllare il nome del criterio secondo la sintassi definita per i criteri automatici "
                            + "(gli ultimi quattro caratteri del nome devono corrispondere all'anno corrente)"));
        }
        matcher = pattern.matcher(dsCriterio);
        if (!matcher.matches()) {
            getMessageBox().addMessage(new Message(MessageLevel.ERR,
                    "Controllare la descrizione del criterio secondo la sintassi definita per i criteri automatici "
                            + "(gli ultimi quattro caratteri della descrizione devono corrispondere all'anno corrente)"));
        }
    }

    public void validaFirmaAutomatica(String tiValidElenco, String tiModValidElenco) {
        if (tiValidElenco == null || tiModValidElenco == null) {
            getMessageBox().addMessage(new Message(MessageLevel.ERR,
                    "Entrambi i campi \"Tipo validazione elenco\" e \"Modalità validazione elenco\" devono essere valorizzati"));
        } else {
            if (tiValidElenco.equals(ElvElencoVer.TiValidElenco.FIRMA.name())
                    && tiModValidElenco.equals(ElvElencoVer.TiModValidElenco.AUTOMATICA.name())) {
                getMessageBox().addMessage(new Message(MessageLevel.ERR,
                        "La combinazione tipo validazione = FIRMA e modalità validazione = AUTOMATICA al momento non e' ammessa"));
            }
        }
    }
}
