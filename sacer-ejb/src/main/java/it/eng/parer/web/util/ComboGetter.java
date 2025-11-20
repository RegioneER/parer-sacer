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

package it.eng.parer.web.util;

import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import it.eng.parer.entity.constraint.AroUnitaDoc;
import it.eng.parer.entity.constraint.DecModelloXsdUd.TiModelloXsdUd;
import it.eng.parer.entity.constraint.ElvElencoVer;
import it.eng.parer.job.utils.JobConstants;
import it.eng.parer.volume.utils.VolumeEnums;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.parer.ws.utils.CostantiDB.TipoDefTemplateEnte;
import it.eng.parer.ws.utils.KeyOrdUtility;
import it.eng.parer.ws.versamento.dto.ConfigRegAnno;
import it.eng.parer.ws.versamento.dto.ConfigRegAnno.TiParte;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.decodemap.DecodeMapIF;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;

/**
 *
 * @author Gilioli_P
 */
@SuppressWarnings("unchecked")
public class ComboGetter {

    public static final String CAMPO_VALORE = "valore";
    public static final String CAMPO_NOME = "nome";

    private ComboGetter() {
	throw new IllegalStateException("Utility class");
    }

    /*
     * GESTIONE DECODEMAP GENERICHE
     */
    public static DecodeMapIF getMappaGenericFlagSiNo() {
	BaseTable bt = new BaseTable();
	BaseRow br = new BaseRow();
	BaseRow br1 = new BaseRow();
	// Imposto i valori della combo INDICATORE
	DecodeMap mappaIndicatore = new DecodeMap();
	br.setString("flag", JobConstants.ComboFlag.SI.name());
	br.setString("valore", JobConstants.ComboFlag.SI.getValue());
	bt.add(br);
	br1.setString("flag", JobConstants.ComboFlag.NO.name());
	br1.setString("valore", JobConstants.ComboFlag.NO.getValue());
	bt.add(br1);
	mappaIndicatore.populatedMap(bt, "valore", "flag");
	return mappaIndicatore;
    }

    /**
     * Metodo statico che crea una decodeMap sulla base dei valori di un enum, ordinato in base al
     * name
     *
     * @param <T>        oggetto generics che estendene Enum con mappa codificata
     * @param key        chiave
     * @param enumerator tipo enumerator
     *
     * @return DecodeMap mappa codifica
     */
    public static <T extends Enum<?>> DecodeMap getMappaSortedGenericEnum(String key,
	    T... enumerator) {
	BaseTable bt = new BaseTable();
	DecodeMap mappa = new DecodeMap();
	for (T mod : sortEnum(enumerator)) {
	    bt.add(createKeyValueBaseRow(key, mod.name()));
	}
	mappa.populatedMap(bt, key, key);
	return mappa;
    }

    /**
     * Metodo statico che crea una decodeMap sulla base dei valori di un enum, ordinato in base
     * all'array fornito come parametro
     *
     * @param <T>        oggetto generics che estendene Enum con mappa codificata
     * @param key        chiave
     * @param enumerator tipo enumerator
     *
     * @return DecodeMap mappa codifica
     */
    public static <T extends Enum<?>> DecodeMap getMappaOrdinalGenericEnum(String key,
	    T... enumerator) {
	BaseTable bt = new BaseTable();
	DecodeMap mappa = new DecodeMap();
	for (T mod : enumerator) {
	    bt.add(createKeyValueBaseRow(key, mod.name()));
	}
	mappa.populatedMap(bt, key, key);
	return mappa;
    }

    /**
     * Metodo statico che crea una decodeMap sulla base dei valori di una lista di String ordinata
     *
     * @param key  chiave
     * @param list lista valori
     *
     * @return DecodeMap mappa codifica
     */
    public static DecodeMap getSortedMapByStringList(String key, List<String> list) {
	BaseTable bt = new BaseTable();
	DecodeMap mappa = new DecodeMap();
	for (String e : new TreeSet<>(list)) {
	    bt.add(createKeyValueBaseRow(key, e));
	}
	mappa.populatedMap(bt, key, key);
	return mappa;
    }

