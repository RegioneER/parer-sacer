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

/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.firma.crypto.verifica;

import static org.apache.tika.metadata.TikaCoreProperties.RESOURCE_NAME_KEY;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;

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

    private Detector tika;

    @PostConstruct
    protected void initSingleton() {
        tika = TikaConfig.getDefaultConfig().getDetector();
    }

    /**
     * Ottieni il mime/type dal byteArray
     *
     * @param fileBytes byteArray del file (piccolo)
     *
     * @return null oppure mimeType del file
     *
     * @throws IOException in caso di errore
     */
    public String detectMimeType(byte[] fileBytes) throws IOException {
        String text = null;

        try (InputStream stream = TikaInputStream.get(fileBytes)) {
            Metadata metadata = new Metadata();
            metadata.set(RESOURCE_NAME_KEY, null);
            MediaType mediaType = tika.detect(stream, metadata);
            text = mediaType.toString();
        }

        return text;
    }
}
