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

package it.eng.parer.firma.crypto.verifica;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.eng.paginator.ejb.PaginatorInterceptor;
import it.eng.parer.crypto.model.exceptions.CryptoParerException;
import it.eng.parer.entity.AroCompDoc;
import it.eng.parer.entity.AroStrutDoc;
import it.eng.parer.exception.CRLNotFoundException;
import it.eng.parer.firma.crypto.helper.CryptoRestConfiguratorHelper;
import it.eng.parer.grantedViewEntity.UsrVAbilAmbEnteConvenz;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.slite.gen.form.ComponentiForm;
import it.eng.parer.slite.gen.form.VolumiForm;
import it.eng.parer.slite.gen.tablebean.AroCompDocRowBean;
import it.eng.parer.slite.gen.tablebean.AroCompDocTableBean;
import it.eng.parer.web.dto.DecCriterioDatiSpecBean;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.util.StringPadding;
import it.eng.parer.ws.utils.CostantiDB;
import it.eng.sequences.hibernate.NonMonotonicSequenceGenerator;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.FrameElement;
import it.eng.spagoLite.FrameElementInterface;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.AbstractBaseTable;
import it.eng.spagoLite.form.Elements;
import it.eng.spagoLite.form.base.BaseElement;
import it.eng.spagoLite.form.fields.Fields;
import it.eng.spagoLite.xmlbean.form.Field;
import java.security.SignatureException;
import java.util.Date;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;

import static org.junit.Assert.assertTrue;

/**
 * I casi di test utilizzati devono essere recuperati dal DB di <strong>sviluppo</strong>.
 *
 * @author Snidero_L
 */
@RunWith(Arquillian.class)
public class FirmaDtVersTest {

    public static final String QUERY_ARO_COMP_DOC = "SELECT a FROM AroCompDoc a JOIN FETCH a.aroFirmaComps f JOIN FETCH f.aroContrFirmaComps WHERE a.idCompDoc=:idCompDoc";

