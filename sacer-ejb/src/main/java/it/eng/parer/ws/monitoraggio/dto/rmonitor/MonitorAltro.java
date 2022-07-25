/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.monitoraggio.dto.rmonitor;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author fioravanti_f
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MonitorAltro {

    private String nome;
    private String stato;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

}
