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

package it.eng.parer.web.ejb;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.apache.commons.io.IOUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.parer.entity.DecClasseErrSacer;
import it.eng.parer.entity.DecErrSacer;
import it.eng.parer.entity.DecErrSacerDett;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;

/**
 *
 * @author Gilioli_P
 *
 *         Cache centralizzata dei messaggi di errore applicativi. Espone sia la gerarchia usata dai
 *         filtri di monitoraggio sia il recupero dei messaggi testuali utilizzati da WS e job.
 */
@Singleton
@LocalBean
@Startup
@Lock(LockType.READ)
public class CaricaErrori {

    private static Logger logger = LoggerFactory.getLogger(CaricaErrori.class);

    @PersistenceContext(unitName = "ParerJPA")
    private EntityManager entityManager;

    DecodeMap mappaClasseErrore = new DecodeMap();
    // Creo le 3 mappe ordinate che conterranno i vari livelli di codifica errore
    SortedMap<String, String> classeErroreMap = new TreeMap<>();
    SortedMap<String, String> sottoClasseErroreMap = new TreeMap<>();
    SortedMap<String, String> codiceErroreMap = new TreeMap<>();
    Map<String, SortedMap<String, String>> sottoClassiPerClasseMap = new HashMap<>();
    Map<String, SortedMap<String, String>> codiciPerClasseSottoClasseMap = new HashMap<>();
    Map<String, String> errorMap = new HashMap<>();

    @PostConstruct
    protected void initSingleton() {
        BaseTable tabellaClasse = new BaseTable();
        try {
            classeErroreMap.clear();
            sottoClasseErroreMap.clear();
            codiceErroreMap.clear();
            sottoClassiPerClasseMap.clear();
            codiciPerClasseSottoClasseMap.clear();
            errorMap.clear();

            TypedQuery<DecErrSacer> query = entityManager.createQuery(
                    "SELECT err FROM DecErrSacer err " + "JOIN FETCH err.decClasseErrSacer classe "
                            + "LEFT JOIN FETCH err.decErrSacerDett dett "
                            + "ORDER BY classe.cdClasseErrSacer, dett.cdSottoclasse, err.cdErr",
                    DecErrSacer.class);
            List<DecErrSacer> errori = query.getResultList();

            for (DecErrSacer errore : errori) {
                String codiceErrore = errore.getCdErr();
                if (codiceErrore == null || codiceErrore.isEmpty()) {
                    continue;
                }
                String descrizioneErrore = decodeMessage(errore.getDsErr());
                errorMap.put(codiceErrore, descrizioneErrore);

                DecClasseErrSacer classe = errore.getDecClasseErrSacer();
                String codiceClasse = classe != null ? classe.getCdClasseErrSacer() : null;
                String descrizioneClasse = classe != null
                        ? decodeMessage(classe.getDsClasseErrSacer())
                        : null;
                if (codiceClasse != null && !codiceClasse.isEmpty()
                        && !classeErroreMap.containsKey(codiceClasse)) {
                    String descrizione = descrizioneClasse != null && !descrizioneClasse.isEmpty()
                            ? descrizioneClasse
                            : descrizioneErrore;
                    classeErroreMap.put(codiceClasse, codiceClasse + " - " + descrizione);
                }

                DecErrSacerDett dettaglio = errore.getDecErrSacerDett();
                String codiceSottoclasse = dettaglio != null ? dettaglio.getCdSottoclasse() : null;
                if (codiceSottoclasse != null && !codiceSottoclasse.isEmpty()) {
                    sottoClasseErroreMap.putIfAbsent(codiceSottoclasse, codiceSottoclasse);
                    if (codiceClasse != null && !codiceClasse.isEmpty()) {
                        sottoClassiPerClasseMap
                                .computeIfAbsent(codiceClasse, key -> new TreeMap<>())
                                .putIfAbsent(codiceSottoclasse, codiceSottoclasse);
                    }
                }

                String[] items = codiceErrore.split("-");
                if (items.length >= 3) {
                    codiceErroreMap.put(codiceErrore, codiceErrore + " - " + descrizioneErrore);
                    if (codiceClasse != null && !codiceClasse.isEmpty() && codiceSottoclasse != null
                            && !codiceSottoclasse.isEmpty()) {
                        codiciPerClasseSottoClasseMap
                                .computeIfAbsent(buildClasseSottoclasseKey(codiceClasse,
                                        codiceSottoclasse), key -> new TreeMap<>())
                                .put(codiceErrore, codiceErrore + " - " + descrizioneErrore);
                    }
                }
            }

            loadPropertyMessages("/messaggi_ejb.properties");

            // Inizializzo la lista della classe errore che verrà subito caricata
            Iterator<String> iteratore = classeErroreMap.keySet().iterator();
            while (iteratore.hasNext()) {
                String codice = (String) iteratore.next();
                BaseRow riga = new BaseRow();
                riga.setString("cd_err", codice);
                riga.setString("ds_err", (String) classeErroreMap.get(codice));
                tabellaClasse.add(riga);
            }
        } catch (Exception e) {
            logger.error("Errore nel recupero della lista errori dal database: " + e.getMessage(),
                    e);
        }
        mappaClasseErrore.populatedMap(tabellaClasse, "cd_err", "ds_err");
    }