    @Deployment
    public static WebArchive createTestArchive() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "sacerTest.war")
                .addPackage(GenericHelper.class.getPackage()).addPackage(AroCompDocRowBean.class.getPackage())
                .addPackage(AroCompDocTableBean.class.getPackage()).addPackage(VolumiForm.class.getPackage())
                .addPackage(JEEBaseRowInterface.class.getPackage()).addPackage(Fields.class.getPackage())
                .addPackage(Elements.class.getPackage()).addPackage(BaseElement.class.getPackage())
                .addClass(EMFError.class).addPackage(AbstractBaseTable.class.getPackage())
                .addPackage(BaseRow.class.getPackage()).addPackage(UsrVAbilAmbEnteConvenz.class.getPackage())
                .addClass(AroCompDoc.class).addClass(AroStrutDoc.class).addClass(Field.Type.class)
                .addClass(StringPadding.class).addClass(CostantiDB.class).addClass(FrameElement.class)
                .addClass(ComponentiForm.RicComponentiFiltri.class).addClass(FrameElementInterface.class)
                .addClass(DecCriterioDatiSpecBean.class).addClass(CryptoParerException.class)
                .addClass(CryptoInvoker.class).addClass(ResponseErrorHandler.class)
                .addClass(ClientHttpRequestFactory.class).addClass(MultiValueMap.class)
                .addClass(CryptoRestConfiguratorHelper.class).addClass(ConfigurationHelper.class)
                .addClass(NonMonotonicSequenceGenerator.class).addClass(PaginatorInterceptor.class)
                .addClass(it.eng.paginator.hibernate.OracleSqlInterceptor.class).addClass(HttpUriRequest.class)
                .addClass(HttpContext.class).addClass(HttpRequest.class).addClass(HttpMessage.class)
                .addClass(CryptoErrorHandler.class).addClass(ObjectMapper.class).addClass(FirmeDtVers.class)
                // with subpackages
                .addPackages(true, "it.eng.spagoLite.form", "it.eng.parer.jboss.timer.common", "it.eng.spagoLite.db",
                        "it.eng.parer.entity", "it.eng.parer.grantedEntity", "it.eng.parer.viewEntity",
                        "org.codehaus.jettison.json", "org.apache.commons.fileupload", "org.apache.xmlbeans",
                        "org.apache.tools.ant", "com.sun.javadoc", "org.apache.commons", "it.eng.paginator.helper",
                        "it.eng.paginator.util", "it.eng.parer.firma.crypto.helper.retry", "org.springframework",
                        "it.eng.parer.crypto.model", "org.apache.http", "com.fasterxml.jackson", "it.eng.parer.retry")
                // NO subpackages
                .addPackages(false, "it.eng.parer.sacerlog.entity", "it.eng.parer.sacerlog.viewEntity",
                        "it.eng.parer.slite.gen.viewbean", "org.apache.commons.lang", "org.apache.tools.ant.taskdefs",
                        "it.eng.parer.aop", "it.eng.parer.exception", "it.eng.parer.web.helper.dto",
                        "it.eng.parer.crypto")
                .addAsResource(FirmaDtVersTest.class.getClassLoader().getResource("persistence.xml"),
                        "META-INF/persistence.xml")
                .addAsResource(FirmaDtVersTest.class.getClassLoader().getResource("countselectlist.properties"),
                        "countselectlist.properties")
                .addAsWebInfResource(FirmaDtVersTest.class.getClassLoader().getResource("ejb-jar-firma.xml"),
                        "ejb-jar.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");

        // System.out.println(war.toString(true));
        return war;

    }

    @Inject
    private FirmeDtVers helper;

    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager em;

    private static final long COMPONENTE_CON_SUBJECT_KEY_ID = 6110330987L;
    private static final long COMPONENTE_SENZA_SUBJECT_KEY_ID = 2770L;
    private static final long COMPONENTE_CRL_NON_RECUPERABILE = 3181323466L;

    /**
     * Esegui il corpo del job di verifica firme con un caso in cui il subject key id della ca del firmatario sia
     * valorizzato
     *
     * @throws SignatureException
     * @throws CRLNotFoundException
     */
    @Test
    @Ignore("Errore multiple bags fetch su QUERY_ARO_COMP_DOC")
    public void testVerificaFirmeDtVers() throws SignatureException, CRLNotFoundException {

        final TypedQuery<AroCompDoc> query = em.createQuery(QUERY_ARO_COMP_DOC, AroCompDoc.class);
        query.setParameter("idCompDoc", COMPONENTE_CON_SUBJECT_KEY_ID);
        AroCompDoc compDoc = query.getSingleResult();
        Date dataRif = new Date();
        helper.verificaFirme(compDoc, dataRif);
        assertTrue(true);
    }

    /**
     * Esegui il corpo del job di verifica firme con un caso in cui il subject key id della ca del firmatario non sia
     * valorizzato
     *
     * @throws SignatureException
     *             in caso di errore generico
     * @throws CRLNotFoundException
     *             in caso di CRL non trovata
     */
    @Test
    @Ignore("Errore multiple bags fetch su QUERY_ARO_COMP_DOC")
    public void testVerificaFirmeDtVersCrlWithoutSubjectDN() throws SignatureException, CRLNotFoundException {

        final TypedQuery<AroCompDoc> query = em.createQuery(QUERY_ARO_COMP_DOC, AroCompDoc.class);
        query.setParameter("idCompDoc", COMPONENTE_SENZA_SUBJECT_KEY_ID);
        AroCompDoc compDoc = query.getSingleResult();
        Date dataRif = new Date();
        helper.verificaFirme(compDoc, dataRif);
        assertTrue(true);
    }

    /**
     * Test per validare la gestione delle eccezioni
     *
     * @throws SignatureException
     *             in caso di errore generico
     * @throws CRLNotFoundException
     *             in caso di CRL non trovata
     */
    @Test
    @Ignore("Non è l'id corretto.")
    public void testCrlNonRecuperabile() throws SignatureException, CRLNotFoundException {
        AroCompDoc compDoc = em.find(AroCompDoc.class, COMPONENTE_CRL_NON_RECUPERABILE);
        Date dataRif = new Date();
        helper.verificaFirme(compDoc, dataRif);
        assertTrue(true);
    }

}
