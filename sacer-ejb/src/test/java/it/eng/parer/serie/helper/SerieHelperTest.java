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

package it.eng.parer.serie.helper;

import static it.eng.ArquillianUtils.aBigDecimal;
import static it.eng.ArquillianUtils.aFlag;
import static it.eng.ArquillianUtils.aListOfBigDecimal;
import static it.eng.ArquillianUtils.aListOfString;
import static it.eng.ArquillianUtils.aLong;
import static it.eng.ArquillianUtils.aString;
import static it.eng.ArquillianUtils.aStringArray;
import static it.eng.ArquillianUtils.assertExceptionMessage;
import static it.eng.ArquillianUtils.assertNoResultException;
import static it.eng.ArquillianUtils.createEnterpriseArchive;
import static it.eng.ArquillianUtils.createPaginatorJavaArchive;
import static it.eng.ArquillianUtils.createSacerJavaArchive;
import static it.eng.ArquillianUtils.todayTs;
import static it.eng.ArquillianUtils.tomorrowTs;
import static it.eng.parer.web.helper.HelperTest.createSacerLogJar;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;

import it.eng.parer.entity.SerFileVerSerie;
import it.eng.parer.entity.SerVerSerie;
import it.eng.parer.entity.constraint.SerUrnFileVerSerie;
import it.eng.parer.entity.constraint.SerUrnIxVolVerSerie;
import it.eng.parer.serie.dto.RicercaSerieBean;
import it.eng.parer.serie.dto.RicercaUdAppartBean;
import it.eng.parer.slite.gen.viewbean.SerVLisErrFileSerieUdTableBean;
import it.eng.parer.slite.gen.viewbean.SerVLisUdAppartSerieTableBean;
import it.eng.parer.slite.gen.viewbean.SerVLisUdAppartVolSerieTableBean;
import it.eng.parer.ws.dto.CSChiaveFasc;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.utils.CostantiDB;

@ArquillianTest
public class SerieHelperTest {
    @EJB
    private SerieHelper helper;

    @Test
    void getLockSerContenutoVerSerieQueryIsOk() {
	Long idVerSerie = aLong();
	String tiContenutoVerSerie = aString();
	try {
	    helper.getLockSerContenutoVerSerie(idVerSerie, tiContenutoVerSerie);
	    assertTrue(true);
	} catch (Exception e) {
	    assertNoResultException(e);
	}
    }

    @Test
    void getSerContenutoVerSerieQueryIsOk() {
	Long idVerSerie = aLong();
	String tiContenutoVerSerie = aString();
	helper.getSerContenutoVerSerie(idVerSerie, tiContenutoVerSerie);
	assertTrue(true);
    }

    @Test
    void getSerSerieQueryIsOk() {
	BigDecimal idTipoSerie = aBigDecimal();
	BigDecimal idStrut = aBigDecimal();
	BigDecimal aaSerie = aBigDecimal();
	helper.getSerSerie(idTipoSerie, idStrut, aaSerie);
	assertTrue(true);
    }

    @Test
    void countSerieQueryIsOk() {
	BigDecimal idStrut = aBigDecimal();
	BigDecimal idTipoSerie = aBigDecimal();
	BigDecimal aaSerie = aBigDecimal();
	Date dataInizio = todayTs();
	Date dataFine = tomorrowTs();
	String cdSerie = aString();
	helper.countSerie(idStrut, idTipoSerie, aaSerie, dataInizio, dataFine, cdSerie);
	assertTrue(true);
    }

    @Test
    void getLastSerVerSerieQueryIsOk() {
	BigDecimal idSerie = aBigDecimal();
	helper.getLastSerVerSerie(idSerie);
	assertTrue(true);
    }

    @Test
    void getLastSerStatoVerSerieQueryIsOk() {
	Long idVerSerie = aLong();
	helper.getLastSerStatoVerSerie(idVerSerie);
	assertTrue(true);
    }

    @Test
    void getLastSerStatoVerSerieWithStatusQueryIsOk() {
	Long idVerSerie = aLong();
	String tiStatoVerSerie = aString();
	helper.getLastSerStatoVerSerieWithStatus(idVerSerie, tiStatoVerSerie);
	assertTrue(true);
    }

