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

import it.eng.parer.amministrazioneStrutture.gestioneRegistro.helper.RegistroHelperTest;
import it.eng.parer.web.helper.HelperTest;
import it.eng.parer.web.util.Transform;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

import org.junit.runner.RunWith;
import static it.eng.ArquillianUtils.*;
import it.eng.ArquillianUtils;
import org.jboss.arquillian.junit.Arquillian;
import javax.ejb.EJB;

@RunWith(Arquillian.class)
public class CriteriRaggrFascicoliHelperTest {
    @EJB
    private CriteriRaggrFascicoliHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(CriteriRaggrFascicoliHelperTest.class.getSimpleName(),
                HelperTest.createSacerLogJar(), HelperTest.createPaginatorJavaArchive(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), CriteriRaggrFascicoliHelperTest.class,
                        CriteriRaggrFascicoliHelper.class, Transform.class));
    }

    @Test
    public void retrieveDecCriterioRaggrFascList_4args_queryIsOk() {
        BigDecimal idAmbiente = aBigDecimal();
        BigDecimal idEnte = aBigDecimal();
        BigDecimal idStrut = aBigDecimal();
        String nmCriterioRaggr = aString();
        helper.retrieveDecCriterioRaggrFascList(idAmbiente, idEnte, idStrut, nmCriterioRaggr);
        assertTrue(true);
    }

    @Test
    public void retrieveDecCriterioRaggrFascList_BigDecimal_queryIsOk() {
        BigDecimal idStruttura = aBigDecimal();
        helper.retrieveDecCriterioRaggrFascList(idStruttura);
        assertTrue(true);
    }

    @Test
    public void getOrgStrutById_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        helper.getOrgStrutById(idStrut);
        assertTrue(true);
    }
}
