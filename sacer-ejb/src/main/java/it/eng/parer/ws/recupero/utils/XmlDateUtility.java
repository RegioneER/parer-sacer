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

package it.eng.parer.ws.recupero.utils;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fioravanti_f
 */
public class XmlDateUtility {

    private static final Logger logger = LoggerFactory.getLogger(XmlDateUtility.class.getName());

    public static XMLGregorianCalendar dateToXMLGregorianCalendar(Date date, TimeZone zone) {
	XMLGregorianCalendar xmlGregorianCalendar = null;
	GregorianCalendar gregorianCalendar = new GregorianCalendar();
	gregorianCalendar.setTime(date);
	gregorianCalendar.setTimeZone(zone);
	try {
	    DatatypeFactory dataTypeFactory = DatatypeFactory.newInstance();
	    xmlGregorianCalendar = dataTypeFactory.newXMLGregorianCalendar(gregorianCalendar);
	} catch (Exception e) {
	    logger.error("Exception in conversion of Date to XMLGregorianCalendar" + e);
	}

	return xmlGregorianCalendar;
    }

    public static XMLGregorianCalendar dateToXMLGregorianCalendar(Date date) {
	XMLGregorianCalendar xmlGregorianCalendar = null;
	GregorianCalendar gregorianCalendar = new GregorianCalendar();
	gregorianCalendar.setTime(date);
	try {
	    DatatypeFactory dataTypeFactory = DatatypeFactory.newInstance();
	    xmlGregorianCalendar = dataTypeFactory.newXMLGregorianCalendar(gregorianCalendar);
	} catch (Exception e) {
	    logger.error("Exception in conversion of Date to XMLGregorianCalendar" + e);
	}

	return xmlGregorianCalendar;
    }

    public static Date xmlGregorianCalendarToDate(XMLGregorianCalendar xmlGregorianCalendar,
	    TimeZone zone) {
	TimeZone.setDefault(zone);
	return new Date(xmlGregorianCalendar.toGregorianCalendar().getTimeInMillis());
    }

    public static Date xmlGregorianCalendarToDate(XMLGregorianCalendar xmlGregorianCalendar) {
	return new Date(xmlGregorianCalendar.toGregorianCalendar().getTimeInMillis());
    }
}
