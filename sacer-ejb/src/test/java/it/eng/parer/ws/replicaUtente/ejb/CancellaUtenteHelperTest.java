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

package it.eng.parer.ws.replicaUtente.ejb;

import it.eng.parer.exception.ParerInternalError;
import it.eng.parer.web.helper.HelperTest;
import it.eng.parer.ws.dto.IRispostaWS;
import it.eng.parer.ws.replicaUtente.dto.CancellaUtenteExt;
import it.eng.parer.ws.replicaUtente.dto.RispostaWSCancellaUtente;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import static it.eng.ArquillianUtils.*;
import it.eng.ArquillianUtils;
import org.jboss.arquillian.junit.Arquillian;
import javax.ejb.EJB;

@RunWith(Arquillian.class)
public class CancellaUtenteHelperTest {
    @EJB
    private CancellaUtenteHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(CancellaUtenteHelperTest.class.getSimpleName(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), CancellaUtenteHelperTest.class,
                        CancellaUtenteHelper.class, CancellaUtenteExt.class, RispostaWSCancellaUtente.class,
                        IRispostaWS.class),
                HelperTest.createSacerLogJar(), createPaginatorJavaArchive());
    }

    @Test
    public void isReferenced() {
        assertFalse(helper.isReferenced(-99));
    }

    @Test
    public void deleteIamUser_queryIsOk() {
        helper.deleteIamUser(-99L);
        assertTrue(true);
    }

    @Test
    public void delete_queryIsOk() throws ParerInternalError {
        final CancellaUtenteExt cuExt = new CancellaUtenteExt();
        cuExt.setIdUserIam(-99);
        helper.deleteIamUser(cuExt, new RispostaWSCancellaUtente());
        assertTrue(true);
    }
}
