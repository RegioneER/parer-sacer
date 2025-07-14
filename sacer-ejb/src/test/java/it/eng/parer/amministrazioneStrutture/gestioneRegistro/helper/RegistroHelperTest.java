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

package it.eng.parer.amministrazioneStrutture.gestioneRegistro.helper;

import static it.eng.ArquillianUtils.aBigDecimal;
import static it.eng.ArquillianUtils.aListOfBigDecimal;
import static it.eng.ArquillianUtils.aListOfLong;
import static it.eng.ArquillianUtils.aLong;
import static it.eng.ArquillianUtils.aString;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.parer.entity.DecRegistroUnitaDoc_;
import it.eng.parer.web.helper.HelperTest;
import it.eng.parer.ws.dto.CSVersatore;

@ArquillianTest
public class RegistroHelperTest {

    @EJB
    private RegistroHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
	return HelperTest.createEnterpriseArchive(RegistroHelperTest.class.getSimpleName(),
		HelperTest.createSacerLogJar(), HelperTest.createPaginatorJavaArchive(),
		HelperTest.createSacerJavaArchive(Arrays.asList(""), RegistroHelperTest.class,
			RegistroHelper.class, CSVersatore.class));
    }

    @Test
    public void checkRegistroUnitaDoc_queryIsOk() {
	String nmCampo = DecRegistroUnitaDoc_.NM_TIPO_SERIE_DA_CREARE;
	String valoreCampo = aString();
	BigDecimal idStrut = aBigDecimal();
	BigDecimal idRegistroUnitaDoc = aBigDecimal();
	helper.checkRegistroUnitaDocByCampoStringa(nmCampo, valoreCampo, idStrut,
		idRegistroUnitaDoc);
	assertTrue(true);
    }

    @Test
    public void getDecRegistroUnitaDocByName_queryIsOk() {
	String cdRegistroUnitaDoc = aString();
	BigDecimal idStrut = aBigDecimal();
	helper.getDecRegistroUnitaDocByName(cdRegistroUnitaDoc, idStrut);
	assertTrue(true);
    }

    @Test
    public void getDecRegistroUnitaDocByName_whitNullObject() {
	String cdRegistroUnitaDoc = aString();
	BigDecimal idStrut = null;
	helper.getDecRegistroUnitaDocByName(cdRegistroUnitaDoc, idStrut);
	assertTrue(true);
    }

    @Test
    public void getDecAARegistroUnitaDocList_queryIsOk() {
	BigDecimal idRegistro = aBigDecimal();
	helper.getDecAARegistroUnitaDocList(idRegistro);
	assertTrue(true);
    }

    @Test
    public void getDecParteNumeroRegistroList_queryIsOk() {
	Long idAaRegistroUnitaDoc = aLong();
	helper.getDecParteNumeroRegistroList(idAaRegistroUnitaDoc);
	assertTrue(true);
    }

    @Test
    public void getRegistriUnitaDocAbilitati_queryIsOk() {
	long idUtente = aLong();
	BigDecimal idStruttura = aBigDecimal();
	helper.getRegistriUnitaDocAbilitati(idUtente, idStruttura);
	assertTrue(true);
    }

    @Test
    public void getRegistriUnitaDocAbilitatiDaStrutturaList_queryIsOk() {
	long idUtente = aLong();
	List<BigDecimal> idStrutturaList = aListOfBigDecimal(2);
	helper.getRegistriUnitaDocAbilitatiDaStrutturaList(idUtente, idStrutturaList);
	assertTrue(true);
    }

    @Test
    public void retrieveDecRegistroUnitaDocsFromTipoSerie_queryIsOk() {
	BigDecimal idStrut = aBigDecimal();
	BigDecimal idTipoSerie = aBigDecimal();
	helper.retrieveDecRegistroUnitaDocsFromTipoSerie(idStrut, idTipoSerie);
	assertTrue(true);
    }

    @Test
    public void retrieveDecRegistroUnitaDocList_queryIsOk() {
	long idStrut = aLong();
	boolean filterValid = false;
	helper.retrieveDecRegistroUnitaDocList(idStrut, filterValid);
	assertTrue(true);
    }

    @Test
    public void countDecRegistroUnitaDoc_queryIsOk() {
	BigDecimal idModelloTipoSerie = aBigDecimal();
	helper.countDecRegistroUnitaDoc(idModelloTipoSerie);
	assertTrue(true);
    }

    @Test
    public void getDecVChkFmtNumeroForRegistro_queryIsOk() {
	long idRegistroUnitaDoc = aLong();
	helper.getDecVChkFmtNumeroForRegistro(idRegistroUnitaDoc);
	assertTrue(true);
    }

    @Test
    public void getMaxAnniConserv_queryIsOk() {
	BigDecimal idTipoSerie = aBigDecimal();
	helper.getMaxAnniConserv(idTipoSerie);
	assertTrue(true);
    }

    @Test
    public void getDecAARegistroUnitaDoc_queryIsOk() {
	BigDecimal idAaRegistroUnitaDoc = aBigDecimal();
	helper.getDecAARegistroUnitaDoc(idAaRegistroUnitaDoc);
	assertTrue(true);
    }

    @Test
    public void getMonAaUdRegistroNumber_queryIsOk() {
	BigDecimal dataInizio = aBigDecimal();
	BigDecimal dataFine = aBigDecimal();
	BigDecimal idRegistro = aBigDecimal();
	helper.getMonAaUdRegistroNumber(dataInizio, dataFine, idRegistro);
	assertTrue(true);
    }

    @Test
    public void checkRangeDecAaRegistroUnitaDoc_queryIsOk() {
	BigDecimal idAaRegistroUnitaDoc = aBigDecimal();
	BigDecimal idRegistroUnitaDoc = aBigDecimal();
	BigDecimal aaMinRegistroUnitaDoc = aBigDecimal();
	BigDecimal aaMaxRegistroUnitaDoc = aBigDecimal();
	helper.checkRangeDecAaRegistroUnitaDoc(idAaRegistroUnitaDoc, idRegistroUnitaDoc,
		aaMinRegistroUnitaDoc, aaMaxRegistroUnitaDoc);
	assertTrue(true);
    }

    @Test
    public void getDecErrAaRegistroUnitaDocList_queryIsOk() {
	BigDecimal idAaRegistroUnitaDoc = aBigDecimal();
	helper.getDecErrAaRegistroUnitaDocList(idAaRegistroUnitaDoc);
	assertTrue(true);
    }

    @Test
    public void existsRegistroUnitaDoc_queryIsOk() {
	BigDecimal idStrut = aBigDecimal();
	String cdRegistroUnitaDoc = aString();
	helper.existsRegistroUnitaDoc(idStrut, cdRegistroUnitaDoc);
	assertTrue(true);
    }

    @Test
    public void checkUnitaDocInDecAaRegUnitaDoc_queryIsOk() {
	BigDecimal idAaRegistroUnitaDoc = aBigDecimal();
	BigDecimal annoDa = aBigDecimal();
	BigDecimal annoA = aBigDecimal();
	Long idRegistroUnitaDoc = aLong();
	String cdRegistroKeyUnitaDoc = aString();
	List<Long> subStruts = aListOfLong(2);
	helper.checkUnitaDocInDecAaRegUnitaDoc(idAaRegistroUnitaDoc, annoDa, annoA,
		idRegistroUnitaDoc, cdRegistroKeyUnitaDoc, subStruts);
	assertTrue(true);
    }

    @Test
    public void countPeriodiValiditaConControlloConsec_queryIsOk() {
	BigDecimal idRegistroUnitaDoc = aBigDecimal();
	helper.countPeriodiValiditaConControlloConsec(idRegistroUnitaDoc);
	assertTrue(true);
    }

    @Test
    public void getDecVLisTiUniDocAmsByStrutByRegistriList_queryIsOk() {
	List<BigDecimal> idRegistri = aListOfBigDecimal(2);
	helper.getDecVLisTiUniDocAmsByStrutByRegistriList(idRegistri);
	assertTrue(true);
    }

    @Test
    public void getDecTipoUnitaDocAmmessoByRegistro_queryIsOk() {
	Long idRegistroUnitaDoc = aLong();
	helper.getDecTipoUnitaDocAmmessoByRegistro(idRegistroUnitaDoc);
	assertTrue(true);
    }

    @Test
    public void existRegistriNonFiscaliAssociati_queryIsOk() {
	long idTipoUnitaDoc = aLong();
	long idRegistroUnitaDocExcluded = aLong();
	helper.existRegistriNonFiscaliAssociati(idTipoUnitaDoc, idRegistroUnitaDocExcluded);
	assertTrue(true);
    }

    @Test
    public void getVersatoreRegistroUd_queryIsOk() {
	BigDecimal idRegistroUnitaDoc = BigDecimal.valueOf(4);
	helper.getVersatoreRegistroUd(idRegistroUnitaDoc);
	assertTrue(true);
    }

    @Test
    public void existsCdRegistroNormaliz_queryIsOk() {
	String cdRegistroNormaliz = aString();
	BigDecimal idStrut = aBigDecimal();
	BigDecimal idRegistroUnitaDocExcluded = aBigDecimal();
	helper.existsCdRegistroNormaliz(cdRegistroNormaliz, idStrut, idRegistroUnitaDocExcluded);
	assertTrue(true);
    }

    @Test
    public void getDecTipoStrutUdRegByName_queryIsOk() {
	BigDecimal idStrutCorrente = aBigDecimal();
	String nmTipoUnitaDoc = aString();
	String nmTipoStrutUnitaDoc = aString();
	String cdRegistroUnitaDoc = aString();
	helper.getDecTipoStrutUdRegByName(idStrutCorrente, nmTipoUnitaDoc, nmTipoStrutUnitaDoc,
		cdRegistroUnitaDoc);
	assertTrue(true);
    }
}
