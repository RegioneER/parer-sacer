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

package it.eng.parer.serie.utils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;

import it.eng.parer.serie.dto.CreazioneSerieBean;

/**
 *
 * @author Bonora_L
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class FutureUtils {

    // Nome parametri future asincroni serie
    public static final String PARAMETER_FUTURE_SERIE = "FutureSerieList";
    public static final String PARAMETER_FUTURE_MAP = "FutureSerieMap";

    /**
     * Aggiunge un nuovo oggetto Future alla mappa in applicationContext
     *
     * @param httpSession
     *            sessione Http
     * @param sessionId
     *            l'id della sessione in gestione
     * @param key
     *            la stringa chiave per l'oggetto Future
     * @param future
     *            l'oggetto Future
     */
    public static void putFutureInMap(HttpSession httpSession, String sessionId, String key, Future<?> future) {
        Map futureMap = FutureUtils.getFutureMap(httpSession, sessionId);
        futureMap.put(key, future);

        FutureUtils.putFutureMapInAppContext(httpSession, sessionId, futureMap);
    }

    /**
     * Aggiunge una nuova serie di oggetti Future alla mappa in applicationContext
     *
     * @param httpSession
     *            sessione Http
     * @param sessionId
     *            l'id della sessione in gestione
     * @param futures
     *            l'oggetto Future di una mappa di risultati
     */
    public static void putFuturesInMap(HttpSession httpSession, String sessionId, Future<Map<String, ?>> futures) {
        putFutureInMap(httpSession, sessionId, PARAMETER_FUTURE_MAP, futures);
    }

    /**
     * Mantiene una mappa in applicationContext contenente per ogni sessionId, una mappa degli oggetti Future per le
     * serie in creazione in corso
     *
     * @param httpSession
     *            sessione Http
     * @param sessionId
     *            l'id della sessione in gestione
     * @param futureMap
     *            la mappa da mantenere per quel sessionId
     */
    public static void putFutureMapInAppContext(HttpSession httpSession, String sessionId, Map futureMap) {
        Map<String, Map<String, Future<?>>> futureMapPerApp = (Map<String, Map<String, Future<?>>>) httpSession
                .getServletContext().getAttribute(PARAMETER_FUTURE_SERIE);
        futureMapPerApp.put(sessionId, futureMap);
        httpSession.getServletContext().setAttribute(PARAMETER_FUTURE_SERIE, futureMapPerApp);
    }

    /**
     * Ottiene dalla mappa in application context la mappa di future per la sessione richiesta
     *
     * @param httpSession
     *            sessione Http
     * @param sessionId
     *            l'id della sessione in gestione
     * 
     * @return la mappa di future per la sessione richiesta
     */
    public static Map<String, Future<?>> getFutureMap(HttpSession httpSession, String sessionId) {
        Map<String, Map<String, Future<?>>> futureMapPerApp = (Map<String, Map<String, Future<?>>>) httpSession
                .getServletContext().getAttribute(PARAMETER_FUTURE_SERIE);
        if (futureMapPerApp == null) {
            futureMapPerApp = new HashMap<>();
        }
        Map futureMap = futureMapPerApp.get(sessionId);
        if (futureMap == null) {
            futureMap = new HashMap<>();
            futureMapPerApp.put(sessionId, futureMap);
        }
        httpSession.getServletContext().setAttribute(PARAMETER_FUTURE_SERIE, futureMapPerApp);
        return futureMap;
    }

    /**
     * Metodo che gestisce la costruzione della stringa chiave per l'oggetto Future che sta per essere creato dal metodo
     * asincrono chiamato, sulla base dei parametri in input
     *
     * @param serieBean
     *            Bean contenente i parametri della chiamata asincrona
     * @param idStrut
     *            id della struttura della serie
     * @param idVerSerie
     *            id della versione della serie su cui è stato chiamato il metodo asincrono
     * 
     * @return la stringa univoca per la gestione dell'oggetto Future che verra' chiamato
     */
    public static String buildKeyFuture(CreazioneSerieBean serieBean, BigDecimal idStrut, Long idVerSerie) {
        String codiceSeriePadre = null;
        if (StringUtils.isNotBlank(serieBean.getCd_serie_padre())) {
            codiceSeriePadre = serieBean.getCd_serie_padre();
        } else if (StringUtils.isNotBlank(serieBean.getCd_serie_padre_da_creare())) {
            codiceSeriePadre = serieBean.getCd_serie_padre_da_creare();
        }
        String codiceSerie = StringUtils.isNotBlank(codiceSeriePadre) ? codiceSeriePadre + "/" + serieBean.getCd_serie()
                : serieBean.getCd_serie();
        return FutureUtils.buildKeyFuture(serieBean.getTi_creazione(), codiceSerie, serieBean.getAa_serie(), idStrut,
                idVerSerie);
    }

    /**
     * Metodo che gestisce la costruzione della stringa chiave per l'oggetto Future che sta per essere creato dal metodo
     * asincrono chiamato (tiCreazione), sulla base dei parametri in input
     *
     * @param tiCreazione
     *            tipo di chiamata asincrona eseguita
     * @param cdSerie
     *            codice della serie
     * @param aaSerie
     *            anno della serie
     * @param idStrut
     *            id della struttura della serie
     * @param idVerSerie
     *            id della versione della serie su cui è stato chiamato il metodo asincrono
     * 
     * @return la stringa univoca per la gestione dell'oggetto Future che verra' chiamato
     */
    public static String buildKeyFuture(String tiCreazione, String cdSerie, BigDecimal aaSerie, BigDecimal idStrut,
            Long idVerSerie) {
        StringBuilder key = new StringBuilder(tiCreazione);
        key.append("#").append(idStrut.toPlainString()).append("#").append(aaSerie).append("#").append(cdSerie)
                .append("#").append(String.valueOf(idVerSerie));
        return key.toString();
    }

    /**
     * Metodo che gestisce la decostruzione della stringa chiave per l'oggetto Future che sta per essere creato dal
     * metodo asincrono chiamato (tiCreazione), sulla base dei parametri in input
     *
     * @param key
     *            la stringa univoca per la gestione dell'oggetto Future che verra' chiamato
     * 
     * @return l'array che costituisce i dati della serie gestita in base alla chiave
     */
    public static String[] unbuildKeyFuture(String key) {
        int firstHash = StringUtils.indexOf(key, "#");
        int lastHash = StringUtils.lastIndexOf(key, "#");
        String tipoCreazione = StringUtils.substring(key, 0, firstHash);
        String idVerSerie = StringUtils.substring(key, lastHash + 1);

        String substring = StringUtils.substring(key, firstHash + 1);
        int aaHash = StringUtils.indexOf(substring, "#");
        String idStrut = StringUtils.substring(substring, 0, aaHash);
        int cdSerieHash = StringUtils.indexOf(substring, "#", aaHash + 1);
        String aaSerie = StringUtils.substring(substring, aaHash + 1, cdSerieHash);
        String cdSerie = StringUtils.substring(key, (firstHash + cdSerieHash + 2), lastHash);
        return new String[] { tipoCreazione, idStrut, aaSerie, cdSerie, idVerSerie };
    }
}
