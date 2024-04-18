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

package it.eng.parer.amministrazioneStrutture.gestioneSistemaMigrazione.helper;

import it.eng.parer.job.validazioneFascicoli.helper.ValidazioneFascicoliHelperTest;
import it.eng.parer.web.helper.HelperTest;
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

@RunWith(Arquillian.class)
public class SistemaMigrazioneHelperTest {

    @EJB
    private SistemaMigrazioneHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(SistemaMigrazioneHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), SistemaMigrazioneHelperTest.class,
                        SistemaMigrazioneHelper.class));
    }

    @Test
    public void retrieveAplSistemaMigraz_BigDecimal_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        helper.retrieveAplSistemaMigraz(idStrut);
        assertTrue(true);
    }

    @Test
    public void retrieveOrgUsoSistemaMigraz_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        helper.retrieveOrgUsoSistemaMigraz(idStrut);
        assertTrue(true);
    }

    @Test
    public void retrieveNmSistemaMigraz_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        helper.retrieveNmSistemaMigraz(idStrut);
        assertTrue(true);
    }

    @Test
    public void retrieveAplSistemaMigraz_String_String_queryIsOk() {
        String nmSistemaMigraz = aString();
        String dsSistemaMigraz = aString();
        helper.retrieveAplSistemaMigraz(nmSistemaMigraz, dsSistemaMigraz);
        assertTrue(true);
    }

    @Test
    public void getAplSistemaMigraz_queryIsOk() {
        String nmSistemaMigraz = aString();
        helper.getAplSistemaMigraz(nmSistemaMigraz);
        assertTrue(true);
    }

    @Test
    public void existsOrgUsoSistemaMigraz_queryIsOk() {
        BigDecimal idSistemaMigraz = aBigDecimal();
        helper.existsOrgUsoSistemaMigraz(idSistemaMigraz);
        assertTrue(true);
    }
}
