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

/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.ws.recuperoFasc.dto;

/**
 *
 * @author DiLorenzo_F
 */
public class ParametriFascParser {

    public enum TipiGestione {

        Ignorato, // default per Fascicolo
        Gestito // default per Utente Alternativo (MEV #21799 : non più default)
    }

    public enum TipiPresenzaTag {

        Opzionale, // default
        Vietato, // per ora non viene considerato
        Richiesto // TODO: verificare: per il ws di restituzione componenti
        // RecDIPComponenteTrasformatoSync
    }

    private TipiPresenzaTag presenzaUtenteAlternativo = TipiPresenzaTag.Opzionale;
    private TipiGestione gestioneUtenteAlternativo = TipiGestione.Ignorato; // MEV #21799 : è il
    // nuovo default a meno
    // che non si decida su
    // singolo endpoint di
    // attivarlo
    private TipiPresenzaTag presenzaTipoFascicolo = TipiPresenzaTag.Opzionale;
    private TipiGestione gestioneTipoFascicolo = TipiGestione.Ignorato;
    private boolean leggiAncheFascAnnullati = false;

    public TipiPresenzaTag getPresenzaUtenteAlternativo() {
        return presenzaUtenteAlternativo;
    }

    public void setPresenzaUtenteAlternativo(TipiPresenzaTag presenzaUtenteAlternativo) {
        this.presenzaUtenteAlternativo = presenzaUtenteAlternativo;
    }

    public TipiGestione getGestioneUtenteAlternativo() {
        return gestioneUtenteAlternativo;
    }

    public void setGestioneUtenteAlternativo(TipiGestione gestioneUtenteAlternativo) {
        this.gestioneUtenteAlternativo = gestioneUtenteAlternativo;
    }

    public TipiPresenzaTag getPresenzaTipoFascicolo() {
        return presenzaTipoFascicolo;
    }

    public void setPresenzaTipoFascicolo(TipiPresenzaTag presenzaTipoFascicolo) {
        this.presenzaTipoFascicolo = presenzaTipoFascicolo;
    }

    public TipiGestione getGestioneTipoFascicolo() {
        return gestioneTipoFascicolo;
    }

    public void setGestioneTipoFascicolo(TipiGestione gestioneTipoFascicolo) {
        this.gestioneTipoFascicolo = gestioneTipoFascicolo;
    }

    public boolean isLeggiAncheFascAnnullati() {
        return leggiAncheFascAnnullati;
    }

    public void setLeggiAncheFascAnnullati(boolean leggiAncheFascAnnullati) {
        this.leggiAncheFascAnnullati = leggiAncheFascAnnullati;
    }

}
