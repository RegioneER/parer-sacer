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

package it.eng.parer.ws.replicaUtente.ejb;

import static it.eng.ArquillianUtils.aFlag;
import static it.eng.ArquillianUtils.aInt;
import static it.eng.ArquillianUtils.aListOfString;
import static it.eng.ArquillianUtils.aLong;
import static it.eng.ArquillianUtils.aString;
import static it.eng.ArquillianUtils.todayTs;
import static it.eng.ArquillianUtils.tomorrowTs;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.integriam.server.ws.reputente.ListaIndIp;
import it.eng.integriam.server.ws.reputente.ListaOrganizAbil;
import it.eng.integriam.server.ws.reputente.ListaServiziAutor;
import it.eng.integriam.server.ws.reputente.ListaTipiDatoAbil;
import it.eng.integriam.server.ws.reputente.OrganizAbil;
import it.eng.integriam.server.ws.reputente.Utente;
import it.eng.parer.web.helper.HelperTest;
import it.eng.parer.ws.dto.IRispostaWS;
import it.eng.parer.ws.replicaUtente.dto.InserimentoUtenteExt;
import it.eng.parer.ws.replicaUtente.dto.ModificaUtenteExt;
import it.eng.parer.ws.replicaUtente.dto.RispostaWSInserimentoUtente;
import it.eng.parer.ws.replicaUtente.dto.RispostaWSModificaUtente;

@ArquillianTest
public class InserimentoUtenteHelperTest {
    @EJB
    private InserimentoUtenteHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
	return HelperTest.createEnterpriseArchive(InserimentoUtenteHelperTest.class.getSimpleName(),
		HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
		HelperTest.createSacerJavaArchive(Arrays.asList(""),
			InserimentoUtenteHelperTest.class, InserimentoUtenteHelper.class,
			ModificaUtenteHelper.class, ModificaUtenteExt.class,
			RispostaWSModificaUtente.class, Utente.class, IRispostaWS.class,
			InserimentoUtenteExt.class, RispostaWSInserimentoUtente.class,
			ListaIndIp.class, ListaOrganizAbil.class, OrganizAbil.class,
			ListaServiziAutor.class, ListaTipiDatoAbil.class));
    }

    @Test
    void existsUtente_queryIsOk() {
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
