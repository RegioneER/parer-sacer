package it.eng.parer.elencoVersamento.utils;

/**
 * Componente presente in elenco con stato IN_ELENCO_CON_FIRME_VERIFICATE_DT_VERS.
 *
 * @author Snidero_L
 */
public class UnitaDocumentariaInElenco {

    private long idUnitaDoc;
    private boolean flSoloDocAggiunti;

    public UnitaDocumentariaInElenco(long idUnitaDoc, Boolean flSoloDocAggiunti) {
        this.idUnitaDoc = idUnitaDoc;
        this.flSoloDocAggiunti = flSoloDocAggiunti;
    }

    /**
     * Id Unità documentaria
     *
     * @return long id
     */
    public long getIdUnitaDoc() {
        return idUnitaDoc;
    }

    /**
     * Ritorna vero se e solo se l'id in elenco è stato calcolato solo a causa di documenti aggiunti.
     *
     * @return boolean true o false
     */
    public boolean isFlSoloDocAggiunti() {
        return flSoloDocAggiunti;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + (int) (this.idUnitaDoc ^ (this.idUnitaDoc >>> 32));
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
        final UnitaDocumentariaInElenco other = (UnitaDocumentariaInElenco) obj;
        if (this.idUnitaDoc != other.idUnitaDoc) {
            return false;
        }
        return true;
    }

}
