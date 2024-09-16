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

package it.eng.parer.soapWS.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Classe Java per getStatoOggettoResponse complex type.
 *
 * <p>
 * Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 *
 * <pre>
 * &lt;complexType name="getStatoOggettoResponse"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="return" type="{http://ws.sacerasi.eng.it/}recuperoStatoOggettoRisposta" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getStatoOggettoResponseTipo", propOrder = { "_return" })
public class GetStatoOggettoResponse {

    @XmlElement(name = "return")
    protected RecuperoStatoOggettoRisposta _return;

    /**
     * Recupera il valore della proprietà return.
     *
     * @return possible object is {@link RecuperoStatoOggettoRisposta }
     *
     */
    public RecuperoStatoOggettoRisposta getReturn() {
        return _return;
    }

    /**
     * Imposta il valore della proprietà return.
     *
     * @param value
     *            allowed object is {@link RecuperoStatoOggettoRisposta }
     *
     */
    public void setReturn(RecuperoStatoOggettoRisposta value) {
        this._return = value;
    }

}
