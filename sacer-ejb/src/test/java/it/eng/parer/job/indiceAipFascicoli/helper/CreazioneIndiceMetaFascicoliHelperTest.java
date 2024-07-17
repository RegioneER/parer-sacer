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

package it.eng.parer.job.indiceAipFascicoli.helper;

import it.eng.parer.entity.OrgStrut;
import it.eng.parer.job.indiceAipSerieUd.helper.CreazioneIndiceVolumeSerieUdHelperTest;
import it.eng.parer.web.helper.HelperTest;
import it.eng.parer.ws.dto.CSChiaveFasc;
import it.eng.parer.ws.dto.CSVersatore;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertTrue;

import org.junit.runner.RunWith;
import static it.eng.ArquillianUtils.*;
import it.eng.ArquillianUtils;
import org.jboss.arquillian.junit.Arquillian;
import javax.ejb.EJB;

@RunWith(Arquillian.class)
public class CreazioneIndiceMetaFascicoliHelperTest {
    @EJB
    private CreazioneIndiceMetaFascicoliHelper helper;

    @Test
    public void getFasVVisFascicolo_queryIsOk() {
        Long idFascicolo = aLong();
        try {
            helper.getFasVVisFascicolo(idFascicolo);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    public void getFasVLisUdInFasc_queryIsOk() {
        Long idFascicolo = aLong();
        Long idUserIamCorrente = aLong();
        helper.getFasVLisUdInFasc(idFascicolo, idUserIamCorrente);
        assertTrue(true);
    }

    @Test
    public void getFasAmminPartec_queryIsOk() {
        Long idFascicolo = aLong();
        helper.getFasAmminPartec(idFascicolo);
        assertTrue(true);
    }

    @Test
    public void getFasSogFascicolo_queryIsOk() {
        Long idFascicolo = aLong();
        helper.getFasSogFascicolo(idFascicolo);
        assertTrue(true);
    }

    @Test
    public void getFasRespFascicolo_queryIsOk() {
        Long idFascicolo = aLong();
        helper.getFasRespFascicolo(idFascicolo);
        assertTrue(true);
    }

    @Test
    public void getFasUniOrgRespFascicolo_queryIsOk() {
        Long idFascicolo = aLong();
        helper.getFasUniOrgRespFascicolo(idFascicolo);
        assertTrue(true);
    }

    @Test
    public void getDecValVoceTitol_queryIsOk() {
        Long idVoceTitol = aLong();
        helper.getDecValVoceTitol(idVoceTitol);
        assertTrue(true);
    }

    @Test
    public void getFasLinkFascicolo_queryIsOk() {
        Long idFascicolo = aLong();
        helper.getFasLinkFascicolo(idFascicolo);
        assertTrue(true);
    }

    @Test
    public void registraFasXsdMetaVerAipFasc_queryIsOk() {
        long idMetaVerAipFascicolo = aLong();
        long idModelloXsdFascicolo = aLong();
        String nmXsd = aString();
        try {
            helper.registraFasXsdMetaVerAipFasc(idMetaVerAipFascicolo, idModelloXsdFascicolo, nmXsd);
            assertTrue(true);
        } catch (Exception e) {
            assertExceptionMessage(e, "ConstraintViolationException");
        }
    }

    // @Test
    // public void registraFasFileMetaVerAipFasc_queryIsOk() {
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
    public void registraFasMetaVerAipFascicolo_queryIsOk() {
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
            helper.registraFasMetaVerAipFascicolo(idVerAipFascicolo, hash, algoHash, encodingHash, codiceVersione,
                    versatore, chiaveFasc);
            assertTrue(true);
        } catch (Exception e) {
            assertExceptionMessage(e, "ConstraintViolationException");
        }
    }

    @Test
    public void retrieveIdModelliFascicoloDaElaborare_queryIsOk() {
        long idAmbiente = aLong();
        helper.retrieveIdModelliFascicoloDaElaborare(idAmbiente);
        assertTrue(true);
    }

    // MEV26576
    @Test
    public void retrieveIdModelliFascicoloDaElaborareV2_queryIsOk() {
        long idAmbiente = aLong();
        String cdVersioneXml = aString();
        helper.retrieveIdModelliFascicoloDaElaborareV2(idAmbiente, cdVersioneXml);
        assertTrue(true);
    }
    // end MEV26576

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(CreazioneIndiceMetaFascicoliHelperTest.class.getSimpleName(),
                HelperTest.createSacerLogJar(), HelperTest.createPaginatorJavaArchive(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), CreazioneIndiceMetaFascicoliHelperTest.class,
                        CreazioneIndiceMetaFascicoliHelper.class, CSVersatore.class, CSChiaveFasc.class,
                        it.eng.parer.ws.utils.MessaggiWSFormat.class));
    }
}
