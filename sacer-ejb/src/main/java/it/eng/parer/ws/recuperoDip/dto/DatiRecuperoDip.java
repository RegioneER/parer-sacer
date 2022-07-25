/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.recuperoDip.dto;

import java.util.LinkedHashMap;
import java.util.List;

/**
 *
 * @author Fioravanti_F
 */
public class DatiRecuperoDip {

    private long numeroElementiTrovati;
    private LinkedHashMap<Long, CompRecDip> elementiTrovati;

    public long getNumeroElementiTrovati() {
        return numeroElementiTrovati;
    }

    public void setNumeroElementiTrovati(long numeroElementiTrovati) {
        this.numeroElementiTrovati = numeroElementiTrovati;
    }

    public LinkedHashMap<Long, CompRecDip> getElementiTrovati() {
        return elementiTrovati;
    }

    public void setElementiTrovati(LinkedHashMap<Long, CompRecDip> elementiTrovati) {
        this.elementiTrovati = elementiTrovati;
    }

}
