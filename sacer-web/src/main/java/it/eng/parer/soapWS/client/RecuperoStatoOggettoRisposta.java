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
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Classe Java per recuperoStatoOggettoRisposta complex type.
 *
 * <p>
 * Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 *
 * <pre>
 * &lt;complexType name="recuperoStatoOggettoRisposta"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cdEsito" type="{http://ws.sacerasi.eng.it/}esitoServizio" minOccurs="0"/&gt;
 *         &lt;element name="cdErr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="dlErr" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="nmAmbiente" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="nmVersatore" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="cdKeyObject" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="statoOggetto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="descrizioneStatoOggetto" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "recuperoStatoOggettoRisposta", propOrder = { "cdEsito", "cdErr", "dlErr", "nmAmbiente", "nmVersatore",
        "cdKeyObject", "statoOggetto", "descrizioneStatoOggetto" })
public class RecuperoStatoOggettoRisposta {

    protected EsitoServizio cdEsito;
    protected String cdErr;
    protected String dlErr;
    protected String nmAmbiente;
    protected String nmVersatore;
    protected String cdKeyObject;
    protected String statoOggetto;
    protected String descrizioneStatoOggetto;

    /**
     * Recupera il valore della proprietà cdEsito.
     *
     * @return possible object is {@link EsitoServizio }
     *
     */
    public EsitoServizio getCdEsito() {
        return cdEsito;
    }

    /**
     * Imposta il valore della proprietà cdEsito.
     *
     * @param value
     *            allowed object is {@link EsitoServizio }
     *
     */
    public void setCdEsito(EsitoServizio value) {
        this.cdEsito = value;
    }

    /**
     * Recupera il valore della proprietà cdErr.
     *
     * @return possible object is {@link String }
     *
     */
    public String getCdErr() {
        return cdErr;
    }

    /**
     * Imposta il valore della proprietà cdErr.
     *
     * @param value
     *            allowed object is {@link String }
     *
     */
    public void setCdErr(String value) {
        this.cdErr = value;
    }

    /**
     * Recupera il valore della proprietà dlErr.
     *
     * @return possible object is {@link String }
     *
     */
    public String getDlErr() {
        return dlErr;
    }

    /**
     * Imposta il valore della proprietà dlErr.
     *
     * @param value
     *            allowed object is {@link String }
     *
     */
    public void setDlErr(String value) {
        this.dlErr = value;
    }

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
     * @param value
     *            allowed object is {@link String }
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
     * @param value
     *            allowed object is {@link String }
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
     * @param value
     *            allowed object is {@link String }
     *
     */
    public void setCdKeyObject(String value) {
        this.cdKeyObject = value;
    }

    /**
     * Recupera il valore della proprietà statoOggetto.
     *
     * @return possible object is {@link String }
     *
     */
    public String getStatoOggetto() {
        return statoOggetto;
    }

    /**
     * Imposta il valore della proprietà statoOggetto.
     *
     * @param value
     *            allowed object is {@link String }
     *
     */
    public void setStatoOggetto(String value) {
        this.statoOggetto = value;
    }

    /**
     * Recupera il valore della proprietà descrizioneStatoOggetto.
     *
     * @return possible object is {@link String }
     *
     */
    public String getDescrizioneStatoOggetto() {
        return descrizioneStatoOggetto;
    }

    /**
     * Imposta il valore della proprietà descrizioneStatoOggetto.
     *
     * @param value
     *            allowed object is {@link String }
     *
     */
    public void setDescrizioneStatoOggetto(String value) {
        this.descrizioneStatoOggetto = value;
    }

}
