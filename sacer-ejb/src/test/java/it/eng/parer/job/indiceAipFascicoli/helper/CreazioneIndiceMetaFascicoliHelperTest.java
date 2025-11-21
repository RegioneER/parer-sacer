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
import static it.eng.ArquillianUtils.assertNoResultException;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.parer.web.helper.HelperTest;
import it.eng.parer.ws.dto.CSChiaveFasc;
import it.eng.parer.ws.dto.CSVersatore;

@ArquillianTest
public class CreazioneIndiceMetaFascicoliHelperTest {
    @EJB
    private CreazioneIndiceMetaFascicoliHelper helper;

    @Test
    void getFasVVisFascicolo_queryIsOk() {
        Long idFascicolo = aLong();
        try {
            helper.getFasVVisFascicolo(idFascicolo);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    void getFasVLisUdInFasc_queryIsOk() {
        Long idFascicolo = aLong();
        Long idUserIamCorrente = aLong();
        helper.getFasVLisUdInFasc(idFascicolo, idUserIamCorrente);
        assertTrue(true);
    }

    @Test
    void getFasAmminPartec_queryIsOk() {
        Long idFascicolo = aLong();
        helper.getFasAmminPartec(idFascicolo);
        assertTrue(true);
    }

    @Test
    void getFasSogFascicolo_queryIsOk() {
        Long idFascicolo = aLong();
        helper.getFasSogFascicolo(idFascicolo);
        assertTrue(true);
    }

    @Test
    void getFasRespFascicolo_queryIsOk() {
        Long idFascicolo = aLong();
        helper.getFasRespFascicolo(idFascicolo);
        assertTrue(true);
    }

    @Test
    void getFasUniOrgRespFascicolo_queryIsOk() {
        Long idFascicolo = aLong();
        helper.getFasUniOrgRespFascicolo(idFascicolo);
        assertTrue(true);
    }

    @Test
    void getDecValVoceTitol_queryIsOk() {
        Long idVoceTitol = aLong();
        helper.getDecValVoceTitol(idVoceTitol);
        assertTrue(true);
    }

    @Test
    void getFasLinkFascicolo_queryIsOk() {
        Long idFascicolo = aLong();
        helper.getFasLinkFascicolo(idFascicolo);
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

    // @Test
    // void registraFasFileMetaVerAipFasc_queryIsOk() {
    // long idMetaVerAipFascicolo = aLong();
    // String file = aString();
    // OrgStrut strut = new OrgStrut();
    // strut.setIdStrut(aLong());
    // Date dtCreazione = todayTs();
    // BackendStorage backendMetadata;
    // Map<String, String> indiceAipFascicoloBlob;
    //
    //
    // try {
    // helper.registraFasFileMetaVerAipFasc(idMetaVerAipFascicolo, file, strut, dtCreazione);
    // } catch (Exception e) {
    // // OrgStrut non è persistibile, mancano dei dati
    // assertExceptionMessage(e, "ConstraintViolationException");
    // }
    // }

    @Test
    void registraFasMetaVerAipFascicolo_queryIsOk() {
        long idVerAipFascicolo = aLong();
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
            helper.registraFasMetaVerAipFascicolo(idVerAipFascicolo, hash, algoHash, encodingHash,
                    codiceVersione, versatore, chiaveFasc);
            assertTrue(true);
        } catch (Exception e) {
            assertExceptionMessage(e, "ConstraintViolationException");
        }
    }

    @Test
    void retrieveIdModelliFascicoloDaElaborare_queryIsOk() {
        long idAmbiente = aLong();
        helper.retrieveIdModelliFascicoloDaElaborare(idAmbiente);
        assertTrue(true);
    }

    // MEV26576
    @Test
    void retrieveIdModelliFascicoloDaElaborareV2_queryIsOk() {
        long idAmbiente = aLong();
        String cdVersioneXml = aString();
        helper.retrieveIdModelliFascicoloDaElaborareV2(idAmbiente, cdVersioneXml);
        assertTrue(true);
    }
    // end MEV26576

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(
                CreazioneIndiceMetaFascicoliHelperTest.class.getSimpleName(),
                HelperTest.createSacerLogJar(), HelperTest.createPaginatorJavaArchive(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""),
                        CreazioneIndiceMetaFascicoliHelperTest.class,
                        CreazioneIndiceMetaFascicoliHelper.class, CSVersatore.class,
                        CSChiaveFasc.class, it.eng.parer.ws.utils.MessaggiWSFormat.class));
    }
}
