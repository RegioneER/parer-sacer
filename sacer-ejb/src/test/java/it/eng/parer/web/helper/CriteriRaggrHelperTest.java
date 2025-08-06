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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import it.eng.parer.entity.DecCriterioRaggr;
import it.eng.parer.entity.DecRegistroUnitaDoc;
import it.eng.parer.entity.DecTipoDoc;
import it.eng.parer.entity.DecTipoUnitaDoc;
import it.eng.parer.exception.ParerUserError;
import it.eng.parer.slite.gen.form.CriteriRaggruppamentoForm;
import it.eng.parer.web.util.ApplEnum;
import it.eng.parer.web.util.Constants;
import it.eng.spagoCore.error.EMFError;

public class CriteriRaggrHelperTest extends HelperTest<CriteriRaggrHelper> {
    @Deployment
    public static Archive<?> createTestArchive() {
	return HelperTest.createEnterpriseArchive(CriteriRaggrHelperTest.class.getSimpleName(),
		HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
		HelperTest.createSacerJavaArchive(Arrays.asList(""), CriteriRaggrHelper.class,
			Constants.class, ApplEnum.class, CriteriRaggrHelperTest.class,
			ConfigurationHelper.class));
    }

    @Test
    void getDecCriterioRaggrByStrutturaCorrenteAndCriterio_queryIsOk() {
	BigDecimal idStrutCorrente = aBigDecimal();
	String nmCriterioRaggr = aString();
	helper.getDecCriterioRaggrByStrutturaCorrenteAndCriterio(idStrutCorrente, nmCriterioRaggr);
	assertTrue(true);
    }

    @Test
    void getDecCriterioRaggrByStrutturaAndCriterio_queryIsOk() {
	BigDecimal idStruttura = aBigDecimal();
	String nome = aString();
	try {
	    helper.getDecCriterioRaggrByStrutturaAndCriterio(idStruttura, nome);
	    assertTrue(true);
	} catch (Exception e) {
	    assertNoResultException(e);
	}
    }

    @Test
    void areAllTipiDocPrincipali_queryIsOk() {
	List<BigDecimal> idTipiDocumentoList = aListOfBigDecimal(2);
	helper.areAllTipiDocPrincipali(idTipiDocumentoList);
	assertTrue(true);
    }

    @Test
    void existNomeCriterio_queryIsOk() {
	String nome = aString();
	BigDecimal idStruttura = aBigDecimal();
	helper.existNomeCriterio(nome, idStruttura);
	assertTrue(true);
    }

    @Test
    void getCriteriRaggr_queryIsOk() {
	BigDecimal idAmbiente = aBigDecimal();
	BigDecimal idEnte = aBigDecimal();
	String nmCriterioRaggr = aString();
	String flCriterioRaggrStandard = aString();
	String flCriterioRaggrFisc = aString();
	String tiValidElenco = aString();
	String tiModValidElenco = aString();
	String tiGestElencoCriterio = aString();
	BigDecimal idRegistroUnitaDoc = aBigDecimal();
	BigDecimal idTipoUnitaDoc = aBigDecimal();
	BigDecimal idTipoDoc = aBigDecimal();
	BigDecimal aaKeyUnitaDoc = aBigDecimal();
	String criterioAttivo = aString();
	long idUtente = aBigDecimal().longValue();

	helper.getCriteriRaggr(idAmbiente, idEnte, idAmbiente, nmCriterioRaggr,
		flCriterioRaggrStandard, flCriterioRaggrFisc, tiValidElenco, tiModValidElenco,
		tiGestElencoCriterio, idRegistroUnitaDoc, idTipoUnitaDoc, idTipoDoc, aaKeyUnitaDoc,
		criterioAttivo, idUtente);
	assertTrue(true);
    }

    @Test
    void retrieveDecCriterioRaggrList_queryIsOk() {
	BigDecimal idAmbiente = aBigDecimal();
	BigDecimal idEnte = aBigDecimal();
	BigDecimal idStrut = aBigDecimal();
	String nmCriterioRaggr = aString();
	helper.retrieveDecCriterioRaggrList(idAmbiente, idEnte, idStrut, nmCriterioRaggr);
	assertTrue(true);
    }

    @Test
    void getCriteriRaggrFiltri_queryIsOk() {
	BigDecimal idCriterioRaggr = aBigDecimal();
	String tiFiltroMultiplo = aString();
	helper.getCriteriRaggrFiltri(idCriterioRaggr, tiFiltroMultiplo);
	assertTrue(true);
    }