    private void loadPropertyMessages(String resourcePath) {
        Properties props = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = getClass().getResourceAsStream(resourcePath);
            if (inputStream == null) {
                return;
            }
            props.load(inputStream);
            for (String key : props.stringPropertyNames()) {
                errorMap.putIfAbsent(key, decodeMessage(props.getProperty(key)));
            }
        } catch (IOException e) {
            logger.error("Errore nel caricamento del file {}: {}", resourcePath, e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private String decodeMessage(String value) {
        return value != null ? StringEscapeUtils.unescapeJava(value) : null;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String getString(String key) {
        return errorMap.get(key);
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String getMessage(String key) {
        return getString(key);
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String getString(String key, Object... params) {
        String message = errorMap.get(key);
        return message != null ? MessageFormat.format(message, cleanTextContent(params)) : null;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String getCompleteMessage(String key) {
        return key + " - " + getString(key) + ";";
    }

    private Object[] cleanTextContent(Object... params) {
        List<Object> cleanedTxtParams = new ArrayList<>();
        Stream.of(params).filter(Objects::nonNull).forEach(param -> {
            String sparam = param.toString();
            sparam = sparam.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", "?");
            sparam = sparam.replaceAll("\\p{C}", "?");
            sparam = StringEscapeUtils.unescapeXml(sparam);
            cleanedTxtParams.add(sparam);
        });
        return cleanedTxtParams.toArray(new Object[0]);
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
        SortedMap<String, String> sottoClassi = sottoClassiPerClasseMap.get(classe);
        if (sottoClassi == null) {
            return mappaSottoClasseErrore;
        }
        Iterator<String> iteratore = sottoClassi.keySet().iterator();
        while (iteratore.hasNext()) {
            String codice = (String) iteratore.next();
            BaseRow riga = new BaseRow();
            riga.setString("cd_err", codice);
            riga.setString("ds_err", sottoClassi.get(codice));
            tabellaSottoClasse.add(riga);
        }
        mappaSottoClasseErrore.populatedMap(tabellaSottoClasse, "cd_err", "ds_err");
        return mappaSottoClasseErrore;
    }

    public DecodeMap filtraCodice(String classe, String sottoClasse) {
        BaseTable tabellaCodice = new BaseTable();
        DecodeMap mappaCodiceErrore = new DecodeMap();
        SortedMap<String, String> codici = codiciPerClasseSottoClasseMap
                .get(buildClasseSottoclasseKey(classe, sottoClasse));
        if (codici == null) {
            return mappaCodiceErrore;
        }
        Iterator<String> iteratore = codici.keySet().iterator();
        while (iteratore.hasNext()) {
            String codice = iteratore.next();
            BaseRow riga = new BaseRow();
            riga.setString("cd_err", codice);
            riga.setString("ds_err", codici.get(codice));
            tabellaCodice.add(riga);
        }
        mappaCodiceErrore.populatedMap(tabellaCodice, "cd_err", "ds_err");
        return mappaCodiceErrore;
    }

    public DecodeMap filtraCodice(String sottoClasse) {
        BaseTable tabellaCodice = new BaseTable();
        DecodeMap mappaCodiceErrore = new DecodeMap();
        Iterator<String> iteratore = codiceErroreMap.keySet().iterator();
        while (iteratore.hasNext()) {
            String codice = iteratore.next();
            String[] items = codice.split("-");
            if (items.length >= 2 && (items[0] + "-" + items[1]).equals(sottoClasse)) {
                BaseRow riga = new BaseRow();
                riga.setString("cd_err", codice);
                riga.setString("ds_err", codiceErroreMap.get(codice));
                tabellaCodice.add(riga);
            }
        }
        mappaCodiceErrore.populatedMap(tabellaCodice, "cd_err", "ds_err");
        return mappaCodiceErrore;
    }

    private String buildClasseSottoclasseKey(String classe, String sottoClasse) {
        return classe + "|" + sottoClasse;
    }
}
