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

package it.eng.parer.amministrazioneStrutture.gestioneTitolario.ejb;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.amministrazioneStrutture.gestioneTitolario.dto.Voce;
import it.eng.parer.amministrazioneStrutture.gestioneTitolario.utils.OperazioneComparator;
import it.eng.parer.amministrazioneStrutture.gestioneTitolario.utils.VoceComparator;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.titolario.xml.ChiudiVoceType;
import it.eng.parer.titolario.xml.CreaTitolario;
import it.eng.parer.titolario.xml.CreaVoceType;
import it.eng.parer.titolario.xml.LivelloType;
import it.eng.parer.titolario.xml.ModificaVoceType;
import it.eng.parer.titolario.xml.OperazioneCreaType;
import it.eng.parer.titolario.xml.OperazioneModificaType;
import it.eng.parer.titolario.xml.OperazioneType;
import it.eng.spagoLite.message.MessageBox;

/**
 *
 * @author Bonora_L
 */
@Stateless(mappedName = "StrutTitolariCheck")
@LocalBean
public class StrutTitolariCheck {

    private static final Logger logger = LoggerFactory.getLogger(StrutTitolariCheck.class);

    public List<LivelloType> checkLivelli(CreaTitolario creaTitolario, MessageBox messageBox) {
	BigInteger numLivelli = creaTitolario.getIntestazione().getNumeroLivelliUtilizzati();
	List<LivelloType> livelli = creaTitolario.getLivelli().getLivello();
	if (livelli.size() > 0) {
	    if (livelli.size() != numLivelli.intValue()) {
		messageBox.addError(
			"\u00C8 necessario indicare le informazioni per ciascuno dei livelli dichiarati nel tag &lt;NumeroLivelliUtilizzati&gt;");
		logger.error(
			"\u00C8 necessario indicare le informazioni per ciascuno dei livelli dichiarati nel tag <NumeroLivelliUtilizzati>");
	    } else {
		Set<String> nomiLivelli = new HashSet<>();
		Set<Integer> numeroLivelli = new HashSet<>();
		for (LivelloType livello : livelli) {
		    if (livello.getNomeLivello() != null && livello.getNumeroLivello() != null
			    && livello.getTipoFormatoLivello().value() != null
			    && livello.getCarattereSeparatoreLivello() != null) {
			if (livello.getNumeroLivello().intValue() <= 0) {
			    messageBox.addError(
				    "Il numero che identifica il livello deve essere un intero positivo minore o uguale al numero di livelli dichiarato nel tag <NumeroLivelliUtilizzati>");
			}
			if (livello.getNumeroLivello().intValue() > numLivelli.intValue()) {
			    messageBox.addError("Il numero che identifica il livello "
				    + livello.getNomeLivello()
				    + " \u00E8 superiore al numero di livelli dichiarato");
			}
			if (!nomiLivelli.add(livello.getNomeLivello())) {
			    messageBox.addError("Il nome livello " + livello.getNomeLivello()
				    + " \u00E8 gi\u00E0 stato definito in un livello precedente");
			}
			if (!numeroLivelli.add(livello.getNumeroLivello().intValue())) {
			    messageBox.addError(
				    "Il numero che identifica il livello non è univoco all'interno del xml");
			}
		    }
		}
		if (!messageBox.hasError()) {
		    // Nel dubbio, riordino i livelli all'interno della lista
		    // FIXME: Controllare l'ordinamento (vedi voce Ordinamenti di liste con
		    // "delegate" sulla wiki)
		    Collections.sort(livelli, new Comparator<LivelloType>() {
			@Override
			public int compare(LivelloType o1, LivelloType o2) {
			    Integer s1 = o1.getNumeroLivello().intValue();
			    Integer s2 = o2.getNumeroLivello().intValue();
			    return s1.compareTo(s2);

			}
		    });
		    for (int i = 0; i < livelli.size(); i++) {
			if ((i == 0 && StringUtils
				.isNotBlank(livelli.get(i).getCarattereSeparatoreLivello()))
				|| (i != 0 && StringUtils
					.isBlank(livelli.get(i).getCarattereSeparatoreLivello()))) {
			    messageBox.addError(
				    "Per tutti i livelli diversi dal primo deve essere indicato il carattere separatore");
			    logger.error(
				    "Per tutti i livelli diversi dal primo deve essere indicato il carattere separatore");
			    break;
			}
		    }
		}
		if (!messageBox.hasError()) {
		    return livelli;
		}
	    }
	} else {
	    messageBox.addError("\u00C8 necessario indicare almeno un livello");
	    logger.error("\u00C8 necessario indicare almeno un livello");
	}
	return null;
    }