    public static <T extends Enum<?>> DecodeMap getMappaTiParte(
	    ConfigRegAnno.TiParte... enumerator) {
	BaseTable bt = new BaseTable();
	DecodeMap mappa = new DecodeMap();
	for (TiParte mod : sortEnum(enumerator)) {
	    BaseRow br = createKeyValueBaseRow("key", mod.name());
	    br.setString("value", mod.descrizione());
	    bt.add(br);
	}
	mappa.populatedMap(bt, "key", "value");
	return mappa;
    }

    public static <T extends Enum<?>> DecodeMap getCustomMappaTiParte(String tiCharParte) {
	DecodeMap mappaCoincidenzaCon = new DecodeMap();
	BaseTable tabella = new BaseTable();
	BaseRow registro = new BaseRow();
	registro.setString("key", ConfigRegAnno.TiParte.REGISTRO.name());
	registro.setString("value", ConfigRegAnno.TiParte.REGISTRO.descrizione());
	BaseRow anno = new BaseRow();
	anno.setString("key", ConfigRegAnno.TiParte.ANNO.name());
	anno.setString("value", ConfigRegAnno.TiParte.ANNO.descrizione());
	BaseRow progressivo = new BaseRow();
	progressivo.setString("key", ConfigRegAnno.TiParte.PROGR.name());
	progressivo.setString("value", ConfigRegAnno.TiParte.PROGR.descrizione());

	tabella.add(registro);
	if (tiCharParte.equals(KeyOrdUtility.TipiCalcolo.NUMERICO.name())
		|| tiCharParte.equals(KeyOrdUtility.TipiCalcolo.NUMERICO_GENERICO.name())) {
	    tabella.add(anno);
	    tabella.add(progressivo);
	}
	if (tiCharParte.equals(KeyOrdUtility.TipiCalcolo.PARTE_GENERICO.name())) {
	    tabella.add(anno);
	}
	if (tiCharParte.equals(KeyOrdUtility.TipiCalcolo.NUMERI_ROMANI.name())) {
	    tabella.add(progressivo);
	}
	mappaCoincidenzaCon.populatedMap(tabella, "key", "value");
	return mappaCoincidenzaCon;
    }

    public static <T extends Enum<?>> DecodeMap getMappaTipoDefTemplateEnte(
	    CostantiDB.TipoDefTemplateEnte... enumerator) {
	BaseTable bt = new BaseTable();
	BaseRow br = new BaseRow();
	DecodeMap mappa = new DecodeMap();
	for (TipoDefTemplateEnte mod : sortEnum(enumerator)) {
	    br.setString("key", mod.name());
	    br.setString("value", mod.descrizione());
	    bt.add(br);
	}
	mappa.populatedMap(bt, "key", "value");
	return mappa;
    }

    private static BaseRow createKeyValueBaseRow(String key, String value) {
	BaseRow br = new BaseRow();
	br.setString(key, value);
	return br;
    }

    public static DecodeMap getMappaPeriodoVers() {
	BaseTable bt = new BaseTable();
	BaseRow br = new BaseRow();
	BaseRow br1 = new BaseRow();
	BaseRow br2 = new BaseRow();
	DecodeMap mappaPeriodoVers = new DecodeMap();
	br.setString("periodo_k", "OGGI");
	br.setString("periodo_v", "Oggi");
	bt.add(br);
	br1.setString("periodo_k", "ULTIMI7");
	br1.setString("periodo_v", "Ultimi 7 giorni");
	bt.add(br1);
	br2.setString("periodo_k", "TUTTI");
	br2.setString("periodo_v", "Tutti");
	bt.add(br2);
	mappaPeriodoVers.populatedMap(bt, "periodo_k", "periodo_v");
	return mappaPeriodoVers;
    }

    public static DecodeMap getMappaHashAlgorithm() {
	BaseTable bt = new BaseTable();
	DecodeMap mappaAlgo = new DecodeMap();
	String key = "ds_algo_hash_file_calc";
	for (VolumeEnums.AlgoHash algo : sortEnum(VolumeEnums.AlgoHash.values())) {
	    bt.add(createKeyValueBaseRow(key, algo.algorithm()));
	}
	mappaAlgo.populatedMap(bt, key, key);
	return mappaAlgo;
    }

    public static DecodeMap getMappaHashEncoding() {
	BaseTable bt = new BaseTable();
	DecodeMap mappaEncoding = new DecodeMap();
	String key = "cd_encoding_hash_file_calc";
	for (VolumeEnums.EncodingHash enc : sortEnum(VolumeEnums.EncodingHash.values())) {
	    bt.add(createKeyValueBaseRow(key, enc.encoding()));
	}
	mappaEncoding.populatedMap(bt, key, key);
	return mappaEncoding;
    }

