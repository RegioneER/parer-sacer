package it.eng.parer.elencoVersFascicoli.utils;

import it.eng.parer.web.util.Constants.TipoEntitaSacer;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author DiLorenzo_F
 */
public class FasFascicoloObj {

    private BigDecimal id;
    private TipoEntitaSacer tiEntitaSacer;
    private BigDecimal aaFascicolo;
    private Date tsVersFascicolo;

    public FasFascicoloObj(BigDecimal id, TipoEntitaSacer tiEntitaSacer, BigDecimal aaFascicolo, Date tsVersFascicolo) {
        this.id = id;
        this.tiEntitaSacer = tiEntitaSacer;
        this.aaFascicolo = aaFascicolo;
        this.tsVersFascicolo = tsVersFascicolo;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public TipoEntitaSacer getTiEntitaSacer() {
        return tiEntitaSacer;
    }

    public void setTiEntitaSacer(TipoEntitaSacer tiEntitaSacer) {
        this.tiEntitaSacer = tiEntitaSacer;
    }

    public BigDecimal getAaFascicolo() {
        return aaFascicolo;
    }

    public void setAaFascicolo(BigDecimal aaFascicolo) {
        this.aaFascicolo = aaFascicolo;
    }

    public Date getTsVersFascicolo() {
        return tsVersFascicolo;
    }

    public void setTsVersFascicolo(Date tsVersFascicolo) {
        this.tsVersFascicolo = tsVersFascicolo;
    }
}
