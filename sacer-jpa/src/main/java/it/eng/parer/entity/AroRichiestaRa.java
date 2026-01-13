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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

import it.eng.parer.entity.constraint.AroRichiestaRa.AroRichiestaTiStato;

/**
 * The persistent class for the ARO_RICHIESTA_RA database table.
 */
@Entity
@Table(name = "ARO_RICHIESTA_RA")

public class AroRichiestaRa implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idRichiestaRa;

    private Date tsInizio;

    private Date tsFine;

    private OrgStrut orgStrut;

    private IamUser iamUser;

    private AroRichiestaTiStato tiStato;

    private String cdErrore;

    private String note;

    private String flSvuotaFtp;

    private List<AroAipRestituzioneArchivio> aroAipRestituzioneArchivios = new ArrayList<>();

    public AroRichiestaRa() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_RICHIESTA_RA")
    @GenericGenerator(name = "SARO_RICHIESTA_RA_ID_RICHIESTA_RA_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SARO_RICHIESTA_RA"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SARO_RICHIESTA_RA_ID_RICHIESTA_RA_GENERATOR")
    public Long getIdRichiestaRa() {
        return this.idRichiestaRa;
    }

    public void setIdRichiestaRa(Long idRichiestaRa) {
        this.idRichiestaRa = idRichiestaRa;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TS_INIZIO")
    public Date getTsInizio() {
        return this.tsInizio;
    }

    public void setTsInizio(Date tsInizio) {
        this.tsInizio = tsInizio;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TS_FINE")
    public Date getTsFine() {
        return this.tsFine;
    }

    public void setTsFine(Date tsFine) {
        this.tsFine = tsFine;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "TI_STATO")
    public AroRichiestaTiStato getTiStato() {
        return this.tiStato;
    }

    public void setTiStato(AroRichiestaTiStato tiStato) {
        this.tiStato = tiStato;
    }

    @Column(name = "CD_ERRORE")
    public String getCdErrore() {
        return this.cdErrore;
    }

    public void setCdErrore(String cdErrore) {
        this.cdErrore = cdErrore;
    }

    @Column(name = "NOTE")
    public String getNote() {
        return this.note;
    }

    @Column(name = "FL_SVUOTA_FTP", columnDefinition = "char(1)")
    public String getFlSvuotaFtp() {
        return this.flSvuotaFtp;
    }

    public void setFlSvuotaFtp(String flSvuotaFtp) {
        this.flSvuotaFtp = flSvuotaFtp;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STRUT")
    public OrgStrut getOrgStrut() {
        return this.orgStrut;
    }

    public void setOrgStrut(OrgStrut orgStrut) {
        this.orgStrut = orgStrut;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USER_IAM")
    public IamUser getIamUser() {
        return this.iamUser;
    }

    public void setIamUser(IamUser iamUser) {
        this.iamUser = iamUser;
    }

    @OneToMany(mappedBy = "aroRichiestaRa", cascade = CascadeType.PERSIST)
    public List<AroAipRestituzioneArchivio> getAroAipRestituzioneArchivios() {
        return this.aroAipRestituzioneArchivios;
    }

    public void setAroAipRestituzioneArchivios(
            List<AroAipRestituzioneArchivio> aroAipRestituzioneArchivios) {
        this.aroAipRestituzioneArchivios = aroAipRestituzioneArchivios;
    }

    public AroAipRestituzioneArchivio addAroAipRestituzioneArchivio(
            AroAipRestituzioneArchivio aroAipRestituzioneArchivio) {
        getAroAipRestituzioneArchivios().add(aroAipRestituzioneArchivio);
        aroAipRestituzioneArchivio.setAroRichiestaRa(this);
        return aroAipRestituzioneArchivio;
    }

    public AroAipRestituzioneArchivio removeAroAipRestituzioneArchivio(
            AroAipRestituzioneArchivio aroAipRestituzioneArchivio) {
        getAroAipRestituzioneArchivios().remove(aroAipRestituzioneArchivio);
        aroAipRestituzioneArchivio.setAroRichiestaRa(null);
        return aroAipRestituzioneArchivio;
    }
}
