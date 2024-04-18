/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.objectstorage.dto;

import java.io.OutputStream;

import it.eng.parer.web.util.Constants.TiEntitaSacerObjectStorage;
import it.eng.parer.ws.recupero.ejb.oracleClb.RecClbOracle.TabellaClob;
import it.eng.parer.ws.recupero.ejb.oracleBlb.RecBlbOracle.TabellaBlob;

/**
 *
 * @author Sinatti_S
 */
public class RecuperoDocBean implements java.io.Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 2246844643029244989L;

    private long id;
    private TiEntitaSacerObjectStorage tipo;
    //
    private transient OutputStream os;
    private TabellaBlob tabellaBlobDaLeggere;
    // MEV#30395
    private TabellaClob tabellaClobDaLeggere;
    // end MEV#30395

    public RecuperoDocBean(TiEntitaSacerObjectStorage tipo, long id, OutputStream os,
            TabellaBlob tabellaBlobDaLeggere) {
        super();
        this.tipo = tipo;
        this.id = id;
        this.os = os;
        this.tabellaBlobDaLeggere = tabellaBlobDaLeggere;
    }

    public RecuperoDocBean(TiEntitaSacerObjectStorage tipo, long id, OutputStream os,
            TabellaClob tabellaClobDaLeggere) {
        super();
        this.tipo = tipo;
        this.id = id;
        this.os = os;
        this.tabellaClobDaLeggere = tabellaClobDaLeggere;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the tipo
     */
    public TiEntitaSacerObjectStorage getTipo() {
        return tipo;
    }

    /**
     * @param tipo
     *            the tipo to set
     */
    public void setTipo(TiEntitaSacerObjectStorage tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the os
     */
    public OutputStream getOs() {
        return os;
    }

    /**
     * @param os
     *            the os to set
     */
    public void setOs(OutputStream os) {
        this.os = os;
    }

    /**
     * @return the tabellaBlobDaLeggere
     */
    public TabellaBlob getTabellaBlobDaLeggere() {
        return tabellaBlobDaLeggere;
    }

    /**
     * @param tabellaBlobDaLeggere
     *            the tabellaBlobDaLeggere to set
     */
    public void setTabellaBlobDaLeggere(TabellaBlob tabellaBlobDaLeggere) {
        this.tabellaBlobDaLeggere = tabellaBlobDaLeggere;
    }

    /**
     * @return the tabellaClobDaLeggere
     */
    public TabellaClob getTabellaClobDaLeggere() {
        return tabellaClobDaLeggere;
    }

    /**
     * @param tabellaClobDaLeggere
     *            the tabellaClobDaLeggere to set
     */
    public void setTabellaClobDaLeggere(TabellaClob tabellaClobDaLeggere) {
        this.tabellaClobDaLeggere = tabellaClobDaLeggere;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "id=" + id + ", tipo=" + tipo + ", tabellaBlobDaLeggere=" + tabellaBlobDaLeggere;
    }

}