    @Test
    void getLastSerStatoSerieQueryIsOk() {
	Long idSerie = aLong();
	helper.getLastSerStatoSerie(idSerie);
	assertTrue(true);
    }

    @Test
    void getSerFileInputVerSerieQueryIsOk() {
	Long idVerSerie = aLong();
	String tiScopoFileInputVerSerie = aString();
	helper.getSerFileInputVerSerie(idVerSerie, tiScopoFileInputVerSerie);
	assertTrue(true);
    }

    @Test
    void getDecTipoSerieUdQueryIsOk() {
	Long idTipoSerie = aLong();
	helper.getDecTipoSerieUd(idTipoSerie);
	assertTrue(true);
    }

    @Test
    void getDecTipoSerieUdNoSelUnitaDocAnnulQueryIsOk() {
	Long idTipoSerie = aLong();
	helper.getDecTipoSerieUdNoSelUnitaDocAnnul(idTipoSerie);
	assertTrue(true);
    }

    @Test
    void getDecOutSelUdQueryIsOk() {
	Long idTipoSerieUd = aLong();
	helper.getDecOutSelUd(idTipoSerieUd);
	assertTrue(true);
    }

    @Test
    void getDecFiltroSelUdSelTablesQueryIsOk() {
	Long idTipoSerieUd = aLong();
	helper.getDecFiltroSelUdSelTables(idTipoSerieUd);
	assertTrue(true);
    }

    @Test
    void getDecFiltroSelUdQueryIsOk() {
	Long idTipoSerieUd = aLong();
	helper.getDecFiltroSelUd(idTipoSerieUd);
	assertTrue(true);
    }

    @Test
    void getDecFiltroSelUdDato_Long_StringQueryIsOk() {
	Long idTipoSerieUd = aLong();
	String nmTipoUnitaDoc = aString();
	helper.getDecFiltroSelUdDato(idTipoSerieUd, nmTipoUnitaDoc);
	assertTrue(true);
    }

    @Test
    void getDecFiltroSelUdDato_Long_LongQueryIsOk() {
	Long idTipoSerieUd = aLong();
	Long idTipoDoc = aLong();
	helper.getDecFiltroSelUdDato(idTipoSerieUd, idTipoDoc);
	assertTrue(true);
    }

    @Test
    void getDecCampoOutSelUdQueryIsOk() {
	Long idOutSelUd = aLong();
	helper.getDecCampoOutSelUd(idOutSelUd);
	assertTrue(true);
    }

    @Test
    void executeQueryListQueryIsOk() {
	String queryString = "SELECT 1 as idUnitaDoc from dual";
	helper.executeQueryList(queryString);
	assertTrue(true);
    }

    @Test
    void executeQueryListWithExceptionQueryIsOk() {
	String queryString = "SELECT 1 as idUnitaDoc from dual";
	helper.executeQueryListWithException(queryString);
	assertTrue(true);
    }

    @Test
    void getUdAppartVerSerieQueryIsOk() {
	Long idContenutoVerSerie = aLong();
	boolean first = false;
	helper.getUdAppartVerSerie(idContenutoVerSerie, first);
	assertTrue(true);
    }

    @Test
    void getMinDtUnitaDocFromUdAppartVerSerieQueryIsOk() {
	Long idContenutoVerSerie = aLong();
	helper.getMinDtUnitaDocFromUdAppartVerSerie(idContenutoVerSerie);
	assertTrue(true);
    }

    @Test
    void getMaxDtUnitaDocFromUdAppartVerSerieQueryIsOk() {
	Long idContenutoVerSerie = aLong();
	helper.getMaxDtUnitaDocFromUdAppartVerSerie(idContenutoVerSerie);
	assertTrue(true);
    }

    @Test
    void getDecCampoInpUdQueryIsOk() {
	Long idTipoSerie = aLong();
	helper.getDecCampoInpUd(idTipoSerie);
	assertTrue(true);
    }

    @Test
    void getDecCampoInpUdDatoProfiloQueryIsOk() {
	Long idTipoSerie = aLong();
	helper.getDecCampoInpUdDatoProfilo(idTipoSerie);
	assertTrue(true);
    }

