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

package it.eng.parer.web.helper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.Assert;

import it.eng.parer.entity.AplParamApplic;
import it.eng.parer.helper.GenericHelper;

@ArquillianTest
@ContextConfiguration(classes = {
        AmministrazioneHelper.class, AmministrazioneHelperUnitTest.class, GenericHelper.class })
public class AmministrazioneHelperUnitTest {
    @Autowired
    private AmministrazioneHelper amministrazioneHelper;

    @Test
    public void itWorks() {
        assertNotNull(amministrazioneHelper);
    }

    @Test
    public void seNonCiSonoParametriRitornaListaVuota() {
        Mockito.when(query.getResultList()).thenReturn(new ArrayList());
        final List<AplParamApplic> aplParamApplicList = amministrazioneHelper
                .getAplParamApplicList("", "", "", "", "", "", "", "", "");
        assertEquals(0, aplParamApplicList.size());
    }

    @BeforeAll
    public void mockEntityManager() {
        EntityManager entityManager = Mockito.mock(EntityManager.class);
        Mockito.when(entityManager.createQuery(anyString())).thenReturn(query);
        amministrazioneHelper.setEntityManager(entityManager);
    }

    // Questa query mock viene ritornata dall'entity manager, anch'esso mock. Prima di ogni test si
    // può
    // impostare il risultato mock da ritornare come result list
    private Query query = Mockito.mock(Query.class);
}
