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
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * The persistent class for the ARO_V_VIS_STATO_RICH_SCARTO database table.
 *
 */
@Entity
@Table(name = "ARO_V_VIS_STATO_RICH_SCARTO")
@NamedQuery(name = "AroVVisStatoRichScarto.findAll", query = "SELECT a FROM AroVVisStatoRichScarto a")
public class AroVVisStatoRichScarto implements Serializable {
    private static final long serialVersionUID = 1L;
    private String cdRichScartoVers;
    private String dsNotaRichScartoVers;
    private String dsRichScartoVers;
    private Date dtRegStatoRichScartoVers;
    private BigDecimal idAmbiente;
    private BigDecimal idEnte;
    private BigDecimal idRichScartoVers;
    private BigDecimal idStatoRichScartoVers;
    private BigDecimal idStrut;
    private String nmAmbiente;
    private String nmEnte;
    private String nmStrut;
    private String nmUseridStato;
    private BigDecimal pgStatoRichScartoVers;
    private String tiStatoRichScartoVers;
    private String tiStatoRichScartoVersCor;

    public AroVVisStatoRichScarto() {
    }

    @Column(name = "CD_RICH_SCARTO_VERS")
    public String getCdRichScartoVers() {
        return this.cdRichScartoVers;
    }

    public void setCdRichScartoVers(String cdRichScartoVers) {
        this.cdRichScartoVers = cdRichScartoVers;
    }

    @Column(name = "DS_NOTA_RICH_SCARTO_VERS")
    public String getDsNotaRichScartoVers() {
        return this.dsNotaRichScartoVers;
    }

    public void setDsNotaRichScartoVers(String dsNotaRichScartoVers) {
        this.dsNotaRichScartoVers = dsNotaRichScartoVers;
    }

    @Column(name = "DS_RICH_SCARTO_VERS")
    public String getDsRichScartoVers() {
        return this.dsRichScartoVers;
    }

    public void setDsRichScartoVers(String dsRichScartoVers) {
        this.dsRichScartoVers = dsRichScartoVers;
    }

    @Temporal(TemporalType.DATE)
    @Column(name = "DT_REG_STATO_RICH_SCARTO_VERS")
    public Date getDtRegStatoRichScartoVers() {
        return this.dtRegStatoRichScartoVers;
    }

    public void setDtRegStatoRichScartoVers(Date dtRegStatoRichScartoVers) {
        this.dtRegStatoRichScartoVers = dtRegStatoRichScartoVers;
    }

    @Column(name = "ID_AMBIENTE")
    public BigDecimal getIdAmbiente() {
        return this.idAmbiente;
    }

    public void setIdAmbiente(BigDecimal idAmbiente) {
        this.idAmbiente = idAmbiente;
    }

    @Column(name = "ID_ENTE")
    public BigDecimal getIdEnte() {
        return this.idEnte;
    }

    public void setIdEnte(BigDecimal idEnte) {
        this.idEnte = idEnte;
    }

    @Column(name = "ID_RICH_SCARTO_VERS")
    public BigDecimal getIdRichScartoVers() {
        return this.idRichScartoVers;
    }

    public void setIdRichScartoVers(BigDecimal idRichScartoVers) {
        this.idRichScartoVers = idRichScartoVers;
    }

    @Id
    @Column(name = "ID_STATO_RICH_SCARTO_VERS")
    public BigDecimal getIdStatoRichScartoVers() {
        return this.idStatoRichScartoVers;
    }

    public void setIdStatoRichScartoVers(BigDecimal idStatoRichScartoVers) {
        this.idStatoRichScartoVers = idStatoRichScartoVers;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    @Column(name = "NM_AMBIENTE")
    public String getNmAmbiente() {
        return this.nmAmbiente;
    }

    public void setNmAmbiente(String nmAmbiente) {
        this.nmAmbiente = nmAmbiente;
    }

    @Column(name = "NM_ENTE")
    public String getNmEnte() {
        return this.nmEnte;
    }

    public void setNmEnte(String nmEnte) {
        this.nmEnte = nmEnte;
    }

    @Column(name = "NM_STRUT")
    public String getNmStrut() {
        return this.nmStrut;
    }

    public void setNmStrut(String nmStrut) {
        this.nmStrut = nmStrut;
    }

    @Column(name = "NM_USERID_STATO")
    public String getNmUseridStato() {
        return this.nmUseridStato;
    }

    public void setNmUseridStato(String nmUseridStato) {
        this.nmUseridStato = nmUseridStato;
    }

    @Column(name = "PG_STATO_RICH_SCARTO_VERS")
    public BigDecimal getPgStatoRichScartoVers() {
        return this.pgStatoRichScartoVers;
    }

    public void setPgStatoRichScartoVers(BigDecimal pgStatoRichScartoVers) {
        this.pgStatoRichScartoVers = pgStatoRichScartoVers;
    }

    @Column(name = "TI_STATO_RICH_SCARTO_VERS")
    public String getTiStatoRichScartoVers() {
        return this.tiStatoRichScartoVers;
    }

    public void setTiStatoRichScartoVers(String tiStatoRichScartoVers) {
        this.tiStatoRichScartoVers = tiStatoRichScartoVers;
    }

    @Column(name = "TI_STATO_RICH_SCARTO_VERS_COR")
    public String getTiStatoRichScartoVersCor() {
        return this.tiStatoRichScartoVersCor;
    }

    public void setTiStatoRichScartoVersCor(String tiStatoRichScartoVersCor) {
        this.tiStatoRichScartoVersCor = tiStatoRichScartoVersCor;
    }

}