package it.eng.parer.firma.crypto.sign;

import it.eng.hsm.beans.HSMUser;
import it.eng.parer.entity.constraint.HsmSessioneFirma.TiSessioneFirma;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Moretti_Lu
 *
 */
public class SigningRequest {
    /**
     * Sacer user
     */
    private long idUtente;

    /**
     * The credentials to require a signature on the HSM
     */
    private HSMUser userHSM;

    /**
     * The type of the document to sign
     */
    private TiSessioneFirma type;

    /**
     * The list of the document to sign
     */
    private List<BigDecimal> files;

    public SigningRequest(long idUtente) {
        this.idUtente = idUtente;
        this.files = new ArrayList<BigDecimal>();
    }

    public long getIdUtente() {
        return this.idUtente;
    }

    public HSMUser getUserHSM() {
        return this.userHSM;
    }

    public void setUserHSM(HSMUser userHSM) {
        this.userHSM = userHSM;
    }

    public TiSessioneFirma getType() {
        return this.type;
    }

    public void setType(TiSessioneFirma type) {
        this.type = type;
    }

    public List<BigDecimal> getFiles() {
        return this.files;
    }

    public void addFile(BigDecimal fileId) {
        this.files.add(fileId);
    }

    protected void setFiles(List<BigDecimal> files) {
        this.files = files;
    }
}