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

package it.eng.parer.job.indiceAip.helper;

import it.eng.parer.elencoVersamento.helper.ElencoVersamentoHelper;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.entity.*;
import it.eng.parer.entity.constraint.FasFascicolo;
import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.job.dto.SessioneVersamentoExt;
import it.eng.parer.volume.helper.VolumeHelper;
import it.eng.parer.volume.utils.ReturnParams;
import it.eng.parer.web.dto.DefinitoDaBean;
import it.eng.parer.web.helper.ComponentiHelper;
import it.eng.parer.web.helper.ConfigurationHelper;

import it.eng.parer.web.helper.UnitaDocumentarieHelper;
import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.utils.MessaggiWSFormat;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;

import java.util.Arrays;

import static it.eng.parer.web.helper.HelperTest.createSacerLogJar;
import static org.junit.Assert.assertTrue;

import org.junit.runner.RunWith;
import static it.eng.ArquillianUtils.*;
import it.eng.ArquillianUtils;
import org.jboss.arquillian.junit.Arquillian;
import javax.ejb.EJB;

@RunWith(Arquillian.class)
public class CreazioneIndiceAipHelperTest {
    @EJB
    private CreazioneIndiceAipHelper helper;

    @Test
    public void getIndexAplIndiceAipUdDaElabQueryIsOk() {
        helper.getIndexAplIndiceAipUdDaElab();
        assertTrue(true);
    }

    @Test
    public void existsIndexAplIndiceAipInCodaPrgMinoreQueryIsOk() {
        AroUnitaDoc ud = mockAroUnitaDoc();
        long progressivoIndiceAip = aLong();
        helper.existsIndexAplIndiceAipInCodaPrgMinore(ud, progressivoIndiceAip);
        assertTrue(true);
    }

    private AroUnitaDoc mockAroUnitaDoc() {
        AroUnitaDoc ud = new AroUnitaDoc();
        ud.setIdUnitaDoc(aLong());
        ud.setOrgStrut(new OrgStrut());
        ud.getOrgStrut().setIdStrut(aLong());
        ud.getOrgStrut().setOrgEnte(new OrgEnte());
        ud.getOrgStrut().getOrgEnte().setOrgAmbiente(new OrgAmbiente());
        ud.getOrgStrut().getOrgEnte().getOrgAmbiente().setIdEnteConserv(aBigDecimal());
        return ud;
    }

