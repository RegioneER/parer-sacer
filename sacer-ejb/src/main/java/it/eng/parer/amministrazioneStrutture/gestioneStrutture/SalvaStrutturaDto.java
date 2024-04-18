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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.amministrazioneStrutture.gestioneStrutture;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Iacolucci_M
 */
public class SalvaStrutturaDto implements Serializable {
    private boolean checkIncludiCriteri;
    private boolean checkIncludiElementiDisattivi;
    private boolean checkIncludiFormati;
    private boolean checkIncludiTipiFascicolo;
    private boolean checkIncludiSistemiMigraz;
    private boolean checkMantieniDateFineValidita;
    private Date dataAttuale;

    public boolean isCheckIncludiCriteri() {
        return checkIncludiCriteri;
    }

    public void setCheckIncludiCriteri(boolean checkIncludiCriteri) {
        this.checkIncludiCriteri = checkIncludiCriteri;
    }

    public boolean isCheckIncludiElementiDisattivi() {
        return checkIncludiElementiDisattivi;
    }

    public void setCheckIncludiElementiDisattivi(boolean checkIncludiElementiDisattivi) {
        this.checkIncludiElementiDisattivi = checkIncludiElementiDisattivi;
    }

    public boolean isCheckIncludiFormati() {
        return checkIncludiFormati;
    }

    public void setCheckIncludiFormati(boolean checkIncludiFormati) {
        this.checkIncludiFormati = checkIncludiFormati;
    }

    public boolean isCheckIncludiTipiFascicolo() {
        return checkIncludiTipiFascicolo;
    }

    public void setCheckIncludiTipiFascicolo(boolean checkIncludiTipiFascicolo) {
        this.checkIncludiTipiFascicolo = checkIncludiTipiFascicolo;
    }

    public boolean isCheckMantieniDateFineValidita() {
        return checkMantieniDateFineValidita;
    }

    public void setCheckMantieniDateFineValidita(boolean checkMantieniDateFineValidita) {
        this.checkMantieniDateFineValidita = checkMantieniDateFineValidita;
    }

    public boolean isCheckIncludiSistemiMigraz() {
        return checkIncludiSistemiMigraz;
    }

    public void setCheckIncludiSistemiMigraz(boolean checkIncludiSistemiMigraz) {
        this.checkIncludiSistemiMigraz = checkIncludiSistemiMigraz;
    }

    public Date getDataAttuale() {
        return dataAttuale;
    }

    public void setDataAttuale(Date dataAttuale) {
        this.dataAttuale = dataAttuale;
    }

}
