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
import static it.eng.ArquillianUtils.aListOfString;
import static it.eng.ArquillianUtils.aString;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBException;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.parer.exception.ParamApplicNotFoundException;
import it.eng.parer.web.helper.dto.AplVGetValParamDto;
import it.eng.parer.ws.utils.CostantiDB;

@ArquillianTest
public class ConfigurationHelperTest {
    @EJB
    private ConfigurationHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
        return HelperTest.createEnterpriseArchive(ConfigurationHelperTest.class.getSimpleName(),
                HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
                HelperTest.createSacerJavaArchive(Arrays.asList(""), ConfigurationHelperTest.class,
                        ConfigurationHelper.class, ParamApplicNotFoundException.class,
                        CostantiDB.class, AplVGetValParamDto.class));
    }

    @Test
    public void getConfiguration_queryIsOk() {
        assertNotNull(helper.getConfiguration());
    }

    @Test
    public void getParamApplicMapValue_queryIsOk() {
        List<String> nmParamApplicList = aListOfString(2);
        BigDecimal idAmbiente = aBigDecimal();
        BigDecimal idStrut = aBigDecimal();
        BigDecimal idTipoUnitaDoc = aBigDecimal();
        BigDecimal idAaTipoFascicolo = aBigDecimal();
        CostantiDB.TipoAplVGetValAppart getVal = CostantiDB.TipoAplVGetValAppart.AATIPOFASCICOLO;
        assertNotNull(helper.getParamApplicMapValue(nmParamApplicList, idAmbiente, idStrut,
                idTipoUnitaDoc, idAaTipoFascicolo, getVal));

        getVal = CostantiDB.TipoAplVGetValAppart.AMBIENTE;
        assertNotNull(helper.getParamApplicMapValue(nmParamApplicList, idAmbiente, idStrut,
                idTipoUnitaDoc, idAaTipoFascicolo, getVal));

        getVal = CostantiDB.TipoAplVGetValAppart.AATIPOFASCICOLO;
        assertNotNull(helper.getParamApplicMapValue(nmParamApplicList, idAmbiente, idStrut,
                idTipoUnitaDoc, idAaTipoFascicolo, getVal));

        getVal = CostantiDB.TipoAplVGetValAppart.APPLIC;
        assertNotNull(helper.getParamApplicMapValue(nmParamApplicList, idAmbiente, idStrut,
                idTipoUnitaDoc, idAaTipoFascicolo, getVal));

        getVal = CostantiDB.TipoAplVGetValAppart.STRUT;
        assertNotNull(helper.getParamApplicMapValue(nmParamApplicList, idAmbiente, idStrut,
                idTipoUnitaDoc, idAaTipoFascicolo, getVal));

        getVal = CostantiDB.TipoAplVGetValAppart.TIPOUNITADOC;
        assertNotNull(helper.getParamApplicMapValue(nmParamApplicList, idAmbiente, idStrut,
                idTipoUnitaDoc, idAaTipoFascicolo, getVal));

    }

    @Test
    public void getParamApplicApplicationName_queryIsOk() {
        assertNotNull(helper.getParamApplicApplicationName());
    }

    @Test
    public void getAplValoreParamApplic_queryIsOk() {
        String nmParamApplic = "";
        String tiAppart = "";
        BigDecimal idAmbiente = null;
        BigDecimal idStrut = null;
        BigDecimal idTipoUnitaDoc = null;
        BigDecimal idAaTipoFascicolo = null;
        helper.getAplValoreParamApplic(nmParamApplic, tiAppart, idAmbiente, idStrut, idTipoUnitaDoc,
                idAaTipoFascicolo);
        assertTrue(true);
    }

    @Test
    public void getParamApplic_queryIsOk() {
        helper.getParamApplic(aString());
        assertTrue(true);
    }

    @Test
    public void getValoreParamApplic_6args_queryIsOk() {
        String nmParamApplic = aString();
        BigDecimal idAmbiente = aBigDecimal();
        BigDecimal idStrut = aBigDecimal();
        BigDecimal idTipoUnitaDoc = aBigDecimal();
        BigDecimal idAaTipoFascicolo = aBigDecimal();

        CostantiDB.TipoAplVGetValAppart tipoAplVGetValAppart = CostantiDB.TipoAplVGetValAppart.AATIPOFASCICOLO;
        assertGetValoreParamApplicIsOk(nmParamApplic, idAmbiente, idStrut, idTipoUnitaDoc,
                idAaTipoFascicolo, tipoAplVGetValAppart);

        tipoAplVGetValAppart = CostantiDB.TipoAplVGetValAppart.AMBIENTE;
        assertGetValoreParamApplicIsOk(nmParamApplic, idAmbiente, idStrut, idTipoUnitaDoc,
                idAaTipoFascicolo, tipoAplVGetValAppart);

        tipoAplVGetValAppart = CostantiDB.TipoAplVGetValAppart.APPLIC;
        assertGetValoreParamApplicIsOk(nmParamApplic, idAmbiente, idStrut, idTipoUnitaDoc,
                idAaTipoFascicolo, tipoAplVGetValAppart);

        tipoAplVGetValAppart = CostantiDB.TipoAplVGetValAppart.STRUT;
        assertGetValoreParamApplicIsOk(nmParamApplic, idAmbiente, idStrut, idTipoUnitaDoc,
                idAaTipoFascicolo, tipoAplVGetValAppart);

        tipoAplVGetValAppart = CostantiDB.TipoAplVGetValAppart.TIPOUNITADOC;
        assertGetValoreParamApplicIsOk(nmParamApplic, idAmbiente, idStrut, idTipoUnitaDoc,
                idAaTipoFascicolo, tipoAplVGetValAppart);

    }

    private void assertGetValoreParamApplicIsOk(String nmParamApplic, BigDecimal idAmbiente,
            BigDecimal idStrut, BigDecimal idTipoUnitaDoc, BigDecimal idAaTipoFascicolo,
            CostantiDB.TipoAplVGetValAppart tipoAplVGetValAppart) {
        try {
            switch (tipoAplVGetValAppart) {
            case AATIPOFASCICOLO:
                helper.getValoreParamApplicByAaTipoFasc(nmParamApplic, idAmbiente, idStrut,
                        idAaTipoFascicolo);
                break;
            case TIPOUNITADOC:
                helper.getValoreParamApplicByTipoUd(nmParamApplic, idAmbiente, idStrut,
                        idTipoUnitaDoc);
                break;
            case STRUT:
                helper.getValoreParamApplicByStrut(nmParamApplic, idAmbiente, idStrut);
                break;
            case AMBIENTE:
                helper.getValoreParamApplicByAmb(nmParamApplic, idAmbiente);
                break;
            default:
                helper.getValoreParamApplicByApplic(nmParamApplic);
                break;
            }

        } catch (EJBException p) {
            // è certo che non trovi il parametro, essendo una stringa random
            assertTrue(p.getMessage().contains("ParamApplicNotFoundException"));
        }
    }

}
