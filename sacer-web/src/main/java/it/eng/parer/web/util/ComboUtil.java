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

package it.eng.parer.web.util;

import it.eng.spagoLite.db.decodemap.DecodeMapIF;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author S257421
 */
public class ComboUtil {

    public static final DecodeMapIF getTipiValidazioneElencoSenzaFirma() {
        // MEV#31945 - Eliminare validazione elenco UD con firma
        Map m = new HashMap();
        m.put(it.eng.parer.entity.constraint.ElvElencoVer.TiValidElenco.NO_INDICE.name(),
                it.eng.parer.entity.constraint.ElvElencoVer.TiValidElenco.NO_INDICE.name());
        m.put(it.eng.parer.entity.constraint.ElvElencoVer.TiValidElenco.NO_FIRMA.name(),
                it.eng.parer.entity.constraint.ElvElencoVer.TiValidElenco.NO_FIRMA.name());
        return new DecodeMap(m);
    }

    public static final DecodeMapIF getTipiValidazioneCriteriRaggruppamentoSenzaFirma() {
        // MEV#31945 - Eliminare validazione elenco UD con firma
        Map m = new HashMap();
        m.put(it.eng.parer.entity.constraint.DecCriterioRaggr.TiValidElencoCriterio.NO_INDICE
                .name(),
                it.eng.parer.entity.constraint.DecCriterioRaggr.TiValidElencoCriterio.NO_INDICE
                        .name());
        m.put(it.eng.parer.entity.constraint.DecCriterioRaggr.TiValidElencoCriterio.NO_FIRMA.name(),
                it.eng.parer.entity.constraint.DecCriterioRaggr.TiValidElencoCriterio.NO_FIRMA
                        .name());
        return new DecodeMap(m);
    }
}
