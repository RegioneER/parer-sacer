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

package it.eng.parer.web.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.slite.gen.tablebean.VrsSessioneVersKoTableBean;
import it.eng.parer.slite.gen.viewbean.MonVLisUniDocDaAnnulTableBean;
import it.eng.parer.web.dto.CounterResultBean;
import it.eng.parer.web.dto.MonitoraggioFiltriListaDocBean;
import it.eng.parer.web.dto.MonitoraggioFiltriListaVersFallitiBean;
import it.eng.parer.web.dto.MonitoraggioFiltriListaVersFallitiDistintiDocBean;
import it.eng.parer.web.util.BlobObject;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.Transform;
import it.eng.spagoCore.error.EMFError;

public class MonitoraggioHelperTest extends HelperTest<MonitoraggioHelper> {

    @Test
    void contaDocNoUd_queryIsOk() {
	long idUtente = aLong();
	BigDecimal idAmbiente = aBigDecimal();
	BigDecimal idEnte = aBigDecimal();
	BigDecimal idStruttura = aBigDecimal();

	helper.contaDocNoUd(idUtente, idAmbiente, idEnte, idStruttura);
	assertTrue(true);
    }

    @Test
    void contaDocUd_queryIsOk() {
	long idUtente = aLong();
	BigDecimal idTipoUnitaDoc = aBigDecimal();
	BigDecimal idAmbiente = aBigDecimal();
	BigDecimal idEnte = aBigDecimal();
	BigDecimal idStruttura = aBigDecimal();

	helper.contaDocUd(idUtente, idTipoUnitaDoc, idAmbiente, idEnte, idStruttura);
	assertTrue(true);
    }

    @Test
    void contaDocStatoVolNoUd_queryIsOk() {
	long idUtente = aLong();
	BigDecimal idAmbiente = aBigDecimal();
	BigDecimal idEnte = aBigDecimal();
	BigDecimal idStruttura = aBigDecimal();

	helper.contaDocStatoVolNoUd(idUtente, idAmbiente, idEnte, idStruttura);
	assertTrue(true);
    }

    @Test
    void contaDocStatoVolUd_queryIsOk() {
	long idUtente = aLong();
	BigDecimal idTipoUnitaDoc = aBigDecimal();
	BigDecimal idAmbiente = aBigDecimal();
	BigDecimal idEnte = aBigDecimal();
	BigDecimal idStruttura = aBigDecimal();

	helper.contaDocStatoVolUd(idUtente, idTipoUnitaDoc, idAmbiente, idEnte, idStruttura);
	assertTrue(true);
    }

    @Test
    void contaSessioniVersRisVer_queryIsOk() {
	long idUtente = aLong();
	BigDecimal idAmbiente = aBigDecimal();
	BigDecimal idEnte = aBigDecimal();
	BigDecimal idStruttura = aBigDecimal();

	helper.contaSessioniVersRisVer(idUtente, idAmbiente, idEnte, idStruttura);
	assertTrue(true);
    }

    @Test
    void contaSessioniAggRisVer_queryIsOk() {
	long idUtente = aLong();
	BigDecimal idAmbiente = aBigDecimal();
	BigDecimal idEnte = aBigDecimal();
	BigDecimal idStruttura = aBigDecimal();

	helper.contaSessioniAggRisVer(idUtente, idAmbiente, idEnte, idStruttura);
	assertTrue(true);
    }

    @Test
    void getMonVRiepStrutIamViewBean_queryIsOk() {
	long idUtente = aLong();
	int idAmbiente = aInt();

	helper.getMonVRiepStrutIamViewBean(idUtente, 100, idAmbiente);
	assertTrue(true);
    }

    @Test
    void getMonVLisDocViewBean_queryIsOk() {
	MonitoraggioFiltriListaDocBean filtri = aMonitoraggioFiltriListaDocBean();
	helper.getMonVLisDocViewBean(filtri, 100);
	assertTrue(true);
    }

