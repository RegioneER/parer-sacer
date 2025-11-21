/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.firma.crypto.sign;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import it.eng.hsm.beans.HSMUser;
import it.eng.parer.entity.constraint.HsmSessioneFirma.TiSessioneFirma;

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
