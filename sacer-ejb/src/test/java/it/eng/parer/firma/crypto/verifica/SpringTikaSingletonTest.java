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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.net.URL;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.container.annotation.ArquillianTest;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

/**
 * Test del singleton Tika
 *
 * @author Snidero_L
 */
@ArquillianTest
public class SpringTikaSingletonTest {

    @Inject
    private SpringTikaSingleton tikaSingleton;

    @Deployment
    public static JavaArchive createTestArchive() {

	return ShrinkWrap.create(JavaArchive.class, "test-tika.jar")
		.addClass(SpringTikaSingleton.class).addClass(Tika.class)// .addClass(SecureProtocolSocketFactory.class)
		.addPackages(true, "org.apache.tika", "org.apache.pdfbox", "org.apache.james",
			"org.apache.commons", "org.osgi", "com.healthmarketscience", "de.l3s",
			"org.apache.fontbox", "org.apache.xerces", "org.w3c.dom", "org.apache.poi",
			"com.google", "org.bouncycastle", "org.mozilla", "com.sun.net",
			"org.objectweb", "org.apache.xmlbeans", "org.apache.tools")
		.addAsResource("org/apache/tika/mime/tika-mimetypes.xml")
		.addAsResource(SpringTikaSingletonTest.class.getClassLoader()
			.getResource("ejb-jar-firma.xml"), "ejb-jar-firma.xml")
		.addAsResource(
			SpringTikaSingletonTest.class.getClassLoader().getResource("testTika.docx"),
			"testTika.docx")
		.addAsManifestResource("META-INF/beans.xml", ArchivePaths.create("beans.xml"));
    }

    @Test
    void testTikaXml() throws Exception {

	URL fileXml = SpringTikaSingletonTest.class.getResource("/ejb-jar-firma.xml");
	byte[] fileBytes = IOUtils.toByteArray(fileXml);
	String expectedMimeType = "application/xml";
	String actualMimeType = tikaSingleton.detectMimeType(fileBytes);

	assertEquals(expectedMimeType, actualMimeType);

    }

    @Test
    void verificaDocx() throws Exception {

	URL fileDocx = SpringTikaSingletonTest.class.getResource("/testTika.docx");
	byte[] fileBytes = IOUtils.toByteArray(fileDocx);
	String expectedDocxMimeType = "application/msword";
	String actualMimeType = tikaSingleton.detectMimeType(fileBytes);

	assertEquals(expectedDocxMimeType, actualMimeType);

    }
}