    private MonitoraggioFiltriListaDocBean aMonitoraggioFiltriListaDocBean() {
	MonitoraggioFiltriListaDocBean filtri = new MonitoraggioFiltriListaDocBean();
	filtri.setAaKeyUnitaDoc(aBigDecimal());
	filtri.setAaKeyUnitaDocA(BigDecimal.TEN);
	filtri.setAaKeyUnitaDocDa(BigDecimal.ONE);
	filtri.setCdKeyUnitaDoc(aString());
	filtri.setCdKeyUnitaDocA(aString());
	filtri.setCdKeyUnitaDocDa(aString());
	filtri.setCdRegistroKeyUnitaDoc(aString());
	filtri.setGiornoVersA(tomorrowTs());
	filtri.setGiornoVersAValidato(tomorrowTs());
	filtri.setGiornoVersDa(todayTs());
	filtri.setGiornoVersDaValidato(todayTs());
	filtri.setIdAmbiente(aBigDecimal());
	filtri.setIdEnte(aBigDecimal());
	filtri.setIdStrut(aBigDecimal());
	filtri.setIdTipoDoc(aBigDecimal());
	filtri.setIdTipoUnitaDoc(aBigDecimal());
	filtri.setIdUserIam(aBigDecimal());
	filtri.setMinutiVersA(BigDecimal.TEN);
	filtri.setMinutiVersDa(BigDecimal.ONE);
	filtri.setOreVersA(BigDecimal.TEN);
	filtri.setOreVersDa(BigDecimal.ONE);
	filtri.setPeriodoVers(aString());
	filtri.setStatoDoc(aString());
	filtri.setStatoVol(aString());
	filtri.setTipoCreazione(aString());
	filtri.setTipoDoc(aString());
	return filtri;
    }

    @Test
    void getMonVLisVersErrIamViewBean_queryIsOk() {
	MonitoraggioFiltriListaVersFallitiBean filtriSes = aMonitoraggioFiltriListaVersFallitiBean();
	int maxResults = 10;
	helper.getMonVLisVersErrIamViewBean(filtriSes, maxResults);
	assertTrue(true);
    }

    private MonitoraggioFiltriListaVersFallitiBean aMonitoraggioFiltriListaVersFallitiBean() {
	MonitoraggioFiltriListaVersFallitiBean filtriSes = new MonitoraggioFiltriListaVersFallitiBean();
	filtriSes.setAnno(BigDecimal.valueOf(2020));
	filtriSes.setAnno_range_a(BigDecimal.valueOf(2021));
	filtriSes.setAnno_range_da(BigDecimal.valueOf(2019));
	filtriSes.setClasseErrore(aString());
	filtriSes.setCodiceErrore(aString());
	filtriSes.setGiornoVersA(tomorrowTs());
	filtriSes.setGiornoVersAValidato(tomorrowTs());
	filtriSes.setGiornoVersDa(todayTs());
	filtriSes.setGiornoVersDaValidato(todayTs());
	filtriSes.setIdAmbiente(aBigDecimal());
	filtriSes.setIdEnte(aBigDecimal());
	filtriSes.setIdStrut(aBigDecimal());
	filtriSes.setIdTipoUnitaDoc(aBigDecimal());
	filtriSes.setIdUserIam(aBigDecimal());
	filtriSes.setMinutiVersA(BigDecimal.TEN);
	filtriSes.setMinutiVersDa(BigDecimal.ONE);
	filtriSes.setNonRisolubile(aFlag());
	filtriSes.setNumero("10");
	filtriSes.setNumero_range_a("19");
	filtriSes.setNumero_range_da("1");
	filtriSes.setOreVersA(BigDecimal.TEN);
	filtriSes.setOreVersDa(BigDecimal.ONE);
	filtriSes.setPeriodoVers("OGGI");
	filtriSes.setRegistro(aSetOfString(2));
	filtriSes.setRegistro_range(aSetOfString(2));
	filtriSes.setRisolto(aFlag());
	filtriSes.setSottoClasseErrore(aString());
	filtriSes.setTipiUD(aString());
	filtriSes.setTipoSes(aString());
	filtriSes.setVerificato(aFlag());
	return filtriSes;
    }

    @Test
    void getMonVLisOperVolIamViewBean_queryIsOk() {
	MonitoraggioHelper.FiltriOperazioniVolumiPlain filtriOV = aFiltriOperazioniVolumiPlain();
	helper.getMonVLisOperVolIamViewBeanPlainFilters(100, filtriOV);
	assertTrue(true);
    }

