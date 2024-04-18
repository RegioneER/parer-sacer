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

package it.eng.parer.ws.richiestaAnnullamentoVersamenti.ejb;

import it.eng.integriam.server.ws.reputente.Utente;
import it.eng.parer.web.helper.HelperTest;
import it.eng.parer.ws.replicaUtente.ejb.ModificaUtenteHelperTest;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import javax.ejb.EJB;

import static org.junit.Assert.assertTrue;
import it.eng.ArquillianUtils;
import static it.eng.ArquillianUtils.*;

public class InvioRichiestaAnnullamentoVersamentiHelperTest {
    @EJB
    private InvioRichiestaAnnullamentoVersamentiHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(InvioRichiestaAnnullamentoVersamentiHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""),
                        InvioRichiestaAnnullamentoVersamentiHelperTest.class,
                        InvioRichiestaAnnullamentoVersamentiHelper.class, Utente.class));
    }

    @Test
    public void isItemPresente_queryIsOk() {
        Long idStrut = aLong();
        String cdRegistroKeyUnitaDoc = aString();
        int aaKeyUnitaDoc = aInt();
        String cdKeyUnitaDoc = aString();

        helper.isItemPresente(idStrut, cdRegistroKeyUnitaDoc, aaKeyUnitaDoc, cdKeyUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void getIamUserByNmUserid_queryIsOk() {
        String nmUserid = aString();

        helper.getIamUserByNmUserid(nmUserid);
        assertTrue(true);
    }

    @Test
    public void getAroVLisItemRichAnnvrs_queryIsOk() {
        BigDecimal idRichAnnVers = aBigDecimal();

        helper.getAroVLisItemRichAnnvrs(idRichAnnVers);
        assertTrue(true);
    }
}
