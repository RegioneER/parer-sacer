package it.eng.tpi.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.http.NameValuePair;

public class RichiestaTpiInput extends RichiestaTpi {

    private String urlRichiesta;
    private Integer timeout;
    private List<NameValuePair> params;

    public RichiestaTpiInput(TipoRichiesta tipoRichiesta, String urlRichiesta, Integer timeout,
            NameValuePair... paramsCollection) {
        super(tipoRichiesta);
        this.urlRichiesta = urlRichiesta;
        this.timeout = timeout;
        this.params = paramsCollection != null ? Arrays.asList(paramsCollection) : new ArrayList<NameValuePair>();
    }

    public RichiestaTpiInput(TipoRichiesta tipoRichiesta, String urlRichiesta, Integer timeout,
            List<NameValuePair> params) {
        super(tipoRichiesta);
        this.urlRichiesta = urlRichiesta;
        this.timeout = timeout;
        this.params = params;
    }

    public String getUrlRichiesta() {
        return urlRichiesta;
    }

    public void setUrlRichiesta(String urlRichiesta) {
        this.urlRichiesta = urlRichiesta;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public List<NameValuePair> getParams() {
        return params;
    }

    public void setParams(List<NameValuePair> params) {
        this.params = params;
    }
}