    public Map<String, Voce> parseVoci(Map<String, Voce> vociMap,
	    List<? extends OperazioneType> operazioni, List<LivelloType> livelli,
	    Date dataInizioValidita, MessageBox messageBox) {
	TreeSet<OperazioneType> listaVoci = new TreeSet<>(new VoceComparator());
	listaVoci.addAll(operazioni);
	if (listaVoci.size() != operazioni.size()) {
	    Iterator it = CollectionUtils.disjunction(listaVoci, operazioni).iterator();
	    while (it.hasNext()) {
		OperazioneType voce = (OperazioneType) it.next();
		String codiceVoceComposito = null;
		if (voce instanceof OperazioneCreaType) {
		    codiceVoceComposito = ((OperazioneCreaType) voce).getCreaVoce()
			    .getCodiceVoceComposito();
		} else if (voce instanceof OperazioneModificaType) {
		    OperazioneModificaType tmpOp = ((OperazioneModificaType) voce);
		    if (tmpOp.getCreaVoce() != null) {
			codiceVoceComposito = tmpOp.getCreaVoce().getCodiceVoceComposito();
		    } else if (tmpOp.getModificaVoce() != null) {
			codiceVoceComposito = tmpOp.getModificaVoce().getCodiceVoceComposito();
		    } else if (tmpOp.getChiudiVoce() != null) {
			codiceVoceComposito = tmpOp.getChiudiVoce().getCodiceVoceComposito();
		    }
		}
		messageBox
			.addError("<li>Codice voce composito " + String.valueOf(codiceVoceComposito)
				+ " non univoco all'interno del xml</li>");
		logger.error("Codice voce composito " + String.valueOf(codiceVoceComposito)
			+ " non univoco all'interno del xml");
	    }
	}

	listaVoci = new TreeSet<>(new OperazioneComparator());
	listaVoci.addAll(operazioni);
	Pattern[] regExs = getLivelliRegex(livelli);
	Set<Integer> numeroOrdinePrimoLivelloSet = new HashSet<>();
	if (vociMap == null) {
	    vociMap = new HashMap<>();
	} else {
	    for (Entry<String, Voce> entry : vociMap.entrySet()) {
		Voce voce = entry.getValue();
		numeroOrdinePrimoLivelloSet.add(voce.getNumeroOrdine());
	    }
	}

	Voce.Operation operation = null;

	for (OperazioneType voce : listaVoci) {
	    if (messageBox.hasError()) {
		break;
	    }
	    CreaVoceType creaVoce = null;
	    ModificaVoceType modVoce = null;
	    ChiudiVoceType chiudiVoce = null;
	    String codiceVoceComposito = null;
	    if (voce instanceof OperazioneCreaType) {
		creaVoce = ((OperazioneCreaType) voce).getCreaVoce();
		codiceVoceComposito = creaVoce.getCodiceVoceComposito();
		operation = Voce.Operation.CREA;
	    } else if (voce instanceof OperazioneModificaType) {
		OperazioneModificaType tmpOp = ((OperazioneModificaType) voce);
		if (tmpOp.getCreaVoce() != null) {
		    creaVoce = tmpOp.getCreaVoce();
		    codiceVoceComposito = creaVoce.getCodiceVoceComposito();
		    operation = Voce.Operation.CREA;
		} else if (tmpOp.getModificaVoce() != null) {
		    modVoce = tmpOp.getModificaVoce();
		    codiceVoceComposito = modVoce.getCodiceVoceComposito();
		    operation = Voce.Operation.MODIFICA;
		} else if (tmpOp.getChiudiVoce() != null) {
		    chiudiVoce = tmpOp.getChiudiVoce();
		    codiceVoceComposito = chiudiVoce.getCodiceVoceComposito();
		    operation = Voce.Operation.CHIUDI;
		}
	    }

	    logger.info(codiceVoceComposito);

	    int indexPattern = 0;
	    for (Pattern regEx : regExs) {
		if (regEx.matcher(codiceVoceComposito).matches()) {
		    Voce newVoce = null;
		    if (operation == Voce.Operation.CREA) {
			newVoce = new Voce(creaVoce);
		    } else if (operation == Voce.Operation.MODIFICA) {
			newVoce = new Voce(modVoce);
		    } else if (operation == Voce.Operation.CHIUDI) {
			newVoce = new Voce(chiudiVoce);
		    }

		    if (operation != Voce.Operation.CREA && indexPattern == 0
			    && newVoce.getNumeroOrdine() == null) {
			Voce old = vociMap.get(codiceVoceComposito);
			if (old != null) {
			    newVoce.setNumeroOrdine(old.getNumeroOrdine());
			} else {
			    messageBox.addError("La voce di classificazione " + codiceVoceComposito
				    + " non esiste");
			    logger.error("Errore inaspettato - Modifica di una voce inesistente");
			}
		    }
		    if (!messageBox.hasError()) {
			if (operation == Voce.Operation.CREA) {
			    if (indexPattern == 0 && !numeroOrdinePrimoLivelloSet
				    .add(newVoce.getNumeroOrdine())) {
				messageBox.addError("Per lo stesso livello di "
					+ newVoce.getCodiceVoceComposito()
					+ " \u00E8 gi\u00E0 esistente una voce con lo stesso numero d'ordine");
				logger.error("Per lo stesso livello di "
					+ newVoce.getCodiceVoceComposito()
					+ " \u00E8 gi\u00E0 esistente una voce con lo stesso numero d'ordine");
				break;
			    }
			}
			checkVoci(newVoce, livelli, indexPattern, vociMap, dataInizioValidita,
				messageBox);
		    }
		    break;
		}
		indexPattern++;
		if (indexPattern == regExs.length) {
		    messageBox.addError("Valore di codice voce " + codiceVoceComposito
			    + " non conforme ai formati specificati dai livelli");
		    logger.error("Valore di codice voce " + codiceVoceComposito
			    + " non conforme ai formati specificati dai livelli");
		}
	    }

	}

	return vociMap;

    }

