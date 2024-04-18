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

package it.eng.parer.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.Validate;

/**
 * The persistent class for the MON_CONTA_BY_STATO_CONSERV_NEW database table.
 *
 */
@Entity
@Table(name = "MON_CONTA_BY_STATO_CONSERV_NEW")

public class MonContaByStatoConservNew implements Serializable {

    public enum TipoConteggio {
        UD_AIP_GENERATO, UD_AIP_NON_GENERATO_AIP_IN_AGG, UD_AIP_NON_GENERATO_PRESA_IN_CARICO,
        UD_AIP_NON_GENERATO_IN_VOLUME_CONS, UD_ANNULL;

        private TipoConteggio() {
            // compiled code
        }
    }

    private static final long serialVersionUID = 1L;
    private Long idContaByStatoConserv;
    private BigDecimal aaKeyUnitaDoc;
    private Date dtRifConta;
    private BigDecimal niCompAipGenerato;
    private BigDecimal niCompAipInAggiorn;
    private BigDecimal niCompAnnul;
    private BigDecimal niCompInVolume;
    private BigDecimal niCompPresaInCarico;
    private DecRegistroUnitaDoc decRegistroUnitaDoc;
    private DecTipoDoc decTipoDoc;
    private DecTipoUnitaDoc decTipoUnitaDoc;
    private OrgStrut orgStrut;
    private OrgSubStrut orgSubStrut;
    private BigDecimal idStrut;
    private BigDecimal idSubStrut;
    private Long idDecRegistroUnitaDoc;
    private Long idDecTipoDoc;
    private Long idDecTipoUnitaDoc;

    public MonContaByStatoConservNew() {/* Hibernate */
    }

    // NON MODIFICARE USATO DA CALCOLO CONSISTENZA
    public MonContaByStatoConservNew(Object dtRifConta, long idStrut, long idSubStrut, BigDecimal aaKeyUnitaDoc,
            long idDecRegistroUnitaDoc, long idDecTipoUnitaDoc, long idDecTipoDoc, Object niComp,
            String tipoConteggio) {
        Validate.noNullElements(Arrays.asList(dtRifConta, idStrut, aaKeyUnitaDoc, idDecRegistroUnitaDoc,
                idDecTipoUnitaDoc, idDecTipoDoc, niComp, tipoConteggio));
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
        this.dtRifConta = (Date) dtRifConta;
        this.idStrut = new BigDecimal(idStrut);
        this.idSubStrut = new BigDecimal(idSubStrut);
        this.idDecRegistroUnitaDoc = idDecRegistroUnitaDoc;
        this.idDecTipoDoc = idDecTipoDoc;
        this.idDecTipoUnitaDoc = idDecTipoUnitaDoc;
        this.niCompAipGenerato = new BigDecimal(0);
        this.niCompAipInAggiorn = new BigDecimal(0);
        this.niCompAnnul = new BigDecimal(0);
        this.niCompInVolume = new BigDecimal(0);
        this.niCompPresaInCarico = new BigDecimal(0);

        switch (TipoConteggio.valueOf(tipoConteggio)) {
        case UD_AIP_GENERATO:
            this.niCompAipGenerato = (BigDecimal) niComp;
            break;
        case UD_AIP_NON_GENERATO_PRESA_IN_CARICO:
            this.niCompPresaInCarico = (BigDecimal) niComp;
            break;
        case UD_AIP_NON_GENERATO_AIP_IN_AGG:
            this.niCompAipInAggiorn = (BigDecimal) niComp;
            break;
        case UD_AIP_NON_GENERATO_IN_VOLUME_CONS:
            this.niCompInVolume = (BigDecimal) niComp;
            break;
        case UD_ANNULL:
            this.niCompAnnul = (BigDecimal) niComp;
            break;
        }

    }

