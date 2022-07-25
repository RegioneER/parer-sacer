package it.eng.parer.serie.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SerieAutomBean {

    private BigDecimal niMesiCreazioneSerie;
    private BigDecimal niGiorniCalcolo;
    private int numeroSerieDaCreare;
    private String tipoIntervallo;
    private int numMesiIntervallo;
    private final List<IntervalliSerieAutomBean> intervalli;

    public SerieAutomBean(BigDecimal niMesiCreazioneSerie) {
        this.niMesiCreazioneSerie = niMesiCreazioneSerie;
        this.niGiorniCalcolo = null;
        this.numeroSerieDaCreare = 1;
        this.numMesiIntervallo = 1;
        this.tipoIntervallo = null;
        this.intervalli = new ArrayList<>();
    }

    public BigDecimal getNiMesiCreazioneSerie() {
        return niMesiCreazioneSerie;
    }

    public void setNiMesiCreazioneSerie(BigDecimal niMesiCreazioneSerie) {
        this.niMesiCreazioneSerie = niMesiCreazioneSerie;
    }

    public BigDecimal getNiGiorniCalcolo() {
        return niGiorniCalcolo;
    }

    public void setNiGiorniCalcolo(BigDecimal niGiorniCalcolo) {
        this.niGiorniCalcolo = niGiorniCalcolo;
    }

    public int getNumeroSerieDaCreare() {
        return numeroSerieDaCreare;
    }

    public void setNumeroSerieDaCreare(int numeroSerieDaCreare) {
        this.numeroSerieDaCreare = numeroSerieDaCreare;
    }

    public String getTipoIntervallo() {
        return tipoIntervallo;
    }

    public void setTipoIntervallo(String tipoIntervallo) {
        this.tipoIntervallo = tipoIntervallo;
    }

    public int getNumMesiIntervallo() {
        return numMesiIntervallo;
    }

    public void setNumMesiIntervallo(int numMesiIntervallo) {
        this.numMesiIntervallo = numMesiIntervallo;
    }

    public List<IntervalliSerieAutomBean> getIntervalli() {
        return intervalli;
    }

}
