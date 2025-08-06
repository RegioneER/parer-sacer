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

import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.message.MessageBox;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Gilioli_P
 */
public class SerieValidator extends TypeValidator {

    public SerieValidator(MessageBox messageBox) {
	super(messageBox);
    }

    /**
     * Metodo di validazione degli attributi selezionati relativi alle regole di acquisizione file
     * (Dati Profilo, Dati Specifici Tipi Ud, Dati Specifici Tipi Doc). Viene controllato che i
     * numeri d'ordine siano numerici e diversi tra loro.
     *
     * @param listaAttributiSelezionati mappa chiave/valore
     *
     * @throws EMFError errore generico
     */
    public void validaAttributiRegoleAcquisizioneFile(
	    Map<String, Map<String, String>> listaAttributiSelezionati) throws EMFError {
	// Scorro la mappa degli attributi selezionati e controllo che siano validi
	Iterator it = listaAttributiSelezionati.entrySet().iterator();
	Set<Integer> numeriOrdine = new HashSet<>();
	int contaCampi = 0;
	while (it.hasNext()) {
	    Map.Entry entry = (Map.Entry) it.next();
	    // Recupero la mappa contente la chiave (il nome del campo del dato specifico)
	    // con il relativo valore (concatenazione dei campi Tipo trasformazione | numero
	    // d'ordine |
	    // idAttribDatoSpec)
	    Map<String, String> datoSpecValore = (Map<String, String>) entry.getValue();
	    Iterator it2 = datoSpecValore.entrySet().iterator();
	    while (it2.hasNext()) {
		Map.Entry entry2 = (Map.Entry) it2.next();
		String valore = (String) entry2.getValue();
		// Splitto la stringa
		String[] parti = StringUtils.split(valore, "|");
		if (StringUtils.isNumeric(parti[1])) {
		    numeriOrdine.add(Integer.parseInt(parti[1]));
		} else {
		    getMessageBox().addError(
			    "Attenzione: uno dei campi selezionati presenta un numero d'ordine formalmente non corretto");
		    break;
		}
	    }
	}
	if (!getMessageBox().hasError()) {
	    if (numeriOrdine.size() != contaCampi) {
		getMessageBox().addError(
			"Attenzione: uno o più campi presentano lo stesso numero d'ordine");
	    }
	}
    }

    // public void validaSceltaPeriodoGiornoVersamento(String periodo, Date data) {
    // if (periodo != null && data != null) {
    // getMessageBox().addMessage(new Message(MessageLevel.ERR, "Filtri periodo e data versamento
    // entrambi
    // valorizzati!"));
    // } else if (periodo == null && data == null) {
    // getMessageBox().addMessage(new Message(MessageLevel.ERR, "Attenzione: è necessario
    // valorizzare almeno uno tra i
    // campi periodo e giorno versamento"));
    // }
    // }
    //
    // public void validaSceltaPeriodoGiornoVersamento(String periodo, Date giorno_vers_da,
    // BigDecimal ore_vers_da,
    // BigDecimal minuti_vers_da, Date giorno_vers_a, BigDecimal ore_vers_a, BigDecimal
    // minuti_vers_a) {
    // if (periodo != null
    // && (giorno_vers_da != null || ore_vers_da != null || minuti_vers_da != null || giorno_vers_a
    // != null ||
    // ore_vers_a != null || minuti_vers_a != null)) {
    // getMessageBox().addMessage(new Message(MessageLevel.ERR, "Campi periodo e data versamento
    // entrambi
    // valorizzati!"));
    // } else if (periodo == null && giorno_vers_da == null && ore_vers_da == null && minuti_vers_da
    // == null &&
    // giorno_vers_a == null && ore_vers_a == null && minuti_vers_a == null) {
    // getMessageBox().addMessage(new Message(MessageLevel.ERR, "Attenzione: è necessario
    // valorizzare almeno uno tra i
    // campi periodo e giorno versamento"));
    // }
    // }
    //
    // public void validaFlagVerificatoNonRisolubile(String verificato, String nonRisolubile) {
    // if (verificato.equals("0") && nonRisolubile.equals("1")) {
    // getMessageBox().addMessage(new Message(MessageLevel.ERR, "Una sessione può essere definita
    // non risolubile o
    // risolubile solo se è stata verificata"));
    // }
    // }
    //
    // public void validaDateCalcoloContenutoSacer(Date dataRifDa, Date dataRifA) {
    // Calendar dataInizio = Calendar.getInstance();
    // dataInizio.set(Calendar.YEAR, 2000);
    // dataInizio.set(Calendar.MONTH, Calendar.JANUARY);
    // dataInizio.set(Calendar.DATE, 1);
    // dataInizio.set(Calendar.HOUR_OF_DAY, 0);
    // dataInizio.set(Calendar.MINUTE, 0);
    // dataInizio.set(Calendar.SECOND, 0);
    // dataInizio.set(Calendar.MILLISECOND, 0);
    // if (dataRifDa.after(dataRifA)) {
    // getMessageBox().addMessage(new Message(MessageLevel.ERR, "Data riferimento Da maggiore di
    // Data riferimento A"));
    // }
    // if (dataRifDa.before(dataInizio.getTime())) {
    // getMessageBox().addMessage(new Message(MessageLevel.ERR, "Data riferimento Da inferiore al 1
    // gennaio 2000"));
    // }
    // if (dataRifA.after(new Date())) {
    // getMessageBox().addMessage(new Message(MessageLevel.ERR, "Data riferimento A successiva alla
    // data corrente"));
    // }
    // }
}
