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

package it.eng.parer.job.indiceAipFascicoli.helper;

import static it.eng.ArquillianUtils.aInt;
import static it.eng.ArquillianUtils.aLong;
import static it.eng.ArquillianUtils.aString;
import static it.eng.ArquillianUtils.assertExceptionMessage;
import static it.eng.ArquillianUtils.createPaginatorJavaArchive;
import static it.eng.ArquillianUtils.createSacerJavaArchive;
import static it.eng.parer.web.helper.HelperTest.createSacerLogJar;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.junit.jupiter.api.Test;

import it.eng.parer.entity.FasVerAipFascicolo;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.volume.helper.VolumeHelper;
import it.eng.parer.volume.utils.ReturnParams;
import it.eng.parer.web.dto.DefinitoDaBean;
import it.eng.parer.web.helper.ComponentiHelper;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.ws.dto.CSChiaveFasc;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.utils.MessaggiWSFormat;

@ArquillianTest
public class CreazioneIndiceAipFascicoliHelperTest {
    @EJB
    private CreazioneIndiceAipFascicoliHelper helper;

    @Test
    void getIndexFasAipFascicoloDaElab_queryIsOk() {
        helper.getIndexFasAipFascicoloDaElab();
        assertTrue(true);
    }

    @Test
    void getProgressivoVersione_queryIsOk() {
        Long idFascicolo = aLong();
        helper.getProgressivoVersione(idFascicolo);
        assertTrue(true);
    }

    @Test
    void getVersioneAIP_queryIsOk() throws ParerInternalError {
        Long idFascicolo = aLong();
        String tiCreazione = aString();
        helper.getVersioneAIP(idFascicolo, tiCreazione);
        assertTrue(true);
    }

    @Test
    void findFasAipFascicoloDaElab_queryIsOk() {
        long idFasAipFascicoloDaElab = aLong();
        helper.findFasAipFascicoloDaElab(idFasAipFascicoloDaElab);
        assertTrue(true);
    }

    @Test
    void retrieveElvVLisIxAipFascByEleOrdered_queryIsOk() {
        long idElencoVersFasc = aLong();
        helper.retrieveElvVLisIxAipFascByEleOrdered(idElencoVersFasc);
        assertTrue(true);
    }

    @Test
    void retrieveIdModelliDaElaborare_queryIsOk() {
        long idAmbiente = aLong();
        String tiModelloXsd = aString();
        helper.retrieveIdModelliDaElaborare(idAmbiente, tiModelloXsd);
        assertTrue(true);
    }

    @Test
    void registraFasMetaVerAipFascicolo_queryIsOk() {
        FasVerAipFascicolo verAipFascicolo = new FasVerAipFascicolo();
        verAipFascicolo.setCdVerAip(aString());
        String hash = aString();
        String algoHash = aString();
        String encodingHash = aString();
        String codiceVersione = aString();
        CSVersatore versatore = new CSVersatore();
        versatore.setAmbiente(aString());
        versatore.setEnte(aString());
        versatore.setSistemaConservazione(aString());
        versatore.setStruttura(aString());
        CSChiaveFasc chiaveFasc = new CSChiaveFasc();
        chiaveFasc.setAnno(aInt());
        chiaveFasc.setNumero(aString());
        try {
            helper.registraFasMetaVerAipFascicolo(verAipFascicolo, hash, algoHash, encodingHash,
                    codiceVersione, versatore, chiaveFasc);
        } catch (Exception e) {
            // FasVerAipFascicolo non è completo quindi fallirà nell'insert
            assertExceptionMessage(e, "ConstraintViolationException");
        }
        assertTrue(true);
    }

    @Test
    void registraFasXsdMetaVerAipFasc_queryIsOk() {
        long idMetaVerAipFascicolo = aLong();
        long idModelloXsdFascicolo = aLong();
        String nmXsd = aString();
        try {
            helper.registraFasXsdMetaVerAipFasc(idMetaVerAipFascicolo, idModelloXsdFascicolo,
                    nmXsd);
            assertTrue(true);
        } catch (Exception e) {
            assertExceptionMessage(e, "ConstraintViolationException");
        }
    }

    @Test
    void retrieveFasVerAipFascicoloOrdered_queryIsOk() {
        long idElencoVersFasc = aLong();
        helper.retrieveFasVerAipFascicoloOrdered(idElencoVersFasc);
        assertTrue(true);
    }

    @Deployment
    public static Archive<?> createTestArchive() {
        return ShrinkWrap.create(EnterpriseArchive.class)
                .addAsResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsModule(createSacerJavaArchive(Collections.emptyList(),
                        CreazioneIndiceAipFascicoliHelper.class, ComponentiHelper.class,
                        VolumeHelper.class, ConfigurationHelper.class, DefinitoDaBean.class,
                        ReturnParams.class, CSVersatore.class, CSChiaveFasc.class,
                        CreazioneIndiceAipFascicoliHelperTest.class, MessaggiWSFormat.class))
                .addAsModule(createPaginatorJavaArchive()).addAsModule(createSacerLogJar());
    }
}