    public Pattern[] getLivelliRegex(List<LivelloType> livelli) {
	Pattern[] patterns = new Pattern[livelli.size()];
	String[] regex = new String[livelli.size()];
	regex[0] = "[a-zA-Z0-9]+";
	for (LivelloType livello : livelli) {
	    if (StringUtils.isNotBlank(livello.getCarattereSeparatoreLivello())) {
		int indice = livello.getNumeroLivello().intValue() - 1;
		regex[indice] = regex[indice - 1]
			+ Pattern.quote(livello.getCarattereSeparatoreLivello()) + "[a-zA-Z0-9]+";
		patterns[indice] = Pattern.compile(regex[indice]);
	    }
	}
	patterns[0] = Pattern.compile(regex[0]);
	return patterns;
    }

    public void checkVoci(Voce voce, List<LivelloType> livelli, int indexLevel,
	    Map<String, Voce> vociMap, Date dataInizioValidita, MessageBox messageBox) {
	voce.setLivello(livelli.get(indexLevel));
	if ((voce.getDataInizioValidita() != null
		&& dataInizioValidita.compareTo(voce.getDataInizioValidita()) <= 0)
		|| (voce.getOperation() != Voce.Operation.CREA)) {
	    String codiceVoce = "";
	    String[] codiceVoceSplittato = null;
	    if (StringUtils.isNotBlank(voce.getLivello().getCarattereSeparatoreLivello())) {
		codiceVoceSplittato = getCodiceVoce(voce, livelli, indexLevel);
		if (codiceVoceSplittato != null) {
		    codiceVoce = codiceVoceSplittato[indexLevel];
		}
	    } else {
		codiceVoce = voce.getCodiceVoceComposito();
	    }
	    if (!messageBox.hasError()) {
		switch (voce.getLivello().getTipoFormatoLivello()) {
		case ROMANO:
		    if (!isValidRomanNumber(codiceVoce)) {
			messageBox.addError("Valore di codice voce " + voce.getCodiceVoceComposito()
				+ " non conforme al formato specificato dal livello "
				+ voce.getLivello().getNomeLivello() + " a cui appartiene la voce");
			logger.error("Valore di codice voce " + voce.getCodiceVoceComposito()
				+ " non conforme al formato specificato dal livello "
				+ voce.getLivello().getNomeLivello() + " a cui appartiene la voce");
		    }
		    break;
		case ALFABETICO:
		    if (!StringUtils.isAlpha(codiceVoce)) {
			messageBox.addError("Valore di codice voce " + voce.getCodiceVoceComposito()
				+ " non conforme al formato specificato dal livello "
				+ voce.getLivello().getNomeLivello() + " a cui appartiene la voce");
			logger.error("Valore di codice voce " + voce.getCodiceVoceComposito()
				+ " non conforme al formato specificato dal livello "
				+ voce.getLivello().getNomeLivello() + " a cui appartiene la voce");
		    }
		    break;
		case ALFANUMERICO:
		    if (!StringUtils.isAlphanumeric(codiceVoce)) {
			messageBox.addError("Valore di codice voce " + voce.getCodiceVoceComposito()
				+ " non conforme al formato specificato dal livello "
				+ voce.getLivello().getNomeLivello() + " a cui appartiene la voce");
			logger.error("Valore di codice voce " + voce.getCodiceVoceComposito()
				+ " non conforme al formato specificato dal livello "
				+ voce.getLivello().getNomeLivello() + " a cui appartiene la voce");
		    }
		    break;
		case NUMERICO:
		    if (!StringUtils.isNumeric(codiceVoce)) {
			messageBox.addError("Valore di codice voce " + voce.getCodiceVoceComposito()
				+ " non conforme al formato specificato dal livello "
				+ voce.getLivello().getNomeLivello() + " a cui appartiene la voce");
			logger.error("Valore di codice voce " + voce.getCodiceVoceComposito()
				+ " non conforme al formato specificato dal livello "
				+ voce.getLivello().getNomeLivello() + " a cui appartiene la voce");
		    }
		    break;
		}
	    }
	    if (!messageBox.hasError()) {
		Date dataInizioValiditaVoce = voce.getDataInizioValidita();
		Date dataFineValiditaVoce = voce.getDataFineValidita();

		if (dataInizioValiditaVoce != null && dataFineValiditaVoce != null
			&& dataFineValiditaVoce.compareTo(dataInizioValiditaVoce) < 0) {
		    messageBox.addError("La data di fine validit\u00E0 di una voce di "
			    + "classificazione non può essere antecedente alla "
			    + "data di inizio validit\u00E0 della voce stessa");
		    logger.error("La data di fine validit\u00E0 di una voce di "
			    + "classificazione non può essere antecedente alla "
			    + "data di inizio validit\u00E0 della voce stessa");
		}
	    }
	    if (!messageBox.hasError()) {
		if (indexLevel > 0) {
		    Voce root = codiceVoceSplittato != null ? vociMap.get(codiceVoceSplittato[0])
			    : null;
		    if (root != null) {
			try {
			    setVoceRicorsiva(root, voce, codiceVoceSplittato, 1);
			} catch (ParerUserError e) {
			    messageBox.addError(e.getDescription());
			}
		    } else {
			messageBox.addError("La voce di classificazione "
				+ voce.getCodiceVoceComposito() + " - " + voce.getDescrizioneVoce()
				+ " prevede un livello padre non presente nel titolario");
			logger.error("La voce di classificazione " + voce.getCodiceVoceComposito()
				+ " - " + voce.getDescrizioneVoce()
				+ " prevede un livello padre non presente nel titolario");
		    }
		} else {
		    if (voce.getOperation() == null || voce.getOperation() == Voce.Operation.CREA) {
			vociMap.put(voce.getCodiceVoceComposito(), voce);
		    } else if (voce.getOperation() == Voce.Operation.MODIFICA) {
			Voce son = vociMap.get(voce.getCodiceVoceComposito());
			modificaVoce(son, voce);
		    } else if (voce.getOperation() == Voce.Operation.CHIUDI) {
			Voce son = vociMap.get(voce.getCodiceVoceComposito());
			chiudiVoce(son, voce);
		    }
		}
	    }
	} else {
	    messageBox.addError("La data di inizio validit\u00E0 di una voce di "
		    + "classificazione non può essere antecedente alla "
		    + "data di inizio validit\u00E0 del titolario cui si riferisce");
	    logger.error("La data di inizio validit\u00E0 di una voce di "
		    + "classificazione non può essere antecedente alla "
		    + "data di inizio validit\u00E0 del titolario cui si riferisce");
	}
    }

