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

package it.eng.parer.fascicoli.helper;

import it.eng.parer.crypto.helper.ElenchiIndiciAipFascSignatureHelperTest;
import it.eng.parer.web.helper.HelperTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

import org.junit.runner.RunWith;
import static it.eng.ArquillianUtils.*;
import it.eng.ArquillianUtils;
import org.jboss.arquillian.junit.Arquillian;
import javax.ejb.EJB;

@RunWith(Arquillian.class)
public class ModelliFascicoliHelperTest {
    @EJB
    private ModelliFascicoliHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(ModelliFascicoliHelperTest.class.getSimpleName(),
                HelperTest.createSacerLogJar(), HelperTest.createPaginatorJavaArchive(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), ModelliFascicoliHelperTest.class,
                        ModelliFascicoliHelper.class));
    }

    @Test
    public void retrieveDecModelloXsdTipoFascicolo_queryIsOk() {
        ModelliFascicoliHelper.Filtri filtri = new ModelliFascicoliHelper.Filtri();
        filtri.setCdXsd(aString());
        filtri.setDsXsd(aString());
        filtri.setFlAttivo(aFlag());
        filtri.setFlDefault(aFlag());
        filtri.setTiModelloXsd(aString());
        List<BigDecimal> idAmbienteList = aListOfBigDecimal(2);
        String tiUsoModelloXsd = aString();
        boolean filterValid = false;
        helper.retrieveDecModelloXsdTipoFascicolo(idAmbienteList, tiUsoModelloXsd, filterValid, filtri);
        assertTrue(true);
    }

    @Test
    public void retrieveDecModelloXsdFascicolo_queryIsOk() {
        BigDecimal idAmbiente = aBigDecimal();
        String tiModelloXsd = aString();
        boolean filterValid = false;

        helper.retrieveDecModelloXsdFascicolo(idAmbiente, tiModelloXsd, filterValid);
        assertTrue(true);
    }

    @Test
    public void getDecModelloXsdFascicolo_queryIsOk() {
        BigDecimal idAmbiente = aBigDecimal();
        String tiModelloXsd = aString();
        String tiUsoModelloXsd = aString();
        String cdXsd = aString();

        helper.getDecModelloXsdFascicolo(idAmbiente, tiModelloXsd, tiUsoModelloXsd, cdXsd);
        assertTrue(true);
    }

    @Test
    public void retrieveDecUsoModelloXsdFasc_queryIsOk() {
        BigDecimal idModelloXsdFascicolo = aBigDecimal();

        helper.retrieveDecUsoModelloXsdFasc(idModelloXsdFascicolo);
        assertTrue(true);
    }

    @Test
    public void existDecUsoModelloXsdFasc_queryIsOk() {
        BigDecimal idModelloXsdFascicolo = aBigDecimal();

        helper.existDecUsoModelloXsdFasc(idModelloXsdFascicolo);
        assertTrue(true);
    }
}
