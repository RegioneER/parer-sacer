package it.eng.parer.volume.utils;

import java.util.List;

/**
 *
 * @author Agati_D
 */
public class ReturnParams {

    private StringBuilder query;

    public StringBuilder getQuery() {
        return query;
    }

    public void setQuery(StringBuilder query) {
        this.query = query;
    }

    public List<DatiSpecQueryParams> getMappone() {
        return mappone;
    }

    public void setMappone(List<DatiSpecQueryParams> mappone) {
        this.mappone = mappone;
    }

    private List<DatiSpecQueryParams> mappone;
}