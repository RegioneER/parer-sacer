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

package it.eng.parer.web.helper;

import it.eng.parer.entity.AplParamApplic;
import it.eng.parer.helper.GenericHelper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { AmministrazioneHelper.class, AmministrazioneHelperUnitTest.class,
        GenericHelper.class })
public class AmministrazioneHelperUnitTest {
    @Autowired
    private AmministrazioneHelper amministrazioneHelper;

    @Test
    public void itWorks() {
        Assert.assertNotNull(amministrazioneHelper);
    }

    @Test
    public void seNonCiSonoParametriRitornaListaVuota() {
        Mockito.when(query.getResultList()).thenReturn(new ArrayList());
        final List<AplParamApplic> aplParamApplicList = amministrazioneHelper.getAplParamApplicList("", "", "", "", "",
                "", "");
        Assert.assertEquals(0, aplParamApplicList.size());
    }

    @Before
    public void mockEntityManager() {
        EntityManager entityManager = Mockito.mock(EntityManager.class);
        Mockito.when(entityManager.createQuery(anyString())).thenReturn(query);
        amministrazioneHelper.setEntityManager(entityManager);
    }

    // Questa query mock viene ritornata dall'entity manager, anch'esso mock. Prima di ogni test si può
    // impostare il risultato mock da ritornare come result list
    private Query query = Mockito.mock(Query.class);
}
