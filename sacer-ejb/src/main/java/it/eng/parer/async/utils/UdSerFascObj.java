package it.eng.parer.async.utils;

import it.eng.parer.web.util.Constants.TipoEntitaSacer;
import java.math.BigDecimal;

/**
 *
 * @author DiLorenzo_F
 */
public class UdSerFascObj {

    private BigDecimal id;
    private TipoEntitaSacer tiEntitaSacer;

    public UdSerFascObj(BigDecimal id, TipoEntitaSacer tiEntitaSacer) {
        this.id = id;
        this.tiEntitaSacer = tiEntitaSacer;
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
}
