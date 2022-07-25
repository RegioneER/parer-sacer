/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.recuperoTpi.ejb;

import it.eng.parer.util.XAUtil;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.recupero.dto.ComponenteRec;
import it.eng.parer.ws.recupero.dto.RecuperoExt;
import it.eng.parer.ws.utils.MessaggiWSBundle;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import javax.annotation.Resource;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xadisk.connector.outbound.XADiskConnection;
import org.xadisk.connector.outbound.XADiskConnectionFactory;

/**
 *
 * @author Fioravanti_F
 */
@Stateless(mappedName = "RecuperoCompFS")
@LocalBean
public class RecuperoCompFS {

    @Resource(mappedName = "jca/xadiskLocal")
    private XADiskConnectionFactory xadCf;
    //
    private static final Logger log = LoggerFactory.getLogger(RecuperoCompFS.class);
    //
    private final static int BUFFERSIZE = 10 * 1024 * 1024; // 10 megabyte

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public RispostaControlli eliminaFileTempRecuperoTPI(RecuperoExt recupero) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrLong(-1);
        rispostaControlli.setrBoolean(false);

        XADiskConnection xadConn = null;
        String testDir;

        try {
            xadConn = xadCf.getConnection();
            // verifica l'esistenza della cartella versatore, in modo non bloccante
            testDir = MessageFormat.format("{0}/{1}/{2}", recupero.getTpiRootTpiDaSacer(), recupero.getTpiRootRecup(),
                    recupero.getSubPathVersatoreArk());
            if (XAUtil.fileExistsAndIsDirectory(xadConn, new File(testDir))) {
                // verifica presenza cartella UD (che verrà rimossa), in modo non bloccante
                testDir = MessageFormat.format("{0}/{1}/{2}/{3}", recupero.getTpiRootTpiDaSacer(),
                        recupero.getTpiRootRecup(), recupero.getSubPathVersatoreArk(),
                        recupero.getSubPathUnitaDocArk());
                if (XAUtil.fileExistsAndIsDirectory(xadConn, new File(testDir))) {
                    // rimozione ricorsiva di cartella e contenuto.
                    XAUtil.rimuoviFileRicorsivamente(xadConn, new File(testDir));
                }
            }
            //
            // verifica l'esistenza della cartella ListaFile, in modo non bloccante
            testDir = MessageFormat.format("{0}/{1}", recupero.getTpiRootTpiDaSacer(), recupero.getTpiListaFile());
            if (XAUtil.fileExistsAndIsDirectory(xadConn, new File(testDir))) {
                // verifica l'esistenza del file di log di retrieve del TSM, in modo non bloccante
                testDir = MessageFormat.format("{0}/{1}/{2}", recupero.getTpiRootTpiDaSacer(),
                        recupero.getTpiListaFile(), recupero.getFileLogRetrieve());
                if (XAUtil.fileExists(xadConn, new File(testDir))) {
                    // rimozione effettiva
                    XAUtil.deleteFile(xadConn, new File(testDir));
                }
            }
            //
            rispostaControlli.setrLong(1);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "RecuperoCompFS.eliminaDirectoryRecTPI: " + e.getMessage()));
            log.error("Si è verificato un errore durante le operazioni su filesystem", e);
        } finally {
            if (xadConn != null) {
                xadConn.close();
                log.info("Effettuata chiusura della connessione XADisk");
            }
        }

        return rispostaControlli;
    }

    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    public RispostaControlli recuperaFileCompSuStream(ComponenteRec componente, OutputStream outputStream,
            RecuperoExt recupero) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        byte[] buffer = new byte[BUFFERSIZE];
        String tmpFilePath;

        tmpFilePath = MessageFormat.format("{0}/{1}/{2}/{3}/{4}", recupero.getTpiRootTpiDaSacer(),
                recupero.getTpiRootRecup(), recupero.getSubPathVersatoreArk(), recupero.getSubPathUnitaDocArk(),
                componente.getNomeFileBreveTivoli());
        try (FileInputStream is = new FileInputStream(tmpFilePath)) {
            log.debug("Lettura del file " + tmpFilePath);
            int len;
            while ((len = is.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
                log.debug("letto file e scritto su stream...");
            }
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "RecuperoCompFS.recuperaFileCompSuStream: " + e.getMessage()));
            log.error("Si è verificato un errore durante le operazioni su filesystem", e);
        }

        return rispostaControlli;
    }
}
