/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.web.util;

/**
 *
 * @author Gilioli_P
 */
public class BlobObject {
    public long id;
    public String name;
    public byte[] blobbo;

    public BlobObject(long id, byte[] blobbo) {
        this.id = id;
        this.blobbo = blobbo;
    }

    public BlobObject(long id, String name, byte[] blobbo) {
        this.id = id;
        this.name = name;
        this.blobbo = blobbo;
    }
}
