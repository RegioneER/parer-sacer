/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package it.eng.parer.viewEntity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the ARO_V_LISAIPUD_SISTEMA_MIGRAZ database table.
 * 
 */
@Entity
@Table(name = "ARO_V_LISAIPUD_SISTEMA_MIGRAZ")
public class AroVLisaipudSistemaMigraz implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigDecimal aaKeyUnitaDocAip;
    private String cdKeyUnitaDocAip;
    private String cdRegistroKeyUnitaDocAip;
    private String dsLinkUnitaDoc;
    private BigDecimal idAmbiente;
    private BigDecimal idEnte;
    private BigDecimal idLinkUnitaDoc;
    private BigDecimal idStrut;
    private BigDecimal idUnitaDoc;
    private BigDecimal idUnitaDocAip;
    private String nmAmbiente;
    private String nmEnte;
    private String nmSistemaMigraz;
    private String nmStrut;

    public AroVLisaipudSistemaMigraz() {/* Hibernate */
    }

    @Column(name = "AA_KEY_UNITA_DOC_AIP")
    public BigDecimal getAaKeyUnitaDocAip() {
        return this.aaKeyUnitaDocAip;
    }

    public void setAaKeyUnitaDocAip(BigDecimal aaKeyUnitaDocAip) {
        this.aaKeyUnitaDocAip = aaKeyUnitaDocAip;
    }

    @Column(name = "CD_KEY_UNITA_DOC_AIP")
    public String getCdKeyUnitaDocAip() {
        return this.cdKeyUnitaDocAip;
    }

    public void setCdKeyUnitaDocAip(String cdKeyUnitaDocAip) {
        this.cdKeyUnitaDocAip = cdKeyUnitaDocAip;
    }

    @Column(name = "CD_REGISTRO_KEY_UNITA_DOC_AIP")
    public String getCdRegistroKeyUnitaDocAip() {
        return this.cdRegistroKeyUnitaDocAip;
    }

    public void setCdRegistroKeyUnitaDocAip(String cdRegistroKeyUnitaDocAip) {
        this.cdRegistroKeyUnitaDocAip = cdRegistroKeyUnitaDocAip;
    }

    @Column(name = "DS_LINK_UNITA_DOC")
    public String getDsLinkUnitaDoc() {
        return this.dsLinkUnitaDoc;
    }

    public void setDsLinkUnitaDoc(String dsLinkUnitaDoc) {
        this.dsLinkUnitaDoc = dsLinkUnitaDoc;
    }

    @Column(name = "ID_AMBIENTE")
    public BigDecimal getIdAmbiente() {
        return this.idAmbiente;
    }

    public void setIdAmbiente(BigDecimal idAmbiente) {
        this.idAmbiente = idAmbiente;
    }

    @Column(name = "ID_ENTE")
    public BigDecimal getIdEnte() {
        return this.idEnte;
    }

    public void setIdEnte(BigDecimal idEnte) {
        this.idEnte = idEnte;
    }

    @Id
    @Column(name = "ID_LINK_UNITA_DOC")
    public BigDecimal getIdLinkUnitaDoc() {
        return this.idLinkUnitaDoc;
    }

    public void setIdLinkUnitaDoc(BigDecimal idLinkUnitaDoc) {
        this.idLinkUnitaDoc = idLinkUnitaDoc;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    @Column(name = "ID_UNITA_DOC")
    public BigDecimal getIdUnitaDoc() {
        return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
        this.idUnitaDoc = idUnitaDoc;
    }

    @Column(name = "ID_UNITA_DOC_AIP")
    public BigDecimal getIdUnitaDocAip() {
        return this.idUnitaDocAip;
    }

    public void setIdUnitaDocAip(BigDecimal idUnitaDocAip) {
        this.idUnitaDocAip = idUnitaDocAip;
    }

    @Column(name = "NM_AMBIENTE")
    public String getNmAmbiente() {
        return this.nmAmbiente;
    }

    public void setNmAmbiente(String nmAmbiente) {
        this.nmAmbiente = nmAmbiente;
    }

    @Column(name = "NM_ENTE")
    public String getNmEnte() {
        return this.nmEnte;
    }

    public void setNmEnte(String nmEnte) {
        this.nmEnte = nmEnte;
    }

    @Column(name = "NM_SISTEMA_MIGRAZ")
    public String getNmSistemaMigraz() {
        return this.nmSistemaMigraz;
    }

    public void setNmSistemaMigraz(String nmSistemaMigraz) {
        this.nmSistemaMigraz = nmSistemaMigraz;
    }

    @Column(name = "NM_STRUT")
    public String getNmStrut() {
        return this.nmStrut;
    }

    public void setNmStrut(String nmStrut) {
        this.nmStrut = nmStrut;
    }
}
