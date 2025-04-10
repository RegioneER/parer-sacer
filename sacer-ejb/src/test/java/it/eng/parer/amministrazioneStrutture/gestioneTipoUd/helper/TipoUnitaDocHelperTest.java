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

package it.eng.parer.amministrazioneStrutture.gestioneTipoUd.helper;

import it.eng.ArquillianUtils;
import static it.eng.ArquillianUtils.*;
import it.eng.parer.amministrazioneStrutture.gestioneSottoStrutture.helper.SottoStruttureHelperTest;
import it.eng.parer.slite.gen.viewbean.DecVLisTiUniDocAmsRowBean;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import javax.ejb.EJB;

import it.eng.parer.web.helper.HelperTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class TipoUnitaDocHelperTest {
    @EJB
    private TipoUnitaDocHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(TipoUnitaDocHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), TipoUnitaDocHelperTest.class,
                        TipoUnitaDocHelper.class));
    }

    @Test
    public void checkTipoUnitaDoc_queryIsOk() {
        String valoreCampo = aString();
        BigDecimal idStrut = aBigDecimal();
        BigDecimal idTipoUnitaDoc = aBigDecimal();
        String nmCampo = "nmTipoSerieDaCreare";
        helper.checkTipoUnitaDocByCampoStringa(nmCampo, valoreCampo, idStrut, idTipoUnitaDoc);
        assertTrue(true);
        nmCampo = "cdSerieDaCreare";
        helper.checkTipoUnitaDocByCampoStringa(nmCampo, valoreCampo, idStrut, idTipoUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void getDecTipoUnitaDocByName_queryIsOk() {
        String nmTipoUnitaDoc = aString();
        BigDecimal idStrut = aBigDecimal();
        helper.getDecTipoUnitaDocByName(nmTipoUnitaDoc, idStrut);
        assertTrue(true);
    }

    @Test
    public void getDecTipoUnitaDocAmmessoByName_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        String nmTipoUnitaDoc = aString();
        String cdRegistroUnitaDoc = aString();
        helper.getDecTipoUnitaDocAmmessoByName(idStrut, nmTipoUnitaDoc, cdRegistroUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void getDecTipoUnitaDocAmmessoByParentId_queryIsOk() {
        BigDecimal idRegistroUnitaDoc = aBigDecimal();
        BigDecimal idTipoUnitaDoc = aBigDecimal();
        helper.getDecTipoUnitaDocAmmessoByParentId(idRegistroUnitaDoc, idTipoUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void getDecTipoUnitaDocAmmessoByTipoUnitaDoc_queryIsOk() {
        BigDecimal idTipoUnitaDoc = aBigDecimal();
        helper.getDecTipoUnitaDocAmmessoByTipoUnitaDoc(idTipoUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void getDecXsdDatiSpecByTipoUnitaDoc_queryIsOk() {
        BigDecimal idTipoUnitaDoc = aBigDecimal();
        helper.getDecXsdDatiSpecByTipoUnitaDoc(idTipoUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void getTipiUnitaDocAbilitati_queryIsOk() {
        long idUtente = aLong();
        BigDecimal idStruttura = aBigDecimal();
        helper.getTipiUnitaDocAbilitati(idUtente, idStruttura);
        assertTrue(true);
    }

    @Test
    public void retrieveTipiUnitaDocAbilitatiDaStrutturaList_queryIsOk() {
        long idUtente = aLong();
        List<BigDecimal> idStrutturaList = aListOfBigDecimal(2);
        helper.retrieveTipiUnitaDocAbilitatiDaStrutturaList(idUtente, idStrutturaList);
        assertTrue(true);
    }

    @Test
    public void retrieveDecTipoUnitaDocsFromTipoSerie_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        BigDecimal idTipoSerie = aBigDecimal();
        helper.retrieveDecTipoUnitaDocsFromTipoSerie(idStrut, idTipoSerie);
        assertTrue(true);
    }

    @Test
    public void countDecTipoUnitaDoc_queryIsOk() {
        BigDecimal idModelloTipoSerie = aBigDecimal();
        helper.countDecTipoUnitaDoc(idModelloTipoSerie);
        assertTrue(true);
    }

    @Test
    public void existsServiziErogatiByStrutAndTipoServizio_queryIsOk() {
        long idTipoServizio = aLong();
        BigDecimal idStrut = aBigDecimal();
        helper.existsServiziErogatiByStrutAndTipoServizio(idTipoServizio, idStrut);
        assertTrue(true);
    }

    /*
     * @Test public void existsServiziErogatiByStrutAndSistVers_queryIsOk() { long idSistemaVersante = aLong();
     * BigDecimal idStrut = aBigDecimal(); helper.existsServiziErogatiByStrutAndSistVers(idSistemaVersante, idStrut);
     * assertTrue(true); }
     */

    @Test
    public void getDecTipoStrutUnitaDocByName_queryIsOk() {
        String nmTipoStrutUnitaDoc = aString();
        BigDecimal idTipoUnitaDoc = aBigDecimal();
        helper.getDecTipoStrutUnitaDocByName(nmTipoStrutUnitaDoc, idTipoUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void getDecVLisTiUniDocAmList_queryIsOk() {
        DecVLisTiUniDocAmsRowBean tipoUnitaDocAmmesso = new DecVLisTiUniDocAmsRowBean();
        tipoUnitaDocAmmesso.setCdRegistroUnitaDoc(aString());
        tipoUnitaDocAmmesso.setIdRegistroUnitaDoc(aBigDecimal());
        tipoUnitaDocAmmesso.setIdTipoUnitaDoc(aBigDecimal());
        tipoUnitaDocAmmesso.setIdTipoUnitaDocAmmesso(aBigDecimal());
        tipoUnitaDocAmmesso.setNmTipoUnitaDoc(aString());
        tipoUnitaDocAmmesso.setNumrecords(aInt());

        helper.getDecVLisTiUniDocAmList(tipoUnitaDocAmmesso);
        assertTrue(true);
    }

    @Test
    public void getDecTipoDocAmmessoList_BigDecimal_queryIsOk() {
        BigDecimal idTipoStrutUnitaDoc = aBigDecimal();
        helper.getDecTipoDocAmmessoList(idTipoStrutUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void getDecTipoDocAmmesso_queryIsOk() {
        BigDecimal idTipoDoc = aBigDecimal();
        BigDecimal idTipoStrutUnitaDoc = aBigDecimal();
        helper.getDecTipoDocAmmesso(idTipoDoc, idTipoStrutUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void getDecTipoDocAmmessoByName_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        String nmTipoDoc = aString();
        String nmTipoStrutUnitaDoc = aString();
        helper.getDecTipoDocAmmessoByName(idStrut, nmTipoDoc, nmTipoStrutUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void existsDecTipoDocAmmesso_queryIsOk() {
        BigDecimal idTipoDocAmmesso = aBigDecimal();
        BigDecimal idTipoDoc = aBigDecimal();
        BigDecimal idTipoStrutUnitaDoc = aBigDecimal();
        String tipoElemento = aString();
        helper.existsDecTipoDocAmmesso(idTipoDocAmmesso, idTipoDoc, idTipoStrutUnitaDoc, tipoElemento);
        assertTrue(true);
    }

    @Test
    public void getDecTipoDocAmmessoList_4args_queryIsOk() {
        BigDecimal idTipoDocAmmesso = aBigDecimal();
        BigDecimal idTipoDoc = aBigDecimal();
        BigDecimal idTipoStrutUnitaDoc = aBigDecimal();
        String tipoElemento = aString();
        helper.getDecTipoDocAmmessoList(idTipoDocAmmesso, idTipoDoc, idTipoStrutUnitaDoc, tipoElemento);
        assertTrue(true);
    }

    @Test
    public void checkRelationsAreEmptyForDecTipoStrutUnitaDoc_queryIsOk() {
        long idTipoStrutUnitaDoc = aLong();
        helper.checkRelationsAreEmptyForDecTipoStrutUnitaDoc(idTipoStrutUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void getDecCategTipoUnitaDocList_queryIsOk() {
        Boolean firstLevel = aBoolean();
        helper.getDecCategTipoUnitaDocList(firstLevel);
        assertTrue(true);
    }

    @Test
    public void getDecCategTipoUnitaDocChildList_queryIsOk() {
        BigDecimal idCategTipoUnitaDoc = aBigDecimal();
        helper.getDecCategTipoUnitaDocChildList(idCategTipoUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void getDecCategTipoUnitaDocByCode_String_queryIsOk() {
        String cdCategTipoUnitaDoc = aString();
        helper.getDecCategTipoUnitaDocByCode(cdCategTipoUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void getDecCategTipoUnitaDocByCodeLike_queryIsOk() {
        String cdCategTipoUnitaDoc = aString();
        helper.getDecCategTipoUnitaDocByCodeLike(cdCategTipoUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void getDecCategTipoUnitaDocByCode_String_boolean_queryIsOk() {
        String cdCategTipoUnitaDoc = aString();
        boolean like = false;
        helper.getDecCategTipoUnitaDocByCode(cdCategTipoUnitaDoc, like);
        assertTrue(true);
    }

    @Test
    public void getListDecCategTipoUnitaDocInUse_queryIsOk() {
        long idCategTipoUnitaDoc = aLong();
        helper.getListDecCategTipoUnitaDocInUse(idCategTipoUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void retrieveDecTipoUnitaDoc_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        helper.retrieveDecTipoUnitaDoc(idStrut);
        assertTrue(true);
    }

    @Test
    public void getDecTipoUnitaDocList_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        boolean filterValid = false;
        helper.getDecTipoUnitaDocList(idStrut, filterValid);
        assertTrue(true);
    }

    @Test
    public void retrieveAplSistemaVersanteListPerTipoUd_queryIsOk() {
        BigDecimal idTipoUnitaDoc = aBigDecimal();
        helper.retrieveAplSistemaVersanteListPerTipoUd(idTipoUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void getAplSistemiVersantiSeparatiPerTipoUd_queryIsOk() {
        BigDecimal idTipoUnitaDoc = aBigDecimal();
        helper.getNmSistemiVersantiRaggruppatiPerTipoUd(idTipoUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void retrieveAplSistemaVersanteList_queryIsOk() {
        helper.retrieveAplSistemaVersanteList();
        assertTrue(true);
    }

    @Test
    public void retrieveOrgTipoServizioList_queryIsOk() {
        String tiClasseTipoServizio = aString();
        helper.retrieveOrgTipoServizioList(tiClasseTipoServizio);
        assertTrue(true);
    }

    @Test
    public void getAplSistemaVersanteByName_queryIsOk() {
        String nmSistemaVersante = aString();
        helper.getAplSistemaVersanteByName(nmSistemaVersante);
        assertTrue(true);
    }

    @Test
    public void getOrgTipoServizioByName_queryIsOk() {
        String cdTipoServizio = aString();
        helper.getOrgTipoServizioByName(cdTipoServizio);
        assertTrue(true);
    }

    @Test
    public void getDecTipoStrutUdSisVersByName_queryIsOk() {
        BigDecimal idStrutCorrente = aBigDecimal();
        String nmTipoUnitaDoc = aString();
        String nmTipoStrutUnitaDoc = aString();
        String nmSistemaVersante = aString();
        helper.getDecTipoStrutUdSisVersByName(idStrutCorrente, nmTipoUnitaDoc, nmTipoStrutUnitaDoc, nmSistemaVersante);
        assertTrue(true);
    }

    @Test
    public void checkTipoUnitaDocByCampoStringa_queryIsOk() {
        final String nmCampo = "cdSerie";
        final String valoreCampo = "prova";
        final BigDecimal idStrut = BigDecimal.ZERO;
        final BigDecimal idTipoUnitaDoc = BigDecimal.ZERO;
        helper.checkTipoUnitaDocByCampoStringa(nmCampo, valoreCampo, idStrut, idTipoUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void getDecVCalcTiServOnTipoUd_queryIsOk() {
        final BigDecimal idStrut = BigDecimal.ZERO;
        final BigDecimal idCategTipoUnitaDoc = BigDecimal.ZERO;
        final String cdAlgoTariffario = "prova";
        helper.getDecVCalcTiServOnTipoUd(idStrut, idCategTipoUnitaDoc, cdAlgoTariffario);
        assertTrue(true);
    }

    @Test
    public void bulkDeleteDecTipoStrutUdSisVers_queryIsOk() {
        helper.bulkDeleteDecTipoStrutUdSisVers(-1L);
        assertTrue(true);
    }

    @Test
    public void deleteDecTipoStrutUdSisVers_queryIsOk() {
        try {
            helper.deleteDecTipoStrutUdSisVers(-1L, BigDecimal.ZERO);
            fail("non deve trovare nulla da cancellare");
        } catch (Exception e) {
            assertExceptionMessage(e, "java.lang.IndexOutOfBoundsException");
        }
    }

    @Test
    public void bulkDeleteDecTipoStrutUdReg_queryIsOk() {
        helper.bulkDeleteDecTipoStrutUdReg(-1L);
        assertTrue(true);
    }

    @Test
    public void deleteDecTipoStrutUdReg_queryIsOk() {
        try {
            helper.deleteDecTipoStrutUdReg(-1l, -1l);
            fail("non deve trovare nulla da cancellare");
        } catch (Exception e) {
            assertExceptionMessage(e, "java.lang.IndexOutOfBoundsException");
        }
    }

    @Test
    public void deleteDecTipoStrutUdXsd_queryIsOk() {
        try {
            helper.deleteDecTipoStrutUdXsd(-1l, -1l);
            fail("non deve trovare nulla da cancellare");
        } catch (Exception e) {
            assertExceptionMessage(e, "java.lang.IndexOutOfBoundsException");
        }
    }

    @Test
    public void bulkDeleteDecTipoStrutUdXsd_queryIsOk() {
        helper.bulkDeleteDecTipoStrutUdXsd(-1L);
        assertTrue(true);
    }

    @Test
    public void exists_checkQuerySintax() {
        helper.existsEstensione("PDF");
        assertTrue(true);
    }

    @Test
    public void isAccordoPerNuovaFatturazione_queryIsOk() {
        helper.isAccordoPerNuovaFatturazione(BigDecimal.ZERO);
        assertTrue(true);
    }
}
