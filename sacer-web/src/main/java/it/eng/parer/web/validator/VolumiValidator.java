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

import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.Message.MessageLevel;
import it.eng.spagoLite.message.MessageBox;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 *
 * @author Gilioli_P
 */
public class VolumiValidator extends TypeValidator {

    public VolumiValidator(MessageBox messageBox) {
        super(messageBox);
    }

    // /**
    // * Metodo di validazione della chiave unità documentaria nei filtri di
    // * ricerca volume
    // *
    // * @param registro
    // * @param anno
    // * @param codice
    // * @throws EMFError
    // */
    // public void validateChiaveDocVolumi(String registro, BigDecimal anno, String codice) throws
    // EMFError {
    // if (registro == null && (anno != null || codice != null)) {
    // getMessageBox().addMessage(new Message(MessageLevel.ERR, "Registro non impostato"));
    // }
    // if (registro != null) {
    // if (anno == null && codice != null) {
    // getMessageBox().addMessage(new Message(MessageLevel.ERR, "Devono essere impostati il registro
    // e l'anno prima del
    // numero"));
    // }
    // }
    // }
    /**
     * Metodo di validazione delle date inserite nei filtri di ricerca volumi
     *
     * @param data_da data da
     * @param data_a  data a
     *
     * @throws EMFError errore generico
     */
    public void validateDataVolumi(Date data_da, Date data_a) throws EMFError {
        if (data_a != null && data_da == null) {
            getMessageBox().addMessage(new Message(MessageLevel.ERR, "Data di inizio assente"));
        }
        if (data_a == null && data_da != null) {
            data_a = new Date();
            if (data_a.before(data_da)) {
                data_a = null;
                getMessageBox().addMessage(new Message(MessageLevel.ERR,
                        "Data di inizio superiore alla data odierna"));
            }
        }
        if (data_da != null && data_a != null) {
            if (data_da.after(data_a)) {
                getMessageBox().addMessage(
                        new Message(MessageLevel.ERR, "Data di inizio superiore a data fine"));
            }
        }
    }

    /**
     * Metodo di validazione dei campi (singoli e range) relativi alla chiave unità documentaria
     * secondo le regole presenti in analisi
     *
     * @param registro  valore registro
     * @param anno      valore anno
     * @param numero    valore numero
     * @param anno_da   anno da
     * @param anno_a    anno a
     * @param numero_da numero da
     * @param numero_a  numero a
     *
     * @return litsa elementi di Object[] con chiave validata
     */
    public Object[] validaChiaveUnitaDocVolumi(String registro, BigDecimal anno, String numero,
            BigDecimal anno_da, BigDecimal anno_a, String numero_da, String numero_a) {
        boolean chiave = false;
        boolean range = false;
        Object[] result = null;

        /*
         * Controllo innanzitutto che non siano stati inseriti i filtri sia sulla chiave UD singola
         * sia sulla chiave UD per range
         */
        if (anno != null || numero != null) {
            chiave = true;
        }
        if (anno_da != null || anno_a != null || numero_da != null || numero_a != null) {
            range = true;
        }
        if (chiave && range) {
            getMessageBox().addError(
                    "Sono stati inseriti valori sia nella ricerca per anno/numero singolo, sia per anno/numero con range");
        } else {
            /* Se i primi controlli sono andati a buon fine */
            /* SINGOLI CAMPI */
            if (chiave || registro != null) {
                // Ricavo i campi
                result = new Object[3];
                result[0] = registro;
                result[1] = anno;
                result[2] = numero;
                // Effettuo i controlli
                if (registro == null && (anno != null || numero != null)) {
                    getMessageBox()
                            .addMessage(new Message(MessageLevel.ERR, "Registro non impostato"));
                }
                if (registro != null) {
                    if (anno == null && numero != null) {
                        getMessageBox().addMessage(new Message(MessageLevel.ERR,
                                "Devono essere impostati il registro e l'anno prima del numero"));
                    }
                }
            } /* RANGE */ else if (range) {
                // Ricavo i campi
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

                // Effettuo i controlli
                if (registro == null && (result[3] != null || result[4] != null || result[1] != null
                        || result[2] != null)) {
                    getMessageBox()
                            .addMessage(new Message(MessageLevel.ERR, "Registro non impostato"));
                }
                if (registro != null) {
                    if ((result[1] == null && (result[3] != null || result[4] != null))
                            || (result[2] == null && (result[3] != null || result[4] != null))) {
                        getMessageBox().addMessage(new Message(MessageLevel.ERR,
                                "Devono essere impostati il registro e l'anno prima del numero"));
                    }
                }

                if ((result[1] != null || result[2] != null)
                        && ((BigDecimal) result[1]).compareTo((BigDecimal) result[2]) > 0) {
                    getMessageBox().addError(
                            "Range di chiavi unità documentaria: Anno Da maggiore di Anno A");
                }
                if ((result[3] != null || result[4] != null)
                        && ((String) result[3]).compareTo((String) result[4]) > 0) {
                    getMessageBox().addError(
                            "Range di chiavi unità documentaria: Numero Da maggiore di Numero A");
                }
            }
        }
        return result;
    }

    /**
     * Metodo di validazione che controlla che il filtro relativo allo stato elenco contiene
     * IN_CODA_JMS_GENERA_INDICE_AIP o IN_CODA_JMS_INDICE_AIP_DA_ELAB, se non sono definiti filtri
     * (singoli e range) relativi alla chiave unità documentaria, oppure se è compilato il filtro
     * “Numero ore in coda JMS”
     *
     * @param chiavi          lista chiavi
     * @param numOreInCodaJMS numero ore in coda
     * @param tiStatoElenco   tipo stato elenco
     *
     * @throws EMFError errore generico
     */
    public void validateCodaJMS(Object[] chiavi, BigDecimal numOreInCodaJMS,
            List<String> tiStatoElenco) throws EMFError {
        if ((tiStatoElenco
                .contains(ElencoEnums.ElencoStatusEnum.IN_CODA_JMS_GENERA_INDICE_AIP.name())
                || tiStatoElenco.contains(
                        ElencoEnums.ElencoStatusEnum.IN_CODA_JMS_INDICE_AIP_DA_ELAB.name()))
                && chiavi != null) {
            getMessageBox().addMessage(new Message(MessageLevel.ERR,
                    "L'uso degli stati IN_CODA_JMS_GENERA_INDICE_AIP o IN_CODA_JMS_INDICE_AIP_DA_ELAB "
                            + "implica che non si usino filtri relativi alle unità documentarie contenute negli elenchi"));
        }
        if (numOreInCodaJMS != null
                && !tiStatoElenco
                        .contains(ElencoEnums.ElencoStatusEnum.IN_CODA_JMS_GENERA_INDICE_AIP.name())
                && !tiStatoElenco.contains(
                        ElencoEnums.ElencoStatusEnum.IN_CODA_JMS_INDICE_AIP_DA_ELAB.name())) {
            getMessageBox().addMessage(new Message(MessageLevel.ERR,
                    "Il filtro “Numero ore in coda JMS” può essere usato solo se il filtro relativo allo stato "
                            + "è compilato con gli stati IN_CODA_JMS_GENERA_INDICE_AIP o IN_CODA_JMS_INDICE_AIP_DA_ELAB"));
        }
    }
}
