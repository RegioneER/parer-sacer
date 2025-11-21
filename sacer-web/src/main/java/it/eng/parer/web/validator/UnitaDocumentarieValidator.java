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

    private static final int DEFAULT_START_YEAR = 2000;
    private static final String MAX_NUMERO_RANGE = "zzzzzzzzzzzz";
    private static final String MIN_NUMERO_RANGE = "000000000000";

    public UnitaDocumentarieValidator(MessageBox messageBox) {
        super(messageBox);
    }

    /**
     * Metodo di validazione: verifica che siano popolati il campo tiScadChiusVolume oppure i campi
     * niTempoScadChius e tiTempoScadChius nella form di creazione/modifica criteri di
     * raggruppamento o ricerca unità documentarie
     *
     * @param tiScadChiusVolume tipologia scadenza volume
     * @param niTempoScadChius  numero tempo scadenza
     * @param tiTempoScadChius  tipologia tempo scadenza
     */
    public void validaTipoScadenza(String tiScadChiusVolume, BigDecimal niTempoScadChius,
            String tiTempoScadChius) {
        if (tiScadChiusVolume == null && (niTempoScadChius == null || tiTempoScadChius == null)) {
            getMessageBox().addMessage(new Message(Message.MessageLevel.ERR,
                    "E’ necessario indicare la scadenza di chiusura. </br>"));
        } else if ((tiScadChiusVolume != null)
                && ((niTempoScadChius != null) || (tiTempoScadChius != null))) {
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
     * @param definitoDaList lista elementi di tipo {@link DefinitoDaBean}
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
                getMessageBox().addError(
                        "Errore Filtri Dati Specifici: inserito un valore con operatore NULLO o NON_NULLO");
            }
        } else if (StringUtils.isBlank(tiOper) && StringUtils.isNotBlank(dlValore)) {
            // controllo se operatore non è stato settato ed è stato settato il valore
            getMessageBox()
                    .addError("Errore Filtri Dati Specifici: inserito un valore senza operatore");
        } else if (StringUtils.isNotBlank(tiOper) && StringUtils.isBlank(dlValore)) {
            // controllo se valore non è stato settato ed è stato settato l'operatore
            if (!tiOper.equals(CostantiDB.TipoOperatoreDatiSpec.NULLO.name())
                    && !tiOper.equals(CostantiDB.TipoOperatoreDatiSpec.NON_NULLO.name())) {
                getMessageBox().addError(
                        "Errore Filtri Dati Specifici: inserito un operatore senza valore");
            }
        }
    }

    public Object[] validaChiaviUnitaDoc(String[] registro, BigDecimal anno, String numero,
            BigDecimal anno_da, BigDecimal anno_a, String numero_da, String numero_a) {
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
                result[1] = new BigDecimal(DEFAULT_START_YEAR);
                result[2] = anno_a;
            } else {
                result[1] = anno_da;
                result[2] = anno_a;
            }

            if (numero_da != null && numero_a == null) {
                result[3] = numero_da;
                result[4] = MAX_NUMERO_RANGE;
            } else if (numero_da == null && numero_a != null) {
                result[3] = MIN_NUMERO_RANGE;
                result[4] = numero_a;
            } else {
                result[3] = numero_da;
                result[4] = numero_a;
            }

            if ((result[1] != null || result[2] != null)
                    && ((BigDecimal) result[1]).compareTo((BigDecimal) result[2]) > 0) {
                getMessageBox()
                        .addError("Range di chiavi unità documentaria: Anno Da maggiore di Anno A");
            }
            if ((result[3] != null || result[4] != null)
                    && ((String) result[3]).compareTo((String) result[4]) > 0) {
                getMessageBox().addError(
                        "Range di chiavi unità documentaria: Numero Da maggiore di Numero A");
            }
        }
        return result;
    }

    public Object[] validaChiaviUnitaDoc(String registro, BigDecimal anno, String numero,
            BigDecimal anno_da, BigDecimal anno_a, String numero_da, String numero_a) {
        String[] registroArr = new String[1];
        registroArr[0] = registro;
        return validaChiaviUnitaDoc(registroArr, anno, numero, anno_da, anno_a, numero_da,
                numero_a);
    }

    public void controllaPresenzaAnno(BigDecimal anno, BigDecimal annoDa, BigDecimal annoA) {
        if (anno == null && annoDa == null && annoA == null) {
            getMessageBox().addMessage(new Message(Message.MessageLevel.ERR,
                    "Almeno uno dei filtri relativo all'anno o al range di anni di chiave unità documentaria deve essere valorizzato</br>"));
        }
    }

    public Object[] validaChiaviUnitaDocRicUd(String[] registro, BigDecimal anno, String numero,
            BigDecimal annoDa, BigDecimal annoA, String numeroDa, String numeroA,
            String numeroContiene) {

        // --- 1. Validazioni preliminari di mutua esclusività ---

        // REGOLA CHIAVE: L'utente non può compilare sia il campo singolo che i campi di range.
        if (anno != null && (annoDa != null || annoA != null)) {
            getMessageBox().addError(
                    "Sono stati inseriti valori sia nella ricerca per Anno singolo, sia per Anno con range.");
            return null;
        }

        int numeroFiltersCount = 0;
        if (numero != null)
            numeroFiltersCount++;
        if (numeroDa != null || numeroA != null)
            numeroFiltersCount++;
        if (numeroContiene != null)
            numeroFiltersCount++;

        if (numeroFiltersCount > 1) {
            getMessageBox().addError(
                    "I filtri 'Numero UD', 'Range di Numero UD' e 'Numero UD Contiene' sono mutuamente esclusivi.");
            return null;
        }

        // --- 2. Analisi del tipo di ricerca inserita dall'utente ---

        boolean isRangeSearch = (annoDa != null || annoA != null || numeroDa != null
                || numeroA != null);
        boolean isSingleSearch = (anno != null || numero != null || numeroContiene != null);

        if (isRangeSearch) {
            // --- CASO A: L'utente sta facendo una ricerca per RANGE ---
            // Qui si applica la logica di default e si restituisce l'array per ripopolare i campi.

            BigDecimal finalAnnoDa = annoDa;
            BigDecimal finalAnnoA = annoA;
            String finalNumeroDa = numeroDa;
            String finalNumeroA = numeroA;

            // Logica di default per l'anno (SOLO se è un range di anni)
            if (annoDa != null || annoA != null) {
                if (annoDa != null && annoA == null) {
                    finalAnnoA = new BigDecimal(Calendar.getInstance().get(Calendar.YEAR));
                } else if (annoDa == null && annoA != null) {
                    finalAnnoDa = new BigDecimal(DEFAULT_START_YEAR);
                }
            }

            // Logica di default per il numero (SOLO se è un range di numeri)
            if (numeroDa != null || numeroA != null) {
                if (numeroDa != null && numeroA == null) {
                    finalNumeroA = MAX_NUMERO_RANGE;
                } else if (numeroDa == null && numeroA != null) {
                    finalNumeroDa = MIN_NUMERO_RANGE;
                }
            }

            // Validazione Da > A
            if (finalAnnoDa != null && finalAnnoA != null
                    && finalAnnoDa.compareTo(finalAnnoA) > 0) {
                getMessageBox()
                        .addError("Range Anno: 'Anno Da' non può essere maggiore di 'Anno A'");
                return null;
            }
            if (finalNumeroDa != null && finalNumeroA != null
                    && finalNumeroDa.compareTo(finalNumeroA) > 0) {
                getMessageBox().addError(
                        "Range Numero: 'Numero Da' non può essere maggiore di 'Numero A'");
                return null;
            }

            // Costruisci e restituisci l'array a 5 elementi per la Action
            Object[] result = new Object[5];
            result[0] = registro;
            result[1] = finalAnnoDa;
            result[2] = finalAnnoA;
            result[3] = finalNumeroDa;
            result[4] = finalNumeroA;
            return result;

        } else if (isSingleSearch) {
            // --- CASO B: L'utente sta facendo una ricerca per VALORE SINGOLO ---
            // La validazione è passata, ma non dobbiamo fare nulla.
            // Restituendo null, il blocco `if (chiavi != null && chiavi.length == 5)` nella Action
            // NON verrà eseguito, e i campi di range non verranno erroneamente popolati.
            return null;

        } else {
            // --- CASO C: Nessun filtro inserito ---
            return null;
        }
    }

    public Object[] validaChiaviUnitaDocRicUdDatiSpec(String[] registro, BigDecimal anno_da,
            BigDecimal anno_a, String numero_da, String numero_a) {
        boolean chiave = false;
        boolean range = false;
        boolean num = false;
        Object[] result = new Object[5];
        result[0] = registro;
        // Valori di default
        result[1] = new BigDecimal(DEFAULT_START_YEAR);
        result[2] = new BigDecimal(GregorianCalendar.getInstance().get(Calendar.YEAR));

        // Sovrascrivi i valori di default se anno_da e/o anno_a sono presenti
        if (anno_da != null) {
            result[1] = anno_da;
        }
        if (anno_a != null) {
            result[2] = anno_a;
        }

        if (numero_da != null && numero_a == null) {
            result[3] = numero_da;
            result[4] = MAX_NUMERO_RANGE;
        } else if (numero_da == null && numero_a != null) {
            result[3] = MIN_NUMERO_RANGE;
            result[4] = numero_a;
        } else {
            result[3] = numero_da;
            result[4] = numero_a;
        }

        if ((result[1] != null || result[2] != null)
                && ((BigDecimal) result[1]).compareTo((BigDecimal) result[2]) > 0) {
            getMessageBox()
                    .addError("Range di chiavi unità documentaria: Anno Da maggiore di Anno A");
        }
        if ((result[3] != null || result[4] != null)
                && ((String) result[3]).compareTo((String) result[4]) > 0) {
            getMessageBox()
                    .addError("Range di chiavi unità documentaria: Numero Da maggiore di Numero A");
        }

        return result;
    }

    public Object[] validaChiaviUnitaDocRicUd(String registro, BigDecimal anno, String numero,
            BigDecimal anno_da, BigDecimal anno_a, String numero_da, String numero_a,
            String numero_contiene) {
        String[] registroArr = new String[1];
        registroArr[0] = registro;
        return validaChiaviUnitaDocRicUd(registroArr, anno, numero, anno_da, anno_a, numero_da,
                numero_a, numero_contiene);
    }

    public void controllaFiltriCollegamenti(String collegamento, String collegamentoRisolto,
            String descrCollegamento, BigDecimal registro, BigDecimal anno, String numero,
            String isOggettoCollegamento, String descrCollegamentoOggetto) {

        // Controllo sul filtro "Collegamento risolto"
        if ((collegamento == null && collegamentoRisolto != null) || (collegamento != null
                && collegamento.equals("0") && collegamentoRisolto != null)) {
            getMessageBox().addError("Campo collegamento risolto non valorizzabile!</br>");
        }

        // Controllo sul filtro "Descrizione collegamento"
        if ((collegamento == null && descrCollegamento != null) || (collegamento != null
                && collegamento.equals("0") && descrCollegamento != null)) {
            getMessageBox().addError("Campo descrizione collegamento non valorizzabile!</br>");
        }

        // Controllo sul filtro "Descrizione collegamento oggetto"
        if ((isOggettoCollegamento == null && descrCollegamentoOggetto != null)
                || (isOggettoCollegamento != null && isOggettoCollegamento.equals("0")
                        && descrCollegamentoOggetto != null)) {
            getMessageBox()
                    .addError("Campo descrizione collegamento oggetto non valorizzabile!</br>");
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
        if ((collegamento == null && numero != null) || collegamento != null
                && !collegamento.equals("1") && numero != null && !numero.equals("")) {
            getMessageBox().addError("Campo Numero non valorizzabile!</br>");
        }
    }
}
