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

import it.eng.parer.scarto.dto.FiltriRicercaUdScartoDto;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.MessageBox;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

public class ScartoValidator extends TypeValidator {

    private static final int DEFAULT_START_YEAR = 2000;
    private static final String MAX_NUMERO_RANGE = "zzzzzzzzzzzz";
    private static final String MIN_NUMERO_RANGE = "000000000000";

    public ScartoValidator(MessageBox messageBox) {
        super(messageBox);
    }

    // /**
    // * Valida la coerenza dei filtri di ricerca UD e applica i valori di default mancanti sui
    // range
    // * (Anno, Numero, Date).
    // *
    // * @param filtri Il DTO contenente i filtri inseriti dall'utente
    // * @return true se la validazione passa, false se ci sono errori bloccanti
    // */
    // public boolean validaChiaviUnitaDocRicUdScarto(FiltriRicercaUdScartoDto filtri) {
    //
    // boolean isValid = true;
    //
    // // ==========================================
    // // 1. CONTROLLI SULL'ANNO
    // // ==========================================
    // if (filtri.getAnno() == null && filtri.getAnnoDa() == null && filtri.getAnnoA() == null) {
    // getMessageBox().addError(
    // "Almeno uno dei filtri relativo all'anno o al range di anni della chiave unit&agrave;
    // documentaria deve essere valorizzato.</br>");
    // isValid = false;
    // }
    //
    // if (filtri.getAnno() != null && (filtri.getAnnoDa() != null || filtri.getAnnoA() != null)) {
    // getMessageBox().addError(
    // "Sono stati inseriti valori sia nella ricerca per Anno singolo, sia per range di
    // Anno.</br>");
    // isValid = false;
    // }
    //
    // if (filtri.getAnnoDa() != null && filtri.getAnnoA() != null
    // && filtri.getAnnoDa().compareTo(filtri.getAnnoA()) > 0) {
    // getMessageBox().addError(
    // "Range Anno: 'Anno Da' non pu&ograve; essere maggiore di 'Anno A'.</br>");
    // isValid = false;
    // }
    //
    // // ==========================================
    // // 2. CONTROLLI SUL NUMERO
    // // ==========================================
    // if (filtri.getNumeroUd() != null
    // && (filtri.getNumeroDa() != null || filtri.getNumeroA() != null)) {
    // getMessageBox().addError(
    // "Sono stati inseriti valori sia nella ricerca per Numero UD singolo, sia per range di Numero
    // UD.</br>");
    // isValid = false;
    // }
    //
    // // ==========================================
    // // 3. CONTROLLI E DEFAULTS SULLE DATE
    // // ==========================================
    // if (filtri.getDataUd() != null
    // && (filtri.getDataUdDa() != null || filtri.getDataUdA() != null)) {
    // getMessageBox().addError(
    // "Sono stati inseriti valori sia nella ricerca per Data singola, sia per range di
    // Date.</br>");
    // isValid = false;
    // }
    //
    // // Se l'utente non ha usato la data singola, controlliamo il range per applicare i default
    // if (filtri.getDataUd() == null) {
    //
    // // CASO A: Ha inserito "Da" ma manca "A" -> Mettiamo "A" = Data odierna
    // if (filtri.getDataUdDa() != null && filtri.getDataUdA() == null) {
    // // Imposta la data di oggi
    // filtri.setDataUdA(new Date());
    // }
    // // CASO B: Ha inserito "A" ma manca "Da" -> Mettiamo "Da" = 1 Gennaio 2000
    // else if (filtri.getDataUdA() != null && filtri.getDataUdDa() == null) {
    // Calendar cal = Calendar.getInstance();
    // cal.set(2000, Calendar.JANUARY, 1, 0, 0, 0); // 1 Gennaio 2000 00:00:00
    // cal.set(Calendar.MILLISECOND, 0);
    // filtri.setDataUdDa(cal.getTime());
    // }
    //
    // // CONTROLLO COERENZA DATE (Da <= A)
    // if (filtri.getDataUdDa() != null && filtri.getDataUdA() != null) {
    // if (filtri.getDataUdDa().after(filtri.getDataUdA())) {
    // getMessageBox().addError(
    // "Range Date: 'Data Da' non pu&ograve; essere successiva a 'Data A'.</br>");
    // isValid = false;
    // }
    // }
    // }
    //
    // return isValid;
    // }

