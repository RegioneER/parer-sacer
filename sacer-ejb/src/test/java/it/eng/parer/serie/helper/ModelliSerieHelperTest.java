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

package it.eng.parer.serie.helper;

import static it.eng.ArquillianUtils.aBigDecimal;
import static it.eng.ArquillianUtils.aLong;
import static it.eng.ArquillianUtils.aString;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;

import javax.ejb.EJB;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.jupiter.api.Test;

import it.eng.parer.web.helper.HelperTest;
import it.eng.parer.web.util.Constants;

@ArquillianTest
public class ModelliSerieHelperTest {
    @EJB
    private ModelliSerieHelper helper;

    @Deployment
    public static Archive<?> createTestArchive() {
	return HelperTest.createEnterpriseArchive(ModelliSerieHelperTest.class.getSimpleName(),
		HelperTest.createPaginatorJavaArchive(), HelperTest.createSacerLogJar(),
		HelperTest.createSacerJavaArchive(Arrays.asList(""), ModelliSerieHelperTest.class,
			ModelliSerieHelper.class, Constants.class));
    }

    @Test
    void retrieveDecModelloTipoSerie_queryIsOk() {
	Long idAmbiente = aLong();
	Long idStrut = aLong();
	boolean filterValid = false;
	helper.retrieveDecModelloTipoSerie(idAmbiente, idStrut, filterValid);
	assertTrue(true);
    }

    @Test
    void retrieveDecModelloTipoSerieFromDecUsoModello_queryIsOk() {
	BigDecimal idStrut = aBigDecimal();
	boolean filterValid = false;
	helper.retrieveDecModelloTipoSerieFromDecUsoModello(idStrut, filterValid);
	assertTrue(true);
    }

    @Test
    void getDecModelloTipoSerie_queryIsOk() {
	String nmModelloTipoSerie = aString();
	BigDecimal idAmbiente = aBigDecimal();
	helper.getDecModelloTipoSerie(nmModelloTipoSerie, idAmbiente);
	assertTrue(true);
    }

    @Test
    void retrieveDecNotaModelloTipoSerie_queryIsOk() {
	BigDecimal idModelloTipoSerie = aBigDecimal();
	helper.retrieveDecNotaModelloTipoSerie(idModelloTipoSerie);
	assertTrue(true);
    }

    @Test
    void retrieveDecModelloCampoInpUd_BigDecimal_queryIsOk() {
	BigDecimal idModelloTipoSerie = aBigDecimal();
	helper.retrieveDecModelloCampoInpUd(idModelloTipoSerie);
	assertTrue(true);
    }

    @Test
    void retrieveDecModelloCampoInpUd_BigDecimal_String_queryIsOk() {
	BigDecimal idModelloTipoSerie = aBigDecimal();
	String tiCampo = aString();
	helper.retrieveDecModelloCampoInpUd(idModelloTipoSerie, tiCampo);
	assertTrue(true);
    }

    @Test
    void getDecModelloCampoInpUd_queryIsOk() {
	BigDecimal idModelloTipoSerie = aBigDecimal();
	String tiCampo = aString();
	String nmCampo = aString();
	helper.getDecModelloCampoInpUd(idModelloTipoSerie, tiCampo, nmCampo);
	assertTrue(true);
    }

    @Test
    void retrieveDecModelloOutSelUd_queryIsOk() {
	BigDecimal idModelloTipoSerie = aBigDecimal();
	helper.retrieveDecModelloOutSelUd(idModelloTipoSerie);
	assertTrue(true);
    }

    @Test
    void retrieveDecModelloCampoOutSelUd_queryIsOk() {
	BigDecimal idModelloOutSelUd = aBigDecimal();
	String tiCampo = aString();
	helper.retrieveDecModelloCampoOutSelUd(idModelloOutSelUd, tiCampo);
	assertTrue(true);
    }

    @Test
    void retrieveDecModelloFiltroTiDoc_queryIsOk() {
	BigDecimal idModelloTipoSerie = aBigDecimal();
	helper.retrieveDecModelloFiltroTiDoc(idModelloTipoSerie);
	assertTrue(true);
    }

    @Test
    void retrieveDecModelloFiltroSelUdattb_queryIsOk() {
	BigDecimal idModelloTipoSerie = aBigDecimal();
	helper.retrieveDecModelloFiltroSelUdattb(idModelloTipoSerie);
	assertTrue(true);
    }

    @Test
    void retrieveDecUsoModelloTipoSerie_queryIsOk() {
	BigDecimal idModelloTipoSerie = aBigDecimal();
	helper.retrieveDecUsoModelloTipoSerie(idModelloTipoSerie);
	assertTrue(true);
    }

    @Test
    void getMaxPgDecNotaModelloSerie_queryIsOk() {
	BigDecimal idModelloTipoSerie = aBigDecimal();
	BigDecimal idTipoNotaSerie = aBigDecimal();
	helper.getMaxPgDecNotaModelloSerie(idModelloTipoSerie, idTipoNotaSerie);
	assertTrue(true);
    }

    @Test
    void existDecUsoModelloTipoSerie_queryIsOk() {
	BigDecimal idModelloTipoSerie = aBigDecimal();
	BigDecimal idStrut = aBigDecimal();
	helper.existDecUsoModelloTipoSerie(idModelloTipoSerie, idStrut);
	assertTrue(true);
    }

    @Test
    void existsRelationsWithModello_queryIsOk() {
	long idTipoDato = 0L;
	Constants.TipoDato tipoDato = Constants.TipoDato.TIPO_UNITA_DOC;
	helper.existsRelationsWithModello(idTipoDato, tipoDato);
	assertTrue(true);
	tipoDato = Constants.TipoDato.TIPO_DOC;
	helper.existsRelationsWithModello(idTipoDato, tipoDato);
	assertTrue(true);
    }

    @Test
    void getDecModelloFiltroTiDoc_queryIsOk() {
	BigDecimal idModelloTipoSerie = aBigDecimal();
	helper.getDecModelloFiltroTiDoc(idModelloTipoSerie);
	assertTrue(true);
    }
}
