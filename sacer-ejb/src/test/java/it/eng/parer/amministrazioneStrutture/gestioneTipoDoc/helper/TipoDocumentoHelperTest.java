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

package it.eng.parer.amministrazioneStrutture.gestioneTipoDoc.helper;

import it.eng.parer.amministrazioneStrutture.gestioneTipoStrutturaDoc.helper.TipoStrutturaDocHelperTest;
import it.eng.parer.web.helper.HelperTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;

import static org.junit.Assert.assertTrue;

import org.junit.runner.RunWith;
import static it.eng.ArquillianUtils.*;
import it.eng.ArquillianUtils;
import org.jboss.arquillian.junit.Arquillian;
import javax.ejb.EJB;

@RunWith(Arquillian.class)
public class TipoDocumentoHelperTest {
    @EJB
    private TipoDocumentoHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(TipoDocumentoHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), TipoDocumentoHelperTest.class,
                        TipoDocumentoHelper.class));
    }

    @Test
    public void getDecTipoDocByName_String_BigDecimal_queryIsOk() {
        String nmTipoDoc = aString();
        BigDecimal idStrut = aBigDecimal();
        helper.getDecTipoDocByName(nmTipoDoc, idStrut);
        assertTrue(true);
    }

    @Test
    public void getDecTipoDocByName_String_long_queryIsOk() {
        String nmTipoDoc = aString();
        long idStrut = aLong();
        helper.getDecTipoDocByName(nmTipoDoc, idStrut);
        assertTrue(true);
    }

    @Test
    public void getTipiDocAbilitati_queryIsOk() {
        long idUtente = aLong();
        BigDecimal idStruttura = aBigDecimal();
        helper.getTipiDocAbilitati(idUtente, idStruttura);
        assertTrue(true);
    }

    @Test
    public void getTipiDocPrincipaliAbilitati_queryIsOk() {
        long idUtente = aLong();
        BigDecimal idStruttura = aBigDecimal();
        helper.getTipiDocPrincipaliAbilitati(idUtente, idStruttura);
        assertTrue(true);
    }

    @Test
    public void getTipiDocAbilitatiDaStrutturaList_queryIsOk() {
        long idUtente = aLong();
        List<BigDecimal> idStrutturaList = aListOfBigDecimal(2);
        boolean docPrincipale = false;
        helper.getTipiDocAbilitatiDaStrutturaList(idUtente, idStrutturaList, docPrincipale);
        assertTrue(true);
    }

    @Test
    public void countDecTipoDocPrincipalePerTipoUnitaDoc_queryIsOk() {
        long idTipoUnitaDoc = aLong();
        long idTipoDoc = aLong();
        helper.countDecTipoDocPrincipalePerTipoUnitaDoc(idTipoUnitaDoc, idTipoDoc);
        assertTrue(true);
    }

    @Test
    public void retrieveDecTipoDocList_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        boolean flPrinc = false;
        Date dtSoppressione = todayTs();
        boolean filterValid = false;
        helper.retrieveDecTipoDocList(idStrut, flPrinc, dtSoppressione, filterValid);
        assertTrue(true);
    }

    @Test
    public void getDecTipoStrutDocAmmessoListByIdTipoDoc_queryIsOk() {
        Long idTipoDoc = aLong();
        helper.getDecTipoStrutDocAmmessoListByIdTipoDoc(idTipoDoc);
        assertTrue(true);
    }

    @Test
    public void getDecTipoStrutDocAmmessoListByIdTipoStrutDoc_queryIsOk() {
        Long idTipoStrutDoc = aLong();
        helper.getDecTipoStrutDocAmmessoListByIdTipoStrutDoc(idTipoStrutDoc);
        assertTrue(true);
    }

    @Test
    public void getDecTipoStrutDocListByIdStrut_queryIsOk() {
        Long idStrut = aLong();
        Date data = todayTs();
        helper.getDecTipoStrutDocListByIdStrut(idStrut, data);
        assertTrue(true);
    }

    @Test
    public void getDecTipoStrutDocAmmessoByName_queryIsOk() {
        BigDecimal idStrutCorrente = aBigDecimal();
        String nmTipoStrutDoc = aString();
        String nmTipoDoc = aString();
        helper.getDecTipoStrutDocAmmessoByName(idStrutCorrente, nmTipoStrutDoc, nmTipoDoc);
        assertTrue(true);
    }

    @Test
    public void getDecTipoStrutDocAmmessoByParentId_queryIsOk() {
        Long idTipoDoc = aLong();
        Long idTipoStrutDoc = aLong();
        helper.getDecTipoStrutDocAmmessoByParentId(idTipoDoc, idTipoStrutDoc);
        assertTrue(true);
    }

    @Test
    public void getDecTipoStrutUdXsdByName_queryIsOk() {
        BigDecimal idStrutCorrente = aBigDecimal();
        String nmTipoUnitaDoc = aString();
        String nmTipoStrutUnitaDoc = aString();
        helper.getDecTipoStrutUdXsdByName(idStrutCorrente, nmTipoUnitaDoc, nmTipoStrutUnitaDoc);
        assertTrue(true);
    }
}
