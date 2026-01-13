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
import java.time.LocalDate;
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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the REC_DT_VERS_RECUP database table.
 */
@Entity
@Table(name = "REC_DT_VERS_RECUP")
public class RecDtVersRecup implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idDtVersRecup;

    private Date dtStatoDtVersRecup;

    // MAC#27666
    /*
     * NB. Data mantenuta intenzionalmente con LocalDate per mantenerla compatibile con il TPI NON
     * TOCCARE
     */
    private LocalDate dtVers;
    // end MAC#27666

    private String flMigraz;

    private String tiStatoDtVersRecup;

    private RecSessioneRecup recSessioneRecup;

    public RecDtVersRecup() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_DT_VERS_RECUP")
    @GenericGenerator(name = "SREC_DT_VERS_RECUP_ID_DT_VERS_RECUP_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SREC_DT_VERS_RECUP"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SREC_DT_VERS_RECUP_ID_DT_VERS_RECUP_GENERATOR")
    public Long getIdDtVersRecup() {
        return this.idDtVersRecup;
    }

    public void setIdDtVersRecup(Long idDtVersRecup) {
        this.idDtVersRecup = idDtVersRecup;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_STATO_DT_VERS_RECUP")
    public Date getDtStatoDtVersRecup() {
        return this.dtStatoDtVersRecup;
    }

    public void setDtStatoDtVersRecup(Date dtStatoDtVersRecup) {
        this.dtStatoDtVersRecup = dtStatoDtVersRecup;
    }

    // MAC#27666
    /*
     * NB. Data mantenuta intenzionalmente con LocalDate per mantenerla compatibile con il TPI NON
     * TOCCARE
     */
    @Column(name = "DT_VERS")
    public LocalDate getDtVers() {
        return this.dtVers;
    }

    public void setDtVers(LocalDate dtVers) {
        this.dtVers = dtVers;
    }
    // end MAC#27666

    @Column(name = "FL_MIGRAZ", columnDefinition = "char(1)")
    public String getFlMigraz() {
        return this.flMigraz;
    }

    public void setFlMigraz(String flMigraz) {
        this.flMigraz = flMigraz;
    }

    @Column(name = "TI_STATO_DT_VERS_RECUP")
    public String getTiStatoDtVersRecup() {
        return this.tiStatoDtVersRecup;
    }

    public void setTiStatoDtVersRecup(String tiStatoDtVersRecup) {
        this.tiStatoDtVersRecup = tiStatoDtVersRecup;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SESSIONE_RECUP")
    public RecSessioneRecup getRecSessioneRecup() {
        return this.recSessioneRecup;
    }

    public void setRecSessioneRecup(RecSessioneRecup recSessioneRecup) {
        this.recSessioneRecup = recSessioneRecup;
    }

}
