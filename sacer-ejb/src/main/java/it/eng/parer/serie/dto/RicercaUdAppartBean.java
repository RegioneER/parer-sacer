package it.eng.parer.serie.dto;

import it.eng.parer.slite.gen.form.SerieUDForm;
import it.eng.spagoCore.error.EMFError;
import java.math.BigDecimal;
import java.util.Date;

public class RicercaUdAppartBean {

    String cdUdSerie;
    Date dtUdSerieDa;
    Date dtUdSerieA;
    String infoUdSerie;
    BigDecimal pgUdSerieDa;
    BigDecimal pgUdSerieA;
    String tiStatoConservazione;

    public RicercaUdAppartBean() {
    }

    public RicercaUdAppartBean(SerieUDForm.FiltriContenutoSerieDetail filtri) throws EMFError {
        this.cdUdSerie = filtri.getCd_ud_serie().parse();
        this.dtUdSerieDa = filtri.getDt_ud_serie_da().parse();
        this.dtUdSerieA = filtri.getDt_ud_serie_a().parse();
        this.infoUdSerie = filtri.getInfo_ud_serie().parse();
        this.pgUdSerieDa = filtri.getPg_ud_serie_da().parse();
        this.pgUdSerieA = filtri.getPg_ud_serie_a().parse();
        this.tiStatoConservazione = filtri.getTi_stato_conservazione().parse();
    }

    public String getCdUdSerie() {
        return cdUdSerie;
    }

    public void setCdUdSerie(String cdUdSerie) {
        this.cdUdSerie = cdUdSerie;
    }

    public Date getDtUdSerieDa() {
        return dtUdSerieDa;
    }

    public void setDtUdSerieDa(Date dtUdSerieDa) {
        this.dtUdSerieDa = dtUdSerieDa;
    }

    public Date getDtUdSerieA() {
        return dtUdSerieA;
    }

    public void setDtUdSerieA(Date dtUdSerieA) {
        this.dtUdSerieA = dtUdSerieA;
    }

    public String getInfoUdSerie() {
        return infoUdSerie;
    }

    public void setInfoUdSerie(String infoUdSerie) {
        this.infoUdSerie = infoUdSerie;
    }

    public BigDecimal getPgUdSerieDa() {
        return pgUdSerieDa;
    }

    public void setPgUdSerieDa(BigDecimal pgUdSerieDa) {
        this.pgUdSerieDa = pgUdSerieDa;
    }

    public BigDecimal getPgUdSerieA() {
        return pgUdSerieA;
    }

    public void setPgUdSerieA(BigDecimal pgUdSerieA) {
        this.pgUdSerieA = pgUdSerieA;
    }

    public String getTiStatoConservazione() {
        return tiStatoConservazione;
    }

    public void setTiStatoConservazione(String tiStatoConservazione) {
        this.tiStatoConservazione = tiStatoConservazione;
    }

}