    @Test
    void getCriteriRaggrbyId_queryIsOk() {
	BigDecimal id = aBigDecimal();
	BigDecimal idStruttura = aBigDecimal();
	helper.getCriteriRaggrbyId(id, idStruttura);
	assertTrue(true);
    }

    @Test
    void getDecVRicCriterioRaggrsByAmministrazioneStruttura_queryIsOk() {
	BigDecimal idStrut = aBigDecimal();
	List<BigDecimal> idRegistroUnitaDocList = aListOfBigDecimal(2);
	List<BigDecimal> idTipoUnitaDocList = aListOfBigDecimal(2);
	List<BigDecimal> idTipoDocList = aListOfBigDecimal(2);
	helper.getDecVRicCriterioRaggrsByAmministrazioneStruttura(idStrut, idRegistroUnitaDocList,
		idTipoUnitaDocList, idTipoDocList);
	assertTrue(true);
    }

    @Test
    void getDecVRicCriterioRaggrsByStruttura_queryIsOk() {
	BigDecimal idStrut = aBigDecimal();
	helper.getDecVRicCriterioRaggrsByStruttura(idStrut, true);
	assertTrue(true);
	helper.getDecVRicCriterioRaggrsByStruttura(idStrut, false);
	assertTrue(true);
    }

    @Test
    void getDecVRicCriterioRaggrsByRegistroNoRange_queryIsOk() {
	BigDecimal idStrut = aBigDecimal();
	BigDecimal idRegistroUnitaDoc = aBigDecimal();
	helper.getDecVRicCriterioRaggrsByRegistroNoRange(idStrut, idRegistroUnitaDoc);
	assertTrue(true);
    }

    @Test
    void getDecVRicCriterioRaggrsByTipoUnitaDoc_queryIsOk() {
	BigDecimal idStrut = aBigDecimal();
	BigDecimal idTipoUnitaDoc = aBigDecimal();
	helper.getDecVRicCriterioRaggrsByTipoUnitaDoc(idStrut, idTipoUnitaDoc);
	assertTrue(true);
    }

    @Test
    void getDecVRicCriterioRaggrsByTipoDoc_queryIsOk() {
	BigDecimal idStrut = aBigDecimal();
	BigDecimal idTipoDoc = aBigDecimal();
	helper.getDecVRicCriterioRaggrsByTipoDoc(idStrut, idTipoDoc);
	assertTrue(true);
    }

    @Test
    void existElvElencoVersPerCriterioRaggr_queryIsOk() {
	BigDecimal idCriterioRaggr = aBigDecimal();
	helper.existElvElencoVersPerCriterioRaggr(idCriterioRaggr);
	assertTrue(true);
    }

    @Test
    void getDecCriterioRaggrRegistroOTipiUdAssociatiList_queryIsOk() {
	BigDecimal idRegistroUnitaDoc = aBigDecimal();
	int aaKeyUnitaDoc = aInt();
	helper.getDecCriterioRaggrRegistroOTipiUdAssociatiList(idRegistroUnitaDoc, aaKeyUnitaDoc);
	assertTrue(true);
    }

    @Test
    void getCriterioStandardPerTipoDatoAnno_queryIsOk() {
	long idTipoDato = aLong();
	helper.getCriterioStandardPerTipoDatoAnno(idTipoDato, Constants.TipoDato.REGISTRO);
	assertTrue(true);
	helper.getCriterioStandardPerTipoDatoAnno(idTipoDato, Constants.TipoDato.TIPO_DOC);
	assertTrue(true);
	helper.getCriterioStandardPerTipoDatoAnno(idTipoDato, Constants.TipoDato.TIPO_UNITA_DOC);
	assertTrue(true);
    }

    @Test
    void existsCriterioPerTipoDato_queryIsOk() {
	Long idTipoDato = aLong();
	String flCriterioRaggrStandard = aString();
	String flCriterioRaggrFisc = aString();
	helper.existsCriterioPerTipoDato(idTipoDato, Constants.TipoDato.REGISTRO,
		flCriterioRaggrStandard, flCriterioRaggrFisc);
	assertTrue(true);
	helper.existsCriterioPerTipoDato(idTipoDato, Constants.TipoDato.TIPO_DOC,
		flCriterioRaggrStandard, flCriterioRaggrFisc);
	assertTrue(true);
	helper.existsCriterioPerTipoDato(idTipoDato, Constants.TipoDato.TIPO_UNITA_DOC,
		flCriterioRaggrStandard, flCriterioRaggrFisc);
	assertTrue(true);
    }

