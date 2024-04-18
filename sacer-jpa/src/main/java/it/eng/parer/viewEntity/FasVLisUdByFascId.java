package it.eng.parer.viewEntity;

import java.io.Serializable;
import javax.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Column;

@Embeddable()
public class FasVLisUdByFascId implements Serializable {

    private BigDecimal idFascicolo;

    @Column(name = "ID_FASCICOLO")
    public BigDecimal getIdFascicolo() {
        return idFascicolo;
    }

    public void setIdFascicolo(BigDecimal idFascicolo) {
        this.idFascicolo = idFascicolo;
    }

    private BigDecimal idUnitaDoc;

    @Column(name = "ID_UNITA_DOC")
    public BigDecimal getIdUnitaDoc() {
        return idUnitaDoc;
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
        this.idUnitaDoc = idUnitaDoc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        FasVLisUdByFascId that = (FasVLisUdByFascId) o;
        return Objects.equals(idFascicolo, that.idFascicolo) && Objects.equals(idUnitaDoc, that.idUnitaDoc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idFascicolo, idUnitaDoc);
    }
}
