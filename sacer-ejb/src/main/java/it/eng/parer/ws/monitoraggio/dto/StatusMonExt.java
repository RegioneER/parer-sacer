/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.monitoraggio.dto;

import it.eng.parer.ws.dto.IWSDesc;
import it.eng.parer.ws.dto.RispostaControlli;
import it.eng.parer.ws.utils.Costanti;
import it.eng.spagoLite.security.User;
import java.util.EnumSet;

/**
 *
 * @author fioravanti_f
 */
public class StatusMonExt implements IStatusMonExt {

    private static final long serialVersionUID = 1L;
    private User utente;
    private String loginName;
    private IWSDesc descrizione;

    public User getUtente() {
        return utente;
    }

    public void setUtente(User utente) {
        this.utente = utente;
    }

    @Override
    public String getDatiXml() {
        throw new UnsupportedOperationException("Metodo non implementato");
    }

    @Override
    public void setDatiXml(String datiXml) {
        throw new UnsupportedOperationException("Metodo non implementato");
    }

    @Override
    public IWSDesc getDescrizione() {
        return descrizione;
    }

    @Override
    public void setDescrizione(IWSDesc descrizione) {
        this.descrizione = descrizione;
    }

    @Override
    public String getLoginName() {
        return loginName;
    }

    @Override
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    @Override
    public String getVersioneWsChiamata() {
        throw new UnsupportedOperationException("Metodo non implementato");
    }

    @Override
    public void setVersioneWsChiamata(String versioneWsChiamata) {
        throw new UnsupportedOperationException("Metodo non implementato");
    }

    @Override
    public RispostaControlli checkVersioneRequest(String versione) {
        throw new UnsupportedOperationException("Metodo non implementato");
    }

    @Override
    public String getVersioneCalc() {
        throw new UnsupportedOperationException("Metodo non implementato");
    }

    @Override
    public EnumSet<Costanti.ModificatoriWS> getModificatoriWSCalc() {
        throw new UnsupportedOperationException("Metodo non implementato");
    }

}
