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
 * The persistent class for the DM_UD_DEL_CONTA database table.
 *
 */
@Entity
@Table(name = "DM_UD_DEL_CONTA")
@NamedQuery(name = "DmUdDelConta.findAll", query = "SELECT d FROM DmUdDelConta d")
public class DmUdDelConta implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long idUdDelConta;
    private Long idUnitaDoc;
    private Date dtRifConta;
    private BigDecimal idStrut;
    private Long idSubStrut;
    private BigDecimal aaKeyUnitaDoc;
    private Long idRegistroUnitaDoc;
    private Long idTipoUnitaDoc;
    private Long idTipoDocPrinc;

    // Metriche inizializzate a zero per sicurezza nei calcoli
    private BigDecimal niUnitaDocVers = BigDecimal.ZERO;
    private BigDecimal niDocVers = BigDecimal.ZERO;
    private BigDecimal niCompVers = BigDecimal.ZERO;
    private BigDecimal niSizeVers = BigDecimal.ZERO;

    private BigDecimal niDocAgg = BigDecimal.ZERO;
    private BigDecimal niCompAgg = BigDecimal.ZERO;
    private BigDecimal niSizeAgg = BigDecimal.ZERO;

    private BigDecimal niUnitaDocAnnul = BigDecimal.ZERO;
    private BigDecimal niDocAnnulUd = BigDecimal.ZERO;
    private BigDecimal niCompAnnulUd = BigDecimal.ZERO;
    private BigDecimal niSizeAnnulUd = BigDecimal.ZERO;

    public DmUdDelConta() {
        /* Hibernate */
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_UD_DEL_CONTA")
    public Long getIdUdDelConta() {
        return this.idUdDelConta;
    }

    public void setIdUdDelConta(Long idUdDelConta) {
        this.idUdDelConta = idUdDelConta;
    }

    @Column(name = "ID_UNITA_DOC")
    public Long getIdUnitaDoc() {
        return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(Long idUnitaDoc) {
        this.idUnitaDoc = idUnitaDoc;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_RIF_CONTA")
    public Date getDtRifConta() {
        return this.dtRifConta;
    }

    public void setDtRifConta(Date dtRifConta) {
        this.dtRifConta = dtRifConta;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    @Column(name = "ID_SUB_STRUT")
    public Long getIdSubStrut() {
        return this.idSubStrut;
    }

    public void setIdSubStrut(Long idSubStrut) {
        this.idSubStrut = idSubStrut;
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    public BigDecimal getAaKeyUnitaDoc() {
        return this.aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    @Column(name = "ID_REGISTRO_UNITA_DOC")
    public Long getIdRegistroUnitaDoc() {
        return this.idRegistroUnitaDoc;
    }

    public void setIdRegistroUnitaDoc(Long idRegistroUnitaDoc) {
        this.idRegistroUnitaDoc = idRegistroUnitaDoc;
    }

    @Column(name = "ID_TIPO_UNITA_DOC")
    public Long getIdTipoUnitaDoc() {
        return this.idTipoUnitaDoc;
    }

    public void setIdTipoUnitaDoc(Long idTipoUnitaDoc) {
        this.idTipoUnitaDoc = idTipoUnitaDoc;
    }

    @Column(name = "ID_TIPO_DOC_PRINC")
    public Long getIdTipoDocPrinc() {
        return this.idTipoDocPrinc;
    }

    public void setIdTipoDocPrinc(Long idTipoDocPrinc) {
        this.idTipoDocPrinc = idTipoDocPrinc;
    }

    @Column(name = "NI_UNITA_DOC_VERS")
    public BigDecimal getNiUnitaDocVers() {
        return this.niUnitaDocVers;
    }

    public void setNiUnitaDocVers(BigDecimal niUnitaDocVers) {
        this.niUnitaDocVers = niUnitaDocVers;
    }

    @Column(name = "NI_DOC_VERS")
    public BigDecimal getNiDocVers() {
        return this.niDocVers;
    }

    public void setNiDocVers(BigDecimal niDocVers) {
        this.niDocVers = niDocVers;
    }

    @Column(name = "NI_COMP_VERS")
    public BigDecimal getNiCompVers() {
        return this.niCompVers;
    }

    public void setNiCompVers(BigDecimal niCompVers) {
        this.niCompVers = niCompVers;
    }

    @Column(name = "NI_SIZE_VERS")
    public BigDecimal getNiSizeVers() {
        return this.niSizeVers;
    }

    public void setNiSizeVers(BigDecimal niSizeVers) {
        this.niSizeVers = niSizeVers;
    }

    @Column(name = "NI_DOC_AGG")
    public BigDecimal getNiDocAgg() {
        return this.niDocAgg;
    }

    public void setNiDocAgg(BigDecimal niDocAgg) {
        this.niDocAgg = niDocAgg;
    }

    @Column(name = "NI_COMP_AGG")
    public BigDecimal getNiCompAgg() {
        return this.niCompAgg;
    }

    public void setNiCompAgg(BigDecimal niCompAgg) {
        this.niCompAgg = niCompAgg;
    }

    @Column(name = "NI_SIZE_AGG")
    public BigDecimal getNiSizeAgg() {
        return this.niSizeAgg;
    }

    public void setNiSizeAgg(BigDecimal niSizeAgg) {
        this.niSizeAgg = niSizeAgg;
    }

    @Column(name = "NI_UNITA_DOC_ANNUL")
    public BigDecimal getNiUnitaDocAnnul() {
        return this.niUnitaDocAnnul;
    }

    public void setNiUnitaDocAnnul(BigDecimal niUnitaDocAnnul) {
        this.niUnitaDocAnnul = niUnitaDocAnnul;
    }

    @Column(name = "NI_DOC_ANNUL_UD")
    public BigDecimal getNiDocAnnulUd() {
        return this.niDocAnnulUd;
    }

    public void setNiDocAnnulUd(BigDecimal niDocAnnulUd) {
        this.niDocAnnulUd = niDocAnnulUd;
    }

    @Column(name = "NI_COMP_ANNUL_UD")
    public BigDecimal getNiCompAnnulUd() {
        return this.niCompAnnulUd;
    }

    public void setNiCompAnnulUd(BigDecimal niCompAnnulUd) {
        this.niCompAnnulUd = niCompAnnulUd;
    }

    @Column(name = "NI_SIZE_ANNUL_UD")
    public BigDecimal getNiSizeAnnulUd() {
        return this.niSizeAnnulUd;
    }

    public void setNiSizeAnnulUd(BigDecimal niSizeAnnulUd) {
        this.niSizeAnnulUd = niSizeAnnulUd;
    }
}