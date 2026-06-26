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
 * The persistent class for the ARO_V_RIC_PROP_SCARTO_VERS database table.
 *
 */
@Entity
@Table(name = "ARO_V_RIC_PROP_SCARTO_VERS")
@NamedQuery(name = "AroVRicPropScartoVers.findAll", query = "SELECT a FROM AroVRicPropScartoVers a")
public class AroVRicPropScartoVers implements Serializable {
    private static final long serialVersionUID = 1L;
    private String cdPropScartoVers;
    private String dsPropScartoVers;
    private Date dtCreazionePropScartoVers;
    private Date dtUltimaModPropScartoVers;
    private BigDecimal idAmbiente;
    private BigDecimal idEnte;
    private BigDecimal idStrut;
    private String nmAmbiente;
    private String nmEnte;
    private String nmStrut;
    private String ntPropScartoVers;
    private String tiStatoPropScartoVersCor;
    // Dati richiesta di autorizzazione
    private String ntAutorita;
    private String cdRegistroRichAut;
    private BigDecimal aaRichAut;
    private String cdRichAut;
    // Dati risposta autorizzazione
    private String cdRegistroRispAut;
    private BigDecimal aaRispAut;
    private String cdRispAut;
    private String tiAutorizzazione;
    // Dati provvedimento di scarto
    private String cdRegistroProvvScarto;
    private BigDecimal aaProvvScarto;
    private String cdProvvScarto;
    private String dsFirmatoDa;

    public AroVRicPropScartoVers() {
    }

    @Column(name = "CD_PROP_SCARTO_VERS")
    public String getCdPropScartoVers() {
        return this.cdPropScartoVers;
    }

    public void setCdPropScartoVers(String cdPropScartoVers) {
        this.cdPropScartoVers = cdPropScartoVers;
    }

    @Column(name = "DS_PROP_SCARTO_VERS")
    public String getDsPropScartoVers() {
        return this.dsPropScartoVers;
    }

    public void setDsPropScartoVers(String dsPropScartoVers) {
        this.dsPropScartoVers = dsPropScartoVers;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE_PROP_SCARTO_VERS")
    public Date getDtCreazionePropScartoVers() {
        return this.dtCreazionePropScartoVers;
    }

    public void setDtCreazionePropScartoVers(Date dtCreazionePropScartoVers) {
        this.dtCreazionePropScartoVers = dtCreazionePropScartoVers;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_ULTIMA_MOD_PROP_SCARTO_VERS")
    public Date getDtUltimaModPropScartoVers() {
        return this.dtUltimaModPropScartoVers;
    }

    public void setDtUltimaModPropScartoVers(Date dtUltimaModPropScartoVers) {
        this.dtUltimaModPropScartoVers = dtUltimaModPropScartoVers;
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

    @Column(name = "NT_PROP_SCARTO_VERS")
    public String getNtPropScartoVers() {
        return this.ntPropScartoVers;
    }

    public void setNtPropScartoVers(String ntPropScartoVers) {
        this.ntPropScartoVers = ntPropScartoVers;
    }

    @Column(name = "TI_STATO_PROP_SCARTO_VERS_COR")
    public String getTiStatoPropScartoVersCor() {
        return this.tiStatoPropScartoVersCor;
    }

    public void setTiStatoPropScartoVersCor(String tiStatoPropScartoVersCor) {
        this.tiStatoPropScartoVersCor = tiStatoPropScartoVersCor;
    }

    @Column(name = "NT_AUTORITA")
    public String getNtAutorita() {
        return this.ntAutorita;
    }

    public void setNtAutorita(String ntAutorita) {
        this.ntAutorita = ntAutorita;
    }

    @Column(name = "CD_REGISTRO_RICH_AUT")
    public String getCdRegistroRichAut() {
        return this.cdRegistroRichAut;
    }

    public void setCdRegistroRichAut(String cdRegistroRichAut) {
        this.cdRegistroRichAut = cdRegistroRichAut;
    }

    @Column(name = "AA_RICH_AUT")
    public BigDecimal getAaRichAut() {
        return this.aaRichAut;
    }

    public void setAaRichAut(BigDecimal aaRichAut) {
        this.aaRichAut = aaRichAut;
    }

    @Column(name = "CD_RICH_AUT")
    public String getCdRichAut() {
        return this.cdRichAut;
    }

    public void setCdRichAut(String cdRichAut) {
        this.cdRichAut = cdRichAut;
    }

    @Column(name = "CD_REGISTRO_RISP_AUT")
    public String getCdRegistroRispAut() {
        return this.cdRegistroRispAut;
    }

    public void setCdRegistroRispAut(String cdRegistroRispAut) {
        this.cdRegistroRispAut = cdRegistroRispAut;
    }

    @Column(name = "AA_RISP_AUT")
    public BigDecimal getAaRispAut() {
        return this.aaRispAut;
    }

    public void setAaRispAut(BigDecimal aaRispAut) {
        this.aaRispAut = aaRispAut;
    }

    @Column(name = "CD_RISP_AUT")
    public String getCdRispAut() {
        return this.cdRispAut;
    }

    public void setCdRispAut(String cdRispAut) {
        this.cdRispAut = cdRispAut;
    }

    @Column(name = "TI_AUTORIZZAZIONE")
    public String getTiAutorizzazione() {
        return this.tiAutorizzazione;
    }

    public void setTiAutorizzazione(String tiAutorizzazione) {
        this.tiAutorizzazione = tiAutorizzazione;
    }

    @Column(name = "CD_REGISTRO_PROVV_SCARTO")
    public String getCdRegistroProvvScarto() {
        return cdRegistroProvvScarto;
    }

    public void setCdRegistroProvvScarto(String cdRegistroProvvScarto) {
        this.cdRegistroProvvScarto = cdRegistroProvvScarto;
    }

    @Column(name = "AA_PROVV_SCARTO")
    public BigDecimal getAaProvvScarto() {
        return aaProvvScarto;
    }

    public void setAaProvvScarto(BigDecimal aaProvvScarto) {
        this.aaProvvScarto = aaProvvScarto;
    }

    @Column(name = "CD_PROVV_SCARTO")
    public String getCdProvvScarto() {
        return cdProvvScarto;
    }

    public void setCdProvvScarto(String cdProvvScarto) {
        this.cdProvvScarto = cdProvvScarto;
    }

    @Column(name = "DS_FIRMATO_DA")
    public String getDsFirmatoDa() {
        return dsFirmatoDa;
    }

    public void setDsFirmatoDa(String dsFirmatoDa) {
        this.dsFirmatoDa = dsFirmatoDa;
    }

    private AroVRicPropScartoVersId aroVRicPropScartoVersId;

    @EmbeddedId()
    public AroVRicPropScartoVersId getAroVRicPropScartoVersId() {
        return aroVRicPropScartoVersId;
    }

    public void setAroVRicPropScartoVersId(AroVRicPropScartoVersId aroVRicPropScartoVersId) {
        this.aroVRicPropScartoVersId = aroVRicPropScartoVersId;
    }

}
