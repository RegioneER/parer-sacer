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

package it.eng.parer.amministrazioneStrutture.gestioneTipoRappresentazione.helper;

import it.eng.parer.helper.GenericHelperTest;
import it.eng.parer.web.helper.HelperTest;
import it.eng.parer.ws.dto.CSVersatore;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import javax.ejb.EJB;

import static org.junit.Assert.assertTrue;

import org.junit.runner.RunWith;
import static it.eng.ArquillianUtils.*;
import it.eng.ArquillianUtils;
import org.jboss.arquillian.junit.Arquillian;
import javax.ejb.EJB;

@RunWith(Arquillian.class)
public class TipoRappresentazioneHelperTest {
    @EJB
    private TipoRappresentazioneHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(TipoRappresentazioneHelperTest.class.getSimpleName(),
                HelperTest.createSacerLogJar(), HelperTest.createPaginatorJavaArchive(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), TipoRappresentazioneHelperTest.class,
                        TipoRappresentazioneHelper.class, CSVersatore.class));
    }

    @Test
    public void getDecTipoRapprCompByName_queryIsOk() {
        String nmTipoRapprComp = aString();
        BigDecimal idStrut = aBigDecimal();
        helper.getDecTipoRapprCompByName(nmTipoRapprComp, idStrut);
        assertTrue(true);
    }

    @Test
    public void retrieveDecRapprCompList_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        boolean filterValid = false;
        helper.retrieveDecRapprCompList(idStrut, filterValid);
        assertTrue(true);
    }

    @Test
    public void checkRelationsAreEmptyForDecTipoRapprComp_queryIsOk() {
        long idTipoRapprComp = aLong();
        helper.checkRelationsAreEmptyForDecTipoRapprComp(idTipoRapprComp);
        assertTrue(true);
    }

    @Test
    public void retrieveDecTrasformTipoRapprList_queryIsOk() {
        Long idTipoRapprComp = aLong();
        helper.retrieveDecTrasformTipoRapprList(idTipoRapprComp);
        assertTrue(true);
    }

    @Test
    public void retrieveDecImageTrasformList_queryIsOk() {
        BigDecimal idTrasformTipoRappr = aBigDecimal();
        helper.retrieveDecImageTrasformList(idTrasformTipoRappr);
        assertTrue(true);
    }

    @Test
    public void getCSVersatoreForImageTrasform_queryIsOk() {
        BigDecimal idImageTrasform = aBigDecimal();
        helper.getCSVersatoreForImageTrasform(idImageTrasform);
        assertTrue(true);
    }

    @Test
    public void getCSVersatoreForTrasformTipoRappr_queryIsOk() {
        BigDecimal idTrasformTipoRappr = aBigDecimal();
        helper.getCSVersatoreForTrasformTipoRappr(idTrasformTipoRappr);
        assertTrue(true);
    }

    @Test
    public void getDecImageTrasformByName_queryIsOk() {
        BigDecimal idTrasformTipoRappr = aBigDecimal();
        String nmImageTrasform = aString();
        helper.getDecImageTrasformByName(idTrasformTipoRappr, nmImageTrasform);
        assertTrue(true);
    }
}
