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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Classe Java per getStatoOggetto complex type.
 *
 * <p>
 * Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 *
 * <pre>
 * &lt;complexType name="getStatoOggetto"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="nmAmbiente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="nmVersatore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cdKeyObject" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getStatoOggettoTipo", propOrder = {
        "nmAmbiente", "nmVersatore", "cdKeyObject" })
public class GetStatoOggetto {

    protected String nmAmbiente;
    protected String nmVersatore;
    protected String cdKeyObject;

    /**
     * Recupera il valore della proprietà nmAmbiente.
     *
     * @return possible object is {@link String }
     *
     */
    public String getNmAmbiente() {
        return nmAmbiente;
    }

    /**
     * Imposta il valore della proprietà nmAmbiente.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setNmAmbiente(String value) {
        this.nmAmbiente = value;
    }

    /**
     * Recupera il valore della proprietà nmVersatore.
     *
     * @return possible object is {@link String }
     *
     */
    public String getNmVersatore() {
        return nmVersatore;
    }

    /**
     * Imposta il valore della proprietà nmVersatore.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setNmVersatore(String value) {
        this.nmVersatore = value;
    }

    /**
     * Recupera il valore della proprietà cdKeyObject.
     *
     * @return possible object is {@link String }
     *
     */
    public String getCdKeyObject() {
        return cdKeyObject;
    }

    /**
     * Imposta il valore della proprietà cdKeyObject.
     *
     * @param value allowed object is {@link String }
     *
     */
    public void setCdKeyObject(String value) {
        this.cdKeyObject = value;
    }

}