    private MonitoraggioHelper.FiltriOperazioniVolumiPlain aFiltriOperazioniVolumiPlain() {
	MonitoraggioHelper.FiltriOperazioniVolumiPlain filtriOV = new MonitoraggioHelper.FiltriOperazioniVolumiPlain();
	filtriOV.setDataOrarioA(tomorrowTs());
	filtriOV.setDataOrarioDa(todayTs());
	filtriOV.setFlAggiungiDocVolume(aFlag());
	filtriOV.setFlChiusuraVolume(aFlag());
	filtriOV.setFlCreaIndiceVolume(aFlag());
	filtriOV.setFlCreaVolume(aFlag());
	filtriOV.setFlEliminaVolume(aFlag());
	filtriOV.setFlErrVerifFirme(aFlag());
	filtriOV.setFlFirmaNoMarcaVolume(aFlag());
	filtriOV.setFlFirmaVolume(aFlag());
	filtriOV.setFlInizioCreaIndice(aFlag());
	filtriOV.setFlInizioVerifFirme(aFlag());
	filtriOV.setFlMarcaIndiceVolume(aFlag());
	filtriOV.setFlModificaVolume(aFlag());
	filtriOV.setFlRecuperaVolumeAperto(aFlag());
	filtriOV.setFlRecuperaVolumeInErrore(aFlag());
	filtriOV.setFlRecuperaVolumeScaduto(aFlag());
	filtriOV.setFlRimuoviDocVolume(aFlag());
	filtriOV.setFlSetVolumeAperto(aFlag());
	filtriOV.setFlSetVolumeDaChiudere(aFlag());
	filtriOV.setFlSetVolumeInErrore(aFlag());
	filtriOV.setIdAmbiente(aBigDecimal());
	filtriOV.setIdEnte(aBigDecimal());
	filtriOV.setIdStrut(aBigDecimal());
	filtriOV.setIdVolumeConserv(aBigDecimal());
	filtriOV.setTiModOper(aString());
	filtriOV.setTiOutput("ANALITICO");
	return filtriOV;
    }

    @Test
    void getElvVLisLogOperViewBean_queryIsOk() {
	MonitoraggioHelper.FiltriOperazioniElenchiVersamentoPlain filtriOE = aFiltriOperazioniElenchiVersamentoPlain();
	helper.getElvVLisLogOperViewBean(filtriOE);
	assertTrue(true);
    }

    private MonitoraggioHelper.FiltriOperazioniElenchiVersamentoPlain aFiltriOperazioniElenchiVersamentoPlain() {
	MonitoraggioHelper.FiltriOperazioniElenchiVersamentoPlain filtriOE = new MonitoraggioHelper.FiltriOperazioniElenchiVersamentoPlain();
	filtriOE.setDataOrarioA(tomorrowTs());
	filtriOE.setDataOrarioDa(todayTs());
	filtriOE.setFlChiusuraElenco(true);
	filtriOE.setFlCreaElenco(true);
	filtriOE.setFlCreaIndiceElenco(true);
	filtriOE.setFlDefNoteElencoChiuso(true);
	filtriOE.setFlDefNoteIndiceElenco(true);
	filtriOE.setFlEliminaElenco(true);
	filtriOE.setFlEndCreazioneElencoAip(true);
	filtriOE.setFlFirmaElenco(true);
	filtriOE.setFlFirmaElencoAip(true);
	filtriOE.setFlFirmaElencoAipFallita(true);
	filtriOE.setFlFirmaElencoAipInCorso(true);
	filtriOE.setFlFirmaInCorso(true);
	filtriOE.setFlFirmaInCorsoFallita(true);
	filtriOE.setFlMarcaElencoAip(true);
	filtriOE.setFlMarcaElencoAipFallita(true);
	filtriOE.setFlModElenco(true);
	filtriOE.setFlRecuperaElencoAperto(true);
	filtriOE.setFlRecuperaElencoScaduto(true);
	filtriOE.setFlRimuoviDocElenco(true);
	filtriOE.setFlRimuoviUdElenco(true);
	filtriOE.setFlSetElencoAperto(true);
	filtriOE.setFlSetElencoDaChiudere(true);
	filtriOE.setFlStartCreazioneElencoAip(true);
	filtriOE.setIdAmbiente(aBigDecimal());
	filtriOE.setIdElencoVers(aBigDecimal());
	filtriOE.setIdEnte(aBigDecimal());
	filtriOE.setIdStrut(aBigDecimal());
	filtriOE.setTiModOper(aString());
	filtriOE.setTiOutput("ANALITICO");
	return filtriOE;
    }