    public void setVoceRicorsiva(Voce root, Voce node, String[] codiceVoceSplittato, int index)
	    throws ParerUserError {
	String codiceVoceSingolo = codiceVoceSplittato[index];
	Voce son = root.getFiglio(codiceVoceSingolo);

	if (node.getOperation() == null || node.getOperation() == Voce.Operation.CREA) {
	    if (son == null) {
		if (index == (codiceVoceSplittato.length - 1)) {
		    if (node.getDataFineValidita() != null && root.getDataFineValidita() != null) {
			// Controllo data fine validità rispetto al padre
			if (node.getDataFineValidita().after(root.getDataFineValidita())) {
			    logger.error("La data di fine validit\u00E0 del nodo "
				    + node.getCodiceVoceComposito()
				    + " non pu\u00F2 essere posteriore alla data di fine validit\u00E0 del nodo padre");
			    throw new ParerUserError("La data di fine validit\u00E0 del nodo "
				    + node.getCodiceVoceComposito()
				    + " non pu\u00F2 essere posteriore alla data di fine validit\u00E0 del nodo padre");
			}
		    }
		    // Controllo numero d'ordine
		    if (node.getNumeroOrdine() != null
			    && root.putNumeroOrdineFiglio(node.getNumeroOrdine())) {
			node.setCodiceVoce(codiceVoceSingolo);
			root.putFiglio(codiceVoceSingolo, node);
		    } else {
			logger.error("Per lo stesso livello di " + node.getCodiceVoceComposito()
				+ " \u00E8 gi\u00E0 esistente una voce con lo stesso numero d'ordine");
			throw new ParerUserError("Per lo stesso livello di "
				+ node.getCodiceVoceComposito()
				+ " \u00E8 gi\u00E0 esistente una voce con lo stesso numero d'ordine");
		    }
		} else {
		    logger.error("La voce di classificazione " + node.getCodiceVoceComposito()
			    + " - " + node.getDescrizioneVoce()
			    + " prevede un livello padre non presente nel titolario");
		    throw new ParerUserError("La voce di classificazione "
			    + node.getCodiceVoceComposito() + " - " + node.getDescrizioneVoce()
			    + " prevede un livello padre non presente nel titolario");
		}
	    } else {
		if (index == (codiceVoceSplittato.length - 1)) {
		    logger.error("Codice voce composito " + node.getCodiceVoceComposito()
			    + " non univoco");
		    throw new ParerUserError(
			    "Codice voce " + node.getCodiceVoceComposito() + " non univoco");
		} else {
		    setVoceRicorsiva(son, node, codiceVoceSplittato, index + 1);
		}
	    }
	} else {
	    if (son != null) {
		if (index == (codiceVoceSplittato.length - 1)) {
		    if (node.getDataFineValidita() != null && root.getDataFineValidita() != null) {
			// Controllo data fine validità rispetto al padre
			if (node.getDataFineValidita().after(root.getDataFineValidita())) {
			    logger.error("La data di fine validit\u00E0 del nodo "
				    + node.getCodiceVoceComposito()
				    + " non pu\u00F2 essere posteriore alla data di fine validit\u00E0 del nodo padre");
			    throw new ParerUserError("La data di fine validit\u00E0 del nodo "
				    + node.getCodiceVoceComposito()
				    + " non pu\u00F2 essere posteriore alla data di fine validit\u00E0 del nodo padre");
			}
		    }
		    if (node.getOperation() == Voce.Operation.MODIFICA) {
			modificaVoce(son, node);
		    } else if (node.getOperation() == Voce.Operation.CHIUDI) {
			chiudiVoce(son, node);
		    }
		} else {
		    setVoceRicorsiva(son, node, codiceVoceSplittato, index + 1);
		}
	    } else {
		logger.error("La voce di classificazione " + node.getCodiceVoceComposito()
			+ " non esiste");
		throw new ParerUserError("La voce di classificazione "
			+ node.getCodiceVoceComposito() + " non esiste");
	    }
	}
    }

