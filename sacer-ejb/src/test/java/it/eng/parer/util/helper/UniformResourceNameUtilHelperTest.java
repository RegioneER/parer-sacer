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

package it.eng.parer.util.helper;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Arrays;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;

import it.eng.parer.serie.helper.SerieHelperTest;
import it.eng.parer.web.helper.HelperTest;

public class UniformResourceNameUtilHelperTest extends HelperTest<UniformResourceNameUtilHelper> {

    @Test
    void existsCdKeyNormalizedQueryIsOk() {
	long idRegistro = 0L;
	BigDecimal aaKeyUnitaDoc = BigDecimal.valueOf(1900);
	String cdKeyUnitaDoc = "KEY";
	String cdKeyUnitaDocNormaliz = "KEYNORMALIZ";
	helper.existsCdKeyNormalized(idRegistro, aaKeyUnitaDoc, cdKeyUnitaDoc,
		cdKeyUnitaDocNormaliz);
	assertTrue(true);
    }

    @Test
    void retrieveVrsVLisXmlUpdUrnDaCalcByUpdQueryIsOk(String methodName) throws Throwable {
	Class[] paramsType = {
		long.class };
	Object[] paramsValue = {
		0L };
	assertTrueByReflection(paramsType, paramsValue, "retrieveVrsVLisXmlUpdUrnDaCalcByUpd");
    }

    @Test
    void retrieveAroUpdUnitaDocByUdQueryIsOk() throws Throwable {
	Class[] paramsType = {
		long.class };
	Object[] paramsValue = {
		0L };
	String methodName = "retrieveAroUpdUnitaDocByUd";
	assertTrueByReflection(paramsType, paramsValue, methodName);
    }

    @Test
    void retrieveAroVLisaipudUrndacalcByudQueryIsOk() throws Throwable {
	Class[] paramsType = {
		long.class };
	Object[] paramsValue = {
		0L };
	String methodName = "retrieveAroVLisaipudUrndacalcByud";
	assertTrueByReflection(paramsType, paramsValue, methodName);
    }

    @Test
    void retrieveVrsVLisXmlDocUrnDaCalcByDocQueryIsOk() throws Throwable {
	Class[] paramsType = {
		long.class };
	Object[] paramsValue = {
		0L };
	String methodName = "retrieveVrsVLisXmlDocUrnDaCalcByDoc";
	assertTrueByReflection(paramsType, paramsValue, methodName);
    }

    @Test
    void retrieveVrsVLisXmlUdUrnDaCalcByUdQueryIsOk() throws Throwable {
	Class[] paramsType = {
		long.class };
	Object[] paramsValue = {
		0L };
	String methodName = "retrieveVrsVLisXmlUdUrnDaCalcByUd";
	assertTrueByReflection(paramsType, paramsValue, methodName);
    }

    private void assertTrueByReflection(Class[] paramsType, Object[] paramsValue, String methodName)
	    throws Throwable {
	Method mthd = UniformResourceNameUtilHelper.class.getDeclaredMethod(methodName, paramsType);
	mthd.setAccessible(true);
	try {
	    mthd.invoke(helper, paramsValue);
	    assertTrue(true);
	} catch (InvocationTargetException e) {
	    throw e.getTargetException();
	}
    }

    @Deployment
    public static Archive<?> createTestArchive() {
	final JavaArchive sacerJavaArchive = createSacerJavaArchive(
		Arrays.asList("it.eng.parer.ws.dto"), UniformResourceNameUtilHelperTest.class,
		UniformResourceNameUtilHelper.class);
	return createEnterpriseArchive(SerieHelperTest.class.getSimpleName(), sacerJavaArchive,
		createPaginatorJavaArchive(), createSacerLogJar());
    }
}