    public static DecodeMap getMappaTiOutputRappr() {
	BaseTable bt = new BaseTable();
	String key = "ti_output_rappr";
	//
	DecodeMap mappaTiOutputRappr = new DecodeMap();
	for (CostantiDB.TipiOutputRappr tiAlgo : sortEnum(
		CostantiDB.TipiOutputRappr.getComboTipoOutputRappr())) {
	    bt.add(createKeyValueBaseRow(key, tiAlgo.toString()));
	}
	mappaTiOutputRappr.populatedMap(bt, key, key);
	return mappaTiOutputRappr;
    }

    public static DecodeMap getMappaTiAlgoRappr() {
	BaseTable bt = new BaseTable();
	String key = "ti_algo_rappr";
	DecodeMap mappaTiAlgoRappr = new DecodeMap();
	for (CostantiDB.TipoAlgoritmoRappr tiAlgo : sortEnum(
		CostantiDB.TipoAlgoritmoRappr.getComboTipoAlgoritmoRappr())) {
	    bt.add(createKeyValueBaseRow(key, tiAlgo.toString()));
	}
	mappaTiAlgoRappr.populatedMap(bt, key, key);
	return mappaTiAlgoRappr;
    }

    public static DecodeMap getMappaTiStatoAnnul() {
	BaseTable bt = new BaseTable();
	String key1 = "ti_stato_annul";
	String key2 = "transcode_annul";
	DecodeMap mappaTiStatoAnnul = new DecodeMap();
	for (VolumeEnums.MonitAnnulStatusEnum tiStato : sortEnum(
		VolumeEnums.MonitAnnulStatusEnum.values())) {
	    BaseRow row = createKeyValueBaseRow(key1, tiStato.name());
	    row.setString(key2, tiStato.getTranscodedValue().name());
	    bt.add(row);
	}
	mappaTiStatoAnnul.populatedMap(bt, key1, key2);
	return mappaTiStatoAnnul;
    }

    public static DecodeMap getMappaTiCampo() {
	BaseTable bt = new BaseTable();
	String key1 = "ti_campo";
	String key2 = "transcode_campo";
	DecodeMap tiCampo = new DecodeMap();
	for (CostantiDB.TipoCampo campo : sortEnum(CostantiDB.TipoCampo.values())) {
	    BaseRow row = createKeyValueBaseRow(key1, campo.name());
	    row.setString(key2, campo.getDescrizione());
	    bt.add(row);
	}
	tiCampo.populatedMap(bt, key1, key2);
	return tiCampo;
    }

    public static DecodeMap getMappaNmCampoDatoProfilo() {
	BaseTable bt = new BaseTable();
	String key1 = "nm_campo";
	String key2 = "transcode_campo";
	DecodeMap nmCampo = new DecodeMap();
	for (CostantiDB.NomeCampo campo : sortEnum(CostantiDB.NomeCampo.getComboDatoProfilo())) {
	    BaseRow row = createKeyValueBaseRow(key1, campo.name());
	    row.setString(key2, campo.getDescrizione());
	    bt.add(row);
	}
	nmCampo.populatedMap(bt, key1, key2);
	return nmCampo;
    }

    public static DecodeMap getMappaNmCampoSubStruttura() {
	BaseTable bt = new BaseTable();
	String key1 = "nm_campo";
	String key2 = "transcode_campo";
	DecodeMap nmCampo = new DecodeMap();
	for (CostantiDB.NomeCampo campo : sortEnum(CostantiDB.NomeCampo.getComboSubStruttura())) {
	    BaseRow row = createKeyValueBaseRow(key1, campo.name());
	    row.setString(key2, campo.getDescrizione());
	    bt.add(row);
	}
	nmCampo.populatedMap(bt, key1, key2);
	return nmCampo;
    }

    public static DecodeMap getMappaTiTipoOrdin() {
	BaseTable bt = new BaseTable();
	String key = "ti_ord_serie";
	String key2 = "transcode_ord_serie";
	DecodeMap mappaTiTipoOrdin = new DecodeMap();
	for (CostantiDB.TipoOrdinamentoTipiSerie tiTipoOrdin : sortEnum(
		CostantiDB.TipoOrdinamentoTipiSerie.getComboTipoOrdinamentoTipiSerie())) {
	    BaseRow row = createKeyValueBaseRow(key, tiTipoOrdin.name());
	    row.setString(key2, tiTipoOrdin.toString());
	    bt.add(row);

	}
	mappaTiTipoOrdin.populatedMap(bt, key, key2);
	return mappaTiTipoOrdin;
    }

