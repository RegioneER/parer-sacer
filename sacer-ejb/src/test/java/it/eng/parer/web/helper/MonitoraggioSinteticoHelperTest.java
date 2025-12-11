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

/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.web.helper;

import static it.eng.ArquillianUtils.aBigDecimal;
import static it.eng.ArquillianUtils.aLong;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;

import javax.ejb.EJB;
import javax.ejb.EJBException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import it.eng.parer.viewEntity.MonVChkUdAmb;
import it.eng.parer.viewEntity.MonVChkUdEnte;
import it.eng.parer.viewEntity.MonVChkUdStrut;
import it.eng.parer.viewEntity.MonVChkUdTipoUd;
import it.eng.parer.viewEntity.MonVCntUdAnnulAmb;
import it.eng.parer.viewEntity.MonVCntUdAnnulEnte;
import it.eng.parer.viewEntity.MonVCntUdAnnulStrut;
import it.eng.parer.viewEntity.MonVCntUdAnnulTipoUd;
import it.eng.parer.web.ejb.MonitoraggioSinteticoEjb;

@ArquillianTest
public class MonitoraggioSinteticoHelperTest {
    @EJB
    private MonitoraggioSinteticoHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest
                .createEnterpriseArchive(MonitoraggioSinteticoHelperTest.class.getSimpleName(),
                        HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                        HelperTest.createSacerJavaArchive(Arrays.asList(""),
                                MonitoraggioSinteticoHelperTest.class,
                                MonitoraggioSinteticoHelper.class));
    }

    @Test
    void getMonVCnt_queryIsOk() {
        String parameters = MonitoraggioSinteticoEjb.VIEW_ID_TIPO_UNITA_DOC + " = :param1";

        String view = MonVCntUdAnnulTipoUd.class.getSimpleName();
        assertNotNull(helper.getMonVCnt(view, parameters, aBigDecimal(), null, null, null));

        view = MonVCntUdAnnulStrut.class.getSimpleName();
        parameters = MonitoraggioSinteticoEjb.VIEW_ID_STRUT + " = :param1";
        String select = "view.monVCntUdAnnulStrutId.idStrut, view.monVCntUdAnnulStrutId.tiStatoAnnul, sum(view.niAnnul) as niAnnul";
        String group_by = "view.monVCntUdAnnulStrutId.idStrut, view.monVCntUdAnnulStrutId.tiStatoAnnul";
        assertNotNull(helper.getMonVCnt(view, parameters, aBigDecimal(), null, select, group_by));

        view = MonVCntUdAnnulEnte.class.getSimpleName();
        parameters = MonitoraggioSinteticoEjb.VIEW_ID_ENTE + " = :param1 AND "
                + MonitoraggioSinteticoEjb.VIEW_ID_USER_IAM + " = :param2";
        select = "view.monVCntUdAnnulEnteId.idEnte, view.monVCntUdAnnulEnteId.idUserIam, view.monVCntUdAnnulEnteId.tiStatoAnnul, sum(view.niAnnul) as niAnnul";
        group_by = "view.monVCntUdAnnulEnteId.idEnte, view.monVCntUdAnnulEnteId.idUserIam, view.monVCntUdAnnulEnteId.tiStatoAnnul";
        assertNotNull(
                helper.getMonVCnt(view, parameters, aBigDecimal(), aLong(), select, group_by));

        view = MonVCntUdAnnulAmb.class.getSimpleName();
        parameters = MonitoraggioSinteticoEjb.VIEW_ID_AMBIENTE + " = :param1 AND "
                + MonitoraggioSinteticoEjb.VIEW_ID_USER_IAM + " = :param2";
        select = "view.monVCntUdAnnulAmbId.idAmbiente, view.monVCntUdAnnulAmbId.idUserIam, view.monVCntUdAnnulAmbId.tiStatoAnnul, sum(view.niAnnul) as niAnnul";
        group_by = "view.monVCntUdAnnulAmbId.idAmbiente, view.monVCntUdAnnulAmbId.idUserIam, view.monVCntUdAnnulAmbId.tiStatoAnnul";
        assertNotNull(
                helper.getMonVCnt(view, parameters, aBigDecimal(), aLong(), select, group_by));
    }

    @Test
    void getMonVChk_queryIsOk() {
        String parameters = MonitoraggioSinteticoEjb.VIEW_ID_TIPO_UNITA_DOC + " = :param1";
        String view = MonVChkUdTipoUd.class.getSimpleName();
        try {
            helper.getMonVChk(view, parameters, BigDecimal.ONE, null);
        } catch (EJBException p) {
            // è certo che non trovi il parametro, essendo una stringa random
            assertTrue(p.getMessage().contains("NoResultException"));
        }

        view = MonVChkUdStrut.class.getSimpleName();
        parameters = MonitoraggioSinteticoEjb.VIEW_ID_STRUT + " = :param1";
        try {
            helper.getMonVChk(view, parameters, BigDecimal.ONE, null);
        } catch (EJBException p) {
            // è certo che non trovi il parametro, essendo una stringa random
            assertTrue(p.getMessage().contains("NoResultException"));
        }

        view = MonVChkUdEnte.class.getSimpleName();
        parameters = MonitoraggioSinteticoEjb.VIEW_ID_ENTE + " = :param1 AND "
                + MonitoraggioSinteticoEjb.VIEW_ID_USER_IAM + " = :param2";
        try {
            helper.getMonVChk(view, parameters, BigDecimal.ONE, 1L);
        } catch (EJBException p) {
            // è certo che non trovi il parametro, essendo una stringa random
            assertTrue(p.getMessage().contains("NoResultException"));
        }

        view = MonVChkUdAmb.class.getSimpleName();
        parameters = MonitoraggioSinteticoEjb.VIEW_ID_AMBIENTE + " = :param1 AND "
                + MonitoraggioSinteticoEjb.VIEW_ID_USER_IAM + " = :param2";
        try {
            helper.getMonVChk(view, parameters, BigDecimal.ONE, 1L);
        } catch (EJBException p) {
            // è certo che non trovi il parametro, essendo una stringa random
            assertTrue(p.getMessage().contains("NoResultException"));
        }
    }
}
