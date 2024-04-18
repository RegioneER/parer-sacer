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

import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.message.MessageBox;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Iacolucci_M
 */
public class MonitoraggioFascicoliValidator extends TypeValidator {

    public MonitoraggioFascicoliValidator(MessageBox messageBox) {
        super(messageBox);
    }

    /*
     * public void validaSceltaPeriodoGiornoVersamento(String periodo, Date giorno_vers_da, BigDecimal ore_vers_da,
     * BigDecimal minuti_vers_da, Date giorno_vers_a, BigDecimal ore_vers_a, BigDecimal minuti_vers_a) { if (periodo !=
     * null && (giorno_vers_da != null || ore_vers_da != null || minuti_vers_da != null || giorno_vers_a != null ||
     * ore_vers_a != null || minuti_vers_a != null)) { getMessageBox().addMessage(new Message(MessageLevel.ERR,
     * "Campi periodo e data versamento entrambi valorizzati!")); } else if (periodo == null && giorno_vers_da == null
     * && ore_vers_da == null && minuti_vers_da == null && giorno_vers_a == null && ore_vers_a == null && minuti_vers_a
     * == null) { getMessageBox().addMessage(new Message(MessageLevel.ERR,
     * "Attenzione: è necessario valorizzare almeno uno tra i campi periodo e giorno versamento")); } }
     */

    /*
     * Controlla che venga inserito un anno oppure un range di anni. In caso di range controlla che l'anno di inizio non
     * sia superiore all'anno di fine. Se non si inserisce l'anno iniziale viene impostato a 2000. Se non si inserisce
     * il numero finale viene impostato all'anno attuale. Se si inserisce un anno inferiore a 2000 viene emesso un
     * messaggio di errore. Se si inserisce un anno superiore all'anno attuale viene emesso un messaggio di errore.
     * Valori di ritorno: NULL: Nel caso non sia stato inserito nulla o venga rilevato un qualsiasi errore. Array con 1
     * elemento: Nel caso in cui sia stato inserito il solo numero Array con due elementi: Nel caso in cui siano stati
     * inseriti i range corretti di numeri.
     */
    /*
     * public BigDecimal[] controllaCoerenzaFiltroRangeAnno(BigDecimal anno, BigDecimal annoDa, BigDecimal annoA, String
     * nomeEntita) { BigDecimal[] arr=null; if (anno!=null) { if (annoDa!=null || annoA!=null) {
     * getMessageBox().addMessage(new Message(Message.MessageLevel.ERR,
     * "Inserire l'anno oppure il range di anni di '"+nomeEntita+"'</br>")); } else { arr=new BigDecimal[1];
     * arr[0]=anno; } } else { if (annoDa!=null || annoA!=null) { if (annoDa!=null && annoA!=null) { if
     * (annoA.compareTo(annoDa)>-1) { arr=new BigDecimal[2]; arr[0]=annoDa; arr[1]=annoA; } else {
     * getMessageBox().addMessage(new Message(Message.MessageLevel.ERR,
     * "L'anno di inizio è superiore all'anno di fine di '"+nomeEntita+"'</br>")); } } else { BigDecimal annoOggi=new
     * BigDecimal(GregorianCalendar.getInstance().get(Calendar.YEAR)); if (annoDa!=null) { if
     * (annoDa.compareTo(annoOggi)>0) { getMessageBox().addMessage(new Message(Message.MessageLevel.ERR,
     * "L'anno di inizio di '"+nomeEntita+"' è superiore all'anno attuale</br>")); } else { arr=new BigDecimal[2];
     * arr[0]=annoDa; arr[1]=annoOggi; } } else { BigDecimal anno2000=new BigDecimal(2000); if
     * (annoA.compareTo(anno2000)<0) { getMessageBox().addMessage(new Message(Message.MessageLevel.ERR,
     * "L'anno di fine di '"+nomeEntita+"' è inferiore all'anno 2000</br>")); } else { arr=new BigDecimal[2];
     * arr[0]=anno2000; arr[1]=annoA; } } } } } return arr; }
     */
    /*
     * Controlla che venga inserito un numero oppure un range di numeri. In caso di range controlla che il numero di
     * inizio non sia superiore al numero di fine. Se non si inserisce il numero iniziale viene impostato a
     * '0000000000000' (zeri). Se non si inserisce il numero finale viene impostato a 'zzzzzzzzzzzzz'. Valori di
     * ritorno: NULL: Nel caso non sia stato inserito nulla o venga rilevato un qualsiasi errore. Array con 1 elemento:
     * Nel caso in cui sia stato inserito il solo numero Array con due elementi: Nel caso in cui siano stati inseriti i
     * range corretti di numeri.
     */
    /*
     * public String[] controllaCoerenzaFiltroRangeNumero(String numero, String numeroDa, String numeroA, String
     * nomeEntita) { String[] arr=null; if (!numero.equals("")) { if (!numeroDa.equals("") || !numeroA.equals("")) {
     * getMessageBox().addMessage(new Message(Message.MessageLevel.ERR,
     * "Inserire il numero oppure il range di numeri di '"+nomeEntita+"'</br>")); } else { arr=new String[1];
     * arr[0]=numero; } } else { if (!numeroDa.equals("") || !numeroA.equals("")) { if (!numeroDa.equals("") &&
     * !numeroA.equals("")) { if (numeroA.compareTo(numeroDa)>-1) { arr=new String[2]; arr[0]=numeroDa; arr[1]=numeroA;
     * } else { getMessageBox().addMessage(new Message(Message.MessageLevel.ERR,
     * "Il numero di inizio è superiore al numero di fine di '"+nomeEntita+"'</br>")); } } else { arr=new String[2]; if
     * (!numeroDa.equals("")) { arr[0]=numeroDa; arr[1]="zzzzzzzzzzzzz"; } else { arr[0]="0000000000000";
     * arr[1]=numeroA; } } } } return arr; }
     */

    public Date[] validaDate(Date data_da, BigDecimal ore_da, BigDecimal minuti_da, Date data_a, BigDecimal ore_a,
            BigDecimal minuti_a, String nm_data_da, String nm_data_a) throws EMFError {
        Date[] dateValidate = super.validaDate(data_da, ore_da, minuti_da, data_a, ore_a, minuti_a, nm_data_da,
                nm_data_a);

        if (dateValidate != null) {
            Calendar dataDa = Calendar.getInstance();
            dataDa.setTime(dateValidate[0]);
            dataDa.set(Calendar.MILLISECOND, 0);
            dateValidate[0] = dataDa.getTime();
            Calendar dataA = Calendar.getInstance();
            dataA.setTime(dateValidate[1]);
            dataA.set(Calendar.MILLISECOND, 999);
            dateValidate[1] = dataA.getTime();
        }
        // Ritorno le date validate
        return dateValidate;
    }

}
