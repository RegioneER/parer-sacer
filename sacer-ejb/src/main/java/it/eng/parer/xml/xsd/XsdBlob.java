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

package it.eng.parer.xml.xsd;

import java.io.Serializable;

/**
 * DTO per trasportare il contenuto di un XSD recuperato dal database.
 */
public class XsdBlob implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String cdXsd;
    private final String blXsd;

    /**
     * Costruttore.
     *
     * @param cdXsd Codice identificativo dell'XSD
     * @param blXsd Contenuto dell'XSD come stringa
     */
    public XsdBlob(String cdXsd, String blXsd) {
        this.cdXsd = cdXsd;
        this.blXsd = blXsd;
    }

    /**
     * @return Codice identificativo dell'XSD
     */
    public String getCdXsd() {
        return cdXsd;
    }

    /**
     * @return Contenuto dell'XSD come stringa
     */
    public String getBlXsd() {
        return blXsd;
    }
}