    public static DecodeMap getMappaTiTipoOrdin_Contro_consec() {
	BaseTable bt = new BaseTable();
	String key = "ti_ord_serie";
	String key2 = "transcode_ord_serie";
	DecodeMap mappaTiTipoOrdin = new DecodeMap();
	CostantiDB.TipoOrdinamentoTipiSerie tiTipoOrdin = CostantiDB.TipoOrdinamentoTipiSerie.KEY_UD_SERIE;
	BaseRow row = createKeyValueBaseRow(key, tiTipoOrdin.name());
	row.setString(key2, tiTipoOrdin.toString());
	bt.add(row);

	mappaTiTipoOrdin.populatedMap(bt, key, key2);
	return mappaTiTipoOrdin;
    }

    public static DecodeMap getMappaTiSelUd() {
	BaseTable bt = new BaseTable();
	String key = "ti_sel_ud";
	String key2 = "transcode_sel_ud";
	DecodeMap mappaTiSelUd = new DecodeMap();
	for (CostantiDB.TipoSelUdTipiSerie tiSelUd : sortEnum(
		CostantiDB.TipoSelUdTipiSerie.getComboTipoSelUdTipiSerie())) {
	    BaseRow row = createKeyValueBaseRow(key, tiSelUd.name());
	    row.setString(key2, tiSelUd.toString());
	    bt.add(row);
	}
	mappaTiSelUd.populatedMap(bt, key, key2);
	return mappaTiSelUd;
    }

    public static DecodeMap getMappaTipo_conten_Serie() {
	BaseTable bt = new BaseTable();
	BaseRow br = new BaseRow();
	// Imposto i valori della combo INDICATORE
	DecodeMap mappaIndicatore = new DecodeMap();
	br.setString("chiave", "Unit\u00E0 Documentaria");
	br.setString("valore", "UNITA_DOC");
	bt.add(br);
	mappaIndicatore.populatedMap(bt, "valore", "chiave");
	return mappaIndicatore;
    }

    public static DecodeMap getMappaTiFiltro() {
	BaseTable bt = new BaseTable();
	String key = "ti_filtro";
	String key2 = "transcode_filtro";
	DecodeMap mappaTiFiltro = new DecodeMap();
	for (CostantiDB.TipoFiltroSerieUd tiFiltro : sortEnum(
		CostantiDB.TipoFiltroSerieUd.getComboTipoDiFiltro())) {
	    BaseRow row = createKeyValueBaseRow(key, tiFiltro.name());
	    row.setString(key2, tiFiltro.toString());
	    bt.add(row);

	}
	mappaTiFiltro.populatedMap(bt, key, key2);
	return mappaTiFiltro;
    }

    public static DecodeMap getMappaTipoDiRappresentazione() {
	BaseTable bt = new BaseTable();
	String key = "ti_out";
	String key2 = "transcode_out";
	DecodeMap mappaTiOut = new DecodeMap();
	for (CostantiDB.TipoDiRappresentazione tiOut : sortEnum(
		CostantiDB.TipoDiRappresentazione.getComboTipoDiRappresentazione())) {
	    BaseRow row = createKeyValueBaseRow(key, tiOut.name());
	    row.setString(key2, tiOut.toString());
	    bt.add(row);
	}
	mappaTiOut.populatedMap(bt, key, key2);
	return mappaTiOut;
    }

    public static DecodeMap getMappaTipoDiTrasformatore() {
	BaseTable bt = new BaseTable();
	// Imposto i valori della combo TipoDiTrasformatore
	String key = "ti_trasform";
	DecodeMap mappaTipoDiTrasformatore = new DecodeMap();
	for (CostantiDB.TipoTrasformatore tiCreaz : sortEnum(
		CostantiDB.TipoTrasformatore.getComboTipiOut())) {
	    bt.add(createKeyValueBaseRow(key, tiCreaz.getTransformString()));
	}
	mappaTipoDiTrasformatore.populatedMap(bt, key, key);

	return mappaTipoDiTrasformatore;
    }