    @Test
    void getMonVLisOperVolOutputAggregato_queryIsOk() {
	helper.getMonVLisOperVolOutputAggregato(aFiltriOperazioniVolumiPlain());
	assertTrue(true);
    }

    @Test
    void getElvVLisLogOperOutputAggregato_queryIsOk() {
	helper.getElvVLisLogOperOutputAggregato(aFiltriOperazioniElenchiVersamentoPlain());
	assertTrue(true);
    }

    @Test
    void getMonTotSacerTable_queryIsOk() {
	List<BigDecimal> idAmbitoTerritList = aListOfBigDecimal(2);
	long idUserIam = aLong();
	MonitoraggioHelper.FiltriContenutoSacerPlain filtriCS = aFiltriContenutoSacerPlain();
	helper.getMonTotSacerTable(idAmbitoTerritList, idUserIam, filtriCS);
	assertTrue(true);
    }

    private MonitoraggioHelper.FiltriContenutoSacerPlain aFiltriContenutoSacerPlain() {
	MonitoraggioHelper.FiltriContenutoSacerPlain filtriCS = new MonitoraggioHelper.FiltriContenutoSacerPlain();
	filtriCS.setAaKeyUnitaDoc(BigDecimal.valueOf(2020));
	filtriCS.setDataRifA(tomorrowTs());
	filtriCS.setDataRifDa(todayTs());
	filtriCS.setIdAmbienteList(aListOfBigDecimal(2));
	filtriCS.setIdCategEnteList(aListOfBigDecimal(2));
	filtriCS.setIdCategStrutList(aListOfBigDecimal(2));
	filtriCS.setIdCategTipoUnitaDocList(aListOfBigDecimal(2));
	filtriCS.setIdEnteList(aListOfBigDecimal(2));
	filtriCS.setIdRegistroUnitaDocList(aListOfBigDecimal(2));
	filtriCS.setIdSottocategTipoUnitaDocList(aListOfBigDecimal(2));
	filtriCS.setIdStrutList(aListOfBigDecimal(2));
	filtriCS.setIdSubStrutList(aListOfBigDecimal(2));
	filtriCS.setIdTipoDocList(aListOfBigDecimal(2));
	filtriCS.setIdTipoUnitaDocList(aListOfBigDecimal(2));
	return filtriCS;
    }

    @Test
    void getMonTotSacerForHomeTable_queryIsOk() {
	BigDecimal idAmbiente = aBigDecimal();
	BigDecimal idEnte = aBigDecimal();
	BigDecimal idStrut = aBigDecimal();
	Date dtRifDa = todayTs();
	Date dtRifA = tomorrowTs();
	long idUserIam = aLong();

	helper.getMonTotSacerForHomeTable(idAmbiente, idEnte, idStrut, dtRifDa, dtRifA, idUserIam);
	assertTrue(true);
    }

    @Test
    void getTotalMonTotSacer_queryIsOk() throws EMFError {
	helper.getTotalMonTotSacer();
	assertTrue(true);
    }

    @Test
    void getNomeAmbienteFromId_queryIsOk() {
	BigDecimal idAmbiente = BigDecimal.valueOf(21);
	try {
	    helper.getNomeAmbienteFromId(idAmbiente);
	    assertTrue(true);
	} catch (Exception e) {
	    assertNoResultException(e);
	}
    }

    @Test
    void getNomeEnteFromId_queryIsOk() {
	BigDecimal idEnte = BigDecimal.valueOf(21);
	try {
	    helper.getNomeEnteFromId(idEnte);
	    assertTrue(true);
	} catch (Exception e) {
	    assertNoResultException(e);
	}
    }