    @Test
    void getDecCampoInpUdDatiSpecQueryIsOk() {
	Long idTipoSerie = aLong();
	Long idTipoUnitaDoc = aLong();
	Long idTipoDoc = aLong();
	helper.getDecCampoInpUdDatiSpec(idTipoSerie, idTipoUnitaDoc, idTipoDoc);
	assertTrue(true);
    }

    @Test
    void existUdInSerieQueryIsOk() {
	Long idContenutoVerSerie = aLong();
	BigDecimal idUnitaDoc = aBigDecimal();
	helper.existUdInSerie(idContenutoVerSerie, idUnitaDoc);
	assertTrue(true);
    }

    @Test
    void getSerVRicSerieUd_List_BigDecimalQueryIsOk() {
	List<BigDecimal> verSeries = aListOfBigDecimal(2);
	BigDecimal idStrut = aBigDecimal();
	helper.getSerVRicSerieUd(verSeries, idStrut);
	assertTrue(true);
    }

    @Test
    void getSerVRicSerieUd_long_RicercaSerieBeanQueryIsOk() {
	long idUserIam = aLong();
	RicercaSerieBean filtri = aRicercaSerieBean();
	helper.getSerVRicSerieUd(idUserIam, filtri);
	assertTrue(true);
    }

    private RicercaSerieBean aRicercaSerieBean() {
	RicercaSerieBean filtri = new RicercaSerieBean();
	filtri.setAa_serie_a(BigDecimal.valueOf(2021));
	filtri.setAa_serie_da(BigDecimal.valueOf(2019));
	filtri.setCd_composito_serie(aString());
	filtri.setDs_serie(aString());
	filtri.setDt_fine_serie(tomorrowTs());
	filtri.setDt_inizio_serie(todayTs());
	filtri.setFl_da_rigenera(aFlag());
	filtri.setFl_elab_bloccata(aFlag());
	filtri.setFl_err_contenuto_acq(aFlag());
	filtri.setFl_err_contenuto_calc(aFlag());
	filtri.setFl_err_contenuto_eff(aFlag());
	filtri.setFl_err_contenuto_file(aFlag());
	filtri.setFl_err_validazione(aFlag());
	filtri.setFl_presenza_consist_attesa(aFlag());
	filtri.setId_ambiente(aBigDecimal());
	filtri.setId_ente(aBigDecimal());
	filtri.setId_modello_tipo_serie(aBigDecimal());
	filtri.setId_registro_unita_doc(aBigDecimal());
	filtri.setId_strut(aBigDecimal());
	filtri.setId_tipo_serie(aBigDecimal());
	filtri.setId_tipo_unita_doc(aBigDecimal());
	filtri.setTi_crea_standard(aFlag());
	filtri.setTi_stato_conservazione(aListOfString(2));
	filtri.setTi_stato_contenuto_acq(aString());
	filtri.setTi_stato_contenuto_calc(aString());
	filtri.setTi_stato_contenuto_eff(aString());
	filtri.setTi_stato_cor_serie(aListOfString(2));
	return filtri;
    }

    @Test
    void getSerVRicSerieUdUsrQueryIsOk() {
	long idUserIam = aLong();
	RicercaSerieBean filtri = aRicercaSerieBean();
	helper.getSerVRicSerieUdUsr(idUserIam, filtri);
	assertTrue(true);
    }

    @Test
    void getSerVJobContenutoBloccatoQueryIsOk() {
	String nmJob = aString();
	BigDecimal idContenutoVerSerie = aBigDecimal();
	helper.getSerVJobContenutoBloccato(nmJob, idContenutoVerSerie);
	assertTrue(true);
    }

    @Test
    void getSerVJobVerSerieBloccatoQueryIsOk() {
	String nmJob = aString();
	BigDecimal idVerSerie = aBigDecimal();
	helper.getSerVJobVerSerieBloccato(nmJob, idVerSerie);
	assertTrue(true);
    }

    @Test
    void getSerVLisNotaSerieQueryIsOk() {
	BigDecimal idVerSerie = aBigDecimal();
	helper.getSerVLisNotaSerie(idVerSerie);
	assertTrue(true);
    }

