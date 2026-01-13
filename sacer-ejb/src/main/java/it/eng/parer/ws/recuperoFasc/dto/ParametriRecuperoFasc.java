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
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.ws.recuperoFasc.dto;

import it.eng.parer.job.utils.JobConstants.TipoSessioniRecupEnum;
import it.eng.parer.ws.utils.CostantiDB.TipiEntitaRecupero;
import it.eng.spagoLite.security.User;

/**
 *
 * @author DiLorenzo_F
 */
public class ParametriRecuperoFasc {

    TipoSessioniRecupEnum tipoRichiedente = TipoSessioniRecupEnum.SERVIZIO;
    TipiEntitaRecupero tipoEntitaSacer = TipiEntitaRecupero.FASCICOLO;
    String descFascicolo = "";
    Long idFascicolo;
    String numeroFascNormalized = "";
    private User utente;
    Long idTipoFascicolo;

    public TipoSessioniRecupEnum getTipoRichiedente() {
        return tipoRichiedente;
    }

    public void setTipoRichiedente(TipoSessioniRecupEnum tipoRichiedente) {
        this.tipoRichiedente = tipoRichiedente;
    }

    public TipiEntitaRecupero getTipoEntitaSacer() {
        return tipoEntitaSacer;
    }

    public void setTipoEntitaSacer(TipiEntitaRecupero tipoEntitaSacer) {
        this.tipoEntitaSacer = tipoEntitaSacer;
    }

    public String getDescFascicolo() {
        return descFascicolo;
    }

    public void setDescFascicolo(String descFascicolo) {
        this.descFascicolo = descFascicolo;
    }

    public Long getIdFascicolo() {
        return idFascicolo;
    }

    public void setIdFascicolo(Long idFascicolo) {
        this.idFascicolo = idFascicolo;
    }

    public User getUtente() {
        return utente;
    }

    public void setUtente(User utente) {
        this.utente = utente;
    }

    public String getNumeroFascNormalized() {
        return numeroFascNormalized;
    }

    public void setNumeroFascNormalized(String numeroFascNormalized) {
        this.numeroFascNormalized = numeroFascNormalized;
    }

    public Long getIdTipoFascicolo() {
        return idTipoFascicolo;
    }

    public void setIdTipoFascicolo(Long idTipoFascicolo) {
        this.idTipoFascicolo = idTipoFascicolo;
    }

}
