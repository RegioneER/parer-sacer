/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.firma.crypto.verifica;

import java.io.IOException;
import java.io.InputStream;
import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.apache.tika.Tika;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;

/**
 *
 * @author Quaranta_M
 *
 *         Singleton per caricare deploy-time Spring e Tika
 */
@Singleton
@LocalBean
@Startup
@Lock(LockType.READ)
public class SpringTikaSingleton {

    private Tika tika;

    @PostConstruct
    protected void initSingleton() {
        tika = new Tika();
    }

    /**
     * Ottieni il mime/type dal byteArray
     *
     * @param fileBytes
     *            byteArray del file (piccolo)
     * 
     * @return null oppure mimeType del file
     * 
     * @throws IOException
     *             in caso di errore
     */
    public String detectMimeType(byte[] fileBytes) throws IOException {
        Metadata metadata = new Metadata();
        String text = null;

        try (InputStream stream = TikaInputStream.get(fileBytes, metadata)) {
            metadata.set(Metadata.RESOURCE_NAME_KEY, null);
            text = tika.detect(stream, metadata);
        }

        return text;
    }
}