    @Test
    void getMaxPgSerVLisNotaSerieQueryIsOk() {
	BigDecimal idVerSerie = aBigDecimal();
	BigDecimal idTipoNotaSerie = aBigDecimal();
	helper.getMaxPgSerVLisNotaSerie(idVerSerie, idTipoNotaSerie);
	assertTrue(true);
    }

    @Test
    void getSerVLisStatoSerieQueryIsOk() {
	BigDecimal idVerSerie = aBigDecimal();
	helper.getSerVLisStatoSerie(idVerSerie);
	assertTrue(true);
    }

    @Test
    void getSerVLisVerSeriePrecQueryIsOk() {
	BigDecimal idVerSerie = aBigDecimal();
	helper.getSerVLisVerSeriePrec(idVerSerie);
	assertTrue(true);
    }

    @Test
    void getSerVLisVolSerieUdQueryIsOk() {
	BigDecimal idVerSerie = aBigDecimal();
	helper.getSerVLisVolSerieUd(idVerSerie);
	assertTrue(true);
    }

    @Test
    void getSerVLisUdAppartVolSerieQueryIsOk() {
	BigDecimal idVolVerSerie = aBigDecimal();
	RicercaUdAppartBean parametri = aRicercaUdAppartBean();
	helper.getSerVLisUdAppartVolSerie(idVolVerSerie, parametri,
		l -> new SerVLisUdAppartVolSerieTableBean());
	assertTrue(true);
    }

    private RicercaUdAppartBean aRicercaUdAppartBean() {
	RicercaUdAppartBean parametri = new RicercaUdAppartBean();
	parametri.setCdUdSerie(aString());
	parametri.setDtUdSerieA(tomorrowTs());
	parametri.setDtUdSerieDa(todayTs());
	parametri.setInfoUdSerie(aString());
	parametri.setPgUdSerieA(BigDecimal.TEN);
	parametri.setPgUdSerieDa(BigDecimal.ZERO);
	parametri.setTiStatoConservazione(aString());
	return parametri;
    }

    @Test
    void getSerVVisContenutoSerieUdQueryIsOk() {
	BigDecimal idVerSerie = aBigDecimal();
	String tipoContenuto = aString();
	helper.getSerVVisContenutoSerieUd(idVerSerie, tipoContenuto);
	assertTrue(true);
    }

    @Test
    void getSerVLisUdAppartSerieLazyLoadedQueryIsOk() {
	BigDecimal idContenutoVerSerie = aBigDecimal();
	RicercaUdAppartBean parametri = aRicercaUdAppartBean();
	helper.getSerVLisUdAppartSerie(idContenutoVerSerie, parametri,
		l -> new SerVLisUdAppartSerieTableBean(), true);
	assertTrue(true);
    }

    @Test
    void getSerVLisUdAppartSerieEagerLoadedQueryIsOk() {
	BigDecimal idContenutoVerSerie = aBigDecimal();
	RicercaUdAppartBean parametri = aRicercaUdAppartBean();
	helper.getSerVLisUdAppartSerie(idContenutoVerSerie, parametri,
		l -> new SerVLisUdAppartSerieTableBean(), false);
	assertTrue(true);
    }

    @Test
    void getSerVLisErrContenSerieUdQueryIsOk() {
	BigDecimal idContenutoVerSerie = aBigDecimal();
	helper.getSerVLisErrContenSerieUd(idContenutoVerSerie);
	assertTrue(true);
    }

    @Test
    void getSerVLisErrFileSerieLazuUdQueryIsOk() {
	BigDecimal idVerSerie = aBigDecimal();
	String tiScopoFileInputVerSerie = aString();
	helper.getSerVLisErrFileSerieUd(idVerSerie, tiScopoFileInputVerSerie,
		l -> new SerVLisErrFileSerieUdTableBean(), true);
	assertTrue(true);
    }

    @Test
    void getSerVLisErrFileSerieUdEagerQueryIsOk() {
	BigDecimal idVerSerie = aBigDecimal();
	String tiScopoFileInputVerSerie = aString();
	helper.getSerVLisErrFileSerieUd(idVerSerie, tiScopoFileInputVerSerie,
		l -> new SerVLisErrFileSerieUdTableBean(), false);
	assertTrue(true);
    }

