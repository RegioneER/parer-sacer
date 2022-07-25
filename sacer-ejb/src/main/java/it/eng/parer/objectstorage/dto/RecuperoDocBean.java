/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.objectstorage.dto;

import java.io.OutputStream;

import com.amazonaws.services.s3.model.S3Object;

import it.eng.parer.web.util.Constants.TiEntitaSacerObjectStorage;
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
    // s3
    private transient S3Object s3Object;

    public RecuperoDocBean(TiEntitaSacerObjectStorage tipo, long id, OutputStream os,
            TabellaBlob tabellaBlobDaLeggere) {
        super();
        this.tipo = tipo;
        this.id = id;
        this.os = os;
        this.tabellaBlobDaLeggere = tabellaBlobDaLeggere;
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

    public S3Object getS3Object() {
        return s3Object;
    }

    public void setS3Object(S3Object s3Object) {
        this.s3Object = s3Object;
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