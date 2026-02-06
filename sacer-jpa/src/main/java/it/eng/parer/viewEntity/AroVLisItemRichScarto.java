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
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * The persistent class for the ARO_V_LIS_ITEM_RICH_SCARTO database table.
 *
 */
@Entity
@Table(name = "ARO_V_LIS_ITEM_RICH_SCARTO")
@NamedQuery(name = "AroVLisItemRichScarto.findAll", query = "SELECT a FROM AroVLisItemRichScarto a")
public class AroVLisItemRichScarto implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigDecimal aaKeyUnitaDoc;
    private String cdKeyUnitaDoc;
    private String cdRegistroKeyUnitaDoc;
    private String dsKeyItem;
    private String dsListaErr;
    private Date dtCreazione;
    private BigDecimal idItemRichScartoVers;
    private BigDecimal idRichScartoVers;
    private BigDecimal idUnitaDoc;
    private BigDecimal pgItemRichScartoVers;
    private String tiStatoItemScarto;

    public AroVLisItemRichScarto() {
        /* Hibernate */
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    public BigDecimal getAaKeyUnitaDoc() {
        return this.aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
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
    @Column(name = "ID_ITEM_RICH_SCARTO_VERS")
    public BigDecimal getIdItemRichScartoVers() {
        return this.idItemRichScartoVers;
    }

    public void setIdItemRichScartoVers(BigDecimal idItemRichScartoVers) {
        this.idItemRichScartoVers = idItemRichScartoVers;
    }

    @Column(name = "ID_RICH_SCARTO_VERS")
    public BigDecimal getIdRichScartoVers() {
        return this.idRichScartoVers;
    }

    public void setIdRichScartoVers(BigDecimal idRichScartoVers) {
        this.idRichScartoVers = idRichScartoVers;
    }

    @Column(name = "ID_UNITA_DOC")
    public BigDecimal getIdUnitaDoc() {
        return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
        this.idUnitaDoc = idUnitaDoc;
    }

    @Column(name = "PG_ITEM_RICH_SCARTO_VERS")
    public BigDecimal getPgItemRichScartoVers() {
        return this.pgItemRichScartoVers;
    }

    public void setPgItemRichScartoVers(BigDecimal pgItemRichScartoVers) {
        this.pgItemRichScartoVers = pgItemRichScartoVers;
    }

    @Column(name = "TI_STATO_ITEM_SCARTO")
    public String getTiStatoItemScarto() {
        return this.tiStatoItemScarto;
    }

    public void setTiStatoItemScarto(String tiStatoItemScarto) {
        this.tiStatoItemScarto = tiStatoItemScarto;
    }

}