    @Test
    public void getProgressivoVersioneQueryIsOk() {
        Long idUnitaDoc = aLong();
        helper.getProgressivoVersione(idUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void getVersioneAIPQueryIsOk() throws ParerInternalError {
        Long idUnitaDoc = aLong();
        String tiCreazione = aString();
        helper.getVersioneAIP(idUnitaDoc, tiCreazione);
        assertTrue(true);
    }

    @Test
    public void getVersioneXSDIndiceAIPQueryIsOk() throws ParerInternalError {
        Long idUnitaDoc = aLong();
        helper.getVersioneXSDIndiceAIP(idUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void getAroIndiceAipUdQueryIsOk() {
        Long idUnitaDoc = aLong();
        helper.getAroIndiceAipUd(idUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void creaAIP_7args_1QueryIsOk() {
        AroIndiceAipUdDaElab udDaElab = mockAroIndiceAipUdDaElab();
        String annoMese = "2020";
        int progressivoVersione = aInt();
        String codiceVersione = aString();
        String hash = aString();
        String xml = aString();
        CSVersatore versatore = new CSVersatore();
        versatore.setAmbiente(aString());
        versatore.setEnte(aString());
        versatore.setSistemaConservazione(aString());
        versatore.setStruttura(aString());
        CSChiave chiave = new CSChiave();
        chiave.setAnno(aLong());
        chiave.setNumero(aString());
        chiave.setTipoRegistro(aString());
        try {
            helper.creaAIP(udDaElab, annoMese, progressivoVersione, codiceVersione, hash, xml, versatore, chiave);
        } catch (Exception e) {
            // errore dovuto al fatto che udDaElab non è persistita
            assertExceptionMessage(e, "Transaction rolled back");
        }
    }

    private AroIndiceAipUdDaElab mockAroIndiceAipUdDaElab() {
        AroIndiceAipUdDaElab udDaElab = new AroIndiceAipUdDaElab();
        udDaElab.setAroUnitaDoc(mockAroUnitaDoc());

        return udDaElab;
    }

    @Test
    public void creaAIP_7args_2QueryIsOk() {
        AroUnitaDoc ud = mockAroUnitaDoc();
        String annoMese = aString();
        int progressivoVersione = aInt();
        String codiceVersione = aString();
        String hash = aString();
        String urnIndiceAIP = aString();
        String xml = aString();
        CSVersatore versatore = new CSVersatore();
        versatore.setAmbiente(aString());
        versatore.setEnte(aString());
        versatore.setSistemaConservazione(aString());
        versatore.setStruttura(aString());
        CSChiave chiave = new CSChiave();
        chiave.setAnno(aLong());
        chiave.setNumero(aString());
        chiave.setTipoRegistro(aString());
        try {
            helper.creaAIP(ud, annoMese, progressivoVersione, codiceVersione, hash, urnIndiceAIP, versatore, chiave);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    public void findAroIndiceAipUdDaElabQueryIsOk() {
        long idAroIndiceAipUdDaElab = aLong();
        helper.findAroIndiceAipUdDaElab(idAroIndiceAipUdDaElab);
        assertTrue(true);
    }

    @Test
    public void checkComponentiPresentiCountQueryIsOk() {
        long idUnitaDoc = aLong();
        long idVerIndiceAip = aLong();
        helper.checkComponentiPresentiCount(idUnitaDoc, idVerIndiceAip);
        assertTrue(true);
    }

    @Test
    public void retrieveAroVerIndiceAipUdOrderedQueryIsOk() {
        long idElencoVers = aLong();
        helper.retrieveAroVerIndiceAipUdOrdered(idElencoVers);
        assertTrue(true);
    }

    @Test
    public void getVersioniSerieCorrentiContenEffettivoByUdAndStatoQueryIsOk() {
        long idUnitaDoc = aLong();
        String[] statiSerie = aStringArray(2);
        helper.getVersioniSerieCorrentiContenEffettivoByUdAndStato(idUnitaDoc, statiSerie);
        assertTrue(true);
    }

    @Test
    public void getFascicoliByUdAndStatoQueryIsOk() {
        long idUnitaDoc = aLong();
        FasFascicolo.TiStatoFascElencoVers[] statiFascicolo = { FasFascicolo.TiStatoFascElencoVers.IN_ATTESA_SCHED,
                FasFascicolo.TiStatoFascElencoVers.NON_SELEZ_SCHED };
        helper.getFascicoliByUdAndStato(idUnitaDoc, statiFascicolo);
        assertTrue(true);
    }

    @Test
    public void getVersioneIndiceAIPV2QueryIsOk() throws ParerInternalError {
        String tiCreazione = "ANTICIPATO";
        Long idUnitaDoc = -9L;
        helper.getVersioneIndiceAIPV2(idUnitaDoc, tiCreazione);
        assertTrue(true);
    }

    @Test
    public void getUltimaVersioneIndiceAipQueryIsOk() {
        Long idUnitaDoc = -9L;
        helper.getUltimaVersioneIndiceAip(idUnitaDoc);
        assertTrue(true);
    }

    @Deployment
    public static Archive<?> createTestArchive() {
        return createEnterpriseArchive(CreazioneIndiceAipHelperTest.class.getSimpleName(), createSacerLogJar(),
                createPaginatorJavaArchive(),
                createSacerJavaArchive(Arrays.asList("it.eng.parer.elencoVersamento.utils"),
                        CreazioneIndiceAipHelper.class, ComponentiHelper.class, VolumeHelper.class,
                        ConfigurationHelper.class, ReturnParams.class, DefinitoDaBean.class,
                        ElencoVersamentoHelper.class, UnitaDocumentarieHelper.class, Constants.class, CSVersatore.class,
                        CSChiave.class, ElencoEnums.class, CreazioneIndiceAipHelperTest.class,
                        SessioneVersamentoExt.class, MessaggiWSFormat.class));
    }
}