    @Test
    void getNomeStrutturaFromId_queryIsOk() {
	BigDecimal idStruttura = aBigDecimal();
	try {
	    helper.getNomeStrutturaFromId(idStruttura);
	    assertTrue(true);
	} catch (Exception e) {
	    assertNoResultException(e);
	}
    }

    @Test
    void getNomeTipoUDFromId_queryIsOk() {
	long idTipoUD = aLong();
	try {
	    helper.getNomeTipoUDFromId(idTipoUD);
	    assertTrue(true);
	} catch (Exception e) {
	    assertNoResultException(e);
	}
    }

    @Test
    void getSessioniErrateListTB_queryIsOk() {
	String flVerificato = aFlag();
	helper.getSessioniErrateListTB(flVerificato, 100);
	assertTrue(true);
    }

    @Test
    void getSessioniErrateListTBWithoutFlagVerificato() {
	final int maxResults = 500;
	final VrsSessioneVersKoTableBean sessioniErrateListTB = helper.getSessioniErrateListTB(null,
		maxResults);
	assertTrue(sessioniErrateListTB.getLazyListInterface().getCountResultSize() > maxResults);
	assertEquals(maxResults - 1, sessioniErrateListTB.getLastRowPageIndex());
    }

    @Test
    void getMonVVisVersErrIamRowBean_queryIsOk() {
	BigDecimal idSessioneVers = aBigDecimal();
	helper.getMonVVisVersErrIamRowBean(idSessioneVers);
	assertTrue(true);
    }

    @Test
    void getMonVVisSesErrIam_queryIsOk() {
	BigDecimal idSessioneVers = aBigDecimal();
	helper.getMonVVisSesErrIam(idSessioneVers);
	assertTrue(true);
    }

    @Test
    void getFileListTableBean_queryIsOk() {
	BigDecimal idSessioneVers = aBigDecimal();

	helper.getFileListTableBean(idSessioneVers);
	assertTrue(true);
    }

    @Test
    void getLogVLisSchedViewBean_queryIsOk() {
	String nomeJob = aString();
	Date dataOrarioDa = todayTs();
	Date dataOrarioA = tomorrowTs();
	helper.getLogVLisSchedViewBeanPlainFilters(nomeJob, dataOrarioDa, dataOrarioA);
	assertTrue(true);
    }

    @Test
    void getLogVLisSchedStrutViewBean_queryIsOk() throws EMFError {
	BigDecimal idStrut = aBigDecimal();
	String nomeJob = aString();
	Date dataOrarioDa = todayTs();
	Date dataOrarioA = tomorrowTs();
	helper.getLogVLisSchedStrutViewBean(idStrut, nomeJob, dataOrarioDa, dataOrarioA);
	assertTrue(true);
    }

    @Test
    void getLogVVisLastSched_queryIsOk() {
	String nomeJob = aString();
	helper.getLogVVisLastSched(nomeJob);
	assertTrue(true);
    }

    @Test
    void getAmbiente_queryIsOk() {
	String nmAmbiente = aString();

	helper.getAmbiente(nmAmbiente);
	assertTrue(true);
    }

    @Test
    void getIdAmbiente_queryIsOk() {
	BigDecimal idEnte = aBigDecimal();

	try {
	    helper.getIdAmbiente(idEnte);
	    assertTrue(true);
	} catch (Exception e) {
	    assertNoResultException(e);
	}
    }

    @Test
    void getIdEnte_queryIsOk() {
	BigDecimal idStruttura = aBigDecimal();
	try {
	    helper.getIdEnte(idStruttura);
	    assertTrue(true);
	} catch (Exception e) {
	    assertNoResultException(e);
	}
    }

    @Test
    void contaVersFallitiDistintiUD_queryIsOk() {
	long idUtente = aLong();
	BigDecimal idAmbiente = aBigDecimal();
	BigDecimal idEnte = aBigDecimal();
	BigDecimal idStruttura = aBigDecimal();

	helper.contaVersFallitiDistintiUD(idUtente, idAmbiente, idEnte, idStruttura);
	assertTrue(true);
    }

    @Test
    void contaVersFallitiDistintiDoc_queryIsOk() {
	long idUtente = aLong();
	BigDecimal idAmbiente = aBigDecimal();
	BigDecimal idEnte = aBigDecimal();
	BigDecimal idStruttura = aBigDecimal();

	helper.contaVersFallitiDistintiDoc(idUtente, idAmbiente, idEnte, idStruttura);
	assertTrue(true);
    }

