/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.web.validator;

import it.eng.parer.job.utils.JobConstants;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.form.fields.impl.Input;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.MessageBox;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TypeValidator {

    MessageBox messageBox = null;

    public TypeValidator(MessageBox messageBox) {
        this.messageBox = messageBox;
    }

    public MessageBox getMessageBox() {
        return this.messageBox;
    }

    /**
     * Metodo di validazione delle date e degli orari inseriti nei filtri di ricerca
     *
     * @param data_da
     *            data di riferimento da
     * @param ore_da
     *            ore di riferimento da
     * @param minuti_da
     *            minuti di riferimento da
     * @param data_a
     *            data di riferimento a
     * @param ore_a
     *            ore di riferimento a
     * @param minuti_a
     *            minuti di riferimento a
     * @param nm_data_da
     *            descrizione campo data da
     * @param nm_data_a
     *            descrizione campo data a
     * 
     * @return dateValidate, un array di Date contenente le date da - a validate
     * 
     * @throws EMFError
     *             errore generico
     */
    public Date[] validaDate(Date data_da, BigDecimal ore_da, BigDecimal minuti_da, Date data_a, BigDecimal ore_a,
            BigDecimal minuti_a, String nm_data_da, String nm_data_a) throws EMFError {
        Date[] dateValidate = null;
        if (data_da != null || ore_da != null || minuti_da != null || data_a != null || ore_a != null
                || minuti_a != null) {
            dateValidate = new Date[2];
            // Verifico che i campi data, ora e minuti siano validi
            isDateValid(data_da, ore_da, minuti_da, nm_data_da);
            isDateValid(data_a, ore_a, minuti_a, nm_data_a);

            // Controllo che i campi degli orari e minuti siano validi (23 ore 59 minuti)
            isTimeValid(ore_da, minuti_da, "Ora da");
            isTimeValid(ore_a, minuti_a, "Ora a");

            // Mi serve un controllo per sapere se l'utente ha inserito la data corrente
            Calendar data_odierna = Calendar.getInstance();
            data_odierna.set(Calendar.HOUR_OF_DAY, 0);
            data_odierna.set(Calendar.MINUTE, 0);
            data_odierna.set(Calendar.SECOND, 0);
            data_odierna.set(Calendar.MILLISECOND, 0);
            Timestamp todayDay = new Timestamp(data_odierna.getTimeInMillis());
            boolean today = false;
            if (data_a == null || (data_a.getTime() == todayDay.getTime())) {
                today = true;
            }

            // Comincio ad inserire, in caso, i valori di default
            Calendar data_orario_da = Calendar.getInstance();
            Calendar data_orario_a = Calendar.getInstance();
            int ora_corrente_a = data_orario_a.get(Calendar.HOUR_OF_DAY);
            int minuto_corrente_a = data_orario_a.get(Calendar.MINUTE);

            if (data_da == null) {
                data_orario_da.set(Calendar.YEAR, 2000);
                data_orario_da.set(Calendar.MONTH, 0);
                data_orario_da.set(Calendar.DAY_OF_MONTH, 1);
            } else {
                data_orario_da.setTime(data_da);
            }

            if (ore_da == null || minuti_da == null) {
                data_orario_da.set(Calendar.HOUR_OF_DAY, 0);
                data_orario_da.set(Calendar.MINUTE, 0);
                data_orario_da.set(Calendar.SECOND, 0);
            } else {
                data_orario_da.set(Calendar.HOUR_OF_DAY, ore_da.intValue());
                data_orario_da.set(Calendar.MINUTE, minuti_da.intValue());
                data_orario_da.set(Calendar.SECOND, 0);
            }

            if (data_a != null) {
                data_orario_a.setTime(data_a);
            }

            if (ore_a != null && minuti_a != null) {
                data_orario_a.set(Calendar.HOUR_OF_DAY, ore_a.intValue());
                data_orario_a.set(Calendar.MINUTE, minuti_a.intValue());
                data_orario_a.set(Calendar.SECOND, 59);
            } else {
                if (today) {
                    data_orario_a.set(Calendar.HOUR_OF_DAY, ora_corrente_a);
                    data_orario_a.set(Calendar.MINUTE, minuto_corrente_a);
                } else {
                    data_orario_a.set(Calendar.HOUR_OF_DAY, 23);
                    data_orario_a.set(Calendar.MINUTE, 59);
                    data_orario_a.set(Calendar.SECOND, 59);
                }
            }

            // Controllo che l'ordine delle date ed orari sia corretto
            this.validaOrdineDateOrari(data_orario_da.getTime(), data_orario_a.getTime(), nm_data_da, nm_data_a);

            // Calendar[] dateValidate = new Calendar[2];
            dateValidate[0] = data_orario_da.getTime();
            dateValidate[1] = data_orario_a.getTime();
        }
        // Ritorno le date validate
        return dateValidate;
    }

    private void isDateValid(Date data, BigDecimal ore, BigDecimal minuti, String nomeCampo) {
        // Controllo se ho inserito tutte le cifre dell'orario
        if (ore == null) {
            // ora null e minuti presente
            if (minuti != null) {
                getMessageBox().addError("Orario " + nomeCampo + " non corretto: valore Ora assente");
            }
            // ora assente e minuti assente = OK, vengono impostati i valori di default (00:00)
        } else {
            // ora presente e minuti assente
            if (minuti == null) {
                getMessageBox().addError("Orario " + nomeCampo + " non corretto: valore Minuti assente");
            } // ora presente e minuti presente
            else {
                // data assente
                if (data == null) {
                    getMessageBox().addError(nomeCampo + " assente");
                }
                // se anche la data è presente va bene
            }
        }
    }

    public void isTimeValid(BigDecimal ore, BigDecimal minuti, String nomeCampo) {
        if ((ore != null && ore.intValue() > 23) || (minuti != null && minuti.intValue() > 59)) {
            getMessageBox().addError(nomeCampo + " non corretto");
        }
    }

    /**
     * Metodo di validazione delle date inserite nei filtri di ricerca e in fase di inserimento dati
     *
     * @param data_da
     *            data di riferimento da
     * @param data_a
     *            data di riferimento a
     * @param nm_data_da
     *            descrizione campo data da
     * @param nm_data_a
     *            descrizione campo data a
     * 
     * @throws EMFError
     *             errore generico
     */
    public void validaOrdineDateOrari(Date data_da, Date data_a, String nm_data_da, String nm_data_a) throws EMFError {
        if (data_a != null && data_da == null) {
            getMessageBox().addError(nm_data_da + " assente");
            // throw new EMFError(EMFError.WARNING, "Data inizio assente");
        }
        if (data_a == null && data_da != null) {
            data_a = new Date();
            if (data_a.before(data_da)) {
                data_a = null;
                getMessageBox().addError(nm_data_da + " superiore alla data odierna");
            }
        }
        if (data_da != null && data_a != null) {
            if (data_da.after(data_a)) {
                getMessageBox().addError(nm_data_da + " superiore a " + nm_data_a);
            }
        }
    }

    /**
     * Metodo di validazione delle dimensioni (size) inserite nei filtri di ricerca
     *
     * @param size_da
     *            dimensione di riferimento da
     * @param size_a
     *            dimensione di riferimento a
     * 
     * @throws EMFError
     *             errore generico
     */
    public void validaDimensioniKb(BigDecimal size_da, BigDecimal size_a) throws EMFError {
        if (size_da != null && size_a == null) {
            getMessageBox().addMessage(new Message(Message.MessageLevel.ERR, "Dimensione finale assente"));
        }
        if (size_da != null && size_a != null) {
            if (size_da.compareTo(size_a) > 0) {
                getMessageBox().addMessage(
                        new Message(Message.MessageLevel.ERR, "Dimensione iniziale superiore a dimensione finale"));
            }
        }
    }

    /**
     * Metodo di validazione struttura
     *
     * @param nmJob
     *            nome job
     * @param idAmbiente
     *            id ambiente
     * @param idStrut
     *            id struttura
     * @param idEnte
     *            id ente
     * 
     * @throws EMFError
     *             errore generico
     */
    public void validaStruttura(String nmJob, BigDecimal idAmbiente, BigDecimal idEnte, BigDecimal idStrut)
            throws EMFError {
        if (nmJob.equals(JobConstants.JobEnum.VERIFICA_VERS_FALLITI.name())
                || nmJob.equals(JobConstants.JobEnum.CALCOLA_CHIAVE_UD_DOC.name())) {
            if (idAmbiente == null) {
                getMessageBox().addMessage(new Message(Message.MessageLevel.ERR, "Ambiente non impostato"));
            } else if (idEnte == null) {
                getMessageBox().addMessage(new Message(Message.MessageLevel.ERR, "Ente non impostato"));
            } else if (idStrut == null) {
                getMessageBox().addMessage(new Message(Message.MessageLevel.ERR, "Struttura non impostata"));
            }
        }
    }

    public class ChiaveBean {
        private BigDecimal anno;
        private BigDecimal annoDa;
        private BigDecimal annoA;
        private String numero;
        private String numeroDa;
        private String numeroA;

        public ChiaveBean() {
        }

        public BigDecimal getAnno() {
            return anno;
        }

        public void setAnno(BigDecimal anno) {
            this.anno = anno;
        }

        public BigDecimal getAnnoDa() {
            return annoDa;
        }

        public void setAnnoDa(BigDecimal annoDa) {
            this.annoDa = annoDa;
        }

        public BigDecimal getAnnoA() {
            return annoA;
        }

        public void setAnnoA(BigDecimal annoA) {
            this.annoA = annoA;
        }

        public String getNumero() {
            return numero;
        }

        public void setNumero(String numero) {
            this.numero = numero;
        }

        public String getNumeroDa() {
            return numeroDa;
        }

        public void setNumeroDa(String numeroDa) {
            this.numeroDa = numeroDa;
        }

        public String getNumeroA() {
            return numeroA;
        }

        public void setNumeroA(String numeroA) {
            this.numeroA = numeroA;
        }

        public boolean isSingleValue() {
            return (anno != null || numero != null);
        }
    }

    private static final int annoDa_default = 2000;
    private static final String numeroDa_default = "000000000000";
    private static final String numeroA_default = "ZZZZZZZZZZZZ";

    public ChiaveBean validaChiavi(String type, Input<BigDecimal> input_anno, Input<BigDecimal> input_annoDa,
            Input<BigDecimal> input_annoA, Input<String> input_numero, Input<String> input_numeroDa,
            Input<String> input_numeroA) throws EMFError {
        ChiaveBean result = new ChiaveBean();
        boolean singleValue = false;
        BigDecimal anno = input_anno.parse();
        BigDecimal annoDa = input_annoDa.parse();
        BigDecimal annoA = input_annoA.parse();
        String numero = input_numero.parse();
        String numeroDa = input_numeroDa.parse();
        String numeroA = input_numeroA.parse();

        if (anno != null || numero != null) {
            singleValue = true;
        }

        if (singleValue && (annoDa != null || annoA != null || numeroDa != null || numeroA != null)) {
            getMessageBox().addError(
                    "Sono stati inseriti valori nella ricerca per chiave " + type + " sia singola che con range");
        } else if (singleValue) {
            result.setAnno(anno);
            result.setNumero(numero);
        } else {
            if (annoDa != null || annoA != null) {
                if (annoDa == null) {
                    annoDa = new BigDecimal(annoDa_default);
                    input_annoDa.setValue(annoDa.toString());
                } else if (annoA == null) {
                    annoA = new BigDecimal(GregorianCalendar.getInstance().get(Calendar.YEAR));
                    input_annoA.setValue(annoA.toString());
                }

                if (annoDa.compareTo(annoA) > 0) {
                    getMessageBox().addError("Range di chiavi " + type + ": " + input_annoDa.getHtmlDescription()
                            + " maggiore di " + input_annoA.getHtmlDescription());
                } else {
                    result.setAnnoDa(annoDa);
                    result.setAnnoA(annoA);
                }
            }

            if (numeroDa != null || numeroA != null) {
                if (numeroDa == null) {
                    numeroDa = numeroDa_default;
                    input_numeroDa.setValue(numeroDa);
                } else if (numeroA == null) {
                    numeroA = numeroA_default;
                    input_numeroA.setValue(numeroA);
                }

                if (numeroDa.compareTo(numeroA) > 0) {
                    getMessageBox().addError("Range di chiavi " + type + ": " + input_numeroDa.getHtmlDescription()
                            + " maggiore di " + input_numeroA.getHtmlDescription());
                } else {
                    result.setNumeroDa(numeroDa);
                    result.setNumeroA(numeroA);
                }
            }
        }
        return result;
    }
}
