package it.eng.parer.elencoVersamento.utils;

import it.eng.parer.web.util.Constants.TipoEntitaSacer;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Agati_D modified by Gilioli_P
 */
public class DocUdObj {

    private BigDecimal id;
    private TipoEntitaSacer tiEntitaSacer;
    private BigDecimal aaKeyUnitaDoc;
    private Date dtCreazione;

    public DocUdObj(BigDecimal id, TipoEntitaSacer tiEntitaSacer, BigDecimal aaKeyUnitadoc, Date dtCreazione) {
        this.id = id;
        this.tiEntitaSacer = tiEntitaSacer;
        this.aaKeyUnitaDoc = aaKeyUnitadoc;
        this.dtCreazione = dtCreazione;
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

    public BigDecimal getAaKeyUnitaDoc() {
        return aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    public Date getDtCreazione() {
        return dtCreazione;
    }

    public void setDtCreazione(Date dtCreazione) {
        this.dtCreazione = dtCreazione;
    }
}
