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

package it.eng.parer.firma.crypto.verifica;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestClientException;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.eng.paginator.ejb.PaginatorInterceptor;
import it.eng.parer.crypto.model.ParerCRL;
import it.eng.parer.crypto.model.exceptions.CryptoParerException;
import it.eng.parer.entity.AroCompDoc;
import it.eng.parer.entity.AroStrutDoc;
import it.eng.parer.firma.crypto.helper.CryptoRestConfiguratorHelper;
import it.eng.parer.grantedViewEntity.UsrVAbilAmbEnteConvenz;
import it.eng.parer.helper.GenericHelper;
import it.eng.parer.slite.gen.form.ComponentiForm;
import it.eng.parer.slite.gen.form.VolumiForm;
import it.eng.parer.slite.gen.tablebean.AroCompDocRowBean;
import it.eng.parer.slite.gen.tablebean.AroCompDocTableBean;
import it.eng.parer.web.dto.DecCriterioDatiSpecBean;
import it.eng.parer.web.helper.ConfigurationHelper;
import it.eng.parer.web.helper.HelperTest;
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

/**
 * I casi di test utilizzati devono essere recuperati dal DB di <strong>sviluppo</strong>.
 *
 * @author Snidero_L
 */
@ArquillianTest
public class CryptoInvokerTest {

    @Deployment
    public static WebArchive createTestArchive() {
        WebArchive war = ShrinkWrap.create(WebArchive.class, "sacerInvokerTest.war")
                .addPackage(GenericHelper.class.getPackage())
                .addPackage(AroCompDocRowBean.class.getPackage())
                .addPackage(AroCompDocTableBean.class.getPackage())
                .addPackage(VolumiForm.class.getPackage())
                .addPackage(JEEBaseRowInterface.class.getPackage())
                .addPackage(Fields.class.getPackage()).addPackage(Elements.class.getPackage())
                .addPackage(BaseElement.class.getPackage()).addClass(EMFError.class)
                .addPackage(AbstractBaseTable.class.getPackage())
                .addPackage(BaseRow.class.getPackage())
                .addPackage(UsrVAbilAmbEnteConvenz.class.getPackage()).addClass(AroCompDoc.class)
                .addClass(AroStrutDoc.class).addClass(Field.Type.class)
                .addClass(StringPadding.class).addClass(CostantiDB.class)
                .addClass(FrameElement.class).addClass(ComponentiForm.RicComponentiFiltri.class)
                .addClass(FrameElementInterface.class).addClass(DecCriterioDatiSpecBean.class)
                .addClass(CryptoParerException.class).addClass(CryptoInvoker.class)
                .addClass(ResponseErrorHandler.class).addClass(ClientHttpRequestFactory.class)
                .addClass(MultiValueMap.class).addClass(CryptoRestConfiguratorHelper.class)
                .addClass(ConfigurationHelper.class).addClass(NonMonotonicSequenceGenerator.class)
                .addClass(PaginatorInterceptor.class).addClass(HttpUriRequest.class)
                .addClass(HttpContext.class).addClass(HttpRequest.class).addClass(HttpMessage.class)
                .addClass(CryptoErrorHandler.class).addClass(ObjectMapper.class)
                .addClass(CryptoParerException.class).addClass(RestClientException.class)
                .addClass(it.eng.paginator.hibernate.OracleSqlInterceptor.class)
                .addClass(HelperTest.class)
                // with subpackages
                .addPackages(true, "it.eng.spagoLite.form", "it.eng.parer.jboss.timer.common",
                        "it.eng.spagoLite.db", "it.eng.parer.entity", "it.eng.parer.grantedEntity",
                        "it.eng.parer.viewEntity", "org.codehaus.jettison.json",
                        "org.apache.commons.fileupload", "org.apache.xmlbeans",
                        "org.apache.tools.ant", "com.sun.javadoc", "org.apache.commons",
                        "it.eng.paginator.helper", "it.eng.paginator.util",
                        "it.eng.parer.firma.crypto.helper.retry", "org.springframework",
                        "it.eng.parer.crypto.model", "org.apache.http", "com.fasterxml.jackson",
                        "it.eng.parer.retry")
                // NO subpackages
                .addPackages(false, "it.eng.parer.sacerlog.entity",
                        "it.eng.parer.sacerlog.viewEntity", "it.eng.parer.slite.gen.viewbean",
                        "org.apache.commons.lang", "org.apache.tools.ant.taskdefs",
                        "it.eng.parer.aop", "it.eng.parer.exception", "it.eng.parer.web.helper.dto",
                        "it.eng.parer.crypto")
                .addAsResource(
                        CryptoInvokerTest.class.getClassLoader().getResource("persistence.xml"),
                        "META-INF/persistence.xml")
                .addAsResource(CryptoInvokerTest.class.getClassLoader()
                        .getResource("countselectlist.properties"), "countselectlist.properties")
                .addAsResource(CryptoInvokerTest.class.getClassLoader()
                        .getResource("fir_file_per_firma.blob"), "fir_file_per_firma.blob")
                .addAsWebInfResource(
                        CryptoInvokerTest.class.getClassLoader().getResource("ejb-jar-invoker.xml"),
                        "ejb-jar.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        // System.out.println(war.toString(true));
        return war;

    }

    @Inject
    private CryptoInvoker invoker;

    @Test
    void invokeCrl() {
        try {
            invoker.retrieveCRL("non esiste", "non esiste");
        } catch (Exception e) {
            HelperTest.assertExceptionMessage(e, "CRL_NOT_FOUND");
        }
    }

    @Test
    void invokeCrlFromFirma() throws URISyntaxException, IOException {
        // URL resource = CryptoInvokerTest.class.getResource("/fir_file_per_firma.blob");
        try (InputStream in = getClass().getResourceAsStream("/fir_file_per_firma.blob")) {

            byte[] firFilePerFirma = IOUtils.toByteArray(in);

            ParerCRL retrieveCRL = invoker.retrieveCRL(firFilePerFirma);
            assertNotNull(retrieveCRL);
        }
    }
}