    private MonitoraggioFiltriListaVersFallitiDistintiDocBean aMonitoraggioFiltriListaVersFallitiDistintiDocBean() {
	MonitoraggioFiltriListaVersFallitiDistintiDocBean filtri = new MonitoraggioFiltriListaVersFallitiDistintiDocBean();
	filtri.setAnno(BigDecimal.valueOf(2020));
	filtri.setAnno_range_a(BigDecimal.valueOf(2019));
	filtri.setAnno_range_da(BigDecimal.valueOf(2021));
	filtri.setClasseErrore(aString());
	filtri.setCodiceErrore(aString());
	filtri.setFlNonRisolub(aFlag());
	filtri.setFlVerificato(aFlag());
	filtri.setIdAmbiente(aBigDecimal());
	filtri.setIdEnte(aBigDecimal());
	filtri.setIdStrut(aBigDecimal());
	filtri.setIdUserIam(aBigDecimal());
	filtri.setNumero("120");
	filtri.setNumero_range_a("10");
	filtri.setNumero_range_da("1");
	filtri.setRegistro(aSetOfString(2));
	filtri.setSottoClasseErrore(aString());
	filtri.setTipoLista(aString());
	return filtri;
    }

    @Test
    void getMonVVisUdNonVers_queryIsOk() {
	BigDecimal idStrut = aBigDecimal();
	String cdRegistroKeyUnitaDoc = aString();
	BigDecimal aaKeyUnitaDoc = aBigDecimal();
	String cdKeyUnitaDoc = aString();

	helper.getMonVVisUdNonVers(idStrut, cdRegistroKeyUnitaDoc, aaKeyUnitaDoc, cdKeyUnitaDoc);
	assertTrue(true);
    }

    @Test
    void getMonVVisDocNonVers_queryIsOk() {
	BigDecimal idStrut = aBigDecimal();
	String cdRegistroKeyUnitaDoc = aString();
	BigDecimal aaKeyUnitaDoc = aBigDecimal();
	String cdKeyUnitaDoc = aString();
	String cdKeyDocVers = aString();

	helper.getMonVVisDocNonVers(idStrut, cdRegistroKeyUnitaDoc, aaKeyUnitaDoc, cdKeyUnitaDoc,
		cdKeyDocVers);
	assertTrue(true);
    }

    @Test
    void getMonVLisVersUdNonVersViewBean_queryIsOk() {
	BigDecimal idStrut = aBigDecimal();
	String cdRegistroKeyUnitaDoc = aString();
	BigDecimal aaKeyUnitaDoc = aBigDecimal();
	String cdKeyUnitaDoc = aString();

	helper.getMonVLisVersUdNonVersViewBean(idStrut, cdRegistroKeyUnitaDoc, aaKeyUnitaDoc,
		cdKeyUnitaDoc);
	assertTrue(true);
    }

    @Test
    void getRegistriFromTotaleMonVLisVersErr_queryIsOk() {
	BigDecimal idAmbiente = aBigDecimal();
	BigDecimal idEnte = aBigDecimal();
	BigDecimal idStrut = aBigDecimal();
	Long idUserIam = aLong();

	helper.getRegistriFromTotaleMonVLisVersErr(idAmbiente, idEnte, idStrut, idUserIam);
	assertTrue(true);
    }

    @Test
    void getRegistriFromTotaleMonVLisUdNonVers_queryIsOk() {
	BigDecimal idAmbiente = aBigDecimal();
	BigDecimal idEnte = aBigDecimal();
	BigDecimal idStrut = aBigDecimal();
	Long idUserIam = aLong();

	helper.getRegistriFromTotaleMonVLisUdNonVers(idAmbiente, idEnte, idStrut, idUserIam);
	assertTrue(true);
    }

