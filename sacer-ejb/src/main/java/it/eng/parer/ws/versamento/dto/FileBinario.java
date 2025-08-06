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

package it.eng.parer.ws.versamento.dto;

import java.io.File;

/**
 *
 * @author Fioravanti_F
 */
public class FileBinario implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 3038080614562309178L;
    private String id;
    private byte[] dati;
    private boolean inMemoria;
    private File fileSuDisco;
    private long dimensione;

    public String getId() {
	return id;
    }

    public void setId(String id) {
	this.id = id;
    }

    public byte[] getDati() {
	return dati;
    }

    public void setDati(byte[] dati) {
	this.dati = dati;
    }

    /**
     * @return the inMemoria
     */
    public boolean isInMemoria() {
	return inMemoria;
    }

    /**
     * @param inMemoria the inMemoria to set
     */
    public void setInMemoria(boolean inMemoria) {
	this.inMemoria = inMemoria;
    }

    /**
     * @return the fileSuDisco
     */
    public File getFileSuDisco() {
	return fileSuDisco;
    }

    /**
     * @param fileSuDisco the fileSuDisco to set
     */
    public void setFileSuDisco(File fileSuDisco) {
	this.fileSuDisco = fileSuDisco;
    }

    /**
     * @return the dimensione
     */
    public long getDimensione() {
	return dimensione;
    }

    /**
     * @param dimensione the dimensione to set
     */
    public void setDimensione(long dimensione) {
	this.dimensione = dimensione;
    }
}
