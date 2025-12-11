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

/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package it.eng.parer.elencoVersamento.utils;

import it.eng.parer.entity.constraint.ElvUpdUdDaElabElenco.ElvUpdUdDaElabTiStatoUpdElencoVers;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author DiLorenzo_F
 */
public class PayLoad implements Serializable {

    private static final long serialVersionUID = 1L;

    private long id;

    private long idStrut;

    private String tipoEntitaSacer;

    private String stato;

    private long aaKeyUnitaDoc;

    private long dtCreazione;

    // MEV#27891
    private Long idCriterio;

    private Long idLogJob;

    private Long dtRegLogJob;
    // end MEV#27891

    // MAC#28020
    private Boolean isTheFirst;

    private Boolean isTheLast;
    // end MAC#28020

    public PayLoad() {
    }

    // Constructor UD, DOC
    public PayLoad(long id, BigDecimal idStrut, String tipoEntitaSacer, String stato,
            BigDecimal aaKeyUnitaDoc, Date dtCreazione) {
        this.id = id;
        this.idStrut = idStrut.longValue();
        this.tipoEntitaSacer = tipoEntitaSacer;
        this.stato = stato;
        this.aaKeyUnitaDoc = aaKeyUnitaDoc.longValue();
        this.dtCreazione = dtCreazione.getTime();
    }

    // Constructor UPD
    public PayLoad(long id, long idStrut, String tipoEntitaSacer,
            ElvUpdUdDaElabTiStatoUpdElencoVers stato, BigDecimal aaKeyUnitaDoc, Date dtCreazione) {
        this.id = id;
        this.idStrut = idStrut;
        this.tipoEntitaSacer = tipoEntitaSacer;
        this.stato = stato.name();
        this.aaKeyUnitaDoc = aaKeyUnitaDoc.longValue();
        this.dtCreazione = dtCreazione.getTime();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdStrut() {
        return idStrut;
    }

    public void setIdStrut(long idStrut) {
        this.idStrut = idStrut;
    }

    public String getTipoEntitaSacer() {
        return tipoEntitaSacer;
    }

    public void setTipoEntitaSacer(String tipoEntitaSacer) {
        this.tipoEntitaSacer = tipoEntitaSacer;
    }

    public String getStato() {
        return stato;
    }

    public void setStato(String stato) {
        this.stato = stato;
    }

    public long getAaKeyUnitaDoc() {
        return aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(long aaKeyUnitaDoc) {
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    public long getDtCreazione() {
        return dtCreazione;
    }

    public void setDtCreazione(long dtCreazione) {
        this.dtCreazione = dtCreazione;
    }

    // MEV#27891
    public Long getIdCriterio() {
        return idCriterio;
    }

    public void setIdCriterio(Long idCriterio) {
        this.idCriterio = idCriterio;
    }

    public Long getIdLogJob() {
        return idLogJob;
    }

    public void setIdLogJob(Long idLogJob) {
        this.idLogJob = idLogJob;
    }

    public Long getDtRegLogJob() {
        return dtRegLogJob;
    }

    public void setDtRegLogJob(Long dtRegLogJob) {
        this.dtRegLogJob = dtRegLogJob;
    }
    // end MEV#27891

    // MAC#28020
    public Boolean isIsTheFirst() {
        return isTheFirst;
    }

    public void setIsTheFirst(Boolean isTheFirst) {
        this.isTheFirst = isTheFirst;
    }

    public Boolean isIsTheLast() {
        return isTheLast;
    }

    public void setIsTheLast(Boolean isTheLast) {
        this.isTheLast = isTheLast;
    }
    // end MAC#28020

}
