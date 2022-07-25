/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.ws.recupero.dto;

import it.eng.parer.job.utils.JobConstants.TipoSessioniRecupEnum;
import it.eng.parer.ws.utils.CostantiDB.TipiEntitaRecupero;
import it.eng.spagoLite.security.User;

/**
 *
 * @author Fioravanti_F
 */
public class ParametriRecupero {

    TipoSessioniRecupEnum tipoRichiedente = TipoSessioniRecupEnum.SERVIZIO;
    TipiEntitaRecupero tipoEntitaSacer = TipiEntitaRecupero.UNI_DOC;
    String descUnitaDoc = "";
    Long idUnitaDoc;
    Long idDocumento;
    Long idComponente;
    Long idTipoDoc;
    Long idRegistro;
    Long idVolume;
    String numeroUdNormalized = "";
    private User utente;

    public TipoSessioniRecupEnum getTipoRichiedente() {
        return tipoRichiedente;
    }

    public void setTipoRichiedente(TipoSessioniRecupEnum tipoRichiedente) {
        this.tipoRichiedente = tipoRichiedente;
    }

    public TipiEntitaRecupero getTipoEntitaSacer() {
        return tipoEntitaSacer;
    }

    public void setTipoEntitaSacer(TipiEntitaRecupero tipoEntitaSacer) {
        this.tipoEntitaSacer = tipoEntitaSacer;
    }

    public String getDescUnitaDoc() {
        return descUnitaDoc;
    }

    public void setDescUnitaDoc(String descUnitaDoc) {
        this.descUnitaDoc = descUnitaDoc;
    }

    public Long getIdUnitaDoc() {
        return idUnitaDoc;
    }

    public void setIdUnitaDoc(Long idUnitaDoc) {
        this.idUnitaDoc = idUnitaDoc;
    }

    public Long getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(Long idDocumento) {
        this.idDocumento = idDocumento;
    }

    public Long getIdComponente() {
        return idComponente;
    }

    public void setIdComponente(Long idComponente) {
        this.idComponente = idComponente;
    }

    public Long getIdTipoDoc() {
        return idTipoDoc;
    }

    public void setIdTipoDoc(Long idTipoDoc) {
        this.idTipoDoc = idTipoDoc;
    }

    public User getUtente() {
        return utente;
    }

    public void setUtente(User utente) {
        this.utente = utente;
    }

    public Long getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(Long idRegistro) {
        this.idRegistro = idRegistro;
    }

    public Long getIdVolume() {
        return idVolume;
    }

    public void setIdVolume(Long idVolume) {
        this.idVolume = idVolume;
    }

    public String getNumeroUdNormalized() {
        return numeroUdNormalized;
    }

    public void setNumeroUdNormalized(String numeroUdNormalized) {
        this.numeroUdNormalized = numeroUdNormalized;
    }

}