    @Test
    void getSerQueryContenutoVerSerie_BigDecimalQueryIsOk() {
	BigDecimal idContenutoVerSerie = aBigDecimal();
	helper.getSerQueryContenutoVerSerie(idContenutoVerSerie);
	assertTrue(true);
    }

    @Test
    void getSerQueryContenutoVerSerie_3argsQueryIsOk() {
	Long idContenutoVerSerie = aLong();
	BigDecimal idRegistroUnitaDoc = aBigDecimal();
	BigDecimal idTipoUnitaDoc = aBigDecimal();
	try {
	    helper.getSerQueryContenutoVerSerie(idContenutoVerSerie, idRegistroUnitaDoc,
		    idTipoUnitaDoc);
	    assertTrue(true);
	} catch (Exception e) {
	    assertNoResultException(e);
	}
    }

    @Test
    void getSerLacunaConsistVerSerie_BigDecimalQueryIsOk() {
	BigDecimal idConsistVerSerie = aBigDecimal();
	helper.getSerLacunaConsistVerSerie(idConsistVerSerie);
	assertTrue(true);
    }

    @Test
    void getSerLacunaConsistVerSerie_4argsQueryIsOk() {
	BigDecimal idConsistVerSerie = aBigDecimal();
	String tiModLacuna = aString();
	BigDecimal niIniLacuna = aBigDecimal();
	BigDecimal niFinLacuna = aBigDecimal();
	helper.getSerLacunaConsistVerSerie(idConsistVerSerie, tiModLacuna, niIniLacuna,
		niFinLacuna);
	assertTrue(true);
    }

    @Test
    void countSerLacunaConsistVerSerieQueryIsOk() {
	BigDecimal idConsistVerSerie = aBigDecimal();
	BigDecimal idLacuna = aBigDecimal();
	String tiModLacuna = CostantiDB.TipoModLacuna.RANGE_PROGRESSIVI.name();
	BigDecimal niIniLacuna = aBigDecimal();
	BigDecimal niFinLacuna = aBigDecimal();
	helper.countSerLacunaConsistVerSerie(idConsistVerSerie, idLacuna, tiModLacuna, niIniLacuna,
		niFinLacuna);
	assertTrue(true);
    }

    @Test
    void getAroUdAppartVerSerieQueryIsOk() {
	long idVerSerie = aLong();
	String tipoContenuto = aString();
	String tipoContenuto2 = aString();
	boolean checkPgNullo = false;
	helper.getAroUdAppartVerSerie(idVerSerie, tipoContenuto, tipoContenuto2, checkPgNullo);
	assertTrue(true);
    }

    @Test
    void getAroUdAppartChiaveDoppiaQueryIsOk() {
	long idContenutoVerSerie = aLong();
	helper.getAroUdAppartChiaveDoppia(idContenutoVerSerie);
	assertTrue(true);
    }

    @Test
    void getAroUdAppartNumeroDoppioQueryIsOk() {
	long idContenutoVerSerie = aLong();
	helper.getAroUdAppartNumeroDoppio(idContenutoVerSerie);
	assertTrue(true);
    }

    @Test
    void countAroUdAppartVerSerieInPgIntervalQueryIsOk() {
	long idContenutoVerSerie = aLong();
	BigDecimal pgUdSerieDa = aBigDecimal();
	BigDecimal pgUdSerieA = aBigDecimal();
	helper.countAroUdAppartVerSerieInPgInterval(idContenutoVerSerie, pgUdSerieDa, pgUdSerieA);
	assertTrue(true);
    }

    @Test
    void getSerVSelUdNoversQueryIsOk() {
	long idVerSerie = aLong();
	BigDecimal aaSelUd = aBigDecimal();
	helper.getSerVSelUdNovers(idVerSerie, aaSelUd);
	assertTrue(true);
    }

    @Test
    void countSerVSelUdNoversQueryIsOk() {
	long idVerSerie = aLong();
	BigDecimal aaSelUd = aBigDecimal();
	helper.countSerVSelUdNovers(idVerSerie, aaSelUd);
	assertTrue(true);
    }

    @Test
    void getSerVBucoNumerazioneUdQueryIsOk() {
	long idContenutoVerSerie = aLong();
	helper.getSerVBucoNumerazioneUd(idContenutoVerSerie);
	assertTrue(true);
    }

