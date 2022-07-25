package it.eng.parer.firma.crypto.verifica;

import java.net.URL;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test del singleton Tika
 *
 * @author Snidero_L
 */
@RunWith(Arquillian.class)
public class SpringTikaSingletonTest {

    @Inject
    private SpringTikaSingleton tikaSingleton;

    @Deployment
    public static JavaArchive createTestArchive() {

        return ShrinkWrap.create(JavaArchive.class, "test-tika.jar").addClass(SpringTikaSingleton.class)
                .addClass(Tika.class)// .addClass(SecureProtocolSocketFactory.class)
                .addPackages(true, "org.apache.tika", "org.apache.pdfbox", "org.apache.james", "org.apache.commons",
                        "org.osgi", "com.healthmarketscience", "de.l3s", "org.apache.fontbox", "org.apache.xerces",
                        "org.w3c.dom", "org.apache.poi", "com.google", "org.bouncycastle", "org.mozilla", "com.sun.net",
                        "org.objectweb", "org.apache.xmlbeans", "org.apache.tools")
                .addAsResource("org/apache/tika/mime/tika-mimetypes.xml")
                .addAsResource(SpringTikaSingletonTest.class.getClassLoader().getResource("ejb-jar-firma.xml"),
                        "ejb-jar-firma.xml")
                .addAsResource(SpringTikaSingletonTest.class.getClassLoader().getResource("testTika.docx"),
                        "testTika.docx")
                .addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"));
    }

    @Test
    public void testTikaXml() throws Exception {

        URL fileXml = SpringTikaSingletonTest.class.getResource("/ejb-jar-firma.xml");
        byte[] fileBytes = IOUtils.toByteArray(fileXml);
        String expectedMimeType = "application/xml";
        String actualMimeType = tikaSingleton.detectMimeType(fileBytes);

        Assert.assertEquals(expectedMimeType, actualMimeType);

    }

    @Test
    public void verificaDocx() throws Exception {

        URL fileDocx = SpringTikaSingletonTest.class.getResource("/testTika.docx");
        byte[] fileBytes = IOUtils.toByteArray(fileDocx);
        String expectedDocxMimeType = "application/msword";
        String actualMimeType = tikaSingleton.detectMimeType(fileBytes);

        Assert.assertEquals(expectedDocxMimeType, actualMimeType);

    }
}
