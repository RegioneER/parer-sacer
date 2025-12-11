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
import static it.eng.ArquillianUtils.aDateArray;
import static it.eng.ArquillianUtils.aListOfString;
import static it.eng.ArquillianUtils.aLong;
import static it.eng.ArquillianUtils.aSetOfString;
import static it.eng.ArquillianUtils.aString;
import static it.eng.ArquillianUtils.todayTs;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.parer.entity.constraint.MonContaSesUpdUdKo;

@ArquillianTest
public class MonitoraggioAggMetaHelperTest {
    @EJB
    private MonitoraggioAggMetaHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest
                .createEnterpriseArchive(MonitoraggioAggMetaHelperTest.class.getSimpleName(),
                        HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                        HelperTest.createSacerJavaArchive(Arrays.asList(""),
                                MonitoraggioAggMetaHelperTest.class,
                                MonitoraggioAggMetaHelper.class));
    }

    @Test
    void getTotali() {

        String tipoEntita = aString();
        BigDecimal idUser = aBigDecimal();
        BigDecimal idAmbiente = aBigDecimal();
        BigDecimal idEnte = aBigDecimal();
        BigDecimal idStrut = aBigDecimal();
        BigDecimal aaKeyUnitaDoc = aBigDecimal();
        BigDecimal aaKeyUnitaDocDa = aBigDecimal();
        BigDecimal aaKeyUnitaDocA = aBigDecimal();
        BigDecimal idTipoUnitaDoc = aBigDecimal();
        BigDecimal idRegistroUnitaDoc = aBigDecimal();
        BigDecimal idTipoDocPrinc = aBigDecimal();
        assertNotNull(helper.getTotali(tipoEntita, idUser, idAmbiente, idEnte, idStrut,
                aaKeyUnitaDoc, aaKeyUnitaDocDa, aaKeyUnitaDocA, idTipoUnitaDoc, idRegistroUnitaDoc,
                idTipoDocPrinc));
    }

    @Test
    void getTotaliDataCorrente() {

        String tipoEntita = aString();
        BigDecimal idUser = aBigDecimal();
        BigDecimal idAmbiente = aBigDecimal();
        BigDecimal idEnte = aBigDecimal();
        BigDecimal idStrut = aBigDecimal();
        BigDecimal aaKeyUnitaDoc = aBigDecimal();
        BigDecimal aaKeyUnitaDocDa = aBigDecimal();
        BigDecimal aaKeyUnitaDocA = aBigDecimal();
        BigDecimal idTipoUnitaDoc = aBigDecimal();
        BigDecimal idRegistroUnitaDoc = aBigDecimal();
        BigDecimal idTipoDocPrinc = aBigDecimal();
        assertNotNull(helper.getTotaliDataCorrente(tipoEntita, idUser, idAmbiente, idEnte, idStrut,
                aaKeyUnitaDoc, aaKeyUnitaDocDa, aaKeyUnitaDocA, idTipoUnitaDoc, idRegistroUnitaDoc,
                idTipoDocPrinc));
    }

    @Test
    void getTotaliFalliti() {

        String tipoEntita = aString();
        BigDecimal idUser = aBigDecimal();
        BigDecimal idAmbiente = aBigDecimal();
        BigDecimal idEnte = aBigDecimal();
        BigDecimal idStrut = aBigDecimal();
        BigDecimal aaKeyUnitaDoc = aBigDecimal();
        BigDecimal aaKeyUnitaDocDa = aBigDecimal();
        BigDecimal aaKeyUnitaDocA = aBigDecimal();
        BigDecimal idTipoUnitaDoc = aBigDecimal();
        BigDecimal idRegistroUnitaDoc = aBigDecimal();
        BigDecimal idTipoDocPrinc = aBigDecimal();
        Map<String, Object> result = helper.getTotaliFalliti(tipoEntita, idUser, idAmbiente, idEnte,
                idStrut, aaKeyUnitaDoc, aaKeyUnitaDocDa, aaKeyUnitaDocA, idTipoUnitaDoc,
                idRegistroUnitaDoc, idTipoDocPrinc);
        assertNotNull(result);
    }

    @Test
    void retrieveVLisAggMeta() {

        BigDecimal idUser = aBigDecimal();
        BigDecimal idAmbiente = aBigDecimal();
        BigDecimal idEnte = aBigDecimal();
        BigDecimal idStrut = aBigDecimal();
        BigDecimal idTipoUnitaDoc = aBigDecimal();
        BigDecimal idRegistroUnitaDoc = aBigDecimal();
        BigDecimal idTipoDoc = aBigDecimal();
        Date[] dateValidate = aDateArray(2);
        BigDecimal rangeAnnoDa = aBigDecimal();
        BigDecimal rangeAnnoA = aBigDecimal();
        String rangeNumeroDa = aString();
        String rangeNumeroA = aString();
        Set<String> statoIndiceAip = aSetOfString(3);
        String flSesUpdKoRisolti = aString();
        assertNotNull(helper.retrieveVLisAggMeta(idUser, idAmbiente, idEnte, idStrut,
                idTipoUnitaDoc, idRegistroUnitaDoc, idTipoDoc, dateValidate, rangeAnnoDa,
                rangeAnnoA, rangeNumeroDa, rangeNumeroA, statoIndiceAip, flSesUpdKoRisolti));
    }

    @Test
    void retrieveVLisAggMetaFalliti() {

        BigDecimal idUser = aBigDecimal();
        BigDecimal idAmbiente = aBigDecimal();
        BigDecimal idEnte = aBigDecimal();
        BigDecimal idStrut = aBigDecimal();
        BigDecimal idTipoUnitaDoc = aBigDecimal();
        BigDecimal idRegistroUnitaDoc = aBigDecimal();
        BigDecimal idTipoDoc = aBigDecimal();
        Date[] dateValidate = aDateArray(3);
        BigDecimal rangeAnnoDa = aBigDecimal();
        BigDecimal rangeAnnoA = aBigDecimal();
        String rangeNumeroDa = aString();
        String rangeNumeroA = aString();
        Set<String> statoSessione = aSetOfString(2);
        BigDecimal idClasseErr = aBigDecimal();
        BigDecimal idErr = aBigDecimal();
        assertNotNull(helper.retrieveVLisAggMetaFalliti(idUser, idAmbiente, idEnte, idStrut,
                idTipoUnitaDoc, idRegistroUnitaDoc, idTipoDoc, dateValidate, rangeAnnoDa,
                rangeAnnoA, rangeNumeroDa, rangeNumeroA, statoSessione, idClasseErr, idErr));
    }

    @Test
    void retrieveVLisAggMetaErrati() {
        Date[] dateValidate = aDateArray(2);
        Set<String> tiStatoSes = aSetOfString(3);
        BigDecimal idClasseErr = aBigDecimal();
        BigDecimal idErr = aBigDecimal();
        assertNotNull(
                helper.retrieveVLisAggMetaErrati(dateValidate, tiStatoSes, idClasseErr, idErr));
    }

    @Test
    void retrieveClasseErrSacerByTipiUso() {
        assertNotNull(helper.retrieveClasseErrSacerByTipiUso(aListOfString(3)));
    }

    @Test
    void retrieveErrSacerByCodClasse() {

        assertNotNull(helper.retrieveErrSacerByCodClasse(aString()));
    }

    @Test
    void retrieveErrSacerByIdClasse() {

        assertNotNull(helper.retrieveErrSacerByIdClasse(aBigDecimal()));
    }

    @Test
    void getVrsErrSesUpdUnitaDocKoList() {
        assertNotNull(helper.getVrsErrSesUpdUnitaDocKoList(aBigDecimal()));
    }

    @Test
    void getVrsErrSesUpdUnitaDocErrList() {
        assertNotNull(helper.getVrsErrSesUpdUnitaDocErrList(aBigDecimal()));
    }

    @Test
    void getAroVLisUpdDocUnitaDocList() {
        assertNotNull(helper.getAroVLisUpdDocUnitaDocList(aBigDecimal()));
    }

    @Test
    void getAroVLisUpdCompUnitaDocList() {
        assertNotNull(helper.getAroVLisUpdCompUnitaDocList(aBigDecimal()));
    }

    @Test
    void getAroVLisUpdKoRisoltiList() {
        assertNotNull(helper.getAroVLisUpdKoRisoltiList(aBigDecimal()));
    }

    @Test
    void getAroWarnUpdUnitaDocList() {
        assertNotNull(helper.getAroWarnUpdUnitaDocList(aBigDecimal()));
    }

    @Test
    void retrieveMonContaSesUpdUdKo() {
        Date dtRifConta = todayTs();
        long idStrut = aLong();
        BigDecimal aaKeyUnitaDoc = aBigDecimal();
        long idRegistroUnitaDoc = aLong();
        long idTipoUnitaDoc = aLong();
        long idTipoDoc = aLong();

        String tiStatoUpdUdKo = MonContaSesUpdUdKo.TiStatoUdpUdKoMonContaSesUpdUdKo.RISOLTO.name();
        assertNotNull(helper.retrieveMonContaSesUpdUdKo(dtRifConta, idStrut, aaKeyUnitaDoc,
                idRegistroUnitaDoc, idTipoUnitaDoc, idTipoDoc, tiStatoUpdUdKo));

        tiStatoUpdUdKo = MonContaSesUpdUdKo.TiStatoUdpUdKoMonContaSesUpdUdKo.VERIFICATO.name();
        assertNotNull(helper.retrieveMonContaSesUpdUdKo(dtRifConta, idStrut, aaKeyUnitaDoc,
                idRegistroUnitaDoc, idTipoUnitaDoc, idTipoDoc, tiStatoUpdUdKo));

        tiStatoUpdUdKo = MonContaSesUpdUdKo.TiStatoUdpUdKoMonContaSesUpdUdKo.NON_RISOLUBILE.name();
        assertNotNull(helper.retrieveMonContaSesUpdUdKo(dtRifConta, idStrut, aaKeyUnitaDoc,
                idRegistroUnitaDoc, idTipoUnitaDoc, idTipoDoc, tiStatoUpdUdKo));

        tiStatoUpdUdKo = MonContaSesUpdUdKo.TiStatoUdpUdKoMonContaSesUpdUdKo.NON_VERIFICATO.name();
        assertNotNull(helper.retrieveMonContaSesUpdUdKo(dtRifConta, idStrut, aaKeyUnitaDoc,
                idRegistroUnitaDoc, idTipoUnitaDoc, idTipoDoc, tiStatoUpdUdKo));
    }

    @Test
    void getLogVVisLastSched() {
        assertNotNull(helper.getLogVVisLastSched(aString()));
    }

    @Test
    void getStruttureVersantiPerAggMeta() {
        assertNotNull(helper.getStruttureVersantiPerAggMeta());
    }

    @Test
    void getAggMetaPerCalcoloContenuto() {
        long idStrut = aLong();
        Date data = todayTs();
        assertNotNull(helper.getAggMetaPerCalcoloContenuto(idStrut, data));
    }

    @Test
    void getAggMetaPerCalcoloContenuto3() {
        long idStrut = aLong();
        Date data = todayTs();
        assertNotNull(helper.getAggMetaPerCalcoloContenuto3(idStrut, data));
    }

    @Test
    void getSesAggMetaPerCalcoloContenuto() {

        long idStrut = aLong();
        Date data = todayTs();
        assertNotNull(helper.getSesAggMetaPerCalcoloContenuto(idStrut, data));
    }

    @Test
    void getSesAggMetaPerCalcoloContenuto3() {

        long idStrut = aLong();
        Date data = todayTs();
        assertNotNull(helper.getSesAggMetaPerCalcoloContenuto3(idStrut, data));
    }

}
