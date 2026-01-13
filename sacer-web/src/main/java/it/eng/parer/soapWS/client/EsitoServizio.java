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

package it.eng.parer.soapWS.client;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Classe Java per esitoServizio.
 * </p>
 * <p>
 * Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * </p>
 *
 * <pre>
 * &lt;simpleType name="esitoServizio"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="OK"/&gt;
 *     &lt;enumeration value="KO"/&gt;
 *     &lt;enumeration value="WARN"/&gt;
 *     &lt;enumeration value="NO_RISPOSTA"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 *
 */
@XmlType(name = "esitoServizio")
@XmlEnum
public enum EsitoServizio {

    OK, KO, WARN, NO_RISPOSTA;

    public String value() {
        return name();
    }

    public static EsitoServizio fromValue(String v) {
        return valueOf(v);
    }

}
