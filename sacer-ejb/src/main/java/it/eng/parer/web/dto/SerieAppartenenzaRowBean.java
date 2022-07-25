package it.eng.parer.web.dto;

import it.eng.spagoLite.db.base.row.BaseRow;
import java.math.BigDecimal;

/**
 *
 * @author gilioli_p
 */
public class SerieAppartenenzaRowBean extends BaseRow {

    public BigDecimal getIdVerSerie() {
        return getBigDecimal("id_ver_serie");
    }

    public void setIdVerSerie(BigDecimal idVerSerie) {
        setObject("id_ver_serie", idVerSerie);
    }

    public String getCdCompositoSerie() {
        return getString("cd_composito_serie");
    }

    public void setCdCompositoSerie(String cdCompositoSerie) {
        setObject("cd_composito_serie", cdCompositoSerie);
    }

    public BigDecimal getAaSerie() {
        return getBigDecimal("aa_serie");
    }

    public void setAaSerie(BigDecimal aaSerie) {
        setObject("aa_serie", aaSerie);
    }

    public String getDsSerie() {
        return getString("ds_serie");
    }

    public void setDsSerie(String dsSerie) {
        setObject("ds_serie", dsSerie);
    }

    public String getCdVerSerie() {
        return getString("cd_ver_serie");
    }

    public void setCdVerSerie(String cdVerSerie) {
        setObject("cd_ver_serie", cdVerSerie);
    }

    public String getTiStatoVerSerie() {
        return getString("ti_stato_ver_serie");
    }

    public void setTiStatoVerSerie(String tiStatoVerSerie) {
        setObject("ti_stato_ver_serie", tiStatoVerSerie);
    }

    public String getTiStatoSerie() {
        return getString("ti_stato_serie");
    }

    public void setTiStatoSerie(String tiStatoSerie) {
        setObject("ti_stato_serie", tiStatoSerie);
    }

}