    @Test
    void getCriteriPerTipoDato_queryIsOk() {
	Long idTipoDato = aLong();
	String flCriterioRaggrStandard = aString();
	String flCriterioRaggrFisc = aString();
	helper.getCriteriPerTipoDato(idTipoDato, Constants.TipoDato.REGISTRO,
		flCriterioRaggrStandard, flCriterioRaggrFisc);
	assertTrue(true);
	helper.getCriteriPerTipoDato(idTipoDato, Constants.TipoDato.TIPO_DOC,
		flCriterioRaggrStandard, flCriterioRaggrFisc);
	assertTrue(true);
	helper.getCriteriPerTipoDato(idTipoDato, Constants.TipoDato.TIPO_UNITA_DOC,
		flCriterioRaggrStandard, flCriterioRaggrFisc);
	assertTrue(true);
    }

    @Test
    void getCriteriPerAssociazioneRegistroTipoUd_queryIsOk() {
	Long idRegistroUnitaDoc = aLong();
	Long idTipoUnitaDoc = aLong();
	helper.getCriteriPerAssociazioneRegistroTipoUd(idRegistroUnitaDoc, idTipoUnitaDoc);
	assertTrue(true);
    }

    @Test
    void getDecVCreaCritRaggrRegistro_queryIsOk() {
	Long idRegistroUnitaDoc = aLong();
	try {
	    helper.getDecVCreaCritRaggrRegistro(idRegistroUnitaDoc);
	    assertTrue(true);
	} catch (Exception e) {
	    assertNoResultException(e);
	}
    }

    @Test
    void getDecVCreaCritRaggrTipoUd_queryIsOk() {
	Long idTipoUnitaDoc = aLong();
	try {
	    helper.getDecVCreaCritRaggrTipoUd(idTipoUnitaDoc);
	    assertTrue(true);
	} catch (Exception e) {
	    assertNoResultException(e);
	}
    }

    @Test
    void getDecVCreaCritRaggrTipoDoc_queryIsOk() {
	Long idTipoDoc = aLong();
	try {
	    helper.getDecVCreaCritRaggrTipoDoc(idTipoDoc);
	    assertTrue(true);
	} catch (Exception e) {
	    assertNoResultException(e);
	}
    }

    @Test
    void areAllRegistriFiscali_queryIsOk() throws ParerUserError {
	List<BigDecimal> idRegistroKeyUnitaDocList = aListOfBigDecimal(2);
	helper.areAllRegistriFiscali(idRegistroKeyUnitaDocList);
	assertTrue(true);
    }

    @Test
    void areAllRegistriAssociatiFiscali_queryIsOk() throws ParerUserError {
	List<BigDecimal> idTipoUnitaDocList = aListOfBigDecimal(2);
	helper.areAllRegistriAssociatiFiscali(idTipoUnitaDocList);
	assertTrue(true);
    }

    @Test
    void getFlCriterioRaggrFiscMessage_queryIsOk() {
	BigDecimal idCriterioRaggr = aBigDecimal();
	try {
	    helper.getFlCriterioRaggrFiscMessage(idCriterioRaggr);
	    assertTrue(true);
	} catch (Exception e) {
	    assertNoResultException(e);
	}
    }

    @Test
    void getCriteriNonCoerenti_queryIsOk() {
	BigDecimal idTipoUnitaDoc = aBigDecimal();
	helper.getCriteriNonCoerenti(idTipoUnitaDoc);
	assertTrue(true);
    }

    private DecCriterioRaggr aDeckCriterioRaggr() {
	DecCriterioRaggr record = new DecCriterioRaggr();
	record.setIdCriterioRaggr(aLong());
	return record;
    }

    @Test
    void getDecCriterioFiltroMultiploByDecCriterioRaggrAndTipoEsitoVerifFirme_queryIsOk() {
	DecCriterioRaggr record = aDeckCriterioRaggr();
	String tipo = aString();
	helper.getDecCriterioFiltroMultiploByDecCriterioRaggrAndTipoEsitoVerifFirme(record, tipo);
	assertTrue(true);
    }

    @Test
    void getDecCriterioFiltroMultiploByDecRegistroUnitaDoc_queryIsOk() {
	DecCriterioRaggr record = aDeckCriterioRaggr();
	DecRegistroUnitaDoc reg = aDecRegistroUnitaDoc();
	helper.getDecCriterioFiltroMultiploByDecRegistroUnitaDoc(record, reg);
	assertTrue(true);
    }

