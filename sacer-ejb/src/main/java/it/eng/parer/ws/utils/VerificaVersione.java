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

package it.eng.parer.ws.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Sinatti_S
 */
public class VerificaVersione {

    public static String elabWsKey(String versioniWsName) {
        return CostantiDB.ParametroAppl.VERSIONI_WS_PREFIX.concat(versioniWsName);
    }

    public static List<String> getWsVersionList(String versioniWsName, Map<String, String> mapWsVersion) {
        // key name on map
        String versioniWsKey = elabWsKey(versioniWsName);
        if (mapWsVersion == null || !mapWsVersion.containsKey(versioniWsKey)) {
            return new ArrayList<>();// empty list
        } else {
            return Arrays.asList(mapWsVersion.get(versioniWsKey).split("\\|")); // FIXME : separator on code
        }
    }

    public static String latestVersion(String versioniWsName, Map<String, String> mapWsVersion) {
        List<String> versioniWs = getWsVersionList(versioniWsName, mapWsVersion);
        if (versioniWs.isEmpty()) {
            /**
             * Di norma questo caso non dovrebbe mai verificarsi in quanto all'atto dell'inizializzazione del ws la
             * mappa contenente i valori è già stata testata @link ControlliWS.caricaVersioniWSDefault
             */
            return StringUtils.EMPTY;
        }
        Collections.sort(versioniWs, new Comparator<String>() {
            @Override
            public int compare(String v1, String v2) {
                String[] v1nodot = v1.split("\\."); // FIXME : dot sep on code
                String[] v2nodot = v2.split("\\."); // FIXME : dot sep on code
                int major1 = major(v1nodot);
                int major2 = major(v2nodot);
                if (major1 == major2) {
                    return minor(v1nodot).compareTo(minor(v2nodot));
                }
                return major1 > major2 ? 1 : -1;
            }

            private int major(String[] version) {
                return Integer.parseInt(version[0]);
            }

            private Integer minor(String[] version) {
                // right padding 0 from right (comparable digits)
                return version.length > 1 ? Integer.parseInt(StringUtils.rightPad(version[1], 4, "0")) : 0;
            }

        });

        return versioniWs.get(versioniWs.size() - 1);// the last one
    }

}
