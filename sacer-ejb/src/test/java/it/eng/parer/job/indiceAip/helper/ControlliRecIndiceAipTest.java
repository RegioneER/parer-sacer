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

package it.eng.parer.job.indiceAip.helper;

import it.eng.parer.elencoVersamento.helper.ElencoVersamentoHelper;
import it.eng.parer.elencoVersamento.utils.ElencoEnums;
import it.eng.parer.entity.AroCompDoc;
import it.eng.parer.entity.constraint.AroUpdDatiSpecUnitaDoc;
import it.eng.parer.entity.constraint.AroUrnVerIndiceAipUd;
import it.eng.parer.entity.constraint.AroVersIniDatiSpec;
import it.eng.parer.entity.constraint.DecModelloXsdUd;
import it.eng.parer.job.dto.SessioneVersamentoExt;
import it.eng.parer.volume.helper.VolumeHelper;
import it.eng.parer.volume.utils.ReturnParams;
import it.eng.parer.web.dto.DefinitoDaBean;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.HelperTest;
import it.eng.parer.web.helper.UnitaDocumentarieHelper;
import it.eng.parer.web.util.Constants;
import it.eng.parer.ws.dto.CSChiave;
import it.eng.parer.ws.dto.CSVersatore;
import it.eng.parer.ws.utils.CostantiDB;
import java.util.Arrays;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import static org.junit.Assert.*;
import org.junit.Ignore;
import org.junit.Test;

public class ControlliRecIndiceAipTest extends HelperTest<ControlliRecIndiceAip> {

    @Test
    @Ignore("può funzionare solo con un id esistente, troppo dipendente dai dati sul DB")
    public void leggiXmlVersamentiAipQueryIsOk() {
        Long id = 179516095L;
        helper.leggiXmlVersamentiAip(id);
        assertTrue(true);
    }

