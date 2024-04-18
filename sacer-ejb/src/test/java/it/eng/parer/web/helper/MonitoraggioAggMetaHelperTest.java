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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.web.helper;

import it.eng.parer.entity.constraint.MonContaSesUpdUdKo;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author manuel.bertuzzi@eng.it
 */

import org.junit.runner.RunWith;
import static it.eng.ArquillianUtils.*;
import it.eng.ArquillianUtils;
import org.jboss.arquillian.junit.Arquillian;
import javax.ejb.EJB;

@RunWith(Arquillian.class)
public class MonitoraggioAggMetaHelperTest {
    @EJB
    private MonitoraggioAggMetaHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(MonitoraggioAggMetaHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), MonitoraggioAggMetaHelperTest.class,
                        MonitoraggioAggMetaHelper.class));
    }

    @Test
    public void getTotali() {

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
        Assert.assertNotNull(helper.getTotali(tipoEntita, idUser, idAmbiente, idEnte, idStrut, aaKeyUnitaDoc,
                aaKeyUnitaDocDa, aaKeyUnitaDocA, idTipoUnitaDoc, idRegistroUnitaDoc, idTipoDocPrinc));
    }

    @Test
    public void getTotaliDataCorrente() {

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
        Assert.assertNotNull(helper.getTotaliDataCorrente(tipoEntita, idUser, idAmbiente, idEnte, idStrut,
                aaKeyUnitaDoc, aaKeyUnitaDocDa, aaKeyUnitaDocA, idTipoUnitaDoc, idRegistroUnitaDoc, idTipoDocPrinc));
    }

    @Test
    public void getTotaliFalliti() {

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
        Map<String, Object> result = helper.getTotaliFalliti(tipoEntita, idUser, idAmbiente, idEnte, idStrut,
                aaKeyUnitaDoc, aaKeyUnitaDocDa, aaKeyUnitaDocA, idTipoUnitaDoc, idRegistroUnitaDoc, idTipoDocPrinc);
        Assert.assertNotNull(result);
    }

    @Test
    public void retrieveVLisAggMeta() {

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
        Assert.assertNotNull(helper.retrieveVLisAggMeta(idUser, idAmbiente, idEnte, idStrut, idTipoUnitaDoc,
                idRegistroUnitaDoc, idTipoDoc, dateValidate, rangeAnnoDa, rangeAnnoA, rangeNumeroDa, rangeNumeroA,
                statoIndiceAip, flSesUpdKoRisolti));
    }

    @Test
    public void retrieveVLisAggMetaFalliti() {

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
        Assert.assertNotNull(helper.retrieveVLisAggMetaFalliti(idUser, idAmbiente, idEnte, idStrut, idTipoUnitaDoc,
                idRegistroUnitaDoc, idTipoDoc, dateValidate, rangeAnnoDa, rangeAnnoA, rangeNumeroDa, rangeNumeroA,
                statoSessione, idClasseErr, idErr));
    }

    @Test
    public void retrieveVLisAggMetaErrati() {
        Date[] dateValidate = aDateArray(2);
        Set<String> tiStatoSes = aSetOfString(3);
        BigDecimal idClasseErr = aBigDecimal();
        BigDecimal idErr = aBigDecimal();
        Assert.assertNotNull(helper.retrieveVLisAggMetaErrati(dateValidate, tiStatoSes, idClasseErr, idErr));
    }

    @Test
    public void retrieveClasseErrSacerByTipiUso() {
        Assert.assertNotNull(helper.retrieveClasseErrSacerByTipiUso(aListOfString(3)));
    }

    @Test
    public void retrieveErrSacerByCodClasse() {

        Assert.assertNotNull(helper.retrieveErrSacerByCodClasse(aString()));
    }

    @Test
    public void retrieveErrSacerByIdClasse() {

        Assert.assertNotNull(helper.retrieveErrSacerByIdClasse(aBigDecimal()));
    }

    @Test
    public void getVrsErrSesUpdUnitaDocKoList() {
        Assert.assertNotNull(helper.getVrsErrSesUpdUnitaDocKoList(aBigDecimal()));
    }

    @Test
    public void getVrsErrSesUpdUnitaDocErrList() {
        Assert.assertNotNull(helper.getVrsErrSesUpdUnitaDocErrList(aBigDecimal()));
    }

    @Test
    public void getAroVLisUpdDocUnitaDocList() {
        Assert.assertNotNull(helper.getAroVLisUpdDocUnitaDocList(aBigDecimal()));
    }

    @Test
    public void getAroVLisUpdCompUnitaDocList() {
        Assert.assertNotNull(helper.getAroVLisUpdCompUnitaDocList(aBigDecimal()));
    }

    @Test
    public void getAroVLisUpdKoRisoltiList() {
        Assert.assertNotNull(helper.getAroVLisUpdKoRisoltiList(aBigDecimal()));
    }

    @Test
    public void getAroWarnUpdUnitaDocList() {
        Assert.assertNotNull(helper.getAroWarnUpdUnitaDocList(aBigDecimal()));
    }

    @Test
    public void retrieveMonContaSesUpdUdKo() {
        Date dtRifConta = todayTs();
        long idStrut = aLong();
        BigDecimal aaKeyUnitaDoc = aBigDecimal();
        long idRegistroUnitaDoc = aLong();
        long idTipoUnitaDoc = aLong();
        long idTipoDoc = aLong();
        String tiStatoUpdUdKo = MonContaSesUpdUdKo.TiStatoUdpUdKoMonContaSesUpdUdKo.RISOLTO.name();
        helper.retrieveMonContaSesUpdUdKo(dtRifConta, idStrut, aaKeyUnitaDoc, idRegistroUnitaDoc, idTipoUnitaDoc,
                idTipoDoc, tiStatoUpdUdKo);
        tiStatoUpdUdKo = MonContaSesUpdUdKo.TiStatoUdpUdKoMonContaSesUpdUdKo.VERIFICATO.name();
        helper.retrieveMonContaSesUpdUdKo(dtRifConta, idStrut, aaKeyUnitaDoc, idRegistroUnitaDoc, idTipoUnitaDoc,
                idTipoDoc, tiStatoUpdUdKo);
        tiStatoUpdUdKo = MonContaSesUpdUdKo.TiStatoUdpUdKoMonContaSesUpdUdKo.NON_RISOLUBILE.name();
        helper.retrieveMonContaSesUpdUdKo(dtRifConta, idStrut, aaKeyUnitaDoc, idRegistroUnitaDoc, idTipoUnitaDoc,
                idTipoDoc, tiStatoUpdUdKo);
        tiStatoUpdUdKo = MonContaSesUpdUdKo.TiStatoUdpUdKoMonContaSesUpdUdKo.NON_VERIFICATO.name();
        helper.retrieveMonContaSesUpdUdKo(dtRifConta, idStrut, aaKeyUnitaDoc, idRegistroUnitaDoc, idTipoUnitaDoc,
                idTipoDoc, tiStatoUpdUdKo);
    }

    @Test
    public void getLogVVisLastSched() {
        helper.getLogVVisLastSched(aString());
    }

    @Test
    public void getStruttureVersantiPerAggMeta() {
        Assert.assertNotNull(helper.getStruttureVersantiPerAggMeta());
    }

    @Test
    public void getAggMetaPerCalcoloContenuto() {
        long idStrut = aLong();
        Date data = todayTs();
        Assert.assertNotNull(helper.getAggMetaPerCalcoloContenuto(idStrut, data));
    }

    @Test
    public void getAggMetaPerCalcoloContenuto3() {
        long idStrut = aLong();
        Date data = todayTs();
        Assert.assertNotNull(helper.getAggMetaPerCalcoloContenuto3(idStrut, data));
    }

    @Test
    public void getSesAggMetaPerCalcoloContenuto() {

        long idStrut = aLong();
        Date data = todayTs();
        Assert.assertNotNull(helper.getSesAggMetaPerCalcoloContenuto(idStrut, data));
    }

    @Test
    public void getSesAggMetaPerCalcoloContenuto3() {

        long idStrut = aLong();
        Date data = todayTs();
        Assert.assertNotNull(helper.getSesAggMetaPerCalcoloContenuto3(idStrut, data));
    }

}
