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

package it.eng.parer.informazioni.noteRilascio.helper;

import it.eng.parer.disciplinare.DisciplinareTecnicoHelperTest;
import it.eng.parer.web.helper.HelperTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertTrue;

import org.junit.runner.RunWith;
import static it.eng.ArquillianUtils.*;
import it.eng.ArquillianUtils;
import org.jboss.arquillian.junit.Arquillian;
import javax.ejb.EJB;

@RunWith(Arquillian.class)
public class NoteRilascioHelperTest {
    @EJB
    private NoteRilascioHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(NoteRilascioHelperTest.class.getSimpleName(),
                HelperTest.createSacerLogJar(), HelperTest.createPaginatorJavaArchive(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), NoteRilascioHelperTest.class,
                        NoteRilascioHelper.class));
    }

    @Test
    public void getAplApplicByName_queryIsOk() {
        String nmApplic = aString();

        helper.getAplApplicByName(nmApplic);
        assertTrue(true);

    }

    @Test
    public void getAplApplic_queryIsOk() {
        String name = "SACER";
        helper.getAplApplic(name);
        assertTrue(true);

    }

    @Test
    public void getAplNoteRilascioList_queryIsOk() {
        BigDecimal idApplic = aBigDecimal();

        helper.getAplNoteRilascioList(idApplic);
        assertTrue(true);

    }

    @Test
    public void getAplNotaRilascioByVersione_queryIsOk() {
        String cdVersione = aString();

        helper.getAplNotaRilascioByVersione(cdVersione);
        assertTrue(true);

    }

    @Test
    public void getAplNotaRilascioById_queryIsOk() {
        BigDecimal idNotaRilascio = aBigDecimal();

        helper.getAplNotaRilascioById(idNotaRilascio);
        assertTrue(true);

    }

    @Test
    public void getAplApplicById_queryIsOk() {
        BigDecimal idApplic = aBigDecimal();

        helper.getAplApplicById(idApplic);
        assertTrue(true);

    }

    @Test
    public void getAplNoteRilascioPrecList_queryIsOk() {
        BigDecimal idApplic = aBigDecimal();
        BigDecimal idNotaRilascio = aBigDecimal();
        Date dtVersione = todayTs();

        helper.getAplNoteRilascioPrecList(idApplic, idNotaRilascio, dtVersione);
        assertTrue(true);

    }
}