    @Test
    public void leggiXmlVersamentiAipDaUnitaDocQueryIsOk() {
        Long idUnitaDoc = -9L;
        helper.leggiXmlVersamentiAipDaUnitaDoc(idUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void leggiXmlVersamentiAipUpdDaUnitaDocQueryIsOk() {
        Long idUnitaDoc = -9L;
        helper.leggiXmlVersamentiAipUpdDaUnitaDoc(idUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void leggiFascicoliSecQueryIsOk() {
        Long idUnitaDoc = -9L;
        helper.leggiFascicoliSec(idUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void leggiAroVersIniUnitaDocQueryIsOk() {
        Long idUnitaDoc = -9L;
        helper.leggiAroVersIniUnitaDoc(idUnitaDoc);
        assertTrue(true);
    }

    @Test
    public void leggiFascicoliSecVersIniUnitaDocQueryIsOk() {
        Long id = -9L;
        helper.leggiFascicoliSecVersIniUnitaDoc(id);
        assertTrue(true);
    }

    @Test
    public void leggiVersamentiAipUpdPgMaxInCodaQueryIsOk() {
        Long id = -9L;
        helper.leggiVersamentiAipUpdPgMaxInCoda(id);
        assertTrue(true);
    }

    @Test
    public void leggiFascicoliSecUpdQueryIsOk() {
        Long id = -9L;
        helper.leggiFascicoliSecUpd(id);
        assertTrue(true);
    }

    @Test
    public void leggiUDocCollegQueryIsOk() {
        Long id = -9L;
        helper.leggiUDocColleg(id);
        assertTrue(true);
    }

    @Test
    public void leggiUDocCollegVersIniUpdQueryIsOk() {
        Long id = -9L;
        helper.leggiUDocCollegVersIniUpd(id);
        assertTrue(true);
    }

    @Test
    public void leggiUDocCollegUpdQueryIsOk() {
        Long id = -9L;
        helper.leggiUDocCollegUpd(id);
        assertTrue(true);
    }

    @Test
    public void leggiDatiSpecEntityQueryIsOk() {
        Long id = -9L;
        for (CostantiDB.TipiUsoDatiSpec tipoUsoAttr : CostantiDB.TipiUsoDatiSpec.values()) {
            for (CostantiDB.TipiEntitaSacer tipoEntitySacer : CostantiDB.TipiEntitaSacer.values()) {
                helper.leggiDatiSpecEntity(tipoUsoAttr, tipoEntitySacer, id);
            }
        }
        assertTrue(true);
    }

    @Test
    public void leggiDatiSpecEntityVersIniUpdQueryIsOk() {
        Long id = -9L;
        for (AroVersIniDatiSpec.TiUsoXsdAroVersIniDatiSpec tiUsoXsd : AroVersIniDatiSpec.TiUsoXsdAroVersIniDatiSpec
                .values()) {
            for (AroVersIniDatiSpec.TiEntitaSacerAroVersIniDatiSpec tipoEntitySacer : AroVersIniDatiSpec.TiEntitaSacerAroVersIniDatiSpec
                    .values()) {
                helper.leggiDatiSpecEntityVersIniUpd(tiUsoXsd, tipoEntitySacer, id);
            }
        }
        assertTrue(true);
    }

    @Test
    public void leggiDatiSpecEntityUpdQueryIsOk() {
        Long id = -9L;
        for (AroUpdDatiSpecUnitaDoc.TiUsoXsdAroUpdDatiSpecUnitaDoc tiUso : AroUpdDatiSpecUnitaDoc.TiUsoXsdAroUpdDatiSpecUnitaDoc
                .values()) {
            for (AroUpdDatiSpecUnitaDoc.TiEntitaAroUpdDatiSpecUnitaDoc tiEntita : AroUpdDatiSpecUnitaDoc.TiEntitaAroUpdDatiSpecUnitaDoc
                    .values()) {
                helper.leggiDatiSpecEntityUpd(tiUso, tiEntita, id);
            }
        }
        assertTrue(true);
    }

    @Test
    public void leggiSottoComponentiQueryIsOk() {
        AroCompDoc compPadre = new AroCompDoc();
        compPadre.setIdCompDoc(-9L);
        helper.leggiSottoComponenti(compPadre);
        assertTrue(true);
    }

    @Test
    public void getVersioneSacerQueryIsOk() {
        helper.getVersioneSacer();
        assertTrue(true);
    }

    @Test
    public void getVersioniPrecedentiAIPQueryIsOk() {
        Long id = -9L;
        helper.getVersioniPrecedentiAIP(id);
        assertTrue(true);
    }

    @Test
    public void getVersioniPrecedentiAIPExternalQueryIsOk() {
        Long id = -9L;
        helper.getVersioniPrecedentiAIPExternal(id);
        assertTrue(true);
    }

    @Test
    public void getVolumiUnitaDocListQueryIsOk() {
        Long id = -9L;
        helper.getVolumiUnitaDocList(id);
        assertTrue(true);
    }

    @Test
    public void leggiComponentiDocumentoQueryIsOk() {
        Long id = -9L;
        helper.leggiComponentiDocumento(anAroDoc());
        assertTrue(true);
    }

    @Test
    public void leggiComponenteDaVistaQueryIsOk() {
        Long id = -9L;
        helper.leggiComponenteDaVista(id);
        assertTrue(true);
    }

    @Test
    public void leggiComponenteDaVersIniUpdQueryIsOk() {
        Long id = -9L;
        helper.leggiComponenteDaVersIniUpd(id, id);
        assertTrue(true);
    }

    @Test
    public void leggiComponenteDaUpdQueryIsOk() {
        Long id = -9L;
        helper.leggiComponenteDaUpd(id, id);
        assertTrue(true);
    }

    @Test
    public void leggiDocumentoDaVersIniUpdQueryIsOk() {
        Long id = -9L;
        helper.leggiDocumentoDaVersIniUpd(id, id);
        assertTrue(true);
    }

    @Test
    public void leggiDocumentoDaUpdQueryIsOk() {
        Long id = -9L;
        helper.leggiDocumentoDaUpd(id, id);
        assertTrue(true);
    }

    @Test
    public void getPrecedentiVersioniIndiceAipQueryIsOk() {
        Long id = -9L;
        helper.getPrecedentiVersioniIndiceAip(id);
        assertTrue(true);
    }

    @Test
    public void leggiXmlVersamentiModelloXsdUnitaDoc_queryIsOk() {
        for (DecModelloXsdUd.TiModelloXsdUd profiloNormativoUnitaDoc : DecModelloXsdUd.TiModelloXsdUd.values()) {
            helper.leggiXmlVersamentiModelloXsdUnitaDoc("tiUsoModelloXsd", profiloNormativoUnitaDoc, 0L);
        }
        assertTrue(true);
    }

    @Deployment
    public static Archive<?> createTestArchive() {
        return createEnterpriseArchive(ControlliRecIndiceAipTest.class.getSimpleName(), createPaginatorJavaArchive(),
                createSacerLogJar(),
                createSacerJavaArchive(
                        Arrays.asList("it.eng.parer.elencoVersamento.utils", "com.fasterxml.uuid",
                                "com.fasterxml.uuid.impl", "it.eng.parer.ws.utils", "it.eng.parer.ws.dto"),
                        ControlliRecIndiceAip.class, ControlliRecIndiceAipTest.class, VolumeHelper.class,
                        ConfigurationHelper.class, ReturnParams.class, DefinitoDaBean.class,
                        ElencoVersamentoHelper.class, UnitaDocumentarieHelper.class, Constants.class, CSVersatore.class,
                        CSChiave.class, ElencoEnums.class, SessioneVersamentoExt.class,
                        it.eng.spagoCore.util.UUIDMdcLogUtil.class, it.eng.spagoCore.ConfigSingleton.class));
    }
}
