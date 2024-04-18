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

package it.eng.parer.amministrazioneStrutture.gestioneFormatiFileDoc.helper;

import it.eng.parer.amministrazioneStrutture.gestioneFormatiFileStandard.helper.FormatoFileStandardHelperTest;
import it.eng.parer.web.helper.HelperTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;

import static org.junit.Assert.assertTrue;
import static it.eng.ArquillianUtils.*;
import it.eng.ArquillianUtils;

public class FormatoFileDocHelperTest {

    @EJB
    private FormatoFileDocHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(FormatoFileDocHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), FormatoFileDocHelperTest.class,
                        FormatoFileDocHelper.class));
    }

    @Test
    public void retrieveDecFormatoFileDocList_BigDecimal_boolean_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        boolean filterValid = false;
        helper.retrieveDecFormatoFileDocList(idStrut, filterValid);
        assertTrue(true);
    }

    @Test
    public void retrieveDecFormatoFileDocList_BigDecimal_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        helper.retrieveDecFormatoFileDocList(idStrut);
        assertTrue(true);
    }

    @Test
    public void getDecFormatoFileDocByName_queryIsOk() {
        String nmFormatoFileDoc = aString();
        BigDecimal idStrut = aBigDecimal();
        helper.getDecFormatoFileDocByName(nmFormatoFileDoc, idStrut);
        assertTrue(true);
    }

    @Test
    public void removeUsoFormatoFileStandardByFormatoFDoc_queryIsOk() {
        BigDecimal idFormatoFileDoc = aBigDecimal();
        helper.removeUsoFormatoFileStandardByFormatoFDoc(idFormatoFileDoc);
        assertTrue(true);
    }

    @Test
    public void getUsoFormatoFileStandardMaxNrOrder_queryIsOk() {
        BigDecimal idFormatoFileDoc = aBigDecimal();
        helper.getUsoFormatoFileStandardMaxNrOrder(idFormatoFileDoc);
        assertTrue(true);
    }

    @Test
    public void getDecFormatoFileAmmessoList_queryIsOk() {
        BigDecimal idTipoCompDoc = aBigDecimal();
        helper.getDecFormatoFileAmmessoList(idTipoCompDoc);
        assertTrue(true);
    }

    @Test
    public void getDecFormatoFileAmmessoNotInList_queryIsOk() {
        Set<String> formati = aSetOfString(2);
        BigDecimal idStrut = aBigDecimal();
        helper.getDecFormatoFileAmmessoNotInList(formati, idStrut);
        assertTrue(true);
    }

    @Test
    public void retrieveDecFormatoFileDocList_BigDecimal_List_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        List<String> formatoFileDocList = aListOfString(2);
        helper.retrieveDecFormatoFileDocList(idStrut, formatoFileDocList);
        assertTrue(true);
    }

    @Test
    public void checkRelationsAreEmptyForDecFormatoFileDoc_queryIsOk() {
        long idFormatoFileDoc = aLong();
        helper.checkRelationsAreEmptyForDecFormatoFileDoc(idFormatoFileDoc);
        assertTrue(true);
    }

    @Test
    public void checkRelationsAreEmptyForDecFormatoFileDocCont_queryIsOk() {
        long idFormatoFileDoc = aLong();
        helper.checkRelationsAreEmptyForDecFormatoFileDocCont(idFormatoFileDoc);
        assertTrue(true);
    }

    @Test
    public void checkRelationsAreEmptyForDecFormatoFileDocConv_queryIsOk() {
        long idFormatoFileDoc = aLong();
        helper.checkRelationsAreEmptyForDecFormatoFileDocConv(idFormatoFileDoc);
        assertTrue(true);
    }

    @Test
    public void retrieveFormatoFileDocNiOrdUsoTB_queryIsOk() {
        BigDecimal idStruttura = aBigDecimal();
        helper.retrieveFormatoFileDocNiOrdUsoTB(idStruttura);
        assertTrue(true);
    }
}