    public static DecodeMap getMappaTipoDiTrasformatoreInp() {
	BaseTable bt = new BaseTable();
	// Imposto i valori della combo TipoDiTrasformatore
	String key = "ti_trasform";
	DecodeMap mappaTipoDiTrasformatore = new DecodeMap();
	for (CostantiDB.TipoTrasformatore tiCreaz : sortEnum(
		CostantiDB.TipoTrasformatore.getComboTipiIn())) {
	    bt.add(createKeyValueBaseRow(key, tiCreaz.getTransformString()));
	}
	mappaTipoDiTrasformatore.populatedMap(bt, key, key);

	return mappaTipoDiTrasformatore;
    }

    public static DecodeMapIF getMappaNiMesiCreazioneSerie() {
	BaseTable bt = new BaseTable();
	String key = "transcode_mesi_creazione_serie";
	DecodeMap mappa = new DecodeMap();

	for (CostantiDB.IntervalliMeseCreazioneSerie niMesi : CostantiDB.IntervalliMeseCreazioneSerie
		.values()) {
	    BaseRow row = createKeyValueBaseRow(key, niMesi.name());
	    bt.add(row);

	}
	mappa.populatedMap(bt, key, key);
	return mappa;
    }

    public static DecodeMapIF getMappaSeparatori(String key) {
	String symbols = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
	BaseTable bt = new BaseTable();
	DecodeMap mappa = new DecodeMap();
	BaseRow spazio = createKeyValueBaseRow(key, "SPAZIO");
	bt.add(spazio);

	for (char symbol : symbols.toCharArray()) {
	    String strSymbol = String.valueOf(symbol);
	    BaseRow row = createKeyValueBaseRow(key, strSymbol);
	    bt.add(row);
	}
	mappa.populatedMap(bt, key, key);
	return mappa;
    }

    public static DecodeMap getMappaTiModelloXsd() {
	BaseTable bt = new BaseTable();
	String key = "ti_modello_xsd";
	DecodeMap mappaTiModelloXsd = new DecodeMap();
	for (CostantiDB.TiModelloXsd tiModelloXsd : sortEnum(CostantiDB.TiModelloXsd.values())) {
	    bt.add(createKeyValueBaseRow(key, tiModelloXsd.toString()));
	}
	mappaTiModelloXsd.populatedMap(bt, key, key);
	return mappaTiModelloXsd;
    }

    public static DecodeMap getMappaTiModelloXsdProfilo() {
	BaseTable bt = new BaseTable();
	String key = "ti_modello_xsd";
	DecodeMap mappaTiModelloXsd = new DecodeMap();
	for (CostantiDB.TiModelloXsdProfilo tiModelloXsd : sortEnum(
		CostantiDB.TiModelloXsdProfilo.values())) {
	    bt.add(createKeyValueBaseRow(key, tiModelloXsd.toString()));
	}
	mappaTiModelloXsd.populatedMap(bt, key, key);
	return mappaTiModelloXsd;
    }

    public static DecodeMap getMappaTiValidElenco() {
	BaseTable bt = new BaseTable();
	String key = "ti_valid_elenco";
	DecodeMap mappaTiValidElenco = new DecodeMap();
	/* Sostituito CostantiDB con le enum it.eng.parer.entity.constraint */
	for (ElvElencoVer.TiValidElenco tiValidElenco : sortEnum(
		ElvElencoVer.TiValidElenco.values())) {
	    bt.add(createKeyValueBaseRow(key, tiValidElenco.toString()));
	}
	mappaTiValidElenco.populatedMap(bt, key, key);
	return mappaTiValidElenco;
    }

    public static DecodeMap getMappaTiModValidElenco() {
	BaseTable bt = new BaseTable();
	String key = "ti_mod_valid_elenco";
	DecodeMap mappaTiModValidElenco = new DecodeMap();
	/* Sostituito CostantiDB con le enum it.eng.parer.entity.constraint */
	for (ElvElencoVer.TiModValidElenco tiModValidElenco : sortEnum(
		ElvElencoVer.TiModValidElenco.values())) {
	    bt.add(createKeyValueBaseRow(key, tiModValidElenco.toString()));
	}
	mappaTiModValidElenco.populatedMap(bt, key, key);
	return mappaTiModValidElenco;
    }

