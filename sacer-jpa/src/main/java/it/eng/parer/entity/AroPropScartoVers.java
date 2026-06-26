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
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The persistent class for the ARO_PROP_SCARTO_VERS database table.
 *
 */
@Entity
@Table(name = "ARO_PROP_SCARTO_VERS")
@NamedQuery(name = "AroPropScartoVers.findAll", query = "SELECT a FROM AroPropScartoVers a")
public class AroPropScartoVers implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long idPropScartoVers;
    // private String cdPropScartoVers;
    private BigDecimal pgPropScartoVers;
    private BigDecimal aaPropScartoVers;
    private String dsPropScartoVers;
    private Date dtCreazione;
    private Date dtUltimaMod;
    private OrgStrut orgStrut;
    private String ntPropScartoVers;
    private BigDecimal idStatoPropScartoVersCor;

    // Dati richiesta di autorizzazione
    private String ntAutorita;
    private String cdRegistroRichAut;
    private BigDecimal aaRichAut;
    private String cdRichAut;

    // Dati risposta dell'autorità
    private String cdRegistroRispAut;
    private BigDecimal aaRispAut;
    private String cdRispAut;
    private String tiAutorizzazione;

    // Dati provvedimento di scarto
    private String cdRegistroProvvScarto;
    private BigDecimal aaProvvScarto;
    private String cdProvvScarto;
    private String dsFirmatoDa;

    // Numero UD in proposta al momento dell'autorizzazione PARZIALE (snapshot pre-revisione)
    private BigDecimal niUdPreRevisione;

    private List<AroItemPropScartoVers> aroItemPropScartoVers = new ArrayList<>();
    private List<AroStatoPropScartoVers> aroStatoPropScartoVers = new ArrayList<>();

    public AroPropScartoVers() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PROP_SCARTO_VERS")
    public Long getIdPropScartoVers() {
        return this.idPropScartoVers;
    }

    public void setIdPropScartoVers(Long idPropScartoVers) {
        this.idPropScartoVers = idPropScartoVers;
    }

    @Column(name = "PG_PROP_SCARTO_VERS")
    public BigDecimal getPgPropScartoVers() {
        return pgPropScartoVers;
    }

    public void setPgPropScartoVers(BigDecimal pgPropScartoVers) {
        this.pgPropScartoVers = pgPropScartoVers;
    }

    // insertable=false, updatable=false perché è una colonna virtuale su DB
    @Column(name = "AA_PROP_SCARTO_VERS", insertable = false, updatable = false)
    public BigDecimal getAaPropScartoVers() {
        return aaPropScartoVers;
    }

    public void setAaPropScartoVers(BigDecimal aaPropScartoVers) {
        this.aaPropScartoVers = aaPropScartoVers;
    }

    // @Column(name = "CD_PROP_SCARTO_VERS")
    // public String getCdPropScartoVers() {
    // return this.cdPropScartoVers;
    // }
    //
    // public void setCdPropScartoVers(String cdPropScartoVers) {
    // this.cdPropScartoVers = cdPropScartoVers;
    // }

    @Column(name = "DS_PROP_SCARTO_VERS")
    public String getDsPropScartoVers() {
        return this.dsPropScartoVers;
    }

    public void setDsPropScartoVers(String dsPropScartoVers) {
        this.dsPropScartoVers = dsPropScartoVers;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE")
    public Date getDtCreazione() {
        return this.dtCreazione;
    }

    public void setDtCreazione(Date dtCreazione) {
        this.dtCreazione = dtCreazione;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_ULTIMA_MOD")
    public Date getDtUltimaMod() {
        return this.dtUltimaMod;
    }

    public void setDtUltimaMod(Date dtUltimaMod) {
        this.dtUltimaMod = dtUltimaMod;
    }

    @Column(name = "ID_STATO_PROP_SCARTO_VERS_COR")
    public BigDecimal getIdStatoPropScartoVersCor() {
        return this.idStatoPropScartoVersCor;
    }

    public void setIdStatoPropScartoVersCor(BigDecimal idStatoPropScartoVersCor) {
        this.idStatoPropScartoVersCor = idStatoPropScartoVersCor;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STRUT")
    public OrgStrut getOrgStrut() {
        return this.orgStrut;
    }

    public void setOrgStrut(OrgStrut orgStrut) {
        this.orgStrut = orgStrut;
    }

    @Column(name = "NT_PROP_SCARTO_VERS")
    public String getNtPropScartoVers() {
        return this.ntPropScartoVers;
    }

    public void setNtPropScartoVers(String ntPropScartoVers) {
        this.ntPropScartoVers = ntPropScartoVers;
    }

    // -----------------------------------------------------------------------
    // Richiesta di autorizzazione
    // -----------------------------------------------------------------------

    @Column(name = "NT_AUTORITA")
    public String getNtAutorita() {
        return ntAutorita;
    }

    public void setNtAutorita(String ntAutorita) {
        this.ntAutorita = ntAutorita;
    }

    @Column(name = "CD_REGISTRO_RICH_AUT")
    public String getCdRegistroRichAut() {
        return cdRegistroRichAut;
    }

    public void setCdRegistroRichAut(String cdRegistroRichAut) {
        this.cdRegistroRichAut = cdRegistroRichAut;
    }

    @Column(name = "AA_RICH_AUT")
    public BigDecimal getAaRichAut() {
        return aaRichAut;
    }

    public void setAaRichAut(BigDecimal aaRichAut) {
        this.aaRichAut = aaRichAut;
    }

    @Column(name = "CD_RICH_AUT")
    public String getCdRichAut() {
        return cdRichAut;
    }

    public void setCdRichAut(String cdRichAut) {
        this.cdRichAut = cdRichAut;
    }

    // -----------------------------------------------------------------------
    // Risposta dell'autorità
    // -----------------------------------------------------------------------

    @Column(name = "CD_REGISTRO_RISP_AUT")
    public String getCdRegistroRispAut() {
        return cdRegistroRispAut;
    }

    public void setCdRegistroRispAut(String cdRegistroRispAut) {
        this.cdRegistroRispAut = cdRegistroRispAut;
    }

    @Column(name = "AA_RISP_AUT")
    public BigDecimal getAaRispAut() {
        return aaRispAut;
    }

    public void setAaRispAut(BigDecimal aaRispAut) {
        this.aaRispAut = aaRispAut;
    }

    @Column(name = "CD_RISP_AUT")
    public String getCdRispAut() {
        return cdRispAut;
    }

    public void setCdRispAut(String cdRispAut) {
        this.cdRispAut = cdRispAut;
    }

    @Column(name = "TI_AUTORIZZAZIONE")
    public String getTiAutorizzazione() {
        return tiAutorizzazione;
    }

    public void setTiAutorizzazione(String tiAutorizzazione) {
        this.tiAutorizzazione = tiAutorizzazione;
    }

    // -----------------------------------------------------------------------
    // Provvedimento di scarto
    // -----------------------------------------------------------------------

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

    @Column(name = "NI_UD_PRE_REVISIONE")
    public BigDecimal getNiUdPreRevisione() {
        return niUdPreRevisione;
    }

    public void setNiUdPreRevisione(BigDecimal niUdPreRevisione) {
        this.niUdPreRevisione = niUdPreRevisione;
    }

    @OneToMany(mappedBy = "aroPropScartoVers", cascade = CascadeType.PERSIST)
    public List<AroStatoPropScartoVers> getAroStatoPropScartoVers() {
        return this.aroStatoPropScartoVers;
    }

    public void setAroStatoPropScartoVers(List<AroStatoPropScartoVers> aroStatoPropScartoVers) {
        this.aroStatoPropScartoVers = aroStatoPropScartoVers;
    }

    @OneToMany(mappedBy = "aroPropScartoVers", cascade = CascadeType.PERSIST)
    public List<AroItemPropScartoVers> getAroItemPropScartoVers() {
        return this.aroItemPropScartoVers;
    }

    public void setAroItemPropScartoVers(List<AroItemPropScartoVers> aroItemPropScartoVers) {
        this.aroItemPropScartoVers = aroItemPropScartoVers;
    }

}
