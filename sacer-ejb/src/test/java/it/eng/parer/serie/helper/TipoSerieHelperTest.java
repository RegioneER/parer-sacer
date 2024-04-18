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

package it.eng.parer.serie.helper;

import it.eng.parer.migrazioneObjectStorage.helper.VerificaMigrazioneSubPartizioniHelperTest;
import it.eng.parer.util.Utils;
import it.eng.parer.web.dto.DecFiltroSelUdAttbBean;

import it.eng.parer.web.helper.HelperTest;
import it.eng.parer.web.util.Constants;
import it.eng.parer.web.util.Transform;
import it.eng.parer.ws.utils.CostantiDB;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

import org.junit.runner.RunWith;
import static it.eng.ArquillianUtils.*;
import it.eng.ArquillianUtils;
import org.jboss.arquillian.junit.Arquillian;
import javax.ejb.EJB;

@RunWith(Arquillian.class)
public class TipoSerieHelperTest {
    @EJB
    private TipoSerieHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(TipoSerieHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), TipoSerieHelperTest.class, TipoSerieHelper.class,
                        Constants.class, DecFiltroSelUdAttbBean.class, Transform.class, Utils.class));
    }

    @Test
    public void retrieveDecTipoSerieList_BigDecimal_boolean_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        boolean filterValid = false;
        helper.retrieveDecTipoSerieList(idStrut, filterValid);
        assertTrue(true);
    }

    @Test
    public void retrieveDecTipoSerieList_long_long_queryIsOk() {
        long idStrut = aLong();
        long idModelloTipoSerie = aLong();
        helper.retrieveDecTipoSerieList(idStrut, idModelloTipoSerie);
        assertTrue(true);
    }

    @Test
    public void retrieveDecTipoSerieList_4args_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        boolean flTipoSeriePadre = false;
        String tipoContenSerie = aString();
        boolean filterValids = false;
        helper.retrieveDecTipoSerieList(idStrut, flTipoSeriePadre, tipoContenSerie, filterValids);
        assertTrue(true);
    }

    @Test
    public void countDecTipoSerie_queryIsOk() {
        BigDecimal idModelloTipoSerie = aBigDecimal();
        helper.countDecTipoSerie(idModelloTipoSerie);
        assertTrue(true);
    }

    @Test
    public void getDecTipoSerie_queryIsOk() {
        long idUser = aLong();
        BigDecimal idAmbiente = aBigDecimal();
        BigDecimal idEnte = aBigDecimal();
        BigDecimal idStrut = aBigDecimal();
        String isAttivo = aString();
        String tipiSerieNoGenModello = aString();
        BigDecimal idModelloTipoSerie = aBigDecimal();
        helper.getDecTipoSerie(idUser, idAmbiente, idEnte, idStrut, isAttivo, tipiSerieNoGenModello,
                idModelloTipoSerie);
        assertTrue(true);
    }

    @Test
    public void getDecRegistroUnitaDocPerTipoSerie_queryIsOk() {
        BigDecimal idTipoSerie = aBigDecimal();
        helper.getDecRegistroUnitaDocPerTipoSerie(idTipoSerie);
        assertTrue(true);
    }

    @Test
    public void getDecTipoUnitaDocByIdRegistroIdSerie_queryIsOk() {
        long idRegistroUnitaDoc = aLong();
        BigDecimal idTipoSerie = aBigDecimal();
        helper.getDecTipoUnitaDocByIdRegistroIdSerie(idRegistroUnitaDoc, idTipoSerie);
        assertTrue(true);
    }

    @Test
    public void getDecXsdDatiSpecByCampoInpUd_queryIsOk() {
        BigDecimal id = aBigDecimal();
        helper.getDecXsdDatiSpecByCampoInpUd(id);
        assertTrue(true);
    }

    @Test
    public void getDecCampoInpUdPerTipoSerie_queryIsOk() {
        BigDecimal idTipoSerie = aBigDecimal();
        String ti_campo = aString();
        helper.getDecCampoInpUdPerTipoSerie(idTipoSerie, ti_campo);
        assertTrue(true);
    }

    @Test
    public void getDecTipoSerieByName_queryIsOk() {
        String nmTipoSerie = aString();
        Long idStrut = aLong();
        helper.getDecTipoSerieByName(nmTipoSerie, idStrut);
        assertTrue(true);
    }

    @Test
    public void checkIsTipoSerieUsed_queryIsOk() {
        BigDecimal idTipoSerie = aBigDecimal();
        int numOfUses = aInt();
        helper.checkIsTipoSerieUsed(idTipoSerie, numOfUses);
        assertTrue(true);
    }

    @Test
    public void getSeriePerTipoSerie_queryIsOk() {
        BigDecimal idTipoSerie = aBigDecimal();
        helper.getSeriePerTipoSerie(idTipoSerie);
        assertTrue(true);
    }

    @Test
    public void getDecRegistroUnitaDocListPerSerieByIdStrut_queryIsOk() {
        long idStrut = aLong();
        helper.getDecRegistroUnitaDocListPerSerieByIdStrut(idStrut);
        assertTrue(true);
    }

    @Test
    public void getDecTipoSerieUd_queryIsOk() {
        BigDecimal idTipoSerie = aBigDecimal();
        BigDecimal idRegistroUnitaDoc = aBigDecimal();
        BigDecimal idTipoUnitaDoc = aBigDecimal();
        helper.getDecTipoSerieUd(idTipoSerie, idRegistroUnitaDoc, idTipoUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void getDecFiltroSelUdList_long_queryIsOk() {
        long idTipoSerieUd = aLong();
        helper.getDecFiltroSelUdList(idTipoSerieUd);
        assertTrue(true);
    }

    @Test
    public void getDecFiltroSelUdList_BigDecimal_CostantiDBTipoFiltroSerieUd_queryIsOk() {
        BigDecimal idTipoSerie = aBigDecimal();
        for (CostantiDB.TipoFiltroSerieUd tiFiltro : CostantiDB.TipoFiltroSerieUd.values()) {
            helper.getDecFiltroSelUdList(idTipoSerie, tiFiltro);
            assertTrue(true);
        }
    }

    @Test
    public void getDecTipoDocPrincipalePerTipoUnitaDoc_queryIsOk() {
        BigDecimal idTipoUnitaDoc = aBigDecimal();
        helper.getDecTipoDocPrincipalePerTipoUnitaDoc(idTipoUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void getDecFiltroSelUdAttbList_queryIsOk() {
        BigDecimal idTipoSerieUd = aBigDecimal();
        helper.getDecFiltroSelUdAttbList(idTipoSerieUd);
        assertTrue(true);
    }

    @Test
    public void getDecOutSelUdPerTIpoSerieUd_queryIsOk() {
        BigDecimal idTipoSerieUd = aBigDecimal();
        helper.getDecOutSelUdPerTIpoSerieUd(idTipoSerieUd);
        assertTrue(true);
    }

    @Test
    public void getDecCampoOutSelUdPerDecOutSelUd_queryIsOk() {
        BigDecimal idOutSelUd = aBigDecimal();
        String ti_campo = aString();
        helper.getDecCampoOutSelUdPerDecOutSelUd(idOutSelUd, ti_campo);
        assertTrue(true);
    }

    @Test
    public void getDecCampoOutSelUd_queryIsOk() {
        BigDecimal idOutSelUd = aBigDecimal();
        String nmCampo = aString();
        String tiCampo = aString();
        helper.getDecCampoOutSelUd(idOutSelUd, nmCampo, tiCampo);
        assertTrue(true);
    }

    @Test
    public void getDecAttribDatiSpecByName_queryIsOk() {
        String nmAttribDatiSpec = aString();
        BigDecimal idTipoDoc = aBigDecimal();
        BigDecimal idTipoUnitaDoc = aBigDecimal();
        BigDecimal idTipoCompDoc = aBigDecimal();
        helper.getDecAttribDatiSpecByName(nmAttribDatiSpec, idTipoDoc, idTipoUnitaDoc, idTipoCompDoc);
        assertTrue(true);
    }

    @Test
    public void getDecAttribDatiSpecTableBean_BigDecimal_ConstantsTipoEntitaSacer_queryIsOk() {
        BigDecimal id = aBigDecimal();
        Constants.TipoEntitaSacer tipoEntitaSacer = Constants.TipoEntitaSacer.UNI_DOC;
        helper.getDecAttribDatiSpecTableBean(id, tipoEntitaSacer);
        assertTrue(true);
        tipoEntitaSacer = Constants.TipoEntitaSacer.DOC;
        helper.getDecAttribDatiSpecTableBean(id, tipoEntitaSacer);
        assertTrue(true);
        tipoEntitaSacer = Constants.TipoEntitaSacer.COMP;
        helper.getDecAttribDatiSpecTableBean(id, tipoEntitaSacer);
        assertTrue(true);
    }

    @Test
    public void getDecAttribDatiSpec_queryIsOk() {
        BigDecimal id = BigDecimal.valueOf(586);
        Constants.TipoEntitaSacer tipoEntitaSacer = Constants.TipoEntitaSacer.UNI_DOC;
        helper.getDecAttribDatiSpec(id, tipoEntitaSacer);
        assertTrue(true);
        tipoEntitaSacer = Constants.TipoEntitaSacer.DOC;
        helper.getDecAttribDatiSpec(id, tipoEntitaSacer);
        assertTrue(true);
        tipoEntitaSacer = Constants.TipoEntitaSacer.COMP;
        helper.getDecAttribDatiSpec(id, tipoEntitaSacer);
        assertTrue(true);
    }

    @Test
    public void getVersioniXsd_queryIsOk() {
        BigDecimal idAttribDatiSpec = aBigDecimal();
        BigDecimal id = aBigDecimal();
        for (Constants.TipoEntitaSacer tipoEntitaSacer : Constants.TipoEntitaSacer.values()) {
            helper.getVersioniXsd(idAttribDatiSpec, id, tipoEntitaSacer);
            assertTrue(true);
        }
    }

    @Test
    public void getDecAttribDatiSpecById_queryIsOk() {
        BigDecimal idAttribDatiSpec = aBigDecimal();
        for (Constants.TipoEntitaSacer tipoEntitaSacer : Constants.TipoEntitaSacer.values()) {
            helper.getDecAttribDatiSpecById(idAttribDatiSpec);
            assertTrue(true);
        }
    }

    @Test
    public void isDecOutSelUdPresent_queryIsOk() {
        BigDecimal idOutSelUd = aBigDecimal();
        String tiOut = aString();
        helper.isDecOutSelUdPresent(idOutSelUd, tiOut);
        assertTrue(true);
    }

    @Test
    public void getDecAttribDatiSpecs_queryIsOk() {
        List<Long> id = Arrays.asList(586L, 1086L);
        Constants.TipoEntitaSacer tipoEntitaSacer = Constants.TipoEntitaSacer.UNI_DOC;
        helper.getDecAttribDatiSpecs(tipoEntitaSacer, id);
        assertTrue(true);
        tipoEntitaSacer = Constants.TipoEntitaSacer.DOC;
        helper.getDecAttribDatiSpecs(tipoEntitaSacer, id);
        assertTrue(true);
        tipoEntitaSacer = Constants.TipoEntitaSacer.COMP;
        helper.getDecAttribDatiSpecs(tipoEntitaSacer, id);
        assertTrue(true);
    }

    @Test
    public void getMaxPgPerTipoFiltro_queryIsOk() {
        BigDecimal idTipoSerieUd = aBigDecimal();
        for (CostantiDB.TipoFiltroSerieUd tipoFiltro : CostantiDB.TipoFiltroSerieUd.values()) {
            helper.getMaxPgPerTipoFiltro(tipoFiltro, idTipoSerieUd);
            assertTrue(true);
        }
    }

    @Test
    public void getDecNoteTipoSerie_queryIsOk() {
        BigDecimal idTipoSerie = aBigDecimal();
        helper.getDecNoteTipoSerie(idTipoSerie);
        assertTrue(true);
    }

    @Test
    public void getDecNotaTipoSerieById_queryIsOk() {
        BigDecimal idDecNotaTipoSerie = aBigDecimal();
        helper.getDecNotaTipoSerieById(idDecNotaTipoSerie);
        assertTrue(true);
    }

    @Test
    public void getDecTipoNotaSerieList_queryIsOk() {
        helper.getDecTipoNotaSerieList();
        assertTrue(true);
    }

    @Test
    public void getDecTipoNotaSerieNoFlMoltList_queryIsOk() {
        BigDecimal idTipoSerie = aBigDecimal();
        helper.getDecTipoNotaSerieNoFlMoltList(idTipoSerie);
        assertTrue(true);
    }

    @Test
    public void getDecTipoNotaSerieNotInVerSerie_queryIsOk() {
        BigDecimal idVerSerie = aBigDecimal();
        helper.getDecTipoNotaSerieNotInVerSerie(idVerSerie);
        assertTrue(true);
    }

    @Test
    public void getDecTipoNotaSerieNotInModelloSerie_queryIsOk() {
        BigDecimal idModelloTipoSerie = aBigDecimal();
        helper.getDecTipoNotaSerieNotInModelloSerie(idModelloTipoSerie);
        assertTrue(true);
    }

    @Test
    public void getAllDecTipoNotaSerieList_queryIsOk() {
        helper.getAllDecTipoNotaSerieList();
        assertTrue(true);
    }

    @Test
    public void getMaxPgPerNotatipoSerie_queryIsOk() {
        BigDecimal idTipoSerie = aBigDecimal();
        BigDecimal idTipoNotaSerie = aBigDecimal();
        helper.getMaxPgPerNotatipoSerie(idTipoSerie, idTipoNotaSerie);
        assertTrue(true);
    }

    @Test
    public void getVersioniXsdPerTipoEntita_queryIsOk() {
        BigDecimal id = aBigDecimal();
        for (Constants.TipoEntitaSacer tipoEntitaSacer : Constants.TipoEntitaSacer.values()) {
            helper.getVersioniXsdPerTipoEntita(id, tipoEntitaSacer);
            assertTrue(true);
        }
    }

    @Test
    public void getFiltroSelUdByIdTipoDoc_queryIsOk() {
        BigDecimal idTipoDoc = aBigDecimal();
        BigDecimal idTipoSerieUd = aBigDecimal();
        helper.getFiltroSelUdByIdTipoDoc(idTipoDoc, idTipoSerieUd);
        assertTrue(true);
    }

    @Test
    public void getDecTipoDocFromFiltroSelUdByIdTipoDoc_queryIsOk() {
        BigDecimal idTipoDoc = aBigDecimal();
        BigDecimal idTipoSerieUd = aBigDecimal();
        helper.getDecTipoDocFromFiltroSelUdByIdTipoDoc(idTipoDoc, idTipoSerieUd);
        assertTrue(true);
    }

    @Test
    public void isTipoSerieModificabile_queryIsOk() {
        BigDecimal idTipoSerie = aBigDecimal();
        try {
            helper.isTipoSerieModificabile(idTipoSerie);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    public void existDecTipoSerieUdForRegistro_queryIsOk() {
        BigDecimal idRegistroUnitaDoc = aBigDecimal();
        helper.existDecTipoSerieUdForRegistro(idRegistroUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void retrieveDecTipoSerieForRegistro_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        BigDecimal idRegistroUnitaDoc = aBigDecimal();
        helper.retrieveDecTipoSerieForRegistro(idStrut, idRegistroUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void multipleDecRegistroUnitaDocInTipiSerie_queryIsOk() {
        BigDecimal idRegistroUnitaDoc = aBigDecimal();
        helper.multipleDecRegistroUnitaDocInTipiSerie(idRegistroUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void existsRelationsWithTipiSerie_queryIsOk() {
        long idTipoDato = aLong();
        Constants.TipoDato tipoDato = Constants.TipoDato.REGISTRO;
        helper.existsRelationsWithTipiSerie(idTipoDato, tipoDato);
        assertTrue(true);
        tipoDato = Constants.TipoDato.TIPO_DOC;
        helper.existsRelationsWithTipiSerie(idTipoDato, tipoDato);
        assertTrue(true);
        tipoDato = Constants.TipoDato.TIPO_UNITA_DOC;
        helper.existsRelationsWithTipiSerie(idTipoDato, tipoDato);
        assertTrue(true);
    }

    @Test
    public void getDecModelloFiltroTiDoc_queryIsOk() {
        BigDecimal idModelloTipoSerie = aBigDecimal();
        String nmTipoDoc = aString();
        helper.getDecModelloFiltroTiDoc(idModelloTipoSerie, nmTipoDoc);
        assertTrue(true);
    }
}
