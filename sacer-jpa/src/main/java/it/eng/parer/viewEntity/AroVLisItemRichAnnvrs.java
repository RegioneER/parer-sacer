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

package it.eng.parer.viewEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the ARO_V_LIS_ITEM_RICH_ANNVRS database table.
 *
 */
@Entity
@Table(name = "ARO_V_LIS_ITEM_RICH_ANNVRS")

public class AroVLisItemRichAnnvrs implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigDecimal aaFascicolo;
    private BigDecimal aaKeyUnitaDoc;
    private String cdKeyFascicolo;
    private String cdKeyUnitaDoc;
    private String cdRegistroKeyUnitaDoc;
    private String dsKeyItem;
    private String dsListaErr;
    private Date dtCreazione;
    private BigDecimal idItemRichAnnulVers;
    private BigDecimal idRichAnnulVers;
    private BigDecimal idUnitaDoc;
    private BigDecimal pgItemRichAnnulVers;
    private String tiItemRichAnnulVers;
    private String tiStatoItem;

    public AroVLisItemRichAnnvrs() {/* Hibernate */
    }

    @Column(name = "AA_FASCICOLO")
    public BigDecimal getAaFascicolo() {
        return this.aaFascicolo;
    }

    public void setAaFascicolo(BigDecimal aaFascicolo) {
        this.aaFascicolo = aaFascicolo;
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    public BigDecimal getAaKeyUnitaDoc() {
        return this.aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    @Column(name = "CD_KEY_FASCICOLO")
    public String getCdKeyFascicolo() {
        return this.cdKeyFascicolo;
    }

    public void setCdKeyFascicolo(String cdKeyFascicolo) {
        this.cdKeyFascicolo = cdKeyFascicolo;
    }

    @Column(name = "CD_KEY_UNITA_DOC")
    public String getCdKeyUnitaDoc() {
        return this.cdKeyUnitaDoc;
    }

    public void setCdKeyUnitaDoc(String cdKeyUnitaDoc) {
        this.cdKeyUnitaDoc = cdKeyUnitaDoc;
    }

    @Column(name = "CD_REGISTRO_KEY_UNITA_DOC")
    public String getCdRegistroKeyUnitaDoc() {
        return this.cdRegistroKeyUnitaDoc;
    }

    public void setCdRegistroKeyUnitaDoc(String cdRegistroKeyUnitaDoc) {
        this.cdRegistroKeyUnitaDoc = cdRegistroKeyUnitaDoc;
    }

    @Column(name = "DS_KEY_ITEM")
    public String getDsKeyItem() {
        return this.dsKeyItem;
    }

    public void setDsKeyItem(String dsKeyItem) {
        this.dsKeyItem = dsKeyItem;
    }

    @Column(name = "DS_LISTA_ERR")
    public String getDsListaErr() {
        return this.dsListaErr;
    }

    public void setDsListaErr(String dsListaErr) {
        this.dsListaErr = dsListaErr;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE")
    public Date getDtCreazione() {
        return this.dtCreazione;
    }

    public void setDtCreazione(Date dtCreazione) {
        this.dtCreazione = dtCreazione;
    }

    @Id
    @Column(name = "ID_ITEM_RICH_ANNUL_VERS")
    public BigDecimal getIdItemRichAnnulVers() {
        return this.idItemRichAnnulVers;
    }

    public void setIdItemRichAnnulVers(BigDecimal idItemRichAnnulVers) {
        this.idItemRichAnnulVers = idItemRichAnnulVers;
    }

    @Column(name = "ID_RICH_ANNUL_VERS")
    public BigDecimal getIdRichAnnulVers() {
        return this.idRichAnnulVers;
    }

    public void setIdRichAnnulVers(BigDecimal idRichAnnulVers) {
        this.idRichAnnulVers = idRichAnnulVers;
    }

    @Column(name = "ID_UNITA_DOC")
    public BigDecimal getIdUnitaDoc() {
        return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
        this.idUnitaDoc = idUnitaDoc;
    }

    @Column(name = "PG_ITEM_RICH_ANNUL_VERS")
    public BigDecimal getPgItemRichAnnulVers() {
        return this.pgItemRichAnnulVers;
    }

    public void setPgItemRichAnnulVers(BigDecimal pgItemRichAnnulVers) {
        this.pgItemRichAnnulVers = pgItemRichAnnulVers;
    }

    @Column(name = "TI_ITEM_RICH_ANNUL_VERS")
    public String getTiItemRichAnnulVers() {
        return this.tiItemRichAnnulVers;
    }

    public void setTiItemRichAnnulVers(String tiItemRichAnnulVers) {
        this.tiItemRichAnnulVers = tiItemRichAnnulVers;
    }

    @Column(name = "TI_STATO_ITEM")
    public String getTiStatoItem() {
        return this.tiStatoItem;
    }

    public void setTiStatoItem(String tiStatoItem) {
        this.tiStatoItem = tiStatoItem;
    }

}