    // NON MODIFICARE USATO DA CALCOLO CONSISTENZA
    public MonContaByStatoConservNew(Object dtRifConta, long idStrut, long idSubStrut, BigDecimal aaKeyUnitaDoc,
            long idDecRegistroUnitaDoc, long idDecTipoUnitaDoc, Object niComp, String tipoConteggio) {
        Validate.noNullElements(Arrays.asList(dtRifConta, idStrut, aaKeyUnitaDoc, idDecRegistroUnitaDoc,
                idDecTipoUnitaDoc, niComp, tipoConteggio));
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
        this.dtRifConta = (Date) dtRifConta;
        this.idStrut = new BigDecimal(idStrut);
        this.idSubStrut = new BigDecimal(idSubStrut);
        this.idDecRegistroUnitaDoc = idDecRegistroUnitaDoc;
        this.idDecTipoDoc = 0L;
        this.idDecTipoUnitaDoc = idDecTipoUnitaDoc;
        this.niCompAipGenerato = new BigDecimal(0);
        this.niCompAipInAggiorn = new BigDecimal(0);
        this.niCompAnnul = new BigDecimal(0);
        this.niCompInVolume = new BigDecimal(0);
        this.niCompPresaInCarico = new BigDecimal(0);

        switch (TipoConteggio.valueOf(tipoConteggio)) {
        case UD_AIP_GENERATO:
            this.niCompAipGenerato = (BigDecimal) niComp;
            break;
        case UD_AIP_NON_GENERATO_PRESA_IN_CARICO:
            this.niCompPresaInCarico = (BigDecimal) niComp;
            break;
        case UD_AIP_NON_GENERATO_AIP_IN_AGG:
            this.niCompAipInAggiorn = (BigDecimal) niComp;
            break;
        case UD_AIP_NON_GENERATO_IN_VOLUME_CONS:
            this.niCompInVolume = (BigDecimal) niComp;
            break;
        case UD_ANNULL:
            this.niCompAnnul = (BigDecimal) niComp;
            break;
        }

    }

