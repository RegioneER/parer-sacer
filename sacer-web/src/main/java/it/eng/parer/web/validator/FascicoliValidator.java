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

import it.eng.parer.fascicoli.dto.RicercaFascicoliBean;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.form.fields.impl.Input;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.MessageBox;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 *
 *
 * @author Moretti_Lu
 */
public class FascicoliValidator extends TypeValidator {

    private final SimpleDateFormat formatDate = new SimpleDateFormat("dd/MM/yyyy");

    public FascicoliValidator(MessageBox messageBox) {
	super(messageBox);
    }

    public void validaChiaviFascicoli(RicercaFascicoliBean result, Input<BigDecimal> input_anno,
	    Input<BigDecimal> input_annoDa, Input<BigDecimal> input_annoA,
	    Input<String> input_numero, Input<String> input_numeroDa, Input<String> input_numeroA)
	    throws EMFError {
	final String type = "fascicolo";
	ChiaveBean chiaviValue = super.validaChiavi(type, input_anno, input_annoDa, input_annoA,
		input_numero, input_numeroDa, input_numeroA);

	if (chiaviValue != null) {
	    if (chiaviValue.getAnno() == null
		    && (chiaviValue.getAnnoDa() == null || chiaviValue.getAnnoA() == null)) {
		getMessageBox().addError(
			"Almeno uno dei filtri relativo all'anno o al range di anni di chiave "
				+ type + " deve essere valorizzato</br>");
	    }

	    if (chiaviValue.isSingleValue()) {
		result.setAa_fascicolo(chiaviValue.getAnno());
		result.setCd_key_fascicolo(chiaviValue.getNumero());
	    } else {
		result.setAa_fascicolo_da(chiaviValue.getAnnoDa());
		result.setAa_fascicolo_a(chiaviValue.getAnnoA());
		result.setCd_key_fascicolo_da(chiaviValue.getNumeroDa());
		result.setCd_key_fascicolo_a(chiaviValue.getNumeroA());
	    }
	}
    }

    public void validaChiaviFascicoliPadre(RicercaFascicoliBean result,
	    Input<BigDecimal> input_anno, Input<BigDecimal> input_annoDa,
	    Input<BigDecimal> input_annoA, Input<String> input_numero, Input<String> input_numeroDa,
	    Input<String> input_numeroA) throws EMFError {
	ChiaveBean chiaviValue = super.validaChiavi("fascicolo padre", input_anno, input_annoDa,
		input_annoA, input_numero, input_numeroDa, input_numeroA);

	if (chiaviValue != null) {
	    if (chiaviValue.isSingleValue()) {
		result.setAa_fascicolo_padre(chiaviValue.getAnno());
		result.setCd_key_fascicolo_padre(chiaviValue.getNumero());
	    } else {
		result.setAa_fascicolo_padre_da(chiaviValue.getAnnoDa());
		result.setAa_fascicolo_padre_a(chiaviValue.getAnnoA());
		result.setCd_key_fascicolo_padre_da(chiaviValue.getNumeroDa());
		result.setCd_key_fascicolo_padre_a(chiaviValue.getNumeroA());
	    }
	}
    }

    public void validaChiaviUDdelFascicolo(RicercaFascicoliBean result,
	    Input<BigDecimal> input_anno, Input<BigDecimal> input_annoDa,
	    Input<BigDecimal> input_annoA, Input<String> input_numero, Input<String> input_numeroDa,
	    Input<String> input_numeroA) throws EMFError {
	ChiaveBean chiaviValue = super.validaChiavi("unita documentaria", input_anno, input_annoDa,
		input_annoA, input_numero, input_numeroDa, input_numeroA);

	if (chiaviValue != null) {
	    if (chiaviValue.isSingleValue()) {
		result.setAa_key_unita_doc(chiaviValue.getAnno());
		result.setCd_key_unita_doc(chiaviValue.getNumero());
	    } else {
		result.setAa_key_unita_doc_da(chiaviValue.getAnnoDa());
		result.setAa_key_unita_doc_a(chiaviValue.getAnnoA());
		result.setCd_key_unita_doc_da(chiaviValue.getNumeroDa());
		result.setCd_key_unita_doc_a(chiaviValue.getNumeroA());
	    }
	}
    }

    public void validaChiaviFascicoliElenchi(RicercaFascicoliBean result,
	    Input<BigDecimal> input_anno, Input<BigDecimal> input_annoDa,
	    Input<BigDecimal> input_annoA, Input<String> input_numero, Input<String> input_numeroDa,
	    Input<String> input_numeroA) throws EMFError {
	ChiaveBean chiaviValue = super.validaChiavi("fascicolo", input_anno, input_annoDa,
		input_annoA, input_numero, input_numeroDa, input_numeroA);

	if (chiaviValue != null) {
	    if (chiaviValue.isSingleValue()) {
		result.setAa_fascicolo(chiaviValue.getAnno());
		result.setCd_key_fascicolo(chiaviValue.getNumero());
	    } else {
		result.setAa_fascicolo_da(chiaviValue.getAnnoDa());
		result.setAa_fascicolo_a(chiaviValue.getAnnoA());
		result.setCd_key_fascicolo_da(chiaviValue.getNumeroDa());
		result.setCd_key_fascicolo_a(chiaviValue.getNumeroA());
	    }
	}
    }

