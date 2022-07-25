/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.restWS.util;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fioravanti_f
 */
public abstract class AbsRequestPrsr {
    private static final Logger log = LoggerFactory.getLogger(AbsRequestPrsr.class);

    /**
     * lettura dell'indirizzo IP del chiamante. Si presuppone che il load balancer o il reverse proxy impostino la
     * variabile RERFwFor tra gli header HTTP della request. Questo è l'unico sistema per recepire l'IP nel caso in cui
     * l'application server non sia esposto direttamente. NOTA: è ovvio che l'application server è esposto direttamente
     * solo sui PC di sviluppo.
     * 
     * @param request
     *            HttpServletRequest standard
     * 
     * @return paramentro letto da request
     */
    public String leggiIpVersante(HttpServletRequest request) {
        String ipVers = request.getHeader("RERFwFor");
        // cerco l'header custom della RER
        if (ipVers == null || ipVers.isEmpty()) {
            ipVers = request.getHeader("X-FORWARDED-FOR");
            // se non c'e`, uso l'header standard
        }
        if (ipVers == null || ipVers.isEmpty()) {
            ipVers = request.getRemoteAddr();
            // se non c'e` perche' la macchina e' esposta direttamente,
            // leggo l'IP fisico del chiamante
        }
        log.info("Request, indirizzo di provenienza - IP:  " + ipVers);
        return ipVers;
    }

    /*
     * Legge dalla request HTTP l'eventuale parametro RERCommonNameCer presente nell'header HTTP. QUesto parametro viene
     * valorizzato dal LBL che per i servizi sicurizzati con mutua autenticazione con certificato estrae dal certificato
     * il Common Name e lo mette nell'header.
     */
    public String leggiCertCommonName(HttpServletRequest request) {
        // cerco l'header custom della RERCommonNameCer
        return request.getHeader("RERCommonNameCert");
    }

}
