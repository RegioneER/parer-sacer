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
package it.eng.parer.amministrazioneStrutture.gestioneDatiSpecifici.helper;

import it.eng.ArquillianUtils;
import it.eng.parer.slite.gen.tablebean.DecXsdDatiSpecRowBean;

import it.eng.parer.web.helper.HelperTest;
import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.utils.CostantiDB;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import javax.ejb.EJB;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.Arrays;

import javax.ejb.EJB;

import static org.junit.Assert.assertTrue;
import static it.eng.ArquillianUtils.*;

/**
 * @author manuel.bertuzzi@eng.it
 */
@RunWith(Arquillian.class)
public class DatiSpecificiHelperTest {
    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(DatiSpecificiHelperTest.class.getSimpleName(),
                HelperTest.createSacerLogJar(), HelperTest.createPaginatorJavaArchive(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), DatiSpecificiHelperTest.class,
                        DatiSpecificiHelper.class, CostantiDB.class, Constants.class));
    }

    @EJB
    private DatiSpecificiHelper helper;

    @Test
    public void getDecAttribDatiSpecById_8args_queryIsOk() {
        Long idStrut = aLong();
        String nmAttribDatiSpec = aString();
        String tiUsoAttrib = aString();
        Long idTipoUnitaDoc = aLong();
        Long idTipoDoc = aLong();
        Long idTipoCompDoc = aLong();
        String nmSistemaMigraz = aString();
        for (CostantiDB.TipiEntitaSacer tiEntitaSacer : CostantiDB.TipiEntitaSacer.values()) {
            helper.getDecAttribDatiSpecById(idStrut, nmAttribDatiSpec, tiEntitaSacer.name(), tiUsoAttrib,
                    idTipoUnitaDoc, idTipoDoc, idTipoCompDoc, nmSistemaMigraz);
            assertTrue(true);
        }
    }

    @Test
    public void getDecAttribDatiSpecById_7args_1_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        String nmAttribDatiSpec = aString();
        String tiUsoAttrib = aString();
        Long idTipoUnitaDoc = aLong();
        Long idTipoDoc = aLong();
        Long idTipoCompDoc = aLong();
        for (CostantiDB.TipiEntitaSacer tiEntitaSacer : CostantiDB.TipiEntitaSacer.values()) {
            helper.getDecAttribDatiSpecById(idStrut, nmAttribDatiSpec, tiEntitaSacer.name(), tiUsoAttrib,
                    idTipoUnitaDoc, idTipoDoc, idTipoCompDoc);
            assertTrue(true);
        }
    }

    @Test
    public void getDecAttribDatiSpecById_7args_2_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        String nmAttribDatiSpec = aString();
        String tiUsoAttrib = aString();
        BigDecimal idTipoUnitaDoc = aBigDecimal();
        BigDecimal idTipoDoc = aBigDecimal();
        BigDecimal idTipoCompDoc = aBigDecimal();
        for (CostantiDB.TipiEntitaSacer tiEntitaSacer : CostantiDB.TipiEntitaSacer.values()) {
            helper.getDecAttribDatiSpecById(idStrut, nmAttribDatiSpec, tiEntitaSacer.name(), tiUsoAttrib,
                    idTipoUnitaDoc, idTipoDoc, idTipoCompDoc);
            assertTrue(true);
        }
    }

    @Test
    public void getDecAttribDatiSpecById_7args_3_queryIsOk() {
        Long idStrut = aLong();
        String nmAttribDatiSpec = aString();
        String tiUsoAttrib = aString();
        BigDecimal idTipoUnitaDoc = aBigDecimal();
        BigDecimal idTipoDoc = aBigDecimal();
        BigDecimal idTipoCompDoc = aBigDecimal();
        for (CostantiDB.TipiEntitaSacer tiEntitaSacer : CostantiDB.TipiEntitaSacer.values()) {
            helper.getDecAttribDatiSpecById(idStrut, nmAttribDatiSpec, tiEntitaSacer.name(), tiUsoAttrib,
                    idTipoUnitaDoc, idTipoDoc, idTipoCompDoc);
            assertTrue(true);
        }
    }

    @Test
    public void getDecAttribDatiSpecById_queryIsOk() {
        Long idStrut = aLong();
        String nmAttribDatiSpec = aString();
        String tiUsoAttrib = aString();
        String nmSistemaMigraz = aString();
        String tiEntitaSacer = CostantiDB.TipiEntitaSacer.UNI_DOC.name();
        helper.getDecAttribDatiSpecById(idStrut, nmAttribDatiSpec, tiEntitaSacer, tiUsoAttrib, nmSistemaMigraz);
        assertTrue(true);
        tiEntitaSacer = CostantiDB.TipiEntitaSacer.DOC.name();
        helper.getDecAttribDatiSpecById(idStrut, nmAttribDatiSpec, tiEntitaSacer, tiUsoAttrib, nmSistemaMigraz);
        assertTrue(true);
        tiEntitaSacer = CostantiDB.TipiEntitaSacer.COMP.name();
        helper.getDecAttribDatiSpecById(idStrut, nmAttribDatiSpec, tiEntitaSacer, tiUsoAttrib, nmSistemaMigraz);
        assertTrue(true);
        tiEntitaSacer = CostantiDB.TipiEntitaSacer.UNI_DOC.name();
        helper.getDecAttribDatiSpecById(idStrut, nmAttribDatiSpec, tiEntitaSacer, tiUsoAttrib, nmSistemaMigraz);
        assertTrue(true);
    }

    @Test
    public void getDecAttribDatiSpecByName_8args_1_queryIsOk() {
        Long idStrut = aLong();
        String nmAttribDatiSpec = aString();
        String tiUsoAttrib = aString();
        String nmTipoUnitaDoc = aString();
        String nmTipoDoc = aString();
        String nmTipoCompDoc = aString();
        String nmSistemaMigraz = aString();

        for (CostantiDB.TipiEntitaSacer tiEntitaSacer : CostantiDB.TipiEntitaSacer.values()) {

            helper.getDecAttribDatiSpecByName(idStrut, nmAttribDatiSpec, tiEntitaSacer.name(), tiUsoAttrib,
                    nmTipoUnitaDoc, nmTipoDoc, nmTipoCompDoc, nmSistemaMigraz);
            assertTrue(true);
        }
    }

    @Test
    public void getDecAttribDatiSpecByName_8args_2_queryIsOk() {
        BigDecimal idStrutCorrente = aBigDecimal();
        String tiUsoAttrib = aString();

        String nmTipoStrutDoc = aString();
        String nmAttribDatiSpec = aString();

        for (CostantiDB.TipiEntitaSacer tiEntitaSacer : CostantiDB.TipiEntitaSacer.values()) {
            String nmTipoUnitaDoc = aString();
            String nmTipoDoc = aString();
            String nmTipoCompDoc = aString();
            helper.getDecAttribDatiSpecByName(idStrutCorrente, tiUsoAttrib, tiEntitaSacer.name(), nmTipoUnitaDoc, null,
                    null, nmTipoStrutDoc, nmAttribDatiSpec);
            assertTrue(true);
            helper.getDecAttribDatiSpecByName(idStrutCorrente, tiUsoAttrib, tiEntitaSacer.name(), null, nmTipoDoc, null,
                    nmTipoStrutDoc, nmAttribDatiSpec);
            assertTrue(true);
            helper.getDecAttribDatiSpecByName(idStrutCorrente, tiUsoAttrib, tiEntitaSacer.name(), null, null,
                    nmTipoCompDoc, nmTipoStrutDoc, nmAttribDatiSpec);
            assertTrue(true);
        }
    }

    @Test
    public void getDecAttribDatiSpecByName_7args_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        String nmAttribDatiSpec = aString();
        String tiUsoAttrib = aString();
        String nmTipoUnitaDoc = aString();
        String nmTipoDoc = aString();
        String nmTipoCompDoc = aString();

        for (CostantiDB.TipiEntitaSacer tiEntitaSacer : CostantiDB.TipiEntitaSacer.values()) {
            helper.getDecAttribDatiSpecByName(idStrut, nmAttribDatiSpec, tiEntitaSacer.name(), tiUsoAttrib,
                    nmTipoUnitaDoc, nmTipoDoc, nmTipoCompDoc);
            assertTrue(true);
        }
    }

    @Test
    public void getDecAttribDatiSpecByName_5args_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        String nmAttribDatiSpec = aString();
        String tiUsoAttrib = aString();
        String nmSistemaMigraz = aString();

        for (CostantiDB.TipiEntitaSacer tiEntitaSacer : CostantiDB.TipiEntitaSacer.values()) {

            helper.getDecAttribDatiSpecByName(idStrut, nmAttribDatiSpec, tiEntitaSacer.name(), tiUsoAttrib,
                    nmSistemaMigraz);
            assertTrue(true);
        }
    }

    @Test
    public void getPreviousVersionAttributesList_queryIsOk() {
        BigDecimal idXsdDatiSpec = aBigDecimal();
        BigDecimal idTipoUnitaDoc = aBigDecimal();
        BigDecimal idTipoDoc = aBigDecimal();
        BigDecimal idTipoCompDoc = aBigDecimal();
        helper.getPreviousVersionAttributesList(idXsdDatiSpec, idTipoUnitaDoc, idTipoDoc, idTipoCompDoc);
        assertTrue(true);
    }

    @Test
    public void getWhichAttributesList_queryIsOk() {
        BigDecimal idXsdDatiSpec = aBigDecimal();
        BigDecimal idTipoUnitaDoc = aBigDecimal();
        BigDecimal idTipoDoc = aBigDecimal();
        BigDecimal idTipoCompDoc = aBigDecimal();
        boolean previous = false;
        helper.getWhichAttributesList(idXsdDatiSpec, idTipoUnitaDoc, idTipoDoc, idTipoCompDoc, previous);
        assertTrue(true);
    }

    @Test
    public void getNmAttribDatiSpecList_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        BigDecimal idTipoUnitaDoc = aBigDecimal();
        BigDecimal idTipoDoc = aBigDecimal();
        BigDecimal idTipoCompDoc = aBigDecimal();
        String tiEntitaSacer = aString();
        String nmSistemaMigraz = aString();
        helper.getNmAttribDatiSpecList(idStrut, idTipoUnitaDoc, idTipoDoc, idTipoCompDoc, tiEntitaSacer,
                nmSistemaMigraz);
        assertTrue(true);
    }

    @Test
    public void retrieveDecAttribDatiSpecList_queryIsOk() {
        BigDecimal idXsdDatiSpec = aBigDecimal();
        helper.retrieveDecAttribDatiSpecList(idXsdDatiSpec);
        assertTrue(true);
    }

    @Test
    public void getDecAttribDatiSpecUniDocAndDoc_queryIsOk() {
        Long idStrut = aLong();
        String nmAttribDatiSpec = aString();
        String tiUsoAttrib = aString();
        String nmTipoUnitaDoc = aString();
        String nmTipoDoc = aString();
        String nmTipoCompDoc = aString();
        for (CostantiDB.TipiEntitaSacer tiEntitaSacer : CostantiDB.TipiEntitaSacer.values()) {
            try {
                helper.getDecAttribDatiSpecUniDocAndDoc(idStrut, nmAttribDatiSpec, tiEntitaSacer.name(), tiUsoAttrib,
                        nmTipoUnitaDoc, nmTipoDoc, nmTipoCompDoc);
                assertTrue(true);
            } catch (Exception e) {
                assertNoResultException(e);
            }
        }
    }

    @Test
    public void retrieveDecXsdDatiSpecList_4args_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        String tiUsoXsd = aString();
        String tiEntitaSacer = aString();
        String nmSistemaMigraz = aString();
        helper.retrieveDecXsdDatiSpecList(idStrut, tiUsoXsd, tiEntitaSacer, nmSistemaMigraz);
        assertTrue(true);
    }

    @Test
    public void retrieveDecXsdDatiSpecList_6args_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        String tiUsoXsd = aString();
        String tiEntitaSacer = aString();
        BigDecimal idTipoUnitaDoc = aBigDecimal();
        BigDecimal idTipoDoc = aBigDecimal();
        BigDecimal idTipoCompDoc = aBigDecimal();
        helper.retrieveDecXsdDatiSpecList(idStrut, tiUsoXsd, tiEntitaSacer, idTipoUnitaDoc, idTipoDoc, idTipoCompDoc);
        assertTrue(true);
    }

    @Test
    public void retrieveDecXsdDatiSpecList_7args_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        String tiUsoXsd = aString();
        String tiEntitaSacer = aString();
        BigDecimal idTipoUnitaDoc = aBigDecimal();
        BigDecimal idTipoDoc = aBigDecimal();
        BigDecimal idTipoCompDoc = aBigDecimal();
        String nmSistemaMigraz = aString();
        helper.retrieveDecXsdDatiSpecList(idStrut, tiUsoXsd, tiEntitaSacer, idTipoUnitaDoc, idTipoDoc, idTipoCompDoc,
                nmSistemaMigraz);
        assertTrue(true);
    }

    @Test
    public void getDecXsdDatiSpecByVersion_queryIsOk() {
        DecXsdDatiSpecRowBean xsdDatiSpec = aDecXsdDatiSpecRowBean();
        helper.getDecXsdDatiSpecByVersion(xsdDatiSpec);
        assertTrue(true);
    }

    private DecXsdDatiSpecRowBean aDecXsdDatiSpecRowBean() {
        DecXsdDatiSpecRowBean xsdDatiSpec = new DecXsdDatiSpecRowBean();
        xsdDatiSpec.setCdVersioneXsd(aString());
        xsdDatiSpec.setNmSistemaMigraz(aString());
        xsdDatiSpec.setIdTipoDoc(aBigDecimal());
        xsdDatiSpec.setIdTipoUnitaDoc(aBigDecimal());
        xsdDatiSpec.setIdTipoCompDoc(aBigDecimal());
        xsdDatiSpec.setIdStrut(aBigDecimal());
        return xsdDatiSpec;
    }

    @Test
    public void checkRelationsAreEmptyForDecXsdDatiSpec_queryIsOk() {
        long idXsdDatiSpec = aLong();
        helper.checkRelationsAreEmptyForDecXsdDatiSpec(idXsdDatiSpec);
        assertTrue(true);
    }

    @Test
    public void campiRegoleInUso_queryIsOk() {
        BigDecimal idXsdDatiSpec = aBigDecimal();
        helper.campiRegoleInUso(idXsdDatiSpec);
        assertTrue(true);
    }

    @Test
    public void getUseOfXsdDatiSpec_queryIsOk() {
        Long idXsdDatiSpec = aLong();
        helper.getUseOfXsdDatiSpec(idXsdDatiSpec);
        assertTrue(true);
    }

    @Test
    public void getDecXsdAttribDatiSpecByAttrib_queryIsOk() {
        BigDecimal idAttribDatiSpec = aBigDecimal();
        BigDecimal idXsdDatiSpec = aBigDecimal();
        helper.getDecXsdAttribDatiSpecByAttrib(idAttribDatiSpec, idXsdDatiSpec);
        assertTrue(true);
    }

    @Test
    public void getMigrazDecXsdAttribDatiSpecByNameAndXsdId_queryIsOk() {
        String nmAttribDatiSpec = aString();
        BigDecimal idStrut = aBigDecimal();
        String nmSistemaMigraz = aString();
        String tiSacerType = aString();
        BigDecimal idXsdDatiSpec = aBigDecimal();
        try {
            helper.getMigrazDecXsdAttribDatiSpecByNameAndXsdId(nmAttribDatiSpec, idStrut, nmSistemaMigraz, tiSacerType,
                    idXsdDatiSpec);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    public void getDecXsdAttribDatiSpecByNameAndXsdId_queryIsOk() {
        String nmAttribDatiSpec = aString();
        BigDecimal idTipoDoc = aBigDecimal();
        BigDecimal idTipoUnitaDoc = aBigDecimal();
        BigDecimal idTipoCompDoc = aBigDecimal();
        BigDecimal idXsdDatiSpec = aBigDecimal();
        try {
            helper.getDecXsdAttribDatiSpecByNameAndXsdId(nmAttribDatiSpec, idTipoDoc, idTipoUnitaDoc, idTipoCompDoc,
                    idXsdDatiSpec);
            assertTrue(true);
        } catch (Exception e) {
            assertNoResultException(e);
        }
    }

    @Test
    public void getLastDecXsdDatiSpecForTipoUnitaDoc_queryIsOk() {
        Long idStrut = aLong();
        Long idTipoUnitaDoc = aLong();
        helper.getLastDecXsdDatiSpecForTipoUnitaDoc(idStrut, idTipoUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void getLastDecXsdDatiSpecForTipoCompDoc_queryIsOk() {
        Long idStrut = aLong();
        Long idTipoCompDoc = aLong();
        helper.getLastDecXsdDatiSpecForTipoCompDoc(idStrut, idTipoCompDoc);
        assertTrue(true);
    }

    @Test
    public void getLastDecXsdDatiSpecForTipoDoc_queryIsOk() {
        Long idStrut = aLong();
        Long idTipoDoc = aLong();
        helper.getLastDecXsdDatiSpecForTipoDoc(idStrut, idTipoDoc);
        assertTrue(true);
    }

    @Test
    public void getLastDecXsdDatiSpec_queryIsOk() {
        helper.getLastDecXsdDatiSpec(aDecXsdDatiSpecRowBean());
        assertTrue(true);
    }

    @Test
    public void getDecAttribDatiSpecList_queryIsOk() {
        BigDecimal idStrut = aBigDecimal();
        BigDecimal idEntity = aBigDecimal();
        helper.getDecAttribDatiSpecList(idStrut, CostantiDB.TipiEntitaSacer.UNI_DOC.name(), idEntity);
        assertTrue(true);
        helper.getDecAttribDatiSpecList(idStrut, CostantiDB.TipiEntitaSacer.DOC.name(), idEntity);
        assertTrue(true);
    }

    @Test
    public void existsCampoSuXsdDatiSpec_queryIsOk() {
        BigDecimal idXsdDatiSpec = aBigDecimal();
        String[] tipiCampo = aStringArray(2);
        helper.existsCampoSuXsdDatiSpec(idXsdDatiSpec, tipiCampo);
        assertTrue(true);
    }

    @Test
    public void getDecXsdDatiSpec_queryIsOk() {
        BigDecimal idStrutCorrente = aBigDecimal();
        String tiUsoXsd = aString();
        String tiEntitaSacer = aString();
        String nmTipoStrutDoc = aString();
        String nmSistemaMigraz = aString();
        String cdVersioneXsd = aString();
        String nmTipoUnitaDoc = aString();
        String nmTipoDoc = null;
        String nmTipoCompDoc = null;
        helper.getDecXsdDatiSpec(idStrutCorrente, tiUsoXsd, tiEntitaSacer, nmTipoUnitaDoc, nmTipoDoc, nmTipoCompDoc,
                nmTipoStrutDoc, nmSistemaMigraz, cdVersioneXsd);
        assertTrue(true);
        nmTipoUnitaDoc = null;
        nmTipoDoc = aString();
        nmTipoCompDoc = null;
        helper.getDecXsdDatiSpec(idStrutCorrente, tiUsoXsd, tiEntitaSacer, nmTipoUnitaDoc, nmTipoDoc, nmTipoCompDoc,
                nmTipoStrutDoc, nmSistemaMigraz, cdVersioneXsd);
        assertTrue(true);
        nmTipoUnitaDoc = null;
        nmTipoDoc = null;
        nmTipoCompDoc = aString();
        helper.getDecXsdDatiSpec(idStrutCorrente, tiUsoXsd, tiEntitaSacer, nmTipoUnitaDoc, nmTipoDoc, nmTipoCompDoc,
                nmTipoStrutDoc, nmSistemaMigraz, cdVersioneXsd);
        assertTrue(true);
    }

    @Test
    public void getDecTipoStrutUdXsdByName_queryIsOk() {
        BigDecimal idStrutCorrente = aBigDecimal();
        String nmTipoStrutUnitaDoc = aString();
        String tiUsoXsd = aString();
        String tiEntitaSacer = aString();
        String nmTipoUnitaDoc = aString();
        String nmSistemaMigraz = aString();
        String cdVersioneXsd = aString();
        helper.getDecTipoStrutUdXsdByName(idStrutCorrente, nmTipoStrutUnitaDoc, tiUsoXsd, tiEntitaSacer, nmTipoUnitaDoc,
                nmSistemaMigraz, cdVersioneXsd);
        assertTrue(true);
    }

    @Test
    public void countXsdDatiSpecInUseInTipiSerie_queryIsOk() {
        BigDecimal idXsdDatiSpec = aBigDecimal();
        helper.countXsdDatiSpecInUseInTipiSerie(idXsdDatiSpec);
        assertTrue(true);
    }
}