    public boolean validaChiaviUnitaDocRicUdScarto(FiltriRicercaUdScartoDto filtri) {

        boolean isValid = true;

        // ==========================================
        // 1. CONTROLLI E DEFAULT SULL'ANNO
        // ==========================================
        if (filtri.getAnno() == null && filtri.getAnnoDa() == null && filtri.getAnnoA() == null) {
            getMessageBox().addError(
                    "Almeno uno dei filtri relativo all'anno o al range di anni della chiave unit&agrave; documentaria deve essere valorizzato.</br>");
            isValid = false;
        }

        if (filtri.getAnno() != null && (filtri.getAnnoDa() != null || filtri.getAnnoA() != null)) {
            getMessageBox().addError(
                    "Sono stati inseriti valori sia nella ricerca per Anno singolo, sia per range di Anno.</br>");
            isValid = false;
        }

        if (filtri.getAnnoDa() != null || filtri.getAnnoA() != null) {
            // Default Anno
            if (filtri.getAnnoDa() == null)
                filtri.setAnnoDa(BigDecimal.valueOf(DEFAULT_START_YEAR));
            if (filtri.getAnnoA() == null)
                filtri.setAnnoA(BigDecimal.valueOf(Calendar.getInstance().get(Calendar.YEAR)));

            // Validazione Coerenza
            if (filtri.getAnnoDa().compareTo(filtri.getAnnoA()) > 0) {
                getMessageBox().addError(
                        "Range Anno: 'Anno Da' non pu&ograve; essere maggiore di 'Anno A'.</br>");
                isValid = false;
            }
        }

        // ==========================================
        // 2. CONTROLLI E DEFAULT SUL NUMERO
        // ==========================================
        if (filtri.getNumeroUd() != null
                && (filtri.getNumeroDa() != null || filtri.getNumeroA() != null)) {
            getMessageBox().addError(
                    "Sono stati inseriti valori sia nella ricerca per Numero UD singolo, sia per range di Numero UD.</br>");
            isValid = false;
        }

        if (filtri.getNumeroDa() != null || filtri.getNumeroA() != null) {
            // Default Numero
            if (filtri.getNumeroDa() == null)
                filtri.setNumeroDa(MIN_NUMERO_RANGE);
            if (filtri.getNumeroA() == null)
                filtri.setNumeroA(MAX_NUMERO_RANGE);

            // Validazione Coerenza (Ordinamento alfabetico per stringhe)
            if (filtri.getNumeroDa().compareTo(filtri.getNumeroA()) > 0) {
                getMessageBox().addError(
                        "Range Numero: 'Numero Da' non pu&ograve; essere maggiore di 'Numero A'.</br>");
                isValid = false;
            }
        }

        // ==========================================
        // 3. CONTROLLI E DEFAULTS SULLE DATE
        // ==========================================
        if (filtri.getDataUd() != null
                && (filtri.getDataUdDa() != null || filtri.getDataUdA() != null)) {
            getMessageBox().addError(
                    "Sono stati inseriti valori sia nella ricerca per Data singola, sia per range di Date.</br>");
            isValid = false;
        }

        if (filtri.getDataUd() == null) {
            if (filtri.getDataUdDa() != null && filtri.getDataUdA() == null) {
                filtri.setDataUdA(new Date()); // Default: oggi
            } else if (filtri.getDataUdA() != null && filtri.getDataUdDa() == null) {
                Calendar cal = Calendar.getInstance();
                cal.set(2000, Calendar.JANUARY, 1, 0, 0, 0);
                cal.set(Calendar.MILLISECOND, 0);
                filtri.setDataUdDa(cal.getTime()); // Default: 01/01/2000
            }

            if (filtri.getDataUdDa() != null && filtri.getDataUdA() != null) {
                if (filtri.getDataUdDa().after(filtri.getDataUdA())) {
                    getMessageBox().addError(
                            "Range Date: 'Data Da' non pu&ograve; essere successiva a 'Data A'.</br>");
                    isValid = false;
                }
            }
        }

        return isValid;
    }

    public boolean validaRegistroTipoUdRicUdScarto(BigDecimal idRegistroUnitaDoc,
            BigDecimal idTipoUnitaDoc) {
        if (idRegistroUnitaDoc == null && idTipoUnitaDoc == null) {
            getMessageBox().addMessage(new Message(Message.MessageLevel.ERR,
                    "Almeno uno dei filtri relativi al registro o al tipo unità documentaria deve essere valorizzato</br>"));
            return false;
        }
        return true;

    }

    public void validaChiaviUnitaDocRicUdScarto(BigDecimal anno, String numero, BigDecimal annoDa,
            BigDecimal annoA, String numeroDa, String numeroA) {

        if (anno == null && annoDa == null && annoA == null) {
            getMessageBox().addMessage(new Message(Message.MessageLevel.ERR,
                    "Almeno uno dei filtri relativo all'anno o al range di anni di chiave unità documentaria deve essere valorizzato</br>"));
        }

        // L'utente non può compilare sia il campo singolo che i campi di range.
        if (anno != null && (annoDa != null || annoA != null)) {
            getMessageBox().addError(
                    "Sono stati inseriti valori sia nella ricerca per Anno singolo, sia per range di Anno.</br>");
        }

        int numeroFiltersCount = 0;
        if (numero != null)
            numeroFiltersCount++;
        if (numeroDa != null || numeroA != null)
            numeroFiltersCount++;

        if (numeroFiltersCount > 1) {
            getMessageBox().addError(
                    "Sono stati inseriti valori sia nella ricerca per Numero UD singolo, sia per range di Numero UD.</br>");
        }

        // --- 2. Analisi del tipo di ricerca inserita dall'utente ---
        boolean isRangeSearch = (annoDa != null || annoA != null || numeroDa != null
                || numeroA != null);

        if (isRangeSearch) {

            BigDecimal finalAnnoDa = annoDa;
            BigDecimal finalAnnoA = annoA;
            String finalNumeroDa = numeroDa;
            String finalNumeroA = numeroA;

            // Validazione Da > A
            if (finalAnnoDa != null && finalAnnoA != null
                    && finalAnnoDa.compareTo(finalAnnoA) > 0) {
                getMessageBox()
                        .addError("Range Anno: 'Anno Da' non può essere maggiore di 'Anno A'</br>");
            }
            if (finalNumeroDa != null && finalNumeroA != null
                    && finalNumeroDa.compareTo(finalNumeroA) > 0) {
                getMessageBox().addError(
                        "Range Numero: 'Numero Da' non può essere maggiore di 'Numero A'</br>");
            }

        }
    }

}
