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

package it.eng.parer.async.helper;

import it.eng.parer.web.dto.MonitoraggioFiltriListaVersFallitiBean;

import it.eng.parer.web.helper.HelperTest;
import it.eng.parer.web.util.Constants;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import org.junit.runner.RunWith;
import static it.eng.ArquillianUtils.*;

import org.jboss.arquillian.junit.Arquillian;

@RunWith(Arquillian.class)
public class CalcoloMonitoraggioHelperTest {
    @EJB
    private CalcoloMonitoraggioHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(CalcoloMonitoraggioHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), CalcoloMonitoraggioHelperTest.class,
                        CalcoloMonitoraggioHelper.class, MonitoraggioFiltriListaVersFallitiBean.class,
                        Constants.class));
    }

    @Test
    public void getListaSessioniVersByUsr_queryIsOk() {
        helper.getListaSessioniVersByUsr();
        assertTrue(true);
    }

    @Test
    public void getListaVersFallitiDaVerif_queryIsOk() {
        Long idStrut = aLong();
        Date ultimaRegistrazione = todayTs();
        helper.getListaVersFallitiDaVerif(idStrut, ultimaRegistrazione);
        assertTrue(true);
    }

    @Test
    public void getListaVersFallitiDaNorisol_queryIsOk() {
        Long idStrut = aLong();
        Date ultimaRegistrazione = todayTs();
        helper.getListaVersFallitiDaNorisol(idStrut, ultimaRegistrazione);
        assertTrue(true);
    }

    @Test
    public void getUltimaRegistrazione_String_Long_queryIsOk() {
        String nmJob = aString();
        Long idStrut = aLong();
        helper.getUltimaRegistrazione(nmJob, idStrut);
        assertTrue(true);
    }

    @Test
    public void getUltimaRegistrazione_String_queryIsOk() {
        String nmJob = aString();
        helper.getUltimaRegistrazione(nmJob);
        assertTrue(true);
    }

    @Test
    public void getStruttureVersanti_queryIsOk() {
        helper.getStruttureVersanti();
        assertTrue(true);
    }

    @Test
    public void getUnitaDocIfExists_queryIsOk() {
        Long idStrut = aLong();
        String cdKeyUnitaDoc = aString();
        BigDecimal aaKeyUnitaDoc = aBigDecimal();
        String cdRegistroKeyUnitaDoc = aString();
        helper.getUnitaDocIfExists(idStrut, cdKeyUnitaDoc, aaKeyUnitaDoc, cdRegistroKeyUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void getSessioniSenzaChiave_queryIsOk() {
        MonitoraggioFiltriListaVersFallitiBean filtriSes = new MonitoraggioFiltriListaVersFallitiBean();
        filtriSes.setIdAmbiente(aBigDecimal());
        filtriSes.setIdEnte(aBigDecimal());
        filtriSes.setIdStrut(aBigDecimal());
        filtriSes.setTipoSes(aString());
        filtriSes.setRisolto(aFlag());
        filtriSes.setPeriodoVers("OGGI");
        filtriSes.setGiornoVersDaValidato(todayTs());
        filtriSes.setGiornoVersAValidato(tomorrowTs());
        filtriSes.setVerificato(aFlag());
        filtriSes.setNonRisolubile(aFlag());
        filtriSes.setClasseErrore(aString());
        filtriSes.setSottoClasseErrore(aString());
        filtriSes.setCodiceErrore(aString());
        filtriSes.setIdUserIam(aBigDecimal());
        helper.getSessioniKoSenzaChiave(filtriSes);
        assertTrue(true);
    }

}
