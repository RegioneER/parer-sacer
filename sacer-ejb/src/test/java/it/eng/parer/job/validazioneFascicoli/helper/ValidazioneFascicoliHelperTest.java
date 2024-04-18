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

package it.eng.parer.job.validazioneFascicoli.helper;

import it.eng.parer.amministrazioneStrutture.gestioneStrutture.helper.AmbientiHelperTest;
import it.eng.parer.entity.constraint.ElvElencoVersFascDaElab;

import it.eng.parer.web.helper.HelperTest;
import it.eng.parer.ws.dto.CSChiaveFasc;
import it.eng.parer.ws.dto.CSVersatore;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

import org.junit.runner.RunWith;
import static it.eng.ArquillianUtils.*;
import it.eng.ArquillianUtils;
import org.jboss.arquillian.junit.Arquillian;
import javax.ejb.EJB;

@RunWith(Arquillian.class)
public class ValidazioneFascicoliHelperTest {
    @EJB
    private ValidazioneFascicoliHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(ValidazioneFascicoliHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), ValidazioneFascicoliHelperTest.class,
                        it.eng.parer.ws.dto.CSChiave.class, ValidazioneFascicoliHelper.class, CSVersatore.class,
                        CSChiaveFasc.class));
    }

    @Test
    public void getElvElencoVersFascDaElab_queryIsOk() {
        long idStrut = aLong();
        for (ElvElencoVersFascDaElab.TiStatoElencoFascDaElab tiStato : ElvElencoVersFascDaElab.TiStatoElencoFascDaElab
                .values()) {
            helper.getElvElencoVersFascDaElab(idStrut, tiStato);
            assertTrue(true);
        }
    }

    @Test
    public void getFascicoliInElencoNonAnnullati_queryIsOk() {
        long idElencoVersFasc = aLong();
        helper.getFascicoliInElencoNonAnnullati(idElencoVersFasc);
        assertTrue(true);
    }

    @Test
    public void existsUdFascicoloByStatoCons_queryIsOk() {
        long idFascicolo = aLong();
        String tiStatoConservazione = aString();
        helper.existsUdFascicoloByStatoCons(idFascicolo, tiStatoConservazione);
        assertTrue(true);
    }

    @Test
    public void getUdFascicoloByStatoCons_queryIsOk() {
        long idFascicolo = aLong();
        String tiStatoConservazione = aString();
        helper.getUdFascicoloByStatoCons(idFascicolo, tiStatoConservazione);
        assertTrue(true);
    }

    @Test
    public void getLastPgFascicoloCoda_queryIsOk() {
        long idFascicolo = aLong();
        helper.getLastPgFascicoloCoda(idFascicolo);
        assertTrue(true);
    }

    @Test
    public void allUdFascicoloStatiConservazione_queryIsOk() {
        long idFascicolo = aLong();
        List<String> statiConservazione = aListOfString(2);
        helper.allUdFascicoloStatiConservazione(idFascicolo, statiConservazione);
        assertTrue(true);
    }

    @Test
    public void getUltimaVersioneIndiceAip_queryIsOk() {
        long idUnitaDoc = aLong();
        helper.getUltimaVersioneIndiceAip(idUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void allFascicoliAnnullati_queryIsOk() {
        long idElencoVersFasc = aLong();
        try {
            helper.allFascicoliAnnullati(idElencoVersFasc);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    public void allAipFascInCoda_queryIsOk() {
        long idElencoVersFasc = aLong();
        try {
            helper.allAipFascInCoda(idElencoVersFasc);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    public void getVolumeUnitaDocPerDataMarcatura_queryIsOk() {
        long idUnitaDoc = aLong();
        helper.getVolumeUnitaDocPerDataMarcatura(idUnitaDoc);
        assertTrue(true);
    }
}