    private DecRegistroUnitaDoc aDecRegistroUnitaDoc() {
	DecRegistroUnitaDoc reg = new DecRegistroUnitaDoc();
	reg.setIdRegistroUnitaDoc(aLong());
	return reg;
    }

    @Test
    void getDecRegistroUnitaDocByIdRegistroUnitaDoc_queryIsOk() {
	List<Long> cdRegistroKeyUnitaDocList = aListOfLong(2);
	helper.getDecRegistroUnitaDocByIdRegistroUnitaDoc(cdRegistroKeyUnitaDocList);
	assertTrue(true);
    }

    @Test
    void getDecCriterioFiltroMultiploByDecCriterioRaggr_queryIsOk() {
	DecCriterioRaggr record = aDeckCriterioRaggr();
	String tipo = aString();
	helper.getDecCriterioFiltroMultiploByDecCriterioRaggr(record, tipo);
	assertTrue(true);
    }

    @Test
    void getOrgUsoSistemaMigrazByNmSistemaMigrazList_queryIsOk() {
	BigDecimal idStruttura = aBigDecimal();
	List<String> nmSistemaMigrazList = aListOfString(2);
	helper.getOrgUsoSistemaMigrazByNmSistemaMigrazList(idStruttura, nmSistemaMigrazList);
	assertTrue(true);
    }

    @Test
    void getDecCriterioFiltroMultiplosByTipoDocAndDecCriterio_queryIsOk() {
	DecCriterioRaggr record = aDeckCriterioRaggr();
	DecTipoDoc tipo = null;
	helper.getDecCriterioFiltroMultiplosByTipoDocAndDecCriterio(record, tipo);
	assertTrue(true);
    }

    @Test
    void getDecTipoDocsByTipoDocList_queryIsOk() {
	List<Long> nmTipoDocList = aListOfLong(2);
	helper.getDecTipoDocsByTipoDocList(nmTipoDocList);
	assertTrue(true);
    }

    @Test
    void getDecCriterioFiltroMultiploList_queryIsOk() {
	DecCriterioRaggr record = aDeckCriterioRaggr();
	DecTipoUnitaDoc tipo = aDecTipoUnitaDoc();
	helper.getDecCriterioFiltroMultiploList(record, tipo);
	assertTrue(true);
    }

    private DecTipoUnitaDoc aDecTipoUnitaDoc() {
	DecTipoUnitaDoc tipo = new DecTipoUnitaDoc();
	tipo.setIdTipoUnitaDoc(aLong());
	return tipo;
    }

    @Test
    void getDecTipoUnitaDocsByTipoUnitaDoc_queryIsOk() {
	List<Long> asList = aListOfLong(2);
	helper.getDecTipoUnitaDocsByTipoUnitaDoc(asList);
	assertTrue(true);
    }

    @Test
    void getOrgStrutById_queryIsOk() {
	BigDecimal idStruttura = aBigDecimal();
	try {
	    helper.getOrgStrutById(idStruttura);
	    assertTrue(true);
	} catch (Exception e) {
	    assertNoResultException(e);
	}
    }

    @Test
    @Disabled("vanno gestiti i filtri e relativo parse")
    void saveCritRaggr_queryIsOk() throws ParerUserError, EMFError {
	final CriteriRaggruppamentoForm.CreaCriterioRaggr creaCriterioRaggr = new CriteriRaggruppamentoForm.CreaCriterioRaggr();
	final Date[] dateCreazioneValidate = {
		todayTs() };
	final BigDecimal idStruttura = BigDecimal.ZERO;
	final String nome = "nome";
	final String criterioStandard = "criterioStandard";
	helper.saveCritRaggr(aLogParam(), creaCriterioRaggr, dateCreazioneValidate, idStruttura,
		nome, criterioStandard);
	assertTrue(true);
    }

    @Test
    void deleteDecCriterioRaggr_queryIsOk() throws ParerUserError {
	try {
	    helper.deleteDecCriterioRaggr(aLogParam(), BigDecimal.ZERO, "nmCriterioRaggr");
	    fail("non dovrebbe trovare DecCritierioRaggr con id 0");
	} catch (Exception e) {
	    assertNoResultException(e);
	}
    }

    @Test
    void getDecCriterioRaggrById_queryIsOk() {
	helper.getDecCriterioRaggrById(BigDecimal.ZERO);
	assertTrue(true);
    }

    @Test
    void getCriteriRaggrFromAmministrazioneStruttura_queryIsOk() {
	helper.getCriteriRaggrFromAmministrazioneStruttura(BigDecimal.ZERO, aListOfBigDecimal(2),
		aListOfBigDecimal(2), aListOfBigDecimal(2));
	assertTrue(true);
    }

