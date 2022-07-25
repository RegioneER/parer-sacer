/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.web.validator;

import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.Message.MessageLevel;
import it.eng.spagoLite.message.MessageBox;
import java.util.List;

/**
 *
 * @author DiLorenzo_F
 */
public class ElenchiVersamentoValidator extends TypeValidator {

    public ElenchiVersamentoValidator(MessageBox messageBox) {
        super(messageBox);
    }

    public void validaTipoValidazione(List<String> tiValidElenco) {
        if (tiValidElenco == null || tiValidElenco.isEmpty()) {
            getMessageBox().addMessage(new Message(MessageLevel.ERR, "Indicare il \"Tipo validazione\""));
        }
    }
}