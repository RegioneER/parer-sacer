package it.eng.parer.web.validator;

import it.eng.parer.web.dto.DefinitoDaBean;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.MessageBox;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class UnitaDocumentarieValidator extends TypeValidator {

    public UnitaDocumentarieValidator(MessageBox messageBox) {
        super(messageBox);
    }

    /**
     * Metodo di validazione: verifica che siano popolati il campo tiScadChiusVolume oppure i campi niTempoScadChius e
     * tiTempoScadChius nella form di creazione/modifica criteri di raggruppamento o ricerca unità documentarie
     *
     * @param tiScadChiusVolume
     *            tipologia scadenza volume
     * @param niTempoScadChius
     *            numero tempo scadenza
     * @param tiTempoScadChius
     *            tipologia tempo scadenza
     */
    public void validaTipoScadenza(String tiScadChiusVolume, BigDecimal niTempoScadChius, String tiTempoScadChius) {
        if (tiScadChiusVolume == null && (niTempoScadChius == null || tiTempoScadChius == null)) {
            getMessageBox().addMessage(
                    new Message(Message.MessageLevel.ERR, "E’ necessario indicare la scadenza di chiusura. </br>"));
        } else if ((tiScadChiusVolume != null) && ((niTempoScadChius != null) || (tiTempoScadChius != null))) {
            getMessageBox().addError(
                    "I campi Tipo scadenza chiusura e Tempo scadenza chiusura non possono essere entrambi valorizzati. </br>");
        } else if (((niTempoScadChius != null) && (tiTempoScadChius == null))
                || ((niTempoScadChius == null) && (tiTempoScadChius != null))) {
            getMessageBox().addError(
                    "I campi relativi a Scadenza chiusura o Scadenza chiusura firme devono essere entrambi valorizzati. </br>");
        }
    }

    /**
     * Metodo di validazione dei dati specifici nei filtri di ricerca
     *
     * @param definitoDaList
     *            lista elementi di tipo {@link DefinitoDaBean}
     */
    public void validaDatiSpec(List<DefinitoDaBean> definitoDaList) {
        for (DefinitoDaBean definitoDa : definitoDaList) {
            final String tiOper = definitoDa.getTiOper();
            final String dlValore = definitoDa.getDlValore();
            if (!getMessageBox().hasError()) {
                checkDatoSpecifico(tiOper, dlValore);
            }
        }
    }

    public void checkDatoSpecifico(final String tiOper, final String dlValore) {
        // controllo se ho settato l'operatore NULLO se è stato inserito un valore
        if (StringUtils.isNotBlank(tiOper) && StringUtils.isNotBlank(dlValore)) {
            if (tiOper.equals(CostantiDB.TipoOperatoreDatiSpec.NULLO.name())
                    || tiOper.equals(CostantiDB.TipoOperatoreDatiSpec.NON_NULLO.name())) {
                getMessageBox()
                        .addError("Errore Filtri Dati Specifici: inserito un valore con operatore NULLO o NON_NULLO");
            }
        } else if (StringUtils.isBlank(tiOper) && StringUtils.isNotBlank(dlValore)) {
            // controllo se operatore non è stato settato ed è stato settato il valore
            getMessageBox().addError("Errore Filtri Dati Specifici: inserito un valore senza operatore");
        } else if (StringUtils.isNotBlank(tiOper) && StringUtils.isBlank(dlValore)) {
            // controllo se valore non è stato settato ed è stato settato l'operatore
            if (!tiOper.equals(CostantiDB.TipoOperatoreDatiSpec.NULLO.name())
                    && !tiOper.equals(CostantiDB.TipoOperatoreDatiSpec.NON_NULLO.name())) {
                getMessageBox().addError("Errore Filtri Dati Specifici: inserito un operatore senza valore");
            }
        }
    }

    public Object[] validaChiaviUnitaDoc(String[] registro, BigDecimal anno, String numero, BigDecimal anno_da,
            BigDecimal anno_a, String numero_da, String numero_a) {
        boolean chiave = false;
        boolean range = false;
        Object[] result = null;
        // Controllo innanzitutto che non siano stati inseriti i filtri sia sulla chiave UD singola
        // sia sulla chiave UD per range
        if (anno_da != null || anno_a != null || numero_da != null || numero_a != null) {
            range = true;
        }
        if (anno != null || numero != null || !range) {
            chiave = true;
        }
        if (chiave && range) {
            getMessageBox().addError(
                    "Sono stati inseriti valori sia nella ricerca per chiave UD singola, sia per chiave UD con range");
        } else if (chiave) {
            result = new Object[3];
            result[0] = registro;
            result[1] = anno;
            result[2] = numero;
        } else if (range) {
            result = new Object[5];
            result[0] = registro;
            if (anno_da != null && anno_a == null) {
                result[1] = anno_da;
                result[2] = new BigDecimal(GregorianCalendar.getInstance().get(Calendar.YEAR));
            } else if (anno_da == null && anno_a != null) {
                result[1] = new BigDecimal(2000);
                result[2] = anno_a;
            } else {
                result[1] = anno_da;
                result[2] = anno_a;
            }

            if (numero_da != null && numero_a == null) {
                result[3] = numero_da;
                result[4] = "zzzzzzzzzzzz";
            } else if (numero_da == null && numero_a != null) {
                result[3] = "000000000000";
                result[4] = numero_a;
            } else {
                result[3] = numero_da;
                result[4] = numero_a;
            }

            if ((result[1] != null || result[2] != null)
                    && ((BigDecimal) result[1]).compareTo((BigDecimal) result[2]) > 0) {
                getMessageBox().addError("Range di chiavi unità documentaria: Anno Da maggiore di Anno A");
            }
            if ((result[3] != null || result[4] != null) && ((String) result[3]).compareTo((String) result[4]) > 0) {
                getMessageBox().addError("Range di chiavi unità documentaria: Numero Da maggiore di Numero A");
            }
        }
        return result;
    }

    public Object[] validaChiaviUnitaDoc(String registro, BigDecimal anno, String numero, BigDecimal anno_da,
            BigDecimal anno_a, String numero_da, String numero_a) {
        String[] registroArr = new String[1];
        registroArr[0] = registro;
        return validaChiaviUnitaDoc(registroArr, anno, numero, anno_da, anno_a, numero_da, numero_a);
    }

    public void controllaPresenzaAnno(BigDecimal anno, BigDecimal annoDa, BigDecimal annoA) {
        if (anno == null && annoDa == null && annoA == null) {
            getMessageBox().addMessage(new Message(Message.MessageLevel.ERR,
                    "Almeno uno dei filtri relativo all'anno o al range di anni di chiave unità documentaria deve essere valorizzato</br>"));
        }
    }

    public Object[] validaChiaviUnitaDocRicUd(String[] registro, BigDecimal anno, String numero, BigDecimal anno_da,
            BigDecimal anno_a, String numero_da, String numero_a, String numero_contiene) {
        boolean chiave = false;
        boolean range = false;
        boolean num = false;
        Object[] result = null;

        if (anno_da != null || anno_a != null || numero_da != null || numero_a != null) {
            range = true;
        }
        if (anno != null || numero != null) {
            chiave = true;
        }
        if (numero_contiene != null) {
            num = true;
        }
        // Controllo innanzitutto che non siano stati inseriti i filtri sia sulla chiave UD singola
        // sia sulla chiave UD per range
        if (chiave && range) {
            getMessageBox().addError(
                    "Sono stati inseriti valori sia nella ricerca per chiave UD singola, sia per chiave UD con range");
        } else if (chiave && num) {
            if (numero != null) {
                getMessageBox().addError(
                        "Sono stati inseriti valori sia nella ricerca per numero UD singola, sia per numero contiene");
            }
        } else if (range && num) {
            getMessageBox().addError("Sono stati inseriti valori sia per chiave UD con range, sia per numero contiene");
        } else if (chiave) {
            result = new Object[3];
            result[0] = registro;
            result[1] = anno;
            result[2] = numero;
        } else if (range) {
            result = new Object[5];
            result[0] = registro;
            if (anno_da != null && anno_a == null) {
                result[1] = anno_da;
                result[2] = new BigDecimal(GregorianCalendar.getInstance().get(Calendar.YEAR));
            } else if (anno_da == null && anno_a != null) {
                result[1] = new BigDecimal(2000);
                result[2] = anno_a;
            } else {
                result[1] = anno_da;
                result[2] = anno_a;
            }

            if (numero_da != null && numero_a == null) {
                result[3] = numero_da;
                result[4] = "zzzzzzzzzzzz";
            } else if (numero_da == null && numero_a != null) {
                result[3] = "000000000000";
                result[4] = numero_a;
            } else {
                result[3] = numero_da;
                result[4] = numero_a;
            }

            if ((result[1] != null || result[2] != null)
                    && ((BigDecimal) result[1]).compareTo((BigDecimal) result[2]) > 0) {
                getMessageBox().addError("Range di chiavi unità documentaria: Anno Da maggiore di Anno A");
            }
            if ((result[3] != null || result[4] != null) && ((String) result[3]).compareTo((String) result[4]) > 0) {
                getMessageBox().addError("Range di chiavi unità documentaria: Numero Da maggiore di Numero A");
            }
        } else if (num) {
            result = new Object[1];
            result[0] = numero_contiene;
        }
        return result;
    }

    public Object[] validaChiaviUnitaDocRicUd(String registro, BigDecimal anno, String numero, BigDecimal anno_da,
            BigDecimal anno_a, String numero_da, String numero_a, String numero_contiene) {
        String[] registroArr = new String[1];
        registroArr[0] = registro;
        return validaChiaviUnitaDocRicUd(registroArr, anno, numero, anno_da, anno_a, numero_da, numero_a,
                numero_contiene);
    }

    public void controllaFiltriCollegamenti(String collegamento, String collegamentoRisolto, String descrCollegamento,
            BigDecimal registro, BigDecimal anno, String numero, String isOggettoCollegamento,
            String descrCollegamentoOggetto) {

        // Controllo sul filtro "Collegamento risolto"
        if ((collegamento == null && collegamentoRisolto != null)
                || (collegamento != null && collegamento.equals("0") && collegamentoRisolto != null)) {
            getMessageBox().addError("Campo collegamento risolto non valorizzabile!</br>");
        }

        // Controllo sul filtro "Descrizione collegamento"
        if ((collegamento == null && descrCollegamento != null)
                || (collegamento != null && collegamento.equals("0") && descrCollegamento != null)) {
            getMessageBox().addError("Campo descrizione collegamento non valorizzabile!</br>");
        }

        // Controllo sul filtro "Descrizione collegamento oggetto"
        if ((isOggettoCollegamento == null && descrCollegamentoOggetto != null) || (isOggettoCollegamento != null
                && isOggettoCollegamento.equals("0") && descrCollegamentoOggetto != null)) {
            getMessageBox().addError("Campo descrizione collegamento oggetto non valorizzabile!</br>");
        }

        // Controllo sul filtro "Registro"
        if ((collegamento == null && registro != null)
                || collegamento != null && !collegamento.equals("1") && registro != null) {
            getMessageBox().addError("Campo Registro non valorizzabile!</br>");
        }

        // Controllo sul filtro "Anno"
        if ((collegamento == null && anno != null)
                || collegamento != null && !collegamento.equals("1") && anno != null) {
            getMessageBox().addError("Campo Anno non valorizzabile!</br>");
        }

        // Controllo sul filtro "Numero"
        if ((collegamento == null && numero != null)
                || collegamento != null && !collegamento.equals("1") && numero != null && !numero.equals("")) {
            getMessageBox().addError("Campo Numero non valorizzabile!</br>");
        }
    }
}
