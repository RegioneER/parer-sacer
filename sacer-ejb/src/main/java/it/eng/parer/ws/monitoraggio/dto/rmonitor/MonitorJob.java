/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.monitoraggio.dto.rmonitor;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Date;

/**
 *
 * @author fioravanti_f
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MonitorJob {

    public enum StatiTimer {
        ON, OFF
    }

    public enum StatiJob {
        CHIUSA_OK, CHIUSA_ERR, IN_CORSO
    }

    public enum Allarmi {
        NESSUN_ALLARME, ERRORE_SCHEDULAZIONE
    }

    private String nomeJob;
    private StatiTimer statoTimer;
    private StatiJob statoJob;
    private Allarmi allarmiDaUltimaChiamata;
    private Date tSUltimaAttivita;

    public String getNomeJob() {
        return nomeJob;
    }

    public void setNomeJob(String nomeJob) {
        this.nomeJob = nomeJob;
    }

    public StatiTimer getStatoTimer() {
        return statoTimer;
    }

    public void setStatoTimer(StatiTimer statoTimer) {
        this.statoTimer = statoTimer;
    }

    public StatiJob getStatoJob() {
        return statoJob;
    }

    public void setStatoJob(StatiJob statoJob) {
        this.statoJob = statoJob;
    }

    public Allarmi getAllarmiDaUltimaChiamata() {
        return allarmiDaUltimaChiamata;
    }

    public void setAllarmiDaUltimaChiamata(Allarmi allarmiDaUltimaChiamata) {
        this.allarmiDaUltimaChiamata = allarmiDaUltimaChiamata;
    }

    // NOTA: "yyyy-MM-dd'T'HH:mm:ss.SSSXXX" questo è il formato RFC3339, analogo al più celebre ISO8601,
    // compreso di frazioni di secondo e timezone
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "CET")
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "GMT")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "CET")
    public Date gettSUltimaAttivita() {
        return tSUltimaAttivita;
    }

    public void settSUltimaAttivita(Date tSUltimaAttivita) {
        this.tSUltimaAttivita = tSUltimaAttivita;
    }

}
