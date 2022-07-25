package it.eng.parer.volume.ejb;

import it.eng.parer.entity.VolVolumeConserv;
import it.eng.parer.volume.helper.VolumeHelper;
import java.text.SimpleDateFormat;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Agati_D
 */
@Stateless
@LocalBean
@Interceptors({ it.eng.parer.aop.TransactionInterceptor.class })
public class VolumeEjb {

    Logger log = LoggerFactory.getLogger(VolumeEjb.class);
    @EJB
    private VolumeHelper volumeHelper;
    SimpleDateFormat dateformat = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS");
    // Inserire la riga sotto nel file di property
    String BATCH_USERNAME = "JOB_CREAZIONE_VOLUMI";

    public VolumeEjb() {
    }

    public byte[] retrieveFileByIdVolume(long idVolume, String fileType) {
        VolVolumeConserv volume = volumeHelper.retrieveVolumeById(idVolume);
        return volumeHelper.retrieveFile(volume, fileType);
    }
}