    public void chiudiVoce(Voce son, Voce node) {
	son.setOperation(node.getOperation());
	if (node.getNoteVoceTitolario() != null) {
	    son.setNoteVoceTitolario(node.getNoteVoceTitolario());
	}
	if (node.getDataFineValidita() != null) {
	    son.setDataFineValidita(node.getDataFineValidita());
	} else {
	    son.setDataFineValidita(Calendar.getInstance().getTime());
	}
    }

    public void modificaVoce(Voce son, Voce node) {
	son.setOperation(node.getOperation());
	son.setDescrizioneVoce(node.getDescrizioneVoce());
	son.setAttivoPerClassificazione(node.getAttivoPerClassificazione());
	son.setTempoConservazione(node.getTempoConservazione());
	if (node.getNoteVoceTitolario() != null) {
	    son.setNoteVoceTitolario(node.getNoteVoceTitolario());
	}
	if (node.getDataFineValidita() != null) {
	    son.setDataFineValidita(node.getDataFineValidita());
	}
    }

    public String[] getCodiceVoce(Voce voce, List<LivelloType> livelli, int indexLevel) {
	String codiceVoce = voce.getCodiceVoceComposito();
	return getCodiceVoce(codiceVoce, livelli, indexLevel);
    }

    public String[] getCodiceVoce(String codiceVoceComposito, List<LivelloType> livelli,
	    int indexLevel) {
	String[] codici = new String[indexLevel + 1];
	for (int i = 0; i < indexLevel; i++) {
	    String separatore = livelli.get(i + 1).getCarattereSeparatoreLivello();
	    int indexChar;
	    if ((indexChar = codiceVoceComposito.indexOf(separatore)) != -1) {
		codici[i] = codiceVoceComposito.substring(0, indexChar);
		codiceVoceComposito = codiceVoceComposito.substring(indexChar + 1);

		if (i == (indexLevel - 1)) {
		    codici[i + 1] = codiceVoceComposito;
		}
	    } else {
		return null;
	    }
	}
	return codici;
    }