    public static DecodeMap getMappaTiGestElencoCriterio(boolean funzioneSigilloAttivata) {
	BaseTable bt = new BaseTable();
	String key = "ti_gest_elenco_criterio";
	DecodeMap mappaTiGestElencoCriterio = new DecodeMap();
	for (CostantiDB.TiGestElencoCriterio tiGestElencoCriterio : sortEnum(
		CostantiDB.TiGestElencoCriterio.values())) {
	    /*
	     * Se non è attivata la funzionalità di sigillo i relativi valori costanti vengono
	     * esclusi dalla lista.
	     */
	    if (funzioneSigilloAttivata == false) {
		if (tiGestElencoCriterio.equals(CostantiDB.TiGestElencoCriterio.SIGILLO)
			|| tiGestElencoCriterio
				.equals(CostantiDB.TiGestElencoCriterio.MARCA_SIGILLO)) {
		    continue;
		}
	    }
	    bt.add(createKeyValueBaseRow(key, tiGestElencoCriterio.toString()));
	}
	mappaTiGestElencoCriterio.populatedMap(bt, key, key);
	return mappaTiGestElencoCriterio;
    }

    public static DecodeMapIF getMappaTiGestioneParam() {
	BaseTable bt = new BaseTable();
	/* Imposto i valori della combo */
	DecodeMap mappaTiGestioneParam = new DecodeMap();
	BaseRow r1 = new BaseRow();
	r1.setString("ti_gestione_param", "amministrazione");
	BaseRow r2 = new BaseRow();
	r2.setString("ti_gestione_param", "conservazione");
	BaseRow r3 = new BaseRow();
	r3.setString("ti_gestione_param", "gestione");
	bt.add(r1);
	bt.add(r2);
	bt.add(r3);
	mappaTiGestioneParam.populatedMap(bt, "ti_gestione_param", "ti_gestione_param");
	return mappaTiGestioneParam;
    }

    public static DecodeMap getMappaStatoSesUpdKo() {
	BaseTable bt = new BaseTable();
	BaseRow br = new BaseRow();
	BaseRow br1 = new BaseRow();
	BaseRow br2 = new BaseRow();
	DecodeMap mappaStato = new DecodeMap();
	br.setString("ti_stato_ses_upd_ko", "NON_RISOLUBILE");
	bt.add(br);
	br1.setString("ti_stato_ses_upd_ko", "NON_VERIFICATO");
	bt.add(br1);
	br2.setString("ti_stato_ses_upd_ko", "VERIFICATO");
	bt.add(br2);
	mappaStato.populatedMap(bt, "ti_stato_ses_upd_ko", "ti_stato_ses_upd_ko");
	return mappaStato;
    }

    // MEV#22438
    public static DecodeMap getMappaStatoUpdUdKo() {
	BaseTable bt = new BaseTable();
	BaseRow br = new BaseRow();
	BaseRow br1 = new BaseRow();
	BaseRow br2 = new BaseRow();
	DecodeMap mappaStato = new DecodeMap();
	br.setString("ti_stato_upd_ud_ko", "NON_RISOLUBILE");
	bt.add(br);
	br1.setString("ti_stato_upd_ud_ko", "NON_VERIFICATO");
	bt.add(br1);
	br2.setString("ti_stato_upd_ud_ko", "VERIFICATO");
	bt.add(br2);
	mappaStato.populatedMap(bt, "ti_stato_upd_ud_ko", "ti_stato_upd_ud_ko");
	return mappaStato;
    }
    // end MEV#22438

    public static DecodeMap getMappaStatoAggiornamento() {
	BaseTable bt = new BaseTable();
	BaseRow br = new BaseRow();
	BaseRow br1 = new BaseRow();
	BaseRow br2 = new BaseRow();
	DecodeMap mappaStatoAgg = new DecodeMap();
	br.setString("ti_stato_ses", "NON_RISOLUBILE");
	bt.add(br);
	br1.setString("ti_stato_ses", "NON_VERIFICATA");
	bt.add(br1);
	br2.setString("ti_stato_ses", "VERIFICATA");
	bt.add(br2);
	mappaStatoAgg.populatedMap(bt, "ti_stato_ses", "ti_stato_ses");
	return mappaStatoAgg;
    }