    @Test
    void getSerVSelUdNoversBucoQueryIsOk() {
	BigDecimal idVerSerie = aBigDecimal();
	BigDecimal pgUdSerIniBuco = aBigDecimal();
	BigDecimal pgUdSerFinBuco = aBigDecimal();
	helper.getSerVSelUdNoversBuco(idVerSerie, pgUdSerIniBuco, pgUdSerFinBuco);
	assertTrue(true);
    }

    @Test
    void countSerErrContenutoVerSerie_BigDecimal_StringQueryIsOk() {
	BigDecimal idContenutoVerSerie = aBigDecimal();
	String tiGravitaErr = aString();
	helper.countSerErrContenutoVerSerie(idContenutoVerSerie, tiGravitaErr);
	assertTrue(true);
    }

    @Test
    void countSerErrContenutoVerSerie_4argsQueryIsOk() {
	BigDecimal idContenutoVerSerie = aBigDecimal();
	String tiGravitaErr = aString();
	String tiErr = aString();
	String tiOrigineErr = aString();
	helper.countSerErrContenutoVerSerie(idContenutoVerSerie, tiGravitaErr, tiErr, tiOrigineErr);
	assertTrue(true);
    }

    @Test
    void countSerErrContenutoVerSerie_long_StringArrQueryIsOk() {
	long idContenutoVerSerie = aLong();
	String[] tiErrs = aStringArray(2);
	helper.countSerErrContenutoVerSerie(idContenutoVerSerie, tiErrs);
	assertTrue(true);
    }

    @Test
    void getSerVLisUdErrFileInputQueryIsOk() {
	BigDecimal idErrFileInput = aBigDecimal();
	helper.getSerVLisUdErrFileInput(idErrFileInput);
	assertTrue(true);
    }

    @Test
    void getSerVLisUdNoversQueryIsOk() {
	BigDecimal idErrContenutoVerSerie = aBigDecimal();
	helper.getSerVLisUdNovers(idErrContenutoVerSerie);
	assertTrue(true);
    }

    @Test
    void getDecTipoSerieAutomQueryIsOk() {
	helper.getDecTipoSerieAutom();
	assertTrue(true);
    }

    @Test
    void getSerieAutomQueryIsOk() {
	BigDecimal aaIniCreaAutom = aBigDecimal();
	BigDecimal aaFinCreaAutom = aBigDecimal();
	long idTipoSerie = aLong();
	helper.getSerieAutom(aaIniCreaAutom, aaFinCreaAutom, idTipoSerie);
	assertTrue(true);
    }

    @Test
    void countSerieWithoutPgSerieUdQueryIsOk() {
	Long idTipoSerie = aLong();
	helper.countSerieWithoutPgSerieUd(idTipoSerie);
	assertTrue(true);
    }

    @Test
    void getSerVerSerieDaElabListQueryIsOk() {
	BigDecimal idAmbiente = aBigDecimal();
	BigDecimal idEnte = aBigDecimal();
	BigDecimal idStrut = aBigDecimal();
	for (CostantiDB.StatoVersioneSerie tiStatoVerSerie : CostantiDB.StatoVersioneSerie
		.values()) {
	    helper.getSerVerSerieDaElabList(idAmbiente, idEnte, idStrut, tiStatoVerSerie);
	    assertTrue(true);
	}
    }

    @Test
    void existsFirmataNoMarcaQueryIsOk() {
	BigDecimal idStrut = aBigDecimal();
	helper.existsFirmataNoMarca(idStrut);
	assertTrue(true);
    }

    @Test
    void getSerVerSerieDaElabQueryIsOk() {
	BigDecimal idStrut = aBigDecimal();
	String tiStatoVerSerie = aString();
	helper.getSerVerSerieDaElab(idStrut, tiStatoVerSerie);
	assertTrue(true);
    }

    @Test
    void getLockAroUnitaDocQueryIsOk() {
	BigDecimal idContenutoVerSerie = aBigDecimal();
	helper.getLockAroUnitaDoc(idContenutoVerSerie);
	assertTrue(true);
    }

    @Test
    void getAroUdAppartVerSerieInContenEffQueryIsOk() {
	Long idVerSerie = aLong();
	String tiStatoConservazioneUd = aString();
	helper.getAroUdAppartVerSerieInContenEff(idVerSerie, tiStatoConservazioneUd);
	assertTrue(true);
    }