    public boolean isValidRomanNumber(String roman) {
	String numeralPattern = "M*(CM|DC{0,3}|CD|C{0,3})(XC|LX{0,3}|XL|X{0,3})(IX|VI{0,3}|IV|I{0,4})";
	Pattern pattern = Pattern.compile(numeralPattern);
	roman = roman.toUpperCase();
	Matcher m = pattern.matcher(roman);
	return m.matches();
    }

    public void addVociToMap(Voce voce, List<LivelloType> livelli, int indexLevel,
	    Map<String, Voce> vociMap) throws ParerUserError {
	String[] codiceVoceSplittato = getCodiceVoce(voce, livelli, indexLevel);
	if (indexLevel > 0) {
	    Voce root = vociMap.get(codiceVoceSplittato[0]);
	    if (root != null) {
		setVoceRicorsiva(root, voce, codiceVoceSplittato, 1);
	    } else {
		logger.error("La voce di classificazione " + voce.getCodiceVoceComposito() + " - "
			+ voce.getDescrizioneVoce()
			+ " prevede un livello padre non presente nel titolario");
		throw new ParerUserError(
			"Errore inaspettato nel recupero delle voci del titolario");
	    }
	} else {
	    voce.setCodiceVoce(voce.getCodiceVoceComposito());
	    vociMap.put(voce.getCodiceVoceComposito(), voce);
	}
    }

}
