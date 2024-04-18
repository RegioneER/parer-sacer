package it.eng.parer.viewEntity;

import java.io.Serializable;
import javax.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Column;

@Embeddable()
public class ElvVLisAllUdByElencoId implements Serializable {

    private BigDecimal idElencoVers;

    @Column(name = "ID_ELENCO_VERS")
    public BigDecimal getIdElencoVers() {
        return idElencoVers;
    }

    public void setIdElencoVers(BigDecimal idElencoVers) {
        this.idElencoVers = idElencoVers;
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
        ElvVLisAllUdByElencoId that = (ElvVLisAllUdByElencoId) o;
        return Objects.equals(idElencoVers, that.idElencoVers) && Objects.equals(idUnitaDoc, that.idUnitaDoc);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idElencoVers, idUnitaDoc);
    }
}