    public static DecodeMap getMappaTiStatoUdElencoVers() {
	BaseTable bt = new BaseTable();
	DecodeMap mappaAlgo = new DecodeMap();
	String key = "ti_stato_ud_elenco_vers";
	for (AroUnitaDoc.TiStatoUdElencoVers a : sortEnum(
		AroUnitaDoc.TiStatoUdElencoVers.values())) {
	    bt.add(createKeyValueBaseRow(key, a.name()));
	}
	mappaAlgo.populatedMap(bt, key, key);
	return mappaAlgo;
    }

    public static DecodeMap getMappaTiModelloXsdUd() {
	BaseTable bt = new BaseTable();
	String key = "ti_modello_xsd";
	DecodeMap mappaTiModelloXsd = new DecodeMap();
	for (TiModelloXsdUd tiModelloXsd : sortEnum(TiModelloXsdUd.values())) {
	    bt.add(createKeyValueBaseRow(key, tiModelloXsd.toString()));
	}
	mappaTiModelloXsd.populatedMap(bt, key, key);
	return mappaTiModelloXsd;
    }

    // MEV26798
    public static DecodeMapIF getTiValoreParamApplicCombo() {
	BaseTable bt = new BaseTable();
	BaseRow br = new BaseRow();
	BaseRow br1 = new BaseRow();

	DecodeMap mappaTipiValori = new DecodeMap();
	br.setString(CAMPO_NOME, Constants.ComboValueParamentersType.STRINGA.name());
	br.setString(CAMPO_VALORE, Constants.ComboValueParamentersType.STRINGA.name());
	bt.add(br);
	br1.setString(CAMPO_NOME, Constants.ComboValueParamentersType.PASSWORD.name());
	br1.setString(CAMPO_VALORE, Constants.ComboValueParamentersType.PASSWORD.name());
	bt.add(br1);
	mappaTipiValori.populatedMap(bt, CAMPO_VALORE, CAMPO_NOME);
	return mappaTipiValori;
    }

    public static DecodeMap getMappaTiStatoJob() {
	BaseTable bt = new BaseTable();
	BaseRow br = new BaseRow();
	BaseRow br1 = new BaseRow();
	BaseRow br2 = new BaseRow();
	DecodeMap mappaStatoAgg = new DecodeMap();
	br.setString("ti_stato_job", "ATTIVO");
	bt.add(br);
	br1.setString("ti_stato_job", "DISATTIVO");
	bt.add(br1);
	br2.setString("ti_stato_job", "IN_ESECUZIONE");
	bt.add(br2);
	mappaStatoAgg.populatedMap(bt, "ti_stato_job", "ti_stato_job");
	return mappaStatoAgg;
    }

    public static DecodeMap getMappaTiMotCancellazione() {
	BaseTable bt = new BaseTable();
	String key = "ti_mot_cancellazione";
	String valore = "ds_mot_cancellazione";
	DecodeMap mappaTiMotCancellazione = new DecodeMap();
	for (CostantiDB.TiMotCancellazione tiMotCancellazione : sortEnum(
		CostantiDB.TiMotCancellazione.values())) {
	    BaseRow row = createKeyValueBaseRow(key, tiMotCancellazione.name());
	    row.setString(valore, tiMotCancellazione.getDescrizione());
	    bt.add(row);
	}
	mappaTiMotCancellazione.populatedMap(bt, key, valore);
	return mappaTiMotCancellazione;
    }

    public static DecodeMap getMappaTiStatoRichiesta() {
	BaseTable bt = new BaseTable();
	String key = "ti_stato_richiesta";
	DecodeMap mappaTiStatoRichiesta = new DecodeMap();
	for (CostantiDB.TiStatoRichiesta tiStatoRichiesta : sortEnum(
		CostantiDB.TiStatoRichiesta.values())) {
	    BaseRow row = createKeyValueBaseRow(key, tiStatoRichiesta.name());
	    bt.add(row);
	}
	mappaTiStatoRichiesta.populatedMap(bt, key, key);
	return mappaTiStatoRichiesta;
    }

    /**
     * Metodo statico per ordinare un enum tramite il valore
     *
     * @param <T>        oggetti generics di tipo Enum
     *
     * @param enumValues l'array di valori dell'enum
     *
     * @return la collezione ordinata
     */
    private static <T extends Enum<?>> Collection<T> sortEnum(T[] enumValues) {
	SortedMap<String, T> map = new TreeMap<>();
	for (T l : enumValues) {
	    map.put(l.name(), l);
	}
	return map.values();
    }
}
