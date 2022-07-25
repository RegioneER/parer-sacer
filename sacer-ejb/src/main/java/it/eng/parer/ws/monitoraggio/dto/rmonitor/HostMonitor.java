/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.monitoraggio.dto.rmonitor;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/**
 *
 * @author fioravanti_f
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HostMonitor {

    private String versione;
    private String codiceErrore;
    private String descrErrore;
    private List<MonitorJob> job;
    private List<MonitorAltro> altri;

    public String getVersione() {
        return versione;
    }

    public void setVersione(String versione) {
        this.versione = versione;
    }

    public String getCodiceErrore() {
        return codiceErrore;
    }

    public void setCodiceErrore(String codiceErrore) {
        this.codiceErrore = codiceErrore;
    }

    public String getDescrErrore() {
        return descrErrore;
    }

    public void setDescrErrore(String descrErrore) {
        this.descrErrore = descrErrore;
    }

    public List<MonitorJob> getJob() {
        return job;
    }

    public void setJob(List<MonitorJob> job) {
        this.job = job;
    }

    public List<MonitorAltro> getAltri() {
        return altri;
    }

    public void setAltri(List<MonitorAltro> altri) {
        this.altri = altri;
    }

}
