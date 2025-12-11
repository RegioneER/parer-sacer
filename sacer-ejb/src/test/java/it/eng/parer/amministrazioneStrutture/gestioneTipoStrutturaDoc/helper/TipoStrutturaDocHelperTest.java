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

package it.eng.parer.amministrazioneStrutture.gestioneTipoStrutturaDoc.helper;

import static it.eng.ArquillianUtils.aBigDecimal;
import static it.eng.ArquillianUtils.aLong;
import static it.eng.ArquillianUtils.aString;
import static it.eng.ArquillianUtils.todayTs;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.parer.web.helper.HelperTest;

@ArquillianTest
public class TipoStrutturaDocHelperTest {
    @EJB
    private TipoStrutturaDocHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(TipoStrutturaDocHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""),
                        TipoStrutturaDocHelperTest.class, TipoStrutturaDocHelper.class));
    }

    @Test
    void getDecTipoStrutDocByName_queryIsOk() {
        String nmTipoStrutDoc = aString();
        BigDecimal idStrut = aBigDecimal();
        helper.getDecTipoStrutDocByName(nmTipoStrutDoc, idStrut);
        assertTrue(true);
    }

    @Test
    void getDecTipoStrutDocList_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        boolean filterValid = false;
        helper.getDecTipoStrutDocList(idStrut, filterValid);
        assertTrue(true);
    }

    @Test
    void getDecTipoCompDocByName_String_BigDecimal_queryIsOk() {
        String nmTipoCompDoc = aString();
        BigDecimal idTipoStrutDoc = aBigDecimal();
        helper.getDecTipoCompDocByName(nmTipoCompDoc, idTipoStrutDoc);
        assertTrue(true);
    }

    @Test
    void getDecTipoCompDocByName_3args_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        String nmTipoStrutDoc = aString();
        String nmTipoCompDoc = aString();
        helper.getDecTipoCompDocByName(idStrut, nmTipoStrutDoc, nmTipoCompDoc);
        assertTrue(true);
    }

    @Test
    void getDecTipoCompDocList_BigDecimal_boolean_queryIsOk() {
        BigDecimal idTipoStrutDoc = aBigDecimal();
        boolean filterValid = false;
        helper.getDecTipoCompDocList(idTipoStrutDoc, filterValid);
        assertTrue(true);
    }

    @Test
    void getDecTipoCompDocList_3args_queryIsOk() {
        Long idStrut = aLong();
        Date data = todayTs();
        Long idTipoStrutDoc = aLong();
        helper.getDecTipoCompDocList(idStrut, data, idTipoStrutDoc);
        assertTrue(true);
    }

    @Test
    void getDecTipoCompDocListByStrut_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        helper.getDecTipoCompDocListByStrut(idStrut);
        assertTrue(true);
    }

    @Test
    void getDecTipoStrutUnitaDocList_queryIsOk() {
        BigDecimal idTipoUnitaDoc = aBigDecimal();
        boolean filterValid = false;
        helper.getDecTipoStrutUnitaDocList(idTipoUnitaDoc, filterValid);
        assertTrue(true);
    }

    @Test
    void getDecTipoStrutUnitaDocByName_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        String nmTipoUnitaDoc = aString();
        String nmTipoStrutUnitaDoc = aString();
        helper.getDecTipoStrutUnitaDocByName(idStrut, nmTipoUnitaDoc, nmTipoStrutUnitaDoc);
        assertTrue(true);
    }

    @Test
    void existsRelationsWithStrutDoc_queryIsOk() {
        long idTipoStrutDoc = aLong();
        helper.existsRelationsWithStrutDoc(idTipoStrutDoc);
        assertTrue(true);
    }

    @Test
    void checkManyRelationsAreEmptyForDecTipoCompDoc_queryIsOk() {
        long idTipoCompDoc = aLong();
        helper.checkManyRelationsAreEmptyForDecTipoCompDoc(idTipoCompDoc);
        assertTrue(true);
    }

    @Test
    void retrieveDecTipoRapprAmmessoByIdTipoCompDoc_queryIsOk() {
        Long idTipoCompDoc = aLong();
        helper.retrieveDecTipoRapprAmmessoByIdTipoCompDoc(idTipoCompDoc);
        assertTrue(true);
    }

    @Test
    void retrieveDecTipoRapprAmmessoByIdTipoRapprComp_queryIsOk() {
        Long idTipoRapprComp = aLong();
        helper.retrieveDecTipoRapprAmmessoByIdTipoRapprComp(idTipoRapprComp);
        assertTrue(true);
    }

    @Test
    void retrieveDecTipoRapprAmmesso_queryIsOk() {
        Long idTipoCompDoc = aLong();
        Long idTipoRapprComp = aLong();
        helper.retrieveDecTipoRapprAmmesso(idTipoCompDoc, idTipoRapprComp);
        assertTrue(true);
    }

    @Test
    void getDecTipoRapprAmmessoByParentId_queryIsOk() {
        Long idTipoCompDoc = aLong();
        Long idTipoRapprComp = aLong();
        helper.getDecTipoRapprAmmessoByParentId(idTipoCompDoc, idTipoRapprComp);
        assertTrue(true);
    }

    @Test
    void getDecTipoRapprCompListByIdStrut_queryIsOk() {
        Long idStrut = aLong();
        Date data = todayTs();
        helper.getDecTipoRapprCompListByIdStrut(idStrut, data);
        assertTrue(true);
    }

    @Test
    void getDecFormatoFileAmmesso_queryIsOk() {
        BigDecimal idTipoCompDoc = aBigDecimal();
        BigDecimal idFormatoFileDoc = aBigDecimal();
        helper.getDecFormatoFileAmmesso(idTipoCompDoc, idFormatoFileDoc);
        assertTrue(true);
    }
}
