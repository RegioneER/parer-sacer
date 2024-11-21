/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.eng.parer.entity;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author gpiccioli
 */

@Entity
@Table(name = "VRS_SESSIONE_VERS_KO_ELIMINATE")
public class VrsSessioneVersKoEliminate {
    private static final long serialVersionUID = 1L;

    private Long idSessioneVersKoEliminata;

    private OrgStrut orgStrut;

    private String nmStrut;

    private String dsStrut;

    private BigDecimal niSesEliminate;

    private Date dtElab;

    private Date dtRif;

    public VrsSessioneVersKoEliminate() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_SESSIONE_VERS_KO_ELIMINATE")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getIdSessioneVersKoEliminata() {
        return this.idSessioneVersKoEliminata;
    }

    public void setIdSessioneVersKoEliminata(Long idSessioneVersKoEliminata) {
        this.idSessioneVersKoEliminata = idSessioneVersKoEliminata;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STRUT")
    public OrgStrut getOrgStrut() {
        return this.orgStrut;
    }

    public void setOrgStrut(OrgStrut orgStrut) {
        this.orgStrut = orgStrut;
    }

    @Column(name = "NM_STRUT")
    public String getNmStrut() {
        return this.nmStrut;
    }

    public void setNmStrut(String nmStrut) {
        this.nmStrut = nmStrut;
    }

    @Column(name = "DS_STRUT")
    public String getDsStrut() {
        return this.dsStrut;
    }

    public void setDsStrut(String dsStrut) {
        this.dsStrut = dsStrut;
    }

    @Column(name = "NI_SES_ELIMINATE")
    public BigDecimal getNiSesEliminate() {
        return this.niSesEliminate;
    }

    public void setNiSesEliminate(BigDecimal niSesEliminate) {
        this.niSesEliminate = niSesEliminate;
    }

    @Column(name = "DT_ELAB")
    public Date getDtElab() {
        return this.dtElab;
    }

    public void setDtElab(Date dtElab) {
        this.dtElab = dtElab;
    }

    @Column(name = "DT_RIF")
    public Date getDtRif() {
        return this.dtRif;
    }

    public void setDtRif(Date dtRif) {
        this.dtRif = dtRif;
    }
}
