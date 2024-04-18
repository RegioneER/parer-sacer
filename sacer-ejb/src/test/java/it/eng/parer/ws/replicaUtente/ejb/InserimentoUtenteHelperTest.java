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

import it.eng.integriam.server.ws.reputente.*;
import it.eng.parer.web.helper.HelperTest;
import it.eng.parer.web.helper.UserHelperTest;
import it.eng.parer.ws.dto.IRispostaWS;
import it.eng.parer.ws.replicaUtente.dto.InserimentoUtenteExt;
import it.eng.parer.ws.replicaUtente.dto.ModificaUtenteExt;
import it.eng.parer.ws.replicaUtente.dto.RispostaWSInserimentoUtente;
import it.eng.parer.ws.replicaUtente.dto.RispostaWSModificaUtente;
import java.util.ArrayList;
import java.util.Arrays;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import org.junit.runner.RunWith;
import static it.eng.ArquillianUtils.*;
import it.eng.ArquillianUtils;
import org.jboss.arquillian.junit.Arquillian;
import javax.ejb.EJB;

@RunWith(Arquillian.class)
public class InserimentoUtenteHelperTest {
    @EJB
    private InserimentoUtenteHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(InserimentoUtenteHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), InserimentoUtenteHelperTest.class,
                        InserimentoUtenteHelper.class, ModificaUtenteHelper.class, ModificaUtenteExt.class,
                        RispostaWSModificaUtente.class, Utente.class, IRispostaWS.class, InserimentoUtenteExt.class,
                        RispostaWSInserimentoUtente.class, ListaIndIp.class, ListaOrganizAbil.class, OrganizAbil.class,
                        ListaServiziAutor.class, ListaTipiDatoAbil.class));
    }

    @Test
    public void existsUtente_queryIsOk() {
        long idUserIam = aLong();
        helper.existsUtente(idUserIam);
        assertTrue(true);
    }

    private Utente aUtente() {
        final Utente utente = new Utente();
        utente.setCdFisc(aString());
        utente.setCdPsw(aString());
        utente.setCdSalt(aString());
        utente.setDsEmail(aString());
        utente.setDtRegPsw(todayTs());
        utente.setDtScadPsw(tomorrowTs());
        utente.setFlAttivo(aFlag());
        utente.setFlContrIp(aFlag());
        utente.setFlUserAdmin(aFlag());
        utente.setIdUserIam(1L);

        final ListaIndIp listaIndIp = new ListaIndIp();
        listaIndIp.setIndIp(new ArrayList<>());
        listaIndIp.getIndIp().add("127.0.0.1");
        utente.setListaIndIp(listaIndIp);

        final ListaOrganizAbil listaOrganizAbil = new ListaOrganizAbil();
        listaOrganizAbil.setOrganizAbilList(new ArrayList<>());
        final OrganizAbil organizAbil = new OrganizAbil();
        organizAbil.setFlOrganizDefault(true);
        organizAbil.setIdOrganizApplicAbil(aInt());
        final ListaServiziAutor listaServiziAutor = new ListaServiziAutor();
        listaServiziAutor.setNmServizioAutor(aListOfString(2));
        organizAbil.setListaServiziAutor(listaServiziAutor);
        listaOrganizAbil.getOrganizAbilList().add(organizAbil);
        utente.setListaOrganizAbil(listaOrganizAbil);
        utente.setNmCognomeUser(aString());
        utente.setNmNomeUser(aString());
        utente.setNmUserid(aString());
        utente.setTipoUser(aString());
        return utente;
    }
}
