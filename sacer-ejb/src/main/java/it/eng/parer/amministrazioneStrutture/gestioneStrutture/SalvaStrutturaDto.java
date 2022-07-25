/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.amministrazioneStrutture.gestioneStrutture;

import java.util.Date;

/**
 *
 * @author Iacolucci_M
 */
public class SalvaStrutturaDto {
    private boolean checkIncludiCriteri;
    private boolean checkIncludiElementiDisattivi;
    private boolean checkIncludiFormati;
    private boolean checkIncludiTipiFascicolo;
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

    public Date getDataAttuale() {
        return dataAttuale;
    }

    public void setDataAttuale(Date dataAttuale) {
        this.dataAttuale = dataAttuale;
    }

}
