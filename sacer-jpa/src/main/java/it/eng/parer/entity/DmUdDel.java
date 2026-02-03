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

package it.eng.parer.entity;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * The persistent class for the DM_UD_DEL database table.
 *
 */
@Entity
@Table(name = "DM_UD_DEL")
@NamedQuery(name = "DmUdDel.findAll", query = "SELECT d FROM DmUdDel d")
public class DmUdDel implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idUnitaDoc;
    private BigDecimal aaKeyUnitaDoc;
    private String cdKeyUnitaDoc;
    private String cdRegistroKeyUnitaDoc;
    private Date dtStatoUdCancellate;
    private Date dtVersamento;
    private BigDecimal idEnte;
    private BigDecimal idStrut;
    private String nmEnte;
    private String nmStrut;
    private String tiStatoUdCancellate;
    private String flAnnul;
    private DmUdDelRichieste dmUdDelRichieste;

    public DmUdDel() {
        /* Hibernate */
    }

    @Id
    @Column(name = "ID_UNITA_DOC")
    public Long getIdUnitaDoc() {
        return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(Long idUnitaDoc) {
        this.idUnitaDoc = idUnitaDoc;
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

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_STATO_UD_CANCELLATE")
    public Date getDtStatoUdCancellate() {
        return this.dtStatoUdCancellate;
    }

    public void setDtStatoUdCancellate(Date dtStatoUdCancellate) {
        this.dtStatoUdCancellate = dtStatoUdCancellate;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_VERSAMENTO")
    public Date getDtVersamento() {
        return this.dtVersamento;
    }

    public void setDtVersamento(Date dtVersamento) {
        this.dtVersamento = dtVersamento;
    }

    @Column(name = "ID_ENTE")
    public BigDecimal getIdEnte() {
        return this.idEnte;
    }

    public void setIdEnte(BigDecimal idEnte) {
        this.idEnte = idEnte;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    @Column(name = "NM_ENTE")
    public String getNmEnte() {
        return this.nmEnte;
    }

    public void setNmEnte(String nmEnte) {
        this.nmEnte = nmEnte;
    }

    @Column(name = "NM_STRUT")
    public String getNmStrut() {
        return this.nmStrut;
    }

    public void setNmStrut(String nmStrut) {
        this.nmStrut = nmStrut;
    }

    @Column(name = "TI_STATO_UD_CANCELLATE")
    public String getTiStatoUdCancellate() {
        return this.tiStatoUdCancellate;
    }

    public void setTiStatoUdCancellate(String tiStatoUdCancellate) {
        this.tiStatoUdCancellate = tiStatoUdCancellate;
    }

    @Column(name = "FL_ANNUL", columnDefinition = "char(1)")
    public String getFlAnnul() {
        return this.flAnnul;
    }

    public void setFlAnnul(String flAnnul) {
        this.flAnnul = flAnnul;
    }

    // bi-directional many-to-one association to DmUdDelRichieste
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UD_DEL_RICHIESTA")
    public DmUdDelRichieste getDmUdDelRichieste() {
        return this.dmUdDelRichieste;
    }

    public void setDmUdDelRichieste(DmUdDelRichieste dmUdDelRichieste) {
        this.dmUdDelRichieste = dmUdDelRichieste;
    }

}