    public Date[] validaOrdineDateOrari(Input<Timestamp> input_dataDa, Input<Timestamp> input_dataA)
	    throws EMFError {
	Date[] result = null;
	Date dataDa = input_dataDa.parse();
	Date dataA = input_dataA.parse();

	if (dataDa != null) {
	    if (dataA == null) {
		dataA = new Date();
		if (dataDa.compareTo(dataA) <= 0) {
		    input_dataA.setValue(formatDate.format(dataA));

		    result = new Date[2];
		    result[0] = dataDa;
		    result[1] = dataA;
		} else {
		    getMessageBox().addError(
			    input_dataDa.getHtmlDescription() + " superiore alla data odierna");
		}
	    } else {
		if (dataDa.compareTo(dataA) <= 0) {
		    result = new Date[2];
		    result[0] = dataDa;
		    result[1] = dataA;
		} else {
		    getMessageBox().addError(input_dataDa.getHtmlDescription() + " superiore alla "
			    + input_dataA.getHtmlDescription());
		}
	    }
	} else if (dataA != null) {
	    getMessageBox().addError(input_dataDa.getHtmlDescription() + " assente");
	}

	return result;
    }

    public Date[] validaDate(Input<Timestamp> input_dataDa, Input<BigDecimal> input_dataDa_h,
	    Input<BigDecimal> input_dataDa_m, Input<Timestamp> input_dataA,
	    Input<BigDecimal> input_dataA_h, Input<BigDecimal> input_dataA_m) throws EMFError {
	Date[] result = null;

	Date dataDa = input_dataDa.parse();
	BigDecimal oreDa = input_dataDa_h.parse();
	BigDecimal minDa = input_dataDa_m.parse();
	Date dataA = input_dataA.parse();
	BigDecimal oreA = input_dataA_h.parse();
	BigDecimal minA = input_dataA_m.parse();

	Date[] dateValide = super.validaDate(dataDa, oreDa, minDa, dataA, oreA, minA,
		input_dataDa.getHtmlDescription(), input_dataA.getHtmlDescription());

	if (!getMessageBox().hasError() && dateValide != null) {
	    result = dateValide;
	    input_dataDa.setValue(formatDate.format(result[0]));
	    input_dataDa_h.setValue(new SimpleDateFormat("HH").format(result[0]));
	    input_dataDa_m.setValue(new SimpleDateFormat("mm").format(result[0]));

	    input_dataA.setValue(formatDate.format(result[1]));
	    input_dataA_h.setValue(new SimpleDateFormat("HH").format(result[1]));
	    input_dataA_m.setValue(new SimpleDateFormat("mm").format(result[1]));
	}

	return result;
    }

    /**
     * Metodo di validazione: verifica che siano popolati il campo tiScadChius oppure i campi
     * niTempoScadChius e tiTempoScadChius nella form di creazione/modifica criteri di
     * raggruppamento fascicoli
     *
     * @param tiScadChius      tipologia scadenza
     * @param niTempoScadChius tempo scadenza
     * @param tiTempoScadChius tipologia tempo scadenza
     */
    public void validaTipoScadenza(String tiScadChius, BigDecimal niTempoScadChius,
	    String tiTempoScadChius) {
	if (tiScadChius == null && (niTempoScadChius == null && tiTempoScadChius == null)) {
	    getMessageBox().addMessage(new Message(Message.MessageLevel.ERR,
		    "E’ necessario indicare la scadenza di chiusura. </br>"));
	} else if (tiScadChius == null
		&& (((niTempoScadChius != null) && (tiTempoScadChius == null))
			|| ((niTempoScadChius == null) && (tiTempoScadChius != null)))) {
	    getMessageBox().addError(
		    "I campi relativi a Scadenza chiusura devono essere entrambi valorizzati. </br>");
	} else if ((tiScadChius != null)
		&& ((niTempoScadChius != null) || (tiTempoScadChius != null))) {
	    getMessageBox().addError(
		    "I campi Tipo scadenza chiusura e Scadenza chiusura non possono essere entrambi valorizzati. </br>");
	}
    }

    public Object[] validaAnniFascicoli(BigDecimal anno, BigDecimal anno_da, BigDecimal anno_a) {
	boolean single = false;
	boolean range = false;
	Object[] result = null;
	// Controllo innanzitutto che non siano stati inseriti i filtri sia sulla data singola
	// sia sulla data per range
	if (anno_da != null || anno_a != null) {
	    range = true;
	}
	if (anno != null || !range) {
	    single = true;
	}
	if (single && range) {
	    getMessageBox().addError(
		    "Sono stati inseriti valori sia nella ricerca per anno singola, sia per anno con range");
	} else if (single) {
	    result = new Object[1];
	    result[0] = anno;
	} else if (range) {
	    result = new Object[2];
	    if (anno_da != null && anno_a == null) {
		result[0] = anno_da;
		result[1] = new BigDecimal(GregorianCalendar.getInstance().get(Calendar.YEAR));
	    } else if (anno_da == null && anno_a != null) {
		result[0] = new BigDecimal(2000);
		result[1] = anno_a;
	    } else {
		result[0] = anno_da;
		result[1] = anno_a;
	    }

	    if ((result[0] != null || result[1] != null)
		    && ((BigDecimal) result[0]).compareTo((BigDecimal) result[1]) > 0) {
		getMessageBox().addError("Range di anni fascicolo: Anno Da maggiore di Anno A");
	    }
	}
	return result;
    }
}
