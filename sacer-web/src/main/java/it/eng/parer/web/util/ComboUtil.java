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
        m.put(it.eng.parer.entity.constraint.DecCriterioRaggr.TiValidElencoCriterio.NO_INDICE.name(),
                it.eng.parer.entity.constraint.DecCriterioRaggr.TiValidElencoCriterio.NO_INDICE.name());
        m.put(it.eng.parer.entity.constraint.DecCriterioRaggr.TiValidElencoCriterio.NO_FIRMA.name(),
                it.eng.parer.entity.constraint.DecCriterioRaggr.TiValidElencoCriterio.NO_FIRMA.name());
        return new DecodeMap(m);
    }
}
