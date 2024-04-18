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

package it.eng.parer.amministrazioneStrutture.gestioneModelliXsdUd.helper;

import it.eng.parer.amministrazioneStrutture.gestioneFormatiFileStandard.helper.FormatoFileStandardHelper;
import it.eng.parer.entity.constraint.DecModelloXsdUd;
import it.eng.parer.slite.gen.form.ModelliUDForm;
import it.eng.parer.web.helper.HelperTest;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.spagoCore.error.EMFError;
import org.apache.commons.lang3.StringUtils;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class ModelliXsdUdHelperTest extends HelperTest<ModelliXsdUdHelper> {
    @Deployment
    public static Archive<?> createTestArchive_queryIsOk() {
        return HelperTest.createEnterpriseArchive(ModelliXsdUdHelperTest.class.getSimpleName(), createSacerLogJar(),
                createPaginatorJavaArchive(),
                createSacerJavaArchive(Arrays.asList(""), ModelliXsdUdHelperTest.class, ModelliXsdUdHelper.class));
    }

    @Test
    public void retrieveDecModelliXsdUdListByTiEntitaInUso_queryIsOk() {
        BigDecimal idTiEntita = BigDecimal.ZERO;
        String tiUsoModello = "tiUsoModello";
        for (CostantiDB.TipiEntitaSacer tiEntitaSacer : CostantiDB.TipiEntitaSacer.values()) {
            try {
                helper.retrieveDecModelliXsdUdListByTiEntitaInUso(idTiEntita, tiEntitaSacer, tiUsoModello, false);
                assertTrue(true);
                helper.retrieveDecModelliXsdUdListByTiEntitaInUso(idTiEntita, tiEntitaSacer, tiUsoModello, true);
                assertTrue(true);
            } catch (Exception e) {
                // è un tipo non gestito, ci può stare
                assertExceptionMessage(e, tiEntitaSacer.name());
            }
        }
    }

    @Test
    public void retrieveDecUsoModelloXsdUdListByTiEntitaInUso_queryIsOk() {
        BigDecimal idModelloXsdUd = BigDecimal.ZERO;
        BigDecimal idTiEntita = BigDecimal.ZERO;
        String tiUsoModello = "tiUsoModello";
        String cdXsd = "cdXsd";
        for (CostantiDB.TipiEntitaSacer tiEntitaSacer : CostantiDB.TipiEntitaSacer.values()) {
            try {
                helper.retrieveDecUsoModelloXsdUdListByTiEntitaInUso(idModelloXsdUd, idTiEntita, tiEntitaSacer,
                        tiUsoModello, cdXsd, StringUtils.EMPTY, true);
                assertTrue(true);
                helper.retrieveDecUsoModelloXsdUdListByTiEntitaInUso(idModelloXsdUd, idTiEntita, tiEntitaSacer,
                        tiUsoModello, cdXsd, StringUtils.EMPTY, false);
                assertTrue(true);
            } catch (Exception e) {
                // è un tipo non gestito, ci può stare
                assertExceptionMessage(e, tiEntitaSacer.name());
            }
        }
    }

    @Test
    public void retrieveDecModelliXsdUd4Amb_queryIsOk() {
        BigDecimal idAmbiente = BigDecimal.ZERO;
        for (CostantiDB.TiUsoModelloXsd tiUsoModelloXsd : CostantiDB.TiUsoModelloXsd.values()) {
            helper.retrieveDecModelliXsdUd4Amb(idAmbiente, tiUsoModelloXsd.name(), true);
            helper.retrieveDecModelliXsdUd4Amb(idAmbiente, tiUsoModelloXsd.name(), false);
        }
        assertTrue(true);
    }

    @Test
    public void retrieveDecModelliXsdUd4AmbAndTiModAndCdXsd_queryIsOk() {
        BigDecimal idAmbiente = BigDecimal.ZERO;
        String cdXsd = "cdXsd";
        String tiUsoModelloXsdUd = "tiUsoModelloXsdUd";
        for (DecModelloXsdUd.TiModelloXsdUd tiModelloXsd : DecModelloXsdUd.TiModelloXsdUd.values()) {
            helper.retrieveDecModelliXsdUd4AmbAndTiModAndCdXsd(idAmbiente, tiModelloXsd.name(), tiUsoModelloXsdUd,
                    cdXsd, true);
            helper.retrieveDecModelliXsdUd4AmbAndTiModAndCdXsd(idAmbiente, tiModelloXsd.name(), tiUsoModelloXsdUd,
                    cdXsd, false);
        }
        assertTrue(true);
    }

    @Test
    public void retrieveDecModelliXsdUd4AmbAndTiModelloXsd_queryIsOk() {
        BigDecimal idAmbiente = BigDecimal.ZERO;
        String tiUsoModelloXsd = "tiUsoModelloXsd";
        for (DecModelloXsdUd.TiModelloXsdUd tiModelloXsdUd : DecModelloXsdUd.TiModelloXsdUd.values()) {
            helper.retrieveDecModelliXsdUd4AmbAndTiModelloXsd(idAmbiente, tiModelloXsdUd.name(), tiUsoModelloXsd, true);
            helper.retrieveDecModelliXsdUd4AmbAndTiModelloXsd(idAmbiente, tiModelloXsdUd.name(), tiUsoModelloXsd,
                    false);
        }
        assertTrue(true);
    }

    @Test
    public void retrieveDecModelliXsdUd4AmbAndTiModelloDefXsd_queryIsOk() {
        BigDecimal idAmbiente = BigDecimal.ZERO;
        String flDefault = "0";
        for (DecModelloXsdUd.TiModelloXsdUd tiModelloXsdUd : DecModelloXsdUd.TiModelloXsdUd.values()) {
            for (DecModelloXsdUd.TiModelloXsdUd modelloXsdUd : DecModelloXsdUd.TiModelloXsdUd.values()) {
                helper.retrieveDecModelliXsdUd4AmbAndTiModelloDefXsd(idAmbiente, modelloXsdUd.name(),
                        tiModelloXsdUd.name(), flDefault, true);
                helper.retrieveDecModelliXsdUd4AmbAndTiModelloDefXsd(idAmbiente, modelloXsdUd.name(),
                        tiModelloXsdUd.name(), flDefault, false);
            }
        }
        assertTrue(true);
    }

    @Test
    public void decUsoModelloXsdUdInUseOnVrs_queryIsOk() {
        BigDecimal idStrut = BigDecimal.ZERO;
        BigDecimal idUsoModelloXsdUd = BigDecimal.ZERO;
        for (CostantiDB.TipiEntitaSacer tiEntitaSacer : CostantiDB.TipiEntitaSacer.values()) {
            try {
                helper.decUsoModelloXsdUdInUseOnVrs(idStrut, idUsoModelloXsdUd, tiEntitaSacer);
                assertTrue(true);
            } catch (Exception e) {
                // tipo non gestito
                assertExceptionMessage(e, tiEntitaSacer.name());
            }
        }
    }

    @Test
    public void decModelloXsdUdInUseOnVrs_queryIsOk() {
        helper.decModelloXsdUdInUseOnVrs(BigDecimal.ZERO);
        assertTrue(true);
    }

    @Test
    public void existDecModelliXsdUdListInUso_queryIsOk() {
        BigDecimal idModelloXsdUd = BigDecimal.ZERO;
        helper.existDecModelliXsdUdListInUso(idModelloXsdUd, false);
        helper.existDecModelliXsdUdListInUso(idModelloXsdUd, true);
        assertTrue(true);
    }

    @Test
    public void findDecModelliXsdUdList_queryIsOk() throws EMFError {
        final List<BigDecimal> idAmbientiToFind = Arrays.asList(BigDecimal.ZERO, BigDecimal.ONE);
        final String cdXsd = "cdXsd";
        final String dsXsd = "dsXsd";
        final String flDefault = "0";
        final String tiUsoModelloXsd = "tiUsoModelloXsd";
        for (DecModelloXsdUd.TiModelloXsdUd tiModelloXsdUd : DecModelloXsdUd.TiModelloXsdUd.values()) {
            helper.findDecModelliXsdUdList(idAmbientiToFind, tiUsoModelloXsd, true, cdXsd, dsXsd, flDefault,
                    tiModelloXsdUd.name());
            helper.findDecModelliXsdUdList(idAmbientiToFind, tiUsoModelloXsd, false, cdXsd, dsXsd, flDefault,
                    tiModelloXsdUd.name());
        }
        assertTrue(true);
    }
}
