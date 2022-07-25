package it.eng.parer.elencoVersamento.utils;

/**
 * Componente dell'unità documentaria presente in elenco.
 *
 * @author Snidero_L
 */
public class ComponenteInElenco {

    private long idCompDoc;

    public ComponenteInElenco(long idCompDoc) {
        this.idCompDoc = idCompDoc;
    }

    /**
     * Id componente.
     *
     * @return id (chiave) del componente
     */
    public long getIdCompDoc() {
        return idCompDoc;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + (int) (this.idCompDoc ^ (this.idCompDoc >>> 32));
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
        final ComponenteInElenco other = (ComponenteInElenco) obj;
        if (this.idCompDoc != other.idCompDoc) {
            return false;
        }
        return true;
    }
}
