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
import java.math.BigDecimal;
import java.util.Date;

import it.eng.parer.grantedEntity.SIOrgEnteSiam;
import it.eng.parer.grantedEntity.SIUsrOrganizIam;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the ORG_DOC_PROCESSO_CONSERV database table.
 */
@Entity
@Table(schema = "SACER_IAM", name = "DEC_DOC_PROCESSO_CONSERV")
public class DecDocProcessoConserv implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idDocProcessoConserv;

    private BigDecimal aaDocProcessoConserv;

    private byte[] blDocProcessoConserv;

    private String cdKeyDocProcessoConserv;

    private String cdRegistroDocProcessoConserv;

    private String dsDocProcessoConserv;

    private Date dtDocProcessoConserv;

    private String nmFileDocProcessoConserv;

    private BigDecimal pgDocProcessoConserv;

    private SIOrgEnteSiam siOrgEnteSiam;

    private SIUsrOrganizIam siUsrOrganizIam;

    private DecTipoDocProcessoConserv decTipoDocProcessoConserv;

    public DecDocProcessoConserv() {
    }

    @Id
    @Column(name = "ID_DOC_PROCESSO_CONSERV")
    @GenericGenerator(name = "SORG_DOC_PROCESSO_CONSERV_ID_DOC_PROCESSO_CONSERV_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SORG_DOC_PROCESSO_CONSERV"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SORG_DOC_PROCESSO_CONSERV_ID_DOC_PROCESSO_CONSERV_GENERATOR")
    public Long getIdDocProcessoConserv() {
        return this.idDocProcessoConserv;
    }

    public void setIdDocProcessoConserv(Long idDocProcessoConserv) {
        this.idDocProcessoConserv = idDocProcessoConserv;
    }

    @Column(name = "AA_DOC_PROCESSO_CONSERV")
    public BigDecimal getAaDocProcessoConserv() {
        return this.aaDocProcessoConserv;
    }

    public void setAaDocProcessoConserv(BigDecimal aaDocProcessoConserv) {
        this.aaDocProcessoConserv = aaDocProcessoConserv;
    }

    @Lob
    @Column(name = "BL_DOC_PROCESSO_CONSERV")
    public byte[] getBlDocProcessoConserv() {
        return this.blDocProcessoConserv;
    }

    public void setBlDocProcessoConserv(byte[] blDocProcessoConserv) {
        this.blDocProcessoConserv = blDocProcessoConserv;
    }

    @Column(name = "CD_KEY_DOC_PROCESSO_CONSERV")
    public String getCdKeyDocProcessoConserv() {
        return this.cdKeyDocProcessoConserv;
    }

    public void setCdKeyDocProcessoConserv(String cdKeyDocProcessoConserv) {
        this.cdKeyDocProcessoConserv = cdKeyDocProcessoConserv;
    }

    @Column(name = "CD_REGISTRO_DOC_PROCESSO_CONSERV")
    public String getCdRegistroDocProcessoConserv() {
        return this.cdRegistroDocProcessoConserv;
    }

    public void setCdRegistroDocProcessoConserv(String cdRegistroDocProcessoConserv) {
        this.cdRegistroDocProcessoConserv = cdRegistroDocProcessoConserv;
    }

    @Column(name = "DS_DOC_PROCESSO_CONSERV")
    public String getDsDocProcessoConserv() {
        return this.dsDocProcessoConserv;
    }

    public void setDsDocProcessoConserv(String dsDocProcessoConserv) {
        this.dsDocProcessoConserv = dsDocProcessoConserv;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_DOC_PROCESSO_CONSERV")
    public Date getDtDocProcessoConserv() {
        return this.dtDocProcessoConserv;
    }

    public void setDtDocProcessoConserv(Date dtDocProcessoConserv) {
        this.dtDocProcessoConserv = dtDocProcessoConserv;
    }

    @Column(name = "NM_FILE_DOC_PROCESSO_CONSERV")
    public String getNmFileDocProcessoConserv() {
        return this.nmFileDocProcessoConserv;
    }

    public void setNmFileDocProcessoConserv(String nmFileDocProcessoConserv) {
        this.nmFileDocProcessoConserv = nmFileDocProcessoConserv;
    }

    private String enteDocProcessoConserv;

    @Column(name = "ENTE_DOC_PROCESSO_CONSERV")
    public String getEnteDocProcessoConserv() {
        return this.enteDocProcessoConserv;
    }

    public void setEnteDocProcessoConserv(String enteDocProcessoConserv) {
        this.enteDocProcessoConserv = enteDocProcessoConserv;
    }

    private String strutturaDocProcessoConserv;

    @Column(name = "STRUTTURA_DOC_PROCESSO_CONSERV")
    public String getStrutturaDocProcessoConserv() {
        return this.strutturaDocProcessoConserv;
    }

    public void setStrutturaDocProcessoConserv(String strutturaDocProcessoConserv) {
        this.strutturaDocProcessoConserv = strutturaDocProcessoConserv;
    }

    private String mimeType;

    @Column(name = "MIMETYPE")
    public String getMimeType() {
        return this.mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @Column(name = "PG_DOC_PROCESSO_CONSERV")
    public BigDecimal getPgDocProcessoConserv() {
        return this.pgDocProcessoConserv;
    }

    public void setPgDocProcessoConserv(BigDecimal pgDocProcessoConserv) {
        this.pgDocProcessoConserv = pgDocProcessoConserv;
    }

    // bi-directional many-to-one association to SIOrgEnteSiam
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ENTE_SIAM")
    public SIOrgEnteSiam getSiOrgEnteSiam() {
        return this.siOrgEnteSiam;
    }

    public void setSiOrgEnteSiam(SIOrgEnteSiam siOrgEnteSiam) {
        this.siOrgEnteSiam = siOrgEnteSiam;
    }

    // bi-directional many-to-one association to SIUsrOrganizIam
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ORGANIZ_IAM")
    public SIUsrOrganizIam getSiUsrOrganizIam() {
        return this.siUsrOrganizIam;
    }

    public void setSiUsrOrganizIam(SIUsrOrganizIam siUsrOrganizIam) {
        this.siUsrOrganizIam = siUsrOrganizIam;
    }

    // bi-directional many-to-one association to DecTipoDocProcessoConservo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_DOC_PROCESSO_CONSERV")
    public DecTipoDocProcessoConserv getDecTipoDocProcessoConserv() {
        return this.decTipoDocProcessoConserv;
    }

    public void setDecTipoDocProcessoConserv(DecTipoDocProcessoConserv decTipoDocProcessoConserv) {
        this.decTipoDocProcessoConserv = decTipoDocProcessoConserv;
    }
}
