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

package it.eng.parer.amministrazioneStrutture.gestioneFormatiFileStandard.helper;

import it.eng.parer.amministrazioneStrutture.gestioneSistemaMigrazione.helper.SistemaMigrazioneHelperTest;
import it.eng.parer.entity.DecFormatoFileStandard;
import it.eng.parer.entity.DecFormatoProprieta;
import it.eng.parer.entity.DecFormatoValutazione;
import it.eng.parer.web.helper.HelperTest;
import it.eng.spagoLite.form.base.BaseElements;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class FormatoFileStandardHelperTest extends HelperTest<FormatoFileStandardHelper> {
    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(FormatoFileStandardHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), FormatoFileStandardHelperTest.class,
                        FormatoFileStandardHelper.class));
    }

    @Test
    public void getDecFormatoFileStandardInListQueryIsOk() {
        Set<String> formati = aSetOfString(2);
        BigDecimal idStrut = aBigDecimal();
        for (BaseElements.Status status : BaseElements.Status.values()) {
            helper.getDecFormatoFileStandardInList(formati, status, idStrut);
            assertTrue(true);
        }
    }

    @Test
    public void getDecFormatoFileStandardInListByNameQueryIsOk() {
        Collection<String> formati = aListOfString(2);
        helper.getDecFormatoFileStandardInListByName(formati);
        assertTrue(true);
    }

    @Test
    public void getDecFormatoFileStandardNotInListQueryIsOk() {
        Collection<String> formati = aListOfString(2);
        for (BaseElements.Status status : BaseElements.Status.values()) {
            helper.getDecFormatoFileStandardNotInList(formati, status);
            assertTrue(true);
        }
    }

    @Test
    public void getDecFormatoFileStandardByNameQueryIsOk() {
        String nmFormato = aString();
        helper.getDecFormatoFileStandardByName(nmFormato);
        assertTrue(true);
    }

    @Test
    public void getDecFormatoFileStandardNameFromEstensioneFileQueryIsOk() {
        String cdEstensioneFile = aString();
        try {
            helper.getDecFormatoFileStandardNameFromEstensioneFile(cdEstensioneFile);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    public void getDecEstensioneFileByNameQueryIsOk() {
        String cdEstensioneFile = aString();
        BigDecimal idFormatoFileStandard = aBigDecimal();
        helper.getDecEstensioneFileByName(cdEstensioneFile, idFormatoFileStandard);
        assertTrue(true);
    }

    @Test
    public void retrieveDecFormatoFileStandardListQueryIsOk() {
        String nmFormatoFileStandard = aString();
        final String nmMimetypeFile = aString();
        helper.retrieveDecFormatoFileStandardList(nmFormatoFileStandard, nmMimetypeFile);
        assertTrue(true);
    }

    @Test
    public void retrieveDecEstensioneFileListQueryIsOk() {
        BigDecimal idFormatoFileStandard = aBigDecimal();
        helper.retrieveDecEstensioneFileList(idFormatoFileStandard);
        assertTrue(true);
    }

    @Test
    public void getDecFormatoFileBustaByNameQueryIsOk() {
        String tiFormatoFirmaMarca = aString();
        BigDecimal idFormatoFileStandard = aBigDecimal();
        helper.getDecFormatoFileBustaByName(tiFormatoFirmaMarca, idFormatoFileStandard);
        assertTrue(true);
    }

    @Test
    public void getDecFormatoFileBustaListQueryIsOk() {
        BigDecimal idFormatoFileStandard = aBigDecimal();
        helper.getDecFormatoFileBustaList(idFormatoFileStandard);
        assertTrue(true);
    }

    @Test
    public void getAllDecFormatoGruppoProprietaQueryIsOk() {
        helper.getAllDecFormatoGruppoProprieta();
        assertTrue(true);
    }

    @Test
    public void getValutazioniPerFormatoQueryIsOk() {
        Long idFormato = -9L;
        final List<DecFormatoValutazione> valutazioniPerFormato = helper.getValutazioniPerFormato(idFormato);
        assertEquals(0, valutazioniPerFormato.size());
    }

    @Test
    public void getDecFormatoProprietaByGruppoQueryIsOk() {
        Long idGruppoProprieta = -9L;
        final List<DecFormatoProprieta> decFormatoProprietaByGruppo = helper
                .getDecFormatoProprietaByGruppo(idGruppoProprieta);
        assertEquals(0, decFormatoProprietaByGruppo.size());
    }

    @Test
    public void calcolaValutazioneQueryIsOk() {
        DecFormatoFileStandard formatoFileStandard = new DecFormatoFileStandard();
        formatoFileStandard.setIdFormatoFileStandard(-9L);
        final BigDecimal valutazione = helper.calcolaValutazione(formatoFileStandard);
        assertNull(valutazione);
    }

    @Test
    public void getMimetypeListQuerySyntaxOk() {
        assertFalse(helper.getMimetypeList().isEmpty());
    }
}
