package it.eng.parer.viewEntity;

import java.io.Serializable;
import javax.persistence.Embeddable;
import java.math.BigDecimal;
import java.util.Objects;
import javax.persistence.Column;

@Embeddable()
public class AroVLisaipudUrndacalcByudId implements Serializable {

    private BigDecimal idUnitaDoc;

    @Column(name = "ID_UNITA_DOC")
    public BigDecimal getIdUnitaDoc() {
        return idUnitaDoc;
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
        this.idUnitaDoc = idUnitaDoc;
    }

    private BigDecimal idVerIndiceAip;

    @Column(name = "ID_VER_INDICE_AIP")
    public BigDecimal getIdVerIndiceAip() {
        return idVerIndiceAip;
    }

    public void setIdVerIndiceAip(BigDecimal idVerIndiceAip) {
        this.idVerIndiceAip = idVerIndiceAip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        AroVLisaipudUrndacalcByudId that = (AroVLisaipudUrndacalcByudId) o;
        return Objects.equals(idUnitaDoc, that.idUnitaDoc) && Objects.equals(idVerIndiceAip, that.idVerIndiceAip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUnitaDoc, idVerIndiceAip);
    }
}
