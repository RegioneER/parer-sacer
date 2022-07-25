package it.eng.parer.serie.dto;

import java.util.Date;
import java.util.Objects;

public class IntervalliSerieAutomBean {

    private Date dtInizioSerie;
    private Date dtFineSerie;
    private String cdSerie;
    private String dsSerie;

    public IntervalliSerieAutomBean(Date dtInizioSerie, Date dtFineSerie, String cdSerie, String dsSerie) {
        this.dtInizioSerie = dtInizioSerie;
        this.dtFineSerie = dtFineSerie;
        this.cdSerie = cdSerie;
        this.dsSerie = dsSerie;
    }

    public Date getDtInizioSerie() {
        return dtInizioSerie;
    }

    public void setDtInizioSerie(Date dtInizioSerie) {
        this.dtInizioSerie = dtInizioSerie;
    }

    public Date getDtFineSerie() {
        return dtFineSerie;
    }

    public void setDtFineSerie(Date dtFineSerie) {
        this.dtFineSerie = dtFineSerie;
    }

    public String getCdSerie() {
        return cdSerie;
    }

    public void setCdSerie(String cdSerie) {
        this.cdSerie = cdSerie;
    }

    public String getDsSerie() {
        return dsSerie;
    }

    public void setDsSerie(String dsSerie) {
        this.dsSerie = dsSerie;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.dtInizioSerie);
        hash = 41 * hash + Objects.hashCode(this.dtFineSerie);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IntervalliSerieAutomBean other = (IntervalliSerieAutomBean) obj;

        if (other.dtInizioSerie == null || this.dtInizioSerie == null
                || this.dtInizioSerie.getTime() != other.dtInizioSerie.getTime()) {
            return false;
        }
        if (other.dtFineSerie == null || this.dtFineSerie == null
                || this.dtFineSerie.getTime() != other.dtFineSerie.getTime()) {
            return false;
        }
        return true;
    }

}
