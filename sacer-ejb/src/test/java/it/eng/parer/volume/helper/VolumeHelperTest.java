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

package it.eng.parer.volume.helper;

import it.eng.parer.serie.helper.TipoSerieHelperTest;
import it.eng.parer.volume.utils.ReturnParams;
import it.eng.parer.web.dto.DefinitoDaBean;

import it.eng.parer.web.helper.HelperTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertTrue;

import org.junit.runner.RunWith;
import static it.eng.ArquillianUtils.*;
import it.eng.ArquillianUtils;
import org.jboss.arquillian.junit.Arquillian;
import javax.ejb.EJB;

@RunWith(Arquillian.class)
public class VolumeHelperTest {
    @EJB
    private VolumeHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(VolumeHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), VolumeHelperTest.class, VolumeHelper.class,
                        ReturnParams.class, DefinitoDaBean.class));
    }

    @Test
    public void retrieveVolumeById_queryIsOk() {
        Long idVolume = aLong();
        helper.retrieveVolumeById(idVolume);
        assertTrue(true);
    }

    @Test
    public void getVolInfo_queryIsOk() {
        Long idUnitaDoc = aLong();
        try {
            helper.getVolInfo(idUnitaDoc);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }
}