    @Id
    @SequenceGenerator(name = "MON_CONTA_BY_STATO_CONSERV_NEW_IDCONTABYSTATOCONSERV_GENERATOR", sequenceName = "SMON_CONTA_BY_STATO_CONSERV_NEW", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MON_CONTA_BY_STATO_CONSERV_NEW_IDCONTABYSTATOCONSERV_GENERATOR")
    @Column(name = "ID_CONTA_BY_STATO_CONSERV")
    public Long getIdContaByStatoConserv() {
        return this.idContaByStatoConserv;
    }

    public void setIdContaByStatoConserv(Long idContaByStatoConserv) {
        this.idContaByStatoConserv = idContaByStatoConserv;
    }

    @Column(name = "AA_KEY_UNITA_DOC")
    public BigDecimal getAaKeyUnitaDoc() {
        return this.aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_RIF_CONTA")
    public Date getDtRifConta() {
        return this.dtRifConta;
    }

    public void setDtRifConta(Date dtRifConta) {
        this.dtRifConta = dtRifConta;
    }

    @Column(name = "NI_COMP_AIP_GENERATO")
    public BigDecimal getNiCompAipGenerato() {
        return this.niCompAipGenerato;
    }

    public void setNiCompAipGenerato(BigDecimal niCompAipGenerato) {
        this.niCompAipGenerato = niCompAipGenerato;
    }

    @Column(name = "NI_COMP_AIP_IN_AGGIORN")
    public BigDecimal getNiCompAipInAggiorn() {
        return this.niCompAipInAggiorn;
    }

    public void setNiCompAipInAggiorn(BigDecimal niCompAipInAggiorn) {
        this.niCompAipInAggiorn = niCompAipInAggiorn;
    }

    @Column(name = "NI_COMP_ANNUL")
    public BigDecimal getNiCompAnnul() {
        return this.niCompAnnul;
    }

    public void setNiCompAnnul(BigDecimal niCompAnnul) {
        this.niCompAnnul = niCompAnnul;
    }

    @Column(name = "NI_COMP_IN_VOLUME")
    public BigDecimal getNiCompInVolume() {
        return this.niCompInVolume;
    }

    public void setNiCompInVolume(BigDecimal niCompInVolume) {
        this.niCompInVolume = niCompInVolume;
    }

    @Column(name = "NI_COMP_PRESA_IN_CARICO")
    public BigDecimal getNiCompPresaInCarico() {
        return this.niCompPresaInCarico;
    }

    public void setNiCompPresaInCarico(BigDecimal niCompPresaInCarico) {
        this.niCompPresaInCarico = niCompPresaInCarico;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_REGISTRO_UNITA_DOC", insertable = false, updatable = false)
    public DecRegistroUnitaDoc getDecRegistroUnitaDoc() {
        return this.decRegistroUnitaDoc;
    }

    public void setDecRegistroUnitaDoc(DecRegistroUnitaDoc decRegistroUnitaDoc) {
        this.decRegistroUnitaDoc = decRegistroUnitaDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_DOC_PRINC", insertable = false, updatable = false)
    public DecTipoDoc getDecTipoDoc() {
        return this.decTipoDoc;
    }

    public void setDecTipoDoc(DecTipoDoc decTipoDoc) {
        this.decTipoDoc = decTipoDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STRUT", insertable = false, updatable = false)
    public OrgStrut getOrgStrut() {
        return this.orgStrut;
    }

    public void setOrgStrut(OrgStrut orgStrut) {
        this.orgStrut = orgStrut;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_UNITA_DOC", insertable = false, updatable = false)
    public DecTipoUnitaDoc getDecTipoUnitaDoc() {
        return this.decTipoUnitaDoc;
    }

    public void setDecTipoUnitaDoc(DecTipoUnitaDoc decTipoUnitaDoc) {
        this.decTipoUnitaDoc = decTipoUnitaDoc;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    @Column(name = "ID_REGISTRO_UNITA_DOC")
    public Long getIdDecRegistroUnitaDoc() {
        return idDecRegistroUnitaDoc;
    }

    public void setIdDecRegistroUnitaDoc(Long idDecRegistroUnitaDoc) {
        this.idDecRegistroUnitaDoc = idDecRegistroUnitaDoc;
    }

    @Column(name = "ID_TIPO_DOC_PRINC")
    public Long getIdDecTipoDoc() {
        return idDecTipoDoc;
    }

    public void setIdDecTipoDoc(Long idDecTipoDoc) {
        this.idDecTipoDoc = idDecTipoDoc;
    }

    @Column(name = "ID_TIPO_UNITA_DOC")
    public Long getIdDecTipoUnitaDoc() {
        return idDecTipoUnitaDoc;
    }

    public void setIdDecTipoUnitaDoc(Long idDecTipoUnitaDoc) {
        this.idDecTipoUnitaDoc = idDecTipoUnitaDoc;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.aaKeyUnitaDoc);
        hash = 89 * hash + Objects.hashCode(this.dtRifConta);
        hash = 89 * hash + Objects.hashCode(this.idStrut);
        hash = 89 * hash + (int) (this.idDecRegistroUnitaDoc ^ (this.idDecRegistroUnitaDoc >>> 32));
        hash = 89 * hash + (int) (this.idDecTipoDoc ^ (this.idDecTipoDoc >>> 32));
        hash = 89 * hash + (int) (this.idDecTipoUnitaDoc ^ (this.idDecTipoUnitaDoc >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MonContaByStatoConservNew other = (MonContaByStatoConservNew) obj;
        if (!Objects.equals(this.aaKeyUnitaDoc, other.aaKeyUnitaDoc)) {
            return false;
        }
        if (!Objects.equals(this.dtRifConta, other.dtRifConta)) {
            return false;
        }
        if (!Objects.equals(this.idStrut, other.idStrut)) {
            return false;
        }
        if (this.idDecRegistroUnitaDoc != other.idDecRegistroUnitaDoc) {
            return false;
        }
        if (this.idDecTipoDoc != other.idDecTipoDoc) {
            return false;
        }
        return this.idDecTipoUnitaDoc == other.idDecTipoUnitaDoc;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SUB_STRUT", insertable = false, updatable = false)
    public OrgSubStrut getOrgSubStrut() {
        return this.orgSubStrut;
    }

    public void setOrgSubStrut(OrgSubStrut orgSubStrut) {
        this.orgSubStrut = orgSubStrut;
    }

    @Column(name = "ID_SUB_STRUT")
    public BigDecimal getIdSubStrut() {
        return this.idSubStrut;
    }

    public void setIdSubStrut(BigDecimal idSubStrut) {
        this.idSubStrut = idSubStrut;
    }

}
