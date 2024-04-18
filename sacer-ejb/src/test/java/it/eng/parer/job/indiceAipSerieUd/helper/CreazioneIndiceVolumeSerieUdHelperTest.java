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

package it.eng.parer.job.indiceAipSerieUd.helper;

import it.eng.parer.fascicoli.helper.CriteriRaggrFascicoliHelperTest;
import it.eng.parer.web.helper.HelperTest;
import it.eng.parer.ws.dto.CSChiaveFasc;
import it.eng.parer.ws.dto.CSVersatore;
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
public class CreazioneIndiceVolumeSerieUdHelperTest {
    @EJB
    private CreazioneIndiceVolumeSerieUdHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(CreazioneIndiceVolumeSerieUdHelperTest.class.getSimpleName(),
                HelperTest.createSacerLogJar(), HelperTest.createPaginatorJavaArchive(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), CreazioneIndiceVolumeSerieUdHelperTest.class,
                        CreazioneIndiceVolumeSerieUdHelper.class, CSVersatore.class, CSChiaveFasc.class,
                        it.eng.parer.ws.dto.CSChiave.class));
    }

    @Test
    public void getSerVCreaIxVolSerieUd_queryIsOk() {
        Long idVolVerSerie = aLong();
        try {
            helper.getSerVCreaIxVolSerieUd(idVolVerSerie);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    public void getSerVLisUdAppartVolSerie_queryIsOk() {
        Long idVolVerSerie = aLong();
        helper.getSerVLisUdAppartVolSerie(idVolVerSerie);
        assertTrue(true);
    }

    @Test
    public void registraVolVerSerie_queryIsOk() {
        long idVerSerie = aLong();
        try {
            helper.registraVolVerSerie(idVerSerie);
        } catch (Exception e) {
            assertExceptionMessage(e, "org.hibernate.exception.ConstraintViolationException");
        }
    }

    @Test
    public void registraSerIxVolVerSerie_queryIsOk() {
        long idVolVerSerie = aLong();
        String cdVerXsdIxVol = aString();
        String xml = aString();
        String hash = aString();
        BigDecimal idStrut = aBigDecimal();
        try {
            helper.registraSerIxVolVerSerie(idVolVerSerie, cdVerXsdIxVol, xml, hash, idStrut);
        } catch (Exception e) {
            assertExceptionMessage(e, "org.hibernate.exception.GenericJDBCException");
        }
    }

    @Test
    public void getUdEffettiveSenzaVolume_queryIsOk() {
        Long idVerSerie = aLong();
        helper.getUdEffettiveSenzaVolume(idVerSerie);
        assertTrue(true);
    }

    @Test
    public void getUltimoProgressivoVolVerSerie_queryIsOk() {
        Long idVerSerie = aLong();
        helper.getUltimoProgressivoVolVerSerie(idVerSerie);
        assertTrue(true);
    }
}
