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

package it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper;

import static it.eng.ArquillianUtils.aBigDecimal;
import static it.eng.ArquillianUtils.aBoolean;
import static it.eng.ArquillianUtils.aListOfBigDecimal;
import static it.eng.ArquillianUtils.aLong;
import static it.eng.ArquillianUtils.aOrgStrut;
import static it.eng.ArquillianUtils.aSetOfBigDecimal;
import static it.eng.ArquillianUtils.aString;
import static it.eng.ArquillianUtils.assertNoResultException;
import static it.eng.ArquillianUtils.todayTs;
import static it.eng.ArquillianUtils.tomorrowTs;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.parer.entity.OrgStrut;
import it.eng.parer.job.allineamentoEntiConvenzionati.utils.CostantiAllineaEntiConv;
import it.eng.parer.web.helper.HelperTest;
import it.eng.parer.web.util.Constants;

@ArquillianTest
public class StruttureHelperTest {
    @EJB
    private StruttureHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
	return HelperTest.createEnterpriseArchive(StruttureHelperTest.class.getSimpleName(),
		HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
		HelperTest.createSacerJavaArchive(Arrays.asList(""), StruttureHelperTest.class,
			StruttureHelper.class, Constants.class, CostantiAllineaEntiConv.class));
    }

    @Test
    void retrieveOrgStrutList_4args_queryIsOk() {
	String nmStrut = aString();
	BigDecimal idEnte = aBigDecimal();
	BigDecimal idAmbiente = aBigDecimal();
	Boolean isTemplate = true;
	helper.retrieveOrgStrutList(nmStrut, idEnte, idAmbiente, isTemplate);
	assertTrue(true);
    }

    @Test
    void retrieveOrgStrutList_long_BigDecimal_queryIsOk() {
	long idUtente = aLong();
	BigDecimal idEnte = aBigDecimal();
	helper.retrieveOrgStrutList(idUtente, idEnte, true);
	assertTrue(true);
    }

    @Test
    void retrieveOrgVRicStrutList_queryIsOk() {
	String nmStrut = aString();
	BigDecimal idEnte = aBigDecimal();
	BigDecimal idAmbiente = aBigDecimal();
	Boolean isTemplate = aBoolean();
	String partizionata = aString();
	String nmSistemaVersante = aString();
	BigDecimal idAmbitoTerrit = aBigDecimal();
	BigDecimal idCategEnte = aBigDecimal();
	BigDecimal idAmbienteEnteConvenz = aBigDecimal();
	BigDecimal idEnteConvenz = aBigDecimal();
	long idUserIamCor = aLong();
	String flParametriStrut = aString();
	helper.retrieveOrgVRicStrutList(nmStrut, idEnte, idAmbiente, isTemplate, partizionata,
		nmSistemaVersante, idAmbitoTerrit, idCategEnte, idAmbienteEnteConvenz,
		idEnteConvenz, flParametriStrut, idUserIamCor);
	assertTrue(true);
    }

    @Test
    void retrieveOrgStrutList_6args_queryIsOk() {
	long idUtente = aLong();
	String nmStrut = aString();
	BigDecimal idEnte = aBigDecimal();
	BigDecimal idAmbiente = aBigDecimal();
	BigDecimal idModelloTipoSerie = aBigDecimal();
	Boolean isTemplate = aBoolean();
	helper.retrieveOrgStrutList(idUtente, nmStrut, idEnte, idAmbiente, idModelloTipoSerie,
		isTemplate, true);
	assertTrue(true);
    }

    @Test
    void retrieveOrgStrutList_3args_queryIsOk() {
	long idUtente = aLong();
	Collection<BigDecimal> idEntiSet = aSetOfBigDecimal(2);
	Collection<BigDecimal> idCategStrutList = aSetOfBigDecimal(2);
	helper.retrieveOrgStrutList(idUtente, idEntiSet, idCategStrutList);
	assertTrue(true);
    }

    @Test
    void getidStrutFromIdTipoStrutUnitaDoc_queryIsOk() {
	BigDecimal idTipoStrutUnitaDoc = aBigDecimal();
	try {
	    helper.getidStrutFromIdTipoStrutUnitaDoc(idTipoStrutUnitaDoc);
	    assertTrue(true);
	} catch (Exception e) {
	    assertNoResultException(e);
	}
    }

    @Test
    void getOrgStrutByName_queryIsOk() {
	String nmStrut = aString();
	BigDecimal idEnte = aBigDecimal();
	helper.getOrgStrutByName(nmStrut, idEnte);
	assertTrue(true);
    }

    @Test
    void getOrgSubStrutByName_queryIsOk() {
	String nmSubStrut = aString();
	OrgStrut orgStrut = aOrgStrut();
	helper.getOrgSubStrutByName(nmSubStrut, orgStrut);
	assertTrue(true);
    }

    @Test
    void getFirstOrgStrutTemplate_queryIsOk() {
	helper.getFirstOrgStrutTemplate();
	assertTrue(true);
    }

    @Test
    void getFirstOrgStrutTemplatePerAmbienteAndTipoDefTemplateEnte_queryIsOk() {
	BigDecimal idAmbiente = aBigDecimal();
	String tipoDefTemplateEnte = aString();
	helper.getFirstOrgStrutTemplatePerAmbienteAndTipoDefTemplateEnte(idAmbiente,
		tipoDefTemplateEnte);
	assertTrue(true);
    }

    @Test
    void getFirstOrgStrutTemplatePerAmbienteAndTipoDefTemplateEntePartizionata_queryIsOk() {
	BigDecimal idAmbiente = aBigDecimal();
	String tipoDefTemplateEnte = aString();
	helper.getFirstOrgStrutTemplatePerAmbienteAndTipoDefTemplateEntePartizionata(idAmbiente,
		tipoDefTemplateEnte);
	assertTrue(true);
    }

    @Test
    void getFirtsOrgStrutTemplatePerEntePartizionata_queryIsOk() {
	BigDecimal idEnte = aBigDecimal();
	helper.getFirtsOrgStrutTemplatePerEntePartizionata(idEnte);
	assertTrue(true);
    }

    @Test
    void countOrgStrutTemplateRaggruppati_queryIsOk() {
	long idUserIam = aLong();
	helper.countOrgStrutTemplateRaggruppati(idUserIam);
	assertTrue(true);
    }

    @Test
    void countOrgStrutTemplatePerAmbienteEnte_queryIsOk() {
	Long idAmbiente = aLong();
	Long idEnte = aLong();
	String tipoDefTemplateEnte = aString();
	helper.countOrgStrutTemplatePerAmbienteEnte(idAmbiente, idEnte, tipoDefTemplateEnte);
	assertTrue(true);
    }

    @Test
    void countOrgStrutTemplateWithCompletedPartitioningRaggruppati_queryIsOk() {
	long idUserIam = aLong();
	helper.countOrgStrutTemplateWithCompletedPartitioningRaggruppati(idUserIam);
	assertTrue(true);
    }

    @Test
    void getOrgCategStrutList_queryIsOk() {
	String cdCategStrut = aString();
	String dsCategStrut = aString();
	helper.getOrgCategStrutList(cdCategStrut, dsCategStrut);
	assertTrue(true);
    }

    @Test
    void getOrgCategStrutByCd_queryIsOk() {
	String cdCategStrut = aString();
	helper.getOrgCategStrutByCd(cdCategStrut);
	assertTrue(true);
    }

    @Test
    void hasAroUnitaDoc_queryIsOk() {
	BigDecimal idStrut = aBigDecimal();
	helper.hasAroUnitaDoc(idStrut);
	assertTrue(true);
    }

    @Test
    void getRelationsWithCriteriRaggruppamento_queryIsOk() {
	long idTipoDato = aLong();
	Constants.TipoDato tipoDato = Constants.TipoDato.REGISTRO;
	helper.getRelationsWithCriteriRaggruppamento(idTipoDato, tipoDato);
	assertTrue(true);

	tipoDato = Constants.TipoDato.TIPO_UNITA_DOC;
	helper.getRelationsWithCriteriRaggruppamento(idTipoDato, tipoDato);
	assertTrue(true);

	tipoDato = Constants.TipoDato.TIPO_DOC;
	helper.getRelationsWithCriteriRaggruppamento(idTipoDato, tipoDato);
	assertTrue(true);
    }

    @Test
    void checkManyRelationsAreEmptyForStruttura_queryIsOk() {
	long idStrut = aLong();
	helper.checkManyRelationsAreEmptyForStruttura(idStrut);
	assertTrue(true);
    }

    @Test
    void existsRelationsWithElenchiVolumiForCriterioRaggruppamento_queryIsOk() {
	long idCriterioRaggr = aLong();
	helper.existsRelationsWithElenchiVolumiForCriterioRaggruppamento(idCriterioRaggr);
	assertTrue(true);
    }

    @Test
    void existsRelationsWithElenchiForCriterioRaggrFasc_queryIsOk() {
	long idCriterioRaggrFasc = aLong();
	helper.existsRelationsWithElenchiForCriterioRaggrFasc(idCriterioRaggrFasc);
	assertTrue(true);
    }

    @Test
    void partitionOK_queryIsOk() {
	BigDecimal idStrut = aBigDecimal();
	try {
	    helper.partitionOK(idStrut);
	    assertTrue(true);
	} catch (Exception e) {
	    assertNoResultException(e);
	}
    }

    @Test
    void getProgressiviTemplatePresentiSuDB_queryIsOk() {
	helper.getProgressiviTemplatePresentiSuDB();
	assertTrue(true);
    }

    @Test
    void getDecVChkFmtNumeroForPeriodo_queryIsOk() {
	long idAaRegistroUnitaDoc = aLong();
	helper.getDecVChkFmtNumeroForPeriodo(idAaRegistroUnitaDoc);
	assertTrue(true);
    }

    @Test
    void getIdStrutAbilitatiFromAmbienteSet_queryIsOk() {
	long idUserIam = aLong();
	Set<? extends BigDecimal> idAmbientiSet = aSetOfBigDecimal(2);
	helper.getIdStrutAbilitatiFromAmbienteSet(idUserIam, idAmbientiSet);
	assertTrue(true);
    }

    @Test
    void getIdStrutAbilitatiFromEnteSet_queryIsOk() {
	long idUserIam = aLong();
	Set<? extends BigDecimal> idEntiSet = aSetOfBigDecimal(2);
	helper.getIdStrutAbilitatiFromEnteSet(idUserIam, idEntiSet);
	assertTrue(true);
    }

    @Test
    void getAmbEnteStrutDefault_queryIsOk() {
	long idUtente = aLong();
	helper.getAmbEnteStrutDefault(idUtente);
	assertTrue(true);
    }

    @Test
    void countOrgStrut_queryIsOk() {
	BigDecimal idEnte = aBigDecimal();
	helper.countOrgStrut(idEnte);
	assertTrue(true);
    }

    @Test
    void haFigliPresentiInSottoLivelloOnlineList_queryIsOk() {
	BigDecimal idPadre = aBigDecimal();
	List<BigDecimal> figliQualunquePresentiInOnline = aListOfBigDecimal(2);
	helper.haFigliPresentiInSottoLivelloOnlineList(idPadre, figliQualunquePresentiInOnline);
	assertTrue(true);
    }

    @Test
    void getIdAmbitoTerritChildList_queryIsOk() {
	BigDecimal idAmbitoTerrit = aBigDecimal();
	helper.getIdAmbitoTerritChildList(idAmbitoTerrit);
	assertTrue(true);
    }

    @Test
    void getIdAmbienteEnteSiamByStrut_queryIsOk() {
	BigDecimal idStrut = aBigDecimal();
	helper.getIdAmbienteEnteSiamByStrut(idStrut);
	assertTrue(true);
    }

    @Test
    void checkEsistenzaAssociazioneEnteConvenzStrutVers_queryIsOk() {
	String nmApplic = aString();
	BigDecimal idStrut = aBigDecimal();
	Date dtIniVal = todayTs();
	Date dtFineVal = tomorrowTs();
	BigDecimal idEnteConvenzOrg = aBigDecimal();
	helper.checkEsistenzaAssociazioneEnteConvenzStrutVers(nmApplic, idStrut, dtIniVal,
		dtFineVal, idEnteConvenzOrg);
	assertTrue(true);
    }

    @Test
    void checkEsistenzaPeriodoValiditaAssociazioneEnteConvenzStrutVers_queryIsOk() {
	BigDecimal idEnteConvenz = aBigDecimal();
	Date dtIniVal = todayTs();
	Date dtFineVal = tomorrowTs();
	helper.checkEsistenzaPeriodoValiditaAssociazioneEnteConvenzStrutVers(idEnteConvenz,
		dtIniVal, dtFineVal);
	assertTrue(true);
    }

    @Test
    void getIamEnteConvenzDaAllinea_queryIsOk() {
	helper.getIamEnteConvenzDaAllinea();
	assertTrue(true);
    }

    @Test
    void retrieveOrgStrutList_0args_queryIsOk() {
	helper.retrieveOrgStrutList();
	assertTrue(true);
    }

    @Test
    void existsCdStrutNormaliz_queryIsOk() {
	String cdStrutNormaliz = aString();
	BigDecimal idEnte = aBigDecimal();
	BigDecimal idStrutExcluded = aBigDecimal();
	helper.existsCdStrutNormaliz(cdStrutNormaliz, idEnte, idStrutExcluded);
	assertTrue(true);
    }

    @Test
    void isCodStrutturaNormalizzatoUnivoco_queryIsOk() {
	String cdStrutNormaliz = aString();
	helper.isCodStrutturaNormalizzatoUnivoco(cdStrutNormaliz);
	assertTrue(true);
    }

    @Test
    void getFunzioneParametri_queryIsOk() {
	helper.getFunzioneParametri();
	assertTrue(true);
    }
}
