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

package it.eng.parer.amministrazioneStrutture.gestioneTipoStrutturaDoc.helper;

import it.eng.parer.crypto.helper.ElenchiIndiciAipSignatureHelperTest;
import it.eng.parer.web.helper.HelperTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import javax.ejb.EJB;

import static org.junit.Assert.assertTrue;

import org.junit.runner.RunWith;
import static it.eng.ArquillianUtils.*;
import it.eng.ArquillianUtils;
import org.jboss.arquillian.junit.Arquillian;
import javax.ejb.EJB;

@RunWith(Arquillian.class)
public class TipoStrutturaDocHelperTest {
    @EJB
    private TipoStrutturaDocHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(TipoStrutturaDocHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), TipoStrutturaDocHelperTest.class,
                        TipoStrutturaDocHelper.class));
    }

    @Test
    public void getDecTipoStrutDocByName_queryIsOk() {
        String nmTipoStrutDoc = aString();
        BigDecimal idStrut = aBigDecimal();
        helper.getDecTipoStrutDocByName(nmTipoStrutDoc, idStrut);
        assertTrue(true);
    }

    @Test
    public void getDecTipoStrutDocList_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        boolean filterValid = false;
        helper.getDecTipoStrutDocList(idStrut, filterValid);
        assertTrue(true);
    }

    @Test
    public void getDecTipoCompDocByName_String_BigDecimal_queryIsOk() {
        String nmTipoCompDoc = aString();
        BigDecimal idTipoStrutDoc = aBigDecimal();
        helper.getDecTipoCompDocByName(nmTipoCompDoc, idTipoStrutDoc);
        assertTrue(true);
    }

    @Test
    public void getDecTipoCompDocByName_3args_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        String nmTipoStrutDoc = aString();
        String nmTipoCompDoc = aString();
        helper.getDecTipoCompDocByName(idStrut, nmTipoStrutDoc, nmTipoCompDoc);
        assertTrue(true);
    }

    @Test
    public void getDecTipoCompDocList_BigDecimal_boolean_queryIsOk() {
        BigDecimal idTipoStrutDoc = aBigDecimal();
        boolean filterValid = false;
        helper.getDecTipoCompDocList(idTipoStrutDoc, filterValid);
        assertTrue(true);
    }

    @Test
    public void getDecTipoCompDocList_3args_queryIsOk() {
        Long idStrut = aLong();
        Date data = todayTs();
        Long idTipoStrutDoc = aLong();
        helper.getDecTipoCompDocList(idStrut, data, idTipoStrutDoc);
        assertTrue(true);
    }

    @Test
    public void getDecTipoCompDocListByStrut_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        helper.getDecTipoCompDocListByStrut(idStrut);
        assertTrue(true);
    }

    @Test
    public void getDecTipoStrutUnitaDocList_queryIsOk() {
        BigDecimal idTipoUnitaDoc = aBigDecimal();
        boolean filterValid = false;
        helper.getDecTipoStrutUnitaDocList(idTipoUnitaDoc, filterValid);
        assertTrue(true);
    }

    @Test
    public void getDecTipoStrutUnitaDocByName_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        String nmTipoUnitaDoc = aString();
        String nmTipoStrutUnitaDoc = aString();
        helper.getDecTipoStrutUnitaDocByName(idStrut, nmTipoUnitaDoc, nmTipoStrutUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void existsRelationsWithStrutDoc_queryIsOk() {
        long idTipoStrutDoc = aLong();
        helper.existsRelationsWithStrutDoc(idTipoStrutDoc);
        assertTrue(true);
    }

    @Test
    public void checkManyRelationsAreEmptyForDecTipoCompDoc_queryIsOk() {
        long idTipoCompDoc = aLong();
        helper.checkManyRelationsAreEmptyForDecTipoCompDoc(idTipoCompDoc);
        assertTrue(true);
    }

    @Test
    public void retrieveDecTipoRapprAmmessoByIdTipoCompDoc_queryIsOk() {
        Long idTipoCompDoc = aLong();
        helper.retrieveDecTipoRapprAmmessoByIdTipoCompDoc(idTipoCompDoc);
        assertTrue(true);
    }

    @Test
    public void retrieveDecTipoRapprAmmessoByIdTipoRapprComp_queryIsOk() {
        Long idTipoRapprComp = aLong();
        helper.retrieveDecTipoRapprAmmessoByIdTipoRapprComp(idTipoRapprComp);
        assertTrue(true);
    }

    @Test
    public void retrieveDecTipoRapprAmmesso_queryIsOk() {
        Long idTipoCompDoc = aLong();
        Long idTipoRapprComp = aLong();
        helper.retrieveDecTipoRapprAmmesso(idTipoCompDoc, idTipoRapprComp);
        assertTrue(true);
    }

    @Test
    public void getDecTipoRapprAmmessoByParentId_queryIsOk() {
        Long idTipoCompDoc = aLong();
        Long idTipoRapprComp = aLong();
        helper.getDecTipoRapprAmmessoByParentId(idTipoCompDoc, idTipoRapprComp);
        assertTrue(true);
    }

    @Test
    public void getDecTipoRapprCompListByIdStrut_queryIsOk() {
        Long idStrut = aLong();
        Date data = todayTs();
        helper.getDecTipoRapprCompListByIdStrut(idStrut, data);
        assertTrue(true);
    }

    @Test
    public void getDecFormatoFileAmmesso_queryIsOk() {
        BigDecimal idTipoCompDoc = aBigDecimal();
        BigDecimal idFormatoFileDoc = aBigDecimal();
        helper.getDecFormatoFileAmmesso(idTipoCompDoc, idFormatoFileDoc);
        assertTrue(true);
    }
}
