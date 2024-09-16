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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the ARO_V_VIS_NOTA_UNITA_DOC database table.
 *
 */
@Entity
@Table(name = "ARO_V_VIS_NOTA_UNITA_DOC")

public class AroVVisNotaUnitaDoc implements Serializable {
    private static final long serialVersionUID = 1L;
    private BigDecimal aaKeyUnitaDoc;
    private String cdKeyUnitaDoc;
    private String cdRegistroKeyUnitaDoc;
    private String cdVerIndiceAip;
    private String dsNotaUnitaDoc;
    private String dsRegistroUnitaDoc;
    private String dsTipoNotaUnitaDoc;
    private Date dtCreazione;
    private Date dtNotaUnitaDoc;
    private BigDecimal idAmbiente;
    private BigDecimal idEnte;
    private BigDecimal idNotaUnitaDoc;
    private BigDecimal idUnitaDoc;
    private BigDecimal idStrut;
    private BigDecimal idTipoNotaUnitaDoc;
    private BigDecimal idTipoUnitaDoc;
    private BigDecimal idUserIamNota;
    private BigDecimal idVerIndiceAip;
    private BigDecimal niOrd;
    private String nmAmbiente;
    private String nmEnte;
    private String nmStrut;
    private String nmTipoUnitaDoc;
    private String nmUseridNota;
    private BigDecimal pgNotaUnitaDoc;

    public AroVVisNotaUnitaDoc() {/* Hibernate */
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

    @Column(name = "CD_VER_INDICE_AIP")
    public String getCdVerIndiceAip() {
        return this.cdVerIndiceAip;
    }

    public void setCdVerIndiceAip(String cdVerIndiceAip) {
        this.cdVerIndiceAip = cdVerIndiceAip;
    }

    @Column(name = "DS_NOTA_UNITA_DOC")
    public String getDsNotaUnitaDoc() {
        return this.dsNotaUnitaDoc;
    }

    public void setDsNotaUnitaDoc(String dsNotaUnitaDoc) {
        this.dsNotaUnitaDoc = dsNotaUnitaDoc;
    }

    @Column(name = "DS_REGISTRO_UNITA_DOC")
    public String getDsRegistroUnitaDoc() {
        return this.dsRegistroUnitaDoc;
    }

    public void setDsRegistroUnitaDoc(String dsRegistroUnitaDoc) {
        this.dsRegistroUnitaDoc = dsRegistroUnitaDoc;
    }

    @Column(name = "DS_TIPO_NOTA_UNITA_DOC")
    public String getDsTipoNotaUnitaDoc() {
        return this.dsTipoNotaUnitaDoc;
    }

    public void setDsTipoNotaUnitaDoc(String dsTipoNotaUnitaDoc) {
        this.dsTipoNotaUnitaDoc = dsTipoNotaUnitaDoc;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE")
    public Date getDtCreazione() {
        return this.dtCreazione;
    }

    public void setDtCreazione(Date dtCreazione) {
        this.dtCreazione = dtCreazione;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_NOTA_UNITA_DOC")
    public Date getDtNotaUnitaDoc() {
        return this.dtNotaUnitaDoc;
    }

    public void setDtNotaUnitaDoc(Date dtNotaUnitaDoc) {
        this.dtNotaUnitaDoc = dtNotaUnitaDoc;
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
    @Column(name = "ID_NOTA_UNITA_DOC")
    public BigDecimal getIdNotaUnitaDoc() {
        return this.idNotaUnitaDoc;
    }

    public void setIdNotaUnitaDoc(BigDecimal idNotaUnitaDoc) {
        this.idNotaUnitaDoc = idNotaUnitaDoc;
    }

    @Column(name = "ID_UNITA_DOC")
    public BigDecimal getIdUnitaDoc() {
        return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
        this.idUnitaDoc = idUnitaDoc;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    @Column(name = "ID_TIPO_NOTA_UNITA_DOC")
    public BigDecimal getIdTipoNotaUnitaDoc() {
        return this.idTipoNotaUnitaDoc;
    }

    public void setIdTipoNotaUnitaDoc(BigDecimal idTipoNotaUnitaDoc) {
        this.idTipoNotaUnitaDoc = idTipoNotaUnitaDoc;
    }

    @Column(name = "ID_TIPO_UNITA_DOC")
    public BigDecimal getIdTipoUnitaDoc() {
        return this.idTipoUnitaDoc;
    }

    public void setIdTipoUnitaDoc(BigDecimal idTipoUnitaDoc) {
        this.idTipoUnitaDoc = idTipoUnitaDoc;
    }

    @Column(name = "ID_USER_IAM_NOTA")
    public BigDecimal getIdUserIamNota() {
        return this.idUserIamNota;
    }

    public void setIdUserIamNota(BigDecimal idUserIamNota) {
        this.idUserIamNota = idUserIamNota;
    }

    @Column(name = "ID_VER_INDICE_AIP")
    public BigDecimal getIdVerIndiceAip() {
        return this.idVerIndiceAip;
    }

    public void setIdVerIndiceAip(BigDecimal idVerIndiceAip) {
        this.idVerIndiceAip = idVerIndiceAip;
    }

    @Column(name = "NI_ORD")
    public BigDecimal getNiOrd() {
        return this.niOrd;
    }

    public void setNiOrd(BigDecimal niOrd) {
        this.niOrd = niOrd;
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

    @Column(name = "NM_STRUT")
    public String getNmStrut() {
        return this.nmStrut;
    }

    public void setNmStrut(String nmStrut) {
        this.nmStrut = nmStrut;
    }

    @Column(name = "NM_TIPO_UNITA_DOC")
    public String getNmTipoUnitaDoc() {
        return this.nmTipoUnitaDoc;
    }

    public void setNmTipoUnitaDoc(String nmTipoUnitaDoc) {
        this.nmTipoUnitaDoc = nmTipoUnitaDoc;
    }

    @Column(name = "NM_USERID_NOTA")
    public String getNmUseridNota() {
        return this.nmUseridNota;
    }

    public void setNmUseridNota(String nmUseridNota) {
        this.nmUseridNota = nmUseridNota;
    }

    @Column(name = "PG_NOTA_UNITA_DOC")
    public BigDecimal getPgNotaUnitaDoc() {
        return this.pgNotaUnitaDoc;
    }

    public void setPgNotaUnitaDoc(BigDecimal pgNotaUnitaDoc) {
        this.pgNotaUnitaDoc = pgNotaUnitaDoc;
    }

}