    @Test
    void getSessioniRecupero_queryIsOk() {
	BigDecimal idAmbiente = BigDecimal.ZERO;
	BigDecimal idEnte = BigDecimal.ZERO;
	BigDecimal idStruttura = aBigDecimal();
	Date[] dateAperture = aDateArray(2);
	Object[] chiavi = {
		aStringArray(2), aBigDecimal(), aString() };
	String tiStato = aString();
	String tiSessione = aString();
	String nmUserid = aString();
	helper.getSessioniRecupero(idAmbiente, idEnte, idStruttura, nmUserid, dateAperture, chiavi,
		tiStato, tiSessione);
	assertTrue(true);
    }

    @Test
    void getIamVLisOrganizDaReplicTableBean_queryIsOk() throws EMFError {
	BigDecimal idAmbiente = aBigDecimal();
	BigDecimal idEnte = aBigDecimal();
	BigDecimal idStrut = aBigDecimal();
	String tiOper = aString();
	String tiStato = aString();

	helper.getIamVLisOrganizDaReplicTableBeanPlainFilters(100, idAmbiente, idEnte, idStrut,
		tiOper, tiStato);
	assertTrue(true);
    }

    @Test
    void getMonVLisUniDocDaAnnulViewBean_queryIsOk() {
	long idUtente = aLong();
	MonitoraggioFiltriListaDocBean filtri = aMonitoraggioFiltriListaDocBean();
	helper.getMonVLisUniDocDaAnnulViewBean(idUtente, filtri, 100,
		l -> new MonVLisUniDocDaAnnulTableBean());
	assertTrue(true);
    }

    @Test
    @Disabled("could not resolve property: orgAmbitoTerrit of: it.eng.parer.entity.OrgEnte")
    void getTotaliUdDocComp_queryIsOk() {
	MonitoraggioHelper.FiltriContenutoSacerPlain filtriCS = aFiltriContenutoSacerPlain();
	List<BigDecimal> idAmbitoTerritList = aListOfBigDecimal(2);
	long idUserIam = aLong();
	helper.getTotaliUdDocComp(idAmbitoTerritList, idUserIam, filtriCS);
	assertTrue(true);
    }

    @Test
    void getTotaliUdDocCompForHome_queryIsOk() {
	BigDecimal idAmbiente = aBigDecimal();
	BigDecimal idEnte = aBigDecimal();
	BigDecimal idStrut = aBigDecimal();
	Date dtRifDa = todayTs();
	Date dtRifA = tomorrowTs();
	long idUserIam = aLong();

	helper.getTotaliUdDocCompForHome(idAmbiente, idEnte, idStrut, dtRifDa, dtRifA, idUserIam);
	assertTrue(true);
    }

    @Deployment
    public static Archive<?> createTestArchive() {

	final JavaArchive sacerJavaArchive = HelperTest.createSacerJavaArchive(
		Arrays.asList("org.springframework.cglib.proxy", "org.springframework.cglib.core"),
		MonitoraggioHelper.class, MonitoraggioFiltriListaDocBean.class,
		MonitoraggioFiltriListaVersFallitiBean.class, CounterResultBean.class,
		MonitoraggioFiltriListaVersFallitiDistintiDocBean.class, BlobObject.class,
		ElencoEnums.class, Constants.class, Transform.class, MonitoraggioHelperTest.class);
	return HelperTest.createEnterpriseArchive(MonitoraggioHelperTest.class.getSimpleName(),
		sacerJavaArchive, createPaginatorJavaArchive(), createSacerLogJar());
    }

    @Test
    @Disabled("ha bisogno di un idSessioneVers reale")
    void salvaDettaglio_queryIsOk() {
	final BigDecimal idSessioneVers = BigDecimal.ZERO;
	final String flSessioneErrVerif = "0";
	final String flSessioneErrNonRisolub = "0";
	final String nmAmbientePerCalcolo = "nmAmbientePerCalcolo";
	final String nmEntePerCalcolo = "nmEntePerCalcolo";
	final String nmStrutPerCalcolo = "nmStrutPerCalcolo";
	final String registroUD = "registroUD";
	final BigDecimal idStrutPerCalcolo = BigDecimal.ZERO;
	final BigDecimal annoUD = BigDecimal.valueOf(-1900);
	final String numUD = "numUD";
	final String chiaveDoc = "chiaveDoc";
	helper.salvaDettaglio(idSessioneVers, flSessioneErrVerif, flSessioneErrNonRisolub,
		nmAmbientePerCalcolo, nmEntePerCalcolo, nmStrutPerCalcolo, idStrutPerCalcolo,
		registroUD, annoUD, numUD, chiaveDoc);
	assertTrue(true);
    }