    @Test
    void getAroUdAppartVerSerieNoAipQueryIsOk() {
	Long idVerSerie = aLong();
	helper.getAroUdAppartVerSerieNoAip(idVerSerie);
	assertTrue(true);
    }

    @Test
    void getIdVerIndiceAipLastQueryIsOk() {
	Long idUnitaDoc = aLong();
	String tiFormatoIndiceAip = aString();
	try {
	    helper.getIdVerIndiceAipLast(idUnitaDoc, tiFormatoIndiceAip);
	    assertTrue(true);
	} catch (Exception e) {
	    assertExceptionMessage(e, " l'indice AIP non \u00E8 definito ");
	}
    }

    @Test
    void getSerVerSerieDaElabByIdVerSerieQueryIsOk() {
	long idVerSerie = aLong();
	helper.getSerVerSerieDaElabByIdVerSerie(idVerSerie);
	assertTrue(true);
    }

    @Test
    void getLockAroUdAppartVerSerieQueryIsOk() {
	long idVerSerie = aLong();
	String tiContenutoVerSerie = aString();
	helper.getLockAroUdAppartVerSerie(idVerSerie, tiContenutoVerSerie);
	assertTrue(true);
    }

    @Test
    void getSerFileVerSerieQueryIsOk() {
	long idVerSerie = aLong();
	String tiFileVerSerie = aString();
	helper.getSerFileVerSerie(idVerSerie, tiFileVerSerie);
	assertTrue(true);
    }

    @Test
    void retrieveFileVerSerieQueryIsOk() {
	SerVerSerie verSerie = new SerVerSerie();
	final String tiFileVerSerie = "il mio";
	final SerFileVerSerie fileCheMiInteressa = new SerFileVerSerie();
	final byte[] myContentBytes = "Contenuto".getBytes();
	fileCheMiInteressa.setBlFile(myContentBytes);
	fileCheMiInteressa.setTiFileVerSerie(tiFileVerSerie);

	final SerFileVerSerie altroFile = new SerFileVerSerie();
	altroFile.setBlFile("immagine jpg".getBytes());
	altroFile.setTiFileVerSerie("non mi interessa");
	verSerie.setSerFileVerSeries(Arrays.asList(altroFile, fileCheMiInteressa));

	byte[] trovato = helper.retrieveFileVerSerie(verSerie, tiFileVerSerie);
	assertEquals(myContentBytes, trovato);
    }

    @Test
    void getSerIxVolVerSerieQueryIsOk() {
	long idVolVerSerie = aLong();
	try {
	    helper.getSerIxVolVerSerie(idVolVerSerie);
	    assertTrue(true);
	} catch (Exception e) {
	    assertNoResultException(e);
	}
    }

    @Test
    void getSerIxVolVerSerieListQueryIsOk() {
	long idVerSerie = aLong();
	helper.getSerIxVolVerSerieList(idVerSerie);
	assertTrue(true);
    }

    @Test
    void getAroUnitaDocAnnullateInContenutoWithTipoSerieUdQueryIsOk() {
	long idContenutoVerSerie = aLong();
	long idRegistroUnitaDoc = aLong();
	long idTipoUnitaDoc = aLong();
	Date minDtAnnul = todayTs();
	helper.getAroUnitaDocAnnullateInContenutoWithTipoSerieUd(idContenutoVerSerie,
		idRegistroUnitaDoc, idTipoUnitaDoc, minDtAnnul);
	assertTrue(true);
    }

    @Test
    void getSerVLisSerDaValidareQueryIsOk() {
	Long idUserIam = aLong();
	BigDecimal idAmbiente = aBigDecimal();
	BigDecimal idEnte = aBigDecimal();
	BigDecimal idStrut = aBigDecimal();
	helper.getSerVLisSerDaValidare(idUserIam, idAmbiente, idEnte, idStrut);
	assertTrue(true);
    }

    @Test
    void getVersioniSerieCorrentiByTipoSerieAndStatoQueryIsOk() {
	BigDecimal idTipoSerie = aBigDecimal();
	String[] statiSerie = aStringArray(2);
	helper.getVersioniSerieCorrentiByTipoSerieAndStato(idTipoSerie, statiSerie);
	assertTrue(true);
    }

