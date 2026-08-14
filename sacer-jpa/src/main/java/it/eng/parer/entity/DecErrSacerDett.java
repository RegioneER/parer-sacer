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
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "DEC_ERR_SACER_DETT")
public class DecErrSacerDett implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idErrSacerDett;

    private String cdSottoclasse;

    private String dsCasistica;

    private String dsSoluzioneSugg;

    private String dtVersInizioVal;

    private String dtVersFineVal;

    private String flDeprecato;

    private String flPubblico;

    private DecErrSacer decErrSacer;

    public DecErrSacerDett() {/* Hibernate */
    }

    @Id
    @Column(name = "ID_ERR_SACER_DETT")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getIdErrSacerDett() {
        return idErrSacerDett;
    }

    public void setIdErrSacerDett(Long idErrSacerDett) {
        this.idErrSacerDett = idErrSacerDett;
    }

    @Column(name = "CD_SOTTOCLASSE", length = 50)
    public String getCdSottoclasse() {
        return cdSottoclasse;
    }

    public void setCdSottoclasse(String cdSottoclasse) {
        this.cdSottoclasse = cdSottoclasse;
    }

    @Column(name = "DS_CASISTICA", length = 4000)
    public String getDsCasistica() {
        return dsCasistica;
    }

    public void setDsCasistica(String dsCasistica) {
        this.dsCasistica = dsCasistica;
    }

    @Column(name = "DS_SOLUZIONE_SUGG", length = 4000)
    public String getDsSoluzioneSugg() {
        return dsSoluzioneSugg;
    }

    public void setDsSoluzioneSugg(String dsSoluzioneSugg) {
        this.dsSoluzioneSugg = dsSoluzioneSugg;
    }

    @Column(name = "DT_VERS_INIZIO_VAL", length = 50)
    public String getDtVersInizioVal() {
        return dtVersInizioVal;
    }

    public void setDtVersInizioVal(String dtVersInizioVal) {
        this.dtVersInizioVal = dtVersInizioVal;
    }

    @Column(name = "DT_VERS_FINE_VAL", length = 50)
    public String getDtVersFineVal() {
        return dtVersFineVal;
    }

    public void setDtVersFineVal(String dtVersFineVal) {
        this.dtVersFineVal = dtVersFineVal;
    }

    @Column(name = "FL_DEPRECATO", columnDefinition = "char(1)")
    public String getFlDeprecato() {
        return flDeprecato;
    }

    public void setFlDeprecato(String flDeprecato) {
        this.flDeprecato = flDeprecato;
    }

    @Column(name = "FL_PUBBLICO", columnDefinition = "char(1)")
    public String getFlPubblico() {
        return flPubblico;
    }

    public void setFlPubblico(String flPubblico) {
        this.flPubblico = flPubblico;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ERR_SACER", unique = true)
    public DecErrSacer getDecErrSacer() {
        return decErrSacer;
    }

    public void setDecErrSacer(DecErrSacer decErrSacer) {
        this.decErrSacer = decErrSacer;
    }
}