    @Test
    void getVrsSessioneVers_queryIsOk() {
	final BigDecimal idSessioneVers = BigDecimal.ZERO;
	helper.getVrsSessioneVersKo(idSessioneVers);
	assertTrue(true);
    }

    @Test
    void saveFlVerificatiNonRisolubili_queryIsOk() {
	final BigDecimal idSessioneVers = BigDecimal.ZERO;
	final String flSessioneErrVerif = "0";
	final String flSessioneErrNonRisolub = "0";
	try {
	    helper.saveFlVerificatiNonRisolubili(idSessioneVers, flSessioneErrVerif,
		    flSessioneErrNonRisolub);
	    fail("non deve trovare nulla con id 0");
	} catch (Exception e) {
	    assertNoResultException(e);
	}
    }

    @Test
    void getBlobboByteList_queryIsOk() {
	final BigDecimal idSessioneVers = BigDecimal.ZERO;
	try {
	    helper.getBlobboByteList(idSessioneVers);
	    fail("non deve trovare nessuna sessione con id 0");
	} catch (Exception e) {
	    assertNoResultException(e);
	}
	assertTrue(true);
    }

    @Test
    void getLogVLisSchedViewBeanPlainFilters_queryIsOk() {
	final String nmJob = "nmJob";
	final Timestamp dataOrarioDa = todayTs();
	final Timestamp dataOrarioA = tomorrowTs();
	helper.getLogVLisSchedViewBeanPlainFilters(nmJob, dataOrarioDa, dataOrarioA);
	assertTrue(true);
    }

    @Test
    void getIamVLisOrganizDaReplicTableBeanPlainFilters_queryIsOk() {
	final int maxResult = 100;
	final BigDecimal idAmbiente = BigDecimal.ZERO;
	final BigDecimal idEnte = BigDecimal.ZERO;
	final BigDecimal idStrut = BigDecimal.ZERO;
	final String tiOper = "tiOper";
	final String tiStato = "tiStato";
	helper.getIamVLisOrganizDaReplicTableBeanPlainFilters(maxResult, idAmbiente, idEnte,
		idStrut, tiOper, tiStato);
	assertTrue(true);
    }

    @Test
    void getListaDifferenzaConsistenzaComp_queryIsOk() {
	final BigDecimal idStrut = BigDecimal.ZERO;
	final Timestamp dtRifContaDa = todayTs();
	final Timestamp dtRifContaA = tomorrowTs();
	helper.getListaDifferenzaConsistenzaComp(idStrut, dtRifContaDa, dtRifContaA);
	assertTrue(true);
    }

    @Test
    void getListaDifferenzaConsistenzaVsCalcoloSacer_queryIsOk() {
	final BigDecimal idStrut = BigDecimal.ZERO;
	final Timestamp dtRifContaDa = todayTs();
	final Timestamp dtRifContaA = tomorrowTs();
	helper.getListaDifferenzaConsistenzaVsCalcoloSacer(idStrut, dtRifContaDa, dtRifContaA);
	assertTrue(true);
    }

    @Test
    void getXmlsSesErr_queryIsOk() {
	try {
	    helper.getXmlsSesErr(BigDecimal.ZERO);
	    fail("non deve trovare sessioni con id 0");
	} catch (Exception e) {
	    assertNoResultException(e);
	}
    }

    @Test
    void getXmlsVersErr_queryIsOk() {
	try {
	    helper.getXmlsVersErr(BigDecimal.ZERO);
	    fail("non deve trovare sessioni con id 0");
	} catch (Exception e) {
	    assertNoResultException(e);
	}
    }

    @Test
    void getLastPositiveRunCalcoloContenutoSacer_queryIsOk() {
	final Calendar lastPositiveRunCalcoloContenutoSacer = helper
		.getLastPositiveRunCalcoloContenutoSacer();
	assertNotNull(lastPositiveRunCalcoloContenutoSacer);
	assertTrue(Calendar.getInstance().after(lastPositiveRunCalcoloContenutoSacer));
    }
}
