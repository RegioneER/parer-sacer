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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the SER_UD_NON_VERS_ERR database table.
 */
@Entity
@Table(name = "SER_UD_NON_VERS_ERR")

public class SerUdNonVersErr implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idUdNonVersErr;

    private SerErrContenutoVerSerie serErrContenutoVerSerie;

    private VrsUnitaDocNonVer vrsUnitaDocNonVer;

    public SerUdNonVersErr() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_UD_NON_VERS_ERR")
    @GenericGenerator(name = "SSER_UD_NON_VERS_ERR_ID_UD_NON_VERS_ERR_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SSER_UD_NON_VERS_ERR"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SSER_UD_NON_VERS_ERR_ID_UD_NON_VERS_ERR_GENERATOR")
    public Long getIdUdNonVersErr() {
        return this.idUdNonVersErr;
    }

    public void setIdUdNonVersErr(Long idUdNonVersErr) {
        this.idUdNonVersErr = idUdNonVersErr;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ERR_CONTENUTO_VER_SERIE")
    public SerErrContenutoVerSerie getSerErrContenutoVerSerie() {
        return this.serErrContenutoVerSerie;
    }

    public void setSerErrContenutoVerSerie(SerErrContenutoVerSerie serErrContenutoVerSerie) {
        this.serErrContenutoVerSerie = serErrContenutoVerSerie;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UNITA_DOC_NON_VERS")
    public VrsUnitaDocNonVer getVrsUnitaDocNonVer() {
        return this.vrsUnitaDocNonVer;
    }

    public void setVrsUnitaDocNonVer(VrsUnitaDocNonVer vrsUnitaDocNonVer) {
        this.vrsUnitaDocNonVer = vrsUnitaDocNonVer;
    }

}
