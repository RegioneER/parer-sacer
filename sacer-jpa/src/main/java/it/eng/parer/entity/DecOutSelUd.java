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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

/**
 * The persistent class for the DEC_OUT_SEL_UD database table.
 */
@Entity
@Table(name = "DEC_OUT_SEL_UD")

public class DecOutSelUd implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idOutSelUd;

    private String dlFormatoOut;

    private String tiOut;

    private List<DecCampoOutSelUd> decCampoOutSelUds = new ArrayList<>();

    private DecTipoSerieUd decTipoSerieUd;

    public DecOutSelUd() {/* Hibernate */
    }

    @Id

    @Column(name = "ID_OUT_SEL_UD")
    @GenericGenerator(name = "SDEC_OUT_SEL_UD_ID_OUT_SEL_UD_GENERATOR", strategy = "it.eng.sequences.hibernate.NonMonotonicSequenceGenerator", parameters = {
            @Parameter(name = SequenceStyleGenerator.SEQUENCE_PARAM, value = "SDEC_OUT_SEL_UD"),
            @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1") })
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SDEC_OUT_SEL_UD_ID_OUT_SEL_UD_GENERATOR")
    public Long getIdOutSelUd() {
        return this.idOutSelUd;
    }

    public void setIdOutSelUd(Long idOutSelUd) {
        this.idOutSelUd = idOutSelUd;
    }

    @Column(name = "DL_FORMATO_OUT")
    public String getDlFormatoOut() {
        return this.dlFormatoOut;
    }

    public void setDlFormatoOut(String dlFormatoOut) {
        this.dlFormatoOut = dlFormatoOut;
    }

    @Column(name = "TI_OUT")
    public String getTiOut() {
        return this.tiOut;
    }

    public void setTiOut(String tiOut) {
        this.tiOut = tiOut;
    }

    @OneToMany(mappedBy = "decOutSelUd", cascade = CascadeType.PERSIST)
    public List<DecCampoOutSelUd> getDecCampoOutSelUds() {
        return this.decCampoOutSelUds;
    }

    public void setDecCampoOutSelUds(List<DecCampoOutSelUd> decCampoOutSelUds) {
        this.decCampoOutSelUds = decCampoOutSelUds;
    }

    public DecCampoOutSelUd addDecCampoOutSelUd(DecCampoOutSelUd decCampoOutSelUd) {
        getDecCampoOutSelUds().add(decCampoOutSelUd);
        decCampoOutSelUd.setDecOutSelUd(this);
        return decCampoOutSelUd;
    }

    public DecCampoOutSelUd removeDecCampoOutSelUd(DecCampoOutSelUd decCampoOutSelUd) {
        getDecCampoOutSelUds().remove(decCampoOutSelUd);
        decCampoOutSelUd.setDecOutSelUd(null);
        return decCampoOutSelUd;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_TIPO_SERIE_UD")
    public DecTipoSerieUd getDecTipoSerieUd() {
        return this.decTipoSerieUd;
    }

    public void setDecTipoSerieUd(DecTipoSerieUd decTipoSerieUd) {
        this.decTipoSerieUd = decTipoSerieUd;
    }

}