    @Test
    void countSerQueryContenutoVerSerieQueryIsOk() {
	BigDecimal idContenutoVerSerie = aBigDecimal();
	BigDecimal idRegistroUnitaDoc = aBigDecimal();
	BigDecimal idTipoUnitaDoc = aBigDecimal();
	helper.countSerQueryContenutoVerSerie(idContenutoVerSerie, idRegistroUnitaDoc,
		idTipoUnitaDoc);
	assertTrue(true);
    }

    @Test
    void countAroUdAppartVerSerieQueryIsOk() {
	long idContenutoVerSerie = aLong();
	helper.countAroUdAppartVerSerie(idContenutoVerSerie);
	assertTrue(true);
    }

    @Test
    void retrieveStruttureQueryIsOk() {
	helper.retrieveStrutture();
	assertTrue(true);
    }

    @Test
    void getSerVRicConsistSerieUdQueryIsOk() {
	RicercaSerieBean filtri = aRicercaSerieBean();
	helper.getSerVRicConsistSerieUd(filtri);
	assertTrue(true);
    }

    @Test
    void getVersioniSerieCorrentiByTipoSerieQueryIsOk() {
	long idTipoSerie = aLong();
	helper.getVersioniSerieCorrentiByTipoSerie(idTipoSerie);
	assertTrue(true);
    }

    @Test
    void retrieveSerVLisVerserByRichannQueryIsOk() {
	long idRichAnnulVers = aLong();
	helper.retrieveSerVLisVerserByRichann(idRichAnnulVers);
	assertTrue(true);
    }

    @Test
    void getUrnIxVolVerSerieByTipiUrnQueryIsOk() {
	BigDecimal idVolVerSerie = BigDecimal.ZERO;
	List<SerUrnIxVolVerSerie.TiUrnIxVolVerSerie> tiUrn = Arrays.asList(
		SerUrnIxVolVerSerie.TiUrnIxVolVerSerie.INIZIALE,
		SerUrnIxVolVerSerie.TiUrnIxVolVerSerie.NORMALIZZATO);
	helper.getUrnIxVolVerSerieByTipiUrn(idVolVerSerie, tiUrn);
	assertTrue(true);
    }

    @Test
    void getSerVVisSerieUdQueryIsOk() {
	BigDecimal idVerSerie = BigDecimal.ZERO;
	helper.getSerVVisSerieUd(idVerSerie);
	assertTrue(true);
    }

    @Test
    void getSerVVisVolVerSerieUdQueryIsOk() {
	BigDecimal idVolVerSerie = BigDecimal.ZERO;
	helper.getSerVVisVolVerSerieUd(idVolVerSerie);
	assertTrue(true);
    }

    @Test
    void getUrnFileVerSerieByTipiUrnQueryIsOk() {
	BigDecimal idVerSerie = BigDecimal.ZERO;
	List<SerUrnFileVerSerie.TiUrnFileVerSerie> tiUrn = Arrays.asList(
		SerUrnFileVerSerie.TiUrnFileVerSerie.INIZIALE,
		SerUrnFileVerSerie.TiUrnFileVerSerie.NORMALIZZATO);
	helper.getUrnFileVerSerieByTipiUrn(idVerSerie, tiUrn);
	assertTrue(true);
    }

    @Test
    void existsCdSerNormaliz() {
	long idStrut = 0L;
	BigDecimal aaSerie = BigDecimal.valueOf(2021);
	String cdSerieNormaliz = "TEST";
	helper.existsCdSerNormaliz(idStrut, aaSerie, cdSerieNormaliz);
	assertTrue(true);
    }

    @Deployment
    public static Archive<?> createTestArchive() {
	final JavaArchive sacerJavaArchive = createSacerJavaArchive(
		Arrays.asList("it.eng.parer.serie.dto"), CSVersatore.class, CSChiaveFasc.class,
		SerieHelper.class, SerieHelperTest.class);
	return createEnterpriseArchive(SerieHelperTest.class.getSimpleName(), sacerJavaArchive,
		createPaginatorJavaArchive(), createSacerLogJar());
    }
}
