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
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the ARO_V_LIS_MARCA_COMP database table.
 * 
 */
@Entity
@Table(name = "ARO_V_LIS_MARCA_COMP")
public class AroVLisMarcaComp implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "ID_COMP_DOC")
    private BigDecimal idCompDoc;

    @Id

    @Column(name = "ID_MARCA_COMP")
    private BigDecimal idMarcaComp;

    @Column(name = "ISSUER_CERTIF_TSA")
    private String issuerCertifTsa;

    @Column(name = "PG_MARCA")
    private BigDecimal pgMarca;

    @Column(name = "TI_ESITO_CONTR_CONFORME")
    private String tiEsitoContrConforme;

    @Column(name = "TI_ESITO_VERIF_MARCA")
    private String tiEsitoVerifMarca;

    @Column(name = "TI_FORMATO_MARCA")
    private String tiFormatoMarca;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "TM_MARCA_TEMP")
    private Date tmMarcaTemp;

    public AroVLisMarcaComp() {/* Hibernate */
    }

    public BigDecimal getIdCompDoc() {
        return this.idCompDoc;
    }

    public void setIdCompDoc(BigDecimal idCompDoc) {
        this.idCompDoc = idCompDoc;
    }

    public BigDecimal getIdMarcaComp() {
        return this.idMarcaComp;
    }

    public void setIdMarcaComp(BigDecimal idMarcaComp) {
        this.idMarcaComp = idMarcaComp;
    }

    public String getIssuerCertifTsa() {
        return this.issuerCertifTsa;
    }

    public void setIssuerCertifTsa(String issuerCertifTsa) {
        this.issuerCertifTsa = issuerCertifTsa;
    }

    public BigDecimal getPgMarca() {
        return this.pgMarca;
    }

    public void setPgMarca(BigDecimal pgMarca) {
        this.pgMarca = pgMarca;
    }

    public String getTiEsitoContrConforme() {
        return this.tiEsitoContrConforme;
    }

    public void setTiEsitoContrConforme(String tiEsitoContrConforme) {
        this.tiEsitoContrConforme = tiEsitoContrConforme;
    }

    public String getTiEsitoVerifMarca() {
        return this.tiEsitoVerifMarca;
    }

    public void setTiEsitoVerifMarca(String tiEsitoVerifMarca) {
        this.tiEsitoVerifMarca = tiEsitoVerifMarca;
    }

    public String getTiFormatoMarca() {
        return this.tiFormatoMarca;
    }

    public void setTiFormatoMarca(String tiFormatoMarca) {
        this.tiFormatoMarca = tiFormatoMarca;
    }

    public Date getTmMarcaTemp() {
        return this.tmMarcaTemp;
    }

    public void setTmMarcaTemp(Date tmMarcaTemp) {
        this.tmMarcaTemp = tmMarcaTemp;
    }

}
