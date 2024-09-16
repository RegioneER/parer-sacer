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

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

/**
 * This object contains factory methods for each Java content interface and Java element interface generated in the
 * pippo package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the Java representation for XML content.
 * The Java representation of XML content can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory methods for each of these are provided in
 * this class.
 *
 */
@XmlRegistry
public class ObjectFactory {

    public static final String HTTP_WS_SACERASI = "http://ws.sacerasi.eng.it/";
    private static final QName _RecuperoStatoOggetto_QNAME = new QName(HTTP_WS_SACERASI, "RecuperoStatoOggetto");
    private static final QName _GetStatoOggetto_QNAME = new QName(HTTP_WS_SACERASI, "getStatoOggetto");
    private static final QName _GetStatoOggettoResponse_QNAME = new QName(HTTP_WS_SACERASI, "getStatoOggettoResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: pippo
     *
     */
    public ObjectFactory() {
        // override default
    }

    /**
     * Create an instance of {@link GetStatoOggetto }
     *
     * @return un oggetto {@link GetStatoOggetto }
     *
     */
    public GetStatoOggetto createGetStatoOggetto() {
        return new GetStatoOggetto();
    }

    /**
     * Create an instance of {@link GetStatoOggettoResponse }
     *
     * @return un oggetto {@link GetStatoOggettoResponse }
     */
    public GetStatoOggettoResponse createGetStatoOggettoResponse() {
        return new GetStatoOggettoResponse();
    }

    /**
     * Create an instance of {@link RecuperoStatoOggettoRisposta }
     *
     * @return un oggetto {@link RecuperoStatoOggettoRisposta }
     */
    public RecuperoStatoOggettoRisposta createRecuperoStatoOggettoRisposta() {
        return new RecuperoStatoOggettoRisposta();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link RecuperoStatoOggettoRisposta }{@code >}
     *
     * @param value
     *            {@link RecuperoStatoOggettoRisposta}
     *
     * @return {@link JAXBElement }{@code <}{@link RecuperoStatoOggettoRisposta}{@code >}
     */
    @XmlElementDecl(namespace = HTTP_WS_SACERASI, name = "RecuperoStatoOggetto")
    public JAXBElement<RecuperoStatoOggettoRisposta> createRecuperoStatoOggetto(RecuperoStatoOggettoRisposta value) {
        return new JAXBElement<>(_RecuperoStatoOggetto_QNAME, RecuperoStatoOggettoRisposta.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetStatoOggetto }{@code >}
     *
     * @param value
     *            {@link GetStatoOggetto}
     *
     * @return {@link JAXBElement }{@code <}{@link GetStatoOggetto }{@code >}
     */
    @XmlElementDecl(namespace = HTTP_WS_SACERASI, name = "getStatoOggetto")
    public JAXBElement<GetStatoOggetto> createGetStatoOggetto(GetStatoOggetto value) {
        return new JAXBElement<>(_GetStatoOggetto_QNAME, GetStatoOggetto.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetStatoOggettoResponse }{@code >}
     *
     * @param value
     *            {@link GetStatoOggettoResponse}
     *
     * @return {@link JAXBElement }{@code <}{@link GetStatoOggettoResponse}{@code >}
     */
    @XmlElementDecl(namespace = HTTP_WS_SACERASI, name = "getStatoOggettoResponse1")
    public JAXBElement<GetStatoOggettoResponse> createGetStatoOggettoResponse(GetStatoOggettoResponse value) {
        return new JAXBElement<>(_GetStatoOggettoResponse_QNAME, GetStatoOggettoResponse.class, null, value);
    }

}
