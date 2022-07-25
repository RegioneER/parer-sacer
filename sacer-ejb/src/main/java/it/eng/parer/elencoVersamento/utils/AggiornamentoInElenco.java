package it.eng.parer.elencoVersamento.utils;

import java.math.BigDecimal;

/**
 * Aggiornamento dell'unità documentaria presente in elenco.
 *
 * @author DiLorenzo_F
 */
public class AggiornamentoInElenco {

    private long idUpdUnitaDoc;
    private BigDecimal pgUpdUnitaDoc;

    public AggiornamentoInElenco(long idUpdUnitaDoc, BigDecimal pgUpdUnitaDoc) {
        this.idUpdUnitaDoc = idUpdUnitaDoc;
        this.pgUpdUnitaDoc = pgUpdUnitaDoc;
    }

    /**
     * Id aggiornamento.
     *
     * @return id (chiave) dell'aggiornamento
     */
    public long getIdUpdUnitaDoc() {
        return idUpdUnitaDoc;
    }

    /**
     * URN dell'aggiornamento calcolatata
     *
     * @return Urn calcolata
     */
    public BigDecimal getPgUpdUnitaDoc() {
        return pgUpdUnitaDoc;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + (int) (this.idUpdUnitaDoc ^ (this.idUpdUnitaDoc >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AggiornamentoInElenco other = (AggiornamentoInElenco) obj;
        if (this.idUpdUnitaDoc != other.idUpdUnitaDoc) {
            return false;
        }
        return true;
    }

}
