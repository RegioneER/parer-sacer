package it.eng.parer.elencoVersamento.utils;

import java.util.Date;

/**
 *
 * @author Quaranta_M
 */
public class ComponenteDaVerificare {

    private long idCompDoc;
    private Date dtCreazione;
    private String flCompFirmato;

    public ComponenteDaVerificare(long idCompDoc, java.util.Date dtCreazione, String flCompFirmato) {
        this.idCompDoc = idCompDoc;
        this.dtCreazione = dtCreazione;
        this.flCompFirmato = flCompFirmato;

    }

    public long getIdCompDoc() {
        return idCompDoc;
    }

    public Date getDtCreazione() {
        return dtCreazione;
    }

    public String getFlCompFirmato() {
        return flCompFirmato;
    }

    public boolean isFlCompFirmato() {
        return flCompFirmato != null && flCompFirmato.equalsIgnoreCase("1");
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + (int) (this.idCompDoc ^ (this.idCompDoc >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ComponenteDaVerificare other = (ComponenteDaVerificare) obj;
        if (this.idCompDoc != other.idCompDoc) {
            return false;
        }
        return true;
    }

}
