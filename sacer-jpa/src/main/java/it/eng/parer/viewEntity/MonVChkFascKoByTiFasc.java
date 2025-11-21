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

package it.eng.parer.viewEntity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.*;

/**
 * The persistent class for the MON_V_CHK_FASC_KO_BY_TI_FASC database table.
 *
 */
@Entity
@Table(name = "MON_V_CHK_FASC_KO_BY_TI_FASC")
@NamedQuery(name = "MonVChkFascKoByTiFasc.findByTiFasc", query = "SELECT m FROM MonVChkFascKoByTiFasc m WHERE m.idTipoFascicolo = :idTipoFascicolo")
public class MonVChkFascKoByTiFasc implements Serializable {

    private static final long serialVersionUID = 1L;
    private String flFascKoNonRisolub30gg;
    private String flFascKoNonRisolubB30gg;
    private String flFascKoNonRisolubCorr;
    private String flFascKoNonVerif30gg;
    private String flFascKoNonVerifB30gg;
    private String flFascKoNonVerifCorr;
    private String flFascKoVerif30gg;
    private String flFascKoVerifB30gg;
    private String flFascKoVerifCorr;
    private BigDecimal idTipoFascicolo;

    public MonVChkFascKoByTiFasc() {/* Hibernate */
    }

    @Column(name = "FL_FASC_KO_NON_RISOLUB_30GG", columnDefinition = "char")
    public String getFlFascKoNonRisolub30gg() {
        return this.flFascKoNonRisolub30gg;
    }

    public void setFlFascKoNonRisolub30gg(String flFascKoNonRisolub30gg) {
        this.flFascKoNonRisolub30gg = flFascKoNonRisolub30gg;
    }

    @Column(name = "FL_FASC_KO_NON_RISOLUB_B30GG", columnDefinition = "char")
    public String getFlFascKoNonRisolubB30gg() {
        return this.flFascKoNonRisolubB30gg;
    }

    public void setFlFascKoNonRisolubB30gg(String flFascKoNonRisolubB30gg) {
        this.flFascKoNonRisolubB30gg = flFascKoNonRisolubB30gg;
    }

    @Column(name = "FL_FASC_KO_NON_RISOLUB_CORR", columnDefinition = "char(1)")
    public String getFlFascKoNonRisolubCorr() {
        return this.flFascKoNonRisolubCorr;
    }

    public void setFlFascKoNonRisolubCorr(String flFascKoNonRisolubCorr) {
        this.flFascKoNonRisolubCorr = flFascKoNonRisolubCorr;
    }

    @Column(name = "FL_FASC_KO_NON_VERIF_30GG", columnDefinition = "char")
    public String getFlFascKoNonVerif30gg() {
        return this.flFascKoNonVerif30gg;
    }

    public void setFlFascKoNonVerif30gg(String flFascKoNonVerif30gg) {
        this.flFascKoNonVerif30gg = flFascKoNonVerif30gg;
    }

    @Column(name = "FL_FASC_KO_NON_VERIF_B30GG", columnDefinition = "char")
    public String getFlFascKoNonVerifB30gg() {
        return this.flFascKoNonVerifB30gg;
    }

    public void setFlFascKoNonVerifB30gg(String flFascKoNonVerifB30gg) {
        this.flFascKoNonVerifB30gg = flFascKoNonVerifB30gg;
    }

    @Column(name = "FL_FASC_KO_NON_VERIF_CORR", columnDefinition = "char(1)")
    public String getFlFascKoNonVerifCorr() {
        return this.flFascKoNonVerifCorr;
    }

    public void setFlFascKoNonVerifCorr(String flFascKoNonVerifCorr) {
        this.flFascKoNonVerifCorr = flFascKoNonVerifCorr;
    }

    @Column(name = "FL_FASC_KO_VERIF_30GG", columnDefinition = "char")
    public String getFlFascKoVerif30gg() {
        return this.flFascKoVerif30gg;
    }

    public void setFlFascKoVerif30gg(String flFascKoVerif30gg) {
        this.flFascKoVerif30gg = flFascKoVerif30gg;
    }

    @Column(name = "FL_FASC_KO_VERIF_B30GG", columnDefinition = "char")
    public String getFlFascKoVerifB30gg() {
        return this.flFascKoVerifB30gg;
    }

    public void setFlFascKoVerifB30gg(String flFascKoVerifB30gg) {
        this.flFascKoVerifB30gg = flFascKoVerifB30gg;
    }

    @Column(name = "FL_FASC_KO_VERIF_CORR", columnDefinition = "char(1)")
    public String getFlFascKoVerifCorr() {
        return this.flFascKoVerifCorr;
    }

    public void setFlFascKoVerifCorr(String flFascKoVerifCorr) {
        this.flFascKoVerifCorr = flFascKoVerifCorr;
    }

    @Id
    @Column(name = "ID_TIPO_FASCICOLO")
    public BigDecimal getIdTipoFascicolo() {
        return this.idTipoFascicolo;
    }

    public void setIdTipoFascicolo(BigDecimal idTipoFascicolo) {
        this.idTipoFascicolo = idTipoFascicolo;
    }

}
