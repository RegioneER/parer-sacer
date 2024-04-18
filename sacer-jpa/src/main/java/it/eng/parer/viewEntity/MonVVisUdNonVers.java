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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the MON_V_VIS_UD_NON_VERS database table.
 */
@Entity
@Table(name = "MON_V_VIS_UD_NON_VERS")
public class MonVVisUdNonVers implements Serializable {

    private static final long serialVersionUID = 1L;

    private Date dtFirstSesErr;

    private Date dtLastSesErr;

    private String flNonRisolub;

    private String flVerif;

    private BigDecimal idAmbiente;

    private BigDecimal idEnte;

    private BigDecimal idStrut;

    private BigDecimal idUnitaDocNonVers;

    private String nmAmbiente;

    private String nmEnte;

    private String nmStrut;

    public MonVVisUdNonVers() {/* Hibernate */
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIRST_SES_ERR")
    public Date getDtFirstSesErr() {
        return this.dtFirstSesErr;
    }

    public void setDtFirstSesErr(Date dtFirstSesErr) {
        this.dtFirstSesErr = dtFirstSesErr;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_LAST_SES_ERR")
    public Date getDtLastSesErr() {
        return this.dtLastSesErr;
    }

    public void setDtLastSesErr(Date dtLastSesErr) {
        this.dtLastSesErr = dtLastSesErr;
    }

    @Column(name = "FL_NON_RISOLUB", columnDefinition = "char(1)")
    public String getFlNonRisolub() {
        return this.flNonRisolub;
    }

    public void setFlNonRisolub(String flNonRisolub) {
        this.flNonRisolub = flNonRisolub;
    }

    @Column(name = "FL_VERIF", columnDefinition = "char(1)")
    public String getFlVerif() {
        return this.flVerif;
    }

    public void setFlVerif(String flVerif) {
        this.flVerif = flVerif;
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

    @Column(name = "ID_UNITA_DOC_NON_VERS")
    public BigDecimal getIdUnitaDocNonVers() {
        return this.idUnitaDocNonVers;
    }

    public void setIdUnitaDocNonVers(BigDecimal idUnitaDocNonVers) {
        this.idUnitaDocNonVers = idUnitaDocNonVers;
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

    private MonVVisUdNonVersId monVVisUdNonVersId;

    @EmbeddedId()
    public MonVVisUdNonVersId getMonVVisUdNonVersId() {
        return monVVisUdNonVersId;
    }

    public void setMonVVisUdNonVersId(MonVVisUdNonVersId monVVisUdNonVersId) {
        this.monVVisUdNonVersId = monVVisUdNonVersId;
    }
}