    @Test
    void getCriteriRaggrFromStruttura_queryIsOk() {
	helper.getCriteriRaggrFromStruttura(BigDecimal.ZERO, false);
	helper.getCriteriRaggrFromStruttura(BigDecimal.ZERO, true);
	assertTrue(true);
    }

    @Test
    void getCriteriRaggrFromRegistroNoRange_queryIsOk() {
	helper.getCriteriRaggrFromRegistroNoRange(BigDecimal.ZERO, BigDecimal.ZERO);
	assertTrue(true);
    }

    @Test
    void getCriteriRaggrFromTipoUnitaDoc_queryIsOk() {
	helper.getCriteriRaggrFromTipoUnitaDoc(BigDecimal.ZERO, BigDecimal.ZERO);
	assertTrue(true);
    }

    @Test
    void getCriteriRaggrFromTipoDoc_queryIsOk() {
	helper.getCriteriRaggrFromTipoDoc(BigDecimal.ZERO, BigDecimal.ZERO);
	assertTrue(true);
    }

    @Test
    void bulkDeleteCriteriRaggr_queryIsOk() {
	helper.bulkDeleteCriteriRaggr(aListOfLong(2));
	assertTrue(true);
    }

    @Test
    void deleteDecCriterioFiltroMultiploTipoEsitoVerifFirmeByDecCriterioRaggr_queryIsOk() {
	helper.deleteDecCriterioFiltroMultiploTipoEsitoVerifFirmeByDecCriterioRaggr(
		aDecCriterioRaggr());
	assertTrue(true);
    }

    @Test
    void deleteDecCriterioFiltroMultiploByDecCriterioRaggrNotInEsitoVerifFirme_queryIsOk() {
	helper.deleteDecCriterioFiltroMultiploByDecCriterioRaggrNotInEsitoVerifFirme(
		aDecCriterioRaggr(), aListOfString(2));
	assertTrue(true);
    }

    @Test
    void deleteDecCriterioFiltroMultiploTipoRegistroUniDocByDecCriterioRaggr_queryIsOk() {
	helper.deleteDecCriterioFiltroMultiploTipoRegistroUniDocByDecCriterioRaggr(
		aDecCriterioRaggr());
	assertTrue(true);
    }

    @Test
    void deleteDecCriterioFiltroMultiploByDecCriterioRaggrNotInIdRegistroUnitaDoc_queryIsOk() {
	helper.deleteDecCriterioFiltroMultiploByDecCriterioRaggrNotInIdRegistroUnitaDoc(
		aDecCriterioRaggr(), aListOfLong(2));
	assertTrue(true);
    }

    @Test
    void deleteDecCriterioFiltroMultiploTipoSistemaMigrazByDecCriterioRaggr_queryIsOk() {
	helper.deleteDecCriterioFiltroMultiploTipoSistemaMigrazByDecCriterioRaggr(
		aDecCriterioRaggr());
	assertTrue(true);
    }

    @Test
    void deleteDecCriterioRaggrByDecCriterioRaggrNotInNmSistemaMigraz_queryIsOk() {
	helper.deleteDecCriterioRaggrByDecCriterioRaggrNotInNmSistemaMigraz(aDecCriterioRaggr(),
		aListOfString(2));
	assertTrue(true);
    }

    @Test
    void deleteDecCriterioFiltroMultiploByDecCriterioNotInTipoDocs_queryIsOk() {
	helper.deleteDecCriterioFiltroMultiploByDecCriterioNotInTipoDocs(aDecCriterioRaggr(),
		aListOfLong(2));
	assertTrue(true);
    }

    @Test
    void deleteDecCriterioFiltroMultiploTipoUniDocByDecCriterio_queryIsOk() {
	helper.deleteDecCriterioFiltroMultiploTipoUniDocByDecCriterio(aDecCriterioRaggr());
	assertTrue(true);
    }

    @Test
    void deleteDecCriterioFiltroMultiploTipoDocByDecCriterio_queryIsOk() {
	helper.deleteDecCriterioFiltroMultiploTipoDocByDecCriterio(aDecCriterioRaggr());
	assertTrue(true);
    }

    @Test
    void deleteDecCriterioFiltroMultiploByDecCriterioNotInTipoUnitaDoc_queryIsOk() {
	helper.deleteDecCriterioFiltroMultiploByDecCriterioNotInTipoUnitaDoc(aDecCriterioRaggr(),
		aListOfLong(2));
	assertTrue(true);
    }
}
