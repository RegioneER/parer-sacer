package it.eng.parer.web.ejb;

import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Startup;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gilioli_P
 *
 *         Cache utilizzata per caricare la lista dei codici di errore da file di properties nei filtri di
 *         Monitoraggio/Riepilogo Versamenti/Versamenti Falliti. La classe singlettona, da buon single e asociale, viene
 *         caricata all'avvio dell'applicazione
 */
@Singleton
@LocalBean
@Startup
@Lock(LockType.READ)
public class CaricaErrori {

    private static Logger logger = LoggerFactory.getLogger(CaricaErrori.class.getName());
    DecodeMap mappaClasseErrore = new DecodeMap();
    // Creo le 3 mappe ordinate che conterranno i vari livelli di codifica errore
    SortedMap<String, String> classeErroreMap = new TreeMap();
    SortedMap<String, String> sottoClasseErroreMap = new TreeMap();
    SortedMap<String, String> codiceErroreMap = new TreeMap();

    @PostConstruct
    protected void initSingleton() {
        BaseTable tabellaClasse = new BaseTable();
        Properties props = new Properties();
        InputStream tmpStream = null;
        try {
            tmpStream = this.getClass().getClassLoader().getResourceAsStream("/descrizione_errori.properties");
            props.load(tmpStream);
            Enumeration enumeratore = props.keys();
            while (enumeratore.hasMoreElements()) {
                Object obj = enumeratore.nextElement();
                String[] items = ((String) obj).split("-");
                // Piazzo il codice nella mappa di competenza a seconda dei casi
                if (items.length == 1) {
                    classeErroreMap.put(items[0], items[0] + " - " + (String) props.get(obj));
                } else if (items.length == 2) {
                    String sottoClasse = items[0] + "-" + items[1];
                    sottoClasseErroreMap.put(sottoClasse, sottoClasse + " - " + (String) props.get(obj));
                } else if (items.length == 3) {
                    codiceErroreMap.put((String) obj, (String) obj + " - " + (String) props.get(obj));
                }
            }
            // Inizializzo la lista della classe errore che verrà subito caricata
            Iterator iteratore = classeErroreMap.keySet().iterator();
            while (iteratore.hasNext()) {
                String codice = (String) iteratore.next();
                BaseRow riga = new BaseRow();
                riga.setString("cd_err", codice);
                riga.setString("ds_err", (String) classeErroreMap.get(codice));
                tabellaClasse.add(riga);
            }
        } catch (IOException e) {
            logger.error("Errore nel recupero della lista errori:" + e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(tmpStream);
        }
        mappaClasseErrore.populatedMap(tabellaClasse, "cd_err", "ds_err");
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void setLogger(Logger logger) {
        CaricaErrori.logger = logger;
    }

    public DecodeMap getMappaClasseErrore() {
        return mappaClasseErrore;
    }

    public void setMappaClasseErrore(DecodeMap mappaClasseErrore) {
        this.mappaClasseErrore = mappaClasseErrore;
    }

    public SortedMap<String, String> getClasseErroreMap() {
        return classeErroreMap;
    }

    public void setClasseErroreMap(SortedMap<String, String> classeErroreMap) {
        this.classeErroreMap = classeErroreMap;
    }

    public SortedMap<String, String> getSottoClasseErroreMap() {
        return sottoClasseErroreMap;
    }

    public void setSottoClasseErroreMap(SortedMap<String, String> sottoClasseErroreMap) {
        this.sottoClasseErroreMap = sottoClasseErroreMap;
    }

    public SortedMap<String, String> getCodiceErroreMap() {
        return codiceErroreMap;
    }

    public void setCodiceErroreMap(SortedMap<String, String> codiceErroreMap) {
        this.codiceErroreMap = codiceErroreMap;
    }

    public DecodeMap filtraSottoclasse(String classe) {
        BaseTable tabellaSottoClasse = new BaseTable();
        DecodeMap mappaSottoClasseErrore = new DecodeMap();
        Iterator iteratore = sottoClasseErroreMap.keySet().iterator();
        while (iteratore.hasNext()) {
            String codice = (String) iteratore.next();
            String[] items = codice.split("-");
            if (items[0].equals(classe) && items.length == 2) {
                BaseRow riga = new BaseRow();
                riga.setString("cd_err", codice);
                riga.setString("ds_err", (String) sottoClasseErroreMap.get(codice));
                tabellaSottoClasse.add(riga);
            }
        }
        mappaSottoClasseErrore.populatedMap(tabellaSottoClasse, "cd_err", "ds_err");
        return mappaSottoClasseErrore;
    }

    public DecodeMap filtraCodice(String sottoClasse) {
        BaseTable tabellaCodice = new BaseTable();
        DecodeMap mappaCodiceErrore = new DecodeMap();
        Iterator iteratore = codiceErroreMap.keySet().iterator();
        while (iteratore.hasNext()) {
            String codice = (String) iteratore.next();
            String[] items = codice.split("-");
            String sc = "";

            if (items.length == 1) {
                sc = items[0];
            } else {
                sc = items[0] + "-" + items[1];
            }

            if (sc.equals(sottoClasse) && items.length == 3) {
                BaseRow riga = new BaseRow();
                riga.setString("cd_err", codice);
                riga.setString("ds_err", (String) codiceErroreMap.get(codice));
                tabellaCodice.add(riga);
            }
        }
        mappaCodiceErrore.populatedMap(tabellaCodice, "cd_err", "ds_err");
        return mappaCodiceErrore;
    }
}
